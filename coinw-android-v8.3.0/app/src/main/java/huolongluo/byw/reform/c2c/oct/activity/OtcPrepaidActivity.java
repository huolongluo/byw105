package huolongluo.byw.reform.c2c.oct.activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcodes.utils.TimeUtils;
import com.blankj.utilcodes.utils.ToastUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.databinding.ActivityOtcprepaidBinding;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseActivity;
import huolongluo.byw.reform.c2c.oct.bean.BaseBean;
import huolongluo.byw.reform.c2c.oct.bean.OrderCancelBean;
import huolongluo.byw.reform.c2c.oct.bean.OrderDetailBean;
import huolongluo.byw.reform.c2c.oct.bean.OtcUserInfoBean;
import huolongluo.byw.reform.c2c.oct.bean.PayOrderInfoBean;
import huolongluo.byw.reform.c2c.oct.utils.ImDotUtils;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.DateUtils;
import huolongluo.byw.util.GsonUtil;
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
/**
 * Created by Administrator on 2019/5/15 0015.
 */
public class OtcPrepaidActivity extends BaseActivity implements View.OnClickListener {
    private ActivityOtcprepaidBinding binding;
    public final static int buyOrder = 1;//购买订单
    public final static int sellOrder = 2;//卖出订单
    int orderId;
    private OrderDetailBean orderDetailBean;
    private int cancelCount = 0;
    private boolean foreground = false;
    private boolean isShowDot = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otcprepaid);
        if (getIntent() != null) {
            orderId = getIntent().getIntExtra("orderId", 0);
        }
        viewClick(binding.include.backIv, v -> finish());
        viewClick(binding.cooonctIv, v -> {
            //联系对方
//            if (orderDetailBean != null) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse("tel:" + orderDetailBean.getData().getInformation()));
//                startActivity(intent);
//            }
            contactMerchant();
        });
        configGet();
        viewClick(binding.complainTv, v -> {//申诉
            complain_check();
          /*  if (orderDetailBean != null && orderDetailBean.getData() != null) {

                if (orderDetailBean.getData().getCanComplaint() == 1) {
                    Intent intent = new Intent(this, OtcAppealActivity.class);
                    intent.putExtra("orderId", orderDetailBean.getData().getOrders().getId());

                    startActivity(intent);
                } else {
                    MToast.show(OtcPrepaidActivity.this, "超过可申诉时间", 1);
                }

            }*/
        });
        viewClick(binding.cancleOrderTv, v -> {
            if (orderDetailBean != null && orderDetailBean.getData() != null) {
                String des;
                if (orderDetailBean.getData().getDealType() == buyOrder && UserInfoManager.getUserInfo().getFid() == orderDetailBean.getData().getOrders().getAdUserId()) {
                    des = getResources().getString(R.string.cancle_des2, cancelCount);
                } else {
                    des = getResources().getString(R.string.cancle_des1, cancelCount);
                }
                DialogUtils.getInstance().showCancleOtcDialog(this, getResources().getString(R.string.cancle_title), des, new DialogUtils.onBnClickListener() {
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
            }
        });
        viewClick(binding.callPhoneLl, v -> {
            //联系对方
//            if (orderDetailBean != null) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse("tel:" + orderDetailBean.getData().getInformation()));
//                startActivity(intent);
//            }
            contactMerchant();
        });
        viewClick(binding.copyIv1, v -> {
            NorUtils.copeText(this, binding.orderIdTv.getText().toString());
            MToast.showButton(this, getString(R.string.xx14), 1);
        });
        viewClick(binding.copyIv2, v -> {
            NorUtils.copeText(this, binding.transReferNumTv.getText().toString());
            MToast.showButton(this, getString(R.string.xx14), 1);
        });
        viewClick(binding.copyIv3, v -> {
            NorUtils.copeText(this, binding.otherNameTv.getText().toString());
            MToast.showButton(this, getString(R.string.xx14), 1);
        });
        viewClick(binding.copyIv4, v -> {
            NorUtils.copeText(this, binding.accountNameTv.getText().toString());
            MToast.showButton(this, getString(R.string.xx14), 1);
        });
        viewClick(binding.copyIv5, v -> {
            NorUtils.copeText(this, binding.bankNameTv.getText().toString());
            MToast.showButton(this, getString(R.string.xx14), 1);
        });
        viewClick(binding.copyIv6, v -> {
            NorUtils.copeText(this, binding.bankAdressTv.getText().toString());
            MToast.showButton(this, getString(R.string.xx14), 1);
        });
        viewClick(binding.copyIv7, v -> {
            NorUtils.copeText(this, binding.transReferNum1Tv.getText().toString());
            MToast.showButton(this, getString(R.string.xx14), 1);
        });
        ms1 = Observable.interval(0, 15, TimeUnit.SECONDS).limit(6000).map(aLong -> 6000 - aLong).doOnSubscribe(() -> {
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
            lunxunOrder();
        }).onErrorReturn(throwable -> {
            Logger.getInstance().errorLog(throwable);
            return 15L;
        }).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe();
        binding.qrCodeRl.setOnClickListener(v -> {
            binding.qrcodeFl.setVisibility(View.VISIBLE);
        });
        binding.qrCodecloseIv.setOnClickListener(v -> {
            binding.qrcodeFl.setVisibility(View.GONE);
        });
        viewClick(binding.saveqrcodeTv, v -> {//保存二维码到本地
            DialogManager.INSTANCE.showProgressDialog(this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (qrcodeUrl != null && !TextUtils.isEmpty(qrcodeUrl)) {
                        // bitmap = getBitMBitmap(qrcodeUrl);
//                        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(qrcodeUrl);
//                        Glide.with(OtcPrepaidActivity.this).asGif().load(new File(qrcodeUrl));
                        Bitmap bitmap = null;
                        try {
                            bitmap = Glide.with(OtcPrepaidActivity.this).asBitmap().load(qrcodeUrl).submit().get();
                        } catch (Throwable t) {
                        }
                        if (bitmap != null) {
                            saveImageToGallery(OtcPrepaidActivity.this, bitmap);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogManager.INSTANCE.dismiss();
                                    MToast.show(OtcPrepaidActivity.this, getString(R.string.xx16), 2);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogManager.INSTANCE.dismiss();
                                    MToast.show(OtcPrepaidActivity.this, getString(R.string.xx17), 2);
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogManager.INSTANCE.dismiss();
                                MToast.show(OtcPrepaidActivity.this, getString(R.string.xx18), 2);
                            }
                        });
                    }
                }
            }).start();
        });
        binding.netErrorView.errerView.setVisibility(View.GONE);
        binding.netErrorView.btnReLoad.setOnClickListener(v -> {
            getOrder();
        });
    }

    String qrcodeUrl;

    private void contactMerchant() {
        if (orderDetailBean == null || orderDetailBean.getData() == null || orderDetailBean.getData().getOrders() == null) {
            Log.e("OtcPrepaidActivity", "order is null ? ");
            //TODO show
            SnackBarUtils.ShowRed(OtcPrepaidActivity.this, getString(R.string.xx19));
            return;
        }
        if (!orderDetailBean.getData().getOrders().isCoinWAd()) {
            clearDot();
            DialogUtils.getInstance().setOnclickListener(null);
            DialogUtils.getInstance().showOneButtonDialog(OtcPrepaidActivity.this, getString(R.string.no_chat), getString(R.string.b38));
            return;
        }
        //获得商家的IM通道名称
        NIMHelper.contactMerchant(this, orderDetailBean, getTime());
    }

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
                    if (orderDetailBean.getCode() == 0 && isForeground()) {
                        clearDot();
                        if (orderDetailBean.getData().getOrders().getStatus() == 2) {
                            Intent intent = new Intent(OtcPrepaidActivity.this, OtcTradeCompleteActivity.class);
                            intent.putExtra("tradeType", 1);
                            intent.putExtra("orderId", orderDetailBean.getData().getOrders().getId());
                            startActivity(intent);
                            finish();
                        } else if (orderDetailBean.getData().getOrders().getStatus() == 5 || orderDetailBean.getData().getOrders().getStatus() == 6) {
                            Intent intent = new Intent(OtcPrepaidActivity.this, OtcAppealDetailActivity.class);
                            intent.putExtra("orderId", orderDetailBean.getData().getOrders().getId());
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
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
                SnackBarUtils.ShowRed(OtcPrepaidActivity.this, errorMsg);
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                try {
                    BaseBean baseBean = new Gson().fromJson(result, BaseBean.class);
                    if (baseBean.getCode() == 0) {
                        BaseApp.collectActivity(OtcPrepaidActivity.this);
                        Intent intent = new Intent(OtcPrepaidActivity.this, OtcAppealActivity.class);
                        intent.putExtra("orderId", orderId);
                        startActivity(intent);
                    } else {
                        MToast.show(OtcPrepaidActivity.this, baseBean.getValue(), 1);
                    }
                } catch (Exception e) {
                    ToastUtils.showShortToast(R.string.xx20);
                    e.printStackTrace();
                }
            }
        });
    }

    //取消订单
    void cancelOrder() {
        Map<String, String> params = new HashMap<>();
        params.put("orderId", orderDetailBean.getData().getOrders().getId() + "");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(this);
        OkhttpManager.postAsync(UrlConstants.cancel_order, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                SnackBarUtils.ShowRed(OtcPrepaidActivity.this, errorMsg);
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                try {
                    BaseBean baseBean = new Gson().fromJson(result, BaseBean.class);
                    if (baseBean.getCode() == 0) {
                        Intent intent = new Intent(OtcPrepaidActivity.this, OtcOrderCancleDetailActivity.class);
                        intent.putExtra("orderId", orderId);
                        intent.putExtra("tradeType", 1);
                        startActivity(intent);
                        finish();
                    } else {
                    }
                    MToast.show(OtcPrepaidActivity.this, baseBean.getValue(), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
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
                SnackBarUtils.ShowRed(OtcPrepaidActivity.this, errorMsg);
                e.printStackTrace();
                DialogManager.INSTANCE.dismiss();
                binding.netErrorView.errerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void requestSuccess(String result) {
                //TODO 待处理
                //OtcAppealDetailActivity
                binding.netErrorView.errerView.setVisibility(View.GONE);
                try {
                    orderDetailBean = new Gson().fromJson(result, OrderDetailBean.class);
                    if (orderDetailBean != null && orderDetailBean.getCode() == 0) {
                        updateUI();
                    } else {
                        MToast.show(OtcPrepaidActivity.this, orderDetailBean.getValue(), 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SnackBarUtils.ShowRed(OtcPrepaidActivity.this, getString(R.string.xx21));
                }
                DialogManager.INSTANCE.dismiss();
                showDot();
            }
        });
    }

    Subscription ms;
    Subscription ms1;
    private long time;

    private void setTime(long time) {
        this.time = time;
    }

    private long getTime() {
        return this.time;
    }

    void updateUI() {
        showDot();
        if (orderDetailBean != null && orderDetailBean.getData() != null) {
            if (OTCOrderHelper.refreshOrderStatus(this, orderDetailBean)) {
                //说明已经刷新跳转了界面，则返回
                return;
            }
            if (!orderDetailBean.getData().isCanComplaint()) {
//                binding.complainTv.setEnabled(false);
                binding.complainTv.setBackgroundResource(R.drawable.otcprepaid_bg3);
            }
            String complaintPayedTips = orderDetailBean.getData().getComplaintPayedTips();
            if (!TextUtils.equals("null", complaintPayedTips)) {
                binding.wranTv.setText(complaintPayedTips);
            }
            int timeLimit = (int) orderDetailBean.getData().getComplaintResidueTime();
            if (timeLimit > 1) {
                binding.arrivalTimeLL.setVisibility(View.GONE);
                binding.timeLimitLl.setVisibility(View.VISIBLE);
                ms = Observable.interval(0, 1, TimeUnit.SECONDS).limit(timeLimit + 1).map(aLong -> timeLimit - aLong).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
                    if (binding == null || binding.tv1 == null || binding.arrivalTimeLL == null) {
                        return;
                    }
                    if (aLong == 1) {
                        binding.arrivalTimeLL.setVisibility(View.VISIBLE);
                        binding.timeLimitLl.setVisibility(View.GONE);
                        binding.complainTv.setEnabled(true);
                        binding.complainTv.setBackgroundResource(R.drawable.otcprepaid_bg2);
                        return;
                    }
                    setTime(aLong * 1000);
                    String time = TimeUtils.millis2String(aLong * 1000, "mm:ss");
                    char[] chars = time.toCharArray();
                    binding.tv1.setText(chars[0] + "");
                    binding.tv2.setText(chars[1] + "");
                    binding.tv3.setText(chars[3] + "");
                    binding.tv4.setText(chars[4] + "");
                }).onErrorReturn(throwable -> {
                    Logger.getInstance().errorLog(throwable);
                    return 1L;
                }).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe();

            } else {
                binding.arrivalTimeLL.setVisibility(View.VISIBLE);
                binding.timeLimitLl.setVisibility(View.GONE);
                binding.complainTv.setEnabled(true);
                binding.complainTv.setBackgroundResource(R.drawable.otcprepaid_bg2);
            }
            binding.nameTitTv.setText(getString(R.string.xx22).replace("##", orderDetailBean.getData().getOrders().getCoinName()));
            binding.orderIdTv.setText(orderDetailBean.getData().getOrders().getOrderNo() + "");
            binding.userNameTv.setText(orderDetailBean.getData().getOppositeUserName() + "");
            binding.amountTv.setText(orderDetailBean.getData().getOrders().getTotalAmount() + " CNY");
            binding.priceTv.setText(orderDetailBean.getData().getOrders().getPrice() + " CNY/" + orderDetailBean.getData().getOrders().getCoinName());
            binding.numTv.setText(orderDetailBean.getData().getOrders().getAmount() + " " + orderDetailBean.getData().getOrders().getCoinName());
            binding.transReferNumTv.setText(orderDetailBean.getData().getOrders().getTransReferNum() + "");
            binding.transReferNum1Tv.setText(orderDetailBean.getData().getOrders().getTransReferNum() + "");
            PayOrderInfoBean.PayAccountsBean buyerSelectedSellerPayType = orderDetailBean.getData().getOrders().getBuyerSelectedSellerPayType();
            //设置支付信息
            binding.otherNameTv.setText(buyerSelectedSellerPayType.getRealName());
            binding.accountTv.setText(buyerSelectedSellerPayType.getAccount());
            binding.bankNameTv.setText(buyerSelectedSellerPayType.getBankName());
            binding.bankAdressTv.setText(buyerSelectedSellerPayType.getBankAdress());
            if (orderDetailBean.getData().getOrders().getCreateTime() != null) {
                binding.createTimeTv.setText(DateUtils.format(orderDetailBean.getData().getOrders().getCreateTime().getTime(), "yyyy-MM-dd HH:mm:ss") + "");
            }
            if (buyerSelectedSellerPayType.getType() == 1) {
                //   binding.qrCodeRl.setVisibility(View.GONE);
                binding.payTypeTv.setText(R.string.xx23);
                binding.payTypeIv.setImageResource(R.mipmap.bank_cark);
                binding.accountNameTv.setText(R.string.xx24);
                binding.bankNameLl.setVisibility(View.VISIBLE);
                binding.bankAdressLl.setVisibility(View.VISIBLE);
            } else if (buyerSelectedSellerPayType.getType() == 2) {
                Glide.with(OtcPrepaidActivity.this).load(buyerSelectedSellerPayType.getQrcode()).into(binding.qrCodeIv);
                qrcodeUrl = buyerSelectedSellerPayType.getQrcode();
                // binding.qrCodeRl.setVisibility(View.VISIBLE);
                binding.payTypeTv.setText(R.string.xx25);
                binding.accountNameTv.setText(R.string.xx26);
                binding.payTypeIv.setImageResource(R.mipmap.wx);
                binding.bankNameLl.setVisibility(View.GONE);
                binding.bankAdressLl.setVisibility(View.GONE);
            } else if (buyerSelectedSellerPayType.getType() == 3) {
                Glide.with(OtcPrepaidActivity.this).load(buyerSelectedSellerPayType.getQrcode()).into(binding.qrCodeIv);
                qrcodeUrl = buyerSelectedSellerPayType.getQrcode();
                // binding.qrCodeRl.setVisibility(View.VISIBLE);
                binding.payTypeTv.setText(R.string.xx27);
                binding.accountNameTv.setText(R.string.sx28);
                binding.payTypeIv.setImageResource(R.mipmap.zfb);
                binding.bankNameLl.setVisibility(View.GONE);
                binding.bankAdressLl.setVisibility(View.GONE);
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
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 90, fos);
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

    public static Bitmap getBitMBitmap(String urlpath) {
        Bitmap map = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            map = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        AppHelper.unsubscribe(ms);
        AppHelper.unsubscribe(ms1);
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
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("OtcPrepaidActivity", "resultCode: " + resultCode);
    }

    public void showDot() {
        //未开通在线聊天功能
        if (!isShowDot) {
            return;
        }
        if (null == orderDetailBean || orderDetailBean.getData() == null) return;
        ImDotUtils.showDot(findViewById(R.id.dot), String.valueOf(orderDetailBean.getData().getOrders().getId()));
        clearDot();
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

    /**
     * 更新红点
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void notifyDot(Event.IMMessage exit) {
        showDot();
    }
}
