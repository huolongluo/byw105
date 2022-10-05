package com.android.legend.model.finance

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//币币资产账户列表
@SuppressLint("ParcelCreator")
@Parcelize
data class BbFinanceAccountListBean(
        val availableBalance:String,//可用余额
        val balanceAmount:String,//余额折合 CNYT
        val coin:FinanceCoin,//账户查询币种基础信息
        val holdBalance:String,//冻结余额
        val supportTransfer:Boolean,//是否支持划转
        val totalBalance:String,//总余额
        val pairs:MutableList<FinancePairsCoin>//交易对最小信息
):Parcelable