package com.android.legend.ui.mine

import android.content.Context
import android.content.Intent
import com.android.legend.base.BaseActivity
import huolongluo.byw.R

class MineActivity :BaseActivity() {

    companion object{
        @JvmStatic
        fun launch(context: Context){
            context.startActivity(Intent(context, MineActivity::class.java))
        }
    }

    override fun getContentViewId(): Int = R.layout.activity_fragment

    override fun initView() {

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fl_container, MineFragment.getInstance())
                .commit()
    }

    override fun initData() {
    }

    override fun initObserve() {
    }
}