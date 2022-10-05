package com.android.legend.ui.mine.safe

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.legend.api.ApiRepository
import com.android.legend.model.BaseResponse
import com.android.legend.model.CommonResult
import huolongluo.byw.model.NVCResult
import kotlinx.coroutines.launch

class ModifyLoginPwdViewModel : ViewModel() {
    val modifyPwdData=MutableLiveData<CommonResult<NVCResult>>()

    fun modifyPwd(oldPwd: String,newPwd: String,smsCode:String,googleCode:String)=viewModelScope.launch {
        modifyPwdData.value=ApiRepository.instance.modifyLoginPwd(oldPwd,newPwd,smsCode,googleCode)
    }
}