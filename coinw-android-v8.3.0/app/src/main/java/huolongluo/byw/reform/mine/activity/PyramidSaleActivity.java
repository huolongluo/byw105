package huolongluo.byw.reform.mine.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.home.bean.SharePicBean;
import huolongluo.byw.reform.mine.bean.BounsBean;
import huolongluo.byw.reform.mine.fragment.PyramidFriendsFragment;
import huolongluo.byw.reform.mine.fragment.PyramidMoneyFragment;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.CreateQRImage;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.cache.CacheManager;
import huolongluo.byw.util.domain.DomainUtil;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.tip.MToast;
import okhttp3.Request;

/**
 * Created by Administrator on 2019/3/20 0020.
 */
public class PyramidSaleActivity extends BaseActivity implements View.OnClickListener {

    private BounsBean bounsBean;
    Unbinder unbinder;
    @BindViews({R.id.name_tv1, R.id.name_tv2, R.id.name_tv3})
    List<TextView> name_tv;
    @BindViews({R.id.number_tv1, R.id.number_tv2, R.id.number_tv3})
    List<TextView> number_tv;
    @BindView(R.id.friendNum_tv)
    TextView friendNum_tv;
    @BindView(R.id.total_tv)
    TextView total_tv;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.share_pc)
    RelativeLayout share_pc;
    @BindView(R.id.imageview1)
    ImageView imageview1;
    @BindView(R.id.qrCode_iv)
    ImageView qrCode_iv;
    @BindView(R.id.share_image)
    TextView share_image;
    @BindView(R.id.uuid_tv)
    TextView uuid_tv;
    @BindView(R.id.share_url)
    TextView share_url;
    @BindView(R.id.button1_ll)
    LinearLayout button1_ll;
    @BindView(R.id.button2_ll)
    LinearLayout button2_ll;
    @BindView(R.id.view1)
    View view1;
    @BindView(R.id.view2)
    View view2;
    @BindView(R.id.back_iv)
    ImageButton back_iv;
    @BindView(R.id.cope_tv)
    LinearLayout cope_tv;
    @BindView(R.id.isLogin_view)
    LinearLayout isLogin_view;
    @BindView(R.id.no_login_ll)
    LinearLayout no_login_ll;
    @BindView(R.id.iv_share)
    ImageView iv_share;
    @BindView(R.id.share_url1)
    TextView share_url1;
    @BindView(R.id.proportion_tv)
    TextView proportion_tv;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    //  @BindView(R.id.refresh_layout)
    //TwinklingRefreshLayout refresh_layout;
    private CreateQRImage mCreateQRImage;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pyramidsale);
        unbinder = ButterKnife.bind(this);
        //  refresh_layout.setEnableRefresh(false);
        //  refresh_layout.setOverScrollTopShow(false);
        // refresh_layout.setEnableLoadmore(true);
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //   scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
        String result = CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().getAsString("mydivide");
        if (!TextUtils.isEmpty(result)) {
            try {
                bounsBean = new Gson().fromJson(result, BounsBean.class);
                if (bounsBean != null && bounsBean.getCode() == 0) {
                    refireshView();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sharePic();
        mCreateQRImage = new CreateQRImage();
        share_image.setOnClickListener(this);
        share_url.setOnClickListener(this);
        button1_ll.setOnClickListener(this);
        button2_ll.setOnClickListener(this);
        cope_tv.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        share_url1.setOnClickListener(this);
        uuid_tv.setText(UserInfoManager.getUserInfo().getFid() + "");
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            Fragment[] fragments = new Fragment[]{new PyramidFriendsFragment(), new PyramidMoneyFragment()};

            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    return fragments[0];
                } else {
                    return fragments[1];
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (scrollView == null) {
            return;
        }
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    scrollView.scrollTo(0, 0);
                    scrollView.fullScroll(ScrollView.FOCUS_UP);//滑到顶部
                } catch (Throwable t) {
                    Logger.getInstance().error(t);
                }
            }
        });
    }

    public Bitmap createViewBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    void refireshView() {
        List<BounsBean.TreeMap> list = bounsBean.getTreeMap();
        for (int i = 0; i < 3; i++) {
            if (list.size() > i) {
                name_tv.get(i).setText(list.get(i).getNameString());
                number_tv.get(i).setText(list.get(i).getQty() + " CNYT");
            }
        }
        // qrCode_iv.setImageBitmap(mCreateQRImage.createQRImage(bounsBean.getSpreadLink(), qrCode_iv.getWidth(), qrCode_iv.getHeight()));
        proportion_tv.setText(bounsBean.getProportion());
        if (UserInfoManager.isLogin()) {
            total_tv.setText(bounsBean.getSettlement() + "");
            friendNum_tv.setText(bounsBean.getTotal() + "");
        }
    }

    void mydivide() {
        DialogManager.INSTANCE.showProgressDialog(this, getString(R.string.load));
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        if (UserInfoManager.isLogin()) {
            params.put("loginToken", UserInfoManager.getToken());
        } else {
            params.put("loginToken", "0");
        }
        netTags.add(UrlConstants.mydivide);
        OkhttpManager.postAsync(UrlConstants.mydivide, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                try {
                    bounsBean = new Gson().fromJson(result, BounsBean.class);
                    if (bounsBean != null && bounsBean.getCode() == 0) {
                        refireshView();
                        CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().put("mydivide", result);
                    } else {
                        MToast.show(PyramidSaleActivity.this, bounsBean.getValue(), 2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UserInfoManager.isLogin()) {
            isLogin_view.setVisibility(View.VISIBLE);
            iv_share.setVisibility(View.VISIBLE);
            no_login_ll.setVisibility(View.GONE);
        } else {
            iv_share.setVisibility(View.GONE);
            isLogin_view.setVisibility(View.GONE);
            no_login_ll.setVisibility(View.VISIBLE);
        }
        mydivide();
    }

    SharePicBean picBean;

    void sharePic() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        netTags.add(UrlConstants.sharePic);
        OkhttpManager.postAsync(UrlConstants.sharePic, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    picBean = new Gson().fromJson(result, SharePicBean.class);
                    if (picBean.getCode() != 0) {
                        picBean = null;
                    } else {
                        RequestOptions ro = new RequestOptions();
                        ro.error(R.mipmap.rmblogo);
                        ro.centerCrop();
                        Glide.with(PyramidSaleActivity.this).load(picBean.getSharePic()).apply(ro).into(imageview1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static Bitmap newBitmap(Bitmap bit1, Bitmap bit2) {
        Bitmap newBit = null;
        int width = bit1.getWidth();
        if (bit2.getWidth() != width) {
            int h2 = bit2.getHeight() * width / bit2.getWidth();
            newBit = Bitmap.createBitmap(width, bit1.getHeight() + h2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(newBit);
            Bitmap newSizeBitmap2 = getNewSizeBitmap(bit2, width, h2);
            canvas.drawBitmap(bit1, 0, 0, null);
            canvas.drawBitmap(newSizeBitmap2, 0, bit1.getHeight(), null);
        } else {
            newBit = Bitmap.createBitmap(width, bit1.getHeight() + bit2.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(newBit);
            canvas.drawBitmap(bit1, 0, 0, null);
            canvas.drawBitmap(bit2, 0, bit1.getHeight(), null);
        }
        return newBit;
    }

    public static Bitmap getNewSizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        float scaleWidth = ((float) newWidth) / bitmap.getWidth();
        float scaleHeight = ((float) newHeight) / bitmap.getHeight();
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap bit1Scale = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bit1Scale;
    }

    @Override
    protected void onDestroy() {
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (Throwable t) {
            }
        }
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1_ll:
                view1.setVisibility(View.VISIBLE);
                view2.setVisibility(View.INVISIBLE);
                viewPager.setCurrentItem(0);
                break;
            case R.id.button2_ll:
                view1.setVisibility(View.INVISIBLE);
                view2.setVisibility(View.VISIBLE);
                viewPager.setCurrentItem(1);
                break;
            case R.id.share_image:
                // showShare(Wechat.NAME);
                shareType = 0;
                //
                if (picBean != null) {
                    qrCode_iv.setImageBitmap(mCreateQRImage.createQRImage(picBean.getAgentRdgister(), qrCode_iv.getWidth(), qrCode_iv.getHeight(), true));
                    showPop();
                } else {
                    sharePic();
                    MToast.show(this, getString(R.string.d3), 2);
                }
                break;
            case R.id.share_url:
            case R.id.iv_share:
                // showShare(Wechat.NAME);
                shareType = 1;
                showPop();
                break;
            case R.id.wechat_bn://微信分享
                showShare(Wechat.NAME);
                if (dialog != null) {
                    dialog.dismiss();
                }
                break;
            case R.id.friend_bn://
                showShare(WechatMoments.NAME);
                if (dialog != null) {
                    dialog.dismiss();
                }
                break;
            case R.id.qq_bn://
                showShare(QQ.NAME);
                if (dialog != null) {
                    dialog.dismiss();
                }
                break;
            case R.id.message_bn://短信分享
               /* Uri smsToUri = Uri.parse("smsto:");
                Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                //"sms_body"必须一样，smsbody是发送短信内容content
                intent.putExtra("sms_body", "你好");
                startActivity(intent);*/
                showShare(ShortMessage.NAME);
                break;
            case R.id.savePng_bn://保存图片
                Bitmap bitmap = createViewBitmap(share_pc);
                if (saveImageToGallery(this, bitmap)) {
                    MToast.show(this, getString(R.string.d5), 2);
                } else {
                    MToast.show(this, getString(R.string.d6), 2);
                   /* new Thread(new Runnable() {
                        @Override
                        public void run() {
                            bitmap = getBitMBitmap("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");

                        }
                    }).start();*/
                }
                if (dialog != null) {
                    dialog.dismiss();
                }
                break;
            case R.id.more_bn://
                systemShare();
                if (dialog != null) {
                    dialog.dismiss();
                }
                break;
            case R.id.tvCancel://
                if (dialog != null) {
                    dialog.dismiss();
                }
                break;
            case R.id.cope_tv:
                if (!TextUtils.isEmpty(UserInfoManager.getUserInfo().getFid() + "")) {
                    NorUtils.copeText(this, UserInfoManager.getUserInfo().getFid() + "");
                    MToast.showButton(this, getString(R.string.e7), 1);
                }
                break;
            case R.id.share_url1:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

    void systemShare() {
        if (picBean != null) {
            if (shareType == 0) {
                Bitmap bitmap = createViewBitmap(share_pc);
                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
                //  Uri uri = Uri.parse(picBean.getAppSharePic());
                //  File file = new File(path);


                       /* if (file.exists()) {
                            Uri uri = Uri.fromFile(file);*/
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(intent, DomainUtil.INSTANCE.getWebUrlSuffix()));
            } else {
                //  Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
                //  Uri uri = Uri.parse(picBean.getAppSharePic());
                //  File file = new File(path);


                       /* if (file.exists()) {
                            Uri uri = Uri.fromFile(file);*/
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/*");
                intent.putExtra(Intent.EXTRA_TEXT, picBean.getAgentRdgister());
                startActivity(Intent.createChooser(intent, DomainUtil.INSTANCE.getWebUrlSuffix()));
            }
        } else {
            MToast.show(this, getString(R.string.e8), 1);
            sharePic();
        }
    }

    int shareType = 0;

    private void showShare(String platform) {
        final OnekeyShare oks = new OnekeyShare();
        //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
        if (platform != null) {
            oks.setPlatform(platform);
        }
        if (shareType == 0) {
            if (picBean != null) {
                //  qrCode_iv.setImageBitmap(mCreateQRImage.createQRImage(picBean.getAgentRdgister(), qrCode_iv.getWidth(), qrCode_iv.getHeight()));
                if (TextUtils.equals(platform, Wechat.NAME) || TextUtils.equals(platform, WechatMoments.NAME)) {
                    Bitmap bitmap = createViewBitmap(share_pc);
                    oks.setImageData(bitmap);
                } else {
                    Bitmap bitmap = createViewBitmap(share_pc);
                    String path = saveBitmap(this, bitmap);
                    oks.setImagePath(path);
                }
                //启动分享
                oks.show(this);
            } else {
                MToast.show(this, getString(R.string.e9), 1);
                sharePic();
            }
        } else if (picBean != null) {
            if (TextUtils.equals(platform, ShortMessage.NAME)) {
                oks.setText(picBean.getAgentRdgister());
                //启动分享
                oks.show(this);
                return;
            }
            oks.disableSSOWhenAuthorize();
            // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
            oks.setTitle(getString(R.string.d7));
            // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
            oks.setTitleUrl(picBean.getAgentRdgister());
            // text是分享文本，所有平台都需要这个字段
            oks.setText(getString(R.string.d8));
            //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
            //  oks.setImageUrl(picBean.getAppSharePic());
            Bitmap Bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.share_logo);
            String path = saveBitmap(this, Bmp);
            oks.setImagePath(path);
            // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
            //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
            // url仅在微信（包括好友和朋友圈）中使用
            oks.setUrl(picBean.getAgentRdgister());
            // comment是我对这条分享的评论，仅在人人网和QQ空间使用
            oks.setComment(getString(R.string.e2));
            // site是分享此内容的网站名称，仅在QQ空间使用
            oks.setSite(getString(R.string.e1));
            // siteUrl是分享此内容的网站地址，仅在QQ空间使用
            oks.setSiteUrl(picBean.getAgentRdgister());
            //启动分享
            oks.show(this);
        } else {
            MToast.show(this, getString(R.string.d9), 1);
            sharePic();
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

    private Dialog dialog;

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

    //保存文件到指定路径
    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
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
}
