package com.legend.modular_contract_sdk.ui.chart.depth

import androidx.lifecycle.MutableLiveData
import com.legend.modular_contract_sdk.base.BaseViewModel
import com.legend.modular_contract_sdk.component.market_listener.MarketData

class DepthViewModel : BaseViewModel() {
    val mDepthLiveData = MutableLiveData<MarketData>()
}