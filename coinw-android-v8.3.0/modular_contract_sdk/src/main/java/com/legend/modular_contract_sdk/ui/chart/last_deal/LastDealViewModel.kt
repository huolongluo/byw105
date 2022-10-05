package com.legend.modular_contract_sdk.ui.chart.last_deal

import androidx.lifecycle.MutableLiveData
import com.legend.modular_contract_sdk.base.BaseViewModel
import com.legend.modular_contract_sdk.common.postValueNotNull
import com.legend.modular_contract_sdk.component.market_listener.LastDealList
import com.legend.modular_contract_sdk.component.market_listener.MarketData
import com.legend.modular_contract_sdk.component.net.RetrofitInstance
import com.legend.modular_contract_sdk.repository.model.LastDeal
import com.legend.modular_contract_sdk.repository.setvices.MarketService

class LastDealViewModel: BaseViewModel() {
    // API获取的全量数据
    val mAllLastDealLiveData = MutableLiveData<List<LastDeal>>()
    // webSocket 推送的增量数据
    val mLastDealLiveData = MutableLiveData<MarketData>()

    fun fetchAllLastDeal(){
        request {
            MarketService.instance().fetchAllLastDeal()
                    .apply {
                        if (this.isSuccess){
                            mAllLastDealLiveData.postValueNotNull(this.data)
                        }
                    }
        }
    }

}