package com.android.legend.ui.bottomSheetDialogFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.legend.api.ApiRepository
import com.android.legend.common.BusMutableLiveData
import com.android.legend.model.CommonResult
import com.android.legend.model.login.LoginParam
import huolongluo.byw.model.AliManMachineEntity
import huolongluo.byw.model.NVCResult
import kotlinx.coroutines.launch

class CommonSmsGoogleVerifyViewModel : ViewModel(){
    val smsData=BusMutableLiveData<CommonResult<NVCResult>>()
    val aliVerifyData= BusMutableLiveData<CommonResult<NVCResult>>()

    fun aliVerify(entity: AliManMachineEntity) = viewModelScope.launch {
        aliVerifyData.value= ApiRepository.instance.aliVerify(entity)
    }
    fun sendSms(type:Int, entity: AliManMachineEntity)= viewModelScope.launch {
        smsData.value= ApiRepository.instance.sendSms(type,entity=entity)
    }
}