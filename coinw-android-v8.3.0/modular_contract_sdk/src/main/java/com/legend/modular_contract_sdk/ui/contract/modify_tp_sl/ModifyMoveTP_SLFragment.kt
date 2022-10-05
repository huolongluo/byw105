package com.legend.modular_contract_sdk.ui.contract.modify_tp_sl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.base.BaseFragment
import com.legend.modular_contract_sdk.component.market_listener.MarketData
import com.legend.modular_contract_sdk.component.market_listener.MarketListenerManager
import com.legend.modular_contract_sdk.component.market_listener.MarketSubscribeType
import com.legend.modular_contract_sdk.component.market_listener.Ticker
import com.legend.modular_contract_sdk.databinding.McSdkFragmentModifyMoveTpSlBinding
import com.legend.modular_contract_sdk.repository.model.PositionAndOrder
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.repository.model.wrap.PositionWrap
import com.legend.modular_contract_sdk.ui.contract.OpenPositionType
import com.legend.modular_contract_sdk.ui.contract.QuantityUnit
import com.legend.modular_contract_sdk.ui.contract.vm.SwapContractViewModel
import com.legend.modular_contract_sdk.utils.*
import com.legend.modular_contract_sdk.utils.inputfilter.DecimalDigitsInputFilter
import com.legend.modular_contract_sdk.widget.dialog.ModifyStopProfitAndLossDialog
import com.orhanobut.logger.Logger

class ModifyMoveTP_SLFragment() : BaseFragment<SwapContractViewModel>() {

    companion object {

        lateinit var mDialog: ModifyStopProfitAndLossDialog

        fun getInstance(position: PositionAndOrder, product: Product, dialog: ModifyStopProfitAndLossDialog): ModifyMoveTP_SLFragment {
            mDialog = dialog
            return ModifyMoveTP_SLFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("position", position)
                    putSerializable("product", product)
                }
            }
        }
    }

    private lateinit var mBinding: McSdkFragmentModifyMoveTpSlBinding

    private val mTriggerPrice = ObservableField<String>("")
    private val mCallbackRate = ObservableField<String>("")
    private val mQuantity = ObservableField<String>("")

    private var mIsUseMarketPrice = ObservableBoolean(false)

    lateinit var mPosition: PositionAndOrder
    lateinit var mProduct: Product

    override fun createViewModel() = ViewModelProvider(requireActivity()).get(SwapContractViewModel::class.java)

    override fun createRootView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = McSdkFragmentModifyMoveTpSlBinding.inflate(inflater, container, false)
        mBinding.triggerPrice = mTriggerPrice
        mBinding.callbackRate = mCallbackRate
        mBinding.quantity = mQuantity
        mBinding.isUseMarketPrice = mIsUseMarketPrice
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            mPosition = it.getSerializable("position") as PositionAndOrder
            mProduct = it.getSerializable("product") as Product
            mBinding.position = PositionWrap(mPosition).apply {
                product = mProduct
            }
        }
        initTradeUnit()
        initView()
        addMarketListener()
    }

    private fun initView() {

        val callbackRateChangeListener = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                mBinding.rgCallbackRate.clearCheck()
            }
        }

        mBinding.rgCallbackRate.setOnCheckedChangeListener { group, checkedId ->
            if (group.findViewById<RadioButton>(checkedId) == null || !group.findViewById<RadioButton>(checkedId).isChecked){
                return@setOnCheckedChangeListener
            }
            mCallbackRate.removeOnPropertyChangedCallback(callbackRateChangeListener)
            when (checkedId) {
                R.id.rb_5_rate -> {
                    // 这里不能直接使用 mCallbackRate.set("") 否则会导致OnPropertyChanged触发多次，而达不到目的
                    mBinding.etCallbackRate.setText("5")
                }
                R.id.rb_10_rate -> {
                    mBinding.etCallbackRate.setText("10")
                }
                R.id.rb_15_rate -> {
                    mBinding.etCallbackRate.setText("15")
                }
                R.id.rb_20_rate -> {
                    mBinding.etCallbackRate.setText("20")
                }
            }
            mCallbackRate.addOnPropertyChangedCallback(callbackRateChangeListener)
        }


        val ClosePositionRateChangeListener = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                mBinding.rgClosePositionRate.clearCheck()
            }
        }

        mBinding.rgClosePositionRate.setOnCheckedChangeListener { group, checkedId ->
            if (group.findViewById<RadioButton>(checkedId) == null || !group.findViewById<RadioButton>(checkedId).isChecked){
                return@setOnCheckedChangeListener
            }
            mQuantity.removeOnPropertyChangedCallback(ClosePositionRateChangeListener)
            when (checkedId) {
                R.id.rb_25_amount -> {
                    setAmount(0.25)
                }
                R.id.rb_50_amount -> {
                    setAmount(0.5)
                }
                R.id.rb_75_amount -> {
                    setAmount(0.75)
                }
                R.id.rb_100_amount -> {
                    setAmount(1.0)
                }
            }
            mQuantity.addOnPropertyChangedCallback(ClosePositionRateChangeListener)
        }


        mBinding.tvMarketPrice.setOnClickListener {
            mIsUseMarketPrice.set(!mIsUseMarketPrice.get())
            mTriggerPrice.set("")
        }

        mBinding.btnConfirm.setOnClickListener {

            var quantity = when (SPUtils.getTradeUnit()) {
                QuantityUnit.SIZE.unit -> {
                    mQuantity.get()
                }
                QuantityUnit.USDT.unit -> {
                    val maxCloseNum = mBinding.position!!.getCanClosePosition(QuantityUnit.USDT.unit).getDouble()
                    (mQuantity.get().getDouble() / maxCloseNum * mPosition.mRemainCurrentPiece).toString().getNum(0)
                }
                QuantityUnit.COIN.unit -> {
                    (mQuantity.get().getDouble() / mProduct.mOneLotSize).toString().getNum(0)
                }
                else -> {
                    mQuantity.get()
                }
            }

            if ((!mIsUseMarketPrice.get()) && mTriggerPrice.get().isNullOrEmpty()) {
                ToastUtils.showShortToast(R.string.mc_sdk_modify_move_tp_sl_tips_input_trigger_price)
                return@setOnClickListener
            }

            if (mCallbackRate.get().isNullOrEmpty()) {
                ToastUtils.showShortToast(R.string.mc_sdk_modify_move_tp_sl_tips_input_callback_rate)
                return@setOnClickListener
            }

            if (mQuantity.get().isNullOrEmpty() || quantity.getDouble() < 0.0) {
                ToastUtils.showShortToast(R.string.mc_sdk_input_close_count)
                return@setOnClickListener
            }

            val lastPrice = mBinding.tvLastPrice.text.toString().getDouble()

            if (lastPrice > 0 && (!mIsUseMarketPrice.get())) {
                val triggerPrice = mTriggerPrice.get().getDouble()

                if (mBinding.position!!.isLong() && triggerPrice <= lastPrice) {
                    ToastUtils.showShortToast(R.string.mc_sdk_modify_move_tp_sl_tips_input_trigger_price_higher_than)
                    return@setOnClickListener
                }

                if ((!mBinding.position!!.isLong()) && triggerPrice >= lastPrice) {
                    ToastUtils.showShortToast(R.string.mc_sdk_modify_move_tp_sl_tips_input_trigger_price_lower_than)
                    return@setOnClickListener
                }
            }


            mViewModel.setMoveTPSL(
                    mPosition.mId,
                    mTriggerPrice.get()!!,
                    (mCallbackRate.get()!!.getDoubleValue() / 100).toString().getNum(4),
                    quantity!!
            )
            mDialog.dismiss()
        }

    }

    private fun setAmount(rate: Double) {

        val scale = when (SPUtils.getTradeUnit()) {
            QuantityUnit.SIZE.unit -> {
                0
            }
            QuantityUnit.USDT.unit -> {
                4
            }
            QuantityUnit.COIN.unit -> {
                mProduct.mOneLotSize.getPrecision()
            }
            else -> {
                0
            }
        }

        mBinding.etAmount.setText((mBinding.position!!.getCanClosePosition(SPUtils.getTradeUnit()).getDouble() * rate).toString().getNum(scale))
    }

    private fun initTradeUnit() {
        // USDT 默认4位小数
        // 按币种输入根据onelotSize小数位数
        mBinding.etPrice.filters = arrayOf(DecimalDigitsInputFilter(6, mProduct.mPricePrecision))
        mBinding.etCallbackRate.filters = arrayOf(DecimalDigitsInputFilter(6, 1))

        when (SPUtils.getTradeUnit()) {
            QuantityUnit.SIZE.unit -> {
                mBinding.tradeUnit = QuantityUnit.SIZE
                mBinding.etAmount.filters = arrayOf(
                        DecimalDigitsInputFilter(6, 0)
                )
            }
            QuantityUnit.USDT.unit -> {
                mBinding.tradeUnit = QuantityUnit.USDT
                mBinding.etAmount.filters = arrayOf(
                        DecimalDigitsInputFilter(6, 4)
                )
            }
            QuantityUnit.COIN.unit -> {
                mBinding.tradeUnit = QuantityUnit.COIN
                mBinding.etAmount.filters = arrayOf(
                        DecimalDigitsInputFilter(6, mProduct.mOneLotSize.getPrecision())
                )
            }
        }

    }

    private fun addMarketListener() {
        mMarketListenerList.add(MarketListenerManager.subscribe(
                MarketSubscribeType.TickerSwap(mProduct.mBase, "usd"),
                MutableLiveData<MarketData>()
        ).apply {
            this.liveData.observeForever {
                val ticker = it as Ticker
                mBinding.tvLastPrice.text = ticker.last
            }
        })
    }


}