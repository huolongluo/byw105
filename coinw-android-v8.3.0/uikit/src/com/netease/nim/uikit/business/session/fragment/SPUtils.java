package com.netease.nim.uikit.business.session.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;


/**
 * Created by Administrator on 2016/3/11.
 */
public class SPUtils {

    public static final String SP_NAME = "config";
    private static SharedPreferences sp;


    public static final String FIST_OPEN_broker = "fist_open_broker";
    public static final String FIST_OPEN_C2C = "fist_open_c2c";

    public static final String Trade_shendu = "Trade_shendu";
    public static final String Trade_dangwei = "Trade_dangwei";


    public static final String MARKET_1 = "market_1";
    public static final String MARKET_2 = "market_2";
    public static final String MARKET_3 = "market_3";

    private static final String fingerprint = "Fingerprint";


    public static final String IS_BIND_GOOGLE = "isBindGoogle";
    public static final String IS_BIND_PHONE = "isBindPhone";
    public static final String UID = "uId";
    ;
    public static final String USER_NAME = "userName";
    ;

    public static final String LOGINTOKEN = "loginToken";
    ;;


    public static void saveFingerprint(Context context, boolean status) {
        saveBoolean(context, fingerprint, status);
    }


    public static boolean getFingerprint(Context context, boolean status) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);

        boolean isExit = sp.contains(fingerprint);

        return getBoolean(context, fingerprint, status);
    }


    public static void delete(Context context) {
        saveString(context, MARKET_1, "");
        saveString(context, MARKET_2, "");
        saveString(context, MARKET_3, "");
    }

    public static void delete1(Context context) {
        saveString(context, MARKET_1, "");
    }

    public static void delete2(Context context) {
        saveString(context, MARKET_2, "");
    }

    public static void delete3(Context context) {
        saveString(context, MARKET_3, "");
    }

    public static void save(Context context, String text) {

        if (TextUtils.isEmpty(getString(context, MARKET_1, null))) {
            saveString(context, MARKET_1, text);
        } else if (TextUtils.isEmpty(getString(context, MARKET_2, null))) {
            if (!TextUtils.equals(text, SPUtils.getString(context, SPUtils.MARKET_1, null))) {
                saveString(context, MARKET_2, text);
            }

        } else if (TextUtils.isEmpty(getString(context, MARKET_3, null))) {
            if (!TextUtils.equals(text, SPUtils.getString(context, SPUtils.MARKET_1, null)) && !TextUtils.equals(text, SPUtils.getString(context, SPUtils.MARKET_2, null))) {
                saveString(context, MARKET_3, text);
            }

        } else {

            String t1 = getString(context, MARKET_1, null);
            String t2 = getString(context, MARKET_2, null);

            saveString(context, MARKET_3, t2);
            saveString(context, MARKET_2, t1);

            saveString(context, MARKET_1, text);
        }

    }


    /**
     * 保存字符串
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveString(Context context, String key, String value) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);


        // value=    MD5Tool.getDefault().encode(sp.getString(key, value));;
        sp.edit().putString(key, value).commit();
    }

    public static void saveDiffString(Context context, String value) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        String s = sp.getString("notice", null);
        if (s != null) {

            if (!s.contains("*" + value + "*")) {
                StringBuilder builder = new StringBuilder(s);
                builder.append(value);
                sp.edit().putString("notice", "*" + builder.toString() + "*").commit();
                //   SPUtils.saveString(NoticeDetailActivity.this,"notice","*"+builder.toString()+"*");
            }
        } else {
            sp.edit().putString("notice", "*" + value + "*").commit();
            //SPUtils.saveString(NoticeDetailActivity.this,"notice","*"+nextBean.getFid()+"*");
        }

    }

    public static String getDiffString(Context context) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getString("notice", null);
    }

    /**
     * 返回字符串
     *
     * @param context  上下文
     * @param key      key
     * @param defValue 默认值
     * @return
     */

    public static String getString(Context context, String key, String defValue) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getString(key, defValue);
    }

    /**
     * 保存布尔
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveBoolean(Context context, String key, boolean value) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        sp.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key,
                                     boolean defValue) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getBoolean(key, defValue);
    }


    public static void saveInt(Context context, String key, int value) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        sp.edit().putInt(key, value).commit();
    }

    public static int getInt(Context context, String key, int defValue) {
        if (sp == null)
            sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getInt(key, defValue);
    }


}
