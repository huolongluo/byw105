package com.legend.modular_contract_sdk.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import com.legend.modular_contract_sdk.api.ModularContractSDK;
import com.orhanobut.logger.Logger;

import java.util.Locale;

/**
 * Created by hy on 2018/10/25 0025.
 */
public class McConfigurationUtils {
    private static final String TAG = "ConfigurationUtils";
    //在某些异常情况语言被还原为系统语言则重新设置为当前语言
    public static void resetLanguage(Context context){
        String language= ModularContractSDK.INSTANCE.getLanguage();
        Logger.t(TAG).d("resetLanguage language:"+language+"  getLocale(context).toString():"+getLocale(context).toString());
        if(!TextUtils.isEmpty(language)&&language.length()>=2){//当app当前语言和系统语言不同，需要重设为app的语言
            if (!getLocale(context).toString().contains(language.substring(0,2))) {
                Logger.t(TAG).d("resetLanguage 重设语言");
               updateActivity(context, language);
            }
        }

    }
    //获取系统语言
    public static Locale getLocale(Context context) {
        Locale locale;
        //7.0以下直接获取系统默认语言
        if (Build.VERSION.SDK_INT < 24) {
            // 等同于context.getResources().getConfiguration().locale;
            locale = Locale.getDefault();
            // 7.0以上获取系统首选语言
        } else {
            LocaleList defaultLocaleList = LocaleList.getDefault();
            StringBuffer sb0 = new StringBuffer();
            for (int i = 0; i < defaultLocaleList.size(); i++) {
                sb0.append(defaultLocaleList.get(i));
                sb0.append(",");
            }

            LocaleList list = context.getResources().getConfiguration().getLocales();
            StringBuffer sb1 = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                sb1.append(list.get(i));
                sb1.append(",");
            }

            LocaleList adjustedDefaultLocaleList = LocaleList.getAdjustedDefault();
            StringBuffer sb2 = new StringBuffer();
            for (int i = 0; i < adjustedDefaultLocaleList.size(); i++) {
                sb2.append(adjustedDefaultLocaleList.get(i));
                sb2.append(",");
            }

            locale = defaultLocaleList.get(0);
        }
        return locale;
    }

    /**
     * 多语言切换，重启回到首页
     *
     * @param context
     * @param lang
     */
    public static void updateActivity(Context context, String lang) {
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
        res.updateConfiguration(conf, dm);
    }

}
