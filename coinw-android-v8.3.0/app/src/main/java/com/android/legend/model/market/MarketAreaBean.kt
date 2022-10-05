package com.android.legend.model.market

import java.io.Serializable

data class MarketAreaBean(
        val active: Boolean,//是否展示
        val areaCoinsStr: String,//USDT_CNYT 可为空
        val areaName: String,//分区名称
        val fLable: String,//HOT NEW,等标签 可为空
        val id: Int,//分区id
        val showRisk: Boolean,//是否风险提示
        var searchNum:Int,//搜索到币对的数量 自定义
        val tradeAreaVos:ArrayList<MarketAreaBean>//下级分区列表 可为空
):Serializable