package com.legend.modular_contract_sdk.repository.model
import com.google.gson.annotations.SerializedName

/**
 * 合约账户信息
 */
data class ContractAssetInfo(
    @SerializedName("available")
    val mAvailable: Double,
    @SerializedName("closeSpreadRateMap")
    val mCloseSpreadRateMap: Map<String, Double>,
    @SerializedName("commissionRateMap")
    val mCommissionRateMap: Map<String, Double>,
    @SerializedName("feeRateMap")
    val mFeeRateMap: Map<String, Map<String, Double>>,
    @SerializedName("totalFee")
    val mTotalFee: Double,
    @SerializedName("totalFundingFee")
    val mTotalFundingFee: Double,
    @SerializedName("totalMargin")
    val mTotalMargin: Double,
    @SerializedName("userPositions")
    val mUserPositions: List<PositionAndOrder>
)