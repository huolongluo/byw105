package huolongluo.byw.reform.c2c.oct.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.legend.model.enumerate.transfer.TransferAccount;
import com.android.legend.ui.transfer.AccountTransferActivity;
import com.blankj.utilcodes.utils.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.activity.bindgoogle.BindGoogleOneActivity;
import huolongluo.byw.byw.ui.activity.renzheng.AuthActivity;
import huolongluo.byw.databinding.ActivityOtcbuyBinding;
import huolongluo.byw.helper.FaceVerifyHelper;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.c2c.oct.activity.sellactivity.OtcUserSellPaymentActivity;
import huolongluo.byw.reform.c2c.oct.bean.AdvertisementsBean;
import huolongluo.byw.reform.c2c.oct.bean.OtcCoinBean;
import huolongluo.byw.reform.c2c.oct.bean.TotalUserWalletBean;
import huolongluo.byw.reform.safecenter.ReserTradePswActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.DoubleUtils;
import okhttp3.Request;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static java.lang.Double.parseDouble;

/**
 * Created by Administrator on 2019/3/13 0013.
 */
public class OtcBuyActivity extends AppCompatActivity implements View.OnClickListener {
    // Unbinder unbinder;
    ActivityOtcbuyBinding mBinding;
    String coinId;
    int id;
    String limitMax;
    String limitMin;
    private int mTimeRecord = 61;//记录计时器暂停的时间用于恢复暂停
    private boolean mIsTimeRecord = true;
    AdvertisementsBean.DataBeanX.DataBean coinInfo;
    int TradeType;//用户 购买 1, 用户出售 2
    double totalVirtualAsset; //币币账户资产
    Subscription ms;
    boolean tag = false;
    private Context mContext;
//    //默认精度
//    int quantityPrecision = 2;
//    int pricePrecision = 3;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_otcbuy);
        mContext = this;
        //  setContentView(R.layout.activity_otc_bug);
        // unbinder = ButterKnife.bind(this);
        mBinding.cancleTv.setOnClickListener(this);
        mBinding.confirmTv.setOnClickListener(this);
        mBinding.closeIv.setOnClickListener(this);
        if (getIntent() != null) {
            coinId = getIntent().getStringExtra("coinId");
            id = getIntent().getIntExtra("id", 0);
            limitMax = getIntent().getStringExtra("limitMax");
            limitMin = getIntent().getStringExtra("limitMin");
            coinInfo = getIntent().getParcelableExtra("dataBean");
            TradeType = getIntent().getIntExtra("TradeType", 0);
        }
        if (coinInfo != null) {
            RequestOptions ro = new RequestOptions();
            ro.error(R.mipmap.rmblogo);
            ro.centerCrop();
            Glide.with(this).load(coinInfo.getCoinUrl()).apply(ro).into((ImageView) mBinding.namelogTv);
            id = coinInfo.getCoinId();
            mBinding.remarkTv.setText(coinInfo.getRemark() + "");
            if (TradeType == 1) {
                mBinding.paiTimeTv.setText(getString(R.string.cd67).replace("##", coinInfo.getPayLimit() + ""));
            } else {
                mBinding.paiTimeTv.setText(getString(R.string.cx68).replace("##", coinInfo.getPayLimit() + ""));
            }
        }

        String msg = String.format("coinId: ", coinId);
        if (AppUtils.getOTCCoinBean() != null) {
            List<OtcCoinBean.DataBean> dataList = AppUtils.getOTCCoinBean().getData();
            String json = GsonUtil.obj2Json(dataList, List.class);
            Logger.getInstance().debug("OtcBuyActivity", msg + "\ndata:  " + json);
        }
        OtcCoinBean.DataBean dataBean = AppUtils.getOTCCoin(id);
        //默认精度
        int quantityPrecision = 2;
        int pricePrecision = 3;
        if (dataBean != null) {
            quantityPrecision = dataBean.getQuantityPrecision();
            pricePrecision = dataBean.getPricePrecision();
        }
        mBinding.warnTv1.setOnClickListener(v -> {
            //JIRA:COIN-1721
            //业务控制条件：是否禁止提币 0.可以提币， 1.禁止提币
            if (!AppHelper.isNoWithdrawal()) {//该用户已被禁止提币
                DialogUtils.getInstance().showOneButtonDialog(OtcBuyActivity.this, getString(R.string.no_transfer), getString(R.string.as16));
                return;
            }
            AccountTransferActivity.Companion.launch(this, TransferAccount.WEALTH.getValue(), TransferAccount.CONTRACT.getValue(), id, null,
                    false, null);
        });
        mBinding.forgetPwdTv.setOnClickListener(new View.OnClickListener() {//忘记密码
            @Override
            public void onClick(View v) {
                if (!UserInfoManager.getUserInfo().isHasC3Validate() && !UserInfoManager.getUserInfo().isHasC2Validate()) {
                    if (UserInfoManager.getUserInfo().isPostC2Validate()) {
                        MToast.showButton(OtcBuyActivity.this, getString(R.string.cx69), 1);
                        return;
                    }
                    MToast.showButton(OtcBuyActivity.this, getString(R.string.cx70), 1);
                    return;
                }
                if (!UserInfoManager.getUserInfo().isGoogleBind()) {
                    startActivity(new Intent(OtcBuyActivity.this, BindGoogleOneActivity.class));
                    MToast.showButton(OtcBuyActivity.this, getString(R.string.cx71), 1);
                    return;
                }
                startActivity(new Intent(OtcBuyActivity.this, ReserTradePswActivity.class));
            }
        });
        if (TradeType == 1) {
            mBinding.pswTv.setVisibility(View.GONE);
            mBinding.forgetPwdTv.setVisibility(View.GONE);
            mBinding.confirmTv.setText(R.string.cx72);
            mBinding.allAmountTv.setText(R.string.cx73);
        } else {
            mBinding.forgetPwdTv.setVisibility(View.VISIBLE);
            mBinding.pswTv.setVisibility(View.VISIBLE);
            mBinding.confirmTv.setText(R.string.cx74);
            mBinding.allAmountTv.setText(R.string.cx75);
        }
        if (coinInfo != null) {
            double canBuyAmount = 0.0;
            double a = coinInfo.getLeftAmount();
            double b = coinInfo.getOrderMax();
            double c = coinInfo.getOrderMin();
            if (a >= b) {
                canBuyAmount = b;
            }
            if (a < b && a >= c) {
                canBuyAmount = a;
            }
            if (a < c) {
                canBuyAmount = a;
            }
            if (TradeType == 1) {
                mBinding.tip1tv.setText(R.string.cx76);
                mBinding.title1Tv.setText(getString(R.string.cx77) + coinInfo.getCoinName());
                mBinding.numEt.setHint(getString(R.string.cx78) + NorUtils.NumberFormatd(quantityPrecision).format(canBuyAmount));
                mBinding.amountTv.setHint(getString(R.string.cx79) + NorUtils.NumberFormatd(pricePrecision).format(canBuyAmount * coinInfo.getPrice()) + "");
            } else {
                String myAst = UserInfoManager.getOtcUserInfoBean().getAsset(coinInfo.getCoinId());
                if (!TextUtils.isEmpty(myAst)) {
                    Double myAsset = DoubleUtils.parseDouble(myAst);
                    if (canBuyAmount > myAsset) {
                        canBuyAmount = myAsset;
                    }
                }
                mBinding.tip1tv.setText(R.string.cx80);
                mBinding.title1Tv.setText(getString(R.string.cx81) + coinInfo.getCoinName());
                mBinding.numEt.setHint(getString(R.string.cx82) + NorUtils.NumberFormatd(quantityPrecision).format(canBuyAmount));
                mBinding.amountTv.setHint(getString(R.string.cx83) + NorUtils.NumberFormatd(pricePrecision).format(canBuyAmount * coinInfo.getPrice()) + "");
            }
            mBinding.totalTv.setText(coinInfo.getPrice_s() + " CNY");
            mBinding.coinNameTv.setText(coinInfo.getCoinName() + "");
            mBinding.limitTv.setText(coinInfo.getOrderMin_s() + "-" + coinInfo.getOrderMax_s() + " " + coinInfo.getCoinName());
            mBinding.bankIv.setVisibility(View.GONE);
            mBinding.alipayIv.setVisibility(View.GONE);
            mBinding.wechatIv.setVisibility(View.GONE);
            for (int i = 0; i < coinInfo.getPayAccountTypes().size(); i++) {
                switch (coinInfo.getPayAccountTypes().get(i)) {
                    case Constant.ALIPAY:
                        mBinding.alipayIv.setVisibility(View.VISIBLE);
                        break;
                    case Constant.BANK:
                        mBinding.bankIv.setVisibility(View.VISIBLE);
                        break;
                    case Constant.WECHAT:
                        mBinding.wechatIv.setVisibility(View.VISIBLE);
                        break;
                }
            }
//            if (coinInfo.getBankId() == 0) {
//                mBinding.bankIv.setVisibility(View.GONE);
//            } else {
//                mBinding.bankIv.setVisibility(View.VISIBLE);
//            }
//            if (coinInfo.getAlipayId() == 0) {
//                mBinding.alipayIv.setVisibility(View.GONE);
//            } else {
//                mBinding.alipayIv.setVisibility(View.VISIBLE);
//            }
//            if (coinInfo.getWechatId() == 0) {
//                mBinding.wechatIv.setVisibility(View.GONE);
//            } else {
//                mBinding.wechatIv.setVisibility(View.VISIBLE);
//            }
        }
        mBinding.numEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String num = s.toString();
                if (tag) {
                    tag = false;
                    return;
                }
                //默认精度
                int quantityPrecision = 2;
                int pricePrecision = 3;
                OtcCoinBean.DataBean dataBean = AppUtils.getOTCCoin(id);
                if (dataBean != null) {
                    quantityPrecision = dataBean.getQuantityPrecision();
                    pricePrecision = dataBean.getPricePrecision();
                }
                if (!TextUtils.isEmpty(num) && num.contains(".")) {
                    int dot = num.indexOf(".");
                    if (num.length() - dot > quantityPrecision) {
                        mBinding.numEt.setTextSize(22);
                        num = NorUtils.NumberFormatNo(quantityPrecision).format(parseDouble(num));

                        setTextValue(mBinding.numEt, num);
                        mBinding.numEt.setSelection(mBinding.numEt.getText().toString().length());
                        return;
                    }
                }
                if (!TextUtils.isEmpty(num) && !num.startsWith(".")) {
                    mBinding.numEt.setTextSize(22);
                    //java.lang.NumberFormatException: For input string: "li13880693"
                    double numd = 0.0d;
                    try {
                        numd = Double.parseDouble(num);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        setTextValue(mBinding.numEt, "");
                    }
                    if (coinInfo != null) {
                        //java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String huolongluo.byw.reform.c2c.oct.bean.OtcUserInfoBean.getAsset(int)' on a null object reference
                        //    at g.a.j.a.b.a.Ac.afterTextChanged(OtcBuyActivity.java:16)
                        //    at android.widget.TextView.sendAfterTextChanged(TextView.java:11671)
                        //    at android.widget.TextView.setText(TextView.java:6858)
                        //    at android.widget.TextView.setText(TextView.java:6649)
                        //    at android.widget.EditText.setText(EditText.java:140)
                        //    at android.widget.TextView.setText(TextView.java:6601)
                        //    at android.widget.TextView.onRestoreInstanceState(TextView.java:6468)
                        //    at android.view.View.dispatchRestoreInstanceState(View.java:21578)
                        if (TradeType == 2 && UserInfoManager.getOtcUserInfoBean() != null) {
                            String myAst = UserInfoManager.getOtcUserInfoBean().getAsset(coinInfo.getCoinId());
                            if (!TextUtils.isEmpty(myAst)) {
                                Double myAsset = DoubleUtils.parseDouble(myAst);
                                if (numd >= DoubleUtils.parseDouble(coinInfo.getOrderMin_s()) && myAsset < numd && numd <= DoubleUtils.parseDouble(coinInfo.getOrderMax_s())) {
                                    mBinding.warnTv.setVisibility(View.VISIBLE);
                                    mBinding.warnTv1.setVisibility(View.VISIBLE);
                                    mBinding.warnTv.setText(R.string.fiat_);
                                    return;
                                }
                            }
                        }
                        if (numd > DoubleUtils.parseDouble(coinInfo.getOrderMax_s())) {
                            mBinding.warnTv1.setVisibility(View.INVISIBLE);
                            mBinding.warnTv.setVisibility(View.VISIBLE);
                            mBinding.warnTv.setText(R.string.too_large);
                        } else if (numd < DoubleUtils.parseDouble(coinInfo.getOrderMin_s())) {
                            mBinding.warnTv1.setVisibility(View.INVISIBLE);
                            mBinding.warnTv.setVisibility(View.VISIBLE);
                            mBinding.warnTv.setText(R.string.too_small);
                        } else {
                            mBinding.warnTv1.setVisibility(View.INVISIBLE);
                            mBinding.warnTv.setVisibility(View.INVISIBLE);
                        }
                        BigDecimal b = new BigDecimal(String.valueOf(numd));
                        BigDecimal c = new BigDecimal(String.valueOf(coinInfo.getPrice()));
                        double sum = b.multiply(c).doubleValue();
                        mBinding.amountTv.setText(NorUtils.NumberFormatd(pricePrecision).format(sum) + "");
                    }
                } else {
                    mBinding.warnTv.setVisibility(View.INVISIBLE);
                    tag = true;
                    mBinding.amountTv.setText("");
                    setTextValue(mBinding.amountTv, "");
                    mBinding.numEt.setTextSize(13);
                    if (TradeType == 1) {
                        // mBinding.amountTv.setHint("最大可买" + Double.parseDouble(coinInfo.getOrderMax_s()) * coinInfo.getPrice());
                    } else {
                        // mBinding.amountTv.setHint("最大可获" + Double.parseDouble(coinInfo.getOrderMax_s()) * coinInfo.getPrice());
                    }
                }
            }
        });
        mBinding.amountTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (tag) {
                    tag = false;
                    return;
                }
                //默认精度
                int quantityPrecision = 2;
                int pricePrecision = 3;
                OtcCoinBean.DataBean dataBean = AppUtils.getOTCCoin(id);
                if (dataBean != null) {
                    quantityPrecision = dataBean.getQuantityPrecision();
                    pricePrecision = dataBean.getPricePrecision();
                }
                String num = s.toString();
                if (!TextUtils.isEmpty(num) && num.contains(".")) {
                    int dot = num.indexOf(".");
                    if (num.length() - dot > pricePrecision) {
                        tag = true;
                        num = NorUtils.NumberFormatNo(pricePrecision).format(parseDouble(num));
                        mBinding.amountTv.setText(num);
                        mBinding.amountTv.setSelection(mBinding.amountTv.getText().toString().length());
                        return;
                    }
                }
                //java.lang.NumberFormatException: For input string: "阿峰"
                //	at sun.misc.FloatingDecimal.readJavaFormatString(FloatingDecimal.java:2043)
                //	at sun.misc.FloatingDecimal.parseDouble(FloatingDecimal.java:110)
                //	at java.lang.Double.parseDouble(Double.java:538)
                if (!TextUtils.isEmpty(num) && !num.startsWith(".")) {
                    double numd = DoubleUtils.parseDouble(num);
                    double sum = DoubleUtils.parseDouble(NorUtils.NumberFormatd(quantityPrecision).format(numd / coinInfo.getPrice()));
                    mBinding.numEt.setTextSize(22);
                    setTextValue(mBinding.numEt, sum + "");
                    if (TradeType == 2) {
                        Double myAsset = sum;
                        if (numd > DoubleUtils.parseDouble(coinInfo.getOrderMin_s()) && myAsset < numd && numd < DoubleUtils.parseDouble(coinInfo.getOrderMax_s())) {
                            mBinding.warnTv.setVisibility(View.VISIBLE);
                            mBinding.warnTv1.setVisibility(View.VISIBLE);
                            mBinding.warnTv.setText(R.string.fiat_);
                            return;
                        }
                    }
                    if (sum > DoubleUtils.parseDouble(coinInfo.getOrderMax_s())) {
                        mBinding.warnTv.setVisibility(View.VISIBLE);
                        mBinding.warnTv1.setVisibility(View.INVISIBLE);
                        mBinding.warnTv.setText(R.string.too_large);
                    } else if (sum < DoubleUtils.parseDouble(coinInfo.getOrderMin_s())) {
                        mBinding.warnTv.setVisibility(View.VISIBLE);
                        mBinding.warnTv1.setVisibility(View.INVISIBLE);
                        mBinding.warnTv.setText(R.string.too_small);
                    } else {
                        mBinding.warnTv1.setVisibility(View.INVISIBLE);
                        mBinding.warnTv.setVisibility(View.INVISIBLE);
                    }
                } else {
                    mBinding.warnTv1.setVisibility(View.INVISIBLE);
                    mBinding.warnTv.setVisibility(View.INVISIBLE);
                    setTextValue(mBinding.numEt, "");
                    mBinding.numEt.setTextSize(13);
                }
            }
        });
        mBinding.allAmountTv.setOnClickListener(this);
        getTotalUserWallet();
    }

    private void setTextValue(TextView txt, String val) {
        tag = true;
        txt.setText(val);
    }

    private void setTextValue(TextView txt, String val, boolean tag) {
        this.tag = tag;
        txt.setText(val);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mIsTimeRecord) {
            startCountDown(mTimeRecord);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopCountDown();
    }

    //获取总资产
    void getTotalUserWallet() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.getTotalUserWallet, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    TotalUserWalletBean userWalletBean = new Gson().fromJson(result, TotalUserWalletBean.class);
                    if (userWalletBean.getCode() == 0) {
                        totalVirtualAsset = DoubleUtils.parseDouble(userWalletBean.getTotalVirtualAsset());
                    } else {
                        totalVirtualAsset = -1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void add_order() {
        if (TextUtils.isEmpty(mBinding.numEt.getText().toString().trim())) {
            ToastUtils.showShortToast(R.string.cx85);
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("adId", coinInfo.getId() + "");
        params.put("type", TradeType + "");//用户 购买 1, 用户出售 2
        params.put("amount", mBinding.numEt.getText().toString());
        if (TradeType == 2) {
            if (TextUtils.isEmpty(mBinding.pswTv.getText().toString().trim())) {
                ToastUtils.showShortToast(R.string.cx86);
                return;
            }
            params.put("tradePwd", mBinding.pswTv.getText().toString());
        }
        params = OkhttpManager.encrypt(params);
        params.put("price", coinInfo.getPrice() + "");
        params.put("externalPayAccountType", (TextUtils.isEmpty(coinInfo.getPayAccountType())) ? "" : coinInfo.getPayAccountType());
        params.put("externalCoinId", coinInfo.getCoinId() + "");
        params.put("externalCoinName", coinInfo.getCoinName() + "");
        params.put("externalUserName", coinInfo.getUserName() + "");
        params.put("isExternal", coinInfo.getIsExternal() + "");
        params.put("externalChannel", TextUtils.isEmpty(coinInfo.getExternalChannel()) ? "" : coinInfo.getExternalChannel());
        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(this);
//        showResult();
        conmitOrder(params);
    }

    private void conmitOrder(Map<String, String> params) {
        OkhttpManager.postAsync(UrlConstants.add_order, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                SnackBarUtils.ShowRed(OtcBuyActivity.this, errorMsg);
                DialogManager.INSTANCE.dismiss();
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.optInt("code", -1);
                    String value = jsonObject.optString("value");
                    if (code == 0) {
                        commitOrderSuccess(jsonObject);
                    } else if (code == 102) {
                        showResult(value);
                    } else if (code == 123) {//需要faceId验证code
                        aliVerify(jsonObject.optString("data"));
                    } else if (code == 124) {//黑名单
                        FaceVerifyHelper.getInstance().aliVerifyBlack(mContext, value, new FaceVerifyHelper.FaceDialogListener() {
                            @Override
                            public void showDialog() {
                                mIsTimeRecord = false;//防止home再进入触发onStart内的重新计时
                                stopCountDown();//该页面显示了face的结果dialog需要停止计时器
                            }

                            @Override
                            public void dismissDialog() {
                                mIsTimeRecord = true;
                                startCountDown(mTimeRecord);
                            }
                        });
                    } else if (code == 125) {
                        DialogUtils.getInstance().showOneButtonDialog(OtcBuyActivity.this, value, getString(R.string.str_isee));
                    } else {
                        MToast.show(OtcBuyActivity.this, value, 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void aliVerify(String triggerType) {
        DialogUtils.getInstance().showRiskTipButtonDialog(mContext, new DialogUtils.onBnClickListener() {
            @Override
            public void onLiftClick(AlertDialog dialog, View view) {
                AppHelper.dismissDialog(dialog);
            }

            @Override
            public void onRightClick(AlertDialog dialog, View view) {
                AppHelper.dismissDialog(dialog);
                //去验证（阿里face验证）
                FaceVerifyHelper.getInstance().verify(mContext, new DialogUtils.onBnClickListener() {
                    @Override
                    public void onLiftClick(AlertDialog dialog, View view) {
                        AppHelper.dismissDialog(dialog);
                    }

                    @Override
                    public void onRightClick(AlertDialog dialog, View view) {
                        AppHelper.dismissDialog(dialog);
                        mIsTimeRecord = true;
                        startCountDown(mTimeRecord);
                    }
                }, new FaceVerifyHelper.FaceDialogListener() {
                    @Override
                    public void showDialog() {
                        mIsTimeRecord = false;//防止home再进入触发onStart内的重新计时
                        stopCountDown();//该页面显示了face的结果dialog需要停止计时器
                    }

                    @Override
                    public void dismissDialog() {
                        mIsTimeRecord = true;
                        startCountDown(mTimeRecord);
                    }
                }, triggerType);
            }
        });
    }

    private void commitOrderSuccess(JSONObject jsonObject) {
        //  ToastUtils.showShortToast(value);
        if (TradeType == 1) {
            int orderId = jsonObject.optInt("data", 0);
            Intent intent = new Intent(OtcBuyActivity.this, OtcPaymentActivity.class);
            intent.putExtra("data", orderId);
            startActivity(intent);
        } else {
            int orderId = jsonObject.optInt("data", 0);
            Intent intent = new Intent(OtcBuyActivity.this, OtcUserSellPaymentActivity.class);
            intent.putExtra("data", orderId);
            startActivity(intent);
        }
        finish();
    }

    private void showResult(String value) {
        DialogUtils.getInstance().showTwoButtonDialog(this, value, getString(R.string.j8), getString(R.string.verification_));
        DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
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
                Intent intent = new Intent(OtcBuyActivity.this, AuthActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCountDown();
    }

    private void stopCountDown() {
        if (ms != null && !ms.isUnsubscribed()) {
            ms.unsubscribe();
        }
    }

    private void startCountDown(int time) {
        ms = Observable.interval(0, 1, TimeUnit.SECONDS).limit(time).map(aLong -> (time - 1) - aLong).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
            if (mBinding == null || mBinding.cancleTv == null) {
                return;
            }
            mBinding.cancleTv.setText(aLong + getString(R.string.cx84));
            mTimeRecord = new Long(aLong).intValue();
            if (aLong == 1) {
                finish();
            }
        }).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_tv:
                add_order();
                break;
            case R.id.cancle_tv:
            case R.id.close_iv:
                finish();
                break;
            case R.id.allAmount_tv://全部
                if (TradeType == 2) {
                    String myAst = UserInfoManager.getOtcUserInfoBean().getAsset(coinInfo.getCoinId());
                    if (!TextUtils.isEmpty(myAst)) {
                        Double myAsset = Double.parseDouble(myAst);
                        if (myAsset > Double.parseDouble(coinInfo.getOrderMax_s())) {
                            setTextValue(mBinding.numEt, coinInfo.getOrderMax_s(), false);
                        } else if (myAsset < coinInfo.getLeftAmount()) {
                            setTextValue(mBinding.numEt, myAsset + "", false);
                        } else {
                            setTextValue(mBinding.numEt, coinInfo.getLeftAmount() + "", false);
                        }
                    } else {
                        setTextValue(mBinding.numEt, coinInfo.getOrderMax_s() + "", false);
                    }
                } else {
                    if (coinInfo.getLeftAmount() < Double.parseDouble(coinInfo.getOrderMax_s())) {
                        setTextValue(mBinding.numEt, coinInfo.getLeftAmount() + "", false);
                    } else {
                        setTextValue(mBinding.numEt, coinInfo.getOrderMax_s() + "", false);
                    }
                }
                mBinding.numEt.setSelection((mBinding.numEt.getText().toString() + "").length());
                mBinding.numEt.setTextSize(22);
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.otc_act_out, R.anim.otc_act_out);
    }
}
