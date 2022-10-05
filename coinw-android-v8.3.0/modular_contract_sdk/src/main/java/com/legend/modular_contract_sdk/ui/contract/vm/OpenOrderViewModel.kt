package com.legend.modular_contract_sdk.ui.contract.vm

import androidx.lifecycle.MutableLiveData
import com.legend.modular_contract_sdk.base.BaseViewModel
import com.legend.modular_contract_sdk.common.event.McRefreshOrderList
import com.legend.modular_contract_sdk.common.postValueNotNull
import com.legend.modular_contract_sdk.repository.model.PositionAndOrder
import com.legend.modular_contract_sdk.repository.model.wrap.PositionWrap
import com.legend.modular_contract_sdk.repository.setvices.ExperienceGoldService
import com.legend.modular_contract_sdk.repository.setvices.MarketService
import com.legend.modular_contract_sdk.ui.contract.PositionMode
import com.legend.modular_contract_sdk.ui.contract.TriggerSetType
import com.legend.modular_contract_sdk.utils.McConstants
import com.legend.modular_contract_sdk.utils.getDouble
import com.legend.modular_contract_sdk.utils.getDoubleValue
import com.orhanobut.logger.Logger
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

class OpenOrderViewModel : BaseViewModel() {
    var mDataLiveData: MutableLiveData<List<PositionAndOrder>> = MutableLiveData()
    var mCancelOrderLiveData: MutableLiveData<Any> = MutableLiveData()
    var mCancelAllOrderLiveData: MutableLiveData<Any> = MutableLiveData()

    fun fetchOrderList(positionType: String, positionModel: Int) {
        request {

            val orderList = mutableListOf<PositionAndOrder>()

            try {
                val expGoldOrders = ExperienceGoldService.instance().fetchPositionAndOrderList(positionType, 1, McConstants.COMMON.PER_PAGE_SIZE,
                        PositionMode.PART.mode,
                        McConstants.COMMON.CONTRACT_TYPE_PERMANENT)

                if (expGoldOrders.isSuccess && expGoldOrders.data!!.rows != null) {
                    expGoldOrders.data!!.rows!!.forEach {
                        it.mIsExperienceGold = true
                        orderList.add(it)
                    }
                }
            } catch (e: Exception){
                e.printStackTrace()
            }


            val ordersResp = MarketService.instance().fetchPositionAndOrderList(
                    positionType, 1, McConstants.COMMON.PER_PAGE_SIZE, positionModel,
                    McConstants.COMMON.CONTRACT_TYPE_PERMANENT
            )

            if (ordersResp.isSuccess && ordersResp.data!!.rows != null) {
                ordersResp.data!!.rows!!.forEach {
                    orderList.add(it)
                }
            }

            orderList.sort()

            mDataLiveData.postValueNotNull(orderList)

            return@request ordersResp
        }
    }

    fun cancelOrder(instrument: String, orderId: Long, isExperienceGold: Boolean = false) {
        request {
            if (isExperienceGold) {
                ExperienceGoldService.instance().cancelOrder(instrument, orderId)
            } else {
                MarketService.instance().cancelOrder(instrument, orderId)
            }.apply {
                if (isSuccess) {
                    mCancelOrderLiveData.postValueNotNull(data ?: "")
                }
            }
        }
    }

    fun cancelAllOrder(positionModel: Int, posType: String) {
        request {

            ExperienceGoldService.instance().cancelAllOrder(PositionMode.PART.mode, posType, McConstants.COMMON.CONTRACT_TYPE_PERMANENT)
                    .apply {

                    }

            MarketService.instance().cancelAllOrder(positionModel, posType, McConstants.COMMON.CONTRACT_TYPE_PERMANENT)
                    .apply {
                        if (isSuccess) {
                            mCancelAllOrderLiveData.postValueNotNull(data ?: "")
                        }
                    }
        }
    }

    fun modifyPositionStopProfitAndLoss(
            positionWrap: PositionWrap,
            takeProfit: String,
            stopLoss: String,
            isExperienceGold: Boolean = false
    ) {
        // 同时传止盈价和止盈率那以止盈价为准
        val body = JSONObject().let {

            it.put("stopProfitPrice", takeProfit)
            it.put("stopLossPrice", stopLoss)

            it.toString().toRequestBody("application/json".toMediaTypeOrNull())
        }
        request {
            if (isExperienceGold) {
                ExperienceGoldService.instance()
                        .modifyPositionStopProfitAndLoss(
                                positionWrap.position.mInstrument,
                                positionWrap.position.mId.toString(),
                                body
                        )
            } else {
                MarketService.instance()
                        .modifyPositionStopProfitAndLoss(
                                positionWrap.position.mInstrument,
                                positionWrap.position.mId.toString(),
                                body
                        )
            }.apply {
                if (isSuccess) {
                    Logger.e("修改止盈止损成功")
                    EventBus.getDefault().post(McRefreshOrderList())
                }
            }
        }
    }

}