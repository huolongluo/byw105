package huolongluo.byw.reform.c2c.oct.fragment;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.legend.model.enumerate.transfer.TransferAccount;
import com.android.legend.ui.login.LoginActivity;
import com.android.legend.ui.transfer.AccountTransferActivity;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.bean.UserInfoBean;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.share.Share;
import huolongluo.byw.byw.ui.activity.bindphone.BindPhoneActivity;
import huolongluo.byw.byw.ui.activity.setchangepsw.SetChangePswActivity;
import huolongluo.byw.byw.ui.oneClickBuy.C2cStatus;
import huolongluo.byw.helper.FaceVerifyHelper;
import huolongluo.byw.helper.IdentityVerifyHelper;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.base.BaseFragment;
import huolongluo.byw.reform.c2c.oct.activity.OtcFiltrateActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcOrderManagerActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcSetNickActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcSetUserInfoActivity;
import huolongluo.byw.reform.c2c.oct.activity.PaymentAccountActivityNew;
import huolongluo.byw.reform.c2c.oct.activity.SellerInfoActivity;
import huolongluo.byw.reform.c2c.oct.activity.sellactivity.OtcOrderShopsManagerActivity;
import huolongluo.byw.reform.c2c.oct.activity.sellactivity.OtcUserSellOtherPayedActivity;
import huolongluo.byw.reform.c2c.oct.bean.C2cIsShowBean;
import huolongluo.byw.reform.c2c.oct.bean.OrderTipBean;
import huolongluo.byw.reform.c2c.oct.bean.OtcCoinBean;
import huolongluo.byw.reform.c2c.oct.bean.OtcUserInfoBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.Util;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.bywx.helper.AppHelper;
import okhttp3.Request;
/**
 * Created by Administrator on 2019/3/12 0012.
 * 买币交易的自选交易，buy和sell的主fragment
 */
public class OtcFragment extends BaseFragment implements View.OnClickListener {
    ImageView filtrate_iv;
    FrameLayout framelayout;
    TextView buy_tv;
    TextView sell_tv;
    ImageView ivBack;
    SlidingTabLayout tablayout;
    private String[] mTitles;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    ImageView menu_iv;
    private OtcCoinBean otcCoinBean;
    private OtcFragmentBuy otcFragmentBuy;
    private OtcFragmentSell otcFragmentSell;
    private RelativeLayout title;
    private TextView otc_title;
    private TextView c2cTrade;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_otc;
    }

    Fragment fragment[];
    private PopupWindow popupWindow;
    TextView c2c_select_tv;

    @Override
    protected void onCreatedView(View rootView) {
        buy_tv = fv(R.id.buy_tv);
        sell_tv = fv(R.id.sell_tv);
        tablayout = fv(R.id.tablayout);
        menu_iv = fv(R.id.menu_iv);
        filtrate_iv = fv(R.id.filtrate_iv);
        c2c_select_tv = fv(R.id.c2c_select_tv);
        c2cTrade = fv(R.id.c2cTrade);
        ivBack = fv(R.id.ivBack);
        c2cTrade.setOnClickListener(this);
        buy_tv.setOnClickListener(this);
        sell_tv.setOnClickListener(this);
        menu_iv.setOnClickListener(this);
        filtrate_iv.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        otc_title = fv(R.id.otc_title);
        title = fv(R.id.title);
        otcFragmentBuy = new OtcFragmentBuy();
        viewClick(c2c_select_tv, v -> {
            ((OtcActivity) getActivity()).selectFirstTradeFragment();
        });
        getChildFragmentManager().beginTransaction().add(R.id.framelayout, otcFragmentBuy).commit();
        //  switchs(1);
        get_base_userinfo();
        refreshConfigList();

        if (BaseApp.FIST_OPEN_OTC == true) {
            otcUserLimit();
        }

        if (C2cStatus.isShow) {
            c2cTrade.setVisibility(View.GONE);
        } else {
            c2cTrade.setVisibility(View.VISIBLE);
        }
        getUserInfo();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (C2cStatus.isShow) {
            c2cTrade.setVisibility(View.GONE);
        } else {
            c2cTrade.setVisibility(View.VISIBLE);
        }
        Logger.getInstance().debug(TAG,"isShowFast:"+C2cStatus.isShowFast);
        if (C2cStatus.isShowFast) {//开启
            title.setVisibility(View.VISIBLE);
            otc_title.setVisibility(View.GONE);
        } else {
            title.setVisibility(View.GONE);
            otc_title.setVisibility(View.VISIBLE);
        }
        if (BaseApp.getSelf() != null && !BaseApp.getSelf().isConnected()) {
            return;
        }
        if (UserInfoManager.isLogin()) {
            get_base_userinfo();
            order_tip();
        }

    }


    private void otcUserLimit() {
        if (!UserInfoManager.isLogin()) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.otc_user_limit, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    if (code == 0) {
                        boolean isNewUser = jsonObject1.getBoolean("isNewUser");
                        if (isNewUser) {
                            boolean isOpenLimit = jsonObject1.getBoolean("isOpenLimit");
                            if (isOpenLimit) {
                                DialogUtils.getInstance().showOtcInfoDialog(requireContext());
                            }
                        }
                        BaseApp.FIST_OPEN_OTC = false;
                        Share.get().setisFirstOpenOtc(true);
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    //查询订单状态
    void order_tip() {
        Map<String, String> params = new HashMap<>();
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.order_tip, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    OrderTipBean orderTipBean = new Gson().fromJson(result, OrderTipBean.class);
                    if (orderTipBean.getCode() == 0) {
                        orderTip(orderTipBean);
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    void orderTip(OrderTipBean orderTipBean) {
        if (orderTipBean.getData().getType() == 1) {
            String msg = "";
            if (orderTipBean.getData().getOtcOrderVo().getPayType() == 1) {
                msg = orderTipBean.getData().getOtcOrderVo().getDealUserNickname() + getString(R.string.qq31) + orderTipBean.getData().getOtcOrderVo().getAccount().substring(orderTipBean.getData().getOtcOrderVo().getAccount().length() - 4) + getString(R.string.qq7);
            } else if (orderTipBean.getData().getOtcOrderVo().getPayType() == 2) {
                msg = orderTipBean.getData().getOtcOrderVo().getDealUserNickname() + getString(R.string.qq8);
            } else {
                msg = orderTipBean.getData().getOtcOrderVo().getDealUserNickname() + getString(R.string.qq9);
            }
            DialogUtils.getInstance().showOtcOrderTipDialog(getActivity(), msg, getString(R.string.qq11), getString(R.string.qq10));
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
                    //去往已付款界面
                    Intent intent = new Intent(getAContext(), OtcUserSellOtherPayedActivity.class);
                    intent.putExtra("orderId", orderTipBean.getData().getOtcOrderVo().getId());
                    startActivity(intent);
                }
            });
        } else if (orderTipBean.getData().getType() == 2) {
            DialogUtils.getInstance().showOtcOrderTipDialog(getActivity(), getString(R.string.as4), getString(R.string.qq32), getString(R.string.qq12));
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
                    //去往商家订单界面
                    Intent intent = new Intent(getActivity(), OtcOrderShopsManagerActivity.class);
                    intent.putExtra("position", 1);
                    startActivity(intent);
                }
            });
        } else if (orderTipBean.getData().getType() == 3) {
            DialogUtils.getInstance().showOtcOrderTipDialog(getActivity(), getString(R.string.as5), getString(R.string.qq33), getString(R.string.qq13));
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
                    //去往商家订单界面
                    startActivity(OtcOrderShopsManagerActivity.class);
                }
            });
        } else if (orderTipBean.getData().getType() == 4) {
            DialogUtils.getInstance().showOtcOrderTipDialog(getActivity(), getString(R.string.as6), getString(R.string.qq34), getString(R.string.qq14));
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
                    //去往商家订单界面
                    startActivity(OtcOrderShopsManagerActivity.class);
                }
            });
        } else if (orderTipBean.getData().getType() == 5) {
            DialogUtils.getInstance().showOtcOrderTipDialog(getActivity(), getString(R.string.as7), getString(R.string.qq35), getString(R.string.qq15));
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
                    //去往已付款界面
                    Intent intent = new Intent(getAContext(), OtcUserSellOtherPayedActivity.class);
                    intent.putExtra("orderId", orderTipBean.getData().getOtcOrderVo().getId());
                    startActivity(intent);
                }
            });
        }
    }

    double currenta = 0;
    int totalOrder = 2;
    Map<String, Integer> map = new HashMap<>();

    void serr() {
        map.put((++totalOrder) + "", 0);
        map.put((++totalOrder) + "", 0);
        map.put((++totalOrder) + "", 0);
        map.put((++totalOrder) + "", 0);
        map.put((++totalOrder) + "", 0);
        map.put((++totalOrder) + "", 0);
        map.put((++totalOrder) + "", 0);
        map.put((++totalOrder) + "", 0);
        for (int i = 0; i < 30; i++) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getValue() < 30) {
                    int a = entry.getValue();
                    entry.setValue(++a);
                    currenta = currenta + 0.4;
                }
            }
            if (currenta >= 10) {
                map.put((++totalOrder) + "", 0);
                currenta = currenta - 9.6;
            }
            if (currenta >= 10) {
                map.put((++totalOrder) + "", 0);
                currenta = currenta - 9.6;
            }
            //currenta = currenta + 2;
            double currenttota = 0;
            for (int entry : map.values()) {
                if (entry < 30) {
                    currenttota = currenttota + ((30 - entry) * 0.4);
                }
            }
            currenttota = currenttota + currenta;
            Log.e("dddddddddd", "天数" + i + ":   个数 =  " + currenttota);
        }
    }

    void switchs(int page) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (page == 0) {
            buy_tv.setTextColor(getAContext().getResources().getColor(R.color.white));
            buy_tv.setBackgroundResource(R.drawable.market_custom_bg1);
            sell_tv.setBackgroundResource(R.color.transparent);
            sell_tv.setTextColor(getAContext().getResources().getColor(R.color.ff918BAE));
            if (otcFragmentSell != null) {
                transaction.hide(otcFragmentSell);
            }
            transaction.show(otcFragmentBuy);
        } else {
            sell_tv.setTextColor(getAContext().getResources().getColor(R.color.white));
            sell_tv.setBackgroundResource(R.drawable.market_custom_bg1);
            buy_tv.setBackgroundResource(R.color.transparent);
            buy_tv.setTextColor(getAContext().getResources().getColor(R.color.ff918BAE));
            transaction.hide(otcFragmentBuy);
            if (otcFragmentSell == null) {
                otcFragmentSell = new OtcFragmentSell();
                transaction.add(R.id.framelayout, otcFragmentSell);
            } else {
                transaction.show(otcFragmentSell);
            }
        }
        transaction.commit();
    }

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
                if (!check()) {
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
            case R.id.linearLayout5://商家管理
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
            case R.id.filtrate_iv://筛选
                startActivityForResult(new Intent(getAContext(), OtcFiltrateActivity.class), 101);
                break;
            case R.id.btn_reLoad://网络错误，从新加载
                break;
            case R.id.buy_tv:
                switchs(0);
                break;
            case R.id.sell_tv:
                switchs(1);
                break;
            case R.id.c2cTrade:
                ((OtcActivity)getActivity()).selectC2cFragment();
                break;
            case R.id.ivBack:
                requireActivity().finish();
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
                                AccountTransferActivity.Companion.launch(getActivity(), TransferAccount.OTC.getValue(),
                                        TransferAccount.WEALTH.getValue(),null,null,false,null);
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

    //获取用户信息
    private void getUserInfo() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        if (!UserInfoManager.isLogin()) {
            return;
        }
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GET_USER_INFO, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                Log.d("个人信息1", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    Log.e("校验token", "code= : " + result);
                    if (code == 0) {
                        JSONObject object = jsonObject.getJSONObject("userInfo");
                        UserInfoManager.setToken(SPUtils.getLoginToken());
                        UserInfoBean userInfoBean = new Gson().fromJson(object.toString(), UserInfoBean.class);
                        if (userInfoBean != null && userInfoBean.getCode() == 0) {
                            UserInfoManager.setUserInfoBean(userInfoBean);
                            //登录IM
                            BaseApp.getSelf().loginIM();
                            //
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == 104) {
            if (data != null) {
                String moneyNum = data.getStringExtra("moneyNum");
                boolean bankStatus = data.getBooleanExtra("bankStatus", false);
                boolean alipayStatus = data.getBooleanExtra("alipayStatus", false);
                boolean wechatStatus = data.getBooleanExtra("wechatStatus", false);
                EventBus.getDefault().post(new Event.OtcFlitrade(bankStatus, alipayStatus, wechatStatus, moneyNum));
            }
        }
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

    private void refreshConfigList() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.getConfig, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                Logger.getInstance().debug(TAG, "url: " + UrlConstants.DOMAIN + UrlConstants.getConfigList + " errorMsg: " + errorMsg);
                Logger.getInstance().error(e);
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void requestSuccess(String result) {
                Logger.getInstance().debug(TAG, "url: " + UrlConstants.DOMAIN + UrlConstants.getConfigList + " result: " + result);
                C2cIsShowBean c2cIsShowBean = GsonUtil.json2Obj(result, C2cIsShowBean.class);
                if (c2cIsShowBean != null && c2cIsShowBean.getC2C_CLOSE() != null) {
                    C2cStatus.isShow = Boolean.parseBoolean(c2cIsShowBean.getC2C_CLOSE().getValue());
                }
                if (c2cIsShowBean != null && c2cIsShowBean.getOTC_ONE_KEY_OPEN() != null) {
                    C2cStatus.isShowFast = Boolean.parseBoolean(c2cIsShowBean.getOTC_ONE_KEY_OPEN().getValue());
                }
            }
        });
    }

    boolean secondCheck() {
        if (!UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().isAllowOtc()) {
            DialogUtils.getInstance().showOneButtonDialog(getAContext(), getString(R.string.qq20), getString(R.string.qq19));
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
        } else if (!UserInfoManager.getOtcUserInfoBean().getData().isOpenLimit()) {
            return true;
        }
//        else if (UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().isNewUser()) {
//            DialogUtils.getInstance().showTwoButtonDialog(getAContext(), getString(R.string.qq23), getString(R.string.qq22), getString(R.string.qq21));
//            DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
//                @Override
//                public void onLiftClick(AlertDialog dialog, View view) {
//                    if (dialog != null) {
//                        dialog.dismiss();
//                    }
//                }
//
//                @Override
//                public void onRightClick(AlertDialog dialog, View view) {
//                    if (dialog != null) {
//                        dialog.dismiss();
//                    }
//                    ((CameraMainActivity) getActivity()).otcToC2c();
//                }
//            });
//            return false;
//        }
        else {
            return true;
        }
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
                    Intent intent = new Intent(getAContext(), BindPhoneActivity.class);
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
                    Intent intent = new Intent(getAContext(), SetChangePswActivity.class);
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
        } else if (UserInfoManager.getOtcUserInfoBean() == null) {
            return false;
        } else if (!IdentityVerifyHelper.getInstance().isOtcIdentityOk(getActivity())) {//没有实名认证
            return false;
        } else if (UserInfoManager.getOtcUserInfoBean() != null && UserInfoManager.getOtcUserInfoBean().getData() != null && UserInfoManager.getOtcUserInfoBean().getData().getOtcUser() != null && !UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().isAllowOtc()) {
            DialogUtils.getInstance().showOneButtonDialog(getAContext(), getString(R.string.as3), getString(R.string.qq29));
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
            MToast.show(getAContext(), getString(R.string.qq30), 1);
            if (TextUtils.isEmpty(UserInfoManager.getOtcUserInfoBean().getData().getOtcUser().getNickname())) {
                Intent intent = new Intent(getAContext(), OtcSetNickActivity.class);
                intent.putExtra("fromToc", true);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getAContext(), OtcSetUserInfoActivity.class);
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
