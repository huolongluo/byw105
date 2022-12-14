package com.legend.modular_contract_sdk.component.market_listener

import androidx.lifecycle.MutableLiveData
import com.legend.modular_contract_sdk.api.ModularContractSDK
import com.legend.modular_contract_sdk.repository.model.LastDeal
import com.legend.modular_contract_sdk.repository.model.PositionAndOrder
import com.legend.modular_contract_sdk.utils.JsonUtil
import com.orhanobut.logger.Logger
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Exception

data class MarketListener(
        val marketSubscribeType: MarketSubscribeType,
        val liveData: MutableLiveData<MarketData>
) {

    override fun hashCode(): Int {
        return marketSubscribeType.hashCode()
    }
}

object MarketListenerManager {

    var mSubscribeList = mutableListOf<MarketListener>()

    var mReconnectionJob: Job? = null

    var mUserToken: String? = null

    var mReconnectTimeA = 1
    var mReconnectTimeB = 2

    fun start() {
        WebSocketUtil.init()
    }

    fun checkConnection(){
        if (WebSocketUtil.webSocket == null && ModularContractSDK.wsHost.isNotEmpty() && !WebSocketUtil.isConnecting()){
            WebSocketUtil.init()
        }
    }

    fun <T : MarketSubscribeType> subscribe(
            marketSubscribeType: T,
            liveData: MutableLiveData<MarketData>
    ): MarketListener {
        reconnectionWebSocketNow()
        checkConnection()
        var listener = MarketListener(marketSubscribeType, liveData)
        mSubscribeList.add(listener)
        if (marketSubscribeType.needSendSubscribe) {
            WebSocketUtil.subscribe(marketSubscribeType)
        }
        return listener
    }

    fun unsubscribe(
            listener: MarketListener
    ) {
        reconnectionWebSocketNow()
        checkConnection()
        mSubscribeList.remove(listener)

        var isContains = mSubscribeList.map {
            it.marketSubscribeType
        }.contains(listener.marketSubscribeType)


        if (!isContains) {
            WebSocketUtil.unsubscribe(listener.marketSubscribeType)
        }

    }

    fun login(userToken: String) {
        mUserToken = userToken
        WebSocketUtil.login(userToken)
    }

    fun logout(userToken: String) {
        WebSocketUtil.logout(userToken)
    }

    fun onMessage(msg: String) {
        var baseData = JsonUtil.fromJsonToObject(msg, BaseData::class.java)

        // ??????????????????
        baseData?.data?.apply {
            when (baseData.biz + baseData.type) {
                MarketSubscribeType.FundingRate.getSymbol() -> {

                    val fundingRate = parseMessage(
                            JSONObject(msg).getJSONObject("data").toString(),
                            FundingRate::class.java
                    )

                    val liveDataList =
                            findLiveData(baseData, MarketSubscribeType.FundingRate.getSymbol())

                    liveDataList.forEach { liveData ->
                        liveData.postValue(fundingRate)
                    }
                }
                MarketSubscribeType.MarkPrice.getSymbol(), MarketSubscribeType.IndexPrice.getSymbol() -> {
                    val price = parseMessage(
                            JSONObject(msg).getJSONObject("data").toString(),
                            Price::class.java
                    )

                    val liveDataList = findLiveData(
                            baseData,
                            MarketSubscribeType.MarkPrice.getSymbol(),
                            MarketSubscribeType.IndexPrice.getSymbol()
                    )
                    liveDataList.forEach { liveData ->
                        liveData.postValue(price)
                    }

                }
                MarketSubscribeType.Depth.getSymbol() -> {
                    val depth = parseMessage(
                            JSONObject(msg).getJSONObject("data").toString(),
                            Depth::class.java
                    )
                    val liveDataList = findLiveData(baseData, MarketSubscribeType.Depth.getSymbol())
                    liveDataList.forEach { liveData ->
                        liveData.postValue(depth)
                    }
                }
                MarketSubscribeType.Ticker.getSymbol(), MarketSubscribeType.TickerSwap.getSymbol() -> {
                    val ticker = parseMessage(
                            JSONObject(msg).getJSONObject("data").toString(),
                            Ticker::class.java
                    )
                    val liveDataList = findLiveData(
                            baseData,
                            MarketSubscribeType.Ticker.getSymbol(),
                            MarketSubscribeType.TickerSwap.getSymbol()
                    )
                    liveDataList.forEach { liveData ->
                        liveData.postValue(ticker)
                    }

                }
                MarketSubscribeType.Candles.getSymbol(), MarketSubscribeType.CandlesSwap.getSymbol() -> {
                    //{"base":"btc","biz":"indexes","data":[["1618562700000","61596.7","61602.0","61233.2","61316.9","0.0"]],"granularity":"15","quote":"usd","type":"candles_swap","zip":false}
                    val candles = parseMessageByList(
                            //??????data??????????????????
                            JSONObject(msg).getJSONArray("data").toString(),
                            MutableList::class.java
                    )

                    // ?????????K?????????????????? ??????????????????????????????????????????
                    val timeUnit = try {
                        JSONObject(msg).getString("granularity")
                    } catch (e: Exception){
                        e.printStackTrace()
                        ""
                    }

                    val liveDataList = findLiveData(
                            baseData,
                            MarketSubscribeType.Candles.getSymbol(),
                            MarketSubscribeType.CandlesSwap.getSymbol()
                    )
                    candles?.let {
                        var candlesList = it.map {
                            return@map Candles(it as List<String>)
                        }
                        liveDataList.forEach { liveData ->
                            liveData.postValue(CandleList(candlesList, timeUnit))
                        }

                    }

                }
                MarketSubscribeType.LastDeal.getSymbol() -> {
                    val lastDeal = parseMessageByList(
                            //??????data??????????????????
                            JSONObject(msg).getJSONArray("data").toString(),
                            LastDeal::class.java
                    )

                    val liveDataList = findLiveData(
                            baseData,
                            MarketSubscribeType.LastDeal.getSymbol()
                    )

                    lastDeal?.let { data ->

                        var lastDealList: LastDealList = LastDealList(data)

                        liveDataList.forEach { livedata ->
                            livedata.postValue(lastDealList)
                        }
                    }

                }
                MarketSubscribeType.Position.getSymbol(), MarketSubscribeType.PositionChange.getSymbol(), MarketSubscribeType.PositionFinish.getSymbol() -> {
                    val positionAndOrder = parseMessage(
                            JSONObject(msg).getJSONObject("data").toString(),
                            PositionAndOrder::class.java
                    )

                    val liveDataList = findLiveData(
                            baseData,
                            MarketSubscribeType.Position.getSymbol(),
                            MarketSubscribeType.PositionChange.getSymbol(),
                            MarketSubscribeType.PositionFinish.getSymbol(),
                            containSymbol = MarketSubscribeType.AllPosition.getSymbol()
                    )


                    positionAndOrder?.let {
                        liveDataList.forEach { liveData ->
                            liveData.postValue(it)
                        }
                    }

                }
                MarketSubscribeType.Assets.getSymbol() -> {
                    val assets = parseMessage(
                            JSONObject(msg).getJSONObject("data").toString(),
                            Assets::class.java
                    )

                    val liveDataList = findLiveData(
                            baseData,
                            MarketSubscribeType.Assets.getSymbol()
                    )

                    assets?.let {
                        liveDataList.forEach { liveData ->
                            liveData.postValue(it)
                        }
                    }

                }
                else -> {
                }
            }
        }

    }

    /**
     * ???????????????????????????????????????
     * @param symbol ??????Symbol?????????????????????????????????
     * @param containSymbol ?????????????????????Symbol????????? ??????????????????
     */
    private fun findLiveData(
            baseData: BaseData,
            vararg symbol: String,
            containSymbol: String? = null
    ): List<MutableLiveData<MarketData>> {
        var liveDataList = mSubscribeList.filter {

            //
            for (s in symbol) {
                if (it.marketSubscribeType.getSymbol() == s || (!containSymbol.isNullOrEmpty() && it.marketSubscribeType.getSymbol()
                                .contains(containSymbol))
                ) {
                    return@filter true
                }
            }

            return@filter false
        }

        return liveDataList.filter { marketListener ->
            return@filter if (marketListener.marketSubscribeType.needSendSubscribe) {
                (marketListener.marketSubscribeType.biz == baseData.biz &&
                        marketListener.marketSubscribeType.type == baseData.type &&
                        marketListener.marketSubscribeType.base == baseData.base &&
                        marketListener.marketSubscribeType.quote == baseData.quote)
            } else {
                // fixme ???????????????LiveData ??????????????? ?????????Filter????????????filter???????????? 2021-4-21 19:33
                (marketListener.marketSubscribeType.biz == baseData.biz &&
                        marketListener.marketSubscribeType.type == baseData.type) || (marketListener.marketSubscribeType.include && (!containSymbol.isNullOrEmpty()) && marketListener.marketSubscribeType.getSymbol()
                        .contains(containSymbol))
            }


        }.map {
            it.liveData
        }

    }

    fun onWebSocketStateChange(@WebSocketState webSocketState: Int) {
        if (webSocketState > WEB_SOCKET_CONNECTION_OPEN) {
            reconnectionWebSocket()
        } else if (webSocketState == WEB_SOCKET_CONNECTION_OPEN) {
            mReconnectTimeA = 1
            mReconnectTimeB = 2

            mReconnectionJob?.let {
                //??????????????????????????????????????????????????? ??????????????????
                if (it.isActive){
                    it.cancel()
                }
            }

            // ????????????????????????????????????
            for (marketListener in mSubscribeList) {
                val marketSubscribeType = marketListener.marketSubscribeType
                if (marketSubscribeType.needSendSubscribe) {
                    WebSocketUtil.subscribe(marketSubscribeType)
                }
            }
            // ????????????????????????
            if (!mUserToken.isNullOrEmpty()) {
                WebSocketUtil.login(mUserToken!!)
            }
        }
    }

    // ??????
    private fun reconnectionWebSocket() {

        if (mReconnectionJob != null && mReconnectionJob!!.isActive) {
            //????????????????????????????????????????????????????????????
            return
        }
        mReconnectionJob = GlobalScope.launch(Dispatchers.IO) {

            // ????????????????????????????????????,???????????????5??? ????????????
            val delay = if (mReconnectTimeA + mReconnectTimeB > 60) {
                60
            } else {
                mReconnectTimeA + mReconnectTimeB
            }

            mReconnectTimeA = mReconnectTimeB
            mReconnectTimeB = delay

            delay(delay * 1000L)
            Logger.t("web-socket").e("websocket ??????")
            WebSocketUtil.connection()
            mReconnectionJob = null
        }
    }

    private fun reconnectionWebSocketNow() {
        if (WebSocketUtil.mWebSocketConnectionState == WEB_SOCKET_CONNECTION_OPEN
                || WebSocketUtil.mWebSocketConnectionState == WEB_SOCKET_CONNECTION_CONNECTING) {
            return
        }
        if (mReconnectionJob != null && mReconnectionJob!!.isActive) {
            Logger.t("web-socket").e("websocket ????????????")
            WebSocketUtil.connection()
        }

    }

}

