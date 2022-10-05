package huolongluo.byw.byw.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.base.BaseView;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.ToastSimple;
import rx.Observable;
import rx.Subscription;

/**
 * <p>
 * Created by 火龙裸 on 2017/8/21.
 */
public abstract class BaseDialog extends DialogFragment implements BaseView {
    /**
     * activity context of fragment
     */
    protected Activity mContext;
    public Subscription subscription;
    Unbinder unbinder;
    View view;
    protected int currentStyle = 0;
    public static final int CANCEL_ORDER = 1;
    public static final int UP_VERSION = 2;
    public static final int EXIT_APP = 5;
    public static final int CHANGE_INFO = 6;
    public static final int START_TRADE = 7;
    public static final int CHOICE_SHOP = 8;
    public static final int CLICK_TIPS = 9;
    private ToastSimple ts;
    public static final int DELETE_CARD = 11;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyAlertDialog);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setDialog(currentStyle);
        if (getContentViewId() != 0) {
            return view = inflater.inflate(getContentViewId(), null);
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    protected View findViewById(int resId) {
        return view.findViewById(resId);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        initViewsAndEvents(view);
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        try {
            if(!this.isAdded()&&manager.findFragmentByTag(tag)==null){//add后不需要再调用show,否则连续调用show会崩溃
                super.show(manager, tag);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        try {
            if (isAdded()) {//没有add不能调用dismiss,否则会崩溃
                super.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public Observable<Void> eventClick(View view) {
        return eventClick(view, 1000);
    }

    public Observable<Void> eventClick(View view, int milliseconds) {
        return RxView.clicks(view).throttleFirst(milliseconds, TimeUnit.MILLISECONDS).doOnError(throwable -> Logger.getInstance().error(throwable));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (null != subscription && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        if (ts != null) {
            ts.release();
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
     * set for choice witch dialog to show
     *
     * @return
     */
    public int setDialog(int style) {
        return currentStyle = style;
    }

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
}
