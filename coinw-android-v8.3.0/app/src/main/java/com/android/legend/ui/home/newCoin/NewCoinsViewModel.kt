package com.android.legend.ui.home.newCoin

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
import kotlinx.coroutines.launch

class NewCoinsViewModel : BaseViewModel() {

    var newCoinData = MutableLiveData<CommonResult<MutableList<NewCoinBean>>>()

    fun getNewCoins(state: Int)= viewModelScope.launch {
        newCoinData.value= ApiRepository.instance.getNewCoins(state)
    }
}