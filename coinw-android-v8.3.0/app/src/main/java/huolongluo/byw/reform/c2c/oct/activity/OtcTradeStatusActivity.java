package huolongluo.byw.reform.c2c.oct.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import huolongluo.byw.R;
import huolongluo.byw.reform.base.BaseActivity;

/**
 * Created by Administrator on 2019/3/14 0014.
 */

public class OtcTradeStatusActivity extends BaseActivity implements View.OnClickListener{

    ImageButton back_iv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otctradestatus);
        back_iv=fv(R.id.back_iv);
        back_iv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {

            case R.id.back_iv:
                finish();
                break;

        }
    }
}
