package huolongluo.byw.util;

/**
 * Created by LS on 2018/7/3.
 */

public class Constant {

    /**
     * lang
     */
    public static String KEY_LANG = "KEY_LANG";
    public static final int ZI_XUAN = 300;
    public static final String MY_CODE = "301";
    public static final int MAX_SIZE=20;//k线委托订单和最新成交显示最大的size
    public static String currentLanguage = "";
    public static boolean isOfflineRedEnvelopesToRegister = false;//判断进入注册是否是离线红包进入
    public static boolean showMoney = true;
    public final static int WECHAT = 2;
    public final static int ALIPAY = 3;
    public final static int BANK = 1;
    public static final String IP_TYPT = "ip_type";
    public static boolean isMainActivityCreate = false;
    public static final String UMENG_EVENT_PRE_BANNER1 = "Home_AD_Banner_1_";
    public static final String UMENG_EVENT_PRE_BANNER2 = "Home_AD_Banner_2_";
    public static final String UMENG_EVENT_PRE_B = "Home_B_Recomend_";
    public static final String UMENG_EVENT_PRE_BG_1 = "Home_BG_Recomend_1_";
    public static final String UMENG_EVENT_PRE_BG_2 = "Home_BG_Recomend_2_";
    public static boolean IS_KYC_ALI_VERIFY=true;//全局存储kyc是否为阿里sdk验证
    public static boolean IS_BDB_CLOSE=true;//币贷宝默认关闭
    public static String KEY_PRICING_METHOD="key_pricing_method";//计价方式的sp key
    /**
     * 计价方式读取汇率的轮询时间
     */
    public static long TIME_GET_EXCHANGE_RATE_FIRST =5*1000;//第一次读取汇率的时间间隔，用于异常处理
    public static long TIME_GET_EXCHANGE_RATE =5*60*1000;//获取汇率的时间间隔 毫秒
    /**
     * 阿里人机验证成功后确定类型
     */
    public static final int ALI_MAN_MACHINE_TYPE_LOGIN=1;
    public static final int ALI_MAN_MACHINE_TYPE_REGISTER=2;
    public static final int ALI_MAN_MACHINE_TYPE_RESET_PWD=3;
    public static final int ALI_MAN_MACHINE_TYPE_CODE=4;
    /**
     * 本地计价方式
     */
    public static final String KEY_SP_EXCHANGE_LIST="exchangeRateList";
    /**
     * 停机维护
     */
    //因为停机维护先进MainActivity再进StopServiceActivity,有些需要禁用的接口在MainActivity的onCreate调用，
    // 所以需要在startUp页面就提前获取停机状态的同时又不能进入停机页面，该值和HyUtils.isServiceStop的判断不冲突
    public static boolean STOP_SERVICE_IS_STOP_STARTUP=false;
    /**
     * 红包V4
     */
    public static final String KEY_THIRD_LAUNCH_URI="key_third_launch_uri";//三方链接跳转
    public static final String KEY_RED_INFO="key_red_info";//本地保存红包信息
    /**
     * 币赢理财
     */
    public static final int TYPE_FINANCE_RECORD_ALL=0;
    public static final int TYPE_FINANCE_RECORD_AIR=1;//空投列表
    public static final int TYPE_FINANCE_RECORD_FEE=2;//手续费返还列表
    public static final int TYPE_FINANCE_RECORD_RECHARGE=3;//充提记录
    public static final int TYPE_FINANCE_RECORD_MANAGE_MONEY=4;//理财记录


    /**
     * 活动弹窗
     */
    public static final String KEY_ACTIVITY_AD="key_activity_ad";
    public static final String KEY_OPEN_CONTRACT="key_open_contract";
    public static final String KEY_GET_GOLD="key_get_gold";

    /**
     * 资产小数保留位数
     */
    public static final int ASSETS_AMOUNT_PRECISION = 8;
    public static final String ASSETS_DEFAULT_AMOUNT = "0.00000000";

}
