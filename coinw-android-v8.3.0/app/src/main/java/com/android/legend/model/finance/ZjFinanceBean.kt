package com.android.legend.model.finance
//赠金
data class ZjFinanceBean(
        val activationCurrencyName:String,//合约限制币种 （如果币种为空，则代表没有限制）
        val awardAmount:String,
        val awardCurrency:String,//奖品币种
        val awardCurrencyId:Int,
        val conditionActiveAmount:String,//合约限制金额
        val conditionCurrencyName:String,
        val conditionLevelAge:String,//合约限制杠杆倍数
        val currentTransferInAmount:String,
        val handleAwardAmount:String,//奖品金额
        val id:Long,
        val taskAwardType:Int,//区分合约、现货 1：现货 2：合约
        val taskExpireDate:String,//过期时间
        val taskId:Long,
        val taskName:String,
        val taskTradeType:Int,
        val taskTypeCoinType:Int,
        val userTaskAwardState:Int,//奖品状态 0：未发放 1：未使用 2：已使用 3：已过期
        val userTaskContractAwardId:Long,
        val userTaskGrantAmount:String,
        val userTaskId:Int,
        val userTaskState:Int,//状态：如果是现货：1：进行中 2：已完成 3:已发放 4：已过期 合约：0：待激活 1：待使用 3：已使用 4：已过期
        val userTaskUid:Long
)
