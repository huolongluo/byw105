package com.android.coinw.utils;
import android.text.TextUtils;

import huolongluo.byw.log.Logger;
public final class ThreadUtil {
    public ThreadUtil() {
    }

    public static final void sleep(String timeStr) {
        if (!TextUtils.isEmpty(timeStr)) {
            try {
                sleep(Long.parseLong(timeStr) * 1000L);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public static final void sleep(Long time) {
        //线程沉睡/等待，主要用于异步/阻塞跟踪测试；暂只采用wait来实现
        try {
            Thread thread = Thread.currentThread();
            synchronized (thread) {
                int step = (int) (time / 1000L);
                for (int i = 0; i < step; ++i) {
                    Logger.getInstance().info("Thread-wait: " + (i + 1) + "s");
                    thread.wait(1000L);
                }
                thread.notify();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}