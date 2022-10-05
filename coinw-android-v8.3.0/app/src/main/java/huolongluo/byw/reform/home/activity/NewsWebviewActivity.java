package huolongluo.byw.reform.home.activity;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.android.coinw.biz.event.BizEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.net.okhttp.HttpUtils;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.fragment.maintab04.MediaUtility;
import huolongluo.byw.byw.ui.fragment.maintab04.WebViewActivity;
import huolongluo.byw.helper.SSLHelper;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.FingerprintUtil;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.byw.util.webview.WebviewUtils;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;
/**
 * Created by haiyang on 2019/1/8.
 */
public class NewsWebviewActivity extends BaseActivity {
    private static final String TAG = "NewsWebviewActivity";
    private WebView webView;
    private View titleView;
    private ImageButton back_iv, ibClose;
    private TextView title_tv;
    private ProgressBar progressbar;
    private String url;
    private String token;
    private String title;
    private Map<String, String> header = new HashMap<>();
    private String currentUrl = url;
    private boolean useH5Title;
    private boolean isBdb;
    public static boolean isBdbJump = false;
    private MyWebChromeClient webChromeClient;
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        progressbar = findViewById(R.id.progressbar);
        progressbar.setProgress(30);
        title_tv = findViewById(R.id.title_tv);
        titleView = findViewById(R.id.title);
        webView = findViewById(R.id.webView);
        back_iv = findViewById(R.id.back_iv);
        ibClose = findViewById(R.id.ib_close);
        url = getIntent().getStringExtra("url");
        isBdb = getIntent().getBooleanExtra("isBdb", false);
        isBdbJump = false;
        //
        ibClose.setVisibility(View.VISIBLE);
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (url.equals(UrlConstants.getFansUp())) {
            findViewById(R.id.title).setVisibility(View.GONE);
        } else if (url.startsWith(UrlConstants.BYB)) {
            findViewById(R.id.title).setVisibility(View.GONE);
        }
        boolean hideTitle = getIntent().getBooleanExtra("hideTitle", false);
        if (hideTitle) {
            findViewById(R.id.title).setVisibility(View.GONE);
        }
        useH5Title = getIntent().getBooleanExtra("useH5Title", false);
        if (!useH5Title) {
            title_tv.setText(R.string.cz21);
        }
//        webView.setLayerType(View.LAYER_TYPE_HARDWARE,null);//开启硬件加速
        Logger.getInstance().debug("WebViewActivity", "url: " + url);
        //判断并加入多语言标识
        url = getUrl(url);
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });
        initWebInfo();
        AppHelper.setSafeBrowsingEnabled(webView);
        setWebviewAttr();
        EventBus.getDefault().register(this);
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
            header.remove("loginToken");
            return;
        }
        header.put("loginToken", tokens);
        header.put("deviceId", FingerprintUtil.getFingerprint(BaseApp.getSelf()));
    }

    @SuppressLint("AddJavascriptInterface")
    private void setWebviewAttr() {
        WebviewUtils.getWebViewSetSettings(webView);
        webView.addJavascriptInterface(new JSCallJavaInterface(), "JSCallJava");
        //点击超链接的时候重新在原来的进程上加载URL
        webView.setWebViewClient(new WebViewClient() {
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        request.getRequestHeaders().put(SSLHelper.getHeadKey(), SSLHelper.getHeadValue(11, request.getUrl().toString()));
                    }
                } catch (Throwable t) {
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

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
                if (url.startsWith("coinw://")) {
                    return true;//拦截掉h5内部的app唤醒
                }
                url = getUrl(url);
                header.put(SSLHelper.getHeadKey(), SSLHelper.getHeadValue(11, url));
                view.loadUrl(url, header);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (webView != null) {
                    webView.getSettings().setBlockNetworkImage(false);
                    //判断webview是否加载了，图片资源
                    if (!webView.getSettings().getLoadsImagesAutomatically()) {
                        //设置wenView加载图片资源
                        webView.getSettings().setLoadsImagesAutomatically(true);
                    }
                }
                super.onPageFinished(view, url);
                if (progressbar != null) {
                    progressbar.setVisibility(View.GONE);
                }
//                webView.loadUrl("javascript:JSCallJava.callJava({\"callName\":\"showNativeLogin\"});");
                log(url, true);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                currentUrl = url;
                if (progressbar != null) {
                    progressbar.setVisibility(View.VISIBLE);
                }
                log(url, false);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                Logger.getInstance().debug("WebViewActivity", " onLoadResource-url: " + url);
                super.onLoadResource(view, url);
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
        });
        webChromeClient=new MyWebChromeClient();
        webView.setWebChromeClient(webChromeClient);
        webView.loadUrl(url, header);
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
        Logger.getInstance().debug("JSInterface", "onMessageEvent: " + event.hide, new Exception());
        if (titleView != null && url != null) {
            if (event.hide) {//隐藏
                titleView.setVisibility(View.GONE);
            } else {//显示
                titleView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 5)
    public void onMessageEvent(Event.NativeH5 event) {
        Logger.getInstance().debug("JSInterface", "onMessageEvent: " + event.title, new Exception());
        title = event.title;
        if (title_tv != null) {
            if (!TextUtils.isEmpty(event.title)) {
                title_tv.setText(event.title);
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
        exit();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void finishEvent(BizEvent.H5.finishEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void offLineRedEnvelopeEvent(BizEvent.OffLineRedEnvelopeEvent event) {
        if (webView != null) {
//            String returnStr="\""+event.returnStr+"\"";
            String returnStr = event.returnStr;
            Logger.getInstance().debug(TAG, "offLineRedEnvelopeEvent returnStr:" + returnStr);
            webView.evaluateJavascript("javascript:getAppInfo(" + returnStr + ")", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    Logger.getInstance().debug(TAG, "offLineRedEnvelopeEvent returnStr value:" + value);
                }
            });
        }
    }
    //调用h5方法把app的计价方式传入
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getPricingMethod(BizEvent.getPricingMethodEvent event) {
        if (webView != null) {
            String returnStr = PricingMethodUtil.getPricingSelectType();
            Logger.getInstance().debug(TAG, "getPricingMethod returnStr:" + returnStr);
            webView.evaluateJavascript("javascript:getPricingMethodVue('" + returnStr + "')", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    Logger.getInstance().debug(TAG, "getPricingMethod returnStr value:" + value);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            // Uri result = (((data == null) || (resultCode != RESULT_OK)) ? null : data.getData());
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, intent);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        } else {
            //这里uploadMessage跟uploadMessageAboveL在不同系统版本下分别持有了
            //WebView对象，在用户取消文件选择器的情况下，需给onReceiveValue传null返回值
            //否则WebView在未收到返回值的情况下，无法进行任何操作，文件选择器会失效
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            } else if (uploadMessageAboveL != null) {
                uploadMessageAboveL.onReceiveValue(null);
                uploadMessageAboveL = null;
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }


    private boolean canGoBack() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private void exit() {
        if (canGoBack()) {
            return;
        }
        finish();
    }

    @Override
    public void finish() {
        if (progressbar != null) {
            progressbar.setVisibility(View.GONE);
        }
        super.finish();
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
        if (webView != null) {
            webView.onResume();
            if (isBdb && isBdbJump) {
                isBdbJump = false;
                setToken(UserInfoManager.getToken());
                url = getUrl(url);
                header.put(SSLHelper.getHeadKey(), SSLHelper.getHeadValue(11, url));
                webView.loadUrl(url, header);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }

    private void log(String url, boolean finish) {
        Logger.getInstance().debug("WebViewActivity", "url: " + url + " finish: " + finish + " currTime: " + System.currentTimeMillis());
    }
    private void openImageChooserActivity(String acceptType) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");//图片上传
        if(!TextUtils.isEmpty(acceptType)&&acceptType.contains("image")){
            i.setType("file/*");//文件上传
        }
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }


    public class MyWebChromeClient extends WebChromeClient{
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
            if (progressbar != null) {
                if (newProgress == 100) {
                    progressbar.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    progressbar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    progressbar.setProgress(newProgress);//设置进度值
                }
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            //设置标题为h5的，因标题太长，暂时不设置
            if (useH5Title && title_tv != null) {
                title_tv.setText(title);
            }
        }

        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> valueCallback) {
            uploadMessage = valueCallback;
            openImageChooserActivity("");
        }

        // For Android  >= 3.0
        public void openFileChooser(ValueCallback valueCallback, String acceptType) {
            uploadMessage = valueCallback;
            openImageChooserActivity(acceptType);
        }

        //For Android  >= 4.1
        public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
            uploadMessage = valueCallback;
            openImageChooserActivity(acceptType);
        }

        // For Android >= 5.0
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            Logger.getInstance().debug(TAG,"onShowFileChooser fileChooserParams acceptType:"+GsonUtil.obj2Json(fileChooserParams.getAcceptTypes(),String[].class));
            String acceptType="";
            if(fileChooserParams.getAcceptTypes()!=null&&fileChooserParams.getAcceptTypes().length>0){
                acceptType=fileChooserParams.getAcceptTypes()[0];
            }
            uploadMessageAboveL = filePathCallback;
            openImageChooserActivity(acceptType);
            return true;
        }

    }
}
