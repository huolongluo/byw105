package com.legend.modular_contract_sdk.repository.model

import com.google.gson.annotations.SerializedName


data class USDRate(
    @SerializedName("usd_cny")
    val mUsdCny: Double,
    @SerializedName("usd_eur")
    val mUsdEur: Double,
    @SerializedName("usd_jpy")
    val mUsdJpy: Double,
    @SerializedName("usd_rub")
    val mUsdRub: Double
)