package huolongluo.byw.reform.c2c.oct.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.databinding.ActivityOtcordercancledetailBinding;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.c2c.oct.bean.OrderDetailBean;
import huolongluo.byw.reform.c2c.oct.utils.ImDotUtils;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.DateUtils;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.NIMHelper;
import okhttp3.Request;

/**
 * Created by Administrator on 2019/5/15 0015.
 */
public class OtcOrderCancleDetailActivity extends BaseActivity implements View.OnClickListener {
    private ActivityOtcordercancledetailBinding binding;
    int orderId;
    int tradeType;//1,购买，2出售
    private OrderDetailBean orderDetailBean;
    private boolean isShowDot = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otcordercancledetail);
        if (getIntent() != null) {
            orderId = getIntent().getIntExtra("orderId", 0);
            tradeType = getIntent().getIntExtra("tradeType", 0);
        }
        if (tradeType == 1) {
            //   binding.tradeTypeTv.setText("卖家");
        } else {
            //  binding.tradeTypeTv.setText("买家");
        }
        viewClick(binding.backIv, v -> finish());
        viewClick(binding.copyIv1, v -> {
            Logger.getInstance().debug("CancelDetail1", "复制成功", new Exception());
            NorUtils.copeText(this, binding.orderIdTv.getText().toString());
            MToast.showButton(this, "复制成功", 1);
        });
        viewClick(binding.rightContact, v -> {
            Logger.getInstance().debug("CancelDetail", "orderDetailBean", new Exception());
            //获得商家的IM通道名称
            contactMerchant(orderDetailBean);
        });
        viewClick(binding.copyIv2, v -> {
            Logger.getInstance().debug("CancelDetail2", "复制成功", new Exception());
            NorUtils.copeText(this, binding.transReferNumTv.getText().toString());
            MToast.showButton(this, getString(R.string.xx6), 1);
        });
        getOrder();
    }

    public void clearDot() {
        try {
            if (!orderDetailBean.getData().getOrders().isCoinWAd()) {
                isShowDot = false;
                findViewById(R.id.dot).setVisibility(View.GONE);
            }
        } catch (Throwable t) {

        }
    }

    private void contactMerchant(OrderDetailBean orderDetail) {
//        String account = "android_test";
//        String pwd = "s123456";
//        NIMHelper.login(this, account, pwd);
        if (orderDetail == null || orderDetail.getData() == null || orderDetail.getData().getOrders() == null) {
            Log.e("otccd", "order is null ? ");
            //TODO show
            return;
        }
        if (!orderDetailBean.getData().getOrders().isCoinWAd()) {
            clearDot();
            DialogUtils.getInstance().showOneButtonDialog(OtcOrderCancleDetailActivity.this, getString(R.string.no_chat), getString(R.string.b38));
            return;
        }
        //获得商家的IM通道名称
        NIMHelper.contactMerchant(this, orderDetail);
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
                SnackBarUtils.ShowRed(OtcOrderCancleDetailActivity.this, errorMsg);
                e.printStackTrace();
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    DialogManager.INSTANCE.dismiss();
                    orderDetailBean = new Gson().fromJson(result, OrderDetailBean.class);
                    if (orderDetailBean != null && orderDetailBean.getCode() == 0) {
                        clearDot();
                        updateUI();
                    } else {
                        MToast.show(OtcOrderCancleDetailActivity.this, orderDetailBean.getValue(), 1);
                    }
                } catch (Exception e) {
                }
                showDot();
            }
        });
    }

    void updateUI() {
        if (orderDetailBean.getData() != null) {
            if (orderDetailBean.getData().getOrders() != null) {
                binding.rightContact.setVisibility(orderDetailBean.getData().getOrders().getSellerPayTypes() != null && orderDetailBean.getData().getOrders().getSellerPayTypes().size() != 0 ? View.VISIBLE : View.GONE);
                binding.orderIdTv.setText(orderDetailBean.getData().getOrders().getOrderNo() + "");
                binding.userNameTv.setText(orderDetailBean.getData().getOppositeUserName() + "");
                binding.amountTv.setText(orderDetailBean.getData().getOrders().getTotalAmount() + " CNY");
                binding.priceTv.setText(orderDetailBean.getData().getOrders().getPrice() + " CNY/" + orderDetailBean.getData().getOrders().getCoinName());
                binding.numTv.setText(orderDetailBean.getData().getOrders().getAmount() + " " + orderDetailBean.getData().getOrders().getCoinName());
                binding.transReferNumTv.setText(orderDetailBean.getData().getOrders().getTransReferNum() + "");
                if (orderDetailBean.getData().getOrders().getCreateTime() != null) {
                    binding.createTimeTv.setText(DateUtils.format(orderDetailBean.getData().getOrders().getCreateTime().getTime(), "yyyy-MM-dd HH:mm:ss") + "");
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
    }

    public void showDot() {
        if (!isShowDot) {
            return;
        }
        if (null == orderDetailBean || orderDetailBean.getData() == null) return;
        ImDotUtils.showDot(findViewById(R.id.dot), String.valueOf(orderDetailBean.getData().getOrders().getId()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDot();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 更新红点
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void notifyDot(Event.IMMessage exit) {
        showDot();
    }
}
