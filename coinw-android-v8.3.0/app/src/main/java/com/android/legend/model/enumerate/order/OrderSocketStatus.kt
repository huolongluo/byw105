package com.android.legend.model.enumerate.order

enum class OrderSocketStatus(val status:String) {
    //open：新增订单  match：已有订单部分成交，更新成交数量 done:移除
    OPEN("open"),MATCH("match"),DONE("done"),
}