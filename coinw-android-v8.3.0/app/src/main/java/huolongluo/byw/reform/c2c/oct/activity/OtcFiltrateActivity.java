package huolongluo.byw.reform.c2c.oct.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import huolongluo.byw.R;
import huolongluo.byw.reform.base.BaseActivity;

/**
 * Created by Administrator on 2019/5/16 0016.
 */

public class OtcFiltrateActivity extends BaseActivity implements View.OnClickListener {

    TextView bank_tv;
    TextView alipay_tv;
    TextView wechat_tv;
    TextView confirm_tv;
    TextView cancle_tv;
    TextView title_tv;
    EditText target_et;

    int patType;


    boolean bankStatus;
    boolean alipayStatus;
    boolean wechatStatus;

    ImageButton back_iv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_otcfiltrate1);
        bank_tv = fv(R.id.bank_tv);
        alipay_tv = fv(R.id.alipay_tv);
        wechat_tv = fv(R.id.wechat_tv);
        confirm_tv = fv(R.id.confirm_tv);
        cancle_tv = fv(R.id.cancle_tv);
        target_et = fv(R.id.target_et);
        back_iv = fv(R.id.back_iv);
        title_tv = fv(R.id.title_tv);


        title_tv.setText(R.string.xx5);
        bank_tv.setOnClickListener(this);
        alipay_tv.setOnClickListener(this);
        wechat_tv.setOnClickListener(this);

        viewClick(back_iv, v -> {

            finish();

        });


        viewClick(confirm_tv, v -> {


            Intent intent = new Intent();

            intent.putExtra("moneyNum", target_et.getText().toString());
            intent.putExtra("bankStatus", bankStatus);
            intent.putExtra("alipayStatus", alipayStatus);
            intent.putExtra("wechatStatus", wechatStatus);
            setResult(104, intent);
            finish();


        });

        viewClick(cancle_tv, v -> {

           /* target_et.setText("");
            bankStatus = true;
            alipayStatus = true;
            wechatStatus = true;

            serView(R.id.bank_tv);
            serView(R.id.alipay_tv);
            serView(R.id.wechat_tv);*/

            Intent intent = new Intent();

            intent.putExtra("moneyNum", "");
            intent.putExtra("bankStatus", false);
            intent.putExtra("alipayStatus", false);
            intent.putExtra("wechatStatus", false);
            setResult(104, intent);
            finish();


        });

    }

    @Override
    public void onClick(View v) {
        serView(v.getId());
    }

    void serView(int position) {


        switch (position) {

            case R.id.bank_tv:

                if (!bankStatus) {
                    bankStatus = true;
                    bank_tv.setBackgroundResource(R.drawable.otcfiltrare_bg1);
                    bank_tv.setTextColor(getResources().getColor(R.color.ff68628A));
                } else {
                    bankStatus = false;
                    bank_tv.setBackgroundResource(R.drawable.otcfiltrare_bg2);
                    bank_tv.setTextColor(getResources().getColor(R.color.ff6D6A7C));
                }
                break;

            case R.id.alipay_tv:
                if (!alipayStatus) {
                    alipayStatus = true;
                    alipay_tv.setBackgroundResource(R.drawable.otcfiltrare_bg1);
                    alipay_tv.setTextColor(getResources().getColor(R.color.ff68628A));
                } else {
                    alipayStatus = false;
                    alipay_tv.setBackgroundResource(R.drawable.otcfiltrare_bg2);
                    alipay_tv.setTextColor(getResources().getColor(R.color.ff6D6A7C));
                }
                break;

            case R.id.wechat_tv:
                if (!wechatStatus) {
                    wechatStatus = true;
                    wechat_tv.setBackgroundResource(R.drawable.otcfiltrare_bg1);
                    wechat_tv.setTextColor(getResources().getColor(R.color.ff68628A));
                } else {
                    wechatStatus = false;
                    wechat_tv.setBackgroundResource(R.drawable.otcfiltrare_bg2);
                    wechat_tv.setTextColor(getResources().getColor(R.color.ff6D6A7C));
                }
                break;


        }


    }


}
