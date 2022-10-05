package huolongluo.byw.reform.c2c.oct.activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcodes.utils.TimeUtils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.databinding.ActivityOtcbuyconfirmBinding;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.c2c.oct.bean.BaseBean;
import huolongluo.byw.reform.c2c.oct.bean.OrderDetailBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.bywx.helper.AppHelper;
import okhttp3.Request;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
/**
 * Created by Administrator on 2019/3/14 0014.
 */
public class OtcOrderNopayActivity extends BaseActivity implements View.OnClickListener {
    ActivityOtcbuyconfirmBinding mBinding;
    String orderId;
    private OrderDetailBean orderDetailBean;
    private Subscription subscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_otcordernopay);
        // setContentView(R.layout.activity_otcbuyconfirm);
        mBinding.include.backIv.setOnClickListener(this);
        if (getIntent() != null) {
            orderId = getIntent().getStringExtra("orderId");
        }
       subscription= Observable.interval(0, 1, TimeUnit.SECONDS).limit(1000).map(aLong -> 1000 - aLong).observeOn(AndroidSchedulers.mainThread()).doOnCompleted(() -> {
        }).doOnNext(aLong -> {
            if (mBinding.timeTv != null) {
                mBinding.timeTv.setText(TimeUtils.millis2String(aLong * 1000, "mm:ss"));
            }
        }).onErrorReturn(throwable -> {
           Logger.getInstance().errorLog(throwable);
           return 0L;
       }).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe();
        viewClick(mBinding.coinfirmTv, v -> {
            DialogUtils.getInstance().showConfirmOtcDialog(this, new DialogUtils.onBnClickListener() {
                @Override
                public void onLiftClick(AlertDialog dialog, View view) {
                }

                @Override
                public void onRightClick(AlertDialog dialog, View view) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    confirmOrder();
                }
            });
        });
    }

    void getOrder() {
        Map<String, String> params = new HashMap<>();
        params.put("orderId", orderId + "");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        Log.e("OkhttpManager.encrypt", "orderId= " + orderId);
        Log.e("OkhttpManager.encrypt", "loginToken= " + UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.get_order, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    orderDetailBean = new Gson().fromJson(result, OrderDetailBean.class);
                    if (orderDetailBean != null && orderDetailBean.getCode() == 0) {
                        updateUI();
                    } else {
                        MToast.show(OtcOrderNopayActivity.this, orderDetailBean.getValue(), 1);
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    void updateUI() {
        if (orderDetailBean.getData() != null) {
            //   mBinding.orderIdTv.setText(orderDetailBean.getData().getId() + "");
            // mBinding.userNameTv.setText(orderDetailBean.getData().getAdUserName() + "");
            mBinding.amountTv.setText(orderDetailBean.getData().getOrders().getTotalAmount() + " CNYT");
            //  mBinding.priceTv.setText(orderDetailBean.getData().getPrice() + " CNYT");
            // mBinding.numTv.setText(orderDetailBean.getData().getAmount() + " " + orderDetailBean.getData().getCoinName());
            mBinding.transReferNumTv.setText(orderDetailBean.getData().getOrders().getTransReferNum() + "");
            if (orderDetailBean.getData().getOrders().getCreateTime() != null) {
                //mBinding.createTimeTv.setText(DateUtils.format(orderDetailBean.getData().getCreateTime().getTime(), "yyyy-MM-dd HH:mm:ss") + "");
            }
        }
    }

    //确认付款
    void confirmOrder() {
        Map<String, String> params = new HashMap<>();
        params.put("orderId", orderId);
        //  params.put("payType",payType+"");//支付方式: 1 银行卡, 2 微信, 3 支付宝
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.confirm_order, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                DialogManager.INSTANCE.dismiss();
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                //{"code":"200","data":"{\"result\":true,\"code\":0,\"value\":\"操作成功\",\"data\":\"付款成功\"}","message":"执行成功"}
                try {
                    BaseBean baseBean = new Gson().fromJson(result, BaseBean.class);
                    if (baseBean.getCode() == 0) {
                    } else {
                    }
                    MToast.show(OtcOrderNopayActivity.this, baseBean.getValue(), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        AppHelper.unsubscribe(subscription);
        super.onDestroy();
    }
}
