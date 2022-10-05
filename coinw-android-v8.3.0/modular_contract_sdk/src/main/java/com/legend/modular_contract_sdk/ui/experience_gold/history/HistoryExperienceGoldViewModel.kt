package com.legend.modular_contract_sdk.ui.experience_gold.history

import androidx.lifecycle.MutableLiveData
import com.legend.modular_contract_sdk.base.BaseViewModel
import com.legend.modular_contract_sdk.repository.model.ExperienceGold
import com.legend.modular_contract_sdk.repository.setvices.ExperienceGoldService
import com.legend.modular_contract_sdk.ui.contract.ExperienceGoldType
import com.legend.modular_contract_sdk.utils.McConstants

class HistoryExperienceGoldViewModel : BaseViewModel() {

    val mUsedExperienceGoldLiveData = MutableLiveData<List<ExperienceGold>>()

    val mExpiredExperienceGoldLiveData = MutableLiveData<List<ExperienceGold>>()

    private val mExperienceGoldService by lazy {
        ExperienceGoldService.instance()
    }

    fun fetchUsedExperienceGold() {
//        val goldList = mutableListOf(
//                newExperienceGold("10",3),
//                newExperienceGold("100",3),
//                newExperienceGold("11100",3),
//                newExperienceGold("12000",3),
//                newExperienceGold("200",3),
//                newExperienceGold("220",3)
//        )
//        mUsedExperienceGoldLiveData.postValue(goldList)
        request {
            mExperienceGoldService.fetchHistoryExperienceGoldList(ExperienceGoldType.USED.type, 1, McConstants.COMMON.PER_PAGE_SIZE)
                    .apply {
                        if (isSuccess && data != null && data!!.rows != null) {
                            mUsedExperienceGoldLiveData.postValue(data!!.rows)
                        }
                    }
        }
    }

    fun fetchExpiredExperienceGold() {
//        val goldList = mutableListOf(
//                newExperienceGold("10", 4),
//                newExperienceGold("100", 4),
//                newExperienceGold("11100",4),
//                newExperienceGold("12000",4),
//                newExperienceGold("200",4),
//                newExperienceGold("220",4)
//        )
//        mExpiredExperienceGoldLiveData.postValue(goldList)
        request {

            mExperienceGoldService.fetchHistoryExperienceGoldList(ExperienceGoldType.EXPIRED.type, 1, McConstants.COMMON.PER_PAGE_SIZE)
                    .apply {
                        if (isSuccess && data != null && data!!.rows != null) {
                            mExpiredExperienceGoldLiveData.postValue(data!!.rows)
                        }
                    }
        }
    }

}
