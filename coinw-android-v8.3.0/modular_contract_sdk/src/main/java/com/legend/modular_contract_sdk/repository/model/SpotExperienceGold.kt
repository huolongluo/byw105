package com.legend.modular_contract_sdk.repository.model

import com.google.gson.annotations.SerializedName
import com.legend.modular_contract_sdk.utils.TimeUtils
import com.legend.modular_contract_sdk.utils.stringTimeToLong


data class SpotExperienceGold(
        @SerializedName("amount")
        val mAmount: String,
        @SerializedName("conditionActiveAmount")
        val mConditionActiveAmount: String,
        @SerializedName("conditionCurrencyId")
        val mConditionCurrencyId: Int,
        @SerializedName("conditionCurrencyName")
        val mConditionCurrencyName: String,
        @SerializedName("conditionType")
        val mConditionType: Int,
        @SerializedName("createTime")
        val mCreateTime: String,
        @SerializedName("currencyName")
        val mCurrencyName: String,
        @SerializedName("currentTransferInAmount")
        val mCurrentTransferInAmount: String,
        @SerializedName("expireTime")
        val mExpireTime: String,
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
){
    fun toExperienceGold(): ExperienceGold {
        return ExperienceGold(
                mAmount,
                mConditionActiveAmount,
                mConditionCurrencyId,
                mConditionCurrencyName,
                mConditionType,
                mCreateTime.stringTimeToLong("yyyy-MM-dd HH:mm:ss"),
                mCurrencyName,
                mCurrentTransferInAmount,
                mExpireTime.stringTimeToLong("yyyy-MM-dd HH:mm:ss"),
                mId,
                mLeverage,
                mStatus,
                mGoldId,
                mGoldRecordId
        )
    }
}