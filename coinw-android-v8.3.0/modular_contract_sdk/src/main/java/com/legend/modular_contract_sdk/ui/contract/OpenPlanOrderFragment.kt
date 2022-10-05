package com.legend.modular_contract_sdk.ui.contract

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.legend.common.util.ThemeUtil
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.api.ModularContractSDK
import com.legend.modular_contract_sdk.api.ModularContractSDK.userIsLogin
import com.legend.modular_contract_sdk.base.BaseFragment
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils
import com.legend.modular_contract_sdk.common.event.ChangeTradeUnitEvent
import com.legend.modular_contract_sdk.common.event.LoginEvent
import com.legend.modular_contract_sdk.common.event.McPositionFinishEvent
import com.legend.modular_contract_sdk.common.event.McRefreshOrderList
import com.legend.modular_contract_sdk.common.showMessageDialog
import com.legend.modular_contract_sdk.component.market_listener.MarketSubscribeType
import com.legend.modular_contract_sdk.component.market_listener.Price
import com.legend.modular_contract_sdk.databinding.McSdkFragmentOpenPlanOrderBinding
import com.legend.modular_contract_sdk.databinding.McSdkItemOpenHeaderBinding
import com.legend.modular_contract_sdk.databinding.McSdkItemOpenOrderBinding
import com.legend.modular_contract_sdk.databinding.McSdkItemOpenPlanOrderBinding
import com.legend.modular_contract_sdk.repository.model.PositionAndOrder
import com.legend.modular_contract_sdk.repository.model.enum.McContractModeEnum
import com.legend.modular_contract_sdk.repository.model.enum.McContractOrderTypeEnum
import com.legend.modular_contract_sdk.repository.model.wrap.PositionWrap
import com.legend.modular_contract_sdk.ui.contract.vm.OpenPlanOrderViewModel
import com.legend.modular_contract_sdk.ui.contract.vm.SwapContractViewModel
import com.legend.modular_contract_sdk.utils.*
import com.legend.modular_contract_sdk.widget.gone
import com.legend.modular_contract_sdk.widget.visible
import com.orhanobut.logger.Logger
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 当前计划委托页面
 */
class OpenPlanOrderFragment : BaseFragment<OpenPlanOrderViewModel>() {

    lateinit var mBinding: McSdkFragmentOpenPlanOrderBinding
    lateinit var mHeaderBinding: McSdkItemOpenHeaderBinding
    private lateinit var mCurrentTradeUnit : QuantityUnit
    lateinit var mContractViewModel : SwapContractViewModel
    companion object {
        fun getInstance(): OpenPlanOrderFragment = OpenPlanOrderFragment()
    }

    override fun createViewModel() = ViewModelProvider(this).get(OpenPlanOrderViewModel::class.java)
    private val headerView by lazy {
        LayoutInflater.from(requireContext()).inflate(R.layout.mc_sdk_item_open_header, mBinding.recycerView, false)
    }

    private val adapter = object : BaseQuickAdapter<PositionAndOrder, BaseViewHolder>(R.layout.mc_sdk_item_open_plan_order) {

        override fun onItemViewHolderCreated(viewHolder: BaseViewHolder, viewType: Int) {
            super.onItemViewHolderCreated(viewHolder, viewType)
            val binding = McSdkItemOpenPlanOrderBinding.bind(viewHolder.itemView)
            binding.executePendingBindings()
        }

        override fun convert(helper: BaseViewHolder, item: PositionAndOrder) {
            helper.itemView.run {
                val pricePrecision = CoinwHyUtils.getPricePrecision(item.mInstrument)

                findViewById<TextView>(R.id.tvSymbol).text = "${item.mInstrument.toUpperCase()}/${McConstants.COMMON.PAIR_RIGHT_NAME} " +
                        context.getString(R.string.mc_sdk_contract_permanent)

                findViewById<TextView>(R.id.tvCondition).apply {
                    text = "${getString(R.string.mc_sdk_contract_latest_price)}${getSymbol(item)}" +
                            "${item.mTriggerPrice.toString().getNum(pricePrecision, true)}"
                    setTextColor(
                            if (item.mDirection == Direction.LONG.direction) ThemeUtil.getUpColor(requireContext())
                            else ThemeUtil.getDropColor(requireContext())
                    )
                }

                findViewById<TextView>(R.id.tvLeverage).text = "${String.format(getString(R.string.mc_sdk_contract_leverage_unit), item.mLeverage, "", "")}"
                refreshLabel(findViewById<TextView>(R.id.tvLabel), item)

                val positionWrap = PositionWrap(item)

                findViewById<TextView>(R.id.tvNumber).text = getString(R.string.mc_sdk_contract_order_num, positionWrap.getTradeUnitStr(mCurrentTradeUnit, requireContext()))
                findViewById<TextView>(R.id.tvNumberValue).text = positionWrap.getEntrustCount(mCurrentTradeUnit.unit)

                findViewById<TextView>(R.id.tvPriceValue).text = "${if (item.mTriggerType == 1) getString(R.string.mc_sdk_contract_market_price)
                else item.mOrderPrice.toString().getNum(pricePrecision, true)}"
                findViewById<TextView>(R.id.tvTime).text = getString(R.string.mc_sdk_contract_create_time) + " ${
                TimeUtils.millis2String(item.mCreatedDate)}"

                helper.setVisible(R.id.tv_experience_gold, item.mIsExperienceGold)

                findViewById<Button>(R.id.btnCancelOrder).setOnClickListener {
                    showMessageDialog(requireContext(), subtitle = resources.getString(R.string.mc_sdk_contract_sure_cancel_order)) {
                        cancelOrder(item.mInstrument, item.mId, item.mIsExperienceGold)
                    }

                }
            }
        }

        private fun getSymbol(item: PositionAndOrder): String {
            var price = item.mOrderPrice
            if (mContractViewModel.mIndexPriceLiveData.value != null){
                val indexPrice = mContractViewModel.mIndexPriceLiveData.value as Price
                price = indexPrice.p.getDouble()
            }
            return if (price >= item.mTriggerPrice) "≤" else "≥"
        }

        private fun refreshLabel(tvLabel: TextView, item: PositionAndOrder) {
            if (TextUtils.equals(item.mDirection, "long")) {
                if (item.mPositionModel == McContractModeEnum.WHOLE_WAREHOUSE.requestValue) {
                    tvLabel.text = getString(R.string.mc_sdk_full_long)
                } else {
                    tvLabel.text = getString(R.string.mc_sdk_part_long)
                }
                tvLabel.background = ThemeUtil.getThemeDrawable(requireContext(), R.attr.bg_buy_btn)
            } else {
                if (item.mPositionModel == McContractModeEnum.WHOLE_WAREHOUSE.requestValue) {
                    tvLabel.text = getString(R.string.mc_sdk_full_short)
                } else {
                    tvLabel.text = getString(R.string.mc_sdk_part_short)
                }
                tvLabel.background = ThemeUtil.getThemeDrawable(requireContext(), R.attr.bg_sell_btn)
            }

        }
    }

    override fun createRootView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        var rootView = inflater.inflate(R.layout.mc_sdk_fragment_open_plan_order, container, false)
        mBinding = McSdkFragmentOpenPlanOrderBinding.bind(rootView)
        mBinding.recycerView.layoutManager = LinearLayoutManager(requireContext())

        mContractViewModel = ViewModelProvider(requireActivity()).get(SwapContractViewModel::class.java)

        mHeaderBinding = McSdkItemOpenHeaderBinding.bind(headerView)
        adapter.addHeaderView(headerView)
        adapter.headerWithEmptyEnable = false
        setEmptyView()
        headerView.findViewById<TextView>(R.id.tvInfo).text =
                getString(R.string.mc_sdk_contract_order_info)
        headerView.findViewById<TextView>(R.id.tvLiquidateAll).text =
                getString(R.string.mc_sdk_contract_cancel_all)
        headerView.findViewById<TextView>(R.id.tvLiquidateAll).setOnClickListener {
            showMessageDialog(
                    requireContext(),
                    subtitle = resources.getString(R.string.mc_sdk_contract_sure_cancel_all)
            ) {
                cancelAllOrder()
            }
        }
        mBinding.recycerView.adapter = adapter
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initObserver()
        initTradeUnit()
    }


    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun initObserver() {
        getViewModel().mDataLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            adapter.setNewData(it as MutableList<PositionAndOrder>?)
        })
        getViewModel().mCancelOrderLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            getData()
        })
        getViewModel().mCancelAllOrderLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            getData()
        })

        mContractViewModel.mIndexPriceLiveData.observe(viewLifecycleOwner, Observer {
            adapter.notifyDataSetChanged()
        })
    }

    fun getData() {
        if (adapter == null) return
        if (!ModularContractSDK.userIsLogin()) {
            adapter.setNewData(null)
            return
        }
        getViewModel().fetchOrderList(McContractOrderTypeEnum.PLAN.requestValue, McConstants.COMMON.CURRENT_WAREHOUSE)
    }

    private fun cancelOrder(instrument: String, id: Long, isExperienceGold: Boolean) {
        if (!ModularContractSDK.userIsLogin()) return
        getViewModel().cancelOrder(instrument, id, isExperienceGold)
    }

    private fun cancelAllOrder() {
        if (!ModularContractSDK.userIsLogin()) return
        getViewModel().cancelAllOrder(McConstants.COMMON.CURRENT_WAREHOUSE, McContractOrderTypeEnum.PLAN.requestValue)
    }

    private fun setEmptyView() {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.mc_sdk_view_empty, mBinding.recycerView, false)
        val tvNoData = view.findViewById<TextView>(R.id.tvNoData)
        val tvNoLogin = view.findViewById<TextView>(R.id.tvNoLogin)
        if (adapter != null) {
            if (userIsLogin()) {
                tvNoData.visible()
                tvNoLogin.gone()
            } else {
                tvNoData.gone()
                tvNoLogin.visible()
            }
            adapter.setEmptyView(view)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loginEvent(event: LoginEvent) {
        getData()
        setEmptyView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshCurrentPlanEvent(event: McRefreshOrderList) {
        getData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun positionChange(event: McPositionFinishEvent) {
        getData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeTradeUnit(event: ChangeTradeUnitEvent){
        initTradeUnit()
        adapter.notifyDataSetChanged()
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

    override fun applyTheme() {
        mHeaderBinding.invalidateAll()
        adapter.notifyDataSetChanged()
    }
}