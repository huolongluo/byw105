package com.legend.modular_contract_sdk.widget.dialog

import android.content.Context
import android.view.View
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.databinding.McSdkDialogOrderConfirmBinding
import com.legend.modular_contract_sdk.ui.contract.Direction
import com.legend.modular_contract_sdk.ui.contract.PositionMode
import com.lxj.xpopup.core.BottomPopupView

class OrderConfirmDialog(context: Context, val direction:String, val triggerPrice: String = "", val price: String, val count: String, val onConfirm: (notShow: Boolean) -> Unit) : BottomPopupView(context) {

    lateinit var mBinding: McSdkDialogOrderConfirmBinding

    override fun getImplLayoutId() = R.layout.mc_sdk_dialog_order_confirm

    override fun onCreate() {
        super.onCreate()
        mBinding = McSdkDialogOrderConfirmBinding.bind(popupImplView)

        if (triggerPrice.isEmpty()) {
            mBinding.tvTriggerPrice.visibility = View.GONE
            mBinding.tvTriggerPriceValue.visibility = View.GONE
        }

        mBinding.tvTriggerPriceValue.text = triggerPrice
        mBinding.tvPriceValue.text = price
        mBinding.tvCountValue.text = count

        if (direction == Direction.LONG.direction){
            mBinding.tvTitle.setText(R.string.mc_sdk_order_confirm_open_long)
        } else {
            mBinding.tvTitle.setText(R.string.mc_sdk_order_confirm_open_short)
        }

        mBinding.ivClose.setOnClickListener {
            dismiss()
        }

        mBinding.btnConfirm.setOnClickListener {
            dismiss()
            onConfirm(mBinding.cbNotShow.isChecked)
        }

    }

}