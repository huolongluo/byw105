package huolongluo.byw.reform.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import huolongluo.byw.R;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.domain.DomainUtil;
import huolongluo.byw.util.tip.MToast;

public class ShareDialog extends Dialog implements View.OnClickListener {


    private static final String SD_PATH = "/sdcard/dskqxt/pic/";
    private static final String IN_PATH = "/dskqxt/pic/";

    private View share_pc;
    private String link;

    private Context context;

    public ShareDialog(@NonNull Context context, View share_pc, String link) {
        super(context, R.style.DialogTheme);
        this.context = context;
        this.share_pc = share_pc;
        this.link = link;
        View contentView = LayoutInflater.from(context).inflate(R.layout.share_pop, null, false);
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
        setContentView(contentView);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    //???????????????????????????
    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
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
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
            //??????????????????????????????
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            // MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, fileName, null);
            //????????????????????????????????????????????????
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            return isSuccess;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wechat_bn://????????????
                showShare(Wechat.NAME);
                dismiss();
                break;
            case R.id.friend_bn://
                showShare(WechatMoments.NAME);
                dismiss();
                break;
            case R.id.qq_bn://
                showShare(QQ.NAME);
                dismiss();
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
                Bitmap bitmap = createViewBitmap(share_pc);
                if (bitmap != null && saveImageToGallery(getContext(), bitmap)) {
                    MToast.show(getContext(), getContext().getString(R.string.d5), 2);
                } else {
                    MToast.show(getContext(), getContext().getString(R.string.d6), 2);
                   /* new Thread(new Runnable() {
                        @Override
                        public void run() {
                            bitmap = getBitMBitmap("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");

                        }
                    }).start();*/
                }
                dismiss();
                break;
            case R.id.more_bn://
                systemShare();
                dismiss();
                break;
            case R.id.tvCancel://
                dismiss();
                break;
        }
    }

    int shareType = 0;


    private void showShare(String platform) {
        final OnekeyShare oks = new OnekeyShare();
        //????????????????????????????????????????????????????????????????????????????????????
        if (platform != null) {
            oks.setPlatform(platform);
        }
        if (shareType == 0) {
            if (TextUtils.equals(platform, Wechat.NAME) || TextUtils.equals(platform, WechatMoments.NAME)) {
                Bitmap bitmap = createViewBitmap(share_pc);
                if (bitmap != null) {
                    oks.setImageData(bitmap);
                }
            } else {
                Bitmap bitmap = createViewBitmap(share_pc);
                if (bitmap != null) {
                    String path = saveBitmap(getContext(), bitmap);
                    oks.setImagePath(path);
                }
            }
            //????????????
            oks.show(getContext());

        } else {
            if (TextUtils.equals(platform, ShortMessage.NAME)) {
                oks.setText(link);
                //????????????
                oks.show(getContext());
                return;
            }
            oks.disableSSOWhenAuthorize();
            // title???????????????????????????????????????????????????????????????QQ????????????
            oks.setTitle(getContext().getString(R.string.d7));
            // titleUrl?????????????????????????????????Linked-in,QQ???QQ????????????
            oks.setTitleUrl(link);
            // text???????????????????????????????????????????????????
            oks.setText(getContext().getString(R.string.d8));
            //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            //  oks.setImageUrl(picBean.getAppSharePic());
            Bitmap Bmp = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.share_logo);
            String path = saveBitmap(getContext(), Bmp);
            oks.setImagePath(path);
            // imagePath???????????????????????????Linked-In?????????????????????????????????
            //oks.setImagePath("/sdcard/test.jpg");//??????SDcard????????????????????????
            // url???????????????????????????????????????????????????
            oks.setUrl(link);
            // comment???????????????????????????????????????????????????QQ????????????
            oks.setComment(getContext().getString(R.string.e2));
            // site??????????????????????????????????????????QQ????????????
            oks.setSite(getContext().getString(R.string.e1));
            // siteUrl??????????????????????????????????????????QQ????????????
            oks.setSiteUrl(link);
            //????????????
            oks.show(getContext());
        }
    }

    public Bitmap createViewBitmap(View v) {
//        java.lang.IllegalArgumentException: width and height must be > 0
//        at android.graphics.Bitmap.createBitmap(Bitmap.java:1042)
//        at android.graphics.Bitmap.createBitmap(Bitmap.java:1009)
//        at android.graphics.Bitmap.createBitmap(Bitmap.java:959)
//        at android.graphics.Bitmap.createBitmap(Bitmap.java:920)
//        at huolongluo.byw.reform.mine.activity.PyramidSaleWebViewActivity.a(PyramidSaleWebViewActivity.java:2)
//        at huolongluo.byw.reform.mine.activity.PyramidSaleWebViewActivity.onClick(PyramidSaleWebViewActivity.java:14)
//        at android.view.View.performClick(View.java:6637)
//        at android.view.View.performClickInternal(View.java:6614)
//        at android.view.View.access$3100(View.java:790)
//        at android.view.View$PerformClick.run(View.java:26171)
//        at android.os.Handler.handleCallback(Handler.java:873)
//        at android.os.Handler.dispatchMessage(Handler.java:99)
//        at android.os.Looper.loop(Looper.java:224)
//        at android.app.ActivityThread.main(ActivityThread.java:7058)
//        at java.lang.reflect.Method.invoke(Native Method)
//        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:536)
//        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:876)
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            v.draw(canvas);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
        return bitmap;
    }


    void systemShare() {

        if (shareType == 0) {
            try {
                Bitmap bitmap = createViewBitmap(share_pc);
                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null));
                //  Uri uri = Uri.parse(picBean.getAppSharePic());
                //  File file = new File(path);


                       /* if (file.exists()) {
                            Uri uri = Uri.fromFile(file);*/
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                context.startActivity(Intent.createChooser(intent, DomainUtil.INSTANCE.getWebUrlSuffix()));
            } catch (Throwable t) {
                Logger.getInstance().error(t);
            }
        } else {
            //  Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
            //  Uri uri = Uri.parse(picBean.getAppSharePic());
            //  File file = new File(path);


                       /* if (file.exists()) {
                            Uri uri = Uri.fromFile(file);*/
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/*");
            intent.putExtra(Intent.EXTRA_TEXT, link);
            context.startActivity(Intent.createChooser(intent, DomainUtil.INSTANCE.getWebUrlSuffix()));
        }
    }
}
