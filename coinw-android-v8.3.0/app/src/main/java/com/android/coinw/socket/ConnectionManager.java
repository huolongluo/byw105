package com.android.coinw.socket;
import com.dhh.websocket.Config;
import com.dhh.websocket.RxWebSocket;
import com.dhh.websocket.WebSocketInfo;

import java.util.concurrent.TimeUnit;

import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.net.okhttp.HttpUtils;
import huolongluo.byw.helper.SSLHelper;
import huolongluo.byw.log.Logger;
import okhttp3.OkHttpClient;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.observers.SafeSubscriber;
import rx.schedulers.Schedulers;
public class ConnectionManager {
    private static final String TAG = "ConnectionManager";
    //是否正在尝试连接
    private static volatile boolean isAttemptConnecting;
    private static volatile Subscription subscription = null;
    private static ConnectionManager instance = new ConnectionManager();
    CoinwWebSocketSubscriber socketSubscriber = new CoinwWebSocketSubscriber();
    private OkHttpClient client = SSLHelper.getBuilder(10, 10, 10)//
            .pingInterval(10, TimeUnit.SECONDS) // 设置心跳间隔，这个是2秒检测一次
            .build();

    public static ConnectionManager getInstance() {
        if (instance == null) {
            synchronized (ConnectionManager.class) {
                if (instance == null) {
                    instance = new ConnectionManager();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化连接配置
     */
    public void init() {
        //由于服务器推送消息时长过长，暂时采用增加重连时长间隔的方法
        Config config = new Config.Builder().setShowLog(true)//
                .setClient(client)   //if you want to set your okhttpClient
                .setShowLog(true, "coinw-websocket")//
                .setReconnectInterval(10, TimeUnit.SECONDS)  //set reconnect interval
                .build();
        RxWebSocket.setConfig(config);
    }

    /**
     * websocket连接成功后，重置连接标识位
     */
    public void connected() {
        isAttemptConnecting = false;
    }

    public boolean checkAlive() {
        if (subscription instanceof SafeSubscriber) {
            SafeSubscriber safeSubscriber = (SafeSubscriber) subscription;
            Subscriber actual = safeSubscriber.getActual();
            if (actual instanceof CoinwWebSocketSubscriber) {
                CoinwWebSocketSubscriber socketSubscriber = (CoinwWebSocketSubscriber) actual;
                long timeMills = System.currentTimeMillis();
                if (socketSubscriber.getAlive() > 0L && timeMills - socketSubscriber.getAlive() < 30 * 1000L) {
                    Logger.getInstance().debug(TAG, "alive: " + socketSubscriber.getAlive());
                    return true;
                }
            }
        }
        return false;
    }

    public long getAlive() {
        if (subscription instanceof SafeSubscriber) {
            SafeSubscriber safeSubscriber = (SafeSubscriber) subscription;
            Subscriber actual = safeSubscriber.getActual();
            if (actual instanceof CoinwWebSocketSubscriber) {
                CoinwWebSocketSubscriber socketSubscriber = (CoinwWebSocketSubscriber) actual;
                return socketSubscriber.getAlive();
            }
        }
        return 0L;
    }

    public synchronized void connect(boolean force) {
        Logger.getInstance().debugThreadLog("ConnectionManager", "connect");
        Logger.getInstance().debug(TAG, "isAttemptConnecting: " + isAttemptConnecting);
        Logger.getInstance().debug(TAG, "connect: " + SessionManager.getInstance().isConnected());
        if (SessionManager.getInstance().isConnected()) {
        }
        if (!HttpUtils.isNetworkConnected(BaseApp.getSelf())) {//检查网络是否已经连接
            //当前无可用网络
            Logger.getInstance().debug(TAG, "network is not available.");
            return;
        }
        // && subscription.isUnsubscribed()
        if (subscription != null) {
            return;
        }
        Logger.getInstance().debug(TAG, "isAttemptConnecting: " + isAttemptConnecting, new Exception());
        if (isAttemptConnecting) {
            return;
        }
        isAttemptConnecting = true;
        try {
            if (force) {
                if (subscription != null && !subscription.isUnsubscribed()) {
                    SessionManager.getInstance().closeSession(1000, "try reinit.");
                    subscription.unsubscribe();
                    Logger.getInstance().debug(TAG, "try unsubscribe.");
                }
                subscription = null;
            }
            //长链接 连接成功的回调通知
            //TODO 优化控制
            String url = UrlConstants.SOCKET_ADDR_new + "/websocket/socketServer";
            subscription = RxWebSocket.get(UrlConstants.SOCKET_ADDR_new + "/websocket/socketServer").observeOn(Schedulers.io()).doOnUnsubscribe(new Action0() {
                @Override
                public void call() {
                    Logger.getInstance().debug(TAG, "doOnUnsubscribe..", new Exception());
                    release();
                }
            }).onErrorReturn(throwable -> {
                Logger.getInstance().errorLog(throwable);
                return null;
            }).doOnError(throwable -> {
                Logger.getInstance().error(TAG, "ws on error!", throwable);
                Logger.getInstance().error(throwable);
            }).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe(socketSubscriber);
            client.dispatcher().executorService().shutdown();
            Logger.getInstance().debug(TAG, "connect: " + url, new Exception());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        isAttemptConnecting = false;
        SessionManager.getInstance().removeSession();
        Logger.getInstance().debug(TAG, "断开连接", new Exception());
    }

    public void release() {
        disconnect();
        try {
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
                Logger.getInstance().debug(TAG, "try unsubscribe.");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
