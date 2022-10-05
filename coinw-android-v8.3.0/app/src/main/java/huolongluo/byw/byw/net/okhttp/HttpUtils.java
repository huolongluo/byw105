package huolongluo.byw.byw.net.okhttp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.L;

/**
 * HttpUtils
 * <p>
 * Created by SLAN on 2016/6/27.
 */
public class HttpUtils {

    /**
     * @param context ApplicationContext
     * @return true:有网络连接
     */
    public static boolean isNetworkConnected(Context context) {
        try {
            //在某种极限情况下，会抛异常
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            return info != null;// 网络是否连接
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    /**
     * 检查当前网络是否可用
     *
     * @param context ApplicationContext
     * @return true:连接
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
//        Logger.getInstance().debug("HttpUtils", "isNetworkAvailable", new Exception());
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] ni = connectivityManager.getAllNetworkInfo();
            if (ni != null && ni.length > 0) {
                for (int i = 0; i < ni.length; i++) {
                    Logger.getInstance().debug("HttpUtils", "状态:" + ni[i].getState() + " 类型:" + ni[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (ni[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断是否为wifi联网
     */
    public static boolean isWiFi(Context cxt) {
        ConnectivityManager cm = (ConnectivityManager) cxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        // wifi的状态：ConnectivityManager.TYPE_WIFI
        // 3G的状态：ConnectivityManager.TYPE_MOBILE
        NetworkInfo.State state = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        return NetworkInfo.State.CONNECTED == state;
    }
}
