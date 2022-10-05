package huolongluo.byw.byw.ui.activity.cointixian;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

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

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.bean.CoinTiXianAddressBean;
import huolongluo.byw.byw.bean.FeeListBean;
import huolongluo.byw.byw.bean.WithdrawChainBean;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.walletlist.NewWalletListActivity;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.mine.activity.BindHPaySuccessActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.byw.util.zxing.MipcaActivityCapture;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.DoubleUtils;
import okhttp3.Request;
/**
 * Created by LS on 2018/7/12.
 */
public class HppTXActivity extends BaseActivity {
    private String shortName;
    public static int coinId;
    private Double frozen;
    private Double total;
    private String logo;
    private int withdrawDigit;
    private List<FeeListBean> mData;
    private List<WithdrawChainBean> mData1;
    public static String addressId;
    public static String address;
    private String level;
    private int times = 120;
    @BindView(R.id.back_iv)
    ImageView back;
    @BindView(R.id.iv_logo)
    ImageView ivlogo;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.address_iv)
    ImageView address_iv;
    /* @BindView(R.id.tv_coin)
     TextView tvCoin;*/
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.et_price)
    EditText etPrice;
    @BindView(R.id.aaaatv)
    TextView aaaatv;
    @BindView(R.id.tv_fee)
    TextView tvFee;
    @BindView(R.id.tv_coin_fee)
    TextView tvCoinFee;
    @BindView(R.id.tv_all_amount)
    TextView tvAllamount;
    @BindView(R.id.et_zijin)
    EditText etTrade;
    @BindView(R.id.et_google)
    EditText etGoogle;
    @BindView(R.id.tv_paste)
    TextView tvPaste;
    @BindView(R.id.et_duanxin)
    EditText etDuanxin;
    @BindView(R.id.tv_duanxin)
    TextView tvDuanxin;
    @BindView(R.id.tv_submit)
    Button tvSumbit;
    @BindView(R.id.iv_scan)
    ImageView ivScan;
    @BindView(R.id.ll_google)
    RelativeLayout ll_google;
    @BindView(R.id.ll_memo)
    RelativeLayout ll_memo;
    @BindView(R.id.et_memo)
    EditText et_memo;
    @BindView(R.id.tv_memo)
    TextView tv_memo;
    @BindView(R.id.amount_tv)
    TextView amount_tv;
    @BindView(R.id.name_tv)
    TextView name_tv;
    @BindView(R.id.name1_tv)
    TextView name1_tv;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.coinName_tv)
    TextView coinName_tv;
    @BindView(R.id.coinName_tv1)
    TextView coinName_tv1;
    @BindView(R.id.max_tv)
    TextView max_tv;
    @BindView(R.id.coinName_tv2)
    TextView coinName_tv2;
    java.text.DecimalFormat df = new java.text.DecimalFormat("######0.0000");
    private boolean iseos = false;
    private String minWithdraw;
    private String maxWithdraw;
    private String memoName;

    /**
     * bind layout resource file
     */
    @Override
    protected int getContentViewId() {
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // return R.layout.activity_tx;
        return R.layout.activity_hpptixian;
    }

    /**
     * Dagger2 use in your application module(not used in 'base' module)
     */
    @Override
    protected void injectDagger() {
    }

    /**
     * init views and events here
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initViewsAndEvents() {
        title_tv.setText(getString(R.string.hyytx));
        if (getBundle() != null) {
            Bundle bundle = getBundle();
            shortName = bundle.getString("shortName");
            AppUtils.showDialogForGRINBiz(this, shortName, getString(R.string.str_msg_coin_grin_withdraw));
            coinId = bundle.getInt("coinId");
            frozen = bundle.getDouble("frozen");
            total = bundle.getDouble("total");
            logo = bundle.getString("logo");
            iseos = bundle.getBoolean("iseos");
            withdrawDigit = bundle.getInt("withdrawDigit", 4);
            memoName = bundle.getString("memoName");
            iseos = !TextUtils.equals(memoName, "0");
            minWithdraw = bundle.getString("minWithdraw");
            maxWithdraw = bundle.getString("maxWithdraw");
        }
        etPrice.setHint(getString(R.string.mintb) + minWithdraw);
        aaaatv.setText(getString(R.string.mintb) + minWithdraw);
        max_tv.setText(getString(R.string.maxtb) + maxWithdraw);
        coinName_tv.setText(shortName);
        coinName_tv1.setText(shortName);
        coinName_tv2.setText(shortName);
        if (iseos) {
            ll_memo.setVisibility(View.VISIBLE);
        } else {
            ll_memo.setVisibility(View.GONE);
        }
        if (UserInfoManager.getUserInfo().isGoogleBind()) {
            ll_google.setVisibility(View.VISIBLE);
        } else {
            ll_google.setVisibility(View.GONE);
        }
        RequestOptions ro = new RequestOptions();
        ro.error(R.mipmap.rmblogo);
        ro.centerCrop();
        Glide.with(this).load(logo).apply(ro).into((ImageView) ivlogo);
        tvPrice.setText(String.valueOf(total));
        tvName.setText(shortName);
        name_tv.setText(shortName);
        name1_tv.setText(shortName);
        //    tvCoin.setText(shortName + "提币地址");
        tvCoinFee.setText(shortName);
        //  getFeeAndAddress();
        etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.toString().contains(".")) {
                    String aa = s.toString();
                    // 说明后面有三位数值
                    int leng = s.toString().indexOf(".");
                    if ((aa.length() > leng + withdrawDigit + 1) && withdrawDigit > 0) {
                        String ss = s.toString().substring(0, leng + withdrawDigit + 1);
                        etPrice.setText(ss);
                        etPrice.setSelection(ss.length());
                    } else if (withdrawDigit == 0) {
                        String ss = s.toString().substring(0, leng);
                        etPrice.setText(ss);
                        etPrice.setSelection(ss.length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    if (TextUtils.equals(".", editable.toString())) {
                        amount_tv.setText("0");
                        return;
                    }
                    amount_tv.setText(NorUtils.NumberFormat(4).format(DoubleUtils.parseDouble(etPrice.getText().toString())));
                } else {
                    amount_tv.setText("0");
                }
            }
        });
        eventClick(ivScan).subscribe(o -> {
            Intent intent = new Intent(HppTXActivity.this, MipcaActivityCapture.class);
            startActivityForResult(intent, 1);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(address_iv).subscribe(o -> {
            Bundle bundle = new Bundle();
            bundle.putString("shortName", shortName);
            bundle.putInt("id", coinId);
            bundle.putString("fromClass", "CoinTiXianActivity");
            Intent intent = new Intent(this, NewWalletListActivity.class);
            intent.putExtra("shortName", shortName);
            intent.putExtra("id", coinId);
            intent.putExtra("fromClass", "CoinTiXianActivity");
            startActivity(intent);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tvAddress).subscribe(o -> {
            Bundle bundle = new Bundle();
            bundle.putString("shortName", shortName);
            bundle.putInt("id", coinId);
            bundle.putString("fromClass", "CoinTiXianActivity");
//            startActivity(WalletListActivity.class, bundle);
            Intent intent = new Intent(this, NewWalletListActivity.class);
            intent.putExtra("shortName", shortName);
            intent.putExtra("id", coinId);
            intent.putExtra("fromClass", "CoinTiXianActivity");
            startActivity(intent);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tvDuanxin).subscribe(o -> {
            getSms();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(back).subscribe(o -> {
            finish();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tvSumbit).subscribe(o -> {
            sumbit();
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tvAllamount).subscribe(o -> {
            etPrice.setText(String.valueOf(total));
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        // 粘贴板有数据，并且是文本
        ClipboardManager mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        eventClick(tvPaste).subscribe(o -> {
            if (mClipboardManager.hasPrimaryClip() && mClipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                ClipData.Item item = mClipboardManager.getPrimaryClip().getItemAt(0);
                CharSequence text = item.getText();
                if (text == null) {
                    return;
                }
                etGoogle.setText(text);
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        eventClick(tv_memo).subscribe(o -> {
            if (mClipboardManager.hasPrimaryClip() && mClipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                ClipData.Item item = mClipboardManager.getPrimaryClip().getItemAt(0);
                CharSequence text = item.getText();
                if (text == null) {
                    return;
                }
                et_memo.setText(text);
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void sumbit() {
        if (etPrice.getText().toString().isEmpty()) {
            showMessage(getString(R.string.empty_je), 2);
            return;
        }
        if (!TextUtils.isEmpty(minWithdraw) && DoubleUtils.parseDouble(etPrice.getText().toString()) < DoubleUtils.parseDouble(minWithdraw)) {
            showMessage(getString(R.string.minje) + minWithdraw, 2);
            return;
        }
        if (tvAddress.getText().toString().isEmpty()) {
            //showMessage("提币地址不能为空", 2);
            // return;
        }
        /* if (et_memo.getText().toString().isEmpty()) {
            showMessage("MEMO不能为空", 2);
            return;
        }*/
        if (etTrade.getText().toString().isEmpty()) {
            showMessage(getString(R.string.psw_empty), 2);
            return;
        }
        if (UserInfoManager.getUserInfo().isGoogleBind()) {
            if (etGoogle.getText().toString().isEmpty()) {
                showMessage(getString(R.string.goole_empty), 2);
                return;
            }
        }
        if (etDuanxin.getText().toString().isEmpty()) {
            showMessage(getString(R.string.empty_msg), 2);
            return;
        }


            /*   if(TextUtils.isEmpty(level)){
                  showMessage("网络异常，请稍后重试", 2);
                   return;
               }
               if(TextUtils.isEmpty(addressId)){
                  showMessage("网络异常，请稍后重试", 2);
                   return;
               }*/
        HashMap<String, String> params = new HashMap<>();
        String loginToken = UserInfoManager.getToken();
        RSACipher rsaCipher = new RSACipher();
        String body = null;
        if (UserInfoManager.getUserInfo().isGoogleBind()) {
            if (iseos) {
                if (et_memo.getText().toString().isEmpty()) {
                    body = "coinId=" + URLEncoder.encode(String.valueOf(coinId)) + "&amount=" + URLEncoder.encode(etPrice.getText().toString()) + "&tradePwd=" + URLEncoder.encode(etTrade.getText().toString()) + "&phoneCode=" + URLEncoder.encode(etDuanxin.getText().toString()) + "&totpCode=" + URLEncoder.encode(etGoogle.getText().toString());
                } else {
                    body = "coinId=" + URLEncoder.encode(String.valueOf(coinId)) + "&amount=" + URLEncoder.encode(etPrice.getText().toString()) + "&tradePwd=" + URLEncoder.encode(etTrade.getText().toString()) + "&phoneCode=" + URLEncoder.encode(etDuanxin.getText().toString()) + "&totpCode=" + URLEncoder.encode(etGoogle.getText().toString());
                    params.put("memo", et_memo.getText().toString());
                }
            } else {
                body = "coinId=" + URLEncoder.encode(String.valueOf(coinId)) + "&amount=" + URLEncoder.encode(etPrice.getText().toString()) + "&tradePwd=" + URLEncoder.encode(etTrade.getText().toString()) + "&phoneCode=" + URLEncoder.encode(etDuanxin.getText().toString()) + "&totpCode=" + URLEncoder.encode(etGoogle.getText().toString());
            }
        } else {
            if (iseos) {
                if (et_memo.getText().toString().isEmpty()) {
                    body = "coinId=" + URLEncoder.encode(String.valueOf(coinId)) + "&amount=" + URLEncoder.encode(etPrice.getText().toString()) + "&tradePwd=" + URLEncoder.encode(etTrade.getText().toString()) + "&phoneCode=" + URLEncoder.encode(etDuanxin.getText().toString());
                } else {
                    body = "coinId=" + URLEncoder.encode(String.valueOf(coinId)) + "&amount=" + URLEncoder.encode(etPrice.getText().toString()) + "&tradePwd=" + URLEncoder.encode(etTrade.getText().toString()) + "&phoneCode=" + URLEncoder.encode(etDuanxin.getText().toString());
                    params.put("memo", et_memo.getText().toString());
                }
            } else {
                body = "coinId=" + URLEncoder.encode(String.valueOf(coinId)) + "&amount=" + URLEncoder.encode(etPrice.getText().toString()) + "&tradePwd=" + URLEncoder.encode(etTrade.getText().toString()) + "&phoneCode=" + URLEncoder.encode(etDuanxin.getText().toString());
            }
        }
        try {
            String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            params.put("body", body1);
            params.put("loginToken", loginToken);
            withdrawBtcSubmit(params);
        } catch (Exception e) {
            e.printStackTrace();
            MToast.show(HppTXActivity.this, getString(R.string.aa62), 2);
        }
    }

    void withdrawBtcSubmit(Map<String, String> params) {
        netTags.add(UrlConstants.hyperpayWithdraw);
        OkhttpManager.postAsync(UrlConstants.hyperpayWithdraw, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                SnackBarUtils.ShowRed(HppTXActivity.this, getString(R.string.net_timeout));
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("value");
                    if (code == 0) {
                        showMessage(msg, 2);
                        startActivity(new Intent(HppTXActivity.this, BindHPaySuccessActivity.class));
                        finish();
                    } else if (code == -10001) {
                        MToast.show(HppTXActivity.this, getString(R.string.login_invilod), 2);
                    } else {
                        showMessage(msg, 2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取短信验证码
    //type 为5时是提币时获取短信验证码
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getSms() {
        Log.e("geddtSms", "getSms");
        tvDuanxin.setEnabled(false);
        HashMap<String, String> params = new HashMap<>();
        String loginToken = SPUtils.getLoginToken();
        RSACipher rsaCipher = new RSACipher();
        try {
            String body = "type=" + URLEncoder.encode("5");
            String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            params.put("loginToken", loginToken);
            params.put("body", body1);
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
        netTags.add(UrlConstants.DOMAIN + UrlConstants.GET_SMS);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GET_SMS, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                tvDuanxin.setEnabled(true);
                MToast.show(HppTXActivity.this, getString(R.string.net_timeout1), 1);
            }

            @Override
            public void requestSuccess(String result) {
                tvDuanxin.setEnabled(true);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    String result1 = jsonObject.getString("result");
                    String msg = jsonObject.getString("value");
                    if (code == 0) {
                        showMessage(msg, 2);
                        times = 120;
                        new Thread(mTask).start();
                    } else {
                        showMessage(msg, 2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    Runnable mTask = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (times > 0) {
                try {
                    Thread.sleep(1000);
                    times--;
                    handler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    };
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (tvDuanxin != null) {
                Activity activity = HppTXActivity.this;
                if (activity != null) {
                    if (times == 0) {
                        tvDuanxin.setEnabled(true);
//                        if("1".equals(needSms)){
                        tvDuanxin.setText(R.string.aa63);
//                        }else if("1".equals(needEmail)){
//                            btn_code.setText(getResources().getString(R.string.get_email));
//                        }
                    } else {
                        tvDuanxin.setEnabled(false);
                        tvDuanxin.setText(times + "S");
                    }
                }
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void selectCoinAddress(Event.clickCoinAddress clickCoinAddress) {
        addressId = String.valueOf(clickCoinAddress.id);
        tvAddress.setText(clickCoinAddress.selectAddress);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
//        this.addressId = String.valueOf(clickCoinAddress.id);
//        getFeeAndAddress();
        if (addressId != null) {
            Log.d("00虚拟币地址id", addressId);
        }
        if (address != null) {
            tvAddress.setText(address);
        }
    }

    @Override
    protected void onDestroy() {
        if (handler != null) {
            try {
                handler.removeCallbacksAndMessages(null);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        super.onDestroy();
        addressId = "";
        address = "";
    }
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode ==1){
//            if(data!=null){
//                Bundle bundle = data.getExtras();
//                if(bundle==null){
//                    return ;
//                }
//                if(bundle.getInt(CodeUtils.RESULT_TYPE)==CodeUtils.RESULT_SUCCESS){
//                    String ret = bundle.getString(CodeUtils.RESULT_STRING);
//
//                    getFeeAndAddress();
//                    if (mData1.size()==0){
//                        showMessage("当前地址不在您的地址簿中，请先添加呦！",2);
//                        address = ret;
////                        tvAddress.setText(address);
//                        new Handler().postDelayed(() ->
//                        {
//                            Intent intent1 = new Intent(CoinTXNewActivity.this,AddAddressNewActivity.class);
//                            Bundle bundle1 = new Bundle();
//                            bundle1.putString("shortName", shortName);
//                            bundle1.putInt("id", coinId);
//                            bundle1.putString("logo",logo);
//                            bundle1.putString("address",ret);
//                            bundle1.putString("fromClass", "CoinTiXianActivity");
//                            intent1.putExtras(bundle1);
//                            startActivity(intent1);
//                        }, 1000);
//
//                        return;
//                    }
//                    if (mData1.size() >0){
//                        for (int i = 0;i<mData1.size();i++){
//                            String add = mData1.get(i).getAddress();
//                            if (ret.equals(mData1.get(i).getAddress())){
//                                address = ret;
//                                tvAddress.setText(address);
//                                return;
//                            }
//                        }
//
//                        showMessage("当前地址不在您的地址簿中，请先添加呦！",2);
//                        new Handler().postDelayed(() ->
//                        {
//                            Intent intent1 = new Intent(CoinTXNewActivity.this,AddAddressNewActivity.class);
//                            Bundle bundle1 = new Bundle();
//                            bundle1.putString("shortName", shortName);
//                            bundle1.putInt("id", coinId);
//                            bundle1.putString("logo",logo);
//                            bundle1.putString("address",ret);
//                            bundle1.putString("fromClass", "CoinTiXianActivity");
//                            intent1.putExtras(bundle1);
//                            startActivity(intent1);
//                        }, 1000);
//                    }
//
//                }
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            return !(event.getX() > left) || !(event.getX() < right) || !(event.getY() > top) || !(event.getY() < bottom);
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }
}
