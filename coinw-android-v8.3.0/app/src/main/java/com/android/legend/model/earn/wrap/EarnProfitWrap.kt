package com.android.legend.model.earn.wrap

import com.android.coinw.biz.trade.helper.TradeDataHelper
import com.android.legend.model.earn.EarnProfit
import huolongluo.byw.util.pricing.PricingMethodUtil

class EarnProfitWrap (val earnProfit: EarnProfit){
    fun getTotalAssets() = PricingMethodUtil.getResultByExchangeRate(earnProfit.totalBalance.toString(), "USDT")

    fun getYesterdayProfit() = PricingMethodUtil.getResultByExchangeRate(earnProfit.yesterdayProfit.toString(), "USDT")

    fun getMonthProfit() = PricingMethodUtil.getResultByExchangeRate(earnProfit.monthlyProfit.toString(), "USDT")
}