package huolongluo.byw.reform.c2c.oct.activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.databinding.ActivityOtccompBinding;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.c2c.oct.bean.BaseBean;
import huolongluo.byw.reform.c2c.oct.bean.OrderDetailBean;
import huolongluo.byw.reform.c2c.oct.bean.PayOrderInfoBean;
import huolongluo.byw.reform.c2c.oct.utils.ImDotUtils;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.DateUtils;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.NIMHelper;
import huolongluo.bywx.OTCOrderHelper;
import okhttp3.Request;
/**
 * Created by Administrator on 2019/5/15 0015.
 */
public class OtcTradeCompleteActivity extends BaseActivity implements View.OnClickListener {
    private ActivityOtccompBinding binding;
    int orderId;
    int tradeType;//1，购买，2，出售
    private OrderDetailBean orderDetailBean;
    private boolean foreground = false;
    private boolean isShowDot = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otccomp);
        if (getIntent() != null) {
            orderId = getIntent().getIntExtra("orderId", 0);
            tradeType = getIntent().getIntExtra("tradeType", 0);
            clearDot();
        }
        if (tradeType == 1) {
            binding.payName.setText(R.string.qa6);
            binding.tishiTv.setText(R.string.qa3);
        } else {
            //   binding.nameTv.setText("买家");
            binding.payName.setText(R.string.qa4);
            binding.tishiTv.setText(R.string.qa5);
        }
        viewClick(binding.include.backIv, v -> finish());
        viewClick(binding.copyIv1, v -> {
            NorUtils.copeText(this, binding.orderIdTv.getText().toString());
            MToast.showButton(this, getString(R.string.xx14), 1);
        });
        viewClick(binding.copyIv2, v -> {
            NorUtils.copeText(this, binding.transReferNumTv.getText().toString());
            MToast.showButton(this, getString(R.string.xx14), 1);
        });
        viewClick(binding.callPhoneLl, v -> {
            contactMerchant();
        });
        viewClick(binding.confirmTv, v -> {
            finish();
        });
        viewClick(binding.cancleTv, v -> {
            complain_check();
            //去申诉
           /* if (orderDetailBean != null && orderDetailBean.getData() != null) {

                if (orderDetailBean.getData().getCanComplaint() == 1) {
                    Intent intent = new Intent(this, OtcAppealActivity.class);
                    intent.putExtra("orderId", orderDetailBean.getData().getOrders().getId());

                    startActivity(intent);
                } else {
                    MToast.show(OtcTradeCompleteActivity.this, "超过可申诉时间", 1);
                }

            }
*/
        });
        showDot();
    }

    //申诉
    void complain_check() {
        Map<String, String> params = new HashMap<>();
        params.put("orderId", orderDetailBean.getData().getOrders().getId() + "");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.complain_check, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                DialogManager.INSTANCE.dismiss();
                SnackBarUtils.ShowRed(OtcTradeCompleteActivity.this, errorMsg);
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                try {
                    BaseBean baseBean = new Gson().fromJson(result, BaseBean.class);
                    if (baseBean.getCode() == 0) {
                        BaseApp.collectActivity(OtcTradeCompleteActivity.this);
                        Intent intent = new Intent(OtcTradeCompleteActivity.this, OtcAppealActivity.class);
                        intent.putExtra("orderId", orderId);
                        startActivity(intent);
                    } else {
                        MToast.show(OtcTradeCompleteActivity.this, baseBean.getValue(), 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
                DialogManager.INSTANCE.dismiss();
                SnackBarUtils.ShowRed(OtcTradeCompleteActivity.this, errorMsg);
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                try {
                    orderDetailBean = new Gson().fromJson(result, OrderDetailBean.class);
                    if (orderDetailBean != null && orderDetailBean.getCode() == 0) {
                        updateUI();
                    } else {
                        MToast.show(OtcTradeCompleteActivity.this, orderDetailBean.getValue(), 1);
                    }
                } catch (Exception e) {
                }
                showDot();
            }
        });
    }

    void updateUI() {
        if (orderDetailBean.getData() != null) {
            binding.orderIdTv.setText(orderDetailBean.getData().getOrders().getOrderNo() + "");
//            binding.userNameTv.setText(orderDetailBean.getData().getOppositeUserName() + "");
            binding.amountTv.setText(orderDetailBean.getData().getOrders().getTotalAmount() + " CNY");
            binding.priceTv.setText(orderDetailBean.getData().getOrders().getPrice() + " CNY/" + orderDetailBean.getData().getOrders().getCoinName());
            binding.numTv.setText(orderDetailBean.getData().getOrders().getAmount() + " " + orderDetailBean.getData().getOrders().getCoinName());
            binding.transReferNumTv.setText(orderDetailBean.getData().getOrders().getTransReferNum() + "");
            if (!TextUtils.isEmpty(orderDetailBean.getData().getComplaintCompleteTips())) {
                binding.wranTv.setText(orderDetailBean.getData().getComplaintCompleteTips() + "");
            }
            if (orderDetailBean.getData().getOrders().getCreateTime() != null) {
                binding.createTimeTv.setText(DateUtils.format(orderDetailBean.getData().getOrders().getCreateTime().getTime(), "yyyy-MM-dd HH:mm:ss") + "");
            }
            //判断：如果是UUEX商家，完成订单后，不可以申诉，也没有申诉的文案显示
            if (orderDetailBean.getData().getOrders() != null && !orderDetailBean.getData().getOrders().isCoinWAd()) {
                clearDot();
                //DialogUtils.getInstance().setOnclickListener(null);
                //DialogUtils.getInstance().showOneButtonDialog(OtcTradeCompleteActivity.this, getString(R.string.no_chat), getString(R.string.b38));
                binding.wranTv.setVisibility(View.GONE);
                binding.cancleTv.setVisibility(View.GONE);
            }else{
                binding.wranTv.setVisibility(View.VISIBLE);
                binding.cancleTv.setVisibility(View.VISIBLE);
            }
            if (OTCOrderHelper.refreshOrderStatus(this, orderDetailBean)) {
                //说明已经刷新跳转了界面，则返回
                return;
            }
            PayOrderInfoBean.PayAccountsBean buyerSelectedSellerPayType = orderDetailBean.getData().getOrders().getBuyerSelectedSellerPayType();
            switch (buyerSelectedSellerPayType.getType()) {
                case Constant.BANK:
                    String acount1 = String.format(buyerSelectedSellerPayType.getBankName() + "(%s)", buyerSelectedSellerPayType.getAccount().substring(buyerSelectedSellerPayType.getAccount().length() - 4));
                    initPayInfo(buyerSelectedSellerPayType, R.mipmap.bank_cark, acount1);
                    break;
                case Constant.WECHAT:
                    String acount2 = String.format(getString(R.string.qa1), buyerSelectedSellerPayType.getAccount().substring(0, 4));
                    initPayInfo(buyerSelectedSellerPayType, R.mipmap.wx, acount2);
                    break;
                case Constant.ALIPAY:
                    String acount3 = String.format(getString(R.string.qa2), buyerSelectedSellerPayType.getAccount().substring(0, 4));
                    initPayInfo(buyerSelectedSellerPayType, R.mipmap.zfb, acount3);
                    break;
            }
        }
    }

    private boolean isForeground() {
        return foreground;
    }

    private void initPayInfo(PayOrderInfoBean.PayAccountsBean buyerSelectedSellerPayType, int ico, String acount) {
        binding.payIv.setImageResource(ico);
        binding.bankNum.setText(acount);
        binding.realName.setText(buyerSelectedSellerPayType.getRealName());
    }

    @Override
    public void onClick(View v) {
    }

    private void contactMerchant() {
        if (orderDetailBean == null || orderDetailBean.getData() == null || orderDetailBean.getData().getOrders() == null) {
            //TODO show
            return;
        }
        if (!orderDetailBean.getData().getOrders().isCoinWAd()) {
            clearDot();
            DialogUtils.getInstance().setOnclickListener(null);
            DialogUtils.getInstance().showOneButtonDialog(OtcTradeCompleteActivity.this, getString(R.string.no_chat), getString(R.string.b38));
            return;
        }
        //获得商家的IM通道名称
        NIMHelper.contactMerchant(this, orderDetailBean, 0);
    }

    public void showDot() {
        if (!isShowDot) {
            return;
        }
        if (null == orderDetailBean || orderDetailBean.getData() == null) return;
        ImDotUtils.showDot(findViewById(R.id.dot), String.valueOf(orderDetailBean.getData().getOrders().getId()));
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

    @Override
    protected void onResume() {
        super.onResume();
        foreground = true;
        getOrder();
        showDot();
    }

    @Override
    protected void onPause() {
        foreground = false;
        super.onPause();
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
