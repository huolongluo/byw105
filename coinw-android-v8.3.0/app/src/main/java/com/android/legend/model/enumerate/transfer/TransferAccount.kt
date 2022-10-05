package com.android.legend.model.enumerate.transfer


//账户划转的划转账户
enum class TransferAccount(val value:String) {
    //example: SPOT: 现货（币币），OTC: 买币，CONTRACT: 合约，WEALTH:资产； BDB:币贷宝; EARN:理财账户
    WEALTH("WEALTH"),SPOT("SPOT"),OTC("OTC"),CONTRACT("CONTRACT"),BDB("COIN_LOAN"),EARN("FINANCIAL")

}