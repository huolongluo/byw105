package com.android.legend.model

import com.chad.library.adapter.base.entity.MultiItemEntity

data class TaskCenterBannerBean(
        val APP_BANNER_4: MutableList<BannerBean>
)

data class BannerBean(
        val goOut: Boolean,
        val id: String,
        val img: String,
        val url: String
)

//"goOut":false,
//"id":744,
//"img":"https://btc018.oss-cn-shenzhen.aliyuncs.com/202108021120019_uhovu.png",
//"mImg":null,
//"url":"http://www.baidu.com"


data class TaskAllBean(
        val NEW_TASK: MutableList<TaskDetail>,
        val SPOT_TASK: MutableList<TaskDetail>,
        val CONTARCT_TASK: MutableList<TaskDetail>,
        val STUDY_TASK: MutableList<TaskDetail>
)

//NEW_TASK(1, "新手任务"),
//SPOT_TASK(8, "新币任务"),
//CONTARCT_TASK(11, "合约任务"),
//STUDY_TASK(14, "学习任务"),
//UN_KNOW(-1, "未知任务");
data class TaskDetail(
        //条目类型
        val itemStyle: Boolean = false,
        val amount: Double = 0.0,
        val awardId: Int = -1,
        val awardType: Int = -1,
        val classify: Int = -1,
        val classifyName: String? = "",
        val content: String? = "",
        val dueTime: Long = -1,
        val expireDate: Int = -1,
        val id: Int = -1,
        var isClaimTask: Int = -1,
        val isComplete: Int = -1,
        val isExpired: Boolean = false,
        val isQualified: Boolean = false,
        val name: String? = "",
        val number: Int = -1,
        val receiveNumber: Int = -1,
        val startTime: Long = -1,
        val taskAward: TaskAwardBean? = null,
        val tradeType: Int = -1,
        val typeId: Int = -1,
        val typeName: String? = "",
        override var itemType: Int = 0
) : MultiItemEntity {
    companion object {
        const val itemTitle: Int = 1
        const val itemContent: Int = 2
    }

    init {
        itemType = if (itemStyle) itemTitle else itemContent
    }
}

//"amount": 0,完成目标交易量数量
//"awardId": 35,奖品id
//"awardType": 1,奖品类型
//"classify": 1,任务分类id
//"classifyName": "新手任务",
//"content": "初级认证",
//"dueTime": 1635487903000,用户任务完成期限 领取后 多长时间完成 期限
//"expireDate": 10,	任务有效期，任务到期截止，上线有效期
//"id": 27,
//"isClaimTask": 0,	是否领取 0：未领取 1：已经领取
//"isComplete": 0,是否已经完成 0：未完成 1：已经完成
//"isExpired": false,是否已经过期 false ：没过期 true ：已过期
//"isQualified": false,任务是否不符合新手条件 true：符合 false ：不符合
//"name": "新手任务",	任务名称
//"number": 100,	任务数量
//"receiveNumber": 0,已经领取数量
//"startTime": 1634623903000,任务开始时间
//"taskAward": {
//    "amount": 0.0001,
//    "currency": "ETH",
//    "currencyId": 16,
//    "name": "现货赠金(ETH,0.0001)",
//    "type": 1
//},
//"tradeType": 1,交易类型 1现货，2合约
//"typeId": 3,任务类型id
//"typeName": "初级认证"任务类型名称
//}
data class TaskAwardBean(
        val amount: Double = 0.0,
        val currency: String? = "",
        val currencyId: Int = -1,
        val name: String? = "",
        val type: Int = -1
)
//"amount": 0.0001,奖品数量
//"currency": "ETH",奖品币种
//"currencyId": 16,奖品币种id
//"name": "现货赠金(ETH,0.0001)",奖品名称
//"type": 1奖品类型