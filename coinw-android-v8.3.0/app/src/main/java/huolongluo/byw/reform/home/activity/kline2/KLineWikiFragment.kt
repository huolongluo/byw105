package huolongluo.byw.reform.home.activity.kline2

import android.os.Bundle
import android.view.View
import com.blankj.utilcodes.utils.ToastUtils
import com.google.gson.reflect.TypeToken
import com.netease.nim.uikit.common.util.sys.ClipboardUtil
import huolongluo.byw.R
import huolongluo.byw.byw.base.BaseFragment
import huolongluo.byw.byw.net.UrlConstants
import huolongluo.byw.helper.INetCallback
import huolongluo.byw.helper.OKHttpHelper
import huolongluo.byw.log.Logger
import huolongluo.byw.model.kline.KLineWiki
import huolongluo.byw.model.result.SingleResult
import huolongluo.byw.reform.home.activity.kline2.common.KLineEntity
import huolongluo.bywx.utils.EncryptUtils
import kotlinx.android.synthetic.main.fragment_k_line_wiki.*
import java.util.*


//K线的百科
class KLineWikiFragment : BaseFragment() {
    private lateinit var entity:KLineEntity
    companion object{
        fun newInstance(entity:KLineEntity) = KLineWikiFragment().also{
            val bundle= Bundle()
            bundle.putParcelable("entity",entity)
            it.arguments=bundle
        }

    }

    override fun getContentViewId() : Int {
        return R.layout.fragment_k_line_wiki
    }

    override fun initDagger() {

    }

    override fun initViewsAndEvents(rootView: View) {
        entity=arguments?.getParcelable("entity")?:KLineEntity()
        tvWebsite.setOnClickListener {
            ClipboardUtil.clipboardCopyText(context,tvWebsite.text)
            ToastUtils.showShortToast(getString(R.string.kline_copied))
        }
        tvArea.setOnClickListener {
            ClipboardUtil.clipboardCopyText(context,tvArea.text)
            ToastUtils.showShortToast(getString(R.string.kline_copied))
        }
        getData()
    }

    private fun getData(){
        val type = object : TypeToken<SingleResult<KLineWiki>>() {}.type
        val params: MutableMap<String, Any> = HashMap()
        params["coinCode"] = entity?.coinName.toUpperCase()
        OKHttpHelper.getInstance().get(UrlConstants.KLINE_WIKI+"?"+ EncryptUtils.encryptStr(params)
                , null, getWikiCallback, type)
    }

    public fun refreshData(entity: KLineEntity){
        this.entity=entity
        getData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun initUiError(){
        tvName.text=""
        tvTime.text=""
        tvIssueTotal.text=""
        tvUseTotal.text=""
        tvWebsite.text=""
        tvArea.text=""
        tvIntroduce.text=""
    }

    private val getWikiCallback= object : INetCallback<SingleResult<KLineWiki>> {
        override fun onSuccess(result: SingleResult<KLineWiki>) {
            if (result == null||tvName==null) { //TODO 处理异常情况
                Logger.getInstance().debug(TAG, "result is null.")
                initUiError()
                return
            }
            if (result.code == "200") {
                val wiki:KLineWiki?=result.data
                tvName.text=if(wiki?.coinName==null) "" else "${wiki?.coinName}(${wiki?.coinCode})"
                tvTime.text=wiki?.time
                tvIssueTotal.text=wiki?.total
                tvUseTotal.text=wiki?.circulationTotal
                tvWebsite.text=wiki?.website
                tvArea.text=wiki?.blockQuery
                tvIntroduce.text=wiki?.introduction
            } else {
                initUiError()
//                SnackBarUtils.ShowRed(activity, result?.message)
            }
        }

        override fun onFailure(e: java.lang.Exception) {
            Logger.getInstance().debug(TAG, "error", e)
            //TODO 处理异常情况
            initUiError()
        }
    }
}
