package huolongluo.byw.byw.ui.fragment.maintab05;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseFragment;
import huolongluo.byw.byw.bean.UserAssetsBean;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.bancard.BankCardListActivity;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.byw.ui.activity.renzheng.RenZhengBeforeActivity;
import huolongluo.byw.byw.ui.fragment.maintab03.bean.AssetCoinsBean;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.c2c.oct.activity.C2cSellSuccessActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.helper.AppHelper;
import okhttp3.Request;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
/**
 * Created by LS on 2018/7/19.
 */
public class TXFragment extends BaseFragment {
    @BindView(R.id.tv_usercny)
    TextView tv_usercny;
    @BindView(R.id.tv_no)
    TextView tv_no;
    @BindView(R.id.et_bankcard)
    TextView et_bankcard;
    @BindView(R.id.tv_choosebank)
    TextView tv_choosebank;
    @BindView(R.id.et_withdrawBalance)
    EditText et_withdrawBalance;
    @BindView(R.id.et_tradepass)
    EditText et_tradepass;
    @BindView(R.id.ll_google_view)
    LinearLayout ll_google_view;
    @BindView(R.id.et_google_code)
    EditText et_google_code;
    @BindView(R.id.ll_phoneview)
    LinearLayout ll_phoneview;
    @BindView(R.id.et_code)
    EditText et_code;
    @BindView(R.id.tv_getcode)
    TextView tv_getcode;
    @BindView(R.id.tv_sure)
    TextView tv_sure;
    @BindView(R.id.service_tv)
    TextView service_tv;
    @BindView(R.id.progressbar1)
    ProgressBar progressbar1;
    @BindView(R.id.progressbar2)
    ProgressBar progressbar2;
    @BindView(R.id.rl_nologin)
    RelativeLayout rl_nologin;
    @BindView(R.id.btn_bus_login)
    Button btn_bus_login;
    @BindView(R.id.rl_identification)
    RelativeLayout rl_identification;
    @BindView(R.id.btn_bus_identification)
    Button btn_bus_identification;
    @BindView(R.id.loading_iv1)
    ImageView loading_iv1;
    private String shortName;
    public static int coinId;
    private String frozen;
    private String total;
    public String bankName = "";
    public String bankId = "";
    private List<AssetCoinsBean> coinsBeanList = new ArrayList<>();
    RSACipher rsaCipher = new RSACipher();
    double tradeFee = 0;
    private Subscription subscription;

    @Override
    protected void initDagger() {
    }

    /**
     * override this method to do operation in the fragment
     * @param rootView
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initViewsAndEvents(View rootView) {
        // getUserAssets();
        EventBus.getDefault().register(this);
        btn_bus_login.setOnClickListener(v -> startActivity(LoginActivity.class));
        loading_iv1.setImageResource(R.drawable.loading);
        AnimationDrawable animationDrawable = (AnimationDrawable) loading_iv1.getDrawable();
        animationDrawable.start();
        viewClick(btn_bus_identification, v -> {
            if (UserInfoManager.getUserInfo().isPostC2Validate()) {
                showMessage(getString(R.string.ee56), 2);
            } else {
                startActivity(RenZhengBeforeActivity.class);
            }
        });
        et_withdrawBalance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    if (s.toString().contains(".")) {
                        String aa = s.toString();
                        // 说明后面有三位数值
                        int leng = s.toString().indexOf(".");
                        if (aa.length() > leng + 3) {
                            String ss = s.toString().substring(0, leng + 3);
                            et_withdrawBalance.setText(ss);
                            et_withdrawBalance.setSelection(ss.length());
                        }
                    }
                    double fee = Double.parseDouble(s.toString()) * tradeFee;
                    ;
                    if (fee < 2) {
                        service_tv.setText("2");
                    } else {
                        service_tv.setText(NorUtils.NumberFormatFString("0.##").format(fee) + "");
                    }
                } else {
                    service_tv.setText("");
                }
            }
        });
        if (UserInfoManager.isLogin()) {
            if (UserInfoManager.getUserInfo().isBindMobil()) {
                ll_phoneview.setVisibility(View.VISIBLE);
            } else {
                ll_phoneview.setVisibility(View.GONE);
            }
            if (UserInfoManager.getUserInfo().isGoogleBind()) {
                ll_google_view.setVisibility(View.VISIBLE);
            } else {
                ll_google_view.setVisibility(View.GONE);
            }
        }
        eventClick(tv_choosebank).subscribe(o -> {
            if (UserInfoManager.isLogin()) {
                if (!UserInfoManager.getUserInfo().isHasC2Validate()) {
                    Toast.makeText(getActivity(), R.string.ee57, Toast.LENGTH_SHORT).show();
                    return;
                }
                et_bankcard.setText("");
                startActivityForResult(new Intent(getActivity(), BankCardListActivity.class), 101);
            } else {
                Toast.makeText(getActivity(), R.string.ee58, Toast.LENGTH_SHORT).show();
            }
            //   startActivity(BankCardListActivity.class);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tv_getcode).subscribe(o -> {
            if (!UserInfoManager.isLogin()) {
                Toast.makeText(getActivity(), R.string.ee59, Toast.LENGTH_SHORT).show();
                return;
            } else if (!UserInfoManager.getUserInfo().isHasC2Validate()) {
                if (UserInfoManager.getUserInfo().isPostC2Validate()) {
                    showMessage(getString(R.string.ee60), 2);
                } else {
                    Toast.makeText(getActivity(), R.string.ee61, Toast.LENGTH_LONG).show();
                }
                return;
            }
            sendMessage();
//            subscription = rmbTiXianPresent.sendMessage("4", Share.get().getLogintoken());
           /* String type = "type=" + URLEncoder.encode("4");
            try {
                String type1 = rsaCipher.encrypt(type, UrlConstants.publicKeys);
                sendMessage(type1);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            }*/
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tv_sure).subscribe(o -> {
            if (!UserInfoManager.isLogin()) {
                startActivity(LoginActivity.class);
                return;
            } else if (!UserInfoManager.getUserInfo().isHasC2Validate()) {
                if (UserInfoManager.getUserInfo().isPostC2Validate()) {
                    showMessage(getString(R.string.ee62), 2);
                } else {
                    DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.ee72), getString(R.string.ee63), getString(R.string.ee71));
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
                            startActivity(RenZhengBeforeActivity.class);
                        }
                    });
                }
                return;
            }
            if ((!TextUtils.isEmpty(et_bankcard.getText().toString().trim())) && (!TextUtils.isEmpty(et_tradepass.getText().toString().trim()))//
                    && (!TextUtils.isEmpty(et_withdrawBalance.getText().toString().trim()))) {
//                if (!Share.get().getIsBindGoogle()){
//                    showMessage("请先绑定google",2);
//
//                }
                if (UserInfoManager.getUserInfo().isGoogleBind()) {
                    if (et_google_code.getText().toString().isEmpty()) {
                        showMessage(getString(R.string.aa53), 2);
                        return;
                    }
                }
                String withdrawBank = URLEncoder.encode(bankId);
                String tradePwd = URLEncoder.encode(et_tradepass.getText().toString());
                String withdrawBalance = URLEncoder.encode(et_withdrawBalance.getText().toString());
                String phoneCode = URLEncoder.encode(et_code.getText().toString());
                String totpCode = URLEncoder.encode(et_google_code.getText().toString());
                String body = null;
                if (UserInfoManager.getUserInfo().isGoogleBind()) {
                    body = "tradePwd=" + URLEncoder.encode(et_tradepass.getText().toString().trim()) + "&withdrawBalance=" + URLEncoder.encode(et_withdrawBalance.getText().toString().trim()) + "&phoneCode=" + URLEncoder.encode(et_code.getText().toString().trim()) + "&totpCode=" + URLEncoder.encode(et_google_code.getText().toString().trim()) + "&withdrawBank=" + URLEncoder.encode(bankId);
                } else {
                    body = "tradePwd=" + URLEncoder.encode(et_tradepass.getText().toString().trim()) + "&withdrawBalance=" + URLEncoder.encode(et_withdrawBalance.getText().toString().trim()) + "&phoneCode=" + URLEncoder.encode(et_code.getText().toString().trim()) + "&withdrawBank=" + URLEncoder.encode(bankId);
                }
                try {
                    String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
                    TiXian(body1);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                }
//                subscription = rmbTiXianPresent.rmbTiXian(et_tradepass.getText().toString().trim(), et_withdrawBalance.getText().toString().trim(), null,//
//                        Share.get().getIsBindPhone() ? et_code.getText().toString().trim() : "", //
//                        Share.get().getIsBindGoogle() ? et_google_code.getText().toString().trim() : "", //
//                        null, Share.get().getLogintoken());
            } else {
                showMessage(getString(R.string.ee64), 1);
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        if (UserInfoManager.isLogin()) {
            if (!UserInfoManager.getUserInfo().isHasC2Validate()) {
                rl_identification.setVisibility(View.VISIBLE);
            } else {
                rl_identification.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        setView();
    }

    public void setView() {
        if (rl_nologin != null) {
            if (UserInfoManager.isLogin()) {
                rl_nologin.setVisibility(View.GONE);
                // if (!UserInfoManager.getUserInfo().isHasC2Validate()) {
                if (!UserInfoManager.getUserInfo().isHasC2Validate()) {
                    rl_identification.setVisibility(View.VISIBLE);
                } else {
                    rl_identification.setVisibility(View.GONE);
                }
            } else {
                rl_identification.setVisibility(View.GONE);
                rl_nologin.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == 102) {
            if (data != null) {
                bankName = data.getStringExtra("bankName");
                bankId = data.getStringExtra("bankId");
                et_bankcard.setText(bankName);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void selectCoinAddress(Event.clickBankCard clickBankCard) {
        //   Log.e("selectCoinAddress","+  "+clickBankCard.address+"   "+clickBankCard.bankId+"  "+clickBankCard.bankNumber+"  "+clickBankCard.bankType);
        //  et_bankcard.setText(clickBankCard.bankNumber);
    }

    /**
     * override this method to return content view id of the fragment
     * <p>
     * ****************************************************************************************************************
     */
    @Override
    protected int getContentViewId() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.fragment_tx_c2c;
    }

    //获取资产信息
    private void getUserAssets() {
        Map<String, String> params = new HashMap<>();
        if (!UserInfoManager.isLogin()) {
            progressbar1.setVisibility(View.GONE);
            progressbar2.setVisibility(View.GONE);
            //  tv_usercny.setText("未登录");
            //  tv_no.setText("未登录");
            return;
        }
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.getUserWalletCnyt, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                SnackBarUtils.ShowRed(getActivity(), errorMsg);
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                Log.d("UserWalletbn", result);
                UserAssetsBean userAssetsBean;
                try {//{"result":true,"total":"18722620.3567","code":0,"frozen":"310.0000","cnytFee":"0.1","value":"操作成功！"}
                    // UserWalletbn userWalletbn = JacksonUtils.json2pojo(result, UserWalletbn.class);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        String total = jsonObject.getString("total");
                        String frozen = jsonObject.getString("frozen");
                        String cnytFee = jsonObject.getString("cnytFee");
                        progressbar1.setVisibility(View.GONE);
                        progressbar2.setVisibility(View.GONE);
                        if (!TextUtils.isEmpty(cnytFee)) {
                            tradeFee = Double.parseDouble(cnytFee);
                        }
                        tv_usercny.setText(total);
                        tv_no.setText(frozen);
                    } else {
                        if (code != 401){
                            MToast.show(getActivity(), jsonObject.getString("value"), 1);
                        }
                    }

                 /*  if (userWalletbn.getCode() == 0) {
                        coinsBeanList = userWalletbn.getUserWallet();
                        frozen = coinsBeanList.get(0).getFrozen();
                        total = coinsBeanList.get(0).getTotal();
                        tradeFee = coinsBeanList.get(0).getFee();
                        progressbar1.setVisibility(View.GONE);
                        progressbar2.setVisibility(View.GONE);

                        tv_usercny.setText(total);
                        tv_no.setText(frozen);

                    }else {
                        MToast.show(getActivity(),userWalletbn.getValue(),1);
                    }*/
                } catch (Exception e) {
                    MToast.show(getActivity(), getString(R.string.ee65), 1);
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserAssets();
//        et_bankcard.setText(bankName);
        if (UserInfoManager.isLogin()) {
            rl_nologin.setVisibility(View.GONE);
        } else {
            rl_nologin.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 更新用户信息，刷新界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshInfo(Event.refreshInfo refreshInfo) {
        if (UserInfoManager.isLogin()) {
            getUserAssets();
            if (UserInfoManager.isLogin()) {
                rl_nologin.setVisibility(View.GONE);
            } else {
                rl_nologin.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 收到 退出登录 的通知
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exitApp(Event.exitApp exit) {
        clearText();
    }

    @Override
    public void onDestroy() {
        AppHelper.unsubscribe(subscription);
        super.onDestroy();
        bankName = "";
        bankId = "";
        EventBus.getDefault().unregister(this);
    }

    //提现
    private void TiXian(String body) {
        HashMap<String, String> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        params.put("body", body);
        showProgress("");
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.WithDrawCny, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                SnackBarUtils.ShowRed(getActivity(), errorMsg);
                e.printStackTrace();
                hideProgress();
            }

            @Override
            public void requestSuccess(String result) {
                hideProgress();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.d("CNYT提现", result);
                    int code = jsonObject.getInt("code");
                    String value = jsonObject.getString("value");
                    // {"result":true,"code":0,"value":"提现请求成功，请耐心等待管理员处理"}
                    if (code == 0) {
                        //  showMessage("提现成功", 1);
                        SnackBarUtils.ShowBlue(getActivity(), getString(R.string.ee66));
                        Bundle bundle = new Bundle();
                        //  bundle.putString("History","TX");
                        //   startActivity(CnyCTHistoryActivity.class,bundle);
                        Intent intent = new Intent(getActivity(), C2cSellSuccessActivity.class);
                        intent.putExtra("total", et_withdrawBalance.getText().toString());
                        intent.putExtra("fee", service_tv.getText().toString());
                        intent.putExtra("bankCard", et_bankcard.getText().toString());
                        startActivity(intent);
                        clearText();
//                        close();
                    } else {
                        showMessage(value, 1);
                    }
                } catch (JSONException e) {
                    MToast.show(getActivity(), getString(R.string.ee67), 1);
                    e.printStackTrace();
                }
            }
        });
    }

    private void clearText() {
        et_bankcard.setText("");
        et_bankcard.clearFocus();
        et_withdrawBalance.setText("");
        et_withdrawBalance.clearFocus();
        service_tv.setText("-- --");
        et_tradepass.setText("");
        et_tradepass.clearFocus();
        et_code.setText("");
        et_code.clearFocus();
        et_google_code.setText("");
        et_google_code.clearFocus();
    }

    //发送验证码
    private void sendMessage() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "4");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        loading_iv1.setVisibility(View.VISIBLE);
        tv_getcode.setText("");
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.SendMessageCode, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                loading_iv1.setVisibility(View.GONE);
                SnackBarUtils.ShowRed(getActivity(), getString(R.string.ee68));
                tv_getcode.setText(getString(R.string.getmsg));
            }

            @Override
            public void requestSuccess(String result) {
                Log.d("获取验证码成功", result);
                loading_iv1.setVisibility(View.GONE);
                SnackBarUtils.ShowBlue(getActivity(), getString(R.string.ee69));
                subscription = Observable.interval(0, 1, TimeUnit.SECONDS).limit(120).map(aLong -> 120 - aLong).doOnSubscribe(() -> {
                    if (tv_getcode != null) {
                        tv_getcode.setEnabled(false);
                    }
                }).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
                    if (tv_getcode != null) {
                        tv_getcode.setText(aLong + " s");
                    }
                }).doOnCompleted(() -> {
                    if (tv_getcode != null) {
                        tv_getcode.setText(R.string.ee70);
                        tv_getcode.setEnabled(true);
                    }
                }).onErrorReturn(throwable -> {
                    Logger.getInstance().errorLog(throwable);
                    return 120L;
                }).doOnError(throwable -> Logger.getInstance().error(throwable)).subscribe();
            }
        });
    }
}
