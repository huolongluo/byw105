package com.android.coinw.service;
import com.android.coinw.ICoreServiceCallback;
import com.android.coinw.ServiceConstants;
import com.android.coinw.model.Message;
import com.android.coinw.model.Response;
import com.android.coinw.utils.Utilities;

import huolongluo.byw.log.Logger;
public class CallbackManager {
    private static CallbackManager instance = new CallbackManager();
    private volatile ICoreServiceCallback callback;

    private CallbackManager() {
    }

    public static CallbackManager getInstance() {
        if (instance == null) {
            synchronized (CallbackManager.class) {
                if (instance == null) {
                    instance = new CallbackManager();
                }
            }
        }
        return instance;
    }

    public boolean isEmpty() {
        return callback == null;
    }

    /**
     * 设置回调接口实例（由客户端实现）
     * @param callback
     */
    public void setServiceCallback(ICoreServiceCallback callback) {
        this.callback = callback;
    }

    /**
     * 通过回调接口向客户端响应结果
     * @param response
     */
    public void sendResponse(Response response) {
        //回调接口为空
        if (callback == null) {
            Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "response callback is null.");
            return;
        }
        //响应数据为空
        if (response == null) {
            Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "response data is null.");
            return;
        }
        Utilities.serviceQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.onResponse(response);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }

    /**
     * 通过回调接口向客户端发送消息
     * @param message
     */
    public void sendMessage(Message message) {
        //回调接口为空
        if (callback == null) {
            Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "response callback is null.");
            return;
        }
        //响应数据为空
        if (message == null) {
            Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "response data is null.");
            return;
        }
        Utilities.serviceQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.onMessage(message);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }
}
