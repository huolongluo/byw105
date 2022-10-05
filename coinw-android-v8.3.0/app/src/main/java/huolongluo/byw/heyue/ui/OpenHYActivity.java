package huolongluo.byw.heyue.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.blankj.utilcodes.utils.ToastUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import huolongluo.byw.R;
import huolongluo.byw.heyue.HeYueUtil;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.StringUtil;
/**
 * 项目名称：my_byw
 * 包名：huolongluo.byw.heyue.ui
 * 开发者：Long
 * 电话：15375447216
 * 邮箱：127124zhao@gmail.com
 * 日期：2019-11-19
 * 开通合约界面
 */
public class OpenHYActivity extends BaseActivity {

    @BindView(R.id.back_iv)
    ImageButton backIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.right_iv)
    ImageView rightIv;
    @BindView(R.id.title)
    LinearLayout title;

    @BindView(R.id.open_hy_check)
    CheckBox openHyCheck;
    @BindView(R.id.open_hy_cancel)
    TextView openHyCancel;
    @BindView(R.id.open_hy_ok)
    TextView openHyOk;
    @BindView(R.id.xieyi_content)
    TextView xieyiContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_hy);
        ButterKnife.bind(this);
        titleTv.setText(getResources().getString(R.string.hy_open_title));
        Bundle extras = getIntent().getExtras();
        //设置协议长文本可拖动
        xieyiContent.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @OnClick({R.id.back_iv, R.id.open_hy_cancel, R.id.open_hy_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.open_hy_cancel:
                finish();
//                HeYueUtil.getInstance().setTokenToSdk();//设置老虎云登录信息
                break;
            case R.id.open_hy_ok:
                if (openHyCheck == null) {
                    return;
                }
                if (!openHyCheck.isChecked()) {
                    ToastUtils.showShortToast(getString(R.string.warn_des_check));
                } else {
                    String loginToken = UserInfoManager.getToken();
                    if (!TextUtils.isEmpty(loginToken)) {
                        HeYueUtil.getInstance().openHeYueAccount(this, loginToken);
                    }
                }
                break;
        }
    }
}
