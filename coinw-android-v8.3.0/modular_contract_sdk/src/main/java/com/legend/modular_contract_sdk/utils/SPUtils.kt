package com.legend.modular_contract_sdk.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.legend.modular_contract_sdk.BuildConfig
import com.legend.modular_contract_sdk.api.ModularContractSDK
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.ui.contract.QuantityUnit

object SPUtils {

    val SP_NAME = "MC_SDK"

    val sp by lazy { ModularContractSDK.context!!.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE) }

    private val KEY_ORDER_CONFIRM = "order_confirm"
    private val KEY_CLOSE_POSITION_CONFIRM = "close_position_confirm"

    private val KET_TRADE_UNIT = "trade_unit"

    private val KET_BASE_URL = "base_url"

    private val KET_LAST_SELECTED_PRODUCT = "last_selected_product"

    // 下单确认
    fun getOrderConfirm(): Boolean = sp.getBoolean(KEY_ORDER_CONFIRM, true)

    fun saveOrderConfirm(orderConfirm: Boolean) {
        sp.edit {
            putBoolean(KEY_ORDER_CONFIRM, orderConfirm)
        }
    }

    // 平仓确认
    fun getClosePositionConfirm(): Boolean = sp.getBoolean(KEY_CLOSE_POSITION_CONFIRM, true)

    fun saveClosePositionConfirm(closePositionConfirm: Boolean) {
        sp.edit {
            putBoolean(KEY_CLOSE_POSITION_CONFIRM, closePositionConfirm)
        }
    }

    // 交易单位 张 币 USDT
    fun getTradeUnit() = sp.getInt(KET_TRADE_UNIT, QuantityUnit.SIZE.unit)

    fun saveTradeUnit(unit: QuantityUnit) {
        sp.edit {
            putInt(KET_TRADE_UNIT, unit.unit)
        }
    }

    fun saveContractBaseUrl(baseUrl: String) {
        sp?.edit {
            putString(KET_BASE_URL, baseUrl)
        }
    }

    fun getContractBaseUrl() = sp?.let {
        sp.getString(KET_BASE_URL, BuildConfig.BASE_URL)
    } ?: BuildConfig.BASE_URL

    fun saveLastSelectedProduct(product: Product) {
        sp.edit {
            putString(KET_LAST_SELECTED_PRODUCT, JsonUtil.toJson(product))
        }
    }

    fun getLastSelectedProduct(): Product? {
        return JsonUtil.fromJsonToObject<Product>(sp.getString(KET_LAST_SELECTED_PRODUCT,""), Product::class.java)
    }
}