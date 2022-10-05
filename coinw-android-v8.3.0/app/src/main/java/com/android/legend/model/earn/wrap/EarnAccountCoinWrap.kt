package com.android.legend.model.earn.wrap

import com.android.legend.model.earn.EarnAccountCoin
import com.legend.modular_contract_sdk.utils.getNum
import huolongluo.byw.util.pricing.PricingMethodUtil

class EarnAccountCoinWrap(val earnAccountCoin: EarnAccountCoin) {

    var mHindAmount = false

    fun getTotalAssets() = if (mHindAmount) {
        "********"
    } else {
//        PricingMethodUtil.getResultByExchangeRate(earnAccountCoin.totalBalance.toString(), "USDT")
        earnAccountCoin.totalBalance.toString().getNum(8, withZero = true)
    }

    fun getAvailableAssets() = if (mHindAmount) {
        "********"
    } else {
//        PricingMethodUtil.getResultByExchangeRate(earnAccountCoin.availableBalance.toString(), "USDT")
        earnAccountCoin.availableBalance.toString().getNum(8, withZero = true)
    }

    fun getHoldAssets() = if (mHindAmount) {
        "********"
    } else {
//        PricingMethodUtil.getResultByExchangeRate(earnAccountCoin.investBalance.toString(), "USDT")
        earnAccountCoin.investBalance.toString().getNum(8, withZero = true)
    }
}