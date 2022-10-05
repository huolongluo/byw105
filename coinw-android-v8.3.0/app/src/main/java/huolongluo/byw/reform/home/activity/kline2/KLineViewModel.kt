package huolongluo.byw.reform.home.activity.kline2

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.coinw.api.kx.model.X24HData
import com.android.coinw.api.kx.model.XDepthData
import com.android.coinw.api.kx.model.XLatestDeal
import com.android.legend.api.ApiRepository
import com.android.legend.model.BaseResponse
import com.android.legend.model.CommonResult
import com.android.legend.model.kline.NetValueBean
import kotlinx.coroutines.launch

class KLineViewModel :ViewModel(){
    val x24HData=MutableLiveData<CommonResult<X24HData>>()
    val latestData=MutableLiveData<CommonResult<List<XLatestDeal>>>()//最新成交
    val klineHistoryData=MutableLiveData<CommonResult<List<List<String>>>>() //k线历史数据，[时间,开盘价,收盘价,最高,最低,成交量,成交额]数据按照这个顺序解析
    val depthData=MutableLiveData<CommonResult<XDepthData>>()//行情快照（深度）
    val netValueData=MutableLiveData<CommonResult<BaseResponse<NetValueBean>>>()//读取净值和费率

    fun get24HData(symbol:String)=viewModelScope.launch {
        x24HData.value=ApiRepository.instance.get24HData(symbol)
    }
    fun getLatestData(symbol:String)=viewModelScope.launch {
        latestData.value=ApiRepository.instance.getLatestData(symbol)
    }
    fun getDepthData(symbol:String)=viewModelScope.launch {
        depthData.value=ApiRepository.instance.getDepthData(symbol)
    }
    fun getKlineHistoryData(granularity:String,symbol:String)=viewModelScope.launch {
        klineHistoryData.value=ApiRepository.instance.getKlineHistoryData(granularity,symbol)
    }
    fun getNetValue(symbol:String)=viewModelScope.launch {
        netValueData.value=ApiRepository.instance.getNetValue(symbol)
    }
}