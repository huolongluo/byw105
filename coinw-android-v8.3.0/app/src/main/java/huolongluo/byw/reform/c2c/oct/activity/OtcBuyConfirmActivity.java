package huolongluo.byw.reform.c2c.oct.activity;
import android.app.AlertDialog;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.oneClickBuy.C2cStatus;
import huolongluo.byw.databinding.ActivityOtcbuyconfirmBinding;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.c2c.oct.bean.BaseBean;
import huolongluo.byw.reform.c2c.oct.bean.OrderCancelBean;
import huolongluo.byw.reform.c2c.oct.bean.OrderDetailBean;
import huolongluo.byw.reform.c2c.oct.bean.PayOrderInfoBean;
import huolongluo.byw.reform.c2c.oct.utils.ImDotUtils;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.NIMHelper;
import okhttp3.Request;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
/**
 * Created by Administrator on 2019/3/14 0014.
 */
public class OtcBuyConfirmActivity extends BaseActivity implements View.OnClickListener {
    private int cancelCount = 0;
    ActivityOtcbuyconfirmBinding mBinding;
    int orderId;
    int payType;
    String adRemark;
    double totalAmount;
    int payLimit;
    int transReferNum;
    PayOrderInfoBean.DataBean.PayMentBean payBean;
    String qrcodeUrl;
    Bitmap bitmap;
    Subscription ms;
    private PayOrderInfoBean.PayAccountsBean payBean1;
    private PayOrderInfoBean payOrderInfoBean;
    private Boolean fastTrade;
    private boolean isShowDot = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_otcbuyconfirm);
        EventBus.getDefault().register(this);
        // setContentView(R.layout.activity_otcbuyconfirm);
        if (getIntent() != null) {
            orderId = getIntent().getIntExtra("orderId", 0);
            adRemark = getIntent().getStringExtra("adRemark");
            payType = getIntent().getIntExtra("payType", 0);
            payLimit = getIntent().getIntExtra("payLimit", 60);
            transReferNum = getIntent().getIntExtra("transReferNum", 0);
            totalAmount = getIntent().getDoubleExtra("totalAmount", 0);
            payBean = getIntent().getParcelableExtra("payBean");
            fastTrade = getIntent().getBooleanExtra("fastTrade", false);
            payBean1 = (PayOrderInfoBean.PayAccountsBean) getIntent().getSerializableExtra("payBean1");
            payOrderInfoBean = C2cStatus.payOrderInfoBean;
            if (null == payOrderInfoBean) {
                payOrderInfoBean = OtcPaymentActivity.payOrderInfoBean;
            }
            clearDot();
        }
        if (fastTrade) {
            mBinding.nonPaymentTv.setText(R.string.a21);
        }
//        Log.i("orderId",orderId);
        mBinding.include.backIv.setOnClickListener(this);
        mBinding.adRemarkTv.setText(adRemark + "");
        viewClick(mBinding.copyIv1, v -> {
            NorUtils.copeText(this, mBinding.nameTv.getText().toString());
            MToast.showButton(this, getString(R.string.cx87), 1);
        });
        viewClick(mBinding.qrCodeRl, v -> {//显示付款二维码
            mBinding.qrcodeFl.setVisibility(View.VISIBLE);
            //      DisplayImageUtils.displayImage(mBinding.qrCodeIv, qrcodeUrl, this, 0, R.mipmap.rmblogo, true, false);
            // Glide.with(this).load(qrcodeUrl).error(R.mipmap.rmblogo).centerCrop().into((ImageView) mBinding.qrCodeIv);
//            ImageLoader.getInstance().displayImage(qrcodeUrl, mBinding.qrCodeIv);
            Glide.with(this).load(qrcodeUrl).into(mBinding.qrCodeIv);
        });
        viewClick(mBinding.saveqrcodeTv, v -> {//保存二维码到本地
            DialogManager.INSTANCE.showProgressDialog(this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (qrcodeUrl != null && !TextUtils.isEmpty(qrcodeUrl)) {
                        // bitmap = getBitMBitmap(qrcodeUrl);
//                        bitmap = ImageLoader.getInstance().loadImageSync(qrcodeUrl);
                        try {
                            bitmap = Glide.with(OtcBuyConfirmActivity.this).asBitmap().load(qrcodeUrl).submit().get();
                        } catch (Throwable t) {
                        }
                        //      bitmap=((BitmapDrawable) ( mBinding.qrCodeIv).getDrawable()).getBitmap();
                        if (bitmap != null) {
                            saveImageToGallery(OtcBuyConfirmActivity.this, bitmap);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogManager.INSTANCE.dismiss();
                                    MToast.show(OtcBuyConfirmActivity.this, getString(R.string.cx88), 2);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogManager.INSTANCE.dismiss();
                                    MToast.show(OtcBuyConfirmActivity.this, getString(R.string.cx89), 2);
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogManager.INSTANCE.dismiss();
                                MToast.show(OtcBuyConfirmActivity.this, getString(R.string.cx90), 2);
                            }
                        });
                    }
                }
            }).start();
        });
        viewClick(mBinding.qrCodecloseIv, v -> {//关闭付款二维码
            mBinding.qrcodeFl.setVisibility(View.GONE);
        });
        viewClick(mBinding.copyIv2, v -> {
            NorUtils.copeText(this, mBinding.accountTv.getText().toString());
            MToast.showButton(this, getString(R.string.cx91), 1);
        });
        viewClick(mBinding.copyIv3, v -> {
            NorUtils.copeText(this, mBinding.transReferNumTv.getText().toString());
            MToast.showButton(this, getString(R.string.cx91), 1);
        });
        viewClick(mBinding.copyIv4, v -> {
            NorUtils.copeText(this, mBinding.bankNameTv.getText().toString());
            MToast.showButton(this, getString(R.string.cx91), 1);
        });
        viewClick(mBinding.copyIv5, v -> {
            NorUtils.copeText(this, mBinding.bankaddrTv.getText().toString());
            MToast.showButton(this, getString(R.string.cx91), 1);
        });
        viewClick(mBinding.callPhoneLl, v -> {
            contactMerchant();
        });
        mBinding.transReferNumTv.setText(transReferNum + "");
        mBinding.amountTv.setText(totalAmount + " CNY");
        mBinding.totalAmountTv.setText(totalAmount + " CNY");
        initOrderView();
//        if (payType == 1) {
//            if (payBean1 != null) {
//                mBinding.nameTv.setText(payBean.getBank().getRealName());
//                mBinding.accountTv.setText(payBean.getBank().getBankNum());
//                mBinding.bankNameTv.setText(payBean.getBank().getBankName() + "");
//                mBinding.bankaddrTv.setText(payBean.getBank().getBankAdress() + "");
//            }
//            mBinding.bankaddrRl.setVisibility(View.VISIBLE);
//            mBinding.bankNameRl.setVisibility(View.VISIBLE);
//            mBinding.payIv.setImageResource(R.mipmap.bank_cark);
//            mBinding.chargeTypeTv.setText("银行卡");
//            mBinding.qrCodeRl.setVisibility(View.GONE);
//            mBinding.accountTitTv.setText("银行卡号");
//        } else if (payType == 2) {
//            if (payBean != null && payBean.getWechat() != null) {
//                mBinding.nameTv.setText(payBean.getWechat().getRealName());
//                mBinding.accountTv.setText(payBean.getWechat().getAccount());
//                qrcodeUrl = payBean.getWechat().getQrcode();
//                ImageLoader.getInstance().displayImage(qrcodeUrl, mBinding.qrCodeIv);
//            }
//            mBinding.accountTitTv.setText("微信账号");
//            mBinding.payIv.setImageResource(R.mipmap.wx_bg);
//            mBinding.chargeTypeTv.setText("微信");
//            mBinding.qrCodeRl.setVisibility(View.VISIBLE);
//            mBinding.bankaddrRl.setVisibility(View.GONE);
//            mBinding.bankNameRl.setVisibility(View.GONE);
//
//        }
//        if (payType == 3) {
//
//            if (payBean != null && payBean.getAlipay() != null) {
//                mBinding.nameTv.setText(payBean.getAlipay().getRealName());
//                mBinding.accountTv.setText(payBean.getAlipay().getAccount());
//
//                qrcodeUrl = payBean.getAlipay().getQrcode();
//                ImageLoader.getInstance().displayImage(qrcodeUrl, mBinding.qrCodeIv);
//            }
//            mBinding.accountTitTv.setText("支付宝账号");
//            mBinding.payIv.setImageResource(R.mipmap.zfb_bg);
//            mBinding.chargeTypeTv.setText("支付宝");
//            mBinding.qrCodeRl.setVisibility(View.VISIBLE);
//            mBinding.bankaddrRl.setVisibility(View.GONE);
//            mBinding.bankNameRl.setVisibility(View.GONE);
//
//        }
        //    DisplayImageUtils.displayImage(mBinding.qrCodeIv, qrcodeUrl, this, 0, R.mipmap.rmblogo, true, false);
        if (payLimit > 2) {
            ms = Observable.interval(0, 1, TimeUnit.SECONDS).limit(payLimit + 1).map(aLong -> payLimit - aLong).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
                if (mBinding.timeTv != null) {
                    mBinding.timeTv.setText(TimeUtils.millis2String(aLong - 2 < 0 ? 0 : (aLong - 2) * 1000, "mm:ss"));
                    //  mBinding.timeLimitTv.setText(TimeUtils.millis2String(aLong * 1000, "mm分ss秒"));
                }
                if (aLong == 0) {
                    Intent intent = new Intent(OtcBuyConfirmActivity.this, OtcOrderCancleDetailActivity.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("tradeType", 1);
                    startActivity(intent);
//                        startActivity(new Intent(OtcPaymentActivity.this, OtcOrderManagerActivity.class));
                    finish();
                }
            }).onErrorReturn(throwable -> {
                Logger.getInstance().errorLog(throwable);
                return null;
            }).doOnError(throwable -> {
                Logger.getInstance().error(throwable);
            }).subscribe();
        }
        viewClick(mBinding.coinfirmTv, v -> {
            DialogUtils.getInstance().showConfirmOtcDialog(this, new DialogUtils.onBnClickListener() {
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
                    confirmOrder();
                }
            });
        });
        viewClick(mBinding.nonPaymentTv, v -> {
            payOrderInfoBean = C2cStatus.payOrderInfoBean;
            if (fastTrade) {
                mBinding.nonPaymentTv.setText(R.string.a21);
                String des;
                if (payOrderInfoBean.getData().getDealType() == OtcPrepaidActivity.buyOrder && UserInfoManager.getUserInfo().getFid() == payOrderInfoBean.getData().getOrder().getAdUserId()) {
                    des = getResources().getString(R.string.cancle_des2, cancelCount);
                } else {
                    des = getResources().getString(R.string.cancle_des1, cancelCount);
                }
                DialogUtils.getInstance().showCancleOtcDialog(this, getResources().getString(R.string.cancle_title1), des, new DialogUtils.onBnClickListener() {
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
                        cancelOrder(payOrderInfoBean.getData().getOrder().getId());
                    }
                });
            } else {
                Intent intent = new Intent(this, OtcPaymentActivity.class);
                intent.putExtra("data", orderId);
                intent.putExtra("payType", payType);
                startActivity(intent);
                finish();
            }
        });
        ms1 = Observable.interval(0, 15, TimeUnit.SECONDS).limit(6000).map(aLong -> 6000 - aLong).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
            lunxunOrder();
        }).onErrorReturn(throwable -> {
            Logger.getInstance().errorLog(throwable);
            return 0L;
        }).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe();
        configGet();
    }

    private void initOrderView() {
        showDot();
        if (payBean1 == null) return;
        switch (payBean1.getType()) {
            case Constant.ALIPAY:
                initPayTypeInfo(View.GONE, R.mipmap.zfb, getString(R.string.cx93), getString(R.string.cx92), View.VISIBLE);
                qrcodeUrl = payBean1.getQrcode();
                Glide.with(this).load(UrlConstants.DOMAIN + qrcodeUrl).into(mBinding.qrCodeIv);
                break;
            case Constant.BANK:
                initPayTypeInfo(View.VISIBLE, R.mipmap.bank_cark, getString(R.string.cx95), getString(R.string.cx94), View.GONE);
                mBinding.bankNameTv.setText(payBean1.getBankName() + "");
                mBinding.bankaddrTv.setText(payBean1.getBankAdress() + "");
                break;
            case Constant.WECHAT:
                initPayTypeInfo(View.GONE, R.mipmap.wx, getString(R.string.cx97), getString(R.string.cx96), View.VISIBLE);
                qrcodeUrl = payBean1.getQrcode();
                Glide.with(this).load(UrlConstants.DOMAIN + qrcodeUrl).into(mBinding.qrCodeIv);
                break;
        }
    }

    private void initPayTypeInfo(int gone, int p, String 支付宝账号, String 支付宝, int visible) {
        mBinding.nameTv.setText(payBean1.getRealName());
        mBinding.accountTv.setText(payBean1.getAccount());
        mBinding.bankaddrRl.setVisibility(gone);
        mBinding.bankNameRl.setVisibility(gone);
        mBinding.payIv.setImageResource(p);
        mBinding.accountTitTv.setText(支付宝账号);
        mBinding.chargeTypeTv.setText(支付宝);
        mBinding.qrCodeRl.setVisibility(visible);
    }

    Subscription ms1;

    void lunxunOrder() {
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
                    if (orderDetailBean.getCode() == 0) {
                        if (orderDetailBean.getData().getOrders().getStatus() == 4 || orderDetailBean.getData().getOrders().getStatus() == 3) {
                            Intent intent = new Intent(OtcBuyConfirmActivity.this, OtcOrderCancleDetailActivity.class);
                            intent.putExtra("orderId", orderId);
                            intent.putExtra("tradeType", 1);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ms != null && !ms.isUnsubscribed()) {
            ms.unsubscribe();
        }
        if (ms1 != null && !ms1.isUnsubscribed()) {
            ms1.unsubscribe();
        }
        EventBus.getDefault().unregister(this);
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

    void confirmOrder() {
        Map<String, String> params = new HashMap<>();
        params.put("orderId", String.valueOf(orderId));
        params.put("payType", payType + "");//支付方式: 1 银行卡, 2 微信, 3 支付宝
        params.put("payAccount", String.valueOf(payBean1.getId()));//支付方式: 1 银行卡, 2 微信, 3 支付宝
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.confirm_order, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                //{"code":"200","data":"{\"result\":true,\"code\":0,\"value\":\"操作成功\",\"data\":\"付款成功\"}","message":"执行成功"}
                try {
                    BaseBean baseBean = new Gson().fromJson(result, BaseBean.class);
                    if (baseBean.getCode() == 0) {
                        Intent intent = new Intent(OtcBuyConfirmActivity.this, OtcPrepaidActivity.class);
                        // Intent intent = new Intent(getAContext(), OtcTradeCompleteActivity.class);
                        intent.putExtra("orderId", orderId);
                        startActivity(intent);
                        finish();
                    } else {
                        MToast.show(OtcBuyConfirmActivity.this, baseBean.getValue(), 1);
                    }
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

    /**
     * 更新红点
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void notifyDot(Event.IMMessage exit) {
        showDot();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void notifyOrderInfo(PayOrderInfoBean payOrderInfoBean) {
        this.payOrderInfoBean = payOrderInfoBean;
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDot();
    }

    public void showDot() {
        if (!isShowDot) {
            return;
        }
        if (null == payBean1) return;
        ImDotUtils.showDot(findViewById(R.id.dot), String.valueOf(orderId));
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

    private void contactMerchant() {
        if (payOrderInfoBean == null || payOrderInfoBean.getData() == null || payOrderInfoBean.getData().getOrder() == null) {
            //TODO show
            return;
        }
        if (!payOrderInfoBean.getData().getOrder().isCoinWAd()) {
            clearDot();
            DialogUtils.getInstance().setOnclickListener(null);
            DialogUtils.getInstance().showOneButtonDialog(OtcBuyConfirmActivity.this, getString(R.string.no_chat), getString(R.string.b38));
            return;
        }
        //获得商家的IM通道名称
        NIMHelper.contactMerchant(this, payOrderInfoBean, 0);
    }

    //取消订单
    void cancelOrder(int adUserId) {
        Map<String, String> params = new HashMap<>();
        params.put("orderId", adUserId + "");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.cancel_order, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                DialogManager.INSTANCE.dismiss();
                SnackBarUtils.ShowRed(OtcBuyConfirmActivity.this, errorMsg);
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    DialogManager.INSTANCE.dismiss();
                    BaseBean baseBean = new Gson().fromJson(result, BaseBean.class);
                    if (baseBean.getCode() == 0) {
                        if (payOrderInfoBean.getData().getOrder() != null) {
                            Intent intent = new Intent(OtcBuyConfirmActivity.this, OtcOrderCancleDetailActivity.class);
                            intent.putExtra("orderId", adUserId);
                            intent.putExtra("tradeType", 1);
                            startActivity(intent);
                        }
                        finish();
                    } else {
                    }
                    MToast.show(OtcBuyConfirmActivity.this, baseBean.getValue(), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
}
