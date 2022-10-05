package huolongluo.byw.util;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.android.legend.ui.bottomSheetDialogFragment.SeniorKycBottomDialogFragment;
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils;
import com.mob.tools.utils.ResHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.liuzhongjun.videorecorddemo.util.CameraUtils;
import com.liuzhongjun.videorecorddemo.util.OnErrorListener;
import java.util.Random;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.tip.MToast;
/**
 * Created by LS on 2018/7/14.
 */

public class Util {
    /**
     * 数据太长时需要缩小字号
     */
    public static void setLongStr(TextView tv){
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }
    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }



    public static int dp2px(Context ctx, float dp) {
        float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    public static int px2dp(Context ctx, float px) {
        float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (px/ scale + 0.5f);
    }
    public static int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }


  static   int[] rad=new int[]{R.drawable.otcname_bg,R.drawable.otcname_bg2,R.drawable.otcname_bg3,R.drawable.otcname_bg4};

    //获取随机数颜色值

    public static int getRadom(){


        Random random=new Random();
        return rad[random.nextInt(3)];
    }

    public static String versionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }
    public static String getStrByPrecision(String str){
        return getStrByPrecision(str,4);
    }

    /**
     * 根据传入的字符串为空或者0做精度处理
     * @param str
     * @param place
     * @return
     */
    public static String getStrByPrecision(String str,int place){
        if(TextUtils.isEmpty(str)||str.equals("0")){
            return NorUtils.NumberFormat(place).format(0.0000000000d);
        }
        return str;
    }

    /**
     * 统一处理停机维护的时候，当msg为需要屏蔽的文案则不提示
     * @param msg
     * @return
     */
    public static boolean isShowErrorMsg(String msg){
        if(CoinwHyUtils.isServiceStop&&(msg.equals(BaseApp.getSelf().getResources().getString(R.string.h4))||msg.equals("服务器异常，请稍后重试"))){//逗号有区别
            return false;
        }
        return true;
    }
    public static void gotoSeniorCertified(FragmentActivity activity, int nationality){
        if(nationality== AppConstants.COMMON.NATIONALITY_CHINA){
            CameraUtils.toHighReZheng(activity, new OnErrorListener() {
                @Override
                public void report(String msg, String route) {
                    Logger.getInstance().report(msg,route);
                }
            });
        }else{
            SeniorKycBottomDialogFragment.Companion.newInstance().show(activity.getSupportFragmentManager(),"Dialog");
        }

    }
    /**
     * 获取屏幕截图的bitmap
     * @param activity
     * @return
     */
    public static Bitmap getScreenShotBitmap(AppCompatActivity activity) {
        if (activity == null){
            return null;
        }
        View view = activity.getWindow().getDecorView();
        //允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        int navigationBarHeight = ScreenUtils.getNavigationBarHeight(view.getContext());


        //获取屏幕宽和高
        int width = ResHelper.getScreenWidth(view.getContext());
        int height = ResHelper.getScreenHeight(view.getContext());

        // 全屏不用考虑状态栏，有导航栏需要加上导航栏高度
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, width,
                    height + navigationBarHeight);
        } catch (Exception e) {
            // 这里主要是为了兼容异形屏做的处理，我这里的处理比较仓促，直接靠捕获异常处理
            // 其实vivo oppo等这些异形屏手机官网都有判断方法
            // 正确的做法应该是判断当前手机是否是异形屏，如果是就用下面的代码创建bitmap


            String msg = e.getMessage();
            // 部分手机导航栏高度不占窗口高度，不用添加，比如OppoR15这种异形屏
            if (msg.contains("<= bitmap.height()")){
                try {
                    bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, width,
                            height);
                } catch (Exception e1) {
                    msg = e1.getMessage();
                    // 适配Vivo X21异形屏，状态栏和导航栏都没有填充
                    if (msg.contains("<= bitmap.height()")) {
                        try {
                            bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, width,
                                    height - com.trycatch.mysnackbar.ScreenUtil.getStatusHeight(view.getContext()));
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }else {
                        e1.printStackTrace();
                    }
                }
            }else {
                e.printStackTrace();
            }
        }

        //销毁缓存信息
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    /**
     * 将两个bitmap上下拼接成一个bitmap返回
     * @param bitmapTop
     * @param bitmapBottom
     * @return
     */
    public static Bitmap addBitmap(Bitmap bitmapTop,Bitmap bitmapBottom){
        int width=bitmapTop.getWidth();
        int height=bitmapTop.getHeight()+bitmapBottom.getHeight();
        Bitmap bitmapResult=Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565);
        Canvas canvas=new Canvas(bitmapResult);
        canvas.drawBitmap(bitmapTop,0,0,null);
        canvas.drawBitmap(bitmapBottom,0,bitmapTop.getHeight(),null);
        return bitmapResult;
    }

    /**
     * 将一个Bitmap绘制在另一个Bitmap之上
     * @param srcBitmap 下面的Bitmap
     * @param targetBitmap 在srcBitmap之上绘制的Bitmap
     * @param x 绘制X坐标
     * @param y 绘制Y坐标
     * @return
     */
    public static Bitmap addBitmap(Bitmap srcBitmap, Bitmap targetBitmap, int x, int y){
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();

        int targetWidth = targetBitmap.getWidth();
        int targetHeight = targetBitmap.getHeight();
        // 被绘制的Bitmap不能大于原始Bitmap
        if (targetWidth > srcWidth || targetHeight > srcHeight){
            return null;
        }

        Bitmap bitmapResult=Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmapResult);
        canvas.drawBitmap(srcBitmap, 0, 0, null);
        canvas.drawBitmap(targetBitmap, x, y, null);
        return bitmapResult;
    }

    private static final String SD_PATH = "/sdcard/dskqxt/pic/";
    private static final String IN_PATH = "/dskqxt/pic/";
    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            savePath = SD_PATH;
        } else {
            savePath = context.getApplicationContext().getFilesDir().getAbsolutePath() + IN_PATH;
            //  savePath = Environment.getDataDirectory().getAbsolutePath() + IN_PATH;
        }
        try {
            filePic = new File(savePath + System.currentTimeMillis() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        //  return filePic.getAbsolutePath();
        return filePic.getAbsolutePath();
    }
    //保存图片到指定路径并在相册显示
    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "Camera" + File.separator;
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
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
            return isSuccess;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    //验证账户google和手机号是否至少绑定了一个，都未绑定返回false并给出提示语
    public static boolean verifyIsBindGoogleOrMobile(Context context){
        if(!UserInfoManager.getUserInfo().isGoogleBind()&&!UserInfoManager.getUserInfo().isBindMobil()){
            MToast.show(context,context.getString(R.string.bind_mobile_or_google));
            return false;
        }
        return true;
    }
}
