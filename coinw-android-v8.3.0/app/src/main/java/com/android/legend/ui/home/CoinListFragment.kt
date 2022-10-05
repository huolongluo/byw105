package com.android.legend.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.coinw.biz.event.BizEvent
import com.android.legend.model.home.TickerWrap
import com.legend.modular_contract_sdk.base.BaseFragment
import com.legend.modular_contract_sdk.component.adapter.DataBindingRecyclerViewAdapter
import com.umeng.analytics.MobclickAgent
import huolongluo.byw.BR
import huolongluo.byw.R
import huolongluo.byw.byw.bean.MarketListBean
import huolongluo.byw.byw.ui.activity.main.MainActivity
import huolongluo.byw.databinding.FragmentCoinListBinding
import huolongluo.byw.databinding.ItemHomeCoinMarketBinding
import huolongluo.byw.util.Constant
import huolongluo.byw.util.pricing.PricingMethodUtil
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class CoinListFragment : BaseFragment<HomeViewModel>() {

    companion object {

        val TYPE_MAIN = 1
        val TYPE_VOLUEM = 2
        val TYPE_NEW = 3
        val TYPE_UP = 4

        fun getInstance(showVolume: Boolean, type: Int): CoinListFragment =
                CoinListFragment().apply {
                    arguments = Bundle().apply {
                        putInt("type", type)
                        putBoolean("show_volume", showVolume)
                    }
                }
    }

    lateinit var mBinding: FragmentCoinListBinding

    lateinit var mAdapter: DataBindingRecyclerViewAdapter<TickerWrap>

    override fun createViewModel(): HomeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

    override fun createRootView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentCoinListBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        arguments?.let {
            val showVolume = it.getBoolean("show_volume", false)
            val type = it.getInt("type")

            if (showVolume) {
                mBinding.tvHomeRight.text = resources.getString(R.string.home_24h_turnover).toString() + "(" + PricingMethodUtil.getPricingSelectType() + ")"
            }

            mAdapter = DataBindingRecyclerViewAdapter(requireContext(), R.layout.item_home_coin_market, BR.ticker, mutableListOf())
            mBinding.rvList.adapter = mAdapter
            mAdapter.setOnItemClickListener { view1, position ->

                if (mAdapter == null) {
                    return@setOnItemClickListener
                }

                val ticker = mAdapter.getAllData().get(position)

                if (ticker != null) {
                    MobclickAgent.onEvent(getContext(), Constant.UMENG_EVENT_PRE_B + (position + 1), ticker.getBaseName())
                    MainActivity.self.gotoTrade(MarketListBean(ticker.getId(), ticker.getRealQuoteName(), ticker.getBaseName()))
                }

            }

            mAdapter.setOnBindingViewHolderListener { holder, position ->
                holder.getBinding<ItemHomeCoinMarketBinding>().showVolume = showVolume
            }

            when (type) {
                TYPE_MAIN -> {
                    mViewModel.mMainCoinListLiveData.observe(viewLifecycleOwner, Observer { pairList ->
                        mAdapter.refreshData(pairList.map { pair -> TickerWrap(pair.ticker) })
                    })
                }
                TYPE_NEW -> {
                    mViewModel.mNewCoinListLiveData.observe(viewLifecycleOwner, Observer { pairList ->
                        mAdapter.refreshData(pairList.map { pair -> TickerWrap(pair.ticker) })
                    })
                }
                TYPE_UP -> {
                    mViewModel.mUpCoinListLiveData.observe(viewLifecycleOwner, Observer { pairList ->
                        mAdapter.refreshData(pairList.map { pair -> TickerWrap(ticker2 = pair) })
                    })
                }
                TYPE_VOLUEM -> {
                    mViewModel.mVolumeCoinListLiveData.observe(viewLifecycleOwner, Observer { pairList ->
                        mAdapter.refreshData(pairList.map { pair -> TickerWrap(ticker2 = pair) })
                    })
                }
            }

        }
    }

    override fun applyTheme() {
        super.applyTheme()
        mBinding.invalidateAll()
        mAdapter.notifyDataSetChanged()
    }

    //本地计价方式切换需要马上刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshPricingMethod(event: BizEvent.ChangeExchangeRate?) {
        arguments?.let {
            val showVolume = it.getBoolean("show_volume", false)

            if (showVolume) {
                mBinding.tvHomeRight.text = resources.getString(R.string.home_24h_turnover).toString() + "(" + PricingMethodUtil.getPricingSelectType() + ")"
            }

            if (::mAdapter.isInitialized) {
                mAdapter.notifyDataSetChanged()
            }
        }
    }

}