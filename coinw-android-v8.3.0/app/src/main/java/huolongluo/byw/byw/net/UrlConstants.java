package huolongluo.byw.byw.net;

import android.content.Context;

import huolongluo.byw.util.LogicLanguage;
import huolongluo.bywx.utils.AppUtils;

/**
 * UrlConstants
 * <p>
 * Created by 火龙裸先生 on 2017/08/11.
 */
public class UrlConstants {
    public final static String hppPackapeNamet = "com.legendwd.hyperpayW";
    public final static String hppPackapeNameo = "com.legendwd.hyperpay";
    public final static int hppVerCode = 20;

    public static String SOCKET_ADDR_new = DomainHelper.getDomain().getNewWS();//"ws://47.56.86.63"; // 测试地址
    public static String SOCKET_ADDR = DomainHelper.getDomain().getWS();// "ws://47.56.86.63/myecho/"; //
    public static String DOMAIN = DomainHelper.getDomain().getHost();// "http://47.56.86.63/";//最新测试
    //    public static String WEB_DOMAIN = "http://www.coinw.la/";//web的域名，人机验证和网络检测使用的h5地址部署在web服务上
//    public static String WEB_DOMAIN = "http://www.coinw.online/";//web的域名，人机验证和网络检测使用的h5地址部署在web服务上
    public static String WEB_DOMAIN = DomainHelper.getWebUrl();//web的域名，生产人机验证和网络检测使用的h5地址部署在web服务上
    //    public static String DYNAMIC_DOMAIN = "https://btc018.oss-cn-shenzhen.aliyuncs.com/coinw2/domain.json";//动态域名的请求地址
    public static String DYNAMIC_DOMAIN = "https://btc018.oss-accelerate.aliyuncs.com/coinw2/domain.json";//动态域名的请求地址-->切换成加速域名
    public static final String userLogin = "app/userLogin.html"; // 登录
    public static final String LOGIN = "appApi2.html"; // 登录
    public static final String REGISTER = "appApi2.html"; // 注册
    public static final String REGISTER_SEND_SMS = "appApi2.html"; // 发送手机验证码
    public static final String REGISTER_SEND_EMAIL = "appApi2.html"; // 发送邮箱验证码
    public static final String CHANGE_PASSWORD = "appApi2.html"; // 修改密码
    public static final String HOME_MARKET = "appApi2.html"; // 获取app首页品种列表
    public static final String HOME_ADVERT = "appApi2.html"; // 获取app首页广告
    public static final String BUY_IN_DATA = "appApi2.html"; // 请求买入界面数据
    public static final String HISTORY_DATA = "appApi2.html"; // 历史
    public static final String CANCEL_ORDER = "appApi2.html"; // 取消订单
    public static final String START_TRADE = "appApi2.html"; // 开始请求交易
    public static final String USER_ASSETS = "appApi2.html"; // 获取用户财务信息
    public static final String CHONGZHI_QRCODE = "appApi2.html"; // 获取充值二维码图片
    public static final String RMB_RECHARGE = "appApi2.html"; // 获取人民币充值信息
    public static final String RECHARGE_RECORD = "appApi2.html"; // 获取充值记录
    public static final String COIN_TIXINA_ADDRESS = "appApi2.html"; // 获取虚拟币提现地址
    public static final String DELETE_COIN_ADDRESS = "appApi2.html"; // 删除钱包地址
    public static final String ADD_COIN_ADDRESS = "appApi2.html"; // 确认添加钱包地址
    public static final String RMB_TIXIAN = "appApi2.html"; // 人民币提现
    public static final String BANK_CARD_LIST = "appApi2.html"; // 银行卡列表
    public static final String DELETE_BANK_CARD = "appApi2.html"; // 删除银行卡
    public static final String ADD_BANK_CARD = "appApi2.html"; // 添加银行卡
    public static final String BANK_LIST = "appApi2.html"; // 银行列表
    public static final String REN_ZHENG = "appApi2.html"; // 实名认证 或者 认证信息查询
    public static final String GET_VERSION = "appApi2.html"; // 获取最新版本号
    public static final String VALIDETAIDENTITY = "appApi2.html"; // 查看实名认证信息
    public static final String SAFE_CENTRE_INFO = "appApi2.html"; // 获取安全设置列表信息
    public static final String CHANGEPASSWORD = "appApi2.html"; // 修改交易密码 或者 登陆密码
    public static final String GOOGLE_AUTH1 = "appApi2.html"; // 谷歌认证第一步获取key
    public static final String GOOGLE_AUTH2 = "appApi2.html"; // 谷歌认证第二步
    public static final String BIND_EMAIL = "appApi2.html"; // 绑定邮箱
    public static final String GETSUCCESS_DEATIL = "appApi2.html";
    public static final String ACTION = "?action="; // action
    public static final String GET_RECHARGELIST = "app/getBtcRechargeListRecord.html";
    public static final String C2_DES = "app/user/kyc/c2/right";//c2 认证成功页面限额描述
    public static final String DETELE_ADD = "app/detelCoinAddress.html";
    public static final String EDIT_ADD = "app/updateWithdrawAddressRemark.html";
    public static final String SUMBIT_WITHDRAW_ADDRESS = "app/setWithdrawBtcAddr.html";
    public static final String COIN_LIST_INFO = "app/getCoinListInfo.html";
    public static final String SUMBIT_TIXIAN = "app/withdrawBtcSubmit.html";
    public static final String GET_GOOGLEAUTH = "app/googleAuth.html";
    public static final String GET_BIND_GOOGLE = "app/getBindGoogle.html";//
    public static final String GET_SMS = "app/sendMessageCode.html";//发送短信验证码
    public static final String GET_SMS_EMAIL = "app/sendMailCode.html";//发送邮箱验证码
    public static final String USER_REGISTER = "app/userRegister.html";//注册
    public static final String GET_FEED_TYPE = "app/getQuestionTypeList.html";//获取反馈类型
    public static final String SAVE_FEED = "app/postQuestion.html";//提交反馈
    public static final String GET_FEED_HIS = "app/listQuestions.html";//历史反馈列表
    public static final String DETELE_FEED_HIS = "app/postQuestion.html";//
    public static final String GET_USER_INFO = "app/getUserInfo.html";//获取用户信息
    public static String UNPAID_ORDER = DOMAIN + "app/otc/activityOrder";//获取未支付订单
    public static final String GET_CNYT_USER_INFO = "app/rechargeCnyInformation.html";//获取商户信息
    public static final String alipayManual = "app/alipayManual.html";//点击买入
    public static final String getUserWalletCnyt = "app/getUserWalletCnyt.html";//c2c提现，新
    public static final String SendMessageCode = "app/sendMessageCode.html";//
    public static final String rechargeCnySubmit = "app/rechargeCnySubmit.html";//点击确定买入操作
    public static final String rechargeListCny = "app/rechargeListCny.html";//c2c充值记录
    public static final String cancelRechargeCnySubmit = "app/cancelRechargeCnySubmit.html";//取消充值
    public static final String alipayManualById = "app/alipayManualById.html";//单条充值记录
    public static final String withdrawListCny = "app/withdrawListCny.html";//提现记录
    public static final String cancelWithdrawcny = "app/cancelWithdrawcny.html";//取消充值
    public static final String SetWithdrawCnyBankInfo = "app/setWithdrawCnyBankInfo.html";//取消充值
    public static final String WithDrawCny = "app/withDrawCny.html";//人民币提现
    public static final String whetherDeductible = "app/whetherDeductible.html";//获取实名认证信息
    public static final String ValidateIdentity = "app/validateIdentity.html";//实名认证
    public static final String ValidateIdentityUpload = "app/validateIdentityUpload.html";//上传身份证照片
    public static final String getSecurity = "app/getSecurity.html";//安全列表设置
    public static String order = DOMAIN + "spot-order/order";//币币下单
    public static String verifyTradePwd = DOMAIN + "spot-order/trade_password";//验证交易密码
    public static final String GetVersion = "app/getVersion.html";//获取版本信息
    public static final String GET_MARKET_DATA = "app/getMarketData.html";//获取行情列表
    public static final String TRADING_AREA = "app/tradingArea.html";//tradingAre交易区
    public static final String SAVE_HOT_SEARCH = "app/saveHotsearch.html";//热搜收集
    public static final String HOT_SEARCH = "app/hotsearch.html";//获取热搜列表
    public static final String FAR_TICLE_LIST = "app/farticleList.html";//新闻列表
    public static final String FAR_TICLE_BY_ID = "app/farticleById.html";//新闻列表
    public static final String MAIL_LIST = "userLetter/query";//站内信列表
    public static final String MAIL_SET_ALL_READ = "userLetter/toHaveReadALL";//站内信全部标为已读
    public static final String BIND_PHONE = "app/bindPhone.html";//绑定手机号 //与后台确认.html可以加也可以不加
    public static final String validatePhone = "app/validatePhone.html";//修改手机号
    public static final String bannerList = "app/bannerList.html";//绑定手机号
    public static final String UserRegisterCode = "app/userRegisterCode.html";//校验注册验证码
    public static final String Withdraw_Info = "app/withdrawInfo.html";//获取银行卡列表
    public static final String Delete_BankAddress = "app/deleteBankAddress.html";//删除银行卡
    public static final String getSuccessDetails = "app/getSuccessDetails.html";//获取交易最新成交
    public static final String activityPic = "app/activityPic.html";//首页活动
    public static final String CONVERT = "app/otc/asset/convert.html";//买币兑换
    public static final String getEntrustInfo = "app/getEntrustInfo.html";//查询所有的委托单
    public static final String app_GET_ALL_ADD = "app/getAllWithdrawAddress.html";//获取地址
    public static final String getConfig = "app/sys/config/get";//是否显示c2c
    public static final String captchaVerify = "app/captchaVerify.html";//腾讯行为验证
    public static final String captchaVerifyAndVerifyAccount = "app/findLoginPassword/captchaAndContactVerify.html";//腾讯行为验证包括账号是否存在
    public static final String getConfigList = "app/sys/config/list.html";//APP获取系统参数
    public static final String userMarketInfo = "app/userMarketInfo.html";//获取用户币种资产信息
    public static final String findLoginPassword = "app/findLoginPassword.html";//忘记密码
    public static final String cancelAllEntrust = "app/cancelAllEntrust.html";//撤销订单
    public static final String updateLoginToken = "app/updateLoginToken.html";//指纹识别，更新token
    public static final String faceIdGetH5Token = "app/faceIdGetH5Token.html";//人脸识别获取token
    public static final String userApproximateData = "app/userApproximateData.html";//首页侧滑数据
    public static final String resetBindtradepass = "app/resetBindtradepass.html";//重置手机号
    public static final String faceIdGetH5TokenPrimary = "app/faceIdGetH5TokenPrimary.html";//高级认证
    public static final String getEntrstCashBackCharges = "app/getEntrstCashBackCharges.html";//交易返现
    public static final String fingerprintVerification = "app/fingerprintVerification.html";//指纹认证
    public static final String getUserEntrustCashBackCharges = "app/getUserEntrustCashBackCharges.html";//交易返现统计
    public static final String changePassword = "app/changePassword.html";//改变交易密码
    public static final String exchangeDepth = "app/exchangeDepthV2.html";//获取交易对数据
    //    public static final String exchangeDepth = "app/exchangeDepthV3.html";//获取交易对数据
    //    public static final String exchangeDepth = "app/exchangeDepthV3.html";//获取交易对数据
    public static final String exchangeDepthV3 = "app/exchangeDepthV3.html";//K线页改版-获取交易对数据
    public static final String exchangeMarket = "app/exchangeMarket.html";//获取行情数据
    public static final String exchangeETFMarket = "app/etf/exchangeMarket.html";//获取ETF行情数据
    public static final String exchangeRiseFall = "app/exchangeRiseFall.html";//首页
    public static final String exchangeKline = "app/exchangeKline.html";//首页
    public static final String getConsulting = "app/getConsulting.html";//快讯
    public static String bindHyperpay = DOMAIN + "app/bindHyperpay.html";//获取hpy的绑定信息
    public static String unBindHyperpay = DOMAIN + "app/unBindHyperpay.html";//hpy的解绑
    public static String hyperpayWithdraw = DOMAIN + "app/hyperpayWithdraw.html";//hpy提现
    public static String hyperpayRecords = DOMAIN + "app/hyperpayRecords.html";//hpy财务记录
    public static String createRecharge = DOMAIN + "app/createRecharge.html";//hpy财务记录
    public static String nodeVipPurchase = DOMAIN + "app/nodeVipPurchase.html";//vip购买信息
    public static String saveVipPurchase = DOMAIN + "app/saveVipPurchase.html";//vip购买
    public static String vipPurchaseList = DOMAIN + "app/vipPurchaseList.html";//vip购买记录
    public static String mydivide = DOMAIN + "app/mydivide.html";//推荐信息
    public static String jsonProfit = DOMAIN + "app/jsonProfit.html";//以获取的佣金
    public static String jsonDetails = DOMAIN + "app/jsonDetails.html";//以推荐的朋友
    public static String limitedTimeTips = DOMAIN + "app/limitedTimeTips.html"; // 获取限时币信息
    public static String open_coins = DOMAIN + "app/otc/open/coins.html";//OTC 可用币种信息
    public static String ACTION_ONE_KEY = DOMAIN + "app/otc/open/coin/oneKey";//OTC 一键买卖币支持的类型接口与coins.html一致
    public static String open_coins_ = DOMAIN + "app/otc/open/legal-coins.html";//OTC 可用币种信息 新接口
    public static String transfer = DOMAIN + "app/otc/asset/transfer.html";//OTC 资产互转
    public static String asset_records = DOMAIN + "app/otc/asset/records.html";//OTC 资产互转,记录
    public static String advertisements = DOMAIN + "app/otc/open/advertisements.html";//OTC获取 首页展示广告
    public static String add_order = DOMAIN + "app/otc/add-order.html";//OTC 用户买卖下单
    public static String update_userinfo = DOMAIN + "app/otc/user/update-userinfo.html";//OTC 设置用户基本信息
    public static String get_base_userinfo = DOMAIN + "app/otc/user/get-base-userinfo.html";//OTC 获取用户基本信息
    public static String pay_order = DOMAIN + "app/otc/pay-order.html";//OTC 买卖订单查看支付页面
    public static String avgAdvertisementPrice = DOMAIN + "app/otc/one/key/avgAdvertisementPrice";//一键买币  单价
    public static String get_orders = DOMAIN + "app/otc/get-orders.html";//OTC 订单管理，根据状态查询订单
    public static String getPaymentList = DOMAIN + "app/otc/pay-account/page.html";//OTC 已经设置的支付列表
    public static String PayEnabled = DOMAIN + "app/otc/pay-account/enabled.html";//OTC 启用支付账号
    public static String PayDisabled = DOMAIN + "app/otc/pay-account/disabled.html";//OTC 禁用支付账号
    public static String AddAcount = DOMAIN + "app/otc/pay-account/add.html";//OTC 添加银行卡
    public static String update_bankCard = DOMAIN + "app/otc/pay-account/update.html";//OTC 添加银行卡
    public static String deleteBankCard = DOMAIN + "app/otc/pay-account/delete.html";//删除银行卡，支付宝，微信
    public static String upload_image = DOMAIN + "app/otc/user/upload.html";//OTC 上传图片
    public static String confirm_order = DOMAIN + "app/otc/confirm-order.html";//OTC  订单确认付款(商户,用户确认付款)
    public static String get_order = DOMAIN + "app/otc/get-order.html";//OTC  用户获取订单详情
    public static String complain_order = DOMAIN + "app/otc/complain-order.html";//OTC  发起申诉
    public static String cancel_complain = DOMAIN + "app/otc/cancel-complain.html";//OTC  取消申诉
    public static String cancel_order = DOMAIN + "app/otc/cancel-order.html";//OTC  取消订单
    public static String order_payment = DOMAIN + "app/otc/order-payment.html";//OTC  用户出售选择支付方式
    public static String order_release = DOMAIN + "app/otc/order-release.html";//OTC  用户出售后，确认收款
    public static String getTotalUserWallet = DOMAIN + "app/getTotalUserWallet.html";//获取用户总资产
    public static String getOtcUserWallet = DOMAIN + "app/getOtcUserWallet.html";//获取用户OTC买币资产
    public static String complain_check = DOMAIN + "app/otc/complain-check.html";//otc查询是否可申诉
    public static String otc_user_limit = DOMAIN + "app/otc/user/otc-user-limit.html";//otc是否开启风控
    public static String order_tip = DOMAIN + "app/otc/order-tip.html";//查询otc订单提醒
    public static String openList = DOMAIN + "app/otc/pay-account/openList";//开启的支付方式
    public static String order_config = DOMAIN + "app/otc/one/key/config";//订单配置，可买 可买限制
    public static String advertisement_list = DOMAIN + "app/otc/advertisement/list-by-adid.html";//otc获取商家信息页的广告
    public static String getOrgInfo = DOMAIN + "app/otc/open/getOrgInfo.html";//otc获取商家信息页的商家信息
    public static String ADVERTISEMENT_DELETE = DOMAIN + "app/otc/advertisement/del"; //删除广告
    public static String validate = DOMAIN + "app/otc/advertisement/validate.html";//otc点击购买和出售的时候判断
    public static String configGet = DOMAIN + "app/otc/config/get.html";//otc点击购买和出售的时候判断
    public static String advertisement = DOMAIN + "app/otc/one/key/advertisement";//查询撮合广告
    public static String add = DOMAIN + "app/otc/one/key/add";//一键提交
    public static String shelve = DOMAIN + "app/otc/advertisement/user-shelve.html";//otc广告上架
    public static String unshelve = DOMAIN + "app/otc/advertisement/user-unshelve.html";//otc广告下架
    public static String assetInfo = DOMAIN + "app/otc/asset/assetInfo.html"; // otc查询我资产信息
    public static String getAliVerifySdkToken = DOMAIN + "app/faceIdVerifyToken"; // 阿里验证前需要获取的sdktoken
    public static String checkAliVerifySdk = DOMAIN + "app/faceIdVerifyToken/check"; // 阿里验证后调用
    public static String getKycTokenUrl = DOMAIN + "app/kyc/tokenUrl"; // 获取阿里验证需要的url参数
    public static String otcIsBlack = DOMAIN + "app/faceIdVerifyToken/isBlack"; // 买币交易菜单点击检查黑名单
    //
    public static String ACTION_ENTRANCE = DOMAIN + "app/entrance/home.html"; // 首页动态区
    /****************************************/
    //IM
    public static String IM_GET_TOKEN = DOMAIN + "app/im/token/get"; // 获得IM通道TOKEN
    //im/accid/get?userId
    public static String IM_GET_ACCID = DOMAIN + "app/im/accid/get"; // 获取对方的网易云信用户名ACCID
    /****************************************/
    public static String sharePic = DOMAIN + "app/sharePic.html";//以推荐的朋友
    public static String ACTION_SHARE_WELFARE = DOMAIN + "app/welfare/user/share.html";//福利中心以推荐的朋友
    public static String REDENVELOPE_INVITE_URL = DOMAIN + "app/redenvelope/invite/info";//红包邀请
    public static String REDENVELOPE_URL = WEB_DOMAIN + "app/redenvelope/activity.html?tabIndex=0";//红包列表
    public static String MY_REDENVELOPE_URL = WEB_DOMAIN + "app/redenvelope/activity.html?tabIndex=1";//我的红包
    public static String NOVICE = DOMAIN + "buy_Bitcoin_guide";//新手营
    public static String BYB = DOMAIN + "financial/view/activity";//币赢宝
    public static String LIST_AREA = DOMAIN + "app/certificationArea.html";//地区
    public static String TRADING_AREA_MAIN = "app/trade/area.html";//包含二级交易区
    public static String hyperpay_download_rul = "https://www.hyperpay.tech/app_down";//hyperpay下载地址
    public static String CONTRACT_ASSET = DOMAIN + "app/contract/asset";//查询合约账户资产 loginToken  coinCode
    public static String CONTRACT_FINANCIAL = DOMAIN + "app/contract/contractFinancial";//合约列表
    public static String INVITE = WEB_DOMAIN + "app/viewIntro";//邀请返佣
    //ETF负责声明
    public static  String ACTION_DISCLAIMER = DOMAIN + "app/etf/disclaimer/get.html";//获取用户是否已经同意免责声明
    public static  String ACTION_DISCLAIMER_AGREE = DOMAIN + "app/etf/disclaimer/agree.html";//获取用户是否已经同意免责声明

    public static  String AGREEMENT_URL_ZH = "https://coinw.zendesk.com/hc/zh-cn/articles/360046853913";//Coinw用户协议
    public static  String AGREEMENT_URL_EN = "https://coinw.zendesk.com/hc/en-us/articles/360046853913";//Coinw用户协议
    public static  String AGREEMENT_URL_KO = "https://coinw.zendesk.com/hc/ko-kr/articles/360046853913";//Coinw用户协议

    public static  String EDIT_VALIDATE = DOMAIN + "app/otc/advertisement/update/validate";//列表点击编辑的验证
    public static  String ACTION_HBT = DOMAIN + "app/viewHbt.html";//HBT4
    //
    public static String ACTION_VIEWWELFARE = DOMAIN + "app/viewWelfare";//
    //
    public static String ACTION_CONFIG_DEPTH = DOMAIN + "open/depth/config";//合并深度2.0（只支持get请求）
    public static String ACTION_CONFIG_HEADER = DOMAIN + "open/sys/config/seckill/header.html";//OTC开关控制


    //
    public static String ACTION_BCH_HARD = DOMAIN + "app/bchHardForkDesc";//分叉业务配置接口
    /**
     * 计价方式
     *
     * @return
     */
    public static String GET_EXCHANGE_RATE = DOMAIN + "app/contract/exchangeRate";  //获取汇率

    /**
     * 币贷宝
     *
     * @return
     */
    public static String GET_BDB_AGREEMENT_STATUS = DOMAIN + "app/coinloan/status";  //查询是否开通币贷宝
    public static String OPEN_BDB_AGREEMENT = DOMAIN + "app/coinloan/open";  //开通币贷宝账户
    public static String GET_BDB_BALANCE = DOMAIN + "app/coinloan/coinLoanFinancial";  //查询币贷宝资产信息

    public static String getTradeBdb() {
        return WEB_DOMAIN + "app/viewCoinLoan.html";  //币贷宝交易h5页面
    }

    /**
     * JPush
     *
     * @return
     */
    public static String JPUSH_BIND = DOMAIN + "app/userBind/bind";  //极光register_id绑定
    /**
     * /**
     * HBT合伙人海报接口
     */
    public static String HBT_GET_POSTER = DOMAIN + "app/poster/get";  //HBT合伙人海报接口
    public static String HBT_H5_URL = DOMAIN + "app/view/hbtPartner";  //HBT合伙人海报接口

    /**
     * 网络检测
     */
    public static String getNetworkDetect() {//网络检测h5页面
        return WEB_DOMAIN + "h5/monitoring?system=android";
    }

    public static String UPLOAD_NETWORK_DETECT_PIC = DOMAIN + "app/detection/upload";  //网络检测图片上传接口
    public static String NETWORK_SPEED_TEST = "https://btc018.oss-cn-shenzhen.aliyuncs.com/app_download_test/5MB.zip";  //下载资源文件测速


    /**
     * 新手首页
     */
    public static String NEW_USER_HOME_PRO_IN = DOMAIN + "app/user/profession/in.html";  //标记用户确定进入专业版
    public static String NEW_USER_HOME_STEP = DOMAIN + "app/user/index/step.html";  //获取APP新首页步骤
    /**
     * K线页重构相应接口
     *
     * @return
     */
    public static String KLINE_WIKI = DOMAIN + "app/getEncyclopediaV2.html";//币种百科

    public static String ALI_VERIFY = DOMAIN + "app/nvcVerify.html";  //阿里人机验证成功后需要调用该接口
    public static String ALI_VERIFY2 = DOMAIN + "app/findLoginPassword/nvcAndContactVerify.html";  //阿里人机验证成功后需要调用该接口
    /**
     * 红包V4
     *
     * @return
     */
    public static String GET_RED_ENVELOPE = DOMAIN + "app/redenvelope/taken";//领取新手红包
    /**
     * 海外优化
     */
    public static String GET_WITHDRAW_LIMIT = DOMAIN + "app/user/withdraw/quotas";//获取提现的额度限制
    public static String GET_ONFIDO_TOKEN = DOMAIN + "app/kyc/overseas/token/get";//获取onfido的token
    public static String CHECK_ONFIDO_RESULT = DOMAIN + "app/kyc/overseas/checks";//onfido sdk完成后的check
    /**
     * 币赢理财
     */
    public static String GET_FINANCE_RECORD = DOMAIN + "app/financial/record";//获取财务记录
    /**
     * 活动弹窗（后台要求，app不用传?channel=1）
     */
    public static String ACTION_ACTIVITY_AD = DOMAIN + "app/activityAd";//活动弹窗业务

    public static String GET_AREA_CODE = "open/coinw/common/countrys";//城市区号列表

    public static String HOME_POPUP = "open/home/popup/getHomePopupContent";//首页弹窗 聚合所有弹窗业务 按优先级弹窗

    public static String getFansUp() {
        return WEB_DOMAIN + "app/viewFansUp.html";//FansUp
    }

    public static String getRedRuleUrl(Context context) {
        String url = "";
        if (LogicLanguage.getLanguage(context).contains("zh")) {
            url = "https://coinw.zendesk.com/hc/zh-cn/articles/360052353313";
        } else if (LogicLanguage.getLanguage(context).contains("en")) {
            url = "https://coinw.zendesk.com/hc/en-us/articles/360052353313";
        } else if (LogicLanguage.getLanguage(context).contains("ko")) {
            url = "https://coinw.zendesk.com/hc/ko-kr/articles/360052353313";
        }
        return url;
    }
    public static String getHelpUrl(Context context) {
        String url = "";
        if (LogicLanguage.getLanguage(context).contains("zh")) {
            url = "https://coinw.zendesk.com/hc/zh-cn";
        } else if (LogicLanguage.getLanguage(context).contains("en")) {
            url = "https://coinw.zendesk.com/hc/en-us";
        } else if (LogicLanguage.getLanguage(context).contains("ko")) {
            url = "https://coinw.zendesk.com/hc/ko-kr";
        }
        return url;
    }
    public static String getDownloadHpp(Context context) {
        String url = "";
        if (LogicLanguage.getLanguage(context).contains("zh")) {
            url = "https://hyperpay.me/app_down?lang=zh-cn";
        } else if (LogicLanguage.getLanguage(context).contains("en")) {
            url = "https://hyperpay.me/app_down?lang=en-us";
        } else if (LogicLanguage.getLanguage(context).contains("ko")) {
            url = "https://hyperpay.me/app_down?lang=ko-kr";
        }
        return url;
    }

    public static String getOtcAdEdit() {
        return AppUtils.getOtcAdDns() + "app/otc/advertisement/view/edit/%d";
    }//编辑广告

    public static String getOtcAdPush() {
        return AppUtils.getOtcAdDns() + "app/otc/advertisement/view/push";
    }//发布广告

    /**
     * 获取合约用户信息
     */
    public static String GET_HY_USER = DOMAIN + "app/contract/user";
    /**
     * 开通合约账户
     */
    public static String OPEN_HY_ACCOUNT = DOMAIN + "app/contract/open.html";

    /**
     * 多链优化
     */
    public static String GET_RECHARGE_ADDRESS = DOMAIN + "app/getDepositAddress";//读取充值的链信息
    public static String GET_WITHDRAW_ADDRESS = DOMAIN + "app/getWithdrawAddress";
    public static String GET_USER_WALLET = DOMAIN + "app/getUserWallet/v1";//读取资产
    /**
     * 3.0 UI重构
     */
    public static String GET_SCROLL_NOTICE = DOMAIN + "open/coinw/notice/hp/get";//首页滚动公告
    public static String UPDATE_SELF        = DOMAIN + "app/updateSelfSelection";//更新自选
    public static String GET_SELF_LIST      = DOMAIN + "app/getSelfSelection";//获取自选列表

    public static void setWebDomain(String webDomain){
        INVITE = webDomain + "app/viewIntro";//邀请返佣
        REDENVELOPE_URL=REDENVELOPE_URL.replace(WEB_DOMAIN,webDomain);
        MY_REDENVELOPE_URL=MY_REDENVELOPE_URL.replace(WEB_DOMAIN,webDomain);
        WEB_DOMAIN = webDomain;
    }

    public static void setDomain(String domain) {

        UNPAID_ORDER                = UNPAID_ORDER                .replace(DOMAIN, domain);
        order                       = order                       .replace(DOMAIN, domain);
        verifyTradePwd              = verifyTradePwd              .replace(DOMAIN, domain);
        bindHyperpay                = bindHyperpay                .replace(DOMAIN, domain);
        unBindHyperpay              = unBindHyperpay              .replace(DOMAIN, domain);
        hyperpayWithdraw            = hyperpayWithdraw            .replace(DOMAIN, domain);
        hyperpayRecords             = hyperpayRecords             .replace(DOMAIN, domain);
        createRecharge              = createRecharge              .replace(DOMAIN, domain);
        nodeVipPurchase             = nodeVipPurchase             .replace(DOMAIN, domain);
        saveVipPurchase             = saveVipPurchase             .replace(DOMAIN, domain);
        vipPurchaseList             = vipPurchaseList             .replace(DOMAIN, domain);
        mydivide                    = mydivide                    .replace(DOMAIN, domain);
        jsonProfit                  = jsonProfit                  .replace(DOMAIN, domain);
        jsonDetails                 = jsonDetails                 .replace(DOMAIN, domain);
        limitedTimeTips             = limitedTimeTips             .replace(DOMAIN, domain);
        open_coins                  = open_coins                  .replace(DOMAIN, domain);
        ACTION_ONE_KEY              = ACTION_ONE_KEY              .replace(DOMAIN, domain);
        open_coins_                 = open_coins_                 .replace(DOMAIN, domain);
        transfer                    = transfer                    .replace(DOMAIN, domain);
        asset_records               = asset_records               .replace(DOMAIN, domain);
        advertisements              = advertisements              .replace(DOMAIN, domain);
        add_order                   = add_order                   .replace(DOMAIN, domain);
        update_userinfo             = update_userinfo             .replace(DOMAIN, domain);
        get_base_userinfo           = get_base_userinfo           .replace(DOMAIN, domain);
        pay_order                   = pay_order                   .replace(DOMAIN, domain);
        avgAdvertisementPrice       = avgAdvertisementPrice       .replace(DOMAIN, domain);
        get_orders                  = get_orders                  .replace(DOMAIN, domain);
        getPaymentList              = getPaymentList              .replace(DOMAIN, domain);
        PayEnabled                  = PayEnabled                  .replace(DOMAIN, domain);
        PayDisabled                 = PayDisabled                 .replace(DOMAIN, domain);
        AddAcount                   = AddAcount                   .replace(DOMAIN, domain);
        update_bankCard             = update_bankCard             .replace(DOMAIN, domain);
        deleteBankCard              = deleteBankCard              .replace(DOMAIN, domain);
        upload_image                = upload_image                .replace(DOMAIN, domain);
        confirm_order               = confirm_order               .replace(DOMAIN, domain);
        get_order                   = get_order                   .replace(DOMAIN, domain);
        complain_order              = complain_order              .replace(DOMAIN, domain);
        cancel_complain             = cancel_complain             .replace(DOMAIN, domain);
        cancel_order                = cancel_order                .replace(DOMAIN, domain);
        order_payment               = order_payment               .replace(DOMAIN, domain);
        order_release               = order_release               .replace(DOMAIN, domain);
        getTotalUserWallet          = getTotalUserWallet          .replace(DOMAIN, domain);
        getOtcUserWallet            = getOtcUserWallet            .replace(DOMAIN, domain);
        complain_check              = complain_check              .replace(DOMAIN, domain);
        otc_user_limit              = otc_user_limit              .replace(DOMAIN, domain);
        order_tip                   = order_tip                   .replace(DOMAIN, domain);
        openList                    = openList                    .replace(DOMAIN, domain);
        order_config                = order_config                .replace(DOMAIN, domain);
        advertisement_list          = advertisement_list          .replace(DOMAIN, domain);
        getOrgInfo                  = getOrgInfo                  .replace(DOMAIN, domain);
        ADVERTISEMENT_DELETE        = ADVERTISEMENT_DELETE        .replace(DOMAIN, domain);
        validate                    = validate                    .replace(DOMAIN, domain);
        configGet                   = configGet                   .replace(DOMAIN, domain);
        advertisement               = advertisement               .replace(DOMAIN, domain);
        add                         = add                         .replace(DOMAIN, domain);
        shelve                      = shelve                      .replace(DOMAIN, domain);
        unshelve                    = unshelve                    .replace(DOMAIN, domain);
        assetInfo                   = assetInfo                   .replace(DOMAIN, domain);
        getAliVerifySdkToken        = getAliVerifySdkToken        .replace(DOMAIN, domain);
        checkAliVerifySdk           = checkAliVerifySdk           .replace(DOMAIN, domain);
        getKycTokenUrl              = getKycTokenUrl              .replace(DOMAIN, domain);
        otcIsBlack                  = otcIsBlack                  .replace(DOMAIN, domain);
        ACTION_ENTRANCE             = ACTION_ENTRANCE             .replace(DOMAIN, domain);
        IM_GET_TOKEN                = IM_GET_TOKEN                .replace(DOMAIN, domain);
        IM_GET_ACCID                = IM_GET_ACCID                .replace(DOMAIN, domain);
        sharePic                    = sharePic                    .replace(DOMAIN, domain);
        ACTION_SHARE_WELFARE        = ACTION_SHARE_WELFARE        .replace(DOMAIN, domain);
        REDENVELOPE_INVITE_URL      = REDENVELOPE_INVITE_URL      .replace(DOMAIN, domain);
        NOVICE                      = NOVICE                      .replace(DOMAIN, domain);
        BYB                         = BYB                         .replace(DOMAIN, domain);
        LIST_AREA                   = LIST_AREA                   .replace(DOMAIN, domain);
        CONTRACT_ASSET              = CONTRACT_ASSET              .replace(DOMAIN, domain);
        CONTRACT_FINANCIAL          = CONTRACT_FINANCIAL          .replace(DOMAIN, domain);
        ACTION_DISCLAIMER           = ACTION_DISCLAIMER           .replace(DOMAIN, domain);
        ACTION_DISCLAIMER_AGREE     = ACTION_DISCLAIMER_AGREE     .replace(DOMAIN, domain);
        EDIT_VALIDATE               = EDIT_VALIDATE               .replace(DOMAIN, domain);
        ACTION_HBT                  = ACTION_HBT                  .replace(DOMAIN, domain);
        ACTION_VIEWWELFARE          = ACTION_VIEWWELFARE          .replace(DOMAIN, domain);
        ACTION_CONFIG_DEPTH         = ACTION_CONFIG_DEPTH         .replace(DOMAIN, domain);
        ACTION_CONFIG_HEADER        = ACTION_CONFIG_HEADER        .replace(DOMAIN, domain);
        ACTION_BCH_HARD             = ACTION_BCH_HARD             .replace(DOMAIN, domain);
        GET_EXCHANGE_RATE           = GET_EXCHANGE_RATE           .replace(DOMAIN, domain);
        GET_BDB_AGREEMENT_STATUS    = GET_BDB_AGREEMENT_STATUS    .replace(DOMAIN, domain);
        OPEN_BDB_AGREEMENT          = OPEN_BDB_AGREEMENT          .replace(DOMAIN, domain);
        GET_BDB_BALANCE             = GET_BDB_BALANCE             .replace(DOMAIN, domain);
        JPUSH_BIND                  = JPUSH_BIND                  .replace(DOMAIN, domain);
        HBT_GET_POSTER              = HBT_GET_POSTER              .replace(DOMAIN, domain);
        HBT_H5_URL                  = HBT_H5_URL                  .replace(DOMAIN, domain);
        UPLOAD_NETWORK_DETECT_PIC   = UPLOAD_NETWORK_DETECT_PIC   .replace(DOMAIN, domain);
        NEW_USER_HOME_PRO_IN        = NEW_USER_HOME_PRO_IN        .replace(DOMAIN, domain);
        NEW_USER_HOME_STEP          = NEW_USER_HOME_STEP          .replace(DOMAIN, domain);
        KLINE_WIKI                  = KLINE_WIKI                  .replace(DOMAIN, domain);
        ALI_VERIFY                  = ALI_VERIFY                  .replace(DOMAIN, domain);
        ALI_VERIFY2                 = ALI_VERIFY2                 .replace(DOMAIN, domain);
        GET_RED_ENVELOPE            = GET_RED_ENVELOPE            .replace(DOMAIN, domain);
        GET_WITHDRAW_LIMIT          = GET_WITHDRAW_LIMIT          .replace(DOMAIN, domain);
        GET_ONFIDO_TOKEN            = GET_ONFIDO_TOKEN            .replace(DOMAIN, domain);
        CHECK_ONFIDO_RESULT         = CHECK_ONFIDO_RESULT         .replace(DOMAIN, domain);
        GET_FINANCE_RECORD          = GET_FINANCE_RECORD          .replace(DOMAIN, domain);
        ACTION_ACTIVITY_AD          = ACTION_ACTIVITY_AD          .replace(DOMAIN, domain);
        GET_HY_USER                 = GET_HY_USER                 .replace(DOMAIN, domain);
        OPEN_HY_ACCOUNT             = OPEN_HY_ACCOUNT             .replace(DOMAIN, domain);
        GET_RECHARGE_ADDRESS        = GET_RECHARGE_ADDRESS        .replace(DOMAIN, domain);
        GET_WITHDRAW_ADDRESS        = GET_WITHDRAW_ADDRESS        .replace(DOMAIN, domain);
        GET_USER_WALLET             = GET_USER_WALLET             .replace(DOMAIN, domain);

        GET_SCROLL_NOTICE = GET_SCROLL_NOTICE.replace(DOMAIN, domain);
        UPDATE_SELF = UPDATE_SELF.replace(DOMAIN, domain);
        GET_SELF_LIST = GET_SELF_LIST.replace(DOMAIN, domain);

        DOMAIN = domain;
    }

}
