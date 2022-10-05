package com.android.coinw.socket;
import android.text.TextUtils;

import com.android.coinw.DataManager;
import com.android.coinw.ServiceConstants;
import com.android.coinw.http.HttpManager;
import com.android.coinw.model.Message;
import com.android.coinw.model.Request;
import com.android.coinw.utils.ParamUtils;

import huolongluo.byw.BuildConfig;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import okhttp3.WebSocket;
public class SessionManager {
    private static final String TAG = "SessionManager";
    private static SessionManager instance = new SessionManager();
    private volatile WebSocket session;
    private volatile IWebSocketListener listener;

    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    public void setSession(WebSocket session) {
        closeSession(1000, "reset session!");
        this.session = null;
        this.session = session;
    }

    public void setWebSocketLinstener(IWebSocketListener listener) {
        this.listener = listener;
    }

    public boolean sendMessage(Message message) {
        if (message.cache) {//是否加入缓存
            //监听数据内容
            if (listener != null) {
                Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "listening to send messages." + message.ciphertext);
                listener.onSend(message);
            } else {
                Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "listening failure." + message.ciphertext);
            }
            //加入HTTP补尝策略
            Request request = ParamUtils.getRequest(message);
            if (request != null) {
                if (TextUtils.equals(message.type, AppConstants.SOCKET.register) || TextUtils.equals(message.type, AppConstants.SOCKET.changeType)) {
                    //加入补偿策略控制
                    HttpManager.getInstance().addRequest(request);
                    //如果是第一次，则更新时间数据
                    DataManager.getInstance().updateReciveTime(request.url, System.currentTimeMillis(), true);
//                } else if (TextUtils.equals(message.type, AppConstants.SOCKET.changeType)) {
//                    //清除补偿策略控制
//                    HttpManager.getInstance().removeRequest(request);
//                    //加入补偿策略控制
//                    HttpManager.getInstance().addRequest(request);
//                    //如果是第一次，则更新时间数据
//                    DataManager.getInstance().updateReciveTime(request.url, System.currentTimeMillis(), true);
                } else {
                    //清除补偿策略控制
                    HttpManager.getInstance().removeRequest(request);
                    DataManager.getInstance().remove(message);
                }
            }
        }
        return sendMessage(message.ciphertext);
    }

    public boolean sendMessage(String msg) {
        Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "send messages is: " + msg);
        if (!isConnected()) {
            return false;
        }
        if (TextUtils.isEmpty(msg) || session == null) {
            return false;
        }
        boolean sendOk = false;
        try {
            if (isConnected()) {
                //TODO 测试专用(未开启)
//                if(BuildConfig.DEBUG){
//                    return false;
//                }
                sendOk = session.send(msg);
            }
            if (!sendOk) {
                disconnect();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return sendOk;
    }

    public void disconnect() {
        if (session != null) {
            //try close ?
        }
    }

    public boolean isConnected() {
        return session != null;
    }

    public void removeSession() {
        closeSession(1000, "remove");
        this.session = null;
    }

    /**
     * 关闭session
     */
    public void closeSession(int code, String reason) {
        if (session == null) {
            return;
        }
        try {
            session.cancel();
            session.close(code, reason);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
