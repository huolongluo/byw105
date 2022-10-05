package com.android.legend.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.legend.api.ApiRepository
import com.android.legend.api.apiService
import com.android.legend.model.CommonResult
import com.android.legend.model.home.*
import com.legend.modular_contract_sdk.base.BaseViewModel
import com.legend.modular_contract_sdk.common.postValueNotNull
import huolongluo.byw.byw.inform.bean.MailUnreadBean
import kotlinx.coroutines.launch
import retrofit2.http.Query

class HomeViewModel : BaseViewModel() {

    var mBannerLiveDate = MutableLiveData<BannerBean>()
    var mNoticeLiveDate = MutableLiveData<List<Notice>>()
    var mCoinListLiveDate = MutableLiveData<HomePairList>()
    var mDynamicMenuLiveDate = MutableLiveData<DynamicHomeMenu>()
    var mPostClickActivityBannerLiveData = MutableLiveData<Banner>()

//    var mRecommendCoinListLiveData = MutableLiveData<List<Pair>>()// 推荐区币种
    var mMainCoinListLiveData = MutableLiveData<List<Pair>>() // 主流榜币种
    var mNewCoinListLiveData = MutableLiveData<List<Pair>>() // 新币榜币种
    var mVolumeCoinListLiveData = MutableLiveData<List<Ticker2>>() // 成交额榜币种
    var mUpCoinListLiveData = MutableLiveData<List<Ticker2>>() // 涨幅榜币种

    var mMailUnreadLiveData = MutableLiveData<CommonResult<MailUnreadBean>>() // 涨幅榜币种

    fun fetchBannerList() {
        request {
            apiService.fetchHomeBanner()
                    .apply {
                        if (this.isReqSuccess()) {
                            mBannerLiveDate.postValueNotNull(this.data)
                        }
                    }
        }
    }

    fun fetchNoticeList() {
        request {
            apiService.fetchHomeNotice()
                    .apply {
                        if (this.isReqSuccess()) {
                            mNoticeLiveDate.postValueNotNull(this.data)
                        }
                    }
        }
    }

    fun fetchCoinList() {
        request {
            apiService.fetchHomeCoinList()
                    .apply {
                        if (this.isReqSuccess()) {
                            mCoinListLiveDate.postValueNotNull(this.data)
                        }

                    }
        }
    }

    fun fetchDynamicMenu() {
        request {
            apiService.fetchDynamicMenu()
                    .apply {
                        if (this.isReqSuccess()) {
                            mDynamicMenuLiveDate.postValueNotNull(this.data)
                        }
                    }
        }
    }

    fun postClickActivityBanner(banner: Banner, type: String, parentType: String, channel: String?){
        request(isShowLoading = true){
            apiService.postClickActivityBanner(type, parentType, channel)
                    .apply {
                        mPostClickActivityBannerLiveData.postValueNotNull(banner)
                    }
        }

    }

    fun getMailUnread()=viewModelScope.launch {
        mMailUnreadLiveData.value=ApiRepository.instance.getMailUnread()
    }
}