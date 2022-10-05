package com.android.coinw.utils;
import android.os.Handler;
import android.os.Looper;

import com.android.coinw.DispatchQueue;

import huolongluo.byw.byw.base.BaseApp;
public class Utilities {
    //TODO 需要控制处理异常情况，如在极端情况下，post失败
    public static volatile DispatchQueue serviceQueue = new DispatchQueue("serviceQueue");//服务端
    public static volatile DispatchQueue clientQueue = new DispatchQueue("clientQueue");//客户端
    public static volatile DispatchQueue clientRecvMsgQueue = new DispatchQueue("clientRecvMsgQueue");//客户端消息接收
    public static volatile DispatchQueue stageQueue = new DispatchQueue("stageQueue");
    public static volatile DispatchQueue globalQueue = new DispatchQueue("globalQueue");

    public static Handler getMainHandler() {
        if (BaseApp.applicationHandler == null) {
            resetMainHandler();
        }
        return BaseApp.applicationHandler;
    }

    public static void resetMainHandler() {
        if (BaseApp.applicationHandler != null) {
            BaseApp.applicationHandler.removeCallbacksAndMessages(null);
        }
        BaseApp.applicationHandler = new Handler(Looper.getMainLooper());
    }
}
