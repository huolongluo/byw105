package huolongluo.byw.reform.mine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import huolongluo.byw.R;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.SPUtils;

/**
 * Created by Administrator on 2019/1/22 0022.
 */

public class RebateActivity extends BaseActivity {


    TextView title_tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rebate);

        title_tv = findViewById(R.id.title_tv);
        title_tv.setText(getResources().getString(R.string.b3));


        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//一键成为经纪人

                if (UserInfoManager.isLogin()) {
                    SPUtils.saveBoolean(RebateActivity.this, SPUtils.FIST_OPEN_broker, true);
                    startActivity(new Intent(RebateActivity.this, PyramidActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(RebateActivity.this, LoginActivity.class));
                }

            }
        });


    }


}
