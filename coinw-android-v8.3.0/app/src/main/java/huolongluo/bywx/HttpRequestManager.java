package huolongluo.bywx;

import android.text.TextUtils;


import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.GsonUtil;

public class HttpRequestManager {

    private static final String TAG = "HttpRequestManager";
    private static HttpRequestManager instance;
    private static volatile ConcurrentHashMap<String, HttpRequest> callbackMap = new ConcurrentHashMap<String, HttpRequest>();

    private HttpRequestManager() {
    }

    public static HttpRequestManager getInstance() {
        if (instance == null) {
            synchronized (HttpRequestManager.class) {
                if (instance == null) {
                    instance = new HttpRequestManager();
                }
            }
        }
        return instance;
    }

    public void post(String url, Map<String, Object> params, INetCallback callback, Type type) {
        doPost(url, params, callback, type);
    }

    public void get(String url, Map<String, Object> params, INetCallback callback, Type type) {
        doGet(url, params, callback, type);
    }

    /**
     * 加入轮询计划
     * @param url
     * @param params
     * @param callback
     * @param type
     */
    public void addPollingPlan(String url, Map<String, Object> params, INetCallback callback, Type type) {
        if (callbackMap == null || TextUtils.isEmpty(url) || callback == null) {
            return;
        }
        HttpRequest hr = new HttpRequest();
        hr.url = url;
        hr.params = params;
        hr.callback = callback;
        hr.type = type;
        HttpRequest mapHr = callbackMap.get(url);
        Logger.getInstance().debug(TAG, "url: " + url + GsonUtil.obj2Json(params, Map.class));
        if (mapHr != null && mapHr.equals(hr)) {
            Logger.getInstance().debug(TAG, "post url is: " + url + ", already exists.");
        } else {
            Logger.getInstance().debug(TAG, "post url is: " + url + ", add polling plan.");
            callbackMap.put(url, hr);
        }
    }

    public void request(String url) {
        if (callbackMap == null || callbackMap.isEmpty()) {
            return;
        }
        HttpRequest hr = callbackMap.get(url);
        if (hr == null) {
            return;
        }
        doPost(url, hr.params, hr.callback, hr.type);
    }

    public void requestAll() {
        if (callbackMap == null || callbackMap.isEmpty()) {
            return;
        }
        Set<Map.Entry<String, HttpRequest>> set = callbackMap.entrySet();
        for (Map.Entry<String, HttpRequest> entry : set) {
            String url = entry.getKey();
            HttpRequest hr = entry.getValue();
            if (hr == null) {
                continue;
            }
            doPost(url, hr.params, hr.callback, hr.type);
        }
    }

    public void remove(String url) {
        if (callbackMap == null || TextUtils.isEmpty(url)) {
            return;
        }
        callbackMap.remove(url);
        OKHttpHelper.getInstance().removeRequest(url);
    }

    private void doPost(String url, Map<String, Object> params, INetCallback callback, final Type type) {
        try {
            Logger.getInstance().debug(TAG, "do post url is  " + url);
            OKHttpHelper.getInstance().post(url, params, callback, type);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void doGet(String url, Map<String, Object> params, INetCallback callback, final Type type) {
        try {
            Logger.getInstance().debug(TAG, "do post url is  " + url);
            OKHttpHelper.getInstance().get(url, params, callback, type);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static class HttpRequest {

        public String url;
        public Map<String, Object> params;
        public INetCallback callback;
        public Type type;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof HttpRequest) {
                HttpRequest hr = (HttpRequest) obj;
                if (this.params == hr.params && this.callback == hr.callback && this.type == hr.type) {
                    return true;
                }
            }
            return super.equals(obj);
        }
    }
}
