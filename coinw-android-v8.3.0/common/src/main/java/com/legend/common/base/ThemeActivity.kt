package com.legend.common.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.legend.common.R
import com.legend.common.component.theme.ChangeThemeEvent
import com.legend.common.component.theme.ThemeColorMode
import com.legend.common.component.theme.ThemeManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


open class ThemeActivity : AppCompatActivity() {

    /**
    切换主题之后启动的Activity都不需要订阅切换主题的事件,UI也不用做额外的适配.
    需要动态切换主题的Activity订阅此事件并对UI做主题适配
     */
    open var needSubscribeChangeThemeEvent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (ThemeManager.getThemeMode()) {
            ThemeColorMode.LIGHT_GREEN_UP_RED_DROP.mode -> {
                setTheme(R.style.CoinW_Theme_Light_GreenUp_RedDrop)
            }
            ThemeColorMode.LIGHT_RED_UP_GREEN_DROP.mode -> {
                setTheme(R.style.CoinW_Theme_Light_RedUp_GreenDrop)
            }
            ThemeColorMode.DARK_GREEN_UP_RED_DROP.mode -> {
            }
            ThemeColorMode.DARK_RED_UP_GREEN_DROP.mode -> {
            }
        }

        if (needSubscribeChangeThemeEvent) {
            EventBus.getDefault().register(this)
        }

    }

    open fun onChangeTheme() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeTheme(event: ChangeThemeEvent) {
        when (event.mThemeColorMode) {
            ThemeColorMode.LIGHT_GREEN_UP_RED_DROP -> {
                setTheme(R.style.CoinW_Theme_Light_GreenUp_RedDrop)
            }
            ThemeColorMode.LIGHT_RED_UP_GREEN_DROP -> {
                setTheme(R.style.CoinW_Theme_Light_RedUp_GreenDrop)
            }
            ThemeColorMode.DARK_GREEN_UP_RED_DROP -> {
            }
            ThemeColorMode.DARK_RED_UP_GREEN_DROP -> {
            }
        }
        onChangeTheme()
    }

}