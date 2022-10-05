package huolongluo.byw.util.notification;

import android.content.Intent;

import com.legend.modular_contract_sdk.coinw.CoinwHyUtils;

import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.activity.stop_service.StopServiceActivity;
import huolongluo.byw.byw.ui.fragment.maintab05.AllFinanceFragment;
import huolongluo.byw.heyue.ui.TransActionHomeFragment;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.NumberUtil;
//通知点击的统一跳转工具类
public class NotificationUtil {
    private static final String TAG = "NotificationUtil";
    /**
     *
     * @param notification 路径
     * @param left 左币名称，需要切换币对才有，否则为null
     * @param right
     */
    public static void toNotification(String notification,String left,String right){
        Logger.getInstance().debug(TAG,"toNotification HyUtils.isServiceStop:"+ CoinwHyUtils.isServiceStop);
        if(CoinwHyUtils.isServiceStop){//停机维护
            Logger.getInstance().debug(TAG,"toNotification HyUtils.isServiceStop进入");
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(BaseApp.getSelf(), StopServiceActivity.class);
            BaseApp.getSelf().startActivity(intent);
            return;
        }

        if(notification.equals(AppConstants.NOTIFICATION.APP_MARKETS)){//跳转2 行情
            MainActivity.self.gotoMarkets();
        }
        else if(notification.equals(AppConstants.NOTIFICATION.APP_TRADE)){//跳转3 交易-币币
            MainActivity.self.gotoTrade(TransActionHomeFragment.TYPE_BB);
        }
        else if(notification.equals(AppConstants.NOTIFICATION.APP_ETF)){//跳转4 交易-ETF
            MainActivity.self.gotoTrade(TransActionHomeFragment.TYPE_ETF);
        }
        else if(notification.equals(AppConstants.NOTIFICATION.APP_FLAT)){//跳转5  买币交易
            MainActivity.self.gotoOtc();
        }
        else if(notification.equals(AppConstants.NOTIFICATION.APP_ME)){//跳转6 我的
            MainActivity.self.gotoMe();
        }
        else if(notification.equals(AppConstants.NOTIFICATION.APP_SWAP)){//跳转7 交易-合约
            MainActivity.self.gotoSwap("");
        }
        else if(notification.equals(AppConstants.NOTIFICATION.APP_ASSETS_SPOT)){//跳转8 我的资产-币币账户
            MainActivity.self.gotoMe();
            MainActivity.self.gotoFinance(AllFinanceFragment.TYPE_BB);
        }
        else if(notification.equals(AppConstants.NOTIFICATION.APP_ASSETS_FIAT)){//跳转9 我的资产-买币账户
            MainActivity.self.gotoMe();
            MainActivity.self.gotoFinance(AllFinanceFragment.TYPE_FB);
        }
        else if(notification.equals(AppConstants.NOTIFICATION.APP_ASSETS_SWAP)){//跳转10 我的资产-合约账户
            MainActivity.self.gotoMe();
            MainActivity.self.gotoFinance(AllFinanceFragment.TYPE_HY);
        }
        else if(notification.equals(AppConstants.NOTIFICATION.APP_ME_VERIFICATION)){//跳转11 我的-身份认证
            MainActivity.self.gotoMe();
            MainActivity.self.gotoIdentity();
        }
        else if(notification.contains(AppConstants.NOTIFICATION.APP_TRADE_COIN)){//跳转12 币币交易指定交易对
            String coinId=notification.substring(AppConstants.NOTIFICATION.APP_TRADE_COIN.length());
            MainActivity.self.selectCoin(left==null?"":left,right==null?"":right, NumberUtil.toInt(coinId),0);
        }
        else if(notification.contains(AppConstants.NOTIFICATION.APP_SWAP_COIN)){//跳转13 合约指定种类
            String coinId=notification.substring(AppConstants.NOTIFICATION.APP_SWAP_COIN.length());
            MainActivity.self.gotoSwapForContractId(NumberUtil.toInt(coinId));
        }
        else if(notification.contains(AppConstants.NOTIFICATION.APP_ETF_COIN)){//跳转14 ETF指定交易对
            String coinId=notification.substring(AppConstants.NOTIFICATION.APP_ETF_COIN.length());
            MainActivity.self.selectCoin(left==null?"":left,right==null?"":right, NumberUtil.toInt(coinId),0,
                    TransActionHomeFragment.TYPE_ETF, 0, true);
        }
//        else if(notification.contains(AppConstants.NOTIFICATION.APP_LEVER_COIN)){//杠杆指定交易对
//            String coinId=notification.substring(AppConstants.NOTIFICATION.APP_LEVER_COIN.length());
//            MainActivity.self.selectCoin("","", NumberUtil.toInt(coinId),0,
//                    TransActionHomeFragment.TYPE_LEVER, 0, false);
//        }
        else if(notification.contains(AppConstants.NOTIFICATION.APP_ASSETS_SPOT_COIN)){//跳转15 我的资产-币币-指定币种

        }
        else if(notification.contains(AppConstants.NOTIFICATION.APP_ASSETS_FLAT_COIN)){//跳转16 我的资产-买币-指定币种

        }
        else if(notification.contains(AppConstants.NOTIFICATION.APP_ASSETS_SWAP_COIN)){//跳转17 我的资产-合约-指定币种

        }
        else if(notification.equals(AppConstants.NOTIFICATION.APP_GXLC)){//跳转19 高息理财
            MainActivity.self.gotoNewHome(notification);
        }
        else if(notification.equals(AppConstants.NOTIFICATION.APP_KF)){//跳转21 在线客服
            MainActivity.self.gotoNewHome(notification);
        }
        else if(notification.equals(AppConstants.NOTIFICATION.APP_YJCZ)){//跳转23 一键充值
            MainActivity.self.gotoNewHome(notification);
        }
        //活动弹窗业务
        else if(notification.equals(AppConstants.NOTIFICATION.APP_VIP)){//跳转24 我的VIP页
            MainActivity.self.gotoMe();
            MainActivity.self.gotoVipInfo();
        }
        //
        else {//跳转 首页
            MainActivity.self.gotoHome();
        }
    }
}
