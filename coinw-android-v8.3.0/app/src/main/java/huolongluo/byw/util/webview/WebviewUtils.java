package huolongluo.byw.util.webview;

import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

import huolongluo.bywx.helper.AppHelper;
public class WebviewUtils {
    /**
     * 统一的webview setting处理
     * @return
     */
    public static void getWebViewSetSettings(WebView webView){
        AppHelper.setSafeBrowsingEnabled(webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webView.getSettings().setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webView.getSettings().setDisplayZoomControls(true); //隐藏原生的缩放控件
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
        webView.getSettings().setBlockNetworkImage(true);

    }
}
