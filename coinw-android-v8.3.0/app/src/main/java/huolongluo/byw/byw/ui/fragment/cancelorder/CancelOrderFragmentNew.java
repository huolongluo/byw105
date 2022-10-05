package huolongluo.byw.byw.ui.fragment.cancelorder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.coinw.biz.event.BizEvent;
import com.android.coinw.biz.trade.BaseView;
import com.android.coinw.biz.trade.TradeViewModel;
import com.android.coinw.biz.trade.helper.TradeDataHelper;
import com.android.legend.model.CommonResult;
import com.android.legend.model.Page2;
import com.android.legend.model.enumerate.order.OrderSocketStatus;
import com.android.legend.model.enumerate.order.OrderType;
import com.android.legend.model.order.OrderItemBean;
import com.android.legend.model.order.OrderSocketBean;
import com.android.legend.util.TimerUtil;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding.view.RxView;
import com.legend.modular_contract_sdk.component.adapter.DataBindingRecyclerViewAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import huolongluo.byw.BR;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.bean.HistoryListBean;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.dialog.AddDialog;
import huolongluo.byw.databinding.FragmentCancelOrderBinding;
import huolongluo.byw.databinding.ItemHistoryBinding;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.trade.fragment.RecycleViewDivider;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.CurrencyPairUtil;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.NumberUtil;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.utils.DoubleUtils;
import rx.Observable;
/**
 * Created by 火龙裸 on 2017/12/26.
 * 当前委托
 */
public class CancelOrderFragmentNew extends BaseView {

    @BindView(R.id.tv_no_data)
    RelativeLayout tv_no_data; // 没有数据
    @BindView(R.id.rv)
    RecyclerView recyclerView;
    private DataBindingRecyclerViewAdapter<OrderItemBean> cancelOrderAdapter;
    private boolean isVisible = true;
    private boolean mIsRefreshing = false;
    FragmentManager fragmentManager;
    private boolean isETF = false;
    private long lastGetOrderTime=0;//记录获取当前委托的时间点
    private FragmentCancelOrderBinding mBinding;
    public CancelOrderFragmentNew(Context context, FragmentManager fragmentManager,Fragment fragment) {
        super(context,fragment);
        this.fragmentManager = fragmentManager;
    }

    public void setETF(boolean isETF) {
        this.isETF = isETF;
    }

    @Override
    public View setContentView(int layoutResID) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.fragment_cancel_order, null, false);
        return mBinding.getRoot();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cancel_order;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        initObserver();
    }

    private void initObserver() {
        mTradeViewModel.getOrdersData().observe(mFragment, new Observer<CommonResult<Page2<List<OrderItemBean>>>>() {
            @Override
            public void onChanged(CommonResult<Page2<List<OrderItemBean>>> listCommonResult) {
                Logger.getInstance().debug(TAG,"viewModel.getOrdersData():"+ GsonUtil.obj2Json(listCommonResult,CommonResult.class));
                if (listCommonResult.isSuccess()) {
                    if(listCommonResult.getData()==null||listCommonResult.getData().getResult()==null){
                        setVisible(recyclerView, View.GONE);
                        setVisible(tv_no_data, View.VISIBLE);
                        return;
                    }

                    handleData(listCommonResult.getData().getResult());
                    lastGetOrderTime=System.currentTimeMillis();
                } else {
                    setVisible(recyclerView, View.GONE);
                    setVisible(tv_no_data, View.VISIBLE);

                }
            }
        });
        mTradeViewModel.getCancelOrderData().observe(mFragment, new Observer<CommonResult<String>>() {
            @Override
            public void onChanged(CommonResult<String> stringCommonResult) {
                Logger.getInstance().debug(TAG,"viewModel.getCancelOrderData():"+ GsonUtil.obj2Json(stringCommonResult,CommonResult.class));
                if(stringCommonResult.isSuccess()){
                    SnackBarUtils.ShowBlue((Activity) mContext, stringCommonResult.getMessage());
                    EventBus.getDefault().post(new BizEvent.Trade.MarketInfo(isETF));
                }else{
                    SnackBarUtils.ShowRed((Activity) mContext, stringCommonResult.getMessage());
                }
            }
        });
        mTradeViewModel.getOrderSocketData().observe(mFragment, new Observer<OrderSocketBean>() {
            @Override
            public void onChanged(OrderSocketBean orderSocketBean) {
                if(orderSocketBean==null) return;
                if(!TextUtils.equals(orderSocketBean.getSymbol(),TradeDataHelper.getInstance().getId(isETF)+"")){
                    return ;
                }
                if(TextUtils.equals(orderSocketBean.getStatus(), OrderSocketStatus.OPEN.getStatus())){
                    if(!TimerUtil.Companion.isTimeExceedInterval(lastGetOrderTime, AppConstants.TIMER.SOCKET_ORDER)){
                        return;
                    }
                    lastGetOrderTime=System.currentTimeMillis();
                    getCurrentOrder();
                }else if(TextUtils.equals(orderSocketBean.getStatus(), OrderSocketStatus.MATCH.getStatus())){
                    if(cancelOrderAdapter!=null){
                        for(OrderItemBean bean:cancelOrderAdapter.getAllData()){
                            if(bean.getOrderId()==orderSocketBean.getOrderId()){
                                bean.setDealSize(DoubleUtils.parseDouble(bean.getSize())-orderSocketBean.getRemainSize()+"");
                                cancelOrderAdapter.notifyDataSetChanged();
                                break;
                            }
                        }

                    }
                }else if(TextUtils.equals(orderSocketBean.getStatus(), OrderSocketStatus.DONE.getStatus())){
                    if(cancelOrderAdapter!=null) {
                        OrderItemBean deleteBean = null;
                        for(OrderItemBean bean:cancelOrderAdapter.getAllData()){
                            if(bean.getOrderId()==orderSocketBean.getOrderId()){
                                deleteBean = bean;
                                break;
                            }
                        }
                        cancelOrderAdapter.getAllData().remove(deleteBean);
                        cancelOrderAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public View getView() {
        return mRootView;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
        if (isVisible) {
            getCurrentOrder();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreatedView(View rootView) {
//        btn_bus_login = rootView.findViewById(R.id.btn_bus_login);
        tv_no_data = rootView.findViewById(R.id.tv_no_data);

        List<OrderItemBean> entrustInfBeanList = new ArrayList<>();
        cancelOrderAdapter = new DataBindingRecyclerViewAdapter<OrderItemBean>(mContext, R.layout.item_history, BR.order, entrustInfBeanList);
        cancelOrderAdapter.setOnBindingViewHolderListener((holder, position) -> {
            ItemHistoryBinding binding = holder.getBinding();
            binding.ivHistoryCancleOrder.setOnClickListener(v -> {
                AddDialog dialog = new AddDialog();
                Bundle bundle = new Bundle();
                bundle.putString("orderId", binding.getOrder().getOrderId() + "");
                bundle.putInt("position", position);
                dialog.setArguments(bundle);
                dialog.setDialog(AddDialog.CANCEL_ORDER);
                dialog.show(fragmentManager, getClass().getSimpleName());
            });

        });
        EventBus.getDefault().register(this);
        recyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL, R.drawable.divider_mileage));
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mIsRefreshing) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        recyclerView.setNestedScrollingEnabled(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cancelOrderAdapter);
        recyclerView.setHasFixedSize(true);//最重要的这句
    }

    private void setAdapter() {
        if (cancelOrderAdapter == null) {
            List<OrderItemBean> entrustInfBeanList = new ArrayList<>();
            cancelOrderAdapter = new DataBindingRecyclerViewAdapter<OrderItemBean>(mContext, R.layout.item_history, BR.order, entrustInfBeanList);
            cancelOrderAdapter.setOnBindingViewHolderListener((holder, position) -> {
                ItemHistoryBinding binding = holder.getBinding();
                binding.ivHistoryCancleOrder.setOnClickListener(v -> {
                    AddDialog dialog = new AddDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("orderId", binding.getOrder().getOrderId() + "");
                    bundle.putInt("position", position);
                    dialog.setArguments(bundle);
                    dialog.setDialog(AddDialog.CANCEL_ORDER);
                    dialog.show(fragmentManager, getClass().getSimpleName());
                });

            });
            recyclerView.setNestedScrollingEnabled(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(cancelOrderAdapter);
            recyclerView.setHasFixedSize(true);//最重要的这句
        } else {
            cancelOrderAdapter.notifyDataSetChanged();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cancelOrder(Event.cancelOrder cancelOrder) {
        mTradeViewModel.cancelOrder(NumberUtil.toLong(cancelOrder.orderId));
    }

    /**
     * 更新用户信息，刷新界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshInfo(Event.refreshInfo refreshInfo) {
        checkStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshMarketInfo(BizEvent.Trade.MarketInfo marketInfo) {//撤单和下单成功调用
        if (marketInfo.isETF != isETF) {
            return;
        }
        getCurrentOrder();
    }

    /**
     * 收到 退出登录 的通知
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exitApp(Event.exitApp exit) {
        checkStatus();
    }

    /**
     * 检查当前状态，并显示相应控件
     */
    private void checkStatus() {
        if (!UserInfoManager.isLogin()) {
            tv_no_data.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            if (cancelOrderAdapter != null) {
                cancelOrderAdapter.cleanData();
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tv_no_data.setVisibility(View.GONE);
        }
    }

    public void reSetView() {
        setAdapter();
    }

    private void getCurrentOrder() {
        if (!UserInfoManager.isLogin()) {
            return;
        }
        if (!BaseApp.isNetAvailable) {
            tv_no_data.setVisibility(View.VISIBLE);
            return;
        }
        // 2021-10-12 去掉当前委托 symbol 参数，为了在列表展示所有委托。
        mTradeViewModel.getOrders(true, 0, null, null, null, null, null);
    }

    private void setVisible(View targetView, int visible) {
        Logger.getInstance().debug(TAG, "targetView: " + targetView.getId() + "visible");
        if (targetView == null) {
            return;
        }
        try {
            if (targetView.getVisibility() == visible) {
                return;
            }
            targetView.setVisibility(visible);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private void handleData(List<OrderItemBean> orders) {
        if (!UserInfoManager.isLogin()) {
            setAdapter();
            cancelOrderAdapter.cleanData();
            cancelOrderAdapter.notifyDataSetChanged();
            return;
        }
        if (orders == null) {
            return;
        }

        if (orders.size()==0) {
            setVisible(recyclerView, View.GONE);
            setVisible(tv_no_data, View.VISIBLE);

        } else {
            if (recyclerView.getVisibility() != View.VISIBLE) {
                setVisible(recyclerView, View.VISIBLE);
                setVisible(tv_no_data, View.GONE);
            }
            setAdapter();
            cancelOrderAdapter.refreshData(orders);
            cancelOrderAdapter.notifyDataSetChanged();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            if (!UserInfoManager.isLogin()) {
                setVisible(recyclerView, View.GONE);
                setVisible(tv_no_data, View.VISIBLE);
            } else {
                setVisible(recyclerView, View.VISIBLE);
                setVisible(tv_no_data, View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.getInstance().debug(TAG,"onDestroy");
        EventBus.getDefault().unregister(this);
    }

    public Observable<Void> eventClick(View view) {
        return eventClick(view, 1000);
    }

    public Observable<Void> eventClick(View view, int milliseconds) {
        return RxView.clicks(view).throttleFirst(milliseconds, TimeUnit.MILLISECONDS).doOnError(throwable -> Logger.getInstance().error(throwable));
    }

    public void startActivity(Class<?> clazz) {
        Intent intent = new Intent(mContext, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        mContext.startActivity(intent);
    }

    public void applyTheme() {
        mBinding.invalidateAll();
        if (cancelOrderAdapter!= null){
            cancelOrderAdapter.notifyDataSetChanged();
        }
    }
}
