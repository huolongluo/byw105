package com.android.legend.model.transfer

/**
 * 划转记录列表的item
 */
data class TransferRecordBean(
        val amount:String,//划转金额
        val coinIcon:String,//币种图标 url
        val coinName:String,
        val fromAccount:String,//出账账户
        val targetAccount:String,//入账账户
        val transferTime:Long,//划转时间
        val status:Int// "0：划转中， 1：划转成功，2：划转失败"
)