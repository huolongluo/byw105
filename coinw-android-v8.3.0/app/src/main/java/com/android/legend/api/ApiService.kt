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
            // todo 定义网络异常字符串
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
     ***************************** 划转  *****************************
     */

    /**
     * 第一次进入划转页面获取数据
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/account/transfer/data")
    suspend fun getTransferData(@FieldMap body: Map<String, Any>): BaseResponse<BaseBizResponse<TransferBean>>
    /**
     * 划转切换币种获取资产数据（该接口相对data消耗更小，协议相关数据返回null）
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/account/transfer/amount")
    suspend fun getTransferFinance(@FieldMap body: Map<String, Any>): BaseResponse<BaseBizResponse<TransferBean>>
    /**
     * 划转
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/account/transfer")
    suspend fun transfer(@FieldMap body: Map<String, Any>): BaseResponse<BaseBizResponse<TransferResultBean>>
    /**
     * 获取合约划转相关信息，是否实名等
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/contract/mud/contractMudTransfer")
    suspend fun getContractMudInfo(@FieldMap body: Map<String, Any>): BaseResponse<NMEntity>
    /**
     * 获取泥码活动
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/contract/mud/info")
    suspend fun getNMInfo(@FieldMap body: Map<String, Any>): BaseResponse<NMInfoEntity>
    /**
     * 获取划转记录
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/account/transfer/record")
    suspend fun getTransferRecord(@FieldMap body: Map<String, Any>): BaseResponse<BaseBizResponse<Page<TransferRecordBean>>>

    /**
     ***************************** 资产  *****************************
     */
    /**
     * 获取币币资产
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/account/list")
    suspend fun getBbFinanceData(@FieldMap body: Map<String, Any>): BaseResponse<BaseBizResponse<BbFinanceBean>>

    /**
     ***************************** 订单  *****************************
     */
    /**
     * 获取委托列表
     */
    @JvmSuppressWildcards
    @GET( "spot-all/orders")
    suspend fun getOrders(@Query("active") active:Boolean,@Query("before") before:Long,@Query("startAt") startAt:Long?,
                          @Query("endAt") endAt:Long?,@Query("limit") limit:Int,@Query("orderType") orderType:String?,
                          @Query("symbol") symbol:String?,@Query("status") status:String?,@Query("side") side:String?)
            : BaseResponse<Page2<List<OrderItemBean>>>
    /**
     * 交易页下单
     */
    @JvmSuppressWildcards
    @POST( "spot-order/order")
    suspend fun order(@Body body: Map<String, Any>): BaseResponse<OrderResultBean>
    /**
     * 按订单号撤单
     */
    @JvmSuppressWildcards
    @DELETE( "spot-order/order/{orderId}")
    suspend fun cancelOrder(@Path("orderId") orderId:Long): BaseResponse<String>
    /**
     * 获取赠金资产列表
     */
    @JvmSuppressWildcards
    @GET( "app/data/task/giveCoinInfo")
    suspend fun getZjList(@QueryMap params:Map<String,Any>): BaseResponse<ZjFinanceInfo>

    /**
     ***************************** k线  *****************************
     */
    /**
     * 领取赠金
     */
    @JvmSuppressWildcards
    @GET( "app/data/task/grantGiveCoin")
    suspend fun useZj(@QueryMap params: Map<String, Any>): BaseResponse<String>
    /**
     * 获取k线24h数据
     */
    @GET( "spot-all/market/stats/{symbol}")
    suspend fun get24HData(@Path("symbol") symbol:String): BaseResponse<X24HData>
    /**
     * 获取最新成交数据
     */
    @JvmSuppressWildcards
    @GET( "spot-all/trade_limit_fills")
    suspend fun getLatestData(@QueryMap params:Map<String,Any>): BaseResponse<List<XLatestDeal>>
    /**
     * 获取行情快照（深度数据）
     * level2:LEVEL2_20,LEVEL2_50,LEVEL2_100
     * 返回的数据为最小粒度，精度我们自己控制
     */
    @GET( "spot-all/market/order_book/{level2}")
    suspend fun getDepthData(@Path("level2") level2:String,@Query("symbol") symbol:String): BaseResponse<XDepthData>
    /**
     * 获取k线历史数据
     */
    @JvmSuppressWildcards
    @GET( "spot-all/market/candles")
    suspend fun getKlineHistoryData(@QueryMap params:Map<String,Any>): BaseResponse<List<List<String>>>
    /**
     * 读取etf净值和费率
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "/app/etf/netValue")
    suspend fun getNetValue(@FieldMap params:Map<String,Any>): BaseResponse<BaseResponse<NetValueBean>>

    /**
     ***************************** 配置  *****************************
     */
    /**
     * 读取币对列表数据用于本地维护
     */
    @JvmSuppressWildcards
    @GET( "app/tradeMapping/get")
    suspend fun getCurrencyPair(@QueryMap params:Map<String,Any>): BaseResponse<BaseResponse<List<CurrencyPairBean>>>

    /**
     ***************************** 登录  *****************************
     */
    /**
     * 阿里人机验证成功后需要调用该接口
     */
    @JvmSuppressWildcards
    @GET( "app/nvcVerify.html")
    suspend fun aliVerify(@QueryMap body: Map<String, Any>): BaseResponse<NVCResult>
    /**
     * 阿里人机验证成功后需要调用该接口
     */
    @JvmSuppressWildcards
    @GET( "app/findLoginPassword/nvcAndContactVerify.html")
    suspend fun aliVerifyFindPwd(@QueryMap body: Map<String, Any>): BaseResponse<NVCResult>
    /**
     * 登录
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/userLogin.html")
    suspend fun login(@FieldMap params:Map<String,Any>): BaseResponse<LoginBean>
    /**
     * 注册
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/userRegister.html")
    suspend fun register(@FieldMap params:Map<String,Any>): BaseResponse<LoginBean>
    /**
     * 发送短信验证码
     */
    @JvmSuppressWildcards
    @GET("app/sendMessageCode.html")
    suspend fun sendSms(@QueryMap body: Map<String, Any>): BaseResponse<NVCResult>
    /**
     * 发送邮箱验证码
     */
    @JvmSuppressWildcards
    @GET("app/sendMailCode.html")
    suspend fun sendLoginEmail(@QueryMap body: Map<String, Any>): BaseResponse<NVCResult>
    /**
     * 验证注册的账号
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("app/checkAndSendCode.html")
    suspend fun checkRegisterAccount(@FieldMap body: Map<String, Any>): BaseResponse<BaseResponse<String>>
    /**
     * 验证注册的验证码
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("app/userRegisterCode.html")
    suspend fun checkRegisterCode(@FieldMap body: Map<String, Any>): BaseResponse<NVCResult>
    /**
     * 重置登录密码验证验证码
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/userNewFindPassword.html")
    suspend fun resetPwdVerifyCode(@FieldMap params:Map<String,Any>): BaseResponse<BaseResponse<String>>
    /**
     * 重置登录密码
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/userUpdateLoginPassword.html")
    suspend fun resetPwd(@FieldMap params:Map<String,Any>): BaseResponse<BaseResponse<String>>
    /**
     * 修改登录密码
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/pwd/modify")
    suspend fun modifyLoginPwd(@FieldMap params:Map<String,Any>): BaseResponse<NVCResult>
    /**
     * 登出
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/loginOut")
    suspend fun logout(@FieldMap params:Map<String,Any>): BaseResponse<Void>

    /**
     * 绑定邮箱
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/bindMail")
    suspend fun bindEmail(@FieldMap body: Map<String, Any>): BaseResponse<CommonBean>

    /**
     * Bank列表
     */
    @JvmSuppressWildcards
    @GET( "app/getWithdrawBankList")
    suspend fun getWithdrawBankList(@QueryMap body: Map<String, Any>): BaseResponse<BankListBean>

    /**
     * 发送验证码(未输入人机校验)
     */
    @JvmSuppressWildcards
    @GET(UrlConstants.GET_SMS)
    suspend fun sendSmsNoManMachine(@QueryMap body: Map<String, Any>): BaseResponse<CommonBean>
    /**
     * 提现
     */
    @JvmSuppressWildcards
    @GET(UrlConstants.WithDrawCny)
    suspend fun withDrawCny(@QueryMap body: Map<String, Any>): BaseResponse<CommonBean>
    /**
     * 海外高级kyc认证
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/kyc/overseas/updateKycStatus.html")
    suspend fun overseaSeniorKyc(@FieldMap body: Map<String, Any>): BaseResponse<CommonBean>
    /**
     * 读取区号国家列表
     */
    @JvmSuppressWildcards
    @GET("open/coinw/common/countrys")
    suspend fun getAreaCode(@QueryMap body: Map<String, Any>): BaseResponse<List<AreaCodeBean>>
    /**
     ***************************** 行情  *****************************
     */
    /**
     * 分区接口列表
     */
    @GET( "open/coinw/trade/app/partiton/list?excludeType=1000,1003")//web端默认不排除，全部展示分区，app 需要排除 1000和1003 新币榜和涨幅榜
    suspend fun getMarketArea(): BaseResponse<ArrayList<MarketAreaBean>>
    /**
     * 分区对应币对(使用的旧接口)
     */
    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST( "app/exchangeMarket.html")
    suspend fun getMarketPair(@FieldMap body: Map<String, Any>): BaseResponse<MarketResult.SubMarketResult<MarketResult.Market>>

    /**
     * 首页公告
     */
    @GET("open/coinw/notice/hp/get")
    suspend fun fetchHomeNotice(@Query("size") size: Int = AppConstants.UI.DEFAULT_PAGE_SIZE): BaseListResponse<Notice>

    /**
     * 首页公告
     */
    @FormUrlEncoded
    @POST("app/bannerList.html")
    suspend fun fetchHomeBanner(@Field("type") type: String = "9", @Field("body") body: String = OkhttpManager.getBody()): BaseResponse<BannerBean>

    /**
     * 用户点击活动banner上报服务端
     */
    @GET("public/activity/saveActivityRecord?")
    suspend fun postClickActivityBanner(@Query("type") type: String, @Query("parentType") parentType: String, @Query("channel") channel: String?): BaseResponse<String>

    /**
     * 首页币种列表（推荐区，主流帮，新币帮）
     */
    @GET("/open/coinw/trade/indexFixedPartition")
    suspend fun fetchHomeCoinList(@Query("body") body: String = OkhttpManager.getBody()): BaseResponse<HomePairList>

    /**
     * 首页动态菜单
     */
    @FormUrlEncoded
    @POST("app/entrance/home.html")
    suspend fun fetchDynamicMenu(@Field("type") type: String = "1"): BaseResponse<DynamicHomeMenu>

    /**
     ***************************** 首页  *****************************
     */
    /**
     * 获取新币列表
     */
    @JvmSuppressWildcards
    @GET("open/currency/calendar/list")
    suspend fun getNewCoins(@QueryMap body: Map<String, Any>): BaseResponse<MutableList<NewCoinBean>>


    /**
     * 获取任务中心Banner
     */
    @JvmSuppressWildcards
    @GET("/open/coinw/banner/list?types=APP_BANNER_4")
    suspend fun getTaskCenterBanner(): BaseResponse<TaskCenterBannerBean>

    /**
     * 获取任务中心任务列表
     */
    @JvmSuppressWildcards
    @GET("/app/data/task/getTaskList")
    suspend fun getTaskCenterList(@QueryMap body: Map<String, Any>): BaseResponse<TaskAllBean>

    /**
     * 获取任务中心我的任务列表
     */
    @JvmSuppressWildcards
    @GET("/app/data/task/getUserTask")
    suspend fun getMyTaskList(@QueryMap body: Map<String, Any>): BaseResponse<MyTaskAllBean>

    /**
     * 任务中心领取任务
     */
    @JvmSuppressWildcards
    @GET("/app/data/task/claimedTask")
    suspend fun getMyTask(@QueryMap body: Map<String, Any>): BaseResponse<GetMyTaskBean>


    /**
     * 获取理财产品列表 productType 1 : 常规产品  2：活动产品
     * productClassifiy
     *   活期CURRENT_FINANCIAL
     *定期 REGULAR_FINANCIAL
     */
    @GET("/financial/v1/invest/getFinancialList")
    suspend fun fetchEarnProductList(@Query("productClassifiy") earnType: String, @Query("productType") productType: String,
                                     @Query("page") page: Int, @Query("pageSize") pageSize: Int): BaseResponse<McBasePage<EarnProduct>>

    /**
     * 站内信详情
     */
    @JvmSuppressWildcards
    @GET("/userLetter/info")
    suspend fun getMailDetails(@QueryMap body: Map<String, Any>): BaseResponse<MailBean>
    /**
     * 站内信未读数量
     */
    @JvmSuppressWildcards
    @GET("/userLetter/getUnReadCount")
    suspend fun getMailUnread(@QueryMap body: Map<String, Any>): BaseResponse<MailUnreadBean>
    /**
     * 理财收益信息
     */
    @GET("/financial/v1/invest/getProfitlInfo")
    suspend fun fetchEarnProfit(): BaseResponse<EarnProfit>

    /**
     * 申购
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
     * 理财账户页面 管理理财账户
     */
    @GET("/financial/v1/invest/getFinancialIncomeList")
    suspend fun fetchMyEarnList(@Query("currencyId") currencyId: Int, @Query("productClassifiy") earnTimeLimitType: String, @Query("financialType") financialType: String): BaseResponse<List<EarnProduct>>

    /**
     * 理财获取账户余额接口 GET
     */
    @GET("/financial/v1/account/getBalance")
    suspend fun fetchEarnAccountBalance(@Header("currencyId") currencyId: String): BaseResponse<EarnAccountCoin>


    /**
     * 赎回
     */
    @POST("/financial/v1/redemption")
    suspend fun redemption(@Body body: RequestBody): BaseResponse<Void>

    /**
     * 理财账户 所有币种余额
     */
    @GET("/financial/v1/account/list")
    suspend fun fetchEarnAccountCoins(): BaseResponse<List<EarnAccountCoin>>

    /**
     * type: 交易类型 SUBSCRIPTION("申购"),REDEMPTION("赎回"),PROFIT("收益")
     * productClassifiy|产品分类  1 : 活期理财 2：定期理财 3：混合理财
     * currencyId|币种 id ：1
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
