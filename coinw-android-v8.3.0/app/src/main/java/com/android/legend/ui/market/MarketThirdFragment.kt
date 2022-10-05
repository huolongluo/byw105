package com.android.legend.ui.market

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.coinw.biz.event.BizEvent
import com.android.coinw.biz.event.BizEvent.SelectCurrentPage
import com.android.legend.base.BaseFragment
import com.android.legend.extension.formatStringByDigits
import com.android.legend.extension.gone
import com.android.legend.extension.visible
import com.android.legend.model.enumerate.market.MarketAreaIdEnum
import com.android.legend.model.market.MarketAreaBean
import com.android.legend.socketio.SocketIOClient.subscribe
import com.android.legend.view.popupwindow.HorizontalPosition
import com.android.legend.view.popupwindow.SmartPopupWindow
import com.android.legend.view.popupwindow.VerticalPosition
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.reflect.TypeToken
import com.legend.common.util.ThemeUtil
import huolongluo.byw.R
import huolongluo.byw.byw.bean.MarketListBean
import huolongluo.byw.byw.ui.activity.main.MainActivity
import huolongluo.byw.byw.ui.present.HotMoneyPresenter
import huolongluo.byw.byw.ui.present.MarketDataPresent
import huolongluo.byw.io.AppConstants
import huolongluo.byw.log.Logger
import huolongluo.byw.model.MarketResult
import huolongluo.byw.util.MathHelper
import huolongluo.byw.util.Util
import huolongluo.byw.util.pricing.PricingMethodUtil
import huolongluo.byw.util.sp.SpUtils2
import huolongluo.byw.util.tip.SnackBarUtils
import kotlinx.android.synthetic.main.fragment_market_first.rv
import kotlinx.android.synthetic.main.fragment_market_third.*
import kotlinx.android.synthetic.main.item_market_pair.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * zh行情第三级币对列表页面
 */
open class MarketThirdFragment : BaseFragment() {
    val secondPosition by lazy { requireArguments().getInt("secondPosition", 0) }
    private val thirdPosition by lazy { requireArguments().getInt("thirdPosition", 0) }//本fragment在tab内的位置
    var currentAreaPairList = mutableListOf<MarketListBean>()//当前分区的币对列表
    val adapter by lazy { getPairAdapter() }
    var sortType = 0 //排序规则 0正常，1:大到小  2：小到大
    var sortClickPosition = 0 //排序点击的位置
    var isVisibleToUser=true//行情不可见取消eventbus的监听

    companion object {
        fun newInstance(secondPosition: Int, thirdPosition: Int): MarketThirdFragment {
            val fragment = MarketThirdFragment()
            val bundle = Bundle()
            bundle.putInt("secondPosition", secondPosition)
            bundle.putInt("thirdPosition", thirdPosition)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getContentViewId(): Int {
        return R.layout.fragment_market_third
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun initView(view: View) {
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = adapter

        swipeRefresh.setColorSchemeResources(R.color.accent_main)
        swipeRefresh.setOnRefreshListener {
            EventBus.getDefault().post(BizEvent.Market.GetMarketPair())
        }

        llMarket.setOnClickListener {
            if (currentAreaPairList.isEmpty()) return@setOnClickListener
            if (sortClickPosition != 0) sortType = 0
            sortClickPosition = 0
            sortClick()
        }
        llPrice.setOnClickListener {
            if (currentAreaPairList.isEmpty()) return@setOnClickListener
            if (sortClickPosition != 1) sortType = 0
            sortClickPosition = 1
            sortClick()
        }
        llRate.setOnClickListener {
            if (currentAreaPairList.isEmpty()) return@setOnClickListener
            if (sortClickPosition != 2) sortType = 0
            sortClickPosition = 2
            sortClick()
        }
    }

    override fun initData() {
        try {
            if (isSelf()) {
                refreshSelfList()
            } else {
                refreshData()
            }
        }catch (t:Throwable){

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun sortList() {//socket和http返回数据检索
        if (sortType == 0) return  //没有检索不做操作
        when (sortType) {
            1 -> {
                when (sortClickPosition) {
                    0 -> {
                        currentAreaPairList.sortByDescending { it.oneDayTotal }
                    }
                    1 -> {
                        currentAreaPairList.sortByDescending { it.latestDealPrice }
                    }
                    2 -> {
                        currentAreaPairList.sortByDescending { it.priceRaiseRate }
                    }
                }
            }
            2 -> {
                when (sortClickPosition) {
                    0 -> {
                        currentAreaPairList.sortBy { it.oneDayTotal }
                    }
                    1 -> {
                        currentAreaPairList.sortBy { it.latestDealPrice }
                    }
                    2 -> {
                        currentAreaPairList.sortBy { it.priceRaiseRate }
                    }
                }
            }
        }

    }

    private fun sortClick() {//点击检索
        when (sortType) {
            0 -> {
                restoreSortIv()
                sortType = 1
                when (sortClickPosition) {
                    0 -> {
                        ivSortMarket.setImageResource(R.mipmap.market_sort_1)
                        currentAreaPairList.sortByDescending { it.oneDayTotal }
                    }
                    1 -> {
                        ivSortPrice.setImageResource(R.mipmap.market_sort_1)
                        currentAreaPairList.sortByDescending { it.latestDealPrice }
                    }
                    2 -> {
                        ivSortRate.setImageResource(R.mipmap.market_sort_1)
                        currentAreaPairList.sortByDescending { it.priceRaiseRate }
                    }
                }

                adapter.setList(currentAreaPairList)
                adapter.notifyDataSetChanged()
            }
            1 -> {
                restoreSortIv()
                sortType = 2
                when (sortClickPosition) {
                    0 -> {
                        ivSortMarket.setImageResource(R.mipmap.market_sort_2)
                        currentAreaPairList.sortBy { it.oneDayTotal }
                    }
                    1 -> {
                        ivSortPrice.setImageResource(R.mipmap.market_sort_2)
                        currentAreaPairList.sortBy { it.latestDealPrice }
                    }
                    2 -> {
                        ivSortRate.setImageResource(R.mipmap.market_sort_2)
                        currentAreaPairList.sortBy { it.priceRaiseRate }
                    }
                }
                adapter.setList(currentAreaPairList)
                adapter.notifyDataSetChanged()
            }
            2 -> {
                restoreSortIv()
                sortType = 0
                when (sortClickPosition) {
                    0 -> {
                        ivSortMarket.setImageResource(R.mipmap.market_sort_0)
                    }
                    1 -> {
                        ivSortPrice.setImageResource(R.mipmap.market_sort_0)
                    }
                    2 -> {
                        ivSortRate.setImageResource(R.mipmap.market_sort_0)
                    }
                }
                refreshData()
            }
        }
    }

    fun restoreSortIv() {
        sortType = 0
        ivSortMarket.setImageResource(R.mipmap.market_sort_0)
        ivSortPrice.setImageResource(R.mipmap.market_sort_0)
        ivSortRate.setImageResource(R.mipmap.market_sort_0)
    }

    fun refreshData() {
        if(isSelf()) return
        if (MarketHelper.pairList.isEmpty()) {//fragment创建时为空
            val l = SpUtils2.getObject(context, AppConstants.SP_KEY.MARKET_PAIR, object : TypeToken<ArrayList<MarketListBean>>() {}.type) as ArrayList<MarketListBean>?
            if (l != null) {
                MarketHelper.addPairList(l)
            }
        }
        if (adapter != null&& MarketHelper.pairList.isNotEmpty()) {
            currentAreaPairList.clear()//当前分区下的币对列表

            val areaSecondList=getAreaSecondList()
            val currentId = if (areaSecondList.size > secondPosition) {//2级有分区
                areaSecondList[secondPosition].id
            } else {//2级没有分区，但3级有分区
                getFirstId()
            }
            for (marketListBean in MarketHelper.pairList) {
                if (marketListBean.getfPartitionIds() == null || marketListBean.getfPartitionIds().isEmpty()) continue
                val ids = marketListBean.getfPartitionIds().split(",")
                if (ids != null && ids.isNotEmpty()) {
                    for (id in ids) {
                        if (id.toIntOrNull() ?: 0 == currentId) {
                            if (areaSecondList.isNotEmpty() && TextUtils.equals(marketListBean.cnyName,
                                            MarketHelper.getAreas(areaSecondList[secondPosition].areaCoinsStr)?.get(thirdPosition))) {
                                currentAreaPairList.add(marketListBean)
                            } else if (areaSecondList.isEmpty() && TextUtils.equals(marketListBean.cnyName,
                                            getFirstAreaCoinStr()?.get(thirdPosition))) {
                                currentAreaPairList.add(marketListBean)
                            }
                            break
                        }
                    }
                }
            }
            sortList()
            ignoreCurrentAreaPairList()
            adapter.setList(currentAreaPairList)
            adapter.notifyDataSetChanged()
        }
    }
    //获取对应的第一级的id字段，用于第二级为空显示第三级 币对id和第一级的id匹配
    open fun getFirstId():Int{
        if(MarketHelper.areaFirstList.isEmpty()||MarketHelper.areaFirstList.size<=MarketHelper.firstCurrentClickPosition) return 0
        return MarketHelper.areaFirstList[MarketHelper.firstCurrentClickPosition].id
    }
    open fun getAreaSecondList():ArrayList<MarketAreaBean>{
        return MarketHelper.getAreaSecondList()
    }
    open fun ignoreCurrentAreaPairList(){

    }
    //获取对应的第一级的areaCoinsStr字段，用于第二级为空显示第三级 firstPosition: 行情为点击的位置，币币侧滑栏不使用，etf为行情etf位置
    open fun getFirstAreaCoinStr():List<String>?{
        if(MarketHelper.areaFirstList.isEmpty()||MarketHelper.areaFirstList.size<=MarketHelper.firstCurrentClickPosition) return null
        return MarketHelper.getAreas(MarketHelper.areaFirstList[MarketHelper.firstCurrentClickPosition].areaCoinsStr)
    }
    private fun refreshNoData() {
        if (!isSelf()) return
        if (currentAreaPairList.size == 0) {
            tvNoData.visible()
        } else {
            tvNoData.gone()
        }
    }

    open fun isSelf() = MarketHelper.isSelf()
    open fun isMarket() = true //只有行情需要屏蔽eventbus,侧滑栏不需要
    private fun refreshSelfList() {
        if (!isSelf()) {
            return
        }
        currentAreaPairList.clear()
        MarketDataPresent.listSelf.forEach { id ->
            for (marketListBean in MarketHelper.pairList) {
                if (marketListBean.id == null) continue
                if (marketListBean.id == id) {
                    val areaSecondList=getAreaSecondList()
                    if (areaSecondList.isNotEmpty() && TextUtils.equals(marketListBean.cnyName,
                                    MarketHelper.getAreas(areaSecondList[secondPosition].areaCoinsStr)?.get(thirdPosition))) {

                        currentAreaPairList.add(marketListBean)
                    } else if (areaSecondList.isEmpty() && TextUtils.equals(marketListBean.cnyName,
                                    getFirstAreaCoinStr()?.get(thirdPosition))) {
                        currentAreaPairList.add(marketListBean)
                    }
                }
            }
        }

        if (adapter != null) {
            sortList()
            refreshNoData()
            ignoreCurrentAreaPairList()
            adapter.setList(currentAreaPairList)
            adapter.notifyDataSetChanged()
        }
    }

    open fun getPairAdapter(): BaseQuickAdapter<MarketListBean, BaseViewHolder> =
            object : BaseQuickAdapter<MarketListBean, BaseViewHolder>(R.layout.item_market_pair) {
                override fun convert(helper: BaseViewHolder, item: MarketListBean) {
                    helper.itemView.ll.setOnClickListener {
                        if(MarketHelper.isEtf()){
                            MainActivity.self.gotoETFTrade(item)
                        }else{
                            MainActivity.self.gotoTrade(item)
                            HotMoneyPresenter.collectHotData(null, "${item.id}")
                            EventBus.getDefault().post(SelectCurrentPage(0))
                        }
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
                    if(item.isSelective){
                        helper.itemView.tvSelect.visible()
                        helper.itemView.tvSelect.setOnClickListener {
                            val pop=SmartPopupWindow.Builder.build(requireActivity(),
                            LayoutInflater.from(requireContext()).inflate(R.layout.pop_new_coin_select,null,false)).createPopupWindow()
                            pop.showAtAnchorView(helper.itemView.tvSelect,VerticalPosition.CENTER,
                                    HorizontalPosition.RIGHT,false)//fitLnScreen为true会因为超出屏幕位置计算错误乱跳
                        }
                    }else{
                        helper.itemView.tvSelect.gone()
                    }
                }

            }

    private fun getSelfLocalList() {
        if (isSelf()) {
            MarketDataPresent.getSelfList()
        }
    }
    private fun isEventBusEnable():Boolean{
        return if(isMarket()){
            isVisibleToUser
        }else{
            true
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshData(data: BizEvent.Market.RefreshPairData) {//币对接口获取后刷新数据
        if(!isEventBusEnable()) return
        if (swipeRefresh.isRefreshing) {
            swipeRefresh.isRefreshing = false
            if (data.isHttpSuccess) {
                SnackBarUtils.ShowBlue(activity, getString(R.string.cc63))
            } else {
                SnackBarUtils.ShowRed(activity, getString(R.string.cc64))
            }
        }
        if (isSelf()) {
            refreshSelfList()
        } else {
            refreshData()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshSelfList(event: BizEvent.Market.RefreshSelfList) {
       if(!isEventBusEnable()) return
        refreshSelfList()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshSelfLocalList(event: BizEvent.Market.RefreshSelfLocalList) {
        if(!isEventBusEnable()) return
        getSelfLocalList()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun isVisibleToUser(event: BizEvent.Market.IsVisibleToUser) {
        this.isVisibleToUser=event.isVisibleToUser
    }
}