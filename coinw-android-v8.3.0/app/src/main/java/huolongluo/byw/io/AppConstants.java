package huolongluo.byw.io;
import android.graphics.Color;

import java.util.HashMap;

import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.util.Constant;
public class AppConstants {
    private static final int[] LIBERTY_COLORS = new int[]{Color.rgb(213, 54, 66), Color.rgb(255, 87, 99), Color.rgb(255, 133, 142), Color.rgb(30, 167, 128), Color.rgb(68, 190, 155), Color.rgb(139, 225, 200)};

    public static final class SOCKET {
        //WS相关订阅
        public static final String SPOT_KLINE = "spot/candle-";//### k线
        public static final String SPOT_LATEST_DEAL = "spot/match:";//### 成交数据
        public static final String SPOT_24H_DATA = "spot/market-ticker:";//### 24h数据
        public static final String SPOT_DEPTH = "spot/level2_"+ Constant.MAX_SIZE+":";//### 盘口
        public static final String SPOT_ORDER = "spot/order";//### 当前委托成交数据即时更新

        //老业务固定传（ALL）
        public static final String SPOT_RED_ENVELOPE = "spot/app_redenvelope:ALL";//### 红包订阅
        public static final String SPOT_HOME = "spot/app_riseFallApi:ALL";//### 首页数据
        public static final String SPOT_MARKET = "spot/app_indexApi:ALL";//### 币币行情
        public static final String SPOT_ETF_MARKET = "spot/app_etfIndexApi:ALL";//### ETF行情

        public static final String SPOT_HOME_MARKET_PART1 = "spot/app_index_ticker:ALL";// 首页推荐区 新币榜 主流榜 行情数据数据
        public static final String SPOT_HOME_MARKET_PART2 = "spot/app_newRiseFallApi:ALL";// 首页 涨幅榜和成交榜 数据

        private static final HashMap<String, String> apiMap = new HashMap<String, String>();
        public static final String indexApi = "indexApi";
        public static final String etfIndexApi = "etfIndexApi";
        public static final String redenvelope = "redenvelope";
        public static final String klineApi = "klineApi";
        public static final String riseFallApi = "riseFallApi";

        public static final String tradeApiV3 = "tradeApiV3";//K线改版-新接口
        public static final String intrustApi = "intrustInfo";//委单
        public static final String register = "register";
        public static final String Remove = "Remove";
        public static final String changeType = "change";
        public static final int OPEN = 0;
        public static final int CLOSE = 1;
        public static final int CHANGE = 2;//切换指令
        /**************本地监听使用****************/

        public static HashMap<String, String> getApi() {
            if (apiMap.isEmpty()) {
                apiMap.put(riseFallApi, UrlConstants.DOMAIN + UrlConstants.exchangeRiseFall);
                apiMap.put(indexApi, UrlConstants.DOMAIN + UrlConstants.exchangeMarket);
                apiMap.put(etfIndexApi, UrlConstants.DOMAIN + UrlConstants.exchangeETFMarket);
                apiMap.put(klineApi, UrlConstants.DOMAIN + UrlConstants.exchangeKline);
                apiMap.put(tradeApiV3, UrlConstants.DOMAIN + UrlConstants.exchangeDepthV3);//K线页改版-新接口
                apiMap.put(intrustApi, UrlConstants.DOMAIN + UrlConstants.getEntrustInfo);
            }
            return apiMap;
        }
    }

    public static final class COLOR {
        public static final int[] getLibertyColors() {
            return LIBERTY_COLORS;
        }
    }

    public static final class COMMON {
        public static final String KEY_HOT_SEARCH = "KEY_HOT_SEARCH";//热搜数据
        public static final String KEY_ACTION_FLAG = "KEY_ACTION_FLAG";//请求跳转标识
        public static final String KEY_ACTION_FROM = "from";//请求跳转标识
        public static final String KEY_ACTION_PARAM = "KEY_ACTION_PARAM";//请求跳转参数KEY
        public static final String KEY_ACTION_ETF = "KEY_ACTION_ETF";//ETF标识KEY
        public static final String KEY_ACTION_ETF_DISCLAIMER = "KEY_ACTION_ETF_DISCLAIMER";//ETF免责声明
        public static final String KEY_ACTION_ETF_DATA = "KEY_ACTION_ETF_DATA";//ETF免责声明
        //
        public static final String FLAG_ACTION_NATIVE_INTERFACE = "NATIVE_INTERFACE";//H5调用本地API接口
        public static final String ACTION_NATIVE_INTERFACE_OTC = "NATIVE_INTERFACE_OTC";//H5调用本地API接口（去OTC）
        public static final String ACTION_NATIVE_INTERFACE_TRADE = "NATIVE_INTERFACE_TRADE";//H5调用本地API接口（去快速交易）
        public static final String ACTION_NATIVE_INTERFACE_TRADE_ETF = "NATIVE_INTERFACE_TRADE_ETF";//H5调用本地API接口（去ETF快速交易）
        public static final String ACTION_NATIVE_INTERFACE_SWAP = "NATIVE_INTERFACE_SWAP";//H5调用本地API接口（去快速交易）
        public static final String ACTION_NATIVE_INTERFACE_HOME = "NATIVE_INTERFACE_HOME";//H5调用本地API接口（去首页）
        //
        public static final String KEY_HOME_DYNAMIC = "KEY_HOME_DYNAMIC";//首页动态缓存数据
        //contract
        public static final String FLAG_ACTION_CONTRACT_INTERFACE = "CONTRACT_INTERFACE";//合约调用本地API接口
        //
        public static final int VAL_HTTP_GET = 1;//get请求
        public static final int VAL_HTTP_POST = 2;//post请求
        public static final String KEY_DEPTH_CONFIG = "KEY_DEPTH_CONFIG";//深度配置KEY
        public static final String KEY_HEADER_CONFIG = "KEY_HEADER_CONFIG";//OTC开关控制
        public static final String KEY_BCH_HARD_CONFIG = "KEY_BCH_HARD_CONFIG";//深度配置KEY
        public static final String TOKEN_SAFE = "aac0e8bf095446fea1d9675b051c2b9f";//知道创宇token值
        public static final String USDT="USDT";
        public static final String CNYT="CNYT";
        public static final String DEFAULT_DISPLAY="--";
        public static final String POINT="• ";

        public static final int NATIONALITY_CHINA=1;//kyc nationality对应的国家
        public static final int NATIONALITY_OTHER=0;//kyc nationality对应的国家
    }

    /**
     * 极光推送的通知点击对应的跳转
     */
    public static final class NOTIFICATION {
        public static final String KEY_NOTIFICATION = "key_notification";//路径传递的key
        public static final String KEY_LEFT = "key_left";//左币传递的key
        public static final String KEY_RIGHT = "key_right";
        public static final String APP_HOME = "app/home";//跳转1 首页
        public static final String APP_MARKETS = "app/markets";//跳转2 行情
        public static final String APP_TRADE = "app/trade";//跳转3 交易 币币
        public static final String APP_ETF = "app/etf";//跳转4 交易 ETF
        public static final String APP_FLAT = "app/fabi";//跳转5 买币交易
        public static final String APP_ME = "app/me";//跳转6 我的
        public static final String APP_SWAP = "app/heyu";//跳转7 交易 合约
        public static final String APP_ASSETS_SPOT = "app/myasset/exchange";//跳转8 我的资产-币币账户
        public static final String APP_ASSETS_FIAT = "app/myasset/fiat";//跳转9 我的资产-买币账户
        public static final String APP_ASSETS_SWAP = "app/myasset/swap";//跳转10 我的资产-合约账户
        public static final String APP_ME_VERIFICATION = "app/me/verification";//跳转11 我的-身份认证
        public static final String APP_TRADE_COIN = "app/trade/";//跳转12 币币交易指定交易对 后跟交易对需要截取
        public static final String APP_SWAP_COIN = "app/heyu/";//跳转13 合约指定种类
        public static final String APP_ETF_COIN = "app/etf/";//跳转14 ETF指定交易对
        public static final String APP_ASSETS_SPOT_COIN = "app/assets/spot/";//跳转15 我的资产-币币-指定币种
        public static final String APP_ASSETS_FLAT_COIN = "app/assets/fiat/";//跳转16 我的资产-买币-指定币种
        public static final String APP_ASSETS_SWAP_COIN = "app/assets/swap/";//跳转17 我的资产-合约-指定币种
        public static final String APP_XSY = "app/xsy";//跳转18 新手营
        public static final String APP_GXLC = "app/gxlc";//跳转19 高息理财
        public static final String APP_FSP = "app/fsp";//跳转20 Fansup
        public static final String APP_KF = "app/kf";//跳转21 在线客服
        public static final String APP_BYB = "app/byb";//跳转22 币赢宝
        public static final String APP_YJCZ = "app/yjcz";//跳转23 一键充值
        public static final String KEY_URL="KEY_URL";
        //活动弹窗业务
        public static final String APP_VIP = "app/vip";//我的VIP页

    }

    public static final class LOCAL {
        public static final String KEY_LOCAL_HOST = "KEY_LOCAL_HOST";//本地缓存选中的主机地址
        public static final String KEY_LOCAL_HOST_WEB = "KEY_LOCAL_HOST_WEB";//本地缓存选中的WEB主机地址
        public static final String KEY_LOCAL_HOST_SWAP = "KEY_LOCAL_HOST_SWAP";//本地缓存选中的swap主机地址
        public static final String KEY_LOCAL_HOST_DOWNLOAD = "KEY_LOCAL_HOST_DOWNLOAD";//本地缓存app下载地址
        public static final String KEY_LOCAL_ONE_KEY = "KEY_LOCAL_ONE_KEY";//本地缓存支持划转的类型
        //由于不可抗原因，杠杆服务需要下架，添加本地开关进行控制
        public static final boolean BOOLE_MGRIN_STATUS = false;//杠杆开关-是否开启
        public static final String INDEX_ZC = "INDEX_ZC";//资产
        public static final String INDEX_BB = "INDEX_BB";//币币
        public static final String INDEX_FB = "INDEX_FB";//买币
        public static final String INDEX_BDB = "INDEX_BDB";//币贷宝
    }

    public static final class BIZ {
        //默认档位
        public static final int KEY_BIZ_DEFAULT_GEAR_POSITION = 20;
        //数量默认精度
        public static final int DEFAULT_QUANTITY_PRECISION = 3;
        //价值默认精度
        public static final int DEFAULT_PRICE_PRECISION = 5;
        //杠杆默认档位
        public static final int KEY_BIZ_DEFAULT_GEAR = 8;
        //默认深度
        public static final String DEFAULT_DEPTH = "0.00001";
        public static final String DEFAULT_ETF_DEPTH = "0.000001";
        //交易的默认手续费
        public static final double DEFAULT_TRADE_FEE = 0.002;
        /**
         * 默认小数位
         */
        public static final int DOT_COUNT_PRICE_DEFAULT = 3;
        public static final int DOT_COUNT_NUM_DEFAULT = 4;
        //杠杆计价符号
        public static final String LEVER_VALUATION_SYMBOL = "$";
        /**
         * 特殊业务控制
         * GRIN币种进行提现时，提现金额的小数必须为GRIN的Memo值，否则无法充值到账。
         */
        public static final String KEY_BIZ_COIN_GRIN="GRIN";
    }

    public static final class UI {
        public static final int DEFAULT_PAGE_SIZE = 30;
    }
    public static final class TIMER {
        public static final long NET_VALUE = 5000;//轮询读取净值的时间间隔
        public static final long SOCKET_ORDER = 1000;//订单新增,订单读取接口的时间间隔
        public static final long TOKEN_EXPIRED = 2000;//拦截器执行太快，需要设置间隔
        public static final long AGREEMENT_OPEN_STATUS = 5000;//协议开通接口异常的调用间隔
    }
    public static final class CODE {
        public static final String STOP_SERVICE_CODE="-999999";
        public static final String LOGIN_EXPIRED = "401";//登录失效
        public static final String NEED_KYC = "10000013";//需要kyc认证的错误码
    }
    public static final class KEY{
        //加密公钥
        public static String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCerxM2vXIRyGRvMzOnbIzvXrRhfvHyO0JA5SZwoXJJWLdklxeVbjye1l8gmPTI2Hd33U0rFfQNuhlht71v9MxL5Pk/0iFAIry3ZPaOj33KgACWWCSH2HGAettuGVblNK5CEH1ppJwsC98sVBWDUBJpAfloLDGR6TVfa+zqegLZXQIDAQAB";
        //交易密码专用公钥
        public static final String TRADE_PWD_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC90YZbBQVMWg5Tw5oqQMcjxvQsBBzJNo5yE4/ivrCq4cUXxTbE4iM9tfnjZipbMqVRbVy189suVDj2aJr5cihJfWqA9dUYvsPyJ5XaaWMwJf4jiXMVpoMI+SMockCwyBAOybDwPJVwdE/z7CkyIf1tqdJRimohoUQ/497+sFcS9wIDAQAB";

    }
    public static final class SP_KEY {
        public static final String MARKET_AREA="MARKET_AREA";
        public static final String MARKET_PAIR="MARKET_PAIR";
    }
}
