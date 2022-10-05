package huolongluo.byw.util;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Type;

import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.view.kline.MainIndex;
import huolongluo.byw.view.kline.SubIndex;
/**
 * Created by Administrator on 2016/3/11.
 */
public class SPUtils {
    public static final String SP_NAME = "config";
    private static SharedPreferences sp;
    public static final String FIST_OPEN_broker = "fist_open_broker";
    public static final String Trade_shendu = "Trade_shendu";
    public static final String Trade_dangwei = "Trade_dangwei";
    public static final String MARKET_1 = "market_1";
    public static final String MARKET_2 = "market_2";
    public static final String MARKET_3 = "market_3";
    private static final String fingerprint = "Fingerprint";
    public static final String IS_BIND_GOOGLE = "isBindGoogle";
    public static final String IS_BIND_PHONE = "isBindPhone";
    public static final String UID = "uId";
    public static final String USER_NAME = "userName";
    public static final String LOGINTOKEN = "loginToken";
    public static final String IS_PRO_HOME = "isProHome";
    public static final String KLINE_MAIN_INDEX = "kLineMainIndex";
    public static final String KLINE_SUB_INDEX = "kLineSubIndex";

    //保存token
    public static void saveLoginToken(String ltoken) {
        saveStringEn(BaseApp.getSelf(), LOGINTOKEN, ltoken);
    }

    //保存token
    public static String getLoginToken() {
        return getStringde(BaseApp.getSelf(), LOGINTOKEN, "");
    }

    public static void saveFingerprint(Context context, boolean status) {
        saveBoolean(context, fingerprint, status);
    }

    public static boolean getFingerprint(Context context, boolean status) {
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        boolean isExit = sp.contains(fingerprint);
        return getBoolean(context, fingerprint, status);
    }

    /**
     * 保存进入首页的信息
     * 退出登录后，不需要清除这个信息
     *
     * @param isProHome 是否为专业版首页，true为专业版，false为新手版
     */
    public static void saveProHome(Context context, boolean isProHome) {
        saveBoolean(context, IS_PRO_HOME, isProHome);
    }

    /**
     * 当前是否为专业版首页，true为专业版，false为新手版
     */
    public static boolean isProHome(Context context) {
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        return getBoolean(context, IS_PRO_HOME, false);
    }

    /**
     * 保存主图的指标
     */
    public static void saveKLineMainIndex(Context context, MainIndex mainIndex) {
        saveString(context, KLINE_MAIN_INDEX, mainIndex.getName());
    }

    /**
     * 获取保存的主图得信息
     * @param context
     * @return
     */
    public static MainIndex getKLineMainIndex(Context context) {
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        String mainIndexStr = getString(context, KLINE_MAIN_INDEX, MainIndex.MA.getName());
        MainIndex mainIndex;
        if (TextUtils.equals(mainIndexStr, MainIndex.MA.getName())) {
            mainIndex = MainIndex.MA;
        } else if (TextUtils.equals(mainIndexStr, MainIndex.BOLL.getName())) {
            mainIndex = MainIndex.BOLL;
        } else if (TextUtils.equals(mainIndexStr, MainIndex.EMA.getName())) {
            mainIndex = MainIndex.EMA;
        } else if (TextUtils.equals(mainIndexStr, MainIndex.SAR.getName())) {
            mainIndex = MainIndex.SAR;
        } else if (TextUtils.equals(mainIndexStr, MainIndex.NONE.getName())) {
            mainIndex = MainIndex.NONE;
        } else { // 默认为MA
            mainIndex = MainIndex.MA;
        }
        return mainIndex;
    }

    /**
     * 保存副图的指标
     */
    public static void saveKLineSubIndex(Context context, SubIndex subIndex) {
        saveString(context, KLINE_SUB_INDEX, subIndex.getName());
    }

    /**
     * 获取保存的副图指标
     */
    public static SubIndex getKLineSubIndex(Context context) {
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        String subIndexStr = getString(context, KLINE_SUB_INDEX, SubIndex.NONE.getName());
        SubIndex subIndex;
        if (TextUtils.equals(subIndexStr, SubIndex.MACD.getName())) {
            subIndex = SubIndex.MACD;
        } else if (TextUtils.equals(subIndexStr, SubIndex.KDJ.getName())) {
            subIndex = SubIndex.KDJ;
        } else if (TextUtils.equals(subIndexStr, SubIndex.RSI.getName())) {
            subIndex = SubIndex.RSI;
        } else if (TextUtils.equals(subIndexStr, SubIndex.OBV.getName())) {
            subIndex = SubIndex.OBV;
        } else if (TextUtils.equals(subIndexStr, SubIndex.WR.getName())) {
            subIndex = SubIndex.WR;
        } else if (TextUtils.equals(subIndexStr, SubIndex.NONE.getName())) {
            subIndex = SubIndex.NONE;
        } else { // 默认为None
            subIndex = SubIndex.NONE;
        }
        return subIndex;
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
        String txt1 = getString(context, MARKET_1, null);
        String txt2 = getString(context, MARKET_2, null);
        String txt3 = getString(context, MARKET_3, null);
        if(android.text.TextUtils.equals(text,txt1)){
            return;
        }
        if(android.text.TextUtils.equals(text,txt2)){
            return;
        }
        if(android.text.TextUtils.equals(text,txt3)){
            return;
        }
        if (android.text.TextUtils.isEmpty(getString(context, MARKET_1, null))) {
            saveString(context, MARKET_1, text);
        } else if (android.text.TextUtils.isEmpty(getString(context, MARKET_2, null))) {
            if (!TextUtils.equals(text, SPUtils.getString(context, SPUtils.MARKET_1, null))) {
                saveString(context, MARKET_2, text);
            }
        } else if (android.text.TextUtils.isEmpty(getString(context, MARKET_3, null))) {
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
     * @param context
     * @param key
     * @param value
     */
    public static void saveString(Context context, String key, String value) {
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        // value=    MD5Tool.getDefault().encode(sp.getString(key, value));;
        sp.edit().putString(key, value).commit();
    }

    public static void saveStringEn(Context context, String key, String value) {
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        value = MD5Tool.getDefault().encode(value);
        sp.edit().putString(key, value).commit();
    }

    public static void saveDiffString(Context context, String value) {
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
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
    public static void saveObject(Context context,String key,Object obj,Class<?> c){
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(key,GsonUtil.obj2Json(obj,c));
        editor.commit();
    }
    public static Object getObject(Context context,String key,Class<?> c){
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        String data=sp.getString(key,"");
        return GsonUtil.json2Obj(data,c);
    }
    public static void saveObject(Context context, String key, Object obj, Type type){
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(key,GsonUtil.obj2Json(obj,type));
        editor.commit();
    }
    public static Object getObject(Context context,String key,Type type){
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        String data=sp.getString(key,"");
        return GsonUtil.json2Obj(data,type);
    }

    public static String getDiffString(Context context) {
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getString("notice", null);
    }
    public static String getMail(Context context) {
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getString("mail"+ UserInfoManager.getUserInfo().getFid(), null);
    }

    /**
     * 返回字符串
     * @param context 上下文
     * @param key key
     * @param defValue 默认值
     * @return
     */
    public static String getString(Context context, String key, String defValue) {
        if (context == null) {
            context = BaseApp.getSelf().getApplicationContext();
        }
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getString(key, defValue);
    }

    public static String getStringde(Context context, String key, String defValue) {
        if (context == null) {
            context = BaseApp.getSelf().getApplicationContext();
        }
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        String value = MD5Tool.getDefault().decode(sp.getString(key, defValue));
        if (TextUtils.isEmpty(value)) {
            return "";
        }
        return value;
        //return sp.getString(key, defValue);
    }

    /**
     * 保存布尔
     * @param context
     * @param key
     * @param value
     */
    public static void saveBoolean(Context context, String key, boolean value) {
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        sp.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getBoolean(key, defValue);
    }

    public static void saveInt(Context context, String key, int value) {
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        sp.edit().putInt(key, value).commit();
    }

    public static int getInt(Context context, String key, int defValue) {
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getInt(key, defValue);
    }

    public static long getLong(Context context, String key, long defValue) {
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getLong(key, defValue);
    }

    public static void saveLong(Context context, String key, long value) {
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        sp.edit().putLong(key, value).commit();
    }
    public static void putLong(Context context, String key, long value) {
        if (sp == null) sp = context.getSharedPreferences(SP_NAME, 0);
        sp.edit().putLong(key, value).commit();
    }

    public static void reload(Context context) {
        sp = context.getSharedPreferences(SP_NAME, 0);
    }
}
