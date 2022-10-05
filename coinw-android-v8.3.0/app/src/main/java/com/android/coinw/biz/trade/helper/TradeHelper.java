package com.android.coinw.biz.trade.helper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.coinw.model.result.AssetResult;
import com.android.legend.ui.login.LoginActivity;
import com.blankj.utilcodes.utils.ToastUtils;
import com.google.android.material.appbar.AppBarLayout;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.LimitedTimeTipsResult;
import huolongluo.byw.reform.home.activity.NewsWebviewActivity;
import huolongluo.byw.reform.home.activity.kline2.KLineActivity;
import huolongluo.byw.reform.home.activity.kline2.common.KLineEntity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.CurrencyPairUtil;
import huolongluo.byw.util.MathHelper;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.bywx.utils.DoubleUtils;
public final class TradeHelper {
    public static String getAssetTotalVal(AssetResult result, int fcountNumber) {
        if (result == null || result.asset == null || result.asset.coin == null) {
            return "--";
        }
        return NorUtils.NumberFormatd(fcountNumber).format(DoubleUtils.parseDouble(result.asset.totalAsset + ""));
    }

    /**
     * 交易页计算预计可买
     * @param buyPrice 买入价格
     * @param totalPrice 可用价格
     * @param id 通过id获取手续费
     * @param fcountNumber 采用数量精度控制
     * @return
     */
    public static String getExpectCanBuy(String buyPrice,String totalPrice,long id,int fcountNumber) {
        if(!UserInfoManager.isLogin()){
            return "--";
        }
        double buyPriceD=DoubleUtils.parseDouble(buyPrice);
        double totalPriceD=DoubleUtils.parseDouble(totalPrice);
        if (TextUtils.isEmpty(buyPrice) || TextUtils.isEmpty(totalPrice)||buyPrice.equals("--")||totalPrice.equals("--")
        ||buyPriceD==0||totalPriceD==0) {
            return "0";
        }
        double feeRate= CurrencyPairUtil.getFeeRateById(id);
        return NorUtils.NumberFormat(fcountNumber, RoundingMode.DOWN).format(
                MathHelper.div(totalPriceD,MathHelper.mul((feeRate+1d),buyPriceD)));
    }

    /**
     * 交易页计算预计可卖
     * @param sellPrice 卖出价
     * @param totalNum 可用数量
     * @param fcountPrice 价格精度
     * @return
     */
    public static String getExpectCanSell(String sellPrice, String totalNum,int fcountPrice) {
        if(!UserInfoManager.isLogin()){
            return "--";
        }
        if (TextUtils.isEmpty(sellPrice) || TextUtils.isEmpty(totalNum)||sellPrice.equals("--")||totalNum.equals("--")) {
            return "0";
        }
        return NorUtils.NumberFormat(fcountPrice, RoundingMode.DOWN).format(MathHelper.mul(sellPrice,totalNum));
    }

    public static boolean checkLogin(Activity atv) {
        if (atv == null) {
            //TODO 处理异常情况
            return false;
        }
        if (!UserInfoManager.isLogin()) {
            DialogUtils.getInstance().showTwoButtonDialog(atv, atv.getString(R.string.ee58), atv.getString(R.string.qe70), atv.getString(R.string.qe71));
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
                    atv.startActivity(new Intent(atv, LoginActivity.class));
                }
            });
            return false;
        } else {
            return true;
        }
    }

    public static void scrollTop(AppBarLayout appBarLayout) {
        if (appBarLayout == null) {
            //TODO 处理异常情况
            return;
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).getBehavior();
        if (behavior instanceof AppBarLayout.Behavior) {
            AppBarLayout.Behavior appBarLayoutBehavior = (AppBarLayout.Behavior) behavior;
            if (true) {
                appBarLayoutBehavior.setTopAndBottomOffset(0); //快熟滑动到顶部
            } else {
                int hight = appBarLayout.getHeight();
                appBarLayoutBehavior.setTopAndBottomOffset(-hight);//快速滑动实现吸顶效果
            }
        }
    }

    /**
     * 统一的k线跳转
     * @param context
     * @param entity
     */
    public static void gotoKLine(Context context, KLineEntity entity){
        if(entity==null||entity.getId()<0){
            return;
        }
        KLineActivity.launch(context,entity);
    }

    public static void gotoRecharge(Activity atv) {//充值
        if (atv == null) {
            //TODO 处理异常情况
            return;
        }
        MainActivity.self.gotoFinance();
        Intent intent = new Intent(atv, MainActivity.class);
        atv.startActivity(intent);
    }

    /********************************************/
    private void saveSelf(Activity atv, String id) {
        if (!TradeHelper.checkLogin(atv)) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("trademId", id + "");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
//        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.USER_SELF_SAVE, params, new OkhttpManager.DataCallBack() {
//            @Override
//            public void requestFailure(Request request, Exception e, String errorMsg) {
//                SnackBarUtils.ShowRed(atv , atv.getString(R.string.qe52));
//            }
//
//            @Override
//            public void requestSuccess(String result) {
//                try {
//                    Log.d("用户添加自选", result);
//                    JSONObject jsonObject = new JSONObject(result);
//                    int code = jsonObject.getInt("code");
//                    String value = jsonObject.getString("value");
//                    if (code == 1) {
//                        CameraMainActivity.selfselection = "1";
//                        CameraMainActivity.currentCoinSeleState = 1;
//                        MarketDataPresent.getSelf().setSelect(Integer.valueOf(id), 1);
//                        isSelf = "1";
//                        starIV.setImageResource(R.mipmap.ic_star_pressed);
//                        //更新自选列表
//                        MarketDataPresent.exchangeMarket(1);
//                        SnackBarUtils.ShowBlue(getActivity(), getString(R.string.qe53));
//                    } else {
//                        SnackBarUtils.ShowRed(getActivity(), " " + value);
//                    }
//                } catch (JSONException e) {
//                    SnackBarUtils.ShowRed(getActivity(), getString(R.string.qe54));
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    public static boolean checkActivity(Activity atv) {
        if (atv == null || atv.isFinishing() || atv.isDestroyed()) {
            //TODO 处理异常情况
            return false;
        }
        return true;
    }

    public static void openLimitedTimeTips(Activity atv, String bchUrl) {
        //打开zdesk
        LimitedTimeTipsResult.LimitedTimeTips limitedTimeTips = TradeDataHelper.getInstance().getLimitedTimeTips();
        if (TextUtils.isEmpty(bchUrl) && (TradeDataHelper.getInstance().getLimitedTimeTips() == null || TextUtils.isEmpty(limitedTimeTips.tradeRuleUrl))) {
            ToastUtils.showShortToast( atv.getString(R.string.service_expec));
            return;
        }
        try {
            if (!checkActivity(atv)) {
                //TODO 处理异常情况
                return;
            }
            Intent intent = new Intent(atv, NewsWebviewActivity.class);
            if (TextUtils.isEmpty(bchUrl)) {
                intent.putExtra("url", limitedTimeTips.tradeRuleUrl);
            } else {
                intent.putExtra("url", bchUrl);
            }
            intent.putExtra("token", UserInfoManager.getToken());
            intent.putExtra("useH5Title", true);
            atv.startActivity(intent);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    public static void showLimitedTimeTips(Activity atv, String msg) {
        if (!checkActivity(atv)) {
            //TODO 处理异常情况
            return;
        }
        LimitedTimeTipsResult.LimitedTimeTips limitedTimeTips = TradeDataHelper.getInstance().getLimitedTimeTips();
        if (TradeDataHelper.getInstance().getLimitedTimeTips() == null || TextUtils.isEmpty(limitedTimeTips.tradeRuleUrl)) {
            ToastUtils.showShortToast( atv.getString(R.string.service_expec));
            return;
        }
        CountDownTimer timer = new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                DialogUtils.getInstance().dismiss();
            }
        }.start();
        //TODO 待处理
        DialogUtils.getInstance().showTwoButtonDialog(atv, msg, atv.getString(R.string.iknow1), atv.getString(R.string.str_click_view_details), v -> {
            timer.cancel();
            //打开zdesk
            LimitedTimeTipsResult.LimitedTimeTips limitedTimeTips1 = TradeDataHelper.getInstance().getLimitedTimeTips();
            if (TradeDataHelper.getInstance().getLimitedTimeTips() == null || TextUtils.isEmpty(limitedTimeTips1.tradeRuleUrl)) {
                ToastUtils.showShortToast( atv.getString(R.string.service_expec));
                return;
            }
            try {
                if (!checkActivity(atv)) {
                    //TODO 处理异常情况
                    return;
                }
                Intent intent = new Intent(atv, NewsWebviewActivity.class);
                intent.putExtra("url", limitedTimeTips1.tradeRuleUrl);
                intent.putExtra("token", UserInfoManager.getToken());
                //intent.putExtra("hideTitle", true);
                atv.startActivity(intent);
            } catch (Throwable t) {
                Logger.getInstance().error(t);
            }
        });
    }
}
