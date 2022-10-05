package com.android.legend.model.earn


/**
 * 理财总收益
 */
data class EarnProfit(
        val userId: Int,
        val monthlyProfit: Double,
    val totalBalance: Double,
    val yesterdayProfit: Double
)