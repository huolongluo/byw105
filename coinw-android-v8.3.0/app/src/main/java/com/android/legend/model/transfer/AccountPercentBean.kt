package com.android.legend.model.transfer
//账户余额资金构成
data class AccountPercentBean(
        val amount:String,//资金数量
        val name:String//资金构成 名称， 枚举
)