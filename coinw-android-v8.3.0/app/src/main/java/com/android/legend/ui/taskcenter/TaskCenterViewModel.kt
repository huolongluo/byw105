package com.android.legend.ui.taskcenter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.legend.api.ApiRepository
import com.android.legend.model.CommonResult
import com.android.legend.model.GetMyTaskBean
import com.android.legend.model.TaskAllBean
import com.android.legend.model.TaskCenterBannerBean
import com.legend.modular_contract_sdk.base.BaseViewModel
import kotlinx.coroutines.launch

class TaskCenterViewModel : BaseViewModel() {


    var mBannerLiveDate = MutableLiveData<CommonResult<TaskCenterBannerBean>>()
    var mTaskAllBeanLiveDate = MutableLiveData<CommonResult<TaskAllBean>>()
    var mGetMyTaskBeanLiveDate = MutableLiveData<CommonResult<GetMyTaskBean>>()

    fun getTaskCenterBanner() = viewModelScope.launch {
        mBannerLiveDate.value = ApiRepository.instance.getTaskCenterBanner()
    }
    //classify字段不传参是获取所有任务，//classify=1:新手任务,8新币任务,11合约任务,14学习任务,-1未知任务
    fun getTaskCenterList(classify: Int, loginToken:String) = viewModelScope.launch {
        mTaskAllBeanLiveDate.value = ApiRepository.instance.getTaskCenterList(classify,loginToken)
    }
    //classify字段不传参是获取所有任务，//classify=1:新手任务,8新币任务,11合约任务,14学习任务,-1未知任务
    fun getTaskCenterList(loginToken:String) = viewModelScope.launch {
        mTaskAllBeanLiveDate.value = ApiRepository.instance.getTaskCenterList(loginToken)
    }
    fun getMyTask(id:Int,loginToken:String) = viewModelScope.launch {
        mGetMyTaskBeanLiveDate.value = ApiRepository.instance.getMyTask(id,loginToken)
    }
}