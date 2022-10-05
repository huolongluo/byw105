package com.android.legend.ui.earn.buy

import android.content.Context
import android.content.Intent
import com.android.legend.base.BaseActivity
import com.android.legend.model.earn.EarnProduct
import huolongluo.byw.R

class EarnBuySuccessActivity : BaseActivity() {
    companion object {
        fun launch(context: Context, earnProduct: EarnProduct, amount: ArrayList<String>, days: Int) {
            val intent = Intent()
            intent.setClass(context, EarnBuySuccessActivity::class.java)
            intent.putExtra("earn_product", earnProduct)
            intent.putStringArrayListExtra("amount", amount)
            intent.putExtra("days", days)
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
                .replace(R.id.fl_container,
                        EarnBuySuccessFragment.getInstance(
                                intent.getSerializableExtra("earn_product") as EarnProduct,
                                intent.getStringArrayListExtra("amount"),
                                intent.getIntExtra("days", -1)))
                .commit()
    }

}