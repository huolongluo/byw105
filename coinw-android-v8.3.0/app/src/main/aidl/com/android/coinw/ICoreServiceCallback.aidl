package com.android.coinw;
// Declare any non-default types here with import statements
import com.android.coinw.model.Response;
import com.android.coinw.model.Message;
/**
* 实现接口请在异步中执行
*/
interface ICoreServiceCallback {
    /** 返回请求数据 **/
    int onResponse(in Response response);
    /** 返回的socket消息 **/
    int onMessage(in Message message);
}
