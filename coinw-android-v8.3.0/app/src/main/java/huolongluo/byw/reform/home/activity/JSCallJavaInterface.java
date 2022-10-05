package huolongluo.byw.reform.home.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.android.coinw.biz.event.BizEvent;
import com.android.coinw.biz.trade.helper.TradeDataHelper;
import com.android.legend.model.enumerate.transfer.TransferAccount;
import com.android.legend.ui.login.RegisterActivity;
import com.android.legend.ui.transfer.AccountTransferActivity;
import com.blankj.utilcodes.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.bindphone.BindPhoneActivity;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.activity.renzheng.RenZhengBeforeActivity;
import huolongluo.byw.byw.ui.activity.setchangepsw.SetChangePswActivity;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.mine.activity.GradeActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.SPUtils;

//window.JSCallJava.callJava('hello input click');
public class JSCallJavaInterface {
    private static final String SHOWNATIVEUSERAUTH = "showNativeUserAuth";//去认证
    private static final String OTCEDITSUCCESS = "OTCEditSuccess";//编辑成功
    private static final String OTCPUBLISHSUCCESS = "OTCPublishSuccess";//发布成功
    private static final String SHOWNATIVESETUPTRADEPASSWORD = "showNativeSetupTradePassword";
    private static final String SHOWNATIVELOGIN = "showNativeLogin";
    private static final String CLOSEBUTTONCLICK = "closeButtonClick";
    private static final String SHOWNAVIGATIONBAR = "showNavigationBar";
    private static final String HIDENAVIGATIONBAR = "hideNavigationBar";
    private static final String UPDATE_TITLE = "updateTitle";//更新窗口的标题
    private static final String SHOWUSERASSET = "showUserAsset";
    private static final String SHOWVIP = "showVip";
    private static final String SHOW_TRADE = "showTrade";//去交易
    private static final String SHOW_TRADE_ETF = "showETFTrade";//去ETF交易
    private static final String SHOW_OTC = "showOTC";//去OTC
    private static final String OPEN_BROWSER = "openBrowser";//调用系统游览器
    private static final String SHOW_HOME = "showHome";//去首页
    private static final String SHOW_SWAP = "showSwap";//去合约
    private static final String SHOW_BIND_TEL = "showBindTel";//绑定手机号
    private static final String SHOW_TRANSFER = "showTransfer";//币贷宝h5直接跳划转
    private static final String ACTION_SHARE_BENEFITS = "shareBenefits";//福利中心分享
    private static final String SHOW_REGISTER = "showRegister";//去注册
    private static final String GET_RED_ENVELOPES = "getOffLineRedEnvelopes";//h5通过返回值获取红包数据
    private static final String PUT_RED_ENVELOPES = "putOffLineRedEnvelopes";//h5传递红包数据
    private static final String SHOW_OFFLINE_RED_ENVELOPES_LOGIN = "showOffLineRedEnvelopesLogin";
    private static final String GET_PRICING_METHOD = "getPricingMethod";//h5获取app的计价方式
    private static final String ACTION_INVITE_RED = "redEnvelopInvite";//本地分享
    public static boolean FromFansUpView = false;
    private static final String TAG = "JSCallJavaInterface";

    private void toLogin() {
        Intent intent = new Intent(BaseApp.getSelf(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApp.getSelf().startActivity(intent);
        NewsWebviewActivity.isBdbJump = true;
    }

    //离线红包的登录，注册成功需要控制对话框的弹出
    private void toOfflineRedEnvelopesLogin() {
        Intent intent = new Intent(BaseApp.getSelf(), LoginActivity.class);
        intent.putExtra("isOfflineRedEnvelopes", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApp.getSelf().startActivity(intent);
    }

    private void toRegister() {
        Intent intent = new Intent(BaseApp.getSelf(), RegisterActivity.class);
        BaseApp.getSelf().startActivity(intent);
    }

    private void toSafeCenter() {
        Intent intent = new Intent(BaseApp.getSelf(), SetChangePswActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("setChangePswTitle", BaseApp.getSelf().getString(R.string.a1));
        bundle.putBoolean("isBindGoogle", UserInfoManager.getUserInfo().isGoogleBind());
        bundle.putBoolean("isBindTelephone", UserInfoManager.getUserInfo().isBindMobil());
        intent.putExtra("bundle", bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApp.getSelf().startActivity(intent);
        NewsWebviewActivity.isBdbJump = true;
        FromFansUpView = true;
    }

    private void shiMinGW() {
        Intent intent = new Intent(BaseApp.getSelf(), RenZhengBeforeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApp.getSelf().startActivity(intent);
        FromFansUpView = true;
    }

    private void toUserAsset() {
        //判断登录
        if (!UserInfoManager.isLogin()) {
            toLogin();
            return;
        }
        //个人资产中心
        if(MainActivity.self==null) return;
        MainActivity.self.gotoFinance();
        Intent intent = new Intent();
        intent.setClass(MainActivity.self, MainActivity.class);
        MainActivity.self.startActivity(intent);
    }

    private void toUserVip() {
        //判断登录
        if (!UserInfoManager.isLogin()) {
            toLogin();
            return;
        }
        //VIP中心
        Intent intent = new Intent();
        intent.setClass(BaseApp.getSelf(), GradeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApp.getSelf().startActivity(intent);
    }

    private void openBrowser(JSONObject json) {
        try {
            String url = json.getString("url");
            Logger.getInstance().debug(TAG, "url: " + url);
            if (TextUtils.isEmpty(url)) {
                return;
            }
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            BaseApp.getSelf().startActivity(intent);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private void toShareBenefits() {//福利中心分享功能
        try {
            Intent intent = new Intent();
            intent.setClass(BaseApp.getSelf(), WelfareCenterShareActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            BaseApp.getSelf().startActivity(intent);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private void toTrade(JSONObject json) {
        if (json == null) {
            return;
        }
        try {
            int id = TradeDataHelper.getInstance().getId(false);
            if (json.has("id")) {
                id = json.getInt("id");
            }
            Intent intent = new Intent(BaseApp.getSelf(), MainActivity.class);
            intent.putExtra(AppConstants.COMMON.KEY_ACTION_FROM, AppConstants.COMMON.FLAG_ACTION_NATIVE_INTERFACE);
            intent.putExtra(AppConstants.COMMON.KEY_ACTION_PARAM, AppConstants.COMMON.ACTION_NATIVE_INTERFACE_TRADE);
            intent.putExtra("tradeId", id);
            intent.putExtra("coinName", "");//由H5未传过来，本地传空串到交易页面，待数据加载后才更新界面
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            BaseApp.getSelf().startActivity(intent);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private void toETFTrade(JSONObject json) {
        if (json == null) {
            return;
        }
        try {
            int id = TradeDataHelper.getInstance().getId(true);
            if (json.has("id")) {
                id = json.getInt("id");
            }
            Intent intent = new Intent(BaseApp.getSelf(), MainActivity.class);
            intent.putExtra(AppConstants.COMMON.KEY_ACTION_FROM, AppConstants.COMMON.FLAG_ACTION_NATIVE_INTERFACE);
            intent.putExtra(AppConstants.COMMON.KEY_ACTION_PARAM, AppConstants.COMMON.ACTION_NATIVE_INTERFACE_TRADE_ETF);
            intent.putExtra("tradeId", id);
            intent.putExtra("coinName", "");//由H5未传过来，本地传空串到交易页面，待数据加载后才更新界面
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            BaseApp.getSelf().startActivity(intent);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private void toSwap(JSONObject json) {
        if (json == null) {
            return;
        }
        try {
            //JIRA: COIN-3196
            int swapType = 0;
            if (json.has("swapType")) {
                swapType = json.getInt("swapType");
            }
            Intent intent = new Intent(BaseApp.getSelf(), MainActivity.class);
            intent.putExtra(AppConstants.COMMON.KEY_ACTION_FROM, AppConstants.COMMON.FLAG_ACTION_NATIVE_INTERFACE);
            intent.putExtra(AppConstants.COMMON.KEY_ACTION_PARAM, AppConstants.COMMON.ACTION_NATIVE_INTERFACE_SWAP);
            intent.putExtra("swapType", swapType);//由H5未传过来，0、1、2，分别跳转到合约开仓、合约平仓、合约持仓界面，不传默认跳开仓。
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            BaseApp.getSelf().startActivity(intent);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private void toBindTel() {
        try {
            Intent intent = new Intent(BaseApp.getSelf(), BindPhoneActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("isBindGoogle", UserInfoManager.getUserInfo().isGoogleBind());
            intent.putExtra("isBindTelephone", false);
            BaseApp.getSelf().startActivity(intent);
            NewsWebviewActivity.isBdbJump = true;
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    //币贷宝h5直接跳转划转
    private void toTransfer(JSONObject json) {
        try {
            AccountTransferActivity.Companion.launch(BaseApp.getSelf(), TransferAccount.WEALTH.getValue(),TransferAccount.BDB.getValue(),null,null,
                    false,json.getString("coinName"),Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            NewsWebviewActivity.isBdbJump = true;
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private void toOTC() {
        toTransfer(1, -1);
        try {
            Intent intent = new Intent(BaseApp.getSelf(), MainActivity.class);
            intent.putExtra(AppConstants.COMMON.KEY_ACTION_FROM, AppConstants.COMMON.FLAG_ACTION_NATIVE_INTERFACE);
            intent.putExtra(AppConstants.COMMON.KEY_ACTION_PARAM, AppConstants.COMMON.ACTION_NATIVE_INTERFACE_OTC);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            BaseApp.getSelf().startActivity(intent);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private void toHome() {
        try {
            Intent intent = new Intent(BaseApp.getSelf(), MainActivity.class);
            intent.putExtra(AppConstants.COMMON.KEY_ACTION_FROM, AppConstants.COMMON.FLAG_ACTION_NATIVE_INTERFACE);
            intent.putExtra(AppConstants.COMMON.KEY_ACTION_PARAM, AppConstants.COMMON.ACTION_NATIVE_INTERFACE_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            BaseApp.getSelf().startActivity(intent);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private void updateTitle(JSONObject json) {
        if (json == null) {
            return;
        }
        if (!json.has("title")) {
            return;
        }
        try {
            String title = json.getString("title");
            EventBus.getDefault().post(new Event.NativeH5(title));
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    @JavascriptInterface
    public String callJava(String msg) throws JSONException {
        // { callName: "showNativeLogin" }
        String returnStr = "";
        Logger.getInstance().debug(TAG + "_BDB", "msg: " + msg);
        JSONObject json = new JSONObject(msg);
        String callName = json.get("callName").toString();
        if (!TextUtils.isEmpty(callName)) {
            switch (callName) {
                case SHOWNATIVEUSERAUTH:
                    shiMinGW();
                    break;
                case SHOWNATIVESETUPTRADEPASSWORD:
                    toSafeCenter();
                    break;
                case SHOWNATIVELOGIN:
                    toLogin();
                    break;
                case CLOSEBUTTONCLICK:
                    EventBus.getDefault().post(new Event.closeCurrentView());
                    break;
                case SHOWNAVIGATIONBAR:
                    Logger.getInstance().debug("JSInterface", "SHOWNAVIGATIONBAR");
                    EventBus.getDefault().post(new Event.FansUpNavigationBar(false));
                    break;
                case HIDENAVIGATIONBAR:
                    Logger.getInstance().debug("JSInterface", "HIDENAVIGATIONBAR");
                    EventBus.getDefault().post(new Event.FansUpNavigationBar(true));
                    break;
                case SHOWUSERASSET:
                    toUserAsset();
                    break;
                case SHOWVIP:
                    toUserVip();
                    break;
                case SHOW_TRADE:
                    toTrade(json);
                    break;
                case SHOW_TRADE_ETF:
                    toETFTrade(json);
                    break;
                case SHOW_OTC:
                    toOTC();
                    break;
                case OPEN_BROWSER:
                    openBrowser(json);
                    break;
                case SHOW_HOME:
                    toHome();
                    break;
                case SHOW_SWAP:
                    toSwap(json);
                    break;
                case SHOW_BIND_TEL:
                    toBindTel();
                    break;
                case SHOW_TRANSFER:
                    toTransfer(json);
                    break;
                case OTCEDITSUCCESS:
                case OTCPUBLISHSUCCESS:
                    ToastUtils.showShortToast(  BaseApp.getSelf().getString(R.string.str_success));
                    EventBus.getDefault().post(new BizEvent.H5.finishEvent());
                    break;
                case ACTION_SHARE_BENEFITS://福利中心分享
                    toShareBenefits();
                    break;
                case UPDATE_TITLE://更新窗口标题
                    updateTitle(json);
                    break;
                case SHOW_REGISTER:
                    toRegister();
                    break;
                case GET_RED_ENVELOPES:
                    returnStr = SPUtils.getString(BaseApp.getSelf(), Constant.KEY_RED_INFO, "");
                    Logger.getInstance().debug(TAG, "GET_RED_ENVELOPES returnStr:" + returnStr);
                    EventBus.getDefault().post(new BizEvent.OffLineRedEnvelopeEvent(returnStr));
                    break;
                case PUT_RED_ENVELOPES:
                    String data = json.getString("data");
                    Logger.getInstance().debug(TAG, "PUT_RED_ENVELOPES data:" + data);
                    SPUtils.saveString(BaseApp.getSelf(), Constant.KEY_RED_INFO, data);
                    break;
                case SHOW_OFFLINE_RED_ENVELOPES_LOGIN:
                    Logger.getInstance().debug(TAG, "SHOW_OFFLINE_RED_ENVELOPES_LOGIN ");
                    toOfflineRedEnvelopesLogin();
                    break;
                case GET_PRICING_METHOD:
                    Logger.getInstance().debug(TAG, "GET_PRICING_METHOD ");
                    EventBus.getDefault().post(new BizEvent.getPricingMethodEvent());
                    break;
                case ACTION_INVITE_RED://本地分享
                    Logger.getInstance().debug(TAG, "RedEnvelopeInviteEvent ");
                    String userId = json.getString("userId");
                    String inviteRedId = json.getString("inviteRedId");
                    EventBus.getDefault().post(new BizEvent.RedEnvelopeInviteEvent(userId, inviteRedId));
                    break;
            }
        }
        return returnStr;
    }

    /**
     * type  =1 为买币交易  2为币币
     *
     * @param type
     */
    private void toTransfer(int type, int id) {
        Intent intent = new Intent(BaseApp.getSelf(), MainActivity.class);
        intent.putExtra("type", type);
        if (type == 2) {
        }
        intent.putExtra("from", "OtcTransferActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApp.getSelf().startActivity(intent);
    }
}
