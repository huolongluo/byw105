package huolongluo.byw.helper;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.android.legend.util.TimerUtil;
import com.legend.common.event.TokenExpired;
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.X509TrustManager;

import huolongluo.byw.BuildConfig;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.bean.ServiceStopBean;
import huolongluo.byw.byw.injection.model.SSLSocketClient;
import huolongluo.byw.byw.ui.activity.stop_service.StopServiceActivity;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.manager.AppManager;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.MD5Util;
import huolongluo.bywx.utils.AppUtils;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

public class SSLHelper {
    private static final String TAG = "SSLHelper";
    private static long lastTime=0;

    public static OkHttpClient.Builder getBuilder(long connectTimeout, long readTimeout, long writeTimeout) {
        connectTimeout = connectTimeout <= 0 ? 0 : connectTimeout;
        readTimeout = readTimeout <= 0 ? 0 : readTimeout;
        writeTimeout = writeTimeout <= 0 ? 0 : writeTimeout;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(connectTimeout, TimeUnit.SECONDS) //
                .readTimeout(readTimeout, TimeUnit.SECONDS) //
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)//
                .retryOnConnectionFailure(false).build();
        builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), x509m); // 忽略https证书
        builder.hostnameVerifier(SSLSocketClient.getHostnameVerifier()); // 忽略https证书
        builder.addInterceptor(chain -> {
            Request request = AppUtils.getRequestBuilder(chain.request().newBuilder(), chain.request().url().toString()).header("loginToken", UserInfoManager.getToken()).build();
            return chain.proceed(request);
        }).addInterceptor(chain -> {
            Response response = chain.proceed(chain.request());
            ResponseBody responseBody = response.body();
            if (response.code() == 200) {
                try {
                    responseBody.source().request(Long.MAX_VALUE); // Buffer the entire body.
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String string = response.peekBody(responseBody.source().buffer().size()).string();
                if (!TextUtils.isEmpty(string) && response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(string);
                        String code = jsonObject.optString("code");
                        if (code.equals(AppConstants.CODE.STOP_SERVICE_CODE)) {//停机维护状态码
                            HttpUrl url = chain.request().url();
                            if (url.toString().contains("app/exchangeDepthV2.html") || url.toString().contains("app/getSuccessDetails.html") || url.toString().contains("/ifcontract/tickers")) {
                            } else if (!CoinwHyUtils.isServiceStop && AppManager.get().isMainActivityExist()) {
                                CoinwHyUtils.isServiceStop = true;
                                ServiceStopBean serviceStopEntity = GsonUtil.json2Obj(string, ServiceStopBean.class);
                                Intent intent = new Intent();
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setClass(BaseApp.getSelf(), StopServiceActivity.class);
                                intent.putExtra("data", serviceStopEntity.getData());
                                BaseApp.getSelf().startActivity(intent);
                            }
                        }
                        else if (code.equals(AppConstants.CODE.LOGIN_EXPIRED)) {//登录失效
                            if(TimerUtil.Companion.isTimeExceedInterval(lastTime,AppConstants.TIMER.TOKEN_EXPIRED)){
                                lastTime=System.currentTimeMillis();
                                EventBus.getDefault().post(new TokenExpired());
                            }
                        }
                    } catch (Throwable t) {
                        Logger.getInstance().debug(TAG,"exp-string: " + string);
                        t.printStackTrace();
                    }
                }
            }
            return response;
        });
        if (BuildConfig.DEBUG || BuildConfig.ENV_DEV) {
            builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }
        return builder;
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

    /*********************知道创宇安全防护**********************/
    public static String getHeadKey() {
        return "X-JSL-API-AUTH";
    }

    public static String getHeadValue(int randomLength, String url) {
        String authToken = AppConstants.COMMON.TOKEN_SAFE; // Token
        final String expTime = ((System.currentTimeMillis() / 1000) + 6000000) + ""; // 时间
        String rd = getRandom(randomLength); // 随机数
        // 原生字符串
        String str = "md5" + "|" + authToken + "|" + expTime + "|" + rd + "|" + url;
        // MD5 加密后的字符串
        String encrypted = MD5Util.encrypt(str);
//        Logger.getInstance().debug("getHeadValue: =========原生字符串： " + str);
//        Logger.getInstance().debug("getHeadValue: =========加密后的字符串： " + encrypted);
        Logger.getInstance().debug("getHeadValue: =========请求头key： " + "X-JSL-API-AUTH");
        Logger.getInstance().debug("getHeadValue: =========请求头value： " + "md5" + "|" + expTime + "|" + rd + "|" + encrypted);
        return "md5" + "|" + expTime + "|" + rd + "|" + encrypted;
    }

    /**
     * @param strLength 要获取的字符串长度
     * <p>
     * 获取指定位数的一个随机字符串
     */
    public static String getRandom(int strLength) {
        Random rand = new Random(System.currentTimeMillis());
        int cnt = 26 * 2 + 10;
        char[] s = new char[strLength];
        for (int i = 0; i < strLength; i++) {
            int v = rand.nextInt(cnt);
            if (v < 10) {
                s[i] = (char) ('0' + v);
            } else if (v < 36) {
                s[i] = (char) (v - 10 + 'A');
            } else {
                s[i] = (char) (v - 36 + 'a');
            }
        }
        return new String(s);
    }
    /*********************知道创宇安全防护**********************/
}
