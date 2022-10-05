package huolongluo.byw.util;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.helper.NetInterceptor;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.helper.SSLHelper;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.bywx.utils.AppUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
/**
 * <p>
 * Created by 火龙裸 on 2017/11/07.
 */
public class OkhttpManager {
    private OkHttpClient client;
    private volatile static OkhttpManager instance = new OkhttpManager();
    private Handler mHandler;
    private Map<String, Call> callMap = new HashMap<>();
    private static final String TAG = "OkHttpManager";
    private String outTimeE = "网络超时，请稍后重试";
    private String systemE = "服务器异常，请稍后重试";
    private static RSACipher rsaCipher = new RSACipher();

    /**
     * 单例模式 OKhttpManager实例
     */
    public static OkhttpManager getInstance() {
        if (instance == null) {
            instance = new OkhttpManager();
        }
        return instance;
    }

    public OkhttpManager() {
//        client = new OkHttpClient().newBuilder().connectTimeout(25, TimeUnit.SECONDS).build();
        outTimeE = AppUtils.getString(R.string.aa56);
        systemE = AppUtils.getString(R.string.err1);
        client = SSLHelper.getBuilder(10, 20, 10).build();
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 移除 取消网络任务
     * @param tag
     */
    public void removeRequest(String tag) {
        Log.i("marketLine", "移除请求" + tag);
        //获取KEY的集合
//        Set<Map.Entry<String, Call>> entries = callMap.entrySet();
        //如果包含KEY
        if (callMap.containsKey(tag)) {
            //获取对应的Call
            Call call = callMap.get(tag);
            //如果没有被取消 执行取消的方法
            if (!call.isCanceled()) {
                call.cancel();
            }
            //移除对应的KEY
            callMap.remove(tag);
        }
    }

    private void println(String url, String msg) {
        if (msg.indexOf("<html>") != 0) {
            msg = msg.replaceAll("\n", "");
            msg = msg.length() > 300 ? msg.substring(0, 300) + " ... " : msg;
            Logger.getInstance().debug(TAG, String.format("url:%s,resp:%s", url, msg));
        } else {
            Logger.getInstance().debug(TAG, String.format("url:%s,resp:%s", url, msg));
        }
    }

    public static void customGetAsync(String url, final DataCallBack callBack) {
        getInstance().p_customGetAsync(url, callBack);
    }

    public static void get(String url, final DataCallBack callback) {
        getInstance().doGet(url, callback);
    }

    public void doGet(String url, final DataCallBack callback) {
        final Request request = AppUtils.getRequestBuilder(url).build();
        OkHttpClient hc = SSLHelper.getBuilder(30, 30, 30).retryOnConnectionFailure(false).build();
        hc.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request, e, callback, outTimeE);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String result = response.body().string();
                    println(request.url().uri().toString(), result);
                    deliverDataSuccess(url, result, callback);
                } catch (Exception e) {
                    e.printStackTrace();
                    deliverDataFailure(request, e, callback, systemE);
                }
            }
        });
    }

    //不反悔200状态码的
    public void p_customGetAsync(final String url, final DataCallBack callBack) {
        final Request request = AppUtils.getRequestBuilder(url).build();
        if (!AppUtils.checkNetworkConnected(request, callBack)) {
            return;
        }
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request, e, callBack, outTimeE);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String result = response.body().string();
                    println(request.url().uri().toString(), result);
                    deliverDataSuccess(url, result, callBack);
                } catch (Exception e) {
                    e.printStackTrace();
                    deliverDataFailure(request, e, callBack, systemE);
                }
            }
        });
    }

    //post请求
//升级
    private void p_postAsync(final String url, Map<String, String> params, final DataCallBack callBack) {
        RequestBody requestBody = null;
        if (params == null) {
            params = new HashMap<String, String>();
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = null;
            if (entry.getValue() == null) {
                value = "";
            } else {
                value = entry.getValue();
            }
            builder.add(key, value);
        }
        requestBody = builder.build();
        final Request request = AppUtils.getRequestBuilder(url).post(requestBody).build();
        if (!AppUtils.checkNetworkConnected(request, callBack)) {
            return;
        }
        Call call = client.newCall(request);
        callMap.put(url, call);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("marketLine", null == e.getMessage() ? "" : e.getMessage());
                //如果是取消了网络请求，则不回调网络请求失败
                if (null != e.getMessage() && e.getMessage().equals("Canceled")) {
                    return;
                }
                deliverDataFailure(request, e, callBack, outTimeE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String result = response.body().string();
                    println(request.url().uri().toString(), result);
                    org.json.JSONObject jsonObject = new org.json.JSONObject(result);
                    if (jsonObject == null) {
                        deliverDataFailure(request, new SystemException("服务器异常"), callBack, "服务器异常");
                        return;
                    }
                    String code = jsonObject.getString("code");
//                    String msg = jsonObject.getString("message");
                    if (TextUtils.equals(code, "200")) {
                        Object object = jsonObject.get("data");
                        deliverDataSuccess(url, object, callBack);
                    } else if (TextUtils.equals(code, "-105")) {
                        if (!BaseApp.UPDATAING) {
                            //0 不需要升级 1-强制升级  2-需要升级（非强制）
                            int forceUpdate = jsonObject.getInt("forceUpdate");
                            if (forceUpdate == 1) {
                                if (!BaseApp.UPDATAING) {
                                    BaseApp.UPDATAING = true;
                                    EventBus.getDefault().post(new Event.NeedUpdate(true));
                                }
                            }
                        }
                    } else {
                        int code1 = Integer.parseInt(code);
                        result = result.replace("\"" + code + "\"", code1 + "");
                        deliverDataSuccess(url, result.replace("message", "value"), callBack);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    deliverDataFailure(request, e, callBack, systemE);
                    println(request.url().uri().toString(), systemE);
                }
            }
        });
    }

    //非升级
    private void p_postAsy(String url, Map<String, String> params, final DataCallBack callBack) {
        RequestBody requestBody = null;
        if (params == null) {
            params = new HashMap<String, String>();
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = null;
            if (entry.getValue() == null) {
                value = "";
            } else {
                value = entry.getValue();
            }
            builder.add(key, value);
        }
        requestBody = builder.build();
        final Request request = AppUtils.getRequestBuilder(url)
                // .addHeader("appVersion", "29")
                .post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request, e, callBack, outTimeE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String result = response.body().string();
                    println(request.url().uri().toString(), result);
                    org.json.JSONObject jsonObject = new org.json.JSONObject(result);
                    if (jsonObject != null) {
                        String code = jsonObject.getString("code");
                        if (TextUtils.equals(code, "200")) {
                            Object object = jsonObject.get("data");
                            deliverDataSuccess(url, object, callBack);
                        } /*else if (android.text.TextUtils.equals(code, "-105")) {
                            //0 不需要升级 1-强制升级  2-需要升级（非强制）
                            int forceUpdate = jsonObject.getInt("forceUpdate");
                            if(forceUpdate==1){
                                EventBus.getDefault().post(new Event.NeedUpdate(true));
                            }

                        }*/ else {
                            // deliverDataFailure(request, new SystemException(msg), callBack, msg);
                            int code1 = Integer.parseInt(code);
                            result = result.replace("\"" + code + "\"", code1 + "");
                            deliverDataSuccess(url, result.replace("message", "value"), callBack);
                        }
                    } else {
                        deliverDataFailure(request, new SystemException("服务器异常"), callBack, "服务器异常");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    deliverDataFailure(request, e, callBack, systemE);
                } catch (JSONException e) {
                    deliverDataFailure(request, e, callBack, systemE);
                    e.printStackTrace();
                }
            }
        });
    }

    //******************  数据分发的方法  ******************/
    private void deliverDataFailure(final Request request, final Exception e, final DataCallBack callBack, String errorMsg) {
        mHandler.post(new Runnable() {//发送到主线程
            @Override
            public void run() {
                if (callBack != null) {
                    try {
                        callBack.requestFailure(request, e, errorMsg);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }

    private void deliverDataSuccess(final String url, final Object result, final DataCallBack callBack) {
        if (result instanceof org.json.JSONObject) {
            deliverDataSuccess(url, result.toString(), callBack);
        } else if (result instanceof String) {
            deliverDataSuccess(url, result.toString(), callBack);
        } else if (result instanceof JSONObject) {
            deliverDataSuccess(url, ((JSONObject) result).toJSONString(), callBack);
        }
    }

    private void deliverDataSuccess(final String url, final String result, final DataCallBack callBack) {
        String msg = result.length() > 300 ? result.substring(0, 300) + " ... " : result;
        Logger.getInstance().debug(TAG, "url-return: " + url + " currentThread: " + Thread.currentThread().getId() + " result: " + msg);
        mHandler.post(new Runnable() {//同样 发送到主线程
            @Override
            public void run() {
                if (callBack != null) {
                    try {
                        callBack.requestSuccess(result);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }

    public static void postAsync(String url, Map<String, String> params, DataCallBack callBack) {
        if (!params.containsKey("body")) {
            params.put("body", getBody());
        }
        getInstance().p_postAsync(url, params, callBack);//POST提交表单 调用方法
    }

    //非升级
    public static void postAsy(String url, Map<String, String> params, DataCallBack callBack) {
        if (!params.containsKey("body")) {
            params.put("body", getBody());
        }
//        String msg = String.format("url:%s,params:%s", url, params.toString());
        Logger.getInstance().debug(TAG, "postAsy-" + url);
        getInstance().p_postAsy(url, params, callBack);//POST提交表单 调用方法
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getBody() {
        try {
            return rsaCipher.encrypt(URLEncoder.encode("type") + "=" + URLEncoder.encode("1"), AppConstants.KEY.PUBLIC_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static Map<String, String> encrypt(Map<String, String> map) {
        StringBuilder params = new StringBuilder();
        HashMap<String, String> hashMap = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.append("&" + URLEncoder.encode(entry.getKey()) + "=" + URLEncoder.encode(entry.getValue()));
        }
        if (params.toString().startsWith("&")) {
            params.deleteCharAt(0);
        }
        Log.i("加密参数", "params " + params);
        try {
            hashMap.put("body", rsaCipher.encrypt(params.toString(), AppConstants.KEY.PUBLIC_KEY));
            return hashMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String encryptGet(Map<String, String> map) {
        StringBuilder params = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.append("&" + entry.getKey() + "=" + entry.getValue());
        }
        if (params.toString().startsWith("&")) {
            params.deleteCharAt(0);
        }
        Log.i("加密参数", "params " + params);
        try {
            return "body=" + URLEncoder.encode(rsaCipher.encrypt(params.toString(), AppConstants.KEY.PUBLIC_KEY));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void getAsync(String url, DataCallBack callBack) {
        getInstance().p_getAsync(url, callBack);//异步GET 调用方法
    }

    //get请求
    public void p_getAsync(String url, final DataCallBack callBack) {
        final Request request = AppUtils.getRequestBuilder(url).build();
        if (!AppUtils.checkNetworkConnected(request, callBack)) {
            return;
        }
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request, e, callBack, outTimeE);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String result = response.body().string();
                    println(request.url().uri().toString(), result);
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("message");
                    if (TextUtils.isEmpty(msg)) {
                        deliverDataSuccess(url, jsonObject.toJSONString(), callBack);
                        return;
                    }
                    if (!TextUtils.isEmpty(code)) {
                        if (android.text.TextUtils.equals(code, "200")) {
                            Object object = jsonObject.get("data");
                            deliverDataSuccess(url, object, callBack);
                        } else if (android.text.TextUtils.equals(code, "-105")) {
                            if (!BaseApp.UPDATAING) {
                                //0 不需要升级 1-强制升级  2-需要升级（非强制）
                                int forceUpdate = jsonObject.getInteger("forceUpdate");
                                if (forceUpdate == 1) {
                                    if (!BaseApp.UPDATAING) {
                                        BaseApp.UPDATAING = true;
                                        EventBus.getDefault().post(new Event.NeedUpdate(true));
                                    }
                                }
                            }
                        } else {
                            deliverDataFailure(request, new SystemException(msg), callBack, msg);
                        }
                    } else {
                        deliverDataSuccess(url, result, callBack);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    deliverDataFailure(request, e, callBack, systemE);
                } catch (Error error) {
                    error.printStackTrace();
                    deliverDataFailure(request, new Exception(error.getCause()), callBack, systemE);
                }
            }
        });
    }

    //******************  数据回调接口  ******************/
    public interface DataCallBack {
        void requestFailure(Request request, Exception e, String errorMsg);
        void requestSuccess(String result);
    }

    public interface DataToBeanCallBack {
        void requestFailure(Exception e, String errorMsg);
        <T> void requestSuccess(T t);
    }
}
