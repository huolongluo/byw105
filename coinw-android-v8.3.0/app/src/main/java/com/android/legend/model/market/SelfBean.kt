package com.android.legend.model.market

data class SelfBean(
        val fcreateTime:String,//添加自选时间
        val trademId:String,
        val type:String,//币对类型SPOT,ETF,CONTRACT
        val uId:Long//用户id
)