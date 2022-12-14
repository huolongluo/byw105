package huolongluo.byw.reform.home.activity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.reflect.TypeToken;

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
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.FingerprintUtil;
import huolongluo.byw.util.ImageHelper;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.domain.DomainUtil;
import huolongluo.byw.util.tip.MToast;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.EncryptUtils;
public class WelfareWebviewActivity extends BaseActivity implements View.OnClickListener {
    private WebView webView;
    private View titleView;
    private ImageButton back_iv;
    private TextView title_tv;
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
    private final String TAG = "WelfareWebviewActivity";
    private HashMap<String, String> dataMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welfare_webview);
        progressbar = findViewById(R.id.progressbar);
        progressbar.setProgress(30);
        title_tv = findViewById(R.id.title_tv);
        titleView = findViewById(R.id.title);
        webView = findViewById(R.id.webView);
        back_iv = findViewById(R.id.back_iv);
        rightIV = findViewById(R.id.right_iv);
        rightIV.setImageResource(R.mipmap.share);
        rightIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toShareBenefits();
            }
        });
        rightIV.setVisibility(View.VISIBLE);
        url = getIntent().getStringExtra("url");
        Logger.getInstance().debug("WebViewActivity", "url: " + url);
        //??????????????????????????????
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
        awardTxt = findViewById(R.id.tv_award);//????????????
        title_tv.setText(R.string.str_welfare_center);
        sharePicIV = findViewById(R.id.iv_share_pic);
        //@mipmap/ic_share_welfare_novalue
        sharePicIV.setImageResource(R.mipmap.ic_share_welfare_novalue);
        sharePic();
    }

    public void startActivity(Class<?> clazz) {
        if (clazz == null) {
            return;
        }
        Intent intent = new Intent(this, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void toShareBenefits() {//????????????????????????
        if (!UserInfoManager.isLogin()) {
            startActivity(LoginActivity.class);
            return;
        }
//        try {
//            Intent intent = new Intent();
//            intent.setClass(this, WelfareCenterShareActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        } catch (Throwable t) {
//            Logger.getInstance().error(t);
//        }
//        shareType = 0;
        shareView.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);
        if (dataMap == null || TextUtils.isEmpty(dataMap.get("welfare_share_pic"))) {
            sharePic();
        } else {
            handleResult(dataMap);
        }
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
                Logger.getInstance().debug("WebViewActivity", " errorCode: " + errorCode + " description: " + description + " failingUrl: " + failingUrl);
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
                Logger.getInstance().debug("WebViewActivity", " onReceivedHttpError");
                if (!HttpUtils.isNetworkConnected(BaseApp.getSelf())) {
                    // ??????????????? android 6.0?????????
                    view.loadUrl("about:blank");// ?????????????????????????????????
//                    view.loadUrl(mErrorUrl);// ???????????????????????????
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Logger.getInstance().debug("WebViewActivity", " onReceivedSslError");
                // android 5.0?????????????????????Mixed Content
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
//            if (event.hide) {//??????
//                titleView.setVisibility(View.GONE);
//            } else {//??????
//                titleView.setVisibility(View.VISIBLE);
//            }
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 5)
    public void onMessageEvent(Event.NativeH5 event) {
        Logger.getInstance().debug("JSInterface", "onMessageEvent: " + event.title, new Exception());
        Logger.getInstance().debug("WebViewActivity", "title: " + event.title + " url: " + webView.getUrl());
        /************??????**********/
        //TODO ??????????????????????????????????????????????????????title?????????"????????????"???????????????
        title = event.title;
        if (title_tv != null) {
            if (!TextUtils.isEmpty(event.title)) {
                title_tv.setText(event.title);
            }
        }
        /************??????**********/
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
            case R.id.wechat_bn://????????????
                showShare(Wechat.NAME);
                break;
            case R.id.friend_bn://
                showShare(WechatMoments.NAME);
                break;
            case R.id.qq_bn://
                showShare(QQ.NAME);
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
                saveImage();
                if (!TextUtils.isEmpty(localTargetPath)) {//?????????????????????-????????????
                    MToast.show(this, getString(R.string.d5), 2);
                } else {//????????????
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
        //?????????????????????
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
        //??????????????????
        saveImage();
        Logger.getInstance().debug(TAG, "platform: " + platform + " localTargetPath: " + localTargetPath);
        try {
            final OnekeyShare oks = new OnekeyShare();
            //????????????????????????????????????????????????????????????????????????????????????
            if (platform != null) {
                oks.setPlatform(platform);
            }
            //
            if (TextUtils.equals(platform, Wechat.NAME) || TextUtils.equals(platform, WechatMoments.NAME)) {
                oks.setImageData(bitmap);
            } else {
                oks.setImagePath(localTargetPath);
            }
            //????????????
            oks.show(this);
        } catch (Throwable t) {
            t.printStackTrace();
        }
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
//        params.put("loginToken", UserInfoManager.getToken());
//        params = EncryptUtils.encrypt(params);
//        params.put("loginToken", UserInfoManager.getToken());
        netTags.add(UrlConstants.ACTION_SHARE_WELFARE);
        DialogManager.INSTANCE.showProgressDialog(this);
        //????????????
//        String url = "http://172.24.249.2:3000/mock/21/app/welfare/user/share";
        Type type = new TypeToken<SingleResult<HashMap<String, String>>>() {
        }.getType();
        //UrlConstants.ACTION_SHARE_WELFARE
        String data = EncryptUtils.encryptStr(params);
        String url = UrlConstants.ACTION_SHARE_WELFARE + "?loginToken=" + UserInfoManager.getToken() + "&" + data;
        OKHttpHelper.getInstance().get(url, new HashMap(), new INetCallback<SingleResult<HashMap<String, String>>>() {
            @Override
            public void onSuccess(SingleResult<HashMap<String, String>> result) throws Throwable {
                if (result == null) {
                    DialogManager.INSTANCE.dismiss();
                    //TODO ??????????????????
                    return;
                }
                try {
                    dataMap = result.data;
                    handleResult(result.data);
                } catch (Throwable t) {
                    Logger.getInstance().error(t);
                }
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void onFailure(Exception e) throws Throwable {
                //TODO ??????????????????
                Logger.getInstance().debug(TAG, "onFailure", e);
                showErrorMessage(R.string.service_expec);
                DialogManager.INSTANCE.dismiss();
            }
        }, type);
    }

    private void handleResult(HashMap<String, String> dataMap) {
        if (dataMap == null) {
            showErrorMessage(R.string.service_expec);
            DialogManager.INSTANCE.dismiss();
            //TODO ??????????????????
            return;
        }
        //???????????????welfare_share_pic
        //???????????????award
        String sharePic = dataMap.get("welfare_share_pic");
        String award = dataMap.get("award");
        Logger.getInstance().debug(TAG, "sharePic: " + sharePic + " award: " + award);
        if (TextUtils.isEmpty(sharePic)) {
            showErrorMessage(R.string.service_expec);
            DialogManager.INSTANCE.dismiss();
            return;
        }
//        RequestOptions ro = new RequestOptions();
//        ro.error(R.mipmap.ic_share_welfare_novalue);
//        ro.placeholder(R.mipmap.ic_share_welfare);
//        ro.fallback(R.mipmap.ic_share_welfare);
//        ro.diskCacheStrategy(DiskCacheStrategy.ALL);
        //
//        if (TextUtils.isEmpty(award)) {//??????????????????????????????0?????????????????????????????????????????????????????????
//            Glide.with(this).load(R.mipmap.ic_share_welfare_novalue).apply(ro).into(sharePicIV);
//            return;
//        }
        //
//        Glide.with(this).load(sharePic).addListener(new RequestListener<Drawable>() {
//            @Override
//            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                Logger.getInstance().debug(TAG, "onLoadFailed", e);
//                showErrorMessage(R.string.service_expec);
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                Logger.getInstance().debug(TAG, "resource-height: " + resource.getIntrinsicHeight());
//                if (!TextUtils.isEmpty(award) && sharePicIV != null) {
//                    sharePicIV.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            settingPosition(resource.getIntrinsicHeight(), award);
//                        }
//                    });
//                }
//                return false;
//            }
//        }).preload();
        loadImage(sharePic, award);
    }

    private void loadImage(String sharePic, String award) {
        RequestOptions ro = new RequestOptions();
        ro.error(R.mipmap.ic_share_welfare_novalue);
        ro.placeholder(R.mipmap.ic_share_welfare);
        ro.fallback(R.mipmap.ic_share_welfare);
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
                if (!TextUtils.isEmpty(award) && sharePicIV != null) {
                    sharePicIV.post(new Runnable() {
                        @Override
                        public void run() {
                            settingPosition(resource.getIntrinsicHeight(), award);
                        }
                    });
                }
                return false;
            }
        }).apply(ro).into(sharePicIV);
    }

    private void settingPosition(int height, String award) {//????????????????????????
        if (sharePicIV == null || awardTxt == null || TextUtils.isEmpty(award)) {
            return;
        }
        try {
            if (height <= 0) {
                height = AppUtils.getDisplayMetrics(this).heightPixels - AppUtils.dp2px(80);
            }
            //?????????????????????????????????????????????????????????0.40?????????
            int marginTop = (int) (height * 0.40);
            Logger.getInstance().debug(TAG, "marginTop: " + marginTop + " award: " + award);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) awardTxt.getLayoutParams();
            if (lp != null) {
                if (lp.topMargin > 0) {//??????topMargin??????0??????????????????
                    awardTxt.setText(award);
                    return;
                }
                lp.topMargin = marginTop;
            }
            awardTxt.setText(award);
            awardTxt.setLayoutParams(lp);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }
}
