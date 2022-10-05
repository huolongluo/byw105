package huolongluo.byw.util.config;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.webkit.WebView;

import com.android.coinw.ConfigManager;
import java.util.Locale;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.LogicLanguage;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.bywx.helper.AppHelper;

/**
 * Created by hy on 2018/10/25 0025.
 */
public class ConfigurationUtils {
    private static final String TAG = "ConfigurationUtils";
    //在某些异常情况语言被还原为系统语言则重新设置为当前语言
    public static void resetLanguage(Context context){
        String language=ConfigManager.getInstance().getLanguage();
        if(!TextUtils.isEmpty(language)&&language.length()>=2){//当app当前语言和系统语言不同，需要重设为app的语言
            if (!getLocale(context).toString().contains(language.substring(0,2))) {
                Logger.getInstance().debug(TAG, "重设语言");
                ConfigurationUtils.updateActivity(context, language);
            }
        }

    }
    //获取系统语言
    private static Locale getLocale(Context context) {
        Resources res = context.getResources();// 获得res资源对象
        Configuration conf = res.getConfiguration();// 获得设置对象
        return conf.locale;
    }

    public static void updateWebViewSettings(Activity atv) {
        try {
            // 解决webview所在的activity语言没有切换问题
            WebView webView = new WebView(atv);
            AppHelper.distoryWebView(webView);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    /**
     * 多语言切换，重启回到首页
     *
     * @param context
     * @param lang
     */
    public static void updateActivity(Context context, String lang) {
        Logger.getInstance().debug("ConfigurationUtils", "updateActivity: " + lang, new Exception());
        // 本地语言设置
        //   Locale myLocale = new Locale(lang);
        Resources res = context.getResources();// 获得res资源对象
        DisplayMetrics dm = res.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
        Configuration conf = res.getConfiguration();// 获得设置对象
        if (lang.contains("zh")) {
            conf.locale = Locale.SIMPLIFIED_CHINESE; // 简体中文
            //切换合约的语言为英文
        } else if(lang.contains("en")){
            conf.locale = Locale.ENGLISH; // 英文
        }
        else if(lang.contains("ko")){
            conf.locale = Locale.KOREAN;
        }
        Logger.getInstance().debug("ConfigurationUtils", "updateActivity: lang" + lang+" conf.locale:"+conf.locale);
        LogicLanguage.changeLanguage(context, lang);
        //  conf.locale = Locale.getDefault();// 跟随系统
        res.updateConfiguration(conf, dm);
        //重新getstring
        context.getResources().getString(R.string.permissions_help_text);
    }

    public static String getCurrentLang(Context context) {
        String sta = context.getResources().getConfiguration().locale.getLanguage() + "_" + context.getResources().getConfiguration().locale.getCountry();
//        Logger.getInstance().debug("ConfigurationUtils", "language: " + sta, new Exception());
        return sta;
    }

    //获取默认语言，该出只在第一次app进入时执行，内部同时执行了计价方式默认跟随语言，规则：系统语言为中文英文韩文，则使用系统语言，否则使用英文
    public static String getDefaultLanguage(Context context){
        String lang="en";
        PricingMethodUtil.setPricingMethod(PricingMethodUtil.PRICING_DOLLAR);
        if(getCurrentLang(context).contains("zh")){
            lang="zh";
            PricingMethodUtil.setPricingMethod(PricingMethodUtil.PRICING_RMB);
        }else if(getCurrentLang(context).contains("en")){
            lang="en";
            PricingMethodUtil.setPricingMethod(PricingMethodUtil.PRICING_DOLLAR);
        }
        else if(getCurrentLang(context).contains("ko")){
            lang="ko";
            PricingMethodUtil.setPricingMethod(PricingMethodUtil.PRICING_WON);
        }
        return lang;
    }
}
