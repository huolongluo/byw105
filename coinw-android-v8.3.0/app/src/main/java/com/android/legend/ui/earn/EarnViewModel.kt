package com.android.legend.ui.earn

import androidx.lifecycle.MutableLiveData
import com.android.legend.api.apiService
import com.android.legend.model.CurrencyInfo
import com.android.legend.model.earn.EarnAccountCoin
import com.android.legend.model.earn.EarnBill
import com.android.legend.model.earn.EarnCurrency
import com.android.legend.model.earn.EarnProfit
import com.android.legend.model.earn.wrap.EarnBillWrap
import com.android.legend.model.earn.wrap.EarnProductWrap
import com.legend.modular_contract_sdk.base.BaseViewModel
import com.legend.modular_contract_sdk.utils.McConstants
import com.legend.modular_contract_sdk.utils.getDouble
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class EarnViewModel : BaseViewModel() {

    // 理财产品
    val mEarnProductListLiveData = MutableLiveData<List<EarnProductWrap>>()
    // 我的已购买理财产品
    val mMyEarnListLiveData = MutableLiveData<List<EarnProductWrap>>()
    // 账单列表
    val mEarnBillListLiveData = MutableLiveData<List<EarnBillWrap>>()
    // 账单搜索币种集合
    val mEarnCurrencyLiveData = MutableLiveData<List<EarnCurrency>>()
    // 币种信息
    val mCurrencyInfoLiveData = MutableLiveData<List<CurrencyInfo>>()

    /**
     * 理财账户所有币种的资产
     */
    val mCoinsEarnAccountLiveData = MutableLiveData<List<EarnAccountCoin>>()

    val mBuySuccessLiveData = MutableLiveData<ArrayList<String>>()

    val mRedemptionSuccessLiveData = MutableLiveData<Boolean>()

    /**
     * 理财账户头部信息
     */
    val mAccountTotalInfoLiveData = MutableLiveData<EarnProfit>()



    fun fetchEarnProductList(earnTimeLimitType: EarnTimeLimitType, earnType: EarnType) {
        request {
            apiService.fetchEarnProductList(earnTimeLimitType.timeLimitName, earnType.type.toString(), 1, 500)
                    .apply {
                        if (isReqSuccess()) {
                            mEarnProductListLiveData.postValue(this.data.rows?.map { EarnProductWrap(it) })
                        }
                    }
        }

    }

    fun fetchEarnAccount() {
        request {
            apiService.fetchEarnAccountCoins().apply {
                if (isReqSuccess()) {
                    mCoinsEarnAccountLiveData.postValue(this.data)
                }
            }
        }
    }

    fun buyEarn(productId: Int, vararg buyCoins:Pair<Int, String>) {
        request(isShowLoading = true) {

            val jsonArray = JSONArray()
            val amount = ArrayList<String>()
            buyCoins.forEach {buyCoin ->
                val currencyJson = JSONObject()
                currencyJson.put("amount", buyCoin.second.getDouble())
                currencyJson.put("currencyId", buyCoin.first)
                amount.add(buyCoin.second)
                jsonArray.put(currencyJson)
            }

            val bodyJson = JSONObject()
            bodyJson.put("productId", productId)
            bodyJson.put("financialProductInvestRequest", jsonArray)

            apiService.buyEarn(bodyJson.toString().toRequestBody("application/json".toMediaTypeOrNull()))
                    .apply {
                        if (isReqSuccess()) {
                            mBuySuccessLiveData.postValue(amount)
                        }
                    }
        }
    }

    fun fetchAccountTotalInfo() {
        request {
            apiService.fetchEarnProfit().apply {
                if (isReqSuccess()) {
                    mAccountTotalInfoLiveData.postValue(data)
                }
            }
        }
    }

    /**
     * 获取我的理财列表
     * earnTimeLimitType 分类 ->活期：CURRENT_FINANCIAL 定期：REGULAR_FINANCIAL 混合：BLEND_FINANCIAL
     * financialType 类型 活期：1 ，定期：2
     */
    fun fetchMyEarnList(currencyId: Int, earnTimeLimitType: String, financialType: String) {
        request(isShowLoading = true) {
            apiService.fetchMyEarnList(currencyId, earnTimeLimitType, financialType)
                    .apply {
                        if (isReqSuccess()) {
                            mMyEarnListLiveData.postValue(data.map { EarnProductWrap(it) })
                        }
                    }
        }
    }

    fun redemption(id: Int) {
        request(isShowLoading = true) {
            val json = JSONObject()
            json.put("productId", id)
            apiService.redemption(json.toString().toRequestBody("application/json".toMediaTypeOrNull())).apply {
                if (isReqSuccess()) {
                    mRedemptionSuccessLiveData.postValue(true)
                }
            }
        }
    }

    fun fetchEarnBill(earnTimeLimitType: Int?, actionType: String?, currencyId: Int?, page:Int, pageSize:Int) {
        request(isShowLoading = false) {
            apiService.fetchEarnBill(actionType, earnTimeLimitType, currencyId, page,  pageSize).apply {
                if (isReqSuccess()) {
                    mEarnBillListLiveData.postValue(data.rows?.map { EarnBillWrap(it) })
                }
            }
        }
    }

    fun fetchEarnCurrencyList(){
        request {
            apiService.fetchEarnCurrencyList().apply {
                if (isReqSuccess()) {
                    mEarnCurrencyLiveData.postValue(data)
                }
            }
        }
    }

    fun fetchCurrencyInfoList(){
        request(){
            apiService.fetchCurrencyInfo().apply {
                if (isReqSuccess()){
                    mCurrencyInfoLiveData.postValue(data)
                }
            }
        }
    }

}