package com.android.legend.ui.finance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.legend.api.ApiRepository
import com.android.legend.model.CommonResult
import com.android.legend.model.finance.ZjFinanceInfo
import kotlinx.coroutines.launch

class ZjViewModel : ViewModel() {
    val zjListData= MutableLiveData<CommonResult<ZjFinanceInfo>>()
    val useData= MutableLiveData<CommonResult<String>>()

    fun getList(giveCoinType: Int)=viewModelScope.launch {
        zjListData.value=ApiRepository.instance.getZjList(giveCoinType)
    }
    fun useZj(id:Int)=viewModelScope.launch {
        useData.value=ApiRepository.instance.useZj(id)
    }
}