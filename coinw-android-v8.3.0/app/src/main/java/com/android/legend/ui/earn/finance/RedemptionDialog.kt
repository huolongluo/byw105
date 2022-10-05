package com.android.legend.ui.earn.finance

import android.content.Context
import android.view.View
import com.android.legend.model.earn.wrap.EarnProductWrap
import com.legend.modular_contract_sdk.utils.getDouble
import com.lxj.xpopup.core.BottomPopupView
import huolongluo.byw.R
import huolongluo.byw.databinding.DialogRedemptionBinding

class RedemptionDialog(context: Context, val earnProduct: EarnProductWrap, val onConfirm: (EarnProductWrap) -> Unit) : BottomPopupView(context) {

    lateinit var mBinding: DialogRedemptionBinding

    override fun getImplLayoutId(): Int = R.layout.dialog_redemption

    override fun onCreate() {
        super.onCreate()

        mBinding = DialogRedemptionBinding.bind(popupImplView)

        mBinding.earnProduct = earnProduct

        if (earnProduct.isMixRegularProduct()){
//            android:text="@{earnProduct.getExpectedProfit(earnProduct.earnProduct.investTotalAmount)}"
            mBinding.tvProfit.setText(R.string.earn_expected_profit)
            mBinding.tvProfitValue.text =
                    earnProduct.getExpectedProfit(earnProduct.earnProduct.investTotalAmount.getDouble()) + earnProduct.getIncomeCurrencyName()
        } else {
            mBinding.tvProfit.setText(R.string.earn_profit)
            mBinding.tvProfitValue.text = earnProduct.earnProduct.incomeTotalAmount
        }

        if (earnProduct.isMixRegularProduct()){
            mBinding.btnRedemption.visibility = View.GONE
        }

        mBinding.btnRedemption.setOnClickListener {
            onConfirm(earnProduct)
            dismiss()
        }

        mBinding.ivClose.setOnClickListener {
            dismiss()
        }

    }
}