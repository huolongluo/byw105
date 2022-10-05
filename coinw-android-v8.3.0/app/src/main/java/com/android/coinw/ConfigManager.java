package com.android.coinw;
import android.text.TextUtils;
import android.util.Base64;
import java.util.Map;

import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.LogicLanguage;
import huolongluo.byw.util.SPUtils;
public class ConfigManager {
    private static ConfigManager instance = new ConfigManager();
    private String language;

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    public void updateConfig(String data) {
        //根据Client进程发送的配置数据进行处理
        if (TextUtils.isEmpty(data)) {
            Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "update config data is null.");
            return;
        }
        try {
            //
            String json = new String(Base64.decode(data, Base64.DEFAULT));
            Map dataMap = GsonUtil.json2Obj(json, Map.class);
            exec(dataMap);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    private void exec(Map<String, Object> dataMap) {
        if (dataMap == null || dataMap.isEmpty()) {
            Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "data map is null.");
        }
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            if (entry == null) {
                continue;
            }
            String key = entry.getKey();
            Object obj = entry.getValue();
            if (TextUtils.isEmpty(key) || obj == null) {
                continue;
            }
            try {
                exec(key, obj);
            } catch (Throwable t) {
                Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, t);
            }
        }
    }

    private void exec(String key, Object value) throws Throwable {
        if (TextUtils.equals(key, ServiceConstants.Config.CFG_KEY_LANGUAGE)) {//语言包更新
            String language = String.valueOf(value);
            setLanguage(language);
            SPUtils.reload(BaseApp.getSelf());
        }
    }

    private void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        //TODO 大量测试
        if (!TextUtils.isEmpty(language)) {
            return language;
        }
        //获得语言包配置名称
        String language = "";
        try {
            language = SPUtils.getString(BaseApp.getSelf(), Constant.KEY_LANG, "");
        } catch (Exception e) {
            Logger.getInstance().error(e);
        }
        if (!TextUtils.isEmpty(language) && language.contains("ko")) {
            language = "ko_KR";
        }
        return language;
    }

    public String getAliVerifyLanguage() {
        //TODO 大量测试
        String l = "zh_CN";
        if (LogicLanguage.getLanguage(BaseApp.appContext).contains("zh")) {
            l = "zh_CN";
        } else if (LogicLanguage.getLanguage(BaseApp.appContext).contains("en")) {
            l = "en_US";
        } else if (LogicLanguage.getLanguage(BaseApp.appContext).contains("ko")) {
            l = "ko_KR";
        }
        return l;
    }
}
