package com.legend.modular_contract_sdk.ui.contract.history

import androidx.lifecycle.MutableLiveData
import com.legend.modular_contract_sdk.base.BaseViewModel
import com.legend.modular_contract_sdk.common.postValueNotNull
import com.legend.modular_contract_sdk.repository.model.BaseResp
import com.legend.modular_contract_sdk.repository.model.McBasePage
import com.legend.modular_contract_sdk.repository.model.PositionAndOrder
import com.legend.modular_contract_sdk.repository.setvices.ExperienceGoldService
import com.legend.modular_contract_sdk.repository.setvices.MarketService
import com.legend.modular_contract_sdk.utils.McConstants

class McHistoryOrderViewModel : BaseViewModel() {
    var mDataLiveData: MutableLiveData<List<PositionAndOrder>> = MutableLiveData()

    fun fetchOrderList(page: Int, mIsExperienceGold: Boolean = false) {
        request(isShowLoading = true) {
            if (mIsExperienceGold) {
                ExperienceGoldService.instance().fetchHistoryOrderList(page, McConstants.COMMON.PER_PAGE_SIZE, McConstants.COMMON.CONTRACT_TYPE_PERMANENT)
                        .apply {
                            if (isSuccess){
                                data!!.rows!!.forEach {
                                    it.mIsExperienceGold = true
                                }
                            }
                        }
            } else {
                MarketService.instance().fetchHistoryOrderList(page, McConstants.COMMON.PER_PAGE_SIZE, McConstants.COMMON.CONTRACT_TYPE_PERMANENT)
            }.apply {
                if (isSuccess) {
                    mDataLiveData.postValueNotNull(data?.rows)
                }
            }
        }
    }

    suspend fun getOrderListMore(page: Int = 1, mIsExperienceGold: Boolean = false): BaseResp<McBasePage<PositionAndOrder>> {
        return if (mIsExperienceGold) {
            ExperienceGoldService.instance().fetchHistoryOrderList(page, McConstants.COMMON.PER_PAGE_SIZE, McConstants.COMMON.CONTRACT_TYPE_PERMANENT)
                    .apply {
                        if (isSuccess){
                            data!!.rows!!.forEach {
                                it.mIsExperienceGold = true
                            }
                        }
                    }
        } else {
            MarketService.instance().fetchHistoryOrderList(page, McConstants.COMMON.PER_PAGE_SIZE, McConstants.COMMON.CONTRACT_TYPE_PERMANENT)
        }
    }
}