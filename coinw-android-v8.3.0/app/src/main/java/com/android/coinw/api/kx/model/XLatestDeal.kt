package com.android.coinw.api.kx.model

//因为接口和socket返回数据不同，写在同一个对象内方便逻辑
data class XLatestDeal(
        val side //buy  sell
        : String,
        val size //数量
        : String,
        val seq //序号
        : Long,
        val price //价格
        : String,
        val time //时间
        : Long = 0
)