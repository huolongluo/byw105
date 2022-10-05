package com.android.legend.model.market

data class MarketPairBean(
        val activityState: Int,//老系统活动状态0-无活动 1-活动中
        val activityUrl: String,//老系统活动url
        val fPartitionIds: String,//分区id集合分别对应分区id  "1,2,3"
        val fav: Boolean,//是否是自选
        val futTag: String,//老系统期货tag
        val isPrioriyty: Boolean,//老系统是否是创新区废弃
        val leftCoinName: String,//左侧币种名称
        val leftCoinUrl: String,//币种logo
        val oneDayHighest: String,//24h最高价格
        val oneDayHighestExchange: String,//24h人民币计价价格最高价格
        val oneDayLowest: String,//24h最低价格
        val oneDayLowestExchange: String,//24h人民币计价价格最低价格
        val oneDayTotal: String,//24小时成交总量
        val oneDayTotalExchange: String,//24小时成交总量人民币计价
        val price: String,//最新价格
        val rightCoinName: String,
        val rose: String,//涨跌比例
        val symbol: String,//法币标识符
        val tmId: String,//币对id
        val tradeAreaId: String,//老系统分区id （废弃）
        val tradeRule: String,//老系统交易规则状态 1- 开启
        val tradeRuleDesc: String,//老系统交易规则tips
        val transferPrice: String,//老系统换算币种价格
        val transferSymbol: String,//老系统换算币种符号
        val type: String,//老系统分区类型
        val whetherQuotation: String//老系统是否是新币
)