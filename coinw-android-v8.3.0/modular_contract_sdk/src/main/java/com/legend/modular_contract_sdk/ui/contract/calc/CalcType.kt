package com.legend.modular_contract_sdk.ui.contract.calc

enum class CalcType (val type:Int){
    CALC_PROFIT(1),
    CALC_CLOSE_PRICE(2),
    CALC_LIQUIDATION(3);

    fun isProfit() = type == CALC_PROFIT.type

    fun isClosePrice() = type == CALC_CLOSE_PRICE.type

    fun isLiquidation() = type == CALC_LIQUIDATION.type
}