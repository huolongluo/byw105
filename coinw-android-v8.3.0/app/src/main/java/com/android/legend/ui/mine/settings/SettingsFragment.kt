package com.android.legend.ui.mine.settings

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.android.legend.base.BaseFragment
import com.legend.common.component.theme.ThemeManager
import com.legend.common.component.theme.TickerColorMode
import huolongluo.byw.BuildConfig
import huolongluo.byw.R
import huolongluo.byw.byw.base.BaseApp
import huolongluo.byw.byw.bean.VersionInfo
import huolongluo.byw.byw.net.UrlConstants
import huolongluo.byw.byw.share.Event
import huolongluo.byw.byw.ui.activity.main.MainViewModel
import huolongluo.byw.byw.ui.dialog.AddDialog
import huolongluo.byw.byw.ui.fragment.maintab04.NetworkDetectActivity
import huolongluo.byw.databinding.FragmentSettingsBinding
import huolongluo.byw.reform.mine.activity.ChangeLanguageActivity
import huolongluo.byw.reform.mine.activity.PricingMethodActivity
import huolongluo.byw.user.UserInfoManager
import huolongluo.byw.util.*
import huolongluo.byw.util.OkhttpManager.DataCallBack
import huolongluo.byw.util.cache.CacheManager
import huolongluo.byw.util.pricing.PricingMethodUtil
import huolongluo.byw.util.tip.SnackBarUtils
import huolongluo.bywx.utils.AppUtils
import huolongluo.bywx.utils.DoubleUtils
import okhttp3.Request
import org.greenrobot.eventbus.Subscribe
import java.util.*

class SettingsFragment : BaseFragment() {


    companion object {
        fun getInstance() = SettingsFragment()
    }

    lateinit var mBinding: FragmentSettingsBinding

    var mainViewModel: MainViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun getContentViewId() = 0

    override fun initView(view: View) {

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        if (LogicLanguage.getLanguage(context).contains("zh")) {
            mBinding.tvLanguage.setText(R.string.dd91)
        } else if (LogicLanguage.getLanguage(context).contains("en")) {
            mBinding.tvLanguage.text = "English"
        } else if (LogicLanguage.getLanguage(context).contains("ko")) {
            mBinding.tvLanguage.text = "한국어"
        }

        if (ThemeManager.getTickerColorMode() == TickerColorMode.GREEN_UP_RED_DROP.mode) {
            mBinding.tvTickerColor.setText(R.string.settings_ticker_color_green_up)
        } else {
            mBinding.tvTickerColor.setText(R.string.settings_ticker_color_red_up)
        }

        mBinding.tvValuation.text = PricingMethodUtil.getPricingName(context)

        mBinding.tvVersion.text = "V ${BuildConfig.VERSION_NAME}"

        mBinding.btnLogout.visibility = if (UserInfoManager.isLogin()) View.VISIBLE else View.GONE

        mBinding.llLanguage.setOnClickListener {
            //语言切换
            startActivity(Intent(activity, ChangeLanguageActivity::class.java))
        }

        mBinding.llValuation.setOnClickListener {
            //计价方式切换
            startActivityForResult(Intent(activity, PricingMethodActivity::class.java), 102)
        }

        mBinding.llTickerColor.setOnClickListener {
            TickerColorSwitchActivity.launch(requireContext())
        }

        mBinding.llNetCheck.setOnClickListener {
            NetworkDetectActivity.launch(mContext)
        }

        mBinding.llVersion.setOnClickListener {
            getVersion()
        }

        mBinding.btnLogout.setOnClickListener {
            val dialog = AddDialog()
            dialog.setDialog(AddDialog.EXIT_APP)
            dialog.setOnClick {
                mainViewModel?.logout()
                mBinding.btnLogout.visibility = View.GONE
            }

            dialog.show(childFragmentManager, javaClass.simpleName)
        }
    }

    override fun initData() {
    }

    override fun onResume() {
        super.onResume()
        if (ThemeManager.getTickerColorMode() == TickerColorMode.GREEN_UP_RED_DROP.mode) {
            mBinding.tvTickerColor.setText(R.string.settings_ticker_color_green_up)
        } else {
            mBinding.tvTickerColor.setText(R.string.settings_ticker_color_red_up)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 102 && resultCode == Activity.RESULT_OK) {
            mBinding.tvValuation.text = PricingMethodUtil.getPricingName(context)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun initTitle() = getString(R.string.settings)

    //获取版本信息
    private fun getVersion() {
        if (!AppUtils.isNetworkConnected()) {
            return
        }
        //渠道包-不提供更新功能
        if (BuildConfig.APP_CHANNEL_VALUE == 1) {
            return
        }
        var params: MutableMap<String?, String?> = HashMap()
        params["type"] = "1"
        params = OkhttpManager.encrypt(params)
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GetVersion, params, object : DataCallBack {
            override fun requestFailure(request: Request, e: Exception, errorMsg: String) {
                SnackBarUtils.ShowRed(activity, getString(R.string.dd75))
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            override fun requestSuccess(result: String) {
                Log.e("检查版本", "result=$result")
                var versionInfo: VersionInfo? = null
                try {
                    versionInfo = GsonUtil.json2Obj(result, VersionInfo::class.java)
                    if (DoubleUtils.parseDouble(BuildConfig.VERSION_CODE.toString() + "") < DoubleUtils.parseDouble(versionInfo.android_version_code)) {
                        UpgradeUtils.getInstance().upgrade(activity, versionInfo)
                    } else {
                        Log.e("SnackBarUtils", "SnackBarUtils")
                        SnackBarUtils.ShowBlue(activity, getString(R.string.dd76))
                        //  showMessage("已是最新版本",1);
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

}