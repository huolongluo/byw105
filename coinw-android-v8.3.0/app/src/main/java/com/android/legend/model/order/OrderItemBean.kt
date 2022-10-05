package com.android.legend.model.order

import android.text.TextUtils
import com.android.coinw.biz.trade.model.OrderSide
import huolongluo.byw.util.MathHelper
import huolongluo.byw.util.noru.NorUtils
import huolongluo.bywx.utils.DoubleUtils

data class OrderItemBean(
        val active: Boolean,//是否为活跃订单
        val cancelAfter: Long,//timeInForce 为 GTT n秒后触发
        val clientOid: String,//用户指定orderId
        val dealAvgPrice: String,//平均成交价
        val dealFunds: String,//成交总额
        var dealSize: String,//成交数量
        val fee: String,//手续费
        val hidden: Boolean,//是否隐藏单
        val iceberg: Boolean,//是否冰山单
        val orderId: Long,//用户指定orderId
        val postOnly: Boolean,//是否为被动委托
        val price: String,//订单价格
        val reason: String,//原因
        val side: String,//方向-买卖
        val size: String,//订单数量(委托量)
        val funds: String,// 委托额， 市价买入时使用
        val status: String,//订单状态
        val stop: String,//止盈止损类型， entry:止盈; loss:止损。
        val stopPrice: String,//触发价格,就必须设置此属性
        val stp: String,//自成交保护策略，DC-取消数量少的一方的订单，并将数量多的一方数量改为新旧差值，CO-取消旧的订单，CN-取消新的订单，CB-双方都取消
        val symbol: Long,//交易对id
        val time: Long,//下单时间
        val timeInForce: String,//订单时效策略;GTC-主动取消，GTT–指定时间后过期，IOC-立即成交可成交的部分，然后取消剩余部分，不进入买卖盘，FOK-如果下单不能全部成交，则取消
        val type: String,//订单类型:LIMIT-限价，MARKET-市价
        val visibleSize: String//冰山单在买卖盘可见数量
){
    fun getNoDealSize():String{

        val numberFormatNo = NorUtils.NumberFormatNo(15);
        return numberFormatNo.format(MathHelper.subReturnBigDecimal(size, dealSize))
    }

    fun getDealPrice():String{
        return if(dealAvgPrice == null || dealAvgPrice.isEmpty()){
            "0.0"
        } else {
            dealAvgPrice
        }
    }

    fun isBuy() = TextUtils.equals(side, OrderSide.BUY.side)


}