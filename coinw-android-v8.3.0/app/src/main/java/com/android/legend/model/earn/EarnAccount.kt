package com.android.legend.model.earn

/**
 * 理财账户余额
 */
data class EarnAccountBalance(
        val id: Int,
        val userId: Int,
        val currencyId: Int,
        val totalBalance: Double,
        val availableBalance: Double,
        val holdBalance: Double,
        val status: Int,
        val updatedAt: Long,
        val createdAt: Long
)


/**
 * 理财账户分币种余额
 */
data class EarnAccountCoin(
    val availableBalance: Double,
    val coinCode: String,
    val coinFullName: String,
    val coinId: Int,
    val investBalance: Double,
    val totalBalance: Double
)