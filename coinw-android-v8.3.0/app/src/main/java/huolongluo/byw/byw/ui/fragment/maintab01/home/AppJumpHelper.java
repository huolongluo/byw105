package huolongluo.byw.byw.ui.fragment.maintab01.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.legend.modular_contract_sdk.coinw.CoinwHyUtils;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.activity.stop_service.StopServiceActivity;
import huolongluo.byw.byw.ui.fragment.maintab04.MineFragment;
import huolongluo.byw.byw.ui.fragment.maintab04.WebViewActivity;
import huolongluo.byw.byw.ui.fragment.maintab05.AllFinanceFragment;
import huolongluo.byw.heyue.ui.TransActionHomeFragment;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.home.activity.NewsWebviewActivity;
import huolongluo.byw.reform.home.activity.WelfareWebviewActivity;
import huolongluo.byw.reform.market.activity.ChongzhiListActivity;
import huolongluo.byw.reform.mine.activity.MoneyManagerActivity;
import huolongluo.byw.reform.mine.activity.PyramidSaleWebViewActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.NumberUtil;
public class AppJumpHelper {
    /**
     * 该功能与首页动态配置功能有冲突，但后台接口服务已经被确认？？？
     */
    private static AppJumpHelper instance = new AppJumpHelper();

    public static AppJumpHelper getInstance() {
        return instance;
    }

    public void gotoTarget(Context context, String portalUrl, String title, boolean hideTitle) {

        //此处根据通知
        String left = "";
        String right = "";
        if (portalUrl.indexOf("app/byb") != -1) {//币赢宝
            gotoWebView(context, portalUrl, title, hideTitle, false);
        } else if (portalUrl.indexOf("app/yqfy") != -1) {//邀请返佣
            startActivity(context, new Intent(context, PyramidSaleWebViewActivity.class));
        } else if (portalUrl.indexOf("app/viewWelfare") != -1) {//福利中心--殊的WebView
            gotoWelfare(context, portalUrl);
        } else if (portalUrl.indexOf("app/viewHbt") != -1) {//HBT特殊处理
            gotoWebView(context, portalUrl, "", false, true);
            //TODO 因合代码后发版内容变更，拆代码的风险很高，故采用加开关来处理
            //TODO 币贷宝待修改
        } else if (portalUrl.indexOf("app/viewCoinLoan") != -1) {//币贷宝
            gotoWebView(context, portalUrl, "", false, true);
        } else if (gotoNativeTarget(context, portalUrl)) {
            gotoNativeTarget(context, portalUrl);
        } else if (CoinwHyUtils.isServiceStop) {//停机维护
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(BaseApp.getSelf(), StopServiceActivity.class);
            BaseApp.getSelf().startActivity(intent);
            return;
        } else if (portalUrl.equals(AppConstants.NOTIFICATION.APP_MARKETS)) {//跳转2 行情
            MainActivity.self.gotoMarkets();
        } else if (portalUrl.equals(AppConstants.NOTIFICATION.APP_TRADE)) {//跳转3 交易-币币
            MainActivity.self.gotoTrade(TransActionHomeFragment.TYPE_BB);
        } else if (portalUrl.equals(AppConstants.NOTIFICATION.APP_ETF)) {//跳转4 交易-ETF
            MainActivity.self.gotoTrade(TransActionHomeFragment.TYPE_ETF);
        } else if (portalUrl.equals(AppConstants.NOTIFICATION.APP_FLAT)) {//跳转5  买币交易
            MainActivity.self.gotoOtc();
        } else if (portalUrl.equals(AppConstants.NOTIFICATION.APP_ME)) {//跳转6 我的
            MainActivity.self.gotoMe();
        } else if (portalUrl.equals(AppConstants.NOTIFICATION.APP_SWAP)) {//跳转7 交易-合约
            MainActivity.self.gotoSwap("");
        } else if (portalUrl.equals(AppConstants.NOTIFICATION.APP_ASSETS_SPOT)) {//跳转8 我的资产-币币账户
            MainActivity.self.gotoMe();
            MainActivity.self.gotoFinance(AllFinanceFragment.TYPE_BB);
        } else if (portalUrl.equals(AppConstants.NOTIFICATION.APP_ASSETS_FIAT)) {//跳转9 我的资产-买币账户
            MainActivity.self.gotoMe();
            MainActivity.self.gotoFinance(AllFinanceFragment.TYPE_FB);
        } else if (portalUrl.equals(AppConstants.NOTIFICATION.APP_ASSETS_SWAP)) {//跳转10 我的资产-合约账户
            MainActivity.self.gotoMe();
            MainActivity.self.gotoFinance(AllFinanceFragment.TYPE_HY);
        } else if (portalUrl.equals(AppConstants.NOTIFICATION.APP_ME_VERIFICATION)) {//跳转11 我的-身份认证
            MainActivity.self.gotoMe();
            MainActivity.self.gotoIdentity();
        } else if (portalUrl.contains(AppConstants.NOTIFICATION.APP_TRADE_COIN)) {//跳转12 币币交易指定交易对
            String coinId = portalUrl.substring(AppConstants.NOTIFICATION.APP_TRADE_COIN.length());
            MainActivity.self.selectCoin(left == null ? "" : left, right == null ? "" : right, NumberUtil.toInt(coinId), 0);
        } else if (portalUrl.contains(AppConstants.NOTIFICATION.APP_SWAP_COIN)) {//跳转13 合约指定种类
            String coinId = portalUrl.substring(AppConstants.NOTIFICATION.APP_SWAP_COIN.length());
            MainActivity.self.gotoSwapForContractId(NumberUtil.toInt(coinId));
        } else if (portalUrl.contains(AppConstants.NOTIFICATION.APP_ETF_COIN)) {//跳转14 ETF指定交易对
            String coinId = portalUrl.substring(AppConstants.NOTIFICATION.APP_ETF_COIN.length());
            MainActivity.self.selectCoin(left == null ? "" : left, right == null ? "" : right, NumberUtil.toInt(coinId), 0,
                    TransActionHomeFragment.TYPE_ETF, 0, true);
//        } else if (portalUrl.contains(AppConstants.NOTIFICATION.APP_LEVER_COIN)) {//杠杆指定交易对
//            String coinId = portalUrl.substring(AppConstants.NOTIFICATION.APP_LEVER_COIN.length());
//            MainActivity.self.selectCoin("", "", NumberUtil.toInt(coinId), 0,
//                    TransActionHomeFragment.TYPE_LEVER, 0, false);
        } else if (portalUrl.contains(AppConstants.NOTIFICATION.APP_ASSETS_SPOT_COIN)) {//跳转15 我的资产-币币-指定币种

        } else if (portalUrl.contains(AppConstants.NOTIFICATION.APP_ASSETS_FLAT_COIN)) {//跳转16 我的资产-买币-指定币种

        } else if (portalUrl.contains(AppConstants.NOTIFICATION.APP_ASSETS_SWAP_COIN)) {//跳转17 我的资产-合约-指定币种

        } else if (portalUrl.equals(AppConstants.NOTIFICATION.APP_GXLC)) {//跳转19 高息理财
            MainActivity.self.gotoNewHome(portalUrl);
        } else if (portalUrl.equals(AppConstants.NOTIFICATION.APP_KF)) {//跳转21 在线客服
            MainActivity.self.gotoNewHome(portalUrl);
        } else if (portalUrl.equals(AppConstants.NOTIFICATION.APP_YJCZ)) {//跳转23 一键充值
            MainActivity.self.gotoNewHome(portalUrl);
        }
        //活动弹窗业务
        else if (portalUrl.equals(AppConstants.NOTIFICATION.APP_VIP)) {//跳转24 我的VIP页
            MainActivity.self.gotoMe();
            MainActivity.self.gotoVipInfo();
//        } else if (portalUrl.equals(AppConstants.NOTIFICATION.APP_LEVER_COIN_DEFAULT)) {//跳转25 杠杆默认交易对
//            MainActivity.self.selectCoin("", "", -1, 0,
//                    TransActionHomeFragment.TYPE_LEVER, 0, false);
        }
        //
//        else {//跳转 首页
//            MainActivity.self.gotoHome();
//        }
        else {
            gotoWebView(context, portalUrl);
        }
    }

    private boolean gotoNativeTarget(Context context, String portalUrl) {
        boolean exist = false;
        if (TextUtils.equals("app/gxlc", portalUrl)) {//高息理财
            exist = true;
            gotoGxlc(context);
        } else if (TextUtils.equals("app/kf", portalUrl)) {//在线客服
            exist = true;
            gotoKf(context);
        } else if (TextUtils.equals("app/fabi", portalUrl)) {//交易-买币
            exist = true;
            try {
                MainActivity.self.gotoOtc();
            } catch (Throwable t) {
                Logger.getInstance().error(t);
            }
        } else if (TextUtils.equals("app/heyu", portalUrl)||TextUtils.equals("app/heyue", portalUrl)) {//交易-合约
            exist = true;
            try {
                MainActivity.self.gotoSwap("");
            } catch (Throwable t) {
                Logger.getInstance().error(t);
            }
        } else if (TextUtils.equals("app/yjcz", portalUrl)) {//一键充值
            exist = true;
            gotoYjcz(context);
        } else if (portalUrl.indexOf("app/yqfy") != -1) {//邀请返佣
            exist = true;
            startActivity(context, new Intent(context, PyramidSaleWebViewActivity.class));
        } else if (TextUtils.equals("app/etf", portalUrl)) {//ETF
            exist = true;
            try {
                MainActivity.self.gotoTrade(TransActionHomeFragment.TYPE_ETF);
            } catch (Throwable t) {
                Logger.getInstance().error(t);
            }
        } else if (TextUtils.equals("app/myasset/exchange", portalUrl)) {//币币资产
            exist = true;
            MainActivity.self.gotoFinance(AllFinanceFragment.TYPE_BB);
        } else if (TextUtils.equals("app/myasset/fiat", portalUrl)) {//买币资产
            exist = true;
            MainActivity.self.gotoFinance(AllFinanceFragment.TYPE_FB);
        } else if (TextUtils.equals("app/myasset/swap", portalUrl)) {//合约资产
            exist = true;
            MainActivity.self.gotoFinance(AllFinanceFragment.TYPE_HY);
        } else if (portalUrl.indexOf("app/viewWelfare") != -1) {//福利中心
            exist = true;
            gotoWelfare(context, portalUrl);
        }
        return exist;
    }


    private void gotoGxlc(Context context) {
        Intent intent = new Intent(context, MoneyManagerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(context, intent);
    }

    private void gotoKf(Context context) {
        startActivity(context, WebViewActivity.class);
    }

    private void gotoYjcz(Context context) {
        if (UserInfoManager.isLogin()) {
            startActivity(context, new Intent(context, ChongzhiListActivity.class));
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("fromClass", MineFragment.class.toString());
            startActivity(context, LoginActivity.class, bundle);
        }
    }

    private void gotoWelfare(Context context, String portalUrl) {//福利中心--特殊的WebView
        try {
            Intent intent = new Intent(context, WelfareWebviewActivity.class);
            intent.putExtra("url", portalUrl);
            intent.putExtra("token", UserInfoManager.getToken());
            intent.putExtra("title", context.getString(R.string.str_welfare_center));
            context.startActivity(intent);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    public void startActivity(Context context, Class<?> clazz, Bundle bundle) {
        try {
            Intent intent = new Intent(context, clazz);
            if (bundle != null) {
                intent.putExtra("bundle", bundle);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(context, intent);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    public void startActivity(Context context, Class<?> clazz) {
        if (clazz == null) {
            return;
        }
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(context, intent);
    }

    private void startActivity(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private void gotoWebView(Context context, String portalUrl) {
        this.gotoWebView(context, portalUrl, true);
    }

    private void gotoWebView(Context context, String portalUrl, boolean hideTitle) {
        this.gotoWebView(context, portalUrl, "", hideTitle, false);
    }

    private static void gotoWebView(Context context, String portalUrl, String title, boolean hideTitle, boolean isBdb) {
        try {
            Intent intent = new Intent(context, NewsWebviewActivity.class);
            intent.putExtra("url", portalUrl);
            intent.putExtra("token", UserInfoManager.getToken());
            intent.putExtra("title", title);
            intent.putExtra("hideTitle", hideTitle);
            intent.putExtra("isBdb", isBdb);
            context.startActivity(intent);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }
}
