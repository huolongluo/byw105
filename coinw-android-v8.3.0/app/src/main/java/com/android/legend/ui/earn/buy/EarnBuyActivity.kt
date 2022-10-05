package com.android.legend.ui.earn.buy

import android.content.Context
import android.content.Intent
import com.android.legend.base.BaseActivity
import com.android.legend.model.earn.EarnProduct
import huolongluo.byw.R

class EarnBuyActivity : BaseActivity() {
    companion object {
        fun launch(context: Context, earnProduct: EarnProduct, selectDeadlineIndex:Int) {
            val intent = Intent()
            intent.setClass(context, EarnBuyActivity::class.java)
            intent.putExtra("earn_product", earnProduct)
            intent.putExtra("select_deadline", selectDeadlineIndex)
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
                .replace(R.id.fl_container, EarnBuyFragment.getInstance(intent.getSerializableExtra("earn_product") as EarnProduct, intent.getIntExtra("select_deadline", 0)))
                .commit()
    }

}