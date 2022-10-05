package com.android.legend.ui.market

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.legend.base.BaseActivity
import com.android.legend.extension.formatStringByDigits
import com.android.legend.extension.gone
import com.android.legend.extension.visible
import com.android.legend.model.enumerate.market.MarketAreaIdEnum
import com.android.legend.model.market.MarketAreaBean
import com.android.legend.view.InputPriceTextWatcher
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.reflect.TypeToken
import huolongluo.byw.R
import huolongluo.byw.byw.bean.MarketListBean
import huolongluo.byw.byw.ui.activity.main.MainActivity
import huolongluo.byw.io.AppConstants
import huolongluo.byw.model.Hot
import huolongluo.byw.util.MathHelper
import huolongluo.byw.util.SPUtils
import huolongluo.byw.util.sp.SpUtils2
import kotlinx.android.synthetic.main.activity_market_search.*
import kotlinx.android.synthetic.main.activity_market_search.rv
import kotlinx.android.synthetic.main.item_market_search.view.tvLeftCoinName
import kotlinx.android.synthetic.main.item_market_search.view.tvPrice
import kotlinx.android.synthetic.main.item_market_search.view.tvRate
import kotlinx.android.synthetic.main.item_market_search.view.tvRightCoinName

class MarketSearchActivity : BaseActivity() {
    var pairList= mutableListOf<MarketListBean>()//所有可搜素的币对列表(只支持版块和币币)
    var searchList= mutableListOf<MarketListBean>()//搜索到符合条件的币对列表
    private val areaFirstList by lazy { SpUtils2.getObject(this, AppConstants.SP_KEY.MARKET_AREA,
            object : TypeToken<ArrayList<MarketAreaBean>>() {}.type) as ArrayList<MarketAreaBean>? }
    private val allPairList by lazy { SpUtils2.getObject(this, AppConstants.SP_KEY.MARKET_PAIR,
            object : TypeToken<ArrayList<MarketListBean>>() {}.type) as ArrayList<MarketListBean>? }//所有币对列表
    val hotList by lazy {
        SPUtils.getObject(this, AppConstants.COMMON.KEY_HOT_SEARCH, object : TypeToken<List<Hot>>() {}.type) as List<Hot>
    }
    val adapter by lazy { getPairAdapter() }

    companion object{
        fun newInstance(context: Context){
            context.startActivity(Intent(context,MarketSearchActivity::class.java))
        }
    }
    override fun getContentViewId(): Int {
        return R.layout.activity_market_search
    }

    override fun initView() {
        rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            run {
                finish()
                val bean=searchList[position]
                MainActivity.self.gotoTrade(bean.coinName, bean.cnyName, "${bean.id}", bean.selfselection)
            }
        }

        tvCancel.setOnClickListener { finish() }
        etContent.requestFocus()
        initHot()
    }

    override fun initData() {
        val areaSecondList= arrayListOf<List<MarketAreaBean>>()//所有可搜索币的2级分区
        areaFirstList?.forEachIndexed { index, marketAreaBean ->
            if(marketAreaBean.id!= MarketAreaIdEnum.CONTRACT.id&&marketAreaBean.id!= MarketAreaIdEnum.SELF.id&&marketAreaBean.id!= MarketAreaIdEnum.ETF.id){
                areaSecondList.add(marketAreaBean.tradeAreaVos)
            }
        }
        areaSecondList.forEach { list->//遍历版块和币币
            list.forEachIndexed { index, marketAreaBean ->//遍历对应的分区id
                allPairList?.forEach { marketListBean->
                    if(!TextUtils.isEmpty(marketListBean.getfPartitionIds())){
                        val ids=marketListBean.getfPartitionIds().split(",")
                        if(ids!=null&& ids.isNotEmpty()){
                            for(id in ids){
                                if(id.toIntOrNull()?:0==marketAreaBean.id){
                                    pairList.add(marketListBean)
                                    break
                                }
                            }
                        }
                    }
                }

            }
        }

    }

    override fun initObserve() {
    }

    private fun initHot(){
        if(!TextUtils.isEmpty(tvLeftCoinName1.text)){//初始化一次
            return
        }
        etContent.addTextChangedListener(object : InputPriceTextWatcher(etContent){
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                val name=s.toString()
                searchList.clear()
                if(!TextUtils.isEmpty(name)){
                    llHot.gone()
                    pairList.forEach { marketListBean ->
                        if(marketListBean.coinName.contains(name,true)){
                            var isExist=false
                            for (bean in searchList){
                                if(bean.id==marketListBean.id){
                                    isExist=true
                                    break
                                }
                            }
                            if(!isExist){
                                searchList.add(marketListBean)
                            }
                        }
                    }
                }else{
                    llHot.visible()
                }
                adapter.setList(searchList)
                adapter.notifyDataSetChanged()
            }
        })
        hotList.forEachIndexed { index, hot ->
            when (index) {
                0 -> {
                    setHotView(rltHot1, tvLeftCoinName1, tvRightCoinName1, hot)
                }
                1 -> {
                    setHotView(rltHot2, tvLeftCoinName2, tvRightCoinName2, hot)
                }
                2 -> {
                    setHotView(rltHot3, tvLeftCoinName3, tvRightCoinName3, hot)
                }
            }
        }
    }
    private fun setHotView(rltHot: RelativeLayout, tvLeftCoinName: TextView, tvRightCoinName: TextView, hot: Hot){
        rltHot.visible()
        tvLeftCoinName.text=hot.shortName
        tvRightCoinName.text="/${hot.name}"
        rltHot.setOnClickListener {
            etContent.setText("")
            llSearch.gone()
            finish()
            MainActivity.self.gotoTrade(hot.shortName, hot.name, hot.tmid, hot.selfselection)
        }
    }
    private fun getPairAdapter(): BaseQuickAdapter<MarketListBean, BaseViewHolder> =
            object : BaseQuickAdapter<MarketListBean, BaseViewHolder>(R.layout.item_market_search) {
                override fun convert(helper: BaseViewHolder, item: MarketListBean) {
                    helper.itemView.tvLeftCoinName.text=item.coinName
                    helper.itemView.tvRightCoinName.text="/${item.cnyName}"
                    helper.itemView.tvPrice.text=item.latestDealPrice?:AppConstants.COMMON.DEFAULT_DISPLAY
                    val rate= MathHelper.mul(100.0, item.priceRaiseRate).formatStringByDigits(2, true)
                    helper.itemView.tvRate.text="$rate%"
                    if (item.priceRaiseRate>0.0){
                        helper.itemView.tvRate.setTextColor( ContextCompat.getColor(context, R.color.up))
                    }else{
                        helper.itemView.tvRate.setTextColor( ContextCompat.getColor(context, R.color.drop))
                    }
                }

            }
}