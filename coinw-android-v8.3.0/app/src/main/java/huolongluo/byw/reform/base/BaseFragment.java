package huolongluo.byw.reform.base;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jakewharton.rxbinding.view.RxView;
import com.legend.common.base.ThemeFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.OkhttpManager;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
/**
 * Created by hy on 2018/10/31 0031.
 */
public abstract class BaseFragment extends ThemeFragment {
    //每次网络请求的url都添加到netTags，用于关闭页面的时候取消网络请求
    protected List<String> netTags = new ArrayList<>();
    private Context mContext;
    public View rootView;
    protected String TAG = this.getClass().getSimpleName();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public Context getContext() {
        if (mContext != null) {
            return mContext;
        } else if (super.getContext() != null) {
            return super.getContext();
        } else {
            return BaseApp.getSelf().getApplicationContext();
        }
    }

    public Context getAContext() {
        if (getActivity() != null) {
            return getActivity();
        } else if (mContext != null) {
            return mContext;
        } else {
            return BaseApp.getSelf().getApplicationContext();
        }
    }

    public void setTitle(int titleRes) {
        TextView title_tv = fv(R.id.title_tv);
        if (title_tv != null) {
            title_tv.setText(getAContext().getResources().getString(titleRes));
        }
    }

    public void startActivity(Class<?> clazz) {
        Intent intent = new Intent(getAContext(), clazz);
        startActivity(intent);
    }

    public void setTextById(int id, String content) {
        TextView textView = fv(id);
        textView.setText(content);
        return;
    }

    public void setImageById(int id, int resId) {
        ImageView iv = fv(id);
        iv.setImageResource(resId);
        return;
    }

    public <T extends View> T fv(int resId) {
        if (rootView != null) {
            return rootView.findViewById(resId);
        }
        return null;
    }

    public void viewClick(View view, View.OnClickListener clickListener) {
        try {
            RxView.clicks(view).throttleFirst(1000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Void>() {
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
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void viewClick(int second, View view, View.OnClickListener clickListener) {
        try {
            RxView.clicks(view).throttleFirst(second, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Void>() {
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
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getRootViewResId(), container, false);
        onCreatedView(rootView);
        getView();
        return rootView;
    }

    protected abstract int getRootViewResId();
    protected abstract void onCreatedView(View rootView);

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        Logger.getInstance().debug(TAG, "setUserVisibleHint: " + isVisibleToUser, new Exception());
    }

    @Override
    public void onResume() {
        super.onResume();
//        Logger.getInstance().debug(TAG, "onResume.");
    }

    @Override
    public void onPause() {
        super.onPause();
//        Logger.getInstance().debug(TAG, "onPause.");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void emptyEvent() {
    }

    @Override
    public void onStop() {
        super.onStop();
//        Logger.getInstance().debug(TAG, "onStop.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for (String tag : netTags) {
            OkhttpManager.getInstance().removeRequest(tag);
        }
        netTags.clear();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }
}
