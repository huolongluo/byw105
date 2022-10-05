package com.legend.modular_contract_sdk.repository.model
import com.google.gson.annotations.SerializedName
import com.legend.modular_contract_sdk.component.market_listener.MarketData

data class LastDeal(
    @SerializedName("createdDate")
    val mCreatedDate: Long,
    @SerializedName("direction")
    val mDirection: String,
    @SerializedName("id")
    val mId: Long,
    @SerializedName("price")
    val mPrice: String,
    @SerializedName("quantity")
    val mQuantity: String
):MarketData()