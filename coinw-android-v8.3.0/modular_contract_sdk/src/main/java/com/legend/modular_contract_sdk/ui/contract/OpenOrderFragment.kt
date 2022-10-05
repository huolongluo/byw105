package com.legend.modular_contract_sdk.ui.contract

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.BR
import com.legend.modular_contract_sdk.api.ModularContractSDK.userIsLogin
import com.legend.modular_contract_sdk.base.BaseFragment
import com.legend.modular_contract_sdk.common.event.ChangeTradeUnitEvent
import com.legend.modular_contract_sdk.common.event.LoginEvent
import com.legend.modular_contract_sdk.common.event.McPositionFinishEvent
import com.legend.modular_contract_sdk.common.event.McRefreshOrderList
import com.legend.modular_contract_sdk.common.showMessageDialog
import com.legend.modular_contract_sdk.common.showModifyStopProfitAndLoss
import com.legend.modular_contract_sdk.component.adapter.DataBindingRecyclerViewAdapter
import com.legend.modular_contract_sdk.databinding.McSdkFragmentOpenOrderBinding
import com.legend.modular_contract_sdk.databinding.McSdkItemMoveTpSlOrderBinding
import com.legend.modular_contract_sdk.databinding.McSdkItemOpenHeaderBinding
import com.legend.modular_contract_sdk.databinding.McSdkItemOpenOrderBinding
import com.legend.modular_contract_sdk.repository.model.PositionAndOrder
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.repository.model.enum.McContractOrderTypeEnum
import com.legend.modular_contract_sdk.repository.model.wrap.OrderWrap
import com.legend.modular_contract_sdk.ui.contract.vm.OpenOrderViewModel
import com.legend.modular_contract_sdk.ui.contract.vm.SwapContractViewModel
import com.legend.modular_contract_sdk.utils.McConstants
import com.legend.modular_contract_sdk.utils.SPUtils
import com.legend.modular_contract_sdk.widget.gone
import com.legend.modular_contract_sdk.widget.visible
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 当前委托页面
 */
class OpenOrderFragment : BaseFragment<OpenOrderViewModel>() {

    lateinit var mBinding: McSdkFragmentOpenOrderBinding
    lateinit var mHeaderBinding: McSdkItemOpenHeaderBinding
    private lateinit var mCurrentTradeUnit : QuantityUnit

    companion object {
        fun getInstance(): OpenOrderFragment = OpenOrderFragment()
    }

    override fun createViewModel() = ViewModelProvider(this).get(OpenOrderViewModel::class.java)
    private val headerView by lazy {
        LayoutInflater.from(requireContext()).inflate(R.layout.mc_sdk_item_open_header, mBinding.recycerView, false)
    }

    private lateinit var mAdapter : DataBindingRecyclerViewAdapter<OrderWrap>

    override fun createRootView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var rootView = inflater.inflate(R.layout.mc_sdk_fragment_open_order, container, false)
        mBinding = McSdkFragmentOpenOrderBinding.bind(rootView)
        mBinding.recycerView.layoutManager = LinearLayoutManager(requireContext())
        mHeaderBinding = McSdkItemOpenHeaderBinding.bind(headerView)

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

        initAdapter()
        mBinding.recycerView.adapter = mAdapter
        setEmptyView()
        return mBinding.root
    }

    private fun initAdapter() {
        mAdapter = DataBindingRecyclerViewAdapter(context, R.layout.mc_sdk_item_open_order, BR.order, mutableListOf())

        mAdapter.setOnBindingViewHolderListener { holder, position ->
            val binding = holder.getBinding<ViewDataBinding>()
            if (binding is McSdkItemOpenOrderBinding) {
                binding.apply {
                    llStopProfit.setOnClickListener {
                        showTP_SL(order!!.order)
                    }

                    btnCancelOrder.setOnClickListener {
                        showMessageDialog(
                                requireContext(),
                                subtitle = resources.getString(R.string.mc_sdk_contract_sure_cancel_order)
                        ) {
                            cancelOrder(order!!.order.mInstrument, order!!.order.mId, order!!.order.mIsExperienceGold)
                        }
                    }
                }
            } else if (binding is McSdkItemMoveTpSlOrderBinding){
                binding.apply {
                    btnCancelOrder.setOnClickListener {
                        showMessageDialog(
                                requireContext(),
                                subtitle = resources.getString(R.string.mc_sdk_contract_sure_cancel_order)
                        ) {
                            cancelOrder(order!!.order.mInstrument, order!!.order.mId, order!!.order.mIsExperienceGold)
                        }
                    }
                }
            }

        }

        val viewTypeMoveTPSL = 1001

        val viewTypeMap = mutableMapOf<Int, Int>()
        viewTypeMap[viewTypeMoveTPSL] = R.layout.mc_sdk_item_move_tp_sl_order

        mAdapter.setViewTypeProvider(viewTypeMap) viewType@{ position ->
            if (mAdapter.allData[position].isMoveTPSLOrder()) {
                return@viewType viewTypeMoveTPSL
            } else {
                return@viewType DataBindingRecyclerViewAdapter.TYPE_NORMAL
            }
        }

        mAdapter.addHeaderView(headerView)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initObserver()
        initTradeUnit()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            getData()
        }
    }

    private fun initObserver() {
        getViewModel().mDataLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            mAdapter.refreshData(it.map {order -> OrderWrap(order) })
        })
        getViewModel().mCancelOrderLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            getData()
        })
        getViewModel().mCancelAllOrderLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            getData()
        })
    }

    private fun getData() {
        if (mAdapter == null) return
        if (!userIsLogin()) {
            mAdapter.cleanData()
            return
        }
        getViewModel().fetchOrderList(McContractOrderTypeEnum.LIMIT.requestValue, McConstants.COMMON.CURRENT_WAREHOUSE)
    }

    private fun cancelOrder(instrument: String, id: Long, isExperienceGold: Boolean) {
        if (!userIsLogin()) return
        getViewModel().cancelOrder(instrument, id, isExperienceGold)
    }

    private fun cancelAllOrder() {
        if (!userIsLogin()) return
        getViewModel().cancelAllOrder(McConstants.COMMON.CURRENT_WAREHOUSE, McContractOrderTypeEnum.LIMIT.requestValue)
    }

    private fun showTP_SL(item: PositionAndOrder) {

        var product: Product? = null

        val viewModel = ViewModelProvider(requireActivity()).get(SwapContractViewModel::class.java)

        viewModel.mProductsLiveData.value!!.forEach {
            if (item.mInstrument.equals(it.mBase, true)) {
                product = it
                return@forEach
            }
        }

        product?.let {
            showModifyStopProfitAndLoss(requireContext(), this@OpenOrderFragment, item, it)
        }


    }

    private fun setEmptyView() {
        val view =
            LayoutInflater.from(requireContext()).inflate(R.layout.mc_sdk_view_empty, mBinding.recycerView, false)
        val tvNoData = view.findViewById<TextView>(R.id.tvNoData)
        val tvNoLogin = view.findViewById<TextView>(R.id.tvNoLogin)
        if (mAdapter != null) {
            if (userIsLogin()) {
                tvNoData.visible()
                tvNoLogin.gone()
            } else {
                tvNoData.gone()
                tvNoLogin.visible()
            }
            mAdapter.setEmptyView(view)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loginEvent(event: LoginEvent) {
        getData()
        setEmptyView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshCurrentOrderEvent(event: McRefreshOrderList) {
        getData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun positionChange(event: McPositionFinishEvent) {//socket接收其他端列表的变化即时刷新当前列表
        getData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeTradeUnit(event: ChangeTradeUnitEvent){
        initTradeUnit()
        mAdapter.notifyDataSetChanged()
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
        mAdapter.notifyDataSetChanged()
    }
}