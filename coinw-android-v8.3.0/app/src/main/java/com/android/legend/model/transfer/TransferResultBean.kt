package com.android.legend.model.transfer

import huolongluo.byw.byw.ui.fragment.contractTab.ContractTransferEntity
//划转结果
data class TransferResultBean(
        val bizType:String, //划转反向
        val otcVo:String,
        val contractVo: ContractTransferEntity//, //合约
        //val marginVo:String, //杠杆
        //val wealthVo:String//币贷宝
)