package huolongluo.byw.reform.c2c.oct.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import huolongluo.byw.R;
import huolongluo.byw.reform.base.BaseActivity;

/**
 * Created by hy on 2018/11/7 0007.
 */

public class C2cSellSuccessActivity extends BaseActivity {

     private TextView total_tv;
     private TextView serview_tv;
     private TextView real_tv;
     private TextView bank_tv;
     private TextView tv_sure;

      private ImageButton back_iv;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
               setContentView(R.layout.activity_c2c_cellsuccess);
        total_tv=fv(R.id.total_tv);
        serview_tv=fv(R.id.serview_tv);
        real_tv=fv(R.id.real_tv);
        bank_tv=fv(R.id.bank_tv);
        tv_sure=fv(R.id.tv_sure);
        back_iv=fv(R.id.back_iv);
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

/*
        intent.putExtra("total", et_withdrawBalance.getText().toString());
        intent.putExtra("fee", service_tv.getText().toString());
        intent.putExtra("bankCard", et_bankcard.getText().toString());*/


        double total=Double.parseDouble(getIntent().getStringExtra("total"));
        double fee=Double.parseDouble(getIntent().getStringExtra("fee"));
        String bankCard=getIntent().getStringExtra("bankCard");

        double real=total-fee;

        total_tv.setText("¥"+getIntent().getStringExtra("total")+"");
        serview_tv.setText("¥"+getIntent().getStringExtra("fee")+"");
        real_tv.setText("¥"+real+"");
        bank_tv.setText(bankCard);
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });






    }
}
