package com.android.legend.model.home

import android.text.TextUtils
import com.legend.modular_contract_sdk.utils.getDouble
import com.legend.modular_contract_sdk.utils.getNum
import huolongluo.byw.byw.base.BaseApp
import huolongluo.byw.util.Util
import huolongluo.byw.util.pricing.PricingMethodUtil

class TickerWrap(val ticker: Ticker? = null, val ticker2: Ticker2? = null) {

    fun getId(): Int {
        return if (ticker != null) {
            ticker.tmId
        } else {
            ticker2?.id?.toInt() ?: 0
        }
    }

    fun getProductName() = getBaseName() + getQuoteName()

    fun getBaseName(): String {
        return if (ticker != null) {
            return ticker.leftCoinName
        } else {
            ticker2?.coinName ?: ""
        }

    }

    fun getQuoteName(): String {
        return if (ticker != null) {
            "/" + ticker.rightCoinName
        } else {
            "/" + (ticker2?.cnyName)
        }
    }

    fun getRealQuoteName(): String {
        return if (ticker != null) {
            ticker.rightCoinName
        } else {
            ticker2?.cnyName ?: ""
        }
    }

    fun getChange(): String {
        return if (ticker != null) {
            (ticker.rose.getDouble() * 100).toString().getNum(2, true, withSymbol = true) + "%"
        } else {
            ((ticker2?.priceRaiseRate?.getDouble()
                    ?: 0.0) * 100).toString().getNum(2, true, withSymbol = true) + "%"
        }

    }

    fun getLast(): String {
        if (ticker != null) {
            return ticker.price
        }
        return ticker2?.LatestDealPrice ?: ""
    }


    // 折合法比
    fun getConvertCurrency(): String {
        return if (ticker != null) {
            if (TextUtils.isEmpty(ticker.price)) {
                "≈ ${PricingMethodUtil.getPricingUnit()} --"
            } else {
                val result = PricingMethodUtil.getResultByExchangeRate(ticker.price, ticker.rightCoinName)
                "≈ ${PricingMethodUtil.getPricingUnit()} $result"
            }
        } else {
            if (TextUtils.isEmpty(ticker2?.currencyPrize)) {
                "≈ ${PricingMethodUtil.getPricingUnit()} --"
            } else {
                val result = PricingMethodUtil.getResultByExchangeRate(ticker2?.LatestDealPrice, ticker2?.cnyName)
                "≈ ${PricingMethodUtil.getPricingUnit()} $result"
            }
        }
    }

    fun isUp(): Boolean {
        return if (ticker != null) {
            ticker.rose.getDouble() >= 0
        } else {
            (ticker2?.priceRaiseRate?.getDouble() ?: 0.0) >= 0
        }
    }

    fun getVolume(): String {
        if (ticker2 != null && !TextUtils.isEmpty(ticker2.legalMoneyCny)) {
            return PricingMethodUtil.getLargePrice(PricingMethodUtil.getResultByExchangeRate(ticker2.legalMoneyCny, "CNY"))
        } else {
            return "- -"
        }
    }

    fun getTextSize(): Int {
        val last = getLast()
        return if (last.length >= 10) Util.sp2px(12) else Util.sp2px(18)
    }

    fun getMarginTop(): Int {
        val last = getLast()
        return if (last.length >= 10) Util.dp2px(BaseApp.getSelf(), 12.0f) else Util.dp2px(BaseApp.getSelf(), 6.0f)
    }
}