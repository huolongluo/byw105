package huolongluo.byw.reform.home.activity.kline2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.coinw.api.kx.model.X24HData
import com.android.coinw.utils.Utilities
import com.android.legend.socketio.SocketIOClient
import com.google.gson.reflect.TypeToken
import com.legend.common.util.ThemeUtil
import com.legend.modular_contract_sdk.utils.StatusBarUtils2
import huolongluo.byw.R
import huolongluo.byw.io.AppConstants
import huolongluo.byw.log.Logger
import huolongluo.byw.reform.base.BaseActivity
import huolongluo.byw.reform.home.activity.kline2.common.KLineEntity
import huolongluo.byw.reform.home.activity.kline2.common.Kline2Constants
import huolongluo.byw.util.MathHelper
import huolongluo.byw.util.SPUtils
import huolongluo.byw.util.config.ConfigurationUtils
import huolongluo.byw.util.noru.NorUtils
import huolongluo.byw.util.pricing.PricingMethodUtil
import huolongluo.byw.view.kline.MainIndex
import huolongluo.byw.view.kline.SubIndex
import huolongluo.bywx.utils.AppUtils
import huolongluo.bywx.utils.DoubleUtils
import kotlinx.android.synthetic.main.activity_kline_fullscreen.*
import kotlin.math.max

class KLineFullScreenActivity : BaseActivity() {
    private val viewModel:KLineViewModel by viewModels()

    companion object {

        private const val TAG = "KLineFullScreenActivity"

        private const val EXTRA_ENTITY = "EXTRA_ENTITY"
        @JvmStatic
        fun launch(activity: Activity, entity: KLineEntity, requestCode: Int) {
            val intent = Intent(activity, KLineFullScreenActivity::class.java)
            intent.putExtra(EXTRA_ENTITY, entity)
            activity.startActivityForResult(intent, requestCode)
        }
    }


    private val entity by lazy { intent.getParcelableExtra(EXTRA_ENTITY) as KLineEntity }

    private val chartFragments by lazy {
        MutableList(Kline2Constants.FULLSCREEN_SELECT_TIME_ARRAY.size) {
            KLineFragment.newInstance(it, entity.kLineId, entity.tradeType,entity.id,entity)
        }
    }


    private val chartTabs by lazy {
        listOf(vLineChart, v1Min, v5Min, v15Min, v30Min, v1Hour, v4Hour, v6Hour, v1Day, v1Week, v1Month)
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
        super.onCreate(savedInstanceState)
        StatusBarUtils2.setStatusBarDefault(this)
        ConfigurationUtils.resetLanguage(this)//需要在加载布局前设置才有效果
        setContentView(R.layout.activity_kline_fullscreen)
        initViews()
        initData()
    }

    private fun initData() {
        tvName.text = entity.coinName + "/" + entity.cnyName

        viewModel.x24HData.observe(this, androidx.lifecycle.Observer {
            Logger.getInstance().debug(TAG,"viewModel.x24HData:${it}")
            if(it.isSuccess){
                if(it?.data!=null){
                    refreshHeadData(it.data)
                }
            }
        })
    }

    private fun initViews() {
        ivClose.setOnClickListener { finish() }
        // 图表tab及viewPager
        viewPager.isUserInputEnabled = false
        (viewPager.getChildAt(0) as RecyclerView).setItemViewCacheSize(0)
        viewPager.adapter = chartAdapter


        // 从sp获取上一次选中图表时间类型的位置
        val selectIndex = max(0, Kline2Constants.FULLSCREEN_SELECT_TIME_ARRAY.indexOf(AppUtils.getSelectTime(entity.tradeType)))
        chartTabs[selectIndex].isSelected = true
        viewPager.setCurrentItem(selectIndex, false)

        val tabsListener: (View) -> Unit = { v ->
            isKLineChange = true
            chartTabs.forEach { it.isSelected = false }
            v.isSelected = true
            val tabIndex = chartTabs.indexOf(v)
            AppUtils.setSelectTime(Kline2Constants.FULLSCREEN_SELECT_TIME_ARRAY[tabIndex], entity.tradeType)
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

    private fun initIndicatorView(mainIndexCallback: (mainIndex: MainIndex) -> Unit,
                                  subIndexCallback: (subIndex: SubIndex) -> Unit) {

        val whiteColor = ThemeUtil.getThemeColor(this, com.legend.modular_contract_sdk.R.attr.colorAccent)
        val unselectedColor = ContextCompat.getColor(this, com.legend.modular_contract_sdk.R.color.mc_sdk_a5a2be)

        // 设置view
        val mainIndex = SPUtils.getKLineMainIndex(this)
        val subIndex = SPUtils.getKLineSubIndex(this)
        when (mainIndex) {
            MainIndex.MA -> tvMa.setTextColor(whiteColor)
            MainIndex.BOLL -> tvBoll.setTextColor(whiteColor)
            MainIndex.EMA -> tvEma.setTextColor(whiteColor)
            MainIndex.SAR -> {
            }/*暂时不需要这个指标*/
            else -> tvMa.setTextColor(whiteColor)
        }
        when (subIndex) {
            SubIndex.MACD -> tvMacd.setTextColor(whiteColor)
            SubIndex.KDJ -> tvKdj.setTextColor(whiteColor)
            SubIndex.RSI -> {
            }/*暂时不需要这个指标*/
            SubIndex.OBV -> {
            }/*暂时不需要这个指标*/
            SubIndex.WR -> {
            }/*暂时不需要这个指标*/
            else -> tvMacd.setTextColor(whiteColor)
        }

        // 设置主指标
        val mainIndexViews = listOf(tvMa, tvEma, tvBoll)
        val mainClickListener: (View) -> Unit = { v ->

            val currentMainIndicator = SPUtils.getKLineMainIndex(this)

            mainIndexViews.forEach {
                (it as TextView).setTextColor(unselectedColor)
            }

            val mainIndex = when (v) {
                tvEma -> MainIndex.EMA
                tvBoll -> MainIndex.BOLL
                else -> MainIndex.MA
            }

            if (mainIndex == currentMainIndicator){
                // 选中的指标和当前指标一样则取消指标
                SPUtils.saveKLineMainIndex(this, MainIndex.NONE)
                mainIndexCallback(MainIndex.NONE)
            } else {
                // 选中的指标和当前指标不一样则选中指标
                (v as TextView).setTextColor(whiteColor)
                SPUtils.saveKLineMainIndex(this, mainIndex)
                mainIndexCallback(mainIndex)
            }


        }
        mainIndexViews.forEach { it.setOnClickListener(mainClickListener) }
        // 副图指标
        val subIndexViews = listOf(tvMacd, tvKdj)
        val subClickListener: (View) -> Unit = { v ->

            val currentSubIndicator = SPUtils.getKLineSubIndex(this)

            subIndexViews.forEach {
                (it as TextView).setTextColor(unselectedColor)
            }

            val subIndex = when (v) {
                tvKdj -> SubIndex.KDJ
                else -> SubIndex.MACD
            }

            if (subIndex == currentSubIndicator) {
                SPUtils.saveKLineSubIndex(this, SubIndex.NONE)
                subIndexCallback(SubIndex.NONE)
            } else {
                (v as TextView).setTextColor(whiteColor)
                SPUtils.saveKLineSubIndex(this, subIndex)
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
    private fun refreshHeadData(data: X24HData) {
        Utilities.getMainHandler().post(Runnable {
            refreshHead(data.last,data.high,data.low,data.volValue,data.changeRate)
        })
    }
    private fun refreshHead(last:String,high:String,low:String,volValue:String,changeRate:String){
        tvPrice.text = last
        tvHighValue.text = high
        tvLowValue.text = low
        tvVolumeValue.text = PricingMethodUtil.getLargePrice(volValue,3)
        ivChange.visibility=View.VISIBLE
        tvExchangePrice.text="≈${PricingMethodUtil.getPricingUnit()}${PricingMethodUtil.getResultByExchangeRate(last,entity.cnyName)}"
        var ratio= MathHelper.mul(100.0,changeRate.toDouble())
        if (DoubleUtils.parseDouble(changeRate) >= 0) {
            tvRate.setTextColor(resources.getColor(R.color.ffff5763))
            tvRate.text = "+${NorUtils.NumberFormat(2).format(ratio)}%"
            ivChange.setImageResource(R.mipmap.zhang)
            tvPrice.setTextColor(resources.getColor(R.color.ffff5763))
        } else {
            tvRate.setTextColor(resources.getColor(R.color.ff44be9b))
            tvRate.text = "${NorUtils.NumberFormat(2).format(ratio)}%"
            ivChange.setImageResource(R.mipmap.die)
            tvPrice.setTextColor(resources.getColor(R.color.ff44be9b))
        }
    }

    private fun subscribeSocket() {
        if (entity == null) {
            return
        }
        subscribeCoin()
    }

    private fun get24HData(){
        viewModel.get24HData("${entity.id}")
    }

    private fun subscribeCoin() {
        if(TextUtils.isEmpty(tvPrice.text.toString())){
            get24HData()
        }
        val pair = entity.coinName + "-" + entity.cnyName
        SocketIOClient.subscribe(TAG, "${AppConstants.SOCKET.SPOT_24H_DATA}$pair", TypeToken.get(X24HData::class.java)) {
            Logger.getInstance().debug(TAG,"socket返回 24h数据:$it")
            refreshHeadData(it)
        }
    }


    private fun unSubscribeSocket() {
        if (entity == null) {
            return
        }
        unSubscribeCoin()
    }

    private fun unSubscribeCoin() {
        val pair = entity.coinName + "-" + entity.cnyName
        SocketIOClient.unsubscribe(TAG, "${AppConstants.SOCKET.SPOT_24H_DATA}$pair")
    }

    override fun finish() {
        // 同步上一个页面的时间选择、指标
        if (isKLineChange) {
            setResult(Activity.RESULT_OK)
        }
        super.finish()
    }
}