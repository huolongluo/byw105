package huolongluo.byw.byw.ui.activity.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.legend.api.ApiRepository
import com.android.legend.model.BaseResponse
import com.android.legend.model.CommonResult
import com.android.legend.model.config.CurrencyPairBean
import huolongluo.byw.byw.share.Event
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class MainViewModel : ViewModel() {
    val currencyPairData = MutableLiveData<CommonResult<BaseResponse<List<CurrencyPairBean>>>>()//本地维护的币对列表数据

    fun getCurrencyPair() = viewModelScope.launch {
        currencyPairData.value = ApiRepository.instance.getCurrencyPair()
    }

    fun logout() = viewModelScope.launch {
        ApiRepository.instance.logout()
        EventBus.getDefault().post(Event.exitApp())
    }
}