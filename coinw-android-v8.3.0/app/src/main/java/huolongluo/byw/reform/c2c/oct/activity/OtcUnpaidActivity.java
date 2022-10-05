package huolongluo.byw.reform.c2c.oct.activity;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.netease.nim.uikit.business.session.fragment.IMMessageEntity;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.databinding.ActivityOtcuserotherUnpaidBinding;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.c2c.oct.bean.OrderDetailBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.NIMHelper;
import huolongluo.bywx.helper.AppHelper;
import okhttp3.Request;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
/**
 * 商家未支付页面
 */
public class OtcUnpaidActivity extends BaseActivity {
    private OrderDetailBean orderDetailBean;
    private int orderId;
    private ActivityOtcuserotherUnpaidBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otcuserother_unpaid);
        initView();
        initData();
    }

    private void initView() {
        viewClick(binding.include.backIv, v -> finish());
        viewClick(binding.callPhoneLl, v -> contactMerchant());
    }

    private void contactMerchant() {
        if (orderDetailBean == null || orderDetailBean.getData() == null || orderDetailBean.getData().getOrders() == null) {
            //TODO show
            return;
        }
        if (!orderDetailBean.getData().getOrders().isCoinWAd()) {
            DialogUtils.getInstance().showOneButtonDialog(OtcUnpaidActivity.this, getString(R.string.no_chat), getString(R.string.b38));
            return;
        }
        //获得商家的IM通道名称
        NIMHelper.contactMerchant(this, orderDetailBean, 0);
    }

    private void initData() {
        if (getIntent() != null) {
            orderId = getIntent().getIntExtra("orderId", 0);
        }
        getOrder();
    }

    void getOrder() {
        Map<String, String> params = new HashMap<>();
        params.put("orderId", orderId + "");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.get_order, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                SnackBarUtils.ShowRed(OtcUnpaidActivity.this, errorMsg);
                DialogManager.INSTANCE.dismiss();
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    orderDetailBean = new Gson().fromJson(result, OrderDetailBean.class);
                    if (orderDetailBean != null && orderDetailBean.getCode() == 0) {
                        updateUI();
                    } else {
                        MToast.show(OtcUnpaidActivity.this, orderDetailBean.getValue(), 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SnackBarUtils.ShowRed(OtcUnpaidActivity.this, getString(R.string.qa18));
                }
                DialogManager.INSTANCE.dismiss();
//                showDot();
            }
        });
    }

    void updateUI() {
        if (orderDetailBean.getData() != null) {
            binding.orderIdTv.setText(orderDetailBean.getData().getOrders().getOrderNo() + "");
            binding.userNameTv.setText(orderDetailBean.getData().getOppositeUserName() + "");
            binding.amountTv.setText(orderDetailBean.getData().getOrders().getTotalAmount() + " CNY");
            binding.priceTv.setText(orderDetailBean.getData().getOrders().getPrice() + " CNY/" + orderDetailBean.getData().getOrders().getCoinName());
            binding.numTv.setText(orderDetailBean.getData().getOrders().getAmount() + " " + orderDetailBean.getData().getOrders().getCoinName());
            binding.transReferNumTv.setText(orderDetailBean.getData().getOrders().getTransReferNum() + "");
            int timeLimt = (int) orderDetailBean.getData().getPayLimit();
            if (timeLimt > 1) {
                Subscription ms = Observable.interval(0, 1, TimeUnit.SECONDS).limit(timeLimt + 1).map(aLong -> timeLimt - aLong).observeOn(AndroidSchedulers.mainThread()).doOnNext(this::call)
                        .onErrorReturn(throwable -> {
                    Logger.getInstance().errorLog(throwable);
                    return 1L;
                }).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe();
                subscriptionArrayList.add(ms);
            }
        }
        showDot();
    }

    private ArrayList<Subscription> subscriptionArrayList = new ArrayList<>();

    public void clearClock() {
        for (Subscription ms : subscriptionArrayList) {
            AppHelper.unsubscribe(ms);
        }
        subscriptionArrayList.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearClock();
    }

    public void showDot() {
        View dot = findViewById(R.id.dot);
        for (IMMessage imMessage : IMMessageEntity.getMessages()) {
            if (null == orderDetailBean || null == orderDetailBean.getData()) return;
            if (null != imMessage.getRemoteExtension().get("orderNo") && null != orderDetailBean.getData().getOrders().getOrderNo()) {
                String orderNo = imMessage.getRemoteExtension().get("orderNo").toString();
                String orderNo1 = String.valueOf(orderDetailBean.getData().getOrders().getId());
                if (orderNo.equals(orderNo1)) {
                    dot.setVisibility(View.VISIBLE);
                    return;
                }
            }
        }
        dot.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDot();
    }

    private void call(Long aLong) {
//        if (aLong == 1) {
//            binding.timeLimit.setVisibility(View.INVISIBLE);
//        } else {
//            binding.timeLimit.setText(String.format("若对方在%s内未付款，订单自动取消",
//                    TimeUtils.millis2String(aLong * 1000, "mm分ss秒")));
//        }
    }
}
