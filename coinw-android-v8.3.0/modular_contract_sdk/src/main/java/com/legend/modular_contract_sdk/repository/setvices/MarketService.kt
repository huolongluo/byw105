package com.legend.modular_contract_sdk.repository.setvices

import androidx.media.AudioAttributesCompat
import com.legend.modular_contract_sdk.component.net.RetrofitInstance
import com.legend.modular_contract_sdk.repository.model.*
import com.legend.modular_contract_sdk.ui.contract.ContractType
import okhttp3.RequestBody
import retrofit2.http.*

interface MarketService {

    companion object {

        private val mMarketService: MarketService
            by lazy { RetrofitInstance.create(MarketService::class.java) }

        fun instance(): MarketService = mMarketService
    }

    @GET("/v1/markets/quotes/swap/{currency}/candles")
    suspend fun fetchCandles(
        @Path("currency") currency: String,
        @Query("granularity") timeUnit: String
    ): BaseListResp<List<String>>

    @GET("/v1/markets/rates")
    suspend fun fetchRate(): BaseResp<USDRate>

    @GET("/v1/futuresc/public/instruments")
    suspend fun fetchProducts(): BaseListResp<Product>

    /**
     * 限价/市价开仓
     */
    @POST("v1/futuresc/thirdClient/trade/{instrument}/open")
    suspend fun placeOrder(
        @Path("instrument") instrument: String,
        @Body body: RequestBody
    ): BaseResp<Any>

    /**
     * 获取实盘当前仓位或者计划委托仓位列表数据
     */
    @GET("/v1/futuresc/thirdClient/trade/positions")
    suspend fun fetchPositionAndOrderList(
        @Query("positionType") positionType: String, @Query("page") page: Int, @Query("rows") rows: Int,
        @Query("positionModel") positionModel: Int, @Query("contractType") contractType: Int
    ): BaseResp<McBasePage<PositionAndOrder>>

    /**
     * 获取历史仓位列表
     */
    @GET("/v1/futuresc/thirdClient/trade/finish")
    suspend fun fetchHistoryOrderList(
        @Query("page") page: Int, @Query("rows") rows: Int,
        @Query("contractType") contractType: Int
    ): BaseResp<McBasePage<PositionAndOrder>>

    /**
     * 手动撤销单个订单
     */
    @POST("/v1/futuresc/thirdClient/trade/{instrument}/cancel/{id}")
    suspend fun cancelOrder(
        @Path("instrument") instrument: String, @Path("id") id: Long
    ): BaseResp<Any>

    /**
     * 手动撤销所有订单
     */
    @FormUrlEncoded
    @POST("/v1/futuresc/thirdClient/trade/cancel/all/{positionModel}")
    suspend fun cancelAllOrder(
        @Path("positionModel") positionModel: Int,
        @Field("posType") posType: String,
        @Field("contractType") contractType: Int
    ): BaseResp<Any>

    /**
     * 手动平仓
     */
    @POST("/v1/futuresc/thirdClient/trade/{instrument}/close/{id}")
    suspend fun closeOrder(
        @Path("instrument") instrument: String, @Path("id") id: Long
    ): BaseResp<Any>

    /**
     * 部分平仓
     */
    @POST("v1/futuresc/thirdClient/trade/{instrument}/closePart/{id}")
    suspend fun closePartOrder(
        @Path("instrument") instrument: String,
        @Path("id") id: Long,
        @Body body: RequestBody
    ): BaseResp<Void>

    /**
     * 加仓
     */
    @POST("v1/futuresc/thirdClient/trade/appendMargin/{id}")
    suspend fun addPosition(@Path("id") id: Long, @Body body: RequestBody): BaseResp<Void>

    /**
     * 全部平仓
     */
    @FormUrlEncoded
    @POST("/v1/futuresc/thirdClient/trade/close/all/{positionModel}")
    suspend fun closeAllOrder(
        @Path("positionModel") positionModel: String,
        @Field("contractType") contractType: String = "1"
    ): BaseResp<Void>


    /**
     * 反手
     */
    @POST("/v1/futuresc/thirdClient/trade/{instrument}/closeAndOpenReverse/{id}")
    suspend fun closeAndOpenReverseOrder(
        @Path("instrument") instrument: String, @Path("id") id: Long
    ): BaseResp<Any>

    /**
     * 修改仓位模式
     */
    @FormUrlEncoded
    @POST("v1/futuresc/thirdClient/trade/changeUserPosition")
    suspend fun modifyPositionMode(
        @Field("positionModel") positionMode: String,
        @Field("layout") layout: String
    ): BaseResp<Any>

    /**
     * 获取用户仓位模式等信息
     */
    @GET("v1/futuresc/thirdClient/trade/getTradeConfig")
    suspend fun fetchUserTreadConfig(): BaseResp<TreadConfig>

    /**
     * 修改止盈止损
     */
    @POST("v1/futuresc/thirdClient/trade/{instrument}/changeStopPrice/{id}")
    suspend fun modifyPositionStopProfitAndLoss(
        @Path("instrument") instrument: String,
        @Path("id") id: String,
        @Body body: RequestBody
    ): BaseResp<Void>
    /**
     * 获取wss域名
     */
    @GET("v1/futuresc/public/urlInfo")
    suspend fun fetchWsHost(): BaseResp<WsHost>

    /**
     * 获取最新成交全量数据
     */
    @GET("v1/futuresc/public/btc/deals")
    suspend fun fetchAllLastDeal(): BaseListResp<LastDeal>


    /**
     * 添加保证金
     */
    @POST("/v1/futuresc/thirdClient/trade/addMargin/{id}")
    suspend fun addMargin(
            @Path("id") id: String,
            @Body body: RequestBody
    ): BaseResp<Void>

    /**
     * 减少保证金
     */
    @POST("/v1/futuresc/thirdClient/trade/reduceMargin/{id}")
    suspend fun minusMargin(
            @Path("id") id: String,
            @Body body: RequestBody
    ): BaseResp<Void>

    @POST("/v1/futuresc/thirdClient/trade/addMoveStopProfitAndLoss")
    suspend fun setMoveTPSL(@Body body: RequestBody):BaseResp<Void>
}