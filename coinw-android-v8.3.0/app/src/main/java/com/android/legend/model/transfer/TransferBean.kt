package com.android.legend.model.transfer

import huolongluo.byw.byw.bean.CoinAddressBean
//划转获取的信息
data class TransferBean(
    val account:String,//源账户
    val accountAvailable:String,//账户可划转余额
    val contractTransferAvailable:String?,//合约专用字段（accountAvailable为账户的可划转余额，contractTransferAvailable为0则全仓持有持仓不能划转）
    val accountTotal:String,//账户可用余额
    val accountTotalPercent:MutableList<AccountPercentBean>?,//账户余额资金构成
    val availableCoins:MutableList<CoinAddressBean>?,//支持划转的币种
    val coinId:Int,
    var targetAccount:String,//目标账户
    val targetTotal:String,//目标账户可用余额
    val unavailableAccounts:MutableList<String>?//未开通账户列表
)