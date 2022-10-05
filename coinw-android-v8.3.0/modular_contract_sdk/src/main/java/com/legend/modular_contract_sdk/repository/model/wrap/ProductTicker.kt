package com.legend.modular_contract_sdk.repository.model.wrap

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.legend.modular_contract_sdk.component.market_listener.Ticker
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.utils.getDouble
import com.legend.modular_contract_sdk.utils.getNum

class ProductTicker(val product: Product) : BaseObservable() {

    @get:Bindable
    var innerTicker: Ticker? = null
        set(vaule) {
            field = vaule
            notifyChange()
        }
        get() = field


    fun getLast(): String? {
        return innerTicker?.last
    }

    fun getChange(): String? {
        return (innerTicker?.changeRate.getDouble() * 100).toString().getNum(2, true) + "%"
    }

    fun isUp(): Boolean? {
        return (innerTicker?.changeRate.getDouble() > 0)
    }

}