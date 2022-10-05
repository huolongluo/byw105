package com.legend.modular_contract_sdk.repository.model.wrap

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.databinding.BaseObservable
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils

import com.legend.modular_contract_sdk.repository.model.PositionAndOrder
import com.legend.modular_contract_sdk.repository.model.enum.McContractModeEnum
import com.legend.modular_contract_sdk.ui.contract.*
import com.legend.modular_contract_sdk.utils.SPUtils
import com.legend.modular_contract_sdk.utils.TimeUtils
import com.legend.modular_contract_sdk.utils.getNum
import com.legend.modular_contract_sdk.utils.getPrecision

class OrderWrap(val order: PositionAndOrder) : BaseObservable() {

    private val mPositionWrap by lazy {
        PositionWrap(order)
    }

    fun getContractName() = mPositionWrap.getContractName()

    fun isExpGold() = mPositionWrap.isExpGold()

    fun getDirection(context: Context) = mPositionWrap.getPositionDesc(context)

    fun isOpenOrder() = mPositionWrap.isOpenOrder()

    fun getDirectionIsLong() = mPositionWrap.getDirectionIsLong()

    fun getDirectionBackground(context: Context) = mPositionWrap.getDirectionBackgroundByHistory(context)

    fun getLeverage(context: Context): String {
        return "${String.format(context.getString(R.string.mc_sdk_contract_leverage_unit), order.mLeverage, "", "")}"
    }

    fun getOrderPrice(): String {
        return order.mOrderPrice.toString().getNum(CoinwHyUtils.getPricePrecision(order.mInstrument), true)
    }

    fun getEntrustCount(): String {
        val quantityUnit = SPUtils.getTradeUnit()
        return getEntrustCount(quantityUnit)
    }

    private fun getEntrustCount(quantityUnit: Int = QuantityUnit.SIZE.unit): String{
        return if (quantityUnit == QuantityUnit.SIZE.unit){
            order.mCurrentPiece.toString().getNum(0)
        } else if (quantityUnit == QuantityUnit.USDT.unit){

            (order.mOpenPrice * order.mBaseSize).toString().getNum(4)

        } else if (quantityUnit == QuantityUnit.COIN.unit){
            order.mBaseSize.toString().getNum(10)
        }

        else {
            ""
        }
    }

    fun getTradeUnitStr(context: Context): String {
        val quantityUnit = SPUtils.getTradeUnit()
        val unit = when (quantityUnit) {
            QuantityUnit.SIZE.unit -> {
                context.getString(R.string.mc_sdk_contract_unit)
            }
            QuantityUnit.USDT.unit -> {
                context.getString(R.string.mc_sdk_usdt)
            }
            QuantityUnit.COIN.unit -> {
                order.mInstrument.toUpperCase()
            }
            else -> {
                ""
            }
        }
        return unit
    }

    fun getMargin(): String {
        return if (mPositionWrap.isOpenOrder()) {
            order.mMargin.toString().getNum()
        } else {
            "- -"
        }
    }

    fun getCreateTime(context: Context): String {
        return context.getString(R.string.mc_sdk_contract_create_time) + " ${TimeUtils.millis2String(order.mCreatedDate)}"
    }

    fun getTakeProfit(): String {
        return if (order.mStopProfitPrice <= 0) {
            "- -"
        } else {
            order.mStopProfitPrice.toString().getNum(CoinwHyUtils.getPricePrecision(order.mInstrument), true)
        }
    }

    fun getStopLoss(): String {
        return if (order.mStopLossPrice <= 0) {
            "- -"
        } else {
            order.mStopLossPrice.toString().getNum(CoinwHyUtils.getPricePrecision(order.mInstrument), true)
        }
    }

    // 移动止盈止损回调率
    fun getCallbackRate(): String {
        val callbackRate = order.mCallbackRate.toBigDecimal()
        val mul = "100".toBigDecimal()
        return (callbackRate * mul).toString().getNum(2, withZero = true) + "%"
    }

    // 移动止盈止损回调率
    fun getMoveTPSLStatus(context: Context): String {
        return if (order.mFinishStatus == MoveTPSLStatus.ACTIVATED.status) {
            return context.getString(R.string.mc_sdk_modify_move_tp_sl_status_activated)
        } else if (order.mFinishStatus == MoveTPSLStatus.INACTIVATED.status) {
            return context.getString(R.string.mc_sdk_modify_move_tp_sl_status_inactivated)
        } else {
            ""
        }
    }

    fun getTriggerPrice(context: Context): String {
        if (order.mTriggerPrice <= 0) {
            return context.getString(R.string.mc_sdk_contract_market_price)
        }
        return if (getDirectionIsLong()) "≥" + order.mTriggerPrice.toString().getNum(CoinwHyUtils.getPricePrecision(order.mInstrument))
        else "≤" + order.mTriggerPrice.toString().getNum(CoinwHyUtils.getPricePrecision(order.mInstrument))
    }

    fun isMoveTPSLOrder(): Boolean {
        return order.mCallbackRate > 0
    }

}