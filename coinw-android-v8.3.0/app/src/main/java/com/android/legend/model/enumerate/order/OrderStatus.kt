package com.android.legend.model.enumerate.order

enum class OrderStatus(val status:String) {
    /**
     * {"success":true,"code":200,"message":"success","retry":false,"data":{"activeOrderStatusMapping":{"ACCEPTED":"待撮合","UN_TRIGGERED":"止盈止损未触发"},
     * "historyOrderStatusMapping":{"CANCELLED":"取消","PARTIALLY_FILLED":"已取消部分成交","FILLED":"已成交","REJECTED":"已拒绝"}}}
     */
    ACCEPTED("ACCEPTED"),UN_TRIGGERED("UN_TRIGGERED"),CANCELLED("CANCELLED"),PARTIALLY_FILLED("PARTIALLY_FILLED"),
    FILLED("FILLED"),REJECTED("REJECTED")
}