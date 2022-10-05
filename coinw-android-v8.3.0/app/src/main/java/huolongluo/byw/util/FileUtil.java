package huolongluo.byw.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;



import java.io.File;

public  class FileUtil {
    public static final String FILEPATH_AITALK="AitalkResource";
    public static final String FILEPATH_ITRANS="itrans";

    public static final String FILEPATH_TTS = "tts";
    /**
     * 外部存储的路径,可以存放不被清除缓存清除的数据 通过getOwnCacheDirectory获取
     */
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    /**
     * 获取数据库,文件下载,图片缓存存储路径
     *
     * @param context
     * @param filePath FILEPATH_DB 数据库路径,FILEPATH_DOWNLOAD 下载路径,FILEPATH_IMAGE_CACHE 图片路径
     * @return
     */
    public static File getFileDirectory(Context context, String filePath) {
        return getIndividualFilesDirectory(context, filePath);
    }

    private static File getIndividualCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = getCacheDirectory(context);
        File individualCacheDir = new File(appCacheDir, cacheDir);
        if ((!(individualCacheDir.exists())) && (!(individualCacheDir.mkdir()))) {
            individualCacheDir = appCacheDir;
        }
        return individualCacheDir;
    }

    private static File getCacheDirectory(Context context) {
        return getCacheDirectory(context, true);
    }

    private static File getIndividualFilesDirectory(Context context, String cacheDir) {
        File appCacheDir = getFilesDirectory(context);
        File individualCacheDir = new File(appCacheDir, cacheDir);
        if ((!(individualCacheDir.exists())) && (!(individualCacheDir.mkdir()))) {
            individualCacheDir = appCacheDir;
        }
        return individualCacheDir;
    }

    private static File getFilesDirectory(Context context) {
        return getFilesDirectory(context, true);
    }

    /**
     * 获取系统默认的Cache存储路径 如果有SDcard则放在
     * SD卡:Android/data/com.iflytek.itma.customer(包名)/cache/ 路径下
     * 如果没有SDcard则放在 系统 /data/data/com.iflytek.itma.customer(包名)/cache/ 路径下
     *
     * @param context
     * @param preferExternal true:优先放在外部SD卡中,没有则放在内部存储 false:直接放在内部存储中
     * @return
     */
    private static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) {
            externalStorageState = "";
        } catch (IncompatibleClassChangeError e) {
            externalStorageState = "";
        }
        if ((preferExternal) && ("mounted".equals(externalStorageState))
                && (hasExternalStoragePermission(context))) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName()
                    + "/cache/";
            Log.w("woang  ",
                    "Can't define system cache directory! '%s' will be used."+
                    cacheDirPath);
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    /**
     * 获取系统默认的Files存储路径 如果有SDcard则放在
     * SD卡:Android/data/com.iflytek.itma.customer(包名)/files/ 路径下
     * 如果没有SDcard则放在 系统 /data/data/com.iflytek.itma.customer(包名)/files/ 路径下
     *
     * @param context
     * @param preferExternal true:优先放在外部SD卡中,没有则放在内部存储 false:直接放在内部存储中
     * @return
     */
    private static File getFilesDirectory(Context context, boolean preferExternal) {
        File appFilesDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) {
            externalStorageState = "";
        } catch (IncompatibleClassChangeError e) {
            externalStorageState = "";
        }
        if ((preferExternal) && ("mounted".equals(externalStorageState))
                && (hasExternalStoragePermission(context))) {
            appFilesDir = getExternalFilesDir(context);
        }
        if (appFilesDir == null) {
            appFilesDir = context.getFilesDir();
        }
        if (appFilesDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName()
                    + "/files/";
            Log.w("aaa",
                    "Can't define system files directory! '%s' will be used."+
                    cacheDirPath);
            appFilesDir = new File(cacheDirPath);
        }
        return appFilesDir;

    }

    /**
     * 获取外部自定义存储路径 如果有SDcard则放在 SD卡/用户指定cacheDir/ 路径下 如果没有SDcard则放在 系统 /用户指定cacheDir/
     * 路径下
     *
     * @param context
     * @param cacheDir true:优先放在外部SD卡中,没有则放在内部存储 false:直接放在内部存储中
     * @return 创建完成之后的路径
     */
    public static File getOwnCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        if ("mounted".equals(Environment.getExternalStorageState())
                && (hasExternalStoragePermission(context))) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(),
                    cacheDir);
        }
        if ((appCacheDir == null)
                || ((!(appCacheDir.exists())) && (!(appCacheDir.mkdirs())))) {
            appCacheDir = new File(context.getCacheDir(), cacheDir);
        }
        if (!(appCacheDir.exists())) {
            if (!(appCacheDir.mkdirs())) {
                Log.w("aa","Unable to create external files directory");
                return null;
            }
        }
        return appCacheDir;
    }

    private static File getExternalFilesDir(Context context) {
        File appCacheDir = context.getExternalFilesDir(null);
        if (!(appCacheDir.exists())) {
            if (!(appCacheDir.mkdirs())) {
                Log.w("aa","Unable to create external files directory");
                return null;
            }
        }
        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        File appCacheDir = context.getExternalCacheDir();
        if (!(appCacheDir.exists())) {
            if (!(appCacheDir.mkdirs())) {
                Log.w("aa","Unable to create external cache directory");
                return null;
            }
        }
        return appCacheDir;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context
                .checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return (perm == 0);
    }

    /**
     * 获取系统SDCard 可用容量大小
     *
     * @param context
     */
    public static long getSDCardAvailableSize(Context context) {
        return getSDCardAvailableSize(context, true);
    }

    /**
     * 获取系统SDCard 容量大小
     *
     * @param context
     * @param isExternalStorage =true 外部存储 =false 内部存储
     */
    private static long getSDCardAvailableSize(Context context, boolean isExternalStorage) {
        // 得到文件系统的信息：存储块大小，总的存储块数量，可用存储块数量
        // 获取sd卡空间
        // 存储设备会被分为若干个区块
        // 每个区块的大小 * 区块总数 = 存储设备的总大小
        // 每个区块的大小 * 可用区块的数量 = 存储设备可用大小
        File path = null;
        if (isExternalStorage)
            path = Environment.getExternalStorageDirectory();
        else
            path = Environment.getDataDirectory();

        StatFs stat = new StatFs(path.getPath());
        long blockSize;
        long totalBlocks;
        long availableBlocks;
        // 由于API18（Android4.3）以后getBlockSize过时并且改为了getBlockSizeLong
        // 因此这里需要根据版本号来使用那一套API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
            availableBlocks = stat.getAvailableBlocks();
        }
        /*// 利用formatSize函数把字节转换为用户等看懂的大小数值单位
        String totalText = Formatter.formatFileSize(context,blockSize * totalBlocks);
        String availableText = Formatter.formatFileSize(context,blockSize * availableBlocks);
        LogUtils.i("SDCard总大小:\n" + totalText);
        LogUtils.i("SDCard可用空间大小:\n" + availableText);*/
        return blockSize * availableBlocks;
    }

    /**
     * aitalkResource资源是否存在
     * @return true/false
     */
    public static boolean aitalkResourceExist(){
//        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String rootPath = "/sdcard";
        File file = new File(rootPath+File.separator+FILEPATH_AITALK);
        boolean exist = file.exists();
        boolean isDir = file.isDirectory();
        return exist && isDir && file.list().length >0;
    }

    /**
     * itrans资源是否存在
     * @return true/false
     */
    public static boolean itransExist(){
//        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String rootPath = "/sdcard";
        File file = new File(rootPath+File.separator+FILEPATH_ITRANS);
        boolean exist = file.exists();
        boolean isDir = file.isDirectory();
        return exist && isDir && file.list().length >0;
    }

    /**
     * tts资源是否存在
     * @return true/false
     */
    public static boolean ttsExist(){
//        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String rootPath = "/sdcard";
        File file = new File(rootPath+File.separator+FILEPATH_TTS);
        boolean exist = file.exists();
        boolean isDir = file.isDirectory();
        return exist && isDir && file.list().length >0;
    }

}
