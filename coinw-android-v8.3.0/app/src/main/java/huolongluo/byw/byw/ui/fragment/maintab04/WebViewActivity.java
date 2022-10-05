package huolongluo.byw.byw.ui.fragment.maintab04;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.legend.common.util.StatusBarUtils;

import java.io.File;

import huolongluo.byw.R;
import huolongluo.byw.util.LogicLanguage;
import huolongluo.byw.util.WebViewSSLErrorUtil;
import huolongluo.byw.util.config.ConfigurationUtils;
import huolongluo.bywx.helper.AppHelper;

/**
 * Created by LS on 2018/7/24.
 */
public class WebViewActivity extends Activity {

    //    @BindView(R.id.webView)
    WebView webView;
    private ImageButton back_iv;
    private TextView title_tv;
    private ProgressBar progressbar;
    OpenFileWebChromeClient mOpenFileWebChromeClient;
    private String id = "39519";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏背景及字体颜色
        StatusBarUtils.setStatusBar(this);
        setContentView(R.layout.activity_webview);
        progressbar = findViewById(R.id.progressbar);
        progressbar.setProgress(30);
        title_tv = findViewById(R.id.title_tv);
        title_tv.setText(R.string.online_service);
        webView = findViewById(R.id.webView);
        back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setWebviewAttr();
        mOpenFileWebChromeClient = new OpenFileWebChromeClient(WebViewActivity.this);
        webView.setWebChromeClient(mOpenFileWebChromeClient);
        ConfigurationUtils.resetLanguage(this);
    }
    /**
     * bind layout resource file
     */
//    @Override
//    protected int getContentViewId() {
////        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        return R.layout.activity_webview;
//    }
    /**
     * Dagger2 use in your application module(not used in 'base' module)
     */
//    @Override
//    protected void injectDagger() {
//
//    }
    /**
     * init views and events here
     */
//    @Override
//    protected void initViewsAndEvents() {
//
//        initToolBar();
//        eventClick(iv_left).subscribe( o ->{
//            finish();
//        });
//        webView = findViewById(R.id.webView);
//
//        setWebviewAttr();
//        mOpenFileWebChromeClient = new OpenFileWebChromeClient(WebViewActivity.this);
//
//        webView.setWebChromeClient(mOpenFileWebChromeClient);
//
//
//    }
//
//    private void initToolBar()
//    {
//        lin1.setVisibility(View.VISIBLE);
//        ImmersedStatusbarUtils.initAfterSetContentView(this, lin1);
//
////        my_toolbar.setTitle("");
//        toolbar_center_title.setText("在线客服");
//        iv_left.setVisibility(View.VISIBLE);
//        iv_left.setImageResource(R.mipmap.back);
//        setSupportActionBar(my_toolbar);
//    }
    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient = new WebChromeClient() {
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton(R.string.dd95, null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();
            //注意:
            //必须要这一句代码:result.confirm()表示:
            //处理结果为确定状态同时唤醒WebCore线程
            //否则不能继续点击按钮
            result.confirm();
            return true;
        }

        //获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            Log.i("ansen", "网页标题:" + title);
        }

        //加载进度回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
//            progressBar.setProgress(newProgress);
        }
    };

    private void setWebviewAttr() {
        int scale = getApplicationContext().getResources().getDisplayMetrics().densityDpi;
        WebSettings webSettings = webView.getSettings();
        AppHelper.setSafeBrowsingEnabled(webView);
        // 设置编码
        webView.getSettings().setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setJavaScriptEnabled(true);// 为WebView使能JavaScript
        //点击超链接的时候重新在原来的进程上加载URL
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // android 5.0以上默认不支持Mixed Content
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
                }
                WebViewSSLErrorUtil.showDialogSSLError(handler, WebViewActivity.this);
               // handler.proceed();
            }
        });

        /*
         * 在线客服，固定写死这三个链接，
         * 不管登录没登录
         * 中文:https://coinw.udesk.cn/im_client/?web_plugin_id=99712&group_id=133212&language=zh-cn
         * 英文:https://coinw.udesk.cn/im_client/?web_plugin_id=99722&group_id=133042&language=en-us
         * 韩文:https://coinw.udesk.cn/im_client/?web_plugin_id=99732&group_id=133202&language=ko
         */

        if (LogicLanguage.getLanguage(this).contains("zh")) {
            webView.loadUrl("https://coinw.udesk.cn/im_client/?web_plugin_id=99712&group_id=133212&language=zh-cn");
        } else if (LogicLanguage.getLanguage(this).contains("en")) {
            webView.loadUrl("https://coinw.udesk.cn/im_client/?web_plugin_id=99722&group_id=133042&language=en-us");
        } else if (LogicLanguage.getLanguage(this).contains("ko")) {
            webView.loadUrl("https://coinw.udesk.cn/im_client/?web_plugin_id=99732&group_id=133202&language=ko");
        }
    }

    public class OpenFileWebChromeClient extends WebChromeClient {

        public static final int REQUEST_FILE_PICKER = 1;
        public ValueCallback<Uri> mFilePathCallback;
        public ValueCallback<Uri[]> mFilePathCallbacks;
        Activity mContext;

        public OpenFileWebChromeClient(Activity mContext) {
            super();
            this.mContext = mContext;
        }

        // Android < 3.0 调用这个方法
        public void openFileChooser(ValueCallback<Uri> filePathCallback) {
            mFilePathCallback = filePathCallback;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "File Chooser"),
                    REQUEST_FILE_PICKER);
        }

        // 3.0 + 调用这个方法
        public void openFileChooser(ValueCallback filePathCallback,
                                    String acceptType) {
            mFilePathCallback = filePathCallback;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "File Chooser"),
                    REQUEST_FILE_PICKER);
        }

        //  / js上传文件的<input type="file" name="fileField" id="fileField" />事件捕获
        // Android > 4.1.1 调用这个方法
        public void openFileChooser(ValueCallback<Uri> filePathCallback,
                                    String acceptType, String capture) {
            mFilePathCallback = filePathCallback;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "File Chooser"),
                    REQUEST_FILE_PICKER);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progressbar.setProgress(newProgress);
        }

        @Override
        public boolean onShowFileChooser(WebView webView,
                                         ValueCallback<Uri[]> filePathCallback,
                                         WebChromeClient.FileChooserParams fileChooserParams) {
            mFilePathCallbacks = filePathCallback;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "File Chooser"),
                    REQUEST_FILE_PICKER);
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //java.lang.NullPointerException
        //	at java.io.File.<init>(File.java:283)
        //	at huolongluo.byw.byw.ui.fragment.maintab04.WebViewActivity.onActivityResult(WebViewActivity.java:12)
        if (mOpenFileWebChromeClient == null) {
            return;
        }
        if (requestCode == OpenFileWebChromeClient.REQUEST_FILE_PICKER) {
            if (mOpenFileWebChromeClient.mFilePathCallback != null) {
                Uri result = intent == null || resultCode != Activity.RESULT_OK ? null
                        : intent.getData();
                if (result != null) {
                    String path = MediaUtility.getPath(getApplicationContext(),
                            result);
                    if (!TextUtils.isEmpty(path)) {
                        Uri uri = Uri.fromFile(new File(path));
                        mOpenFileWebChromeClient.mFilePathCallback
                                .onReceiveValue(uri);
                    } else {
                        mOpenFileWebChromeClient.mFilePathCallback
                                .onReceiveValue(null);
                    }
                } else {
                    mOpenFileWebChromeClient.mFilePathCallback
                            .onReceiveValue(null);
                }
            }
            if (mOpenFileWebChromeClient.mFilePathCallbacks != null) {
                Uri result = intent == null || resultCode != Activity.RESULT_OK ? null
                        : intent.getData();
                if (result != null) {
                    String path = MediaUtility.getPath(getApplicationContext(),
                            result);
                    if (!TextUtils.isEmpty(path)) {
                        Uri uri = Uri.fromFile(new File(path));
                        mOpenFileWebChromeClient.mFilePathCallbacks
                                .onReceiveValue(new Uri[]{uri});
                    } else {
                        mOpenFileWebChromeClient.mFilePathCallbacks
                                .onReceiveValue(null);
                    }
                } else {
                    mOpenFileWebChromeClient.mFilePathCallbacks
                            .onReceiveValue(null);
                }
            }
            mOpenFileWebChromeClient.mFilePathCallback = null;
            mOpenFileWebChromeClient.mFilePathCallbacks = null;
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            AppHelper.distoryWebView(webView);
        }
    }
}
