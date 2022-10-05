package com.android.legend.ui.market

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.legend.api.ApiRepository
import com.android.legend.common.BusMutableLiveData
import com.android.legend.model.CommonResult
import com.android.legend.model.market.MarketAreaBean
import huolongluo.byw.model.MarketResult
import kotlinx.coroutines.launch

class MarketViewModel : ViewModel() {

    val areaData = BusMutableLiveData<CommonResult<ArrayList<MarketAreaBean>>>()
    val pairData = BusMutableLiveData<CommonResult<MarketResult.SubMarketResult<MarketResult.Market>>>()

    fun getMarketArea() = viewModelScope.launch {
        areaData.value= ApiRepository.instance.getMarketArea()
    }
    fun getMarketPair() = viewModelScope.launch {
        pairData.value= ApiRepository.instance.getMarketPair()
    }
}