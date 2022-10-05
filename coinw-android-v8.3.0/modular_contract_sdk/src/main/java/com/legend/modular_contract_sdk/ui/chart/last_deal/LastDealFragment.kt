package com.legend.modular_contract_sdk.ui.chart.last_deal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.legend.modular_contract_sdk.BR
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.base.BaseFragment
import com.legend.modular_contract_sdk.component.adapter.DataBindingRecyclerViewAdapter
import com.legend.modular_contract_sdk.component.market_listener.LastDealList
import com.legend.modular_contract_sdk.component.market_listener.MarketListener
import com.legend.modular_contract_sdk.component.market_listener.MarketListenerManager
import com.legend.modular_contract_sdk.component.market_listener.MarketSubscribeType
import com.legend.modular_contract_sdk.databinding.McSdkFragmentLastDealBinding
import com.legend.modular_contract_sdk.repository.model.LastDeal
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.repository.model.wrap.LastDealWrap
import com.orhanobut.logger.Logger

// 最新成交 实时成交
class LastDealFragment : BaseFragment<LastDealViewModel>(){

    companion object{
        fun getInstance(product: Product) = LastDealFragment().apply {
            var bundle = Bundle()
            bundle.putSerializable("product", product)
            arguments = bundle
        }
    }

    lateinit var mBinding: McSdkFragmentLastDealBinding

    lateinit var mAdapter: DataBindingRecyclerViewAdapter<LastDealWrap>

    lateinit var mCurrentProduct: Product

    var mLastDealList = mutableListOf<LastDeal>()

    val MAX_NUM = 20 // 最多展示多少条

    override fun createViewModel() = ViewModelProvider(this).get(LastDealViewModel::class.java)

    override fun createRootView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = McSdkFragmentLastDealBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments == null){
            return
        }
        mCurrentProduct = requireArguments().let {
            it.getSerializable("product") as Product
        }
        initView()
        initData()
        changeProduct(mCurrentProduct)
    }

    private fun initView() {
        mAdapter = DataBindingRecyclerViewAdapter(context, R.layout.mc_sdk_item_last_deal, BR.lastDeal, mutableListOf())
        mBinding.rvList.adapter = mAdapter
    }

    private fun initData() {
        mViewModel.mLastDealLiveData.observe(this, Observer {
            val lastDealList = it as LastDealList

            mLastDealList.addAll(0, lastDealList.dataList)

            if (mLastDealList.size > MAX_NUM){
                mLastDealList = mLastDealList.subList(0, MAX_NUM)
            }

            mAdapter.refreshData(mLastDealList.map {
                LastDealWrap(it, mCurrentProduct)
            })

        })
    }

    fun changeProduct(product: Product){
        mCurrentProduct = product
        removeAllMarketListener()
        mLastDealList.clear()
        mAdapter.cleanData()
        addLastDealListener()

        mBinding.tvAmount.text = getString(R.string.mc_sdk_amount_depth_title, product.mBase.toUpperCase())
    }

    private fun addLastDealListener() {
        mMarketListenerList.add(
                MarketListenerManager.subscribe(MarketSubscribeType.LastDeal(mCurrentProduct.mBase, "usdt"), mViewModel.mLastDealLiveData)
        )
    }




}