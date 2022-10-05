package com.android.legend.ui.mine.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.android.legend.base.BaseActivity
import com.legend.common.component.theme.ThemeColorMode
import com.legend.common.component.theme.ThemeManager
import com.legend.common.component.theme.TickerColorMode
import com.legend.common.util.ThemeUtil
import huolongluo.byw.R
import huolongluo.byw.databinding.ActivityTickerColorSwitchBinding

class TickerColorSwitchActivity :BaseActivity(){

    companion object{
        fun launch(context: Context){
            context.startActivity(Intent(context, TickerColorSwitchActivity::class.java))
        }
    }

    lateinit var mBinding: ActivityTickerColorSwitchBinding

    lateinit var ivSelectedGreenUp: ImageView
    lateinit var ivSelectedRedUp: ImageView
    lateinit var rlGreenUp: View
    lateinit var rlRedUp: View
    

    override fun getContentViewId() = R.layout.activity_ticker_color_switch

    override fun initView() {

        ivSelectedGreenUp = findViewById(R.id.iv_selected_green_up)
        ivSelectedRedUp = findViewById(R.id.iv_selected_red_up)
        rlGreenUp = findViewById(R.id.rl_green_up)
        rlRedUp = findViewById(R.id.rl_red_up)
        
        if (ThemeManager.getTickerColorMode() == TickerColorMode.GREEN_UP_RED_DROP.mode){
            ivSelectedGreenUp.visibility = View.VISIBLE
            ivSelectedRedUp.visibility = View.GONE
        } else {
            ivSelectedGreenUp.visibility = View.GONE
            ivSelectedRedUp.visibility = View.VISIBLE
        }

        rlGreenUp.setOnClickListener {
            if (ThemeManager.getTickerColorMode() == TickerColorMode.GREEN_UP_RED_DROP.mode){
                return@setOnClickListener
            }
            ivSelectedGreenUp.visibility = View.VISIBLE
            ivSelectedRedUp.visibility = View.GONE
            ThemeManager.changeTickerColorMode()
        }

        rlRedUp.setOnClickListener {
            if (ThemeManager.getTickerColorMode() == TickerColorMode.RED_UP_GREEN_DROP.mode){
                return@setOnClickListener
            }
            ivSelectedGreenUp.visibility = View.GONE
            ivSelectedRedUp.visibility = View.VISIBLE
            ThemeManager.changeTickerColorMode()
        }


    }

    override fun initData() {
    }

    override fun initObserve() {
    }

    override fun initTitle(): String {
        return getString(R.string.settings_ticker_color)
    }
}