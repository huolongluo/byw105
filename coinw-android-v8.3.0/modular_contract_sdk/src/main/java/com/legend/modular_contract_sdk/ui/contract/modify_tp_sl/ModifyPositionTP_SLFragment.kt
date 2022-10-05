package com.legend.modular_contract_sdk.ui.contract.modify_tp_sl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.legend.common.util.ThemeUtil
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.base.BaseFragment
import com.legend.modular_contract_sdk.component.market_listener.MarketData
import com.legend.modular_contract_sdk.component.market_listener.MarketListenerManager
import com.legend.modular_contract_sdk.component.market_listener.MarketSubscribeType
import com.legend.modular_contract_sdk.component.market_listener.Ticker
import com.legend.modular_contract_sdk.databinding.McSdkFragmentModifyPositionTpSlBinding
import com.legend.modular_contract_sdk.repository.model.PositionAndOrder
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.repository.model.wrap.PositionWrap
import com.legend.modular_contract_sdk.ui.contract.vm.SwapContractViewModel
import com.legend.modular_contract_sdk.utils.getDoubleValue
import com.legend.modular_contract_sdk.utils.inputfilter.DecimalDigitsInputFilter
import com.legend.modular_contract_sdk.widget.dialog.ModifyStopProfitAndLossDialog
import com.legend.modular_contract_sdk.widget.dialog.SelectItemDialog

class ModifyPositionTP_SLFragment : BaseFragment<SwapContractViewModel>() {


    companion object {

        lateinit var mDialog : ModifyStopProfitAndLossDialog

        fun getInstance(position: PositionAndOrder, product: Product, dialog: ModifyStopProfitAndLossDialog): ModifyPositionTP_SLFragment{
            mDialog = dialog
            return ModifyPositionTP_SLFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("position", position)
                    putSerializable("product", product)
                }
            }
        }
    }

    lateinit var mBinding: McSdkFragmentModifyPositionTpSlBinding

    lateinit var mPosition : PositionAndOrder
    lateinit var mProduct: Product

    var mTakeProfit = ObservableField<String>("")
    var mStopLoss = ObservableField<String>("")

    override fun createViewModel() = ViewModelProvider(requireActivity()).get(SwapContractViewModel::class.java)

    override fun createRootView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = McSdkFragmentModifyPositionTpSlBinding.inflate(inflater, container, false)
        mBinding.takeProfit = mTakeProfit
        mBinding.stopLoss = mStopLoss

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            mPosition = it.getSerializable("position") as PositionAndOrder
            mProduct = it.getSerializable("product") as Product
            mBinding.position = PositionWrap(mPosition)
        }
        initView()
        addMarketListener()
    }


    private fun initView() {
        mTakeProfit.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                mBinding.tvEstimateProfit.text =
                        mBinding.position!!.getTakeProfitAmount(mTakeProfit.get() ?: "").apply {
                            if (getDoubleValue() > 0) {
                                mBinding.tvEstimateProfit.setTextColor(ThemeUtil.getThemeColor(requireContext(), R.attr.up_color))
                            } else if (getDoubleValue() < 0) {
                                mBinding.tvEstimateProfit.setTextColor(ThemeUtil.getThemeColor(requireContext(), R.attr.drop_color))
                            } else {
                                mBinding.tvEstimateProfit.setTextColor(ThemeUtil.getThemeColor(requireContext(), R.attr.col_text_title))
                            }
                        }
            }
        })

        mStopLoss.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                mBinding.tvEstimateLoss.text =
                        mBinding.position!!.getStopLossAmount(mStopLoss.get() ?: "").apply {
                            if (getDoubleValue() > 0) {
                                mBinding.tvEstimateLoss.setTextColor(ThemeUtil.getThemeColor(requireContext(), R.attr.up_color))
                            } else if (getDoubleValue() < 0) {
                                mBinding.tvEstimateLoss.setTextColor(ThemeUtil.getThemeColor(requireContext(), R.attr.drop_color))
                            } else {
                                mBinding.tvEstimateLoss.setTextColor(ThemeUtil.getThemeColor(requireContext(), R.attr.col_text_title))
                            }
                        }
            }
        })

        if (mPosition.mStopProfitPrice > 0) {
            mTakeProfit.set(mPosition.mStopProfitPrice.toString())
        } else {
            mTakeProfit.set("")
        }

        if (mPosition.mStopLossPrice > 0) {
            mStopLoss.set(mPosition.mStopLossPrice.toString())
        } else {
            mStopLoss.set("")
        }

        mBinding.etStopProfitPrice.filters = arrayOf(
                DecimalDigitsInputFilter(6, mProduct.mPricePrecision)
        )

        mBinding.etStopLossPrice.filters = arrayOf(
                DecimalDigitsInputFilter(6, mProduct.mPricePrecision)
        )

        mBinding.btnConfirm.setOnClickListener {
            val positionWrap: PositionWrap = mBinding.position!!
            mViewModel.modifyPositionStopProfitAndLoss(positionWrap, mTakeProfit.get()!!, mStopLoss.get()!!, positionWrap.position.mIsExperienceGold)
            mDialog.dismiss()
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