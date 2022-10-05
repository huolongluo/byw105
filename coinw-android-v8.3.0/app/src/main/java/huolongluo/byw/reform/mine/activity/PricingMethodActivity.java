package huolongluo.byw.reform.mine.activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import huolongluo.byw.R;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.util.pricing.PricingMethodUtil;

/**
 * 计价方式
 */
public class PricingMethodActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "PricingMethodActivity";

    private TextView title_tv;
    private RelativeLayout rltRmb;
    private RelativeLayout rltDollar;
    private RelativeLayout rltWon;
    private ImageView ivRmb;
    private ImageView ivDollar;
    private ImageView ivWon;
    private ImageView back_iv;
    private long lastTimeMills = System.currentTimeMillis();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pricing_method);
        rltRmb = findViewById(R.id.rlt_rmb);
        rltDollar = findViewById(R.id.rlt_dollar);
        rltWon = findViewById(R.id.rlt_won);
        rltRmb.setOnClickListener(this);
        rltDollar.setOnClickListener(this);
        rltWon.setOnClickListener(this);
        title_tv = findViewById(R.id.title_tv);
        ivRmb = findViewById(R.id.iv_rmb);
        ivDollar = findViewById(R.id.iv_dollar);
        ivWon = findViewById(R.id.iv_won);
        title_tv.setText(getString(R.string.pricing_method));
        back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(view -> finish());
        initChooseView();
    }

    private void initChooseView() {
        if (PricingMethodUtil.getPricingMethod()==PricingMethodUtil.PRICING_RMB) {
            ivRmb.setVisibility(View.VISIBLE);
            ivDollar.setVisibility(View.GONE);
            ivWon.setVisibility(View.GONE);
        } else if(PricingMethodUtil.getPricingMethod()==PricingMethodUtil.PRICING_DOLLAR){
            ivRmb.setVisibility(View.GONE);
            ivDollar.setVisibility(View.VISIBLE);
            ivWon.setVisibility(View.GONE);
        }
        else if(PricingMethodUtil.getPricingMethod()==PricingMethodUtil.PRICING_WON){
            ivRmb.setVisibility(View.GONE);
            ivDollar.setVisibility(View.GONE);
            ivWon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        //控制快速点击
        long currTimeMills = System.currentTimeMillis();
        if (currTimeMills - lastTimeMills < 800L) {
            return;
        }
        switch (v.getId()) {
            case R.id.rlt_rmb:
                PricingMethodUtil.setPricingMethod(PricingMethodUtil.PRICING_RMB);
                setResult(RESULT_OK);
                break;
            case R.id.rlt_dollar:
                PricingMethodUtil.setPricingMethod(PricingMethodUtil.PRICING_DOLLAR);
                setResult(RESULT_OK);
                break;
            case R.id.rlt_won:
                PricingMethodUtil.setPricingMethod(PricingMethodUtil.PRICING_WON);
                setResult(RESULT_OK);
                break;

        }

        finish();
    }
}
