package com.android.legend.model.finance

data class ZjFinanceInfo(
    val unlock:String,//解锁资产
    val nounlock :String,//未解锁资产
    val data:MutableList<ZjFinanceBean>
)
