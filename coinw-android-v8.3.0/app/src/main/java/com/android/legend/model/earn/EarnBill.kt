package com.android.legend.model.earn

data class EarnBill(
    val name: String,
    val amount: Double,
    val creationTime: Long,
    val currencyName: String,
    val financialType: Int,
    val productClassifiy: Int,
    val type: String
)