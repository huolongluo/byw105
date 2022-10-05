package com.legend.modular_contract_sdk.widget.dialog

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.legend.common.util.ThemeUtil
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.component.market_listener.*
import com.legend.modular_contract_sdk.databinding.McSdkDialogClosePositionBinding
import com.legend.modular_contract_sdk.repository.model.wrap.PositionWrap
import com.legend.modular_contract_sdk.ui.contract.ClosePositionType
import com.legend.modular_contract_sdk.ui.contract.PositionType
import com.legend.modular_contract_sdk.ui.contract.QuantityUnit
import com.legend.modular_contract_sdk.utils.*
import com.legend.modular_contract_sdk.utils.inputfilter.DecimalDigitsInputFilter
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.core.CenterPopupView
import com.orhanobut.logger.Logger
import java.math.BigDecimal

class ClosePartPositionDialog(
        context: Context,
        val tradeUnit: QuantityUnit,
        val position: PositionWrap,
        val onConfirm: (PositionType, price: String, count: Double, inputCount: String) -> Unit
) : BottomPopupView(context) {

    private lateinit var mBinding: McSdkDialogClosePositionBinding

    //1.按比例 2.按张数
    private var mClosePositionCount = ObservableField<String>("")
    private var mClosePositionPrice = ObservableField<String>("")

    // 0:市价 1：限价
    private var mClosePositionType = ObservableInt(0)

    var mMarketListener: MarketListener? = null

    override fun onCreate() {
        super.onCreate()
        mBinding = McSdkDialogClosePositionBinding.bind(popupImplView)

        mBinding.closePositionCount = mClosePositionCount
        mBinding.closePositionType = mClosePositionType
        mBinding.closePositionPrice = mClosePositionPrice

        var unit = ""
        var scale = 0

        when (tradeUnit) {
            QuantityUnit.SIZE -> {
                unit = context.getString(R.string.mc_sdk_contract_unit)
                scale = 0
            }
            QuantityUnit.USDT -> {
                unit = context.getString(R.string.mc_sdk_usdt)
                scale = 4
            }
            QuantityUnit.COIN -> {
                unit = position.product?.mBase?.toUpperCase() ?: ""
                scale = position.product?.mOneLotSize?.getPrecision()
                        ?: 3
            }
        }

        val maxCount = position.getCanClosePosition(tradeUnit.unit).toDouble()

        mBinding.etClosePositionCount.filters = arrayOf(DecimalDigitsInputFilter(6, scale))

        mBinding.etClosePositionPrice.filters = arrayOf(DecimalDigitsInputFilter(6, position.product?.mPricePrecision
                ?: 6))

        mBinding.tvOpenPrice.text = position.getOpen()

        mBinding.tvCanCloseNum.text = "${position.getCanClosePosition(tradeUnit.unit)} $unit"

        mBinding.tvUnit.text = unit

        mBinding.tvDirection.background =
                if (position.isLong()) ThemeUtil.getThemeDrawable(context, R.attr.bg_buy_btn)
                else ThemeUtil.getThemeDrawable(context, R.attr.bg_sell_btn)

        mBinding.tvDirection.text = context.getString(position.getDirection())

        mBinding.tvTitle.text = context.getString(R.string.mc_sdk_contract_name, position.getContractName())

        mClosePositionCount.addOnPropertyChangedCallback(
                object : Observable.OnPropertyChangedCallback() {
                    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                        mClosePositionCount.get()?.let { inputCount ->
                            if (inputCount.getDouble() > maxCount) {
                                mClosePositionCount.set("${maxCount.toInt()}")
                                return@let
                            }

                        }
                    }

                }
        )

        mClosePositionPrice.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                applyButtonEnable()
            }
        })

        mClosePositionCount.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                applyButtonEnable()
            }
        })

        mBinding.tvChangeClosePositionType.setOnClickListener {
            if (mClosePositionType.get() == 0) {
                mClosePositionType.set(1)
            } else {
                mClosePositionType.set(0)
            }
            mClosePositionPrice.set("")
            applyButtonEnable()
        }

        applyButtonEnable()

        mBinding.rgRate.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_rate_25 -> {
                    val num = (maxCount * 0.25).toString().getNum(scale)
                    mClosePositionCount.set(num)
                }
                R.id.rb_rate_50 -> {
                    val num = (maxCount * 0.5).toString().getNum(scale)
                    mClosePositionCount.set(num)
                }
                R.id.rb_rate_75 -> {
                    val num = (maxCount * 0.75).toString().getNum(scale)
                    mClosePositionCount.set(num)
                }
                R.id.rb_rate_100 -> {
                    mClosePositionCount.set(position.getCanClosePosition(tradeUnit.unit))
                }
            }
        }

        mBinding.ivClose.setOnClickListener {
            dismiss()
        }

        mBinding.btnConfirm.setOnClickListener {

            val price = mClosePositionPrice.get()
            val inputCount = mClosePositionCount.get()

            val type = if (mClosePositionType.get() == 0) {
                PositionType.EXECUTE
            } else {
                PositionType.PLAN
            }

            if (type == PositionType.PLAN && TextUtils.isEmpty(price)) {
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(inputCount)) {
                ToastUtils.showShortToast(R.string.mc_sdk_input_close_count)
                return@setOnClickListener
            }

            when (tradeUnit) {
                QuantityUnit.SIZE -> {
                    onConfirm(type, price!!, inputCount.getDouble(), inputCount!!)
                }
                QuantityUnit.USDT -> {
                    position.product?.let {
                        var count = inputCount.getDouble() / maxCount * position.getCanClosePosition(QuantityUnit.SIZE.unit).toDouble()

                        if (count == 0.0) {
                            Toast.makeText(context, R.string.mc_sdk_input_close_count, Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        if (count > 0 && count < 1) {
                            count = 1.0
                        }
                        count = count.toString().getNum(0).getDouble()

                        Logger.e("close count = $count")
                        onConfirm(type, price!!, count, inputCount!!)
                    }
                }
                QuantityUnit.COIN -> {
                    position.product?.let {
                        var count = inputCount.getDouble() / it.mOneLotSize

                        if (count == 0.0) {
                            Toast.makeText(context, R.string.mc_sdk_input_close_count, Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        if (count > 0 && count < 1) {
                            count = 1.0
                        }
                        count = count.toString().getNum(0).getDouble()
                        Logger.e("close count = $count")
                        onConfirm(type, price!!, count, inputCount!!)
                    }
                }
            }

            dismiss()


        }

        mMarketListener = MarketListenerManager.subscribe(
                MarketSubscribeType.TickerSwap(position.product!!.mBase, "usd"),
                MutableLiveData<MarketData>()
        ).apply {
            this.liveData.observeForever {
                val ticker = it as Ticker
                mBinding.tvLastPrice.text = ticker.last
            }
        }

    }

    override fun onDismiss() {
        SoftKeyboardUtils.hideSoftKeyboard(mBinding.etClosePositionCount)
        if (mMarketListener != null) {
            MarketListenerManager.unsubscribe(mMarketListener!!)
        }
        super.onDismiss()
    }

    override fun getImplLayoutId() = R.layout.mc_sdk_dialog_close_position

    private fun applyButtonEnable() {

        if (mClosePositionType.get() != 0 && TextUtils.isEmpty(mClosePositionPrice.get())) {
            mBinding.btnConfirm.isEnabled = false
            return
        }

        if (TextUtils.isEmpty(mClosePositionCount.get())) {
            mBinding.btnConfirm.isEnabled = false
            return
        }

        mBinding.btnConfirm.isEnabled = true
    }

}