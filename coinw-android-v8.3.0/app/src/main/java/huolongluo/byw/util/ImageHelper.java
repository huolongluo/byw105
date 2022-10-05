package huolongluo.byw.util;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

import huolongluo.byw.log.Logger;
public class ImageHelper {
    public static Bitmap getCacheBitmapFromView(View view) {
        boolean drawingCacheEnabled = true;
        view.setDrawingCacheEnabled(drawingCacheEnabled);
        view.buildDrawingCache(drawingCacheEnabled);
        Bitmap drawingCache = view.getDrawingCache();
        android.graphics.Bitmap bitmap;
        if (drawingCache != null) {
            bitmap = Bitmap.createBitmap(drawingCache);
            view.setDrawingCacheEnabled(false);
        } else {
            bitmap = null;
        }
        return bitmap;
    }

    public static Bitmap createViewBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    public static void recycle(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        try {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = null;
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    //保存文件到指定路径
    public static String saveImageToGallery(Context context, Bitmap bmp) {
        //// java.lang.NullPointerException: Attempt to invoke virtual method 'boolean android.graphics.Bitmap.compress(android.graphics.Bitmap$CompressFormat, int, java.io.OutputStream)' on a null object reference
        ////      at huolongluo.byw.reform.home.activity.ShareActivity.saveImageToGallery(ShareActivity.java:212)
        ////      at huolongluo.byw.reform.home.activity.ShareActivity.onClick(ShareActivity.java:130)
        if (bmp == null) {
            return "";
        }
        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "Camera" + File.separator;
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        String filePath = file.getPath();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
            //把文件插入到系统图库
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            // MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, fileName, null);
            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                return filePath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
