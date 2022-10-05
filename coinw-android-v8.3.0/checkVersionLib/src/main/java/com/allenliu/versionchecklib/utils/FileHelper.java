package com.allenliu.versionchecklib.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import androidx.annotation.NonNull;

public class FileHelper {
    private static final String PATH="/Upgrade/";

    @Deprecated
    public static String getDownloadApkCachePath() {

        String appCachePath = null;


        if (checkSDCard()) {

            appCachePath = Environment.getExternalStorageDirectory() + PATH;
        } else {
            appCachePath = Environment.getDataDirectory().getPath() + PATH;
        }
        File file = new File(appCachePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return appCachePath;
    }

    public static String getDownloadApkCachePath(Context context) {
        String appCachePath;
        if (checkSDCard()) {
            appCachePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + PATH;

        } else {
            appCachePath = context.getFilesDir().getAbsolutePath() + PATH;

        }


        File file = new File(appCachePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return appCachePath;
    }


    /**
     *
     */
    private static boolean checkSDCard() {

        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

    }


    public static String dealDownloadPath(@NonNull String downloadAPKPath) {
        if (!downloadAPKPath.endsWith(File.separator)) {
            downloadAPKPath += File.separator;
        }
        return downloadAPKPath;

    }
}