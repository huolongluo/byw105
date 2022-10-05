package com.legend.modular_contract_sdk.ui.chart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.legend.modular_contract_sdk.base.BaseFragment
import com.legend.modular_contract_sdk.common.UserConfigStorage
import com.legend.modular_contract_sdk.component.market_listener.CandleList
import com.legend.modular_contract_sdk.component.market_listener.MarketListenerManager
import com.legend.modular_contract_sdk.component.market_listener.MarketSubscribeType
import com.legend.modular_contract_sdk.repository.model.Product
import kotlinx.coroutines.*

/**
 * KLine
 */
class McKLineFragment : BaseFragment<McKlineViewModel>(), CoroutineScope by MainScope() {

    private lateinit var kLineView: McKLineView
    private var job: Job? = null

    private val position by lazy { arguments?.getInt("position") ?: 0 }
    private lateinit var product: Product
    /**
     * 是否在全屏K线页面
     */
    private val isFullScreen by lazy { context is McKLineFullScreenActivity }

    private lateinit var selectTime : CandlesTimeUnit

    private lateinit var requestTag: String

    companion object {
        fun newInstance(position: Int, timeUnit: CandlesTimeUnit, product: Product) = McKLineFragment().also {
            val args = Bundle()
            args.putInt("position", position)
            args.putSerializable("time_unit", timeUnit)
            args.putSerializable("product", product)
            it.arguments = args
        }
    }

    override fun createViewModel() = ViewModelProvider(this).get(McKlineViewModel::class.java)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        product= arguments?.getSerializable("product") as Product
        selectTime = arguments?.getSerializable("time_unit") as CandlesTimeUnit
        requestTag = "${position}KLineFragment"
        kLineView.showProgressBar()
        getHistoryData()
        initData()
    }

    override fun createRootView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        kLineView = McKLineView(context)
        return kLineView
    }

    private fun initData(){
        getViewModel().mChartLiveData.observe(requireActivity(),Observer{ its ->
            try {
                if (!kLineView.isInit) return@Observer
                val candleList=its as CandleList
                // 时间间隔不一致的数据直接return
                if (!selectTime.timeUnit.equals(candleList.timeUnit, ignoreCase = true)){
                    return@Observer
                }
                val dataList = arrayListOf<McHisData>()
                candleList.candleList.map{
                    dataList.add(
                    McHisData(it.getTime().toLong(),
                            it.getOpen().toDouble(),
                            it.getHigh().toDouble(),
                            it.getLow().toDouble(),
                            it.getClose().toDouble(),
                            it.getVolume().toDouble(),
                            0.0))
                }
                if (position == 0) { // 分时图
                    kLineView.addLineDataList(dataList)
                } else {
                    kLineView.addKDataList(dataList)
                }
            }catch (e: Exception) {
                e.printStackTrace()
            }

        })
        getViewModel().mAllChartLiveData.observe(viewLifecycleOwner, Observer { its->
            val candleList = its as CandleList
            try {
                // 子线程中操作K线
                val digits = getDigits(candleList.candleList[0].getClose())
                val dataList = arrayListOf<McHisData>()
                candleList.candleList.map{
                    dataList.add(
                            McHisData(it.getTime().toLong(),
                                    it.getOpen().toDouble(),
                                    it.getHigh().toDouble(),
                                    it.getLow().toDouble(),
                                    it.getClose().toDouble(),
                                    it.getVolume().toDouble(),
                                    0.0))
                }
                setKLineData(dataList, digits)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun getDateFormat(): String {
        return when {
            selectTime.longTime <= 60 -> {
                "HH:mm"
            }
            selectTime.longTime <= 6 * 60 * 60 -> {
                "MM-dd HH:mm"
            }
            else -> {
                "yy-MM-dd"
            }
        }
    }


    override fun onResume() {
        super.onResume()
        addListener()
    }

    override fun onPause() {
        super.onPause()
        removeListener()
    }

    private fun getHistoryData() {
        getViewModel().fetchCandles(product.mBase, selectTime.timeUnit)
    }

    private fun removeListener() {
        removeAllMarketListener()
    }


    private fun addListener() {
        product?.let { product ->
            mMarketListenerList.add(
                    MarketListenerManager.subscribe(
                            MarketSubscribeType.CandlesSwap(
                                    product.mBase,
                                    "usd",
                                    selectTime.timeUnit,
                                    System.currentTimeMillis().toString()
                            ),
                            getViewModel().mChartLiveData
                    )
            )
        }
    }


    private fun getDigits(price: String): Int {
        val i = price.indexOf('.')
        if (i >= 0) {
            return price.length - i - 1
        }
        return 0
    }

    private fun setKLineData(dataList: List<McHisData>, digits: Int) {
        if (position == 0) { // 分时图
            if (!kLineView.isInit) {
                if (isFullScreen) {
                    kLineView.setChartCount(250, 150, 50)
                } else {
                    kLineView.setChartCount(150, 100, 50)
                }
                kLineView.config(digits, getDateFormat(), isFullScreen)
                kLineView.initChartPriceData(dataList, true)
                kLineView.dismissProgressBar()
            } else {
                kLineView.addLineDataList(dataList)
            }
        } else {
            if (!kLineView.isInit) {
                if (isFullScreen) {
                    kLineView.setChartCount(200, 80, 40)
                } else {
                    kLineView.setChartCount(100, 50, 30)
                }
                kLineView.config(digits, getDateFormat(), isFullScreen)
                kLineView.initChartKData(dataList, true)
                kLineView.dismissProgressBar()
            } else {
                kLineView.addKDataList(dataList)
            }
        }
    }
    fun showMainIndex(mainIndex: McMainIndex) {
        if (::kLineView.isInitialized && kLineView.isInit) {
            kLineView.showMainIndex(mainIndex)
        }
    }

    fun showSubIndex(subIndex: McSubIndex) {
        if (::kLineView.isInitialized && kLineView.isInit) {
            kLineView.showSubIndex(subIndex)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
        kLineView.clear()
    }
}