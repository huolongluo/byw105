package com.android.coinw.api.kx.model

data class X24HData(
        val changePrice: String,//24h涨跌价格
        val changeRate: String,//24h涨跌幅
        val last: String,//最新价
        val high: String,//最高价
        val low: String,//最低价
        val symbol: String, //交易对 id
        val time: String,
        val vol: String,//24h成交量
        val volValue: String//成交额
 )