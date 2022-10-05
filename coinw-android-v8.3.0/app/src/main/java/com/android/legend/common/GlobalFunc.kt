package com.android.legend.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.android.legend.ui.login.LoginActivity
import huolongluo.byw.user.UserInfoManager

fun isLogin():Boolean{
    if(UserInfoManager.isLogin()){
        return true
    }
    return false
}
//全局返回是否登录，没登录直接跳转登录
fun isLoginOrGotoLoginActivity(context: Context):Boolean{
    val isLogin= isLogin()
    if(!isLogin){
        LoginActivity.launch(context)
    }
    return isLogin
}