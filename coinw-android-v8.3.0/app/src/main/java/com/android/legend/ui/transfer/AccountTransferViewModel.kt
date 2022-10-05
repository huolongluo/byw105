package com.android.legend.ui.transfer

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.legend.api.ApiRepository
import com.android.legend.model.BaseBizResponse
import com.android.legend.model.CommonResult
import com.android.legend.model.enumerate.transfer.TransferAccount
import com.android.legend.model.transfer.TransferBean
import com.android.legend.model.transfer.TransferResultBean
import huolongluo.byw.R
import huolongluo.byw.byw.manager.DialogManager2
import huolongluo.byw.byw.ui.fragment.contractTab.NMInfoEntity
import huolongluo.byw.byw.ui.fragment.contractTab.nima.NMEntity
import huolongluo.byw.log.Logger
import huolongluo.byw.util.tip.MToast
import kotlinx.coroutines.launch
import java.lang.Exception

class AccountTransferViewModel : ViewModel() {
    companion object {
        const val TAG = "AccountTransferViewModel"
    }
    val transferData=MutableLiveData<CommonResult<BaseBizResponse<TransferBean>>>() //第一次进来使用该数据
    val transferResult=MutableLiveData<CommonResult<BaseBizResponse<TransferResultBean>>>() //划转结果
    val contractMudInfo=MutableLiveData<CommonResult<NMEntity>>()//获取合约划转相关信息，是否实名等
    val nmInfo=MutableLiveData<CommonResult<NMInfoEntity>>()//获取泥码活动

    //获取全部数据，第一次进入和切换账户调用
    fun getTransferData(srcAccount:String,targetAccount:String,coinId:Int) = viewModelScope.launch {
        transferData.value= ApiRepository.instance.getTransferData(srcAccount,targetAccount,coinId)
    }
    //切换币种读取资产
    fun getTransferFinance(srcAccount:String,targetAccount:String,coinId:Int) = viewModelScope.launch {
        transferData.value= ApiRepository.instance.getTransferFinance(srcAccount,targetAccount,coinId)
    }
    //划转
    fun transfer(context: Context, transferBean: TransferBean?, amount:String, coinId: Int)=viewModelScope.launch{
        Logger.getInstance().debug(TAG,"transferBean.account:${transferBean?.account} transferBean.targetAccoun: ${transferBean?.targetAccount}")
        if(TextUtils.isEmpty(amount)||amount.startsWith(".")||amount.toDouble()<=0){
            MToast.show(context, context.getString(R.string.qa12), 1)
            return@launch
        }
        if(transferBean==null){
            MToast.show(context, context.getString(R.string.cx55), 1)
            return@launch
        }
        try {
            if (TextUtils.equals(transferBean.account, TransferAccount.CONTRACT.value) && amount.toDouble() > transferBean.contractTransferAvailable!!.toDouble()) {
                MToast.show(context, context.getString(R.string.Insufficient_balance), 1)
                return@launch
            }
        } catch (e: Exception) {
            // transferBean.contractTransferAvailable 有可能为空所以加个try-catch
            e.printStackTrace()
        }
        if(amount.toDouble()>transferBean.accountAvailable.toDouble()){
            MToast.show(context, context.getString(R.string.Insufficient_balance), 1)
            return@launch
        }

        DialogManager2.INSTANCE.showProgressDialog(context)
        transferResult.value=ApiRepository.instance.transfer(transferBean.account,transferBean.targetAccount,amount,coinId)
    }

    fun getContractMudInfo()=viewModelScope.launch {
        contractMudInfo.value=ApiRepository.instance.getContractMudInfo()
    }
    fun getNMInfo()=viewModelScope.launch {
        nmInfo.value=ApiRepository.instance.getNMInfo()
    }
}