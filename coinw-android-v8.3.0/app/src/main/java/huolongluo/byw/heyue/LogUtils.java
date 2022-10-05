package huolongluo.byw.heyue;

import android.util.Log;

import huolongluo.byw.BuildConfig;

/**
 * 日志工具类
 */

public class LogUtils {

    private static volatile LogUtils logUtil;//日志工具类
    private String className;//类名
    private String methodName;//方法名
    private int lineNumber;//行数
    public String TAG = "coinw_hy_log";
    public boolean isDebug = BuildConfig.DEBUG;

    public static LogUtils getInstance() {
        if (null == logUtil) {
            synchronized (LogUtils.class) {
                if (null == logUtil) {
                    logUtil = new LogUtils();
                }
            }
        }
        return logUtil;
    }

    public void init(String tag, boolean isDebug) {
        this.isDebug = isDebug;
        this.TAG = tag;
    }

    public void getMethodNames(StackTraceElement[] sELement) {
        className = sELement[1].getFileName();
        methodName = sELement[1].getMethodName();
        lineNumber = sELement[1].getLineNumber();
    }

    public boolean isDebuggable() {
        return isDebug;
    }

    public String createLog(String message) {
        String buffer = methodName +
                "(" + className + ":" + lineNumber + ")" +
                message;
        return buffer;
    }

    public void e(String message) {
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.e(TAG, createLog(message));
    }


    public void i(String message) {
        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.i(TAG, createLog(message));
    }

    public void d(String message) {
        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.d(TAG, createLog(message));
    }

    public void v(String message) {
        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.v(TAG, createLog(message));
    }

    public void w(String message) {
        if (!isDebuggable())
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.w(TAG, createLog(message));
    }

    public void wtf(String message) {
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.wtf(TAG, createLog(message));
    }

}
