package huolongluo.byw.util;

import android.content.Context;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by zhoujing on 2017/10/19.
 */

public class LogicLanguage {

    public interface IOrderListener {
        void onLanguageChange(boolean kill_process);
    }

    private static LogicLanguage instance = null;

    public static LogicLanguage getInstance() {
        if (null == instance)
            instance = new LogicLanguage();
        return instance;
    }

    private List<IOrderListener> mListeners = new ArrayList<>();

    private LogicLanguage() {

    }

    public void registListener(IOrderListener listener) {

        if (listener == null) return;

        int iCount;
        for (iCount = 0; iCount < mListeners.size(); iCount++) {
            if (listener.equals(mListeners.get(iCount)))
                break;
        }

        if (iCount >= mListeners.size())
            mListeners.add(listener);
    }


    public void unregistListener(IOrderListener listener) {

        if (listener == null) return;

        int iCount;
        for (iCount = 0; iCount < mListeners.size(); iCount++) {
            if (listener.equals(mListeners.get(iCount))) {
                mListeners.remove(mListeners.get(iCount));
                return;
            }
        }
    }

    public void refresh(boolean kill_process) {
        for (int i = 0; i < mListeners.size(); i++) {
            if (mListeners.get(i) != null) {
                mListeners.get(i).onLanguageChange(kill_process);
            }
        }
    }

    private static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language.endsWith("zh");
    }

    public static boolean isZhEnv(Context context) {
        String language = PreferenceManager.getInstance(context).getSharedString(PreferenceManager.PREF_LANGUAGE, "auto");
        if (TextUtils.equals(language, "auto")) {
            return isZh(context);
        } else {
            return TextUtils.equals(language, "zh");
        }
    }
    public static boolean isKorean(Context context){
         if(getLanguage(context).contains("ko")){
            return true;
        }
         return false;
    }
    public static boolean isEnglish(Context context){
        if(getLanguage(context).contains("en")){
            return true;
        }
        return false;
    }

    public static String getLanguage(Context context){
        return PreferenceManager.getInstance(context).getSharedString(PreferenceManager.PREF_LANGUAGE, "auto");
    }

    public static String getPhonePreByLanguage(Context context){
        String pre="+86";
        if(getLanguage(context).contains("zh")){
            pre="+86";
        }
        else if(getLanguage(context).contains("en")){
            pre="+1";
        }
        else if(getLanguage(context).contains("ko")){
            pre="+82";
        }
        return pre;
    }
    /**
     * 中英文语言切换
     * 如果是中文 isZh=true,英文 isZh=false
     *
     * @param context
     * @param language
     */
    public static void changeLanguage(Context context, String language) {
        if (language.contains("zh")) {
            changeAppLanguage(context, Locale.SIMPLIFIED_CHINESE, true);
        } else if(language.contains("en")){
            changeAppLanguage(context, Locale.ENGLISH, true);
        }
        else if(language.contains("ko")){
            changeAppLanguage(context, Locale.KOREAN, true);
        }
    }

    /**
     * 多语言切换
     *
     * @param context
     * @param locale
     * @param save
     */
    public static void changeAppLanguage(Context context, Locale locale, boolean save) {
//        Resources resources = context.getResources();
//        DisplayMetrics metrics = resources.getDisplayMetrics();
//        Configuration configuration = resources.getConfiguration();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            configuration.setLocale(locale);
//        } else {
//            configuration.locale = locale;
//        }
//        resources.updateConfiguration(configuration, metrics);
        if (save) {
            if (locale.getLanguage().endsWith("zh")) {
                PreferenceManager.getInstance(context).putSharedString(PreferenceManager.PREF_LANGUAGE, "zh");
            } else if (locale.getLanguage().endsWith("en")) {
                PreferenceManager.getInstance(context).putSharedString(PreferenceManager.PREF_LANGUAGE, "en");
            }
            else if (locale.getLanguage().endsWith("ko")) {
                PreferenceManager.getInstance(context).putSharedString(PreferenceManager.PREF_LANGUAGE, "ko");
            }
        }
        LogicLanguage.getInstance().refresh(false);
    }
}
