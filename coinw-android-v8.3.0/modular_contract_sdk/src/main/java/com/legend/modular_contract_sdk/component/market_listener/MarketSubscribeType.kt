package com.legend.modular_contract_sdk.component.market_listener

import com.google.gson.annotations.Expose

sealed class MarketSubscribeType(
    val base: String,
    val quote: String,
    val biz: String,
    val type: String,
    val zip: Boolean = false,
    @Expose
    val needSendSubscribe: Boolean = true,// 是否需要发送订阅消息@Expose
    @Expose
    val isUserSubscribe: Boolean = false,// 是用户相关的订阅消息
    @Expose
    val include: Boolean = false // 是否包含这个Symbol的消息都会收到推送
) {

    fun getSymbol() = biz + type

    /**
     * 资金费率
     */
    class FundingRate(baseCurrency: String, quoteCurrency: String) :
        MarketSubscribeType(baseCurrency, quoteCurrency, "funding_rate", "funding_rate") {
        companion object {
            private val mInstance = FundingRate("", "")
            fun getSymbol() = mInstance.biz + mInstance.type
        }
    }

    /**
     * 标记价格
     */
    class MarkPrice(baseCurrency: String, quoteCurrency: String) :
        MarketSubscribeType(baseCurrency, quoteCurrency, "mark_price", "mark_price") {
        companion object {
            private val mInstance = MarkPrice("", "")
            fun getSymbol() = mInstance.biz + mInstance.type
        }
    }

    /**
     * 指数价格
     */
    class IndexPrice(baseCurrency: String, quoteCurrency: String) :
        MarketSubscribeType(baseCurrency, quoteCurrency, "index_price", "index_price") {
        companion object {
            private val mInstance = IndexPrice("", "")
            fun getSymbol() = mInstance.biz + mInstance.type
        }
    }

    /**
     * 盘口深度
     */
    class Depth(baseCurrency: String, quoteCurrency: String, type: String = "level5") :
        MarketSubscribeType(baseCurrency, quoteCurrency, "depth", type) {
        companion object {
            private val mInstance = Depth("", "")
            fun getSymbol() = mInstance.biz + mInstance.type
        }
    }

    /**
     * 最新价格 闪电合约
     */
    class Ticker(baseCurrency: String, quoteCurrency: String) :
        MarketSubscribeType(baseCurrency, quoteCurrency, "indexes", "ticker") {
        companion object {
            private val mInstance = Ticker("", "")
            fun getSymbol() = mInstance.biz + mInstance.type
        }
    }

    /**
     * K线 闪电合约
     */
    class Candles(
        baseCurrency: String,
        quoteCurrency: String,
        val granularity: String,
        val since: String
    ) :
        MarketSubscribeType(baseCurrency, quoteCurrency, "indexes", "candles") {
        companion object {
            private val mInstance = Candles("", "", "", "")
            fun getSymbol() = mInstance.biz + mInstance.type
        }
    }

    /**
     * 最新价格 永续合约
     */
    class TickerSwap(baseCurrency: String, quoteCurrency: String) :
        MarketSubscribeType(baseCurrency, quoteCurrency, "indexes", "ticker_swap") {
        companion object {
            private val mInstance = TickerSwap("", "")
            fun getSymbol() = mInstance.biz + mInstance.type
        }
    }

    /**
     * K线 永续合约
     */
    class CandlesSwap(
        baseCurrency: String,
        quoteCurrency: String,
        val granularity: String,
        val since: String
    ) :
        MarketSubscribeType(baseCurrency, quoteCurrency, "indexes", "candles_swap") {
        companion object {
            private val mInstance = CandlesSwap("", "", "", "")
            fun getSymbol() = mInstance.biz + mInstance.type
        }
    }

    // 所有的仓位信息变化都会通知
    class AllPosition() :
        MarketSubscribeType(
            "",
            "",
            "cfd",
            "position",
            needSendSubscribe = false,
            include = true,
            isUserSubscribe = true
        ) {
        companion object {
            private val mInstance = AllPosition()
            fun getSymbol() = mInstance.biz + mInstance.type
        }
    }


    class Position() : MarketSubscribeType(
        "",
        "",
        "cfd",
        "position",
        needSendSubscribe = true,
        isUserSubscribe = true
    ) {
        companion object {
            private val mInstance = Position()
            fun getSymbol() = mInstance.biz + mInstance.type
        }
    }

    // 加仓时只有position_change推送
    class PositionChange() :
        MarketSubscribeType(
            "",
            "",
            "cfd",
            "position_change",
            needSendSubscribe = true,
            isUserSubscribe = true
        ) {
        companion object {
            private val mInstance = PositionChange()
            fun getSymbol() = mInstance.biz + mInstance.type
        }
    }

    // 全部平仓时才有position_finish
    class PositionFinish() :
        MarketSubscribeType(
            "",
            "",
            "cfd",
            "position_finish",
            needSendSubscribe = true,
            isUserSubscribe = true
        ) {
        companion object {
            private val mInstance = PositionFinish()
            fun getSymbol() = mInstance.biz + mInstance.type
        }
    }

    // 全部平仓时才有position_finish
    class Assets() :
        MarketSubscribeType(
            "",
            "",
            "cfd",
            "assets",
            needSendSubscribe = true,
            isUserSubscribe = true
        ) {
        companion object {
            private val mInstance = Assets()
            fun getSymbol() = mInstance.biz + mInstance.type
        }
    }

    class LastDeal( baseCurrency: String,
                    quoteCurrency: String):
            MarketSubscribeType(
                    baseCurrency,
                    quoteCurrency,
                    "cfd",
                    "fills",
                    needSendSubscribe = true,
                    isUserSubscribe = false
            ){
        companion object {
            private val mInstance = LastDeal("", "")
            fun getSymbol() = mInstance.biz + mInstance.type
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MarketSubscribeType) return false

        if (base != other.base) return false
        if (quote != other.quote) return false
        if (biz != other.biz) return false
        if (type != other.type) return false
        if (zip != other.zip) return false

        return true
    }


}