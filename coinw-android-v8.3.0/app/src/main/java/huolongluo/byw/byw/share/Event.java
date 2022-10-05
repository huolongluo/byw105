package huolongluo.byw.byw.share;

import java.util.List;

import huolongluo.byw.byw.bean.HomeMarketBean;
import okhttp3.WebSocket;

/**
 * Created by 火龙裸先生 on 2017/8/11 0011.
 */
public class Event {
    public static class CardPayCallBack {
        public String payType;
        public String data;
        public boolean isCallBackToService;

        public CardPayCallBack(String payType, String data, boolean isCallBackToService) {
            this.payType = payType;
            this.data = data;
            this.isCallBackToService = isCallBackToService;
        }
    }

    /**
     * 刷新 用户登录信息
     */
    public static class refreshInfo {
    }

    public static class SwitchHomeUi {
        public SwitchHomeUi(boolean isNewbie) {
            this.isNewbie = isNewbie;
        }

        // 是否是新手版
        public boolean isNewbie = false;
    }

    /**
     * 阿里人脸识别成功反馈
     */
    public static class AliVerifySuccess {
        private int tradeType;//区分购买和出售
        private String coinId;//区分是哪个fragment

        public int getTradeType() {
            return tradeType;
        }

        public void setTradeType(int tradeType) {
            this.tradeType = tradeType;
        }

        public String getCoinId() {
            return coinId;
        }

        public void setCoinId(String coinId) {
            this.coinId = coinId;
        }
    }

    /**
     * 兑换买币回调
     */
    public static class exchangeClick {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    /**
     * 刷新fansup的数据
     */
    public static class refreFansUpInfo {
    }

    public static class deleteAddress {
        public String addressId;
        public deleteAddress(String addressId) {
            this.addressId = addressId;
        }
    }

    /**
     * 刷新fansup的数据
     */
    public static class FansUpNavigationBar {
        public boolean hide;

        public FansUpNavigationBar(boolean hide) {
            this.hide = hide;
        }
    }

    public static class NativeH5 {
        public String title;

        public NativeH5(String title) {
            this.title = title;
        }
    }

    public static class refreshVip {
    }

    /**
     * 关闭fansup  webview页面
     */
    public static class closeCurrentView {
    }

    /**
     * 语言切换
     */
    public static class LanguageChange {
    }

    public static class RestartApp {
    }

    /**
     * im通知消息
     */
    public static class IMMessage {
        public List<com.netease.nimlib.sdk.msg.model.IMMessage> messages;

        public IMMessage() {
        }

        public IMMessage(List<com.netease.nimlib.sdk.msg.model.IMMessage> messages) {
            this.messages = messages;
        }
    }

    public static class exitApp {
        public exitApp() {
        }
    }

    /**
     * 更新一键买币的币种信息
     */
    public static class UPTitle {
    }

    public static class BdbOpenAgreement {
    }

    public static class cancelOrder {
        public String orderId;
        public int position;

        public cancelOrder(String orderId, int position) {
            this.orderId = orderId;
            this.position = position;
        }
    }

    /**
     * 点击 充值 或 提现
     *
     * @param clickType 1 充值 2 提现
     * @param position  点击的item位置
     */
    public static class clickAssets {
        public int clickType;
        public int position;
        public String coinName;

        public clickAssets(int clickType, int position, String coinName) {
            this.clickType = clickType;
            this.position = position;
            this.coinName = coinName;
        }
    }

    /**
     * 用户输入完交易密码，点击了确定对话框
     */
    public static class getStart_Trade_psd {
        public int type; // 0 买入 1卖出
        public String trade_psd;
        public boolean isETF;

        public getStart_Trade_psd(int type, String trade_psd, boolean isETF) {
            this.type = type;
            this.trade_psd = trade_psd;
            this.isETF = isETF;
        }
    }

    /**
     * 用户输入完交易密码，点击了确定对话框
     */
    public static class getStart_Trade_ {
        public int type; // 0 买入 1卖出
        public String trade_psd;
        public boolean isETF;

        public getStart_Trade_(int type, String trade_psd, boolean isETF) {
            this.type = type;
            this.trade_psd = trade_psd;
            this.isETF = isETF;
        }
    }

    public static class closeActivity {
        public String className;

        public closeActivity(String className) {
            this.className = className;
        }
    }

    /**
     * 点击了TabLayout下的“买入”、“卖出”
     * 的RecyclerView的Item
     */
    public static class clickRvItem {
        public String selcetPrice;
        public String toFragment;

        public clickRvItem(String selcetPrice, String toFragment) {
            this.selcetPrice = selcetPrice;
            this.toFragment = toFragment;
        }
    }

    /**
     * 首页获取到了品种信息
     */
    public static class VarietiesInfo {
        public HomeMarketBean homeMarketBean;

        public VarietiesInfo(HomeMarketBean homeMarketBean) {
            this.homeMarketBean = homeMarketBean;
        }
    }

    /**
     * 首页获取到了品种信息
     */
    public static class NeedUpdate {
        public boolean need;

        public NeedUpdate(boolean need) {
            this.need = need;
        }
    }

    public static class clickQQService {
        public String qq;

        public clickQQService(String qq) {
            this.qq = qq;
        }
    }

    public static class clickCoinAddress {
        public int id;
        public String selectAddress;
        public boolean isInternalAddress;

        public clickCoinAddress(int id, String selectAddress,boolean isInternalAddress) {
            this.id = id;
            this.selectAddress = selectAddress;
            this.isInternalAddress = isInternalAddress;
        }
    }

    public static class clickBankCard {
        public String bankType;
        public String address;
        public String bankNumber;
        public String bankId;

        public clickBankCard(String bankType, String address, String bankNumber, String bankId) {
            this.bankType = bankType;
            this.address = address;
            this.bankNumber = bankNumber;
            this.bankId = bankId;
        }
    }
    public static class clickSureUpVersioin {
    }

    public static class clickBankName {
        public String bankName;

        public clickBankName(String bankName) {
            this.bankName = bankName;
        }
    }

    public static class clickHomeMarket {
        public int selectOptionID;
        public String selectOptionName;
        public int position;

        public clickHomeMarket(int selectOptionID, String selectOptionName, int position) {
            this.selectOptionID = selectOptionID;
            this.selectOptionName = selectOptionName;
            this.position = position;
        }
    }

    /**
     * 长链接 是否连接成功 首页回调
     */
    public static class WebSocketConnectMain {
        public WebSocket webSocket;
        public boolean connectSucce;

        public WebSocketConnectMain(WebSocket webSocket, boolean connectSucce) {
            this.webSocket = webSocket;
            this.connectSucce = connectSucce;
        }
    }

    /**
     * 长链接 是否连接成功 交易回调
     */
    public static class WebSocketConnectTrade {
        public WebSocket webSocket;
        public boolean connectSucce;

        public WebSocketConnectTrade(WebSocket webSocket, boolean connectSucce) {
            this.webSocket = webSocket;
            this.connectSucce = connectSucce;
        }
    }

    /**
     * 长链接消息回传 首页
     */
    public static class WebSocketCallBackMain {
        public String data;

        public WebSocketCallBackMain(String data) {
            this.data = data;
        }
    }

    /**
     * 长链接消息回传 交易
     */
    public static class WebSocketCallBackTrade {
        public String data;

        public WebSocketCallBackTrade(String data) {
            this.data = data;
        }
    }

    /**
     * 用户选择/切换品种
     */
    public static class ChangeOption {
        public String optionName;

        public ChangeOption(String optionName) {
            this.optionName = optionName;
        }
    }
    public static class RefreshMarketSelfData {}//币币的socket数据触发更新自选的列表
    public static class MarketBBSocket {//进入自选tab需要开启和关闭币币的socket
        public boolean isOpen;
        public MarketBBSocket(boolean isOpen) {
            this.isOpen = isOpen;
        }
    }
    /**
     * 点击了市场界面Item上的Chart图表
     */
    public static class ClickOptionItemChart {
        public int id;
        public String cnName;
        public String coinName;
        public int position;

        public ClickOptionItemChart(int id, String cnName, String coinName, int position) {
            this.id = id;
            this.cnName = cnName;
            this.coinName = coinName;
            this.position = position;
        }
    }

    public static class OtcFlitrade {
        public boolean bankStatus;
        public boolean alipayStatus;
        public boolean wechatStatus;
        public String moneyNum;

        public OtcFlitrade(boolean bankStatus, boolean alipayStatus, boolean wechatStatus, String moneyNum) {
            this.bankStatus = bankStatus;
            this.alipayStatus = alipayStatus;
            this.wechatStatus = wechatStatus;
            this.moneyNum = moneyNum;
        }
    }

    public static class ChangeLanguage{

    }
}
