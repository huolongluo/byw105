package com.android.legend.ui.market

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.coinw.biz.event.BizEvent
import com.android.legend.base.BaseFragment
import com.android.legend.extension.gone
import com.android.legend.extension.visible
import com.android.legend.model.enumerate.market.MarketAreaIdEnum
import com.android.legend.model.enumerate.market.MarketRiskWarningIdEnum
import com.android.legend.model.market.MarketAreaBean
import com.android.legend.socketio.SocketIOClient
import com.android.legend.socketio.SocketIOClient.unsubscribe
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.reflect.TypeToken
import huolongluo.byw.R
import huolongluo.byw.byw.bean.MarketListBean
import huolongluo.byw.byw.ui.present.MarketDataPresent
import huolongluo.byw.io.AppConstants
import huolongluo.byw.log.Logger
import huolongluo.byw.model.MarketResult
import huolongluo.byw.util.GsonUtil
import huolongluo.byw.util.sp.SpUtils2
import kotlinx.android.synthetic.main.activity_kline_fullscreen.*
import kotlinx.android.synthetic.main.fragment_market_first.*
import kotlinx.android.synthetic.main.fragment_market_first.tabLayout
import kotlinx.android.synthetic.main.fragment_market_first.viewPager
import kotlinx.android.synthetic.main.fragment_market_second.*
import kotlinx.android.synthetic.main.inc_risk_warning.*
import kotlinx.android.synthetic.main.item_market_first.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * zh行情第一级 自选 币币 合约 eft级
 */
open class MarketFirstFragment : BaseFragment() {
    val viewModel: MarketViewModel by viewModels()
    private val areaAdapter by lazy { getAreaFirstAdapter() }//第一级分区的adapter
    lateinit var secondAdapter:FragmentStateAdapter //第二级分区的adapter
    private var firstLastClickPosition = 0
    private var isFirst = true
    var secondFragments = mutableListOf<MarketSecondFragment>()
    private var isVisibleToUser = false

    companion object {
        fun newInstance() = MarketFirstFragment()
    }

    override fun getContentViewId(): Int {
        return R.layout.fragment_market_first
    }

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun applyTheme() {
        super.applyTheme()
        refreshPairData(true)
    }

    override fun initView(view: View) {
        ivSearch.setOnClickListener {
            MarketSearchActivity.newInstance(requireContext())
        }

        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv.adapter = areaAdapter
        areaAdapter.setOnItemClickListener { adapter, view, position ->
            run {
                if (position == MarketHelper.firstCurrentClickPosition) return@run
                firstLastClickPosition = MarketHelper.firstCurrentClickPosition
                MarketHelper.firstCurrentClickPosition = position
                clickFirstTab()
            }
        }

        setSecondAdapter()
    }

    override fun initData() {
        initLocalAreaData()
        viewModel.getMarketArea()
        MarketDataPresent.getSelfHttpList()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isVisibleToUser=isVisibleToUser
        EventBus.getDefault().post(BizEvent.Market.IsVisibleToUser(isVisibleToUser))
        if(isVisibleToUser){
            EventBus.getDefault().post(BizEvent.Market.RefreshSelfLocalList())
            subscribeSocket()
            if(isRegisterEventBus()){
                if(!EventBus.getDefault().isRegistered(this)){
                    EventBus.getDefault().register(this)
                }
            }
        }else{
            unSubscribeSocket()
            if(isRegisterEventBus()){
                if(EventBus.getDefault().isRegistered(this)){
                    EventBus.getDefault().unregister(this)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resume()
    }

    override fun onPause() {
        super.onPause()
        unSubscribeSocket()
    }
    open fun resume(){
        if(isVisibleToUser){
            EventBus.getDefault().post(BizEvent.Market.RefreshSelfLocalList())
            subscribeSocket()
        }
    }
    override fun initObserve() {
        viewModel.areaData.observe(this, Observer {
            Logger.getInstance().debug(TAG, "行情分区接口数据：$it")
            if (it.isSuccess) {
                if (it.data == null) return@Observer
                SpUtils2.saveObject(context, AppConstants.SP_KEY.MARKET_AREA, it.data, object : TypeToken<ArrayList<MarketAreaBean>>() {}.type)
                MarketHelper.addAreaFirstList(it.data)
                refreshAreaData()
            }
        })
        viewModel.pairData.observe(this, Observer {
            Logger.getInstance().debug(TAG, "行情币对接口数据：$it")
            if (it.isSuccess) {
                it.data?.data?.list?.let { it1 ->
                    SpUtils2.saveObject(context, AppConstants.SP_KEY.MARKET_PAIR, it1, object : TypeToken<ArrayList<MarketListBean>>() {}.type)
                    MarketHelper.addPairList(it1)
                    refreshPairData(true)
                }
            } else {
                initLocalPairData()
            }
        })
    }
    fun subscribeSocket(){
        SocketIOClient.subscribe<MarketResult.Market>(TAG, AppConstants.SOCKET.SPOT_MARKET, object : TypeToken<MarketResult.Market>() {}) { bean: MarketResult.Market ->
            Logger.getInstance().debug(TAG,"行情币对socket数据：${bean.list}")
            MarketHelper.addPairList(bean.list)
            refreshPairData(true)
        }
    }
    fun unSubscribeSocket(){
        unsubscribe(TAG, AppConstants.SOCKET.SPOT_MARKET)
    }
    private fun clickFirstTab() {
        try {
            MarketHelper.setNormal(areaAdapter.getViewByPosition(firstLastClickPosition, R.id.tv) as TextView)
            MarketHelper.setSelected(areaAdapter.getViewByPosition(MarketHelper.firstCurrentClickPosition, R.id.tv) as TextView)
        }catch (t: Throwable){
            t.printStackTrace()
        }

        if (MarketHelper.areaFirstList[MarketHelper.firstCurrentClickPosition].id!= MarketAreaIdEnum.CONTRACT.id
                &&MarketHelper.areaFirstList[MarketHelper.firstCurrentClickPosition].id!= MarketAreaIdEnum.ETF.id) {
            ivSearch.visible()
        } else {
            ivSearch.gone()
        }
        riskWarning.gone()
        initSecondTab()
    }

    private fun initLocalAreaData() {
        val l = SpUtils2.getObject(context, AppConstants.SP_KEY.MARKET_AREA, object : TypeToken<ArrayList<MarketAreaBean>>() {}.type) as ArrayList<MarketAreaBean>?
        if (l != null&&l.isNotEmpty()) {
            MarketHelper.addAreaFirstList(l)
            Logger.getInstance().debug(TAG, "行情分区本地数据：${MarketHelper.areaFirstList}")
            refreshAreaData()
        }
    }
    private fun initLocalPairData(){
        val l = SpUtils2.getObject(context, AppConstants.SP_KEY.MARKET_PAIR, object : TypeToken<ArrayList<MarketListBean>>() {}.type) as ArrayList<MarketListBean>?
        if (l != null&&l.isNotEmpty()) {
            Logger.getInstance().debug(TAG, "行情币对本地数据：$l")
            MarketHelper.addPairList(l)
            refreshPairData(false)
        }
    }

    open fun refreshAreaData() {
        viewModel.getMarketPair()
        areaAdapter.setList(MarketHelper.areaFirstList)
        areaAdapter.notifyDataSetChanged()
    }
    private fun refreshPairData(isHttpSuccess: Boolean){
        if(MarketHelper.areaFirstList==null) return
        EventBus.getDefault().post(BizEvent.Market.RefreshPairData(isHttpSuccess))
    }

    open fun setSecondAdapter(){
        val areaSecondList=MarketHelper.getAreaSecondList()
        secondAdapter=getAreaSecondAdapter()
        viewPager.adapter = secondAdapter
        TabLayoutMediator(tabLayout, viewPager){ tab, position->
            if(areaSecondList==null||areaSecondList.size<=position) return@TabLayoutMediator
            val bean=areaSecondList[position]
            val view = View.inflate(context, R.layout.item_market_second, null)
            val textView = view.findViewById<TextView>(R.id.tv)
            textView.text = bean.areaName
            tab.customView = view
        }.attach()
    }

    //行情初始化第二级的tab
    fun initSecondTab() {
        secondFragments.clear()
        val areaSecondList = MarketHelper.getAreaSecondList()
        if (areaSecondList == null || areaSecondList.size == 0) {
            tabLayout.gone()
            secondFragments.add(MarketSecondFragment.newInstance(0))
        } else {
            tabLayout.visible()
            tabLayout.removeAllTabs()
            for (index in areaSecondList.indices){
                if(MarketHelper.isIgnore(index)){
                    continue
                }
                secondFragments.add(MarketSecondFragment.newInstance(index))
            }
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    showWarn(tab.position)
                    viewPager.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
        }
        setSecondAdapter()
    }
    fun showWarn(position: Int) {//风险提示本地写死，后期优化
        val areaSecondList=MarketHelper.getAreaSecondList()
        if(areaSecondList.size<=position) return
        if(!areaSecondList[position].showRisk) {
            riskWarning.gone()
            return
        }
        riskWarning.visible()
        var text=""
        when(areaSecondList[position].id){
            MarketRiskWarningIdEnum.STORAGE.id -> text = getString(R.string.storage_tip)
            MarketRiskWarningIdEnum.DAO.id -> text = getString(R.string.dao_tip)
            MarketRiskWarningIdEnum.NFT.id -> text = getString(R.string.nft_tip)
            MarketRiskWarningIdEnum.DEFI.id -> text = getString(R.string.defi_tip)
            MarketRiskWarningIdEnum.NEW.id -> text = getString(R.string.xianshi_wran12)
        }
        tvWarn.text=text
        riskWarning.setOnClickListener(View.OnClickListener { view: View? ->
            ivSpread.isActivated = !ivSpread.isActivated
            if (tvWarn.visibility == View.VISIBLE) {
                tvWarn.gone()
            } else {
                tvWarn.visible()
            }
        })
    }

    //第一级分区
    private fun getAreaFirstAdapter(): BaseQuickAdapter<MarketAreaBean, BaseViewHolder> =
            object : BaseQuickAdapter<MarketAreaBean, BaseViewHolder>(R.layout.item_market_first) {
                override fun convert(helper: BaseViewHolder, item: MarketAreaBean) {
                    helper.itemView.tv.text = item.areaName
                    if (!TextUtils.isEmpty(item.fLable)) {
                        helper.itemView.tvLabel.visible()
                        helper.itemView.tvLabel.text = item.fLable
                    } else {
                        helper.itemView.tvLabel.gone()
                    }
                    if(isFirst&&helper.layoutPosition==0){
                        isFirst = false
                        clickFirstTab()
                        MarketHelper.setSelected(helper.itemView.tv)
                    }else{
                        MarketHelper.setNormal(helper.itemView.tv)
                        if(helper.layoutPosition==MarketHelper.firstCurrentClickPosition){
                            MarketHelper.setSelected(helper.itemView.tv)
                        }
                    }
                }

            }
    //第二级viewpager
    fun getAreaSecondAdapter(): FragmentStateAdapter = object : FragmentStateAdapter(this) {
        override fun getItemCount():Int{
            return secondFragments.size
        }

        override fun createFragment(position: Int):Fragment {
           return secondFragments[position]
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getMarketPair(data: BizEvent.Market.GetMarketPair){
        viewModel.getMarketPair()
    }
}