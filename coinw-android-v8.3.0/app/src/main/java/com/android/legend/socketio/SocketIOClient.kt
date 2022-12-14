package com.android.legend.socketio

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import com.android.coinw.test.log.WsLogViewModel
import com.android.coinw.utils.Utilities
import com.android.legend.model.SocketIOResponse
import com.android.legend.model.WSToken
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import huolongluo.byw.BuildConfig
import huolongluo.byw.byw.base.BaseApp
import huolongluo.byw.byw.net.BaseResponse
import huolongluo.byw.byw.net.UrlConstants
import huolongluo.byw.helper.SSLHelper
import huolongluo.byw.log.Logger
import huolongluo.byw.user.UserInfoManager
import huolongluo.byw.util.GsonUtil
import huolongluo.bywx.utils.AppUtils
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.transports.Polling
import io.socket.engineio.client.transports.WebSocket
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap

object SocketIOClient {
    private const val TAG = "SocketIOClient"

    private const val EVENT_SUBSCRIBE = "subscribe"
    private const val EVENT_UNSUBSCRIBE = "unsubscribe"
    private const val EVENT_LOGIN = "login"
    private const val EVENT_LOGOUT = "logout"

    private const val ERROR_SERVER_DISCONNECT = "io server disconnect"

    private lateinit var socket: Socket

    private val listenerMap = ConcurrentHashMap<Pair<String, String>, Emitter.Listener>()

    private val gson = Gson()

    private val handler = Handler(Looper.getMainLooper())

    private val opts = IO.Options()

    private val client = SSLHelper.getBuilder(15, 15, 15).build()

    private var onResetTokenCallback: (() -> String)? = null

    private val logViewModel by lazy { ViewModelProvider(BaseApp.getSelf()).get(WsLogViewModel::class.java) }

    fun setOnResetTokenCallback(callback: () -> String) {
        onResetTokenCallback = callback
    }

    private fun resetToken(token: String) {
        opts.query = "token=$token"
    }

    /**
     * ??????token???????????????null
     * ???http??????????????????5s????????????????????????????????????token??????
     */
    fun getTokenNotNull(): WSToken {
        val token: WSToken? = getToken()
        if (token == null) {
            // ??????token?????????5s?????????
            Thread.sleep(5000)
            return getTokenNotNull()
        }
        return token
    }

    private fun getToken(): WSToken? {
        return try {
            //{"success":true,"code":200,"message":"success","retry":false,"data":{"token":"PUP5bJLMSlpIwfzjnAvisg","endpoint":"wss://ws.coinw.io:80","protocol":"websocket","timestamp":1605184044291,"expiredTime":1605184074291,"pingInterval":10000}}
            //TODO ??????????????????
            val url = if (UserInfoManager.isLogin()) {
                UrlConstants.DOMAIN + "pusher/private-token?uid=${UserInfoManager.getUserInfo().fid}"
            } else {
                UrlConstants.DOMAIN + "pusher/public-token"
            }



            val request = AppUtils
                    .getRequestBuilder(url)
                    .build()
            val response = client.newCall(request).execute()
            val json = response.body?.string()

            val rsp = GsonUtil.json2Obj<BaseResponse<WSToken>>(json, object : TypeToken<BaseResponse<WSToken>>() {}.type)
            rsp?.data
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    @JvmStatic
    fun init() {
        Utilities.serviceQueue.postRunnable {
            val wsToken = getTokenNotNull()
            val url = wsToken.getUrl()

            val token = wsToken.token

            setOnResetTokenCallback {
                getTokenNotNull().token
            }
            connect(url, token)
        }
    }

    @JvmStatic
    fun connect(url: String, token: String) {
        opts.transports = arrayOf(WebSocket.NAME)
        opts.query = "token=$token"
        opts.reconnectionAttempts = Int.MAX_VALUE
        opts.reconnectionDelay = 5000
        opts.reconnectionDelayMax = 10000
        opts.callFactory = client
        opts.webSocketFactory = client
        socket = IO.socket(url, opts)
        socket.on(Socket.EVENT_CONNECT) {
//            Logger.getInstance().debug(TAG, "EVENT_CONNECT")
            listenerMap.keys()
                    .asSequence()
                    .map { it.second }
                    .distinct()
                    .forEach { event ->
                        socket.emit(EVENT_SUBSCRIBE, createSubscribeArgs(event), Ack {
//                            Logger.getInstance().debug(TAG, "subscribe $event ack ${it?.contentToString()}")
                        })
                    }
        }.on(Socket.EVENT_CONNECT_ERROR) {
//            Logger.getInstance().debug(TAG, "EVENT_CONNECT_ERROR ${it?.contentToString()}")
        }.on(Socket.EVENT_ERROR) {
//            Logger.getInstance().debug(TAG, "EVENT_ERROR ${it?.contentToString()}")
        }.on(Socket.EVENT_DISCONNECT) {
//            Logger.getInstance().debug(TAG, "EVENT_DISCONNECT ${it?.contentToString()}")
            if (it[0].toString() == ERROR_SERVER_DISCONNECT) {
                val newToken = onResetTokenCallback?.invoke()
                if (!newToken.isNullOrEmpty()) {
                    resetToken(newToken)
                    socket.connect()
                }
            }
        }.on(Socket.EVENT_RECONNECT) {
//            Logger.getInstance().debug(TAG, "EVENT_RECONNECT ${it?.contentToString()}")
        }.on(Socket.EVENT_RECONNECTING) {
//            Logger.getInstance().debug(TAG, "EVENT_RECONNECTING ${it?.contentToString()}")
//                onReconnectListener?.invoke()
        }.on(Socket.EVENT_RECONNECT_ATTEMPT) {
//            Logger.getInstance().debug(TAG, "EVENT_RECONNECT_ATTEMPT ${it?.contentToString()}")
        }.on(EVENT_SUBSCRIBE) {
//            Logger.getInstance().debug(TAG, "EVENT_SUBSCRIBE ${it?.contentToString()}")
            if (it.isNotEmpty()) {
                listenerMap.values.forEach { l -> l.call(it[0]) }
                // ????????????????????????
                if (BuildConfig.DEBUG || BuildConfig.ENV_DEV) {
                    val response = gson.fromJson(
                            it[0].toString(),
                            SocketIOResponse::class.java
                    )
                    logViewModel.addLog(response)
                }
            }
        }
        socket.connect()
    }

    private fun createSubscribeArgs(event: String): JSONObject {
        val json = JSONObject()
        json.put("op", EVENT_SUBSCRIBE)
        json.put("args", event)
        return json
    }

    /**
     * ???????????????????????????????????????
     */
    @JvmStatic
    fun destroy() {
        handler.removeCallbacksAndMessages(null)
        listenerMap.clear()
        if (::socket.isInitialized) {
            socket.off()
            socket.disconnect()
        }
    }

    /**
     * ??????????????????????????????app??????????????????????????????
     */
    @JvmStatic
    fun connect() {
        if (::socket.isInitialized) socket.connect()
    }

    /**
     * ??????????????????????????????????????????
     * ????????????????????????app???????????????????????????
     */
    @JvmStatic
    fun disconnect() {
        if (::socket.isInitialized) socket.disconnect()
    }

    @JvmStatic
    fun isConnected() = if (::socket.isInitialized) socket.connected() else false

    @JvmStatic
    fun login() {
        Utilities.serviceQueue.postRunnable {
            val wsToken = getTokenNotNull()
            val token = wsToken.token

            val json = JSONObject()
            json.put("op", EVENT_LOGIN)
            json.put("args", token)
            if (::socket.isInitialized) {
                socket.emit(EVENT_LOGIN, json, Ack {
//                    Logger.getInstance().debug(TAG, "subscribe $EVENT_LOGIN ack ${it?.contentToString()}")
                    // todo ??????ack???????????????????????????
                })
            } else {
                connect(wsToken.getUrl(), token)
            }
        }
    }

    @JvmStatic
    fun logout() {
        if (::socket.isInitialized) {
            val json = JSONObject()
            json.put("op", EVENT_LOGOUT)
            socket.emit(EVENT_LOGOUT, json, Ack {
//                Logger.getInstance().debug(TAG, "subscribe $EVENT_LOGOUT ack ${it?.contentToString()}")
                // todo ??????ack???????????????????????????
            })
        }
    }

    /**
     * ????????????????????????????????????????????????????????????????????????subscribeId??????
     */
    @JvmStatic
    fun <T> subscribe(
            subscribeId: String,
            event: String,
            typeToken: TypeToken<T>,
            callback: (T) -> Unit
    ) {
        val listener = Emitter.Listener { args ->
            val response = gson.fromJson(
                    args[0].toString(),
                    SocketIOResponse::class.java
            )
            if (response.channel == event) {
                if (BuildConfig.ENV_DEV) {
                    try {
                        val data = gson.fromJson<T>(response.data, typeToken.type)
                        if (data != null) {
                            handler.post { callback(data) }
                        }
                    } catch (t: Throwable) {
//                        Logger.getInstance().debug(TAG, "" + response.data)
                        t.printStackTrace()
                    }
                } else {
                    try {
                        val data = gson.fromJson<T>(response.data, typeToken.type)
                        if (data != null) {
                            handler.post { callback(data) }
                        }
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                }

            }
        }
        val count = listenerMap.keys.count { it.second == event }
        // ??????listenerMap????????????????????????????????????emit???????????????
        if (count == 0 && ::socket.isInitialized) {
            val json = createSubscribeArgs(event)
            Logger.getInstance().debug(TAG, "SocketIOClient-subscribe $json")
            socket.emit(EVENT_SUBSCRIBE, json, Ack {
                Logger.getInstance().debug(TAG, "subscribe $event ack ${it?.contentToString()}")
                // todo ??????ack???????????????????????????

            })
        }
        if (!listenerMap.containsKey(subscribeId to event)) {
            listenerMap[subscribeId to event] = listener
        }

    }

    fun dealData() {

    }

    /**
     * ????????????
     */
    @JvmStatic
    fun unsubscribe(subscribeId: String, event: String) {
        // ??????????????????
        if (listenerMap.containsKey(subscribeId to event)) {
            listenerMap.remove(subscribeId to event)

            // ?????????????????????????????????????????????????????????
            val count = listenerMap.keys.count { it.second == event }
            if (count == 0 && ::socket.isInitialized) {
                val json = JSONObject()
                json.put("op", EVENT_UNSUBSCRIBE)
                json.put("args", event)
//                Logger.getInstance().debug(TAG, "SocketIOClient-subscribe $json")
                socket.emit(EVENT_UNSUBSCRIBE, json, Ack {
//                    Logger.getInstance().debug(TAG, "unsubscribe $event ack ${it?.contentToString()}")
                    // todo ??????ack???????????????????????????
                })
            }
        }
    }
}