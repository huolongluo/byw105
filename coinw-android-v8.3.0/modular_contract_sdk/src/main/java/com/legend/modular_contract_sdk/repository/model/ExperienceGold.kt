package com.legend.modular_contract_sdk.repository.model
import com.google.gson.annotations.SerializedName
import com.legend.modular_contract_sdk.utils.TimeUtils


data class ExperienceGold(
    @SerializedName("amount")
    val mAmount: String,
    @SerializedName("conditionActiveAmount")
    val mConditionActiveAmount: String?,
    @SerializedName("conditionCurrencyId")
    val mConditionCurrencyId: Int,
    @SerializedName("conditionCurrencyName")
    val mConditionCurrencyName: String,
    @SerializedName("conditionType")
    val mConditionType: Int,
    @SerializedName("createTime")
    val mCreateTime: Long,
    @SerializedName("currencyName")
    val mCurrencyName: String,
    @SerializedName("currentTransferInAmount")
    val mCurrentTransferInAmount: String?,
    @SerializedName("expireTime")
    val mExpireTime: Long,
    @SerializedName("id")
    val mId: Int,
    @SerializedName("leverage")
    val mLeverage: Int,
    @SerializedName("status")
    val mStatus: Int,
    @SerializedName("goldId")
    val mGoldId: Int,
    @SerializedName("goldRecordId")
    val mGoldRecordId: Int
)