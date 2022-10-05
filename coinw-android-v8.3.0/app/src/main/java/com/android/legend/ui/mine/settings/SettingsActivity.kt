package com.android.legend.ui.mine.settings

import com.android.legend.base.BaseActivity
import com.android.legend.ui.mine.MineFragment
import huolongluo.byw.R

class SettingsActivity : BaseActivity() {

    override fun getContentViewId() = R.layout.activity_fragment

    override fun initView() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fl_container, SettingsFragment.getInstance())
                .commit()
    }

    override fun initData() {
    }

    override fun initObserve() {
    }
}