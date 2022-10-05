package huolongluo.byw.reform.c2c.oct.activity.sellactivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

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
import huolongluo.byw.databinding.ActivityOtcusersellpaymentBinding;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.c2c.adapter.RechargeAdapter;
import huolongluo.byw.reform.c2c.oct.activity.OtcOrderCancleDetailActivity;
import huolongluo.byw.reform.c2c.oct.bean.BaseBean;
import huolongluo.byw.reform.c2c.oct.bean.PayOrderInfoBean;
import huolongluo.byw.reform.c2c.oct.utils.ImDotUtils;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.TextUtils;
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
public class OtcUserSellPaymentActivity extends BaseActivity implements View.OnClickListener {
    ActivityOtcusersellpaymentBinding mBinding;
    int orderId;
    private PayOrderInfoBean payOrderInfoBean;
    //    int payType = -1;//1 银行卡, 2 微信, 3 支付宝
    private Subscription ms;
    private List<PayOrderInfoBean.PayAccountsBean> dataList = new ArrayList<>();
    private RechargeAdapter rechargeAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_otcusersellpayment);
        EventBus.getDefault().register(this);
        if (getIntent() != null) {
            orderId = getIntent().getIntExtra("data", 0);
        }
        mBinding.cancleTv.setOnClickListener(this);
        mBinding.included.backIv.setOnClickListener(this);
        viewClick(mBinding.copyIv1, v -> {
            NorUtils.copeText(this, mBinding.orderIdTv.getText().toString());
            MToast.showButton(this, getString(R.string.cx13), 1);
        });
        viewClick(mBinding.copyIv2, v -> {
            NorUtils.copeText(this, mBinding.remarkNoTv.getText().toString());
            MToast.showButton(this, getString(R.string.cx14), 1);
        });
        viewClick(mBinding.confirmTv, this);
        viewClick(mBinding.callPhoneLl, v -> {
            contactMerchant();
        });
        payOrder();
        rechargeAdapter = new RechargeAdapter(this, dataList, true);
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

    private void contactMerchant() {
        if (payOrderInfoBean == null || payOrderInfoBean.getData() == null || payOrderInfoBean.getData().getOrder() == null) {
            Log.e("OtcPrepaidActivity", "order is null ? ");
            //TODO show
            return;
        }
        if (!payOrderInfoBean.getData().getOrder().isCoinWAd()) {
            DialogUtils.getInstance().showOneButtonDialog(OtcUserSellPaymentActivity.this, getString(R.string.no_chat), getString(R.string.b38));
            return;
        }
        //获得商家的IM通道名称
        NIMHelper.contactMerchant(this, payOrderInfoBean);
    }

    //出售设置收款方式
    void order_payment() {
        Map<String, String> params = new HashMap<>();
        params.put("orderId", orderId + "");
        params.put("payType", rechargeAdapter.getPayType() + "");
//        StringBuilder selectAccount = new StringBuilder();
//        List<Integer> selectIndexList = rechargeAdapter.getPayTypes();
//        for (int i = 0; i < selectIndexList.size(); i++) {
//            selectAccount = selectAccount.
//                    append(payOrderInfoBean.getData().getPayAccounts().get(selectIndexList.get(i) - 1).getId()).
//                    append(",");
//        }
        String payAccounts = rechargeAdapter.getPayIds();
        //异常处理
        if (TextUtils.isChar(payAccounts)) {
            ToastUtils.showLongToast(getString(R.string.aa40));
            return;
        }
        params.put("payAccounts", rechargeAdapter.getPayIds());
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.order_payment, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                SnackBarUtils.ShowRed(OtcUserSellPaymentActivity.this, errorMsg);
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                BaseBean baseBean = new Gson().fromJson(result, BaseBean.class);
                if (baseBean.getCode() == 0) {
                    if (payOrderInfoBean != null) {
                        Intent intent = new Intent(OtcUserSellPaymentActivity.this, OtcUserSellWaitOtherPayActivity.class);
                        intent.putExtra("orderId", payOrderInfoBean.getData().getOrder().getId() + "");
                        intent.putExtra("type", 0);
                       /* intent.putExtra("payType", payType);
                        intent.putExtra("totalAmount", payOrderInfoBean.getData().getOrder().getTotalAmount());
                        intent.putExtra("complaintPayedTips", payOrderInfoBean.getData().getComplaintPayedTips());
                        intent.putExtra("payLimit", payOrderInfoBean.getData().getPayLimit());
                        intent.putExtra("transReferNum", payOrderInfoBean.getData().getOrder().getTransReferNum());
                        intent.putExtra("payBean", payOrderInfoBean.getData().getPayMent());*/
                        startActivity(intent);
                        finish();
                        return;
                    }
                }
                MToast.show(OtcUserSellPaymentActivity.this, baseBean.getValue(), 1);
            }
        });
    }

    //查询订单信息
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
                SnackBarUtils.ShowRed(OtcUserSellPaymentActivity.this, errorMsg);
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                try {
                    payOrderInfoBean = new Gson().fromJson(result, PayOrderInfoBean.class);
                    if (payOrderInfoBean.getCode() == 0) {
                        updataUI();
                    }
                    showDot();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void cancelCount() {
        int timeLimit = payOrderInfoBean.getData().getPayLimit();
        if (timeLimit > 1) {
            ms = Observable.interval(0, 1, TimeUnit.SECONDS).limit(timeLimit + 2).map(aLong -> timeLimit + 1 - aLong).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
                Log.e("doOnNextaad", "==  " + aLong);
                long time = aLong - 2 < 0 ? 0 : (aLong - 2) * 1000;
                mBinding.tv1.setText(TimeUtils.millis2String(time, "mm:ss"));
                if (aLong == 0) {
                    cancelOrderAction();
                }
            }).onErrorReturn(throwable -> {
                Logger.getInstance().errorLog(throwable);
                return 1L;
            }).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe();
        }
    }

    private void cancelOrderAction() {
        Intent intent = new Intent(this, OtcOrderCancleDetailActivity.class);
        intent.putExtra("orderId", orderId);
        intent.putExtra("tradeType", 1);
        startActivity(intent);
        finish();
    }

    void updataUI() {

        if (payOrderInfoBean != null && payOrderInfoBean.getData() != null) {
            if (payOrderInfoBean.getData().getOrder() != null) {
                showWarnDialog(payOrderInfoBean.getData().getOrder().getMessageList());
                mBinding.orderIdTv.setText(payOrderInfoBean.getData().getOrder().getOrderNo() + "");
                mBinding.orderNumTv.setText(payOrderInfoBean.getData().getOrder().getAmount() + "" + payOrderInfoBean.getData().getOrder().getCoinName());
                mBinding.priceTv.setText(payOrderInfoBean.getData().getOrder().getPrice() + " CNY/" + payOrderInfoBean.getData().getOrder().getCoinName());
                mBinding.totalPriceTv.setText(payOrderInfoBean.getData().getOrder().getTotalAmount() + " CNY");
                mBinding.remarkNoTv.setText(payOrderInfoBean.getData().getOrder().getTransReferNum() + "");
                mBinding.timeTv.setText(payOrderInfoBean.getData().getOrder().getCreateTime_s());
                initRechargeList(payOrderInfoBean.getData());
            }
            if (payOrderInfoBean.getData().getOtcUserinfo() != null) {
                mBinding.nickNameTv.setText(payOrderInfoBean.getData().getOrder().getAdUserName());
            }
        }
        cancelCount();
    }

    private void showWarnDialog(List<PayOrderInfoBean.DataBean.OrderBean.MessageListBean> messageList) {
        if (null != messageList && messageList.size() != 0) {
            DialogUtils.getInstance().showOneButtonDialog(this, messageList.get(0).getValue(), getString(R.string.str_isee));
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancle_tv:
                DialogUtils.getInstance().showCancleOtcSellDialog(OtcUserSellPaymentActivity.this, new DialogUtils.onBnClickListener() {
                    @Override
                    public void onLiftClick(AlertDialog dialog, View view) {
                        //Intent intent = new Intent(OtcPaymentActivity.this, OtcTradeStatusActivity.class);
                        //startActivity(intent);
                      /*  if(payOrderInfoBean.getData().getOrder()!=null){
                            Intent intent = new Intent(OtcPaymentActivity.this, OtcOrderCancleDetailActivity.class);
                            intent.putExtra("orderId", orderId);
                            startActivity(intent);
                        }*/
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
                    ToastUtils.showShortToast(R.string.cx15);
                    return;
                }
                order_payment();//设置优先收款方式
                break;
            case R.id.back_iv:
                finish();
                break;
        }
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
                SnackBarUtils.ShowRed(OtcUserSellPaymentActivity.this, errorMsg);
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                try {
                    BaseBean baseBean = new Gson().fromJson(result, BaseBean.class);
                    if (baseBean.getCode() == 0) {
                        if (payOrderInfoBean.getData().getOrder() != null) {
                            Intent intent = new Intent(OtcUserSellPaymentActivity.this, OtcOrderCancleDetailActivity.class);
                            intent.putExtra("orderId", orderId);
                            intent.putExtra("tradeType", 2);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                    }
                    MToast.show(OtcUserSellPaymentActivity.this, baseBean.getValue(), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 更新红点
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void notifyDot(Event.IMMessage exit) {
        showDot();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDot();
    }

    public void showDot() {
        if (null == payOrderInfoBean || payOrderInfoBean.getData() == null) {
            return;
        }
        ImDotUtils.showDot(findViewById(R.id.dot), String.valueOf(payOrderInfoBean.getData().getOrder().getId()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        AppHelper.unsubscribe(ms);
    }
}
