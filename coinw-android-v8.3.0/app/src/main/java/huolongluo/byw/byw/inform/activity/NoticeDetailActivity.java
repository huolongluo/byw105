package huolongluo.byw.byw.inform.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.inform.bean.FarticleBean;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.domain.DomainUtil;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.helper.AppHelper;
import okhttp3.Request;

/**
 * Created by hy on 2018/9/17 0017.
 */
public class NoticeDetailActivity extends BaseActivity {

    private int id;
    private TextView title_tv1;
    private TextView time0_tv;
    private TextView time1_tv;
    private TextView next_tv;
    private TextView next1_tv;
    private LinearLayout next_rl;
    private WebView webView;
    private ImageView iv_left;
    private LinearLayout share_ll;
    private FrameLayout net_error_view;
    private FarticleBean currentBean;
    private FarticleBean nextBean;
    private FarticleBean previousBean;
    private TextView title_tv;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_noticedetail;
    }

    @Override
    protected void injectDagger() {
    }

    @Override
    protected void initViewsAndEvents() {
        net_error_view = findViewById(R.id.net_error_view);
        net_error_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                farticleById(id);
            }
        });
        share_ll = findViewById(R.id.share_ll);
        title_tv = findViewById(R.id.title_tv);
        title_tv.setText(getString(R.string.notice));
        share_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   Intent intent = new Intent(Intent.ACTION_SEND);
              //  intent.setType("text/plain");
              //  Uri uri= Uri.parse("https://www.coinw.me/service/article.html?id=159");
              //  intent.setData(uri);
                  intent.setType("text/plain");

               // intent.putExtra(Intent.EXTRA_TEXT, "https://www.coinw.me/");
                intent.putExtra(Intent.EXTRA_TEXT, "https://www.coinw.me/service/article.html?id=159");

                startActivity(Intent.createChooser(intent, "对话框标题"));*/
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.artboard);
                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bmp, null, null));
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                //   intent.putExtra(Intent.EXTRA_TEXT, "分享");
                //   intent.putExtra(Intent.EXTRA_TEXT, "wwwwwwwwwwwwwwwwwwww");
                intent.setType("image/*");
                //   intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, DomainUtil.INSTANCE.getWebUrlSuffix()));
                //  startActivity(intent);
            }
        });
        iv_left = findViewById(R.id.back_iv);
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView = findViewById(R.id.webView);
        webView.setScrollContainer(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setBackgroundColor(0); // 设置背景色
        webView.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255
      /*  webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });*/
        title_tv1 = findViewById(R.id.title_tv1);
        time0_tv = findViewById(R.id.time0_tv);
        time1_tv = findViewById(R.id.time1_tv);
        next_tv = findViewById(R.id.next_tv);
        next1_tv = findViewById(R.id.next1_tv);
        next_rl = findViewById(R.id.next_rl);
        id = getIntent().getIntExtra("id", 0);
        // CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().put("tradingArea", result);
        if (id != 0) {
            String s = SPUtils.getString(this, "notice", null);
            if (s != null) {
                if (!s.contains("*" + id + "*")) {
                    StringBuilder builder = new StringBuilder(s);
                    builder.append(id);
                    SPUtils.saveString(this, "notice", "*" + builder.toString() + "*");
                }
            } else {
                SPUtils.saveString(this, "notice", "*" + id + "*");
            }
        }
        showProgress(getString(R.string.loading));
        farticleById(id);
        next_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previousBean != null && previousBean.getFid() != 0) {
                    showProgress(getString(R.string.loading));
                    farticleById(previousBean.getFid());
                    String s = SPUtils.getString(NoticeDetailActivity.this, "notice", null);
                    if (s != null) {
                        if (!s.contains("*" + previousBean.getFid() + "*")) {
                            StringBuilder builder = new StringBuilder(s);
                            builder.append(previousBean.getFid());
                            SPUtils.saveString(NoticeDetailActivity.this, "notice", "*" + builder.toString() + "*");
                        }
                    } else {
                        SPUtils.saveString(NoticeDetailActivity.this, "notice", "*" + nextBean.getFid() + "*");
                    }
                } else {
                    MToast.show(NoticeDetailActivity.this, getString(R.string.lastw), 1);
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void farticleById(int idd) {
        String body = null;
        RSACipher rsaCipher = new RSACipher();
        String id = URLEncoder.encode(idd + "");
        body = "id=" + id;
        try {
            body = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            // body=URLEncoder.encode(body);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("body", body);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.FAR_TICLE_BY_ID, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                // MToast.show(NoticeDetailActivity.this,"网络请求超时，请稍后重试",2);
                SnackBarUtils.ShowRed(NoticeDetailActivity.this, getString(R.string.net_timeout1));
                hideProgress();
                net_error_view.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void requestSuccess(String result) {
                hideProgress();
                if (net_error_view.getVisibility() != View.GONE) {
                    net_error_view.setVisibility(View.GONE);
                }
                try {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    int code = jsonObject.getIntValue("code");
                    String value = jsonObject.getString("value");
                    if (code == 0) {
                        currentBean = jsonObject.getObject("farticle", FarticleBean.class);
                        nextBean = jsonObject.getObject("latterOne", FarticleBean.class);
                        previousBean = jsonObject.getObject("previousOne", FarticleBean.class);
                        if (currentBean != null) {
                            title_tv1.setText(currentBean.getFtitle() + "");
                            time0_tv.setText(currentBean.getFcreateTime() + "");
                            time1_tv.setText(currentBean.getFcreateTime() + "");
                            AppHelper.setSafeBrowsingEnabled(webView);
                            webView.getSettings().setDefaultTextEncodingName("UTF-8");
                            webView.loadData(currentBean.getFcontent(), "text/html; charset=UTF-8", null);
                        }
                        if (previousBean != null && previousBean.getFid() != 0) {
                            next_tv.setText(previousBean.getFtitle() + "");
                        } else {
                            next_tv.setText(getString(R.string.lastw));
                        }
                    } else {
                        MToast.show(NoticeDetailActivity.this, value + "", 2);
                        net_error_view.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    net_error_view.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        AppHelper.distoryWebView(webView);
        super.onDestroy();
    }
}
