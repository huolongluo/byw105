package com.android.coinw;
public class ServiceConstants {
    //http方式
    public static final int TYPE_SERVICE_HTTP = 1;
    //socket方式
    public static final int TYPE_SERVICE_SOCKET = 2;
    //成功
    public static final int TYPE_RECV_MSG_STATUS_SUCCESS = 3;
    //失败
    public static final int TYPE_RECV_MSG_STATUS_FAIL = 4;
    /****************************************/
    public static final String APP_BROADCAST_SVR_START = "app.broadcast.svr.start";//启动服务的广播
    public static final String APP_BROADCAST_SVR_STOP = "app.broadcast.svr.stop";//停止服务的广播
    /****************************************/
    public static final String TAG_SERVICE = "service";
    public static final String TAG_CLIENT = "client";

    public static final class Config {
        //语言
        public static final String CFG_KEY_LANGUAGE = "language";
    }
}
