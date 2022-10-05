package com.legend.modular_contract_sdk.repository.model

import com.google.gson.annotations.SerializedName


data class TreadConfig(
    @SerializedName("actual")
    val mActual: Int,
    @SerializedName("layout")
    val mLayout: Int,
    @SerializedName("swapLeverage")
    val mSwapLeverage: Int,
    @SerializedName("swapStopLossRate")
    val mSwapStopLossRate: Double,
    @SerializedName("swapStopProfitRate")
    val mSwapStopProfitRate: Double
)