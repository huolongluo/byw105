package com.android.legend.base;

import android.os.Bundle;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;

import huolongluo.byw.R;
/**
 * 登录注册模块的activity基类
 */
public abstract class BaseLoginActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView ivBack=findViewById(R.id.ivBack);
        if(ivBack!=null){
            ivBack.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        TextView tvTitle=findViewById(R.id.tvTitle);
        if(tvTitle!=null){
            TextView tvTips=findViewById(R.id.tvTips);
            LinearLayout llCodeTips=findViewById(R.id.llCodeTips);
            TextView tvCodeTips=findViewById(R.id.tvCodeTips);
            tvTitle.setText(initTitle());
            String tips=initTips();
            if (TextUtils.isEmpty(tips)) {
                tvTips.setVisibility(View.GONE);
            } else {
                tvTips.setVisibility(View.VISIBLE);
            }
            tvTips.setText(tips);
            if(initCodeTips()!=null){
                String codeTips=initCodeTips().toString();
                if (TextUtils.isEmpty(codeTips)) {
                    llCodeTips.setVisibility(View.GONE);
                } else {
                    llCodeTips.setVisibility(View.VISIBLE);
                }
                tvCodeTips.setText(initCodeTips());
            }
        }
    }
    protected String initTips(){return "";}//红色的提示文案
    protected Spanned initCodeTips(){return null;}//验证码的提示文案
}

