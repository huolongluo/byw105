package com.android.legend.ui.earn.bill

import android.content.Context
import android.content.Intent
import com.android.legend.base.BaseActivity
import com.android.legend.ui.earn.buy.EarnBuyFragment
import com.android.legend.ui.earn.buy.EarnBuySuccessActivity
import huolongluo.byw.R

class EarnBillActivity :BaseActivity() {

    companion object {
        fun launch(context: Context) {
            val intent = Intent()
            intent.setClass(context, EarnBillActivity::class.java)
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
                .replace(R.id.fl_container, EarnBillFragment.getInstance())
                .commit()
    }
}