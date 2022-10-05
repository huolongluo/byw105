package com.android.coinw.socket;
import android.text.TextUtils;

import com.android.coinw.DataManager;
import com.android.coinw.ServiceConstants;
import com.android.coinw.model.Api;
import com.android.coinw.model.Message;
import com.android.coinw.service.CallbackManager;
import com.android.coinw.utils.ParamUtils;
import com.android.coinw.utils.Utilities;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.GsonUtil;
/**
 * 监听接口
 */
public class SampleWebSocketListener implements IWebSocketListener {
    //可考虑是否把响应数据一起缓存?
//    private volatile ConcurrentHashMap<String, Message> paramsMap = new ConcurrentHashMap<String, Message>();
    private volatile ConcurrentHashMap<String, Api> apiMap = new ConcurrentHashMap<String, Api>();
    //    private volatile ConcurrentHashMap<String, Long> recvMap = new ConcurrentHashMap<>();
    private boolean init = false;

    private void init() {
        //初始化基础映射类型数据
        initApi();
        //初始化设定时间更长
        Message message = ParamUtils.getMessage(AppConstants.SOCKET.riseFallApi, AppConstants.SOCKET.OPEN, 10000L, null, null);
        if (message != null) {
//            paramsMap.put(AppConstants.SOCKET.riseFallApi, message);
            DataManager.getInstance().updateMessage(message);
        }
    }

    private void initApi() {
        //TODO 初始化基础映射类型数据
        //可采用配置文件来处理，暂时采用代码方式
        Api riseFallApi = ParamUtils.getApi(AppConstants.SOCKET.riseFallApi);
        Api indexApi = ParamUtils.getApi(AppConstants.SOCKET.indexApi);
        Api etfIndexApi = ParamUtils.getApi(AppConstants.SOCKET.etfIndexApi);
        Api klineApi = ParamUtils.getApi(AppConstants.SOCKET.klineApi);
        Api redenvelope = ParamUtils.getApi(AppConstants.SOCKET.redenvelope);
        Api tradeApiV3 = ParamUtils.getApi(AppConstants.SOCKET.tradeApiV3);
        if (riseFallApi != null) {
            apiMap.put(riseFallApi.api, riseFallApi);
        }
        if (indexApi != null) {
            apiMap.put(indexApi.api, indexApi);
        }
        if (etfIndexApi != null) {
            apiMap.put(etfIndexApi.api, etfIndexApi);
        }
        if (klineApi != null) {
            apiMap.put(klineApi.api, klineApi);
        }
        if (redenvelope != null) {
            apiMap.put(redenvelope.api, redenvelope);
        }
        if (tradeApiV3 != null) {
            apiMap.put(tradeApiV3.api, tradeApiV3);
        }
    }

    @Override
    public void onOpen() {
        if (!init) {//只做第一次
            init();
            init = true;
        }
        Set<Map.Entry<String, Message>> set = DataManager.getInstance().getMessages().entrySet();
        for (Map.Entry<String, Message> entry : set) {
            String api = entry.getKey();
            Message message = entry.getValue();
            if (TextUtils.isEmpty(api) || message == null || TextUtils.equals(message.type, "Remove")) {
                //初始连接成功后，清除异常数据或本地Remove指令
                DataManager.getInstance().getMessages().remove(api);
                continue;
            }
            //重置请求时间
            DataManager.getInstance().updateReciveTime(api, System.currentTimeMillis(), false);
            SessionManager.getInstance().sendMessage(message.ciphertext);
        }
    }

    @Override
    public void onSend(Message message) {
        if (message == null) {
            return;
        }
        DataManager.getInstance().updateMessage(message);
    }

    @Override
    public void onMessage(String api, String type, String[] params, String msg) {
        Api refApi = apiMap.get(api);
        if (refApi == null || refApi.type == null) {
            //TODO 处理为空的情况
            Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "api or type is null!");
            return;
        }
        DataManager.getInstance().updateReciveTime(api, System.currentTimeMillis(), true);
        Object obj = GsonUtil.json2Obj(msg, refApi.type);
        dispatchMessage(refApi, api, params, obj);
    }

    public void dispatchMessage(Api refApi, String api, String[] params, Object data) {
        if (data == null) {
            Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "data is null ? " + (data == null));
            return;
        }
        Message message = DataManager.getInstance().getMessages().get(api);
        if (message == null) {
            Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, "get req msg obj is null!");
            return;
        }
        String paramStr = Arrays.toString(params);
        String msgParamStr = Arrays.toString(message.params);
        String logMsg = String.format("recv api:%s,params:%s,msg-params:%s", api, paramStr, msgParamStr);
        Logger.getInstance().debug(ServiceConstants.TAG_SERVICE, logMsg);
        //判断服务器推送的消息，是否是当前客户端订阅的消息
        if (!TextUtils.equals(paramStr, msgParamStr)) {
            //TODO 由于历史原因，服务器接口定义暂不能改，则控制如下
            //取消某消息订阅,并且不加入缓存
            Message cancelMsg = ParamUtils.getMessage(false, api, AppConstants.SOCKET.CLOSE, params);
            //执行在约定线程中
            Utilities.serviceQueue.postRunnable(new Runnable() {
                @Override
                public void run() {
                    SessionManager.getInstance().sendMessage(cancelMsg);
                }
            });
            return;
        }
        message.className = refApi.className;
        message.data = data;
        //通过回调接口向客户端发送消息
        CallbackManager.getInstance().sendMessage(message);
    }
}
