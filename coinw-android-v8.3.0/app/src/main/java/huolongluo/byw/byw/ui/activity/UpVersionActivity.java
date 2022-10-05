package huolongluo.byw.byw.ui.activity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
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
import huolongluo.byw.util.L;
import huolongluo.bywx.helper.AppHelper;
/**
 * Created by 火龙裸 on 2018/1/9.
 */
public class UpVersionActivity extends BaseActivity {
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
    protected AgentWeb mAgentWeb;
    private String downUrl;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_up_version;
    }

    @Override
    protected void injectDagger() {
        activityComponent().inject(this);
    }

    private void initToolBar() {
        lin1.setVisibility(View.VISIBLE);
        //全屏。
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ImmersedStatusbarUtils.initAfterSetContentView(this, lin1); // 沉浸式
//        my_toolbar.setTitle("");
        toolbar_center_title.setText(getString(R.string.k2));
        iv_left.setVisibility(View.VISIBLE);
        iv_left.setImageResource(R.mipmap.back);
        setSupportActionBar(my_toolbar);
    }

    @Override
    protected void initViewsAndEvents() {
        if (getBundle() != null) {
            downUrl = getBundle().getString("downUrl");
            L.e("===============================WebView请求地址：" + downUrl);
        }
        initToolBar();
        eventClick(iv_left).subscribe(o -> close(), throwable -> {
            Logger.getInstance().error(throwable);
        });
        mAgentWeb = AgentWeb.with(this)//传入Activity or Fragment
                .setAgentWebParent(container, new LinearLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout
                // .LayoutParams ,第一个参数和第二个参数应该对应。
                .useDefaultIndicator()// 使用默认进度条
                .defaultProgressBarColor() // 使用默认进度条颜色
                .setReceivedTitleCallback(mCallback) //设置 Web 页面的 title 回调
                .createAgentWeb()//
                .ready().go(downUrl);
    }

    private ChromeClientCallbackManager.ReceivedTitleCallback mCallback = new ChromeClientCallbackManager.ReceivedTitleCallback() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            Log.d("升级 ", "title= : " + title);
            /**
             *  根据用户点击了网页页面按钮，获取到点击的内容标题title
             * */
        }
    };

    @Override
    protected void onDestroy() {
        AppHelper.distoryWebView(mAgentWeb);
        super.onDestroy();
    }
}
