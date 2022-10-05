package huolongluo.bywx.utils;

import android.os.Build;
import android.text.TextUtils;

import com.android.coinw.utils.Utilities;

import huolongluo.byw.byw.bean.KChartBean;
import huolongluo.byw.util.GsonUtil;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import huolongluo.byw.byw.bean.KChartBean;
import huolongluo.byw.util.GsonUtil;

public class RecordUtils {

    public static void tryRecord(String rootName, String subName, String msg, String imei) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                record(rootName, subName, msg, imei);
            }
        });
    }

    private static void record(String rootName, String subName, String msg, String imei) {
        if (TextUtils.isEmpty(rootName) || TextUtils.isEmpty(msg)) {
            return;
        }
        try {
            String rootPath = "/sdcard/coinw/" + rootName + "/";
            File rootFile = new File(rootPath);
            if (!rootFile.exists()) {
                rootFile.mkdirs();
            }
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String date = formatter.format(new Date());
            String name = rootPath + rootName + "_" + Build.MODEL.replace(" ", "_").trim() + "_" + imei + "_" + date + subName + ".log";
            //控制文件大小
            File logFile = new File(name);
            //当日志文件大于100M，则清除前50M日志内容
            if (logFile.length() >= 1024 * 1024 * 100) {
                FileUtils.deleteContent(name, 1024 * 1024 * 50);
            }
            FileUtils.writeFile(msg + "\n", name, true);
            //清除一周前的记录数据
            String[] fs = rootFile.list();
            String lastWeek = rootName + "_" + Build.MODEL.replace(" ", "_").trim() + "_" + imei + "_" + getLastWeek(formatter) + subName + ".log";
            if (fs == null && fs.length <= 0) {
                return;
            }
            try {
                for (String f : fs) {
                    if (lastWeek.compareTo(f) < 0) {
                        continue;
                    }
                    File file = new File(f);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void tryRecord(String rootName, String msg, String imei) {
        tryRecord(rootName, "", msg, imei);
    }

    public static void tryRecordForThread(final String rootName, final String subName, final String headName, final KChartBean chart, final String imei) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String json = GsonUtil.obj2Json(chart, KChartBean.class);
                tryRecord(rootName, subName, headName + "_" + json, imei);
            }
        }.start();
    }

    private static String getLastWeek(DateFormat formatter) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -7);
        Date d = c.getTime();
        return formatter.format(d);
    }

    public static void tryUploadFile(String fileName) {
        //需要后台文件上传接口支持
    }

    public static void tryUploadRecord(String rootName) {
        //需要后台接口支持
    }
}