package huolongluo.byw.model;

import java.lang.reflect.Type;
import java.util.Map;

import huolongluo.byw.helper.INetCallback;
import huolongluo.bywx.OnResultCallback;

public class ReqData {

    public String tag;
    //HTTP
    public String url;
    public Map<String, Object> params;
    public Type type;
    //由于历史原因，故未把网络回调抽象为一个公司回调接口。
    public INetCallback netCallback;
    /*****************************/
    //SOCKET
    public String api;
    public Type socketType;
    //服务器向客户端推送的消息间隔时间
    public long interval;
    //由于历史原因，故未把网络回调抽象为一个公司回调接口。
    public OnResultCallback socketCallback;
}
