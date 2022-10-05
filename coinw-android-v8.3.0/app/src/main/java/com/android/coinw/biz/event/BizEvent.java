package com.android.coinw.biz.event;

import java.util.List;

import huolongluo.byw.model.AliManMachineEntity;
import huolongluo.byw.reform.trade.bean.TradeInfoBean;
import huolongluo.byw.reform.trade.bean.TradeOrderBean;

public class BizEvent {
    public static class CloseActivity {}
    public static class H5 {
        public static class finishEvent {
        }//关闭页面
    }

    public static class Trade {
        /**
         * 数据更新
         */
        public static class DataEvent {
            public boolean isETF;

            public DataEvent(TradeInfoBean trade, boolean isETF) {
                this.trade = trade;
                this.isETF = isETF;
            }

            //TODO 数据设置与更新?
            public TradeInfoBean trade;
            //TODO 其他所需数据
        }

        /**
         * 选择币种
         */
        public static class CoinEvent {
            public String id;
            public String coinName;
            public String cnyName;
            public int selfStation;
            public boolean isETF;

            public CoinEvent(String id, String coinName, String cnyName, int selfStation, boolean isETF) {
                this.id = id;
                this.coinName = coinName;
                this.cnyName = cnyName;
                this.selfStation = selfStation;
                this.isETF = isETF;
            }
        }

        /**
         * 点击盘口买入卖出  发送点击的价格和数量,用于填充交易价格
         */
        public static class ClickHandicap {
            public boolean isETF;
            public boolean isSell;
            public String price;
            public String count;
        }

        public static class SelectViewPager {
            public int index;
            public boolean isETF;
        }

        public static class KLineClickEvent {
            public boolean isBuy;

            public KLineClickEvent(boolean isBuy) {
                this.isBuy = isBuy;
            }
        }

        public static class LatestEvent {
            public int id;
            public boolean isETF;
            public List<TradeOrderBean> trades;

            public LatestEvent(int id, List<TradeOrderBean> trades, boolean isETF) {
                this.id = id;
                this.trades = trades;
                this.isETF = isETF;
            }
        }

        public static class Gear {
            public boolean isETF;

            public Gear(boolean isETF) {
                this.isETF = isETF;
            }
        }

        public static class MarketInfo {
            public boolean isETF;

            public MarketInfo(boolean isETF) {
                this.isETF = isETF;
            }
        }

        public static class CancelOrderInfo {
            public boolean isETF;

            public CancelOrderInfo(boolean isETF) {
                this.isETF = isETF;
            }
        }

        public static class RefreshMarketInfo {
            public boolean isETF;

            public RefreshMarketInfo(boolean isETF) {
                this.isETF = isETF;
            }
        }

        public static class Menu {
        }

        public static class Reload {

        }

        //币币
        public static class ReloadBB {

        }

        //主要更新侧滑栏和行情
        public static class UpdateSelf {

        }

        //k线更新交易页
        public static class UpdateSelfTrade {

        }
    }

    public static class RedEnvelope {
    }

    public static class Aliverify {
        public String type;
        public String code;
        public String message;

        public Aliverify(String type, String code, String message) {
            this.type = type;
            this.code = code;
            this.message = message;
        }
    }

    public static class AliManMachine {
        public int type;
        public AliManMachineEntity entity;

        public AliManMachine(int type, AliManMachineEntity entity) {
            this.type = type;
            this.entity = entity;
        }
    }

    public static class Quotes {
        public static class SlidingMenu {
            public boolean open;

            public SlidingMenu(boolean open) {
                this.open = open;
            }
        }
    }

    /**
     * 滑动时红包是否贴边
     */
    public static class ShowRedEnvelope {
        public ShowRedEnvelope(boolean showRedEnvelope) {
            this.showRedEnvelope = showRedEnvelope;
        }

        public boolean showRedEnvelope;
    }

    public static class ShakeRedEnvelope {
        public ShakeRedEnvelope(boolean showRedEnvelope) {
            this.showRedEnvelope = showRedEnvelope;
        }

        public boolean showRedEnvelope;
    }

    public static class ChangeExchangeRate {
    }

    public static class RefreshRedEnvelope {
    }

    public static class SelectCurrentPage {
        public SelectCurrentPage(int page) {
            this.page = page;
        }

        public int page;
    }

    //离线红包调用h5方法传递红包数据
    public static class OffLineRedEnvelopeEvent {
        public String returnStr;

        public OffLineRedEnvelopeEvent(String returnStr) {
            this.returnStr = returnStr;
        }
    }

    public static class getPricingMethodEvent {
        public getPricingMethodEvent() {
        }
    }

    public static class RedEnvelopeInviteEvent {//红包分享
        public String userId, inviteRedId;

        public RedEnvelopeInviteEvent(String userId, String inviteRedId) {
            this.userId = userId;
            this.inviteRedId = inviteRedId;
        }

    }

    public static class Bdb {
        /**
         * 跳转位置
         */
        public static class GotoEvent {
            public int position;

            public GotoEvent(int position) {
                this.position = position;
            }
        }

        public static class OpenAgreementSuccess {
        }
    }

    public static class Contract {
        public static class OpenAgreementSuccess {
        }
    }
    public static class Market {
        public static class RefreshPairData {//刷新币对数据包含socket
            public boolean isHttpSuccess;
            public RefreshPairData(boolean isHttpSuccess){
                this.isHttpSuccess=isHttpSuccess;
            }
        }
        public static class GetMarketPair {//下拉刷新读取交易对数据
        }
        public static class RefreshSelfList {//3.0行情重构后登录状态的刷新自选列表
        }
        public static class RefreshSelfLocalList {//3.0行情重构后离线状态的刷新自选列表
        }
        public static class RefreshBbSideSearchPairList {//币币侧滑栏搜索刷新当前页面币对列表
        }
        public static class IsVisibleToUser {
            public boolean isVisibleToUser;

            public IsVisibleToUser(boolean isVisibleToUser) {
                this.isVisibleToUser = isVisibleToUser;
            }
        }
    }

    public static class Earn {
        // 理财申购成功事件
        public static class EarnBuySuccess{

        }
    }
    public static class TaskCenterRefresh{
        //任务中心刷新列表
    }
}
