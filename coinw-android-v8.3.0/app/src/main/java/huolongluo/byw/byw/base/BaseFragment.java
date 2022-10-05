package huolongluo.byw.byw.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jakewharton.rxbinding.view.RxView;
import com.legend.common.base.ThemeFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.base.BaseView;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.ToastSimple;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * <p>
 * Created by 火龙裸 on 2017/8/21.
 * Class Note:
 * <p/>
 * Base Fragment for all the Fragment defined in the project
 * 1 extended from {@link BaseFragment} to do
 * some base operation.
 * 2 do operation in initViewAndEvents(){@link #initViewsAndEvents(View rootView)}
 */
public abstract class BaseFragment extends ThemeFragment implements BaseView {

    /**
     * activity context of fragmentEventBus
     */
    protected Activity mContext;
    Unbinder unbinder;
    public Subscription subscription;
    protected List<String> netTags = new ArrayList<>();
    private ToastSimple ts;
    protected String TAG = this.getClass().getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        initDagger();
    }

    protected abstract void initDagger();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getContentViewId() != 0) {
            return inflater.inflate(getContentViewId(), null);
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

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

    @Override
    public void onStop() {
        super.onStop();
//        Logger.getInstance().debug(TAG, "onStop.", new Exception());
    }

    public View view;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        this.view = view;
        initViewsAndEvents(view);
    }

    public <T extends View> T findView(int resId) {
        if (view != null) {
            return view.findViewById(resId);
        }
        return null;
    }

    public void startActivity(Class<?> clazz) {
        Intent intent = new Intent(mContext, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void startActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(mContext, clazz);
        if (null != bundle) {
            intent.putExtra("bundle", bundle);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void viewClick(View view, View.OnClickListener clickListener) {
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
    }

    public Observable<Void> eventClick(View view) {
        return eventClick(view, 1000);
    }

    public Observable<Void> eventClick(View view, int milliseconds) {
        return RxView.clicks(view).throttleFirst(milliseconds, TimeUnit.MILLISECONDS).doOnError(throwable -> Logger.getInstance().error(throwable));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != unbinder) {
            unbinder.unbind();
        }
        unSubscription();
        for (String tag : netTags) {
            OkhttpManager.getInstance().removeRequest(tag);
        }
        netTags.clear();
    }

    public void unSubscription() {
        if (null != subscription && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    /**
     * override this method to do operation in the fragment
     */
    protected abstract void initViewsAndEvents(View rootView);

    /**
     * override this method to return content view id of the fragment
     */
    protected abstract int getContentViewId();

    /**
     * implements methods in BaseView
     */
    @Override
    public void showMessage(String msg, double seconds) {
        ts = ToastSimple.show(msg, seconds);
    }

    @Override
    public void close() {
        mContext.finish();
    }

    @Override
    public void showProgress(String message) {
        DialogManager.INSTANCE.showProgressDialog(mContext, message);
    }

    @Override
    public void showProgress(String message, int progress) {
        DialogManager.INSTANCE.showProgressDialog(mContext, message, progress);
    }

    @Override
    public void hideProgress() {
        DialogManager.INSTANCE.dismiss();
    }

    @Override
    public void showErrorMessage(String msg, String content) {
        DialogManager.INSTANCE.showErrorDialog(mContext, msg, content);
    }

    @Override
    public void onDestroy() {
        if (ts != null) {
            ts.release();
        }
        super.onDestroy();
    }
}
