package com.android.coinw.service;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.android.coinw.ICoreService;

import huolongluo.byw.log.Logger;
import huolongluo.byw.util.GsonUtil;

public abstract class CoreServiceRemoteAbs extends Service {
    protected final String TAG = "BrushService";
    protected final ICoreService.Stub serviceStub = new CoreServiceStub();

    @Override
    public IBinder onBind(Intent arg0) {
        return serviceStub;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        String action = intent.getAction();
        String json = GsonUtil.obj2Json(intent, Intent.class);
        Logger.getInstance().debug(TAG, "onUnbind,action: " + action + " json: " + json);
        //客户端与服务端已断开，清空回调接口
        CallbackManager.getInstance().setServiceCallback(null);
        return super.onUnbind(intent);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        Logger.getInstance().debug(TAG, "bindService");
        return super.bindService(service, conn, flags);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        Logger.getInstance().debug(TAG, "unbindService");
        super.unbindService(conn);
        if (conn == null) {
            return;
        }
    }
}
