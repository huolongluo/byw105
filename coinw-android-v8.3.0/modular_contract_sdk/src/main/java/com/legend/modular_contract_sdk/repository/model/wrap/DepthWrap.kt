package com.legend.modular_contract_sdk.repository.model.wrap

import com.legend.modular_contract_sdk.component.market_listener.Depth
import com.legend.modular_contract_sdk.utils.getDouble
import com.legend.modular_contract_sdk.utils.getNum
import java.math.BigDecimal

class DepthWrap(val depth: Depth, val oneLotSize: Double, val precision: Int) {

    var t: Long = 0
    var asks: List<PendingOrderWrap> = mutableListOf()
    var bids: List<PendingOrderWrap> = mutableListOf()
    var n: String = ""


    init {
        t = depth.t
        asks = depth.asks.map { PendingOrderWrap(it) }
        bids = depth.bids.map { PendingOrderWrap(it) }
        n = depth.n
    }


    inner class PendingOrderWrap(private val pendingOrder: Depth.PendingOrder) {
        fun getM(): String {
            return (pendingOrder.m.getDouble() / oneLotSize).toString()
                .getNum(0, roundingMode = BigDecimal.ROUND_UP)
        }

        fun getP(): String {
            return pendingOrder.p.getNum(precision, true)
        }
    }
}