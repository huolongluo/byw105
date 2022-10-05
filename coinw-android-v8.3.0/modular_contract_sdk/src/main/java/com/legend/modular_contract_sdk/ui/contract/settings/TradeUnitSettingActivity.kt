package com.legend.modular_contract_sdk.ui.contract.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.base.BaseActivity
import com.legend.modular_contract_sdk.base.BaseViewModel
import com.legend.modular_contract_sdk.common.event.ChangeTradeUnitEvent
import com.legend.modular_contract_sdk.databinding.McSdkActivityTradeUnitBinding
import com.legend.modular_contract_sdk.ui.contract.QuantityUnit
import com.legend.modular_contract_sdk.utils.McConstants
import com.legend.modular_contract_sdk.utils.SPUtils
import com.legend.modular_contract_sdk.widget.gone
import com.legend.modular_contract_sdk.widget.visible
import org.greenrobot.eventbus.EventBus

class TradeUnitSettingActivity() : BaseActivity<BaseViewModel>() {


    companion object{
        fun launch(context: Context) {
            context.startActivity(Intent(context, TradeUnitSettingActivity::class.java))
        }
    }

    lateinit var mBinding : McSdkActivityTradeUnitBinding

    override fun createViewModel() = ViewModelProvider(this).get(BaseViewModel::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.mc_sdk_activity_trade_unit)

        mBinding.llCoin.setOnClickListener{
            SPUtils.saveTradeUnit(QuantityUnit.COIN)
            EventBus.getDefault().post(ChangeTradeUnitEvent())
            applySelectedUnit()
            finish()
        }

        mBinding.llUsdt.setOnClickListener{
            SPUtils.saveTradeUnit(QuantityUnit.USDT)
            EventBus.getDefault().post(ChangeTradeUnitEvent())
            applySelectedUnit()
            finish()
        }

        mBinding.llCount.setOnClickListener {
            SPUtils.saveTradeUnit(QuantityUnit.SIZE)
            EventBus.getDefault().post(ChangeTradeUnitEvent())
            applySelectedUnit()
            finish()
        }

        applyToolBar(getString(R.string.mc_sdk_switch_trade_unit))

        applySelectedUnit()

        mBinding.tvCountDesc.text = getString(R.string.mc_sdk_trade_unit_desc, "${McConstants.COMMON.CURRENT_PRODUCT.mOneLotSize} ${McConstants.COMMON.CURRENT_PRODUCT.mBase.toUpperCase()}")

    }

    private fun applySelectedUnit() {
        when (SPUtils.getTradeUnit()) {
            QuantityUnit.COIN.unit -> {
                mBinding.ivCoin.visible()
                mBinding.ivUsdt.gone()
                mBinding.ivCount.gone()
            }
            QuantityUnit.USDT.unit -> {
                mBinding.ivCoin.gone()
                mBinding.ivUsdt.visible()
                mBinding.ivCount.gone()
            }
            QuantityUnit.SIZE.unit -> {
                mBinding.ivCoin.gone()
                mBinding.ivUsdt.gone()
                mBinding.ivCount.visible()
            }
            else -> {
            }
        }
    }
}