package com.legend.modular_contract_sdk.ui.experience_gold

import androidx.lifecycle.MutableLiveData
import com.legend.modular_contract_sdk.base.BaseViewModel
import com.legend.modular_contract_sdk.repository.model.ExperienceGold
import com.legend.modular_contract_sdk.repository.setvices.ExperienceGoldService

class ExperienceGoldViewModel:BaseViewModel() {

    var mExperienceGoldLiveData = MutableLiveData<List<ExperienceGold>>()

    //Integer	status	状态：0-待激活 1-待使用，默认不传
    //Integer	sortType	排序方式：1-按照接收体验金顺序倒序排序，默认值 2-面额大到小 3-即将失效优先
    var mStatus = MutableLiveData<Int>(null)
    var mSortType = MutableLiveData<Int>(1)

    private val mExperienceGoldService by lazy {
        ExperienceGoldService.instance()
    }

    fun getInitData(showLoading:Boolean) {
        request(isShowLoading = showLoading) {
            mExperienceGoldService.fetchExperienceGoldList(mStatus.value, mSortType.value!!)
                    .apply {
                        if (this.isSuccess) {
                            mExperienceGoldLiveData.postValue(this.data)
                        }

                    }
        }
    }



}