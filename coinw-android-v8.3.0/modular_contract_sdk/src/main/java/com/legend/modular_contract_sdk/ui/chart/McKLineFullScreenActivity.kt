package com.legend.modular_contract_sdk.ui.chart

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.legend.common.util.ThemeUtil
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.base.BaseActivity
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils
import com.legend.modular_contract_sdk.common.UserConfigStorage
import com.legend.modular_contract_sdk.component.market_listener.MarketListenerManager
import com.legend.modular_contract_sdk.component.market_listener.MarketSubscribeType
import com.legend.modular_contract_sdk.component.market_listener.Price
import com.legend.modular_contract_sdk.component.market_listener.Ticker
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.utils.*
import kotlinx.android.synthetic.main.mc_sdk_activity_kline_fullscreen.*
import kotlin.math.max

class McKLineFullScreenActivity : BaseActivity<McKlineViewModel>()
{

    companion object {

        private const val TAG = "KLineFullScreenActivity"

        @JvmStatic
        fun launch(activity: Activity, product: Product, requestCode: Int) {
            val intent = Intent(activity, McKLineFullScreenActivity::class.java)
            intent.putExtra("product", product)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    private lateinit var product:Product

    private val chartFragments by lazy {
        MutableList(McConstants.KLINE.SELECT_FULL_TIME_ARRAY.size) {
            McKLineFragment.newInstance(it, McConstants.KLINE.SELECT_TIME_ARRAY[it], product)
        }
    }

    override fun createViewModel()= ViewModelProvider(this).get(McKlineViewModel::class.java)
    private val chartTabs by lazy {
        listOf(vLineChart, v1Min, v5Min, v15Min, v1Hour, v4Hour, v1Day, v1Week)
    }

    private val chartAdapter = object : FragmentStateAdapter(this) {
        override fun getItemCount() = chartFragments.size
        override fun createFragment(position: Int) = chartFragments[position]
    }


    /**
     * K线的时间、指标信息是否改变
     */
    private var isKLineChange = false
    override fun onCreate(savedInstanceState: Bundle?) {
        McConfigurationUtils.resetLanguage(this)//需要在加载布局前设置才有效果
        super.onCreate(savedInstanceState)
        StatusBarUtils2.setStatusBarDefault(this)
        product= intent.getSerializableExtra("product") as Product
        setContentView(R.layout.mc_sdk_activity_kline_fullscreen)
        initViews()
        initObserver()
        initData()
    }
    private fun initObserver(){
        getViewModel().mTickerLiveData.observe(this, androidx.lifecycle.Observer{
            val ticker = it as Ticker
            val pricePrecision =if (product == null) 4 else product.mPricePrecision
            tvPrice.text = ticker.last.getNum(pricePrecision, true)
            tvHighValue.text = ticker.high.getNum(pricePrecision, true)
            tvLowValue.text = ticker.low.getNum(pricePrecision, true)
            var ratio= MathUtils.mul(100.0, ticker.changeRate.toDoubleOrNull()?:0.0)
            when {
                ticker.changeRate.toDoubleOrNull()?:0.0 > 0 -> {
                    tvRate.text = "+"+ratio.toString().getNum(2) + "%"
                    ivChange.visibility= View.VISIBLE
                    ivChange.setImageResource(R.drawable.mc_sdk_zhang)
                    tvPrice.setTextColor(resources.getColor(R.color.mc_sdk_ff5763))
                    tvRate.setTextColor(resources.getColor(R.color.mc_sdk_ff5763))
                }
                ticker.changeRate.toDoubleOrNull()?:0.0==0.0 -> {
                    tvRate.text = "+"+ratio.toString().getNum(2) + "%"
                    ivChange.visibility= View.GONE
                    tvPrice.setTextColor(resources.getColor(R.color.mc_sdk_a5a2be))
                    tvRate.setTextColor(resources.getColor(R.color.mc_sdk_a5a2be))
                }
                else -> {
                    tvRate.text = ratio.toString().getNum(2) + "%"
                    ivChange.visibility= View.VISIBLE
                    ivChange.setImageResource(R.drawable.mc_sdk_die)
                    tvPrice.setTextColor(ContextCompat.getColor(this,R.color.mc_sdk_44be9b))
                    tvRate.setTextColor(ContextCompat.getColor(this,R.color.mc_sdk_44be9b))
                }
            }

            ivChange.visibility = View.GONE
        })

        getViewModel().mPriceLiveData.observe(this, Observer {
            val markPrice = it as Price
            val pricePrecision =if (product == null) 4 else product.mPricePrecision
            tvMarkPrice.text = markPrice.p.getNum(pricePrecision)
        })
    }
    private fun initData() {
    }

    private fun initViews() {
        ivClose.setOnClickListener { finish() }
        // 图表tab及viewPager
        viewPager.isUserInputEnabled = false
        (viewPager.getChildAt(0) as RecyclerView).setItemViewCacheSize(0)
        viewPager.adapter = chartAdapter


        // 从sp获取上一次选中图表时间类型的位置
        val selectIndex = max(0, McConstants.KLINE.SELECT_FULL_TIME_ARRAY.indexOf(UserConfigStorage.getSelectTime()))
        chartTabs[selectIndex].isSelected = true
        viewPager.setCurrentItem(selectIndex, false)

        val tabsListener: (View) -> Unit = { v ->
            isKLineChange = true
            chartTabs.forEach { it.isSelected = false }
            v.isSelected = true
            val tabIndex = chartTabs.indexOf(v)
            UserConfigStorage.setSelectTime(McConstants.KLINE.SELECT_FULL_TIME_ARRAY[tabIndex])
            viewPager.setCurrentItem(tabIndex, false)
        }
        chartTabs.forEach { it.setOnClickListener(tabsListener) }


       initIndicatorView({ mainIndex ->
           isKLineChange = true
           chartFragments.forEach { it.showMainIndex(mainIndex) }
       }, { subIndex ->
           isKLineChange = true
           chartFragments.forEach { it.showSubIndex(subIndex) }
       })

    }

    private fun initIndicatorView(mainIndexCallback: (mainIndex: McMainIndex) -> Unit,
                                  subIndexCallback: (subIndex: McSubIndex) -> Unit) {

        val whiteColor = ThemeUtil.getThemeColor(this, R.attr.colorAccent)
        val unselectedColor = ContextCompat.getColor(this, R.color.mc_sdk_a5a2be)

        // 设置view
        val mainIndex = UserConfigStorage.getKLineMainIndex()
        val subIndex = UserConfigStorage.getKLineSubIndex()
        when (mainIndex) {
            McMainIndex.MA -> tvMa.setTextColor(whiteColor)
            McMainIndex.BOLL -> tvBoll.setTextColor(whiteColor)
            McMainIndex.EMA -> tvEma.setTextColor(whiteColor)
            McMainIndex.SAR -> {
            }/*暂时不需要这个指标*/
            else -> tvMa.setTextColor(whiteColor)
        }
        when (subIndex) {
            McSubIndex.MACD -> tvMacd.setTextColor(whiteColor)
            McSubIndex.KDJ -> tvKdj.setTextColor(whiteColor)
            McSubIndex.RSI -> {
            }/*暂时不需要这个指标*/
            McSubIndex.OBV -> {
            }/*暂时不需要这个指标*/
            McSubIndex.WR -> {
            }/*暂时不需要这个指标*/
            else -> tvMacd.setTextColor(whiteColor)
        }

        // 设置主指标
        val mainIndexViews = listOf(tvMa, tvEma, tvBoll)
        val mainClickListener: (View) -> Unit = { v ->

            val currentMainIndicator = UserConfigStorage.getKLineMainIndex()

            mainIndexViews.forEach {
                (it as TextView).setTextColor(unselectedColor)
            }

            val mainIndex = when (v) {
                tvEma -> McMainIndex.EMA
                tvBoll -> McMainIndex.BOLL
                else -> McMainIndex.MA
            }

            if (mainIndex == currentMainIndicator){
                // 选中的指标和当前指标一样则取消指标
                UserConfigStorage.saveKLineMainIndex(McMainIndex.NONE)
                mainIndexCallback(McMainIndex.NONE)
            } else {
                // 选中的指标和当前指标不一样则选中指标
                (v as TextView).setTextColor(whiteColor)
                UserConfigStorage.saveKLineMainIndex( mainIndex)
                mainIndexCallback(mainIndex)
            }


        }
        mainIndexViews.forEach { it.setOnClickListener(mainClickListener) }
        // 副图指标
        val subIndexViews = listOf(tvMacd, tvKdj)
        val subClickListener: (View) -> Unit = { v ->

            val currentSubIndicator = UserConfigStorage.getKLineSubIndex()

            subIndexViews.forEach {
                (it as TextView).setTextColor(unselectedColor)
            }

            val subIndex = when (v) {
                tvKdj -> McSubIndex.KDJ
                else -> McSubIndex.MACD
            }

            if (subIndex == currentSubIndicator) {
                UserConfigStorage.saveKLineSubIndex(McSubIndex.NONE)
                subIndexCallback(McSubIndex.NONE)
            } else {
                (v as TextView).setTextColor(whiteColor)
                UserConfigStorage.saveKLineSubIndex( subIndex)
                subIndexCallback(subIndex)
            }
        }
        subIndexViews.forEach { it.setOnClickListener(subClickListener) }

    }

    override fun onResume() {
        super.onResume()
        subscribeSocket()
    }

    override fun onPause() {
        super.onPause()
        unSubscribeSocket()
    }

    private fun subscribeSocket() {
        if (product == null) {
            return
        }
        unSubscribeSocket()

        mMarketListenerList.add(
                MarketListenerManager.subscribe(
                        MarketSubscribeType.TickerSwap(product.mBase, "usd"),
                        getViewModel().mTickerLiveData
                )
        )

        mMarketListenerList.add(
                MarketListenerManager.subscribe(
                        MarketSubscribeType.MarkPrice(product.mBase, "usd"),
                        getViewModel().mPriceLiveData
                )
        )
    }


    private fun unSubscribeSocket() {
        CoinwHyUtils.removeAllMarketListener(mMarketListenerList)
    }


    override fun finish() {
        // 同步上一个页面的时间选择、指标
        if (isKLineChange) {
            setResult(Activity.RESULT_OK)
        }
        super.finish()
    }
}