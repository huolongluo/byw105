package com.android.legend.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.legend.api.ApiRepository
import com.android.legend.common.BusMutableLiveData
import com.android.legend.model.BaseResponse
import com.android.legend.model.CommonResult
import com.android.legend.model.login.LoginParam
import huolongluo.byw.model.AliManMachineEntity
import huolongluo.byw.model.NVCResult
import huolongluo.byw.reform.bean.LoginBean
import kotlinx.coroutines.launch

class ResetLoginPwdViewModel :ViewModel() {
    val resetPwdData= BusMutableLiveData<CommonResult<BaseResponse<String>>>()

    fun resetPwd(loginParam: LoginParam) = viewModelScope.launch {
        resetPwdData.value= ApiRepository.instance.resetPwd(loginParam)
    }
}