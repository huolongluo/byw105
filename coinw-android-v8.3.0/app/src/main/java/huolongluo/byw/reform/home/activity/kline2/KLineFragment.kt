package huolongluo.byw.reform.home.activity.kline2

import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.android.legend.socketio.SocketIOClient
import com.google.gson.reflect.TypeToken
import com.legend.common.base.ThemeActivity
import com.legend.common.util.ThemeUtil
import huolongluo.byw.heyue.LogUtils
import huolongluo.byw.io.AppConstants
import huolongluo.byw.log.Logger
import huolongluo.byw.model.ReqData
import huolongluo.byw.reform.base.BaseViewFragment
import huolongluo.byw.reform.home.activity.kline2.common.Kline2Constants
import huolongluo.byw.view.kline.HisData
import huolongluo.byw.view.kline.KLineView
import huolongluo.byw.view.kline.MainIndex
import huolongluo.byw.view.kline.SubIndex
import huolongluo.bywx.utils.AppUtils
import kotlinx.coroutines.*

/**
 * KLine
 */
class KLineFragment : BaseViewFragment(), CoroutineScope by MainScope() {
    private val viewModel:KLineViewModel by viewModels()

    private lateinit var kLineView: KLineView
    private var job: Job? = null

    private val selectId by lazy { arguments?.getString(EXTRA_SELECT_ID) ?: "45" }
    private val position by lazy { arguments?.getInt(EXTRA_POSITION) ?: 0 }
    private val coinId by lazy { arguments?.getInt(EXTRA_COIN_ID) ?: -1 }

    private val tradeType by lazy {
        arguments?.getInt(EXTRA_TRADE_TYPE) ?: Kline2Constants.TRADE_TYPE_COIN
    }
    private val entity by lazy {
        arguments?.getParcelable(EXTRA_KLINE_ENTITY)?:huolongluo.byw.reform.home.activity.kline2.common.KLineEntity()
    }
    /**
     * 是否在全屏K线页面
     */
    private val isFullScreen by lazy { context is KLineFullScreenActivity }

    /**
     * 获取时间，由于分时图和1分钟K线冲突，分时图用0代替
     * 这里将0还原成60，即时间间隔为1分钟
     */
    private val selectTime by lazy {
        val time = AppUtils.getSelectTime(tradeType)
        if (time == 0) 60 else time
    }

    private lateinit var requestTag: String

    companion object {
        private const val EXTRA_SELECT_ID = "EXTRA_SELECT_ID"
        private const val EXTRA_POSITION = "EXTRA_POSITION"
        private const val EXTRA_TRADE_TYPE = "EXTRA_TRADE_TYPE"
        private const val EXTRA_COIN_ID = "EXTRA_COIN_ID"
        private const val EXTRA_KLINE_ENTITY = "EXTRA_KLINE_ENTITY"
        fun newInstance(position: Int, selectId: String, @Kline2Constants.TradeType tradeType: Int,coinId:Int,
                        entity: huolongluo.byw.reform.home.activity.kline2.common.KLineEntity) = KLineFragment().also {
            val args = Bundle()
            args.putInt(EXTRA_POSITION, position)
            args.putString(EXTRA_SELECT_ID, selectId)
            args.putInt(EXTRA_TRADE_TYPE, tradeType)
            args.putInt(EXTRA_COIN_ID, coinId)
            args.putParcelable(EXTRA_KLINE_ENTITY, entity)
            it.arguments = args
        }
    }

    override fun onCreatedView(rootView: View?) {
        requestTag = "${position}KLineFragment"
        kLineView.showProgressBar()
        getHistoryData()
        initData()
    }

    @NonNull
    override fun getRootView(): View {
        kLineView = KLineView(context)
        return kLineView
    }
    private fun initData(){
        viewModel.klineHistoryData.observe(this, Observer {
            kLineView.dismissProgressBar()
            Logger.getInstance().debug(TAG,"viewModel.klineHistoryData:$it")
            if(it.isSuccess){
                if(it.data!=null&& it.data.isNotEmpty()&&it.data[0].size>2){
                    launch(context = Dispatchers.IO) {
                        try {
                            // 子线程中操作K线
                            val digits = getDigits(it.data[0][2])
                            LogUtils.getInstance().d("KLineFragment $position 精度 $digits")

                            val dataList= arrayListOf<HisData>()
                            for (data in it.data.reversed()){
                                dataList.add(
                                        HisData(data[0].toLong(),
                                        data[1].toDouble(),
                                        data[2].toDouble(),
                                        data[3].toDouble(),
                                        data[4].toDouble(),
                                        data[5].toDouble(),
                                        data[6].toDouble()))
                            }
                            setKLineData(dataList, digits)
                        } catch (t: Throwable) {
                            Logger.getInstance().error(t)
                        }
                    }
                }
            }
        })
    }

    private fun getDateFormat(): String {
        return if (selectTime <= 60) {
            "HH:mm"
        } else if (selectTime <= 6 * 60 * 60) {
            "MM-dd HH:mm"
        } else {
            "yy-MM-dd"
        }
    }


    override fun onResume() {
        super.onResume()
        LogUtils.getInstance().d("$requestTag onResume")
        addListener()
    }

    override fun onPause() {
        super.onPause()
        LogUtils.getInstance().d("$requestTag onPause")
        removeListener()
    }

    private fun getHistoryData() {
        val granularity =getGranularity()
        viewModel.getKlineHistoryData(granularity,"$coinId")
    }
    private fun getGranularity():String{
        return Kline2Constants.SELECT_TIME_TO_GRANULARITY[selectTime]?:""
    }

    private fun removeListener() {
        val pair = entity.coinName + "-" + entity.cnyName
        val granularity =getGranularity()
        SocketIOClient.unsubscribe(TAG, "${AppConstants.SOCKET.SPOT_KLINE}$granularity:$pair")
    }


    private fun addListener() {
        val rd = ReqData()
        rd.tag = requestTag
        val pair = entity.coinName+ "-" + entity.cnyName
        val granularity =getGranularity()
        SocketIOClient.subscribe(TAG, "${AppConstants.SOCKET.SPOT_KLINE}$granularity:$pair", TypeToken.get(Array<String>::class.java)) {
            Logger.getInstance().debug(TAG,"socket返回 k线数据:size:${it.size} ${it[0]} ${it[1]} ${it[2]} ${it[3]} ${it[4]} ${it[5]} ${it[6]}")
            launch(context = Dispatchers.IO) {
                try {
                    if (!kLineView.isInit) return@launch
                    val dataList = arrayListOf<HisData>()
                    dataList.add(
                            HisData(it[0].toLong(),
                                    it[1].toDouble(),
                                    it[2].toDouble(),
                                    it[3].toDouble(),
                                    it[4].toDouble(),
                                    it[5].toDouble(),
                                    it[6].toDouble()))
                    if (position == 0) { // 分时图
                        kLineView.addLineDataList(dataList)
                    } else {
                        kLineView.addKDataList(dataList)
                    }
                }catch (e: Exception) {
                    Logger.getInstance().error(e)
                }
            }
        }
    }


    private fun getDigits(price: String): Int {
        val i = price.indexOf('.')
        if (i >= 0) {
            return price.length - i - 1
        }
        return 0
    }

    private fun setKLineData(dataList: List<HisData>, digits: Int) {
        Logger.getInstance().debug(TAG,"setKLineData k线历史数据:$dataList  digits:$digits")
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
    fun showMainIndex(mainIndex: MainIndex) {
        if (::kLineView.isInitialized && kLineView.isInit) {
            kLineView.showMainIndex(mainIndex)
        }
    }

    fun showSubIndex(subIndex: SubIndex) {
        if (::kLineView.isInitialized && kLineView.isInit) {
            kLineView.showSubIndex(subIndex)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        LogUtils.getInstance().d("$requestTag onDestroyView")
        job?.cancel()
        kLineView.clear()
    }
}