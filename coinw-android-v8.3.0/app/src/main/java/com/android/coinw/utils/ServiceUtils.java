package com.android.coinw.utils;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.android.coinw.service.CoreService;

import java.util.List;

import huolongluo.byw.log.Logger;
public class ServiceUtils {
    /**
     * 启动核心服务
     * @param context
     */
    public static void startService(Context context) {
        startService(context, false);
    }

    public static void stopService(Context context) {
        try {
            Intent intent = new Intent();
            intent.setClass(context, CoreService.class);
            context.getApplicationContext().stopService(intent);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void startService(Context context, boolean init) {
        if (context == null) {
            return;
        }
        //如果不是初始化，则再判断核心服务是否已经运行
        if (!init && isRunningService(context)) {
            //尝试绑定
            //AppClient.getInstance().bindService(context.getApplicationContext());
            Logger.getInstance().debug("ServiceUtils", "Service is running!");
            return;
        }
        try {
            Intent intent = new Intent();
            intent.setClass(context, CoreService.class);
            //TODO 测试专用
            context.getApplicationContext().startService(intent);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        //尝试绑定
        //AppClient.getInstance().bindService(context.getApplicationContext());
    }

    /**
     * 判断核心服务器是否正在运行
     * @param context
     * @return
     */
    public static boolean isRunningService(Context context) {
        // catch up in case we crashed but other processes are still running
        if (context == null) {
            return false;
        }
        try {
            ActivityManager mAm = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> rsis = mAm.getRunningServices(256);
            for (ActivityManager.RunningServiceInfo rsi : rsis) {
                if (rsi == null || rsi.service == null) {
                    continue;
                }
                if (TextUtils.equals(rsi.service.getPackageName(), context.getPackageName())) {
                    return true;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }
}
