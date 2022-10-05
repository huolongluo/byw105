package com.legend.common.component.theme

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.legend.common.R
import org.greenrobot.eventbus.EventBus

@SuppressLint("StaticFieldLeak")
object ThemeManager {

    private const val SP_NAME = "theme-manager"
    private const val THEME_MODE = "theme-mode"

    var mContext:Context? = null

    var mSp : SharedPreferences?=null

    val DEFAULT_THEME = ThemeColorMode.LIGHT_GREEN_UP_RED_DROP

    fun initThemeManager(context: Context){
        mContext=context
        mSp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }

    fun getTickerColorMode():Int{
        initSp()
        val themeMode = mSp?.getInt(THEME_MODE, DEFAULT_THEME.mode)
        return when(themeMode){
            ThemeColorMode.LIGHT_GREEN_UP_RED_DROP.mode,ThemeColorMode.DARK_GREEN_UP_RED_DROP.mode ->{
                TickerColorMode.GREEN_UP_RED_DROP.mode
            }
            ThemeColorMode.LIGHT_RED_UP_GREEN_DROP.mode,ThemeColorMode.DARK_RED_UP_GREEN_DROP.mode ->{
                TickerColorMode.RED_UP_GREEN_DROP.mode
            }
            else -> {
                TickerColorMode.RED_UP_GREEN_DROP.mode
            }
        }
    }

    fun getThemeMode():Int {
        initSp()
        return mSp?.getInt(THEME_MODE, DEFAULT_THEME.mode)?:DEFAULT_THEME.mode
    }

    // 切换涨跌色
    fun changeTickerColorMode(){
        initSp()
        when(getThemeMode()){
            ThemeColorMode.LIGHT_GREEN_UP_RED_DROP.mode->{
                mSp?.edit {
                    putInt(THEME_MODE, ThemeColorMode.LIGHT_RED_UP_GREEN_DROP.mode)
                }
                EventBus.getDefault().post(ChangeThemeEvent(ThemeColorMode.LIGHT_RED_UP_GREEN_DROP))
            }
            ThemeColorMode.LIGHT_RED_UP_GREEN_DROP.mode->{
                mSp?.edit {
                    putInt(THEME_MODE, ThemeColorMode.LIGHT_GREEN_UP_RED_DROP.mode)
                }
                EventBus.getDefault().post(ChangeThemeEvent(ThemeColorMode.LIGHT_GREEN_UP_RED_DROP))
            }
            ThemeColorMode.DARK_GREEN_UP_RED_DROP.mode ->{
                //todo 夜间模式 涨跌色
            }
            ThemeColorMode.DARK_RED_UP_GREEN_DROP.mode ->{
                //todo 夜间模式 涨跌色
            }
        }
    }

    // 日间模式夜间模式切换
    fun changeTheme(activity: Activity) {
        when(ThemeManager.getThemeMode()){
            ThemeColorMode.LIGHT_GREEN_UP_RED_DROP.mode->{
            }
            ThemeColorMode.LIGHT_RED_UP_GREEN_DROP.mode->{
            }
            ThemeColorMode.DARK_GREEN_UP_RED_DROP.mode ->{
            }
            ThemeColorMode.DARK_RED_UP_GREEN_DROP.mode ->{
            }
        }
    }
    fun initSp(){//umeng报错mSp未初始化
        if(mSp==null){
            mSp= mContext?.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        }
    }
}