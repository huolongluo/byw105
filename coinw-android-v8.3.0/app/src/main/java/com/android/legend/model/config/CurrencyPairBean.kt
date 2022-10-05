package com.android.legend.model.config
//本地维护的交易对配置
data class CurrencyPairBean(
        val leftCoinName: String,
        val rightCoinName: String,
        val priceScale: Int,//价格精度
        val countScale: Int,//数量精度
        val tradeMappingName: String,//币对名称
        val tradeMappingId: Long,//币对id
        val pairFees:Map<String,PairFeeBean>//手续费率，key为用户账户等级，需要区分用户等级
        )