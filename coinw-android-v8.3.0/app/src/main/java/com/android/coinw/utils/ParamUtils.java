package com.android.coinw.utils;

import android.text.TextUtils;

import com.android.coinw.ServiceConstants;
import com.android.coinw.model.Api;
import com.android.coinw.model.Message;
import com.android.coinw.model.Request;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import huolongluo.byw.byw.bean.KChartBean;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.RiseFallBean;
import huolongluo.byw.byw.ui.redEnvelope.RedEnvelopeEntity;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.model.MarketResult;
import huolongluo.byw.reform.trade.bean.TradeInfoBean;
import huolongluo.byw.reform.trade.bean.TradeOrder;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.pricing.PricingMethodUtil;

public class ParamUtils {
    public static Message getMessage(String api, int operate, String[] p) {
        //默认加入缓存
        return getMessage(true, api, operate, -1, p, null);
    }

    public static Message getMessage(String api, int operate, String[] p, Map<String, Object> maps) {
        //默认加入缓存
        return getMessage(true, api, operate, -1, p, maps);
    }

    public static Message getMessage(boolean cache, String api, int operate, String[] p) {
        return getMessage(cache, api, operate, -1, p, null);
    }

    public static Message getMessage(String api, int operate, long interval, String[] p, Map<String, Object> maps) {
        //默认加入缓存
        return getMessage(true, api, operate, interval, p, maps);
    }

    public static Message getChangeMessage(String api, int operate, long interval, String[] p, Map<String, Object> maps, Map<String, Object> currMaps) {
        //默认加入缓存
        return getChangeMessage(true, api, operate, interval, p, maps, currMaps);
    }

    public static Message getMessage(boolean cache, String api, int operate, String[] p, Map<String, Object> maps) {
        return getMessage(cache, api, operate, -1, p, maps);
    }

    public static Message getMessage(boolean cache, String api, int operate, long interval, String[] p, Map<String, Object> maps) {
        if (TextUtils.isEmpty(api)) {
            Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "request url is null.");
            return null;
        }
        String type = AppConstants.SOCKET.Remove;
        if (operate == AppConstants.SOCKET.OPEN) {
            type = AppConstants.SOCKET.register;
        }
        if (p == null) {
            p = new String[]{"-1"};
        }
        String msg = getCiphertext(api, type, p);
        Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "msg: " + msg);
        if (TextUtils.isEmpty(msg)) {
            Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "request parse json is null.");
            return null;
        }
        //向本地核心服务发送消息
        Message message = new Message(cache, api, type, p, maps, msg, interval, System.currentTimeMillis());
        return message;
    }

    public static Message getChangeMessage(boolean cache, String api, int operate, long interval, String[] p, Map<String, Object> maps, Map<String, Object> currMaps) {
        if (TextUtils.isEmpty(api)) {
            Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "request url is null.");
            return null;
        }
        String type = AppConstants.SOCKET.Remove;
        if (operate == AppConstants.SOCKET.OPEN) {
            type = AppConstants.SOCKET.register;
        } else if (operate == AppConstants.SOCKET.CHANGE) {
            type = AppConstants.SOCKET.changeType;
        }
        if (p == null) {
            p = new String[]{"-1"};
        }
        Map<String, String[]> changeMap = new HashMap<String, String[]>();
        changeMap.put(AppConstants.SOCKET.Remove, getParams(maps));
        changeMap.put(AppConstants.SOCKET.register, getParamsRegister(currMaps));
        String msg = getChangeCiphertext(api, type, p, changeMap);
        Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "msg: " + msg);
        Logger.getInstance().debug("ParamUtils", "getChangeMessage msg:" + msg);
        if (TextUtils.isEmpty(msg)) {
            Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "request parse json is null.");
            return null;
        }
        //向本地核心服务发送消息
        Message message = new Message(cache, api, type, p, currMaps, msg, interval, System.currentTimeMillis());
        return message;
    }

    public static Api getApi(String api) {
        Type type;
        Api refApi = null;
        if (TextUtils.equals(api, AppConstants.SOCKET.riseFallApi)) {
            type = new TypeToken<RiseFallBean>() {
            }.getType();
            refApi = new Api(UrlConstants.DOMAIN + UrlConstants.exchangeRiseFall, AppConstants.SOCKET.riseFallApi, type, RiseFallBean.class.getName());
        } else if (TextUtils.equals(api, AppConstants.SOCKET.indexApi)) {
            type = new TypeToken<MarketResult.Market>() {
            }.getType();
            refApi = new Api(UrlConstants.DOMAIN + UrlConstants.exchangeMarket, AppConstants.SOCKET.indexApi, type, MarketResult.Market.class.getName());
        } else if (TextUtils.equals(api, AppConstants.SOCKET.etfIndexApi)) {
            type = new TypeToken<MarketResult.Market>() {
            }.getType();
            refApi = new Api(UrlConstants.DOMAIN + UrlConstants.exchangeETFMarket, AppConstants.SOCKET.etfIndexApi, type, MarketResult.Market.class.getName());
        } else if (TextUtils.equals(api, AppConstants.SOCKET.klineApi)) {
            type = new TypeToken<KChartBean>() {
            }.getType();
            refApi = new Api(UrlConstants.DOMAIN + UrlConstants.exchangeKline, AppConstants.SOCKET.klineApi, type, KChartBean.class.getName());
        } else if (TextUtils.equals(api, AppConstants.SOCKET.tradeApiV3)) {
            type = new TypeToken<TradeOrder>() {
            }.getType();
            //refApi = new Api(UrlConstants.DOMAIN + UrlConstants.exchangeDepthV3, AppConstants.SOCKET.tradeApiV3, type, TradeOrder.class.getName());
        } else if (TextUtils.equals(api, AppConstants.SOCKET.redenvelope)) {
            type = new TypeToken<RedEnvelopeEntity>() {
            }.getType();
            refApi = new Api(UrlConstants.DOMAIN + "websocket/socketServer", AppConstants.SOCKET.redenvelope, type, RedEnvelopeEntity.class.getName());
        }
        return refApi;
    }

    public static String getApiForUrl(String url) {
        if (TextUtils.equals(url, UrlConstants.DOMAIN + UrlConstants.exchangeRiseFall)) {
            return AppConstants.SOCKET.riseFallApi;
        } else if (TextUtils.equals(url, UrlConstants.DOMAIN + UrlConstants.exchangeMarket)) {
            return AppConstants.SOCKET.indexApi;
        } else if (TextUtils.equals(url, UrlConstants.DOMAIN + UrlConstants.exchangeETFMarket)) {
            return AppConstants.SOCKET.etfIndexApi;
        } else if (TextUtils.equals(url, UrlConstants.DOMAIN + UrlConstants.exchangeKline)) {
            return AppConstants.SOCKET.klineApi;
        } else if (TextUtils.equals(url, UrlConstants.DOMAIN + UrlConstants.exchangeDepthV3)) {
            return AppConstants.SOCKET.tradeApiV3;
        }
        // //本地监听任务使用
        //                apiMap.put(LAST_ORDER_DETAILS, UrlConstants.DOMAIN + UrlConstants.getSuccessDetails);
        return url;
    }

    public static String getUrlForApi(String api) {
        if (TextUtils.equals(api, AppConstants.SOCKET.riseFallApi)) {
            return UrlConstants.DOMAIN + UrlConstants.exchangeRiseFall;
        } else if (TextUtils.equals(api, AppConstants.SOCKET.indexApi)) {
            return UrlConstants.DOMAIN + UrlConstants.exchangeMarket;
        } else if (TextUtils.equals(api, AppConstants.SOCKET.etfIndexApi)) {
            return UrlConstants.DOMAIN + UrlConstants.exchangeETFMarket;
        } else if (TextUtils.equals(api, AppConstants.SOCKET.klineApi)) {
            return UrlConstants.DOMAIN + UrlConstants.exchangeKline;
        } else if (TextUtils.equals(api, AppConstants.SOCKET.tradeApiV3)) {
            return UrlConstants.DOMAIN + UrlConstants.exchangeDepthV3;
        }
        return api;
    }

    public static String getCiphertext(String api, String type, String[] params) {
        //{"param":["102","0.01"],"type":"register","url":"tradeApi"}
        HashMap dataMap = new HashMap();
        dataMap.put("url", api);
        dataMap.put("type", type);
        dataMap.put("param", params);
        String msg = GsonUtil.obj2Json(dataMap, HashMap.class);
        Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "msg: " + msg);
        return msg;
    }

    public static String getChangeCiphertext(String api, String type, String[] params, Map changeMap) {
        //{"param":["102","0.01"],"type":"register","url":"tradeApi"}
        HashMap dataMap = new HashMap();
        dataMap.put("url", api);
        dataMap.put("type", type);
        dataMap.put("param", params);
        dataMap.put("changeObj", changeMap);
        String msg = GsonUtil.obj2Json(dataMap, HashMap.class);
        Logger.getInstance().debug(ServiceConstants.TAG_CLIENT, "msg: " + msg);
        return msg;
    }

    public static Request getRequest(String url, Map<String, Object> params, int method) {
        return new Request(true, true, 10000L, url, params, method);
    }

    public static Request getRequest(String url, Map<String, Object> params, int method, boolean immed) {
        return new Request(true, true, 10000L, url, params, method);
    }

    public static Request getRequest(Message message) {
        Api refApi = getApi(message.api);
        //未找到对应数据
        if (refApi == null) {
            return null;
        }
        return new Request(true, true, false, message.interval + 1500L, refApi.url, message.maps, System.currentTimeMillis(), 10 * 1000L, AppConstants.COMMON.VAL_HTTP_GET);
    }

    /**
     * 接口所需参数
     *
     * @param symbol
     * @param step
     * @return
     */
    public static HashMap<String, Object> getKLineMap(String symbol, String step) {
        HashMap<String, Object> maps = new HashMap<String, Object>();
        maps.put("symbol", symbol);
        maps.put("step", step);
        return maps;
    }

    /**
     * 接口所需参数
     *
     * @param symbol
     * @param step
     * @return
     */
    public static HashMap<String, Object> getKLineMap(int symbol, int step) {
        HashMap<String, Object> maps = new HashMap<String, Object>();
        maps.put("symbol", symbol);
        maps.put("step", step);
        return maps;
    }

    public static HashMap<String, Object> getTradeMap(int symbol, String mergerType) {
        HashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("symbol", symbol);
        maps.put("mergerType", mergerType);
        return maps;
    }

    public static HashMap<String, Object> getTradeMap(String symbol, String mergerType) {
        HashMap<String, Object> maps = new LinkedHashMap<String, Object>();
        maps.put("symbol", symbol);
        maps.put("mergerType", mergerType);
        return maps;
    }

    public static String[] getParams(Map<String, Object> dataMap) {
        List<String> paramList = new ArrayList<String>();
        if (dataMap == null) {
            return new String[]{};
        }
        Set<Map.Entry<String, Object>> set = dataMap.entrySet();
        for (Map.Entry<String, Object> entry : set) {
            paramList.add(String.valueOf(entry.getValue()));
        }
        String[] params = new String[paramList.size()];
        paramList.toArray(params);
        return params;
    }

    public static String[] getParamsRegister(Map<String, Object> dataMap) {
        List<String> paramList = new ArrayList<String>();
        if (dataMap == null) {
            return new String[]{};
        }
        Set<Map.Entry<String, Object>> set = dataMap.entrySet();
        for (Map.Entry<String, Object> entry : set) {
            paramList.add(String.valueOf(entry.getValue()));
        }
        String[] params = new String[paramList.size() + 1];
        paramList.toArray(params);
        params[params.length - 1] = PricingMethodUtil.getPricingSelectType();
        return params;
    }
}
