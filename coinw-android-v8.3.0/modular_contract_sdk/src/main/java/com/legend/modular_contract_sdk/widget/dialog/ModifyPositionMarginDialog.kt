package com.legend.modular_contract_sdk.widget.dialog

import android.content.Context
import android.widget.Toast
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.BR
import com.legend.modular_contract_sdk.databinding.McSdkDialogModifyPositionMarginBinding
import com.legend.modular_contract_sdk.repository.model.ContractAssetInfo
import com.legend.modular_contract_sdk.repository.model.wrap.PositionWrap
import com.legend.modular_contract_sdk.utils.CalculateUtil
import com.legend.modular_contract_sdk.utils.getDouble
import com.legend.modular_contract_sdk.utils.getNum
import com.legend.modular_contract_sdk.utils.inputfilter.DecimalDigitsInputFilter
import com.lxj.xpopup.core.BottomPopupView

import kotlin.math.min

class ModifyPositionMarginDialog(context: Context, val usableBalances: ContractAssetInfo, val positionWrap: PositionWrap, val onConfirm: (amount: String, type: Int) -> Unit) : BottomPopupView(context) {

    lateinit var mBinding: McSdkDialogModifyPositionMarginBinding

    val mAmount = ObservableField<String>("")

    // 最大可增加|减少 的保证金
    val mMaxModifyMargin = ObservableField<String>("")

    // 预估爆仓价
    val mLiquidationPrice = ObservableField<String>("")

    // 增加还是减少 1增加 -1 减少
    val mType = ObservableInt(1)

    override fun getImplLayoutId() = R.layout.mc_sdk_dialog_modify_position_margin

    var mMaxAdd = ""
    var mMaxMinus = ""
    var mMaxAddNum = 0.0
    var mMaxMinusNum = 0.0

    override fun onCreate() {
        super.onCreate()
        mBinding = McSdkDialogModifyPositionMarginBinding.bind(popupImplView)

        mBinding.amount = mAmount

        mBinding.maxModifyMargin = mMaxModifyMargin

        mBinding.liquidationPrice = mLiquidationPrice

        mBinding.type = mType

        mBinding.position = positionWrap

        initMaxNum()

        mMaxModifyMargin.set(mMaxAdd)

        calcLiquidationPrice()

        mBinding.btnCancel.setOnClickListener {
            dismiss()
        }

        mBinding.tvSwitch.setOnClickListener {
            mType.set(if (mType.get() == 1) -1 else 1)
            mAmount.set("")
            if (mType.get() == 1) {
                mMaxModifyMargin.set(mMaxAdd)
            } else {
                mMaxModifyMargin.set(mMaxMinus)
            }
        }

        mBinding.etAmount.filters = arrayOf(DecimalDigitsInputFilter(100, 4))

        mBinding.tvMax.setOnClickListener {

            if (mType.get() == 1) {
                mAmount.set(mMaxAdd)
                selectLast()
            } else {
                mAmount.set(mMaxMinus)
                selectLast()
            }
        }

        mAmount.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                calcLiquidationPrice()

                if (mType.get() == 1) {
                    if (mAmount.get().getDouble() > mMaxAddNum) {
                        mAmount.set(mMaxAdd)
                        selectLast()
                    }
                } else {
                    if (mAmount.get().getDouble() > mMaxMinusNum) {
                        mAmount.set(mMaxMinus)
                        selectLast()
                    }
                }


            }

        })

        mBinding.btnConfirm.setOnClickListener {
            if (mAmount.get().isNullOrEmpty() || mAmount.get().getDouble() <= 0) {
                return@setOnClickListener
            }
            dismiss()
            onConfirm(mAmount.get()!!, mType.get())
        }

    }

    private fun selectLast() {
        mBinding.etAmount.postDelayed({
            mBinding.etAmount.setSelection(mAmount.get()!!.length)
        }, 100)
    }

    //初始化最大可增加可减少
    private fun initMaxNum() {
        //最大可增加金额=逐仓账户余额-逐仓占用保证金
        //最大可减少金额=min（A，B）其中：
        //    A=SUM（仓位保证金）-开仓占用保证金
        //    B=SUM（仓位保证金）-开仓占用保证金+未实现盈亏
        //    =SUM（仓位保证金）-开仓占用保证金+合约方向*basesize*（标记价-开仓价）
        //    合约方向：多1，空-1
        mMaxAddNum = usableBalances.mAvailable
        mMaxAdd = usableBalances.mAvailable.toString().getNum(4, withZero = true)

        val profit = positionWrap.getRealProfit()
        val A = positionWrap.position.mMargin - positionWrap.position.mPositionMargin
        val B = if (profit < 0) {
            (A + profit) * 0.95
        } else {
            A + profit
        }
        var maxMinusMargin = min(A, B)
        if (maxMinusMargin < 0) {
            maxMinusMargin = 0.0
        }
        mMaxMinusNum = maxMinusMargin
        mMaxMinus = maxMinusMargin.toString().getNum(4, withZero = true)
    }

    private fun calcLiquidationPrice() {
        val margin = positionWrap.position.mMargin + (mType.get() * mAmount.get().getDouble())
        positionWrap.product?.let {
            mLiquidationPrice.set(CalculateUtil.getPartPositionLiquidationPrice(positionWrap, it, margin))
        }
    }
}