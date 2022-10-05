package huolongluo.byw.util;

import com.google.gson.Gson;

import java.io.Reader;

import okhttp3.internal.Util;

/**
 * Gson工具类
 *
 * @author guocj
 * @date 2015-10-10下午2:58:03
 * @Copyright(c)
 */
public class GsonUtil {

    private static Gson gson = new Gson();

    /**
     * new Gson().fromJson的包装方法,处理了异常
     *
     * @param json 字符串
     * @param cls  需要转换的类
     * @return
     */
    public static <T> T json2Obj(String json, Class<T> cls) {
        try {
            return gson.fromJson(json, cls);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过type把json字符串转换成对象
     *
     * @param json 字符串
     * @param type 需要反射的类型
     * @return
     */
    public static <T> T json2Obj(String json, java.lang.reflect.Type type) {
        try {
            return gson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 对象转换成json字符串
     *
     * @param obj 实体类对象
     * @param cls 类对象
     * @return
     */
    public static String obj2Json(Object obj, Class cls) {
        try {
            return gson.toJson(obj, cls);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 通过type把对象转换成json字符串
     *
     * @param obj  实体类对象
     * @param type 反射的类型
     * @return
     */
    public static String obj2Json(Object obj, java.lang.reflect.Type type) {
        try {
            return gson.toJson(obj, type);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static <T> T json2Obj(Reader reader, java.lang.reflect.Type type) {
        try {
            return gson.fromJson(reader, type);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
//        finally {
//            if (reader != null) {
//                try {
////                    Util.closeQuietly(source)
//                    Util.closeQuietly(reader);
////                    reader.close();
//                } catch (Throwable t) {
//                    t.printStackTrace();
//                }
//            }
//        }
    }
}
