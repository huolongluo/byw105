package com.allenliu.versionchecklib.v2.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.allenliu.versionchecklib.v2.builder.BuilderManager;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.eventbus.AllenEventType;
import com.allenliu.versionchecklib.v2.eventbus.CommonEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by allenliu on 2018/1/18.
 */

public abstract class AllenBaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        setTransparent(this);
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    /**
     * 使状态栏透明
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void transparentStatusBar(Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
//        } else {
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
    }

    /**
     * 设置根布局参数
     */
    private void setRootView(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(true);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }
    }

    public void setTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        transparentStatusBar(activity);
        setRootView(activity);
    }

    protected void throwWrongIdsException() {
        throw new RuntimeException("customize dialog must use the specify id that lib gives");
    }

    protected DownloadBuilder getVersionBuilder() {
        if (BuilderManager.getInstance().getDownloadBuilder() == null)
            finish();
        return BuilderManager.getInstance().getDownloadBuilder();


    }

    protected void checkForceUpdate() {
        if (getVersionBuilder() != null && getVersionBuilder().getForceUpdateListener() != null) {
            getVersionBuilder().getForceUpdateListener().onShouldForceUpdate();
            finish();
        }
    }

    protected void cancelHandler() {
        DownloadBuilder builder=getVersionBuilder();
        if (builder != null) {
            if (builder.getOnCancelListener() != null)
                builder.getOnCancelListener().onCancel();
            if(this instanceof UIActivity&&builder.getReadyDownloadCancelListener()!=null){
                builder.getReadyDownloadCancelListener().onCancel();
            }else if(this instanceof DownloadFailedActivity&&builder.getDownloadFailedCancelListener()!=null){
                builder.getDownloadFailedCancelListener().onCancel();
            }else if(this instanceof DownloadingActivity&&builder.getDownloadingCancelListener()!=null){
                builder.getDownloadingCancelListener().onCancel();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveEvent(CommonEvent commonEvent) {
        if (commonEvent.getEventType() == AllenEventType.CLOSE) {
            finish();
            EventBus.getDefault().removeStickyEvent(commonEvent);
        }
    }

    public abstract void showDefaultDialog();

    public abstract void showCustomDialog();

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
