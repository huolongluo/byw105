package com.android.legend.model.kline
//etf需要的费率和净值
data class NetValueBean(
        val marketName:String,//BTC_USDT
        val price:String,//净值
        val etfFundsRate:String,//资金费率
        val timestamp:Long//返回时间戳
)