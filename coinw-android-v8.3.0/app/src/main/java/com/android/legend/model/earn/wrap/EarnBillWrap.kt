package com.android.legend.model.earn.wrap

import android.content.Context
import com.android.legend.model.earn.EarnBill
import com.android.legend.ui.earn.EarnBillActionType
import com.android.legend.ui.earn.EarnTimeLimitType
import com.legend.modular_contract_sdk.utils.TimeUtils
import com.legend.modular_contract_sdk.utils.getNum
import huolongluo.byw.R

class EarnBillWrap(val earnBill: EarnBill) {

    fun getEarnProductType(context:Context): String{
        return when(earnBill.productClassifiy){
            EarnTimeLimitType.CURRENT.type -> {
                context.getString(R.string.earn_current)
            }
            EarnTimeLimitType.REGULAR.type -> {
                context.getString(R.string.earn_regular)
            }
            EarnTimeLimitType.MIX.type -> {
                if (earnBill.financialType == EarnTimeLimitType.CURRENT.type) {
                    context.getString(R.string.earn_mix) + context.getString(R.string.earn_current)
                } else if (earnBill.financialType == EarnTimeLimitType.REGULAR.type) {
                    context.getString(R.string.earn_mix) + context.getString(R.string.earn_regular)
                } else {
                    context.getString(R.string.earn_mix)
                }
            }
            else -> {
                context.getString(R.string.earn_current)
            }
        }
    }

    fun getActionName(): Int {
        return when (earnBill.type) {
            EarnBillActionType.PROFIT.actionName -> {
                R.string.earn_profit
            }
            EarnBillActionType.SUBSCRIPTION.actionName -> {
                R.string.earn_buy
            }
            EarnBillActionType.REDEMPTION.actionName -> {
                R.string.earn_redemption
            }
            else -> {
                R.string.load_empty
            }
        }
    }

    fun getCreateDate(): String {
        return TimeUtils.millis2String(earnBill.creationTime)
    }

    fun getAmount(): String {
        return earnBill.amount.toString().getNum() + "  " + earnBill.currencyName
    }
}