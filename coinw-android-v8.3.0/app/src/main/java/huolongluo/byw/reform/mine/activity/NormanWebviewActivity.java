package huolongluo.byw.reform.mine.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
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

import java.io.File;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.ui.fragment.maintab04.MediaUtility;
import huolongluo.byw.byw.ui.fragment.maintab04.WebViewActivity;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.bywx.helper.AppHelper;

/**
 * Created by Administrator on 2019/1/11 0011.
 */
public class NormanWebviewActivity extends BaseActivity {

    @BindView(R.id.webView)
    WebView webView;
    private ImageButton back_iv;
    private TextView title_tv;
    private ProgressBar progressbar;
    OpenFileWebChromeClient mOpenFileWebChromeClient;
    private String id = "39519";
    String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        progressbar = findViewById(R.id.progressbar);
        progressbar.setProgress(30);
        title_tv = findViewById(R.id.title_tv);
        title_tv.setText("");
        webView = findViewById(R.id.webView);
        back_iv = findViewById(R.id.back_iv);
        url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        if (title != null) {
            title_tv.setText(title);
        }
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setWebviewAttr();
        mOpenFileWebChromeClient = new OpenFileWebChromeClient(NormanWebviewActivity.this);
        webView.setWebChromeClient(mOpenFileWebChromeClient);
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
//        toolbar_center_title.setText("????????????");
//        iv_left.setVisibility(View.VISIBLE);
//        iv_left.setImageResource(R.mipmap.back);
//        setSupportActionBar(my_toolbar);
//    }
    //WebChromeClient????????????WebView??????Javascript????????????????????????????????????title??????????????????
    private WebChromeClient webChromeClient = new WebChromeClient() {
        //?????????js???alert???????????????????????????????????????dialog??????
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton(getString(R.string.k3), null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();
            //??????:
            //????????????????????????:result.confirm()??????:
            //???????????????????????????????????????WebCore??????
            //??????????????????????????????
            result.confirm();
            return true;
        }

        //??????????????????
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            title_tv.setText(title);
            Log.i("ansen", "????????????:" + title);
        }

        //??????????????????
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
//            progressBar.setProgress(newProgress);
        }
    };

    private void setWebviewAttr() {
        int scale = getApplicationContext().getResources().getDisplayMetrics().densityDpi;
        AppHelper.setSafeBrowsingEnabled(webView);
        WebSettings webSettings = webView.getSettings();
        // ????????????
        webView.getSettings().setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setJavaScriptEnabled(true);// ???WebView??????JavaScript
        //?????????????????????????????????????????????????????????URL
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
                // android 5.0?????????????????????Mixed Content
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
                }
                handler.proceed();
            }
        });

      /*  if (Share.get().getLogintoken().isEmpty()){
            id = Share.get().getUid();
            webView.loadUrl("https://coinw.udesk.cn/im_client/?web_plugin_id="+id);
        }else {
            id = "39519";
            webView.loadUrl("https://coinw.udesk.cn/im_client/?web_plugin_id="+id);
        }*/
        webView.loadUrl(url);
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

        // Android < 3.0 ??????????????????
        public void openFileChooser(ValueCallback<Uri> filePathCallback) {
            mFilePathCallback = filePathCallback;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "File Chooser"),
                    REQUEST_FILE_PICKER);
        }

        // 3.0 + ??????????????????
        public void openFileChooser(ValueCallback filePathCallback,
                                    String acceptType) {
            mFilePathCallback = filePathCallback;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "File Chooser"),
                    REQUEST_FILE_PICKER);
        }

        //  / js???????????????<input type="file" name="fileField" id="fileField" />????????????
        // Android > 4.1.1 ??????????????????
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
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == WebViewActivity.OpenFileWebChromeClient.REQUEST_FILE_PICKER) {
            if (mOpenFileWebChromeClient.mFilePathCallback != null) {
                Uri result = intent == null || resultCode != Activity.RESULT_OK ? null
                        : intent.getData();
                if (result != null) {
                    String path = MediaUtility.getPath(getApplicationContext(),
                            result);
                    Uri uri = Uri.fromFile(new File(path));
                    mOpenFileWebChromeClient.mFilePathCallback
                            .onReceiveValue(uri);
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
                    Uri uri = Uri.fromFile(new File(path));
                    mOpenFileWebChromeClient.mFilePathCallbacks
                            .onReceiveValue(new Uri[]{uri});
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
    protected void onDestroy() {
        AppHelper.distoryWebView(webView);
        super.onDestroy();
    }
}
