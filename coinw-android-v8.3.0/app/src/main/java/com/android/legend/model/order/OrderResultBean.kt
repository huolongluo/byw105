package com.android.legend.model.order
//币币下单结果
data class OrderResultBean(
        val clientOid:String,
        val orderId:Long,
        val status:String,
        val side:String
)