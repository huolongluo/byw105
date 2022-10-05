package huolongluo.byw.util.sp;
import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Type;

import huolongluo.byw.util.GsonUtil;
//config由于数据过大会导致读取缓慢，新建一个类替换
public class SpUtils2 {

    private static String getSpName(){return "config2";}//存储简单基本数据
    private static String getSpNameObject(){return "configObject";}//存储对象数据

    private static SharedPreferences getSp(Context context){
       return context.getSharedPreferences(getSpName(), 0);
    }
    private static SharedPreferences getSpObject(Context context){
        return context.getSharedPreferences(getSpNameObject(), 0);
    }

    public static void saveObject(Context context,String key,Object obj,Class<?> c){
        SharedPreferences.Editor editor=getSpObject(context).edit();
        editor.putString(key, GsonUtil.obj2Json(obj,c));
        editor.commit();
    }
    public static Object getObject(Context context,String key,Class<?> c){
        String data=getSpObject(context).getString(key,"");
        return GsonUtil.json2Obj(data,c);
    }
    public static void saveObject(Context context, String key, Object obj, Type type){
        SharedPreferences.Editor editor=getSp(context).edit();
        editor.putString(key,GsonUtil.obj2Json(obj,type));
        editor.commit();
    }
    public static Object getObject(Context context,String key,Type type){
        String data=getSp(context).getString(key,"");
        return GsonUtil.json2Obj(data,type);
    }
    public static void saveString(Context context, String key, String value) {
        getSp(context).edit().putString(key, value).commit();
    }
    public static String getString(Context context, String key, String defValue) {
        return getSp(context).getString(key, defValue);
    }
    public static void saveBoolean(Context context, String key, boolean value) {
        getSp(context).edit().putBoolean(key, value).commit();
    }
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        return getSp(context).getBoolean(key, defValue);
    }
    public static void saveLong(Context context, String key, long value) {
        getSp(context).edit().putLong(key, value).commit();
    }
    public static long getLong(Context context, String key, long defValue) {
        return getSp(context).getLong(key, defValue);
    }
}
