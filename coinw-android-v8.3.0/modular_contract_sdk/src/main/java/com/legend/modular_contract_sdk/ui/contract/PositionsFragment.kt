package com.legend.modular_contract_sdk.ui.contract

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.legend.common.util.ThemeUtil
import com.legend.modular_contract_sdk.BR
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.api.ModularContractSDK
import com.legend.modular_contract_sdk.base.BaseFragment
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils
import com.legend.modular_contract_sdk.common.*
import com.legend.modular_contract_sdk.common.event.ChangeTradeUnitEvent
import com.legend.modular_contract_sdk.common.event.GetSharePicEvent
import com.legend.modular_contract_sdk.common.event.LoginEvent
import com.legend.modular_contract_sdk.component.adapter.DataBindingRecyclerViewAdapter
import com.legend.modular_contract_sdk.component.market_listener.MarketListener
import com.legend.modular_contract_sdk.component.market_listener.MarketListenerManager
import com.legend.modular_contract_sdk.component.market_listener.MarketSubscribeType
import com.legend.modular_contract_sdk.component.market_listener.Price
import com.legend.modular_contract_sdk.databinding.McSdkFragmentPositionsBinding
import com.legend.modular_contract_sdk.databinding.McSdkItemPositionBinding
import com.legend.modular_contract_sdk.databinding.McSdkViewSharePositionBinding
import com.legend.modular_contract_sdk.databinding.McSdkViewSharePositionNewBinding
import com.legend.modular_contract_sdk.onekeyshare.CreateQRImage
import com.legend.modular_contract_sdk.repository.model.PositionAndOrder
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.repository.model.wrap.PositionWrap
import com.legend.modular_contract_sdk.ui.contract.vm.SwapContractViewModel
import com.legend.modular_contract_sdk.utils.*
import com.legend.modular_contract_sdk.widget.gone
import com.legend.modular_contract_sdk.widget.visible
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.mc_sdk_view_empty.view.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 仓位Fragment
 */
class PositionsFragment : BaseFragment<SwapContractViewModel>() {

    private lateinit var mBinding: McSdkFragmentPositionsBinding

    private var mMarkPriceMarketListenerList: MutableList<MarketListener> = mutableListOf()
    private var mPositionAndOrders: List<PositionAndOrder>? = null
    private var mIsVisibleToUser = false
    private lateinit var mCurrentTradeUnit : QuantityUnit

    private lateinit var mAdapter: DataBindingRecyclerViewAdapter<PositionWrap>

    companion object {
        fun getInstance(): PositionsFragment = PositionsFragment()
        private const val TAG = "PositionsFragment"
    }

    override fun createRootView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.mc_sdk_fragment_positions, container, false)
        mBinding = McSdkFragmentPositionsBinding.bind(rootView)

        initListener()
        refreshNoLogin()
        initTradeUnit()
        return mBinding.root
    }


    private fun initListener() {
        mBinding.rvPositions.adapter = DataBindingRecyclerViewAdapter(
                context,
                R.layout.mc_sdk_item_position,
                BR.position,
                mutableListOf<PositionWrap>()
        ).apply {

            mAdapter = this

            setOnBindingViewHolderListener { holder, position ->
                val itemBinding = holder.getBinding<McSdkItemPositionBinding>()
                itemBinding.tradeUnit = mCurrentTradeUnit
                val positionWarp: PositionWrap =
                        (mBinding.rvPositions.adapter as DataBindingRecyclerViewAdapter<PositionWrap>).allData[position]

                itemBinding.btnClosePosition.setOnClickListener {
                    showMessageDialog(
                            requireContext(),
                            subtitle = getString(R.string.mc_sdk_contract_position_close_tip)
                    ) {
                        mViewModel.closeOrder(
                                positionWarp.position.mInstrument,
                                positionWarp.position.mId, isExperienceGold = positionWarp.isExpGold()
                        )
                    }
                }

                itemBinding.btnClosePartPosition.setOnClickListener {

                    if (mViewModel.mTradingLiveData.value!!){
                        return@setOnClickListener
                    }

                    showClosePartPositionDialog(
                            requireContext(),
                            mViewModel.mQuantityUnitLiveData.value!!,
                            positionWarp
                    ) closePosition@{ type: PositionType, price: String, count: Double, inputCount: String ->

                        if (SPUtils.getClosePositionConfirm()) {

                            val showCount = if (SPUtils.getTradeUnit() == QuantityUnit.USDT.unit) {
                                inputCount + getString(R.string.mc_sdk_usdt)
                            } else if (SPUtils.getTradeUnit() == QuantityUnit.SIZE.unit) {
                                inputCount + getString(R.string.mc_sdk_contract_unit)
                            } else {
                                inputCount + positionWarp.product!!.mBase.toUpperCase()
                            }

                            showClosePositionConfirmDialog(requireContext(), price, showCount) onConfirm@{ notShow ->
                                if (mViewModel.mTradingLiveData.value!!){
                                    return@onConfirm
                                }
                                if (notShow){
                                    SPUtils.saveClosePositionConfirm(!notShow)
                                }
                                mViewModel.closePartOrder(
                                        positionWarp.position.mInstrument,
                                        positionWarp.position.mId, null, count, type, price
                                        , isExperienceGold = positionWarp.isExpGold()
                                )
                            }
                        } else {
                            mViewModel.closePartOrder(
                                    positionWarp.position.mInstrument,
                                    positionWarp.position.mId, null, count, type, price
                                    , isExperienceGold = positionWarp.isExpGold()
                            )
                        }


                    }
                }

                itemBinding.btnReverseOpenPosition.setOnClickListener {
                    showMessageDialog(
                            requireContext(),
                            subtitle = getString(R.string.mc_sdk_contract_position_close_reverse_tip)
                    ) {
                        mViewModel.closeAndOpenReverseOrder(
                                positionWarp.position.mInstrument,
                                positionWarp.position.mId, isExperienceGold = positionWarp.isExpGold()
                        )
                    }
                }

//                itemBinding.btnAddPosition.setOnClickListener {
//                    showAddPositionDialog(requireContext()) { count, addPositionType ->
//                        mViewModel.addPosition(count, positionWarp.position.mId, addPositionType,
//                                isExperienceGold = positionWarp.isExpGold())
//                    }
//                }

                itemBinding.tvDirection.text = getString(positionWarp.getDirection())

                itemBinding.tvDirection.background =
                        if (positionWarp.isLong()) ThemeUtil.getThemeDrawable(requireContext(), R.attr.bg_buy_btn)
                        else ThemeUtil.getThemeDrawable(requireContext(), R.attr.bg_sell_btn)

                itemBinding.btnChangeTakeProfitStopLoss.setOnClickListener {

                    var product: Product? = null
                    mViewModel.mProductsLiveData.value!!.forEach {
                        if (positionWarp.position.mInstrument.equals(it.mBase, true)) {
                            product = it
                            return@forEach
                        }
                    }

                    product?.let {
                        showModifyStopProfitAndLoss(
                                requireContext(),
                                this@PositionsFragment,
                                positionWarp.position,
                                it,
                                showMoveTP_SL = !positionWarp.isExpGold()// 体验金不支持移动止盈止损
                        )
                    }

                }

                itemBinding.tvShare.setOnClickListener {

                    showShare(positionWarp)

                }

                itemBinding.tvId.setOnClickListener {
                    McClipboardUtils.copyingToClipboard(requireContext(), positionWarp.position.mId.toString())
                    ToastUtils.showShortToast(R.string.mc_sdk_copy_success)
                }


                if (positionWarp.isExpGold() || positionWarp.position.mPositionModel == PositionMode.FULL.mode){
                    itemBinding.ivAddPositionMargin.visibility = View.GONE
                } else {
                    itemBinding.ivAddPositionMargin.visibility = View.VISIBLE
                }

                itemBinding.viewAddPositionMargin.setOnClickListener {
                    showModifyPositionMarginDialogFun(positionWarp)
                }

            }

        }

        mBinding.tvCloseAllPosition.setOnClickListener {
            showMessageDialog(
                    requireContext(),
                    subtitle = getString(R.string.mc_sdk_contract_all_position_close_tip)
            ) {
                mViewModel.mPositionModeLiveData.value?.let {
                    mViewModel.closeAllOrder(it.mode.toString())
                }
            }
        }
    }

    private fun showModifyPositionMarginDialogFun(positionWarp: PositionWrap) {

        if (!positionWarp.isExpGold() && positionWarp.position.mPositionModel == PositionMode.PART.mode){
            showModifyPositionMarginDialog(requireContext(), positionWarp, mViewModel.mContractAssetInfoLiveData.value!!){ amount, type ->
                if (type == 1){
                    mViewModel.addMargin(positionWarp, amount)
                } else {
                    mViewModel.minusMargin(positionWarp, amount)
                }
            }
        }


    }


    private fun showShare(positionWrap: PositionWrap) {
        lifecycleScope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
            Logger.e("share errror")
        }) {
            context?.let {
                val dialog = showLoading(it)

                val shareView = layoutInflater.inflate(R.layout.mc_sdk_view_share_position_new, mBinding.rvPositions, false)

                val binding = McSdkViewSharePositionNewBinding.bind(shareView)

                binding.isHistoryOrder = false
                binding.position = positionWrap
                binding.executePendingBindings()

                val bitmap = withContext(Dispatchers.IO){

                    val sharePicResult = async {
                        suspendCoroutine<String> {
                            EventBus.getDefault().post(GetSharePicEvent{isSuccess, result ->
                                it.resume(result)
                            })
                        }
                    }.await()

                    Logger.e("shareInfo: $sharePicResult")

                    val qrcodeSize = ViewUtil.dip2px(context, 52f)

                    val qrLogo = BitmapFactory.decodeResource(requireContext().resources, R.drawable.mc_sdk_qr_logo)

                    binding.ivQrcode.setImageBitmap(CreateQRImage.createQRCodeWithLogo( sharePicResult, qrcodeSize, qrLogo))

                    shareView.measure(
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
                    shareView.layout(0, 0,
                            shareView.measuredWidth,
                            shareView.measuredHeight)

                    val shareBitmap = Bitmap.createBitmap(shareView.measuredWidth, shareView.measuredHeight, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(shareBitmap)
                    shareView.draw(canvas)
                    shareBitmap
                }

                dialog.dismiss()
                showImageShareDialog(it, bitmap)

            }

        }
    }

    override fun createViewModel() = ViewModelProvider(requireActivity()).get(SwapContractViewModel::class.java)

    fun setPositions(positionAndOrders: List<PositionAndOrder>) {

        if (!::mBinding.isInitialized) {
            return
        }
        mPositionAndOrders = positionAndOrders
        CoinwHyUtils.removeAllMarketListener(mMarkPriceMarketListenerList)

        val positions = positionAndOrders.map { position ->
            PositionWrap(position)
        }

        if (positionAndOrders.isNotEmpty()) {
            mBinding.tvCloseAllPosition.visibility = View.VISIBLE
            mBinding.tvPositionInfo.visibility = View.VISIBLE
            mBinding.line.visibility = View.VISIBLE
            mBinding.empty.ll.visibility = View.GONE
        } else {
            mBinding.tvCloseAllPosition.visibility = View.GONE
            mBinding.tvPositionInfo.visibility = View.GONE
            mBinding.line.visibility = View.GONE
            mBinding.empty.ll.visibility = View.VISIBLE
        }

        positions.forEach { position ->

            var product: Product? = null
            // todo 空指针隐患 仓位请求成功后 交易对列表还没有取到
            mViewModel.mProductsLiveData.value?.forEach {
                if (position.position.mInstrument.equals(it.mBase, true)) {
                    product = it
                    return@forEach
                }
            }

            position.product = product

            mViewModel.mTickersLiveData.value?.let {
                if (product != null) {
                    if (it[product!!.mBase] == null) {
                        return@let
                    }
                    val markPrice = it[product!!.mBase] as Price
                    position.markPrice = markPrice.p.getDouble()
                }

            }

            mMarkPriceMarketListenerList.add(
                    MarketListenerManager.subscribe(
                            MarketSubscribeType.MarkPrice(position.position.mInstrument.toLowerCase(), "usd"),
                            MutableLiveData()
                    ).apply {
                        liveData.observe(this@PositionsFragment, Observer {
                            var markPrice = it as Price
                            position.tickerMap = mViewModel.mTickersLiveData.value
                            position.usableBalances = mViewModel.mContractAssetInfoLiveData.value
                            position.markPrice = markPrice.p.getDouble()
                            position.risk = mViewModel.mFullModeRiskSizeData.value!!
                        })
                    }
            )
        }

        (mAdapter).refreshData(positions)
    }


    override fun onResume() {
        super.onResume()
        addSocketListener()
    }

    override fun onPause() {
        super.onPause()
        CoinwHyUtils.removeAllMarketListener(mMarkPriceMarketListenerList)
    }

    private fun addSocketListener() {
        if (mAdapter.allData.isNotEmpty()) {
            mAdapter.allData.forEach { position ->
                mMarkPriceMarketListenerList.add(
                        MarketListenerManager.subscribe(
                                MarketSubscribeType.MarkPrice(position.position.mInstrument.toLowerCase(), "usd"),
                                MutableLiveData()
                        ).apply {
                            liveData.observe(this@PositionsFragment, Observer {
                                var markPrice = it as Price
                                position.tickerMap = mViewModel.mTickersLiveData.value
                                position.usableBalances = mViewModel.mContractAssetInfoLiveData.value
                                position.markPrice = markPrice.p.getDouble()
                                position.risk = mViewModel.mFullModeRiskSizeData.value!!
                            })
                        }
                )
            }
        }

    }

    private fun refreshNoLogin() {
        if (ModularContractSDK.userIsLogin()) {
            mBinding.empty.tvNoLogin.gone()
            mBinding.empty.tvNoData.visible()
            mBinding.empty.ivNoData.visible()
        } else {
            mBinding.empty.tvNoLogin.visible()
            mBinding.empty.tvNoData.gone()
            mBinding.empty.ivNoData.gone()
        }
    }

    private fun initTradeUnit(){
        when (SPUtils.getTradeUnit()) {
            QuantityUnit.SIZE.unit -> {
                mCurrentTradeUnit = QuantityUnit.SIZE
            }
            QuantityUnit.USDT.unit -> {
                mCurrentTradeUnit = QuantityUnit.USDT
            }
            QuantityUnit.COIN.unit -> {
                mCurrentTradeUnit = QuantityUnit.COIN
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loginInEvent(event: LoginEvent) {
        refreshNoLogin()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeTradeUnit(event: ChangeTradeUnitEvent){
        initTradeUnit()
        mBinding.rvPositions.adapter?.notifyDataSetChanged()
    }

}