package huolongluo.byw.reform.c2c.oct.activity.sellactivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcodes.utils.TimeUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.databinding.ActivityOtcuserwaitotherpayBinding;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.c2c.adapter.RechargeAdapter;
import huolongluo.byw.reform.c2c.oct.activity.OtcOrderCancleDetailActivity;
import huolongluo.byw.reform.c2c.oct.bean.OrderDetailBean;
import huolongluo.byw.reform.c2c.oct.bean.PayOrderInfoBean;
import huolongluo.byw.reform.c2c.oct.utils.ImDotUtils;
import huolongluo.byw.user.UserInfoManager;
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
 * Created by Administrator on 2019/3/14 0014.
 */
public class OtcUserSellWaitOtherPayActivity extends BaseActivity implements View.OnClickListener {
    ActivityOtcuserwaitotherpayBinding mBinding;
    String orderId;
    int payType;
    int type;
    double totalAmount;
    int payLimit;
    int transReferNum;
    PayOrderInfoBean.DataBean.PayMentBean payBean;
    String qrcodeUrl;
    Subscription ms;
    Subscription ms1;
    private Timer timer;
    String complaintPayedTips;
    private PayOrderInfoBean payOrderInfoBean;
    private boolean foreground = false;
    private boolean isShowDot = true;
    private List<PayOrderInfoBean.PayAccountsBean> dataList = new ArrayList<>();
    private RechargeAdapter rechargeAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_otcuserwaitotherpay);
        // setContentView(R.layout.activity_otcbuyconfirm);
        mBinding.include.backIv.setOnClickListener(this);
        viewClick(mBinding.copyIv2, v -> {
            NorUtils.copeText(this, mBinding.accountTv.getText().toString());
            MToast.showButton(this, getString(R.string.b2), 1);
        });
        viewClick(mBinding.copyIv3, v -> {
            NorUtils.copeText(this, mBinding.transReferNumTv.getText().toString());
            MToast.showButton(this, getString(R.string.b2), 1);
        });
        viewClick(mBinding.copyIv4, v -> {
            NorUtils.copeText(this, mBinding.orderIdTv.getText().toString());
            MToast.showButton(this, getString(R.string.b2), 1);
        });
        viewClick(mBinding.callPhoneLl, v -> {
            contactMerchant();
        });
        if (getIntent() != null) {
            orderId = getIntent().getStringExtra("orderId");
            type = getIntent().getIntExtra("type", 0);
        }
        payOrder(orderId + "");
        ms1 = Observable.interval(0, 15, TimeUnit.SECONDS).limit(6000).map(aLong -> 6000 - aLong).doOnSubscribe(() -> {
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
            getOrder();//轮训查询状态
        }).onErrorReturn(throwable -> {
            Logger.getInstance().errorLog(throwable);
            return 15L;
        }).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe();
        viewClick(mBinding.qrCodeRl, v -> {//显示二维码
            mBinding.qrcodeFl.setVisibility(View.VISIBLE);
            //   DisplayImageUtils.displayImage(mBinding.qrCodeIv, qrcodeUrl, this, 0, R.mipmap.add_bank_card, true, false);
//            ImageLoader.getInstance().displayImage(qrcodeUrl, mBinding.qrCodeIv);
            Glide.with(this).load(qrcodeUrl).into(mBinding.qrCodeIv);
        });
        viewClick(mBinding.qrCodecloseIv, v -> {//关闭付款二维码
            mBinding.qrcodeFl.setVisibility(View.GONE);
        });
        viewClick(mBinding.saveqrcodeTv, v -> {//保存二维码到本地
            DialogManager.INSTANCE.showProgressDialog(this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (qrcodeUrl != null && !TextUtils.isEmpty(qrcodeUrl)) {
                        // bitmap = getBitMBitmap(qrcodeUrl);
//                        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(qrcodeUrl);
                        //      bitmap=((BitmapDrawable) ( mBinding.qrCodeIv).getDrawable()).getBitmap();
                        Bitmap bitmap = null;
                        try {
                            bitmap = Glide.with(OtcUserSellWaitOtherPayActivity.this).asBitmap().load(qrcodeUrl).submit().get();
                        } catch (Throwable t) {
                        }
                        if (bitmap != null) {
                            saveImageToGallery(OtcUserSellWaitOtherPayActivity.this, bitmap);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogManager.INSTANCE.dismiss();
                                    MToast.show(OtcUserSellWaitOtherPayActivity.this, getString(R.string.d5), 2);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogManager.INSTANCE.dismiss();
                                    MToast.show(OtcUserSellWaitOtherPayActivity.this, getString(R.string.d6), 2);
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogManager.INSTANCE.dismiss();
                                MToast.show(OtcUserSellWaitOtherPayActivity.this, getString(R.string.d6), 2);
                            }
                        });
                    }
                }
            }).start();
        });
        rechargeAdapter = new RechargeAdapter(this, dataList, false, false);
        mBinding.rechargeMethodList.setAdapter(rechargeAdapter);
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

    //保存文件到指定路径
    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        //  String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dearxy";
        String storePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "Camera" + File.separator;
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            //把文件插入到系统图库
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            // MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, fileName, null);
            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    void payOrder(String orderId) {
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
                SnackBarUtils.ShowRed(OtcUserSellWaitOtherPayActivity.this, errorMsg);
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                try {
                    payOrderInfoBean = new Gson().fromJson(result, PayOrderInfoBean.class);
                    if (payOrderInfoBean.getCode() == 0) {
                        clearDot();
                        payType = payOrderInfoBean.getData().getOrder().getPayType();
                        payLimit = payOrderInfoBean.getData().getPayLimit();
                        transReferNum = payOrderInfoBean.getData().getOrder().getTransReferNum();
                        totalAmount = payOrderInfoBean.getData().getOrder().getTotalAmount();
                        payBean = payOrderInfoBean.getData().getPayMent();
                        complaintPayedTips = payOrderInfoBean.getData().getComplaintPayedTips();
                        upUI();
                    } else {
                        MToast.show(OtcUserSellWaitOtherPayActivity.this, payOrderInfoBean.getValue(), 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showWarnDialog(List<PayOrderInfoBean.DataBean.OrderBean.MessageListBean> messageList) {
        if (null != messageList && messageList.size() != 0) {
            DialogUtils.getInstance().showOneButtonDialog(this, messageList.get(0).getValue(), "我知道了");
        }
    }

    void upUI() {
        showDot();
        if (payLimit > 1) {
            ms = Observable.interval(0, 1, TimeUnit.SECONDS).limit(payLimit + 1).map(aLong -> payLimit - aLong).doOnSubscribe(() -> {
            }).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
                if (mBinding == null || mBinding.timeTv == null) {
                    return;
                }
                mBinding.timeTv.setText(TimeUtils.millis2String(aLong * 1000, "mm:ss"));
                //  mBinding.timeLimitTv.setText(TimeUtils.millis2String(aLong * 1000, "mm分ss秒"));
                if (aLong == 1) {
                    mBinding.timeTv.setText(TimeUtils.millis2String(0, "mm:ss"));
                    // mBinding.timeLimitTv.setText(TimeUtils.millis2String(0, "mm分ss秒"));
                }
            }).onErrorReturn(throwable -> {
                Logger.getInstance().errorLog(throwable);
                return 1L;
            }).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe();
        }
        mBinding.amountTv.setText(totalAmount + " CNY");
        mBinding.orderIdTv.setText(payOrderInfoBean.getData().getOrder().getOrderNo() + "");
        mBinding.transReferNumTv.setText(transReferNum + "");
        if (type == 1) {//客户出售等待付款  0 为商家出售等待付款
            mBinding.paymentWayView.setVisibility(View.GONE);
            mBinding.payInfoView.setVisibility(View.VISIBLE);
            mBinding.nameTv.setText(payOrderInfoBean.getData().getOrder().getUserName());
            mBinding.money.setText(String.format("%s CNY/%s", payOrderInfoBean.getData().getOrder().getPrice(), payOrderInfoBean.getData().getOrder().getCoinName()));
            mBinding.count.setText(String.format("%s%s", payOrderInfoBean.getData().getOrder().getAmount(), payOrderInfoBean.getData().getOrder().getCoinName()));
            payBean = payOrderInfoBean.getData().getPayMent();
        } else {
            mBinding.paymentWayView.setVisibility(View.VISIBLE);
            mBinding.payInfoView.setVisibility(View.GONE);
            initRechargeList(payOrderInfoBean.getData());
        }
    }

    void getOrder() {
        Map<String, String> params = new HashMap<>();
        params.put("orderId", orderId + "");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.get_order, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    OrderDetailBean orderDetailBean = new Gson().fromJson(result, OrderDetailBean.class);
                    if (orderDetailBean.getCode() == 0 && isForeground()) {
                        if (orderDetailBean.getData().getOrders().getStatus() == 1) {
                            Intent intent = new Intent(OtcUserSellWaitOtherPayActivity.this, OtcUserSellOtherPayedActivity.class);
                            // Intent intent = new Intent(getAContext(), OtcTradeCompleteActivity.class);
                            intent.putExtra("orderId", orderDetailBean.getData().getOrders().getId());
                            startActivity(intent);
                            finish();
                        } else if (orderDetailBean.getData().getOrders().getStatus() == 3 || orderDetailBean.getData().getOrders().getStatus() == 4) {
                            Intent intent = new Intent(OtcUserSellWaitOtherPayActivity.this, OtcOrderCancleDetailActivity.class);
                            intent.putExtra("orderId", orderDetailBean.getData().getOrders().getId());
                            intent.putExtra("tradeType", 2);//出售
                            startActivity(intent);
                            finish();
                        }
                    }
                    showDot();
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppHelper.unsubscribe(ms);
        AppHelper.unsubscribe(ms1);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                finish();
                break;
        }
    }

    private void contactMerchant() {
        if (payOrderInfoBean == null || payOrderInfoBean.getData() == null || payOrderInfoBean.getData().getOrder() == null) {
            //TODO show
            return;
        }
        if (!payOrderInfoBean.getData().getOrder().isCoinWAd()) {
            clearDot();
            DialogUtils.getInstance().setOnclickListener(null);
            DialogUtils.getInstance().showOneButtonDialog(OtcUserSellWaitOtherPayActivity.this, getString(R.string.no_chat), getString(R.string.b38));
            return;
        }
        //获得商家的IM通道名称
        NIMHelper.contactMerchant(this, payOrderInfoBean, 0);
    }

    /**
     * 更新红点
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void notifyDot(Event.IMMessage exit) {
        showDot();
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

    private boolean isForeground() {
        return foreground;
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
}
