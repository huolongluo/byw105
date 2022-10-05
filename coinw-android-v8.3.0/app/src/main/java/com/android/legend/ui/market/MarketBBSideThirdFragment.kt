package com.android.legend.ui.market

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
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
import huolongluo.byw.util.sp.SpUtils2
import huolongluo.byw.util.tip.SnackBarUtils
import kotlinx.android.synthetic.main.fragment_market_first.rv
import kotlinx.android.synthetic.main.fragment_market_third.*
import kotlinx.android.synthetic.main.item_market_bb_side_pair.view.*
import kotlinx.android.synthetic.main.item_market_bb_side_pair.view.tvLeftCoinName
import kotlinx.android.synthetic.main.item_market_bb_side_pair.view.tvPrice
import kotlinx.android.synthetic.main.item_market_bb_side_pair.view.tvRightCoinName
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * 币币行情侧滑栏第三级，逻辑参考MarketThirdFragment
 */
open class MarketBBSideThirdFragment : MarketThirdFragment() {
    private val bbAreaSecondList by lazy { arguments?.getSerializable("bbAreaSecondList") as ArrayList<MarketAreaBean> }

    companion object {
        fun newInstance(secondPosition: Int, thirdPosition: Int,bbAreaSecondList:ArrayList<MarketAreaBean>): MarketBBSideThirdFragment {
            val fragment = MarketBBSideThirdFragment()
            val bundle = Bundle()
            bundle.putInt("secondPosition", secondPosition)
            bundle.putInt("thirdPosition", thirdPosition)
            bundle.putSerializable("bbAreaSecondList",bbAreaSecondList)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initView(view: View) {
        super.initView(view)
        llSort.gone()
    }
    override fun getAreaSecondList():ArrayList<MarketAreaBean>{
        return bbAreaSecondList
    }

    override fun onResume() {
        super.onResume()
        refreshData()//币币侧滑栏搜索刷新翻页的币对列表
    }

    override fun isSelf() = secondPosition == 0 //币币侧滑栏自选始终在第一个
    override fun isMarket()=false
    override fun getPairAdapter(): BaseQuickAdapter<MarketListBean, BaseViewHolder> =
            object : BaseQuickAdapter<MarketListBean, BaseViewHolder>(R.layout.item_market_bb_side_pair)
            {
                override fun convert(helper: BaseViewHolder, item: MarketListBean) {
                    helper.itemView.rlt.setOnClickListener {
                        if(isEtf()){
                            MainActivity.self.gotoETFTrade(item)
                        }else{
                            MainActivity.self.gotoTrade(item)
                            HotMoneyPresenter.collectHotData(null, "${item.id}")
                            EventBus.getDefault().post(SelectCurrentPage(0))
                        }

                        MainActivity.self.closeDrawer()
                    }
                    helper.itemView.tvLeftCoinName.text = item.coinName
                    helper.itemView.tvRightCoinName.text = "/${item.cnyName}"
                    MarketHelper.setTextLong(helper.itemView.tvPrice,item.latestDealPrice)
                    helper.itemView.tvPrice.text = item.latestDealPrice
                            ?: AppConstants.COMMON.DEFAULT_DISPLAY
                    if (item.priceRaiseRate > 0.0) {
                        helper.itemView.tvPrice.setTextColor(ThemeUtil.getUpColor(context))
                    } else {
                        helper.itemView.tvPrice.setTextColor(ThemeUtil.getDropColor(context))
                    }
                }

            }
    open fun isEtf():Boolean{
        return false
    }
    //有搜索文本需要过滤搜索币对
    override fun ignoreCurrentAreaPairList(){
        if(!TextUtils.isEmpty(MarketHelper.bbSideSearchTxt)&&!isEtf()){
            val l= mutableListOf<MarketListBean>()
            l.addAll(currentAreaPairList)
            currentAreaPairList.clear()
            l.filter { it.coinName.contains(MarketHelper.bbSideSearchTxt,ignoreCase = true) }.forEach { bean->
                currentAreaPairList.add(bean)
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshBbSideSearchPairList(event: BizEvent.Market.RefreshBbSideSearchPairList) {//币币侧滑栏搜索刷新当前页面币对列表
        refreshData()
    }
}