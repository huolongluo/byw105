package com.android.legend.ui.earn.finance

import android.content.Context
import android.content.Intent
import com.android.legend.base.BaseActivity
import com.android.legend.ui.earn.bill.EarnBillActivity
import com.android.legend.ui.earn.bill.EarnBillFragment
import huolongluo.byw.R

class MyEarnProductActivity : BaseActivity(){
    companion object {
        fun launch(context: Context, currencyId:Int) {
            val intent = Intent()
            intent.setClass(context, MyEarnProductActivity::class.java)
            intent.putExtra("currency_id",currencyId)
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
                .replace(R.id.fl_container, MyEarnProductFragment.getInstance(intent.getIntExtra("currency_id", -1)))
                .commit()
    }
}