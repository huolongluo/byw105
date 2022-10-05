package com.legend.modular_contract_sdk.repository.model.wrap

import android.content.Context
import android.text.TextUtils
import androidx.databinding.BaseObservable
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.api.ModularContractSDK
import com.legend.modular_contract_sdk.repository.model.ExperienceGold
import com.legend.modular_contract_sdk.ui.contract.ExperienceGoldType
import com.legend.modular_contract_sdk.utils.TimeUtils
import com.legend.modular_contract_sdk.utils.rangeTime

class ExperienceGoldWrap(val experienceGold: ExperienceGold) : BaseObservable() {
    fun getTimeSpan(): String {
        return experienceGold.mCreateTime rangeTime experienceGold.mExpireTime
    }

    fun getExpireTime(): String{

        return TimeUtils.millis2String(experienceGold.mExpireTime)
    }

    fun getAmount(): String {
        return experienceGold.mAmount
    }

    fun getDesc(context: Context): String {
        return experienceGold.mAmount + " USDT - " +context.getString(R.string.mc_sdk_contract_order_type_part) +" X"+ experienceGold.mLeverage
    }

    fun isEnable() = experienceGold.mStatus == ExperienceGoldType.ACTIVE.type || experienceGold.mStatus == ExperienceGoldType.NOT_ACTIVE.type

    fun isActive() = experienceGold.mStatus == ExperienceGoldType.ACTIVE.type

    fun isNotActive() = experienceGold.mStatus == ExperienceGoldType.NOT_ACTIVE.type

    fun getActionName(context: Context): String =
            when (experienceGold.mStatus) {
                ExperienceGoldType.NOT_ACTIVE.type -> context.resources.getString(R.string.mc_sdk_to_activate)
                ExperienceGoldType.ACTIVE.type -> context.resources.getString(R.string.mc_sdk_to_use)
                else -> ""
            }

    fun getLimit(context: Context): String {
        return if (TextUtils.isEmpty(experienceGold.mCurrencyName)) {
            context.resources.getString(R.string.mc_sdk_contract_experience_gold_desc1)
        } else {
            context.resources.getString(R.string.mc_sdk_contract_experience_gold_limit_coin, experienceGold.mCurrencyName + "/USDT")
        }
    }

    fun getConditionCurrencyName() = experienceGold.mConditionCurrencyName

    fun getConditionAmount() = experienceGold.mConditionActiveAmount

    fun getCurrentAmount() = experienceGold.mCurrentTransferInAmount

    fun getLeverage() = experienceGold.mLeverage
}