package com.legend.modular_contract_sdk.repository.model.wrap

import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.repository.model.LastDeal
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.ui.contract.Direction
import com.legend.modular_contract_sdk.utils.getDate
import com.legend.modular_contract_sdk.utils.getNum

class LastDealWrap (val lastDeal:LastDeal,val product: Product){
    fun getDate(): String{
        return lastDeal.mCreatedDate.getDate("HH:mm:ss")
    }

    fun isBuy() = lastDeal.mDirection == "long"

    fun getDirection() = run {
        if (lastDeal.mDirection == Direction.LONG.direction) {
            R.string.mc_sdk_buy
        } else {
            R.string.mc_sdk_sell
        }
    }

    fun getPrice():String{
        return lastDeal.mPrice.getNum(product.mPricePrecision, withZero = true)
    }
}