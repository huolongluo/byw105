package com.android.legend.model

data class CurrencyInfo(
    val coinCode: String,
    val fisHbtCoin: Boolean,
    val fisHyperpayRecharge: Boolean,
    val fisHyperpayWithdraw: Boolean,
    val fullName: String,
    val hyperpayName: String,
    val id: Int,
    val innovationZone: Boolean,
    val logo: String,
    val shortName: String,
    val spotStatus: Int,
    val status: Int,
    val type: Int
)