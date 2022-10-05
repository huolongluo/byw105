package com.android.legend.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import cn.jpush.android.api.JPushInterface
import com.android.coinw.biz.event.BizEvent
import com.android.legend.model.login.LoginParam
import huolongluo.byw.R
import huolongluo.byw.byw.base.BaseApp
import huolongluo.byw.byw.share.Event
import huolongluo.byw.byw.ui.activity.main.MainActivity
import huolongluo.byw.heyue.HeYueUtil
import huolongluo.byw.reform.bean.LoginBean
import huolongluo.byw.user.UserInfoManager
import huolongluo.byw.util.AgreementUtils
import huolongluo.byw.util.SPUtils
import huolongluo.byw.util.StringUtil
import huolongluo.byw.util.tip.DialogUtils
import huolongluo.byw.util.tip.MToast
import org.greenrobot.eventbus.EventBus

object LoginHelper {
    @JvmStatic
    fun updateLoginData(loginBean: LoginBean, activity: Activity, userName: String){
        UserInfoManager.setUserInfoBean(loginBean.userInfo)
        AgreementUtils.initAgreementOpenStatus(activity, loginBean.userInfo?.fid.toString())
        loginBean.userInfo?.loginToken = loginBean.loginToken
        SPUtils.saveString(activity, SPUtils.USER_NAME, userName)
        SPUtils.saveLoginToken(loginBean.loginToken)
        UserInfoManager.setToken(loginBean.loginToken)
        HeYueUtil.getInstance().getHeYueUser(loginBean.loginToken)
        SPUtils.saveString(activity, SPUtils.UID, loginBean.userInfo?.fid.toString())
        loginBean.userInfo?.isBindMobil?.let { SPUtils.saveBoolean(activity, SPUtils.IS_BIND_PHONE, it) }
        BaseApp.getSelf().loginIM()
        JPushInterface.setAlias(activity, 0, UserInfoManager.getUserInfo()?.fid.toString())
        loginBean.userInfo?.isGoogleBind?.let { SPUtils.saveBoolean(activity, SPUtils.IS_BIND_GOOGLE, it) }

        EventBus.getDefault().post(Event.refreshInfo())
    }
    @JvmStatic
    fun registerSuccess(loginBean: LoginBean, activity: Activity, userName: String){
        updateLoginData(loginBean, activity, userName)
        if(!loginBean.isArea_available){
            showWarningDialog(activity,false,"")
        }else{
            nextRegister(activity)
        }
    }
    @JvmStatic
    fun loginSuccess(loginBean: LoginBean, activity: Activity, userName: String, fromClass: String?){
        updateLoginData(loginBean, activity, userName)
        if(!loginBean.isArea_available){
            showWarningDialog(activity,true,fromClass)
        }else{
            nextLogin(activity,fromClass)
        }
    }

    @JvmStatic
    fun getCodeTips(loginParam: LoginParam, context: Context):Spanned{
        return if(loginParam.isPhone)
            Html.fromHtml(String.format(context.getString(R.string.phone_code_tips), "+${loginParam.areaCode} " +
                    "${StringUtil.getHidePhone(loginParam.username)}"))
        else Html.fromHtml(String.format(context.getString(R.string.email_code_tips), "${StringUtil.getHideEmail(loginParam.username)}"))
    }
    @JvmStatic
    fun nextLogin(activity: Activity,fromClass:String?){
        MToast.show(activity, activity.getString(R.string.login_success), 1)
        EventBus.getDefault().post(BizEvent.CloseActivity())
        MainActivity.self.startAgreementOpenStatusTimer()
        if (!TextUtils.isEmpty(fromClass)) {
            activity.startActivity(Intent(activity, MainActivity::class.java))
            activity.finish()
        } else {
            EventBus.getDefault().post(Event.refreFansUpInfo())
            EventBus.getDefault().post(BizEvent.RefreshRedEnvelope())
            EventBus.getDefault().post(Event.closeActivity("LoginActivity"))
            activity.finish()
        }
        val intent = Intent("jerry")
        intent.putExtra("change", "success")
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent)
    }
    @JvmStatic
    fun nextRegister(activity: Activity){
        MToast.show(activity, activity.getString(R.string.regist_suc), 1)
        EventBus.getDefault().post(BizEvent.CloseActivity())
        val intent1 = Intent(activity, MainActivity::class.java)
        intent1.putExtra("change", "regist_success")
        activity.startActivity(intent1)
        val intent = Intent("jerry")
        intent.putExtra("change", "regist_success")
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent)
    }
    @JvmStatic
    fun showWarningDialog(activity: Activity,isLogin:Boolean, fromClass: String?){
        DialogUtils.getInstance().showOneButtonDialog(activity,activity.getString(R.string.area_available),activity.getString(R.string.iknow)) {
            if(isLogin){
                nextLogin(activity,fromClass)
            }else{
                nextRegister(activity)
            }
        }
    }
}