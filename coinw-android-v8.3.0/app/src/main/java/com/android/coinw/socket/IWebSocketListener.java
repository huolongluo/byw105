package com.android.coinw.socket;

import com.android.coinw.model.Message;
/**
 * 自定义WebSocket监听接口
 */
public interface IWebSocketListener {
    /**
     * WebSocket打开
     */
    void onOpen();
    /**
     * 向服务器发送的消息
     * @param message
     */
    void onSend(Message message);
    /**
     * 接收到服务器消息
     * @param msg
     */
    void onMessage(String api, String type, String[] params, String msg);
}