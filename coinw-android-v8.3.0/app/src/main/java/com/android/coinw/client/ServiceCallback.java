package com.android.coinw.client;
import android.os.RemoteException;

import com.android.coinw.ICoreServiceCallback;
import com.android.coinw.ServiceConstants;
import com.android.coinw.model.Message;
import com.android.coinw.model.Response;
import com.android.coinw.utils.Utilities;

import huolongluo.byw.log.Logger;

/**
 * 核心服务回调接口
 */
public class ServiceCallback extends ICoreServiceCallback.Stub {

    @Override
    public int onResponse(Response response) throws RemoteException {
        //
        if (response == null) {
            return 0;
        }
        //TODO 发送失败，大量测试
        //向主线程发送更新数据消息
        boolean posted = Utilities.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                //TODO 线程跟踪验证日志
                Logger.getInstance().debugThreadLog(ServiceConstants.TAG_CLIENT, "client onResponse");
//                if (response.req == null) {
//                    Logger.getInstance().debugThreadLog(ServiceConstants.TAG_CLIENT, "response,get request is null!");
//                    return;
//                }
//                AppClient.getInstance().dispatchResponse(response.req.url, response.data, response.req.params);
//                AppClient.getInstance().dispatchResponse(response);
            }
        });
        if (!posted) {
            //TODO Handler的特殊异常情况，详情见Handler源码定义
            //向主线程发送消息失败后的处理
            Utilities.resetMainHandler();
        }
        return 0;
    }

    @Override
    public int onMessage(Message message) throws RemoteException {
        //向主线程发送更新数据消息
        boolean posted = Utilities.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                //TODO 线程跟踪验证日志
                Logger.getInstance().debugThreadLog(ServiceConstants.TAG_CLIENT, "client onResponse");
//                AppClient.getInstance().dispatchMessage(message.api, message.data, message.params);
            }
        });
        if (!posted) {
            //TODO Handler的特殊异常情况，详情见Handler源码定义
            //向主线程发送消息失败后的处理
            Utilities.resetMainHandler();
        }
        return 0;
    }
}
