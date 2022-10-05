package huolongluo.byw.byw.ui.activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.ImmersedStatusbarUtils;
import rx.functions.Func1;
/**
 * Created by Administrator on 2018/1/30.
 */
public class AboutUsActivity extends BaseActivity {
    @BindView(R.id.iv_left)
    ImageView iv_left;
    @BindView(R.id.toolbar_center_title)
    TextView toolbar_center_title;
    @BindView(R.id.my_toolbar)
    Toolbar my_toolbar;
    @BindView(R.id.lin1)
    RelativeLayout lin1;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_about_us;
    }

    @Override
    protected void injectDagger() {
    }

    private void initToolBar() {
        lin1.setVisibility(View.VISIBLE);
        ImmersedStatusbarUtils.initAfterSetContentView(this, lin1);
//        my_toolbar.setTitle("");
        toolbar_center_title.setText(getString(R.string.m1));
        iv_left.setVisibility(View.VISIBLE);
        iv_left.setImageResource(R.mipmap.back);
        setSupportActionBar(my_toolbar);
    }

    @Override
    protected void initViewsAndEvents() {
        initToolBar();
        eventClick(iv_left).onErrorReturn(throwable -> {
            Logger.getInstance().errorLog(throwable);
            return null;
        }).subscribe(o -> close(), throwable -> {
        });
    }
}
