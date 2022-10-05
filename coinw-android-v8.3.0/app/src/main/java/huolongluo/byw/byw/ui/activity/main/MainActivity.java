package huolongluo.byw.byw.ui.activity.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.android.coinw.biz.event.BizEvent;
import com.android.coinw.biz.trade.helper.TradeDataHelper;
import com.android.coinw.utils.Utilities;
import com.android.legend.common.ContractModuleBridge;
import com.android.legend.model.BaseResponse;
import com.android.legend.model.CommonResult;
import com.android.legend.model.config.CurrencyPairBean;
import com.android.legend.model.enumerate.transfer.TransferAccount;
import com.android.legend.socketio.SocketIOClient;
import com.android.legend.ui.contract.ContractFragment;
import com.android.legend.ui.home.HomeFragment;
import com.android.legend.ui.market.MarketBBSideFirstFragment;
import com.android.legend.ui.market.MarketETFSideFirstFragment;
import com.android.legend.ui.market.MarketFirstFragment;
import com.android.legend.ui.transfer.AccountTransferActivity;
import com.blankj.utilcodes.utils.TimeUtils;
import com.blankj.utilcodes.utils.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.onlynight.noswipeviewpager.library.NoSwipeViewPager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.legend.common.event.TokenExpired;
import com.legend.modular_contract_sdk.api.EventCallback;
import com.legend.modular_contract_sdk.api.ModularContractSDK;
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils;
import com.legend.modular_contract_sdk.common.DialogUtilKt;
import com.legend.modular_contract_sdk.common.event.JumpToContractTransfer;
import com.legend.modular_contract_sdk.common.event.SelectExperienceGoldEvent;
import com.legend.modular_contract_sdk.repository.model.BaseResp;
import com.legend.modular_contract_sdk.repository.model.ExperienceGold;
import com.legend.modular_contract_sdk.ui.experience_gold.ExperienceGoldViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import cn.jpush.android.api.JPushInterface;
import huolongluo.byw.BuildConfig;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseActivity;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.bean.ActivityAD;
import huolongluo.byw.byw.bean.HomePopup;
import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.byw.bean.UserInfoBean;
import huolongluo.byw.byw.bean.VersionInfo;
import huolongluo.byw.byw.inform.activity.NoticeDetailActivity;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.net.okhttp.HttpUtils;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.StartUpActivity;
import huolongluo.byw.byw.ui.activity.ThirdLaunchActivity;

import com.android.legend.ui.login.LoginActivity;
import com.legend.modular_contract_sdk.utils.JsonUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.CenterPopupView;

import huolongluo.byw.byw.ui.fragment.bdb.bean.BdbFinancialAgreementStatus;
import huolongluo.byw.byw.ui.fragment.contractTab.ContractUserInfoEntity;
import huolongluo.byw.byw.ui.fragment.contractTab.nima.NMAirDropEntity;
import huolongluo.byw.byw.ui.fragment.maintab01.MarketNewFragment;
import huolongluo.byw.byw.ui.fragment.maintab01.home.AppJumpHelper;
import huolongluo.byw.byw.ui.fragment.maintab05.AllFinanceFragment;
import huolongluo.byw.byw.ui.fragment.maintab05.C2CFragmnet;
import huolongluo.byw.byw.ui.oneClickBuy.C2cStatus;
import huolongluo.byw.byw.ui.oneClickBuy.FastTradeFragment;
import huolongluo.byw.byw.ui.present.MarketDataPresent;
import huolongluo.byw.byw.ui.redEnvelope.AppManagers;
import huolongluo.byw.byw.ui.redEnvelope.DragFloatActionButton;
import huolongluo.byw.byw.ui.redEnvelope.RedEnvelopeAnimation;
import huolongluo.byw.byw.ui.redEnvelope.RedEnvelopeEntity;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.heyue.HeYueUtil;
import huolongluo.byw.heyue.ui.TransActionHomeFragment;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.manager.AppManager;
import huolongluo.byw.model.RedEnvelope;
import huolongluo.byw.model.result.SingleResult;
import huolongluo.byw.reform.bean.ExchangeRate;
import huolongluo.byw.reform.c2c.oct.activity.OtcBuyConfirmActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcPaymentActivity;
import huolongluo.byw.reform.c2c.oct.activity.sellactivity.OtcUserSellOtherPayedActivity;
import huolongluo.byw.reform.c2c.oct.activity.sellactivity.OtcUserSellPaymentActivity;
import huolongluo.byw.reform.c2c.oct.bean.C2cIsShowBean;
import huolongluo.byw.reform.c2c.oct.bean.PayOrderInfoBean;
import huolongluo.byw.reform.c2c.oct.fragment.OtcActivity;
import huolongluo.byw.reform.home.activity.JSCallJavaInterface;
import huolongluo.byw.reform.home.activity.NewsWebviewActivity;
import huolongluo.byw.reform.home.activity.RedEnvelopeWebviewActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.AgreementUtils;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.CurrencyPairUtil;
import huolongluo.byw.util.DeviceUtils;
import huolongluo.byw.util.FastClickUtils;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.UpgradeUtils;
import huolongluo.byw.util.UtilSystem;
import huolongluo.byw.util.config.ConfigurationUtils;
import huolongluo.byw.util.domain.DomainUtil;
import huolongluo.byw.util.notification.NotificationUtil;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import huolongluo.bywx.OnResultCallback;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.DataUtils;
import huolongluo.bywx.utils.DoubleUtils;
import huolongluo.bywx.utils.EncryptUtils;
import huolongluo.bywx.utils.HotDataUtils;
import huolongluo.bywx.utils.PermissionUtils;
import okhttp3.Request;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static huolongluo.byw.byw.ui.oneClickBuy.FastTradeFragment.BUY;
import static huolongluo.byw.byw.ui.oneClickBuy.FastTradeFragment.SELL;

/**
 * <p>
 * Created by 火龙裸 on 2017/8/10.
 */
public class MainActivity extends BaseActivity {

    private ImageView cancle_iv1;
    private View obscuration_view;
    private ImageView xianshiWarn_cloasetv;
    private LinearLayout main_view;
    private LinearLayout xianshi_view;
    public DrawerLayout drawer_layout;
    //四个Fragment
    private MarketFirstFragment marketFragment; // 行情
    private C2CFragmnet c2CFragmnet; // c2c
    private ContractFragment mContractFragment;//买币交易界面
    private AllFinanceFragment allFinanceFragment; //资产
    private HomeFragment homeFragment2;
    private String waitType = "";//悬浮出售倒计时标题
    private TransActionHomeFragment transactionHomeFragment;//交易界面
    public static int tag = 0;//假如等于0就什么都不做，为1则切换到买卖Fragment界面
    private LocalBroadcastManager broadcastManager;
    public RadioGroup radioGroup;
    public NoSwipeViewPager viewPager;
    private String newsId;
    private TextView showWarnViewTitle;
    private TextView showWarnView;
    private Fragment[] fragments;
    // 请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;
    public static int currentPagePosition = 3;
    private String TAG = "MainActivity";
    private long lastTimeMills = System.currentTimeMillis();
    private DragFloatActionButton redEnvelope;
    private FastTradeFragment fastTradeFragment;
    private RelativeLayout pay_orderView;
    private Timer timerExchangeRateFirst;
    private Timer timerExchangeRate;
    private Timer timerCurrencyPair;
    private Timer timerAgreement;
    private Timer loopTimer;//循环定时器
    private int loopIndex = 0;
    private int lastCheckedId = R.id.radioB_1;//停机维护用于记录上一次选中的tabid
    private MainViewModel viewModel;
    //用于记录开通协议接口是否调用成功,成功后不用再调用接口
    private boolean isHyOpenStatusGetSuccess = false, isBdbOpenStatusGetSuccess = false;

    @Override
    protected void injectDagger() {
        activityComponent().inject(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    public void gotoHome() {
        if (radioGroup != null) {
            radioGroup.check(R.id.radioB_1);
        }
    }

    public void gotoMarkets() {
        if (radioGroup != null) {
            radioGroup.check(R.id.radioB_2);
        }
    }

    public void gotoTrade(MarketListBean bean) {
        EventBus.getDefault().post(new Event.ChangeOption(bean.getCoinName()));
        selectCoin(bean.getCoinName(), bean.getCnyName(), Integer.valueOf(bean.getId()), bean.getSelfselection());
    }

    public void gotoETFTrade(MarketListBean bean) {
        EventBus.getDefault().post(new Event.ChangeOption(bean.getCoinName()));
        selectCoin(bean.getCoinName(), bean.getCnyName(), Integer.valueOf(bean.getId()), bean.getSelfselection(), TransActionHomeFragment.TYPE_ETF, 0, true);
    }

    public void gotoSwapForContractId(int contractId) {
        if (radioGroup != null) {
            radioGroup.check(R.id.radioB_4);
        }
    }

    public void gotoSwap(String coinName) {
        if (radioGroup != null) {
            radioGroup.check(R.id.radioB_4);
        }
    }

    public void gotoTrade(String coinName, String cnyName, String tmid, int selfselection) {
        Logger.getInstance().debug(TAG, "gotoTrade: " + tmid);
        EventBus.getDefault().post(new Event.ChangeOption(coinName));
        selectCoin(coinName, cnyName, Integer.valueOf(tmid), selfselection);
    }

    public void gotoTrade(int type) {
        if (transactionHomeFragment != null) {
            transactionHomeFragment.setTypeAndPosition("", type, 0);
        }
        if (radioGroup != null) {
            radioGroup.check(R.id.radioB_3);
        }
    }

    public void gotoOtc() {
        if (DataUtils.isOpenHeader()) {
            OtcActivity.launch(this);
        }
    }

    public void gotoH5(String url, boolean hideTitle, boolean useH5Title) {
        Intent intent = new Intent(this, NewsWebviewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("token", UserInfoManager.getToken());
        if (hideTitle) {
            intent.putExtra("hideTitle", hideTitle);
        }
        if (useH5Title) {
            intent.putExtra("useH5Title", useH5Title);
        }
        startActivity(intent);
    }

    public void gotoFastTrade() {
        gotoFastTrade("", false);
    }

    public void gotoFastTrade(String amount, boolean isCnyt) {
        if (!DataUtils.isOpenHeader()) {
            return;
        }
        OtcActivity.launchFastTrade(this, amount, isCnyt);
    }

    public void gotoMe() {
        if (radioGroup != null) {
            radioGroup.check(R.id.radioB_5);
        }
    }

    public void gotoNewHome(String notification) {
        if (homeFragment2 != null) {
            homeFragment2.toPage(notification);
        }
    }

    public void gotoFinance() {
        this.gotoFinance(-1);
    }

    public void gotoFinance(int type) {
        if (type == AllFinanceFragment.TYPE_FB) {
            if (!DataUtils.isOpenHeader()) {
                return;
            }
        }
        mainGotoFinance(type);
    }

    public void gotoIdentity() {
//        if (mineFragment != null) {
//            mineFragment.gotoIdentity();
//        }
    }

    public void gotoVipInfo() {
//        if (mineFragment != null) {
//            mineFragment.gotoVipInfo();
//        }
    }

    private void mainGotoFinance(int type) {//首页进入资产的公共方法
        if (UserInfoManager.isLogin()) {
            radioGroup.check(R.id.radioB_5);
            if (allFinanceFragment != null && type >= 0) {
                allFinanceFragment.switchItem(type);
            }
        } else {
            startActivity(LoginActivity.class);
        }
    }

    //交易界面切换交易对
    public void selectCoin(String coinName, String cnyName, int id, int selfStation) {
        selectCoin(coinName, cnyName, id, selfStation, TransActionHomeFragment.TYPE_BB, 0, false);
    }

    //交易界面切换交易对
    public void selectCoin(String coinName, String cnyName, int id, int selfStation, int type, int position, boolean isETF) {
        Logger.getInstance().debug(TAG, "id: " + id + " coinName: " + coinName + " cnyName: " + cnyName + " selfStation: " + selfStation);
        if (type == TransActionHomeFragment.TYPE_BB || type == TransActionHomeFragment.TYPE_ETF) {
            if (transactionHomeFragment != null) {
                transactionHomeFragment.setTypeAndPosition("", type, 0);
            }
            //EventBus.getDefault().post(new BizEvent.Trade.CoinEvent(id + "", coinName, cnyName, selfStation));
            radioGroup.check(R.id.radioB_3);
            EventBus.getDefault().post(new BizEvent.Trade.CoinEvent(id + "", coinName, cnyName, selfStation, isETF));
        }
    }

    @Override
    protected int getContentViewId() {
        ConfigurationUtils.resetLanguage(this);
        return R.layout.activity_main;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Logger.getInstance().debug(TAG, "onNewIntent");
        checkIsStopService();
        try {
            select(intent);
            gotoByScheme();
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private void checkIsStopService() {//检测是否处于停机维护
        if (CoinwHyUtils.isServiceStop) {
            setDisableStopService();
            gotoSwap("");
        }
    }


    private void select(Intent intent) {
        if (intent == null) {
            return;
        }
        String notification = intent.getStringExtra(AppConstants.NOTIFICATION.KEY_NOTIFICATION);//是否通知点击的跳转
        if (!TextUtils.isEmpty(notification)) {
            String left = intent.getStringExtra(AppConstants.NOTIFICATION.KEY_LEFT);
            String right = intent.getStringExtra(AppConstants.NOTIFICATION.KEY_RIGHT);
            NotificationUtil.toNotification(notification, left, right);//推送的通知点击的跳转
            return;
        }
        String url = intent.getStringExtra(AppConstants.NOTIFICATION.KEY_URL);//是否跳转的h5
        if (!TextUtils.isEmpty(url)) {
            gotoH5(url, false, true);
            return;
        }
        String from = intent.getStringExtra("from");//从币种详情跳过来的
        if (android.text.TextUtils.equals("CoinDetailActivity", from)) {
            int areaType = intent.getIntExtra("areaType", 0);
            if ("CNYT".equalsIgnoreCase(intent.getStringExtra("coinName"))) {
                gotoOtc();
            } else {
                if (areaType == 4) {//ETF业务
                    //TODO 待优化
                    selectCoin(intent.getStringExtra("coinName"), "", intent.getIntExtra("tradeId", 0), 0, TransActionHomeFragment.TYPE_ETF, 0, true);
                } else {
                    //TODO 待优化
                    selectCoin(intent.getStringExtra("coinName"), "", intent.getIntExtra("tradeId", 0), 0);
                }
            }
        } else if (android.text.TextUtils.equals("OtcTransferActivity", from)) {
            int type = intent.getIntExtra("type", 0);
            if (type == 1) {
                gotoOtc();
            } else {
                radioGroup.check(R.id.radioB_3);
            }
        } else if (android.text.TextUtils.equals("AuthFailureActivity", from)) {
            int type = intent.getIntExtra("type", 0);
            if (type == 1) {
                gotoHome();
            }
        } else if (TextUtils.equals(AppConstants.COMMON.FLAG_ACTION_NATIVE_INTERFACE, from)) {//说明是由H5调用Native方法
            String action = intent.getStringExtra(AppConstants.COMMON.KEY_ACTION_PARAM);
            if (TextUtils.equals(action, AppConstants.COMMON.ACTION_NATIVE_INTERFACE_OTC)) {//去OTC
                gotoOtc();
            } else if (TextUtils.equals(action, AppConstants.COMMON.ACTION_NATIVE_INTERFACE_TRADE)) {//去交易
                selectCoin(intent.getStringExtra("coinName"), "", intent.getIntExtra("tradeId", 0), 0);
            } else if (TextUtils.equals(action, AppConstants.COMMON.ACTION_NATIVE_INTERFACE_TRADE_ETF)) {//去ETF交易
                selectCoin(intent.getStringExtra("coinName"), "", intent.getIntExtra("tradeId", 0), 0, TransActionHomeFragment.TYPE_ETF, 0, true);
            } else if (TextUtils.equals(action, AppConstants.COMMON.ACTION_NATIVE_INTERFACE_SWAP)) {//去合约
                gotoSwap("");
            } else if (TextUtils.equals(action, AppConstants.COMMON.ACTION_NATIVE_INTERFACE_HOME)) {//去首页
                radioGroup.check(R.id.radioB_1);
            }
        } else if (TextUtils.equals(AppConstants.COMMON.FLAG_ACTION_CONTRACT_INTERFACE, from)) {//说明由合约调用的方法
            String action = intent.getStringExtra(AppConstants.COMMON.KEY_ACTION_PARAM);
            int position = intent.getIntExtra("position", 0);
            if (TextUtils.equals(action, AppConstants.COMMON.ACTION_NATIVE_INTERFACE_TRADE)) {//去交易
                String coinName = intent.getStringExtra("coinName");
                intent.getIntExtra("tradeId", 0);
                gotoSwap(coinName);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void toFansUp() {
        Intent intent = new Intent(this, NewsWebviewActivity.class);
        intent.putExtra("url", UrlConstants.getFansUp());
        intent.putExtra("token", UserInfoManager.getToken());
        intent.putExtra("title", "FansUp");
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!ModularContractSDK.INSTANCE.isInit()){
            // 防止ModularContractSDK单例被回收 MainActivity 创建时再次确认一边
            initContract();
        }
        AppManagers.getAppManager().addActivity(this);
        SocketIOClient.init();
        setAlias(0, "");
        initRedEnvelope();
        refreshConfigList();
        Logger.getInstance().debug(TAG, "build: " + Build.BOARD + " " + Build.BRAND + " " + Build.MODEL);
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    //加载热搜数据
                    if (Constant.STOP_SERVICE_IS_STOP_STARTUP) {
                        return;
                    }
                    HotDataUtils.load();
                    DataUtils.loadDepthConfig();
                    DataUtils.loadHeaderConfig();
                    DataUtils.loadData();
                } catch (Throwable t) {
                    Logger.getInstance().error(t);
                }
            }
        });
        gotoByScheme();
        initData();
        startTimer();
    }

    private void initData() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        initObserver();
    }

    private void initObserver() {
        viewModel.getCurrencyPairData().observe(this, new Observer<CommonResult<BaseResponse<List<CurrencyPairBean>>>>() {
            @Override
            public void onChanged(CommonResult<BaseResponse<List<CurrencyPairBean>>> baseResponseCommonResult) {
                Logger.getInstance().debug(TAG, "viewModel.getCurrencyPairData():" + GsonUtil.obj2Json(baseResponseCommonResult, CommonResult.class));
                if (baseResponseCommonResult.isSuccess()) {
                    if (baseResponseCommonResult.getData() == null || baseResponseCommonResult.getData().getData() == null ||
                            baseResponseCommonResult.getData().getData().size() == 0) {
                        return;
                    }
                    CurrencyPairUtil.saveSpData(MainActivity.this, baseResponseCommonResult.getData().getData());
                    CurrencyPairUtil.initMap(baseResponseCommonResult.getData().getData());
                }
            }
        });
    }

    private void initContract() {
        ModularContractSDK.INSTANCE.initSDK(this, "coinw",  new EventCallback() {

            @Override
            public void needLogin() {//需要登录 回调此方法时请调用登录方法
                HeYueUtil.getInstance().toLoginOrAgreementActivity();
            }

        });

        ContractModuleBridge.INSTANCE.init(this);
    }

    //获取协议的开通状态，该接口后期会整合为一个接口
    private void getAgreementOpenStatus() {
        if (!UserInfoManager.isLogin()) return;
        getHyOpenStatus();
        if (!Constant.IS_BDB_CLOSE) {//该值依赖配置接口
            getBdbOpenStatus();
        }
    }

    //外部链接唤醒app的跳转
    private void gotoByScheme() {
        Uri uri = getIntent().getParcelableExtra(Constant.KEY_THIRD_LAUNCH_URI);
        if (uri == null) {
            Logger.getInstance().debug(TAG, "gotoByScheme uri==null");
            return;
        }
        //完整url 例：coinw://pattern=web?url=https://www.baidu.com    coinw://pattern=app?url=app/myasset/fiat
        String urlComplete = uri.toString();
        Logger.getInstance().debug(TAG, "gotoByScheme 完整urlComplete:" + urlComplete);
        String host = uri.getHost();
        Logger.getInstance().debug(TAG, "gotoByScheme host:" + host);
        String[] arrs = host.split("=");
        String type = "app";
        if (arrs != null && arrs.length >= 2) {
            type = arrs[1];//web或者app
        }
        String url = uri.getQueryParameter("url");
        Logger.getInstance().debug(TAG, "gotoByScheme 参数url:" + url);
        if (TextUtils.equals(type, "web")) {
            gotoH5(url, true, false);//直接跳转h5
        } else {
            NotificationUtil.toNotification(url, "", "");
        }

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            //活动弹窗
            showSafeWarn();
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private void startTimer() {
        startExchangeRateTimerFirst();
        startCurrencyPairTimer();
        startAgreementOpenStatusTimer();
        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")) {
            startLoopTimer();//动态域名切换只在release包生效
        }
    }

    private void startExchangeRateTimerFirst() {
        List<ExchangeRate> listExchangeRate = (List<ExchangeRate>) SPUtils.getObject(MainActivity.this, Constant.KEY_SP_EXCHANGE_LIST,
                new TypeToken<List<ExchangeRate>>() {
                }.getType());//先从本地读取汇率列表
        if (listExchangeRate != null && listExchangeRate.size() > 0) {
            PricingMethodUtil.initMap(listExchangeRate);
        }
        if (timerExchangeRateFirst == null) {
            timerExchangeRateFirst = new Timer();
            timerExchangeRateFirst.schedule(new TimerTask() {
                @Override
                public void run() {
                    Logger.getInstance().debug(TAG, "startExchangeRateTimerFirst");
                    if (!TextUtils.isEmpty(PricingMethodUtil.EXCHANGE_RATE_HY)) {//正常拿到数据了则开启5分钟的轮询
                        timerExchangeRateFirst.cancel();
                        timerExchangeRateFirst = null;
                        startExchangeRateTimer();
                    } else {//未拿到数据继续轮询调用
                        Logger.getInstance().debug(TAG, "startExchangeRateTimerFirst:" + "调用获取汇率接口");
                        getExchangeRate();
                    }
                }
            }, 1000, Constant.TIME_GET_EXCHANGE_RATE_FIRST);//延迟1000执行，否则混淆后容易崩溃报错java.lang.ExceptionInInitializerError
        }
    }

    private void startExchangeRateTimer() {
        if (timerExchangeRate == null) {
            timerExchangeRate = new Timer();
            timerExchangeRate.schedule(new TimerTask() {
                @Override
                public void run() {
                    Logger.getInstance().debug(TAG, "startExchangeRateTimer:" + "调用获取汇率接口");
                    getExchangeRate();
                }
            }, Constant.TIME_GET_EXCHANGE_RATE, Constant.TIME_GET_EXCHANGE_RATE);
        }
    }

    private void startCurrencyPairTimer() {
        if (CurrencyPairUtil.getSpData(this) == null) {//本地没数据启动timer，保证一定能获取到数据
            if (timerCurrencyPair == null) {
                timerCurrencyPair = new Timer();
                timerCurrencyPair.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Logger.getInstance().debug(TAG, "startCurrencyPairTimer");
                        if (CurrencyPairUtil.getSpData(MainActivity.this) == null) {
                            viewModel.getCurrencyPair();
                        } else {
                            timerCurrencyPair.cancel();
                            timerCurrencyPair = null;
                        }
                    }
                }, 1000, Constant.TIME_GET_EXCHANGE_RATE_FIRST);//延迟1000执行，否则混淆后容易崩溃报错java.lang.ExceptionInInitializerError
            }
        } else {//本地有数据读一次接口更新数据
            viewModel.getCurrencyPair();
            CurrencyPairUtil.initMap(this);//使用本地数据初始化
        }
    }

    public void startAgreementOpenStatusTimer() {
        if (!UserInfoManager.isLogin()) return;
        if (AgreementUtils.isBdbOpen() && AgreementUtils.isHyOpen()) {//协议都开通不需要启动timer
            return;
        }

        if (timerAgreement == null) {
            timerAgreement = new Timer();
            timerAgreement.schedule(new TimerTask() {
                @Override
                public void run() {
                    Logger.getInstance().debug(TAG, "startAgreementOpenStatusTimer");
                    if (!UserInfoManager.isLogin()) {//timer还在运行用户退出需要关闭
                        timerAgreement.cancel();
                        timerAgreement = null;
                        return;
                    }
                    if (!isBdbOpenStatusGetSuccess || !isHyOpenStatusGetSuccess) {//有一个没获取成功继续调用接口
                        getAgreementOpenStatus();
                    } else {
                        timerAgreement.cancel();
                        timerAgreement = null;
                    }
                }
            }, 1000, AppConstants.TIMER.AGREEMENT_OPEN_STATUS);
        }
    }

    private void startLoopTimer() {
        if (loopTimer == null) {
            loopTimer = new Timer();
            loopTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    refreshDomain();
                }
            }, 1000, 3 * 60 * 1000L);
        }
    }

    /**
     * 循环获得动态域名
     */
    private void refreshDomain() {
        //TODO 由于目前线上用户请求动态域名超过2s或超时，故采用轮询方式获得最新动态域名，并更新缓存文件
        // 1、注意强更版本（gradle配置的域名必须为目前生产最新域名）
        // 2、目前仅加相关日志，先观察用户使用情况；若用户一直不能请求到动态域名接口，需要完善。
        loopIndex++;
        if (loopIndex > 1000) {//控制记数器不能太大（超范围）
            loopIndex = 1000;
        }
        OkhttpManager.get(UrlConstants.DYNAMIC_DOMAIN, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                String msg = TAG + "-getDomain-loopIndex:" + loopIndex + " exception:" + (e != null ? e.getMessage() : "");
                Logger.getInstance().debug(TAG, "读取动态域名失败 " + msg);
                Logger.getInstance().report("读取动态域名失败", msg);
            }

            @Override
            public void requestSuccess(String result) {
                Logger.getInstance().debug(TAG, "读取动态域名成功 loopIndex:" + loopIndex + " result:" + result);
                DomainUtil.INSTANCE.switchDomain(BaseApp.getSelf(), result, false);
            }
        });
    }

    private void stopLoopTimer() {
        if (loopTimer == null) {
            return;
        }
        loopTimer.cancel();
        loopTimer = null;
    }

    /**
     * 安全提醒（是否root）
     */
    private void showSafeWarn() {
        if (AppUtils.isSuEnable()) {
            Toast.makeText(MainActivity.this, getString(R.string.warn_root), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        Logger.getInstance().debug(TAG, "fragment-name: " + fragment.getId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConfigurationUtils.resetLanguage(this);
        unPaidOrder();

        if (JSCallJavaInterface.FromFansUpView) {
            toFansUp();
        }
        try {
            RadioButton radioB_4 = findViewById(R.id.radioB_4);
            if (DataUtils.isOpenHeader()) {
                radioB_4.setVisibility(View.VISIBLE);
            } else {
                radioB_4.setVisibility(View.GONE);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * 注册广播接收器
     */
    private void registerReceiver() {//4427660C8125F62B658FFAE9F6C376F2_1536627572130_67347
        broadcastManager = LocalBroadcastManager.getInstance(MainActivity.this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("jerry");
        broadcastManager.registerReceiver(mAdDownLoadReceiver, intentFilter);
    }

    private BroadcastReceiver mAdDownLoadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String change = intent.getStringExtra("change");
            if ("yes".equals(change)) {
                // 这地方只能在主线程中刷新UI,子线程中无效，因此用Handler来实现
                new Handler().post(new Runnable() {
                    public void run() {
                        //在这里来写你需要刷新的地方
                        //例如：testView.setText("恭喜你成功了");
                        //   setSelect(5);
                        radioGroup.check(R.id.radioB_3);
                        if (tag == 5) {
                            tag = 0; //重新初始化为0
                            gotoOtc();
                        } else if (tag == 6) {
                            tag = 0; //重新初始化为0
                            Intent intent = new Intent("jerry");
                            intent.putExtra("change", "CnytT");
                            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
                        }
                    }
                });
            } else if ("success".equals(change)) {
                if (CoinwHyUtils.isServiceStop) {
                    gotoSwap("");
                }
            } else if ("regist_success".equals(change)) {
                DialogUtils.getInstance().showRegistSucDialog(MainActivity.this);
                String data = SPUtils.getString(MainActivity.this, Constant.KEY_RED_INFO, "");
                if (!TextUtils.isEmpty(data)) {//有红包信息，注册后需要调用接口领取
                    getRedEnvelope(data);
                    SPUtils.saveString(MainActivity.this, Constant.KEY_RED_INFO, "");
                }
            }
        }
    };
    /**
     * 注销广播
     */
    public static MainActivity self;

    private TextView area_name_tv;
    private TextView trade_explain_tv1;
    private TextView trade_explain_tv2;
    private TextView trade_explain_tv3;
    private TextView trade_explain_tv4;
    private RelativeLayout trade_explain_view;
    private RelativeLayout xianzhi_explain_view;//限时区
    private LinearLayout trade_explain_tv3_ll;
    private boolean isPast;
    private KeyStore keyStore;
    private static final String DEFAULT_KEY_NAME = "default_key";
    private CancellationSignal mCancellationSignal;
    private TextView result_tv;

    //获取用户信息，判断loginToken是否过期，过期的话跳转至登录
    private void getUserInfo() {
        if (!AppUtils.isNetworkConnected()) {
            return;
        }
        //Share.get().setLogintoken("11111111212");
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", SPUtils.getLoginToken());
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GET_USER_INFO, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                // Share.get().setLogintoken("");
                SPUtils.saveLoginToken("");
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    Log.i("校验token", "code= : " + result);
                    if (code == 0) {
                        JSONObject object = jsonObject.getJSONObject("userInfo");
                        UserInfoBean infoBean = new Gson().fromJson(object.toString(), UserInfoBean.class);
                        UserInfoManager.getDeaflt().setUserInfoBean(infoBean);
                        UserInfoManager.getDeaflt().setToken(SPUtils.getLoginToken());
                        EventBus.getDefault().post(new Event.refreshInfo());
                        //登录IM
                        BaseApp.getSelf().loginIM();
                        //
                    }
                    DialogUtils.getInstance().dismiss();
                } catch (Exception e) {
                    //Share.get().setLogintoken("");
                    SPUtils.saveLoginToken("");
                    e.printStackTrace();
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(DEFAULT_KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC).setUserAuthenticationRequired(true).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
            keyGenerator.init(builder.build());
            keyGenerator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(23)
    private Cipher initCipher() {
        try {
            SecretKey key = (SecretKey) keyStore.getKey(DEFAULT_KEY_NAME, null);
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void stopListening() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    private void updateLoginToken() {
        if (!AppUtils.isNetworkConnected()) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("updateLoginToken", loginToken);//{"loginToken":"13FE5EF033DE5122915143E7B51E774F_1546051042418_67351","value":"更新成功！","result":true,"code":0}
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.updateLoginToken, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                Log.i("updateLoginToken", "errorMsg== " + errorMsg);
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        String loginToken = jsonObject.getString("loginToken");
                        if (!android.text.TextUtils.isEmpty(loginToken)) {
                            //  Share.get().setLogintoken(loginToken);
                            SPUtils.saveLoginToken(loginToken);
                            getUserInfo();
                        }
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("updateLoginToken", "result== " + result);
            }
        });
    }


    @TargetApi(Build.VERSION_CODES.M)
    void fingerprintLogin() {
        FingerprintManager manager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        if (!manager.isHardwareDetected()) {
            MToast.showButton(this, getString(R.string.aa22), 1);
            return;
        } else if (!manager.hasEnrolledFingerprints()) {
            MToast.showButton(this, getString(R.string.aa23), 1);
            return;
        }
        startFingerPrint(initCipher());
        View dialogView = DialogUtils.getInstance().showFingerprintDialog(this, new DialogUtils.onBnClickListener() {
            @Override
            public void onLiftClick(AlertDialog dialog, View view) {
                stopListening();
            }

            @Override
            public void onRightClick(AlertDialog dialog, View view) {
                stopListening();
            }
        });
        result_tv = dialogView.findViewById(R.id.result_tv);
    }

    @TargetApi(Build.VERSION_CODES.M)
    void startFingerPrint(Cipher cipher) {
        FingerprintManager manager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        Log.i("指纹识别", "是否支持硬件= " + manager.isHardwareDetected());
        Log.i("指纹识别", "是否可用= " + manager.hasEnrolledFingerprints());
        mCancellationSignal = new CancellationSignal();
        manager.authenticate(new FingerprintManager.CryptoObject(cipher), mCancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.i("指纹识别", "识别出错= " + errString);
                if (result_tv != null && result_tv != null) {
                    result_tv.setText(R.string.aa24);
                    result_tv.setTextColor(getResources().getColor(R.color.red));
                }
                MToast.show(MainActivity.this, errString + " ", 1);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
                Log.i("指纹识别", "识别hlp= " + helpString);
                if (result_tv != null && result_tv != null) {
                    result_tv.setText(R.string.aa24);
                    result_tv.setTextColor(getResources().getColor(R.color.red));
                }
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.i("指纹识别", "识别成功= " + result.getCryptoObject().getCipher().getProvider().getInfo());
                if (result_tv != null && result_tv != null) {
                    result_tv.setText(R.string.aa20);
                    result_tv.setTextColor(getResources().getColor(R.color.ff8881a6));
                }
                updateLoginToken();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.i("指纹识别", "识别失败 ");
                if (result_tv != null && result_tv != null) {
                    result_tv.setText(R.string.aa11);
                    result_tv.setTextColor(getResources().getColor(R.color.red));
                }
            }
        }, null);
    }

    String loginToken = "";

    @Override
    protected void initViewsAndEvents() {
        Logger.getInstance().debug(TAG, "initViewsAndEvents", new Exception());
        if (mSwipeBackLayout != null) {
            mSwipeBackLayout.setEnableGesture(false);
        }
        //清除全屏标记
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        trade_explain_tv1 = findViewById(R.id.trade_explain_tv1);
        trade_explain_tv2 = findViewById(R.id.trade_explain_tv2);
        trade_explain_tv3 = findViewById(R.id.trade_explain_tv3);
        trade_explain_tv4 = findViewById(R.id.trade_explain_tv4);
        trade_explain_tv3_ll = findViewById(R.id.trade_explain_tv3_ll);
        area_name_tv = findViewById(R.id.area_name_tv);
        trade_explain_view = findViewById(R.id.trade_explain_view);
        xianzhi_explain_view = findViewById(R.id.xianzhi_explain_view);
        main_view = findViewById(R.id.main_view);
        xianshi_view = findViewById(R.id.xianshi_view);
        cancle_iv1 = findViewById(R.id.cancle_iv1);
        xianshiWarn_cloasetv = findViewById(R.id.xianshiWarn_cloasetv);
        obscuration_view = findViewById(R.id.obscuration_view);
        redEnvelope = findViewById(R.id.fb);
        //国际化
//        redEnvelope.setImageResource(R.drawable.ic_redenvelope);
        Glide.with(this).load(R.drawable.redenvelope).into(redEnvelope);
        redEnvelope.setOnClickListener(view -> {
            toRedEnvelopeList();
        });
        viewClick(xianshiWarn_cloasetv, v -> showXianshiWarn(1));
        cancle_iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowTradeExplain(2);
            }
        });
        //注册成功的显示
        if (getIntent() != null) {
            if (android.text.TextUtils.equals(getIntent().getStringExtra("change"), "regist_success")) {
                DialogUtils.getInstance().showRegistSucDialog(MainActivity.this);
            }
        }

        viewPager = findViewById(R.id.viewPager);

        homeFragment2 = HomeFragment.getInstance();
        marketFragment = MarketFirstFragment.Companion.newInstance();
        //新的交易界面，老交易界面tab+合约fragment
        transactionHomeFragment = new TransActionHomeFragment();
        allFinanceFragment = new AllFinanceFragment();

        c2CFragmnet = new C2CFragmnet();
        mContractFragment = ContractFragment.getInstance();
        fastTradeFragment = new FastTradeFragment();
        fragments = new Fragment[]{homeFragment2, marketFragment, transactionHomeFragment, mContractFragment, fastTradeFragment, c2CFragmnet, allFinanceFragment};
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Logger.getInstance().debug(TAG, "fragments-length: " + fragments.length);
                if (position < fragments.length) {
                    return fragments[position];
                }
                return null;
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        };
        viewPager.setOffscreenPageLimit(6);
        viewPager.setAdapter(pagerAdapter);
        //  viewPager.setCurrentItem(1);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 3 || position == 4) {
                    currentPagePosition = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        radioGroup = findViewById(R.id.main_radio);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (pay_orderView == null || obscuration_view == null || viewPager == null || drawer_layout == null) {
                    return;
                }
                pay_orderView.setVisibility(View.GONE);
                if (!BaseApp.isNetAvailable) {
                    // MToast.show(CameraMainActivity.this, "当前网络不可用", Toast.LENGTH_SHORT);
                }
                if (obscuration_view.getVisibility() != View.GONE) {
                    obscuration_view.setVisibility(View.GONE);
                }
                //
                //
                switch (checkedId) {
                    case R.id.radioB_1:
                        if (CoinwHyUtils.checkIsStopService(MainActivity.this)) {
                            radioGroup.check(lastCheckedId);
                            return;
                        }
                        lastCheckedId = checkedId;
                        refreshConfigList();
                        setRedEnvelopeVisible(AppUtils.isRedEnvelope());
                        viewPager.setCurrentItem(0, false);
                        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        break;
                    case R.id.radioB_2:
                        lastCheckedId = checkedId;
                        setRedEnvelopeVisible(false);
                        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        viewPager.setCurrentItem(1, false);
                        break;
                    case R.id.radioB_3:
                        lastCheckedId = checkedId;
                        SPUtils.saveBoolean(MainActivity.this, "isFirst", false);
                        //TODO 侧滑
                        setRedEnvelopeVisible(false);
                        viewPager.setCurrentItem(2, false);
                        break;
                    case R.id.radioB_4:
                        if (CoinwHyUtils.checkIsStopService(MainActivity.this)) {
                            radioGroup.check(lastCheckedId);
                            return;
                        }
                        lastCheckedId = checkedId;
                        setRedEnvelopeVisible(false);
                        unPaidOrder();
                        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        viewPager.setCurrentItem(currentPagePosition, false);
                        break;
                    case R.id.radioB_5:
                        if (!UserInfoManager.isLogin()) {
                            startActivity(LoginActivity.class);
                            radioGroup.check(lastCheckedId);
                            return;
                        }
                        lastCheckedId = checkedId;
                        refreshConfigList();
                        setRedEnvelopeVisible(AppUtils.isRedEnvelope());
                        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        viewPager.setCurrentItem(6, false);
                        break;
                }
            }
        });
        if (!CoinwHyUtils.isServiceStop) {//停机维护调用该方法会触发toast
            radioGroup.check(R.id.radioB_1);
        }
        showWarnViewTitle = findViewById(R.id.tv_dialog_risk_title);
        showWarnView = findViewById(R.id.showWarnView);
        pay_orderView = findViewById(R.id.pay_order);
        //侧滑view
        drawer_layout = findViewById(R.id.drawer_layout);
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawer_layout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                isOpen = true;
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
                if (viewPager.getCurrentItem() == 2) {
                    if (transactionHomeFragment != null && transactionHomeFragment.isOpenETF()) {
                        openETF();
                    } else {
                        openTrade();
                    }
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //TODO 侧滑
                isOpen = false;
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                if (viewPager.getCurrentItem() == 2) {
                    Intent intent = new Intent("jerry");
                    intent.putExtra("change", "refresh");
                    intent.putExtra("coin", TradeDataHelper.getInstance().getCoinName());
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
                    if (transactionHomeFragment != null && transactionHomeFragment.isOpenETF()) {
                        closeETF();
                    } else {
                        closeTrade();
                    }
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        // 点击除开侧边栏的区域会收起侧边栏。
        drawer_layout.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawer_layout.closeDrawers();
                    break;
            }
            return false;
        });
        registerReceiver();
        self = this;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        //启动页传过来的
        if (getIntent() != null) {
            isPast = getIntent().getBooleanExtra("isPast", false);
            loginToken = getIntent().getStringExtra("loginToken");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //指纹登录
            if (isPast == true && !android.text.TextUtils.isEmpty(loginToken)) {
                try {
                    if (SPUtils.getFingerprint(this, false)) {
                        initKey();
                        fingerprintLogin();
                        //更新token
                        return;
                    }
                } catch (Throwable t) {
                    //说明虽然开启了Finger，但不能正常使用
                    //TODO 待验证失败情况（找到可复现的机型）
                    Logger.getInstance().error(t);
                }
            }
        }
        getVersion();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PermissionUtils.ALL_NEED_PERMISSIONS, REQUEST_PERMISSION_CODE);
            }
        }
    }

    private void initSide() {
        sideETFFragment = MarketETFSideFirstFragment.Companion.newInstance();
        sideSearchFragment = MarketBBSideFirstFragment.Companion.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.sidesLip_frameLayout, sideETFFragment).add(R.id.sidesLip_frameLayout, sideSearchFragment).commit();
    }

    public void closeDrawer() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * 跳转到红包列表
     */
    private void toRedEnvelopeList() {
        if (FastClickUtils.isFastClick(1000)) {
            //防止快速重复点击
            return;
        }
        RedEnvelopeWebviewActivity.launch(this, UrlConstants.REDENVELOPE_URL, getResources().getString(R.string.str_red_envelope));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE && permissions != null) {
            for (int i = 0; i < permissions.length; i++) {
                Logger.getInstance().debug(TAG, "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }

    //版本升级
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void needUpdata(Event.NeedUpdate data) {
        getVersion();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshPricingMethod(BizEvent.ChangeExchangeRate event) {
        if (transactionHomeFragment != null) {//更新合约
            //将本地存的汇率列表来更新合约的汇率
            PricingMethodUtil.setHyExchangeRate((List<ExchangeRate>) SPUtils.getObject(MainActivity.this, Constant.KEY_SP_EXCHANGE_LIST,
                    new TypeToken<List<ExchangeRate>>() {
                    }.getType()));
            transactionHomeFragment.updateExchangeRate(PricingMethodUtil.getPricingUnit(), PricingMethodUtil.EXCHANGE_RATE_HY);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)//token失效弹出对话框
    public void tokenExpired(TokenExpired data) {
        Activity topActivity = UtilSystem.getRunningActivity();
        if (!TextUtils.equals(topActivity.getClass().getSimpleName(), StartUpActivity.class.getSimpleName())
                && !TextUtils.equals(topActivity.getClass().getSimpleName(), ThirdLaunchActivity.class.getSimpleName())) {
            DialogUtils.getInstance().showTokenExpiredDialog(topActivity);
        }
    }

    /**
     * 红包领取通知
     *
     * @param data
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void RedEnvelope(BizEvent.ShakeRedEnvelope data) {
        redEnvelope(data.showRedEnvelope);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void RefreshRedEnvelope(BizEvent.RefreshRedEnvelope data) {
        unregisterRedEvent();
        registerRedEvent();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void selectedExperienceGold(SelectExperienceGoldEvent data) {
        gotoSwap("");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void activationExperienceGold(JumpToContractTransfer data) {
        AccountTransferActivity.Companion.launch(this, TransferAccount.WEALTH.getValue(), TransferAccount.CONTRACT.getValue(), data.getCurrencyId(), null,
                false, data.getCurrencyName());
    }

    private void registerRedEvent() {
        SocketIOClient.subscribe(TAG, AppConstants.SOCKET.SPOT_RED_ENVELOPE, new TypeToken<RedEnvelopeEntity>() {
        }, bean -> {
            EventBus.getDefault().post(new BizEvent.ShakeRedEnvelope(bean.isHaveNew == 1));
            return null;
        });
    }

    private void unregisterRedEvent() {
        SocketIOClient.unsubscribe(TAG, AppConstants.SOCKET.SPOT_RED_ENVELOPE);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshInfo(Event.refreshInfo data) {
        if (!TextUtils.isEmpty(JPushInterface.getRegistrationID(getApplicationContext()))) {
            Logger.getInstance().debug(TAG, "RegistrationID:" + JPushInterface.getRegistrationID(getApplicationContext()));
            bindJPush(JPushInterface.getRegistrationID(getApplicationContext()));
        }
        initRedEnvelope();
        //重新刷新配置文件
        refreshConfigList();
        SocketIOClient.login();
    }

    private void bindJPush(String registrationID) {
        Map<String, Object> params = new HashMap<>();
        params.put("registerId", registrationID);
        params.put("platform", "Android");
//        params.put("appKey", "02a5dd0951198b9e6222c2d9");//JPush上注册的包名对应的appkey.//--此账号为测试账号
        params.put("appKey", "b547d910cfa28ecd1d127500");//JPush上注册的包名对应的appkey.//--此账号为生产账号（请小心使用）
        params = EncryptUtils.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        Type type = new TypeToken<SingleResult<SingleResult<String>>>() {
        }.getType();
        OKHttpHelper.getInstance().postForStringResult(UrlConstants.JPUSH_BIND, params, BindJPushCallback, type);
    }

    /**
     * 初始化
     */
    private void initRedEnvelope() {
        registerRedEvent();
    }

    //socket返回数据
    protected OnResultCallback onRecvDataCallback = new OnResultCallback<RedEnvelopeEntity>() {
        @Override
        public void onResult(RedEnvelopeEntity obj, String[] params) {
            EventBus.getDefault().post(new BizEvent.ShakeRedEnvelope(obj.isHaveNew == 1));
        }

        @Override
        public void onFail() {
            Log.i(getClass().toString(), "------------2");
            Logger.getInstance().debug(TAG, "error", new Exception());
            //TODO 处理异常情况
        }
    };

    public void setObscuration(int vis) {
        //11-12 11:31:01.620 E/AndroidRuntime( 6988): Caused by: java.lang.NullPointerException: Attempt to invoke virtual method 'void android.view.View.setVisibility(int)' on a null object reference
        //11-12 11:31:01.620 E/AndroidRuntime( 6988):     at huolongluo.byw.byw.ui.activity.main.CameraMainActivity.setObscuration(CameraMainActivity.java:766)
        //11-12 11:31:01.620 E/AndroidRuntime( 6988):     at huolongluo.byw.byw.ui.fragment.maintab01.MarketNewFragment$8.afterTextChanged(MarketNewFragment.java:752)
        if (obscuration_view != null) {
            obscuration_view.setVisibility(vis);
        }
    }

    public static boolean isOpen = false;

    public void ShowTradeExplain(int operatType) {
        Logger.getInstance().debug(TAG, "ShowTradeExplain", new Exception());
        if (operatType == 0 || operatType == 1) {
            trade_explain_view.setVisibility(View.VISIBLE);
            Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in_from_button);
            animation1.setInterpolator(new DecelerateInterpolator());
            main_view.clearAnimation();
            main_view.startAnimation(animation1);
            if (operatType == 0) {
                area_name_tv.setText(R.string.aa10);
                trade_explain_tv1.setText(R.string.aa9);
                trade_explain_tv4.setText(getResources().getString(R.string.trade_explain4));
                trade_explain_tv3.setVisibility(View.GONE);
                trade_explain_tv2.setVisibility(View.GONE);
                trade_explain_tv3_ll.setVisibility(View.GONE);
            } else {
                area_name_tv.setText(R.string.cx_area);
                trade_explain_tv1.setText(R.string.aa8);
                trade_explain_tv4.setText(getResources().getString(R.string.trade_explain3));
                trade_explain_tv3.setVisibility(View.VISIBLE);
                trade_explain_tv2.setVisibility(View.VISIBLE);
                trade_explain_tv3_ll.setVisibility(View.VISIBLE);
            }
        } else {
            Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out_from_button);
            animation1.setInterpolator(new DecelerateInterpolator());
            animation1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Logger.getInstance().debug(TAG, "ShowTradeExplain-onAnimationEnd", new Exception());
                    trade_explain_view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            main_view.clearAnimation();
            main_view.startAnimation(animation1);
        }
    }

    public void showXianshiWarn(int operatType) {
        if (operatType == 0) {
            showWarnViewTitle.setText(getResources().getString(R.string.risk));
            showWarnView.setText(getResources().getString(R.string.xianshi_wran1));
            initWarnView();
        } else if (operatType == 2) {
            showWarnViewTitle.setText(getResources().getString(R.string.risk));
            showWarnView.setText(getResources().getString(R.string.xianshi_wran11));
            initWarnView();
        } else if (operatType == 3) {
            showWarnViewTitle.setText(getResources().getString(R.string.risk));
            showWarnView.setText(getResources().getString(R.string.xianshi_wran12));
            initWarnView();
        } else if (operatType == 4) {//杠杆交易风险提示
            showWarnViewTitle.setText(getResources().getString(R.string.risk_ratio_des));
            showWarnView.setText(getResources().getString(R.string.margin_26));
            initWarnView();
        } else if (operatType == 6) {//defi专区风险提示
            showWarnViewTitle.setText(getResources().getString(R.string.risk));
            showWarnView.setText(getResources().getString(R.string.defi_tip));
            initWarnView();
        } else if (operatType == 7) {//dao专区风险提示
            showWarnViewTitle.setText(getResources().getString(R.string.risk));
            showWarnView.setText(getResources().getString(R.string.dao_tip));
            initWarnView();
        } else if (operatType == MarketNewFragment.TYPE_STORAGE) {//存储专区风险提示
            showWarnViewTitle.setText(getResources().getString(R.string.risk));
            showWarnView.setText(getResources().getString(R.string.storage_tip));
            initWarnView();
        } else if (operatType == MarketNewFragment.TYPE_NFT) {//nft专区风险提示
            showWarnViewTitle.setText(getResources().getString(R.string.risk));
            showWarnView.setText(getResources().getString(R.string.nft_tip));
            initWarnView();
        } else {//关闭
            Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out_from_button);
            animation1.setInterpolator(new DecelerateInterpolator());
            animation1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    xianzhi_explain_view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            xianshi_view.clearAnimation();
            xianshi_view.startAnimation(animation1);
        }
    }

    private void initWarnView() {
        xianzhi_explain_view.setVisibility(View.VISIBLE);
        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in_from_button);
        animation1.setInterpolator(new DecelerateInterpolator());
        xianshi_view.clearAnimation();
        xianshi_view.startAnimation(animation1);
    }


    //版本更新
    //获取版本信息/版本升级
    private void getVersion() {
        if (!AppUtils.isNetworkConnected()) {
            return;
        }
        //渠道包-不提供更新功能
        if (BuildConfig.APP_CHANNEL_VALUE == 1) {
            return;
        }
        //  HashMap<String, String> params = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        // String s=OkhttpManager.encryptGet(params);
        params = OkhttpManager.encrypt(params);
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.GetVersion, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                if (BaseApp.UPDATAING) {
                    BaseApp.UPDATAING = false;
                } else {
                    getHomePopup();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void requestSuccess(String result) {
                //  Log.i("版本升级", "url= :" + UrlConstants.GetVersion + "    result= :  " + result);
                VersionInfo versionInfo = null;
                try {
                    versionInfo = GsonUtil.json2Obj(result, VersionInfo.class);
                    //  showUpdataDialog(versionInfo);
                    // showUpdataDialog(versionInfo);
                    //   Log.i("版本升级", "current version :" + ApkUtils.getVersionCode() + " service version= :  " + versionInfo.getAndroid_version_code());
                    if (DoubleUtils.parseDouble(BuildConfig.VERSION_CODE + "") < DoubleUtils.parseDouble(versionInfo.getAndroid_version_code())) {
                        UpgradeUtils.getInstance().upgrade(MainActivity.this, versionInfo);
                    } else {
                        if (!BaseApp.UPDATAING) {
                            BaseApp.UPDATAING = false;
                            getHomePopup();
                        }
                    }
                    //  showUpdataDialog(versionInfo);
                } catch (Exception e) {
                    getHomePopup();
                    e.printStackTrace();
                }
            }
        });
    }

    // 获取首页弹窗信息 包括 1活动，2合约开通提醒，3体验金发放
    private void getHomePopup() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");

        long latestDisplayTime = SPUtils.getLong(this, Constant.KEY_ACTIVITY_AD, 0L);

        String s = OkhttpManager.encryptGet(params);
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        sb.append("i18n=" + AppUtils.getAppLanguage());
        sb.append("&");
        sb.append("channel=1");
        sb.append("&");
        sb.append("latestDisplayTime=" + latestDisplayTime);


        OkhttpManager.get(UrlConstants.DOMAIN + UrlConstants.HOME_POPUP + sb.toString(), new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {

            }

            @Override
            public void requestSuccess(String result) {
                Type type = new TypeToken<HomePopup>() {
                }.getType();

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String homePopupStr = jsonObject.get("data").toString();
                    HomePopup homePopup = JsonUtil.fromJsonToObject(homePopupStr, type);
                    showActivityAdDialog(homePopup);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private void showActivityAdDialog(HomePopup homePopup) {

        if (homePopup.getActivityAd() != null) {
            new XPopup.Builder(this)
                    .enableDrag(false)
                    .hasShadowBg(true)
                    .asCustom(new CenterPopupView(this) {

                                  boolean jumpToActivity = false;

                                  @Override
                                  protected int getImplLayoutId() {
                                      return R.layout.dialog_activity_ad;
                                  }

                                  @Override
                                  protected void onCreate() {
                                      super.onCreate();
                                      ImageView ivAd = findViewById(R.id.iv_ad);
                                      ImageView ivClose = findViewById(R.id.iv_close);
                                      Glide.with(this)
                                              .load(homePopup.getActivityAd().adUrl)
                                              .into(ivAd);

                                      ivAd.setOnClickListener(v -> {
                                          jumpToActivity = true;
                                          AppJumpHelper.getInstance().gotoTarget(MainActivity.this, homePopup.getActivityAd().redirectUrl, "", true);
                                          dismiss();
                                      });

                                      ivClose.setOnClickListener(v -> {
                                          dismiss();
                                      });
                                  }

                                  @Override
                                  protected void onDismiss() {
                                      super.onDismiss();
                                      if (!jumpToActivity) {
                                          showOtherDialog(homePopup);
                                      }
                                  }

                              }
                    ).show();

        } else {
            showOtherDialog(homePopup);
        }

    }

    private void showOtherDialog(HomePopup homePopup) {
        if (homePopup.getGoldAmount() > 0) {
            showOpenContractDialog(homePopup);
        } else if (homePopup.getGoldRecordUser() != null) {
            long experienceGoldDialogLatestDisplayTime = SPUtils.getLong(this, Constant.KEY_GET_GOLD, 0L);
            if (!TimeUtils.isToday(experienceGoldDialogLatestDisplayTime)) {
                DialogUtilKt.showUseExperienceGoldDialog(MainActivity.this, homePopup.getGoldRecordUser());
                SPUtils.saveLong(MainActivity.this, Constant.KEY_GET_GOLD, System.currentTimeMillis());
            }
        }
    }

    private void showOpenContractDialog(HomePopup homePopup) {
        long latestDisplayTime = SPUtils.getLong(this, Constant.KEY_OPEN_CONTRACT, 0L);
        if (!TimeUtils.isToday(latestDisplayTime)) {
            DialogUtilKt.showMessageDialog(this,
                    null, null,
                    getString(R.string.hy_open_contract_dialog, homePopup.getGoldAmount()),
                    true, true,
                    getString(R.string.bdb_agreement_cancel), getString(R.string.bdb_agreement_open),
                    () -> {
                        HeYueUtil.getInstance().openHY();
                        return null;
                    }
            );
            SPUtils.saveLong(MainActivity.this, Constant.KEY_OPEN_CONTRACT, System.currentTimeMillis());
        }
    }


    //监听网络变化
    private class NetWorkChangeBroast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Logger.getInstance().debug(TAG, "connected-" + intent.getAction(), new Exception());
            }
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                //  MToast.show(context, "网络已恢复", Toast.LENGTH_SHORT);
                BaseApp.isNetAvailable = true;
                Log.i("网络状态", "=  有网络");
                connected();
            } else {
                MToast.show(context, getString(R.string.aa7), Toast.LENGTH_SHORT);
                BaseApp.isNetAvailable = false;
                Log.i("网络状态", "=  无网络");
            }
        }
    }

    ;

    private void connected() {
        Logger.getInstance().debug(TAG, "connected", new Exception());
        //检查数据是否需要重新加载
        long currTimeMills = System.currentTimeMillis();
        if (currTimeMills - lastTimeMills <= 2 * 1000) {//过滤在2秒内的网络频繁切换
            return;
        }

        if (marketFragment != null) {
            EventBus.getDefault().post(new BizEvent.Trade.ReloadBB());
//            marketFragment.reload();
        }
    }

    public void reFreshSide() {
    }

    private void setDisableStopService() {
        RadioButton radioB_1 = findViewById(R.id.radioB_1);
        if (radioB_1 == null) {
            return;
        }
        Drawable drawable1 = getResources().getDrawable(R.mipmap.tabbutton_1_gray); //获取图片
        drawable1.setBounds(0, 0, DeviceUtils.dip2px(this, 22), DeviceUtils.dip2px(this, 22));  //设置图片参数
        radioB_1.setCompoundDrawables(null, drawable1, null, null);
        radioB_1.setTextColor(getResources().getColor(R.color.color_cccccc));
//        radioB_1.setEnabled(false);

        RadioButton radioB_4 = findViewById(R.id.radioB_4);
        Drawable drawable4 = getResources().getDrawable(R.mipmap.tabbutton_4_gray); //获取图片
        drawable4.setBounds(0, 0, DeviceUtils.dip2px(this, 22), DeviceUtils.dip2px(this, 22));  //设置图片参数
        radioB_4.setCompoundDrawables(null, drawable4, null, null);
        radioB_4.setTextColor(getResources().getColor(R.color.color_cccccc));
//        radioB_4.setEnabled(false);

    }

    public MarketBBSideFirstFragment sideSearchFragment;
    public MarketETFSideFirstFragment sideETFFragment;

    public void openETF() {
        if (sideSearchFragment == null || sideETFFragment == null) {
            initSide();
        }
        if (sideETFFragment != null) {
            getSupportFragmentManager().beginTransaction().hide(sideSearchFragment).show(sideETFFragment).commitAllowingStateLoss();
            sideETFFragment.open();
        }
    }

    private void openTrade() {
        if (sideSearchFragment == null || sideETFFragment == null) {
            initSide();
        }
        if (sideSearchFragment != null) {
            getSupportFragmentManager().beginTransaction().hide(sideETFFragment).show(sideSearchFragment).commitAllowingStateLoss();
            sideSearchFragment.open();
        }
    }

    private void closeETF() {
        if (sideETFFragment != null) {
            sideETFFragment.close();
            getSupportFragmentManager().beginTransaction().hide(sideETFFragment).commitAllowingStateLoss();
        }
    }

    private void closeTrade() {
        if (sideSearchFragment != null) {
            sideSearchFragment.close();
            getSupportFragmentManager().beginTransaction().hide(sideSearchFragment).commitAllowingStateLoss();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void upTitle(Event.LanguageChange languageChange) {
        MarketDataPresent.getSelf().requestTitle1();
        AppUtils.restart(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exitApp(Event.exitApp event) {
        SPUtils.saveLoginToken("");
        UserInfoManager.clearUser();
        ModularContractSDK.INSTANCE.logout();
        AgreementUtils.reset();
    }

    /**
     * 红包滚动显示隐藏
     *
     * @param showRedEnvelope
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void scrollShowEedEnvelope(BizEvent.ShowRedEnvelope showRedEnvelope) {
        RedEnvelopeAnimation.scrollShowEedEnvelope(showRedEnvelope, redEnvelope, getWindowManager().getDefaultDisplay().getWidth());
    }

    @Override
    public void onBackPressed() {
        if (!isFinishing()) {
            super.onBackPressed();
        }
    }

    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                showMessage(getString(R.string.aa6), 2);
                mExitTime = System.currentTimeMillis();
            } else {
                AppManager.get().AppExit(this);
            }
            return true;
        }
        return true;
    }

    @Override
    public void hideProgress() {
        if (this != null) {
            DialogManager.INSTANCE.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.getInstance().debug(TAG, "onDestroy");
        SocketIOClient.destroy();
        UpgradeUtils.getInstance().release();
        AppManagers.getAppManager().finishActivity(this);
        if (timerExchangeRateFirst != null) {
            timerExchangeRateFirst.cancel();
            timerExchangeRateFirst = null;
        }
        if (timerExchangeRate != null) {
            timerExchangeRate.cancel();
            timerExchangeRate = null;
        }
        if (timerCurrencyPair != null) {
            timerCurrencyPair.cancel();
            timerCurrencyPair = null;
        }
        if (timerAgreement != null) {
            timerAgreement.cancel();
            timerAgreement = null;
        }
        EventBus.getDefault().unregister(this);
        try {
            if (broadcastManager != null && mAdDownLoadReceiver != null) {
                broadcastManager.unregisterReceiver(mAdDownLoadReceiver);//注销广播
            }
//            if (networkChangeReceiver != null) {
//                unregisterReceiver(networkChangeReceiver);
//                networkChangeReceiver = null;
//            }
        } catch (Exception e) {
        }
        stopLoopTimer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void redEnvelope(boolean showRedEnvelope) {
        //AnimationDrawable animationDrawable = (AnimationDrawable) redEnvelope.getDrawable();
        if (showRedEnvelope) {
            Glide.with(this).asGif().load(R.drawable.redenvelope).into(redEnvelope);
//            animationDrawable.start();
//            Glide.with(this).load(R.drawable.redenvelope).listener(new RequestListener() {
//                @Override
//                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
//                    return false;
//                }
//
//                @Override
//                public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
//                    if (resource instanceof GifDrawable) {
//                        ((GifDrawable) resource).setLoopCount(Animation.INFINITE);
//                    }
//                    return false;
//                }
//            }).into(redEnvelope);
        } else {
//            if (animationDrawable.isRunning()) {
////                Glide.with(this).load(R.drawable.redenvelope).into(redEnvelope);
//            }
//            animationDrawable.stop();
            Glide.with(this).load(R.drawable.redenvelope).into(redEnvelope);
        }
    }

    private void setRedEnvelopeVisible(boolean visible) {
        redEnvelope.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void refreshConfigList() {
        if (Constant.STOP_SERVICE_IS_STOP_STARTUP) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.getConfig, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                Logger.getInstance().debug(TAG, "url: " + UrlConstants.DOMAIN + UrlConstants.getConfig + " errorMsg: " + errorMsg);
                Logger.getInstance().error(e);
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void requestSuccess(String result) {
                //{"RED_ENVELOPE_DISPLAY_CLOSE":{"describe":"关闭红包功能展示给用户 true 关闭","value":"true"},"C2C_CLOSE":{"describe":"关闭C2C true 关闭","value":"true"},"RED_ENVELOPE_CLOSE":{"describe":"关闭红包 true 关闭","value":"false"}}
                Logger.getInstance().debug(TAG, "url: " + UrlConstants.DOMAIN + UrlConstants.getConfig + " result: " + result);
                C2cIsShowBean c2cIsShowBean = GsonUtil.json2Obj(result, C2cIsShowBean.class);
                if (c2cIsShowBean != null) {
                    try {
                        Constant.IS_KYC_ALI_VERIFY = Boolean.valueOf(c2cIsShowBean.getOTC_KYC_ALI_YUN_FACE_ID().value);
                    } catch (Exception e) {
                        Logger.getInstance().error(TAG, "OTC_KYC_ALI_YUN_FACE_ID的value值有误 转bool失败");
                        e.printStackTrace();
                    }
                    //TODO 因合代码后发版内容变更，拆代码的风险很高，故采用加开关来处理
                    //TODO 币贷宝待修改
                    //币贷宝开关
                    if (c2cIsShowBean.getCOIN_LOAN_DISPLAY_CLOSE() != null && !TextUtils.isEmpty(c2cIsShowBean.getCOIN_LOAN_DISPLAY_CLOSE().value)) {
                        if (c2cIsShowBean.getCOIN_LOAN_DISPLAY_CLOSE().value.equals("true")) {//关闭
                            Constant.IS_BDB_CLOSE = true;
                            isBdbOpenStatusGetSuccess = true;//币贷宝关闭不需要调用开通接口，关闭timer
                        } else {
                            Constant.IS_BDB_CLOSE = false;
                        }
                    }
                    AppUtils.updateAppConfig(c2cIsShowBean);
                    if (!TextUtils.isEmpty(AppUtils.getETFCoinId())) {
                        TradeDataHelper.getInstance().refreshETF(AppUtils.getETFCoinId());
                    }
                }
                //需要判断当前是否选择的是“首页”和“我的”页面，其他业务需求要求不显示
                int resId = radioGroup.getCheckedRadioButtonId();
                if (resId == R.id.radioB_1 || resId == R.id.radioB_5) {
                    if (c2cIsShowBean != null && c2cIsShowBean.getRED_ENVELOPE_CLOSE() != null) {
                        if (Boolean.parseBoolean(c2cIsShowBean.getRED_ENVELOPE_CLOSE().getValue())) {
                            setRedEnvelopeVisible(false);
                            AppUtils.updateRedEnvelope(false);
                        } else {
                            setRedEnvelopeVisible(true);
                            AppUtils.updateRedEnvelope(true);
                        }
                    } else {
                        AppUtils.updateRedEnvelope(false);
                        setRedEnvelopeVisible(false);
                    }
                } else {
                    //隐藏红包项
                    setRedEnvelopeVisible(false);
                    AppUtils.updateRedEnvelope(false);
                }
            }
        });
    }

    //领取红包
    private void getRedEnvelope(String data) {
        Map<String, Object> params = new HashMap<>();
        String name = null;
        String num = null;
//        params.put("redEnvelopeName", "red_envelope_CNYT501595837868659");
//        params.put("itemMoney", "4");
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                if (i == 0) {
                    name = json.getString("redEnvelopeName");
                    num = json.getString("showMoney");
                } else {
                    name += "," + json.getString("redEnvelopeName");
                    num += "," + json.getString("showMoney");
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("redEnvelopeName", name);
        params.put("itemMoney", num);
        params = EncryptUtils.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        Type type = new TypeToken<SingleResult<SingleResult<List<RedEnvelope>>>>() {
        }.getType();
        OKHttpHelper.getInstance().post(UrlConstants.GET_RED_ENVELOPE, params, getRedEnvelopeCallback, type);
    }

    private void unPaidOrder() {
        //如果未登录，则不进行调用
        if (!UserInfoManager.isLogin()) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("loginToken", SPUtils.getLoginToken());
        OkhttpManager.postAsync(UrlConstants.UNPAID_ORDER, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
            }

            @Override
            public void requestSuccess(String result) {
                try {
                    PayOrderInfoBean orderTipBean = new Gson().fromJson(result, PayOrderInfoBean.class);
                    if (orderTipBean.getCode() == 0) {
                        if (orderTipBean.getData() != null && orderTipBean.getData().getOrder() != null) {
                            initUnPaidOrder(orderTipBean);
                        } else {
                            if (ms != null) {
                                ms.unsubscribe();
                            }
                            pay_orderView.setVisibility(View.GONE);
                        }
                    } else {
                        if (orderTipBean.getCode() != 401) {
                            ToastUtils.showLongToast(orderTipBean.getValue());
                        }
                    }
                } catch (Exception e) {
                    pay_orderView.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        });
    }

    void initUnPaidOrder(PayOrderInfoBean orderTipBean) {
        if (radioGroup.getCheckedRadioButtonId() == R.id.radioB_4) {
            pay_orderView.setVisibility(View.VISIBLE);
        } else {
            pay_orderView.setVisibility(View.GONE);
        }
        ImageView logo = findViewById(R.id.coin_logo);
        String coinIdLogo = orderTipBean.getData().getCoinIdLogo();
        Glide.with(this).load(coinIdLogo).into(logo);
        TextView pay_des = findViewById(R.id.pay_des);
        TextView pay_type = findViewById(R.id.pay_type);
        int type = orderTipBean.getData().getOrder().getType();
//        orderTipBean.getData().
        if (type == BUY) {
            payCount(orderTipBean, pay_des, BUY);
            pay_type.setText(getString(R.string.cx1) + orderTipBean.getData().getOrder().getCoinName());
        } else {
            payCount(orderTipBean, pay_des, SELL);
            pay_type.setText(getString(R.string.cx2) + orderTipBean.getData().getOrder().getCoinName());
        }
        pay_orderView.setOnClickListener(view1 -> {
            if (type == BUY) {
                toPay(orderTipBean);
            } else {
                if (!orderTipBean.getData().getOrder().isSellerSelectedPayType()) {//待设置
                    waitType = getString(R.string.to_set);
                    pay_des.setText(getString(R.string.to_set));
                    Intent intent = new Intent(MainActivity.this, OtcUserSellPaymentActivity.class);
                    intent.putExtra("data", orderTipBean.getData().getOrder().getId());
                    startActivity(intent);
                } else if (orderTipBean.getData().getOrder().getAdStatus() == 1) {//待收款
                    waitType = getString(R.string.to_get);
                    pay_des.setText(getString(R.string.to_get));
                    Intent intent = new Intent(MainActivity.this, OtcUserSellOtherPayedActivity.class);
                    // Intent intent = new Intent(getAContext(), OtcTradeCompleteActivity.class);
                    intent.putExtra("orderId", orderTipBean.getData().getOrder().getId());
                    startActivity(intent);
                } else {
                    pay_orderView.setVisibility(View.GONE);
                    return;
                }
//                Intent intent = new Intent(this, OtcUserSellWaitOtherPayActivity.class);
//                intent.putExtra("orderId", String.valueOf(orderTipBean.getData().getOrder().getId()));
//                intent.putExtra("type", 0);
//                startActivity(intent);
            }
        });
    }

    private void toPay(PayOrderInfoBean orderTipBean) {
        if (orderTipBean.getData().getOrder().isOneKey()) {
            Intent intent = new Intent(this, OtcBuyConfirmActivity.class);
            intent.putExtra("orderId", orderTipBean.getData().getOrder().getId());
            intent.putExtra("adRemark", orderTipBean.getData().getAdRemark() + "");
            intent.putExtra("payType", orderTipBean.getData().getOrder().getBuyerSelectedSellerPayType().getType());
            intent.putExtra("totalAmount", orderTipBean.getData().getOrder().getTotalAmount());
            intent.putExtra("payLimit", orderTipBean.getData().getPayLimit());
            intent.putExtra("transReferNum", orderTipBean.getData().getOrder().getTransReferNum());
            intent.putExtra("payBean1", orderTipBean.getData().getOrder().getBuyerSelectedSellerPayType());
            C2cStatus.payOrderInfoBean = orderTipBean;
            intent.putExtra("fastTrade", true);
            startActivity(intent);
            EventBus.getDefault().post(orderTipBean);
        } else {
            Intent intent = new Intent(this, OtcPaymentActivity.class);
            intent.putExtra("data", orderTipBean.getData().getOrder().getId());
            startActivity(intent);
        }
    }

    private Subscription ms;

    @SuppressLint("SetTextI18n")
    void payCount(PayOrderInfoBean orderTipBean, TextView pay_des, int buy) {
        if (ms != null) {
            ms.unsubscribe();
        }
        int payLimit = orderTipBean.getData().getPayLimit();
        if (buy == 2) {
            if (!orderTipBean.getData().getOrder().isSellerSelectedPayType()) {//待设置
                waitType = getString(R.string.to_set);
                pay_des.setText(getString(R.string.to_set));
            } else if (orderTipBean.getData().getOrder().getAdStatus() == 1) {//待收款
                waitType = getString(R.string.to_get);
                pay_des.setText(getString(R.string.to_get));
            } else {
                pay_orderView.setVisibility(View.GONE);
                return;
            }
        }
        if (payLimit > 2) {
            ms = Observable.interval(0, 1, TimeUnit.SECONDS).limit(payLimit + 1).map(aLong -> payLimit - aLong).observeOn(AndroidSchedulers.mainThread()).doOnNext(aLong -> {
                if (pay_des != null) {
                    pay_des.setText((buy == BUY ? getString(R.string.please_pay) : waitType) + TimeUtils.millis2String(aLong - 2 < 0 ? 0 : (aLong - 2) * 1000, "mm:ss"));
                    //  mBinding.timeLimitTv.setText(TimeUtils.millis2String(aLong * 1000, "mm分ss秒"));
                }
                if (aLong == 0 && pay_orderView != null) {
                    pay_orderView.setVisibility(View.GONE);
                }
            }).onErrorReturn(throwable -> {
                Logger.getInstance().errorLog(throwable);
                return 1L;
            }).doOnError(throwable -> {
                Logger.getInstance().error(throwable);
            }).subscribe();
        }
    }

    // 这是来自 JPush Example 的设置别名的 Activity 里的代码，更详细的示例请参考 JPush Example。一般 App 的设置的调用入口，在任何方便的地方调用都可以。
    //设置推送的别名
    private void setAlias(int sequence, String label) {
//        String registrationID = JPushInterface.getRegistrationID(this);
        Log.e("registrationID", "-----------------");
        if (UserInfoManager.isLogin()) {
            JPushInterface.setAlias(getApplicationContext(), sequence, String.valueOf(UserInfoManager.getUserInfo().getFid()));
//            JPushInterface.setAlias(getApplicationContext(), "", new TagAliasCallback() {
//                @Override
//                public void gotResult(int i, String s, Set<String> set) {
//
//                }
//            });
        }
    }


    public void resetAgreement() {
        isHyOpenStatusGetSuccess = false;
        isBdbOpenStatusGetSuccess = false;
    }

    private void getHyOpenStatus() {
        if (isHyOpenStatusGetSuccess) return;
        if (!AgreementUtils.isHyOpen()) {
            Map<String, String> params = new HashMap<>();
            params.put("loginToken", UserInfoManager.getToken());
            OkhttpManager.postAsync(UrlConstants.GET_HY_USER, params, new OkhttpManager.DataCallBack() {
                @Override
                public void requestFailure(Request request, Exception e, String errorMsg) {
                }

                @Override
                public void requestSuccess(String result) {
                    ContractUserInfoEntity contractUserInfoEntity = GsonUtil.json2Obj(result, ContractUserInfoEntity.class);
                    if (null != contractUserInfoEntity && null != contractUserInfoEntity.getData()) {
                        ContractUserInfoEntity.DataBean data = contractUserInfoEntity.getData();
                        isHyOpenStatusGetSuccess = true;
                        if (data.getStatus() == 0) {//未开通
                        } else if (data.getStatus() == 1) {//已开通
                            AgreementUtils.saveHyOpen(MainActivity.this);
                            ModularContractSDK.INSTANCE.login(UserInfoManager.getToken());
                        }
                    }
                }
            });
        }
    }

    private void getBdbOpenStatus() {
        if (isBdbOpenStatusGetSuccess) return;
        if (!AgreementUtils.isBdbOpen()) {
            Map<String, Object> params = new HashMap<>();
            params.put("type", 1);
            Type type = new TypeToken<SingleResult<String>>() {
            }.getType();
            OKHttpHelper.getInstance().get(UrlConstants.GET_BDB_AGREEMENT_STATUS + "?" + EncryptUtils.encryptStr(params) + "&loginToken=" + UserInfoManager.getToken(),
                    checkBdbAgreementCallback, type);
        }
    }

    /**
     * 获取汇率
     */
    private void getExchangeRate() {
        if (!HttpUtils.isNetworkConnected(BaseApp.getSelf())) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        Type type = new TypeToken<SingleResult<SingleResult<List<ExchangeRate>>>>() {
        }.getType();
        params.put("type", 1);
        String url = UrlConstants.GET_EXCHANGE_RATE + "?" + EncryptUtils.encryptStr(params) + "&loginToken=" + UserInfoManager.getToken();
        OKHttpHelper.getInstance().get(url, null, getExchangeRateCallback, type);
    }

    private INetCallback<SingleResult<SingleResult<List<ExchangeRate>>>> getExchangeRateCallback = new
            INetCallback<SingleResult<SingleResult<List<ExchangeRate>>>>() {
                @Override
                public void onSuccess(SingleResult<SingleResult<List<ExchangeRate>>> result) throws Throwable {
                    if (result == null) {
                        //TODO 处理异常情况
                        Logger.getInstance().debug(TAG, "result is null.");
                        return;
                    }
                    Logger.getInstance().debug(TAG, "getExchangeRateCallback result:" + result);
                    if (result.data == null || !TextUtils.equals(result.code, "200") || result.data.data == null || result.data.data.size() <= 0) {
                        Logger.getInstance().debug(TAG, "data is null.");
                        return;
                    }
                    if (result.data.code.equals("0")) {
                        PricingMethodUtil.initMap(result.data.data);
                        PricingMethodUtil.setHyExchangeRate(result.data.data);
                        SPUtils.saveObject(MainActivity.this, Constant.KEY_SP_EXCHANGE_LIST, result.data, new TypeToken<List<ExchangeRate>>() {
                        }.getType());
                        if (transactionHomeFragment != null) {
                            transactionHomeFragment.updateExchangeRate(PricingMethodUtil.getPricingUnit(), PricingMethodUtil.EXCHANGE_RATE_HY);
                        }
                    }
                }

                @Override
                public void onFailure(Exception e) throws Throwable {
                    Logger.getInstance().debug(TAG, "error", e);
                    //TODO 处理异常情况
                }
            };
    INetCallback<SingleResult<SingleResult<String>>> BindJPushCallback = new INetCallback<SingleResult<SingleResult<String>>>() {
        @Override
        public void onSuccess(SingleResult<SingleResult<String>> result) throws Throwable {
            if (result == null) {
                return;
            }
            if (result.code.equals("200") && result.data.code.equals("0")) {
                Logger.getInstance().debug(TAG, "绑定jpush接口调用成功");
            }
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
            Logger.getInstance().debug(TAG, "error", e);
        }
    };
    private INetCallback<SingleResult<SingleResult<List<RedEnvelope>>>> getRedEnvelopeCallback = new INetCallback<SingleResult<SingleResult<List<RedEnvelope>>>>() {
        @Override
        public void onSuccess(SingleResult<SingleResult<List<RedEnvelope>>> result) {
            Logger.getInstance().debug(TAG, "getRedEnvelopeCallback result:" + GsonUtil.obj2Json(result, SingleResult.class));
            if (result == null || result.data == null || result.data.data == null || result.data.data.size() == 0) {
                //TODO 处理异常情况
                Logger.getInstance().debug(TAG, "result is null.");
                return;
            }
            try {
                if (TextUtils.equals(result.code, "200") && TextUtils.equals(result.data.code, "0")) {
                    RedEnvelope redComplete = null;//记录找到的第一个已领完的失败
                    for (RedEnvelope redEnvelope : result.data.data) {
                        if (redEnvelope.getStatus() == 1) {//查询到领取成功的则直接显示
                            DialogUtils.getInstance().showRedEnvelopesSuccessDialog(MainActivity.this, redEnvelope.getItemMoney(), redEnvelope.getCurrency());
                            return;
                        }
                        if (redEnvelope.getStatus() == 2 && redComplete == null) {//取第一个找到的已领完
                            redComplete = redEnvelope;
                        }
                    }
                    if (Constant.isOfflineRedEnvelopesToRegister) {//只有离线红包跳转的注册才显示失败的弹窗
                        if (redComplete != null) {
                            DialogUtils.getInstance().showRedEnvelopesFailedDialog(MainActivity.this, redComplete.getTitle(), redComplete.getContext(), false);
                            return;
                        }
                        DialogUtils.getInstance().showRedEnvelopesFailedDialog(MainActivity.this, redComplete.getTitle(), redComplete.getContext(), true);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
            Logger.getInstance().debug(TAG, "error", e);
        }
    };
    private INetCallback<SingleResult<String>> checkBdbAgreementCallback = new INetCallback<SingleResult<String>>() {
        @Override
        public void onSuccess(SingleResult<String> response) throws Throwable {
            if (response == null || response.data == null || !response.code.equals("200")) {
                return;
            }
            Type type = new TypeToken<SingleResult<BdbFinancialAgreementStatus>>() {
            }.getType();
            SingleResult<BdbFinancialAgreementStatus> result = GsonUtil.json2Obj(response.data, type);
            if (result.code.equals("0")) {
                isBdbOpenStatusGetSuccess = true;
                if (result.data.getStatus() == 1) {//已开通
                    AgreementUtils.saveBdbOpen(MainActivity.this);
                }
            }
        }

        @Override
        public void onFailure(Exception e) throws Throwable {
            //TODO 处理异常情况
        }
    };


    @Override
    public boolean getNeedSubscribeChangeThemeEvent() {
        return true;
    }


    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        homeFragment2.applyTheme();
        marketFragment.applyTheme();
        transactionHomeFragment.applyTheme();
        mContractFragment.applyTheme();
        if (sideSearchFragment != null) {
            sideSearchFragment.applyTheme();
        }
        if (sideETFFragment != null) {
            sideETFFragment.applyTheme();
        }
    }

    @Subscribe
    public void onChangeLanguage(Event.ChangeLanguage event) {
        recreate();
    }
}

