package com.android.legend.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.legend.common.base.ThemeActivity;
import com.legend.common.util.StatusBarUtils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import huolongluo.byw.R;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.home.activity.NewsWebviewActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.bywx.StatusHolder;
import huolongluo.bywx.utils.AppUtils;

public abstract class BaseActivity extends ThemeActivity {
    protected String TAG = this.getClass().getName();
    protected Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏背景及字体颜色
        StatusBarUtils.setStatusBar(this);
        if (getContentViewId() != 0) {
            setContentView(getContentViewId());
        }
        toolbar=findViewById(R.id.toolbar);
        if(toolbar!=null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setNavigationOnClickListener((v) -> {
                // 退出当前页面
                finish();
            });
            toolbar.setTitle(initAnimationTitle());
            TextView tvTitle=findViewById(R.id.tvTitle);
            if(tvTitle!=null){
                tvTitle.setText(initTitle());
            }
            if(!TextUtils.isEmpty(initRightText())){
                TextView tvRight=findViewById(R.id.tvRight);
                tvRight.setVisibility(View.VISIBLE);
                tvRight.setText(initRightText());
                tvRight.setOnClickListener(initRightTextClickListener());
                if(initRightTextColor()!=0){
                    tvRight.setTextColor(ContextCompat.getColor(this,initRightTextColor()));
                }
            }
        }
        initView();
        initData();
        initObserve();
        if(isRegisterEventBus()){
            if(!EventBus.getDefault().isRegistered(this)){
                EventBus.getDefault().register(this);
            }
        }
        //通用处理方法
        if (StatusHolder.getInstance().isKill()) {
            Logger.getInstance().error("app was kill");
            AppUtils.restart(this);
            return;
        } else {
            Logger.getInstance().debug("app was normal");
        }
    }
    //初始化居中文本的title
    protected String initTitle(){return "";}
    //初始化带动画文本的title
    protected String initAnimationTitle(){return "";}
    protected abstract int getContentViewId();
    protected abstract void initView();
    protected abstract void initData();
    protected abstract void initObserve();
    //toolbar最右边的文本，默认不显示
    protected String initRightText(){return "";}
    protected int initRightTextColor(){return 0;}
    protected View.OnClickListener initRightTextClickListener(){return null;}
    protected boolean isRegisterEventBus(){return false;}

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isRegisterEventBus()){
            if(EventBus.getDefault().isRegistered(this)){
                EventBus.getDefault().unregister(this);
            }
        }
    }
    public void gotoH5(Context context, String url, String title) {
        Intent intent = new Intent(context, NewsWebviewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("token", UserInfoManager.getToken());
        if (TextUtils.isEmpty(title)) {
            intent.putExtra("hideTitle", true);
        } else {
            intent.putExtra("title", title);
        }
        context.startActivity(intent);
    }
}
