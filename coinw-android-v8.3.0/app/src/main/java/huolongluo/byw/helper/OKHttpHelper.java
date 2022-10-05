package huolongluo.byw.helper;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import huolongluo.byw.BuildConfig;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.bywx.handler.DataAdapterHandler;
import huolongluo.bywx.helper.TestHelper;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.EncryptUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
public class OKHttpHelper {
    private static final String TAG = "OKHttpHelper";
    private static Handler ler = new Handler(Looper.getMainLooper());
    private static OKHttpHelper instance = new OKHttpHelper();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient httpClient = null;
    private Map<String, Call> callMap = new HashMap<>();

    private OKHttpHelper() {
//        OkHttpClient.Builder builder = SSLHelper.getBuilder(10, 30, 10);
//        client = builder.addNetworkInterceptor(new NetInterceptor()).retryOnConnectionFailure(true).build();
//        client = builder.retryOnConnectionFailure(false).build();
    }

    public static OKHttpHelper getInstance() {
        //Caused by: java.lang.RuntimeException: Can't create handler inside thread Thread[RxIoScheduler-2,5,main] that has not called Looper.prepare()
        //    at android.os.Handler.<init>(Handler.java:207)
        //    at android.os.Handler.<init>(Handler.java:119)
        //    at g.a.d.v.<clinit>(OKHttpHelper.java:1)
        //    ... 16 more
//        if (instance == null) {
//            instance = new OKHttpHelper();
//        }
        return instance;
    }

    /**
     * 移除 取消网络任务
     * @param tag
     */
    public void removeRequest(String tag) {
        //Logger.getInstance().debug(TAG, "移除请求" + tag);
        //获取KEY的集合
//        Set<Map.Entry<String, Call>> entries = callMap.entrySet();
        //如果包含KEY
        if (callMap.containsKey(tag)) {
            //获取对应的Call
            Call call = callMap.get(tag);
            //解决已知Crash问题
            //java.lang.NullPointerException: Attempt to invoke virtual method 'boolean n.L.d()' on a null object reference
            //	at g.a.d.y.a(OKHttpHelper.java:6)
            //如果没有被取消 执行取消的方法
            if (call != null && !call.isCanceled()) {
                call.cancel();
            }
            //移除对应的KEY
            callMap.remove(tag);
        }
    }

    public void removeAllRequest() {
        //Logger.getInstance().debug(TAG, "移除所有请求");
        //获取KEY的集合
//        Set<Map.Entry<String, Call>> entries = callMap.entrySet();
        //如果包含KEY
        Set<Map.Entry<String, Call>> set = callMap.entrySet();
        for (Map.Entry<String, Call> entry : set) {
            Call call = entry.getValue();
            String req = entry.getKey();
            //Logger.getInstance().debug(TAG, "remove url is: " + req);
            if (call != null && !call.isCanceled()) {
                call.cancel();
            }
        }
        callMap.clear();
    }

    private OkHttpClient getClient() {
        if (httpClient == null) {
            httpClient = getClient(10, 30, 10);
        }
        return httpClient;
    }

    private OkHttpClient getClient(long connectTimeout, long readTimeout, long writeTimeout) {
        OkHttpClient.Builder builder = SSLHelper.getBuilder(connectTimeout, readTimeout, writeTimeout);
//        client = builder.addNetworkInterceptor(new NetInterceptor()).retryOnConnectionFailure(true).build();
        OkHttpClient client = builder.retryOnConnectionFailure(false).build();
        return client;
    }

    /**
     * get请求使用该方法
     * @param url
     * @param callback
     * @param type
     */
    public void get(final String url, final INetCallback callback, final Type type) {
        this.get(url, null, callback, type);
    }

    /**
     * get请求不推荐使用该方法，map传入会使参数明文拼接在url上面
     * @param url
     * @param map
     * @param callback
     * @param type
     */
    public void get(String url, Map<String, Object> map, final INetCallback callback, final Type type) {
        //check network
        if (!AppUtils.checkNetworkConnected(callback)) {
            return;
        }
        Call c = callMap.get(url);
        if (c != null && c.isExecuted() && !c.isCanceled()) {
            Logger.getInstance().debugThreadLog(TAG, url);
            return;
        }
        if (map == null) {
            map = new HashMap<String, Object>();
            map.put("_type", 1);
        }
        //TODO 待深度优化
//        // 对url和参数做一下拼接处理
        StringBuffer urlSb = new StringBuffer();
        urlSb.append(url);
        if (url.contains("?")) {
            // 如果？不在最后一位
            if (urlSb.indexOf("?") != urlSb.length() - 1) {
                urlSb.append("&");
            }
        } else {
            urlSb.append("?");
        }
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                urlSb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        if (urlSb.indexOf("&") != -1) {
            urlSb.deleteCharAt(urlSb.lastIndexOf("&"));
        }
        String u = urlSb.toString();
        if (u.endsWith("?")) {
            u = urlSb.deleteCharAt(urlSb.lastIndexOf("?")).toString();
        }
        if (u.indexOf("?") != -1) {
            if (u.indexOf("body=") == -1) {
                u = u + "&" + EncryptUtils.encryptStr(map);
            }
        } else {
            if (u.indexOf("body=") == -1) {
                u = u + "?" + EncryptUtils.encryptStr(map);
            }
        }
//        Logger.getInstance().info(TAG, "get url: " + urlSb);
        // new 一个OkHttpClient对象
        OkHttpClient client = getClient();
        Request request = AppUtils.getRequestBuilder(u).build();
        Call call = client.newCall(request);
        callMap.put(url, call);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                try {
                    callMap.remove(url);
                } catch (Throwable t) {
                }
                if (callback != null) {
                    ler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                callback.onFailure(e);
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    callMap.remove(url);
                } catch (Throwable t) {
                }
                String result = response.body().string();
                if (result != null) {
                    String logStr = String.format("%s\n%s", url, result);
                    Logger.getInstance().debug(logStr);
                }
                parse(url, result, type, callback);
            }
        });
    }

    public void post(final String url, Map<String, Object> map, Callback callback) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            builder.add(entry.getKey() + "", entry.getValue() + "");
        }
        final String paramStr = GsonUtil.obj2Json(map, Map.class) + "";
        FormBody formBody = builder.build();
        String logStr = String.format("request %s,%s", url, paramStr);
        Logger.getInstance().debug(logStr);
        Request request = AppUtils.getRequestBuilder(url).post(formBody).build();
        OkHttpClient client = getClient();
        if (client == null) {
            //极限情况下，client可能为空
            Logger.getInstance().error(TAG, "client is null,so return!");
            return;
        }
        Call call = client.newCall(request);
        callMap.put(url, call);
        if (callback != null) {
            call.enqueue(callback);
        }
    }

    public void post(final String url, Map<String, Object> map, boolean longTime, Callback callback) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            builder.add(entry.getKey() + "", entry.getValue() + "");
        }
        final String paramStr = GsonUtil.obj2Json(map, Map.class) + "";
        FormBody formBody = builder.build();
        String logStr = String.format("request %s,%s", url, paramStr);
        Logger.getInstance().debug(logStr);
        Request request = AppUtils.getRequestBuilder(url).post(formBody).build();
//        OkHttpClient httpClient = client;
        OkHttpClient client = getClient();
//        if (longTime) {
//            OkHttpClient.Builder clientBuilder = SSLHelper.getBuilder(10, 50, 10);
////            httpClient = clientBuilder.addNetworkInterceptor(new NetInterceptor()).retryOnConnectionFailure(true).build();
//            client = clientBuilder.retryOnConnectionFailure(false).build();
//        }
//        if (client == null) {
//            return;
//        }
        Call call = client.newCall(request);
        if (callback != null) {
            call.enqueue(callback);
        }
    }

    /**
     * //处理服务器响应数据中data为字符串情况
     * @param url
     * @param map
     * @param callback
     * @param type
     */
    public void postForStringResult(final String url, Map<String, Object> map, final INetCallback callback, final Type type) {
        //处理服务器响应数据中data为字符串情况
        doPost(url, map, callback, type, true);
    }

    public void post(final String url, Map<String, Object> map, final INetCallback callback, final Type type) {
        doPost(url, map, callback, type, false);
    }

    private void parse(final String url, Reader reader, final Type type, final boolean isStr, final INetCallback callback) {
        if (Logger.DEBUG) {
            //Logger.getInstance().debug(TAG, "url: " + url + " parse.");
        }
        Observable.just(reader).observeOn(Schedulers.io()).map(new Func1<Reader, Object>() {
            @Override
            public Object call(Reader r) {
                try {
                    if (isStr) {//处理服务器响应数据中data为字符串情况
                        Type strType = new TypeToken<SingleResult<String>>() {
                        }.getType();
                        SingleResult<String> result = GsonUtil.json2Obj(r, strType);
                        if (result == null) {
                            return null;
                        }
                        return DataAdapterHandler.getObject(url, result.data, type);
                    } else {
                        return DataAdapterHandler.getObject(url, r, type);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    if (reader != null) {
                        //TODO 测试专用
                        TestHelper.debug(url, reader);
                        //
                        try {
                            Util.closeQuietly(r);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Object>() {
            @Override
            public void call(final Object retObj) {
                if (Logger.DEBUG) {
                    //Logger.getInstance().debug(TAG, "url: " + url + " callback." + GsonUtil.obj2Json(retObj, SingleResult.class));
                }
                if (callback == null) {
                    return;
                }
                try {
                    callback.onSuccess(retObj);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
            Log.e(TAG, "Throwable " + throwable.getMessage());
        });
    }

    private void parse(final String url, String text, final Type type, final INetCallback callback) {
        if (Logger.DEBUG) {
            //Logger.getInstance().debug(TAG, "url: " + url + " parse.   text:" + text);
        }
        Observable.just(text).observeOn(Schedulers.io()).map(new Func1<String, Object>() {
            @Override
            public Object call(String s) {
                try {
                    //TODO 测试专用
                    TestHelper.debug(url, s);
                    return DataAdapterHandler.getObject(url, s, type);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Object>() {
            @Override
            public void call(final Object retObj) {
                if (Logger.DEBUG) {
                    //Logger.getInstance().debug(TAG, "url: " + url + " callback.");
                }
                if (callback == null) {
                    return;
                }
                try {
                    callback.onSuccess(retObj);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
            Log.e(TAG, "Throwable " + throwable.getMessage());
        });
    }

    private void doPost(final String url, Map<String, Object> map, final INetCallback callback, final Type type, boolean isStr) {
        //check network
        if (!AppUtils.checkNetworkConnected(callback)) {
            return;
        }
//        OkHttpClient client = new OkHttpClient();
//        OkHttpClient client = new OkHttpClient.Builder().addNetworkInterceptor(new NetInterceptor()).retryOnConnectionFailure(true).build();
        boolean existUrl = false;
        Call c = callMap.get(url);
        if (c != null && c.isExecuted() && !c.isCanceled()) {
            Logger.getInstance().debugThreadLog(TAG, url);
            existUrl = true;
        }
        boolean existCall = false;
        //检查参数是否一至
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            builder.add(entry.getKey() + "", entry.getValue() + "");
            if (existCall && existUrl) {
                if (c != null && c.request() != null) {
                    String value = c.request().header(entry.getKey());
                    if (TextUtils.isEmpty(value)) {
                        existCall = true;
                    } else if (!TextUtils.equals(entry.getValue() + "", value)) {
                        existCall = true;
                    }
                }
            }
        }
        if (existCall) {
            return;
        }
        final String paramStr = GsonUtil.obj2Json(map, Map.class) + "";
        FormBody formBody = builder.build();
        String logStr = String.format("request %s,%s", url, paramStr);
        Logger.getInstance().debug(logStr);
        //测试专用
        if (BuildConfig.DEBUG && BuildConfig.VERSION_CODE < 45) {
            //k线数据跟踪
            if (url.contains("exchangeKline.html")) {
                Logger.getInstance().debug(TAG, "exchangeKline.html");
            }
        }
        Request request = AppUtils.getRequestBuilder(url).post(formBody).build();
        OkHttpClient client = getClient();
        if (client == null) {
            //极限情况下，client可能为空
            Logger.getInstance().error(TAG, "client is null,so return!");
            return;
        }
        Call call = client.newCall(request);
        callMap.put(url, call);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                e.printStackTrace();
                String logStr = String.format("error %s,%s", url, paramStr);
                Logger.getInstance().error(logStr, new Exception());
                try {
                    callMap.remove(url);
                } catch (Throwable t) {
                }
                if (callback == null) {
                    return;
                }
                ler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callback.onFailure(e);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    callMap.remove(url);
                } catch (Throwable t) {
                }
                //测试专用
                if (BuildConfig.DEBUG && BuildConfig.VERSION_CODE < 45) {
                    //k线数据跟踪
                    if (url.contains("exchangeKline.html")) {
                        Logger.getInstance().debug(TAG, "exchangeKline.html");
                    }
                }
                //TODO 注意事项
                // com.google.gson.JsonSyntaxException: java.lang.IllegalStateException: closed
                //拦截器里面,打印了请求体,有说法是,response.body() 只能调用一次,再次调用会出现状态异常
                ResponseBody rb = response.body();
                if (isStr) {
                    parse(url, rb.string(), type, callback);
                } else {
                    parse(url, rb.charStream(), type, isStr, callback);
                }
            }
        });
    }

    public String get(String url) throws IOException {
        OkHttpClient client = getClient();
        if (client == null) {
            //极限情况下，client可能为空
            Logger.getInstance().error(TAG, "client is null,so return!");
            return "";
        }
        Request req = AppUtils.getRequestBuilder(url).build();
        Response rep = client.newCall(req).execute();
        return rep.body().string();
    }

    public String get(String url, Headers.Builder builder) throws IOException {
        return get(url, builder, 10, 30, 10);
    }

    public String get(String url, Headers.Builder builder, long connectTimeout, long readTimeout, long writeTimeout) throws IOException {
        OkHttpClient client = getClient(connectTimeout, readTimeout, writeTimeout);
        if (client == null) {
            //极限情况下，client可能为空
            Logger.getInstance().error(TAG, "client is null,so return!");
            return "";
        }
        Request req = AppUtils.getRequestBuilder(url).headers(builder.build()).build();
        Response rep = client.newCall(req).execute();
        return rep.body().string();
    }

    public String post(String url, Map<String, Object> map) {
//        OkHttpClient client = new OkHttpClient();
        OkHttpClient client = getClient();
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            builder.add(entry.getKey() + "", entry.getValue() + "");
        }
        FormBody formBody = builder.build();
        Request request = AppUtils.getRequestBuilder(url).post(formBody).build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            if (response == null) {
                return "";
            }
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String post(String url, String json) throws IOException {
//        if (client == null) {
//            //极限情况下，client可能为空
//            Logger.getInstance().error(TAG, "client is null,so return!");
//            return "";
//        }
        OkHttpClient client = getClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request req = AppUtils.getRequestBuilder(url).post(body).build();
        /*
         * Call call = client.newCall(request); call.
         */
        Response rep = client.newCall(req).execute();
        return rep.body().string();
    }

    /**
     * 上传
     * @param url
     * @param filePath
     * @param affixType
     * @param callback
     * @param type
     */
    public void upload(final String url, final String filePath, final int affixType, final INetCallback callback, final Type type) {
        // type:1//文件类型
        // 1身份证正面
        // 2身份证反面
        // 3健康证正面
        // 4健康证反面
        // 5合同
        // 6培训视频
        // 7头像
        OkHttpClient client = new OkHttpClient();
        File file = new File(filePath);
        String tmpUrlStr = url;
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (file != null) {
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
            String filename = file.getName();
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("file", filename, body).addFormDataPart("type", String.valueOf(affixType));
        }
        if (client == null) {
            //极限情况下，client可能为空
            Logger.getInstance().error(TAG, "client is null,so return!");
            return;
        }
        final String tmpUrl = tmpUrlStr;
        Request request = AppUtils.getRequestBuilder(url).post(requestBody.build()).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                // String logStr = String.format("url:%s,filePath:%s,type:%s,result:%s", url, filePath, affixType,
                // result);
                String logStr = String.format("%s,%s,%s\n%s", tmpUrl, filePath, affixType, result);
                Logger.getInstance().debug(logStr);
                final Object o = GsonUtil.json2Obj(result, type);
                ler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callback.onSuccess(o);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    static X509TrustManager x509m = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };
}
