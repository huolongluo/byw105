package com.android.coinw.client;
import android.content.Context;
import android.text.TextUtils;

import com.android.coinw.model.Message;
import com.android.coinw.model.Request;
import com.android.coinw.model.Response;
import com.android.coinw.utils.ParamUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import huolongluo.byw.BuildConfig;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.ReqData;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.bywx.HttpRequestManager;
public class AppClient {
    private static final String TAG = "AppClient";
    private volatile ConcurrentHashMap<String, ReqData> listenerMap = new ConcurrentHashMap<String, ReqData>();
    private volatile ConcurrentHashMap<String, Message> messageMap = new ConcurrentHashMap<>();
    private static volatile ConcurrentHashMap<String, HttpRequestManager.HttpRequest> callbackMap = new ConcurrentHashMap<String, HttpRequestManager.HttpRequest>();
    //    SocketCallbackMap<String, List<ReqData>> scm = new SocketCallbackMap<String, List<ReqData>>();
    private static AppClient instance = new AppClient();
    //核心服务连接器
    private ServiceConnecter connecter = new ServiceConnecter();

    private AppClient() {
    }

    public static AppClient getInstance() {
        if (instance == null) {
            synchronized (AppClient.class) {
                if (instance == null) {
                    instance = new AppClient();
                }
            }
        }
        return instance;
    }

    /**
     * 绑定后台核心服务
     * @param context
     * @return
     */
    public boolean bindService(Context context) {
        return connecter.bindService(context);
    }

    /**
     * 向核心服务发送消息
     * @param url
     * @param operate
     * @param p
     */
    public void sendMessage(String url, int operate, String[] p, Map<String, Object> maps) {
        this.sendMessage(url, operate, 55000L, p, maps);
    }

    /**
     * 向核心服务发送消息
     * @param url
     * @param operate
     * @param interval
     * @param p
     */
    public void sendMessage(String url, int operate, long interval, String[] p, Map<String, Object> maps) {
        //向本地核心服务发送消息
        Message message = ParamUtils.getMessage(url, operate, interval, p, maps);
        //TODO 处理获得消息为空的情况
        if (message != null) {
            //加入缓存
            messageMap.put(url, message);
            connecter.sendMessage(message);
        }
    }

    public void addListener(ReqData rd, boolean init) {
        listenerMap.put(rd.api + rd.tag, rd);
    }

    public void apiIndex(int operate) {
        if (operate == AppConstants.SOCKET.OPEN) {
            subscribe(AppConstants.SOCKET.indexApi, 55000L, new String[]{PricingMethodUtil.getPricingSelectType()}, null);
        } else {
            sendMessage(AppConstants.SOCKET.indexApi, AppConstants.SOCKET.CLOSE, new String[]{PricingMethodUtil.getPricingSelectType()}, null);
        }
    }

    public void apiETFIndex(int operate) {
        if (operate == AppConstants.SOCKET.OPEN) {
            subscribe(AppConstants.SOCKET.etfIndexApi, 55000L, new String[]{PricingMethodUtil.getPricingSelectType()}, null);
        } else {
            sendMessage(AppConstants.SOCKET.etfIndexApi, AppConstants.SOCKET.CLOSE, new String[]{PricingMethodUtil.getPricingSelectType()}, null);
        }
    }

    /**
     * 红包
     * @param operate
     */
    public void apiRedEnvelope(int operate, String... p) {
        if (operate == AppConstants.SOCKET.OPEN) {
//            if (TextUtils.isEmpty(UserInfoManager.getToken())) {
//                return;
//            }
            Map<String, Object> maps = new HashMap<>();
            maps.put("type", "register");
            maps.put("sessionId", TextUtils.isEmpty(UserInfoManager.getToken()) ? "" : UserInfoManager.getToken());
            maps.put("url", "redenvelope");
            maps.put("param", new int[]{UserInfoManager.getUserInfo().getFid()});
            subscribe(AppConstants.SOCKET.redenvelope, 5000L, p, maps);
        } else {
            unsubscribe(AppConstants.SOCKET.redenvelope, p);
        }
    }

    public void apiKLine(int operate, Map<String, Object> maps, String... p) {
        //添加计价方式头部到参数中(参数位置在第三位)
        String[] newParamsArray = new String[p.length + 1];
        System.arraycopy(p, 0, newParamsArray, 0, p.length);
        newParamsArray[newParamsArray.length - 1] = PricingMethodUtil.getPricingSelectType();
        if (operate == AppConstants.SOCKET.OPEN) {
            subscribe(AppConstants.SOCKET.klineApi, 55000L, newParamsArray, maps);
        } else {
            unsubscribe(AppConstants.SOCKET.klineApi, newParamsArray);
        }
    }

    public void apiDepthV3(int operate, Map<String, Object> maps, String... p) {
        if (operate == AppConstants.SOCKET.OPEN) {
            subscribe(AppConstants.SOCKET.tradeApiV3, 3000L, p, maps);
        } else {
            unsubscribe(AppConstants.SOCKET.tradeApiV3, p);
        }
    }

    public void apiRiseFall(int operate, String... p) {
        //添加计价方式头部到参数中(参数位置在第一位)
        String[] newParamsArray = new String[p.length + 1];
        System.arraycopy(p, 0, newParamsArray, 1, p.length);
        newParamsArray[0] = PricingMethodUtil.getPricingSelectType();
        Logger.getInstance().debug(TAG, "apiRiseFall newParamsArray[0]:" + newParamsArray[0]);
        if (operate == AppConstants.SOCKET.OPEN) {
            subscribe(AppConstants.SOCKET.riseFallApi, 55000L, newParamsArray, null);
        } else {
            unsubscribe(AppConstants.SOCKET.riseFallApi, newParamsArray);
        }
    }

    public void apiIntrustInfo(int operate, Map<String, Object> maps, String... p) {
        if (operate == AppConstants.SOCKET.OPEN) {
            subscribe(AppConstants.SOCKET.intrustApi, 5000L, p, maps);
        } else {
            unsubscribe(AppConstants.SOCKET.intrustApi, p);
        }
    }

    /**
     * 原参数数据
     * @param url
     * @param params
     * @param callback
     * @param type
     */
    public void addPlanning(String url, Map<String, Object> params, INetCallback callback, Type type) {
        //默认采用post方式，目的兼容币币旧接口
        this.addPlanning(url, params, callback, type, AppConstants.COMMON.VAL_HTTP_POST);
    }

    /**
     * 原参数数据
     * @param url
     * @param params
     * @param callback
     * @param type
     */
    public void addPlanning(String url, Map<String, Object> params, INetCallback callback, Type type, int method) {
        //TODO 测试专用
        if (BuildConfig.DEBUG && TextUtils.equals(url, UrlConstants.DOMAIN + UrlConstants.getSuccessDetails)) {
            //测试不加入计划
            //return;
        }
        if (callbackMap == null || TextUtils.isEmpty(url) || callback == null) {
            return;
        }
        HttpRequestManager.HttpRequest hr = new HttpRequestManager.HttpRequest();
        hr.url = url;
        hr.params = params;
        hr.callback = callback;
        hr.type = type;
        HttpRequestManager.HttpRequest mapHr = callbackMap.get(url);
        Logger.getInstance().debug(TAG, "url: " + url + GsonUtil.obj2Json(params, Map.class));
        if (mapHr != null && mapHr.equals(hr)) {
            Logger.getInstance().debug(TAG, "post url is: " + url + ", already exists.");
        } else {
            Logger.getInstance().debug(TAG, "post url is: " + url + ", add polling plan.");
            callbackMap.put(url, hr);
        }
        Request request = ParamUtils.getRequest(url, params, method);
        connecter.sendRequest(request);
    }

    public void removePlanning(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        callbackMap.remove(url);
        connecter.removeRequest(url);
    }

    /**
     * http响应数据
     * @param url
     * @param data
     * @param params
     */
    public void dispatchResponse(String url, Object data, Map<String, Object> params) {
        ReqData rd = listenerMap.get(url);
        if (rd == null || rd.netCallback == null) {
            //未找到回调接口
            return;
        }
        try {
            rd.netCallback.onSuccess(data);
            //手动清空对象
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void dispatchResponse(Response response) {
        if (response == null || response.req == null) {
            return;
        }
        String url = response.req.url;
        List<ReqData> dataList = getReqData(url);
        if (dataList == null || dataList.isEmpty()) {
            //检查是否为补偿策略响应数据
            String api = ParamUtils.getApiForUrl(url);//本地策略控制
            if (!TextUtils.isEmpty(api)) {
                dispatchMessage(api, response.data, ParamUtils.getParams(response.req.params));
            }
            return;
        }
        //适配多个注册回调
        for (ReqData rdd : dataList) {
            if (rdd == null) {
                continue;
            }
            try {
                rdd.netCallback.onSuccess(response.data);
                //手动清空对象
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
//        ReqData rd = listenerMap.get(url);
//        if (rd == null || rd.netCallback == null) {
//            //检查是否为补偿策略响应数据
//            String api = ParamUtils.getApiForUrl(url);//本地策略控制
//            if (!TextUtils.isEmpty(api)) {
//                dispatchMessage(api, response.data, ParamUtils.getParams(response.req.params));
//            }
//            //未找到回调接口
//            return;
//        }
//        try {
//            rd.netCallback.onSuccess(response.data);
//            //手动清空对象
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
    }

    private List<ReqData> getReqData(String key) {
        List<ReqData> dataList = new ArrayList<ReqData>();
        Set<Map.Entry<String, ReqData>> set = listenerMap.entrySet();
        for (Map.Entry<String, ReqData> entry : set) {
            String entryKey = entry.getKey();
            if (TextUtils.isEmpty(entryKey)) {
                continue;
            }
            if (entryKey.startsWith(key)) {
                dataList.add(entry.getValue());
            }
        }
        return dataList;
    }

    /**
     * socket消息
     * @param api
     * @param data
     * @param p
     */
    public void dispatchMessage(String api, Object data, String[] p) {
        List<ReqData> dataList = getReqData(api);
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
        //适配多个注册回调
        for (ReqData rdd : dataList) {
            if (rdd == null) {
                continue;
            }
            dispatchMessage(rdd, data, p);
        }
//        ReqData rd = listenerMap.get(api);
//        if (rd == null) {
//            //未找到回调接口
//            return;
//        }
//        if (rd.socketCallback != null) {
//            try {
//                rd.socketCallback.onResult(data, p);
//                //手动清空对象
//            } catch (Throwable t) {
//                t.printStackTrace();
//            }
//        } else if (rd.netCallback != null) {//本地缓存
//            try {
//                rd.netCallback.onSuccess(data);
//                //手动清空对象
//            } catch (Throwable t) {
//                t.printStackTrace();
//            }
//        }
    }

    private void dispatchMessage(ReqData rd, Object data, String[] p) {
        if (rd == null) {
            //未找到回调接口
            return;
        }
        if (rd.socketCallback != null) {
            try {
                rd.socketCallback.onResult(data, p);
                //手动清空对象
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else if (rd.netCallback != null) {//本地缓存
            try {
                rd.netCallback.onSuccess(data);
                //手动清空对象
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    /**
     * 本地任务监听
     * @param rd
     */
    public void registerLocalListener(ReqData rd) {
        try {
            listenerMap.put(rd.api, rd);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * 移除本地任务监听
     * @param api
     */
    public void removeLocalListener(String api) {
        try {
            listenerMap.remove(api);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void removeListener(String url) {
        listenerMap.remove(url);
    }

    public void removeListener(String url, String tag) {
        listenerMap.remove(url + tag);
    }

    /**
     * 订阅消息
     * @param url
     * @param p
     */
    public void subscribe(String url, String[] p, Map<String, Object> maps) {
        //通过核心服务，完成向服务器订阅消息
        subscribe(url, -1, p, maps);
    }

    public void subscribe(String url, long interval, String[] p, Map<String, Object> maps) {
        //通过核心服务，完成向服务器订阅消息
        sendMessage(url, AppConstants.SOCKET.OPEN, interval, p, maps);
    }

    public void subscribeForChange(String url, long interval, String[] p, Map<String, Object> maps) {
        //通过核心服务，完成向服务器订阅消息
        sendMessage(url, AppConstants.SOCKET.CHANGE, interval, p, maps);
    }

    /**
     * 取消订阅消息
     * @param url
     * @param p
     */
    public void unsubscribe(String url, String[] p) {
        //通过核心服务，完成向服务器反订阅消息
        sendMessage(url, AppConstants.SOCKET.CLOSE, p, null);
    }

    public void updateLanguage(String language) {
        Map dataMap = new HashMap();
        dataMap.put("language", language);
        String json = GsonUtil.obj2Json(dataMap, Map.class);
        connecter.updateConfig(json);
    }
}