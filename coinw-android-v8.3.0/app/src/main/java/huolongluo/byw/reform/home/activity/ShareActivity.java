package huolongluo.byw.reform.home.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.legend.modular_contract_sdk.onekeyshare.CreateQRImage;
import com.legend.modular_contract_sdk.utils.ViewUtil;
import com.netease.nim.uikit.common.util.media.BitmapDecoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import huolongluo.byw.BuildConfig;
import huolongluo.byw.R;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.home.bean.SharePicBean;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.Util;
import huolongluo.byw.util.domain.DomainUtil;
import huolongluo.byw.util.tip.MToast;
import huolongluo.bywx.helper.AppHelper;
import okhttp3.Request;

//import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2019/3/19 0019.
 */
public class ShareActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton back_iv;
    private TextView title_tv;
    private TextView share_image;
    private TextView share_url;
    private ImageView background;
    // String url = "https://www.coinw.ai/mobile/download.html";
    private int shareType = 0;
    private String mAppDownloadUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        title_tv = findViewById(R.id.title_tv);
        share_image = findViewById(R.id.share_image);
        share_url = findViewById(R.id.share_url);
        title_tv.setText(R.string.cz22);
        back_iv = findViewById(R.id.back_iv);
        background = findViewById(R.id.background);
        share_image.setOnClickListener(this);
        share_url.setOnClickListener(this);
        // url=getIntent().getStringExtra("url");
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mAppDownloadUrl = SPUtils.getString(this, AppConstants.LOCAL.KEY_LOCAL_HOST_DOWNLOAD, BuildConfig.HOST_DOWNLOAD);
        sharePic();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_image:
                // showShare(Wechat.NAME);
                shareType = 0;
                showPop();
                break;
            case R.id.share_url:
                // showShare(Wechat.NAME);
                shareType = 1;
                showPop();
                break;
            case R.id.wechat_bn://????????????
                showShare(Wechat.NAME);
                AppHelper.dismissDialog(dialog);
                break;
            case R.id.friend_bn://
                showShare(WechatMoments.NAME);
                AppHelper.dismissDialog(dialog);
                break;
            case R.id.qq_bn://
                showShare(QQ.NAME);
                AppHelper.dismissDialog(dialog);
                break;
            case R.id.message_bn://????????????
               /* Uri smsToUri = Uri.parse("smsto:");
                Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                //"sms_body"???????????????smsbody?????????????????????content
                intent.putExtra("sms_body", "??????");
                startActivity(intent);*/
                showShare(ShortMessage.NAME);
                break;
            case R.id.savePng_bn://????????????
                if (saveImageToGallery(this, bitmap)) {
                    MToast.show(this, getString(R.string.cz23), 2);
                }
                AppHelper.dismissDialog(dialog);
                break;
            case R.id.more_bn://
                systemShare();
                AppHelper.dismissDialog(dialog);
                break;
            case R.id.tvCancel:
                AppHelper.dismissDialog(dialog);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        shareType = 0;
    }

    void sharePic() {
        Bitmap srcBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.backaaa);
        int qrcodeSize = (int) (srcBitmap.getWidth() * 0.16);

        Bitmap qrLogo = BitmapFactory.decodeResource(getResources(), R.drawable.mc_sdk_qr_logo);
        Bitmap qrcodeBitmap = CreateQRImage.createQRCodeWithLogo(mAppDownloadUrl, qrcodeSize, qrLogo, 0);

        int x = (int) (srcBitmap.getWidth() * 0.7653);
        int y = (int) (srcBitmap.getHeight() * 0.8182);
        bitmap = Util.addBitmap(srcBitmap, qrcodeBitmap, x, y);

        background.setImageBitmap(bitmap);
    }

    //???????????????????????????
    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        //// java.lang.NullPointerException: Attempt to invoke virtual method 'boolean android.graphics.Bitmap.compress(android.graphics.Bitmap$CompressFormat, int, java.io.OutputStream)' on a null object reference
        ////      at huolongluo.byw.reform.home.activity.ShareActivity.saveImageToGallery(ShareActivity.java:212)
        ////      at huolongluo.byw.reform.home.activity.ShareActivity.onClick(ShareActivity.java:130)
        if (bmp == null) {
            return false;
        }
        // ??????????????????
        //  String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dearxy";
        String storePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "Camera" + File.separator;
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //??????io?????????????????????????????????
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
            //??????????????????????????????
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            // MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, fileName, null);
            //????????????????????????????????????????????????
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            return isSuccess;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    void systemShare() {
        try {
            if (shareType == 0) {
                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(intent, DomainUtil.INSTANCE.getWebUrlSuffix()));
            } else {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/*");
                intent.putExtra(Intent.EXTRA_TEXT, mAppDownloadUrl);
                startActivity(Intent.createChooser(intent, "Coinw.mpwe"));
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private Dialog dialog;
    Bitmap bitmap;

    void showPop() {
        if (dialog != null) {
            dialog.show();
            return;
        }
        dialog = new Dialog(this, R.style.DialogTheme);
        View contentView = LayoutInflater.from(this).inflate(R.layout.share_pop, null, false);
        LinearLayout wechat_bn = contentView.findViewById(R.id.wechat_bn);
        LinearLayout friend_bn = contentView.findViewById(R.id.friend_bn);
        LinearLayout qq_bn = contentView.findViewById(R.id.qq_bn);
        LinearLayout message_bn = contentView.findViewById(R.id.message_bn);
        LinearLayout savePng_bn = contentView.findViewById(R.id.savePng_bn);
        LinearLayout more_bn = contentView.findViewById(R.id.more_bn);
        TextView tvCancel = contentView.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(this);
        wechat_bn.setOnClickListener(this);
        friend_bn.setOnClickListener(this);
        qq_bn.setOnClickListener(this);
        message_bn.setOnClickListener(this);
        savePng_bn.setOnClickListener(this);
        more_bn.setOnClickListener(this);
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    //String url="https://www.coinw.me/mobile/download.html";
    private void showShare(String platform) {
        try {
            final OnekeyShare oks = new OnekeyShare();
            //????????????????????????????????????????????????????????????????????????????????????
            if (platform != null) {
                oks.setPlatform(platform);
            }
            if (shareType == 0) {
                if (TextUtils.equals(platform, Wechat.NAME) || TextUtils.equals(platform, WechatMoments.NAME)) {
                    oks.setImageData(bitmap);
                } else {
                    String shareImgPath = Util.saveBitmap(this, bitmap);
                    oks.setImagePath(shareImgPath);
                }
                //????????????
                oks.show(this);
            } else {
                if (TextUtils.equals(platform, ShortMessage.NAME)) {
                    oks.setText(mAppDownloadUrl);
                    //????????????
                    oks.show(this);
                    return;
                }
                oks.disableSSOWhenAuthorize();
                // title???????????????????????????????????????????????????????????????QQ????????????
                oks.setTitle(getString(R.string.cz28));
                // titleUrl?????????????????????????????????Linked-in,QQ???QQ????????????
                oks.setTitleUrl(mAppDownloadUrl);
                // text???????????????????????????????????????????????????
                oks.setText(getString(R.string.cz29));
                //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                Bitmap Bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.share_logo);
                String path = saveBitmap(this, Bmp);
                oks.setImagePath(path);
                // imagePath???????????????????????????Linked-In?????????????????????????????????
                //oks.setImagePath("/sdcard/test.jpg");//??????SDcard????????????????????????
                // url???????????????????????????????????????????????????
                oks.setUrl(mAppDownloadUrl);
                // comment???????????????????????????????????????????????????QQ????????????
                oks.setComment(getString(R.string.cz30));
                // site??????????????????????????????????????????QQ????????????
                oks.setSite(getString(R.string.cz31));
                // siteUrl??????????????????????????????????????????QQ????????????
                oks.setSiteUrl(mAppDownloadUrl);
                //????????????
                oks.show(this);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
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

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
//??????sso??????
        //  oks.disableSSOWhenAuthorize();
        oks.setTitle(getString(R.string.cz33));
        oks.setTitleUrl("https://hyperpay.me/app_down");
        // oks.setImagePath(path);
// ????????????GUI
        oks.show(this);
// title???????????????????????????????????????????????????????????????QQ???????????????
        // oks.setTitle("??????");
// titleUrl???????????????????????????QQ???QQ???????????????
        //oks.setTitleUrl("http://sharesdk.cn");
// text???????????????????????????????????????????????????
        //  oks.setText("??????????????????");
// imagePath???????????????????????????Linked-In?????????????????????????????????
//oks.setImagePath("/sdcard/test.jpg");//??????SDcard????????????????????????
// url???????????????????????????????????????????????????
        //  oks.setUrl("http://sharesdk.cn");
// comment???????????????????????????????????????????????????QQ????????????
        //  oks.setComment("????????????????????????");
// site??????????????????????????????????????????QQ????????????
        //  oks.setSite(getString(R.string.app_name));
// siteUrl??????????????????????????????????????????QQ????????????
        // oks.setSiteUrl("http://sharesdk.cn");
    }
}
