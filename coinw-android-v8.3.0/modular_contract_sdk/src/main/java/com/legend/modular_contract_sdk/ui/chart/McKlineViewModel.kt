package com.legend.modular_contract_sdk.ui.chart

import androidx.lifecycle.MutableLiveData
import com.legend.modular_contract_sdk.base.BaseViewModel
import com.legend.modular_contract_sdk.common.postValueNotNull
import com.legend.modular_contract_sdk.component.market_listener.CandleList
import com.legend.modular_contract_sdk.component.market_listener.Candles
import com.legend.modular_contract_sdk.component.market_listener.MarketData
import com.legend.modular_contract_sdk.component.net.LoadState
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.repository.model.USDRate
import com.legend.modular_contract_sdk.repository.setvices.MarketService

class McKlineViewModel : BaseViewModel() {

    var mChartLiveData: MutableLiveData<MarketData> = MutableLiveData()
    var mTickerLiveData: MutableLiveData<MarketData> = MutableLiveData()
    var mAllChartLiveData: MutableLiveData<MarketData> = MutableLiveData()
    var mChartLoadStateLiveData: MutableLiveData<LoadState> = MutableLiveData()
    var mPriceLiveData: MutableLiveData<MarketData> = MutableLiveData()
    var mRateLiveData: MutableLiveData<USDRate> = MutableLiveData()
    var mProductLiveData: MutableLiveData<List<Product>> = MutableLiveData()

    fun fetchCandles(currency: String, timeUnit: String) {
        request({
            mChartLoadStateLiveData.postValue(LoadState.Failure(it.message ?: ""))
        }) {
            mChartLoadStateLiveData.postValue(LoadState.StartLoading())
            MarketService.instance().fetchCandles(currency, timeUnit)
                .apply {
                    if (isSuccess) {
                        val candleList = data?.map {
                            Candles(it)
                        }.let {
                            CandleList(it!!, timeUnit)
                        }
                        mAllChartLiveData.postValueNotNull(candleList)
                        mChartLoadStateLiveData.postValue(LoadState.Complete())
                    } else {
                        mChartLoadStateLiveData.postValue(LoadState.Complete())
                    }

                }
        }
    }

    fun fetchUsdRate() {
        request {
            MarketService.instance().fetchRate()
                .apply {
                    if (isSuccess) {
                        mRateLiveData.postValueNotNull(data)
                    }
                }
        }
    }

    //获取所有交易对
    fun fetchAllProduct() {
        request {
            MarketService.instance().fetchProducts()
                .apply {
                    if (isSuccess) {
                        mProductLiveData.postValueNotNull(data)
                    }
                }
        }
    }

}