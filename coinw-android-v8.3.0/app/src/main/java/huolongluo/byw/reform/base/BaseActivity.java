package huolongluo.byw.reform.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jakewharton.rxbinding.view.RxView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.log.Logger;
import huolongluo.byw.manager.AppManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.bywx.StatusHolder;
import huolongluo.bywx.utils.AppUtils;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
/**
 * Created by Administrator on 2018/11/1 0001.
 */
public class BaseActivity extends BaseSwipeBackActivity {

    //每次网络请求的url都添加到netTags，用于关闭页面的时候取消网络请求
    protected List<String> netTags = new ArrayList<>();
    private Context mContext = null; //context

    public <T extends View> T fv(int resId) {
        return (T) findViewById(resId);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        //通用处理方法
        if (StatusHolder.getInstance().isKill()) {
            Logger.getInstance().error("app was kill");
            AppUtils.restart(this);
            return;
        } else {
            Logger.getInstance().debug("app was normal");
        }
        AppManager.get().addActivity(this);
    }
//    @NonNull
//    @Override
//    public AppCompatDelegate getDelegate() {
//        return SkinAppCompatDelegateImpl.get(this, this);
//    }
    @Override
    protected void onDestroy() {

        super.onDestroy();
        for (String tag : netTags) {
            OkhttpManager.getInstance().removeRequest(tag);
            OKHttpHelper.getInstance().removeRequest(tag);
        }
        AppManager.get().finishActivity(this);
        netTags.clear();
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

    public boolean isMyDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (isDestroyed()) {
                return true;
            }
        }
        return isFinishing();
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

    public interface OnTitleRightClickListener {
        void onClick();
    }

    public void showProgress(String msg) {
        DialogManager.INSTANCE.showProgressDialog(mContext, msg);
    }

    public void initTitle(String title, @DrawableRes int rightIvId, OnTitleRightClickListener onTitleRightClickListener) {
        TextView titleView = findViewById(R.id.title_tv);
        ImageView back_iv = findViewById(R.id.back_iv);
        ImageView right_iv = findViewById(R.id.right_iv);
        if (titleView != null) {
            titleView.setText(title);
        }

        if (back_iv != null) {
            back_iv.setOnClickListener(v -> finish());
        }
        if (rightIvId != 0 && right_iv != null && onTitleRightClickListener != null) {
            right_iv.setImageDrawable(getResources().getDrawable(rightIvId));
            right_iv.setOnClickListener(v -> onTitleRightClickListener.onClick());
        }
    }

    public void hideProgress() {
        DialogManager.INSTANCE.dismiss();
    }

}
