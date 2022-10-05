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
/**
 * Created by Administrator on 2018/1/30.
 */
public class ShiMingActivity extends BaseActivity {
    @BindView(R.id.iv_left)
    ImageView iv_left;
    @BindView(R.id.toolbar_center_title)
    TextView toolbar_center_title;
    @BindView(R.id.my_toolbar)
    Toolbar my_toolbar;
    @BindView(R.id.lin1)
    RelativeLayout lin1;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.id)
    TextView id;
    @BindView(R.id.time)
    TextView time;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_shi_ming;
    }

    @Override
    protected void injectDagger() {
    }

    private void initToolBar() {
        lin1.setVisibility(View.VISIBLE);
        ImmersedStatusbarUtils.initAfterSetContentView(this, lin1);
//        my_toolbar.setTitle("");
        toolbar_center_title.setText(getString(R.string.j3));
        iv_left.setVisibility(View.VISIBLE);
        iv_left.setImageResource(R.mipmap.back);
        setSupportActionBar(my_toolbar);
    }

    @Override
    protected void initViewsAndEvents() {
        initToolBar();
        eventClick(iv_left).subscribe(o -> close(), throwable -> {
            Logger.getInstance().error(throwable);
        });
        if (getBundle() != null) {
            String realName = getBundle().getString("realName");
            String identityNo = getBundle().getString("identityNo");
            String postRealValidateTime = getBundle().getString("postRealValidateTime");
            name.setText(String.format(getString(R.string.j5), realName));
            id.setText(String.format(getString(R.string.j6), identityNo));
            time.setText(String.format(getString(R.string.j7), postRealValidateTime));
        }
    }
}
