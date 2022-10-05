package com.android.coinw.api.kx.model

data class XDepthData(
        val seq:Int,//序号，类似id
        val time:Long,
        val asks:ArrayList<ArrayList<String>>,
        val bids:ArrayList<ArrayList<String>>
)