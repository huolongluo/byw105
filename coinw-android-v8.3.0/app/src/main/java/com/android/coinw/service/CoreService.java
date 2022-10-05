package com.android.coinw.service;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.android.coinw.CoinwLooper;
import com.android.coinw.DataManager;
import com.android.coinw.INetworkChangeListener;
import com.android.coinw.socket.ConnectionManager;
import com.android.coinw.socket.SampleNetworkChangeListener;
import com.android.coinw.utils.Utilities;
import com.netease.nimlib.c;
import com.netease.nimlib.d.e;
import com.netease.nimlib.n.a;
import com.netease.nimlib.s.r;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.service.ResponseService;

import huolongluo.byw.R;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.log.Logger;
/**
 * 后台核心服务
 */
public class CoreService extends CoreServiceRemoteAbs {
    protected INetworkChangeListener networkChangeListener = new SampleNetworkChangeListener();
    private boolean a;
    @Override
    public void onCreate() {
        Logger.getInstance().debug(TAG, "CoreService onCreate");
        //初始化连接配置
        ConnectionManager.getInstance().init();
        //启动自检服务，单独配置一个线程任务
        CoinwLooper.getInstance().startService();
        //单独处理网络变化
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        try {
            CoinwLooper.getInstance().release();
            unregisterReceiver(netWorkStateReceiver);
            DataManager.getInstance().removeAllRequest();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        //清理掉已用资源
        ConnectionManager.getInstance().release();
        Logger.getInstance().debug(TAG, "CoreService onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            //this.a();
//            e.a().e();
        } catch (Throwable var4) {
        }

        this.a = false;
        return super.onStartCommand(intent, flags, startId);
    }

    private static int b(Context var0) {
        StatusBarNotificationConfig var1;
        return (var1 = c.g().statusBarNotificationConfig) != null && var1.notificationSmallIconId != 0 ? var1.notificationSmallIconId : var0.getApplicationInfo().icon;
    }

    private void a() {
        if (r.a(this) && !this.a) {
            try {
//                com.netease.nimlib.n.e.e(this);
//                this.startForeground(10001, (new a.c.d(this, com.netease.nimlib.n.e.d(this))).a(b(this)).a());

                Intent activityIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), 0, activityIntent, 0);
                Notification notification = new Notification.Builder(getApplication()).setAutoCancel(true).
                        setSmallIcon(R.drawable.login_logo).setTicker("前台Service启动").setContentTitle("前台Service运行中").
                        setContentText("这是一个正在运行的前台Service").setWhen(System.currentTimeMillis()).setContentIntent(pendingIntent).build();
                startForeground(10009, notification);
//                Utilities.getMainHandler()
//                com.netease.nimlib.e.b.a.a(this)
                Utilities.getMainHandler().postDelayed(new Runnable() {
                    @Override
                    public final void run() {
                        CoreService.this.stopForeground(true);
                    }
                }, 1000L);
                this.a = true;
            } catch (Throwable var1) {
            }
        }
    }
    private final BroadcastReceiver netWorkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.getInstance().debug(TAG, "网络状态发生变化");
            //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
            try {
                networkChange(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void networkChange(Context context) {
            if (networkChangeListener == null) {
                return;
            }
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                //获取WIFI连接的信息
                NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                //获取移动数据连接的信息
                NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                    Logger.getInstance().debug(TAG, "WIFI已连接,移动数据已连接");
                    networkChangeListener.onConnected();
                } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
                    Logger.getInstance().debug(TAG, "WIFI已连接,移动数据已断开");
                    networkChangeListener.onConnected();
                } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                    Logger.getInstance().debug(TAG, "WIFI已断开,移动数据已连接");
                    networkChangeListener.onConnected();
                } else {
                    Logger.getInstance().debug(TAG, "WIFI已断开,移动数据已断开");
                    networkChangeListener.onDisconnected();
                }
                //API大于23时使用下面的方式进行网络监听
            } else {
                //获取所有网络连接的信息
                Network[] networks = connMgr.getAllNetworks();
                //用于存放网络连接信息
                StringBuilder sb = new StringBuilder();
                boolean onDisconnected = false;
                //通过循环将网络信息逐个取出来
                for (int i = 0; i < networks.length; i++) {
                    //获取ConnectivityManager对象对应的NetworkInfo对象
                    NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                    if (networkInfo.isConnected()) {
                        onDisconnected = true;
                    }
                    sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
                }
                if (onDisconnected) {
                    networkChangeListener.onConnected();
                    Logger.getInstance().debug(TAG, "网络已连接 " + sb.toString());
                } else {
                    networkChangeListener.onDisconnected();
                    Logger.getInstance().debug(TAG, "网络已断开");
                }
            }
        }
    };
}