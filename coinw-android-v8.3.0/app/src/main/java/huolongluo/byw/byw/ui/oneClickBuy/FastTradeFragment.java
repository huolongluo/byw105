package huolongluo.byw.byw.ui.oneClickBuy;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.legend.ui.transfer.AccountTransferActivity;
import com.blankj.utilcodes.utils.ToastUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseFragment;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.bindphone.BindPhoneActivity;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.byw.ui.activity.setchangepsw.SetChangePswActivity;
import huolongluo.byw.byw.ui.oneClickBuy.bean.AdvertisementEntity;
import huolongluo.byw.byw.ui.oneClickBuy.bean.AvgEntity;
import huolongluo.byw.byw.ui.oneClickBuy.bean.CardInfosEntity;
import huolongluo.byw.byw.ui.oneClickBuy.bean.CommitedEntity;
import huolongluo.byw.byw.ui.oneClickBuy.bean.OrderConfigEntity;
import huolongluo.byw.helper.FaceVerifyHelper;
import huolongluo.byw.helper.IdentityVerifyHelper;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.c2c.oct.activity.OtcBuyConfirmActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcOrderManagerActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcSetNickActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcSetUserInfoActivity;
import huolongluo.byw.reform.c2c.oct.activity.PaymentAccountActivityNew;
import huolongluo.byw.reform.c2c.oct.activity.SellerInfoActivity;
import huolongluo.byw.reform.c2c.oct.activity.sellactivity.OtcOrderShopsManagerActivity;
import huolongluo.byw.reform.c2c.oct.activity.sellactivity.OtcUserSellWaitOtherPayActivity;
import huolongluo.byw.reform.c2c.oct.bean.OtcCoinBean;
import huolongluo.byw.reform.c2c.oct.bean.OtcUserInfoBean;
import huolongluo.byw.reform.c2c.oct.bean.PayOrderInfoBean;
import huolongluo.byw.reform.c2c.oct.fragment.OtcActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.Util;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.DoubleUtils;
import okhttp3.Request;

import static java.lang.Double.parseDouble;

/***
 * 快捷交易
 */
public class FastTradeFragment extends BaseFragment implements View.OnClickListener, OneClickBuyHelper.IClickOrderInfoCallback {
    @BindView(R.id.c2cTrade)
    TextView c2cTrade;
    @BindView(R.id.et_buy_money)
    EditText et_buy_money;
    @BindView(R.id.serviceCharge)
    TextView serviceCharge;
    @BindView(R.id.des_price)
    TextView des_price;
    private OrderConfigEntity orderConfigEntity;
    @BindView(R.id.tablayout)
    LinearLayout tabLayout;
    @BindView(R.id.tv_cnyt_t)
    TextView tv_cnyt_t;
    @BindView(R.id.tv_cnyt_c)
    TextView tv_cnyt_c;
    @BindView(R.id.title_head)
    TextView title_head;
    @BindView(R.id.buy_type)
    TextView buy_type;
    @BindView(R.id.tv_commit)
    TextView commitTxt;
    @BindView(R.id.menu_iv)
    ImageView menu_iv;
    @BindView(R.id.v_line)
    View lineView;
    @BindView(R.id.iv_money)
    View moneyView;
    @BindView(R.id.tv_unit)
    TextView unitTxt;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    private int buyOrSellType = 1;//0为金额购买，1为数量购买   1:购买，2：出售
    public static final int BUY = 1, SELL = 2;
    private static final int PRICE = 1, COUNT = 2;
    private int type = 1;//1为购买 2为出售
    private int payTypes;
    //TODO 临时处理重复提交（JIRA：COIN-2385）
    private boolean isPosting = false;
    private TextView[] tvCoinNames;
    private int positionClickTab = 0;
    private boolean tag = false;

    @Override
    protected void initDagger() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setHint(String txt) {
        if (TextUtils.isEmpty(txt)) {
            return;
        }
        SpannableString ss = new SpannableString(txt);//定义hint的值
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(16, true);//设置字体大小 true表示单位是sp
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        et_buy_money.setHint(new SpannedString(ss));
    }

    @Override
    protected void initViewsAndEvents(View rootView) {
        initTitle();
        avgPrice();
        //设置默认值
        setHint(String.format(getString(R.string.quantity_minimum_buy), "100"));
        EventBus.getDefault().register(this);
        SoftKeyBoardListener.setListener(getActivity(), new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                if (lineView != null) {
                    //et_buy_money.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                    lineView.setBackgroundColor(getResources().getColor(R.color.color_4E4883));
                }
            }

            @Override
            public void keyBoardHide(int height) {
                if (et_buy_money != null && lineView != null) {
                    //et_buy_money.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    lineView.setBackgroundColor(getResources().getColor(R.color.color_ecedf4));
                    lineView.setFocusable(true);
                }
            }
        });
        //添加事件，控制不要重复添加
        addEvent();
        ivBack.setOnClickListener(this);
    }

    private void addEvent() {
        et_buy_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                beforeEdit = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (tag) {
                    tag = false;
                    return;
                }
                String num = charSequence.toString();
                if (num.length() == 0) {
                    // No entered text so will show hint
                    //et_buy_money.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    lineView.setBackgroundColor(getResources().getColor(R.color.color_ecedf4));
                } else {
                    //et_buy_money.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                    lineView.setBackgroundColor(getResources().getColor(R.color.color_4E4883));
                }
                try {
                    if (buyOrSellType == PRICE && null != orderConfigEntity && !TextUtils.isEmpty(num)) {
                        double buyMax = Double.parseDouble(orderConfigEntity.getData().getBuyMax());
                        double et_content = Double.parseDouble(num);
                        StringBuilder builder = new StringBuilder(charSequence.toString());
                        if (et_content > buyMax) {
                            builder.delete(i, i + 1);
                            setTextValue(et_buy_money, builder.toString());
                            et_buy_money.setSelection(builder.length());
                            ToastUtils.showLongToast(String.format(getString(R.string.max_quantity), String.valueOf(buyMax)));
                            return;
                        }
                    }
                } catch (Throwable t) {
                    Logger.getInstance().error(t);
                    t.printStackTrace();
                }
                OtcCoinBean.DataBean dataBean = AppUtils.getOTCCoin(coinId);
                //默认精度
                //判断是价格还是数量交易
                int quantityPrecision = 2;
                int pricePrecision = 3;
                if (dataBean != null) {
                    quantityPrecision = dataBean.getQuantityPrecision();
                    pricePrecision = dataBean.getPricePrecision();
                }
                int precision = pricePrecision;
                if (buyOrSellType == COUNT) {
                    precision = quantityPrecision;
                }

                if (!TextUtils.isEmpty(num) && num.contains(".")) {
                    int dot = num.indexOf(".");
                    if (num.length() - dot > precision + 1) {
                        num = NorUtils.NumberFormatNo(precision).format(parseDouble(num));
                        setTextValue(et_buy_money, num);
                        et_buy_money.setSelection(num.length());
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    private void setTextValue(TextView txt, String val) {
        tag = true;
        txt.setText(val);

    }

    public void setAmount(String amount, boolean isCnyt) {
        if (et_buy_money != null) {
            et_buy_money.setText(amount);
            if (!isCnyt) {//不是cnyt需要切换tab
                changeCoinName(1);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void upTitle(Event.UPTitle data) {
        initTitle();
        avgPrice();
    }

    @OnClick({R.id.tv_cnyt_c, R.id.menu_iv, R.id.tv_cnyt_t, R.id.c2cTrade, R.id.otc_select_tv, R.id.one_click_buy_select_tv, R.id.bt_commit, R.id.buy_type})
    public void onClick_(View view) {
        switch (view.getId()) {
            case R.id.c2cTrade:
                ((OtcActivity) getActivity()).selectC2cFragment();
                break;
            case R.id.otc_select_tv:
                ((OtcActivity) getActivity()).selectOtcFragment();
                break;
            case R.id.one_click_buy_select_tv:

                break;
            case R.id.bt_commit:
                commit();
                break;
            case R.id.tv_cnyt_c:
                tv_cnyt_t.setTextColor(getResources().getColor(R.color.ffa9a4c0));
                tv_cnyt_c.setTextColor(getResources().getColor(R.color.white));
                initTab(BUY, R.drawable.market_custom_bg, R.drawable.market_norml_bg);
                break;
            case R.id.tv_cnyt_t:
                tv_cnyt_t.setTextColor(getResources().getColor(R.color.white));
                tv_cnyt_c.setTextColor(getResources().getColor(R.color.ffa9a4c0));
                initTab(SELL, R.drawable.market_norml_bg, R.drawable.market_custom_bg);
                break;
            case R.id.buy_type:
                setbuyType();
                break;
            case R.id.menu_iv:
                showPopup();
                break;
            case R.id.ivBack:
                requireActivity().finish();
                break;
        }
    }

    /**
     * 点击按数量 或者 金额购买 逻辑
     */
    private void setbuyType() {
        buyOrSellType = (buyOrSellType == PRICE ? COUNT : PRICE);
        moneyView.setVisibility(buyOrSellType == PRICE ? View.VISIBLE : View.GONE);
        unitTxt.setVisibility(buyOrSellType == PRICE ? View.GONE : View.VISIBLE);
        unitTxt.setText(TextUtils.isEmpty(selectName) ? "" : selectName);
        if (type == BUY) {
            buy_type.setText(buyOrSellType == PRICE ? R.string.by_count : R.string.by_amount);
        } else {
            buy_type.setText(buyOrSellType == PRICE ? R.string.by_count_sell : R.string.by_amount_sell);
        }
        et_buy_money.setText("");
        initTitleDes();
        if (orderConfigEntity != null && orderConfigEntity.getCode() == 0) {
            if (buyOrSellType == COUNT) {
                setHint(type == BUY ? getString(R.string.cx85) : getString(R.string.cx85_sell));
            } else {
                if (type == BUY) {
                    setHint(String.format(getString(R.string.quantity_minimum_buy), orderConfigEntity.getData().getBuyMin()));
                } else {
                    setHint(String.format(getString(R.string.quantity_minimum_sell), orderConfigEntity.getData().getBuyMin()));
                }
            }
        } else {
            if (buyOrSellType == COUNT) {
                setHint(type == BUY ? getString(R.string.cx85) : getString(R.string.cx85_sell));
            } else {
                setHint(String.format(getString(R.string.quantity_minimum_buy), "100"));
            }
        }
    }

    private void initTab(int i, int p, int p2) {
        type = i;
        moneyView.setVisibility(buyOrSellType == PRICE ? View.VISIBLE : View.GONE);
        if (type == BUY) {
            buy_type.setText(buyOrSellType == PRICE ? R.string.by_count : R.string.by_amount);
        } else {
            buy_type.setText(buyOrSellType == PRICE ? R.string.by_count_sell : R.string.by_amount_sell);
        }
        getConfig();
        initTitleDes();
        avgPrice();
        tv_cnyt_c.setBackground(getActivity().getResources().getDrawable(p));
        tv_cnyt_t.setBackground(getActivity().getResources().getDrawable(p2));
    }

    private void initTitleDes() {
        if (type == BUY) {
            commitTxt.setText(R.string.buy_now);
            if (buyOrSellType == PRICE) {
                title_head.setText(R.string.buy_amount);
            } else {
                title_head.setText(R.string.buy_count);
            }
        } else {
            commitTxt.setText(R.string.sell_now);
            if (buyOrSellType == PRICE) {
                title_head.setText(R.string.sell_amount);
            } else {
                title_head.setText(R.string.sell_count);
            }
        }
    }

    private void commit() {
        if (!UserInfoManager.isLogin()) {//未登录
            startActivity(LoginActivity.class);
            return;
        }
        //风控
        if (!check1() || orderConfigEntity == null) {
            return;
        }
        if (et_buy_money == null) {
            return;
        }
        if (TextUtils.isEmpty(et_buy_money.getText())) {
            ToastUtils.showLongToast(getString(R.string.but_des));
            return;
        }
//        04-08 17:59:45.207 E/AndroidRuntime( 7431): FATAL EXCEPTION: main
//        04-08 17:59:45.207 E/AndroidRuntime( 7431): Process: huolongluo.byw, PID: 7431
//        04-08 17:59:45.207 E/AndroidRuntime( 7431): java.lang.NumberFormatException: For input string: "."
//        04-08 17:59:45.207 E/AndroidRuntime( 7431): at sun.misc.FloatingDecimal.readJavaFormatString(FloatingDecimal.java:2043)
//        04-08 17:59:45.207 E/AndroidRuntime( 7431): at sun.misc.FloatingDecimal.parseDouble(FloatingDecimal.java:110)
//        04-08 17:59:45.207 E/AndroidRuntime( 7431): at java.lang.Double.parseDouble(Double.java:538)
//        04-08 17:59:45.207 E/AndroidRuntime( 7431): at huolongluo.byw.byw.ui.oneClickBuy.FastTradeFragment.j(FastTradeFragment.java:6)
//        04-08 17:59:45.207 E/AndroidRuntime( 7431): at huolongluo.byw.byw.ui.oneClickBuy.FastTradeFragment.onClick_(FastTradeFragment.java:13)
//        04-08 17:59:45.207 E/AndroidRuntime( 7431): at g.a.b.f.e.Z.doClick(FastTradeFragment_ViewBinding.java:1)
//        04-08 17:59:45.207 E/AndroidRuntime( 7431): at butterknife.internal.DebouncingOnClickListener.onClick(DebouncingOnClickListener.java:4)
        double et_content = DoubleUtils.parseDouble(et_buy_money.getText().toString());
        double buyMin = DoubleUtils.parseDouble(orderConfigEntity.getData().getBuyMin());
        double buyMax = DoubleUtils.parseDouble(orderConfigEntity.getData().getBuyMax());
        if (buyOrSellType == PRICE && et_content < buyMin) {
            ToastUtils.showLongToast(String.format(getString(R.string.quantity_minimum), String.valueOf(buyMin)));
            return;
        }
        if (buyOrSellType == PRICE && et_content > buyMax) {
            ToastUtils.showLongToast(String.format(getString(R.string.max_quantity), String.valueOf(buyMax)));
            return;
        }
        if (type == BUY) {
//            OneClickBuyHelper.showPopupWindow(getActivity(), null, this);
            showPopupWindow(null);
        } else {
            getPayAccount();
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fast_trade_fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        initTitle();
        getConfig();
        if (C2cStatus.isShow) {
            c2cTrade.setVisibility(View.GONE);
        } else {
            c2cTrade.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取支付方式
     * 支付方式: 1 银行卡, 2 微信, 3 支付宝
     */
    public void showPopupWindow(CardInfosEntity orderTipBean) {
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.one_click_select_card_layout, null);
        RelativeLayout zfb = inflate.findViewById(R.id.zfb_view);
        RelativeLayout yhk = inflate.findViewById(R.id.yhk_view);
        RelativeLayout wx = inflate.findViewById(R.id.wx_view);
        TextView hide = inflate.findViewById(R.id.hide);//取消弹框
        TextView count_zhb = inflate.findViewById(R.id.count_zhb);
        TextView count_yhk = inflate.findViewById(R.id.count_yhk);
        TextView count_wx = inflate.findViewById(R.id.count_wx);
        PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        hide.setOnClickListener(v -> popupWindow.dismiss());
        if (null == orderTipBean) {//购买
            zfb.setVisibility(View.VISIBLE);
            wx.setVisibility(View.VISIBLE);
            yhk.setVisibility(View.VISIBLE);
            zfb.setOnClickListener(v -> orderInfo(3, null, -1, inflate, popupWindow));
            wx.setOnClickListener(v -> orderInfo(2, null, -1, inflate, popupWindow));
            yhk.setOnClickListener(v -> orderInfo(1, null, -1, inflate, popupWindow));
            popupWindow.showAtLocation(inflate, Gravity.BOTTOM, 0, 0);
            return;
        }
        //出售
        for (CardInfosEntity.DataBean dataBean : orderTipBean.getData()) {
            switch (dataBean.getType()) {
                case 3:
                    zfb.setVisibility(View.VISIBLE);
                    zfb.setOnClickListener(v -> orderInfo(3, dataBean.getAccount(), dataBean.getId(), inflate, popupWindow));
                    count_zhb.setText(dataBean.getAccount().replaceAll("(\\w+)\\w{5}(\\w+)", "$1***$2"));
                    break;
                case 2:
                    wx.setOnClickListener(v -> orderInfo(2, dataBean.getAccount(), dataBean.getId(), inflate, popupWindow));
                    wx.setVisibility(View.VISIBLE);
                    count_wx.setText(dataBean.getAccount().replaceAll("(\\w+)\\w{5}(\\w+)", "$1***$2"));
                    break;
                case 1:
                    yhk.setOnClickListener(v -> orderInfo(1, dataBean.getAccount(), dataBean.getId(), inflate, popupWindow));
                    yhk.setVisibility(View.VISIBLE);
                    count_yhk.setText(dataBean.getAccount().replaceAll("(\\w+)\\w{5}(\\w+)", "$1***$2"));
                    break;
            }
        }
        if (orderTipBean.getData().size() == 0) {
            RelativeLayout noPayType_ll = inflate.findViewById(R.id.noPayType_ll);
            noPayType_ll.setVisibility(View.VISIBLE);
            TextView to_set_card = inflate.findViewById(R.id.to_set_card);
            to_set_card.setOnClickListener(view -> {
                Intent intent = new Intent(getActivity(), PaymentAccountActivityNew.class);
                startActivity(intent);
                popupWindow.dismiss();
            });
        } else {
        }
        popupWindow.showAtLocation(inflate, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 订单处理-弹窗
     *
     * @param
     * @param account
     * @param inflate
     * @param popupWindow
     */
    @Override
    public void orderInfo(int payType, String account, int accountId, View inflate, PopupWindow popupWindow) {
        String advertisementType = String.valueOf(type);
        Map<String, String> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        params.put("advertisementType", advertisementType);
        params.put("coinId", String.valueOf(coinId));
        params.put("oneKeyTransactionType", String.valueOf(buyOrSellType));
        params.put("payWay", String.valueOf(payType));
        params.put("value", et_buy_money.getText().toString());
        OkhttpManager.postAsync(UrlConstants.advertisement, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    Logger.getInstance().debug(TAG, "url: " + UrlConstants.advertisement + " result:" + result);
                    AdvertisementEntity orderTipBean = new Gson().fromJson(result, AdvertisementEntity.class);
                    if (orderTipBean.getCode() == 0) {
                        orderInfoSuccess(payType, account, accountId, inflate, popupWindow, orderTipBean, params);
                    } else if (orderTipBean.getCode() == 123) {//需要faceId验证code
                        aliVerify(popupWindow, orderTipBean.getData().getUrl());
                    } else if (orderTipBean.getCode() == 124) {//黑名单
                        AppHelper.dismissPopupWindow(popupWindow);
                        FaceVerifyHelper.getInstance().aliVerifyBlack(getActivity(), orderTipBean.getValue());
                    } else {
//                        ToastUtils.showLongToast(orderTipBean.getValue());
                        AppHelper.dismissPopupWindow(popupWindow);
                        DialogUtils.getInstance().showOtcTipDialog(getActivity(), orderTipBean.getValue(), new DialogUtils.onBnClickListener() {
                            @Override
                            public void onLiftClick(AlertDialog dialog, View view) {
                                try {
                                    //重新下单
                                    AppHelper.dismissPopupWindow(popupWindow);
                                    if (et_buy_money != null) {
                                        et_buy_money.setText("");
                                    }
                                } catch (Throwable t) {
                                    Logger.getInstance().error(t);
                                }
                            }

                            @Override
                            public void onRightClick(AlertDialog dialog, View view) {
                                try {
                                    //自选交易
                                    AppHelper.dismissPopupWindow(popupWindow);
                                    if (et_buy_money != null) {
                                        et_buy_money.setText("");
                                    }
                                    //
                                    ((OtcActivity) getActivity()).selectOtcFragment();
                                } catch (Throwable t) {
                                    Logger.getInstance().error(t);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void aliVerify(PopupWindow popupWindow, String triggerType) {
        AppHelper.dismissPopupWindow(popupWindow);
        DialogUtils.getInstance().showRiskTipButtonDialog(getActivity(), new DialogUtils.onBnClickListener() {
            @Override
            public void onLiftClick(AlertDialog dialog, View view) {
                AppHelper.dismissDialog(dialog);
            }

            @Override
            public void onRightClick(AlertDialog dialog, View view) {
                AppHelper.dismissDialog(dialog);
                //去验证（阿里face验证）
                FaceVerifyHelper.getInstance().verify(getContext(), new DialogUtils.onBnClickListener() {
                    @Override
                    public void onLiftClick(AlertDialog dialog, View view) {
                        AppHelper.dismissDialog(dialog);
                    }

                    @Override
                    public void onRightClick(AlertDialog dialog, View view) {
                        AppHelper.dismissDialog(dialog);
                    }
                }, triggerType);
            }
        });
    }

    private void orderInfoSuccess(int payType, String account, int accountId, View inflate, PopupWindow popupWindow, AdvertisementEntity orderTipBean, Map<String, String> params) {
        AdvertisementEntity.DataBean data = orderTipBean.getData();
        params.put("sellPayWayId", String.valueOf(accountId));
        data.setOrderInfoMap(params);
        String payTypeName = "";
        payTypes = payType;
        switch (payType) {
            case 1:
                payTypeName = getString(R.string.b91);
                break;
            case 2:
                payTypeName = getString(R.string.b93);
                break;
            case 3:
                payTypeName = getString(R.string.b92);
                break;
        }
        setOrderInfo(inflate, popupWindow, data, TextUtils.isEmpty(account) ? payTypeName : account);
    }

    private void setOrderInfo(View inflate, PopupWindow popupWindow, AdvertisementEntity.DataBean orderTipBean, String accountInfo) {
        LinearLayout ll_commit = inflate.findViewById(R.id.ll_commit);
        LinearLayout ll_choose_card = inflate.findViewById(R.id.ll_choose_card);
        TextView account = inflate.findViewById(R.id.account);
        ImageView payIcon = inflate.findViewById(R.id.icon_pay);
        TextView price = inflate.findViewById(R.id.price);
        TextView count = inflate.findViewById(R.id.count);
        TextView total_prices = inflate.findViewById(R.id.total_prices);
        TextView title = inflate.findViewById(R.id.title);
        Button bt_commit = inflate.findViewById(R.id.bt_commit);
        account.setText(accountInfo);
        price.setText(String.format("%sCNY/%s", orderTipBean.getPrice_s(), orderTipBean.getCoinName()));
        initPayWayIcon(payIcon);
        count.setText(orderTipBean.getNum_s() + orderTipBean.getCoinName());
        total_prices.setText(orderTipBean.getTotalAmount_s() + " CNY");
        EditText et_pwd = inflate.findViewById(R.id.et_pwd);
        View pwd_desx = inflate.findViewById(R.id.pwd_desx);
        TextView payWaydes = inflate.findViewById(R.id.payWaydes);
        et_pwd.setVisibility(type == BUY ? View.GONE : View.VISIBLE);
        pwd_desx.setVisibility(type == BUY ? View.GONE : View.VISIBLE);
        payWaydes.setText(type == BUY ? getResources().getString(R.string.pay_way_) : getResources().getString(R.string.a66));
        bt_commit.setText(type == BUY ? getResources().getString(R.string.cc50) : getResources().getString(R.string.sell_commit));
        title.setText(type == BUY ? getResources().getString(R.string.cc50) : getResources().getString(R.string.sell_commit));
        eventClick(bt_commit).subscribe(o -> {
            if (type == SELL && TextUtils.isEmpty(et_pwd.getText().toString())) {
                ToastUtils.showLongToast(getString(R.string.pwd_empty));
                return;
            }
            //TODO 临时处理（JIRA：COIN-2385）
            //进行大量测试
            if (isPosting) {
                return;
            }
            isPosting = true;
            commitOrder(orderTipBean, null == et_pwd.getText() ? "" : et_pwd.getText().toString(), popupWindow);
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
//        commitTxt.setOnClickListener(view -> {
//            if (type == SELL && TextUtils.isEmpty(et_pwd.getText().toString())) {
//                ToastUtils.showLongToast(getString(R.string.pwd_empty));
//                return;
//            }
//            commitOrder(orderTipBean, null == et_pwd.getText() ? "" : et_pwd.getText().toString(), popupWindow);
//        });
        inflate.findViewById(R.id.back).setOnClickListener(view -> {
            ll_choose_card.setVisibility(View.VISIBLE);
            ll_commit.setVisibility(View.GONE);
        });
        inflate.findViewById(R.id.hide1).setOnClickListener(view -> {
            if (null != popupWindow) {
                popupWindow.dismiss();
            }
        });
        ll_choose_card.setVisibility(View.GONE);
        ll_commit.setVisibility(View.VISIBLE);
    }

    private void initPayWayIcon(ImageView account) {
        Drawable drawable = null;
        switch (payTypes) {
            case Constant.ALIPAY:
                drawable = getResources().getDrawable(R.mipmap.zfb_bg);
                break;
            case Constant.BANK:
                drawable = getResources().getDrawable(R.mipmap.bank_card);
                break;
            case Constant.WECHAT:
                drawable = getResources().getDrawable(R.mipmap.wx_bg);
                break;
        }
        if (null != drawable) {
            account.setImageDrawable(drawable);
        }
    }

    private void commitOrder(AdvertisementEntity.DataBean orderTipBean, String pwd, PopupWindow popupWindow) {
        Map<String, String> params = orderTipBean.getOrderInfoMap();
        params.put("adId", String.valueOf(orderTipBean.getAdId()));
        params.put("tradePwd", pwd);
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.add, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                isPosting = false;
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    isPosting = false;
                    CommitedEntity orderTipBean = new Gson().fromJson(result, CommitedEntity.class);
                    if (orderTipBean.getCode() == 0) {
//                                            et_buy_money.setText("");
                        if (null != popupWindow) {
                            popupWindow.dismiss();
                        }
                        ToastUtils.showLongToast(orderTipBean.getValue());
                        if (type == BUY) {
                            payOrder(orderTipBean.getData());
                        } else {
                            Intent intent = new Intent(getActivity(), OtcUserSellWaitOtherPayActivity.class);
                            intent.putExtra("orderId", orderTipBean.getData());
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        }
                    } else if (orderTipBean.getCode() == 125) {
                        DialogUtils.getInstance().showOneButtonDialog(getActivity(), orderTipBean.getValue(), getString(R.string.str_isee));
                    } else {
                        AppHelper.dismissPopupWindow(popupWindow);
                        DialogUtils.getInstance().showOtcTipDialog(getActivity(), orderTipBean.getValue(), new DialogUtils.onBnClickListener() {
                            @Override
                            public void onLiftClick(AlertDialog dialog, View view) {
                                try {
                                    //重新下单
                                    AppHelper.dismissPopupWindow(popupWindow);
                                    if (et_buy_money != null) {
                                        et_buy_money.setText("");
                                    }
                                } catch (Throwable t) {
                                    Logger.getInstance().error(t);
                                }
                            }

                            @Override
                            public void onRightClick(AlertDialog dialog, View view) {
                                try {
                                    //自选交易
                                    AppHelper.dismissPopupWindow(popupWindow);
                                    if (et_buy_money != null) {
                                        et_buy_money.setText("");
                                    }
                                    ((OtcActivity) getActivity()).selectOtcFragment();
                                } catch (Throwable t) {
                                    Logger.getInstance().error(t);
                                }
                            }
                        });
//                        ToastUtils.showLongToast(orderTipBean.getValue());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void toPay(PayOrderInfoBean orderTipBean) {
        Intent intent = new Intent(getActivity(), OtcBuyConfirmActivity.class);
        intent.putExtra("orderId", orderTipBean.getData().getOrder().getId());
        intent.putExtra("adRemark", orderTipBean.getData().getAdRemark() + "");
        intent.putExtra("payType", orderTipBean.getData().getOrder().getBuyerSelectedSellerPayType().getType());
        intent.putExtra("totalAmount", orderTipBean.getData().getOrder().getTotalAmount());
        intent.putExtra("payLimit", orderTipBean.getData().getPayLimit());
        intent.putExtra("transReferNum", orderTipBean.getData().getOrder().getTransReferNum());
        intent.putExtra("payBean1", orderTipBean.getData().getOrder().getBuyerSelectedSellerPayType());
        intent.putExtra("fastTrade", true);
        startActivity(intent);
    }

    /**
     * 获取支付方式
     * 支付方式: 1 银行卡, 2 微信, 3 支付宝
     */
    private void getPayAccount() {
        Map<String, String> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        params.put("type", "0");
        OkhttpManager.postAsync(UrlConstants.openList, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    CardInfosEntity orderTipBean = new Gson().fromJson(result, CardInfosEntity.class);
                    if (orderTipBean.getCode() == 0) {
                        //支持同一类型多个账号
                        OneClickBuyHelper.showPopupWindow(getActivity(), orderTipBean, FastTradeFragment.this);
//                        showPopupWindow(orderTipBean);
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    /**
     * 更新用户信息，刷新界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshInfo(Event.refreshInfo refreshInfo) {
        //清空数据
        if (et_buy_money != null) {
            et_buy_money.setText("");
        }
        getConfig();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.AliVerifySuccess event) {
        if (event.getTradeType() == 3) {//3为快速交易
            String successMsg = getActivity().getString(R.string.str_risk_verify_succeed);
            String clickMsg = getActivity().getString(R.string.str_continue_trading);
            DialogUtils.getInstance().showRiskVerifyButtonDialog(getActivity(), successMsg, R.mipmap.ic_status_success, clickMsg, new DialogUtils.onBnClickListener() {
                @Override
                public void onLiftClick(AlertDialog dialog, View view) {
                }

                @Override
                public void onRightClick(AlertDialog dialog, View view) {
                    AppHelper.dismissDialog(dialog);
                }
            });
        }
    }

    /**
     * 订单  最大最小限制
     */
    private void getConfig() {
        Map<String, String> params = new HashMap<>();
        if (TextUtils.isEmpty(UserInfoManager.getToken())) {
            return;
        }
        params.put("loginToken", UserInfoManager.getToken());
        params.put("type", "0");
        OkhttpManager.postAsync(UrlConstants.order_config, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    orderConfigEntity = new Gson().fromJson(result, OrderConfigEntity.class);
                    if (orderConfigEntity.getCode() == 0) {
                        if (type == BUY) {
                            setHint(String.format(getString(R.string.quantity_minimum_buy), orderConfigEntity.getData().getBuyMin()));
                        } else {
                            setHint(String.format(getString(R.string.quantity_minimum_sell), orderConfigEntity.getData().getBuyMin()));
                        }
                        if (buyOrSellType == COUNT) {
                            setHint(type == BUY ? getString(R.string.cx85) : getString(R.string.cx85_sell));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private int coinId = 2;
    private String selectName = "";
    String beforeEdit = "";

    void initTitle() {
        if (null == C2cStatus.oneClickCoinsId) {
            return;
        }
        tabLayout.removeAllViews();
        tvCoinNames = new TextView[C2cStatus.oneClickCoinsId.size()];
        for (int i = 0; i < C2cStatus.oneClickCoinsId.size(); i++) {
            OtcCoinBean.DataBean dataBean = C2cStatus.oneClickCoinsId.get(i);

            View inflate = View.inflate(mContext, R.layout.tabview, null);
            inflate.setTag(i);
            tvCoinNames[i] = inflate.findViewById(R.id.coinName);
            tvCoinNames[i].setText(dataBean.getCoinName());
            if (tabLayout.getChildCount() == 0) {
                Drawable drawable1 = getResources().getDrawable(R.drawable.line_);
                drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                tvCoinNames[i].setCompoundDrawables(null, null, null, drawable1);
                coinId = dataBean.getCoinId();
                selectName = dataBean.getCoinName();
            }
            inflate.setOnClickListener(view -> {
                changeCoinName((Integer) view.getTag());
            });
            tabLayout.addView(inflate);
        }

    }

    private void changeCoinName(int position) {
        if (C2cStatus.oneClickCoinsId == null || C2cStatus.oneClickCoinsId.size() <= position || tvCoinNames == null ||
                tvCoinNames.length <= position) {
            Logger.getInstance().debug(TAG, "changeCoinName C2cStatus.oneClickCoinsId.size:" +
                    C2cStatus.oneClickCoinsId.size() + " position:" + position + " tvCoinNames.length:" + tvCoinNames.length);
            Logger.getInstance().debug(TAG, "changeCoinName return");
            return;
        }
        OtcCoinBean.DataBean dataBean = C2cStatus.oneClickCoinsId.get(position);
        coinId = dataBean.getCoinId();
        selectName = dataBean.getCoinName();
        avgPrice();
        Drawable drawable = getResources().getDrawable(R.drawable.line_2);
        for (int i = 0; i < tabLayout.getChildCount(); i++) {
            TextView childAt = tabLayout.getChildAt(i).findViewById(R.id.coinName);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            childAt.setCompoundDrawables(null, null, null, drawable);
        }
        Drawable drawable1 = getResources().getDrawable(R.drawable.line_);
        drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
        tvCoinNames[position].setCompoundDrawables(null, null, null, drawable1);
        //重置数据
        et_buy_money.setText("");
        if (buyOrSellType == COUNT) {
            setbuyType();
        }
    }

    void payOrder(String orderId) {
        Map<String, String> params = new HashMap<>();
        params.put("orderId", orderId);
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        DialogManager.INSTANCE.showProgressDialog(getActivity());
        OkhttpManager.postAsync(UrlConstants.pay_order, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                DialogManager.INSTANCE.dismiss();
                SnackBarUtils.ShowRed(getActivity(), errorMsg);
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                try {
                    PayOrderInfoBean payOrderInfoBean = new Gson().fromJson(result, PayOrderInfoBean.class);
                    if (payOrderInfoBean.getCode() == 0) {
                        C2cStatus.payOrderInfoBean = payOrderInfoBean;
                        toPay(payOrderInfoBean);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void avgPrice() {
        Map<String, String> params = new HashMap<>();
        params.put("advertisementType", String.valueOf(type));
        params.put("coinId", String.valueOf(coinId));
        OkhttpManager.postAsync(UrlConstants.avgAdvertisementPrice, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                SnackBarUtils.ShowRed(getActivity(), errorMsg);
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    AvgEntity avgEntity = new Gson().fromJson(result, AvgEntity.class);
                    if (avgEntity.getCode() == 0 && avgEntity.getData() != null) {
                        //05-13 11:45:28.264 W/System.err(20140): java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String huolongluo.byw.byw.ui.oneClickBuy.bean.AvgEntity$DataBean.getAvgPrice()' on a null object reference
                        //05-13 11:45:28.264 W/System.err(20140):         at huolongluo.byw.byw.ui.oneClickBuy.FastTradeFragment$10.requestSuccess(FastTradeFragment.java:913)
                        //05-13 11:45:28.264 W/System.err(20140):         at huolongluo.byw.util.OkhttpManager$6.run(OkhttpManager.java:339)
                        des_price.setText(String.format(getString(R.string.unit_price), avgEntity.getData().getAvgPrice(), avgEntity.getData().getCoinName()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void validate() {
        pressButon();
    }

    void pressButon() {
        if (!check1()) {
            return;
        }
    }

    boolean check1() {
        if (!UserInfoManager.isLogin()) {//未登录
            startActivity(LoginActivity.class);
            return false;
        } else if (!UserInfoManager.getUserInfo().isBindMobil()) {//未绑定手机号
            DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.as20), getString(R.string.as21), getString(R.string.as22));
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
                    Intent intent = new Intent(getActivity(), BindPhoneActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.putExtra("isBindGoogle", UserInfoManager.getUserInfo().isGoogleBind());
                    intent.putExtra("isBindTelephone", false);
                    intent.putExtra("fromToc", true);
                    startActivity(intent);
                }
            });
            return false;
        } else if (UserInfoManager.getUserInfo().getIsHasTradePWD() != 1) {//没有交易密码
            DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.as23), getString(R.string.as25), getString(R.string.as24));
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
                    Intent intent = new Intent(getActivity(), SetChangePswActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("setChangePswTitle", getString(R.string.as26));
                    bundle.putBoolean("isBindGoogle", UserInfoManager.getUserInfo().isGoogleBind());
                    bundle.putBoolean("isBindTelephone", true);
                    bundle.putBoolean("fromToc", true);
                    intent.putExtra("bundle", bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
            });
            return false;
        } else if (!IdentityVerifyHelper.getInstance().isOtcIdentityOk(getActivity())) {//没有实名认证
            return false;
        } else if (!secondCheck()) {//风控校验
            return false;
        } else if (UserInfoManager.getOtcUserInfoBean().getData().getOtcUserLevel() == 2) {
            MToast.show(getActivity(), getString(R.string.as30), 1);
            if (TextUtils.isEmpty(UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getNickname())) {
                Intent intent = new Intent(getActivity(), OtcSetNickActivity.class);
                intent.putExtra("fromToc", true);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), OtcSetUserInfoActivity.class);
                intent.putExtra("nickName", UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getNickname());
                intent.putExtra("fromToc", true);
                startActivity(intent);
            }
            return false;
        } else {
            return true;
        }
    }

    boolean secondCheck() {
        if (null == UserInfoManager.getOtcUserInfoBean()) {
            return false;
        }
        if (!UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().isAllowOtc()) {
            DialogUtils.getInstance().showOneButtonDialog(getActivity(), getString(R.string.as40), getString(R.string.as16));
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
                }
            });
            return false;
        } else if (UserInfoManager.getOtcUserInfoBean().getData().isMerch()) {
            return true;//如果是商家，不需要判断风控
        } else if (!UserInfoManager.getOtcUserInfoBean().getData().isOpenLimit()) {
            return true;
        } else {
            return true;
        }
    }

    private PopupWindow popupWindow;

    void showPopup() {
        if (popupWindow == null) {
            View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.otc_meun_pop_view, null, false);
            popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            LinearLayout linearLayout1 = contentView.findViewById(R.id.linearLayout1);
            LinearLayout linearLayout2 = contentView.findViewById(R.id.linearLayout2);
            LinearLayout linearLayout3 = contentView.findViewById(R.id.linearLayout3);
            LinearLayout linearLayout4 = contentView.findViewById(R.id.linearLayout4);
            LinearLayout linearLayout5 = contentView.findViewById(R.id.linearLayout5);
            ImageView imageview1 = contentView.findViewById(R.id.imageview1);
            ImageView imageview2 = contentView.findViewById(R.id.imageview2);
            ImageView imageview3 = contentView.findViewById(R.id.imageview3);
            ImageView imageview4 = contentView.findViewById(R.id.imageview4);
            View.OnTouchListener onTouchListener = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        switch (v.getId()) {
                            case R.id.linearLayout1:
                                v.setBackgroundResource(R.drawable.otcmenu_bg3);
                                imageview1.setImageResource(R.mipmap.hu1);
                                break;
                            case R.id.linearLayout2:
                                v.setBackgroundColor(getResources().getColor(R.color.ff5e568a));
                                imageview2.setImageResource(R.mipmap.order1);
                                break;
                            case R.id.linearLayout3:
                                v.setBackgroundColor(getResources().getColor(R.color.ff5e568a));
                                imageview3.setImageResource(R.mipmap.type1);
                                break;
                            case R.id.linearLayout4:
                                v.setBackgroundResource(R.drawable.otcmenu_bg4);
                                imageview4.setImageResource(R.mipmap.gggl1);
                                break;
                            case R.id.linearLayout5:
                                v.setBackgroundResource(R.drawable.otcmenu_bg4);
                                //    imageview4.setImageResource(R.mipmap.gggl1);
                                break;
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        switch (v.getId()) {
                            case R.id.linearLayout1:
                                v.setBackgroundResource(R.drawable.otcmenu_bg1);
                                imageview1.setImageResource(R.mipmap.hz2);
                                break;
                            case R.id.linearLayout2:
                                v.setBackgroundColor(getResources().getColor(R.color.ff4d447f));
                                imageview2.setImageResource(R.mipmap.order2);
                                break;
                            case R.id.linearLayout3:
                                v.setBackgroundColor(getResources().getColor(R.color.ff4d447f));
                                imageview3.setImageResource(R.mipmap.type2);
                                break;
                            case R.id.linearLayout4:
                                v.setBackgroundColor(getResources().getColor(R.color.ff4d447f));
                                imageview4.setImageResource(R.mipmap.gggl2);
                                break;
                            case R.id.linearLayout5:
                                v.setBackgroundResource(R.drawable.otcmenu_bg2);
                                //imageview4.setImageResource(R.mipmap.gggl2);
                                break;
                        }
                    }
                    return false;
                }
            };
            linearLayout1.setOnTouchListener(onTouchListener);
            linearLayout2.setOnTouchListener(onTouchListener);
            linearLayout3.setOnTouchListener(onTouchListener);
            linearLayout4.setOnTouchListener(onTouchListener);
            linearLayout5.setOnTouchListener(onTouchListener);
            linearLayout1.setOnClickListener(this);
            linearLayout2.setOnClickListener(this);
            linearLayout3.setOnClickListener(this);
            linearLayout4.setOnClickListener(this);
            linearLayout5.setOnClickListener(this);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setTouchable(true);
            popupWindow.showAsDropDown(menu_iv, 0, Util.dp2px(getActivity(), 10));
        } else {
            popupWindow.showAsDropDown(menu_iv, 0, Util.dp2px(getActivity(), 10));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_iv:
                showPopup();
                break;
            case R.id.linearLayout1://划转   ,判断是否及时
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                if (!UserInfoManager.isLogin()) {
                    startActivity(LoginActivity.class);
                    return;
                }
                if (!check1()) {
                    return;
                }
                //JIRA:COIN-1721
                //业务控制条件：是否禁止提币 0.可以提币， 1.禁止提币
                if (!AppHelper.isNoWithdrawal()) {//该用户已被禁止提币
                    DialogUtils.getInstance().showOneButtonDialog(getActivity(), getString(R.string.no_transfer), getString(R.string.as16));
                    return;
                }
                isBlack(1);
                break;
            case R.id.linearLayout2://用户订单管理
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                if (!UserInfoManager.isLogin()) {
                    startActivity(LoginActivity.class);
                    return;
                }
                if (!check()) {
                    return;
                }
                isBlack(2);
                break;
            case R.id.linearLayout3://商户订单管理
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                if (!UserInfoManager.isLogin()) {
                    startActivity(LoginActivity.class);
                    return;
                }
                if (!check()) {
                    return;
                }
                isBlack(3);
                break;
            case R.id.linearLayout4://支付设置
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                if (!UserInfoManager.isLogin()) {
                    startActivity(LoginActivity.class);
                    return;
                }
                if (!check()) {
                    return;
                }
                isBlack(4);
                break;
            case R.id.linearLayout5://基本设置
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                if (!UserInfoManager.isLogin()) {
                    startActivity(LoginActivity.class);
                    return;
                }
                if (!check()) {
                    return;
                }
                isBlack(5);
                break;
            case R.id.btn_reLoad://网络错误，从新加载
                break;
        }
    }

    private void isBlack(int type) {
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params.put("loginToken", SPUtils.getLoginToken());
        OkhttpManager.postAsync(UrlConstants.otcIsBlack, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    Logger.getInstance().debug(TAG, "url: " + UrlConstants.getAliVerifySdkToken + " result:" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        switch (type) {
                            case 1:
                                AccountTransferActivity.Companion.launch(getActivity(), null,null,null,null,
                                        false,null);
                                break;
                            case 2:
                                startActivity(OtcOrderManagerActivity.class);
                                break;
                            case 3:
                                try {
                                    if (UserInfoManager.getOtcUserInfoBean().getData().isMerch()) {
                                        Intent intent = new Intent(getActivity(), OtcOrderShopsManagerActivity.class);
                                        // intent.putExtra("position",1);
                                        startActivity(intent);
                                        // startActivity(OtcOrderShopsManagerActivity.class);
                                    } else {
                                        MToast.show(getContext(), getString(R.string.qq16), 2);
                                    }
                                } catch (Exception e) {
                                    MToast.show(getActivity(), getString(R.string.qq17), 1);
                                    get_base_userinfo();
                                }
                                break;
                            case 4:
                                startActivity(PaymentAccountActivityNew.class);
                                break;
                            case 5:
                                if (UserInfoManager.getOtcUserInfoBean() != null) {
                                    if (TextUtils.isEmpty(UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getNickname())) {
                                        startActivity(OtcSetNickActivity.class);
                                    } else {
                                        if (UserInfoManager.getOtcUserInfoBean().getData().isMerch()) {
                                            Intent intent = new Intent(getContext(), SellerInfoActivity.class);
                                            intent.putExtra("userId", UserInfoManager.getUserInfo().getFid() + "");
                                            startActivity(intent);
                                        } else {
                                            MToast.show(getContext(), getString(R.string.qq18), 2);
                                        }
                                    }
                                }
                                break;
                        }
                    } else if (code == 124) {
                        FaceVerifyHelper.getInstance().aliVerifyBlack(getContext(), jsonObject.getString("value"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void get_base_userinfo() {
        if (TextUtils.isEmpty(UserInfoManager.getToken())) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.get_base_userinfo, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    OtcUserInfoBean otcUserInfoBean = new Gson().fromJson(result, OtcUserInfoBean.class);
                    otcUserInfoBean.refreshAsset();
                    if (otcUserInfoBean.getCode() == 0) {
                        UserInfoManager.setOtcUserInfoBean(otcUserInfoBean);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 权限校验
    boolean check() {
        if (!UserInfoManager.isLogin()) {//未登录
            startActivity(LoginActivity.class);
            return false;
        } else if (UserInfoManager.getUserInfo() != null && !UserInfoManager.getUserInfo().isBindMobil()) {//未绑定手机号
            DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.qq69), getString(R.string.qq68), getString(R.string.qq25));
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
                    Intent intent = new Intent(getContext(), BindPhoneActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.putExtra("isBindGoogle", UserInfoManager.getUserInfo().isGoogleBind());
                    intent.putExtra("isBindTelephone", false);
                    intent.putExtra("fromToc", true);
                    startActivity(intent);
                }
            });
            return false;
        } else if (UserInfoManager.getUserInfo() != null && UserInfoManager.getUserInfo().getIsHasTradePWD() != 1) {//没有交易密码
            DialogUtils.getInstance().showTwoButtonDialog(getActivity(), getString(R.string.as12), getString(R.string.as1), getString(R.string.qq26));
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
                    Intent intent = new Intent(getActivity(), SetChangePswActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("setChangePswTitle", getString(R.string.qq27));
                    bundle.putBoolean("isBindGoogle", UserInfoManager.getUserInfo().isGoogleBind());
                    bundle.putBoolean("isBindTelephone", true);
                    bundle.putBoolean("fromToc", true);
                    intent.putExtra("bundle", bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
            });
            return false;
        } else if (!IdentityVerifyHelper.getInstance().isOtcIdentityOk(getActivity())) {//没有实名认证
            return false;
        } else if (UserInfoManager.getOtcUserInfoBean() != null && UserInfoManager.getOtcUserInfoBean().getData() != null && UserInfoManager.getOtcUserInfoBean().getData().getOtcUser() != null && !UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().isAllowOtc()) {
            DialogUtils.getInstance().showOneButtonDialog(getActivity(), getString(R.string.as3), getString(R.string.qq29));
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
                }
            });
            return false;
        } else if (UserInfoManager.getOtcUserInfoBean() != null && UserInfoManager.getOtcUserInfoBean().getData() != null && UserInfoManager.getOtcUserInfoBean().getData().getOtcUserLevel() == 2) {
            MToast.show(getContext(), getString(R.string.qq30), 1);
            if (TextUtils.isEmpty(UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getNickname())) {
                Intent intent = new Intent(getActivity(), OtcSetNickActivity.class);
                intent.putExtra("fromToc", true);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), OtcSetUserInfoActivity.class);
                intent.putExtra("nickName", UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getNickname());
                intent.putExtra("fromToc", true);
                startActivity(intent);
            }
            return false;
        } else {
            return true;
        }
    }
}
