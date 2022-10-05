package huolongluo.bywx.helper;
import java.io.BufferedReader;
import java.io.Reader;

import huolongluo.byw.BuildConfig;
import huolongluo.byw.log.Logger;
import huolongluo.bywx.utils.AppUtils;
public class TestHelper {
    private static final String TAG = "TestHelper";

    public static void debug(String url, Reader reader) {
        try {
            debugHttp(url, reader);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void debug(String url, String result) {
        try {
            debugHttp(url, result);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void debugHttp(String url, Reader reader) {
        //TODO 测试专用
        if (BuildConfig.DEBUG && BuildConfig.VERSION_CODE < 45) {
            //k线数据跟踪
            if (url.contains("exchangeKline.html")) {
                String result = AppUtils.getString(reader);
                Logger.getInstance().debug(TAG, "recv: " + result);
            }
        }
    }

    private static void debugHttp(String url, String result) {
        //TODO 测试专用
        if (BuildConfig.DEBUG && BuildConfig.VERSION_CODE < 45) {
            //k线数据跟踪
            if (url.contains("exchangeKline.html") || url.contains("klineApi")) {
                Logger.getInstance().debug(TAG, "recv: " + result);
            }
        }
    }

}
