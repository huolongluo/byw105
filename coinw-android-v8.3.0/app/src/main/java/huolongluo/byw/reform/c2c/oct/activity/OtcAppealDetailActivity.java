package huolongluo.byw.reform.c2c.oct.activity;

import android.app.AlertDialog;
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
import huolongluo.byw.databinding.ActivityOtcaooealdetailBinding;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.c2c.oct.bean.BaseBean;
import huolongluo.byw.reform.c2c.oct.bean.OrderDetailBean;
import huolongluo.byw.reform.c2c.oct.bean.OtcUserInfoBean;
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
 * Created by dell on 2019/6/11.
 */

public class OtcAppealDetailActivity extends BaseActivity {

    ActivityOtcaooealdetailBinding binding;

    int orderId;
    private OrderDetailBean orderDetailBean;
    private boolean foreground = false;
    private boolean isShowDot = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otcaooealdetail);
        if (getIntent() != null) {
            orderId = getIntent().getIntExtra("orderId", 0);
        }

        viewClick(binding.include.backIv, v -> finish());
        viewClick(binding.contactLl, v -> {
            //联系对方
            //orderDetailBean.getData().getInformation();//电话
            contactMerchant();
        });
        viewClick(binding.cancelAction, v -> {
            if (null != orderDetailBean && null != orderDetailBean.getData() && null != orderDetailBean.getData().getOrders()) {
//                Intent intent = new Intent(this, OtcCancleAppealActivity.class);
//                intent.putExtra("orderId", orderDetailBean.getData().getOrders().getId());
//                startActivity(intent);
                DialogUtils.getInstance().showCancelAppealDialog(this, new DialogUtils.onBnClickListener() {
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
                        cancelAppeal();
                    }
                });

            }
        });

        viewClick(binding.copyIv1, v -> {

            NorUtils.copeText(this, binding.orderIdTv.getText().toString());
            MToast.showButton(this, getString(R.string.cx48), 1);

        });

        viewClick(binding.copyIv2, v -> {

            NorUtils.copeText(this, binding.transReferNumTv.getText().toString());
            MToast.showButton(this, getString(R.string.cx48), 1);

        });
        viewClick(binding.copyIv3, v -> {

            NorUtils.copeText(this, binding.otherNameTv.getText().toString());
            MToast.showButton(this, getString(R.string.cx48), 1);

        });
        viewClick(binding.copyIv4, v -> {

            NorUtils.copeText(this, binding.accountNameTv.getText().toString());
            MToast.showButton(this, getString(R.string.cx48), 1);

        });
        viewClick(binding.copyIv5, v -> {

            NorUtils.copeText(this, binding.bankNameTv.getText().toString());
            MToast.showButton(this, getString(R.string.cx48), 1);

        });
        viewClick(binding.copyIv6, v -> {

            NorUtils.copeText(this, binding.bankAdressTv.getText().toString());
            MToast.showButton(this, getString(R.string.cx48), 1);

        });

        viewClick(binding.copyIv7, v -> {

            NorUtils.copeText(this, binding.transReferNum1Tv.getText().toString());
            MToast.showButton(this, getString(R.string.cx48), 1);

        });
        viewClick(binding.contactLl, v -> {

            if (orderDetailBean != null) {
//                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + orderDetailBean.getData().getInformation()));//跳转到拨号界面，同时传递电话号码
//                startActivity(dialIntent);
                contactMerchant();
            }

        });

    }

    private void cancelAppeal() {
        cancel_complain();
    }

    private void contactMerchant() {
        if (orderDetailBean == null || orderDetailBean.getData() == null || orderDetailBean.getData().getOrders() == null) {
            Log.e("OtcPrepaidActivity", "order is null ? ");
            //TODO show
            SnackBarUtils.ShowRed(OtcAppealDetailActivity.this, getString(R.string.cx55));
            return;
        }
        if (!orderDetailBean.getData().getOrders().isCoinWAd()) {
            clearDot();
            DialogUtils.getInstance().setOnclickListener(null);
            DialogUtils.getInstance().showOneButtonDialog(OtcAppealDetailActivity.this, getString(R.string.no_chat), getString(R.string.b38));
            return;
        }
        //获得商家的IM通道名称
        NIMHelper.contactMerchant(this, orderDetailBean);
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
    public void clearDot() {
        try {
            if (!orderDetailBean.getData().getOrders().isCoinWAd()) {
                isShowDot = false;
                findViewById(R.id.dot).setVisibility(View.GONE);
            }
        } catch (Throwable t) {

        }
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
                SnackBarUtils.ShowRed(OtcAppealDetailActivity.this, errorMsg);
                DialogManager.INSTANCE.dismiss();
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    orderDetailBean = new Gson().fromJson(result, OrderDetailBean.class);
                    if (orderDetailBean != null && orderDetailBean.getCode() == 0) {
                        clearDot();
                        updateUI();
                    } else {
                        MToast.show(OtcAppealDetailActivity.this, orderDetailBean.getValue(), 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SnackBarUtils.ShowRed(OtcAppealDetailActivity.this, getString(R.string.cx56));
                }
                DialogManager.INSTANCE.dismiss();
                showDot();
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
            binding.transReferNum1Tv.setText(orderDetailBean.getData().getOrders().getTransReferNum() + "");


            binding.cancelAction.setVisibility(View.GONE);
            OrderDetailBean.DataBean.ComplaintInfoBean ci = orderDetailBean.getData().getComplaintInfo();
            if (ci != null) {
                if (ci.getUid() == UserInfoManager.getUserInfo().getFid()) {
                    switch (orderDetailBean.getData().getComplaintInfo().getStatus()) {
                        case 3:
                            binding.shensuName.setText(R.string.cx57);
                            break;
                        case 1:
                            binding.shensuName.setText(R.string.cx58);
                            binding.cancelAction.setVisibility(View.VISIBLE);
                            break;
                    }

                } else {
                    switch (ci.getStatus()) {
                        case 3:
                            binding.shensuName.setText(R.string.cx59);
                            break;
                        case 1:
                            binding.shensuName.setText(R.string.cx60);
                            binding.cancelAction.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            } else {
                if (OTCOrderHelper.refreshOrderStatus(this, orderDetailBean)) {
                    //说明已经刷新跳转了界面，则返回
                    return;
                }
            }
//            if (orderDetailBean.getData().getCanComplaint() == 0) {
//                binding.shensuName.setText("对方提出申诉");
//            } else {
//                binding.shensuName.setText("您已提出申诉");
//            }

            if (orderDetailBean.getData().getOrders().getCreateTime() != null) {
                binding.createTimeTv.setText(DateUtils.format(orderDetailBean.getData().getOrders().getCreateTime().getTime(), "yyyy-MM-dd HH:mm:ss") + "");
            }
            PayOrderInfoBean.PayAccountsBean buyerSelectedSellerPayType = orderDetailBean.getData().getOrders().getBuyerSelectedSellerPayType();
            binding.otherNameTv.setText(orderDetailBean.getData().getOrders().getBuyerSelectedSellerPayType().getRealName());
            binding.accountTv.setText(orderDetailBean.getData().getOrders().getBuyerSelectedSellerPayType().getAccount());
            binding.bankNameTv.setText(orderDetailBean.getData().getOrders().getBuyerSelectedSellerPayType().getBankName() + "");
            binding.bankAdressTv.setText(orderDetailBean.getData().getOrders().getBuyerSelectedSellerPayType().getBankAdress() + "");
            switch (buyerSelectedSellerPayType.getType()) {
                case Constant.BANK:
                    binding.payTypeTv.setText(R.string.cx61);
                    binding.payTypeIv.setImageResource(R.mipmap.bank_cark);
                    binding.accountNameTv.setText(R.string.cx62);
                    binding.bankNameLl.setVisibility(View.VISIBLE);
                    binding.bankAdressLl.setVisibility(View.VISIBLE);
                    break;
                case Constant.WECHAT:
                    binding.payTypeTv.setText(R.string.cx63);
                    binding.accountNameTv.setText(R.string.cx64);
                    binding.payTypeIv.setImageResource(R.mipmap.wx);
                    binding.bankNameLl.setVisibility(View.GONE);
                    binding.bankAdressLl.setVisibility(View.GONE);
                    break;
                case Constant.ALIPAY:
                    binding.payTypeTv.setText(R.string.cx65);
                    binding.accountNameTv.setText(R.string.cx66);
                    binding.payTypeIv.setImageResource(R.mipmap.zfb);
                    binding.bankNameLl.setVisibility(View.GONE);
                    binding.bankAdressLl.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private boolean isSeller() {
        //买方：
        //登陆人ID=uid      并且 type=1
        //登陆人ID=adUserId 并且 type=2
        //卖方：
        //登陆人ID=uid      并且 type=2
        //登陆人ID=adUserId 并且 type=1
        int uid = orderDetailBean.getData().getOrders().getUid();
        int type = orderDetailBean.getData().getOrders().getType();
        int adUserId = orderDetailBean.getData().getOrders().getAdUserId();
        OtcUserInfoBean ouib = UserInfoManager.getOtcUserInfoBean();
        int cuid = ouib.getData().getOtcUser().getUid();
        if (uid == cuid && type == 2) {
            return true;
        } else if (adUserId == cuid && type == 1) {
            return true;
        }
        return false;
    }

    private boolean isForeground() {
        return foreground;
    }

    public void showDot() {
        if(!isShowDot){
            return;
        }
        if (null == orderDetailBean || orderDetailBean.getData() == null) return;
        ImDotUtils.showDot(findViewById(R.id.dot), String.valueOf(orderDetailBean.getData().getOrders().getId()));
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

    public void cancel_complain() {
        Map<String, String> params = new HashMap<>();
        params.put("orderId", orderId + "");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken() + "");
        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.cancel_complain, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                SnackBarUtils.ShowRed(OtcAppealDetailActivity.this, "" + errorMsg);
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    DialogManager.INSTANCE.dismiss();
                    BaseBean baseBean = new Gson().fromJson(result, BaseBean.class);
                    if (baseBean.getCode() == 0) {
                        finish();
                    } else {

                    }
                    MToast.show(OtcAppealDetailActivity.this, baseBean.getValue(), 1);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        });
    }

}
