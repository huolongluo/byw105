package com.android.coinw.security;
import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.DeviceUtils;
import huolongluo.byw.util.RSAEncrypt;
import huolongluo.bywx.utils.RecordUtils;
public class EncryptUtils {

    private String keys = "-----BEGIN PUBLIC KEY-----\n" + "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCerxM2vXIRyGRvMzOnbIzvXrRh\n" + "fvHyO0JA5SZwoXJJWLdklxeVbjye1l8gmPTI2Hd33U0rFfQNuhlht71v9MxL5Pk/0\n" + "iFAIry3ZPaOj33KgACWWCSH2HGAettuGVblNK5CEH1ppJwsC98sVBWDUBJpAfloLD\n" + "GR6TVfa+zqegLZXQIDAQAB\n" + "-----END PUBLIC KEY-----\n";
    static {
        System.loadLibrary("crypto");
        System.loadLibrary("cipher");
    }

    /**
     * HmacSHA1签名
     * @param context
     * @param src
     * @return
     */
    public native byte[] encodeByHmacSHA1(Context context, byte[] src);

    /**
     * SHA1签名
     * @param src
     * @return
     */
    public native String encodeBySHA1(byte[] src);

    /**
     * SHA224签名
     * @param src
     * @return
     */
    public native String encodeBySHA224(byte[] src);

    /**
     * SHA384签名
     * @param src
     * @return
     */
    public native String encodeBySHA256(byte[] src);

    /**
     * SHA256签名
     * @param src
     * @return
     */
    public native String encodeBySHA384(byte[] src);

    /**
     * SHA512签名
     * @param src
     * @return
     */
    public native String encodeBySHA512(byte[] src);

    /**
     * AES加密
     * @param keys
     * @param src
     * @return
     */
    public native byte[] encodeByAES(byte[] keys, byte[] src);

    /**
     * AES解密
     * @param keys
     * @param src
     * @return
     */
    public native byte[] decodeByAES(byte[] keys, byte[] src);

    /**
     * RSA公钥加密
     * @param keys
     * @param src
     * @return
     */
    public native byte[] encodeByRSAPubKey(byte[] keys, byte[] src);

    /**
     * RSA私钥解密
     * @param keys
     * @param src
     * @return
     */
    public native byte[] decodeByRSAPrivateKey(byte[] keys, byte[] src);

    /**
     * RSA私钥加密
     * @param keys
     * @param src
     * @return
     */
    public native byte[] encodeByRSAPrivateKey(byte[] keys, byte[] src);

    /**
     * RSA公钥解密
     * @param keys
     * @param src
     * @return
     */
    public native byte[] decodeByRSAPubKey(byte[] keys, byte[] src);

    /**
     * RSA私钥签名
     * @param keys
     * @param src
     * @return
     */
    public native byte[] signByRSAPrivateKey(byte[] keys, byte[] src);

    /**
     * RSA公钥验证签名
     * @param keys
     * @param src
     * @param sign
     * @return 1:验证成功
     */
    public native int verifyByRSAPubKey(byte[] keys, byte[] src, byte[] sign);

    /**
     * 异或加解密
     * @param src
     * @return
     */
    public native byte[] xOr(byte[] src);

    /**
     * MD5编码
     * @param src
     * @return 默认小写
     */
    public native String md5(byte[] src);

    /**
     * 获取apk-sha1
     * @param context
     * @return
     */
    public native String sha1OfApk(Context context);

    /**
     * 校验apk签名是否有效
     * @param context
     * @return
     */
    public native boolean verifySha1OfApk(Context context);

    /**
     * HmacSHA1签名
     * @param context
     * @param src
     * @return
     */
    public String encodeByHmacSHA1(Context context, String src) {
        byte[] encodeByHmacSHA1 = this.encodeByHmacSHA1(context, src.getBytes());
        return Base64.encodeToString(encodeByHmacSHA1, Base64.NO_WRAP);
    }

    /**
     * SHA1签名
     * @param src
     * @return
     */
    public String encodeBySHA1(String src) {
        return this.encodeBySHA1(src.getBytes());
    }

    /**
     * SHA224签名
     * @param src
     * @return
     */
    public String encodeBySHA224(String src) {
        return this.encodeBySHA224(src.getBytes());
    }

    /**
     * SHA384签名
     * @param src
     * @return
     */
    public String encodeBySHA256(String src) {
        return this.encodeBySHA256(src.getBytes());
    }

    /**
     * SHA256签名
     * @param src
     * @return
     */
    public String encodeBySHA384(String src) {
        return this.encodeBySHA384(src.getBytes());
    }

    /**
     * SHA512签名
     * @param src
     * @return
     */
    public String encodeBySHA512(String src) {
        return this.encodeBySHA512(src.getBytes());
    }

    /**
     * AES加密
     * @param keys
     * @param src
     * @return
     */
    public String encodeByAES(String keys, String src) {
        byte[] encodeAES = this.encodeByAES(keys.getBytes(), src.getBytes());
        return Base64.encodeToString(encodeAES, Base64.NO_WRAP);
    }

    /**
     * AES解密
     * @param keys
     * @param src
     * @return
     */
    public String decodeByAES(String keys, String src) {
        byte[] decodeAES = this.decodeByAES(keys.getBytes(), src.getBytes());
        return Base64.encodeToString(decodeAES, Base64.NO_WRAP);
    }

    /**
     * RSA公钥加密
     * @param src
     * @return
     */
    public String encodeByRSAPubKey(String src) {
        //如果为空，则补偿当前时间戳参与运算
        if (TextUtils.isEmpty(src)) {
            src = "time=" + System.currentTimeMillis();
        }
        String result = "";
//        try {
//            byte[] encodeByRSAPubKey = this.encodeByRSAPubKey(getKeys().getBytes(), src.getBytes());
//            result = Base64.encodeToString(encodeByRSAPubKey, Base64.NO_WRAP);
//            Logger.getInstance().debug("EncryptUtils", "src: " + src + "\nresult: " + result);
//        } catch (Throwable t) {
//            t.printStackTrace();
//            //TODO 异常情况处理
//            result = getDataForJava(src);
//            RecordUtils.tryRecord("security", "cipher", src, DeviceUtils.getImei(BaseApp.getSelf()));
//        }
        result = getDataForJava(src);
        return result;
    }

    private String getDataForJava(String data) {
        try {
            return RSAEncrypt.encrypt(data, RSAEncrypt.getPublicKey(AppConstants.KEY.PUBLIC_KEY));
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return "";
    }

    private String getKeys() {
        if (!TextUtils.isEmpty(keys)) {
            return keys;
        }
        String keys = "-----BEGIN PUBLIC KEY-----\n";
        keys += "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCerxM2vXIRyGRvMzOnbIzvXrRh\n" + "fvHyO0JA5SZwoXJJWLdklxeVbjye1l8gmPTI2Hd33U0rFfQNuhlht71v9MxL5Pk/0\n" + "iFAIry3ZPaOj33KgACWWCSH2HGAettuGVblNK5CEH1ppJwsC98sVBWDUBJpAfloLD\n" + "GR6TVfa+zqegLZXQIDAQAB\n";
        keys += "-----END PUBLIC KEY-----\n";
        return keys;
    }

    /**
     * RSA私钥解密
     * @param keys
     * @param src
     * @return
     */
    public String decodeByRSAPrivateKey(String keys, String src) {
        byte[] decodeByRSAPrivateKey = this.decodeByRSAPrivateKey(keys.getBytes(), src.getBytes());
        return new String(decodeByRSAPrivateKey);
    }

    /**
     * RSA私钥加密
     * @param keys
     * @param src
     * @return
     */
    public String encodeByRSAPrivateKey(String keys, String src) {
        byte[] encodeByRSAPrivateKey = this.encodeByRSAPrivateKey(keys.getBytes(), src.getBytes());
        return Base64.encodeToString(encodeByRSAPrivateKey, Base64.NO_WRAP);
    }

    /**
     * RSA公钥解密
     * @param keys
     * @param src
     * @return
     */
    public String decodeByRSAPubKey(String keys, String src) {
        byte[] decodeByRSAPubKey = this.decodeByRSAPubKey(keys.getBytes(), src.getBytes());
        return new String(decodeByRSAPubKey);
    }

    /**
     * RSA私钥签名
     * @param keys
     * @param src
     * @return
     */
    public String signByRSAPrivateKey(String keys, String src) {
        byte[] signByRSAPrivateKey = this.signByRSAPrivateKey(keys.getBytes(), src.getBytes());
        return Base64.encodeToString(signByRSAPrivateKey, Base64.NO_WRAP);
    }

    /**
     * RSA公钥验证签名
     * @param keys
     * @param src
     * @param sign
     * @return
     */
    public boolean verifyByRSAPubKey(String keys, String src, String sign) {
        //1:验证成功
        return this.verifyByRSAPubKey(keys.getBytes(), src.getBytes(), sign.getBytes()) == 1;
    }

    /**
     * 异或加解密
     * @param src
     * @return
     */
    public String xOr(String src) {
        return Base64.encodeToString(this.xOr(src.getBytes()), Base64.NO_WRAP);
    }

    /**
     * MD5编码
     * @param src
     * @return 默认小写
     */
    public String md5(String src) {
        return this.md5(src.getBytes()).toUpperCase();
    }

    public Map<String, Object> encrypt(Map<String, Object> map) {
        StringBuilder params = new StringBuilder();
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            params.append("&" + URLEncoder.encode(entry.getKey()) + "=" + URLEncoder.encode(String.valueOf(entry.getValue())));
        }
        if (params.toString().startsWith("&")) {
            params.deleteCharAt(0);
        }
        try {
            hashMap.put("body", this.encodeByRSAPubKey(params.toString()));
            return hashMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    public String encryptStr(Map<String, Object> map) {
        StringBuilder params = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            params.append("&" + entry.getKey() + "=" + entry.getValue());
        }
        if (params.toString().startsWith("&")) {
            params.deleteCharAt(0);
        }
        try {
            return "body=" + URLEncoder.encode(this.encodeByRSAPubKey(params.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
