package com.android.coinw;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.android.coinw.socket.SampleNetworkChangeListener;

import huolongluo.byw.log.Logger;
public class CoinwReceiver extends BroadcastReceiver {
    protected INetworkChangeListener networkChangeListener = new SampleNetworkChangeListener();
    private final String TAG = "CoinwReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.getInstance().debug(TAG, "网络状态发生变化");
        //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
        try {
            //TODO 注意在BroadcastReceiver的注意事项
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
}
