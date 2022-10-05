package com.android.legend.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.legend.api.ApiRepository
import com.android.legend.common.BusMutableLiveData
import com.android.legend.model.BaseResponse
import com.android.legend.model.CommonResult
import com.android.legend.model.login.LoginParam
import huolongluo.byw.model.AliManMachineEntity
import huolongluo.byw.model.AreaCodeBean
import huolongluo.byw.model.NVCResult
import huolongluo.byw.reform.bean.LoginBean
import kotlinx.coroutines.launch

class LoginViewModel :ViewModel() {
    val aliVerifyData= BusMutableLiveData<CommonResult<NVCResult>>()
    val aliVerifyFindPwdData= BusMutableLiveData<CommonResult<NVCResult>>()
    val loginData=BusMutableLiveData<CommonResult<LoginBean>>()
    val registerData=BusMutableLiveData<CommonResult<LoginBean>>()
    val sendCodeData=BusMutableLiveData<CommonResult<NVCResult>>()
    val checkRegisterAccountData=BusMutableLiveData<CommonResult<BaseResponse<String>>>()
    val checkRegisterCodeData=BusMutableLiveData<CommonResult<NVCResult>>()
    val resetPwdVerifyCodeData=BusMutableLiveData<CommonResult<BaseResponse<String>>>()
    val areaCodeData=BusMutableLiveData<CommonResult<List<AreaCodeBean>>>()

    fun checkRegisterAccount(loginParam: LoginParam)= viewModelScope.launch {
        checkRegisterAccountData.value= ApiRepository.instance.checkRegisterAccount(loginParam)
    }
    fun checkRegisterCode(loginParam: LoginParam)= viewModelScope.launch {
        checkRegisterCodeData.value= ApiRepository.instance.checkRegisterCode(loginParam)
    }
    fun resetPwdVerifyCode(loginParam: LoginParam)= viewModelScope.launch {
        resetPwdVerifyCodeData.value= ApiRepository.instance.resetPwdVerifyCode(loginParam)
    }

    fun aliVerify(entity: AliManMachineEntity) = viewModelScope.launch {
        aliVerifyData.value= ApiRepository.instance.aliVerify(entity)
    }
    //找回密码使用该接口
    fun aliVerifyFindPwd(loginParam: LoginParam,entity: AliManMachineEntity) = viewModelScope.launch {
        aliVerifyFindPwdData.value= ApiRepository.instance.aliVerifyFindPwd(loginParam,entity)
    }
    fun login(loginParam: LoginParam) = viewModelScope.launch {
        loginData.value= ApiRepository.instance.login(loginParam)
    }
    fun register(loginParam: LoginParam) = viewModelScope.launch {
        registerData.value= ApiRepository.instance.register(loginParam)
    }
    fun sendLoginSms(loginParam: LoginParam,entity: AliManMachineEntity)= viewModelScope.launch {
        sendCodeData.value= ApiRepository.instance.sendSms(loginParam.messageType,loginParam.username,loginParam.areaCode,entity)
    }
    fun sendLoginEmail(loginParam: LoginParam,entity: AliManMachineEntity)= viewModelScope.launch {
        sendCodeData.value= ApiRepository.instance.sendLoginEmail(loginParam,entity)
    }
    fun getAreaCode()= viewModelScope.launch {
        areaCodeData.value= ApiRepository.instance.getAreaCode()
    }
}