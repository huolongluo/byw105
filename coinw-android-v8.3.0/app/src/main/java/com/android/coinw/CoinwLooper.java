package com.android.coinw;
import com.android.coinw.http.HttpManager;
import com.android.coinw.socket.ConnectionManager;
import com.android.coinw.socket.SessionManager;
import com.android.coinw.utils.Utilities;

import java.util.concurrent.TimeUnit;

import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.net.okhttp.HttpUtils;
import huolongluo.byw.log.Logger;
import huolongluo.bywx.utils.AppUtils;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
public class CoinwLooper {
    private static CoinwLooper instance = new CoinwLooper();
    private static final String TAG = "CoinwLooper";
    private Subscription ms;
    private long lastUpdateTime;
    private int times = 0;

    public static CoinwLooper getInstance() {
        if (instance == null) {
            synchronized (CoinwLooper.class) {
                if (instance == null) {
                    instance = new CoinwLooper();
                }
            }
        }
        return instance;
    }

    private CoinwLooper() {
        if (ms == null) {
            startServiceChecker();
        } else if (ms.isUnsubscribed()) {
            startServiceChecker();
        } else if (System.currentTimeMillis() - lastUpdateTime > 30 * 1000L) {
            startServiceChecker();
        }
    }

    public void startService() {
        lastUpdateTime = System.currentTimeMillis();
        Logger.getInstance().debug("CoinwLooper", "start service checker!");
    }

    /**
     * 启动自检服务，按照周期执行
     */
    private void startServiceChecker() {
        release();
        lastUpdateTime = System.currentTimeMillis();
        // 自检服务器打开
        ms = Observable.interval(1000L, 3000L, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).onErrorReturn(throwable -> {
            Logger.getInstance().errorLog(throwable);
            return 0L;
        }).observeOn(AndroidSchedulers.mainThread()).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe(aLong -> {
            Logger.getInstance().debugThreadLog("CoinwLooper", "service checker");
            try {
                loop();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }, throwable -> {
            Logger.getInstance().debug(TAG, "startServiceChecker", throwable);
        });
    }

    private void loop() throws Throwable {
        lastUpdateTime = System.currentTimeMillis();
        if (!HttpUtils.isNetworkConnected(BaseApp.getSelf())) {
            DataManager.getInstance().removeAllRequest();
            Logger.getInstance().debug(TAG, "network is not available,so return and continue task.");
            return;
        }
        try {
            if (BaseApp.getSelf() != null && AppUtils.isScreenOff(BaseApp.getSelf())) {
                DataManager.getInstance().removeAllRequest();
                return;
            }
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
        //获得服务器最新数据
        HttpManager.getInstance().getLatestData();
        //
        if (times > 100) {
            times = 0;
            return;
        } else if (times > 10) {
            times++;
            return;
        }
        //
        long alive = ConnectionManager.getInstance().getAlive();
        if (alive > 0L && System.currentTimeMillis() - alive > 20 * 1000L) {
            times++;
            //尝试重联
            ConnectionManager.getInstance().connect(false);
        }
        //TODO ws不稳定的情况下，补尝方案
        if (SessionManager.getInstance().isConnected()) {
            times = 0;
            return;
        }
        times++;
        //如果socket判断，尝试重新连接
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                ConnectionManager.getInstance().connect(false);
            }
        });
    }

    public void checkLoopService() {
        if (!HttpUtils.isNetworkConnected(BaseApp.getSelf())) {
            Logger.getInstance().debug(TAG, "network is not available,so return and continue task.");
            return;
        }
        long currTime = System.currentTimeMillis();
        //判断轮询是否正常运行
        if (currTime - lastUpdateTime > 10 * 1000L) {
            Logger.getInstance().debug(TAG, "check loop service,try start service checker.");
            startServiceChecker();
        }
    }

    public void release() {
        if (ms == null) {
            return;
        }
        try {
            if (ms.isUnsubscribed()) {
                return;
            }
            ms.unsubscribe();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
