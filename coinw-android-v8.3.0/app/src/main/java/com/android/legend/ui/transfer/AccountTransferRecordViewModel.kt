package com.android.legend.ui.transfer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.legend.api.ApiRepository
import com.android.legend.common.BusMutableLiveData
import com.android.legend.model.BaseBizResponse
import com.android.legend.model.CommonResult
import com.android.legend.model.Page
import com.android.legend.model.transfer.TransferRecordBean
import kotlinx.coroutines.launch

class AccountTransferRecordViewModel : ViewModel(){
    val recordList=BusMutableLiveData<CommonResult<BaseBizResponse<Page<TransferRecordBean>>>>()

    fun getTransferRecord(srcAccount:String?,targetAccount:String?,pageNum:Int)=viewModelScope.launch {
        recordList.value=ApiRepository.instance.getTransferRecord(srcAccount,targetAccount,pageNum)
    }
}