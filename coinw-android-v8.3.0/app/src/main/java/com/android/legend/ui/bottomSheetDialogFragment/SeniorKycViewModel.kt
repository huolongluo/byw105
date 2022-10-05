package com.android.legend.ui.bottomSheetDialogFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.legend.api.ApiRepository
import com.android.legend.model.CommonResult
import huolongluo.byw.byw.bean.CommonBean
import kotlinx.coroutines.launch

class SeniorKycViewModel : ViewModel(){
    val agreeLiveData=MutableLiveData<CommonResult<CommonBean>>()

    fun overseaSeniorKyc() = viewModelScope.launch{
        agreeLiveData.value=ApiRepository.instance.overseaSeniorKyc()
    }
}