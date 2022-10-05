package com.legend.modular_contract_sdk.ui.chart.depth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.base.BaseFragment
import com.legend.modular_contract_sdk.component.market_listener.Depth
import com.legend.modular_contract_sdk.component.market_listener.MarketData
import com.legend.modular_contract_sdk.component.market_listener.MarketListenerManager
import com.legend.modular_contract_sdk.component.market_listener.MarketSubscribeType
import com.legend.modular_contract_sdk.databinding.McSdkFragmentDepthBinding
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.widget.depthview.DepthFutureViewDelegate

class DepthFragment : BaseFragment<DepthViewModel>() {

    lateinit var mBinding: McSdkFragmentDepthBinding
    lateinit var mDepthDelegate: DepthFutureViewDelegate

    lateinit var mCurrentProduct: Product

    companion object {
        fun getInstance(product: Product) = DepthFragment().apply {
            var bundle = Bundle()
            bundle.putSerializable("product", product)
            arguments = bundle
        }
    }

    override fun createRootView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var rootView = inflater.inflate(R.layout.mc_sdk_fragment_depth, container, false)
        mBinding = McSdkFragmentDepthBinding.bind(rootView)
        initDepthView()
        return rootView
    }

    override fun createViewModel(): DepthViewModel =
        ViewModelProvider(this).get(DepthViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product = arguments?.let {
            it.getSerializable("product") as Product
        }
        changeProduct(product!!)
    }

    private fun initDepthView() {
        mDepthDelegate = DepthFutureViewDelegate(mBinding.viewDepth)
        mDepthDelegate.init()
        mDepthDelegate.setMaxSize(20)
    }
    private fun isDepthDelegateInitialized() = ::mDepthDelegate.isInitialized
    fun changeProduct(product: Product) {
        if(!isDepthDelegateInitialized()){//umeng此处会保not initialized
            initDepthView()
        }
        mCurrentProduct = product
        removeAllMarketListener()

        mDepthDelegate.setLotSize(mCurrentProduct.mOneLotSize, 0, mCurrentProduct.mPricePrecision)

        mMarketListenerList.add(
            MarketListenerManager.subscribe(
                MarketSubscribeType.Depth(mCurrentProduct.mBase, "usd"),
                getViewModel().mDepthLiveData
            )
        )

        // todo 深度多次订阅数量叠加,暂时只能判断是否订阅解决。 修改了 depth.m 之后会影响其他订阅

        val observer = Observer<MarketData> {
            val depth = it as Depth
            mDepthDelegate.setData(depth)
        }

        if (!getViewModel().mDepthLiveData.hasObservers()) {
            getViewModel().mDepthLiveData.observe(this@DepthFragment, observer)
        }


    }

    override fun applyTheme() {
        super.applyTheme()
        mDepthDelegate.resetPaint()
    }
}