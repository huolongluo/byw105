package huolongluo.byw.byw.ui.activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.ChromeClientCallbackManager;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.ImmersedStatusbarUtils;
import huolongluo.bywx.helper.AppHelper;
/**
 * Created by 火龙裸 on 2017/12/25.
 */
public class AdvertActivity extends BaseActivity {
    @BindView(R.id.iv_left)
    ImageView iv_left;
    @BindView(R.id.toolbar_center_title)
    TextView toolbar_center_title;
    @BindView(R.id.my_toolbar)
    Toolbar my_toolbar;
    @BindView(R.id.lin1)
    RelativeLayout lin1;
    @BindView(R.id.container)
    LinearLayout container;
    private String title;
    private String id;
    protected AgentWeb mAgentWeb;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_advert;
    }

    @Override
    protected void injectDagger() {
        activityComponent().inject(this);
    }

    private void initToolBar() {
        lin1.setVisibility(View.VISIBLE);
        ImmersedStatusbarUtils.initAfterSetContentView(this, lin1);
//        my_toolbar.setTitle("");
        toolbar_center_title.setText(title);
        iv_left.setVisibility(View.VISIBLE);
        iv_left.setImageResource(R.mipmap.back);
        setSupportActionBar(my_toolbar);
    }

    @Override
    protected void initViewsAndEvents() {
        if (getBundle() != null) {
            title = getBundle().getString("title");
            id = getBundle().getString("id");
        }
        initToolBar();
        eventClick(iv_left).subscribe(o -> close(), throwable -> {
            Logger.getInstance().error(throwable);
        });
        mAgentWeb = AgentWeb.with(this).setAgentWebParent(container, new LinearLayout.LayoutParams(-1, -1))
                // .LayoutParams ,第一个参数和第二个参数应该对应。
                .useDefaultIndicator()// 使用默认进度条
                .defaultProgressBarColor() // 使用默认进度条颜色
                .setWebChromeClient(mWebChromeClient).setWebViewClient(mWebViewClient).setReceivedTitleCallback(mCallback) //设置 Web 页面的 title 回调
                .createAgentWeb()//
                .ready().go(id);
//                .ready().go(Share.get().getBaseUrl() + "app/article.html?id=" + id);
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //do you  work
            AppHelper.setSafeBrowsingEnabled(view);
            //        //支持javascript
            view.getSettings().setJavaScriptEnabled(true);
            // 设置可以支持缩放
            view.getSettings().setSupportZoom(true);
            // 设置出现缩放工具
            view.getSettings().setBuiltInZoomControls(true);
            //扩大比例的缩放
            view.getSettings().setUseWideViewPort(true);
            //自适应屏幕
            view.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            view.getSettings().setLoadWithOverviewMode(true);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // android 5.0以上默认不支持Mixed Content
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
            }
            handler.proceed();
        }
    };
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //do you work
//            Log.i("Info","onProgress:"+newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }
    };
    private ChromeClientCallbackManager.ReceivedTitleCallback mCallback = new ChromeClientCallbackManager.ReceivedTitleCallback() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
        }
    };

    @Override
    protected void onDestroy() {
        AppHelper.distoryWebView(mAgentWeb);
        super.onDestroy();
    }
}
