package com.legend.modular_contract_sdk.ui.contract

import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.legend.common.event.ContractGoldEvent
import com.legend.common.util.ThemeUtil.getThemeDrawable
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.api.ModularContractSDK
import com.legend.modular_contract_sdk.api.ModularContractSDK.userIsLogin
import com.legend.modular_contract_sdk.base.BaseFragment
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils
import com.legend.modular_contract_sdk.common.*
import com.legend.modular_contract_sdk.common.event.*
import com.legend.modular_contract_sdk.component.market_listener.*
import com.legend.modular_contract_sdk.databinding.McSdkFragmentContractBinding
import com.legend.modular_contract_sdk.repository.model.ContractAssetInfo
import com.legend.modular_contract_sdk.repository.model.PositionAndOrder
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.repository.model.enum.McContractModeEnum
import com.legend.modular_contract_sdk.repository.model.wrap.ExperienceGoldWrap
import com.legend.modular_contract_sdk.ui.chart.McKLineActivity
import com.legend.modular_contract_sdk.ui.contract.history.McHistoryOrderActivity
import com.legend.modular_contract_sdk.ui.contract.vm.SwapContractViewModel
import com.legend.modular_contract_sdk.ui.experience_gold.ExperienceGoldActivity
import com.legend.modular_contract_sdk.ui.experience_gold.ExperienceGoldViewModel
import com.legend.modular_contract_sdk.utils.*
import com.legend.modular_contract_sdk.utils.inputfilter.DecimalDigitsInputFilter
import com.legend.modular_contract_sdk.utils.inputfilter.MaxInputValidator
import com.legend.modular_contract_sdk.widget.CustomListPopupView
import com.legend.modular_contract_sdk.widget.depthview.DepthFutureViewDelegate
import com.lxj.xpopup.XPopup
import com.orhanobut.logger.Logger
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.math.RoundingMode
import kotlin.math.max

/**
 * 合约界面
 */
class SwapContractFragment : BaseFragment<SwapContractViewModel>() {
    lateinit var mBinding: McSdkFragmentContractBinding
    lateinit var mDepthDelegate: DepthFutureViewDelegate
    lateinit var mPositionFragment: PositionsFragment
    lateinit var mOpenOrderFragment: OpenOrderFragment
    lateinit var mOpenPlanOrderFragment: OpenPlanOrderFragment
    private var mProduct: Product? = null
    private var mIsVisibleToUser = false
    private var mPositionMode: PositionMode? = null
    private var mIsUseExperienceGold = ObservableField<Boolean>(false)
    private var mTickerIsUp = ObservableField<Boolean>(false)

    private var mIgnoreThisProgressChange = false

    companion object {
        fun getInstance(): SwapContractFragment = SwapContractFragment()
        private const val TAG = "SwapContractFragment"
    }

    override fun createRootView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        val rootView = inflater.inflate(R.layout.mc_sdk_fragment_contract, container, false)
        mBinding = McSdkFragmentContractBinding.bind(rootView)
        mBinding.isUseExperienceGold = mIsUseExperienceGold
        mBinding.isTickerUp = mTickerIsUp
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDepthView()
        initTabViews()
        initSeekBar()
        initViewListeners()
        initLiveData()

        getDatas()
    }

    private fun initSeekBar() {
        val seekBarColorDrawable = getThemeDrawable(requireContext(), R.attr.bg_seek_bar_buy)
        val drawables = arrayOfNulls<Drawable>(2)
        drawables[0] = resources.getDrawable(R.drawable.bg_seek_bar)
        drawables[1] = ClipDrawable(seekBarColorDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL)

        val newLayerDrawable = LayerDrawable(drawables)
        newLayerDrawable.setId(0, android.R.id.background)
        newLayerDrawable.setId(1, android.R.id.progress)

        mBinding.layoutProgress.thumbOffset = 0

        mBinding.layoutProgress.progressDrawable = newLayerDrawable

        mBinding.layoutProgress.thumb = getThemeDrawable(requireContext(), R.attr.ic_seek_thumb_buy)

    }

    override fun createViewModel() = ViewModelProvider(requireActivity()).get(SwapContractViewModel::class.java)

    override fun leftMenuIsBack() = false
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        Logger.t(Companion.TAG).d("setUserVisibleHint isVisibleToUser:$isVisibleToUser")
        mIsVisibleToUser = isVisibleToUser
        if (isVisibleToUser) {
            if (mRootView == null) return
            addSocketListener()
            if (mProduct == null) {
                getDatas()
            }
            mViewModel.fetchContractAssetsInfo()
        } else {
            removeAllMarketListener()
        }
    }

    override fun onResume() {
        super.onResume()
        Logger.t(Companion.TAG).d("onResume mIsVisibleToUser:$mIsVisibleToUser")

        if(!ModularContractSDK.wsHost.isNullOrEmpty()){
            MarketListenerManager.checkConnection()
        }

        if (mIsVisibleToUser) {
            addSocketListener()
            if (mProduct == null) {
                getDatas()
            }
            mViewModel.fetchContractAssetsInfo()
        }
    }

    override fun onPause() {
        super.onPause()
        Logger.t(Companion.TAG).d("onPause")
        removeAllMarketListener()
    }

    private fun getDatas() {
        mViewModel.fetchWsHost()
        mViewModel.fetchAllProduct()
        mViewModel.fetchUserTreadConfig()
        mViewModel.startFundingFeeCountdown()
    }

    private fun initLiveData() {

        mDepthDelegate.setMaxSize(5)

        mViewModel.mWsHostLiveData.observe(viewLifecycleOwner, Observer {

            ModularContractSDK.wsHost = it + "/custom"
            Logger.e("wsHost = ${ModularContractSDK.wsHost}")
            MarketListenerManager.start()
        })

        mViewModel.mUserTreadConfigLiveData.observe(viewLifecycleOwner, Observer {

            if (UserConfigStorage.getLeverage(-1) < 0) {
                UserConfigStorage.setLeverage(it.mSwapLeverage)
            }

            if (it.mActual == PositionMode.FULL.mode) {
                mViewModel.mPositionModeLiveData.postValue(PositionMode.FULL)
            } else {
                mViewModel.mPositionModeLiveData.postValue(PositionMode.PART)
            }

            if (it.mLayout == PositionMergeMode.MERGE.mode){
                mViewModel.mPositionMergeModeLiveData.postValue(PositionMergeMode.MERGE)
            } else {
                mViewModel.mPositionMergeModeLiveData.postValue(PositionMergeMode.PARTITION)
            }
        })

        mViewModel.mProductsLiveData.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                val lastSelectedProduct = SPUtils.getLastSelectedProduct()
                lastSelectedProduct?.let {lastSelectedProduct ->
                    // 找到最后一次使用的交易对，继续使用
                    for (product in it) {
                        if (product.mId == lastSelectedProduct.mId){
                            mViewModel.updateProduct(product)
                            return@let
                        }
                    }
                    // 如果最后一次使用的交易对没在交易对列表里则使用第一个交易对
                    mViewModel.updateProduct(it[0])
                }?:mViewModel.updateProduct(it[0])

                CoinwHyUtils.products = it as ArrayList<Product>?
                for (i in it.indices) {
                    CoinwHyUtils.productsMap[it[i].mBase.toUpperCase()] = it[i].mPricePrecision
                }
            }
        })

        mViewModel.mDepthLiveData.observe(viewLifecycleOwner, Observer {
            val depth = it as Depth
            mDepthDelegate.setData(depth)

            mViewModel.calcMaxCanOpenSize()
            mViewModel.calcOccupyMargin()

            if (mViewModel.mPositionTypeLiveData.value == PositionType.EXECUTE) {
                mViewModel.mQuantityUnitLiveData.value?.let { unit ->
                    calcTradeUnitConvert(unit, mViewModel.mQuantityLiveData.value.toString())
                }
            }

        })

        mViewModel.mIndexPriceLiveData.observe(viewLifecycleOwner, Observer {
            val price = it as Price
            mDepthDelegate.setLastIndex(price.p, mViewModel.mProductLiveData.value?.mPricePrecision
                    ?: 2)
        })

        mViewModel.mMarketPriceLiveData.observe(viewLifecycleOwner, Observer {
            val price = it as Price
            mBinding.tvMarkPrice.text = price.p.getNum(mViewModel.mProductLiveData.value?.mPricePrecision
                    ?: 2)
        })

        mViewModel.mTickerLiveData.observe(viewLifecycleOwner, Observer {
            val ticker = it as Ticker
            mDepthDelegate.setLast(ticker.last, mViewModel.mProductLiveData.value?.mPricePrecision
                    ?: 2, ticker.changeRate)

            val changeDouble = ticker.changeRate.getDouble()
            val change = (changeDouble * 100.0).toString()

            mBinding.tvChange.text = change.getNum(2, withSymbol = true) + "%"

            mTickerIsUp.set(changeDouble >= 0)

            if (ticker.last.isNotEmpty() && !(mViewModel.mInitLiveData.value as Boolean)) {
                mViewModel.updatePrice(ticker.last)
                mViewModel.mInitLiveData.postValue(true)
            }
        })

        mViewModel.mPositionLiveData.observe(viewLifecycleOwner, Observer {
            val positionAndOrder = it as PositionAndOrder
            Logger.e("position = ${positionAndOrder}")
        })

        mViewModel.mPositionChangeLiveData.observe(viewLifecycleOwner, Observer {
            val positionAndOrder = it as PositionAndOrder
            Logger.e("position change= ${positionAndOrder}")
        })

        mViewModel.mPositionFinishLiveData.observe(viewLifecycleOwner, Observer {
            EventBus.getDefault().post(McPositionFinishEvent())
        })

        mViewModel.mAllPositionLiveData.observe(viewLifecycleOwner, Observer {
            val positionAndOrder = it as PositionAndOrder
            Logger.e("all position= ${positionAndOrder}")
            mViewModel.fetchContractAssetsInfo()
            EventBus.getDefault().post(McRefreshOrderList())
        })

        mViewModel.mFundingRateLiveData.observe(viewLifecycleOwner, Observer {
            val fundingRate = it as FundingRate
            mBinding.viewFundingRate.text = "${
                NumberStringUtil.formatAmount(
                        (if (TextUtils.isEmpty(fundingRate.r)) "0" else fundingRate.r).toDouble() * 100,
                        4,
                        NumberStringUtil.AmountStyle.FillZeroNoComma,
                        RoundingMode.HALF_UP
                )
            }%"
        })

        mViewModel.mContractAssetInfoLiveData.observe(viewLifecycleOwner, Observer {
            updateAssetViews(it, mViewModel.mTickersLiveData.value)
//            EventBus.getDefault().post(PositionsEvent(it.mUserPositions))
            // 用户资产有变化刷新最大可开张数
            mViewModel.calcMaxCanOpenSize()
        })

        mViewModel.mHoldPositionsLiveData.observe(viewLifecycleOwner, Observer {
            mPositionFragment.setPositions(it)
        })

        mViewModel.mTickersLiveData.observe(viewLifecycleOwner, Observer {
            updateAssetViews(mViewModel.mContractAssetInfoLiveData.value, it)
        })

        mViewModel.mOrderPlaceResultLiveData.observe(viewLifecycleOwner, Observer {
            mViewModel.resetInputLiveData()
            mBinding.layoutProgress.progress = 0
            // 清除体验金数据
            mIsUseExperienceGold.set(false)
            mBinding.tvExperienceGold.setText(R.string.mc_sdk_contract_experience_gold)
            mViewModel.mUseExperienceGold.value = null
        })



        mViewModel.mProductLiveData.observe(viewLifecycleOwner, Observer {
            mBinding.layoutProgress.progress = 0

            mProduct = it

            if (mIsUseExperienceGold.get()!! && mViewModel.mUseExperienceGold.value != null) {
                if (mViewModel.mUseExperienceGold.value!!.mCurrencyName.isNotEmpty() && !mProduct!!.mBase.equals(mViewModel.mUseExperienceGold.value!!.mCurrencyName, ignoreCase = true)) {
                    mIsUseExperienceGold.set(false)
                    mViewModel.mUseExperienceGold.value = null
                }
            }

            addSocketListener()
            McConstants.COMMON.CURRENT_PAIR_PRECISION = it.mPricePrecision
            McConstants.COMMON.CURRENT_PRODUCT = it

            mViewModel.fetchContractAssetsInfo()

            mBinding.tvTitle1.text = it.mBase.toUpperCase() + "/" + it.mQuote.toUpperCase()

            mDepthDelegate.setLotSize(it.mOneLotSize, 0, it.mPricePrecision)
            updateEditorFilter()
            if (mProduct != null && mProduct!!.mMaxLeverage < UserConfigStorage.getLeverage()) {
                UserConfigStorage.setLeverage(mProduct!!.mMaxLeverage)
                if (mPositionMode != null) {
                    mViewModel.mPositionModeLiveData.postValue(mPositionMode)
                }
            }
        })
        mViewModel.mPositionTypeLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                PositionType.PLAN -> {
                    mBinding.layoutLimitPrice.visibility = View.VISIBLE
                    mBinding.layoutMarketPrice.visibility = View.GONE
                    mBinding.layoutPlanOrder.visibility = View.GONE
                    mBinding.layoutTriggerSetTitle.visibility = View.VISIBLE
                    mBinding.viewPositionType.text = getString(R.string.mc_sdk_contract_limit_order)
                }
                PositionType.EXECUTE -> {
                    mBinding.layoutLimitPrice.visibility = View.GONE
                    mBinding.layoutMarketPrice.visibility = View.VISIBLE
                    mBinding.layoutPlanOrder.visibility = View.GONE
                    mBinding.layoutTriggerSetTitle.visibility = View.VISIBLE
                    mBinding.viewPositionType.text =
                            getString(R.string.mc_sdk_contract_market_order)
                    mBinding.viewPrice.setText("")
                }
                PositionType.PLAN_TRIGGER -> {
                    mBinding.layoutLimitPrice.visibility = View.GONE
                    mBinding.layoutMarketPrice.visibility = View.GONE
                    mBinding.layoutPlanOrder.visibility = View.VISIBLE
                    mBinding.layoutTriggerSetTitle.visibility = View.GONE
                    mBinding.viewPositionType.text = getString(R.string.mc_sdk_contract_plan_order)
                    mViewModel.updateTargetLayoutVisible(false)
                }
            }
            mViewModel.resetInputLiveData()
            mViewModel.calcOccupyMargin()
            mViewModel.mQuantityUnitLiveData.value?.let { unit ->
                calcTradeUnitConvert(unit, mViewModel.mQuantityLiveData.value.toString())
            }
        })
        mViewModel.mPositionModeLiveData.observe(viewLifecycleOwner, Observer {
            mPositionMode = it
            when (it) {
                PositionMode.FULL -> {
                    mBinding.viewLeverage.text =
                            "${getString(R.string.mc_sdk_contract_order_type_full)} X ${UserConfigStorage.getLeverage()}"
                    McConstants.COMMON.CURRENT_WAREHOUSE = McContractModeEnum.WHOLE_WAREHOUSE.requestValue
                    mBinding.layoutAssetFull.visibility = View.VISIBLE
                    mBinding.layoutAssetPart.visibility = View.GONE
                }
                PositionMode.PART -> {
                    mBinding.viewLeverage.text =
                            "${getString(R.string.mc_sdk_contract_order_type_part)} X ${UserConfigStorage.getLeverage()}"
                    McConstants.COMMON.CURRENT_WAREHOUSE = McContractModeEnum.WAREHOUSE.requestValue
                    mBinding.layoutAssetFull.visibility = View.GONE
                    mBinding.layoutAssetPart.visibility = View.VISIBLE
                }
            }
            // 仓位模式变化，杠杆倍数变化 重新计算最大可开
            mViewModel.calcMaxCanOpenSize()
            mViewModel.calcOccupyMargin()
        })
        mViewModel.mLeverageLiveData.observe(viewLifecycleOwner, Observer {

        })
        mViewModel.mPriceLiveData.observe(viewLifecycleOwner, Observer {
            if (it != mBinding.viewPrice.text.toString()) {
                mBinding.viewPrice.setText(it)
                mBinding.viewPrice.postDelayed({
                    try {
                        // fix umeng 错误统计
                        // https://apm.umeng.com/platform/5d385057570df3ea9d000253/error_analysis/crash/detail/5554443305083?errorId=5554443305083
                        mBinding.viewPrice.setSelection(it.length)
                    } catch (e : IndexOutOfBoundsException){
                        e.printStackTrace()
                    }

                },100)
            }

            mViewModel.calcMaxCanOpenSize()
            mViewModel.calcOccupyMargin()
            mViewModel.mQuantityUnitLiveData.value?.let {unit ->
                calcTradeUnitConvert(unit, mViewModel.mQuantityLiveData.value.toString())
            }
        })
        mViewModel.mTriggerPriceLiveData.observe(viewLifecycleOwner, Observer {
            if (it != mBinding.viewTriggerPrice.text.toString()) {
                mBinding.viewTriggerPrice.setText(it)
            }

            mViewModel.calcMaxCanOpenSize()
            mViewModel.mQuantityUnitLiveData.value?.let { unit ->
                calcTradeUnitConvert(unit, mViewModel.mQuantityLiveData.value.toString())
            }
        })
        mViewModel.mExecutePriceLiveData.observe(viewLifecycleOwner, Observer {
            if (it != mBinding.viewTriggerExecutePrice.text.toString()) {
                mBinding.viewTriggerExecutePrice.setText(it)
            }

            mViewModel.calcMaxCanOpenSize()
            mViewModel.calcOccupyMargin()
            mViewModel.mQuantityUnitLiveData.value?.let {unit ->
                calcTradeUnitConvert(unit, mViewModel.mQuantityLiveData.value.toString())
            }
        })
        mViewModel.mExecuteTypeLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                TriggerType.PLAN -> {
                    mBinding.viewTriggerExecutePrice.isEnabled = true
                    mBinding.viewTriggerExecutePrice.hint =
                            getString(R.string.mc_sdk_contract_execute_price)
                    mBinding.viewTriggerPriceType.text =
                            getString(R.string.mc_sdk_contract_market_price)
                }
                TriggerType.EXECUTE -> {
                    mBinding.viewTriggerExecutePrice.isEnabled = false
                    mBinding.viewTriggerExecutePrice.setText("")
                    mBinding.viewTriggerExecutePrice.hint =
                            getString(R.string.mc_sdk_contract_market_price)
                    mBinding.viewTriggerPriceType.text =
                            getString(R.string.mc_sdk_contract_limit_price)
                }
            }
        })

        mViewModel.mQuantityUnitLiveData.observe(viewLifecycleOwner, Observer {
            mBinding.viewQuantity.setText("")
            mBinding.layoutProgress.progress = 0
            updateEditorFilter()
            resetTradeUnitConvert(it)
            val unit = when(it){
                QuantityUnit.SIZE -> {
                    getString(R.string.mc_sdk_contract_unit)
                }
                QuantityUnit.USDT -> {
                    getString(R.string.mc_sdk_usdt)
                }
                QuantityUnit.COIN -> {
                    mViewModel.mProductLiveData.value?.mBase?.toUpperCase()
                }
            }
            mBinding.viewCanOpenLongUnit.setText(getString(R.string.mc_sdk_contract_max_open_long, unit))
            mBinding.viewCanOpenShortUnit.setText(getString(R.string.mc_sdk_contract_max_open_short, unit))
        })

        mViewModel.mQuantityLiveData.observe(viewLifecycleOwner, Observer {
            if (it != mBinding.viewQuantity.text.toString()) {
                if (it.isEmpty()) {
                    mBinding.viewQuantity.setText("")
                    resetTradeUnitConvert(mViewModel.mQuantityUnitLiveData.value!!)
                } else {

                    when(mViewModel.mQuantityUnitLiveData.value){
                        QuantityUnit.SIZE ->{
                            mBinding.viewQuantity.setText(
                                    NumberStringUtil.formatAmount(
                                            it,
                                            0,
                                            NumberStringUtil.AmountStyle.FillZeroNoComma
                                    )
                            )
                        }
                        QuantityUnit.USDT ->{
                            mBinding.viewQuantity.setText(
                                    NumberStringUtil.formatAmount(
                                            it,
                                            4,
                                            NumberStringUtil.AmountStyle.FillZeroNoComma
                                    )
                            )
                        }
                        QuantityUnit.COIN ->{
                            mBinding.viewQuantity.setText(
                                    NumberStringUtil.formatAmount(
                                            it,
                                            mViewModel.mProductLiveData.value?.mOneLotSize?.getPrecision() ?: 3,
                                            NumberStringUtil.AmountStyle.FillZeroNoComma
                                    )
                            )

                        }
                    }

                }

            }
            mViewModel.calcOccupyMargin()
            mViewModel.mQuantityUnitLiveData.value?.let {unit ->
                calcTradeUnitConvert(unit, it)
            }

            val quantitySize = it.getDouble()

            mIgnoreThisProgressChange = true
            val progress = ((quantitySize / (mViewModel.mMaxCanOpenLongSizeData.value ?: 0.0)) * mBinding.layoutProgress.max).toInt()
            mBinding.layoutProgress.progress = if (progress > mBinding.layoutProgress.max){
                mBinding.layoutProgress.max
            } else {
                progress
            }
            mIgnoreThisProgressChange = false
        })
        mViewModel.mTargetTriggerLayoutVisibleLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                mBinding.layoutTriggerSet.visibility = View.VISIBLE
                mDepthDelegate.setMaxSize(7)
            } else {
                mBinding.layoutTriggerSet.visibility = View.GONE
                mDepthDelegate.setMaxSize(6)
            }
        })
        mViewModel.mStopProfitTypeLiveData.observe(viewLifecycleOwner, Observer {
            mBinding.viewStopProfit.setText("")
            updateEditorFilter()
            when (it) {
                TriggerSetType.PRICE -> {
                    mBinding.viewProfitSetType.text =
                            getString(R.string.mc_sdk_contract_target_set_by_percent)
                    mBinding.viewProfitTriggerUnit.setText(R.string.mc_sdk_usdt)
                    mBinding.viewStopProfit.hint = getText(R.string.mc_sdk_hint_take_profit_price)
                }
                TriggerSetType.PERCENT -> {
                    mBinding.viewProfitSetType.text =
                            getString(R.string.mc_sdk_contract_target_set_by_price)
                    mBinding.viewProfitTriggerUnit.text = "%"
                    mBinding.viewStopProfit.hint = getText(R.string.mc_sdk_hint_take_profit_rate)
                }
                else -> {

                }
            }
        })
        mViewModel.mStopLossTypeLiveData.observe(viewLifecycleOwner, Observer {
            mBinding.viewStopLoss.setText("")
            updateEditorFilter()
            when (it) {
                TriggerSetType.PRICE -> {
                    mBinding.viewLossSetType.text =
                            getString(R.string.mc_sdk_contract_target_set_by_percent)
                    mBinding.viewLossTriggerUnit.setText(R.string.mc_sdk_usdt)
                    mBinding.viewStopLoss.hint = getText(R.string.mc_sdk_hint_stop_loss_price)
                }
                TriggerSetType.PERCENT -> {
                    mBinding.viewLossSetType.text =
                            getString(R.string.mc_sdk_contract_target_set_by_price)
                    mBinding.viewLossTriggerUnit.text = "%"
                    mBinding.viewStopLoss.hint = getText(R.string.mc_sdk_hint_stop_loss_rate)
                }
                else -> {

                }
            }
        })
        mViewModel.mStopProfitRateLiveData.observe(viewLifecycleOwner, Observer {
            if (mViewModel.mStopProfitTypeLiveData.value == TriggerSetType.PERCENT) {
                if (it != mBinding.viewStopProfit.text.toString()) {
                    mBinding.viewStopProfit.setText(it)
                }
            }
        })
        mViewModel.mStopProfitPriceLiveData.observe(viewLifecycleOwner, Observer {
            if (mViewModel.mStopProfitTypeLiveData.value == TriggerSetType.PRICE) {
                if (it != mBinding.viewStopProfit.text.toString()) {
                    mBinding.viewStopProfit.setText(it)
                }
            }
        })
        mViewModel.mStopLossRateLiveData.observe(viewLifecycleOwner, Observer {
            if (mViewModel.mStopLossTypeLiveData.value == TriggerSetType.PERCENT) {
                if (it != mBinding.viewStopLoss.text.toString()) {
                    mBinding.viewStopLoss.setText(it)
                }
            }
        })
        mViewModel.mStopLossPriceLiveData.observe(viewLifecycleOwner, Observer {
            if (mViewModel.mStopLossTypeLiveData.value == TriggerSetType.PRICE) {
                if (it != mBinding.viewStopLoss.text.toString()) {
                    mBinding.viewStopLoss.setText(it)
                }
            }
        })
        mViewModel.mLongMarginLiveData.observe(viewLifecycleOwner, Observer {
            mBinding.tvLongMargin.text = it + " " + McConstants.COMMON.PAIR_RIGHT_NAME
        })
        mViewModel.mShortMarginLiveData.observe(viewLifecycleOwner, Observer {
            mBinding.tvShortMargin.text = it + " " + McConstants.COMMON.PAIR_RIGHT_NAME
        })

        mViewModel.mMaxCanOpenLongSizeData.observe(viewLifecycleOwner, Observer {canOpenLong ->
            mViewModel.mQuantityUnitLiveData.value?.let {
                val scale = when (it) {
                    QuantityUnit.SIZE -> {
                        0
                    }
                    QuantityUnit.USDT -> {
                        4
                    }
                    QuantityUnit.COIN -> {
                        (mViewModel.mProductLiveData.value?.mOneLotSize ?: 0.1).getPrecision()
                    }
                }

                mBinding.viewCanOpenLong.text = canOpenLong.toString().getNum(scale, withZero = true)
            }
        })
        mViewModel.mMaxCanOpenShortSizeData.observe(viewLifecycleOwner, Observer {canOpenShort ->
             mViewModel.mQuantityUnitLiveData.value?.let {
                 val scale = when (it) {
                     QuantityUnit.SIZE -> {
                         0
                     }
                     QuantityUnit.USDT -> {
                         4
                     }
                     QuantityUnit.COIN -> {
                         (mViewModel.mProductLiveData.value?.mOneLotSize ?: 0.1).getPrecision()
                     }
                 }

                 mBinding.viewCanOpenShort.text = canOpenShort.toString().getNum(scale, withZero = true)
             }
        })

        mViewModel.mUseExperienceGold.observe(viewLifecycleOwner, Observer { experienceGold ->
            if (experienceGold != null) {
                mBinding.tvExperienceGold.text = ExperienceGoldWrap(experienceGold).getDesc(requireContext())
            }
            mViewModel.calcMaxCanOpenSize()
        })

        mViewModel.mCoinExperienceGold.observe(viewLifecycleOwner, Observer { gold ->

            context?.let {
                showSelectExperienceGoldDialog(it, gold.second, mViewModel.mUseExperienceGold.value) { isUse, experienceGold ->
                    mIsUseExperienceGold.set(isUse)
                    if (isUse) {
                        mViewModel.mUseExperienceGold.value = experienceGold!!.experienceGold
                        mBinding.tvExperienceGold.text = experienceGold.getDesc(requireContext())
                    } else {
                        mViewModel.mUseExperienceGold.value = null
                        mBinding.tvExperienceGold.setText(R.string.mc_sdk_contract_experience_gold)
                    }

                }
            }
        })

        mViewModel.mFundingFeeCountdownLiveData.observe(viewLifecycleOwner, Observer {
            mBinding.tvFundingRateCountdown.text = it
        })

    }

    private fun addSocketListener() {
        removeAllMarketListener()
        // 订阅
        addPositionListener()
        if (mProduct == null) return
        addTradeListener(mProduct!!)
    }

    private fun updateEditorFilter() {
        mBinding.viewPrice.filters = arrayOf(
                DecimalDigitsInputFilter(6, mViewModel.mProductLiveData.value?.mPricePrecision ?: 4)
        )
        mBinding.viewTriggerPrice.filters = arrayOf(
                DecimalDigitsInputFilter(6, mViewModel.mProductLiveData.value?.mPricePrecision ?: 4)
        )
        mBinding.viewTriggerExecutePrice.filters = arrayOf(
                DecimalDigitsInputFilter(6, mViewModel.mProductLiveData.value?.mPricePrecision ?: 4)
        )
        when (mViewModel.mQuantityUnitLiveData.value) {
            QuantityUnit.SIZE -> {
                mBinding.viewQuantity.filters = arrayOf(
                        DecimalDigitsInputFilter(6, 0)
                )
            }
            QuantityUnit.USDT -> {
                mBinding.viewQuantity.filters = arrayOf(
                        DecimalDigitsInputFilter(6, 4)
                )
            }
            QuantityUnit.COIN -> {
                mBinding.viewQuantity.filters = arrayOf(
                        DecimalDigitsInputFilter(6, mViewModel.mProductLiveData.value?.mOneLotSize?.getPrecision() ?: 4)
                )
            }
        }

//        逐仓下：
//        止盈率最多1000%，小数两位；止盈价可输入正数，整数部分6位，小数部分为币种精度
//        止损率最多90%，小数两位；止损价可输入正数，整数部分6位，小数部分为币种精度
//
//        全仓下：
//        止盈率最多1000%，小数两位；止盈价可输入正数，整数部分6位，小数部分为币种精度
//        止损率6位的整数，小数两位， 止损价可输入正数，整数部分6位，小数部分为币种精度
        when (mViewModel.mStopProfitTypeLiveData.value) {
            TriggerSetType.PRICE -> {
                mBinding.viewStopProfit.filters = arrayOf(
                        DecimalDigitsInputFilter(6, mViewModel.mProductLiveData.value?.mPricePrecision
                                ?: 4)
                )
            }
            TriggerSetType.PERCENT -> {
                mBinding.viewStopProfit.filters = arrayOf(
                        MaxInputValidator(1000),
                        DecimalDigitsInputFilter(4, 2)
                )
            }
        }

        when (mViewModel.mStopLossTypeLiveData.value) {
            TriggerSetType.PRICE -> {
                mBinding.viewStopLoss.filters = arrayOf(
                        DecimalDigitsInputFilter(6, mViewModel.mProductLiveData.value?.mPricePrecision
                                ?: 4)
                )
            }
            TriggerSetType.PERCENT -> {
                if (mViewModel.mPositionModeLiveData.value == PositionMode.FULL) {
                    mBinding.viewStopLoss.filters = arrayOf(
                            DecimalDigitsInputFilter(6, 2)
                    )
                } else {
                    mBinding.viewStopLoss.filters = arrayOf(
                            MaxInputValidator(90),
                            DecimalDigitsInputFilter(2, 2)
                    )
                }
            }
        }

    }

    private fun resetTradeUnitConvert(unit: QuantityUnit){
        when (unit) {
            QuantityUnit.SIZE -> {
                mBinding.viewQuantityUnit.text = getString(R.string.mc_sdk_trade_unit_convert, getString(R.string.mc_sdk_contract_unit), "0" + getString(R.string.mc_sdk_usdt))
            }
            QuantityUnit.USDT -> {
                //  开仓单位为USDT 时 折合为币
                mViewModel.mProductLiveData.value?.apply {
                    mBinding.viewQuantityUnit.text = getString(R.string.mc_sdk_trade_unit_convert, getString(R.string.mc_sdk_usdt), "0${mBase.toUpperCase()}")
                }
            }
            QuantityUnit.COIN -> {
                mViewModel.mProductLiveData.value?.apply {
                    mBinding.viewQuantityUnit.text = getString(R.string.mc_sdk_trade_unit_convert, mBase.toUpperCase(), "0" + getString(R.string.mc_sdk_usdt))
                }

            }
        }
    }

    private fun calcTradeUnitConvert(unit: QuantityUnit, amount:String){
        when(unit){
            QuantityUnit.SIZE ->{

//                单位为张 ：数量 * oneLotSize * 价格   ≈ XX USDT
//                单位为币： 数量*价格 ≈ XX USDT
//                单位为U：数量/价格 ≈ XX 币
//                开仓单位为张时 折合为USDT
                mViewModel.mProductLiveData.value?.apply {

                    val usdtAmount = (amount.getDouble() * mOneLotSize * mViewModel.getPriceForCalc().getDouble()).toString().getNum(4, withZero = true)

                    mBinding.viewQuantityUnit.text = getString(R.string.mc_sdk_trade_unit_convert, getString(R.string.mc_sdk_contract_unit), usdtAmount + getString(R.string.mc_sdk_usdt))
                }

            }
            QuantityUnit.USDT ->{
                //  开仓单位为USDT 时 折合为币
                mViewModel.mProductLiveData.value?.apply {

                    val coinAmount = (amount.getDouble() / mViewModel.getPriceForCalc().getDouble()).toString().getNum(mOneLotSize.getPrecision(),withZero = true)

                    mBinding.viewQuantityUnit.text = getString(R.string.mc_sdk_trade_unit_convert, getString(R.string.mc_sdk_usdt), coinAmount + mBase.toUpperCase())
                }

            }
            QuantityUnit.COIN ->{
                //  开仓单位为币时 折合为USDT
                mViewModel.mProductLiveData.value?.apply {

                    val usdtAmount = (amount.getDouble() * mViewModel.getPriceForCalc().getDouble()).toString().getNum(4, withZero = true)

                    mBinding.viewQuantityUnit.text = getString(R.string.mc_sdk_trade_unit_convert, mBase.toUpperCase(), usdtAmount + getString(R.string.mc_sdk_usdt))
                }
            }
        }
    }

    private fun updateAssetViews(
            assetInfo: ContractAssetInfo?,
            tickerMap: MutableMap<String, MarketData>?
    ) {

        if (assetInfo == null || tickerMap == null) {
            mBinding.viewAsset.text = "- -"
            mBinding.viewAssetRisk.text = "- -"
            mBinding.tvAssetAvailableMargin.text = "- -"
            mBinding.viewUnrealized.text = "- -"

            mBinding.viewPositionMargin.text = "- -"
            mBinding.tvAssetAvailableMargin.text = "- -"
            mBinding.viewUnrealizedPart.text = "- -"
            return
        }
        if (mViewModel.mPositionModeLiveData.value == PositionMode.FULL) {
            mBinding.viewAsset.text = NumberStringUtil.formatAmount(
                    CalculateUtil.getNetValue(assetInfo, tickerMap), 2,
                    NumberStringUtil.AmountStyle.FillZeroNoComma
            )
            val risk = CalculateUtil.getRiskRate(assetInfo, tickerMap)
            mViewModel.mFullModeRiskSizeData.postValue(risk)
            mBinding.viewAssetRisk.text = "${
            risk.toString().getNum(2, withZero = true)
            }%"

            mBinding.viewUnrealized.text = NumberStringUtil.formatAmount(
                    CalculateUtil.getUnrealizedLoss(assetInfo, tickerMap), 2,
                    NumberStringUtil.AmountStyle.FillZeroNoComma
            )
            mBinding.tvAssetAvailableMargin.text = NumberStringUtil.formatAmount(
                    max(CalculateUtil.getUsableDeposit(mViewModel.mPositionModeLiveData.value!!, assetInfo, tickerMap), 0.0), 4,
                    NumberStringUtil.AmountStyle.FillZeroNoComma
            ) + " " + McConstants.COMMON.PAIR_RIGHT_NAME
        } else {
            mBinding.viewPositionMargin.text = NumberStringUtil.formatAmount(
                    assetInfo.mTotalMargin, 4,
                    NumberStringUtil.AmountStyle.FillZeroNoComma
            )

            mBinding.viewUnrealizedPart.text = NumberStringUtil.formatAmount(
                    CalculateUtil.getUnrealizedLoss(assetInfo, tickerMap), 2,
                    NumberStringUtil.AmountStyle.FillZeroNoComma
            )
            mBinding.tvAssetAvailableMargin.text = NumberStringUtil.formatAmount(
                    max(CalculateUtil.getUsableDeposit(mViewModel.mPositionModeLiveData.value!!, assetInfo, tickerMap), 0.0), 4,
                    NumberStringUtil.AmountStyle.FillZeroNoComma
            ) + " " + McConstants.COMMON.PAIR_RIGHT_NAME
        }
    }


    private fun initViewListeners() {

        mBinding.viewPrice.addTextChangedListener {
            TextWatcherTrickUtil.fillZeroBeforePoint(it)
            mViewModel.updatePrice(it.toString())
        }
        mBinding.viewTriggerPrice.addTextChangedListener {
            TextWatcherTrickUtil.fillZeroBeforePoint(it)
            mViewModel.updateTriggerPrice(it.toString())
        }
        mBinding.viewTriggerExecutePrice.addTextChangedListener {
            TextWatcherTrickUtil.fillZeroBeforePoint(it)
            mViewModel.updateTriggerExecutePrice(it.toString())
        }
        mBinding.viewQuantity.addTextChangedListener {
            if (mViewModel.mQuantityUnitLiveData.value == QuantityUnit.SIZE) {
                TextWatcherTrickUtil.noFirstZero(it)
            } else {
                TextWatcherTrickUtil.fillZeroBeforePoint(it)
            }
            mViewModel.updateQuantity(it.toString())
        }
        mBinding.viewStopProfit.addTextChangedListener {
            when (mViewModel.mStopProfitTypeLiveData.value) {
                TriggerSetType.PRICE -> {
                    mViewModel.updateStopProfitPrice(it.toString())
                }
                TriggerSetType.PERCENT -> {
                    mViewModel.updateStopProfitRate(it.toString())
                }
            }
        }
        mBinding.viewStopLoss.addTextChangedListener {
            when (mViewModel.mStopLossTypeLiveData.value) {
                TriggerSetType.PRICE -> {
                    mViewModel.updateStopLossPrice(it.toString())
                }
                TriggerSetType.PERCENT -> {
                    mViewModel.updateStopLossRate(it.toString())
                }
            }
        }


        mBinding.viewLong.setOnClickListener {
            if (CoinwHyUtils.isToOtherPage()) {
                return@setOnClickListener
            }

            if (mViewModel.mTradingLiveData.value!!){
                return@setOnClickListener
            }

            // 使用体验金下单模式,但是没有选择体验金
            if (mIsUseExperienceGold.get()!! && (!mViewModel.isUseExperienceGold())) {
                ToastUtils.showLongToast(R.string.mc_sdk_please_choice_experience_gold)
                return@setOnClickListener
            }

            if (checkPlaceOrder()) {

                // 下单是否二次确认
                if (SPUtils.getOrderConfirm()){
                    var triggerPrice = ""
                    var price = ""
                    var count = if (mViewModel.isUseExperienceGold()){
                        mViewModel.mUseExperienceGold.value!!.mAmount + getString(R.string.mc_sdk_usdt)
                    } else {
                        if (mViewModel.mQuantityUnitLiveData.value == QuantityUnit.USDT){
                            mViewModel.mQuantityLiveData.value + getString(R.string.mc_sdk_usdt)
                        } else if(mViewModel.mQuantityUnitLiveData.value == QuantityUnit.SIZE){
                            mViewModel.mQuantityLiveData.value + getString(R.string.mc_sdk_contract_unit)
                        } else {
                            mViewModel.mQuantityLiveData.value + mViewModel.mProductLiveData.value?.mBase?.toUpperCase()
                        }
                    }

                    when (mViewModel.mPositionTypeLiveData.value) {
                        PositionType.PLAN -> {
                            price = mBinding.viewPrice.text.toString()
                        }
                        PositionType.EXECUTE -> {
                            price = getString(R.string.mc_sdk_contract_market_price)
                        }
                        PositionType.PLAN_TRIGGER -> {

                            triggerPrice = mBinding.viewTriggerPrice.text.toString()
                            price = mBinding.viewTriggerExecutePrice.text.toString()

                        }
                    }

                    showOrderConfirmDialog(requireContext(), Direction.LONG.direction, triggerPrice, price, count) onConfirm@{ notShow ->
                        // notShow 不再提示
                        SPUtils.saveOrderConfirm(!notShow)
                        if (mViewModel.mTradingLiveData.value!!){
                            return@onConfirm
                        }
                        mViewModel.apiPlaceOrder(Direction.LONG.direction)
                    }
                } else {
                    mViewModel.apiPlaceOrder(Direction.LONG.direction)
                }

            }
        }

        mBinding.viewShort.setOnClickListener {
            if (CoinwHyUtils.isToOtherPage()) {
                return@setOnClickListener
            }

            if (mViewModel.mTradingLiveData.value!!){
                return@setOnClickListener
            }

            // 使用体验金下单模式,但是没有选择体验金
            if (mIsUseExperienceGold.get()!! && (!mViewModel.isUseExperienceGold())) {
                ToastUtils.showLongToast(R.string.mc_sdk_please_choice_experience_gold)
                return@setOnClickListener
            }

            if (checkPlaceOrder()) {

                // 下单是否二次确认
                if (SPUtils.getOrderConfirm()){
                    var triggerPrice = ""
                    var price = ""
                    var count = if (mViewModel.isUseExperienceGold()){
                        mViewModel.mUseExperienceGold.value!!.mAmount + getString(R.string.mc_sdk_usdt)
                    } else {
                        if (mViewModel.mQuantityUnitLiveData.value == QuantityUnit.USDT){
                            mViewModel.mQuantityLiveData.value + getString(R.string.mc_sdk_usdt)
                        } else if(mViewModel.mQuantityUnitLiveData.value == QuantityUnit.SIZE){
                            mViewModel.mQuantityLiveData.value + getString(R.string.mc_sdk_contract_unit)
                        } else {
                            mViewModel.mQuantityLiveData.value + mViewModel.mProductLiveData.value?.mBase?.toUpperCase()
                        }
                    }

                    when (mViewModel.mPositionTypeLiveData.value) {
                        PositionType.PLAN -> {
                            price = mBinding.viewPrice.text.toString()
                        }
                        PositionType.EXECUTE -> {
                            price = getString(R.string.mc_sdk_contract_market_price)
                        }
                        PositionType.PLAN_TRIGGER -> {

                            triggerPrice = mBinding.viewTriggerPrice.text.toString()
                            price = mBinding.viewTriggerExecutePrice.text.toString()

                        }
                    }

                    showOrderConfirmDialog(requireContext(), Direction.SHORT.direction, triggerPrice, price, count) onConfirm@{ notShow ->
                        // notShow 不再提示
                        SPUtils.saveOrderConfirm(!notShow)
                        if (mViewModel.mTradingLiveData.value!!){
                            return@onConfirm
                        }
                        mViewModel.apiPlaceOrder(Direction.SHORT.direction)
                    }
                } else {
                    mViewModel.apiPlaceOrder(Direction.SHORT.direction)
                }

            }
        }
        mDepthDelegate.setOnItemClickListener { isBuy, it ->
            if (it.size > 0) {
                mViewModel.onDepthItemClick(it[0].toString())
            }
        }

        mDepthDelegate.setOnIndexPriceClickListener {
            showMessageDialog(
                    requireContext(),
                    title = getString(R.string.mc_sdk_contract_index_price_tip_title),
                    content = getString(R.string.mc_sdk_contract_index_price_tip),
                    cancel = false,
                    confirmText = getString(R.string.mc_sdk_know)
            ) {

            }
        }

        mBinding.viewLeverage.setOnClickListener {
            if (CoinwHyUtils.isToOtherPage()) {
                return@setOnClickListener
            }
            var maxLeverage = 100
            var list = CoinwHyUtils.products ?: arrayListOf()
            if (list.size > 0) {
                for (i in list.indices) {
                    if (list[i].mBase == mProduct?.mBase) {
                        maxLeverage = list[i].mMaxLeverage
                        break
                    }
                }
            }

            showModifyLeverageDialog(
                    requireContext(),
                    mViewModel.mPositionModeLiveData.value!!,
                    mViewModel.mPositionMergeModeLiveData.value!!,
                    UserConfigStorage.getLeverage(), maxLeverage,
                    { leverage ->

                        val last = (mViewModel.mTickerLiveData.value!! as Ticker).last.getDoubleValue()

                        val maxOpenSize = CalculateUtil.getMaxOpenSheet(
                                last,
                                mViewModel.mContractAssetInfoLiveData.value,
                                leverage,
                                mViewModel.mProductLiveData.value,
                                mViewModel.mPositionModeLiveData.value!!,
                                mViewModel.mTickersLiveData.value!!,
                                mViewModel.mProductLiveData.value?.mTakerFee ?: 0.0,
                                mViewModel.mQuantityUnitLiveData.value!!)

                        val scale = when(mViewModel.mQuantityUnitLiveData.value!!) {
                            QuantityUnit.SIZE -> {
                                0
                            }
                            QuantityUnit.USDT -> {
                                4
                            }
                            QuantityUnit.COIN -> {
                                mViewModel.mProductLiveData.value?.mOneLotSize?.getPrecision()
                                        ?: 3
                            }
                        }

                        return@showModifyLeverageDialog maxOpenSize.toString().getNum(scale)
                    }
            ) { positionMode, positionMergeMode, leverage ->

                // 不管切换仓位模式成不成功 始终先保存杠杆切换
                UserConfigStorage.setLeverage(leverage)

                if (positionMode != mViewModel.mPositionModeLiveData.value || positionMergeMode != mViewModel.mPositionMergeModeLiveData.value) {
                    mViewModel.updatePositionMode(positionMode, positionMergeMode, leverage)
                } else {
                    mViewModel.mPositionModeLiveData.postValue(positionMode)
                    mViewModel.mPositionMergeModeLiveData.postValue(positionMergeMode)
                }

            }
        }

        mBinding.viewProfitSetType.setOnClickListener {
            mViewModel.updateStopProfitType()
        }

        mBinding.viewLossSetType.setOnClickListener {
            mViewModel.updateStopLossType()
        }


        mBinding.viewTriggerSetSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            mViewModel.updateTargetLayoutVisible(isChecked)
        }

        setLeftIcon(R.drawable.mc_sdk_anim_menu) {
            showSelectProductDialog(
                    requireContext(),
                    mViewModel.mProductsLiveData.value ?: ArrayList()
            ) { product ->
                mViewModel.updateProduct(product)
            }
        }

        mBinding.llTitle.setOnClickListener {
            context?.let {
                showSelectProductDialog(
                        requireContext(),
                        mViewModel.mProductsLiveData.value ?: ArrayList()
                ) { product ->
                    mViewModel.updateProduct(product)
                }
            }

        }
        mBinding.ivHistory.setOnClickListener {
            if (CoinwHyUtils.isToOtherPage()) {
                return@setOnClickListener
            }
            McHistoryOrderActivity.launch(requireActivity())
        }

        mBinding.layoutPositionType.setOnClickListener {
            val titles = arrayOf(
                    getString(R.string.mc_sdk_contract_market_order),
                    getString(R.string.mc_sdk_contract_limit_order),
                    getString(R.string.mc_sdk_contract_plan_order)
            )

            showSelectItemDialog(requireContext(), titles){index, text ->
                val type = when (index) {
                    0 -> {
                        PositionType.EXECUTE
                    }
                    1 -> {
                        PositionType.PLAN
                    }
                    2 -> {
                        PositionType.PLAN_TRIGGER
                    }
                    else -> PositionType.PLAN
                }
                mViewModel.updatePositionType(type)

            }

        }

        mBinding.viewTriggerPriceType.setOnClickListener {
            mViewModel.updateTriggerType()
        }

        mBinding.viewAssetRiskTitle.setOnClickListener {
            showMessageDialog(
                    requireContext(),
                    title = getString(R.string.mc_sdk_contract_positions_risk_tip),
                    content = getString(R.string.mc_sdk_contract_positions_risk_desc),
                    cancel = false,
                    confirmText = getString(R.string.mc_sdk_know)
            ) {

            }
        }
        mBinding.viewTriggerSet.setOnClickListener {
            showMessageDialog(
                    requireContext(),
                    title = getString(R.string.mc_sdk_contract_target_set_tip),
                    content = getString(R.string.mc_sdk_contract_target_set_desc),
                    cancel = false,
                    confirmText = getString(R.string.mc_sdk_know)
            ) {

            }
        }
        mBinding.viewLongOrderMargin.setOnClickListener {
            showMessageDialog(
                    requireContext(),
                    title = getString(R.string.mc_sdk_contract_order_margin_tip),
                    content = getString(R.string.mc_sdk_contract_order_margin_desc),
                    cancel = false,
                    confirmText = getString(R.string.mc_sdk_know)
            ) {

            }
        }
        mBinding.viewShortOrderMargin.setOnClickListener {
            showMessageDialog(
                    requireContext(),
                    title = getString(R.string.mc_sdk_contract_order_margin_tip),
                    content = getString(R.string.mc_sdk_contract_order_margin_desc),
                    cancel = false,
                    confirmText = getString(R.string.mc_sdk_know)
            ) {

            }
        }

//        mBinding.viewQuantityUnit.setOnClickListener {

//        }

        mBinding.viewPriceIncrease.setOnClickListener {
            mViewModel.increasePriceStepValue()
        }
        mBinding.viewPriceDecrease.setOnClickListener {
            mViewModel.decreasePriceStepValue()
        }
        mBinding.viewTriggerPriceIncrease.setOnClickListener {
            mViewModel.increaseTriggerPriceStepValue()
        }
        mBinding.viewTriggerPriceDecrease.setOnClickListener {
            mViewModel.decreaseTriggerPriceStepValue()
        }
        mBinding.ivRightIcon.setOnClickListener {
            if (getViewModel().mProductsLiveData.value == null || mProduct == null) {
                ToastUtils.showShortToast(getString(R.string.mc_sdk_net_error))
                return@setOnClickListener
            }
            McKLineActivity.launch(requireContext(), getViewModel().mProductsLiveData.value!! as ArrayList<Product>, mProduct!!)
        }
        mBinding.ivMenuIcon.setOnClickListener {
            showContractMenuDialog(requireContext(), mBinding.ivMenuIcon, mViewModel.mProductsLiveData.value ?: ArrayList())
        }
        mBinding.layoutFundingRate.setOnClickListener {
            showMessageDialog(
                    requireContext(),
                    title = getString(R.string.mc_sdk_contract_funding_rate_desc_title),
                    content = getString(R.string.mc_sdk_contract_funding_rate_desc_content),
                    cancel = false,
                    confirmText = getString(R.string.mc_sdk_know)
            ) {

            }
        }

        mBinding.layoutProgress.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                if (mIgnoreThisProgressChange){
                    return
                }

                if (mViewModel.mMaxCanOpenLongSizeData.value == null){
                    return
                }

                seekBar?.let {
                    val size = mViewModel.mMaxCanOpenLongSizeData.value as Double * progress / it.max
                    mViewModel.mQuantityUnitLiveData.value?.let {
                        val scale = when (it) {
                            QuantityUnit.SIZE -> {
                                0
                            }
                            QuantityUnit.USDT -> {
                                4
                            }
                            QuantityUnit.COIN -> {
                                (mViewModel.mProductLiveData.value?.mOneLotSize ?: 0.1).getPrecision()
                            }
                        }

                        mViewModel.updateQuantity(size.toString().getNum(scale))
                    }

                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })


        mBinding.llExperienceGold.setOnClickListener {

            if (CoinwHyUtils.isToOtherPage()) {
                return@setOnClickListener
            }

            if (mViewModel.mProductLiveData.value == null) {
                return@setOnClickListener
            }

            mViewModel.fetchCoinExperienceGold()

        }

        mBinding.ivExperienceGold.setOnClickListener {
            if (CoinwHyUtils.isToOtherPage()) {
                return@setOnClickListener
            }
            ExperienceGoldActivity.launch(requireContext())
        }

        mBinding.llAvailable.setOnClickListener {
            if (CoinwHyUtils.isToOtherPage()) {
                return@setOnClickListener
            }
            EventBus.getDefault().post(JumpToContractTransfer(-1, ""))
        }

    }


    private fun checkPlaceOrder(): Boolean {
        mViewModel.mQuantityLiveData.value?.let {

            if (mIsUseExperienceGold.get()!!) {
                // 体验金不用输入数量
                return@let
            }

            if (it.isEmpty()) {
                Toast.makeText(context, R.string.mc_sdk_contract_place_order_tip_input_quantity, Toast.LENGTH_SHORT).show()
                return false
            }
        }
        when (mViewModel.mPositionTypeLiveData.value) {
            PositionType.PLAN -> {
                if (TextUtils.isEmpty(mBinding.viewPrice.text.toString())) {
                    Toast.makeText(context, R.string.mc_sdk_contract_place_order_tip_input_price, Toast.LENGTH_SHORT).show()
                    return false
                }
            }
            PositionType.EXECUTE -> {
            }
            PositionType.PLAN_TRIGGER -> {
                if (TextUtils.isEmpty(mBinding.viewTriggerPrice.text.toString())) {
                    Toast.makeText(context, R.string.mc_sdk_contract_place_order_tip_input_trigger_price, Toast.LENGTH_SHORT).show()
                    return false
                }
                if (mViewModel.mExecuteTypeLiveData.value == TriggerType.PLAN) {
                    if (TextUtils.isEmpty(mBinding.viewTriggerExecutePrice.text.toString())) {
                        Toast.makeText(context, R.string.mc_sdk_contract_place_order_tip_input_execute_price, Toast.LENGTH_SHORT).show()
                        return false
                    }
                }

            }
            else -> {
            }
        }
        return true
    }

    private fun initDepthView() {
        mDepthDelegate = DepthFutureViewDelegate(mBinding.viewDepth)
        mDepthDelegate.init()
    }

    private fun initTabViews() {
        val fragments: MutableList<Fragment> = ArrayList()
        val titles = arrayOf(
                getString(R.string.mc_sdk_contract_cur_position),
                getString(R.string.mc_sdk_contract_cur_order),
                getString(R.string.mc_sdk_contract_cur_plan_order)
        )
//        fragments.add(PositionsFragment.getInstance())
        mPositionFragment = PositionsFragment.getInstance()
        mOpenOrderFragment = OpenOrderFragment.getInstance()
        mOpenPlanOrderFragment = OpenPlanOrderFragment.getInstance()
        fragments.add(mPositionFragment)
        fragments.add(mOpenOrderFragment)
        fragments.add(mOpenPlanOrderFragment)
        mBinding.viewPager.adapter = object : FragmentStateAdapter(this.requireActivity()) {
            override fun getItemCount(): Int {
                return fragments.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }

        }
        mBinding.viewPager.offscreenPageLimit = 3
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }


    private fun addPositionListener() {
        mMarketListenerList.add(
                MarketListenerManager.subscribe(
                        MarketSubscribeType.Position(),
                        mViewModel.mPositionLiveData
                )
        )

        mMarketListenerList.add(
                MarketListenerManager.subscribe(
                        MarketSubscribeType.PositionChange(),
                        mViewModel.mPositionChangeLiveData
                )
        )

        mMarketListenerList.add(
                MarketListenerManager.subscribe(
                        MarketSubscribeType.PositionFinish(),
                        mViewModel.mPositionFinishLiveData
                )
        )

        mMarketListenerList.add(
                MarketListenerManager.subscribe(
                        MarketSubscribeType.AllPosition(),
                        mViewModel.mAllPositionLiveData
                )
        )

    }


    private fun addTradeListener(product: Product) {
        mMarketListenerList.add(
                MarketListenerManager.subscribe(
                        MarketSubscribeType.Depth(product.mBase, "usd"),
                        mViewModel.mDepthLiveData
                )
        )

        mMarketListenerList.add(
                MarketListenerManager.subscribe(
                        MarketSubscribeType.IndexPrice(product.mBase, "usd"),
                        mViewModel.mIndexPriceLiveData
                )
        )

        mMarketListenerList.add(
                MarketListenerManager.subscribe(
                        MarketSubscribeType.TickerSwap(product.mBase, "usd"),
                        mViewModel.mTickerLiveData
                )
        )

        mViewModel.mProductsLiveData.value?.forEach { product ->
            run {
                mMarketListenerList.add(
                        MarketListenerManager.subscribe(
                                MarketSubscribeType.MarkPrice(product.mBase, "usd"),
                                MutableLiveData()
                        ).apply {
                            liveData.observe(viewLifecycleOwner, Observer { price ->
                                kotlin.run {
                                    mViewModel.mTickersLiveData.value?.set(product.mBase, price)
                                    mViewModel.mTickersLiveData.postValue(mViewModel.mTickersLiveData.value)

                                    if (product.mId == mViewModel.mProductLiveData.value?.mId) {
                                        mViewModel.mMarketPriceLiveData.postValue(price)
                                    }
                                }
                            })
                        }
                )
            }
        }

        mMarketListenerList.add(
                MarketListenerManager.subscribe(
                        MarketSubscribeType.FundingRate(product.mBase, "usd"),
                        mViewModel.mFundingRateLiveData
                )
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loginEvent(event: LoginEvent) {
        if (userIsLogin()) {
            mViewModel.fetchUserTreadConfig()
            mViewModel.fetchContractAssetsInfo()
        } else {
            mPositionFragment.setPositions(mutableListOf())
            mViewModel.resetUserLiveData()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeProduct(event: ChangeProductEvent) {
        mViewModel.updateProduct(event.product)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getExperienceGold(event: ContractGoldEvent) {
        val experienceGoldViewModel = ViewModelProvider(this).get(ExperienceGoldViewModel::class.java)
        experienceGoldViewModel.mStatus.value=1
        experienceGoldViewModel.mExperienceGoldLiveData.observe(this, Observer {
            it.forEach { data->
                if(data.mId==event.id){
                    EventBus.getDefault().post(SelectExperienceGoldEvent(data))
                    return@forEach
                }
            }
        })
        experienceGoldViewModel.getInitData(true)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun selectedExperienceGold(event: SelectExperienceGoldEvent) {

        if (mViewModel.mProductsLiveData.value != null) {
            if (!mViewModel.mProductLiveData.value!!.mBase.equals(event.experienceGold.mCurrencyName, ignoreCase = true)) {
                mViewModel.mProductsLiveData.value!!.forEach {

                    if (it.mBase.equals(event.experienceGold.mCurrencyName, ignoreCase = true)) {
                        mViewModel.updateProduct(it)
                        return@forEach
                    }
                }
            }

            mIsUseExperienceGold.set(true)
            mViewModel.mUseExperienceGold.postValueNotNull(event.experienceGold)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeTradeUnit(event: ChangeTradeUnitEvent){
        mViewModel.updateQuantityUnitType()
    }

    override fun applyTheme() {
        mBinding.invalidateAll()
        mDepthDelegate.resetPaint()
        mOpenOrderFragment.applyTheme()
        mOpenPlanOrderFragment.applyTheme()
    }
}

