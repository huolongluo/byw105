package huolongluo.byw.byw.ui.activity.cointixian;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.legend.model.enumerate.common.CommonCodeTypeEnum;
import com.android.legend.ui.bottomSheetDialogFragment.CommonSmsGoogleVerifyBottomDialogFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.math.BigDecimal;
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
import butterknife.OnClick;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.bean.WithdrawBean;
import huolongluo.byw.byw.bean.WithdrawChainBean;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.manager.DialogManager2;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;

import com.android.legend.ui.login.LoginActivity;
import com.legend.common.view.textview.DashTextView;
import com.onfido.api.client.Utils;

import huolongluo.byw.byw.ui.activity.renzheng.AuthActivity;
import huolongluo.byw.byw.ui.activity.walletlist.NewWalletListActivity;
import huolongluo.byw.byw.ui.fragment.maintab03.bean.AssetCoinsBean;
import huolongluo.byw.byw.ui.fragment.maintab03.bean.WithdrawFeeBean;
import huolongluo.byw.byw.ui.fragment.maintab03.bean.WithdrawLimitBean;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.WithdrawLimit;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.FastClickUtils;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.MathHelper;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.DataUtils;
import huolongluo.bywx.utils.DoubleUtils;
import okhttp3.Request;

/**
 * Created by LS on 2018/7/12.
 * zh提现页面
 */
public class CoinTXNewActivity extends BaseActivity {
    private String shortName;
    private String logo;
    public static int coinId;
    private String total;
    private int withdrawDigit;
    public static String addressId = "";
    //跳转切换提币地址后，判断切换后地址是否是内部地址，true不收手续费，false收手续费
    private boolean mIsInternalAddress = false;
    //判断默认提币地址是否是内部地址
    private boolean mIsInternalAddressDefault = false;
    //判断是不是默认地址，默认是，切换链名也会恢复默认地址
    private boolean mIsDefaultAddress = true;
    private String level;
    @BindView(R.id.back_iv)
    ImageView back;
    @BindView(R.id.ivIcon)
    ImageView ivIcon;
    @BindView(R.id.tvCoinWithdraw)
    TextView tvCoinWithdraw;
    @BindView(R.id.tvAvailable)
    TextView tvAvailable;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_des)
    TextView desTxt;
    @BindView(R.id.et_price)
    EditText etPrice;
    @BindView(R.id.tvRiskTips)
    TextView tvRiskTips;
    @BindView(R.id.aaaatv)
    TextView aaaatv;
    @BindView(R.id.rvChain)
    RecyclerView rvChain;
    @BindView(R.id.aaaatv1)
    TextView aaaatv1;
    @BindView(R.id.tv_fee)
    TextView tvFee;
    @BindView(R.id.dtv_Fee)
    DashTextView dtvFee;
    @BindView(R.id.tvRealAmountValue)
    TextView tvRealAmountValue;//实际到账数量
    @BindView(R.id.tvRealAmountUnit)
    TextView tvRealAmountUnit;
    @BindView(R.id.tvPriceTips)
    TextView tvPriceTips;
    @BindView(R.id.tv_coin_fee)
    TextView tvCoinFee;
    @BindView(R.id.tv_all_amount)
    TextView tvAllamount;
    @BindView(R.id.et_zijin)
    EditText etTrade;
    @BindView(R.id.tv24HLimit)
    TextView tv24HLimit;
    @BindView(R.id.llOtcTips)
    LinearLayout llOtcTips;
    @BindView(R.id.tv_submit)
    Button tvSumbit;
    @BindView(R.id.ll_memo)
    RelativeLayout ll_memo;
    @BindView(R.id.et_memo)
    EditText et_memo;
    @BindView(R.id.tv_memo)
    TextView tv_memo;
    @BindView(R.id.ll_des)
    LinearLayout descLayout;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.coinName_tv)
    TextView coinName_tv;
    @BindView(R.id.tv6)
    TextView tv6;
    @BindView(R.id.coinName_tv1)
    TextView coinName_tv1;
    @BindView(R.id.coinName_tv2)
    TextView coinName_tv2;
    @BindView(R.id.des)
    TextView des;
    @BindView(R.id.desLayout)
    LinearLayout desLayout;
    @BindView(R.id.ivQuestion)
    ImageView ivQuestion;
    @BindView(R.id.tvLimit)
    TextView tvLimit;
    private boolean iseos = false;
    private ArrayList<WithdrawFeeBean> feeList;
    double minimumService;//最小手续费
    private ArrayList<WithdrawLimitBean> withdrawQtyArr;//提币最小最大
    private String memoName;
    private AssetCoinsBean assetBean;
    private HashMap<String, WithdrawChainBean> cacheMap = new HashMap<String, WithdrawChainBean>();
    private WithdrawLimit withdrawLimit;//提现额度
    private final int TYPE1 = 1, TYPE2 = 2, TYPE3 = 3;
    //BCH分叉业务
    private boolean isBCH = false;
    private BaseQuickAdapter<String, BaseViewHolder> adapter;
    private WithdrawBean withdrawBean;
    private List<String> chainList;//链名列表
    private List<WithdrawChainBean> addressList;//地址列表
    private int currentChainPosition = 0;

    /**
     * bind layout resource file
     */
    @Override
    protected int getContentViewId() {
        return R.layout.activity_cointixian_usdt;
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
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initViewsAndEvents() {
        title_tv.setText(R.string.aa44);
        initChainView();
        if (getBundle() != null) {
            Bundle bundle = getBundle();
            assetBean = bundle.getParcelable("assetBean");
            if (assetBean == null) return;

            String mainNetworkWithdraw = assetBean.getMainNetworkWithdraw();
            if (!TextUtils.isEmpty(mainNetworkWithdraw) && !mainNetworkWithdraw.equals("0")) {
                desLayout.setVisibility(View.VISIBLE);
                des.setText(AppConstants.COMMON.POINT + mainNetworkWithdraw);
            }
            String withdrawlimitqty = assetBean.getWithdrawlimitqty();
            if (DoubleUtils.parseDouble(withdrawlimitqty) > 0) {
                tv24HLimit.setVisibility(View.VISIBLE);
                tv24HLimit.setText(String.format(getString(R.string.withdraw_24h_limit_tips), withdrawlimitqty));
            }
            shortName = assetBean.getShortName();
            logo = assetBean.getLogo();
            title_tv.setText(String.format(getString(R.string.tx_1), shortName));
            AppUtils.showDialogForGRINBiz(this, shortName, getString(R.string.str_msg_coin_grin_withdraw));
            coinId = assetBean.getId();
            total = assetBean.getTotal();
            memoName = assetBean.getMemoName();
            iseos = assetBean.isIseos();
            withdrawDigit = assetBean.getWithdrawDigit();
            iseos = !TextUtils.equals(memoName, "0");
            if (TextUtils.isEmpty(memoName)) {
                iseos = false;
            }
            feeList = assetBean.getWithdrawFees();
            minimumService = assetBean.getMinimumService();
            withdrawQtyArr = assetBean.getWithdrawQtyArr();
        }
        if (TextUtils.equals(shortName, "USDT")) {
            llOtcTips.setVisibility(View.VISIBLE);
        }
        //判断是否为BCH分叉业务
        isBCH = DataUtils.isBCHABC(shortName + "");
        if (isBCH) {
            tvAllamount.setVisibility(View.GONE);
        }
        //
        etPrice.setHint(getString(R.string.aa45) + getMinWithdraw());
        tvRiskTips.setText(AppConstants.COMMON.POINT + getString(R.string.safe));
        aaaatv.setText(AppConstants.COMMON.POINT + getString(R.string.aa45) + getMinWithdraw());
        aaaatv1.setText(AppConstants.COMMON.POINT + getString(R.string.maxtb) + getMaxWithdraw());
        coinName_tv.setText(shortName);
        coinName_tv1.setText(shortName);
        coinName_tv2.setText(shortName);
        dtvFee.setOnClickListener(v -> showFeeDialog());
        if (iseos) {
            ll_memo.setVisibility(View.VISIBLE);
            tv6.setVisibility(View.VISIBLE);
            tv6.setText(memoName + getString(R.string.aa48));
        } else {
            ll_memo.setVisibility(View.GONE);
            tv6.setVisibility(View.GONE);
        }
        RequestOptions ro = new RequestOptions();
        ro.error(R.mipmap.rmblogo);
        ro.fitCenter();
        Glide.with(this).load(logo).apply(ro).into(ivIcon);
        tvCoinWithdraw.setText(String.format(getString(R.string.coin_withdraw), shortName));
        tvAvailable.setText(total + shortName);
        tvCoinFee.setText(shortName);
        tvRealAmountUnit.setText(shortName);
        getFeeAndAddress();
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
                String amount;
                if (!editable.toString().isEmpty()) {
                    if (tvPriceTips.getVisibility() == View.GONE) {
                        tvPriceTips.setVisibility(View.VISIBLE);
                    }
                    if (TextUtils.equals(".", editable.toString())) {
                        return;
                    }
                    double countFee = DoubleUtils.parseDouble(editable.toString()) * getCurrentFee() + getFixedFee();
                    String realFee;
                    //为解决费率为0时，取固定费率的问题（以前代码逻辑缺失??醉了）
                    //为保险起见，保手写法，暂不动以前代码。
                    if (getCurrentFee() <= 0) {
                        realFee = NorUtils.NumberFormat(withdrawDigit).format(getFixedFee());
                    } else if (countFee > minimumService) {
                        realFee = NorUtils.NumberFormat(withdrawDigit).format(countFee);
                    } else {
                        realFee = NorUtils.NumberFormat(withdrawDigit).format(minimumService);
                    }

                    //先判断是否是默认提币地址
                    if (mIsDefaultAddress) {
                        //判断默认提币地址是否是内部地址
                        if (mIsInternalAddressDefault) {
                            tvFee.setText("0.0000");
                            realFee = "0";
                        } else {
                            tvFee.setText(realFee);
                        }
                    }else {
                        //判断成功切换后的提币地址是否是内部地址
                        if (mIsInternalAddress) {
                            tvFee.setText("0.0000");
                            realFee = "0";
                        } else {
                            tvFee.setText(realFee);
                        }
                    }

                    if (DoubleUtils.parseDouble(etPrice.getText().toString()) < DoubleUtils.parseDouble(realFee)) {
                        amount = "0";
                    } else {
                        amount = NorUtils.NumberFormat(withdrawDigit).format(DoubleUtils.parseDouble(
                                etPrice.getText().toString()) - DoubleUtils.parseDouble(realFee)) + "";
                    }

                    double realAmount = DoubleUtils.parseDouble(amount);//实际到账
                    double availableAmount = DoubleUtils.parseDouble(total);//可用数量
                    double fee = DoubleUtils.parseDouble(tvFee.getText().toString());//手续费
                    if (MathHelper.sub(availableAmount, fee) < realAmount) {
                        tvPriceTips.setText(getString(R.string.withdraw_no_balance));
                    } else if (DoubleUtils.parseDouble(editable.toString()) > DoubleUtils.parseDouble(getMaxWithdraw())) {
                        tvPriceTips.setText(getString(R.string.withdraw_exceed_max));
                    } else if (DoubleUtils.parseDouble(editable.toString()) < DoubleUtils.parseDouble(getMinWithdraw())) {
                        tvPriceTips.setText(getString(R.string.withdraw_under_min));
                    } else {
                        tvPriceTips.setVisibility(View.GONE);
                    }
                } else {
                    tvPriceTips.setVisibility(View.GONE);
                    //先判断是否是默认提币地址
                    if (mIsDefaultAddress) {
                        //判断默认提币地址是否是内部地址
                        if (mIsInternalAddressDefault) {
                            tvFee.setText("0.0000");
                            Utils.Log.d("mage默认", "00000000");
                        } else {
                            tvFee.setText(NorUtils.NumberFormat(withdrawDigit).format(getFixedFee()));
                            Utils.Log.d("mage默认", getFixedFee() + "");
                        }
                    } else {
                        //判断成功切换后的提币地址是否是内部地址
                        if (mIsInternalAddress) {
                            tvFee.setText("0.0000");
                            Utils.Log.d("mage", "00000000");
                        } else {
                            tvFee.setText(NorUtils.NumberFormat(withdrawDigit).format(getFixedFee()));
                            Utils.Log.d("mage", getFixedFee() + "");
                        }
                    }
                    amount = "0";
                    tvFee.setText("0");
                }
                tvRealAmountValue.setText(amount);
            }

        });
        eventClick(tvAddress).subscribe(o -> {
            toAddAddress();
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
            if ((DoubleUtils.parseDouble(total) < DoubleUtils.parseDouble(getMaxWithdraw()))) {
                etPrice.setText(total);
            } else {
                etPrice.setText(getMaxWithdraw());
            }
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
        // 粘贴板有数据，并且是文本
        ClipboardManager mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
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
        ImageView rightIV = findViewById(R.id.address_ivx);
        rightIV.setOnClickListener(v -> toAddAddress());
        EventBus.getDefault().register(this);
        getWithDrawLimit();
    }

    private void initChainView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_chain) {
            @Override
            protected void convert(@NotNull BaseViewHolder helper, String chainName) {
                TextView tvChain = helper.itemView.findViewById(R.id.tvChain);
                tvChain.setText(chainName);
                if (currentChainPosition == helper.getLayoutPosition()) {
                    tvChain.setSelected(true);
                } else {
                    tvChain.setSelected(false);
                }
            }
        };
        adapter.setOnItemClickListener((adapter, view, position) -> {
            changeChain(position);
        });
        rvChain.setLayoutManager(layoutManager);
        rvChain.setAdapter(adapter);
    }

    private void showFeeDialog() {
        DialogUtils.getInstance().showTwoButtonDialog(this, getString(R.string.fee_tips), getString(R.string.chain_fee_change), "", getString(R.string.iknow1), new DialogUtils.onBnClickListener() {
            @Override
            public void onLiftClick(AlertDialog dialog, View view) {
            }

            @Override
            public void onRightClick(AlertDialog dialog, View view) {
                AppHelper.dismissDialog(dialog);
            }
        });
    }

    private void changeChain(int position) {
        if (chainList == null || chainList.size() == 0) return;
        currentChainPosition = position;
        initAddress(chainList.get(position));
        desTxt.setText(String.format(getString(R.string.recharge_des1), chainList.get(position), getMinWithdraw(), shortName, getMaxWithdraw(), shortName));
        etPrice.setHint(getString(R.string.aa45) + getMinWithdraw());
        etPrice.setText("");

        adapter.notifyDataSetChanged();
    }

    private void getWithDrawLimit() {
        DialogManager.INSTANCE.showProgressDialog(this);
        Type type = new TypeToken<SingleResult<WithdrawLimit>>() {
        }.getType();
        Map<String, Object> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        OKHttpHelper.getInstance().get(UrlConstants.GET_WITHDRAW_LIMIT, params, getWithDrawLimitCallback, type);
    }

    private double getCurrentFee() {
        String chainName = chainList.get(currentChainPosition);
        double fee = 0.0;
        for (int i = 0; i < feeList.size(); i++) {
            if (TextUtils.equals(feeList.get(i).getChain(), chainName)) {
                fee = feeList.get(i).getFee();
                break;
            }
        }
        return fee;
    }

    private double getFixedFee() {
        String chainName = chainList.get(currentChainPosition);
        double fixedFee = 0.0;
        for (int i = 0; i < feeList.size(); i++) {
            if (TextUtils.equals(feeList.get(i).getChain(), chainName)) {
                fixedFee = feeList.get(i).getFixedFee();
                break;
            }
        }
        return fixedFee;
    }

    private void toAddAddress() {
        //TODO 待处理
        Bundle bundle = new Bundle();
        bundle.putString("shortName", shortName);
        bundle.putInt("id", coinId);
        bundle.putString("fromClass", "CoinTiXianActivity");
//            startActivity(WalletListActivity.class, bundle);
        Intent intent = new Intent(CoinTXNewActivity.this, NewWalletListActivity.class);
        intent.putExtra("shortName", shortName);
        intent.putExtra("id", coinId);
        intent.putExtra("fromClass", "CoinTiXianActivity");
        if (chainList != null) {
            intent.putExtra("chainName", chainList.get(currentChainPosition));
        }
        startActivityForResult(intent, 0);
    }

    @OnClick({R.id.ivQuestion})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivQuestion:
                if (FastClickUtils.isFastClick(1000)) {
                    return;
                }
                if (withdrawLimit == null) {
                    getWithDrawLimit();
                    return;
                }
                DialogUtils.getInstance().showTwoButtonDialog(CoinTXNewActivity.this, String.format(getString(R.string.withdraw_24h_limit), withdrawLimit.getTotal(), withdrawLimit.getCoinName(), new BigDecimal(withdrawLimit.getMaxQuota()).toPlainString(), withdrawLimit.getCoinName()), getString(R.string.cancle), getString(R.string.authenticate), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!UserInfoManager.isLogin()) {
                            startActivity(LoginActivity.class);
                            return;
                        }
                        startActivity(new Intent(CoinTXNewActivity.this, AuthActivity.class));
                    }
                });
                break;
        }
    }

    private String getAddressStr(WithdrawChainBean chainBean) {
        return chainBean.getRemark() + "-" + chainBean.getAddress();
    }

    private void initAddress(String chainName) {
        tvAddress.setText("");
        addressId = "";
        for (int i = 0; i < addressList.size(); i++) {
            if (TextUtils.equals(addressList.get(i).getChainName(), chainName)) {
                tvAddress.setText(getAddressStr(addressList.get(i)));
                addressId = addressList.get(i).getId() + "";
                //判断默认地址是否是内部地址的标识
                mIsInternalAddressDefault = addressList.get(i).getIsInternalAddress();
                //切换链名会把提币地址赋值为默认地址，所以赋值为true
                mIsDefaultAddress = true;
                break;
            }
        }
    }

    private String getMinWithdraw() {
        if (withdrawQtyArr == null || withdrawQtyArr.size() <= currentChainPosition) {
            return "--";
        }
        return withdrawQtyArr.get(currentChainPosition).getMinWithdraw();
    }

    private String getMaxWithdraw() {
        if (withdrawQtyArr == null || withdrawQtyArr.size() <= currentChainPosition) {
            return "--";
        }
        return withdrawQtyArr.get(currentChainPosition).getMaxWithdraw();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void sumbit() {
        if (etPrice.getText().toString().isEmpty()) {
            showMessage(getString(R.string.aa49), 2);
            return;
        }
        if (!TextUtils.isEmpty(getMinWithdraw()) && DoubleUtils.parseDouble(etPrice.getText().toString()) < DoubleUtils.parseDouble(getMinWithdraw())) {
            showMessage(getString(R.string.aa50) + getMinWithdraw(), 2);
            return;
        }
        if (tvAddress.getText().toString().isEmpty()) {
            showMessage(getString(R.string.aa51), 2);
            return;
        }
        if (etTrade.getText().toString().isEmpty()) {
            showMessage(getString(R.string.aa52), 2);
            return;
        }
        CommonSmsGoogleVerifyBottomDialogFragment.Companion.newInstance(CommonCodeTypeEnum.TYPE_WITHDRAW.getType(), new CommonSmsGoogleVerifyBottomDialogFragment.CodeListener() {
            @Override
            public void getCode(@NotNull String code, boolean isSms) {
                submit(code, isSms);
            }
        }).show(getSupportFragmentManager(), "dialog");
    }

    private void submit(String code, boolean isSms) {
        HashMap<String, String> params = new HashMap<>();
        String loginToken = UserInfoManager.getToken();
        RSACipher rsaCipher = new RSACipher();
        String body = null;
        String smsCode = isSms ? code : "";
        String googleCode = isSms ? "" : code;
        if (UserInfoManager.getUserInfo().isGoogleBind()) {
            if (iseos) {
                if (et_memo.getText().toString().isEmpty()) {
                    body = "symbol=" + URLEncoder.encode(String.valueOf(coinId)) + "&virtualaddres=" + URLEncoder.encode(addressId) + "&level=" + URLEncoder.encode(level) + "&withdrawAmount=" + URLEncoder.encode(etPrice.getText().toString()) + "&tradePwd=" + URLEncoder.encode(etTrade.getText().toString()) + "&phoneCode=" + URLEncoder.encode(smsCode) + "&totpCode=" + URLEncoder.encode(googleCode);
                } else {
                    body = "symbol=" + URLEncoder.encode(String.valueOf(coinId)) + "&virtualaddres=" + URLEncoder.encode(addressId) + "&level=" + URLEncoder.encode(level) + "&withdrawAmount=" + URLEncoder.encode(etPrice.getText().toString()) + "&tradePwd=" + URLEncoder.encode(etTrade.getText().toString()) + "&phoneCode=" + URLEncoder.encode(smsCode) + "&totpCode=" + URLEncoder.encode(googleCode);
                    //      + "&memo=" + URLEncoder.encode(et_memo.getText().toString());
                    params.put("memo", et_memo.getText().toString());
                }
            } else {
                body = "symbol=" + URLEncoder.encode(String.valueOf(coinId)) + "&virtualaddres=" + URLEncoder.encode(addressId) + "&level=" + URLEncoder.encode(level) + "&withdrawAmount=" + URLEncoder.encode(etPrice.getText().toString()) + "&tradePwd=" + URLEncoder.encode(etTrade.getText().toString()) + "&phoneCode=" + URLEncoder.encode(smsCode) + "&totpCode=" + URLEncoder.encode(googleCode);
            }
        } else {
            if (iseos) {
                if (et_memo.getText().toString().isEmpty()) {
                    body = "symbol=" + URLEncoder.encode(String.valueOf(coinId)) + "&virtualaddres=" + URLEncoder.encode(addressId) + "&level=" + URLEncoder.encode(level) + "&withdrawAmount=" + URLEncoder.encode(etPrice.getText().toString()) + "&tradePwd=" + URLEncoder.encode(etTrade.getText().toString()) + "&phoneCode=" + URLEncoder.encode(smsCode);
                } else {
                    body = "symbol=" + URLEncoder.encode(String.valueOf(coinId)) + "&virtualaddres=" + URLEncoder.encode(addressId) + "&level=" + URLEncoder.encode(level) + "&withdrawAmount=" + URLEncoder.encode(etPrice.getText().toString()) + "&tradePwd=" + URLEncoder.encode(etTrade.getText().toString()) + "&phoneCode=" + URLEncoder.encode(smsCode);// + "&memo=" + URLEncoder.encode(et_memo.getText().toString());
                    params.put("memo", et_memo.getText().toString());
                }
            } else {
                body = "symbol=" + URLEncoder.encode(String.valueOf(coinId)) + "&virtualaddres=" + URLEncoder.encode(addressId) + "&level=" + URLEncoder.encode(level) + "&withdrawAmount=" + URLEncoder.encode(etPrice.getText().toString()) + "&tradePwd=" + URLEncoder.encode(etTrade.getText().toString()) + "&phoneCode=" + URLEncoder.encode(smsCode);
            }
        }
        try {
            String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            params.put("body", body1);
            params.put("loginToken", loginToken);
            withdrawBtcSubmit(params);
            Log.d("eos提币", body + ">>>>>>>" + body1 + ">>>>>>>>>" + loginToken);
        } catch (Exception e) {
            e.printStackTrace();
            MToast.show(CoinTXNewActivity.this, getString(R.string.aa55), 2);
        }
    }

    void withdrawBtcSubmit(Map<String, String> params) {
        DialogManager2.INSTANCE.showProgressDialog(this);
        netTags.add(UrlConstants.DOMAIN + UrlConstants.SUMBIT_TIXIAN);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.SUMBIT_TIXIAN, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                DialogManager2.INSTANCE.dismiss();
                SnackBarUtils.ShowRed(CoinTXNewActivity.this, getString(R.string.aa56));
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager2.INSTANCE.dismiss();
                Log.d("虚拟币提现", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("value");
                    if (code == 0) {
                        MToast.show(CoinTXNewActivity.this, msg, 1);
                        finish();
                    } else if (code == -10) {//提现额度超过
                        if (withdrawLimit == null) {
                            return;
                        }
                        if (isC3()) {
                            DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                                @Override
                                public void onLiftClick(AlertDialog dialog, View view) {
                                }

                                @Override
                                public void onRightClick(AlertDialog dialog, View view) {
                                    AppHelper.dismissDialog(dialog);
                                }
                            });
                            DialogUtils.getInstance().showOneButtonDialog1(CoinTXNewActivity.this, msg, getString(R.string.iknow));
                        } else {
                            DialogUtils.getInstance().showTwoButtonDialog(CoinTXNewActivity.this, msg, getString(R.string.cancle), getString(R.string.authenticate), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!UserInfoManager.isLogin()) {
                                        startActivity(LoginActivity.class);
                                        return;
                                    }
                                    startActivity(new Intent(CoinTXNewActivity.this, AuthActivity.class));
                                }
                            });
                        }
                    } else if (code == -10001) {
                        MToast.show(CoinTXNewActivity.this, getString(R.string.aa57), 2);
                    } else {
                        showMessage(msg, 2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isC3() {
        if (withdrawLimit == null) {
            return false;
        }
        if (TextUtils.equals("C3", withdrawLimit.getKycLevel())) {
            return true;
        }
        return false;
    }

    //获取手续费
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getFeeAndAddress() {
        HashMap<String, String> params = new HashMap<>();
        String loginToken = SPUtils.getLoginToken();
        RSACipher rsaCipher = new RSACipher();
        String body = "coinId=" + URLEncoder.encode(String.valueOf(coinId));
        try {
            String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            params.put("body", body1);
            params.put("loginToken", loginToken);
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
        OkhttpManager.postAsync(UrlConstants.GET_WITHDRAW_ADDRESS, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                SnackBarUtils.ShowRed(CoinTXNewActivity.this, errorMsg);
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    withdrawBean = GsonUtil.json2Obj(result, WithdrawBean.class);
                    initChainData();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }

    private void initChainData() {
        if (withdrawBean == null || withdrawBean.getList() == null) return;
        chainList = withdrawBean.getChains();
        addressList = withdrawBean.getList();
        if (chainList.size() > 1) {
            desTxt.setVisibility(View.VISIBLE);
            descLayout.setVisibility(View.GONE);
        } else {//一个链则直接取地址内第一个
            desTxt.setVisibility(View.GONE);
            descLayout.setVisibility(View.VISIBLE);
        }
        if (withdrawBean.getFee_list().size() > 0) {
            level = String.valueOf(withdrawBean.getFee_list().get(0).getLevel());
        }

        adapter.setList(chainList);
        changeChain(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void selectCoinAddress(Event.clickCoinAddress clickCoinAddress) {
        tvAddress.setText(clickCoinAddress.selectAddress);
        addressId = String.valueOf(clickCoinAddress.id);
        //提币地址页面（NewWalletListActivity）传递过来的标识，标注clickCoinAddress.selectAddress地址是否是内部地址,true不收取手续费,false收取
        mIsInternalAddress = clickCoinAddress.isInternalAddress;
        //只要切换提币地址并成功返回，赋值为false,如果不回调这里说明点击了返回键，而点击了返回键页面不发生变化
        mIsDefaultAddress = false;
        //当输入提币数量后，再切换提币地址时,可能由内部地址切换为外部收费地址,手续费需要变化,故etPrice置空，重新输入计算手续费
        etPrice.setText("");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)//删除地址后刷新
    public void deleteAddress(Event.deleteAddress deleteAddress) {
        if (TextUtils.equals(addressId, deleteAddress.addressId)) {//删除的地址为当前显示的则清空
            tvAddress.setText("");
            addressId = "";
            for (int i = 0; i < addressList.size(); i++) {
                if (TextUtils.equals(deleteAddress.addressId, addressList.get(i).getId() + "")) {
                    addressList.remove(i);
                    break;
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
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
     *
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

    protected INetCallback<SingleResult<WithdrawLimit>> getWithDrawLimitCallback = new INetCallback<SingleResult<WithdrawLimit>>() {
        @Override
        public void onSuccess(SingleResult<WithdrawLimit> result) throws Throwable {
            DialogManager.INSTANCE.dismiss();
            if (result == null) {
                //TODO 处理异常情况
                return;
            }
            if (result.code.equals("200")) {
                if (result.data == null) {//没有数据则为0
                    return;
                }
                withdrawLimit = result.data;
                if (isC3()) {
                    ivQuestion.setVisibility(View.GONE);
                } else {
                    ivQuestion.setVisibility(View.VISIBLE);
                }
                tvLimit.setText(withdrawLimit.getQuota() + "/" + withdrawLimit.getTotal() + withdrawLimit.getCoinName());
            }
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
            DialogManager.INSTANCE.dismiss();
            //TODO 处理异常情况
        }
    };
}
