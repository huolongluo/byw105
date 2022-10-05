package com.legend.modular_contract_sdk.widget.dialog

import android.content.Context
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.ObservableField
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.databinding.McSdkDialogAddPositionBinding

import com.legend.modular_contract_sdk.ui.contract.AddPositionType
import com.legend.modular_contract_sdk.utils.inputfilter.DigitInputFilter
import com.legend.modular_contract_sdk.utils.TextWatcherTrickUtil
import com.legend.modular_contract_sdk.utils.getDouble
import com.legend.modular_contract_sdk.utils.inputfilter.DecimalDigitsInputFilter
import com.lxj.xpopup.core.CenterPopupView

class AddPositionDialog(context: Context, val onConfirm: (Double, AddPositionType) -> Unit) :
    CenterPopupView(context) {

    lateinit var mBinding: McSdkDialogAddPositionBinding

    private var mAddPositionCount = ObservableField<String>("")

    override fun getImplLayoutId() = R.layout.mc_sdk_dialog_add_position

    override fun onCreate() {
        super.onCreate()
        mBinding = McSdkDialogAddPositionBinding.bind(popupImplView)
        mBinding.addPositionType = AddPositionType.SHEET.type
        mBinding.addPositionCount = mAddPositionCount
        mBinding.etClosePositionCount.filters = arrayOf(DecimalDigitsInputFilter(6, 0))

        mBinding.tvAddPositionType2.setOnClickListener {
            mBinding.etClosePositionCount.setText("")
            if (mBinding.addPositionType == AddPositionType.SHEET.type) {
                mBinding.addPositionType = AddPositionType.AMOUNT.type
                mBinding.etClosePositionCount.filters = arrayOf(DecimalDigitsInputFilter(6, 4))
            } else {
                mBinding.addPositionType = AddPositionType.SHEET.type
                mBinding.etClosePositionCount.filters = arrayOf(DecimalDigitsInputFilter(6, 0))
            }
        }

        mBinding.btnCancel.setOnClickListener {
            dismiss()
        }

        mBinding.btnConfirm.setOnClickListener {
            if (mAddPositionCount.get()!!.isEmpty()) {
                Toast.makeText(context, R.string.mc_sdk_add_position_input_tip, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val type = if (mBinding.addPositionType == AddPositionType.SHEET.type) {
                AddPositionType.SHEET
            } else {
                AddPositionType.AMOUNT
            }
            onConfirm(mAddPositionCount.get().getDouble(), type)
            dismiss()
        }
    }
}
