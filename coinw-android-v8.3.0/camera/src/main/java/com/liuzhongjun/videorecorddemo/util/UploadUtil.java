package com.liuzhongjun.videorecorddemo.util;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class UploadUtil {
    private OkHttpClient okHttpClient;

    private UploadUtil() {
        okHttpClient = new OkHttpClient();
    }

    /**
     * 使用静态内部类的方式实现单例模式
     */
    private static class UploadUtilInstance {
        private static final UploadUtil INSTANCE = new UploadUtil();
    }

    public static UploadUtil getInstance() {
        return UploadUtilInstance.INSTANCE;
    }

    /**
     * @param url  服务器地址
     * @param file 所要上传的文件
     * @return 响应结果
     * @throws IOException
     */
    public ResponseBody upload(Context context, String url, File file, File file2) throws IOException {
//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(120, TimeUnit.SECONDS)
//                .readTimeout(120, TimeUnit.SECONDS)
//                .writeTimeout(120, TimeUnit.SECONDS)//
//                .build();
        OkHttpClient client = getOkHttpClient(context);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("firstVideo ", file.getName(),

                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .addFormDataPart("secondVideo ", file.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"), file2))
                .addFormDataPart("loginToken", CameraApp.token)
                .addFormDataPart("body", CameraApp.body)
                .build();
//        if(url.startsWith("https")){
//            url = url.replace("https","http");
//        }
        Request request = new Request.Builder()
                .header("Authorization", "ClientID" + UUID.randomUUID())
                .addHeader(getHeadKey(), getHeadValue(11, url))
                .header("deviceId", CameraApp.deviceId)
                .url(url)
                .header("loginToken", CameraApp.token)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response.code() + " message: " + response.message());
        }
        return response.body();
    }

    /*********************知道创宇安全防护**********************/
    public static String getHeadKey() {
        return "X-JSL-API-AUTH";
    }

    public static final String TOKEN_SAFE = "aac0e8bf095446fea1d9675b051c2b9f";//知道创宇token值

    /**
     * 加密
     *
     * @param plaintext 明文
     * @return ciphertext 密文
     */
    public final static String encrypt(String plaintext) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] btInput = plaintext.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getHeadValue(int randomLength, String url) {
        String authToken = TOKEN_SAFE; // Token
        final String expTime = ((System.currentTimeMillis() / 1000) + 6000000) + ""; // 时间
        String rd = getRandom(randomLength); // 随机数
        // 原生字符串
        String str = "md5" + "|" + authToken + "|" + expTime + "|" + rd + "|" + url;
        // MD5 加密后的字符串
        String encrypted = encrypt(str);
//        Logger.getInstance().debug("getHeadValue: ==========原生字符串： " + str);
//        Logger.getInstance().debug("getHeadValue: =======加密后的字符串： " + encrypted);
//        Logger.getInstance().debug("getHeadValue: ===========请求头key： " + "X-JSL-API-AUTH");
//        Logger.getInstance().debug("getHeadValue: =========请求头value： " + "md5" + "|" + expTime + "|" + rd + "|" + encrypted);
        return "md5" + "|" + expTime + "|" + rd + "|" + encrypted;
    }

    /**
     * @param strLength 要获取的字符串长度
     *                  <p>
     *                  获取指定位数的一个随机字符串
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
    private OkHttpClient getOkHttpClient(Context context) {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(1500, TimeUnit.SECONDS) //
                .readTimeout(1200, TimeUnit.SECONDS) //
                .writeTimeout(1200, TimeUnit.SECONDS)//
                .retryOnConnectionFailure(false).build();
        X509TrustManager trustManager;
        SSLSocketFactory sslSocketFactory;
        try {
            trustManager = trustManagerForCertificates(trustedCertificatesInputStream(context));
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        builder.sslSocketFactory(CameraSSLSocketClient.getSSLSocketFactory(), x509m);
        builder.hostnameVerifier(CameraSSLSocketClient.getHostnameVerifier());
        return builder.build();
    }

    X509TrustManager x509m = new X509TrustManager() {
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

    private static InputStream trustedCertificatesInputStream(Context context) {
        InputStream inputStream = null;
        try {
//            inputStream = BaseApp.getSelf().getAssets().open("coinw.cer");
//            inputStream = BaseApp.getSelf().getAssets().open("coinw.ai.cer");
            inputStream = context.getAssets().open("appn.cer");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    private static X509TrustManager trustManagerForCertificates(InputStream in) throws GeneralSecurityException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        }
        char[] password = "password".toCharArray();
        // Put the certificates a key store.
        KeyStore keyStore = newEmptyKeyStore(password);
        int index = 0;
        for (Certificate certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            //keyStore.setCertificateEntry(certificateAlias, certificate);
            //appn未加载anchor
            keyStore.setCertificateEntry("anchor", certificate);
        }
        // Use it to build an X509 trust manager.
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }

    private static KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream in = null;
            keyStore.load(in, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}