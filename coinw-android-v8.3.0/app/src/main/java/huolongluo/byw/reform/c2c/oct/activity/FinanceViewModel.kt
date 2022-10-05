package huolongluo.byw.reform.c2c.oct.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.legend.api.ApiRepository
import com.android.legend.model.BaseBizResponse
import com.android.legend.model.CommonResult
import com.android.legend.model.finance.BbFinanceBean
import kotlinx.coroutines.launch

//资产模块
class FinanceViewModel :ViewModel(){
    val bbFinanceData=MutableLiveData<CommonResult<BaseBizResponse<BbFinanceBean>>>()

    fun getBbFinanceData(account:String)=viewModelScope.launch {
        bbFinanceData.value=ApiRepository.instance.getBbFinanceData(account)
    }
}