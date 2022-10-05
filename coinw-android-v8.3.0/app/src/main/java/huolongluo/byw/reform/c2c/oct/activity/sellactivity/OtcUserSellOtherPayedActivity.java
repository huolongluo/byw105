package huolongluo.byw.reform.c2c.oct.activity.sellactivity;
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
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.databinding.ActivityOtcuserotherpayedNewBinding;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcAppealActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcTradeCompleteActivity;
import huolongluo.byw.reform.c2c.oct.bean.BaseBean;
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
import huolongluo.bywx.OTCOrderHelper;
import huolongluo.bywx.helper.AppHelper;
import okhttp3.Request;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
/**
 * Created by Administrator on 2019/3/14 0014.
 */
public class OtcUserSellOtherPayedActivity extends BaseActivity implements View.OnClickListener {
    ActivityOtcuserotherpayedNewBinding mBinding;
    int orderId;
    int payType;
    String qrcodeUrl;
    private boolean foreground = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_otcuserotherpayed_new);
        mBinding.include.backIv.setOnClickListener(this);
        if (getIntent() != null) {
            orderId = getIntent().getIntExtra("orderId", 0);
        }
//        viewClick(mBinding.copyIv1, v -> {
//
//            NorUtils.copeText(this, mBinding.nameTv.getText().toString());
//            MToast.showButton(this, "复制成功", 1);
//
//        });
//        viewClick(mBinding.copyIv2, v -> {
//
//            NorUtils.copeText(this, mBinding.accountTv.getText().toString());
//            MToast.showButton(this, "复制成功", 1);
//
//        });
//        viewClick(mBinding.copyIv3, v -> {
//
//            NorUtils.copeText(this, mBinding.transReferNumTv.getText().toString());
//            MToast.showButton(this, "复制成功", 1);
//
//        });
        viewClick(mBinding.copyIv4, v -> {
            NorUtils.copeText(this, mBinding.orderIdTv.getText().toString());
            MToast.showButton(this, getString(R.string.cx3), 1);
        });
        viewClick(mBinding.callPhoneLl, v -> contactMerchant());
//        viewClick(mBinding.copyIv5, v -> {
//
//            NorUtils.copeText(this, mBinding.bankNameTv.getText().toString());
//            MToast.showButton(this, "复制成功", 1);
//
//        });
//        viewClick(mBinding.copyIv6, v -> {
//
//            NorUtils.copeText(this, mBinding.accountaddrTv.getText().toString());
//            MToast.showButton(this, "复制成功", 1);
//
//        });
        viewClick(mBinding.qrCodeRl, v -> {//显示付款二维码
            //  mBinding.qrcodeFl.setVisibility(View.VISIBLE);
            //   ImageLoader.getInstance().displayImage(qrcodeUrl,  mBinding.qrCodeIv);
            // Glide.with(this).load(qrcodeUrl).error(R.mipmap.rmblogo).centerCrop().into((ImageView) mBinding.qrCodeIv);
        });
        payOrder();
        viewClick(mBinding.cancleTv, v -> {//申诉
            complain_check();
        });
        viewClick(mBinding.confirmTv, v -> {// 确认收款
            DialogUtils.getInstance().showOtcConfirmDialog(this, new DialogUtils.onBnClickListener() {
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
        viewClick(mBinding.qrCodecloseIv, v -> {//关闭付款二维码
            mBinding.qrcodeFl.setVisibility(View.GONE);
        });
        ms1 = Observable.interval(0, 15, TimeUnit.SECONDS).limit(6000).map(aLong -> 6000 - aLong).doOnSubscribe(() -> {
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
            getOrder();//轮训查询状态
        }).onErrorReturn(throwable -> {
            Logger.getInstance().errorLog(throwable);
            return 15L;
        }).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe();
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
                            bitmap = Glide.with(OtcUserSellOtherPayedActivity.this).asBitmap().load(qrcodeUrl).submit().get();
                        } catch (Throwable t) {
                        }
                        if (bitmap != null) {
                            saveImageToGallery(OtcUserSellOtherPayedActivity.this, bitmap);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogManager.INSTANCE.dismiss();
                                    MToast.show(OtcUserSellOtherPayedActivity.this, getString(R.string.cx4), 2);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogManager.INSTANCE.dismiss();
                                    MToast.show(OtcUserSellOtherPayedActivity.this, getString(R.string.cx5), 2);
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogManager.INSTANCE.dismiss();
                                MToast.show(OtcUserSellOtherPayedActivity.this, getString(R.string.cx6), 2);
                            }
                        });
                    }
                }
            }).start();
        });
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
                        OTCOrderHelper.refreshOrderStatus(OtcUserSellOtherPayedActivity.this, orderDetailBean);
//                        if (orderDetailBean.getData().getOrders().getStatus() == 5 ||
//                                orderDetailBean.getData().getOrders().getStatus() == 6
//
//                        ) {
//                            Intent intent = new Intent(OtcUserSellOtherPayedActivity.this, OtcAppealDetailActivity.class);
//
//                            intent.putExtra("orderId", orderDetailBean.getData().getOrders().getId());
//                            startActivity(intent);
//                            finish();
//                        } else if (orderDetailBean.getData().getOrders().getStatus() == 3) {
//                            Intent intent = new Intent(OtcUserSellOtherPayedActivity.this, OtcOrderCancleDetailActivity.class);
//                            intent.putExtra("orderId", orderDetailBean.getData().getOrders().getId());
//                            intent.putExtra("tradeType", 2);
//                            startActivity(intent);
//                            finish();
//                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    Subscription ms1;

    private boolean isForeground() {
        return foreground;
    }

    //申诉
    void complain_check() {
        Map<String, String> params = new HashMap<>();
        params.put("orderId", orderId + "");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.complain_check, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                DialogManager.INSTANCE.dismiss();
                SnackBarUtils.ShowRed(OtcUserSellOtherPayedActivity.this, errorMsg);
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                try {
                    BaseBean baseBean = new Gson().fromJson(result, BaseBean.class);
                    if (baseBean.getCode() == 0) {
                        BaseApp.collectActivity(OtcUserSellOtherPayedActivity.this);
                        Intent intent = new Intent(OtcUserSellOtherPayedActivity.this, OtcAppealActivity.class);
                        intent.putExtra("orderId", orderId);
                        startActivity(intent);
                    } else {
                        MToast.show(OtcUserSellOtherPayedActivity.this, baseBean.getValue(), 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private PayOrderInfoBean payOrderInfoBean;

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
                SnackBarUtils.ShowRed(OtcUserSellOtherPayedActivity.this, errorMsg);
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                try {
                    payOrderInfoBean = new Gson().fromJson(result, PayOrderInfoBean.class);
                    if (payOrderInfoBean != null && payOrderInfoBean.getCode() == 0) {
                        updataUI();
                    } else {
                        MToast.show(OtcUserSellOtherPayedActivity.this, payOrderInfoBean.getValue(), 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    Subscription ms;

    void updataUI() {
        showDot();
        mBinding.orderIdTv.setText(payOrderInfoBean.getData().getOrder().getOrderNo() + "");
        if (!payOrderInfoBean.getData().isCanComplaint()) {
//            mBinding.cancleTv.setEnabled(false);
            mBinding.cancleTv.setBackgroundColor(getResources().getColor(R.color.ffD5D3DF));
        }
        if (!TextUtils.isEmpty(payOrderInfoBean.getData().getComplaintPayedTips())) {
            mBinding.wranTv.setText(payOrderInfoBean.getData().getComplaintPayedTips() + "");
        }
        int limitTime = payOrderInfoBean.getData().getComplaintResidueTime();
        if (limitTime > 1) {
            ms = Observable.interval(0, 1, TimeUnit.SECONDS).limit(limitTime + 1).map(aLong -> limitTime - aLong).doOnSubscribe(() -> {
            }).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
                //   mBinding.timeLimitTv.setText(TimeUtils.millis2String(aLong * 1000, "mm分ss秒"));
                if (mBinding == null || mBinding.cancleTv == null) {
                    return;
                }
                if (aLong == 1) {
                    mBinding.cancleTv.setEnabled(true);
                    mBinding.cancleTv.setBackgroundColor(getResources().getColor(R.color.ffa1a1c9));
                }
            }).onErrorReturn(throwable -> {
                Logger.getInstance().errorLog(throwable);
                return 1L;
            }).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe();
        }
        mBinding.transReferNumTv.setText(payOrderInfoBean.getData().getOrder().getTransReferNum() + "");
        mBinding.amountTv.setText(payOrderInfoBean.getData().getOrder().getTotalAmount() + " CNY");
        mBinding.des.setText(payOrderInfoBean.getData().getOrder().getConfirmReceiptMsg());
        PayOrderInfoBean.PayAccountsBean buyerSelectedSellerPayType = payOrderInfoBean.getData().getOrder().getBuyerSelectedSellerPayType();
        if (buyerSelectedSellerPayType.getType() == 1) {
//            mBinding.accountAddRl.setVisibility(View.VISIBLE);
            mBinding.bankNameRl.setVisibility(View.VISIBLE);
//            mBinding.accountaddrTv.setText(buyerSelectedSellerPayType.getBankAdress() + "");
            mBinding.bankNameTv.setText(buyerSelectedSellerPayType.getBankName() + "");
            mBinding.payIv.setImageResource(R.mipmap.bank_cark);
            mBinding.chargeTypeTv.setText(R.string.cx7);
//            // mBinding.qrCodeRl.setVisibility(View.GONE);
            mBinding.accountTitTv.setText(R.string.cx8);
//            mBinding.des.setText(buyerSelectedSellerPayType.getRealName());
            mBinding.accountTv.setText(buyerSelectedSellerPayType.getAccount());
            mBinding.transReferNumTv.setText(payOrderInfoBean.getData().getOrder().getTransReferNum() + "");
        } else if (buyerSelectedSellerPayType.getType() == 2) {
//            mBinding.accountAddRl.setVisibility(View.GONE);
            mBinding.nameRl.setVisibility(View.VISIBLE);
            mBinding.nameTv.setText(buyerSelectedSellerPayType.getRealName());
            mBinding.bankNameRl.setVisibility(View.GONE);
            mBinding.payIv.setImageResource(R.mipmap.wx);
            mBinding.chargeTypeTv.setText(R.string.cx9);
//            // mBinding.qrCodeRl.setVisibility(View.VISIBLE);
//
            mBinding.accountTitTv.setText(R.string.cx10);
//            mBinding.des.setText(buyerSelectedSellerPayType.getRealName());
            mBinding.accountTv.setText(buyerSelectedSellerPayType.getAccount());
            mBinding.transReferNumTv.setText(payOrderInfoBean.getData().getOrder().getTransReferNum() + "");
//
//
//            qrcodeUrl = buyerSelectedSellerPayType.getQrcode();
        } else if (buyerSelectedSellerPayType.getType() == 3) {
//            mBinding.accountAddRl.setVisibility(View.GONE);
            mBinding.nameRl.setVisibility(View.VISIBLE);
            mBinding.nameTv.setText(buyerSelectedSellerPayType.getRealName());
            mBinding.bankNameRl.setVisibility(View.GONE);
            mBinding.accountTitTv.setText(R.string.cx11);
            mBinding.payIv.setImageResource(R.mipmap.zfb);
            mBinding.chargeTypeTv.setText(R.string.cx12);
//            //  mBinding.qrCodeRl.setVisibility(View.VISIBLE);
//            mBinding.des.setText(buyerSelectedSellerPayType.getRealName());
            mBinding.accountTv.setText(buyerSelectedSellerPayType.getAccount());
            mBinding.transReferNumTv.setText(payOrderInfoBean.getData().getOrder().getTransReferNum() + "");
            qrcodeUrl = buyerSelectedSellerPayType.getQrcode();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppHelper.unsubscribe(ms);
        AppHelper.unsubscribe(ms1);
        EventBus.getDefault().unregister(this);
    }

    //确认收款
    void confirmOrder() {
        Map<String, String> params = new HashMap<>();
        params.put("orderId", orderId + "");
        //  params.put("payType", payType + "");//支付方式: 1 银行卡, 2 微信, 3 支付宝
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.order_release, params, new OkhttpManager.DataCallBack() {
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
                        Intent intent = new Intent(OtcUserSellOtherPayedActivity.this, OtcTradeCompleteActivity.class);
                        intent.putExtra("orderId", orderId);
                        intent.putExtra("tradeType", 2);
                        startActivity(intent);
                        finish();
                    } else {
                    }
                    MToast.show(OtcUserSellOtherPayedActivity.this, baseBean.getValue(), 1);
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

    private void contactMerchant() {
        if (payOrderInfoBean == null || payOrderInfoBean.getData() == null || payOrderInfoBean.getData().getOrder() == null) {
            //TODO show
            return;
        }
        if (!payOrderInfoBean.getData().getOrder().isCoinWAd()) {
            DialogUtils.getInstance().showOneButtonDialog(OtcUserSellOtherPayedActivity.this, getString(R.string.no_chat), getString(R.string.b38));
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
        if (null == payOrderInfoBean || payOrderInfoBean.getData() == null) return;
        ImDotUtils.showDot(findViewById(R.id.dot), String.valueOf(payOrderInfoBean.getData().getOrder().getId()));
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
