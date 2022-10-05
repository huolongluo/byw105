package huolongluo.bywx.utils;
import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import huolongluo.byw.BuildConfig;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.RSACipher;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
public class EncryptUtils {
    private static RSACipher rsaCipher = new RSACipher();
    private static final String TAG = "EncryptUtils";
    public static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static Map<String, Object> encrypt(Map<String, Object> map) {
        StringBuilder params = new StringBuilder();
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            params.append("&" + URLEncoder.encode(entry.getKey()) + "=" + URLEncoder.encode(String.valueOf(entry.getValue())));
        }
        if (params.toString().startsWith("&")) {
            params.deleteCharAt(0);
        }
        if (BuildConfig.DEBUG) {
            Logger.getInstance().debug(TAG, "params " + params + " map: " + GsonUtil.obj2Json(map, Map.class));
        }
        try {
            hashMap.put("body", rsaCipher.encrypt(params.toString(), AppConstants.KEY.PUBLIC_KEY));
            return hashMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static Map<String, String> encryptForStr(Map<String, String> map) {
        StringBuilder params = new StringBuilder();
        HashMap<String, String> hashMap = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.append("&" + URLEncoder.encode(entry.getKey()) + "=" + URLEncoder.encode(String.valueOf(entry.getValue())));
        }
        if (params.toString().startsWith("&")) {
            params.deleteCharAt(0);
        }
        if (BuildConfig.DEBUG) {
            Logger.getInstance().debug(TAG, "params " + params + " map: " + GsonUtil.obj2Json(map, Map.class));
        }
        try {
            hashMap.put("body", rsaCipher.encrypt(params.toString(), AppConstants.KEY.PUBLIC_KEY));
            return hashMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    public static Map<String, Object> encryptNoEncode(Map<String, Object> map) {
        StringBuilder params = new StringBuilder();
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            params.append("&" + entry.getKey() + "=" + entry.getValue());
        }
        if (params.toString().startsWith("&")) {
            params.deleteCharAt(0);
        }
        if (BuildConfig.DEBUG) {
            Logger.getInstance().debug(TAG, "params " + params + " map: " + GsonUtil.obj2Json(map, Map.class));
        }
        try {
            hashMap.put("body", rsaCipher.encrypt(params.toString(), AppConstants.KEY.PUBLIC_KEY));
            return hashMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String encryptStr(Map<String, Object> map) {
        StringBuilder params = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            params.append("&" + entry.getKey() + "=" + entry.getValue());
        }
        if (params.toString().startsWith("&")) {
            params.deleteCharAt(0);
        }
        try {
            return "body=" + URLEncoder.encode(rsaCipher.encrypt(params.toString(), AppConstants.KEY.PUBLIC_KEY));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String encryptByPublicKey(String data) throws Exception {
        return encryptByPublicKey(data, AppConstants.KEY.PUBLIC_KEY);
    }

    public static String encryptByPublicKey(String data, String publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        PublicKey rsaPublicKey = RSACipher.stringToPublicKey(publicKey);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    public static byte[] decryptByPublicKey(byte[] data, byte[] key) throws Exception {
        //测试通过（普通方式）
        byte[] text = Base64.decode(data, Base64.DEFAULT);
        String publicKeys = new String(key);
        PublicKey pubKey = RSACipher.stringToPublicKey(publicKeys);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, pubKey);
        return cipher.doFinal(text);
    }

    /**
     * 分段方式
     * @param result
     * @return
     * @throws Exception
     */
    public static String decryptByPublicKey(String result) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        PublicKey rsaPublicKey = RSACipher.stringToPublicKey(AppConstants.KEY.PUBLIC_KEY);
        return decrypt(result, rsaPublicKey);
    }

    public static String decrypt(String data, PublicKey key) {
        byte[] text = Base64.decode(data, Base64.DEFAULT);
        //测试通过（分段方式）
        byte[] dectyptedText = null;
        try {
            //使用第三方算法加密库 BC
            final Cipher cipher = Cipher.getInstance(TRANSFORMATION, "BC");
            cipher.init(Cipher.DECRYPT_MODE, key);
            //分段
            int inputLen = text.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > 128) {
                    cache = cipher.doFinal(text, offSet, 128);
                } else {
                    cache = cipher.doFinal(text, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * 128;
            }
            dectyptedText = out.toByteArray();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (dectyptedText == null) {
            return "";
        }
        return new String(dectyptedText);
    }
}
