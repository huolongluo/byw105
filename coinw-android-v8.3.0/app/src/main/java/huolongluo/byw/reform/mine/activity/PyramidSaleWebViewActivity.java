package huolongluo.byw.reform.mine.activity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.mob.tools.utils.ResHelper;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.net.okhttp.HttpUtils;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.hbtpartner.HBTPartnerShareDialog;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.helper.SSLHelper;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.home.activity.JSCallJavaInterface;
import huolongluo.byw.reform.home.bean.SharePicBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.CreateQRImage;
import huolongluo.byw.util.DeviceUtils;
import huolongluo.byw.util.FingerprintUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.domain.DomainUtil;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;
import okhttp3.Request;
public class PyramidSaleWebViewActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "PyramidSaleWebViewActiv";
    public static final int TYPE_PYRAMID = 0;
    public static final int TYPE_HBT = 1;
    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    /**
     * ????????????????????????
     */
    private int type = TYPE_PYRAMID;
    Unbinder unbinder;
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
    @BindView(R.id.back_iv)
    ImageButton back_iv;
    @BindView(R.id.cope_tv)
    LinearLayout cope_tv;
    @BindView(R.id.no_login_ll)
    LinearLayout no_login_ll;
    @BindView(R.id.share_url1)
    TextView share_url1;
    @BindView(R.id.title)
    View titleView;
    private CreateQRImage mCreateQRImage;
    //
    private WebView webView;
    private TextView title_tv;
    private ProgressBar progressbar;
    private String url;
    private String token;
    private String title;
    private Map<String, String> header = new HashMap<>();
    private String currentUrl = url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_pyramidsale_webview);
        type = getIntent().getIntExtra(EXTRA_TYPE, TYPE_PYRAMID);
        unbinder = ButterKnife.bind(this);
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canGoBack()) {
                    return;
                }
                finish();
            }
        });
        sharePic();
        mCreateQRImage = new CreateQRImage();
        share_image.setOnClickListener(this);
        share_url.setOnClickListener(this);
        cope_tv.setOnClickListener(this);
        share_url1.setOnClickListener(this);
        uuid_tv.setText(UserInfoManager.getUserInfo().getFid() + "");
        ////
        progressbar = findViewById(R.id.progressbar);
        progressbar.setProgress(30);
        title_tv = findViewById(R.id.title_tv);
        webView = findViewById(R.id.webView);
        back_iv = findViewById(R.id.back_iv);
        url = getIntent().getStringExtra("url");
        titleView = findViewById(R.id.title);
        if (url.equals(UrlConstants.getFansUp())) {
            titleView.setVisibility(View.GONE);
        } else if (url.startsWith(UrlConstants.BYB)) {
            titleView.setVisibility(View.GONE);
        }
        boolean hideTitle = getIntent().getBooleanExtra("hideTitle", false);
        if (hideTitle) {
            titleView.setVisibility(View.GONE);
        }
        //????????????????????????title
        titleView.setVisibility(View.GONE);
        Logger.getInstance().debug("PyramidSaleWebViewActivity", "url: " + url);
        //??????????????????????????????
        url = getUrl(url);
        initWebInfo();
        AppHelper.setSafeBrowsingEnabled(webView);
        setWebviewAttr();
        EventBus.getDefault().register(this);
        setCodeLayout();//??????????????????????????????
    }

    @Override
    public void onBackPressed() {
        if (canGoBack()) {
            return;
        }
        super.onBackPressed();
    }

    private boolean canGoBack() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }

    //??????????????????????????????
    private void setCodeLayout() {
        //????????????????????????????????????????????????title???????????????????????????
        int height = ResHelper.getScreenHeight(this) - DeviceUtils.getStatusBarHeight(this) - ResHelper.dipToPx(this, 40) - ResHelper.dipToPx(this, 74);
        int blankHeight = (int) (height * (183.0 / 1334.0));
        int marginHeight = (blankHeight - ResHelper.dipToPx(this, 64)) / 2;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) qrCode_iv.getLayoutParams();
        lp.bottomMargin = marginHeight;
        qrCode_iv.setLayoutParams(lp);
        Logger.getInstance().debug(TAG, "setCodeLayout getScreenHeight:" + ResHelper.getScreenHeight(this) + "statusBarHeight:" + DeviceUtils.getStatusBarHeight(this) + "blankHeight:" + blankHeight + "marginHeight:" + marginHeight);
    }

    @SuppressLint("AddJavascriptInterface")
    private void setWebviewAttr() {
        int scale = getApplicationContext().getResources().getDisplayMetrics().densityDpi;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true); //????????????????????????true??????????????????????????????
        webView.getSettings().setBuiltInZoomControls(true); //????????????????????????????????????false?????????WebView????????????
        webView.getSettings().setDisplayZoomControls(true); //???????????????????????????
//        webView.getSettings().setBlockNetworkImage(false);//?????????????????????
        webView.getSettings().setLoadsImagesAutomatically(true); //????????????????????????
        webView.getSettings().setDefaultTextEncodingName("utf-8");//??????????????????
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //DEBUG????????????WebView?????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webView.addJavascriptInterface(new JSCallJavaInterface(), "JSCallJava");
        webView.getSettings().setBlockNetworkImage(true);
        //?????????????????????????????????????????????????????????URL
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Logger.getInstance().debug("PyramidSaleWebViewActivity", " errorCode: " + errorCode + " description: " + description + " failingUrl: " + failingUrl);
                if (!HttpUtils.isNetworkConnected(BaseApp.getSelf())) {
                    // ??????????????? android 6.0?????????
                    view.loadUrl("about:blank");// ?????????????????????????????????
//                    view.loadUrl(mErrorUrl);// ???????????????????????????
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                url = getUrl(url);
                header.put(SSLHelper.getHeadKey(), SSLHelper.getHeadValue(11, url));
                view.loadUrl(url, header);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                webView.getSettings().setBlockNetworkImage(false);
                //??????webview??????????????????????????????
                if (!webView.getSettings().getLoadsImagesAutomatically()) {
                    //??????wenView??????????????????
                    webView.getSettings().setLoadsImagesAutomatically(true);
                }
                super.onPageFinished(view, url);
                progressbar.setVisibility(View.GONE);
//                webView.loadUrl("javascript:JSCallJava.callJava({\"callName\":\"showNativeLogin\"});");
                log(url, true);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                currentUrl = url;
                progressbar.setVisibility(View.VISIBLE);
                log(url, false);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                if (!HttpUtils.isNetworkConnected(BaseApp.getSelf())) {
                    // ??????????????? android 6.0?????????
                    view.loadUrl("about:blank");// ?????????????????????????????????
//                    view.loadUrl(mErrorUrl);// ???????????????????????????
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // android 5.0?????????????????????Mixed Content
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
                }
                handler.proceed();
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Logger.getInstance().debug("PyramidSaleWebViewActivity", cm.message() + " -- From line " + cm.lineNumber() + " of " + cm.sourceId());
                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Logger.getInstance().debug("PyramidSaleWebViewActivity", "url: " + url + " message: " + message, new Exception());
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    progressbar.setVisibility(View.GONE);//??????????????????????????????
                } else {
                    progressbar.setVisibility(View.VISIBLE);//????????????????????????????????????
                    progressbar.setProgress(newProgress);//???????????????
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //???????????????h5???????????????????????????????????????
//                if (useH5Title) {
//                    title_tv.setText(title);
//                }
            }
        });
        webView.loadUrl(url, header);
    }

    private void log(String url, boolean finish) {
        Logger.getInstance().debug("WebViewActivity", "url: " + url + " finish: " + finish + " currTime: " + System.currentTimeMillis());
    }

    private void initWebInfo() {
        title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            title_tv.setText(title);
        }
        token = getIntent().getStringExtra("token");
        //??????????????????
        header.put("lang", AppUtils.getLanguage());
        header.put("system", "android");
        header.put(SSLHelper.getHeadKey(), SSLHelper.getHeadValue(11, url));
        setToken(token);
    }

    private void setToken(String tokens) {
        if (TextUtils.isEmpty(tokens)) {
            return;
        }
        header.put("loginToken", tokens);
        header.put("deviceId", FingerprintUtil.getFingerprint(BaseApp.getSelf()));
    }

    private String getUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        //??????URL?????????????????????
        //??????key??????lang????????????H5????????????
        if (url.indexOf("lang") != -1) {
            return url;
        } else if (url.indexOf("?") == -1) {
            return url + "?lang=" + AppUtils.getLanguage();
        } else {
            return url + "&lang=" + AppUtils.getLanguage();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 5)
    public void onMessageEvent(Event.refreFansUpInfo event) {
        if (webView != null && url != null) {
            setToken(UserInfoManager.getToken());
            webView.loadUrl(url, header);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 5)
    public void onMessageEvent(Event.FansUpNavigationBar event) {
        //???????????????????????????????????????
        Logger.getInstance().debug("JSInterface", "onMessageEvent: " + event.hide, new Exception());
        if (titleView != null && url != null) {
            if (event.hide) {//??????
                titleView.setVisibility(View.GONE);
            } else {//??????
                titleView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 5)
    public void onMessageEvent(Event.refreshVip event) {
        Logger.getInstance().debug("WebViewActivity", "refreshVip", new Exception());
        if (webView != null && currentUrl != null) {
            setToken(UserInfoManager.getToken());
            webView.loadUrl("javascript:window.USER.getTopsInfo();");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 5)
    public void onMessageEvent(Event.closeCurrentView event) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UserInfoManager.isLogin()) {
            no_login_ll.setVisibility(View.GONE);
        } else {
            no_login_ll.setVisibility(View.VISIBLE);
        }
        uuid_tv.setText(UserInfoManager.getUserInfo().getFid() + "");
        webView.onResume();
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
                        ro.diskCacheStrategy(DiskCacheStrategy.ALL);
                        Glide.with(PyramidSaleWebViewActivity.this).load(picBean.getSharePic()).apply(ro).into(imageview1);
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
        // ?????????????????????matrix??????
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // ??????????????????
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
        if (unbinder != null) {
            unbinder.unbind();
        }
        EventBus.getDefault().unregister(this);
        if (webView != null) {
            AppHelper.distoryWebView(webView);
        }
        super.onDestroy();
    }

    private int dp2px(float dp) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_image:
                if (type == TYPE_PYRAMID) {
                    // showShare(Wechat.NAME);
                    shareType = 0;
                    share_pc.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                    //
                    if (picBean != null) {
                        qrCode_iv.setImageBitmap(mCreateQRImage.createQRImage(picBean.getAgentRdgister(), dp2px(64), dp2px(64), true));
                        showPop();
                    } else {
                        sharePic();
                        MToast.show(this, getString(R.string.d3), 2);
                    }
                } else {
                    new HBTPartnerShareDialog().show(getSupportFragmentManager());
                }
                break;
            case R.id.share_url:
            case R.id.iv_share:
                // showShare(Wechat.NAME);
                shareType = 1;
                showPop();
                break;
            case R.id.wechat_bn://????????????
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
            case R.id.message_bn://????????????
               /* Uri smsToUri = Uri.parse("smsto:");
                Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                //"sms_body"???????????????smsbody?????????????????????content
                intent.putExtra("sms_body", "??????");
                startActivity(intent);*/
                showShare(ShortMessage.NAME);
                break;
            case R.id.savePng_bn://????????????
                Bitmap bitmap = null;
                if(webView.getVisibility()==View.VISIBLE){
                    bitmap = createViewBitmap(webView);
                }else{
                    bitmap = createViewBitmap(share_pc);
                }
                if (bitmap != null && saveImageToGallery(this, bitmap)) {
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
                webView.setVisibility(View.VISIBLE);
                share_pc.setVisibility(View.GONE);
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
                try {
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
        //????????????????????????????????????????????????????????????????????????????????????
        if (platform != null) {
            oks.setPlatform(platform);
        }
        if (shareType == 0) {
            if (picBean != null) {
                qrCode_iv.setImageBitmap(mCreateQRImage.createQRImage(picBean.getAgentRdgister(), dp2px(64), dp2px(64), true));
                if (TextUtils.equals(platform, Wechat.NAME) || TextUtils.equals(platform, WechatMoments.NAME)) {
                    Bitmap bitmap = createViewBitmap(share_pc);
                    if (bitmap != null) {
                        oks.setImageData(bitmap);
                    }
                } else {
                    Bitmap bitmap = createViewBitmap(share_pc);
                    if (bitmap != null) {
                        String path = saveBitmap(this, bitmap);
                        oks.setImagePath(path);
                    }
                }
                //????????????
                oks.show(this);
            } else {
                MToast.show(this, getString(R.string.e9), 1);
                sharePic();
            }
        } else if (picBean != null) {
            if (TextUtils.equals(platform, ShortMessage.NAME)) {
                oks.setText(picBean.getAgentRdgister());
                //????????????
                oks.show(this);
                return;
            }
            oks.disableSSOWhenAuthorize();
            // title???????????????????????????????????????????????????????????????QQ????????????
            oks.setTitle(getString(R.string.d7));
            // titleUrl?????????????????????????????????Linked-in,QQ???QQ????????????
            oks.setTitleUrl(picBean.getAgentRdgister());
            // text???????????????????????????????????????????????????
            oks.setText(getString(R.string.d8));
            //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            //  oks.setImageUrl(picBean.getAppSharePic());
            Bitmap Bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.share_logo);
            String path = saveBitmap(this, Bmp);
            oks.setImagePath(path);
            // imagePath???????????????????????????Linked-In?????????????????????????????????
            //oks.setImagePath("/sdcard/test.jpg");//??????SDcard????????????????????????
            // url???????????????????????????????????????????????????
            oks.setUrl(picBean.getAgentRdgister());
            // comment???????????????????????????????????????????????????QQ????????????
            oks.setComment(getString(R.string.e2));
            // site??????????????????????????????????????????QQ????????????
            oks.setSite(getString(R.string.e1));
            // siteUrl??????????????????????????????????????????QQ????????????
            oks.setSiteUrl(picBean.getAgentRdgister());
            //????????????
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
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (share_pc != null) {
                    share_pc.setVisibility(View.GONE);
                }
                if (webView != null) {
                    webView.setVisibility(View.VISIBLE);
                }
            }
        });
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
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
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
            //????????????????????????????????????????????????????????????????????????????????????????????????
//            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
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

    @Override
    public void finish() {
        super.finish();
        if (progressbar != null) {
            progressbar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }
}
