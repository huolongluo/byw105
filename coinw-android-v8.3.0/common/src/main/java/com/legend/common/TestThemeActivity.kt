package com.legend.common

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.legend.common.databinding.ActivityTestThemeBinding
import com.legend.common.util.ThemeUtil

class TestThemeActivity : Activity(){
    lateinit var tvUp:TextView
    lateinit var tvDrop:TextView
    lateinit var btnBuy: Button
    lateinit var btnsell:Button

    lateinit var mBinding:ActivityTestThemeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_test_theme)

        tvUp = findViewById(R.id.tv_up)
        tvDrop = findViewById(R.id.tv_drop)
        btnBuy = findViewById(R.id.btn_buy)
        btnsell = findViewById(R.id.btn_sell)

        findViewById<View>(R.id.btn_1).setOnClickListener {
            setTheme(R.style.CoinW_Theme_Light_GreenUp_RedDrop)
            applyTheme()
        }
        findViewById<View>(R.id.btn_2).setOnClickListener {
            setTheme(R.style.CoinW_Theme_Light_RedUp_GreenDrop)
            applyTheme()
        }
    }

    private fun applyTheme() {
        mBinding.invalidateAll()
//        tvUp.background = ThemeUtil.getThemeDrawable(this, R.attr.bg_buy_btn)
//        tvDrop.background = ThemeUtil.getThemeDrawable(this, R.attr.bg_sell_btn)
//        btnBuy.background = ThemeUtil.getThemeDrawable(this, R.attr.bg_buy_btn)
//        btnsell.background = ThemeUtil.getThemeDrawable(this, R.attr.bg_sell_btn)
    }
}