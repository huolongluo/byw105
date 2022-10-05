package com.android.coinw.biz.trade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.log.Logger;
/**
 * ViewGroup  单独的View模块集成该类
 */
public abstract class BaseView {
    protected View mRootView;
    protected Context mContext;
    protected Fragment mFragment;
    protected TradeViewModel mTradeViewModel;
    private Unbinder mUnBinder;
    protected String TAG = this.getClass().getSimpleName();

    public BaseView(Context context) {
        this(context,null);
    }
    public BaseView(Context context, Fragment fragment) {//币币和etf使用mTradeViewModel
        this.mContext = context;
        this.mFragment=fragment;
        mRootView = setContentView(getLayoutId());
        mUnBinder = ButterKnife.bind(this, mRootView);
        initView();
        if(fragment!=null){
            mTradeViewModel=new ViewModelProvider(fragment).get(TradeViewModel.class);
        }
        initData();
        EventBus.getDefault().register(this);
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    public View setContentView(@LayoutRes int layoutResID) {
        View view = LayoutInflater.from(mContext).inflate(layoutResID, null, false);
//        return View.inflate(mContext, layoutResID, null);
        return view;
    }

    /**
     * 同父类的生命周期
     */
    public void onDestroy() {
        if (mUnBinder != Unbinder.EMPTY) {
            mUnBinder.unbind();
        }
        EventBus.getDefault().unregister(this);
        unRegisterObserver();
        this.mUnBinder = null;
        this.mContext = null;
    }
    private void unRegisterObserver(){
        if(mTradeViewModel!=null){
            if(mTradeViewModel.getDepthData().hasObservers()){
                mTradeViewModel.getDepthData().removeObservers(mFragment);
            }
            if(mTradeViewModel.getCancelOrderData().hasObservers()){
                mTradeViewModel.getCancelOrderData().removeObservers(mFragment);
            }
            if(mTradeViewModel.getOrdersData().hasObservers()){
                mTradeViewModel.getOrdersData().removeObservers(mFragment);
            }
            if(mTradeViewModel.getOrderResult().hasObservers()){
                mTradeViewModel.getOrderResult().removeObservers(mFragment);
            }
        }
    }
    /**
     * EventBus接收方法，在ui线程执行  基类需要有默认注解方法,需要其他接收，可在子类单独添加注解
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(Event event) {
    }

    public View getView() {
        return mRootView;
    }
}
