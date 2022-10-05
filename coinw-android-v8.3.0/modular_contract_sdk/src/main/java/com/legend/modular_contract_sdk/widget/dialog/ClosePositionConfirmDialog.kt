package com.legend.modular_contract_sdk.widget.dialog

import android.content.Context
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.databinding.McSdkDialogClosePositionConfirmBinding
import com.legend.modular_contract_sdk.ui.contract.QuantityUnit
import com.legend.modular_contract_sdk.utils.SPUtils
import com.lxj.xpopup.core.BottomPopupView

class ClosePositionConfirmDialog(context: Context, val price: String, val count: String, val onConfirm: (notShowAgain: Boolean) -> Unit) : BottomPopupView(context){

    lateinit var mBinding: McSdkDialogClosePositionConfirmBinding

    override fun getImplLayoutId() = R.layout.mc_sdk_dialog_close_position_confirm

    override fun onCreate() {
        super.onCreate()
        mBinding = McSdkDialogClosePositionConfirmBinding.bind(popupImplView)

        mBinding.tvPriceValue.text =
                if (price.isNullOrEmpty()) context.getString(R.string.mc_sdk_contract_market_price)
                else price + context.getString(R.string.mc_sdk_usdt)
        mBinding.tvCountValue.text = count


        mBinding.ivClose.setOnClickListener {
            dismiss()
        }

        mBinding.btnConfirm.setOnClickListener {
            dismiss()
            onConfirm(mBinding.cbNotShow.isChecked)
        }

    }
}