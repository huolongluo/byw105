package com.legend.modular_contract_sdk.ui.contract.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.api.ModularContractSDK
import com.legend.modular_contract_sdk.base.BaseViewModel
import com.legend.modular_contract_sdk.common.UserConfigStorage
import com.legend.modular_contract_sdk.common.event.McRefreshOrderList
import com.legend.modular_contract_sdk.common.postValueNotNull
import com.legend.modular_contract_sdk.component.market_listener.*
import com.legend.modular_contract_sdk.repository.model.*
import com.legend.modular_contract_sdk.repository.model.wrap.PositionWrap
import com.legend.modular_contract_sdk.repository.setvices.ExperienceGoldService
import com.legend.modular_contract_sdk.repository.setvices.MarketService
import com.legend.modular_contract_sdk.repository.setvices.UserServices
import com.legend.modular_contract_sdk.ui.contract.*
import com.legend.modular_contract_sdk.utils.*
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import retrofit2.http.Field
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max


/**
 * 合约页面vm
 */
class SwapContractViewModel : BaseViewModel() {

    /**
     * ui live data
     */
    var mInitLiveData = MutableLiveData<Boolean>(false)
    var mWsHostLiveData = MutableLiveData<String>()
    var mPositionTypeLiveData = MutableLiveData<PositionType>(PositionType.EXECUTE)
    var mPositionModeLiveData = MutableLiveData<PositionMode>(PositionMode.FULL)
    var mPositionMergeModeLiveData = MutableLiveData<PositionMergeMode>(PositionMergeMode.MERGE)
    var mLeverageLiveData = MutableLiveData<Int>(100)
    var mPriceLiveData = MutableLiveData<String>("")
    var mTriggerPriceLiveData = MutableLiveData<String>("")
    var mExecutePriceLiveData = MutableLiveData<String>("")
    var mExecuteTypeLiveData = MutableLiveData<TriggerType>(TriggerType.PLAN)
    var mQuantityUnitLiveData = MutableLiveData<QuantityUnit>()
    var mQuantityLiveData = MutableLiveData<String>("")
    var mTargetTriggerLayoutVisibleLiveData = MutableLiveData<Boolean>(false)
    var mStopProfitTypeLiveData = MutableLiveData<TriggerSetType>(TriggerSetType.PRICE)
    var mStopProfitRateLiveData = MutableLiveData<String>("")
    var mStopProfitPriceLiveData = MutableLiveData<String>("")
    var mStopLossTypeLiveData = MutableLiveData<TriggerSetType>(TriggerSetType.PRICE)
    var mStopLossRateLiveData = MutableLiveData<String>("")
    var mStopLossPriceLiveData = MutableLiveData<String>("")
    var mLongMarginLiveData = MutableLiveData<String>("")
    var mShortMarginLiveData = MutableLiveData<String>("")
    var mProductLiveData = MutableLiveData<Product>()
    var mMaxCanOpenLongSizeData = MutableLiveData<Double>()
    var mMaxCanOpenShortSizeData = MutableLiveData<Double>()
    var mFullModeRiskSizeData = MutableLiveData<Double>(0.0)
    var mUseExperienceGold = MutableLiveData<ExperienceGold>()
    var mCoinExperienceGold = MutableLiveData<Pair<String, List<ExperienceGold>>>()//当前交易对可用体验金集合
    var mFundingFeeCountdownLiveData = MutableLiveData<String>()//资金费倒计时
    var mTradingLiveData = MutableLiveData<Boolean>(false)// 是否正在下单过程中 避免连续点击

    /**
     * api live data
     */
    var mAssetsLiveData = MutableLiveData<MarketData>()
    var mPositionLiveData = MutableLiveData<MarketData>()
    var mPositionChangeLiveData = MutableLiveData<MarketData>()
    var mPositionFinishLiveData = MutableLiveData<MarketData>()
    var mAllPositionLiveData = MutableLiveData<MarketData>()
    val mDepthLiveData = MutableLiveData<MarketData>()
    val mIndexPriceLiveData = MutableLiveData<MarketData>()
    val mMarketPriceLiveData = MutableLiveData<MarketData>()
    var mTickerLiveData: MutableLiveData<MarketData> = MutableLiveData()
    var mTickersLiveData: MutableLiveData<MutableMap<String, MarketData>> = MutableLiveData(mutableMapOf())
    val mFundingRateLiveData = MutableLiveData<MarketData>()
    var mContractAssetInfoLiveData = MutableLiveData<ContractAssetInfo?>()// 用户资产
    var mProductsLiveData = MutableLiveData<List<Product>>()
    var mUserTreadConfigLiveData = MutableLiveData<TreadConfig>()
    var mOrderPlaceResultLiveData = MutableLiveData<Boolean>()
    var mHoldPositionsLiveData = MutableLiveData<List<PositionAndOrder>>() // 仓位

    fun fetchWsHost() {
        request {
            MarketService.instance().fetchWsHost().apply {
                mWsHostLiveData.postValue(data?.ws ?: "")
            }
        }
    }

    fun updatePositionType(positionType: PositionType) {
        if (positionType != mPositionTypeLiveData.value) {
            mPositionTypeLiveData.postValue(positionType)
        }
    }

    fun onDepthItemClick(price: String) {
        if (mPositionTypeLiveData.value == PositionType.PLAN_TRIGGER) {
            updateTriggerPrice(price)
        } else {
            updatePrice(price)
        }
    }

    fun updatePrice(price: String) {
        if (price == mPriceLiveData.value) {
            return
        }
        mPriceLiveData.postValue(price)
    }

    fun updateTriggerPrice(price: String) {
        if (price == mTriggerPriceLiveData.value) {
            return
        }
        mTriggerPriceLiveData.postValue(price)
    }

    fun updateTriggerExecutePrice(price: String) {
        if (price == mExecutePriceLiveData.value) {
            return
        }
        mExecutePriceLiveData.postValue(price)
    }

    fun updateTriggerType() {
        when (mExecuteTypeLiveData.value) {
            TriggerType.PLAN -> {
                mExecuteTypeLiveData.postValue(TriggerType.EXECUTE)
            }
            TriggerType.EXECUTE -> {
                mExecuteTypeLiveData.postValue(TriggerType.PLAN)
            }
        }
    }

    fun updateQuantity(quantity: String) {
        if (quantity == mQuantityLiveData.value) {
            return
        }
        mQuantityLiveData.postValue(quantity)
    }

    fun updateQuantityUnitType() {
        when (SPUtils.getTradeUnit()) {
            QuantityUnit.SIZE.unit -> {
                mQuantityUnitLiveData.postValue(QuantityUnit.SIZE)
            }
            QuantityUnit.USDT.unit -> {
                mQuantityUnitLiveData.postValue(QuantityUnit.USDT)
            }
            QuantityUnit.COIN.unit -> {
                mQuantityUnitLiveData.postValue(QuantityUnit.COIN)
            }
        }
    }

    fun updateStopProfitRate(stopProfitRate: String) {
        if (stopProfitRate == mStopProfitRateLiveData.value) {
            return
        }
        mStopProfitRateLiveData.postValue(stopProfitRate)
    }

    fun updateStopProfitPrice(stopProfitPrice: String) {
        if (stopProfitPrice == mStopProfitPriceLiveData.value) {
            return
        }
        mStopProfitPriceLiveData.postValue(stopProfitPrice)
    }

    fun updateStopLossRate(stopLossRate: String) {
        if (stopLossRate == mStopLossRateLiveData.value) {
            return
        }
        mStopLossRateLiveData.postValue(stopLossRate)
    }

    fun updateStopLossPrice(stopLossPrice: String) {
        if (stopLossPrice == mStopLossPriceLiveData.value) {
            return
        }
        mStopLossPriceLiveData.postValue(stopLossPrice)
    }

    fun updatePositionMode(positionMode: PositionMode, positionMergeMode: PositionMergeMode, leverage: Int) {

        request(isShowLoading = true) {
            MarketService.instance().modifyPositionMode(positionMode.mode.toString(), positionMergeMode.mode.toString())
                    .apply {
                        if (isSuccess) {
                            mPositionModeLiveData.postValue(positionMode)
                            mPositionMergeModeLiveData.postValue(positionMergeMode)
                        } else {
                            mPositionModeLiveData.postValue(mPositionModeLiveData.value)
                        }
                    }
        }
    }

    fun updateStopProfitType() {
        when (mStopProfitTypeLiveData.value) {
            TriggerSetType.PRICE -> {
                mStopProfitTypeLiveData.postValue(TriggerSetType.PERCENT)
            }
            TriggerSetType.PERCENT -> {
                mStopProfitTypeLiveData.postValue(TriggerSetType.PRICE)
            }
        }
    }

    fun updateProduct(product: Product) {
        if (product.mId == mProductLiveData.value?.mId) {
            return
        }
        SPUtils.saveLastSelectedProduct(product)
        mProductLiveData.postValue(product)
        resetMarketLiveData()
        resetInputLiveData()
        updateQuantityUnitType()
        mInitLiveData.postValue(false)
    }

    fun updateStopLossType() {
        when (mStopLossTypeLiveData.value) {
            TriggerSetType.PRICE -> {
                mStopLossTypeLiveData.postValue(TriggerSetType.PERCENT)
            }
            TriggerSetType.PERCENT -> {
                mStopLossTypeLiveData.postValue(TriggerSetType.PRICE)
            }
        }
    }

    fun updateTargetLayoutVisible(isChecked: Boolean) {
        mTargetTriggerLayoutVisibleLiveData.postValue(isChecked)
    }

    /**
     * 可开张数
     */
    fun calcMaxCanOpenSize() {
        var longPrice: String? = ""
        var shortPrice: String? = ""
        when (mPositionTypeLiveData.value) {
            PositionType.PLAN -> {
                longPrice = mPriceLiveData.value
                shortPrice = mPriceLiveData.value
            }
            PositionType.EXECUTE -> {
                longPrice = getSell1Price()
                shortPrice = getBuy1Price()
            }
            PositionType.PLAN_TRIGGER -> {
                when (mExecuteTypeLiveData.value) {
                    TriggerType.PLAN -> {
                        if (mExecutePriceLiveData.value.isNullOrEmpty()) {
                            longPrice = mTriggerPriceLiveData.value
                            shortPrice = mTriggerPriceLiveData.value
                        } else {
                            longPrice = mExecutePriceLiveData.value
                            shortPrice = mExecutePriceLiveData.value
                        }
                    }
                    TriggerType.EXECUTE -> {
                        longPrice = mTriggerPriceLiveData.value
                        shortPrice = mTriggerPriceLiveData.value
                    }
                }
            }
        }
        if (longPrice.isNullOrEmpty()) {
            longPrice = "0"
        }
        if (shortPrice.isNullOrEmpty()) {
            shortPrice = "0"
        }
        mQuantityUnitLiveData.value?.let {
            longPrice.let {
                val maxLongSize = CalculateUtil.getMaxOpenSheet(
                        it.getDoubleValue(),
                        mContractAssetInfoLiveData.value,
                        UserConfigStorage.getLeverage(),
                        mProductLiveData.value,
                        mPositionModeLiveData.value!!,
                        mTickersLiveData.value!!,
                        if (isUseExperienceGold()) 0.0 else mProductLiveData.value?.mTakerFee
                                ?: 0.0,
                        mQuantityUnitLiveData.value!!
                )

                mMaxCanOpenLongSizeData.postValue(max(maxLongSize, 0.0))
            }
        }

        mQuantityUnitLiveData.value?.let {
            shortPrice.let {
                val maxShortSize = CalculateUtil.getMaxOpenSheet(
                        it.getDoubleValue(),
                        mContractAssetInfoLiveData.value,
                        UserConfigStorage.getLeverage(),
                        mProductLiveData.value,
                        mPositionModeLiveData.value!!,
                        mTickersLiveData.value!!,
                        if (isUseExperienceGold()) 0.0 else mProductLiveData.value?.mTakerFee
                                ?: 0.0,
                        mQuantityUnitLiveData.value!!
                )

                mMaxCanOpenShortSizeData.postValue(max(maxShortSize, 0.0))
            }
        }
    }

    // 获取用于计算的价格，市价时去最新价；限价时去输入的价格，如果没输入取最新价；计划委托时取执行价，计划委托市价时取触发价。
    fun getPriceForCalc(): String {
        var price: String? = ""
        when (mPositionTypeLiveData.value) {
            PositionType.PLAN -> {
                price = mPriceLiveData.value
            }
            PositionType.EXECUTE -> {
                price = getLastPrice()
            }
            PositionType.PLAN_TRIGGER -> {
                when (mExecuteTypeLiveData.value) {
                    TriggerType.PLAN -> {
                        price = mExecutePriceLiveData.value
                        if (mExecutePriceLiveData.value.isNullOrEmpty()) {
                            price = mTriggerPriceLiveData.value
                        }
                    }
                    TriggerType.EXECUTE -> {
                        price = mTriggerPriceLiveData.value
                    }
                }
            }
        }

        if (price.isNullOrEmpty()) {
            price = getLastPrice()
        }
        return price
    }

    /**
     * 计算占用保证金
     */
    fun calcOccupyMargin() {
        if (mQuantityLiveData.value.isNullOrEmpty()) {
            mLongMarginLiveData.value = ("0".getNum(4, true))
            mShortMarginLiveData.value = ("0".getNum(4, true))
            return
        }


        if (mProductLiveData.value == null) {
            return
        }


        val calcLongPrice = when (mPositionTypeLiveData.value) {
            PositionType.EXECUTE -> {
                getSell1Price().getDouble()
            }
            PositionType.PLAN -> {
                if (mPriceLiveData.value.getDouble() > 0) {
                    mPriceLiveData.value.getDouble()
                } else {
                    getSell1Price().getDouble()
                }
            }
            PositionType.PLAN_TRIGGER -> {

                when (mExecuteTypeLiveData.value) {
                    TriggerType.PLAN -> {
                        if (mExecutePriceLiveData.value.getDouble() > 0) {
                            mExecutePriceLiveData.value.getDouble()
                        } else {
                            mTriggerPriceLiveData.value.getDouble()
                        }
                    }
                    TriggerType.EXECUTE -> {

                        if (mTriggerPriceLiveData.value.getDouble() > 0) {
                            mTriggerPriceLiveData.value.getDouble()
                        } else {
                            getSell1Price().getDouble()
                        }
                    }
                    else -> {
                        0.0
                    }
                }


            }
            else -> getSell1Price().getDouble()
        }


        val calcShortPrice = when (mPositionTypeLiveData.value) {
            PositionType.EXECUTE -> {
                getBuy1Price().getDouble()
            }
            PositionType.PLAN -> {
                if (mPriceLiveData.value.getDouble() > 0) {
                    mPriceLiveData.value.getDouble()
                } else {
                    getBuy1Price().getDouble()
                }
            }
            PositionType.PLAN_TRIGGER -> {
                when (mExecuteTypeLiveData.value) {
                    TriggerType.PLAN -> {
                        if (mExecutePriceLiveData.value.getDouble() > 0) {
                            mExecutePriceLiveData.value.getDouble()
                        } else {
                            mTriggerPriceLiveData.value.getDouble()
                        }
                    }
                    TriggerType.EXECUTE -> {

                        if (mTriggerPriceLiveData.value.getDouble() > 0) {
                            mTriggerPriceLiveData.value.getDouble()
                        } else {
                            getBuy1Price().getDouble()
                        }
                    }
                    else -> {
                        0.0
                    }
                }
            }
            else -> getBuy1Price().getDouble()
        }

        val quantity = mQuantityLiveData.value.getDouble()
        val oneLotSize = mProductLiveData.value!!.mOneLotSize
        val leverage = UserConfigStorage.getLeverage()
        // 计算买卖成本(张) = 张数 * 面值 * 预成交价价格 / 杠杆倍数
        // 计算买卖成本(币) = 币数 * 预成交价价格 / 杠杆倍数
        // 计算买卖成本(USDT) = 金额 / 杠杆倍数

        var longMargin = 0.0
        var shortMargin = 0.0

        when (mQuantityUnitLiveData.value) {
            QuantityUnit.SIZE -> {
                longMargin = quantity * oneLotSize * calcLongPrice / leverage
                shortMargin = quantity * oneLotSize * calcShortPrice / leverage
            }
            QuantityUnit.COIN -> {
                longMargin = quantity * calcLongPrice / leverage
                shortMargin = quantity * calcShortPrice / leverage
            }
            QuantityUnit.USDT -> {
                longMargin = quantity / leverage
                shortMargin = quantity / leverage
            }
        }

        mLongMarginLiveData.value = (longMargin.toString().getNum(4, true))
        mShortMarginLiveData.value = (shortMargin.toString().getNum(4, true))

    }

    private fun getBuy1Price(): String {
        mDepthLiveData.value?.let {
            return (it as Depth).bids.firstOrNull()?.p ?: ""
        }
        return ""
    }

    private fun getSell1Price(): String {
        mDepthLiveData.value?.let {
            return (it as Depth).asks.firstOrNull()?.p ?: ""
        }
        return ""
    }

    private fun getLastPrice(): String {
        mTickerLiveData.value?.let {
            return (it as Ticker).last
        }
        return ""
    }

    fun increasePriceStepValue() {
        mPriceLiveData.value?.let {
            val price = it.getDouble() + getStepValue()
            val pricePrecision = mProductLiveData.value?.mPricePrecision ?: 1
            mPriceLiveData.postValue(
                    NumberStringUtil.formatAmount(price, pricePrecision, NumberStringUtil.AmountStyle.FillZeroNoComma, RoundingMode.HALF_UP))
        }
    }

    fun decreasePriceStepValue() {
        mPriceLiveData.value?.let {
            if (it.isNotEmpty()) {
                val price = max(it.toDouble() - getStepValue(), 0.0)
                val pricePrecision = mProductLiveData.value?.mPricePrecision ?: 1
                mPriceLiveData.postValue(
                        NumberStringUtil.formatAmount(price, pricePrecision, NumberStringUtil.AmountStyle.FillZeroNoComma, RoundingMode.HALF_UP))
            }
        }
    }

    fun increaseTriggerPriceStepValue() {
        mTriggerPriceLiveData.value?.let {
            val price = it.getDouble() + getStepValue()
            val pricePrecision = mProductLiveData.value?.mPricePrecision ?: 1
            mTriggerPriceLiveData.postValue(
                    NumberStringUtil.formatAmount(price, pricePrecision, NumberStringUtil.AmountStyle.FillZeroNoComma, RoundingMode.HALF_UP))
        }
    }

    fun decreaseTriggerPriceStepValue() {
        mTriggerPriceLiveData.value?.let {
            if (it.isNotEmpty()) {
                val price = max(it.toDouble() - getStepValue(), 0.0)
                val pricePrecision = mProductLiveData.value?.mPricePrecision ?: 1
                mTriggerPriceLiveData.postValue(
                        NumberStringUtil.formatAmount(price, pricePrecision, NumberStringUtil.AmountStyle.FillZeroNoComma, RoundingMode.HALF_UP))
            }
        }
    }

    private fun getStepValue(): Double {
        var value = 1.0
        val pricePrecision = mProductLiveData.value?.mPricePrecision ?: 1
        for (i in 1..pricePrecision) {
            value /= 10
        }
        return value
    }

    /**
     * 重置和product相关的市场数据
     */
    fun resetMarketLiveData() {
        mDepthLiveData.postValue(Depth())
        mTickerLiveData.postValue(Ticker())
        mIndexPriceLiveData.postValue(Price())
        mMarketPriceLiveData.postValue(Price())
        mFundingRateLiveData.postValue(FundingRate())
    }

    /**
     * 重置个人相关的数据
     */
    fun resetUserLiveData() {
        mContractAssetInfoLiveData.postValue(null)
        resetInputLiveData()
    }

    fun resetInputLiveData() {
        mQuantityLiveData.postValue("")
        mPriceLiveData.postValue("")
        mTriggerPriceLiveData.postValue("")
        mExecutePriceLiveData.postValue("")
        mStopLossPriceLiveData.postValue("")
        mStopLossRateLiveData.postValue("")
        mStopProfitPriceLiveData.postValue("")
        mStopProfitRateLiveData.postValue("")
    }

    fun isUseExperienceGold() = mUseExperienceGold.value != null

    fun apiPlaceOrder(direction: String) {

        mTradingLiveData.postValue(true)

        val json = JSONObject()
        json.put("direction", direction)
        json.put("leverage", UserConfigStorage.getLeverage())
        if (mQuantityUnitLiveData.value == QuantityUnit.COIN) {
            mProductLiveData.value?.let {
                json.put("quantityUnit", QuantityUnit.SIZE.unit)
                val number = (mQuantityLiveData.value.getDouble() / it.mOneLotSize).toString().getNum(0)
                json.put("quantity", number)
            }
        } else if (mQuantityUnitLiveData.value == QuantityUnit.USDT) {
            json.put("quantityUnit", QuantityUnit.USDT.unit)
            val number = mQuantityLiveData.value.getDouble() / UserConfigStorage.getLeverage()
            json.put("quantity", number)
        } else {
            json.put("quantityUnit", (mQuantityUnitLiveData.value as QuantityUnit).unit)
            json.put("quantity", mQuantityLiveData.value)
        }

        json.put("positionModel", (mPositionModeLiveData.value as PositionMode).mode)
        json.put("positionType", (mPositionTypeLiveData.value as PositionType).type)
        json.put("contractType", ContractType.PERPETUAL.type)

        //使用体验金
        if (isUseExperienceGold()) {
            json.put("goldId", mUseExperienceGold.value?.mId)
            json.put("quantityUnit", QuantityUnit.USDT.unit)
            json.put("quantity", mUseExperienceGold.value?.mAmount)
            json.put("leverage", mUseExperienceGold.value?.mLeverage)
            json.put("positionModel", PositionMode.PART.mode)
        }

        if (mPositionTypeLiveData.value == PositionType.PLAN_TRIGGER) {
            json.put("openPrice", mExecutePriceLiveData.value)
            json.put("triggerPrice", mTriggerPriceLiveData.value)
            json.put("triggerType", mExecuteTypeLiveData.value?.type)

        } else {
            json.put("openPrice", mPriceLiveData.value)
            if (mTargetTriggerLayoutVisibleLiveData.value as Boolean) {
                when (mStopProfitTypeLiveData.value as TriggerSetType) {
                    TriggerSetType.PERCENT -> {

                        if ((mStopProfitRateLiveData.value as String).isNotEmpty()) {
                            json.put("stopProfitRate", mStopProfitRateLiveData.value!!.toDouble() / 100)
                        }
                    }
                    TriggerSetType.PRICE -> {
                        json.put("stopProfitPrice", mStopProfitPriceLiveData.value)
                    }
                }
                when (mStopLossTypeLiveData.value as TriggerSetType) {
                    TriggerSetType.PERCENT -> {
                        if ((mStopLossRateLiveData.value as String).isNotEmpty()) {
                            json.put("stopLossRate", mStopLossRateLiveData.value!!.toDouble() / 100)
                        }
                    }
                    TriggerSetType.PRICE -> {
                        json.put("stopLossPrice", mStopLossPriceLiveData.value)
                    }
                }
            }
        }


        val body: RequestBody =
                json.toString().toRequestBody("application/json".toMediaTypeOrNull())


        request(isShowLoading = true, errorBlock = {
            mTradingLiveData.postValue(false)
        }) {
            if (isUseExperienceGold()) {
                ExperienceGoldService.instance().placeOrder(mProductLiveData.value!!.mBase, body)
            } else {
                MarketService.instance().placeOrder(mProductLiveData.value!!.mBase, body)
            }
                    .apply {
                        if (isSuccess) {
                            EventBus.getDefault().post(McRefreshOrderList())
                            mOrderPlaceResultLiveData.postValue(true)
                            mPriceLiveData.postValue("")
                            mTriggerPriceLiveData.postValue("")
                            mExecutePriceLiveData.postValue("")
                            fetchContractAssetsInfo()
                        }
                        mTradingLiveData.postValue(false)
                    }
        }
    }

    fun fetchContractAssetsInfo() {
        if (ModularContractSDK.userIsLogin()) {

            request {

                val positions = mutableListOf<PositionAndOrder>()
                try {
                    val positionsResp = ExperienceGoldService.instance().fetchPositionAndOrderList(PositionType.EXECUTE.type, 1, 1000, PositionMode.PART.mode, McConstants.COMMON.CONTRACT_TYPE_PERMANENT)

                    if (positionsResp.isSuccess && positionsResp.data != null && positionsResp.data!!.rows != null) {
                        positionsResp.data!!.rows!!.forEach {
                            it.mIsExperienceGold = true
                            positions.add(it)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }


                val accountInfoResp = UserServices.instance().fetchAccountInfo()
                        .apply {
                            if (this.isSuccess) {
                                mContractAssetInfoLiveData.postValueNotNull(data)
                            }
                        }

                if (accountInfoResp.isSuccess && accountInfoResp.data != null) {
                    positions.addAll(accountInfoResp.data!!.mUserPositions)
                }

                positions.sort()

                mHoldPositionsLiveData.postValue(positions)

                return@request accountInfoResp
            }


        }

    }

    //获取所有交易对
    fun fetchAllProduct() {
        request {
            MarketService.instance().fetchProducts()
                    .apply {
                        if (isSuccess) {
                            mProductsLiveData.postValueNotNull(data)
                        }
                    }
        }
    }

    /**
     * 平仓
     */
    fun closeOrder(instrument: String, orderId: Long, isExperienceGold: Boolean = false) {
        request(isShowLoading = true) {
            if (isExperienceGold) {
                ExperienceGoldService.instance().closeOrder(instrument, orderId)
            } else {
                MarketService.instance().closeOrder(instrument, orderId)
            }.apply {
                if (isSuccess) {
                    showMessageLiveData.postValue(ModularContractSDK.context?.getString(R.string.mc_sdk_close_position_success))
                    fetchContractAssetsInfo()
                }
            }
        }
    }

    fun closePartOrder(instrument: String, orderId: Long, closeRate: Double?, closeNum: Double?, type: PositionType, price: String, isExperienceGold: Boolean = false) {

        mTradingLiveData.postValue(true)

        val json = JSONObject()
        closeRate?.let {
            json.put("closeRate", closeRate)
        }
        closeNum?.let {
            json.put("closeNum", closeNum)
        }

        json.put("positionType", type.type)

        if (type == PositionType.PLAN) {

            json.put("orderPrice", price)
        }

        val body: RequestBody =
                json.toString().toRequestBody("application/json".toMediaTypeOrNull())

        request(isShowLoading = true, errorBlock = {
            mTradingLiveData.postValue(false)
        }) {

            if (isExperienceGold) {
                ExperienceGoldService.instance().closePartOrder(instrument, orderId, body)
            } else {
                MarketService.instance().closePartOrder(instrument, orderId, body)
            }.apply {
                if (isSuccess) {
                    showMessageLiveData.postValue(ModularContractSDK.context?.getString(R.string.mc_sdk_close_position_success))
                    fetchContractAssetsInfo()
                }
                mTradingLiveData.postValue(false)
            }
        }
    }

    fun addPosition(amount: Double, orderId: Long, type: AddPositionType, isExperienceGold: Boolean = false) {
        val body = JSONObject().let {
            it.put("appendMargin", amount)
            it.put("quantityUnit", type.type)
            it.toString().toRequestBody("application/json".toMediaTypeOrNull())
        }
        request(isShowLoading = true) {
            if (isExperienceGold) {
                ExperienceGoldService.instance().addPosition(orderId, body)
            } else {
                MarketService.instance().addPosition(orderId, body)
            }.apply {
                if (isSuccess) {
                    showMessageLiveData.postValue(ModularContractSDK.context?.getString(R.string.mc_sdk_add_position_success))
                    fetchContractAssetsInfo()
                }
            }
        }
    }

    /**
     * 反手
     */
    fun closeAndOpenReverseOrder(instrument: String, orderId: Long, isExperienceGold: Boolean = false) {
        request(isShowLoading = true) {
            if (isExperienceGold) {
                ExperienceGoldService.instance().closeAndOpenReverseOrder(instrument, orderId)
            } else {
                MarketService.instance().closeAndOpenReverseOrder(instrument, orderId)
            }.apply {
                if (isSuccess) {
                    showMessageLiveData.postValue(ModularContractSDK.context?.getString(R.string.mc_sdk_reverse_open_position_success))
                    fetchContractAssetsInfo()
                }
            }
        }
    }

    fun closeAllOrder(positionMode: String, isExperienceGold: Boolean = false) {
        request(isShowLoading = true) {
            ExperienceGoldService.instance().closeAllOrder(PositionMode.PART.mode.toString()).apply {

            }

            MarketService.instance().closeAllOrder(positionMode)
                    .apply {
                        if (isSuccess) {
                            showMessageLiveData.postValue(ModularContractSDK.context?.getString(R.string.mc_sdk_close_all_position_success))
                            fetchContractAssetsInfo()
                        }
                    }
        }
    }

    fun fetchUserTreadConfig() {
        if (ModularContractSDK.userIsLogin()) {
            request {
                MarketService.instance().fetchUserTreadConfig()
                        .apply {
                            if (isSuccess) {
                                mUserTreadConfigLiveData.postValue(data)
                            }
                        }
            }
        }
    }

    fun startFundingFeeCountdown() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                try {

                    val dayFormat = SimpleDateFormat("yy/MM/dd")
                    val sdf = SimpleDateFormat("HH:mm:ss")
                    sdf.timeZone = TimeZone.getTimeZone("GMT+00:00")
                    dayFormat.timeZone = TimeZone.getTimeZone("GMT+00:00")

                    val hourTime: Long = 1000 * 60 * 60
                    val _8hourTime: Long = hourTime * 8
                    val dayDate = dayFormat.parse(dayFormat.format(Date()))
                    val todayPastTime = System.currentTimeMillis() - dayDate.time

                    val fundingFeeCountdown = _8hourTime - todayPastTime % _8hourTime
                    mFundingFeeCountdownLiveData.postValue(sdf.format(Date(fundingFeeCountdown)))

                    delay(1000)
                } catch (e: Exception) {
                    delay(1000)
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
        request(isShowLoading = true) {

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
                    showMessageLiveData.postValue(ModularContractSDK.context?.getString(R.string.mc_sdk_change_success))
                    //修改成功之后刷新仓位信息
                    fetchContractAssetsInfo()
                }
            }
        }
    }

    /**
     * 参数类型	参数名称	参数含义	是否必传
    openId	仓位ID	是
    callbackRate	回调率	是
    triggerPrice	激活价格，不传按市价触发，传了按照限价触发	否
    quantity	张数	是
     */
    fun setMoveTPSL(openId: Long, triggerPrice: String, callbackRate:String,  quantity:String){
        request {

            val body = JSONObject().let {

                it.put("openId", openId)
                it.put("callbackRate", callbackRate)
                it.put("triggerPrice", triggerPrice)
                it.put("quantity", quantity)

                it.toString().toRequestBody("application/json".toMediaTypeOrNull())
            }

            MarketService.instance().setMoveTPSL(body)
                    .apply {
                        if (isSuccess) {
                            Logger.e("修改止盈止损成功")
                            showMessageLiveData.postValue(ModularContractSDK.context?.getString(R.string.mc_sdk_action_success))
                            //修改成功之后刷新仓位信息
                            fetchContractAssetsInfo()
                        }
            }
        }

    }

    // 获取当前交易对可用保证金
    fun fetchCoinExperienceGold() {
        request(isShowLoading = true) {
            ExperienceGoldService.instance().fetchExperienceGoldList(mProductLiveData.value!!.mBase)
                    .apply {
                        if (isSuccess) {
                            mCoinExperienceGold.postValueNotNull(Pair(mProductLiveData.value!!.mBase, data!!))
                        }
                    }
        }

    }

    fun addMargin(positionWrap: PositionWrap, marginAmount: String) {
        request(isShowLoading = true) {

            val body = JSONObject().let {
                it.put("addMargin", marginAmount)
                it.toString().toRequestBody("application/json".toMediaTypeOrNull())
            }

            MarketService.instance().addMargin(positionWrap.position.mId.toString(), body)
                    .apply {
                        if (isSuccess) {
                            // 追加保证金之后刷新资产和仓位
                            fetchContractAssetsInfo()
                        }
                    }
        }
    }

    fun minusMargin(positionWrap: PositionWrap, marginAmount: String) {
        request(isShowLoading = true) {

            val body = JSONObject().let {
                it.put("reduceMargin", marginAmount)
                it.toString().toRequestBody("application/json".toMediaTypeOrNull())
            }

            MarketService.instance().minusMargin(positionWrap.position.mId.toString(), body)
                    .apply {
                        if (isSuccess) {
                            // 追加保证金之后刷新资产和仓位
                            fetchContractAssetsInfo()
                        }
                    }
        }
    }

}