package huolongluo.bywx.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.netease.nimlib.sdk.msg.model.IMMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import huolongluo.byw.BuildConfig;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.net.okhttp.HttpUtils;
import huolongluo.byw.helper.INetCallback;
import huolongluo.byw.helper.SSLHelper;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.AliManMachineEntity;
import huolongluo.byw.reform.c2c.oct.bean.C2cIsShowBean;
import huolongluo.byw.reform.c2c.oct.bean.OtcCoinBean;
import huolongluo.byw.reform.home.activity.kline2.common.Kline2Constants;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.FingerprintUtil;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.MathHelper;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.byw.util.tip.DialogUtils;
import okhttp3.HttpUrl;
import okhttp3.Request;

public class AppUtils {
    private static final String TAG = "AppUtils";
    private static int selectTime = 4 * 60 * 60;
    private static boolean redEnvelope = false;//控制本地选择不同TAB是否需要显示
    private static C2cIsShowBean config;
    private static OtcCoinBean otcCoinBean;

    public static boolean isScreenOff(Context context) {
        KeyguardManager manager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return manager.inKeyguardRestrictedInputMode();
    }

    public static boolean isNetworkConnected() {
        if (BaseApp.getSelf() != null && BaseApp.getSelf().isConnected()) {
            return true;
        }
        return false;
    }

    public static String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        String processName = "";
        try {
            ActivityManager manager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
                if (process.pid == pid) {
                    processName = process.processName;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return processName;
    }

    @Deprecated
    public static void setSelectTime(int selectTime) {
        SPUtils.saveInt(BaseApp.getSelf(), "selectTime", selectTime);
    }

    @Deprecated
    public static int getSelectTime() {
        return SPUtils.getInt(BaseApp.getSelf(), "selectTime", 4 * 60 * 60);
    }

    public static void setSelectTime(int selectTime, @Kline2Constants.TradeType int tradeType) {
        SPUtils.saveInt(BaseApp.getSelf(), "selectTime" + tradeType, selectTime);
    }

    public static int getSelectTime(@Kline2Constants.TradeType int tradeType) {
        // 进入默认显示15min线
        return SPUtils.getInt(BaseApp.getSelf(), "selectTime" + tradeType, 15 * 60);
    }
    /**
     * 隐藏软键盘(只适用于Activity，不适用于Fragment)
     */
    public static void hideSoftKeyboard(Activity activity) {
        if (activity == null) {
            return;
        }
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    /**
     * @param callback
     * @return
     */
    public static boolean checkNetworkConnected(INetCallback callback) {//TODO 暂时处理方法，后续再优化
        if (HttpUtils.isNetworkConnected(BaseApp.getSelf())) {
            return true;
        }
        if (callback != null) {
            try {
                String msg = getString(R.string.net_exp);
                //"网络连接失败，请检查!"
                Exception e = new Exception(msg);
                callback.onFailure(e);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return false;
    }

    public static boolean checkNetworkConnected(Request request, OkhttpManager.DataCallBack callback) {
        if (HttpUtils.isNetworkConnected(BaseApp.getSelf())) {
            return true;
        }
        if (callback != null) {
            try {
                String msg = getString(R.string.net_exp);
                //"网络连接失败，请检查!"
                Exception e = new Exception(msg);
                callback.requestFailure(request, e, e.getMessage());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return false;
    }

    public static int getProgress(double amount, double totalAmount) {
        if (totalAmount == 0.0) {
            return 1;
        }
        double value = ((amount * 100) / totalAmount) * 3;
        return value > 0.0d ? (int) (Math.ceil(value)) : 1;
    }

    /**
     * 判断是否为当前选择的k线数据(1分钟，5分钟，30分钟，1小时等)
     *
     * @param time
     * @param params
     * @return
     */
    public static boolean isCurrKline(int time, String[] params) {
        if (params == null || params.length == 0) {
            return false;
        }
        if (params.length < 2) {
            return false;
        }
        if (TextUtils.equals(params[1], time + "")) {
            return true;
        }
        return false;
    }

    public static String getString(int resId) {
        if (resId < 0) {
            return "";
        }
        try {
            return BaseApp.getSelf().getString(resId);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
        return "";
    }

    public static String getString(Reader reader) {
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader bread = new BufferedReader(reader);
            char[] buf = new char[1024];
            int count = 0;
            while ((count = bread.read(buf)) != -1) {
                buffer.append(buf, 0, count);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return new String(buffer);
    }

    public static void restart(Activity atv) {
        try {
            Logger.getInstance().debug("AppUtils", "restart");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent LaunchIntent = atv.getPackageManager().getLaunchIntentForPackage(atv.getApplication().getPackageName());
                    LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    atv.startActivity(LaunchIntent);
                    atv.finish();
                    Logger.getInstance().debug("AppUtils", "restart running ...");
                }
            }, 100);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    public static Request.Builder getRequestBuilder(Request.Builder builder, String url) {
        //获得语言包配置名称
        String language = "";
        try {
            language = SPUtils.getString(BaseApp.getSelf(), Constant.KEY_LANG, "");
        } catch (Exception e) {
            Logger.getInstance().error(e);
        }
        return getRequestBuilder(builder, url, language);
    }

    public static Request.Builder getRequestBuilder(String url) {
        //获得语言包配置名称
        String language = "";
        try {
            language = SPUtils.getString(BaseApp.getSelf(), Constant.KEY_LANG, "");
        } catch (Exception e) {
            Logger.getInstance().error(e);
        }
        return getRequestBuilder(url, language);
    }

    public static Request.Builder getRequestBuilder(Request.Builder builder, String url, String language) {
        //获得语言包配置名称
        String deviceId = "";
        try {
            deviceId = FingerprintUtil.getFingerprint(BaseApp.getSelf());
            if (TextUtils.isEmpty(language)) {
                language = SPUtils.getString(BaseApp.getSelf(), Constant.KEY_LANG, "");
            }
        } catch (Exception e) {
            Logger.getInstance().error(e);
        }
        if (!TextUtils.isEmpty(language)) {
            if (language.contains("ko")) {
                language = "ko_KR";
            } else if (language.contains("en")) {
                language = "en_US";
            } else if (language.contains("zh")) {
                language = "zh_CN";
            }
        }
        builder.header("language", language)//语言
                //Accept-Language是理财接口多语言单独需要的一个header，后端不是同一个项目所以需要单独处理一下，把下划线"_" 替换为中划线"-"
                .header("Accept-Language", language.replaceAll("_","-"))
                .header("selectType", PricingMethodUtil.getPricingSelectType())//计价方式
                .header("clientTag", "android")//终端
                .header("appVersion", BuildConfig.VERSION_CODE + "")//当前版本号
                .header("deviceId", deviceId)//设备指纹唯一标识符
                .header("loginToken", TextUtils.isEmpty(UserInfoManager.getToken()) ? "" : UserInfoManager.getToken()) //
                .header(SSLHelper.getHeadKey(), SSLHelper.getHeadValue(11, url));//
        if (BuildConfig.ENV_DEV) {
            String msg = String.format("language:%s,clientTag:android,appVersion:%s,deviceId:%s", language, BuildConfig.VERSION_CODE + "", deviceId);
            Logger.getInstance().debug("AppUtils", msg);
        }
        return builder;
    }

    public static Request.Builder getRequestBuilder(String url, String language) {
        //获得语言包配置名称
        String deviceId = "";
        try {
            deviceId = FingerprintUtil.getFingerprint(BaseApp.getSelf());
            if (TextUtils.isEmpty(language)) {
                language = SPUtils.getString(BaseApp.getSelf(), Constant.KEY_LANG, "");
            }
        } catch (Exception e) {
            Logger.getInstance().error(e);
        }
        if (!TextUtils.isEmpty(language)) {
            if (language.contains("ko")) {
                language = "ko_KR";
            } else if (language.contains("en")) {
                language = "en_US";
            } else if (language.contains("zh")) {
                language = "zh_CN";
            }
        }
        Request.Builder builder = new Request.Builder().url(HttpUrl.parse(url));//
//                .header("language", language)//语言
//                .header("selectType", PricingMethodUtil.getPricingSelectType())//计价方式
//                .header("clientTag", "android")//终端
//                .header("appVersion", BuildConfig.VERSION_CODE + "")//当前版本号
//                .header("deviceId", deviceId)//设备指纹唯一标识符
//                .header("loginToken", UserInfoManager.getToken())
////                ;
//                .header(SSLHelper.getHeadKey(), SSLHelper.getHeadValue(11, url));//
        if (BuildConfig.ENV_DEV) {
            String msg = String.format("language:%s,clientTag:android,appVersion:%s,deviceId:%s", language, BuildConfig.VERSION_CODE + "", deviceId);
            Logger.getInstance().debug("AppUtils", msg);
        }
        return builder;
    }

    /**
     * 为兼容HyperPay多语言接口
     *
     * @return
     */
    public static String getLanguageTag() {
        String language = "";
        try {
            language = SPUtils.getString(BaseApp.getSelf(), Constant.KEY_LANG, "");
        } catch (Exception e) {
            Logger.getInstance().error(e);
        }
        if (TextUtils.isEmpty(language)) {
            //TODO 处理为空情况
        } else if (language.startsWith("en") || language.startsWith("EN")) {
            return "en";
        } else if (language.contains("ko")) {
            return "korea_real";
        } else if (language.startsWith("zh") || language.startsWith("ZH")) {
            return "";
        }
        return language;
    }

    public static boolean isZHEnv() {
        String language = "";
        try {
            language = SPUtils.getString(BaseApp.getSelf(), Constant.KEY_LANG, "");
        } catch (Exception e) {
            Logger.getInstance().error(e);
        }
        if (TextUtils.isEmpty(language)) {
            return isTagEnv(BaseApp.getSelf(), "zh");
        } else if (language.startsWith("en") || language.startsWith("EN")) {
            return false;
        } else if (language.startsWith("zh") || language.startsWith("ZH")) {
            return true;
        }
        return false;
    }

    public static boolean isENEnv() {
        String language = "";
        try {
            language = SPUtils.getString(BaseApp.getSelf(), Constant.KEY_LANG, "");
        } catch (Exception e) {
            Logger.getInstance().error(e);
        }
        if (TextUtils.isEmpty(language)) {
            return isTagEnv(BaseApp.getSelf(), "en");
        } else if (language.startsWith("en") || language.startsWith("EN")) {
            return true;
        } else if (language.startsWith("zh") || language.startsWith("ZH")) {
            return false;
        }
        return false;
    }

    private static boolean isTagEnv(Context context, String tag) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language.endsWith(tag);
    }

    /**
     * 是否刷新订单
     *
     * @param messages
     * @param status
     * @return
     */
    public static boolean isRefreshOrder(List<IMMessage> messages, int status) {
        Logger.getInstance().debug("AppUtils", "is refresh order status: " + status, new Exception());
        if (messages == null || messages.isEmpty()) {
            return false;
        }
        //TODO 由于历史原因，此处订单全部刷新
        for (IMMessage message : messages) {
            if (message == null) {
                continue;
            }
            Map<String, Object> dataMap = message.getRemoteExtension();
            try {
                Object obj = dataMap.get("orderStatus");
                if (obj != null) {
                    String orderStatus = String.valueOf(obj);
                    int os = Integer.parseInt(orderStatus);
                    //弱判断全刷新（收到消息，所有订单列表数据刷新）
                    if (os >= 0) {
                        return true;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return false;
    }

    public static int getDefaultCoinId() {
        return 421;
    }

    public static String getDefaultCoinName() {
        return "CWT";
    }

    public static String getDefaultCnyName() {
        return "USDT";
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        //获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {   //listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);  //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight();  //统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //listView.getDividerHeight()获取子项间分隔符占用的高度
        //params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    /**
     * 小开关（控制悬浮窗）
     * 需要控制本地选择TAB，是否需要显示悬浮窗
     *
     * @return
     */
    public static boolean isRedEnvelope() {
        if (AppUtils.config == null || AppUtils.config.getRED_ENVELOPE_CLOSE() == null) {
            return false;
        }
        if (redEnvelope) {//判断本地选择的TAB，是否需要显示悬浮窗
            //优先判断大开关
            //大开关开启的情况下，再判断小开关是否开启
            if (AppUtils.isRedEnvelopeClose()) {
                return false;
            } else {
                return !Boolean.parseBoolean(AppUtils.config.getRED_ENVELOPE_CLOSE().getValue());//开关关闭，需要显示；反则：开关打开，需要隐藏
            }
        } else {
            return false;
        }
    }

    public static boolean isOnfidoOpen() {
        if (AppUtils.config == null || AppUtils.config.getOnFidoSwitch() == null) {
            return false;
        }
        return MathHelper.stringToBoolean(AppUtils.config.getOnFidoSwitch().value);
    }

    public static String getOnfidoMaxTimePerDay() {
        if (AppUtils.config == null || AppUtils.config.getLimitCertNumWithDay() == null) {
            return "3";
        }
        return AppUtils.config.getLimitCertNumWithDay().value;
    }

    public static void updateRedEnvelope(boolean redEnvelope) {//小开关（控制悬浮窗）
        AppUtils.redEnvelope = redEnvelope;
    }

    public static void updateAppConfig(C2cIsShowBean config) {//大开关（控制整个app端红包业务）
        AppUtils.config = config;
    }

    public static String getETFCoinId() {
        if (AppUtils.config == null || AppUtils.config.getDEFAULT_ETF_TRADE_ID() == null) {
            return "";
        }
        return AppUtils.config.getDEFAULT_ETF_TRADE_ID().getValue();
    }

    public static String getOtcAdDns() {
        if (AppUtils.config == null || AppUtils.config.getOTC_ADVERTISE_VIEW_HOST() == null) {
            return UrlConstants.DOMAIN;
        }
        return AppUtils.config.getOTC_ADVERTISE_VIEW_HOST();
    }

    /**
     * 大开关（控制整个app端红包业务）
     * //开关关闭，需要显示；反则：开关打开，需要隐藏
     *
     * @return
     */
    public static boolean isRedEnvelopeClose() {
        if (AppUtils.config == null || AppUtils.config.getRED_ENVELOPE_DISPLAY_CLOSE() == null) {
            return false;
        }
        return Boolean.parseBoolean(AppUtils.config.getRED_ENVELOPE_DISPLAY_CLOSE().getValue());
    }

    /**
     * 是否存在su命令，并且有执行权限
     *
     * @return 存在su命令，并且有执行权限返回true
     */
    public static boolean isSuEnable() {
        File file = null;
        String[] paths = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/", "/su/bin/"};
        try {
            for (String path : paths) {
                file = new File(path + "su");
                if (file.exists() && file.canExecute()) {
                    Logger.getInstance().debug("AppUtils", "find su in : " + path);
                    return true;
                }
            }
        } catch (Exception e) {
            Logger.getInstance().error(e);
        }
        return false;
    }

    public static String getUnitFlag(String unit) {
        if (TextUtils.isEmpty(unit)) {
            return "";
        }
        if (unit.equalsIgnoreCase("USDT")) {
            return "$";
        } else {
            return "¥";
        }
    }

    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    public static DisplayMetrics getDisplayMetrics(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /**
     * 判断service是否已经运行
     * 必须判断uid,因为可能有重名的Service,所以要找自己程序的Service,不同进程只要是同一个程序就是同一个uid,个人理解android系统中一个程序就是一个用户
     * 用pid替换uid进行判断强烈不建议,因为如果是远程Service的话,主进程的pid和远程Service的pid不是一个值,在主进程调用该方法会导致Service即使已经运行也会认为没有运行
     * 如果Service和主进程是一个进程的话,用pid不会出错,但是这种方法强烈不建议,如果你后来把Service改成了远程Service,这时候判断就出错了
     *
     * @param className Service的全名,例如PushService.class.getName()
     * @return true:Service已运行 false:Service未运行
     */
    public static boolean isServiceExisted(Context context, String className) {
        if (context == null) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = am.getRunningServices(Integer.MAX_VALUE);
        int myUid = android.os.Process.myUid();
        for (ActivityManager.RunningServiceInfo rsi : serviceList) {
            String name = rsi.service.getClassName();
            String msg = String.format("className: " + name);
            Logger.getInstance().debug("AppUtils", msg);
            if (rsi.uid == myUid && TextUtils.equals(name, className)) {
                String logMsg = String.format("pid:%s,uid:%s,crashCount:%s,started:%s", rsi.pid, rsi.uid, rsi.crashCount, rsi.started);
                Logger.getInstance().debug("AppUtils", logMsg);
                if (rsi.pid > 0) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static String getLanguage() {
        //获得语言包配置名称
        String language = "";
        try {
            if (TextUtils.isEmpty(language)) {
                language = SPUtils.getString(BaseApp.getSelf(), Constant.KEY_LANG, "");
            }
        } catch (Exception e) {
            Logger.getInstance().error(e);
        }
        if (!TextUtils.isEmpty(language)) {
            if (language.contains("zh")) {
                language = Locale.SIMPLIFIED_CHINESE.toString();
            } else if (language.contains("en")) {
                language = Locale.US.toString();
            } else if (language.contains("ko")) {
                language = Locale.KOREAN.toString();
            }
        } else {
            language = Locale.US.toString();
        }
        return language;
    }

    public static String getAppLanguage() {
        String language = SPUtils.getString(BaseApp.getSelf(), Constant.KEY_LANG, "");
        if (!TextUtils.isEmpty(language)) {
            if (language.contains("ko")) {
                language = "ko_KR";
            } else if (language.contains("en")) {
                language = "en_US";
            } else if (language.contains("zh")) {
                language = "zh_CN";
            }
        } else {
            language = "en_US";
        }
        return language;
    }

    public static void updateOTCCoinBean(OtcCoinBean otcCoinBean) {
        Logger.getInstance().debug(TAG, "updateOTCCoinBean", new Exception());
        AppUtils.otcCoinBean = otcCoinBean;
    }

    public static OtcCoinBean getOTCCoinBean() {
        return AppUtils.otcCoinBean;
    }

    public static OtcCoinBean.DataBean getOTCCoin(int id) {
        if (AppUtils.otcCoinBean == null || AppUtils.otcCoinBean.getData() == null) {
            return null;
        }
        for (OtcCoinBean.DataBean dataBean : AppUtils.otcCoinBean.getData()) {
            if (id == dataBean.getCoinId()) {
                return dataBean;
            }
        }
        return null;
    }

    public static OtcCoinBean.DataBean getOTCCoin(String coinName) {
        if (AppUtils.otcCoinBean == null || AppUtils.otcCoinBean.getData() == null) {
            return null;
        }
        for (OtcCoinBean.DataBean dataBean : AppUtils.otcCoinBean.getData()) {
            if (TextUtils.equals(coinName, dataBean.getCoinName())) {
                return dataBean;
            }
        }
        return null;
    }

    public static void showDialogForGRINBiz(Activity activity, String coinName, String message) {
        if (TextUtils.isEmpty(coinName)) {
            return;
        } else if (TextUtils.equals(AppConstants.BIZ.KEY_BIZ_COIN_GRIN, coinName.toUpperCase(Locale.ENGLISH))) {
            DialogUtils.getInstance().showCommonInfoDialog(activity, getString(R.string.iknow1), message, false);
        }
    }

    public static String getNVC(AliManMachineEntity aliEntity) {
        if (aliEntity == null) {
            return "";
        } else {
            return String.format("&nvcSessionId=%s&nvcSig=%s&nvcToken=%s&nvcScene=%s", aliEntity.getNvcSessionId(), aliEntity.getNvcSig(), aliEntity.getNvcToken(), aliEntity.getNvcScene());
        }
    }

    public static String getExceptionAllInformation(Exception e) {
        if (e == null) {
            return "";
        }
        String sOut = "";
        sOut += e.getMessage() + "\r\n";
        StackTraceElement[] trace = e.getStackTrace();
        for (StackTraceElement s : trace) {
            sOut += "\tat " + s + "\r\n";
        }
        return sOut;
    }
}
