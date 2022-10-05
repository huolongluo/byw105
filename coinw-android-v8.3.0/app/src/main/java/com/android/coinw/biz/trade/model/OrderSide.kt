package com.android.coinw.biz.trade.model
//下单的方向side为接口使用，type本地逻辑使用
enum class OrderSide (val side:String,val type:Int){
    BUY("BUY",0),SELL("SELL",1)
}