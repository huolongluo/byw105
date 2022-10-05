package com.legend.modular_contract_sdk.repository.setvices

import com.legend.modular_contract_sdk.component.net.RetrofitInstance
import com.legend.modular_contract_sdk.repository.model.*
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * 体验金接口
 */
interface ExperienceGoldService {
    companion object {

        private val mExperienceGoldService by lazy {
            RetrofitInstance.create(ExperienceGoldService::class.java)
        }

        fun instance(): ExperienceGoldService = mExperienceGoldService
    }


    /**
     * 体验金列表
     */
    @GET("/v1/futuresc/simulated/trade/listGold")
    suspend fun fetchExperienceGoldList(@Query("status") status: Int?, @Query("sortType") sortType: Int): BaseListResp<ExperienceGold>

    /**
     * 交易页体验金列表
     */
    @GET("/v1/futuresc/simulated/trade/listGoldByTrade")
    suspend fun fetchExperienceGoldList(@Query("instrument") instrument: String): BaseListResp<ExperienceGold>

    /**
     * 历史体验金列表 带分页
     */
    @GET("/v1/futuresc/simulated/trade/listHistoryGold")
    suspend fun fetchHistoryExperienceGoldList(@Query("status") status: Int,@Query("page") page: Int, @Query("rows") rows: Int): BaseResp<McBasePage<ExperienceGold>>


    /**
     * 限价/市价开仓
     */
    @POST("/v1/futuresc/simulated/trade/{instrument}/open")
    suspend fun placeOrder(
            @Path("instrument") instrument: String,
            @Body body: RequestBody
    ): BaseResp<Any>

    /**
     * 手动平仓
     */
    @POST("/v1/futuresc/simulated/trade/{instrument}/close/{id}")
    suspend fun closeOrder(
            @Path("instrument") instrument: String, @Path("id") id: Long
    ): BaseResp<Any>


    /**
     * 反手
     */
    @POST("/v1/futuresc/simulated/trade/{instrument}/closeAndOpenReverse/{id}")
    suspend fun closeAndOpenReverseOrder(
            @Path("instrument") instrument: String, @Path("id") id: Long
    ): BaseResp<Any>


    /**
     * 部分平仓
     */
    @POST("/v1/futuresc/simulated/trade/{instrument}/closePart/{id}")
    suspend fun closePartOrder(
            @Path("instrument") instrument: String,
            @Path("id") id: Long,
            @Body body: RequestBody
    ): BaseResp<Void>

    /**
     * 加仓
     */
    @POST("/v1/futuresc/simulated/trade/appendMargin/{id}")
    suspend fun addPosition(@Path("id") id: Long, @Body body: RequestBody): BaseResp<Void>


    /**
     * 全部平仓
     */
    @FormUrlEncoded
    @POST("/v1/futuresc/simulated/trade/close/all/{positionModel}")
    suspend fun closeAllOrder(
            @Path("positionModel") positionModel: String,
            @Field("contractType") contractType: String = "1"
    ): BaseResp<Void>

    /**
     * 手动撤销单个订单
     */
    @POST("/v1/futuresc/simulated/trade/{instrument}/cancel/{id}")
    suspend fun cancelOrder(
            @Path("instrument") instrument: String, @Path("id") id: Long
    ): BaseResp<Any>

    /**
     * 手动撤销所有订单
     */
    @FormUrlEncoded
    @POST("/v1/futuresc/simulated/trade/cancel/all/{positionModel}")
    suspend fun cancelAllOrder(
            @Path("positionModel") positionModel: Int,
            @Field("posType") posType: String,
            @Field("contractType") contractType: Int
    ): BaseResp<Any>

    /**
     * 获取历史仓位列表
     */
    @GET("/v1/futuresc/simulated/trade/finish")
    suspend fun fetchHistoryOrderList(
            @Query("page") page: Int, @Query("rows") rows: Int,
            @Query("contractType") contractType: Int
    ): BaseResp<McBasePage<PositionAndOrder>>

    /**
     * 获取实盘当前仓位或者计划委托仓位列表数据
     */
    @GET("/v1/futuresc/simulated/trade/positions")
    suspend fun fetchPositionAndOrderList(
            @Query("positionType") positionType: String, @Query("page") page: Int, @Query("rows") rows: Int,
            @Query("positionModel") positionModel: Int?, @Query("contractType") contractType: Int
    ): BaseResp<McBasePage<PositionAndOrder>>

    /**
     * 修改止盈止损
     */
    @POST("/v1/futuresc/simulated/trade/{instrument}/changeStopPrice/{id}")
    suspend fun modifyPositionStopProfitAndLoss(
            @Path("instrument") instrument: String,
            @Path("id") id: String,
            @Body body: RequestBody
    ): BaseResp<Void>

}