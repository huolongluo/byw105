package com.android.legend.model.order
//当前订单的推送数据
data class OrderSocketBean(
        val orderId:Long,
        val price:String,
        val remainSize:Double,//未成交数量(需要更新该值)
        val size:String,//本次推送数据成交的数量
        val status:String,//本次推送的类型 open：新增订单  match：已有订单部分成交，更新成交数量 done:移除
        val symbol:String
)