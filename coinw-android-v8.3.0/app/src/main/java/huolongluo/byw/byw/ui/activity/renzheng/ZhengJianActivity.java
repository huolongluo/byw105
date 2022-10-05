package huolongluo.byw.byw.ui.activity.renzheng;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.ImmersedStatusbarUtils;
/**
 * Created by LS on 2018/7/23.
 */
public class ZhengJianActivity extends BaseActivity {
    @BindView(R.id.iv_left)
    ImageView iv_left;
    @BindView(R.id.toolbar_center_title)
    TextView toolbar_center_title;
    @BindView(R.id.my_toolbar)
    Toolbar my_toolbar;
    @BindView(R.id.lin1)
    RelativeLayout lin1;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_name1)
    TextView tv_name1;
    @BindView(R.id.tv_name2)
    TextView tv_name2;
    @BindView(R.id.ll_01)
    LinearLayout ll_01;
    @BindView(R.id.ll_02)
    LinearLayout ll_02;
    @BindView(R.id.ll_03)
    LinearLayout ll_03;

    /**
     * bind layout resource file
     */
    @Override
    protected int getContentViewId() {
        return R.layout.zhengjian_activity;
    }

    /**
     * Dagger2 use in your application module(not used in 'base' module)
     */
    @Override
    protected void injectDagger() {
    }

    /**
     * init views and events here
     */
    @Override
    protected void initViewsAndEvents() {
        initToolBar();

       /* if (RenZhengInfoActivity.countryName.equals("中国")){
            ll_01.setVisibility(View.VISIBLE);
            ll_02.setVisibility(View.VISIBLE);
            ll_03.setVisibility(View.VISIBLE);
        }
        if (RenZhengInfoActivity.countryName.equals("英国")){
            ll_01.setVisibility(View.GONE);
            ll_02.setVisibility(View.VISIBLE);
            ll_03.setVisibility(View.GONE);
        }
        if (RenZhengInfoActivity.countryName.equals("美国")){
            ll_01.setVisibility(View.GONE);
            ll_02.setVisibility(View.VISIBLE);
            ll_03.setVisibility(View.GONE);
        }*/
        ll_01.setVisibility(View.VISIBLE);
        ll_02.setVisibility(View.GONE);
        ll_03.setVisibility(View.GONE);
        eventClick(iv_left).subscribe(o -> {
            finish();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tv_name).subscribe(o -> {
            Intent intent = new Intent();
            intent.putExtra("ZhenJianName", tv_name.getText().toString());
            intent.putExtra("ZhenJianId", "0");
            setResult(RESULT_OK, intent);
            finish();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tv_name1).subscribe(o -> {
            Intent intent = new Intent();
            intent.putExtra("ZhenJianName", tv_name1.getText().toString());
            intent.putExtra("ZhenJianId", "2");
            setResult(RESULT_OK, intent);
            finish();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tv_name2).subscribe(o -> {
            Intent intent = new Intent();
            intent.putExtra("ZhenJianName", tv_name2.getText().toString());
            intent.putExtra("ZhenJianId", "3");
            setResult(RESULT_OK, intent);
            finish();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
    }

    private void initToolBar() {
        lin1.setVisibility(View.VISIBLE);
        //全屏。
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ImmersedStatusbarUtils.initAfterSetContentView(this, lin1); // 沉浸式
//        my_toolbar.setTitle("");
        toolbar_center_title.setText(getString(R.string.select_id_type));
        iv_left.setVisibility(View.VISIBLE);
        iv_left.setImageResource(R.mipmap.back);
        setSupportActionBar(my_toolbar);
    }
}
