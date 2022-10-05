package com.android.legend.ui.market

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.DisplayCompat
import androidx.core.view.GravityCompat
import androidx.core.view.marginTop
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.android.coinw.biz.event.BizEvent
import com.android.legend.base.BaseFragment
import com.android.legend.extension.formatStringByDigits
import com.android.legend.extension.gone
import com.android.legend.extension.visible
import com.android.legend.model.enumerate.market.MarketAreaIdEnum
import com.android.legend.model.market.MarketAreaBean
import com.android.legend.socketio.SocketIOClient
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.reflect.TypeToken
import com.legend.common.util.ThemeUtil
import com.legend.modular_contract_sdk.utils.ViewUtil
import huolongluo.byw.R
import huolongluo.byw.byw.bean.MarketListBean
import huolongluo.byw.byw.ui.activity.main.MainActivity
import huolongluo.byw.byw.ui.present.HotMoneyPresenter
import huolongluo.byw.byw.ui.present.MarketDataPresent
import huolongluo.byw.io.AppConstants
import huolongluo.byw.log.Logger
import huolongluo.byw.model.MarketResult
import huolongluo.byw.util.MathHelper
import huolongluo.byw.util.pricing.PricingMethodUtil
import huolongluo.byw.util.sp.SpUtils2
import kotlinx.android.synthetic.main.fragment_market_bb_side.*
import kotlinx.android.synthetic.main.fragment_market_bb_side.tabLayout
import kotlinx.android.synthetic.main.fragment_market_bb_side.viewPager
import kotlinx.android.synthetic.main.fragment_market_first.*
import kotlinx.android.synthetic.main.item_market_pair.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

//币币行情侧滑栏第一级，逻辑参考MarketFirstFragment
open class MarketBBSideFirstFragment : MarketFirstFragment() {
    private var isOpen = false
    private var bbAreaSecondList=arrayListOf<MarketAreaBean>()//侧滑栏币币的二级tab数据

    private var mSearchAdapter : BaseQuickAdapter<MarketListBean, BaseViewHolder>? = null

    companion object {
        fun newInstance() = MarketBBSideFirstFragment()
    }

    override fun isRegisterEventBus(): Boolean {
        return false
    }

    override fun getContentViewId(): Int {
        return R.layout.fragment_market_bb_side
    }

    override fun initView(view: View) {
        etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                MarketHelper.bbSideSearchTxt=s.toString()
                if (TextUtils.isEmpty(s.toString())) {
                    bbAreaSecondList.forEach { bean ->
                        bean.searchNum = 0
                    }

                    rv_search_result.visibility = View.GONE

                } else {

                    rv_search_result.visibility = View.VISIBLE

                    var filterList = MarketHelper.pairList.filter {
                        it.coinName.contains(s, ignoreCase = true) && !it.getfPartitionIds().contains(MarketHelper.ETF_AREA_ID)
                    }.toMutableList()

                    Logger.getInstance().error("filterList s: $s filterList Size:${filterList.size}")

                    mSearchAdapter?.setNewInstance(filterList)

//                    bbAreaSecondList.forEach { bean ->
//                        var num = 0
//                        for (marketListBean in MarketHelper.pairList) {
//                            if(bean.id==MarketAreaIdEnum.SECOND_BB.id) {//自选
//                                MarketDataPresent.listSelf.forEach { selfId->
//                                    if(selfId==marketListBean.id&&marketListBean.coinName.contains(s.toString(),ignoreCase = true)){
//                                        num++
//                                    }
//                                }
//                            } else {
//                                if (marketListBean.getfPartitionIds() == null || marketListBean.getfPartitionIds().isEmpty()) continue
//                                val ids = marketListBean.getfPartitionIds().split(",")
//                                if (ids != null && ids.isNotEmpty()) {
//                                    for (id in ids) {
//                                        if (id.toIntOrNull() ?: 0 == bean.id&&marketListBean.coinName.contains(s.toString(),ignoreCase = true)) {
//                                            num++
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        bean.searchNum = num
//                    }
                }
//                refreshSecondAdapter()
//                EventBus.getDefault().post(BizEvent.Market.RefreshBbSideSearchPairList())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        setSecondAdapter()
        setSearchRecyclerViewAdapter()
    }

    private fun setSearchRecyclerViewAdapter() {
        rv_search_result.adapter  =
                object : BaseQuickAdapter<MarketListBean, BaseViewHolder>(R.layout.item_market_pair) {
                    override fun convert(helper: BaseViewHolder, item: MarketListBean) {
                        helper.itemView.ll.setOnClickListener {
                            if(MarketHelper.isEtf()){
                                MainActivity.self.gotoETFTrade(item)
                            }else{
                                MainActivity.self.gotoTrade(item)
                                HotMoneyPresenter.collectHotData(null, "${item.id}")
                                EventBus.getDefault().post(BizEvent.SelectCurrentPage(0))
                            }
                            etContent.setText("")
                            MainActivity.self?.drawer_layout?.closeDrawer(GravityCompat.START)
                        }
                        helper.itemView.tvLeftCoinName.text = item.coinName
                        helper.itemView.tvRightCoinName.text = "/${item.cnyName}"
                        helper.itemView.tvVol.text = PricingMethodUtil.getLargePrice(item.oneDayTotal.toString(), 3)
                        MarketHelper.setTextLong(helper.itemView.tvPrice,item.latestDealPrice)
                        helper.itemView.tvPrice.text = item.latestDealPrice
                                ?: AppConstants.COMMON.DEFAULT_DISPLAY
                        helper.itemView.tvPricing.text = "≈" + PricingMethodUtil.getPricingUnit() + PricingMethodUtil.getResultByExchangeRate(
                                item.latestDealPrice, item.cnyName)
                        val rate = MathHelper.mul(100.0, item.priceRaiseRate).formatStringByDigits(2, true)
                        helper.itemView.tvRate.text = "$rate%"
                        if (item.priceRaiseRate > 0.0) {
                            helper.itemView.tvRate.background = ThemeUtil.getThemeDrawable(context, R.attr.bg_buy_btn)
                        } else {
                            helper.itemView.tvRate.background = ThemeUtil.getThemeDrawable(context, R.attr.bg_sell_btn)
                        }
                    }

                }.apply {

                    mSearchAdapter = this

                    val emptyText = TextView(context).apply {
                        layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                        textSize = 14f
                        text = getString(R.string.load_empty)
                        setTextColor(ThemeUtil.getThemeColor(context, R.attr.col_text_content))
                        gravity = Gravity.CENTER
                        setPadding(0, ViewUtil.dip2px(context, 200f), 0, 0)
                        val drawableTop = resources.getDrawable(R.mipmap.ic_self_no_data)
                        drawableTop.setBounds(0,0,drawableTop.intrinsicWidth, drawableTop.intrinsicHeight)
                        setCompoundDrawables(null, drawableTop, null, null)
                    }

                    setEmptyView(emptyText)
                }
    }

    override fun resume() {
        if (isOpen) {
            EventBus.getDefault().post(BizEvent.Market.RefreshSelfLocalList())
            subscribeSocket()
        }
    }

    open fun open() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        isOpen = true
        subscribeSocket()
        EventBus.getDefault().post(BizEvent.Market.RefreshSelfLocalList())
        if(MarketHelper.isBBLast){
            MainActivity.self?.drawer_layout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    fun close(){
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
        isOpen=false
        unSubscribeSocket()
    }

    override fun refreshAreaData() {
        viewModel.getMarketPair()
        bbAreaSecondList.clear()
        MarketHelper.areaFirstList.forEach { bean ->
            if (isEtf()) {
                if (bean.id == MarketAreaIdEnum.ETF.id) {
                    bean.tradeAreaVos.forEach { areaBean ->
                        bbAreaSecondList.add(areaBean)
                    }
                }
            } else {
                if (bean.id == MarketAreaIdEnum.SELF.id)//添加自选
                {
                    bean.tradeAreaVos.forEach { selfBean ->
                        if (selfBean.id == MarketAreaIdEnum.SECOND_BB.id) {
                            bbAreaSecondList.add(selfBean)
                        }
                    }

                }
                if (bean.id != MarketAreaIdEnum.CONTRACT.id && bean.id != MarketAreaIdEnum.SELF.id && bean.id != MarketAreaIdEnum.ETF.id) {//现货
                    bean.tradeAreaVos.forEach { areaBean ->
                        bbAreaSecondList.add(areaBean)
                    }
                }
            }
        }
        bbAreaSecondList = MarketHelper.ignoreList(bbAreaSecondList)

        secondFragments.clear()
        tabLayout.removeAllTabs()
        if (bbAreaSecondList.size == 0) {
            tabLayout.gone()
            secondFragments.add(getSecondFragment(0))
        } else {
            for (index in bbAreaSecondList.indices) {
                secondFragments.add(getSecondFragment(index))
            }
        }
        setSecondAdapter()
    }

    open fun getSecondFragment(index: Int): MarketBBSideSecondFragment {
        return MarketBBSideSecondFragment.newInstance(index,bbAreaSecondList)
    }

    open fun isEtf(): Boolean {
        return false
    }

    override fun setSecondAdapter() {
        secondAdapter = getAreaSecondAdapter()
        viewPager.adapter = secondAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (!isEtf()) {
                    MarketHelper.isSecondBBLast = position == secondFragments.size - 1
                }
            }
        })
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tabLayoutMediator(tab, position)
        }.attach()
    }

    fun refreshSecondAdapter() {
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tabLayoutMediator(tab, position)
        }.attach()
    }

    fun tabLayoutMediator(tab: TabLayout.Tab, position: Int) {
        if (bbAreaSecondList?.isEmpty() || bbAreaSecondList.size <= position) return
        val bean = bbAreaSecondList[position]
        val view = View.inflate(context, R.layout.item_market_second, null)
        val textView = view.findViewById<TextView>(R.id.tv)
        val flSearch = view.findViewById<FrameLayout>(R.id.flSearch)
        val tvSearch = view.findViewById<TextView>(R.id.tvSearch)
        flSearch.gone()
        if (!isEtf() && position == 0) {//自选
            textView.text = getString(R.string.dd14)
        } else {
            textView.text = bean.areaName
        }
        if (bean.searchNum > 0) {
            flSearch.visible()
            tvSearch.text = "${if (bean.searchNum > 99) 99 else bean.searchNum}"
        } else {
            flSearch.gone()
        }
        tab.customView = view
    }
}