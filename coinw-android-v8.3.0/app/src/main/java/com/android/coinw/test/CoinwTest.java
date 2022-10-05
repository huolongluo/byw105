package com.android.coinw.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.net.DomainHelper;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.RiseFallResult;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.reform.bean.LoginBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.ApkUtils;
import huolongluo.byw.util.DeviceUtils;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.uploadimage.BitmapUtils;
import huolongluo.bywx.utils.EncryptUtils;
import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 代码级跟踪测试
 * 生产环境配置会删除该类的内容
 */
public final class CoinwTest {
    private static final String TAG = "CoinwTest";

    public static void login(String phone, String pwd) {
        String code = "888888";
        String imei = DeviceUtils.getImei(BaseApp.getSelf());
        Map<String, Object> params = new HashMap<>();
        params.put("email", phone);
        params.put("password", pwd);
        params.put("phoneCode", code);
        params.put("imei", imei);
        params = EncryptUtils.encrypt(params);
        String symbol = "1000" + System.currentTimeMillis() + "5";
        params.put("random", symbol);
        params.put("appVersion", ApkUtils.getVersionCode());
        params.put("type", 1);
        params = EncryptUtils.encrypt(params);
        //请求地区数据
        Type type = new TypeToken<SingleResult<LoginBean>>() {
        }.getType();
        INetCallback<SingleResult<LoginBean>> callback = new INetCallback<SingleResult<LoginBean>>() {
            @Override
            public void onSuccess(SingleResult<LoginBean> result) throws Throwable {
                Logger.getInstance().debug(TAG, "login result!", new Exception());
            }

            @Override
            public void onFailure(Exception e) throws Throwable {
                Logger.getInstance().debug(TAG, "error", e);
            }
        };
        OKHttpHelper.getInstance().post(UrlConstants.LIST_AREA, params, callback, type);
    }

    public static void upload() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                //kyc/video/upload--KYC上传视频
                //kycUpload();
                //app/otc/user/upload.html--OTC上传图片
//                otcUserUpload();
                //app/validateIdentityUpload.html--上传身份证照片
//                validateIdentityUpload();
                //app/detection/upload--网络检测图片上传接口
                detectionUpload();
            }
        }.start();
    }

    private static void detectionUpload() {
        String path = "/sdcard/DCIM/camera/IMG_20190701_184851.jpg";
        HashMap<String, String> params = new HashMap<>();
        //第一步:将Bitmap压缩至字节数组输出流ByteArrayOutputStream
        Bitmap bitmap = BitmapUtils.getSmallBitmap(path);
        final ByteArrayOutputStream[] byteArrayOutputStream = {new ByteArrayOutputStream()};
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream[0]);
        //第二步:利用Base64将字节数组输出流中的数据转换成字符串String
        byte[] byteArray = byteArrayOutputStream[0].toByteArray();
        String imageString1 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        params.put("file", imageString1);
        params.put("detectionId", UUID.randomUUID().toString());
        params.put("ext1", "png");
        params.put("loginToken", SPUtils.getLoginToken());
        OkhttpManager.postAsync(UrlConstants.UPLOAD_NETWORK_DETECT_PIC, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                Logger.getInstance().error(e);
            }

            @Override
            public void requestSuccess(String result) {
                Logger.getInstance().debug(TAG, result);
            }
        });
    }

    private static void validateIdentityUpload() {
        String path = "/sdcard/DCIM/camera/IMG_20190701_184851.jpg";
        HashMap<String, String> params = new HashMap<>();
        //第一步:将Bitmap压缩至字节数组输出流ByteArrayOutputStream
        Bitmap bitmap = BitmapUtils.getSmallBitmap(path);
        final ByteArrayOutputStream[] byteArrayOutputStream = {new ByteArrayOutputStream()};
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream[0]);
        //第二步:利用Base64将字节数组输出流中的数据转换成字符串String
        byte[] byteArray = byteArrayOutputStream[0].toByteArray();
        String imageString1 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        params.put("filedata1", imageString1);
        params.put("ext1", "png");
        params.put("loginToken", SPUtils.getLoginToken());
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.ValidateIdentityUpload, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                Logger.getInstance().error(e);
            }

            @Override
            public void requestSuccess(String result) {
                Logger.getInstance().debug(TAG, result);
            }
        });
    }

    private static void otcUserUpload() {
        String path = "/sdcard/DCIM/camera/IMG_20190701_184851.jpg";
        //第一步:将Bitmap压缩至字节数组输出流ByteArrayOutputStream
        Bitmap bitmap = BitmapUtils.getSmallBitmap(path);
        final ByteArrayOutputStream[] byteArrayOutputStream = {new ByteArrayOutputStream()};
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream[0]);
        //第二步:利用Base64将字节数组输出流中的数据转换成字符串String
        byte[] byteArray = byteArrayOutputStream[0].toByteArray();
        String imageString1 = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
        HashMap<String, String> params = new HashMap<>();
        params.put("filedata1", imageString1);
        params.put("ext1", "zhifubao.png");
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.upload_image, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                Logger.getInstance().error(e);
            }

            @Override
            public void requestSuccess(String result) {
                Logger.getInstance().debug(TAG, result);
            }
        });
    }

    private static void kycUpload() {
        try {
            String token = UserInfoManager.getToken();
            String body = UserInfoManager.getEnCodeToken(token);
            String url = DomainHelper.getDomain().getHost() + "app/kyc/video/upload";
            File file = new File("/sdcard/DCIM/camera/VID_20191213_161109.mp4");
            File file2 = new File("/sdcard/DCIM/camera/VID_20191213_161109.mp4");
            Response response = doUpload(token, body, url, file, file2);
            if (response == null) {
                Logger.getInstance().debug(TAG, "upload response is null!");
                return;
            }
            String msg = String.format("code:%d,message:%s", response.code(), response.message());
            Logger.getInstance().debug(TAG, msg);
            if (response.body() == null) {
                Logger.getInstance().debug(TAG, "upload response body is null!");
                return;
            }
            String result = response.body().string();
            Logger.getInstance().debug(TAG, result);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static Response doUpload(String token, String body, String url, File file, File file2) throws Throwable {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1500, TimeUnit.SECONDS).readTimeout(1200, TimeUnit.SECONDS).writeTimeout(1200, TimeUnit.SECONDS)//
                .build();
//        OkHttpClient client =getOkHttpClient(context);
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("firstVideo ", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file)).addFormDataPart("secondVideo ", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file2)).addFormDataPart("loginToken", token).addFormDataPart("body", body).build();
        if (url.startsWith("https")) {
            url = url.replace("https", "http");
        }
        Request request = new Request.Builder().header("Authorization", "ClientID" + UUID.randomUUID()).url(url).header("loginToken", token).post(requestBody).build();
        Logger.getInstance().debug(TAG, "upload start!");
        Response response = client.newCall(request).execute();
        Logger.getInstance().debug(TAG, "upload end!");
        return response;
    }

    public static void socketIO() {
        try {

            Socket mSocket = IO.socket("https://192.168.30.75:9099");
//            socketMap.put(url, mSocket);
        } catch (URISyntaxException e) {
            e.printStackTrace();
//            throw new RuntimeException(e);
        }
    }

    public static void testNetWork(int vId) {
        String svrStr = "https://appn.uzip.com";
        if (vId == R.id.tv_svr63) {
            svrStr = "http://47.56.86.63";
        } else if (vId == R.id.tv_svr_178_5) {
            svrStr = "http://47.75.178.5";
        } else if (vId == R.id.tv_svr_online_web) {
            svrStr = "https://coinw.ai";
        }
        String url = svrStr + "/" + UrlConstants.exchangeRiseFall;
        Map<String, Object> params = new HashMap<>();
        params.put("type", "1");
        params = EncryptUtils.encrypt(params);
        Type type = new TypeToken<RiseFallResult>() {
        }.getType();
        INetCallback<RiseFallResult> callback = new INetCallback<RiseFallResult>() {
            @Override
            public void onSuccess(RiseFallResult rfr) throws Throwable {
                Logger.getInstance().debug(TAG, " result is null? " + (rfr == null), new Exception());
                if (rfr != null) {
                    String json = GsonUtil.obj2Json(rfr, RiseFallResult.class);
                    Logger.getInstance().debug(TAG, "json: " + json);
                }
            }

            @Override
            public void onFailure(Exception e) throws Throwable {
                Logger.getInstance().debug(TAG, "error", e);
            }
        };
        OKHttpHelper.getInstance().postForStringResult(url, params, callback, type);
    }

    /**
     * 合并深度2.0
     */
    public static void testDepthV2() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", 1);
        params = EncryptUtils.encrypt(params);
        //请求地区数据
        Type type = new TypeToken<SingleResult<HashMap<String, String[]>>>() {
        }.getType();
        INetCallback<SingleResult<HashMap<String, String[]>>> callback = new INetCallback<SingleResult<HashMap<String, String[]>>>() {
            @Override
            public void onSuccess(SingleResult<HashMap<String, String[]>> result) throws Throwable {
                Logger.getInstance().debug(TAG, "depthV2!", new Exception());
                HashMap<String, String[]> dataMap = result.data;
                String[] btc = new String[]{"0.001", "0.01", "0.0001", "0.1"};
                dataMap.put("45", btc);
                String json = GsonUtil.obj2Json(dataMap, Map.class);
                Logger.getInstance().debug(TAG, "depthV2: " + json);
                //排序
                Arrays.sort(btc);
                String json2 = GsonUtil.obj2Json(dataMap, Map.class);
                Logger.getInstance().debug(TAG, "depthV2: " + json2);
//                SPUtils.saveString(BaseApp.getSelf(), AppConstants.COMMON.KEY_DEPTH_CONFIG, json);
            }

            @Override
            public void onFailure(Exception e) throws Throwable {
                Logger.getInstance().debug(TAG, "error", e);
            }
        };
        String url = "http://172.24.249.2:3000/mock/22/api/v1/public?command=returnDepthConfig";
        OKHttpHelper.getInstance().get(url, params, callback, type);
    }

    private static void sort(String[] values) {
//        List<String> list = Arrays.sort(values);
//        Collections.sort(values, new Comparator<String>() {
//            @Override
//            public int compare(String s1, String s2) {
//                return s1.compareTo(s2);
//            }
//        });
    }

    public static void test(Context context) {
        //TODO 测试验证异常情况
        String t = null;
        //Logger.getInstance().debug("test",t.toLowerCase());
        //
        //.onErrorReturn(throwable -> {
        //            Logger.getInstance().errorLog(throwable);
        //            return 61L;
        //        })
        TextView tv_getcode = null;
        Observable.interval(0, 1, TimeUnit.SECONDS).limit(61).map(aLong -> 120 - aLong).doOnSubscribe(() -> {
            if (tv_getcode != null) {
                tv_getcode.setEnabled(false);
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnCompleted(() -> {
            if (tv_getcode != null) {
                tv_getcode.setText(R.string.aa68);
                tv_getcode.setEnabled(true);
            }
        }).doOnNext(aLong -> {
            Logger.getInstance().debug("test", t.toLowerCase());
            if (tv_getcode != null) {
                tv_getcode.setText(aLong + " s");
            }
        }).doOnError(throwable -> {
            Logger.getInstance().error(throwable);
        }).subscribe();
    }
}
