package com.android.legend.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.legend.api.ApiRepository
import com.android.legend.model.CommonResult
import huolongluo.byw.byw.bean.BankListBean
import huolongluo.byw.byw.bean.CommonBean
import kotlinx.coroutines.launch

class BasicViewModel : ViewModel() {
    companion object {
        const val TAG = "EmailViewModel"
    }

    val bindEmail = MutableLiveData<CommonResult<CommonBean>>()
    val bankList = MutableLiveData<CommonResult<BankListBean>>()
    val sms = MutableLiveData<CommonResult<CommonBean>>()
    val withDrawCny = MutableLiveData<CommonResult<CommonBean>>()

    //绑定邮箱
    fun bindEmail(email: String) = viewModelScope.launch {
        bindEmail.value = ApiRepository.instance.bindEmail(email)
    }

    //获得银行列表
    fun getWithdrawBankList() = viewModelScope.launch {
        bankList.value = ApiRepository.instance.getWithdrawBankList()
    }

    //发送短信
    fun sendSMS(type: String) = viewModelScope.launch {
        sms.value = ApiRepository.instance.sendSmsNoManMachine(type)
    }

    //
    fun withDrawCny(body: String) = viewModelScope.launch {
        withDrawCny.value = ApiRepository.instance.withDrawCny(body)
    }
}