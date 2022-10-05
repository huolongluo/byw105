package huolongluo.byw.log;
import android.text.TextUtils;
import android.util.Log;

import com.android.coinw.DispatchQueue;
import com.android.coinw.time.FastDateFormat;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;

import huolongluo.byw.BuildConfig;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.MathHelper;

import static android.os.Looper.getMainLooper;
public class Logger {
    private static final String TAG = "[coinw]";
    private final String TAG_PREFIX = "coinw";
    public static boolean DEBUG = true;
    static Logger instance;
    //
    private OutputStreamWriter streamWriter = null;
    private FastDateFormat dateFormat = null;
    private DispatchQueue logQueue = null;
    private File currentFile = null;
    private File networkFile = null;
    private boolean initied;

    private Logger() {
        //        if (!BuildVars.LOGS_ENABLED) {
//            return;
//        }
//        init();
    }

    public static Logger getInstance() {
//        Logger localInstance = instance;
//        if (localInstance == null) {
//            synchronized (Logger.class) {
//                localInstance = instance;
//                if (localInstance == null) {
//                    instance = localInstance = new Logger();
//                }
//            }
//        }
//        04-01 00:51:33.337 E/AndroidRuntime(14340): Caused by: java.lang.NullPointerException: throw with null exception
//        04-01 00:51:33.337 E/AndroidRuntime(14340):     at g.a.h.a.a(Logger.java:11)
//        04-01 00:51:33.337 E/AndroidRuntime(14340):     at g.a.h.a.a(Logger.java:8)
//        04-01 00:51:33.337 E/AndroidRuntime(14340):     at huolongluo.byw.reform.login_regist.LoginVerifyActivity$d.a(LoginVerifyActivity.java:7)
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void init() {
        if (initied) {
            return;
        }
        dateFormat = FastDateFormat.getInstance("dd_MM_yyyy_HH_mm_ss", Locale.US);
        try {
            File sdCard = BaseApp.applicationContext.getExternalFilesDir(null);
            if (sdCard == null) {
                return;
            }
            File dir = new File(sdCard.getAbsolutePath() + "/logs");
            dir.mkdirs();
            currentFile = new File(dir, dateFormat.format(System.currentTimeMillis()) + ".txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            logQueue = new DispatchQueue("logQueue");
            currentFile.createNewFile();
            FileOutputStream stream = new FileOutputStream(currentFile);
            streamWriter = new OutputStreamWriter(stream);
            streamWriter.write("-----start log " + dateFormat.format(System.currentTimeMillis()) + "-----\n");
            streamWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initied = true;
    }

    public static void ensureInitied() {
        getInstance().init();
    }

    public static String getNetworkLogPath() {
//        if (!BuildVars.LOGS_ENABLED) {
//            return "";
//        }
        try {
            File sdCard = BaseApp.applicationContext.getExternalFilesDir(null);
            if (sdCard == null) {
                return "";
            }
            File dir = new File(sdCard.getAbsolutePath() + "/logs");
            dir.mkdirs();
            getInstance().networkFile = new File(dir, getInstance().dateFormat.format(System.currentTimeMillis()) + "_net.txt");
            return getInstance().networkFile.getAbsolutePath();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    public void info(String msg) {
        this.info(TAG, msg);
    }

    public void info(String tag, String msg) {
        this.info(tag, msg, null);
    }

    public void info(String msg, Throwable e) {
        this.info(TAG, msg, e);
    }

    public void info(String tag, String msg, Throwable e) {
        // want to do.
        tag = getTag(tag);
        if (e == null) {
            Log.i(tag, msg);
        } else {
            Log.i(tag, msg, e);
        }
        //日志
        record(tag, msg);
    }

    public void error(String msg) {
        this.error(TAG, msg);
    }

    public void error(Throwable e) {
        if (e == null) {
            this.error(TAG, new Exception());
            return;
        }
        this.error(e.getMessage());
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
    }

    public void errorLog(Throwable t) {
        error(t);
        //
    }

    public void error(String tag, String msg) {
        this.error(tag, msg, null);
    }

    public void error(String msg, Throwable e) {
        this.error(TAG, msg, null);
    }

    public void error(String tag, String msg, Throwable e) {
        if (!DEBUG) {
            return;
        }
        //Caused by: java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String java.lang.Throwable.getMessage()' on a null object reference
        //    at g.a.g.a.b(Logger.java:9)
        //    at g.a.g.a.c(Logger.java:5)
        //    at g.a.g.a.b(Logger.java:1)
        //    at g.a.g.a.a(Logger.java:5)
        //    at g.a.j.d.I.a(LoginVerifyActivity.java:3)
        final String t = getTag(tag);
        if (!TextUtils.isEmpty(msg)) {
            if (e == null) {
                Log.e(t, msg);
            } else {
                Log.e(t, msg, e);
            }
        } else if (e != null) {
            Log.e(t, e.getMessage(), e);
        } else {
            Log.e(t, "msg:" + msg);
        }
//        //record
//        ensureInitied();
//        if (getInstance().streamWriter == null) {
//            return;
//        }
//        getInstance().logQueue.postRunnable(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " E/" + t + ":" + e + "\n");
//                    getInstance().streamWriter.write(e.toString());
//                    getInstance().streamWriter.flush();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    public void debug(String msg) {
        this.debug(TAG, msg);
    }

    public void debug(String tag, String msg) {
        this.debug(tag, msg, null);
    }

    public void debug(String msg, Throwable e) {
        this.debug(TAG, msg, e);
    }

    public void debug(String tag, String msg, Throwable e) {
        if (!BuildConfig.ENV_DEV) {
            return;
        }
        final String t = getTag(tag);
        String[] strs = getPartLogTxt(msg);
        if (strs == null) {
            return;
        }
        for (int i = 0; i < strs.length; i++) {
            if (e == null) {
                Log.d(t, strs[i]);
            } else {
                Log.d(t, strs[i], e);
            }
        }
//        //record
//        ensureInitied();
//        //add by guocj
//        if (!TextUtils.isEmpty(msg) && msg.indexOf("proximity") != -1) {
//            //TODO
//        } else {
//            Log.d(tag, msg);
//        }
//        if (getInstance().streamWriter == null) {
//            return;
//        }
//        getInstance().logQueue.postRunnable(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    getInstance().streamWriter.write(getInstance().dateFormat.format(System.currentTimeMillis()) + " D/ " + t + ":" + msg + "\n");
//                    getInstance().streamWriter.flush();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    //将日志分段显示完全，因为logcat每行只显示4*1024个字符长度
    private String[] getPartLogTxt(String txt) {
        if (TextUtils.isEmpty(txt)) {
            return null;
        }
        int num = (int) Math.ceil(MathHelper.div(txt.length() + "", "4000"));
        String[] strs = new String[num];
        for (int i = 0; i < num; i++) {
            int start = i * 4000;
            int end = i == num - 1 ? txt.length() : (i + 1) * 4000;
            strs[i] = txt.substring(start, end);
        }
        return strs;
    }

    public void debugThreadLog(String tag, String subTag) {
        try {
            //TODO 线程跟踪验证日志
            String logMsg = String.format(subTag + " (pid-mtid-ctid:%s-%s-%s)", android.os.Process.myPid(), getMainLooper().getThread().getId(), Thread.currentThread().getId());
            Logger.getInstance().debug("thread_" + tag, logMsg);
        } catch (Throwable t) {
        }
    }

    private String getTag(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return TAG;
        } else if (tag.indexOf("[") == -1) {
            tag = "[" + TAG_PREFIX + "_" + tag + "]";
        } else {
            tag = "" + TAG + "";
        }
        return tag;
    }

    private void record(String tag, String msg) {
        //日志
        //TODO
    }

    public void report(String msg) {
        try {
            MobclickAgent.reportError(BaseApp.getSelf(), msg);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    /**
     * 错误上报格式：  msg_UID_错误路径   例：  高级认证视频录制异常_UID400371_路径CustomRecordActivity-OnErrorListener-onError
     * @param msg 描述信息
     * @param route 发送错误的路径
     */
    public void report(String msg,String route) {
        try {
            String report=msg+"_"+"UID"+UserInfoManager.getUserInfo().getFid()+"_"+"路径"+route;
            Logger.getInstance().debug(TAG,"report:"+report);
            MobclickAgent.reportError(BaseApp.getSelf(), report);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    public void report(Throwable t) {
        try {
            MobclickAgent.reportError(BaseApp.getSelf(), t);
        } catch (Throwable e) {
            Logger.getInstance().error(e);
        }
    }
}
