package com.legend.modular_contract_sdk.ui.contract.vm

import androidx.lifecycle.MutableLiveData
import com.legend.modular_contract_sdk.base.BaseViewModel
import com.legend.modular_contract_sdk.common.postValueNotNull
import com.legend.modular_contract_sdk.repository.model.PositionAndOrder
import com.legend.modular_contract_sdk.repository.setvices.ExperienceGoldService
import com.legend.modular_contract_sdk.repository.setvices.MarketService
import com.legend.modular_contract_sdk.ui.contract.PositionMode
import com.legend.modular_contract_sdk.utils.McConstants

class OpenPlanOrderViewModel : BaseViewModel() {
    var mDataLiveData: MutableLiveData<List<PositionAndOrder>> = MutableLiveData()
    var mCancelOrderLiveData: MutableLiveData<Any> = MutableLiveData()
    var mCancelAllOrderLiveData: MutableLiveData<Any> = MutableLiveData()

    fun fetchOrderList(positionType: String, positionModel: Int) {
        request {

            val orderList = mutableListOf<PositionAndOrder>()

            try {
                val experienceGoldOrderResp = ExperienceGoldService.instance().fetchPositionAndOrderList(positionType, 1,
                        McConstants.COMMON.PER_PAGE_SIZE, PositionMode.PART.mode,
                        McConstants.COMMON.CONTRACT_TYPE_PERMANENT)

                if (experienceGoldOrderResp.isSuccess && experienceGoldOrderResp.data!!.rows != null) {
                    experienceGoldOrderResp.data!!.rows!!.forEach {
                        it.mIsExperienceGold = true
                        orderList.add(it)
                    }
                }
            } catch (e: Exception){
                e.printStackTrace()
            }

            val orderResp = MarketService.instance().fetchPositionAndOrderList(positionType, 1,
                    McConstants.COMMON.PER_PAGE_SIZE, positionModel,
                    McConstants.COMMON.CONTRACT_TYPE_PERMANENT)

            if (orderResp.isSuccess && orderResp.data!!.rows != null) {
                orderResp.data!!.rows!!.forEach {
                    orderList.add(it)
                }
            }

            mDataLiveData.postValueNotNull(orderList)

            return@request orderResp
        }
    }

    fun cancelOrder(instrument: String, orderId: Long, isExperienceGold: Boolean = false) {
        request(isShowLoading = true) {
            if (isExperienceGold) {
                ExperienceGoldService.instance().cancelOrder(instrument, orderId)
            } else {
                MarketService.instance().cancelOrder(instrument, orderId)
            }.apply {
                if (isSuccess) {
                    mCancelOrderLiveData.postValueNotNull(data ?: "")
                } else {
                    onError(msg ?: "")
                }
            }
        }
    }

    fun cancelAllOrder(positionModel: Int, posType: String) {
        request(isShowLoading = true) {
            ExperienceGoldService.instance().cancelAllOrder(PositionMode.PART.mode, posType, McConstants.COMMON.CONTRACT_TYPE_PERMANENT)
                    .apply {
                        if (isSuccess) {

                        }
                    }
            MarketService.instance().cancelAllOrder(positionModel, posType, McConstants.COMMON.CONTRACT_TYPE_PERMANENT)
                    .apply {
                        if (isSuccess) {
                            mCancelAllOrderLiveData.postValueNotNull(data ?: "")
                        }
                    }
        }
    }
}