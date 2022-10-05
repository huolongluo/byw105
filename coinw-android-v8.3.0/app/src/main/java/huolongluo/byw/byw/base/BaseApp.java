package huolongluo.byw.byw.base;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.multidex.MultiDex;

import com.alibaba.security.biometrics.theme.ALBiometricsConfig;
import com.alibaba.security.biometrics.transition.TransitionMode;
import com.alibaba.security.cloud.CloudRealIdentityTrigger;
import com.android.coinw.ConfigManager;
import com.android.coinw.utils.Utilities;
import com.android.legend.common.ContractModuleBridge;
import com.blankj.utilcodes.utils.Utils;
import com.legend.common.component.theme.ThemeManager;
import com.legend.modular_contract_sdk.api.EventCallback;
import com.legend.modular_contract_sdk.api.ModularContractSDK;
import com.legend.modular_contract_sdk.repository.model.Product;
import com.liuzhongjun.videorecorddemo.util.CameraApp;
import com.mob.*;
import com.mob.MobSDK;
import com.mob.tools.proguard.ProtectedMemberKeeper;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.UIKitOptions;
import com.netease.nim.uikit.business.contact.core.query.PinYin;
import com.netease.nim.uikit.business.session.fragment.IMMessageEntity;
import com.netease.nim.uikit.common.CommonUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;
import com.netease.nimlib.sdk.util.NIMUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import huolongluo.byw.BuildConfig;
import huolongluo.byw.R;
import huolongluo.byw.byw.injection.component.ApplicationComponent;
import huolongluo.byw.byw.injection.component.DaggerApplicationComponent;
import huolongluo.byw.byw.injection.model.ApiModule;
import huolongluo.byw.byw.injection.model.ApplicationModule;
import huolongluo.byw.byw.net.DomainHelper;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.helper.OKHttpHelper;
import huolongluo.byw.heyue.HeYueUtil;
import huolongluo.byw.log.Logger;
import huolongluo.byw.nim.DemoPushContentProvider;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.config.ConfigurationUtils;
import huolongluo.bywx.NIMHelper;
import huolongluo.bywx.utils.AppUtils;
/**
 * Created by Administrator on 2017/8/10 0010.
 */
public class BaseApp extends MobApplication implements ViewModelStoreOwner, ProtectedMemberKeeper {
    private static final String TAG = "BaseApp";
    private ApplicationComponent mAppComponent;
    private static BaseApp instance;
    public static boolean isNetAvailable = true;
    public static boolean FIST_OPEN_C2C = false;
    public static boolean FIST_OPEN_OTC = false;
    public static boolean UPDATAING = false;
    public static volatile Context applicationContext;
    public static volatile Handler applicationHandler;
    private boolean netConnected = false;
    public static Context appContext;
    /**
     * 完成新手指引的弹窗，每次app运行的时候，只显示一次
     */
    public boolean isOpenStepCompleteDialog = false;
    /**
     * 全局的viewmodel作用域，当两个activity公用一套数据时，可以使用它来完成数据同步
     */
    private ViewModelStore appViewModelStore;

    public boolean isConnected() {
        return netConnected;
    }

    public void setNetConnected(boolean netConnected) {
        this.netConnected = netConnected;
    }

    public static BaseApp getSelf() {
        if (instance != null) {
            return instance;
        }
        return null;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            MultiDex.install(this);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            //android 9.0私有api弹框提示解决方案
            if (Build.VERSION.SDK_INT < 28) {
                return;
            }
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        if (appViewModelStore == null) {
            appViewModelStore = new ViewModelStore();
        }
        return appViewModelStore;
    }

    private static List<Activity> activityList = new ArrayList<>();

    public static void collectActivity(Activity activity) {
        activityList.add(activity);
    }

    public static void finishActivity() {
        for (Activity activity : activityList) {
            if (activity != null) {
                activity.finish();
                activity = null;
            }
        }
    }

    public static void removerActivity() {
        activityList.clear();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        ThemeManager.INSTANCE.initThemeManager(this);//umeng报错lateinit property mSp has not been initialized
        initWebView();
        initPush();
        instance = this;
        CameraApp.context = this;
        applicationContext = getApplicationContext();
        applicationHandler = new Handler(applicationContext.getMainLooper());
        //
        try {
            MobSDK.init(this);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
        /*************友盟-开始**************/
        //测试
//        UMConfigure.init(this, "5e85566b167edd8a440001b5", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
//        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_AUTO);
//        UMConfigure.setLogEnabled(true);
        /*************友盟-结束**************/
        if (BuildConfig.ENV_DEV) {//测试环境
            UMConfigure.init(this, "5fb60c56257f6b73c097a1ce", BuildConfig.UMENG_CHANNEL_NAME_VALUE, UMConfigure.DEVICE_TYPE_PHONE, null);
            UMConfigure.setLogEnabled(true);
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_AUTO);
        } else {//生产环境
            // Bugly
            //CrashReport.initCrashReport(getApplicationContext(), "d0783a2944", true);
            //  UMConfigure.init(this, "5d385057570df3ea9d000253", "main", int deviceType, String pushSecret);
            /*************友盟-开始**************/
            //TODO 测试环境
            //UMConfigure.init(this, "5e85566b167edd8a440001b5", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);

            //生产环境
            UMConfigure.init(this, "5d385057570df3ea9d000253", BuildConfig.UMENG_CHANNEL_NAME_VALUE, UMConfigure.DEVICE_TYPE_PHONE, null);
            UMConfigure.setLogEnabled(true);
//            UMCrashManager.
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_AUTO);
            /*************友盟结束**************/
        }
//        Utils.init(this);
//        closeAndroidPDialog();
//        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）
        NIMClient.init(this, loginInfo(), options());
        Utilities.stageQueue.postRunnable(() -> {
            init();
        });
        initContract();
    }

    private void init() {
        Utils.init(this);
        closeAndroidPDialog();
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）
//        NIMClient.init(this, loginInfo(), options());
//        // 以下逻辑只在主进程初始化时执行
        if (NIMUtil.isMainProcess(this)) {
            //TODO 主进程中注册网络监听
            //单独处理网络变化
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netWorkStateReceiver, filter);
            PinYin.init(this);
            PinYin.validate();
            // 初始化UIKit模块
            initUIKit();
            registerObservers(true);
            initAliFace();
        }
        initDefaultLanguage();
    }

    private void initAliFace() {
        try {
            //第二个参数是本地日志能力（若打开 会记录问题到本地，方便后期排查线上用户问题）
            CloudRealIdentityTrigger.initialize(this, true, buildALBiometricsConfig());
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    /**
     * 通过ALBiometricsConfig 自定义您的UI
     * @return
     */
    private ALBiometricsConfig buildALBiometricsConfig() {
        //TODO 详细参数参考UICustom
        ALBiometricsConfig.Builder alBiometricsConfig = new ALBiometricsConfig.Builder();
        alBiometricsConfig.setNeedSound(false);//默认是否开启声音
        alBiometricsConfig.transitionMode = TransitionMode.BOTTOM;//从下弹出
        return alBiometricsConfig.build();
    }

    private void initPush() {
        //TODO 当前版本暂时禁用推送功能
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
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

    private void initWebView() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                String processName = AppUtils.getCurrentProcessName(this);
                Log.e("getProcessName", processName);
                if (!"huolongluo.byw".equals(processName)) {//判断不等于默认进程名称
                    WebView.setDataDirectorySuffix(processName);
                }
            }
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    public void initDefaultLanguage() {
        String language = ConfigManager.getInstance().getLanguage();
        if (StringUtil.isEmpty(language)) {
            //语言切换核心代码
            ConfigurationUtils.updateActivity(this, ConfigurationUtils.getDefaultLanguage(this));
        } else {
            //语言切换核心代码
            ConfigurationUtils.updateActivity(this, language);
        }
    }

    private boolean isProduce() {
        return true;
    }

    private void initUIKit() {
        // 初始化
        NimUIKit.init(this, buildUIKitOptions());
        // 设置地理位置提供者。如果需要发送地理位置消息，该参数必须提供。如果不需要，可以忽略。
        // 添加自定义推送文案以及选项，请开发者在各端（Android、IOS、PC、Web）消息发送时保持一致，以免出现通知不一致的情况
        NimUIKit.setCustomPushContentProvider(new DemoPushContentProvider());
        IMMessageEntity.init(this);
    }

    private UIKitOptions buildUIKitOptions() {
        UIKitOptions options = new UIKitOptions();
        // 设置app图片/音频/日志等缓存目录
        options.appCacheDir = getAppCacheDir(this) + "/app";
        return options;
    }

    // 如果返回值为 null，则全部使用默认参数。
    private SDKOptions options() {
        SDKOptions options = new SDKOptions();
        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        config.notificationEntrance = MainActivity.class; // 点击通知栏跳转到该Activity
        config.notificationSmallIconId = R.drawable.coinw_logo;
        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;
        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://huolongluo.byw/raw/msg";
        config.hideContent = true;
        config.showBadge = false;
        //options.statusBarNotificationConfig = config;
        // 配置保存图片，文件，log 等数据的目录
        // 如果 options 中没有设置这个值，SDK 会使用采用默认路径作为 SDK 的数据目录。
        // 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
        String sdkPath = getAppCacheDir(this) + "/nim"; // 可以不设置，那么将采用默认路径
        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
        options.sdkStorageRootPath = sdkPath;
        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true;
        // 配置附件缩略图的尺寸大小。表示向服务器请求缩略图文件的大小
        // 该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
        // 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
        options.userInfoProvider = new UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String account) {
                return null;
            }

            @Override
            public String getDisplayNameForMessageNotifier(String account, String sessionId, SessionTypeEnum sessionType) {
                return null;
            }

            @Override
            public Bitmap getAvatarForMessageNotifier(SessionTypeEnum sessionType, String sessionId) {
                return null;
            }
        };
        /**************解决云信SDK引起CRASH问题-开始***************/
        //[KB0373] 报错：Fatal Exception: android.app.RemoteServiceException: Context.startForegroundService() did not then call Service.startForeground()
        //官方解决方法请查看（https://faq.yunxin.163.com/kb/main/#/item/KB0373）
        options.disableAwake = true;
        /**************解决云信SDK引起CRASH问题-结束***************/
        return options;
    }

    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeReceiveMessage(incomingMessageObserver, register);
        // 已读回执监听
        if (NimUIKitImpl.getOptions().shouldHandleReceipt) {
            service.observeMessageReceipt(messageReceiptObserver, register);
        }
    }

    /**
     * 消息接收观察者
     */
    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            onMessageIncoming(messages);
            IMMessageEntity.getMessages().addAll(messages);
            IMMessageEntity.saveUnReadMessageInfo();
            EventBus.getDefault().post(new Event.IMMessage(messages));
        }
    };

    private void onMessageIncoming(List<IMMessage> messages) {
        Log.e(TAG, "incomingMessageObserver");
        if (CommonUtil.isEmpty(messages)) {
            return;
        }
    }

    /**
     * 已读回执观察者
     */
    private Observer<List<MessageReceipt>> messageReceiptObserver = new Observer<List<MessageReceipt>>() {
        @Override
        public void onEvent(List<MessageReceipt> messageReceipts) {
            Log.e(TAG, "messageReceiptObserver");
        }
    };

    /**
     * 配置 APP 保存图片/语音/文件/log等数据的目录
     * 这里示例用SD卡的应用扩展存储目录
     */
    String getAppCacheDir(Context context) {
        String storageRootPath = null;
        try {
            // SD卡应用扩展存储区(APP卸载后，该目录下被清除，用户也可以在设置界面中手动清除)，请根据APP对数据缓存的重要性及生命周期来决定是否采用此缓存目录.
            // 该存储区在API 19以上不需要写权限，即可配置 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="18"/>
            if (context.getExternalCacheDir() != null) {
                storageRootPath = context.getExternalCacheDir().getCanonicalPath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(storageRootPath)) {
            // SD卡应用公共存储区(APP卸载后，该目录不会被清除，下载安装APP后，缓存数据依然可以被加载。SDK默认使用此目录)，该存储区域需要写权限!
            storageRootPath = Environment.getExternalStorageDirectory() + "/" + getPackageName();
        }
        return storageRootPath;
    }

    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    private LoginInfo loginInfo() {
        String account = SPUtils.getString(this, "im_account", "");
        String token = SPUtils.getString(this, "im_token", "");
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }

    public ApplicationComponent getAppComponent() {
        if (null == mAppComponent) {
            mAppComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).apiModule(new ApiModule(this)).build();
        }
        return mAppComponent;
    }

    public static BaseApp get(Context context) {
        return (BaseApp) context.getApplicationContext();
    }

    public void setAppComponent(ApplicationComponent mAppComponent) {
        this.mAppComponent = mAppComponent;
    }

    /**
     * 登录IM
     */
    public void loginIM() {
        NIMHelper.getToken();
    }

    private final BroadcastReceiver netWorkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.getInstance().debug(TAG, "网络状态发生变化");
            //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
            try {
                networkChange(context);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        private void networkChange(Context context) {
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                //获取WIFI连接的信息
                NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                //获取移动数据连接的信息
                NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                    Logger.getInstance().debug(TAG, "WIFI已连接,移动数据已连接");
                    setNetConnected(true);
                } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
                    Logger.getInstance().debug(TAG, "WIFI已连接,移动数据已断开");
                    setNetConnected(true);
                } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                    Logger.getInstance().debug(TAG, "WIFI已断开,移动数据已连接");
                    setNetConnected(true);
                } else {
                    setNetConnected(false);
                    //TODO 清除所有网络请求
                    OKHttpHelper.getInstance().removeAllRequest();
                    Logger.getInstance().debug(TAG, "WIFI已断开,移动数据已断开");
                }
                //API大于23时使用下面的方式进行网络监听
            } else {
                //获取所有网络连接的信息
                Network[] networks = connMgr.getAllNetworks();
                //用于存放网络连接信息
                StringBuilder sb = new StringBuilder();
                boolean connected = false;
                //通过循环将网络信息逐个取出来
                for (int i = 0; i < networks.length; i++) {
                    //获取ConnectivityManager对象对应的NetworkInfo对象
                    NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                    if (networkInfo.isConnected()) {
                        connected = true;
                    }
                    sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
                }
                if (connected) {
                    setNetConnected(true);
                    Logger.getInstance().debug(TAG, "网络已连接 " + sb.toString());
                } else {
                    setNetConnected(false);
                    //TODO 清除所有网络请求
                    OKHttpHelper.getInstance().removeAllRequest();
                    Logger.getInstance().debug(TAG, "网络已断开");
                }
            }
        }
    };
}
