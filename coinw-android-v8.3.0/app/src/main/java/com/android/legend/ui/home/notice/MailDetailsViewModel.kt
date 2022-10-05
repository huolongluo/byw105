package com.android.legend.ui.home.notice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.legend.api.ApiRepository
import com.android.legend.api.apiService
import com.android.legend.model.CommonResult
import com.android.legend.model.home.*
import com.android.legend.model.home.newCoin.NewCoinBean
import com.android.legend.model.login.LoginParam
import com.legend.modular_contract_sdk.base.BaseViewModel
import com.legend.modular_contract_sdk.common.postValueNotNull
import huolongluo.byw.byw.inform.bean.MailBean
import kotlinx.coroutines.launch

class MailDetailsViewModel : BaseViewModel() {

    var mailData = MutableLiveData<CommonResult<MailBean>>()

    fun getMailDetails(fId: Long)= viewModelScope.launch {
        mailData.value= ApiRepository.instance.getMailDetails(fId)
    }
}