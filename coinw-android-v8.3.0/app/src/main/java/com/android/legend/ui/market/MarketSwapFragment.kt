package com.android.legend.ui.market

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.legend.extension.formatStringByDigits
import com.android.legend.extension.gone
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.legend.common.util.ThemeUtil
import com.legend.modular_contract_sdk.api.ProductTickerProvider.addTickerListener
import com.legend.modular_contract_sdk.api.ProductTickerProvider.mMarketListenerList
import com.legend.modular_contract_sdk.api.ProductTickerProvider.mProductCallback
import com.legend.modular_contract_sdk.api.ProductTickerProvider.mProductList
import com.legend.modular_contract_sdk.api.ProductTickerProvider.mTickerCallback
import com.legend.modular_contract_sdk.api.ProductTickerProvider.start
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils.Companion.removeAllMarketListener
import com.legend.modular_contract_sdk.common.event.ChangeProductEvent
import com.legend.modular_contract_sdk.component.market_listener.Ticker
import com.legend.modular_contract_sdk.repository.model.Product
import huolongluo.byw.R
import huolongluo.byw.byw.ui.activity.main.MainActivity
import huolongluo.byw.io.AppConstants
import huolongluo.byw.log.Logger
import huolongluo.byw.util.GsonUtil
import huolongluo.byw.util.MathHelper
import huolongluo.byw.util.pricing.PricingMethodUtil
import kotlinx.android.synthetic.main.fragment_market_first.*
import kotlinx.android.synthetic.main.fragment_market_first.rv
import kotlinx.android.synthetic.main.fragment_market_third.*
import kotlinx.android.synthetic.main.item_market_pair.view.*
import org.greenrobot.eventbus.EventBus
import java.util.*

class MarketSwapFragment :MarketThirdFragment(){
    private val products= mutableListOf<Product>()
    private val swapAdapter by lazy { getSwapAdapters() }

    companion object {
        fun newInstance(secondPosition: Int, thirdPosition: Int):MarketSwapFragment {
            val fragment=MarketSwapFragment()
            val bundle= Bundle()
            bundle.putInt("secondPosition", secondPosition)
            bundle.putInt("thirdPosition", thirdPosition)
            fragment.arguments= bundle
            return fragment
        }
    }

    override fun initView(view: View) {
        ivSortMarket.gone()
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        (rv.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        rv.adapter = swapAdapter

        swipeRefresh.setColorSchemeResources(R.color.accent_main)
        swipeRefresh.setOnRefreshListener {
            products.clear()
            removeSocketListener()
            start()
        }

        llPrice.setOnClickListener {
            if(products==null||products.isEmpty()) return@setOnClickListener
            if(sortClickPosition!=1) sortType=0
            sortClickPosition=1
            when(sortType){
                0 -> {
                    restoreSortIv()
                    sortType = 1
                    ivSortPrice.setImageResource(R.mipmap.market_sort_1)
                    products.sortByDescending { it.mLast?.toDouble() }
                    swapAdapter.setList(products)
                    swapAdapter.notifyDataSetChanged()
                }
                1 -> {
                    restoreSortIv()
                    sortType = 2
                    ivSortPrice.setImageResource(R.mipmap.market_sort_2)
                    products.sortBy { it.mLast?.toDouble() }
                    swapAdapter.setList(products)
                    swapAdapter.notifyDataSetChanged()
                }
                2 -> {
                    restoreSortIv()
                    sortType = 0
                    ivSortPrice.setImageResource(R.mipmap.market_sort_0)
                    products.sortBy { it.mSort }
                    swapAdapter.setList(products)
                    swapAdapter.notifyDataSetChanged()
                }
            }
        }
        llRate.setOnClickListener {
            if(products==null||products.isEmpty()) return@setOnClickListener
            if(sortClickPosition!=2) sortType=0
            sortClickPosition=2
            when(sortType){
                0 -> {
                    restoreSortIv()
                    sortType = 1
                    ivSortRate.setImageResource(R.mipmap.market_sort_1)
                    products.sortByDescending { it.mChangeRate }
                    swapAdapter.setList(products)
                    swapAdapter.notifyDataSetChanged()
                }
                1 -> {
                    restoreSortIv()
                    sortType = 2
                    ivSortRate.setImageResource(R.mipmap.market_sort_2)
                    products.sortBy { it.mChangeRate }
                    swapAdapter.setList(products)
                    swapAdapter.notifyDataSetChanged()
                }
                2 -> {
                    restoreSortIv()
                    sortType = 0
                    ivSortRate.setImageResource(R.mipmap.market_sort_0)
                    products.sortBy { it.mSort }
                    swapAdapter.setList(products)
                    swapAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun initData() {
        mTickerCallback = { ticker: Ticker ->
            Logger.getInstance().debug(TAG, "setMTickerCallback ticker:" + GsonUtil.obj2Json(ticker, Ticker::class.java))
            for (i in products.indices) {
                if (products[i].mBase.equals(ticker.currencyCode, ignoreCase = true)) {
                    products[i].mLast = ticker.last
                    products[i].mChangeRate = ticker.changeRate
                    swapAdapter.notifyItemChanged(i)
                    break
                }
            }
        }
        mProductCallback = label@{ list: MutableList<Product> ->
            if(swipeRefresh.isRefreshing){
                swipeRefresh.isRefreshing=false
            }
            Logger.getInstance().debug(TAG, "setMProductCallback 获取合约所有交易对成功")
            if (list == null || list.size == 0) return@label
            Logger.getInstance().debug(TAG, "getMProductsLiveData products:" + GsonUtil.obj2Json(list, MutableList::class.java))
            setAdapterData(list)
        }
    }
    override fun onResume() {
        super.onResume()
        getData()
    }
    override fun onPause() {
        super.onPause()
        removeSocketListener()
    }
    private fun getData(){
        removeSocketListener()
        if (mProductList == null || mProductList!!.size == 0) { //读取交易对接口后不用再读取
            start()
        } else {
            if (swapAdapter.data == null || swapAdapter.data.size == 0) { //getMProductList有时会有数据但adapter还未设置数据
                setAdapterData(mProductList!!)
            }
            addTickerListener()
        }
    }
    private fun setAdapterData(list: MutableList<Product>) {
        if (products == null || swapAdapter == null) return
        products.clear()
        products.addAll(list)
        swapAdapter.setNewInstance(products)
        swapAdapter.notifyDataSetChanged()
    }
    private fun removeSocketListener() {
        removeAllMarketListener(mMarketListenerList)
    }

    private fun getSwapAdapters(): BaseQuickAdapter<Product, BaseViewHolder> =
            object : BaseQuickAdapter<Product, BaseViewHolder>(R.layout.item_market_pair) {

                override fun convert(helper: BaseViewHolder, item: Product) {
                    helper.itemView.ll.setOnClickListener {
                        EventBus.getDefault().post(ChangeProductEvent(item))
                        MainActivity.self.gotoSwapForContractId(0)
                    }
                    helper.itemView.tvLeftCoinName.text=item.mBase.toUpperCase()
                    helper.itemView.tvRightCoinName.text="/${item.mQuote.toUpperCase()}"
                    helper.itemView.llVol.gone()
                    helper.itemView.tvPrice.text=item.mLast?: AppConstants.COMMON.DEFAULT_DISPLAY
                    helper.itemView.tvPricing.text= "≈${PricingMethodUtil.getPricingUnit()}${
                        PricingMethodUtil.getResultByExchangeRate(item.mLast, AppConstants.COMMON.USDT)}"
                    val rate= MathHelper.mul("100", item.mChangeRate).formatStringByDigits(2, true)
                    helper.itemView.tvRate.text="$rate%"
                    if (rate.toDoubleOrNull()?:0.0>0.0){
                        helper.itemView.tvRate.background= ThemeUtil.getThemeDrawable(context,R.attr.bg_buy_btn)
                    }else{
                        helper.itemView.tvRate.background= ThemeUtil.getThemeDrawable(context,R.attr.bg_sell_btn)
                    }

                }

            }
}