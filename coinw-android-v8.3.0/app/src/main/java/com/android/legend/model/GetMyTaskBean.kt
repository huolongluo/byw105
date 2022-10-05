package com.android.legend.model

 data class GetMyTaskBean (
    val code : Int,
    val date : String,
    val message : String,
    val retry : Boolean,
    val success : Boolean
 )
//"code": 1000002,
//"data": null,
//"message": "查询不到此任务",
//"retry": true,
//"success": false

//200: "领取成功!",
//10001: "没有找到用户",
//1000001: "任务已过期无法领取",
//1000002: "查询不到此任务",
//1000003: "领取任务失败,联系管理员",
//1000004: "任务数量不足，无法领取",
//1000005: "系统繁忙稍后再试..",
//1000006: "用户在黑名单与管理员联系",
//1000007: "已经领取过这个任务，不能再次领取",
//1000010: "注册任务,无法领取!",
//1000011: "非法领取,已经通知管理员恶意操作将被封号!",
//10000012: "未开通合约",