package com.android.legend.api

import android.text.TextUtils
import com.android.coinw.api.kx.model.X24HData
import com.android.coinw.api.kx.model.XDepthData
import com.android.coinw.api.kx.model.XLatestDeal
import com.android.legend.exception.BizException
import com.android.legend.model.*
import com.android.legend.model.config.CurrencyPairBean
import com.android.legend.model.earn.*
import com.android.legend.model.finance.BbFinanceBean
import com.android.legend.model.finance.ZjFinanceInfo
import com.android.legend.model.home.BannerBean
import com.android.legend.model.home.DynamicHomeMenu
import com.android.legend.model.home.HomePairList
import com.android.legend.model.home.Notice
import com.android.legend.model.home.newCoin.NewCoinBean
import com.android.legend.model.kline.NetValueBean
import com.android.legend.model.market.MarketAreaBean
import com.android.legend.model.order.OrderItemBean
import com.android.legend.model.order.OrderResultBean
import com.android.legend.model.transfer.TransferBean
import com.android.legend.model.transfer.TransferRecordBean
import com.android.legend.model.transfer.TransferResultBean
import com.android.legend.model.GetMyTaskBean
import com.android.legend.model.MyTaskAllBean
import com.android.legend.model.TaskAllBean
import com.android.legend.model.TaskCenterBannerBean
import com.legend.modular_contract_sdk.repository.model.McBasePage
import huolongluo.byw.R
import huolongluo.byw.byw.base.BaseApp
import huolongluo.byw.byw.bean.BankListBean
import huolongluo.byw.byw.bean.CommonBean
import huolongluo.byw.byw.inform.bean.MailBean
import huolongluo.byw.byw.inform.bean.MailUnreadBean
import huolongluo.byw.byw.net.DomainHelper
import huolongluo.byw.byw.net.UrlConstants
import huolongluo.byw.byw.ui.fragment.contractTab.NMInfoEntity
import huolongluo.byw.byw.ui.fragment.contractTab.nima.NMEntity
import huolongluo.byw.io.AppConstants
import huolongluo.byw.log.Logger
import huolongluo.byw.model.AreaCodeBean
import huolongluo.byw.model.MarketResult
import huolongluo.byw.model.NVCResult
import huolongluo.byw.reform.bean.LoginBean
import huolongluo.byw.util.OkhttpManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import retrofit2.http.*
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

val apiService by lazy {
    RetrofitClient.getService<ApiService>(
            DomainHelper.getDomain().getHost()
    )
}

suspend fun <T : Any> parseResponse(block: suspend () -> BaseResponse<T>): CommonResult<T> {
    return withContext(Dispatchers.IO) {
        try {
            val response = block()
            val message = response.message ?: ""
            if (TextUtils.equals("200",response.code)) {
                CommonResult(true,response.code, response.data, message, null)
            } else {
                CommonResult(false,response.code, response.data, message, BizException(message))
            }
        } catch (e: Throwable) {
            // todo ???????????????????????????
            Logger.getInstance().error(e)
            val notConnectString = BaseApp.appContext.getString(R.string.net_exp)
            when (e) {
                is ConnectException, is UnknownHostException -> CommonResult(false, "-1",null, notConnectString, e)
                is SocketTimeoutException -> CommonResult(false, "-1",null, notConnectString, e)
                else -> CommonResult(false, "-1",null, e.message ?: notConnectString, e)
            }
        }
    }
}

interface ApiService {
    /**
     ***************************** ??????  *****************************
     */

    /**
     * ???????????????????????????????????????
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/account/transfer/data")
    suspend fun getTransferData(@FieldMap body: Map<String, Any>): BaseResponse<BaseBizResponse<TransferBean>>
    /**
     * ??????????????????????????????????????????????????????data???????????????????????????????????????null???
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/account/transfer/amount")
    suspend fun getTransferFinance(@FieldMap body: Map<String, Any>): BaseResponse<BaseBizResponse<TransferBean>>
    /**
     * ??????
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/account/transfer")
    suspend fun transfer(@FieldMap body: Map<String, Any>): BaseResponse<BaseBizResponse<TransferResultBean>>
    /**
     * ????????????????????????????????????????????????
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/contract/mud/contractMudTransfer")
    suspend fun getContractMudInfo(@FieldMap body: Map<String, Any>): BaseResponse<NMEntity>
    /**
     * ??????????????????
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/contract/mud/info")
    suspend fun getNMInfo(@FieldMap body: Map<String, Any>): BaseResponse<NMInfoEntity>
    /**
     * ??????????????????
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/account/transfer/record")
    suspend fun getTransferRecord(@FieldMap body: Map<String, Any>): BaseResponse<BaseBizResponse<Page<TransferRecordBean>>>

    /**
     ***************************** ??????  *****************************
     */
    /**
     * ??????????????????
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/account/list")
    suspend fun getBbFinanceData(@FieldMap body: Map<String, Any>): BaseResponse<BaseBizResponse<BbFinanceBean>>

    /**
     ***************************** ??????  *****************************
     */
    /**
     * ??????????????????
     */
    @JvmSuppressWildcards
    @GET( "spot-all/orders")
    suspend fun getOrders(@Query("active") active:Boolean,@Query("before") before:Long,@Query("startAt") startAt:Long?,
                          @Query("endAt") endAt:Long?,@Query("limit") limit:Int,@Query("orderType") orderType:String?,
                          @Query("symbol") symbol:String?,@Query("status") status:String?,@Query("side") side:String?)
            : BaseResponse<Page2<List<OrderItemBean>>>
    /**
     * ???????????????
     */
    @JvmSuppressWildcards
    @POST( "spot-order/order")
    suspend fun order(@Body body: Map<String, Any>): BaseResponse<OrderResultBean>
    /**
     * ??????????????????
     */
    @JvmSuppressWildcards
    @DELETE( "spot-order/order/{orderId}")
    suspend fun cancelOrder(@Path("orderId") orderId:Long): BaseResponse<String>
    /**
     * ????????????????????????
     */
    @JvmSuppressWildcards
    @GET( "app/data/task/giveCoinInfo")
    suspend fun getZjList(@QueryMap params:Map<String,Any>): BaseResponse<ZjFinanceInfo>

    /**
     ***************************** k???  *****************************
     */
    /**
     * ????????????
     */
    @JvmSuppressWildcards
    @GET( "app/data/task/grantGiveCoin")
    suspend fun useZj(@QueryMap params: Map<String, Any>): BaseResponse<String>
    /**
     * ??????k???24h??????
     */
    @GET( "spot-all/market/stats/{symbol}")
    suspend fun get24HData(@Path("symbol") symbol:String): BaseResponse<X24HData>
    /**
     * ????????????????????????
     */
    @JvmSuppressWildcards
    @GET( "spot-all/trade_limit_fills")
    suspend fun getLatestData(@QueryMap params:Map<String,Any>): BaseResponse<List<XLatestDeal>>
    /**
     * ????????????????????????????????????
     * level2:LEVEL2_20,LEVEL2_50,LEVEL2_100
     * ?????????????????????????????????????????????????????????
     */
    @GET( "spot-all/market/order_book/{level2}")
    suspend fun getDepthData(@Path("level2") level2:String,@Query("symbol") symbol:String): BaseResponse<XDepthData>
    /**
     * ??????k???????????????
     */
    @JvmSuppressWildcards
    @GET( "spot-all/market/candles")
    suspend fun getKlineHistoryData(@QueryMap params:Map<String,Any>): BaseResponse<List<List<String>>>
    /**
     * ??????etf???????????????
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "/app/etf/netValue")
    suspend fun getNetValue(@FieldMap params:Map<String,Any>): BaseResponse<BaseResponse<NetValueBean>>

    /**
     ***************************** ??????  *****************************
     */
    /**
     * ??????????????????????????????????????????
     */
    @JvmSuppressWildcards
    @GET( "app/tradeMapping/get")
    suspend fun getCurrencyPair(@QueryMap params:Map<String,Any>): BaseResponse<BaseResponse<List<CurrencyPairBean>>>

    /**
     ***************************** ??????  *****************************
     */
    /**
     * ????????????????????????????????????????????????
     */
    @JvmSuppressWildcards
    @GET( "app/nvcVerify.html")
    suspend fun aliVerify(@QueryMap body: Map<String, Any>): BaseResponse<NVCResult>
    /**
     * ????????????????????????????????????????????????
     */
    @JvmSuppressWildcards
    @GET( "app/findLoginPassword/nvcAndContactVerify.html")
    suspend fun aliVerifyFindPwd(@QueryMap body: Map<String, Any>): BaseResponse<NVCResult>
    /**
     * ??????
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/userLogin.html")
    suspend fun login(@FieldMap params:Map<String,Any>): BaseResponse<LoginBean>
    /**
     * ??????
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/userRegister.html")
    suspend fun register(@FieldMap params:Map<String,Any>): BaseResponse<LoginBean>
    /**
     * ?????????????????????
     */
    @JvmSuppressWildcards
    @GET("app/sendMessageCode.html")
    suspend fun sendSms(@QueryMap body: Map<String, Any>): BaseResponse<NVCResult>
    /**
     * ?????????????????????
     */
    @JvmSuppressWildcards
    @GET("app/sendMailCode.html")
    suspend fun sendLoginEmail(@QueryMap body: Map<String, Any>): BaseResponse<NVCResult>
    /**
     * ?????????????????????
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("app/checkAndSendCode.html")
    suspend fun checkRegisterAccount(@FieldMap body: Map<String, Any>): BaseResponse<BaseResponse<String>>
    /**
     * ????????????????????????
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("app/userRegisterCode.html")
    suspend fun checkRegisterCode(@FieldMap body: Map<String, Any>): BaseResponse<NVCResult>
    /**
     * ?????????????????????????????????
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/userNewFindPassword.html")
    suspend fun resetPwdVerifyCode(@FieldMap params:Map<String,Any>): BaseResponse<BaseResponse<String>>
    /**
     * ??????????????????
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/userUpdateLoginPassword.html")
    suspend fun resetPwd(@FieldMap params:Map<String,Any>): BaseResponse<BaseResponse<String>>
    /**
     * ??????????????????
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/pwd/modify")
    suspend fun modifyLoginPwd(@FieldMap params:Map<String,Any>): BaseResponse<NVCResult>
    /**
     * ??????
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/loginOut")
    suspend fun logout(@FieldMap params:Map<String,Any>): BaseResponse<Void>

    /**
     * ????????????
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/bindMail")
    suspend fun bindEmail(@FieldMap body: Map<String, Any>): BaseResponse<CommonBean>

    /**
     * Bank??????
     */
    @JvmSuppressWildcards
    @GET( "app/getWithdrawBankList")
    suspend fun getWithdrawBankList(@QueryMap body: Map<String, Any>): BaseResponse<BankListBean>

    /**
     * ???????????????(?????????????????????)
     */
    @JvmSuppressWildcards
    @GET(UrlConstants.GET_SMS)
    suspend fun sendSmsNoManMachine(@QueryMap body: Map<String, Any>): BaseResponse<CommonBean>
    /**
     * ??????
     */
    @JvmSuppressWildcards
    @GET(UrlConstants.WithDrawCny)
    suspend fun withDrawCny(@QueryMap body: Map<String, Any>): BaseResponse<CommonBean>
    /**
     * ????????????kyc??????
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/kyc/overseas/updateKycStatus.html")
    suspend fun overseaSeniorKyc(@FieldMap body: Map<String, Any>): BaseResponse<CommonBean>
    /**
     * ????????????????????????
     */
    @JvmSuppressWildcards
    @GET("open/coinw/common/countrys")
    suspend fun getAreaCode(@QueryMap body: Map<String, Any>): BaseResponse<List<AreaCodeBean>>
    /**
     ***************************** ??????  *****************************
     */
    /**
     * ??????????????????
     */
    @GET( "open/coinw/trade/app/partiton/list?excludeType=1000,1003")//web??????????????????????????????????????????app ???????????? 1000???1003 ?????????????????????
    suspend fun getMarketArea(): BaseResponse<ArrayList<MarketAreaBean>>
    /**
     * ??????????????????(??????????????????)
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/exchangeMarket.html")
    suspend fun getMarketPair(@FieldMap body: Map<String, Any>): BaseResponse<MarketResult.SubMarketResult<MarketResult.Market>>

    /**
     * ????????????
     */
    @GET("open/coinw/notice/hp/get")
    suspend fun fetchHomeNotice(@Query("size") size: Int = AppConstants.UI.DEFAULT_PAGE_SIZE): BaseListResponse<Notice>

    /**
     * ????????????
     */
    @FormUrlEncoded
    @POST("app/bannerList.html")
    suspend fun fetchHomeBanner(@Field("type") type: String = "9", @Field("body") body: String = OkhttpManager.getBody()): BaseResponse<BannerBean>

    /**
     * ??????????????????banner???????????????
     */
    @GET("public/activity/saveActivityRecord?")
    suspend fun postClickActivityBanner(@Query("type") type: String, @Query("parentType") parentType: String, @Query("channel") channel: String?): BaseResponse<String>

    /**
     * ?????????????????????????????????????????????????????????
     */
    @GET("/open/coinw/trade/indexFixedPartition")
    suspend fun fetchHomeCoinList(@Query("body") body: String = OkhttpManager.getBody()): BaseResponse<HomePairList>

    /**
     * ??????????????????
     */
    @FormUrlEncoded
    @POST("app/entrance/home.html")
    suspend fun fetchDynamicMenu(@Field("type") type: String = "1"): BaseResponse<DynamicHomeMenu>

    /**
     ***************************** ??????  *****************************
     */
    /**
     * ??????????????????
     */
    @JvmSuppressWildcards
    @GET("open/currency/calendar/list")
    suspend fun getNewCoins(@QueryMap body: Map<String, Any>): BaseResponse<MutableList<NewCoinBean>>


    /**
     * ??????????????????Banner
     */
    @JvmSuppressWildcards
    @GET("/open/coinw/banner/list?types=APP_BANNER_4")
    suspend fun getTaskCenterBanner(): BaseResponse<TaskCenterBannerBean>

    /**
     * ??????????????????????????????
     */
    @JvmSuppressWildcards
    @GET("/app/data/task/getTaskList")
    suspend fun getTaskCenterList(@QueryMap body: Map<String, Any>): BaseResponse<TaskAllBean>

    /**
     * ????????????????????????????????????
     */
    @JvmSuppressWildcards
    @GET("/app/data/task/getUserTask")
    suspend fun getMyTaskList(@QueryMap body: Map<String, Any>): BaseResponse<MyTaskAllBean>

    /**
     * ????????????????????????
     */
    @JvmSuppressWildcards
    @GET("/app/data/task/claimedTask")
    suspend fun getMyTask(@QueryMap body: Map<String, Any>): BaseResponse<GetMyTaskBean>


    /**
     * ???????????????????????? productType 1 : ????????????  2???????????????
     * productClassifiy
     *   ??????CURRENT_FINANCIAL
     *?????? REGULAR_FINANCIAL
     */
    @GET("/financial/v1/invest/getFinancialList")
    suspend fun fetchEarnProductList(@Query("productClassifiy") earnType: String, @Query("productType") productType: String,
                                     @Query("page") page: Int, @Query("pageSize") pageSize: Int): BaseResponse<McBasePage<EarnProduct>>

    /**
     * ???????????????
     */
    @JvmSuppressWildcards
    @GET("/userLetter/info")
    suspend fun getMailDetails(@QueryMap body: Map<String, Any>): BaseResponse<MailBean>
    /**
     * ?????????????????????
     */
    @JvmSuppressWildcards
    @GET("/userLetter/getUnReadCount")
    suspend fun getMailUnread(@QueryMap body: Map<String, Any>): BaseResponse<MailUnreadBean>
    /**
     * ??????????????????
     */
    @GET("/financial/v1/invest/getProfitlInfo")
    suspend fun fetchEarnProfit(): BaseResponse<EarnProfit>

    /**
     * ??????
     * "productId":3,
    "financialProductInvestRequest":[
    {
    "amount":1111.11,
    "currencyId":50
    },
    {
    "amount":1.2,
    "currencyId":63
    }
    ]
     */
    @POST("/financial/v1/invest/applyFinancial")
    suspend fun buyEarn(@Body buyInfo: RequestBody): BaseResponse<String>

    /**
     * ?????????????????? ??????????????????
     */
    @GET("/financial/v1/invest/getFinancialIncomeList")
    suspend fun fetchMyEarnList(@Query("currencyId") currencyId: Int, @Query("productClassifiy") earnTimeLimitType: String, @Query("financialType") financialType: String): BaseResponse<List<EarnProduct>>

    /**
     * ?????????????????????????????? GET
     */
    @GET("/financial/v1/account/getBalance")
    suspend fun fetchEarnAccountBalance(@Header("currencyId") currencyId: String): BaseResponse<EarnAccountCoin>


    /**
     * ??????
     */
    @POST("/financial/v1/redemption")
    suspend fun redemption(@Body body: RequestBody): BaseResponse<Void>

    /**
     * ???????????? ??????????????????
     */
    @GET("/financial/v1/account/list")
    suspend fun fetchEarnAccountCoins(): BaseResponse<List<EarnAccountCoin>>

    /**
     * type: ???????????? SUBSCRIPTION("??????"),REDEMPTION("??????"),PROFIT("??????")
     * productClassifiy|????????????  1 : ???????????? 2??????????????? 3???????????????
     * currencyId|?????? id ???1
     */
    @GET("/financial/v1/invest/getTransactionRecord")
    suspend fun fetchEarnBill(@Query("type") type: String?,
                              @Query("productClassifiy") earnType: Int?,
                              @Query("currencyId") currencyId: Int?,
                              @Query("page") page: Int,
                              @Query("pageSize") pageSize: Int): BaseResponse<McBasePage<EarnBill>>

    @GET("/financial/v1/open/getCoinTypeList")
    suspend fun fetchEarnCurrencyList():BaseResponse<List<EarnCurrency>>

    @GET("/open/spot/coins")
    suspend fun fetchCurrencyInfo():BaseResponse<List<CurrencyInfo>>
}
