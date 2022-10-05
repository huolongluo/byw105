package com.legend.modular_contract_sdk.ui.chart

enum class CandlesTimeUnit(
    var id: Int,
    var timeUnit: String,
    var longTime: Long,
    var previous: CandlesTimeUnit?
) {
    //    SEC1(0, "1S", 1, null, 0),
//    SEC15(1, "15S", 15, SEC1, 15),
    MIN1(2, "1", 1 * 60, null),
    MIN_1(1, "1", 1 * 60, MIN1),
    MIN5(
        3, "5", 5 * 60,
        MIN1
    ),
    MIN15(
        4, "15", 15 * 60,
        MIN5
    ),
    HUOR1(
        5, "1H", 60 * 60,
        MIN15
    ),
    HUOR4(
        6, "4H", 240 * 60,
        HUOR1
    ),
    DAY(
        8, "1D", 1440 * 60,
        HUOR4
    ),
    WEEK(
        9, "1W", 10080 * 60,
        DAY
    );
}

enum class MainIndicator(var id: Int, var indicatorName: String) {
    MA(1, "MA"),
    EMA(1, "EMA"),
    BOLL(1, "BOLL")
}

enum class SubIndicator(var id: Int, var indicatorName: String) {
    MACD(1, "MACD"),
    KDJ(1, "KDJ"),
}