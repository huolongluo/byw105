package com.android.coinw.socket;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.android.coinw.utils.GZipUtil;
import com.dhh.websocket.WebSocketSubscriber;

import huolongluo.byw.BuildConfig;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.net.okhttp.HttpUtils;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.SocketResult;
import huolongluo.byw.util.DeviceUtils;
import huolongluo.byw.util.GsonUtil;
import huolongluo.bywx.utils.RecordUtils;
import okhttp3.WebSocket;
import okio.ByteString;

import static android.os.Looper.getMainLooper;
public class CoinwWebSocketSubscriber extends WebSocketSubscriber {
    private final String TAG = "CoinwWebSocketSubscriber";
    private IWebSocketListener socketListener = new SampleWebSocketListener();
    private long alive = 0L;

    public CoinwWebSocketSubscriber() {
        //初始化监听
        SessionManager.getInstance().setWebSocketLinstener(socketListener);
    }

    public long getAlive() {
        return alive;
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket) {
        alive = System.currentTimeMillis();
        Logger.getInstance().debugThreadLog("CoinwWebSocketSubscriber", "onOpen");
        Logger.getInstance().debug("CoinwWebSocketSubscriber", "onOpen", new Exception());
        try {
            if (!HttpUtils.isNetworkConnected(BaseApp.getSelf())) {
                Logger.getInstance().debug(TAG, "network is not available.");
                SessionManager.getInstance().closeSession(1000, "network is not available.");
                return;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        //已经连接成功
        ConnectionManager.getInstance().connected();
        //缓存会话；后续业务发展，可考虑扩展对多个会话进行支持
        SessionManager.getInstance().setSession(webSocket);
        //连接成功后，针对业务内容进行处理
        if (socketListener != null) {
            socketListener.onOpen();
        }
    }

    @Override
    public void onMessage(String text) {
        alive = System.currentTimeMillis();
        if (TextUtils.isEmpty(text)) {
            Logger.getInstance().debug(TAG, "recv msg is null or empty.");
            return;
        }
        try {
            if (text.indexOf("isHaveNew") != -1) {
                //红包消息
            } else {
                //不跟踪红包消息
                Logger.getInstance().debugThreadLog(TAG, "received new message.");
                if (BuildConfig.DEBUG || BuildConfig.ENV_DEV) {
                    if (text.length() > 100) {
                        Logger.getInstance().debug(TAG, text.substring(0, 100));
                    } else {
                        Logger.getInstance().debug(TAG, text);
                    }
                }
            }
            SocketResult result = GsonUtil.json2Obj(text, SocketResult.class);
            if (result == null) {
                return;
            }
            String data = result.data;
            if (TextUtils.isEmpty(data) || data.length() < 4) {
                Logger.getInstance().error(TAG, "推送数据错误");
                return;
            }
            if (socketListener != null) {
                socketListener.onMessage(result.url, result.type, result.param, data);
            }
            result = null;
            text = null;
        } catch (Throwable e) {
            Logger.getInstance().error(TAG, "异常 result: " + text, e);
            RecordUtils.tryRecord("socket", "common", text, DeviceUtils.getImei(BaseApp.getSelf()));
            e.printStackTrace();
        }
    }

    @Override
    protected void onMessage(@NonNull ByteString byteString) {
        if (byteString == null) {
            return;
        }
        Logger.getInstance().debug(TAG, "onMessage-ByteString1");
        byte[] byteData = GZipUtil.unGZip(byteString.toByteArray());
        if (byteData == null) {
            return;
        }
        Logger.getInstance().debug(TAG, "onMessage-ByteString2");
        onMessage(new String(byteData));
    }

    @Override
    protected void onReconnect() {
        String logMsg = String.format("onReconnect-main-thread:%s,curr-thread:%s", getMainLooper().getThread().getId(), Thread.currentThread().getId());
        Logger.getInstance().debug(TAG, logMsg);
        alive = System.currentTimeMillis();
    }

    @Override
    protected void onClose() {
        Logger.getInstance().error(TAG, "onClose.");
        SessionManager.getInstance().closeSession(1000, "release.");
    }

    @Override
    public void onError(Throwable e) {
        Logger.getInstance().error(TAG, "onError.", e);
        SessionManager.getInstance().closeSession(1000, "release.");
        if (e != null) {
            e.printStackTrace();
        }
    }
}