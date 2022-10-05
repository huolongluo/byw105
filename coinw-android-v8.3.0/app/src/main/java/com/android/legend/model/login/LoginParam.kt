package com.android.legend.model.login

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 登录相关接口传递的参数
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class LoginParam(
        val username:String,  //此处为username，字段名参考以前的代码
        var password:String,
        var phoneCode:String,//默认传0
        var googleCode:String,//默认传0
        var messageType:Int,//发送短信或邮箱验证码的type
        val imei:String,
        val areaCode:String,
        val random:String,
        val appVersion:String,
        val fromClass:String?,
        var inviteCode:String,
        val isPhone:Boolean
):Parcelable
