package com.android.coinw;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.util.concurrent.CountDownLatch;

import huolongluo.byw.log.Logger;

public class DispatchQueue extends Thread {

    private volatile Handler handler = null;
    private CountDownLatch syncLatch = new CountDownLatch(1);
    private String threadName;

    public DispatchQueue(final String threadName) {
        this.threadName = threadName;
        setName(threadName);
        start();
    }

    private void createHandler() {
        //获得Handler线程Looper
        //TODO 待优化，需要处理线程异常情况
        HandlerThread handlerThread = new HandlerThread(TextUtils.isEmpty(threadName) ? "dispatch-queue-" + System.currentTimeMillis() : threadName);
        handlerThread.setPriority(Thread.MAX_PRIORITY);
        handlerThread.setDaemon(true);
        handlerThread.start();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        handler = new Handler(handlerThread.getLooper());
    }

    public void sendMessage(Message msg, int delay) {
        try {
            syncLatch.await();
            boolean sendOk;
            if (delay <= 0) {
                sendOk = handler.sendMessage(msg);
            } else {
                sendOk = handler.sendMessageDelayed(msg, delay);
            }
            //TODO Handler的特殊异常情况，详情见Handler源码定义
            //如果Handler发送消息失败，则重新获得Handler的Looper中的MessageQueue，详情见Handler源码定义
            if (!sendOk) {
                Logger.getInstance().debug("DispatchQueue" + threadName, "Handler发送消息失败!", new Exception());
                createHandler();
            }
        } catch (Exception e) {
            Logger.getInstance().error(e);
        }
    }

    public void cancelRunnable(Runnable runnable) {
        try {
            syncLatch.await();
            handler.removeCallbacks(runnable);
        } catch (Exception e) {
            Logger.getInstance().error(e);
        }
    }

    public void postRunnable(Runnable runnable) {
        postRunnable(runnable, 0);
    }

    public void postRunnable(Runnable runnable, long delay) {
        //TODO 需要控制处理异常情况，如在极端情况下，post失败
        try {
            syncLatch.await();
            boolean sendOk;
            if (delay <= 0) {
                sendOk = handler.post(runnable);
            } else {
                sendOk = handler.postDelayed(runnable, delay);
            }
            if (!sendOk) {
                Logger.getInstance().debug("DispatchQueue" + threadName, "Handler发送消息失败!", new Exception());
                createHandler();
            }
        } catch (Exception e) {
            Logger.getInstance().error(e);
        }
    }

    public void cleanupQueue() {
        try {
            syncLatch.await();
            handler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            Logger.getInstance().error(e);
        }
    }
//
//    public void handleMessage(Message inputMessage) {
//
//    }

    @Override
    public void run() {
//        Looper.prepare();
//        handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                DispatchQueue.this.handleMessage(msg);
//            }
//        };
        createHandler();
        syncLatch.countDown();
//        Looper.loop();
    }
}
