package huolongluo.byw.byw.ui.activity.renzheng;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.legend.common.util.StatusBarUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import huolongluo.byw.R;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.util.config.ConfigurationUtils;
import huolongluo.bywx.helper.AppHelper;

import static android.os.Build.VERSION_CODES.M;
/**
 * C
 */
public class WebviewActivity extends Activity {
    private ProgressBar progressbar;
    public static final int INPUT_FILE_REQUEST_CODE = 1;
    public static final int INPUT_VIDEO_CODE = 2;
    private static final int REQUEST_CAMERA_CODE = 1;
    private WebView webview;
    private String url;
    private ValueCallback<Uri[]> mFilePathCallback;
    private ValueCallback<Uri> nFilePathCallback;
    private String mCameraPhotoPath;
    private Uri photoURI;
    private ImageButton back_iv;
    private TextView title_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏背景及字体颜色
        StatusBarUtils.setStatusBar(this);
        setContentView(R.layout.activity_renzhengwebview);
        back_iv = findViewById(R.id.back_iv);
        title_tv = findViewById(R.id.title_tv);
        progressbar = findViewById(R.id.progressbar);
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WebviewActivity.this, MainActivity.class));
                finish();
            }
        });
        title_tv.setText(getString(R.string.identity1));
        initData();
    }

    private void initData() {
        String reqUrl = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(reqUrl)) {
            String data = getIntent().getStringExtra("data");
            // final String mBiz_id = getIntent().getStringExtra("bizid");
            url = "https://api.megvii.com/faceid/lite/do?token=" + data;
        } else {
            url = reqUrl;
        }
        webview = findViewById(R.id.webview);
        ConfigurationUtils.resetLanguage(this);
        WebSettings webSettings = webview.getSettings();
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webview.setScrollbarFadingEnabled(false);
        AppHelper.setSafeBrowsingEnabled(webview);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAllowFileAccess(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            webSettings.setDisplayZoomControls(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressbar.setVisibility(View.GONE);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //java.lang.RuntimeException: Failure delivering result ResultInfo{who=null, request=2, result=-1, data=Intent { flg=0x1 }}
                //to activity {huolongluo.byw/huolongluo.byw.byw.ui.activity.renzheng.WebviewActivity}:
                // java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String android.net.Uri.toString()' on a null object reference
                if (request != null && request.getUrl() != null) {
                    String url = request.getUrl().toString();
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // android 5.0以上默认不支持Mixed Content
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
                }
                handler.proceed();
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressbar.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            //for  Android 4.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                if (nFilePathCallback != null) {
                    nFilePathCallback.onReceiveValue(null);
                }
                nFilePathCallback = uploadMsg;
                if ("image/*".equals(acceptType)) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                            takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                        } catch (IOException ex) {
                            Log.e("TAG", "Unable to create Image File", ex);
                        }
                        if (photoFile != null) {
                            mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        } else {
                            takePictureIntent = null;
                        }
                    }
                    startActivityForResult(takePictureIntent, INPUT_FILE_REQUEST_CODE);
                } else if ("video/*".equals(acceptType)) {
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takeVideoIntent, INPUT_VIDEO_CODE);
                    }
                }
            }

            @SuppressLint("NewApi")
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;
                String[] acceptTypes = fileChooserParams.getAcceptTypes();
                if (acceptTypes[0].equals("image/*")) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                            takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                        } catch (IOException ex) {
                            Log.e("TAG", "Unable to create Image File", ex);
                        }
                        //适配7.0
                        if (Build.VERSION.SDK_INT > M) {
                            if (photoFile != null && photoFile.exists()) {
                                photoURI = FileProvider.getUriForFile(WebviewActivity.this, "huolongluo.byw.fileprovider", photoFile);
                                //  BuildConfig.APPLICATION_ID+".fileprovider", photoFile);
                                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            } else {
                                Toast.makeText(WebviewActivity.this, getString(R.string.file_noex), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (photoFile != null) {
                                mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            } else {
                                takePictureIntent = null;
                            }
                        }
                    }
                    startActivityForResult(takePictureIntent, INPUT_FILE_REQUEST_CODE);
                } else if (acceptTypes[0].equals("video/*")) {
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takeVideoIntent, INPUT_VIDEO_CODE);
                    }
                }
                return true;
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                return true;
            }
        });
        webview.loadUrl(url);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // File storageDir = Environment.getExternalStorageDirectory();
        File image = File.createTempFile(imageFileName, //  前缀
                ".jpg",        //  后缀
                storageDir     //  文件夹
        );
        //  mCameraPhotoPath = image.getAbsolutePath();
        return image;
        //  File file=new File(Environment.getExternalStorageDirectory()+"/20190103_110234.jpg");

      /*  if(!file.exists()){
            file.createNewFile();
        }
        return file;*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != INPUT_FILE_REQUEST_CODE && requestCode != INPUT_VIDEO_CODE) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        Uri[] results = null;
        Uri mUri = null;
        if (resultCode == Activity.RESULT_OK && requestCode == INPUT_FILE_REQUEST_CODE) {
            if (data == null) {
                if (Build.VERSION.SDK_INT > M) {
                    mUri = photoURI;
                    results = new Uri[]{mUri};
                } else {
                    if (mCameraPhotoPath != null) {
                        mUri = Uri.parse(mCameraPhotoPath);
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                }
            } else {
                Uri nUri = data.getData();
                if (nUri != null) {
                    mUri = nUri;
                    results = new Uri[]{nUri};
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == INPUT_VIDEO_CODE) {
            mUri = data.getData();
            results = new Uri[]{mUri};
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            nFilePathCallback.onReceiveValue(mUri);
            nFilePathCallback = null;
        } else {
            if (null == mFilePathCallback) return;
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        } else {
            // finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        AppHelper.distoryWebView(webview);
        super.onDestroy();
    }
}
