package com.legend.modular_contract_sdk.coinw

import android.content.Context
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.api.ModularContractSDK
import com.legend.modular_contract_sdk.component.market_listener.MarketListener
import com.legend.modular_contract_sdk.component.market_listener.MarketListenerManager
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.utils.ToastUtils
import kotlin.collections.ArrayList

/**
 * 合约和coinw相关的工具类
 * 减少coinw app和合约module的关联
 */
class CoinwHyUtils {
    companion object{
        @JvmField var isServiceStop = false //全局标记是否为停机维护状态中d
        @JvmField var products:ArrayList<Product>?=null//所有币对列表
        @JvmField var productsMap = mutableMapOf<String,Int>()//用于通过币名(BTC)读取精度

        /**
         * 检查是否处于停机维护中
         * @return
         */
        @JvmStatic fun checkIsStopService(context: Context): Boolean {
            if (isServiceStop) {
                ToastUtils.showShortToast(R.string.mc_sdk_stop_service)
            }
            return isServiceStop
        }

        /**
         * coinw行情读取tab列表
         */
        @JvmStatic fun getMarketTabs(context: Context):ArrayList<CoinwHyMarketTab>?{
            return arrayListOf(CoinwHyMarketTab(1,context.getString(R.string.mc_sdk_usdt_contract)))
        }
        @JvmStatic fun removeAllMarketListener(marketListenerList: MutableList<MarketListener>){
            marketListenerList.forEach {
                MarketListenerManager.unsubscribe(it)
            }
            marketListenerList.clear()
        }

        /**
         * 判断是否需要跳转到其他页面，比如登录和协议开通
         */
        @JvmStatic fun isToOtherPage():Boolean{
            if (!ModularContractSDK.userIsLogin()) {
                ModularContractSDK.mEventCallback?.needLogin()
                return true
            }
            return false
        }

        /**
         * 通过币名（BTC）获取精度
         */
        fun getPricePrecision(coinName:String):Int{
            return if(productsMap==null) 2 else productsMap[coinName.toUpperCase()]?:2
        }
    }
}