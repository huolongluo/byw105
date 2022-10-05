package com.android.coinw.biz.trade

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.coinw.api.kx.model.XDepthData
import com.android.coinw.api.kx.model.XLatestDeal
import com.android.legend.api.ApiRepository
import com.android.legend.common.BusMutableLiveData
import com.android.legend.model.BaseResponse
import com.android.legend.model.CommonResult
import com.android.legend.model.Page2
import com.android.legend.model.kline.NetValueBean
import com.android.legend.model.order.OrderItemBean
import com.android.legend.model.order.OrderResultBean
import com.android.legend.model.order.OrderSocketBean
import kotlinx.coroutines.launch

/**
 * 币币交易的viewmodel  注意：每添加一个livedata需要在BaseView的unRegisterObserver内加上
 */
class TradeViewModel :ViewModel(){
    val orderResult=BusMutableLiveData<CommonResult<OrderResultBean>>()
    val ordersData=BusMutableLiveData<CommonResult<Page2<List<OrderItemBean>>>>()
    val depthData=BusMutableLiveData<CommonResult<XDepthData>>()//http的行情快照（深度）
    val cancelOrderData=BusMutableLiveData<CommonResult<String>>()
    val netValueData=MutableLiveData<CommonResult<BaseResponse<NetValueBean>>>()//读取净值和费率
    val latestData=MutableLiveData<CommonResult<List<XLatestDeal>>>()//最新成交

    val orderSocketData=BusMutableLiveData<OrderSocketBean>()//收到订单的socket数据

    fun order(price:String,size:String,funds:String,side:String,symbol:String,type:String)=viewModelScope.launch {
        orderResult.value=ApiRepository.instance.order(price,size,funds,side,symbol,type)
    }
    fun getOrders(active: Boolean, before: Long, startAt: Long?, endAt:Long?,symbol: String?,status:String?,side: String?)
            =viewModelScope.launch {
        ordersData.value=ApiRepository.instance.getOrders(active,before,startAt,endAt,symbol,null,status,side)
    }
    fun cancelOrder(orderId:Long)=viewModelScope.launch {
        cancelOrderData.value=ApiRepository.instance.cancelOrder(orderId)
    }
    fun getDepthData(symbol:String)=viewModelScope.launch {
        depthData.value=ApiRepository.instance.getDepthData(symbol)
    }
    fun getNetValue(symbol:String)=viewModelScope.launch {
        netValueData.value=ApiRepository.instance.getNetValue(symbol)
    }
    fun getLatestData(symbol:String)=viewModelScope.launch {
        latestData.value=ApiRepository.instance.getLatestData(symbol)
    }
    fun setOrderSocketData(data:OrderSocketBean){
        orderSocketData.value=data
    }
}