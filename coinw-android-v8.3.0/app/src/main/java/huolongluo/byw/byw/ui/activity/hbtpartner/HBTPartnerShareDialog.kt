package huolongluo.byw.byw.ui.activity.hbtpartner

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcodes.utils.ToastUtils
import com.bumptech.glide.Glide
import com.google.gson.reflect.TypeToken
import com.mob.tools.utils.ResHelper
import huolongluo.byw.R
import huolongluo.byw.byw.net.UrlConstants
import huolongluo.byw.helper.INetCallback
import huolongluo.byw.helper.OKHttpHelper
import huolongluo.byw.log.Logger
import huolongluo.byw.model.result.SingleResult
import huolongluo.byw.reform.base.BaseFragment
import huolongluo.byw.reform.dialog.ShareDialog
import huolongluo.byw.user.UserInfoManager
import huolongluo.byw.util.CreateQRImage
import huolongluo.byw.util.GsonUtil
import huolongluo.byw.util.ScreenshotUtil
import huolongluo.byw.util.Util
import huolongluo.bywx.utils.EncryptUtils
import kotlinx.android.synthetic.main.dialog_hbt_partner_share.*
import kotlinx.android.synthetic.main.fragment_hbt_partner_share.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class HBTPartnerShareDialog : DialogFragment() {


    companion object {
        private const val TAG = "HBTPartnerShareDialog"
        private const val PAGER_HEIGHT = 480
        private const val PAGER_WIDTH = PAGER_HEIGHT * 0.5F  // 长宽比为0.5
    }

    private val url by lazy {
        val param = EncryptUtils.encryptStr(mutableMapOf<String, Any>("bodyType" to "1"))
        UrlConstants.HBT_GET_POSTER + "?loginToken=" + UserInfoManager.getToken() + "&" + param
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.MyAlertDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_hbt_partner_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView.setOnClickListener { dismissAllowingStateLoss() }

        OKHttpHelper.getInstance().get(url, object : INetCallback<SingleResult<String>> {
            override fun onSuccess(t: SingleResult<String>) {
                Logger.getInstance().debug("$TAG -> $t")
                if (t.code == "200") {
                    val bean: SingleResult<HBTSharePosterBean>? = GsonUtil.json2Obj(t.data, object : TypeToken<SingleResult<HBTSharePosterBean>>() {}.type)
                    if (bean?.code == "0") {
                        setView(bean.data)
                    } else {
                        ToastUtils.showShortToast(bean?.message)
                        dismissAllowingStateLoss()
                    }
                } else {
                    ToastUtils.showShortToast(t.message)
                    dismissAllowingStateLoss()
                }
            }

            override fun onFailure(e: java.lang.Exception) {
                Logger.getInstance().debug("$TAG -> ${e.message}")
                dismissAllowingStateLoss()
            }
        }, object : TypeToken<SingleResult<String>>() {}.type)
    }

    private fun setView(bean: HBTSharePosterBean) {
        val fragments = bean.poster.map { HBTPartnerShareFragment.newInstance(it, bean.agentRdgister) }
        imgViewPager.offscreenPageLimit = 1
        val recyclerView = imgViewPager.getChildAt(0) as RecyclerView
        val padding = (ResHelper.getScreenWidth(requireContext()) - Util.dp2px(context, PAGER_WIDTH + 24F)) / 2
        recyclerView.setPadding(padding, 0, padding, 0)
        recyclerView.clipToPadding = false
        recyclerView.setItemViewCacheSize(fragments.size)
        imgViewPager.adapter = object : FragmentStateAdapter(this) {

            override fun getItemCount(): Int = fragments.size

            override fun createFragment(position: Int) = fragments[position]

        }
        imgViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                fragments.forEachIndexed { index, fragment -> fragment.setSelect(position == index) }
            }
        })
//        imgViewPager.post { imgViewPager.setCurrentItem(INIT_PAGE, false) }
        btnShare.setOnClickListener {
            //            fragments.getOrNull(imgViewPager.currentItem)?.savePic()
            fragments.getOrNull(imgViewPager.currentItem)?.sharePic()
        }
        progressBar.visibility = View.GONE
        btnShare.visibility = View.VISIBLE
        imgViewPager.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(width, height)
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, TAG)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        OKHttpHelper.getInstance().removeRequest(url)
    }


    class HBTPartnerShareFragment : BaseFragment(), CoroutineScope by MainScope() {

        private val createQRImage = CreateQRImage()

        companion object {
            fun newInstance(url: String, shareUrl: String) = HBTPartnerShareFragment().also {
                val args = Bundle()
                args.putString("url", url)
                args.putString("shareUrl", shareUrl)
                it.arguments = args
            }
        }

        override fun getRootViewResId() = R.layout.fragment_hbt_partner_share

        override fun onCreatedView(rootView: View) {
        }

        private val shareUrl by lazy {
            arguments?.getString("shareUrl") ?: ""
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val url = arguments?.getString("url")
            if (url != null) {
                Glide.with(requireContext()).load(url).into(ivImg)
            }
            // 动态计算二维码位置
            val qrSize = Util.dp2px(requireContext(), PAGER_WIDTH * 0.25F)
            ivQrcode.setImageBitmap(createQRImage.createQRImage(shareUrl, qrSize, qrSize, true))
            // 设置位置
            val lp = ivQrcode.layoutParams as ViewGroup.MarginLayoutParams
            lp.bottomMargin = Util.dp2px(context, HBTPartnerShareDialog.PAGER_HEIGHT * 0.08F)
            lp.rightMargin = Util.dp2px(context, HBTPartnerShareDialog.PAGER_HEIGHT * 0.05F)
            lp.width = qrSize
            lp.height = qrSize
            ivQrcode.layoutParams = lp
        }

        fun setSelect(select: Boolean) {
            ivCheck?.visibility = if (select) View.VISIBLE else View.GONE
            imgBg?.isSelected = select
        }

        fun sharePic() {
            ShareDialog(requireContext(), flScreenShot, shareUrl).show()
        }

        fun savePic() {
            val outputPath = Environment.getExternalStorageDirectory().absolutePath + "/" +
                    Environment.DIRECTORY_DCIM + "/Camera/coinw_hbt_partner_share${System.currentTimeMillis()}.jpg"
            ScreenshotUtil.screenshotView(this, flScreenShot, outputPath, {
                //保存成功
                context?.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, it.toUri()));
                launch { ToastUtils.showShortToast(getString(R.string.d5)) }
            }, { /*保存失败*/launch { ToastUtils.showShortToast(getString(R.string.d6)) } })

        }

        override fun onDestroyView() {
            super.onDestroyView()
            cancel()
        }
    }
}