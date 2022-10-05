package com.android.coinw.http;
import android.text.TextUtils;

import com.android.coinw.ConfigManager;
import com.android.coinw.DataManager;
import com.android.coinw.ServiceConstants;
import com.android.coinw.adapter.IResponseAdapter;
import com.android.coinw.adapter.ResponseAdapter;
import com.android.coinw.model.Message;
import com.android.coinw.model.Request;
import com.android.coinw.utils.ParamUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.helper.SSLHelper;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.GsonUtil;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.EncryptUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Response;
public class HttpManager {
    private static HttpManager instance = new HttpManager();
    //    private OkHttpClient client = SSLHelper.getBuilder(30, 30, 30).addNetworkInterceptor(new NetInterceptor()).retryOnConnectionFailure(true).build();
    private OkHttpClient client = SSLHelper.getBuilder(10, 30, 10).retryOnConnectionFailure(false).build();
    private IResponseAdapter adapter = new ResponseAdapter();
    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            try {
                Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "http-onFailure: " + call.request().url().host());
                adapter.dispatch(call.request().url().url().toString(), e);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            try {
                String url = call.request().url().url().toString();
//                if (TextUtils.equals(url, "http://47.56.86.63/app/getSuccessDetails.html")) {
//                    String result = response.body().string();
//                    Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "result: " + result);
//                    Type type = AdapterUtils.getType(url);
//                    Object obj = DataAdapterHandler.getObject(result, type);
//                    Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "obj == null? " + (obj == null));
//                }
                //更新数据最新获得时间
                DataManager.getInstance().updateReciveTime(url, System.currentTimeMillis(), false);
//                return DataAdapterHandler.getObject(response.body().charStream(), type);
                adapter.dispatch(url, response.body().charStream());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    };

    private HttpManager() {
    }

    private void init() {
//        client = SSLHelper.getBuilder(30, 30, 30).addNetworkInterceptor(new NetInterceptor()).retryOnConnectionFailure(true).build();
//        client = SSLHelper.getBuilder(10, 30, 10).retryOnConnectionFailure(false).build();
    }

    public static HttpManager getInstance() {
        if (instance == null) {
            synchronized (HttpManager.class) {
                if (instance == null) {
                    instance = new HttpManager();
                }
            }
        }
        return instance;
    }

    public void addRequest(Request request) {
        DataManager.getInstance().updateRequest(request);
    }

    public void removeRequest(Request request) {
        DataManager.getInstance().remove(request);
    }

    public void sendRequest(Request request) {
        addRequest(request);
        //TODO 发送HTTP请求
        //TODO 如果未加入策略控制，则需要立即发起请求
        //TODO 待补充
        //如果需要满足更多业务场景，根据业务进行扩展控制
        if (request.immed) {
            String json = GsonUtil.obj2Json(request, Request.class);
            Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "sendRequest: " + json);
            DataManager.getInstance().updateReciveTime(request.url, System.currentTimeMillis(), true);
            post(request.url, request.params, request.method, callback);
        }
    }

    /**
     * 获得最新数据
     */
    public void getLatestData() {
        //HTTP补尝策略
//        getDataForHttp();
        //其他补尝策略
        getPolicyData();
    }

    /**
     * 检测是否通过HTTP补尝策略，来获得数据
     */
    private void getDataForHttp() {
        String json = GsonUtil.obj2Json(DataManager.getInstance().getReciveTimes(), Map.class);
        Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "getDataForHttp-json: " + json);
        Set<Map.Entry<String, Long>> set = DataManager.getInstance().getReciveTimes().entrySet();
        for (Map.Entry<String, Long> entry : set) {
            String key = entry.getKey();
            Long latestTime = entry.getValue();//最新时间
            if (TextUtils.isEmpty(key) || latestTime == null) {
                continue;
            }
            //ws两秒内没有返回数据，则通过http补尝策略
            Message message = DataManager.getInstance().getMessages().get(key);
            if (message == null) {
                continue;
            }
            if (TextUtils.equals(key, AppConstants.SOCKET.klineApi)) {
                if (!checkKlineAndTryHttp(message, latestTime)) {
                    continue;
                }
            }
            String url = key;
            //判断是否不为缓存的HTTP请求
            if (!(key.startsWith("http"))) {
                url = AppConstants.SOCKET.getApi().get(key);
            }
            if (isAllowableTime(url, message.interval, latestTime)) {
                continue;
            }
            Request request = DataManager.getInstance().getRequest(url);
            if (request != null) {
                Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "url: " + url + " request is null ? " + (request == null));
                post(url, request.params, request.method, callback);
            }
        }
    }

    /**
     * 补偿策略
     */
    private void getPolicyData() {
        Set<Map.Entry<String, Request>> set = DataManager.getInstance().getPolicyRequests().entrySet();
        for (Map.Entry<String, Request> entry : set) {
            String url = entry.getKey();
            Request request = entry.getValue();
            if (TextUtils.isEmpty(url) || request == null) {
                continue;
            }
            //ws两秒内没有返回数据，则通过http补尝策略
            String api = ParamUtils.getApiForUrl(url);
            if (TextUtils.equals(api, AppConstants.SOCKET.klineApi)) {
                long latestTime = DataManager.getInstance().getReciveTimes().get(api);
                Message message = DataManager.getInstance().getMessages().get(api);
                if (message == null) {
                    continue;
                }
                if (!checkKlineAndTryHttp(message, latestTime)) {
                    continue;
                }
            }
            Long latestTime = DataManager.getInstance().getReciveTimes().get(api);
            //TODO 因BTC数据返回问题，故作临时处理下
            //TODO 特殊临时处理方法
            //BTC(45) BTC3L(172)
//            Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "pre-policy url: " + request.url + " request is null ? " + (request == null));
//            if (url.indexOf(AppConstants.SOCKET.tradeApi) != -1 || url.indexOf(UrlConstants.exchangeDepth) != -1) {
//                //&& !TextUtils.isEmpty(request.ciphertext) && request.ciphertext.indexOf("45") != -1 || request.ciphertext.indexOf("172") != -1
//                //安全测试
//                if (url.indexOf(UrlConstants.exchangeDepth) != -1) {
//                }
//                if (request.params.containsValue(45) || request.params.containsValue("45") || request.params.containsValue(172) || request.params.containsValue("172")) {
//                    //
//                    Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "btc-policy url: " + request.url + " request is null ? " + (request == null));
//                    //更新最新时间
//                    post(request.url, request.params, request.method, callback);
//                    continue;
//                }
//            }
            if (isAllowableTime(request.url, request.interval, latestTime)) {
                continue;
            }
            DataManager.getInstance().updateReciveTime(api, System.currentTimeMillis(), false);
            Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "crr-policy url: " + request.url + " request is null ? " + (request == null));
            //更新最新时间
            post(request.url, request.params, request.method, callback);
        }
    }

    private boolean isAllowableTime(String url, Long interval, Long latestTime) {
        if (TextUtils.isEmpty(url)) {
            return true;
        }
        Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "url: " + url + " interval: " + interval + " latestTime: " + latestTime);
        if (latestTime == null || latestTime <= 0L) {
            DataManager.getInstance().getReciveTimes().put(url, System.currentTimeMillis());
            return true;
        }
        interval = interval <= 0L ? 4000L : interval;
        //在允许间隔时间范围内（根据业务要求）
        if (System.currentTimeMillis() - latestTime <= interval) {
            return true;
        }
        return false;
    }

    private boolean checkKlineAndTryHttp(Message message, long latestTime) {
        //TODO 由历史原因暂处理方法
        if (TextUtils.equals(message.api, AppConstants.SOCKET.klineApi)) {
            if (message.params == null || message.params.length < 2) {
                return false;
            }
            String p = message.params[1];
            try {
                if (Integer.parseInt(p) <= 300) {//1分钟和5分钟
                    if (System.currentTimeMillis() - latestTime >= 7500L) {//7.5s
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void post(String url, Map<String, Object> map, int method, Callback callback) {
        //TODO 临时处理方法，过滤掉websocket/socketServer
        //尝试先取消上次请求
//        DataManager.getInstance().removeRequestCall(url);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.endsWith("socketServer")) {
            return;
        }
        Call oldCall = DataManager.getInstance().getRequestCall(url);
        if (oldCall != null && !oldCall.isCanceled() && oldCall.isExecuted()) {
            Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "post: " + url, new Exception());
        }
        //
        try {
            if (map == null) {
                map = new HashMap<String, Object>();
            }
            Object obj = map.get("loginToken");
            String loginToken = obj == null ? "" : String.valueOf(obj);
            FormBody.Builder builder = new FormBody.Builder();
            if (map == null) {
                map = new HashMap();
            }
            map = EncryptUtils.encrypt(map);
            map.put("loginToken", loginToken);
            //TODO 其他补充参数
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                builder.add(entry.getKey() + "", entry.getValue() + "");
            }
            final String paramStr = GsonUtil.obj2Json(map, Map.class) + "";
            FormBody formBody = builder.build();
            String logStr = String.format("request %s,%s", url, paramStr);
            Logger.getInstance().debug(logStr);
            String targetUrl = method == AppConstants.COMMON.VAL_HTTP_POST ? url : url + "?" + EncryptUtils.encryptStr(map);
            //注意跨进行调用SharedPerfrence的问题
            okhttp3.Request.Builder rb = AppUtils.getRequestBuilder(targetUrl, ConfigManager.getInstance().getLanguage());
            okhttp3.Request request;
            // "?" + EncryptUtils.encryptStr(params);
            if (method == AppConstants.COMMON.VAL_HTTP_GET) {
                request = rb.get().build();
//                url = url + "?" + EncryptUtils.encryptStr(map);
            } else {
                request = rb.post(formBody).build();
            }
//            okhttp3.Request request = method == AppConstants.COMMON.VAL_HTTP_POST ? rb.post(formBody).build() : rb.get().build();//处理调用方式
//            OkHttpClient client = SSLHelper.getBuilder(10, 30, 10).retryOnConnectionFailure(false).build();
//            if (client == null) {
//                init();
//            }
            Call call = client.newCall(request);
            //加入请求管理控制
            DataManager.getInstance().addRequestCall(url, call);
            if (callback != null) {
                call.enqueue(callback);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}