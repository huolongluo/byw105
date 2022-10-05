package com.android.legend.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.legend.modular_contract_sdk.common.event.GetSharePicEvent
import com.legend.modular_contract_sdk.common.event.ShowExperienceGildExplainEvent
import huolongluo.byw.R
import huolongluo.byw.byw.net.UrlConstants
import huolongluo.byw.log.Logger
import huolongluo.byw.reform.home.activity.NewsWebviewActivity
import huolongluo.byw.reform.home.bean.SharePicBean
import huolongluo.byw.user.UserInfoManager
import huolongluo.byw.util.OkhttpManager
import huolongluo.byw.util.OkhttpManager.DataCallBack
import okhttp3.Request
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

@SuppressLint("StaticFieldLeak")
object ContractModuleBridge {

    private var mContext:Context? = null

    fun init(context: Context) {
        mContext = context
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getShareDownloadLink(event: GetSharePicEvent){
        var params: MutableMap<String?, String?> = HashMap()
        params["type"] = "1"
        params = OkhttpManager.encrypt(params)
        params["loginToken"] = UserInfoManager.getToken()

        OkhttpManager.postAsync(UrlConstants.sharePic, params, object : DataCallBack {
            override fun requestFailure(request: Request, e: Exception, errorMsg: String) {
                e.printStackTrace()
            }

            override fun requestSuccess(result: String) {
                try {
                    val picBean = Gson().fromJson(result, SharePicBean::class.java)
                    if (picBean.getCode() == 0) {
                        huolongluo.byw.log.Logger.getInstance().error("getShareInfo: $result")
                        huolongluo.byw.log.Logger.getInstance().error("getShareInfo: ${picBean.getAgentRdgister()}")
                        event.callback.invoke(true, picBean.getAgentRdgister())
                    } else {
                        event.callback.invoke(false, "")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun showExperienceGoldExplain(event: ShowExperienceGildExplainEvent){
        try {
            val intent = Intent(mContext, NewsWebviewActivity::class.java)
            intent.putExtra("url", "https://coinw.zendesk.com/hc/zh-cn/articles/4404653630745")
            intent.putExtra("token", UserInfoManager.getToken())
            intent.putExtra("title", mContext?.getString(R.string.mc_sdk_contract_experience_gold_title))
            intent.putExtra("hideTitle", false)
            intent.putExtra("isBdb", false)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mContext?.startActivity(intent)
        } catch (t: Throwable) {
            Logger.getInstance().error(t)
        }
    }

}