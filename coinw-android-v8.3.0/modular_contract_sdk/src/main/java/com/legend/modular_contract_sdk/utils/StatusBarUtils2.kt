package com.legend.modular_contract_sdk.utils

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager
import com.legend.modular_contract_sdk.R

object StatusBarUtils2 {
    /**
     * 设置状态栏背景颜色为白色且字体颜色为黑色
     */
    fun setStatusBar(activity: Activity) {
        //>6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 设置状态栏底色颜色
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.statusBarColor = activity.resources.getColor(R.color.white)
            // 设置状态栏字体颜色
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            //<6.0 ,小于6.0大于4.4目前只有小米跟魅族可以实现状态栏文字图标修改颜色，从友盟看6.0以下机型非常少，故不做适配
        }
    }

    /**
     * 设置状态栏默认颜色背景颜色为黑色且字体颜色为白色
     */
    fun setStatusBarDefault(activity: Activity) {
        //>6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 设置状态栏底色颜色
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.statusBarColor = activity.resources.getColor(R.color.color_19162B)
            // 设置状态栏字体颜色
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }
}