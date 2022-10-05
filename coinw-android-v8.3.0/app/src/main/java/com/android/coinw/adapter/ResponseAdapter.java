package com.android.coinw.adapter;
import com.android.coinw.DataManager;
import com.android.coinw.ServiceConstants;
import com.android.coinw.model.Request;
import com.android.coinw.model.Response;
import com.android.coinw.model.result.Result;
import com.android.coinw.service.CallbackManager;
import com.android.coinw.utils.AdapterUtils;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

import huolongluo.byw.log.Logger;
import huolongluo.byw.model.HistoryListBeanResult;
import huolongluo.byw.model.TradeOrderResult;
import huolongluo.bywx.handler.DataAdapterHandler;
public class ResponseAdapter implements IResponseAdapter {
    @Override
    public void dispatch(String url, IOException e) throws Throwable {
        Logger.getInstance().debugThreadLog(ServiceConstants.TAG_SERVICE, "exception,url is: " + url);
        Type type = AdapterUtils.getType(url);
        if (type == null) {
            Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "exception,dispatch type is null!");
            return;
        }
        Request request = DataManager.getInstance().getRequest(url);
        if (request == null) {
            Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "exception,dispatch obj, get request is null!");
        }
        Response response = new Response(request, ServiceConstants.TYPE_RECV_MSG_STATUS_FAIL, e.getMessage(), null, AdapterUtils.getClassName(url));
        CallbackManager.getInstance().sendResponse(response);
    }

    @Override
    public void dispatch(String url, Reader reader) throws Throwable {
        Logger.getInstance().debugThreadLog(ServiceConstants.TAG_SERVICE, "dispatch,url is: " + url);
        Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "dispatch url is: " + url);
        Type type = AdapterUtils.getType(url);
        if (type == null) {
            Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "dispatch type is null!");
            return;
        }
        Object obj = DataAdapterHandler.getObject(reader, type);
        if (obj instanceof Result) {//通用类型
            Result result = (Result) obj;
            Request request = DataManager.getInstance().getRequest(url);
            if (request == null) {
                Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "dispatch obj, get request is null!");
            }
            //
            if (result.data == null) {
                //如：app需要更新
                Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "dispatch obj, data is null! message is " + result.message);
                Response response = new Response(request, ServiceConstants.TYPE_RECV_MSG_STATUS_FAIL, result.message, null, AdapterUtils.getClassName(url));
                CallbackManager.getInstance().sendResponse(response);
                return;
            }
            Response response = new Response(request, result.message, result.data.data, AdapterUtils.getClassName(url));
            CallbackManager.getInstance().sendResponse(response);
            return;
        } else if (obj instanceof TradeOrderResult) {//最新成交
            TradeOrderResult result = (TradeOrderResult) obj;
            Request request = DataManager.getInstance().getRequest(url);
            if (request == null) {
                Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "dispatch obj, get request is null!");
            }
            Response response = new Response(request, result.message, result.data, AdapterUtils.getClassName(url));
            CallbackManager.getInstance().sendResponse(response);
            return;
        } else if (obj instanceof HistoryListBeanResult) {//当前委单
            HistoryListBeanResult result = (HistoryListBeanResult) obj;
            Request request = DataManager.getInstance().getRequest(url);
            if (request == null) {
                Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "dispatch obj, get request is null!");
            }
            Response response = new Response(request, result.message, result.data, AdapterUtils.getClassName(url));
            CallbackManager.getInstance().sendResponse(response);
            return;
        }
        Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "dispatch obj is not Result!");
    }
}