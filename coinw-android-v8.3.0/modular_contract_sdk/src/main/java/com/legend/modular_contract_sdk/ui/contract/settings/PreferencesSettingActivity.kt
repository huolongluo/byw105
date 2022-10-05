package com.legend.modular_contract_sdk.ui.contract.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.base.BaseActivity
import com.legend.modular_contract_sdk.base.BaseViewModel
import com.legend.modular_contract_sdk.common.event.ChangeTradeUnitEvent
import com.legend.modular_contract_sdk.ui.contract.QuantityUnit
import com.legend.modular_contract_sdk.utils.McConstants
import com.legend.modular_contract_sdk.utils.SPUtils
import com.legend.modular_contract_sdk.widget.gone
import com.legend.modular_contract_sdk.widget.visible
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PreferencesSettingActivity : BaseActivity<BaseViewModel>() {

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, PreferencesSettingActivity::class.java))
        }
    }

    override fun createViewModel(): BaseViewModel = ViewModelProvider(this).get(BaseViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mc_sdk_activity_preferences_setting)
        applyToolBar(getString(R.string.mc_sdk_preferences_setting))

        findViewById<Switch>(R.id.switch_order_confirm).apply {
            isChecked = SPUtils.getOrderConfirm()
            setOnCheckedChangeListener { buttonView, isChecked ->
                SPUtils.saveOrderConfirm(isChecked)
            }
        }

        findViewById<Switch>(R.id.switch_close_position_confirm).apply {
            isChecked = SPUtils.getClosePositionConfirm()
            setOnCheckedChangeListener { buttonView, isChecked ->
                SPUtils.saveClosePositionConfirm(isChecked)
            }
        }

        findViewById<View>(R.id.ll_trade_unit).setOnClickListener {
            TradeUnitSettingActivity.launch(this)
        }

        applySelectedUnit()

        findViewById<TextView>(R.id.tv_count_desc).text = getString(R.string.mc_sdk_trade_unit_desc, "${McConstants.COMMON.CURRENT_PRODUCT.mOneLotSize} ${McConstants.COMMON.CURRENT_PRODUCT.mBase.toUpperCase()}")

    }

    private fun applySelectedUnit() {
        when (SPUtils.getTradeUnit()) {
            QuantityUnit.COIN.unit -> {
                findViewById<TextView>(R.id.tv_trade_unit).setText(R.string.mc_sdk_trade_unit_coin)
            }
            QuantityUnit.USDT.unit -> {
                findViewById<TextView>(R.id.tv_trade_unit).setText(R.string.mc_sdk_usdt)
            }
            QuantityUnit.SIZE.unit -> {
                findViewById<TextView>(R.id.tv_trade_unit).setText(R.string.mc_sdk_contract_unit)
            }
            else -> {
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeTradeUnit(event: ChangeTradeUnitEvent) {
        applySelectedUnit()
        finish()
    }
}