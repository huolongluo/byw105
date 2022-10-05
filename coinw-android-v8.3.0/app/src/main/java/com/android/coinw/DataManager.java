package com.android.coinw;
import android.text.TextUtils;
import android.util.Log;

import com.android.coinw.model.Message;
import com.android.coinw.model.Request;
import com.android.coinw.utils.MapUtils;
import com.android.coinw.utils.ParamUtils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.GsonUtil;
import okhttp3.Call;
/**
 * 数据管理器
 * <br>
 * 1、管理SOCKET消息
 * 2、管理HTTP请求
 * 3、管理接收数据及接收时间
 */
public class DataManager {
    private static DataManager instance = new DataManager();
    private static volatile ConcurrentHashMap<String, Request> requestMap = new ConcurrentHashMap<String, Request>();
    private static volatile ConcurrentHashMap<String, Message> messageMap = new ConcurrentHashMap<String, Message>();
    //socket和http响应回来，都会更新数据最新时间
    private static ConcurrentHashMap<String, Long> reciveTimeMap = new ConcurrentHashMap<String, Long>();
    private static volatile ConcurrentHashMap<String, Request> policyMap = new ConcurrentHashMap<String, Request>();
    private Map<String, Call> callMap = new HashMap<>();
    private final String TAG = "DataManager";

    public static DataManager getInstance() {
        if (instance == null) {
            synchronized (DataManager.class) {
                if (instance == null) {
                    instance = new DataManager();
                }
            }
        }
        return instance;
    }

    public void updateMessage(Message message) {
        if (message == null || TextUtils.isEmpty(message.api)) {
            return;
        }
        //TODO 大量测试
        messageMap.remove(message.api);
        messageMap.put(message.api, message);
    }

    public void updateRequest(Request request) {
        if (request == null || TextUtils.isEmpty(request.url)) {
            return;
        }
        //TODO 大量测试
        if (request.policy) {
            String key = ParamUtils.getApiForUrl(request.url);
            policyMap.put(key, request);
        } else {
            requestMap.put(request.url, request);
        }
    }

    public void updateReciveTime(String keySrc, Long time, boolean init) {
        if (TextUtils.isEmpty(keySrc)) {
            return;
        }
        String key = ParamUtils.getApiForUrl(keySrc);
        if (TextUtils.isEmpty(key)) {
            return;
        }
        reciveTimeMap.put(key, time);
        Logger.getInstance().debug(TAG, "key: " + key + " time: " + time);
    }

    public void removeRequest(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        key = ParamUtils.getApiForUrl(key);
        reciveTimeMap.remove(key);
        requestMap.remove(key);
        policyMap.remove(key);
    }

    public Map<String, Message> getMessages() {
        return MapUtils.cloneMap(messageMap);
    }

    public Message getMessage(String api) {
        Object obj = MapUtils.cloneMap(messageMap).get(api);
        if (obj instanceof Request) {
            return (Message) obj;
        }
        return null;
    }

    public Map<String, Request> getRequests() {
        return MapUtils.cloneMap(requestMap);
    }

    public Request getRequest(String url) {
        url = ParamUtils.getApiForUrl(url);
        Object obj = MapUtils.cloneMap(requestMap).get(url);
        if (obj == null) {
            obj = MapUtils.cloneMap(policyMap).get(url);
        }
        if (obj instanceof Request) {
            return (Request) obj;
        }
        return null;
    }

    public Map<String, Request> getPolicyRequests() {
        return MapUtils.cloneMap(policyMap);
    }

    public Map<String, Long> getReciveTimes() {
        return MapUtils.cloneMap(reciveTimeMap);
    }

    public void remove(Message message) {
        if (message == null) {
            return;
        }
        Message cacheMessage = messageMap.get(message.api);
        if (cacheMessage == null) {
            return;
        }
        if (!cacheMessage.equals(message)) {//不是同一指令，不清除缓存
            return;
        }
        //TODO 临时加载方法
//        if (message != null && TextUtils.equals(message.api, AppConstants.SOCKET.tradeApi)) {
//            String messageStr = GsonUtil.obj2Json(message, Request.class);
//            String msgStr = GsonUtil.obj2Json(cacheMessage, Request.class);
//            if (!TextUtils.equals(messageStr, msgStr)) {
//                return;
//            }
//        }
        messageMap.remove(message.api);
        reciveTimeMap.remove(message.api);
        //取消补尝策略控制
        String url = ParamUtils.getUrlForApi(message.api);
        Logger.getInstance().debug(TAG, "remove message-request-url: " + url);
        requestMap.remove(url);
        policyMap.remove(url);
        requestMap.remove(message.api);
        policyMap.remove(message.api);
    }

    public void remove(Request request) {
        if (request == null) {
            return;
        }
        Request req = policyMap.get(request.url);
//        if (!checkRemove(request, req)) {
//            return;
//        }
        removeRequest(request.url);
    }

    public void addRequestCall(String key, Call call) {
        callMap.put(key, call);
    }

    public void removeRequestCall(String tag) {
        Logger.getInstance().debug(TAG, "remove request call is : " + tag);
        //获取KEY的集合
//        Set<Map.Entry<String, Call>> entries = callMap.entrySet();
        //如果包含KEY
        if (callMap.containsKey(tag)) {
            //获取对应的Call
            Call call = callMap.get(tag);
            //如果没有被取消 执行取消的方法
            if (call != null && !call.isCanceled()) {
                call.cancel();
            }
            //移除对应的KEY
            callMap.remove(tag);
        }
    }

    public Call getRequestCall(String tag) {
        Logger.getInstance().debug(TAG, "get request call is : " + tag);
        return callMap.get(tag);
    }

    public void removeAllRequest() {
        Logger.getInstance().debug(TAG, "移除所有请求");
        //获取KEY的集合
//        Set<Map.Entry<String, Call>> entries = callMap.entrySet();
        //如果包含KEY
        Set<Map.Entry<String, Call>> set = callMap.entrySet();
        for (Map.Entry<String, Call> entry : set) {
            Call call = entry.getValue();
            String req = entry.getKey();
            Logger.getInstance().debug(TAG, "remove url is: " + req);
            if (call != null && !call.isCanceled()) {
                call.cancel();
            }
        }
        callMap.clear();
    }
}
