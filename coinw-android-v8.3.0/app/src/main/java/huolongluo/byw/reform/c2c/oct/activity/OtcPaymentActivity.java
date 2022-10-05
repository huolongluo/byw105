package huolongluo.byw.reform.c2c.oct.activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcodes.utils.TimeUtils;
import com.blankj.utilcodes.utils.ToastUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.databinding.ActivityOtcpaymentBinding;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.c2c.adapter.RechargeAdapter;
import huolongluo.byw.reform.c2c.oct.bean.BaseBean;
import huolongluo.byw.reform.c2c.oct.bean.OrderCancelBean;
import huolongluo.byw.reform.c2c.oct.bean.PayOrderInfoBean;
import huolongluo.byw.reform.c2c.oct.utils.ImDotUtils;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.NIMHelper;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;
import okhttp3.Request;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
/**
 * Created by Administrator on 2019/3/13 0013.
 */
public class OtcPaymentActivity extends BaseActivity implements View.OnClickListener {
    private boolean isOrderCancel = false;
    private boolean isVisioble = true;
    private static final String TAG = "OtcPaymentActivity";
    private int cancelCount = 0;
    ImageButton back_iv;
    ActivityOtcpaymentBinding mBinding;
    int orderId;
    public static PayOrderInfoBean payOrderInfoBean;
    //    int payType = -1;//1 银行卡, 2 微信, 3 支付宝
    private boolean isShowDot = true;
    private List<PayOrderInfoBean.PayAccountsBean> dataList = new ArrayList<>();
    private RechargeAdapter rechargeAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_otcpayment);
        EventBus.getDefault().register(this);
        int payType = -1;
        if (getIntent() != null) {
            orderId = getIntent().getIntExtra("data", 0);
        }
        Log.i("orderId", String.valueOf(orderId));
        mBinding.cancleTv.setOnClickListener(this);
        mBinding.confirmTv.setOnClickListener(this);
        mBinding.included.backIv.setOnClickListener(this);
        mBinding.callPhoneLl.setOnClickListener(this);
        viewClick(mBinding.copyIv1, v -> {
            NorUtils.copeText(this, mBinding.orderIdTv.getText().toString());
            MToast.showButton(this, getString(R.string.xx14), 0);
        });
        viewClick(mBinding.copyIv2, v -> {
            NorUtils.copeText(this, mBinding.remarkNoTv.getText().toString());
            MToast.showButton(this, getString(R.string.xx11), 1);
        });
        configGet();
        payOrder();
        rechargeAdapter = new RechargeAdapter(this, dataList);
        if (payType != 0) {
            rechargeAdapter.selectPayType(payType);
        }
        mBinding.rechargeMethodList.setAdapter(rechargeAdapter);
        mBinding.rechargeMethodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    rechargeAdapter.select(position);
                    rechargeAdapter.notifyDataSetChanged();
                } catch (Throwable t) {
                    Logger.getInstance().error(t);
                }
            }
        });
    }

    private void initRechargeList(PayOrderInfoBean.DataBean data) {
        try {
            dataList.clear();
            dataList.addAll(data.getPayAccounts());
            rechargeAdapter.notifyDataSetChanged();
            AppUtils.setListViewHeightBasedOnChildren(mBinding.rechargeMethodList);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    void configGet() {
        Map<String, String> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.configGet, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                OrderCancelBean orderCancelBean = GsonUtil.json2Obj(result, OrderCancelBean.class);
                cancelCount = orderCancelBean.getData().getOrderCancelCountsByDay().getValue();
              /*   {
                     "code": 0,
                         "data": {
                     "orderCancelCountsByDay": {
                         "describe": "一日内取消购买订单次数",
                                 "value": 10
                     }
                 },
                     "result": true,
                         "value": "操作成功"
                 }*/
            }
        });
    }

    void payOrder() {
        Map<String, String> params = new HashMap<>();
        params.put("orderId", orderId + "");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.pay_order, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                DialogManager.INSTANCE.dismiss();
                SnackBarUtils.ShowRed(OtcPaymentActivity.this, errorMsg);
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                try {
                    payOrderInfoBean = new Gson().fromJson(result, PayOrderInfoBean.class);
                    if (payOrderInfoBean.getData().getOrder().isOneKey() && payOrderInfoBean.getData().getOrder().getUid() == UserInfoManager.getUserInfo().getFid()) {
                        toBuyConfirmFromOneKey();
                    }
                    if (payOrderInfoBean.getCode() == 0) {
                        clearDot();
                        updataUI();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showDot();
            }
        });
    }

    private void toBuyConfirmFromOneKey() {
        Intent intent = new Intent(OtcPaymentActivity.this, OtcBuyConfirmActivity.class);
        intent.putExtra("orderId", payOrderInfoBean.getData().getOrder().getId());
        intent.putExtra("adRemark", payOrderInfoBean.getData().getAdRemark() + "");
        intent.putExtra("payType", rechargeAdapter.getPayType());
        intent.putExtra("totalAmount", payOrderInfoBean.getData().getOrder().getTotalAmount());
        intent.putExtra("payLimit", (int) currentLimit);
        intent.putExtra("transReferNum", payOrderInfoBean.getData().getOrder().getTransReferNum());
        intent.putExtra("payBean1", payOrderInfoBean.getData().getOrder().getBuyerSelectedSellerPayType());
        intent.putExtra("fastTrade", true);
        startActivity(intent);
        finish();
    }

    Subscription ms;
    private long time;

    private void updateTime(long time) {
        this.time = time;
        Log.e(TAG, "time222: " + time);
    }

    private long getTime() {
        return this.time;
    }

    private long currentLimit;

    void updataUI() {
        if (payOrderInfoBean != null && payOrderInfoBean.getData() != null) {
            int timeLimit = payOrderInfoBean.getData().getPayLimit();
            if (timeLimit > 1) {
                ms = Observable.interval(0, 1, TimeUnit.SECONDS).limit(timeLimit + 2).map(aLong -> timeLimit + 1 - aLong).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
                    Log.e("doOnNextaad", "==  " + aLong);
                    if (mBinding == null || mBinding.timeLimetTv == null) {
                        return;
                    }
                    long time = aLong - 2 < 0 ? 0 : (aLong - 2) * 1000;
                    updateTime(time);
                    Log.e(TAG, "time: " + time);
                    mBinding.timeLimetTv.setText(getString(R.string.remainingTime) + TimeUtils.millis2String(time, "mm:ss"));
                    currentLimit = aLong;
                    if (aLong == 0) {
                        isOrderCancel = true;
                        if (isVisioble) {
                            cancelOrderAction();
                        }
                    }
                }).onErrorReturn(throwable -> {
                    Logger.getInstance().errorLog(throwable);
                    return 1L;
                }).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe();
            }
            if (payOrderInfoBean.getData().getOrder() != null) {
                mBinding.orderIdTv.setText(payOrderInfoBean.getData().getOrder().getOrderNo() + "");
                mBinding.cointitleTv.setText(getString(R.string.buy1) + payOrderInfoBean.getData().getOrder().getCoinName());
                mBinding.orderNumTv.setText(payOrderInfoBean.getData().getOrder().getAmount() + "" + payOrderInfoBean.getData().getOrder().getCoinName());
                mBinding.priceTv.setText(payOrderInfoBean.getData().getOrder().getPrice() + " CNY/" + payOrderInfoBean.getData().getOrder().getCoinName());
                mBinding.totalPriceTv.setText(payOrderInfoBean.getData().getOrder().getTotalAmount() + " CNY");
                mBinding.remarkNoTv.setText(payOrderInfoBean.getData().getOrder().getTransReferNum() + "");
                mBinding.timeTv.setText(payOrderInfoBean.getData().getOrder().getCreateTime_s());
                initRechargeList(payOrderInfoBean.getData());
//                initBankInfo(payOrderInfoBean.getData());
                if (payOrderInfoBean.getData().getOrder().getCreateTime() != null) {
                    //   mBinding.timeTv.setText(DateUtils.formatDateTime(payOrderInfoBean.getData().getOrder().getCreateTime().getTime(), "yyyy-MM-dd HH:mm:ss"));
                }
            }
            if (payOrderInfoBean.getData().getOtcUserinfo() != null) {
                mBinding.nickNameTv.setText(payOrderInfoBean.getData().getOtcUserinfo().getNickname());
            }
            if (payOrderInfoBean.getData().getOrder() != null && !payOrderInfoBean.getData().getOrder().isCoinWAd()) {
                findViewById(R.id.img).setVisibility(View.GONE);
            }
        }
    }

    private void cancelOrderAction() {
        Intent intent = new Intent(OtcPaymentActivity.this, OtcOrderCancleDetailActivity.class);
        intent.putExtra("orderId", orderId);
        intent.putExtra("tradeType", 1);
        startActivity(intent);
//                        startActivity(new Intent(OtcPaymentActivity.this, OtcOrderManagerActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancle_tv:
                String des;
                //java.lang.NullPointerException: Attempt to invoke virtual method 'int huolongluo.byw.reform.c2c.oct.bean.PayOrderInfoBean$DataBean.getDealType()' on a null object reference
                //    at huolongluo.byw.reform.c2c.oct.activity.OtcPaymentActivity.onClick(OtcPaymentActivity.java:8)
                //    at android.view.View.performClick(View.java:6637)
                if (payOrderInfoBean == null || payOrderInfoBean.getData() == null || payOrderInfoBean.getData().getOrder() == null || UserInfoManager.getUserInfo() == null) {
                    break;
                }
                if (payOrderInfoBean.getData().getDealType() == OtcPrepaidActivity.buyOrder && UserInfoManager.getUserInfo().getFid() == payOrderInfoBean.getData().getOrder().getAdUserId()) {
                    des = getResources().getString(R.string.cancle_des2, cancelCount);
                } else {
                    des = getResources().getString(R.string.cancle_des1, cancelCount);
                }
                DialogUtils.getInstance().showCancleOtcDialog(OtcPaymentActivity.this, getResources().getString(R.string.cancle_title1), des, new DialogUtils.onBnClickListener() {
                    @Override
                    public void onLiftClick(AlertDialog dialog, View view) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onRightClick(AlertDialog dialog, View view) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        cancelOrder();
                    }
                });
                break;
            case R.id.confirm_tv:
                if (rechargeAdapter == null || rechargeAdapter.getPayType() == -1) {
                    MToast.show(this, getString(R.string.xx15), 1);
                    return;
                }
                if (payOrderInfoBean != null) {
                    toBuyConfirm();
                } else {
                    ToastUtils.showShortToast(R.string.xx10);
                    payOrder();
                }
                break;
            case R.id.back_iv:
                finish();
                break;
            case R.id.callPhone_Ll:
                contactMerchant();
                break;
        }
    }

    private void toBuyConfirm() {
        Intent intent = new Intent(OtcPaymentActivity.this, OtcBuyConfirmActivity.class);
        intent.putExtra("orderId", payOrderInfoBean.getData().getOrder().getId());
        intent.putExtra("adRemark", payOrderInfoBean.getData().getAdRemark() + "");
        intent.putExtra("payType", rechargeAdapter.getPayType());
        intent.putExtra("totalAmount", payOrderInfoBean.getData().getOrder().getTotalAmount());
        intent.putExtra("payLimit", (int) currentLimit);
        intent.putExtra("transReferNum", payOrderInfoBean.getData().getOrder().getTransReferNum());
//                    intent.putExtra("payBean", payOrderInfoBean.getData().getPayMent());
        intent.putExtra("payBean1", rechargeAdapter.getPaymentAccount());
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppHelper.unsubscribe(ms);
        EventBus.getDefault().unregister(this);
    }

    private void contactMerchant() {
        if (payOrderInfoBean == null || payOrderInfoBean.getData() == null || payOrderInfoBean.getData().getOrder() == null) {
            Log.e(TAG, "order is null ? ");
            //TODO show
            return;
        }
        if (!payOrderInfoBean.getData().getOrder().isCoinWAd()) {
            clearDot();
            DialogUtils.getInstance().setOnclickListener(null);
            DialogUtils.getInstance().showOneButtonDialog(OtcPaymentActivity.this, getString(R.string.no_chat), getString(R.string.b38));
            findViewById(R.id.img).setVisibility(View.GONE);
            return;
        }
        //获得商家的IM通道名称
        NIMHelper.contactMerchant(this, payOrderInfoBean, getTime());
        isVisioble = false;
    }

    //取消订单
    void cancelOrder() {
        Map<String, String> params = new HashMap<>();
        params.put("orderId", orderId + "");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.cancel_order, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                DialogManager.INSTANCE.dismiss();
                SnackBarUtils.ShowRed(OtcPaymentActivity.this, errorMsg);
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    DialogManager.INSTANCE.dismiss();
                    BaseBean baseBean = new Gson().fromJson(result, BaseBean.class);
                    if (baseBean.getCode() == 0) {
                        if (payOrderInfoBean.getData().getOrder() != null) {
                            Intent intent = new Intent(OtcPaymentActivity.this, OtcOrderCancleDetailActivity.class);
                            intent.putExtra("orderId", orderId);
                            intent.putExtra("tradeType", 1);
                            startActivity(intent);
                        }
                        finish();
                    } else {
                    }
                    MToast.show(OtcPaymentActivity.this, baseBean.getValue(), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isVisioble = true;
        if (isOrderCancel) {
            cancelOrderAction();
        }
    }

    public void showDot() {
        if (!isShowDot) {
            return;
        }
        if (null == payOrderInfoBean || payOrderInfoBean.getData() == null) {
            return;
        }
        ImDotUtils.showDot(findViewById(R.id.dot), String.valueOf(payOrderInfoBean.getData().getOrder().getId()));
    }

    public void clearDot() {
        try {
            if (!payOrderInfoBean.getData().getOrder().isCoinWAd()) {
                isShowDot = false;
                findViewById(R.id.dot).setVisibility(View.GONE);
            }
        } catch (Throwable t) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDot();
    }

    /**
     * 更新红点
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void notifyDot(Event.IMMessage exit) {
        showDot();
    }
}
