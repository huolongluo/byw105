package com.android.coinw;
// Declare any non-default types here with import statements
import com.android.coinw.ICoreServiceCallback;
import com.android.coinw.model.Message;
import com.android.coinw.model.Request;

interface ICoreService {
    /** 注册监听 **/
    void registerListener(ICoreServiceCallback callback);
    /** 反注册监听 **/
    void unregisterListener();
    /** 向服务器发送消息 **/
    void sendMessage(in Message message);
    /** 向服务器发起请求 **/
    void sendRequest(in Request request);
    /** 取消请求 **/
    void removeRequest(String url);
    /** 更新配置信息 **/
    void updateConfig(String data);
}
