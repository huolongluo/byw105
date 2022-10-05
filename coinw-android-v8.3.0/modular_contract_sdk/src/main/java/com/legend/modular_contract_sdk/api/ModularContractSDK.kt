package com.legend.modular_contract_sdk.api

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.legend.modular_contract_sdk.BuildConfig
import com.legend.modular_contract_sdk.common.event.LoginEvent
import com.legend.modular_contract_sdk.component.market_listener.MarketListenerManager
import com.orhanobut.logger.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus

@SuppressLint("StaticFieldLeak")
object ModularContractSDK {

    var isInit = false

    var context: Context? = null

    var mEventCallback: EventCallback? = null

    var appId = ""
        private set

    var theme = -1
        private set

    var userToken = ""
        private set
    var language = "en"

    var host=""//动态域名由app传入
    var wsHost=""//ws域名通过接口返回

    fun initSDK(context: Context?, appId: String,eventCallback: EventCallback) {
        isInit = true
        ModularContractSDK.context = context
        ModularContractSDK.appId = appId
        mEventCallback = eventCallback
        initLogger()

    }
    fun resetLanguage(language: String){
        this.language=language
    }

    private fun initLogger() {
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true) // (Optional) Whether to show thread info or not. Default true
            .methodCount(0) // (Optional) How many method line to show. Default 2
            .methodOffset(5) // (Optional) Hides internal method calls up to offset. Default 5
            .logStrategy(LogcatLogStrategy()) // (Optional) Changes the log strategy to print out. Default LogCat
            //            .tag("My custom tag") // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
    }


    fun login(token: String) {
        if(TextUtils.equals(token, userToken)){
            return
        }
        userToken = token
        MarketListenerManager.login(userToken)
        EventBus.getDefault().post(LoginEvent())
    }

    fun logout() {
        if (userIsLogin()) {
            MarketListenerManager.logout(userToken)
            userToken = ""
            EventBus.getDefault().post(LoginEvent())
        }
    }

    fun userIsLogin() = !userToken.isNullOrEmpty()

    fun getCurrentLanguage():String{
        if(TextUtils.equals(language,"ko")){
            return "ko-KR"
        }
        return language
    }
}