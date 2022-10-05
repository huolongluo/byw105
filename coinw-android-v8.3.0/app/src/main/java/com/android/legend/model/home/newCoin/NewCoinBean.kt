package com.android.legend.model.home.newCoin

/**
 * 新币
 */
data class NewCoinBean(
        val base:String,//基础货币
        val quote:String,//计价货币
        val intro:String,//活动简介
        val id:Int,
        val banner:String,
        val onlineReason:String,//上线原因
        val poster:String,//海报
        var quotationTime:String,//上线时间
        var state:Int,//0 待上线 1 已上线
        val high:String,//历史最高价
        val open:String,//开盘价
        val rate:String?,//最高涨幅
        val researchReportAddress:String//研究报告地址
)

//"base": "LTC",
//"intro": "",
//"logo": "https://btc018.oss-cn-shenzhen.aliyuncs.com/201807110221019_Pa4DS.png",
//"onlineReason": "好",
//"poster": "https://btc018.oss-cn-shenzhen.aliyuncs.com/201807110221019_Pa4DS.png",
//"quotationTime": "2021-09-17 09:00:00",
//"quote": "USDT",
//"researchReportAddress": "coinw.la",
//"state": 1
