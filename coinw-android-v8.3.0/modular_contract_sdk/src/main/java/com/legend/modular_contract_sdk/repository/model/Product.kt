package com.legend.modular_contract_sdk.repository.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Product(
        @SerializedName("base")
        val mBase: String,
        @SerializedName("closeSpread")
        val mCloseSpread: Double,
        @SerializedName("commissionRate")
        val mCommissionRate: Double,
        @SerializedName("configBo")
        val mConfigBo: ConfigBo,
        @SerializedName("createdDate")
        val mCreatedDate: Long,
        @SerializedName("defaultLeverage")
        val mDefaultLeverage: Int,
        @SerializedName("defaultStopLossRate")
        val mDefaultStopLossRate: Double,
        @SerializedName("defaultStopProfitRate")
        val mDefaultStopProfitRate: Double,
        @SerializedName("id")
        val mId: Int,
        @SerializedName("indexId")
        val mIndexId: Int,
        @SerializedName("leverage")
        val mLeverage: List<Int>,
        @SerializedName("makerFee")
        val mMakerFee: Double,
        @SerializedName("maxLeverage")
        val mMaxLeverage: Int,
        @SerializedName("maxPosition")
        val mMaxPosition: Int,
        @SerializedName("minLeverage")
        val mMinLeverage: Int,
        @SerializedName("minSize")
        val mMinSize: Int,
        @SerializedName("name")
        val mName: String,
        @SerializedName("oneLotMargin")
        val mOneLotMargin: Int,
        @SerializedName("oneLotSize")
        val mOneLotSize: Double,
        @SerializedName("oneMaxPosition")
        val mOneMaxPosition: Int,
        @SerializedName("openSpread")
        val mOpenSpread: Double,
        @SerializedName("pricePrecision")
        val mPricePrecision: Int,// 币种精度
        @SerializedName("quote")
        val mQuote: String,
        @SerializedName("selected")
        val mSelected: Int,
        @SerializedName("settledAt")
        val mSettledAt: Long,
        @SerializedName("settlementRate")
        val mSettlementRate: Double,
        @SerializedName("sort")
        val mSort: Int,
        @SerializedName("status")
        val mStatus: String,
        @SerializedName("stopSurplusRate")
        val mStopSurplusRate: Double,//逐仓维持保证金率
        @SerializedName("takerFee")
        val mTakerFee: Double,
        var mLast: String?,//最新价
        var mChangeRate: String?,//
        var mOldPrice: String?,//记录上一次的最新价
        @SerializedName("updatedDate")
        val mUpdatedDate: Long,
        @SerializedName("stopCrossPositionRate")
        val mStopCrossPositionRate: Double// 全仓风险率
) : Serializable {
    fun getProductName() =
            mBase.toUpperCase() + "/" + mQuote.toUpperCase()

        fun getBaseName() = mBase.toUpperCase()
        fun getQuoteName() = "/${mQuote.toUpperCase()}"

}

data class ConfigBo(
        @SerializedName("margins")
        val mMargins: Margins,
        @SerializedName("simulatedMargins")
        val mSimulatedMargins: SimulatedMargins
) : Serializable

data class Margins(
        @SerializedName("10")
        val mX10: Double,
        @SerializedName("100")
        val mX100: Double,
        @SerializedName("20")
        val mX20: Double,
        @SerializedName("5")
        val mX5: Double,
        @SerializedName("50")
        val mX50: Double
) : Serializable

data class SimulatedMargins(
        @SerializedName("10")
        val mX10: Double,
        @SerializedName("20")
        val mX20: Double,
        @SerializedName("5")
        val mX5: Double
) : Serializable