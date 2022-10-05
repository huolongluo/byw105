package com.android.legend.ui.earn

import android.content.Context
import android.content.Intent
import com.android.legend.base.BaseActivity
import huolongluo.byw.R

class EarnActivity : BaseActivity() {

    companion object{
        fun launch(context: Context){
            val intent = Intent()
            intent.setClass(context, EarnActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun initObserve() {
    }

    override fun getContentViewId() = R.layout.activity_earn

    override fun initData() {

    }

    override fun initView() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fl_container, EarnFrameFragment.getInstance())
                .commit()
    }

}