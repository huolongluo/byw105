package com.legend.modular_contract_sdk.component.market_listener

import com.google.gson.annotations.SerializedName
import com.legend.modular_contract_sdk.repository.model.LastDeal


/**
 * 外层数据 用于判断类型
 */
open class BaseData(
    var base: String,
    var quote: String,
    var biz: String,
    var type: String,
    var granularity: String?,
    var zip: Boolean,
    var data: Any?
)

open class MarketData

/**
 * 资金费率
 */
data class FundingRate(var r: String = "", var nt: Long = 0) : MarketData()


/**
 * 标记价格 指数价格
 */
data class Price(var p: String = "", var n: String = "") : MarketData()


/**
 * 深度
 */
data class Depth(
    var t: Long = 0,
    var asks: MutableList<PendingOrder> = mutableListOf(),
    var bids: MutableList<PendingOrder> = mutableListOf(),
    var n: String = ""
) : MarketData() {
    data class PendingOrder(var p: String = "", var m: String = "")
}

/**
 * Ticker {"high":"40100","last":"39306.07","low":"36708.89","changeRate":"0.054302","type":"MARKET","currencyCode":"btc","open":"37281.6"}
 */
data class Ticker(
    var open: String = "",
    var high: String = "",
    var low: String = "",
    var last: String = "",
    var changeRate: String = "",
    var type: String = "",
    var currencyCode: String = ""
) : MarketData()

//["1617947100000","58101.4","58115.6","58008.1","58015.1","0.0"]
//顺序 时间，开，高，低,收
data class Candles(var data: List<String>) : MarketData() {
    fun getTime() = data[0]
    fun getOpen() = data[1]
    fun getHigh() = data[2]
    fun getLow() = data[3]
    fun getClose() = data[4]
    fun getVolume() = data[5]
}

data class CandleList(var candleList: List<Candles>, var timeUnit:String) : MarketData()

data class Assets(
    @SerializedName("availableMargin")
    val mAvailableMargin: Double,
    @SerializedName("currency")
    val mCurrency: String,
    @SerializedName("hold")
    val mHold: Double,
    @SerializedName("margin")
    val mMargin: Double,
    @SerializedName("profitUnreal")
    val mProfitUnreal: Double,
    @SerializedName("userId")
    val mUserId: Int
) : MarketData()

data class LastDealList(var dataList: List<LastDeal>) : MarketData()