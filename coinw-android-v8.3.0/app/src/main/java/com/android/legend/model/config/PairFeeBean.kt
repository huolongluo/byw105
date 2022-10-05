package com.android.legend.model.config
//交易对对应的手续费率
data class PairFeeBean(
        val makerFee:String,
        val pairId:Long,
        val takerFee:String
)