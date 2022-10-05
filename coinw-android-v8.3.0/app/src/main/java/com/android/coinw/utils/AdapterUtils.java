package com.android.coinw.utils;
import android.text.TextUtils;

import com.android.coinw.model.result.Result;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import huolongluo.byw.byw.bean.HistoryListBean;
import huolongluo.byw.byw.bean.KChartBean;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.RiseFallBean;
import huolongluo.byw.model.HistoryListBeanResult;
import huolongluo.byw.model.MarketResult;
import huolongluo.byw.model.TradeOrderResult;
import huolongluo.byw.reform.trade.bean.TradeInfoBean;
import huolongluo.byw.reform.trade.bean.TradeOrderReBean;
public class AdapterUtils {
    public static Type getType(String url) {
        Type type = null;
        if (TextUtils.equals(url, UrlConstants.DOMAIN + UrlConstants.exchangeRiseFall)) {
            type = new TypeToken<Result<RiseFallBean>>() {
            }.getType();
        } else if (TextUtils.equals(url, UrlConstants.DOMAIN + UrlConstants.exchangeMarket)) {
            type = new TypeToken<Result<MarketResult.Market>>() {
            }.getType();
        } else if (TextUtils.equals(url, UrlConstants.DOMAIN + UrlConstants.exchangeETFMarket)) {
            type = new TypeToken<Result<MarketResult.Market>>() {
            }.getType();
        } else if (TextUtils.equals(url, UrlConstants.DOMAIN + UrlConstants.exchangeKline)) {
            type = new TypeToken<Result<KChartBean>>() {
            }.getType();
        } else if (TextUtils.equals(url, UrlConstants.DOMAIN + UrlConstants.exchangeDepth)) {
            type = new TypeToken<Result<TradeInfoBean>>() {
            }.getType();
        } else if (TextUtils.equals(url, UrlConstants.DOMAIN + UrlConstants.getSuccessDetails)) {
            type = new TypeToken<TradeOrderResult>() {
            }.getType();
        } else if (TextUtils.equals(url, UrlConstants.DOMAIN + UrlConstants.getEntrustInfo)) {
            type = new TypeToken<HistoryListBeanResult>() {
            }.getType();
        }
        //TODO 其他情况
        return type;
    }

    public static String getClassName(String url) {
        String className = "";
        if (TextUtils.equals(url, UrlConstants.DOMAIN + UrlConstants.exchangeRiseFall)) {
            className = RiseFallBean.class.getName();
        } else if (TextUtils.equals(url, UrlConstants.DOMAIN + UrlConstants.exchangeMarket)) {
            className = MarketResult.Market.class.getName();
        } else if (TextUtils.equals(url, UrlConstants.DOMAIN + UrlConstants.exchangeETFMarket)) {
            className = MarketResult.Market.class.getName();
        } else if (TextUtils.equals(url, UrlConstants.DOMAIN + UrlConstants.exchangeKline)) {
            className = KChartBean.class.getName();
        } else if (TextUtils.equals(url, UrlConstants.DOMAIN + UrlConstants.exchangeDepth)) {
            className = TradeInfoBean.class.getName();
        } else if (TextUtils.equals(url, UrlConstants.DOMAIN + UrlConstants.getSuccessDetails)) {
            className = TradeOrderReBean.class.getName();
        } else if (TextUtils.equals(url, UrlConstants.DOMAIN + UrlConstants.getEntrustInfo)) {
            className = HistoryListBean.class.getName();
        }
        //TODO 其他情况
        return className;
    }
}
