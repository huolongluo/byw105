package com.android.coinw.service;
import android.os.RemoteException;

import com.android.coinw.CoinwLooper;
import com.android.coinw.ConfigManager;
import com.android.coinw.DataManager;
import com.android.coinw.ICoreService;
import com.android.coinw.ICoreServiceCallback;
import com.android.coinw.ServiceConstants;
import com.android.coinw.http.HttpManager;
import com.android.coinw.model.Message;
import com.android.coinw.model.Request;
import com.android.coinw.socket.SessionManager;
import com.android.coinw.utils.Utilities;

import huolongluo.byw.log.Logger;
public class CoreServiceStub extends ICoreService.Stub {
    @Override
    public void registerListener(ICoreServiceCallback callback) throws RemoteException {
        Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "register listener success! callback == null ? " + (callback == null));
        if (callback == null) {
            //客户端注册设置的回调接口为空，做异常处理
            return;
        }
        CallbackManager.getInstance().setServiceCallback(callback);
    }

    @Override
    public void unregisterListener() throws RemoteException {
        Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "unregister listener success!");
    }

    @Override
    public void sendMessage(final Message message) throws RemoteException {
        Utilities.serviceQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                SessionManager.getInstance().sendMessage(message);
                CoinwLooper.getInstance().checkLoopService();
            }
        });
    }

    @Override
    public void sendRequest(Request request) throws RemoteException {
        Utilities.serviceQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                HttpManager.getInstance().sendRequest(request);
            }
        });
    }

    @Override
    public void removeRequest(String url) throws RemoteException {
        Utilities.serviceQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                DataManager.getInstance().removeRequest(url);
            }
        });
    }

    @Override
    public void updateConfig(String data) {
        Utilities.serviceQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                ConfigManager.getInstance().updateConfig(data);
            }
        });
    }
}
