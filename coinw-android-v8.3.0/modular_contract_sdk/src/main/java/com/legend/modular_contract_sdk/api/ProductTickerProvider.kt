package com.legend.modular_contract_sdk.api

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.legend.modular_contract_sdk.component.market_listener.*
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.repository.setvices.MarketService
import com.orhanobut.logger.Logger
import kotlinx.coroutines.*

object ProductTickerProvider {

    val mMarketListenerList = mutableListOf<MarketListener>()

    var mProductList: MutableList<Product>?=null

    var mTickerCallback: ((ticker: Ticker) -> Unit)? = null
    var mProductCallback: ((products: MutableList<Product>) -> Unit)? = null

    fun start() {
        fetchAllProduct()
    }

    fun addTickerListener() {
        if (mProductList!=null) {
            mProductList!!.forEach { product ->
                val tickerLiveData = MutableLiveData<MarketData>()
                mMarketListenerList.add(
                    MarketListenerManager.subscribe(
                        MarketSubscribeType.TickerSwap(
                            product.mBase,
                            "usd"
                        ),
                        tickerLiveData
                    )
                )
                tickerLiveData.observeForever {
                    val ticker = it as Ticker
                    mTickerCallback?.invoke(ticker)
                }

            }
        }

    }

    private fun fetchAllProduct() {
        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    val productResp = MarketService.instance().fetchProducts()
                    if (productResp.isSuccess) {
                        productResp.data?.apply {
                            mProductList = mutableListOf()
                            mProductList!!.addAll(this)
                            withContext(Dispatchers.Main) {
                                mProductCallback?.invoke(
                                    mProductList!!
                                )
                                if (mTickerCallback != null) {
                                    addTickerListener()
                                }
                            }
                        }
                        break
                    }
                    delay(5000)
                } catch (e: Exception) {
                    Logger.e(e, e.message ?: "No Message")
                    delay(5000)
                }

            }

        }
    }
}