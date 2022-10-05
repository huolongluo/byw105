package com.android.legend.model.finance

data class BbFinanceBean(
        val accountType:String,
        val accounts:MutableList<BbFinanceAccountListBean>,
        val total:String//总资产 CNYT折算
)