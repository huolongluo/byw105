package com.android.legend.model

data class MyTaskAllBean(
        val PENDING_PAY: MutableList<MyTaskDetail>,
        val COMPLETE: MutableList<MyTaskDetail>,
        val PROVIDED: MutableList<MyTaskDetail>,
        val EXPIRED: MutableList<MyTaskDetail>
)

//PENDING_PAY(1, "进行中"),
//COMPLETE(2, "已完成"),
//PROVIDED(3, "已发放"),
//EXPIRED(4, "已过期"),
//UN_KNOW(-1, "未知状态");
data class MyTaskDetail(
        val awardId: Int,
        val compleTime: Long,
        val content: String,
        val dueTime: Long,
        val finishAmount: Double,
        val id: Int,
        val number: Int,
        val taskAward: MyTaskAwardBean,
        val taskAwardType: Int,
        val typeId: Int,
        val taskName: String,
        val taskState: Int,
        val taskStateName: String,
        val taskTradeType: Int,
        val taskTypeId: Int,
        val totalAmount: Double
)


//"awardId": 35,奖品id
//""compleTime": 1634619442000,,完成时间
//"content": "初级认证",任务描述
//"dueTime": 1635487903000,用户任务完成期限 领取后 多长时间完成 期限
// "finishAmount": 5000,	完成数量
//"id": 27,
//"number": 100,	任务数量
//"taskAward": {
//    "amount": 0.0001,
//    "currency": "ETH",
//    "currencyId": 16,
//    "name": "现货赠金(ETH,0.0001)",
//    "type": 1
//},
//      "taskAwardType": 1, 任务奖品类型 1 现货奖品 2体验金奖品
//"typeId": 3,任务类型id
//"taskName": "全币对累计交易5000"任务名称
//"taskState": 3,任务状态 1：进行中 2：已完成 3:已发放 4：已过期 '
//"taskStateName": "PROVIDED",任务状态名
// "taskTradeType": 2,任务交易类型 1 现货交易 2 合约交易
//"taskTypeId": 13,
// "totalAmount": 5000
//}
data class MyTaskAwardBean(
        val amount: Double,
        val currency: String,
        val currencyId: Int,
        val name: String,
        val type: Int
)
//"amount": 0.0001,奖品数量
//"currency": "ETH",奖品币种
//"currencyId": 16,奖品币种id
//"name": "现货赠金(ETH,0.0001)",奖品名称
//"type": 1奖品类型