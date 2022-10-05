package com.legend.modular_contract_sdk.component.market_listener

import android.content.Context
import androidx.annotation.IntDef
import androidx.core.content.edit
import com.legend.modular_contract_sdk.BuildConfig
import com.legend.modular_contract_sdk.api.ModularContractSDK
import com.legend.modular_contract_sdk.component.net.RetrofitInstance
import com.legend.modular_contract_sdk.utils.ToastUtils
import com.orhanobut.logger.Logger
import okhttp3.*
import okio.ByteString
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*
import java.util.concurrent.TimeUnit

const val WEB_SOCKET_CONNECTION_NONE = 998
const val WEB_SOCKET_CONNECTION_CONNECTING = 999
const val WEB_SOCKET_CONNECTION_OPEN = 1000
const val WEB_SOCKET_CONNECTION_ERROR = 1001
const val WEB_SOCKET_CONNECTION_CLOSED = 1002
const val WEB_SOCKET_CONNECTION_CLOSING = 1003


@IntDef(
        WEB_SOCKET_CONNECTION_NONE,
        WEB_SOCKET_CONNECTION_OPEN,
        WEB_SOCKET_CONNECTION_ERROR,
        WEB_SOCKET_CONNECTION_CLOSED,
        WEB_SOCKET_CONNECTION_CLOSING,
        WEB_SOCKET_CONNECTION_CONNECTING
)
@Retention(RetentionPolicy.SOURCE)
annotation class WebSocketState

object WebSocketUtil : WebSocketListener() {

    private val sharedPreferences by lazy {
        ModularContractSDK.context?.getSharedPreferences("mc_sdk_user_config", Context.MODE_PRIVATE)
    }

    @WebSocketState
    var mWebSocketConnectionState = WEB_SOCKET_CONNECTION_NONE


    var webSocket: WebSocket? = null

    var client = OkHttpClient.Builder()
            .apply {
                RetrofitInstance.createSSLSocketFactory()?.let {
                    sslSocketFactory(it, RetrofitInstance.TrustAllManager)
                }
            }
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .pingInterval(30, TimeUnit.SECONDS)
            .build()

    var uuid:String = ""

    var reason:String = "" // 重连原因

    init {

        sharedPreferences?.apply {
            uuid = getString("mc_sdk_uuid","")
            if (uuid.isEmpty()){
                edit {
                    uuid = UUID.randomUUID().toString()
                    putString("mc_sdk_uuid", uuid)
                }
            }
        }

    }

    fun init() {
        connection()
    }

    fun <T : MarketSubscribeType> subscribe(marketSubscribeType: T) {
        sendMessage(parseSubscribeType(marketSubscribeType))
    }

    fun unsubscribe(marketSubscribeType: MarketSubscribeType) {
        sendMessage(parseUnsubscribeType(marketSubscribeType))
    }

    fun login(userToken: String) {
        sendMessage("{\"event\":\"signin\",\"params\":{\"token\":\"${userToken}\"}}")
//        sendMessage("{\"event\":\"signin\",\"params\":{\"token\":\"61d426a312cf8a1e3919d43f1f5967e14d95d61cda8654fc245983d8a2f40f74\"}}")
    }

    fun logout(userToken: String) {
        sendMessage("{\"event\":\"signout\",\"params\":{\"token\":\"${userToken}\"}}")
    }

    fun connection() {

        // 线上安卓端WebSocket建链次数过多,以下参数用来识别建链次数过多的原因
        val connectType = if (mWebSocketConnectionState > WEB_SOCKET_CONNECTION_OPEN){
            "Reconnect reason : $reason"
        } else {
            "First connect"
        }

        val userAgent = "Company:${android.os.Build.MANUFACTURER}, " +
                "Product:${android.os.Build.PRODUCT}, " +
                "Brand:${android.os.Build.BRAND}, " +
                "Model:${android.os.Build.MODEL}, " +
                "Device:${android.os.Build.DEVICE}," +
                "SDK_INT:${android.os.Build.VERSION.SDK_INT}," +
                "VERSION:${android.os.Build.VERSION.RELEASE}"

        val headers: Headers = Headers.Builder()
                .addUnsafeNonAscii("connect-type", connectType)
                .addUnsafeNonAscii("device-id", uuid)
                .addUnsafeNonAscii("X-User-Agent", userAgent)
                .build()

        Logger.t("web-socket").d("connectType = $connectType, \n userAgent = $userAgent \n uuid = $uuid")

        try{
            var request = Request.Builder()
                    .headers(headers)
                    .url(ModularContractSDK.wsHost)
                    .build()
            client.newWebSocket(request, this)
        } catch (e:Exception){
            // java.lang.IllegalArgumentException:
            // Unexpected char 0x7ea2 at 28 in X-User-Agent
            // value: Company:xiaolajiao, Product:红辣椒8X, Brand:xiaolajiao, Model:20190325D, Device:D40K,SDK_INT:28,VERSION:9
            try {
                var request = Request.Builder()
                        .url(ModularContractSDK.wsHost)
                        .build()
                client.newWebSocket(request, this)
            } catch (e: Exception){
                if (BuildConfig.DEBUG){
                    ToastUtils.showShortToast(e.message)
                }
                e.printStackTrace()
            }

        }


        mWebSocketConnectionState = WEB_SOCKET_CONNECTION_CONNECTING
    }

    private fun sendMessage(msg: String) {
        webSocket?.apply {
            Logger.t("web-socket").d("sendMessage $msg")
            send(msg)
        }
    }

    fun disconnect() {
        webSocket?.apply {
            close(0, "正常关闭")
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Logger.t("web-socket").d("onClosed")
        this.reason = reason
        mWebSocketConnectionState = WEB_SOCKET_CONNECTION_CLOSED
        MarketListenerManager.onWebSocketStateChange(mWebSocketConnectionState)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Logger.t("web-socket").d("onClosing")
        this.reason = reason
        mWebSocketConnectionState = WEB_SOCKET_CONNECTION_CLOSING
        MarketListenerManager.onWebSocketStateChange(mWebSocketConnectionState)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Logger.t("web-socket").e(t, "onFailure ${t.message}")
        reason = t.toString()
        try {
            mWebSocketConnectionState = WEB_SOCKET_CONNECTION_ERROR
            MarketListenerManager.onWebSocketStateChange(mWebSocketConnectionState)
            webSocket.close(1001, t.message)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Logger.t("web-socket").d("onMessage $text")
        if (text.isNotEmpty() && !text.contains("{\"result\":true}")) {
            try {
                MarketListenerManager.onMessage(text)
            } catch (e: Exception) {
                Logger.e(e, e.message ?: "")
            }

        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
//        Logger.t("web-socket").d("onMessage ${String(bytes.toByteArray())}")
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Logger.t("web-socket").d("onOpen")
        this.webSocket = webSocket
        mWebSocketConnectionState = WEB_SOCKET_CONNECTION_OPEN
        MarketListenerManager.onWebSocketStateChange(mWebSocketConnectionState)
    }

    fun isConnecting() = mWebSocketConnectionState == WEB_SOCKET_CONNECTION_CONNECTING || mWebSocketConnectionState == WEB_SOCKET_CONNECTION_NONE


}