package huolongluo.byw.byw.ui.activity.renzheng;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.legend.common.util.StatusBarUtils;

import huolongluo.byw.R;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.util.Util;
public class AuthFailureActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏背景及字体颜色
        StatusBarUtils.setStatusBar(this);
        setContentView(com.liuzhongjun.videorecorddemo.R.layout.auth_failure_activity);
        initView();
    }

    private void initView() {
        TextView title_tv = findViewById(com.liuzhongjun.videorecorddemo.R.id.title_tv);
        title_tv.setText(R.string.camera_b2);
        findViewById(com.liuzhongjun.videorecorddemo.R.id.back_iv).setOnClickListener(view -> finish());
        findViewById(R.id.toHome).setOnClickListener(view -> {
            toTransfer(1);
            finish();
        });
        findViewById(R.id.toAuth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.gotoSeniorCertified(AuthFailureActivity.this,getIntent().getIntExtra("nationality",1));
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    /**
     * type  =1 为买币交易  2为币币
     *
     * @param type
     */
    private void toTransfer(int type) {
        Intent intent = new Intent(AuthFailureActivity.this, MainActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("from", "AuthFailureActivity");
        startActivity(intent);
    }

    public void startActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

}
