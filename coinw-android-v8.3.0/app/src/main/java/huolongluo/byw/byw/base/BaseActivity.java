package huolongluo.byw.byw.base;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.jakewharton.rxbinding.view.RxView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.base.BaseView;
import huolongluo.byw.byw.injection.component.ActivityComponent;
import huolongluo.byw.byw.injection.component.DaggerActivityComponent;
import huolongluo.byw.byw.injection.model.ActivityModule;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.log.Logger;
import huolongluo.byw.manager.AppManager;
import huolongluo.byw.reform.base.BaseSwipeBackActivity;
import huolongluo.byw.util.DeviceUtils;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.ScreenShotListenManager;
import huolongluo.byw.util.ToastSimple;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.StatusHolder;
import huolongluo.bywx.utils.AppUtils;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
/**
 * Created by 火龙裸先生 on 2017/8/10.
 * Class Note:
 * 1 所有的activity继承于这个类
 */
public abstract class BaseActivity extends BaseSwipeBackActivity implements BaseView {
    private Context mContext = null; //context
    private ActivityComponent mActivityComponent;
    public Subscription subscription;
    Unbinder unbinder;
    protected List<String> netTags = new ArrayList<>();
    public ScreenShotListenManager screenShotListenManager;
    public static BaseActivity instance;
    protected Toolbar toolbar;
    ToastSimple ts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //通用处理方法
        if (StatusHolder.getInstance().isKill()) {
            Logger.getInstance().error("app was kill");
            AppUtils.restart(this);
            return;
        } else {
            Logger.getInstance().debug("app was normal");
        }
        instance = this;
        screenShotListenManager = ScreenShotListenManager.newInstance(this);
        closeKeyboard();
        mContext = this;
        AppManager.get().addActivity(this);
        if (getContentViewId() != 0) {
            setContentView(getContentViewId());
        }
        unbinder = ButterKnife.bind(this);
        toolbar=findViewById(R.id.toolbar);
        if(toolbar!=null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setNavigationOnClickListener((v) -> {
                // 退出当前页面
                finish();
            });
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
                if(initRightLeftDrawable()!=0){
                    Drawable drawable=ContextCompat.getDrawable(this,initRightLeftDrawable());
                    drawable.setBounds(0,0, DeviceUtils.dip2px(this,16),DeviceUtils.dip2px(this,16));
                    tvRight.setCompoundDrawables(drawable,null,null,null);
                }
            }
        }
        // dagger2注解
        injectDagger();
        initViewsAndEvents();
    }

    public ActivityComponent activityComponent() {
        if (null == mActivityComponent) {
            mActivityComponent = DaggerActivityComponent.builder().applicationComponent(BaseApp.get(this).getAppComponent()).activityModule(new ActivityModule(this)).build();
        }
        return mActivityComponent;
    }
//    @NonNull
//    @Override
//    public AppCompatDelegate getDelegate() {
//        return SkinAppCompatDelegateImpl.get(this, this);
//    }

    //初始化居中文本的title
    protected String initTitle(){return "";}
    //toolbar最右边的文本，默认不显示
    protected String initRightText(){return "";}
    protected int initRightTextColor(){return 0;}
    protected int initRightLeftDrawable(){return 0;}
    protected View.OnClickListener initRightTextClickListener(){return null;}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.get().finishActivity(this);
        if (null != unbinder) {
            unbinder.unbind();
        }
        unSubscription();
        for (String tag : netTags) {
            OkhttpManager.getInstance().removeRequest(tag);
        }
        netTags.clear();
        if (ts != null) {
            ts.release();
        }
    }

    public void unSubscription() {
        if (null != subscription && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public void startActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void startActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtra("bundle", bundle);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public Observable<Void> eventClick(View view) {
        return eventClick(view, 1000);
    }

    public void viewClick(View view, View.OnClickListener clickListener) {
        RxView.clicks(view).throttleFirst(1000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).onErrorReturn(throwable -> null).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe(new Subscriber<Void>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Void aVoid) {
                clickListener.onClick(view);
            }
        });
    }

    public Observable<Void> eventClick(View view, int milliseconds) {
//两秒连续点击，只取第一次的点击有效
        return RxView.clicks(view).throttleFirst(milliseconds, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).onErrorReturn(throwable -> {
            Logger.getInstance().errorLog(throwable);
            return null;
        }).doOnError(throwable -> Logger.getInstance().error(throwable));
    }

    public Bundle getBundle() {
        return getIntent().getBundleExtra("bundle");
    }

    /**
     * bind layout resource file
     */
    protected abstract int getContentViewId();
    /**
     * Dagger2 use in your application module(not used in 'base' module)
     */
    protected abstract void injectDagger();
    /**
     * init views and events here
     */
    protected abstract void initViewsAndEvents();

    /**
     * implements methods in BaseView
     */
    @Override
    public void showMessage(String msg, double seconds) {
        ts = ToastSimple.show(msg, seconds);
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void showProgress(String msg) {
        DialogManager.INSTANCE.showProgressDialog(mContext, msg);
    }

    @Override
    public void showProgress(String msg, int progress) {
        DialogManager.INSTANCE.showProgressDialog(mContext, msg, progress);
    }

    @Override
    public void hideProgress() {
        DialogManager.INSTANCE.dismiss();
    }

    @Override
    public void showErrorMessage(String msg, String content) {
        DialogManager.INSTANCE.showErrorDialog(mContext, msg, content);
    }

    public void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
// CRASH: huolongluo.byw (pid 31202)
// Short Msg: java.lang.ArrayIndexOutOfBoundsException
// Long Msg: java.lang.ArrayIndexOutOfBoundsException: length=1; index=1
// Build Label: HONOR/JSN-AL00a/HWJSN-HM:10/HONORJSN-AL00a/10.0.0.157C00:user/release-keys
// Build Changelist: 10.0.0.157C00
// Build Time: 1577514241000
// java.lang.ArrayIndexOutOfBoundsException: length=1; index=1
//      at me.imid.swipebacklayout.lib.ViewDragHelper.saveLastMotion(ViewDragHelper.java:939)
//      at me.imid.swipebacklayout.lib.ViewDragHelper.processTouchEvent(ViewDragHelper.java:1233)
//      at me.imid.swipebacklayout.lib.SwipeBackLayout.onTouchEvent(SwipeBackLayout.java:378)
//      at android.view.View.dispatchTouchEvent(View.java:13503)
//      at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:3067)
//      at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2752)
//      at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:3073)
//      at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2766)
//      at com.android.internal.policy.DecorView.superDispatchTouchEvent(DecorView.java:613)
//      at com.android.internal.policy.PhoneWindow.superDispatchTouchEvent(PhoneWindow.java:1933)
//      at android.app.Activity.dispatchTouchEvent(Activity.java:4147)
//      at huolongluo.byw.byw.base.BaseActivity.dispatchTouchEvent(BaseActivity.java:251)
//      at huolongluo.byw.byw.ui.activity.findpwd.ChangePwdStepOneActivity.dispatchTouchEvent(ChangePwdStepOneActivity.java:333)
//      at androidx.appcompat.view.WindowCallbackWrapper.dispatchTouchEvent(WindowCallbackWrapper.java:69)
//      at com.android.internal.policy.DecorView.dispatchTouchEvent(DecorView.java:559)
        try {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                View v = getCurrentFocus();
                if (isShouldHideKeyboard(v, ev)) {
                    hideKeyboard(v.getWindowToken());
                }
            }
            return super.dispatchTouchEvent(ev);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
        return true;
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            return !(event.getX() > left) || !(event.getX() < right) || !(event.getY() > top) || !(event.getY() < bottom);
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    public enum LoadType {
        FRISTLOAD, // 首次加载
        REFRESH, // 下拉刷新
        LOADMORE// 加载更多
    }

    @Override
    protected void onResume() {
        super.onResume();
        // startScreenShotListen();
        if (!BaseApp.isNetAvailable) {
            MToast.show(this, "当前网络不可用", Toast.LENGTH_SHORT);
        }
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 212) {
            if (data != null) {
                String message = data.getStringExtra("message");
                int type = data.getIntExtra("type", 0);
                if (!android.text.TextUtils.isEmpty(message)) {
                    if (type != 0) {
                        SnackBarUtils.ShowRed(BaseActivity.this, message);
                    } else {
                        SnackBarUtils.ShowBlue(BaseActivity.this, message);
                    }
                }
            }
        }
    }

    public boolean isMyDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (isDestroyed()) {
                return true;
            }
        }
        return isFinishing();
    }
}
