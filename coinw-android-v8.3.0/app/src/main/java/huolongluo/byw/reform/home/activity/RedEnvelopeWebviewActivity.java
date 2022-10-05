package huolongluo.byw.reform.home.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.coinw.biz.event.BizEvent;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.mob.tools.utils.ResHelper;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.net.okhttp.HttpUtils;
import huolongluo.byw.byw.share.Event;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.helper.SSLHelper;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.bean.RedEnvelopeInviteBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.CreateQRImage;
import huolongluo.byw.util.DeviceUtils;
import huolongluo.byw.util.ImageHelper;
import huolongluo.byw.util.QRCodeUtil;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.Util;
import huolongluo.byw.util.domain.DomainUtil;
import huolongluo.byw.util.tip.MToast;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.EncryptUtils;

public class RedEnvelopeWebviewActivity extends BaseActivity implements View.OnClickListener {
    private WebView webView;
    private ImageButton back_iv;
    private TextView title_tv;
    private View titleView;
    private ImageView rightIV;
    private ProgressBar progressbar;
    private String url;
    private String token;
    private String title;
    private Map<String, String> header = new HashMap<>();
    private String currentUrl = url;
    //
    private ImageView sharePicIV;
    private TextView awardTxt;
    private RelativeLayout sharePicLayout;
    private Bitmap bitmap;
    private String localTargetPath = "";
    private View shareView;
    private View contentView;
    private ImageView qrCode_iv;
    private final String TAG = "RedEnvelopeWebviewActivity";
    private RedEnvelopeInviteBean redEnvelopeInviteBean;
    private CreateQRImage mCreateQRImage;
    //
    private String userId, inviteRedId;

    public static void launch(Context context, String url, String title) {
        //需求改动
        Intent intent = new Intent(context, RedEnvelopeWebviewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        //
        if (UserInfoManager.isLogin()) {//登录状态，传入token
            intent.putExtra("token", UserInfoManager.getToken());
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redenvelope_webview);
        progressbar = findViewById(R.id.progressbar);
        progressbar.setProgress(30);
        title_tv = findViewById(R.id.title_tv);
        titleView = findViewById(R.id.title);
        webView = findViewById(R.id.webView);
        back_iv = findViewById(R.id.back_iv);
        qrCode_iv = findViewById(R.id.qrCode_iv);
        url = getIntent().getStringExtra("url");
        Logger.getInstance().debug("WebViewActivity", "url: " + url);
        //判断并加入多语言标识
        url = getUrl(url);
        back_iv.setOnClickListener(view -> {
            if (canGoBack()) {
                return;
            }
            finish();
        });
        initWebInfo();
        AppHelper.setSafeBrowsingEnabled(webView);
        setWebviewAttr();
        EventBus.getDefault().register(this);
        //
        findViewById(R.id.wechat_bn).setOnClickListener(this);
        findViewById(R.id.friend_bn).setOnClickListener(this);
        findViewById(R.id.qq_bn).setOnClickListener(this);
        findViewById(R.id.message_bn).setOnClickListener(this);
        findViewById(R.id.savePng_bn).setOnClickListener(this);
        findViewById(R.id.more_bn).setOnClickListener(this);
        findViewById(R.id.tvCancel).setOnClickListener(this);
        //
        shareView = findViewById(R.id.ll_share);
        contentView = findViewById(R.id.ll_webview);
        sharePicLayout = findViewById(R.id.rl_share_pic);
        awardTxt = findViewById(R.id.tv_award);//奖励数量
        title_tv.setText(R.string.str_red_envelope);
        titleView.setVisibility(View.GONE);
        sharePicIV = findViewById(R.id.iv_share_pic);
        sharePicIV.setImageResource(R.mipmap.ic_share_redenvelope_invite);
        mCreateQRImage = new CreateQRImage();
        sharePic();
        setCodeLayout();
    }

    public void startActivity(Class<?> clazz) {
        if (clazz == null) {
            return;
        }
        Intent intent = new Intent(this, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void toShare(String userId, String inviteRedId) {//分享功能
        if (!UserInfoManager.isLogin()) {
            startActivity(LoginActivity.class);
            return;
        }
        this.userId = userId;
        this.inviteRedId = inviteRedId;
        shareView.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);
        if (redEnvelopeInviteBean == null || TextUtils.isEmpty(redEnvelopeInviteBean.url)) {
            sharePic();
        } else {
            handleResult(redEnvelopeInviteBean);
        }
    }

    private String getUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        //判断URL是否有语言标识
        //传参key值为lang，由前端H5确认决定
        if (url.indexOf("lang") != -1) {
            return url;
        } else if (url.indexOf("?") == -1) {
            return url + "?lang=" + AppUtils.getLanguage();
        } else {
            return url + "&lang=" + AppUtils.getLanguage();
        }
    }

    private void initWebInfo() {
        title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            title_tv.setText(title);
        }
        token = getIntent().getStringExtra("token");
        //加入语言标识
        header.put("lang", AppUtils.getLanguage());
        header.put("system", "android");
        header.put(SSLHelper.getHeadKey(), SSLHelper.getHeadValue(11, url));
        setToken(token);
    }

    private void setToken(String tokens) {
        if (TextUtils.isEmpty(tokens)) {
            return;
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            try {
//                RSACipher rsaCipher = new RSACipher();
//                tokens = rsaCipher.encrypt(tokens, AppConstants.KEY.PUBLIC_KEY).replace("\n", "");
//                header.put("loginToken", tokens);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        //根据后台要求去掉加密
        header.put("loginToken", tokens);
    }

    //动态设置二维码的位置
    private void setCodeLayout() {
        //高度为屏幕高度减去状态栏高度减去title高度减去底部的高度
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
        webView.getSettings().setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webView.getSettings().setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webView.getSettings().setDisplayZoomControls(true); //隐藏原生的缩放控件
//        webView.getSettings().setBlockNetworkImage(false);//解决图片不显示
        webView.getSettings().setLoadsImagesAutomatically(true); //支持自动加载图片
        webView.getSettings().setDefaultTextEncodingName("utf-8");//设置编码格式
        webView.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //DEBUG模式打开WebView的调试
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webView.addJavascriptInterface(new JSCallJavaInterface(), "JSCallJava");
        webView.getSettings().setBlockNetworkImage(true);
        //点击超链接的时候重新在原来的进程上加载URL
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Logger.getInstance().debug("WebViewActivity", " errorCode: " + errorCode + " description: " + description + " failingUrl: " + failingUrl);
                if (!HttpUtils.isNetworkConnected(BaseApp.getSelf())) {
                    // 这个方法在 android 6.0才出现
                    view.loadUrl("about:blank");// 避免出现默认的错误界面
//                    view.loadUrl(mErrorUrl);// 加载自定义错误页面
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
                //判断webview是否加载了，图片资源
                if (!webView.getSettings().getLoadsImagesAutomatically()) {
                    //设置wenView加载图片资源
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
                Logger.getInstance().debug("WebViewActivity", " onReceivedHttpError");
                if (!HttpUtils.isNetworkConnected(BaseApp.getSelf())) {
                    // 这个方法在 android 6.0才出现
                    view.loadUrl("about:blank");// 避免出现默认的错误界面
//                    view.loadUrl(mErrorUrl);// 加载自定义错误页面
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Logger.getInstance().debug("WebViewActivity", " onReceivedSslError");
                // android 5.0以上默认不支持Mixed Content
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
                }
                handler.proceed();
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                log(url, true);
                super.onLoadResource(view, url);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Logger.getInstance().debug("WebViewActivity", cm.message() + " -- From line " + cm.lineNumber() + " of " + cm.sourceId());
                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Logger.getInstance().debug("WebViewActivity", "url: " + url + " message: " + message, new Exception());
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    progressbar.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    progressbar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    progressbar.setProgress(newProgress);//设置进度值
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //设置标题为h5的，因标题太长，暂时不设置
//                if (useH5Title) {
//                    title_tv.setText(title);
//                }
            }

            @Override
            public void onCloseWindow(WebView window) {
                log(window.getUrl(), true);
                super.onCloseWindow(window);
            }
        });
        webView.loadUrl(url, header);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 5)
    public void onMessageEvent(Event.refreFansUpInfo event) {
        if (webView != null && url != null) {
            setToken(UserInfoManager.getToken());
            webView.loadUrl(url, header);
        }
        try {
            sharePic();
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 5)
    public void onMessageEvent(Event.FansUpNavigationBar event) {
        Logger.getInstance().debug("JSInterface", "onMessageEvent: " + event.hide, new Exception());
//        if (titleView != null && url != null) {
//            if (event.hide) {//隐藏
//                titleView.setVisibility(View.GONE);
//            } else {//显示
//                titleView.setVisibility(View.VISIBLE);
//            }
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 5)
    public void onMessageEvent(Event.NativeH5 event) {
        Logger.getInstance().debug("JSInterface", "onMessageEvent: " + event.title, new Exception());
        Logger.getInstance().debug("WebViewActivity", "title: " + event.title + " url: " + webView.getUrl());
        /************开始**********/
        //TODO 根据与产品沟通，第一版福利中心的页面title显示为"福利中心"，故不允许
        title = event.title;
        if (title_tv != null) {
            if (!TextUtils.isEmpty(event.title)) {
                title_tv.setText(event.title);
            }
        }
        /************结束**********/
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
    public void finish() {
        super.finish();
        if (progressbar != null) {
            progressbar.setVisibility(View.GONE);
        }
        ImageHelper.recycle(bitmap);
        DialogManager.INSTANCE.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            AppHelper.distoryWebView(webView);
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JSCallJavaInterface.FromFansUpView = false;
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    private void log(String url, boolean finish) {
        Logger.getInstance().debug("WebViewActivity", "url: " + url + " finish: " + finish + " currTime: " + System.currentTimeMillis());
    }

    private boolean canGoBack() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            String title = webView.getTitle();
            Logger.getInstance().debug("WebViewActivity", "title: " + title + " url: " + webView.getUrl());
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (canGoBack()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wechat_bn://微信分享
                showShare(Wechat.NAME);
                break;
            case R.id.friend_bn://
                showShare(WechatMoments.NAME);
                break;
            case R.id.qq_bn://
                showShare(QQ.NAME);
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
                saveImage();
                if (!TextUtils.isEmpty(localTargetPath)) {//已经生成好图片-保存成功
                    MToast.show(this, getString(R.string.d5), 2);
                } else {//保存失败
                    MToast.show(this, getString(R.string.d6), 2);
                }
                break;
            case R.id.more_bn://
                systemShare();
                break;
            case R.id.tvCancel://
                shareView.setVisibility(View.GONE);
                contentView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void saveImage() {
        //生成图片并保存
        if (sharePicLayout != null && bitmap == null || TextUtils.isEmpty(localTargetPath)) {
            bitmap = ImageHelper.createViewBitmap(sharePicLayout);
            localTargetPath = ImageHelper.saveImageToGallery(getApplicationContext(), bitmap);
        }
    }

    private void systemShare() {
        try {
            saveImage();
            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, DomainUtil.INSTANCE.getWebUrlSuffix()));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void showShare(String platform) {
        //首先生成图片
        saveImage();
        Logger.getInstance().debug(TAG, "platform: " + platform + " localTargetPath: " + localTargetPath);
        try {
            final OnekeyShare oks = new OnekeyShare();
            //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
            if (platform != null) {
                oks.setPlatform(platform);
            }
            //
            if (TextUtils.equals(platform, Wechat.NAME) || TextUtils.equals(platform, WechatMoments.NAME)) {
                oks.setImageData(bitmap);
            } else {
                oks.setImagePath(localTargetPath);
            }
            //启动分享
            oks.show(this);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void RedEnvelopeEvent(BizEvent.RedEnvelopeInviteEvent event) {
        toShare(event.userId, event.inviteRedId);
    }

    private void showErrorMessage(int resId) {
        if (resId <= 0) {
            return;
        }
        Toast.makeText(this, getString(resId), Toast.LENGTH_LONG);
    }

    private void sharePic() {
        Map<String, Object> params = new HashMap<>();
        params.put("bodyType", "1");
        netTags.add(UrlConstants.REDENVELOPE_INVITE_URL);
        DialogManager.INSTANCE.showProgressDialog(this);
        //测试地址
        Type type = new TypeToken<SingleResult<SingleResult<RedEnvelopeInviteBean>>>() {
        }.getType();
        String data = EncryptUtils.encryptStr(params);
        String url = UrlConstants.REDENVELOPE_INVITE_URL + "?loginToken=" + UserInfoManager.getToken() + "&" + data;
        OKHttpHelper.getInstance().get(url, new HashMap(), new INetCallback<SingleResult<SingleResult<RedEnvelopeInviteBean>>>() {
            @Override
            public void onSuccess(SingleResult<SingleResult<RedEnvelopeInviteBean>> result) throws Throwable {
                if (result == null) {
                    DialogManager.INSTANCE.dismiss();
                    //TODO 异常处理情况
                    return;
                }
                try {
                    if (result.data == null || result.data.data == null) {
                        return;
                    }
                    redEnvelopeInviteBean = result.data.data;
                    handleResult(redEnvelopeInviteBean);
                } catch (Throwable t) {
                    Logger.getInstance().error(t);
                }
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void onFailure(Exception e) throws Throwable {
                //TODO 处理异常情况
                Logger.getInstance().debug(TAG, "onFailure", e);
                showErrorMessage(R.string.service_expec);
                DialogManager.INSTANCE.dismiss();
            }
        }, type);
    }

    private void handleResult(RedEnvelopeInviteBean inviteBean) {
        if (inviteBean == null || inviteBean.img == null) {
            showErrorMessage(R.string.service_expec);
            DialogManager.INSTANCE.dismiss();
            //TODO 处理异常情况
            return;
        }
        String sharePic = inviteBean.img.i18nZhCn;
        String language = AppUtils.getAppLanguage();
        if (TextUtils.equals(language, "en_US")) {
            sharePic = inviteBean.img.i18nEnUs;
        } else if (TextUtils.equals(language, "ko_KR")) {
            sharePic = inviteBean.img.i18nKoKr;
        }

        Logger.getInstance().debug(TAG, "sharePic: " + sharePic);
        if (TextUtils.isEmpty(sharePic)) {
            showErrorMessage(R.string.service_expec);
            DialogManager.INSTANCE.dismiss();
            return;
        }
        loadImage(sharePic, inviteBean.url);
    }

    private void loadImage(String sharePic, String qrCodeUrl) {
        RequestOptions ro = new RequestOptions();
        ro.error(R.mipmap.ic_share_redenvelope_invite);
        ro.placeholder(R.mipmap.ic_share_redenvelope_invite);
        ro.fallback(R.mipmap.ic_share_redenvelope_invite);
        ro.diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(this).load(sharePic).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Logger.getInstance().debug(TAG, "onLoadFailed", e);
                showErrorMessage(R.string.service_expec);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Logger.getInstance().debug(TAG, "resource-height: " + resource.getIntrinsicHeight());
                if (sharePicIV != null) {
                    sharePicIV.post(() -> settingQRCode(qrCodeUrl));
                }
                return false;
            }
        }).apply(ro).into(sharePicIV);
    }

    private void settingQRCode(String sharePic) {//重新设置显示位置
        //生成二维码
        if (qrCode_iv == null || mCreateQRImage == null || TextUtils.isEmpty(userId) || TextUtils.isEmpty(inviteRedId)) {
            String msg = String.format("RET-userId:%s,inviteRedId:%s", userId, inviteRedId);
            Logger.getInstance().debug(TAG, msg);
            return;
        }
        String url = sharePic + "?agentid=" + userId + "&inviteRedId=" + inviteRedId;
        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.coin_logo_1);
        Bitmap qrCodeBitmap = QRCodeUtil.createQRCodeBitmap(url, 800, 800, "UTF-8", ErrorCorrectionLevel.H, 1, Color.BLACK, Color.WHITE, logoBitmap, 0.3F);
        Glide.with(this).load(qrCodeBitmap).into(qrCode_iv);
//        qrCode_iv.setImageBitmap(mCreateQRImage.createQRImage(sharePic, Util.dp2px(RedEnvelopeWebviewActivity.this, 64), Util.dp2px(RedEnvelopeWebviewActivity.this, 64), true));
    }
}
