package com.android.legend.ui.taskcenter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.legend.api.ApiRepository
import com.android.legend.model.CommonResult
import com.android.legend.model.MyTaskAllBean
import com.legend.modular_contract_sdk.base.BaseViewModel
import kotlinx.coroutines.launch

class MyTaskViewModel : BaseViewModel() {

    var mMyTaskAllBeanLiveDate = MutableLiveData<CommonResult<MyTaskAllBean>>()

    fun getMyTaskList(loginToken:String) = viewModelScope.launch {
        mMyTaskAllBeanLiveDate.value = ApiRepository.instance.getMyTaskList(loginToken)
    }
}