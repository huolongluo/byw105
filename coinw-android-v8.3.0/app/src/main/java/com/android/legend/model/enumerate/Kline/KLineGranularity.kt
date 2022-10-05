package com.android.legend.model.enumerate.Kline
//k线数据的粒度
enum class KLineGranularity(val value:String) {
    //粒度固定值，可选枚举不区分大小写[1m, 3m, 5m, 15m, 30m, 1h, 2h, 4h, 6h, 12h, 1d, 1w,1M] 区分大小写
    MIN1("1m"),MIN5("5m"),MIN15("15m"),MIN30("30m"),HOUR1("1h"),HOUR4("4h"),HOUR6("6h"),
    DAY1("1d"),WEEK1("1w"),MONTH1("1M")
}