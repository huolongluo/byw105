package com.android.coinw.biz.trade;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

import com.android.coinw.api.kx.model.XDepthData;
import com.android.coinw.biz.event.BizEvent;
import com.android.coinw.biz.trade.helper.ETFHepler;
import com.android.coinw.biz.trade.helper.TradeDataHelper;
import com.android.coinw.biz.trade.helper.TradeHelper;
import com.android.coinw.biz.trade.model.Coin;
import com.android.coinw.biz.trade.model.OrderSide;
import com.android.coinw.model.result.AssetResult;
import com.android.legend.common.GlobalFuncKt;
import com.android.legend.model.BaseResponse;
import com.android.legend.model.CommonResult;
import com.android.legend.model.enumerate.order.OrderType;
import com.android.legend.model.enumerate.transfer.TransferAccount;
import com.android.legend.model.order.OrderResultBean;
import com.android.legend.ui.transfer.AccountTransferActivity;
import com.blankj.utilcodes.utils.ToastUtils;
import com.chad.library.BR;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.manager.DialogManager2;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;

import com.android.legend.ui.login.LoginActivity;
import com.legend.modular_contract_sdk.common.DialogUtilKt;
import com.legend.modular_contract_sdk.ui.contract.settings.PreferencesSettingActivity;
import com.legend.modular_contract_sdk.utils.StringUtilKt;
import com.legend.modular_contract_sdk.widget.CustomListPopupView;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.AttachPopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;

import huolongluo.byw.byw.ui.activity.safe_centre.SafeCentreActivity;
import huolongluo.byw.byw.ui.dialog.AddDialog;
import huolongluo.byw.databinding.TradeLeftViewBinding;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.reform.trade.bean.TradeOrder;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.CurrencyPairUtil;
import huolongluo.byw.util.FastClickUtils;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.MathHelper;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.StringUtil;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.utils.DoubleUtils;
import huolongluo.bywx.utils.EncryptUtils;
import huolongluo.bywx.utils.ValueUtils;
import okhttp3.Request;

public class TradeLeftView extends BaseView implements IChangeCoinListener {
    @BindView(R.id.tv_trade_type)
    View typeView;
    @BindView(R.id.qiv_price)
    QuoteInputView priceInputView;//价格
    @BindView(R.id.qiv_num)
    QuoteInputView numInputView;//数量
    @BindView(R.id.qiv_amount)
    QuoteInputView amountInputView;//金额
    @BindView(R.id.tv_total_available)
    TextView avlTotalText;//可用
    @BindView(R.id.tv_tip_total_available)
    TextView avlTotalTipTxt;//预计可用
    @BindView(R.id.tv_total_available_unit)
    TextView avlTotalUnitTxt;//可用-单位
    @BindView(R.id.tv_tip_buy_available)
    TextView buyTotalTipTxt;//预计可买
    @BindView(R.id.tv_buy_available)
    TextView avlBuyTxt;//可买
    @BindView(R.id.tv_buy_available_unit)
    TextView avlBuyUnitTxt;//可买-单位
    @BindView(R.id.tv_buy)
    TextView confirmTxt;//购买/出售
    @BindView(R.id.iv_sumbit_loadding)
    ImageView sumbitLoaddingIV;//加载
    @BindView(R.id.iv_transfer)
    ImageView ivTransfer;//划转
    //ETF需求内容-开始
    //该业务数据的加载性能和体验需要考虑
    @BindView(R.id.ll_net_worth)
    LinearLayout netWorthLayout;
    @BindView(R.id.tv_net_worth)
    TextView netWorthTxt;
    @BindView(R.id.seek_bar)
    SeekBarRelativeLayout seekBarRelativeLayout;
    //
    protected int fcountPrice = 5;
    protected int fcountNumber = 3;
    private boolean isSell = false;//默认为买入
    protected String mRmbTotal;//可用
    //临时参数
    protected String mCoinTotal;
    protected boolean needWrite = true;//是否可以写入-通过服务器数据进行设置
    protected AssetResult assetResult;
    protected boolean isContinue = false;
    //
    protected String mFirstSell;
    protected String mFirstBuy;
    protected boolean isUpdateData = false;
    private FragmentManager fragmentManager;
    private boolean isETF = false;
    private String side;//仅用于异常的时候判断方向，正常还是需要判断接口返回的

    private TradeLeftViewBinding mBinding;
    private ObservableBoolean mIsBuy;

    public static final int TRADE_TYPE_LIMIT = 1;
    public static final int TRADE_TYPE_MARKET = 2;
    private ObservableInt mTradeType;
    int mAmountIndex = -1;
    int mNumIndex = -1;

    public void setETF(boolean isETF) {
        this.isETF = isETF;
        netWorthLayout.setVisibility(isETF ? View.VISIBLE : View.GONE);
        refreshPrecious();
    }

    public TradeLeftView(Context context, Fragment fragment) {
        super(context, fragment);
    }

    @Override
    public View setContentView(int layoutResID) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.trade_left_view, null, false);
        mIsBuy = new ObservableBoolean(true);
        mBinding.setIsBuy(mIsBuy);
        mTradeType = new ObservableInt(TRADE_TYPE_LIMIT);
        mBinding.setTradeType(mTradeType);

        return mBinding.getRoot();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trade_left_view;
    }

    @Override
    protected void initView() {
        //添加事件
        addEvent();
        //设置Hint属性
//        priceInputView.setHint(R.string.price);
        numInputView.setHint(R.string.num);
        amountInputView.setHint(R.string.d15);
        initSeekBar();

        mBinding.ivMinus.setOnClickListener(v -> {
            int precious = CurrencyPairUtil.getPricePreciousById(TradeDataHelper.getInstance().getId(isETF));
            String price = priceInputView.getText();

            if (StringUtilKt.getDouble(price) == 0) {
                return;
            }

            if (StringUtilKt.getDouble(price) < 0) {
                priceInputView.setText("0");
                return;
            }

            String step = StringUtilKt.getMinStep(precious);
            Logger.getInstance().error("ivMinus" + "precious = " + precious + " step = " + step + " price=" + price);

            String result = new BigDecimal(price).subtract(new BigDecimal(step)).toString();

            priceInputView.setText(StringUtilKt.getNum(StringUtilKt.getNum(result)));
        });

        mBinding.ivPlus.setOnClickListener(v -> {
            int precious = CurrencyPairUtil.getPricePreciousById(TradeDataHelper.getInstance().getId(isETF));
            String price = priceInputView.getText();

            if (price.isEmpty()) {
                price = "0";
            }

            String step = StringUtilKt.getMinStep(precious);

            Logger.getInstance().error("ivPlus" + "precious = " + precious + " step = " + step + " price=" + price);

            String result = new BigDecimal(price).add(new BigDecimal(step)).toString();

            priceInputView.setText(StringUtilKt.getNum(result));
        });

        ivTransfer.setOnClickListener((view) -> {

                    if (!UserInfoManager.isLogin() || confirmTxt.getText().toString().startsWith(mContext.getResources().getString(R.string.qe36))) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        mContext.startActivity(intent);
                    } else {
                        AccountTransferActivity.Companion.launch(mContext, TransferAccount.WEALTH.getValue(), TransferAccount.SPOT.getValue(), TradeDataHelper.getInstance().getId(isETF), null,
                                false, TradeDataHelper.getInstance().getCoinName(isETF));
                    }

                }
        );

        mBinding.tvTradeType.setOnClickListener(v -> {

            String[] titles = new String[]{getString(R.string.mc_sdk_contract_limit_order), getString(R.string.mc_sdk_contract_market_order)};

            DialogUtilKt.showSelectItemDialog(mContext, titles, (index, s) -> {
                if (index == 0) {
                    mTradeType.set(TRADE_TYPE_LIMIT);
                } else if (index == 1) {
                    mTradeType.set(TRADE_TYPE_MARKET);
                }
                applyInputViewVisible();
                refreshCanBuyOrSell();
                return null;
            });

        });
    }

    public void updateNetValue(String value) {
        if (netWorthTxt == null) {
            return;
        }
        netWorthTxt.setText(TextUtils.isEmpty(value) ? "" : value);
    }

    private void applyInputViewVisible() {
        if (mTradeType.get() == TRADE_TYPE_MARKET) {
            if (mIsBuy.get()) {
                amountInputView.setVisibility(View.VISIBLE);
                numInputView.setVisibility(View.INVISIBLE);
                swapNumAmount(true);
            } else {
                amountInputView.setVisibility(View.INVISIBLE);
                numInputView.setVisibility(View.VISIBLE);
                swapNumAmount(false);
            }
        } else {
            amountInputView.setVisibility(View.VISIBLE);
            numInputView.setVisibility(View.VISIBLE);
            swapNumAmount(false);
        }
        seekBarRelativeLayout.setProgress(0);
    }

    private void swapNumAmount(boolean isSwap) {

        if (mNumIndex == -1 || mAmountIndex == -1){
            mAmountIndex = mBinding.llRoot.indexOfChild(amountInputView);
            mNumIndex  = mBinding.llRoot.indexOfChild(numInputView);
        }

        mBinding.llRoot.removeView(amountInputView);
        mBinding.llRoot.removeView(numInputView);

        if (isSwap){
            mBinding.llRoot.addView(amountInputView, mNumIndex);
            mBinding.llRoot.addView(numInputView, mAmountIndex);
        } else {
            mBinding.llRoot.addView(numInputView, mNumIndex);
            mBinding.llRoot.addView(amountInputView, mAmountIndex);
        }

    }

    private void initSeekBar() {
        seekBarRelativeLayout.initSeekBar();
        seekBarRelativeLayout.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isUpdateData) {
                    return;
                }
                if (!isSell) {
                    if (!TextUtils.isEmpty(mRmbTotal) && !TextUtils.isEmpty(mFirstSell) && progress != 0) {
                        try {
                            if (!priceInputView.getText().isEmpty()) {
                                mFirstSell = priceInputView.getText();
                            }
                            if (progress == 100) {
                                numInputView.setText(avlBuyTxt.getText().toString());
                            } else {
                                //使用可用乘以百分比再算手续费下的购买量误差比使用预计可买乘以百分比小
                                numInputView.setText(NorUtils.NumberFormat(fcountNumber, RoundingMode.DOWN).format(
                                        MathHelper.div
                                                (
                                                        MathHelper.div
                                                                (
                                                                        MathHelper.mul
                                                                                (
                                                                                        DoubleUtils.parseDouble(avlTotalText.getText().toString()), MathHelper.div(progress + "", "100")
                                                                                ),
                                                                        1 + CurrencyPairUtil.getFeeRateById(TradeDataHelper.getInstance().getId(isETF))
                                                                ), DoubleUtils.parseDouble(priceInputView.getText())
                                                )
                                ));
                            }
                            if (mTradeType.get() == TRADE_TYPE_MARKET) {

                                amountInputView.setText(
                                        NorUtils.NumberFormat(fcountPrice).format(
                                                MathHelper.mul(mRmbTotal,
                                                        String.valueOf(MathHelper.div(progress, 100))
                                                )
                                        )
                                );
                            } else {
                                amountInputView.setText(NorUtils.NumberFormat(fcountPrice).format(
                                        MathHelper.mul(numInputView.getText(), priceInputView.getText())));
                            }

                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    } else if (progress == 0) {
                        amountInputView.setText("0");
                        numInputView.setText("");
                    } else if (TextUtils.isEmpty(mRmbTotal)) {
                        if (UserInfoManager.isLogin() && !isContinue) {
                            isContinue = true;
                            MToast.show(mContext, mContext.getResources().getString(R.string.qe74), 1);
                        }
                    } else if (TextUtils.isEmpty(mFirstBuy) && !isContinue) {
                        isContinue = true;
                        MToast.show(mContext, mContext.getResources().getString(R.string.qe73), 1);
                    }
                } else {
                    if (!TextUtils.isEmpty(mCoinTotal) && !TextUtils.isEmpty(mFirstBuy) && progress != 0) {
                        try {
                            if (!priceInputView.getText().toString().isEmpty()) {
                                mFirstBuy = priceInputView.getText().toString();
                            }
                            priceInputView.setText(mFirstBuy);
                            Double number = (DoubleUtils.parseDouble(mCoinTotal) * progress) / 100;
                            Double tradeSum = number * DoubleUtils.parseDouble(mFirstBuy);
                            updateNum(ValueUtils.getValue(number.doubleValue() + "", fcountNumber));
                            updateAmount(NorUtils.NumberFormat(fcountPrice).format(tradeSum.doubleValue()) + "");
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    } else if (progress == 0) {
                        amountInputView.setText("0");
                        numInputView.setText("");
                    } else if (TextUtils.isEmpty(mRmbTotal)) {
                        if (UserInfoManager.isLogin() && !isContinue) {
                            isContinue = true;
                            MToast.show(mContext, mContext.getResources().getString(R.string.qe75), 0);
                        }
                    } else if (TextUtils.isEmpty(mFirstSell) && !isContinue) {
                        isContinue = true;
                        MToast.show(mContext, mContext.getResources().getString(R.string.qe76), 0);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBarRelativeLayout.setProgress(0);
        checkStatus();
    }

    private void checkStatus() {
        //判断是否已经是登录状态
        if (UserInfoManager.isLogin()) {
            //启用状态
            seekBarRelativeLayout.setClickable(true);
            seekBarRelativeLayout.setEnabled(true);
            seekBarRelativeLayout.setFocusable(true);
        } else {
            //禁用状态
            seekBarRelativeLayout.setClickable(false);
            seekBarRelativeLayout.setEnabled(false);
            seekBarRelativeLayout.setFocusable(false);
        }
    }

    private void addEvent() {
        priceInputView.addTextChangedListener(new QuoteTextWatcher(priceInputView) {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                super.beforeTextChanged(s, start, count, after);
                Logger.getInstance().debug(TAG, "priceInputView: " + s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                Logger.getInstance().debug(TAG, "priceInputView: " + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Logger.getInstance().debug(TAG, "priceInputView: " + s.toString());
                //价格
                String price = s.toString().trim();
                //折合法币
                mBinding.tvConvertCny.setText("≈ " + PricingMethodUtil.getResultByExchangeRate(price, TradeDataHelper.getInstance().getCnyName(isETF), 2) + " " + PricingMethodUtil.getPricingSelectType());
                if (isUpdateData) {//数据更新状态，不连动
                    return;
                }
                Logger.getInstance().error("TextChange fcountPrice : " + fcountPrice + "  this:" + TradeLeftView.this.hashCode() + "  " + CurrencyPairUtil.getPricePreciousById(TradeDataHelper.getInstance().getId(isETF)));
                //用户改变交易价格，根据交易数量，计算出新的交易金额




//        val result = PricingMethodUtil.getResultByExchangeRate(ticker.price, ticker.rightCoinName)
//        "≈ ${PricingMethodUtil.getPricingUnit()} $result"

                //控制金额
                if (TextUtils.isEmpty(price) && mTradeType.get() == TRADE_TYPE_LIMIT) {
                    updateAmount("");
                    setButtonEnabled(false);
                    refreshCanBuyOrSell();
                    return;
                }
                //输入0.的控制
                if (price.contains(".")) {
                    if (price.startsWith(".")) {
                        updatePrice("0.");
                        priceInputView.setSelection(priceInputView.getText().toString().length());
                        setButtonEnabled(false);
                        return;
                    }
                    int dot = price.indexOf(".");
                    if (price.length() - dot > fcountPrice + 1) {
                        updatePrice(NorUtils.NumberFormatNo(fcountPrice).format(DoubleUtils.parseDouble(price)));
                        priceInputView.setSelection(priceInputView.getText().toString().length());
                        return;
                    }
                }
                String num = numInputView.getText().toString().trim();
                if (TextUtils.isEmpty(num)) {
                    setButtonEnabled(false);
                    refreshCanBuyOrSell();
                    return;
                }
                //当前视图是否是焦点视图，或者子视图里面有焦点视图。
                if (priceInputView.hasFocus()) {
                    computeAmount(price, num);
                }
                setButtonEnabled(true);
                refreshCanBuyOrSell();
            }
        });
        numInputView.addTextChangedListener(new QuoteTextWatcher(numInputView) {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Logger.getInstance().debug(TAG, "numInputView: " + s.toString());
                //注意更新价格和数量的先后顺序
                if (isUpdateData) {//数据更新状态，不连动
                    return;
                }
                //用户改变交易数量，根据交易价格，计算出新的交易金额
                String num = s.toString().trim();
                updateSeekBar(num);
                if (TextUtils.isEmpty(num)) {
                    updateAmount("");
                    setButtonEnabled(false);
                    return;
                }
                if (num.contains(".")) {
                    if (num.startsWith(".")) {
                        updateNum("0.");
                        numInputView.setSelection(numInputView.getText().toString().length());
                        setButtonEnabled(false);
                        return;
                    }
                    int dot = num.indexOf(".");
//                    08-13 22:51:52.101 1110088 14581 14581 E AndroidRuntime: FATAL EXCEPTION: main
//                    08-13 22:51:52.101 1110088 14581 14581 E AndroidRuntime: Process: huolongluo.byw, PID: 14581
//                    08-13 22:51:52.101 1110088 14581 14581 E AndroidRuntime: java.lang.NumberFormatException: For input string: "z(0/m05p.1"
                    try {
                        if (num.length() - dot > fcountNumber + 1) {
                            updateNum(NorUtils.NumberFormatNo(fcountNumber).format(DoubleUtils.parseDouble(num)));
                            numInputView.setSelection(numInputView.getText().length());
                            return;
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
                if (TextUtils.isEmpty(num)) {
                    updateAmount("");
                    setButtonEnabled(false);
                    return;
                }
                String price = priceInputView.getText().trim();
                if (TextUtils.isEmpty(price) && mTradeType.get() == TRADE_TYPE_LIMIT) {//价格
                    updateAmount("");
                    setButtonEnabled(false);
                    return;
                }
                if (numInputView.hasFocus()) {
                    computeAmount(price, num);
                }
                if (!isSell && DoubleUtils.parseDouble(num) > DoubleUtils.parseDouble(avlBuyTxt.getText().toString())) {//购买数量大于预计可买时
                    ToastUtils.showShortToast(String.format(getString(R.string.buying_volume_too_hign), avlBuyTxt.getText().toString()));
                    setButtonEnabled(false);
                } else if (isSell && DoubleUtils.parseDouble(num) > DoubleUtils.parseDouble(avlTotalText.getText().toString())) {
                    ToastUtils.showShortToast(String.format(getString(R.string.selling_volume_too_hign), avlTotalText.getText().toString()));
                    setButtonEnabled(false);
                } else {
                    setButtonEnabled(true);
                }
            }
        });



        amountInputView.addTextChangedListener(new QuoteTextWatcher(amountInputView) {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Logger.getInstance().debug(TAG, "amountInputView: " + s.toString());
                String amount = s.toString().trim();
                String price = priceInputView.getText().trim();
                String num = numInputView.getText().trim();
                if (isUpdateData) {//数据更新状态，不连动
                    return;
                }
                if (num.contains(".")) {
                    if (num.startsWith(".")) {
                        updateAmount("0.");
                        amountInputView.setSelection(amountInputView.getText().toString().length());
                        setButtonEnabled(false);
                        return;
                    }
                }

                int dot = amount.indexOf(".");
                if (dot >= 0){
                    try {
                        if (amount.length() - dot > fcountNumber + fcountPrice + 1) {
                            updateAmount(NorUtils.NumberFormat(fcountNumber + fcountPrice, RoundingMode.DOWN).format(DoubleUtils.parseDouble(amount)));
                            amountInputView.setSelection(amountInputView.getText().length());
                            return;
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }

                if (amountInputView.hasFocus()) {
                    computeNum(price, amount);
                }
                setButtonEnabled(true);



            }
        });
        confirmTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtils.isFastClick(1000)) {
                    return;
                }
                //判断是否为ETF
                if (isETF) {
                    confirmETF();
                } else {
                    confirm();
                }
            }
        });
    }

    private void confirmETF() {
        if (!UserInfoManager.isLogin() || confirmTxt.getText().toString().startsWith(mContext.getResources().getString(R.string.qe36))) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(intent);
        } else {
            if (!ETFHepler.checkETFDisclaimer()) {
                ETFHepler.showAgreeTip(mContext, callback);
            } else {
                confirm();
            }
        }
    }

    INetCallback<SingleResult<SingleResult<String>>> callback = new INetCallback<SingleResult<SingleResult<String>>>() {
        @Override
        public void onSuccess(SingleResult<SingleResult<String>> result) {
            try {
                if (result.data.success) {
                    SPUtils.saveInt(BaseApp.getSelf(), AppConstants.COMMON.KEY_ACTION_ETF_DISCLAIMER, 1);
                    confirm();
                } else {
                    SnackBarUtils.ShowRed((Activity) mContext, getString(R.string.service_expec));

                }
            } catch (Throwable t) {
                Logger.getInstance().error(t);
            }
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
            try {
                SnackBarUtils.ShowRed((Activity) mContext, getString(R.string.service_expec));
            } catch (Throwable t) {
                Logger.getInstance().error(t);
            }
        }
    };

    /**
     * 根据价格和数量计算金额
     *
     * @param price
     * @param num
     */
    private void computeAmount(String price, String num) {
        if (TextUtils.isEmpty(price) || TextUtils.isEmpty(num)) {
            updateAmount("");
            return;
        }
        try {
            Double totalPrice = DoubleUtils.parseDouble(num) * DoubleUtils.parseDouble(price);
            if (!TextUtils.isEmpty(mRmbTotal)) {
                if (totalPrice > DoubleUtils.parseDouble(mRmbTotal)) {
                    //购买的时候才进行提示
                    amountInputView.setText(NorUtils.NumberFormatNo(fcountPrice+fcountNumber).format(totalPrice.doubleValue()) + "");
                } else {
                    amountInputView.setText(NorUtils.NumberFormatNo(fcountPrice+fcountNumber).format(totalPrice.doubleValue()) + "");
                }
            } else {
                amountInputView.setText(NorUtils.NumberFormatNo(fcountPrice+fcountNumber).format(totalPrice.doubleValue()) + "");
                amountInputView.setText(NorUtils.NumberFormatNo(fcountPrice+fcountNumber).format(totalPrice.doubleValue()) + "");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void updateAmount(String value) {
        isUpdateData = true;
        String text = amountInputView.getText().trim();
        if (!TextUtils.equals(value, text)) {
            amountInputView.setText(value);
        }
        isUpdateData = false;
    }

    private void updatePrice(String value) {
        isUpdateData = true;
        String text = priceInputView.getText().trim();
        if (!TextUtils.equals(value, text)) {
            priceInputView.setText(value);
        }
        isUpdateData = false;
    }

    private void updateNum(String value) {
        isUpdateData = true;
        String text = numInputView.getText().trim();
        if (!TextUtils.equals(value, text)) {
            numInputView.setText(value);
        }
        isUpdateData = false;
    }

    /**
     * 根据金额和价格计算出数量
     *
     * @param price
     * @param amount
     */
    private void computeNum(String price, String amount) {
        if (TextUtils.isEmpty(price) || TextUtils.isEmpty(amount)) {
            updateNum("0");
            return;
        }
        Double totalPrice = DoubleUtils.parseDouble(amount);
        if (totalPrice <= 0) {
            updateNum("0");
        }
        Double priceVal = DoubleUtils.parseDouble(price);
        if (priceVal <= 0) {
            updateNum("0");
            return;
        }
        Double numVal = totalPrice / priceVal;
        String numStr = NorUtils.NumberFormat(fcountNumber, RoundingMode.DOWN).format(numVal);
        updateNum(numStr);
        updateSeekBar(numStr);
    }

    private void setButtonEnabled(boolean enabled) {
        if (!UserInfoManager.isLogin()) {
            confirmTxt.setEnabled(true);
            return;
        }
        if (confirmTxt.isEnabled() == enabled) {
            //已经是当前状态
            return;
        }
        confirmTxt.setEnabled(enabled);
    }

    /**
     * 是否为出售
     *
     * @param isSell
     */
    public void changeTag(boolean isSell) {
        if (this.isSell == isSell) {
            //重复点击
            return;
        }
        if (numInputView == null) {
            return;
        }
        isUpdateData = true;
        this.isSell = isSell;
        mIsBuy.set(!isSell);

        //更新界面状态数据
        applyInputViewVisible();
        setValue();
        setConfirmValue(TradeDataHelper.getInstance().getCoinName());
        resetUI(assetResult);
        //更新价格
        updatePrice();
        seekBarRelativeLayout.updateSeekBar(!isSell);
        if (!isSell) {
            //卖1的价格
            amountInputView.setText("");
            buyTotalTipTxt.setText(R.string.qe66);
        } else {
            amountInputView.setText("");
            buyTotalTipTxt.setText(R.string.qe68);
        }
        seekBarRelativeLayout.setProgress(0);
        isUpdateData = false;
    }

    private void updateSeekBar(String num) {
        isUpdateData = true;
        String price = priceInputView.getText().trim();
        if (TextUtils.isEmpty(num) || TextUtils.isEmpty(price) || TextUtils.isEmpty(mRmbTotal)) {
            seekBarRelativeLayout.setProgress(0);
            isUpdateData = false;
            return;
        }
        try {
            if (isSell) {
                Double numDouble = DoubleUtils.parseDouble(num);
                Double total = DoubleUtils.parseDouble(mCoinTotal);
                if (total == 0d || numDouble == 0d) {
                    seekBarRelativeLayout.setProgress(0);
                } else if (numDouble >= total) {
                    seekBarRelativeLayout.setProgress(100);
                } else {
                    seekBarRelativeLayout.setProgress((int) (TradeDataHelper.getInstance().div(numDouble, total, 5) * 100));
                }
            } else {
                double numD = DoubleUtils.parseDouble(num);
                double canBuyD = DoubleUtils.parseDouble(avlBuyTxt.getText().toString());
                if (numD == 0d || canBuyD == 0d) {
                    seekBarRelativeLayout.setProgress(0);
                } else if (numD >= canBuyD) {
                    seekBarRelativeLayout.setProgress(100);
                } else {
                    seekBarRelativeLayout.setProgress((int) (TradeDataHelper.getInstance().div(numD, canBuyD, 5) * 100));
                }
            }
        } catch (Throwable t) {
            Logger.getInstance().debug(TAG, "updateSeekBar!");
        }
        isUpdateData = false;
    }

    private void updateSeekBarForSell(String num) {
        isUpdateData = true;
        String price = priceInputView.getText().trim();
        if (TextUtils.isEmpty(num) || TextUtils.isEmpty(price) || TextUtils.isEmpty(mCoinTotal)) {
            seekBarRelativeLayout.setProgress(0);
            return;
        }
        try {
            Double totalPrice = DoubleUtils.parseDouble(num);
            Double total = DoubleUtils.parseDouble(mCoinTotal);
            if (total == 0d || totalPrice == 0d) {
                seekBarRelativeLayout.setProgress(0);
            } else if (totalPrice >= total) {
                seekBarRelativeLayout.setProgress(100);
            } else {
                seekBarRelativeLayout.setProgress((int) (TradeDataHelper.getInstance().div(totalPrice, total, 5) * 100));
            }
        } catch (Throwable t) {
            Logger.getInstance().debug(TAG, "updateSeekBar!");
        }
        isUpdateData = false;
    }

    private void updatePrice() {
        if (priceInputView == null) {
            return;
        }
        try {
            if (!isSell) {
                if (TradeUtils.getInstance().getDepthList() != null && TradeUtils.getInstance().getDepthList().size() > 0) {
                    //卖1的价格
                    priceInputView.setText(TradeUtils.getInstance().getDepthList().get(0).sellPrice);
                }
            } else {
                //买1的价格
                if (TradeUtils.getInstance().getDepthList() != null && TradeUtils.getInstance().getDepthList().size() > 0) {
                    priceInputView.setText(TradeUtils.getInstance().getDepthList().get(0).buyPrice);
                }
            }
        } catch (Throwable t) {
            Logger.getInstance().debug(TAG, t);
        }
    }

    private void setConfirmValue(String coinName) {
        Logger.getInstance().debug(TAG, "setConfirmValue-coinName: " + coinName);
        if (confirmTxt == null) {
            return;
        }
        //更新购买与出售按钮
        if (this.isSell) {
            if (UserInfoManager.isLogin()) {
                confirmTxt.setText(mContext.getString(R.string.qe18) + coinName);
            } else {
                confirmTxt.setText(R.string.qe21);
            }
        } else {
            if (UserInfoManager.isLogin()) {
                confirmTxt.setText(mContext.getString(R.string.qe19) + coinName);
            } else {
                confirmTxt.setText(R.string.qe21);
            }
        }
        seekBarRelativeLayout.setProgress(0);
        numInputView.setUnit(coinName);
        amountInputView.setUnit(TradeDataHelper.getInstance().getCnyName(isETF));
        checkStatus();
    }

    private void resetUI(AssetResult result) {
        if (avlTotalText == null || avlBuyTxt == null || assetResult == null) {
            return;
        }
        //控制买卖数据显示
        if (isSell) {
            if (UserInfoManager.isLogin()) {
                confirmTxt.setText(mContext.getString(R.string.qe18) + ValueUtils.getString(TradeDataHelper.getInstance().getCoinName(isETF)));
            }
            buyTotalTipTxt.setText(R.string.qe68);
            String total = NorUtils.NumberFormatd(fcountNumber).format(result.asset.coin.total);
            avlTotalText.setText(total);
            mBinding.tvMax.setText(total);
            avlBuyUnitTxt.setText(TradeDataHelper.getInstance().getCnyName(isETF));
            mBinding.tvMaxUnit.setText(TradeDataHelper.getInstance().getCoinName(isETF));
            avlTotalUnitTxt.setText(ValueUtils.getString(TradeDataHelper.getInstance().getCoinName(isETF)));
            mCoinTotal = avlTotalText.getText().toString();
        } else {
            if (UserInfoManager.isLogin()) {
                confirmTxt.setText(mContext.getString(R.string.qe19) + ValueUtils.getString(TradeDataHelper.getInstance().getCoinName(isETF)));
            }
            buyTotalTipTxt.setText(R.string.qe66);
            int scale = StringUtilKt.getPrecision(result.asset.rmb.total);
            String total = NorUtils.NumberFormatNo(scale).format(result.asset.rmb.total);
            avlTotalText.setText(total);
            avlBuyUnitTxt.setText(ValueUtils.getString(TradeDataHelper.getInstance().getCoinName(isETF)));

            avlTotalUnitTxt.setText(TradeDataHelper.getInstance().getCnyName(isETF));
            mRmbTotal = avlTotalText.getText().toString();
        }
        refreshCanBuyOrSell();
        numInputView.setText("");
        //判断是否可用
        confirmTxt.setEnabled(true);
    }

    private void refreshCanBuyOrSell() {
        if (!isSell) {
            refreshCanBuy();
        } else {
            refreshCanSell();
        }
    }

    private void refreshCanSell() {
        avlBuyTxt.setText(TradeHelper.getExpectCanSell(priceInputView.getText().trim(), avlTotalText.getText().toString(), fcountPrice));
    }

    //刷新预计可买的数量
    private void refreshCanBuy() {
        if (mTradeType.get() == TRADE_TYPE_MARKET){
            if (assetResult == null){
                return;
            }
            String total = NorUtils.NumberFormatd(fcountNumber).format(assetResult.asset.rmb.total);
            mBinding.tvMax.setText(total);
            mBinding.tvMaxUnit.setText(ValueUtils.getString(TradeDataHelper.getInstance().getCnyName(isETF)));
        } else {
            mBinding.tvMax.setText(TradeHelper.getExpectCanBuy(priceInputView.getText().trim(), avlTotalText.getText().toString(),
                    TradeDataHelper.getInstance().getId(isETF), fcountNumber));
            mBinding.tvMaxUnit.setText(ValueUtils.getString(TradeDataHelper.getInstance().getCoinName(isETF)));
        }

        avlBuyTxt.setText(TradeHelper.getExpectCanBuy(priceInputView.getText().trim(), avlTotalText.getText().toString(),
                TradeDataHelper.getInstance().getId(isETF), fcountNumber));
    }

    @Override
    protected void initData() {
        refreshPrecious();
        initObserver();
    }

    private void initObserver() {
        mTradeViewModel.getOrderResult().observe(mFragment, new Observer<CommonResult<OrderResultBean>>() {
            @Override
            public void onChanged(CommonResult<OrderResultBean> orderResultBeanCommonResult) {
                Logger.getInstance().debug(TAG, "viewModel.getOrderResult():" + GsonUtil.obj2Json(orderResultBeanCommonResult, CommonResult.class));
                DialogManager2.INSTANCE.dismiss();
                showLoadding(false);
                if (numInputView == null) {
                    return;
                }
                if (orderResultBeanCommonResult.isSuccess()) {
                    orderResult(orderResultBeanCommonResult);
                } else {
                    if (TextUtils.equals(orderResultBeanCommonResult.getCode(), "200017")) {
                        int type = 0;
                        if (TextUtils.equals(side, OrderSide.BUY.getSide())) {
                            type = OrderSide.BUY.getType();
                        } else {
                            type = OrderSide.SELL.getType();
                        }
                        startTradeDialog(type);
                        SnackBarUtils.ShowRed((Activity) mContext, orderResultBeanCommonResult.getMessage());
                    } else if (TextUtils.equals(orderResultBeanCommonResult.getCode(), "-102")) {
                        try {
                            TradeHelper.showLimitedTimeTips((Activity) mContext, orderResultBeanCommonResult.getMessage());
                        } catch (Throwable t) {
                            Logger.getInstance().error(t);
                        }
                    } else {
                        SnackBarUtils.ShowRed((Activity) mContext, orderResultBeanCommonResult.getMessage());
                    }
                }
            }
        });
        mTradeViewModel.getDepthData().observe(mFragment, new Observer<CommonResult<XDepthData>>() {
            @Override
            public void onChanged(CommonResult<XDepthData> xDepthDataCommonResult) {
                Logger.getInstance().debug(TAG, "viewModel.getDepthData():" + GsonUtil.obj2Json(xDepthDataCommonResult, CommonResult.class));
                if (numInputView == null) {
                    TradeUtils.getInstance().setDepthList(null);
                    return;
                }
                if (xDepthDataCommonResult.isSuccess()) {
                    if (xDepthDataCommonResult.getData() == null) {
                        Logger.getInstance().debug(TAG, "result is null.");
                        TradeUtils.getInstance().setDepthList(null);
                        return;
                    }
                    if (TextUtils.equals(xDepthDataCommonResult.getCode(), "200")) {
                        ArrayList<TradeOrder.OrderInfo> list = TradeUtils.getDepthList(xDepthDataCommonResult.getData());
                        if (list != null && !list.isEmpty()) {
                            TradeUtils.getInstance().setDepthList(list);
                        }
                        setValue();
                    }
                } else {
                    TradeUtils.getInstance().setDepthList(null);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeCoin(BizEvent.Trade.CoinEvent coinEvent) {
        refreshPrecious();
        seekBarRelativeLayout.setProgress(0);
        if (coinEvent == null) {
            return;
        }
        if (coinEvent.isETF != isETF) {
            return;
        }
        if (TradeUtils.getInstance().getDepthList() == null) {
            reset(coinEvent.coinName, coinEvent.cnyName);
        } else if (!TextUtils.equals(coinEvent.id, TradeDataHelper.getInstance().getId(isETF) + "")) {//选择不同币种，清除数据
            reset(coinEvent.coinName, coinEvent.cnyName);
        }
        this.priceInputView.setText("");
    }

    private void refreshPrecious() {
        fcountPrice = CurrencyPairUtil.getPricePreciousById(TradeDataHelper.getInstance().getId(isETF));
        fcountNumber = CurrencyPairUtil.getQuantityPreciousById(TradeDataHelper.getInstance().getId(isETF));
        Logger.getInstance().error("refreshPrecious fcountPrice : " + fcountPrice + "  this:" + this.hashCode());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginActivityClose(Event.closeActivity event){
        if (event.className.equals("LoginActivity")){
            setConfirmValue(TradeDataHelper.getInstance().getCoinName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeCoin(BizEvent.Trade.Gear gear) {
        if (gear.isETF != isETF) {
            return;
        }
        //更新了档位
        updatePrice();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 5)
    public void onItemClick(BizEvent.Trade.ClickHandicap clickHandicap) {
        if (clickHandicap == null) {
            //TODO 异常情况
            return;
        }
        if (clickHandicap.isETF != isETF) {
            return;
        }
        isUpdateData = true;

        numInputView.clearFocus();
        amountInputView.clearFocus();
        priceInputView.setText(NorUtils.NumberFormat(fcountPrice).format(DoubleUtils.parseDouble(clickHandicap.price)) + "", true);
        refreshCanBuyOrSell();
        //TODO 待优化
        if (!UserInfoManager.isLogin()) {
            numInputView.setText("");
        } else {
            numInputView.setText(ValueUtils.getValue(clickHandicap.count, fcountNumber));
        }
        String num = numInputView.getText().trim();
        if (!isSell) {//买
            updateAmount("");
            if (!TextUtils.isEmpty(num)) {
                Double numVal = DoubleUtils.parseDouble(num);
                if (!TextUtils.isEmpty(mRmbTotal)) {
                    //如果点击的数量大于了预计可买，则取预计可买
                    if (numVal > DoubleUtils.parseDouble(avlBuyTxt.getText().toString())) {
                        updateNum(avlBuyTxt.getText().toString());
                    } else {
                        updateNum(num);
                    }
                    updateAmount(NorUtils.NumberFormat(fcountPrice).format(MathHelper.mul(numInputView.getText(), priceInputView.getText())));
                } else if (numVal > 0) {
                    updateAmount(NorUtils.NumberFormat(fcountPrice).format(DoubleUtils.parseDouble(clickHandicap.price) * numVal) + "");
                }
            }
            updateSeekBar(numInputView.getText().trim());
        } else {
            double total = DoubleUtils.parseDouble(mCoinTotal);
            double clickNum = DoubleUtils.parseDouble(clickHandicap.count);
            if (TextUtils.isEmpty(num)) {
                amountInputView.setText("");
            } else if (!TextUtils.isEmpty(mCoinTotal)) {
                try {
                    if (total > clickNum) {
                        num = ValueUtils.getValue(clickHandicap.count, fcountNumber);
                        numInputView.setText(ValueUtils.getValue(clickHandicap.count, fcountNumber));
                        amountInputView.setText(NorUtils.NumberFormat(fcountPrice).format(DoubleUtils.parseDouble(clickHandicap.price) * DoubleUtils.parseDouble(clickHandicap.count)) + "");
                    } else {
                        num = ValueUtils.getValue(mCoinTotal, fcountNumber);
                        numInputView.setText(ValueUtils.getValue(mCoinTotal, fcountNumber));
                        amountInputView.setText(NorUtils.NumberFormat(fcountPrice).format(DoubleUtils.parseDouble(clickHandicap.price) * DoubleUtils.parseDouble(mCoinTotal)) + "");
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            } else {
                num = "";
                numInputView.setText("");
            }
            updateSeekBarForSell(num);
            setButtonEnabled(true);
        }
        isUpdateData = false;
    }

    public void reset() {
        //根据当前选择的币种，重新初始化数据
        Coin coin = TradeDataHelper.getInstance().getCoin(isETF);
        this.reset(coin.coinName, coin.cnyName);
    }

    private void reset(String coinName, String cnyName) {
        if (numInputView == null) {
            return;
        }
        this.isUpdateData = true;
        this.assetResult = null;
        this.numInputView.setValue("", coinName);
        this.amountInputView.setValue("", cnyName);
        this.priceInputView.setUnit("");
        this.avlTotalText.setText("--");
        this.avlTotalUnitTxt.setText("--");
        this.avlBuyTxt.setText("--");
        mBinding.tvMax.setText("--");
        this.avlBuyUnitTxt.setText("--");
        this.isUpdateData = false;
        this.seekBarRelativeLayout.setProgress(0);
        if (!UserInfoManager.isLogin()) {
            this.confirmTxt.setText(R.string.qe21);
        } else {
            this.confirmTxt.setText("");
        }
        showLoadding(true);
        this.setButtonEnabled(true);
        checkStatus();
    }

    private void setValue() {
        if (numInputView == null) {
            return;
        }
        String price = "";
        if (isSell) {
            if (TradeUtils.getInstance().getDepthList().isEmpty()) {
                needWrite = false;
                return;
            }
            price = TradeUtils.getInstance().getDepthList().get(0).buyPrice;
        } else {
            if (TradeUtils.getInstance().getDepthList().isEmpty()) {
                needWrite = false;
                return;
            }
            price = TradeUtils.getInstance().getDepthList().get(0).sellPrice;
        }
        numInputView.setValue("", TradeDataHelper.getInstance().getCoinName(isETF));
        amountInputView.setValue("", TradeDataHelper.getInstance().getCnyName(isETF));
        priceInputView.setValue(NorUtils.NumberFormat(fcountPrice).format(DoubleUtils.parseDouble(price)) + "",
                "");

        if (!TradeUtils.getInstance().getDepthList().isEmpty()) {
            mFirstBuy = TradeUtils.getInstance().getDepthList().get(0).buyPrice;
            mFirstSell = TradeUtils.getInstance().getDepthList().get(0).sellPrice;
        }
        needWrite = false;
    }

    public void updateAsset(AssetResult result) {
        if (result == null) {
            //TODO 处理异常情况
            return;
        }
        checkStatus();
        this.assetResult = result;
        //TODO 待跟踪数据
        resetUI(result);
        //TODO 可能会有更新数据问题
        showLoadding(false);
    }

    @Override
    public void onChange(String id, String coinName, String cnyName, int selfStation) {
        this.needWrite = true;
        //TODO 实现切换币种方法
        setConfirmValue(coinName);
    }

    boolean checkInput() {

        if (mTradeType.get() == TRADE_TYPE_MARKET) {
            if (mIsBuy.get()) {
                if (TextUtils.isEmpty(amountInputView.getText().toString())) {
                    MToast.show(mContext, getString(R.string.qe94), 1);
                    return false;
                }
                if (DoubleUtils.parseDouble(amountInputView.getText().toString()) <= 0) {
                    MToast.show(mContext, getString(R.string.qe94), 1);
                    return false;
                }
            } else {
                if (TextUtils.isEmpty(numInputView.getText().toString())) {
                    MToast.show(mContext, getString(R.string.qe95), 1);
                    return false;
                }
                if (DoubleUtils.parseDouble(numInputView.getText().toString()) <= 0) {
                    MToast.show(mContext, getString(R.string.qe95), 1);
                    return false;
                }
            }

        } else {

            if (TextUtils.isEmpty(priceInputView.getText().toString())) {
                MToast.show(mContext, getString(R.string.qe48), 1);
                return false;
            }

            if (TextUtils.isEmpty(numInputView.getText().toString())) {
                MToast.show(mContext, getString(R.string.qe49), 1);
                return false;
            }
            if (DoubleUtils.parseDouble(numInputView.getText().toString()) <= 0) {
                MToast.show(mContext, getString(R.string.qe49), 1);
                return false;
            }
        }

        // 市价卖出时 不检查金额
        if (isSell && mTradeType.get() == TRADE_TYPE_MARKET){
            return true;
        }

        if ("USDT".equalsIgnoreCase(TradeDataHelper.getInstance().getCnyName(isETF))) {
            if (DoubleUtils.parseDouble(amountInputView.getText().toString()) < 5) {
                MToast.show(mContext, getString(R.string.qe50) + TradeDataHelper.getInstance().getCnyName(isETF), 1);
                return false;
            }
        } else {
            if (DoubleUtils.parseDouble(amountInputView.getText().toString()) < 10) {
                MToast.show(mContext, getString(R.string.qe51) + TradeDataHelper.getInstance().getCnyName(isETF), 1);
                return false;
            }
        }
        return true;
    }

    protected void confirm() {
        if (UserInfoManager.isLogin() && !confirmTxt.getText().toString().startsWith(mContext.getResources().getString(R.string.qe36))) {
            if (checkInput()) {
                if (!isSell && confirmTxt.getText().toString().startsWith(getString(R.string.qe37))) {
                    //TODO 待优化
                    if (assetResult != null && assetResult.asset.rmb != null) {
                        if (assetResult.asset.rmb.total < DoubleUtils.parseDouble(amountInputView.getText().toString())) {
                            MToast.show(mContext, getString(R.string.qe15), 2);
                            return;
                        }
                    } else {
                        MToast.show(mContext, getString(R.string.qe16), 1);
                        return;
                    }
                } else if (isSell && confirmTxt.getText().toString().startsWith(getString(R.string.qe38))) {
                    //TODO 待优化
                    if (assetResult != null) {
                        if (assetResult.asset.coin.total < DoubleUtils.parseDouble(numInputView.getText().toString().trim())) {
                            MToast.show(mContext, getString(R.string.qe23), 2);
                            return;
                        }
                    } else {
                        MToast.show(mContext, getString(R.string.qe24), 1);
                        return;
                    }
                }
                if (isSell) {
                    DialogUtils.getInstance().showTwoButtonDialog(mContext, getString(R.string.qe42), getString(R.string.qe43), getString(R.string.qe44));
                } else {
                    DialogUtils.getInstance().showTwoButtonDialog(mContext, getString(R.string.qe45), getString(R.string.qe46), getString(R.string.qe47));
                }
                DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                    @Override
                    public void onLiftClick(AlertDialog dialog, View view) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onRightClick(AlertDialog dialog, View view) {
                        dialog.dismiss();
                        if (!isSell) {
                            order(OrderSide.BUY.getSide());
                        } else if (isSell) {
                            order(OrderSide.SELL.getSide());
                        }
                    }
                });
            }
        } else {
            Intent intent = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(intent);
        }
    }

    //验证交易密码
    private void verifyTradePwd(String side, String pwd) {
        if (!GlobalFuncKt.isLoginOrGotoLoginActivity(mContext)) {
            return;
        }
        DialogManager2.INSTANCE.showProgressDialog(mContext);
        Type type = new TypeToken<BaseResponse<Map<String, Long>>>() {
        }.getType();
        Map<String, Object> params = new HashMap<>();
        params.put("password", pwd);
        //params= EncryptUtils.encrypt(params); //新系统不需要加密
        params.put("loginToken", UserInfoManager.getToken());
        OKHttpHelper.getInstance().post(UrlConstants.verifyTradePwd, params, new INetCallback<BaseResponse<Map<String, Long>>>() {
            @Override
            public void onSuccess(BaseResponse<Map<String, Long>> response) {
                DialogManager2.INSTANCE.dismiss();
                try {
                    Logger.getInstance().debug(TAG, "result:" + GsonUtil.obj2Json(response, SingleResult.class));
                    if (response == null) {
                        Logger.getInstance().debug(TAG, "result is null.");
                        return;
                    }
                    if (TextUtils.equals(response.getCode(), "200") && response.getData().containsKey("expireTime")) {
                        TradeDataHelper.getInstance().updatePasswordTime(response.getData().get("expireTime"));
                        order(side);
                    } else {
                        SnackBarUtils.ShowRed((Activity) mContext, response.getMessage());
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                DialogManager2.INSTANCE.dismiss();
                Logger.getInstance().debug(TAG, "error", e);
                SnackBarUtils.ShowRed((Activity) mContext, getString(R.string.qe27));
            }
        }, type);
    }

    //下单
    private void order(String side) {
        if (!GlobalFuncKt.isLoginOrGotoLoginActivity(mContext)) {
            return;
        }
        DialogManager2.INSTANCE.showProgressDialog(mContext);
        showLoadding(true);
        this.side = side;
        String orderType;

        String amount = amountInputView.getText();

        if (mTradeType.get() == TRADE_TYPE_LIMIT) {
            orderType = OrderType.LIMIT.getType();
        } else {
            orderType = OrderType.MARKET.getType();
            double feeRate = CurrencyPairUtil.getFeeRateById(TradeDataHelper.getInstance().getId(isETF));
            double amountDouble = DoubleUtils.parseDouble(amount);

            double fee = amountDouble * feeRate;
            // 购买金额减去手续费如果小于总资产，则减去手续费再下单
            if (amountDouble + fee > assetResult.asset.rmb.total){
                amountDouble = amountDouble - fee;
            }

            amount = NorUtils.NumberFormat(4, RoundingMode.DOWN).format(amountDouble);
        }
        mTradeViewModel.order(priceInputView.getText(), numInputView.getText(), amount, side, TradeDataHelper.getInstance().getId(isETF) + "",
                orderType);
    }

    private void orderResult(CommonResult<OrderResultBean> response) {
        if (response == null || response.getData() == null) {
            Logger.getInstance().debug(TAG, "result is null.");
            return;
        }
        if (TextUtils.equals(response.getCode(), "200")) {
            priceInputView.setText("");
            numInputView.setText("");
            amountInputView.setText("");
            SnackBarUtils.ShowBlue((Activity) mContext, response.getMessage());
            EventBus.getDefault().post(new BizEvent.Trade.MarketInfo(isETF));
        }
    }

    private void setConfirmButton() {
        if (isSell) {
            if (UserInfoManager.isLogin()) {
                confirmTxt.setText(mContext.getString(R.string.qe18) + ValueUtils.getString(TradeDataHelper.getInstance().getCoinName(isETF)));
            } else {
                confirmTxt.setText(R.string.qe21);
            }
        } else {
            if (UserInfoManager.isLogin()) {
                confirmTxt.setText(mContext.getString(R.string.qe19) + ValueUtils.getString(TradeDataHelper.getInstance().getCoinName(isETF)));
            } else {
                confirmTxt.setText(R.string.qe21);
            }
        }
    }

    public void showLoadding(boolean isShow) {
        try {
            if (!UserInfoManager.isLogin()) {
                confirmTxt.setText(R.string.qe21);
                sumbitLoaddingIV.setVisibility(View.GONE);
                AnimationDrawable animationDrawable = (AnimationDrawable) sumbitLoaddingIV.getDrawable();
                animationDrawable.stop();
            } else if (isShow) {
                confirmTxt.setText("");
                sumbitLoaddingIV.setVisibility(View.VISIBLE);
                AnimationDrawable animationDrawable = (AnimationDrawable) sumbitLoaddingIV.getDrawable();
                animationDrawable.start();
                return;
            } else {
                sumbitLoaddingIV.setVisibility(View.GONE);
                AnimationDrawable animationDrawable = (AnimationDrawable) sumbitLoaddingIV.getDrawable();
                animationDrawable.stop();
            }
            setConfirmButton();
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private String getString(int resId) {
        //TODO 异常情况
        return mContext.getResources().getString(resId);
    }

    AddDialog dialog = null;

    private void startTradeDialog(int type) {//0 买入 1卖出
        /**
         * 交易密码对话框
         * */
        if (dialog != null && dialog.isVisible()) {
            return;
        }
        dialog = new AddDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putInt("type1", 0);
        bundle.putBoolean("isETF", isETF);
        dialog.setArguments(bundle);
        dialog.setDialog(AddDialog.START_TRADE);
        if (null != fragmentManager) {
            dialog.show(fragmentManager, getClass().getSimpleName());
        }
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    /**
     * 交易 “密码” 输入后
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getShift_psd(Event.getStart_Trade_psd start_trade_psd) {
        //判断是否为同一业务类型
        if (start_trade_psd.isETF != isETF) {
            //说明不是同一下单业务
            Logger.getInstance().debug(TAG, "非当前业务下单类型!");
            return;
        }
        String tradeCnyPrice = priceInputView.getText().toString().trim(); // 交易价格
        String tradeAmount = numInputView.getText().toString().trim();// 交易数量
        String tradePwd = start_trade_psd.trade_psd; // 交易密码
        //
        try {
            tradePwd = EncryptUtils.encryptByPublicKey(tradePwd, AppConstants.KEY.TRADE_PWD_PUBLIC_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (start_trade_psd.type == 0) {
            verifyTradePwd(OrderSide.BUY.getSide(), tradePwd);
        } else if (start_trade_psd.type == 1) {
            verifyTradePwd(OrderSide.SELL.getSide(), tradePwd);
        }
    }

    public void applyTheme() {
        mBinding.invalidateAll();
        seekBarRelativeLayout.updateSeekBar(!isSell);
    }

}
