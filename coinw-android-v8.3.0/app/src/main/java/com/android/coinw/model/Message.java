package com.android.coinw.model;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
public class Message<T> implements Parcelable {
    //是否加入缓存
    public boolean cache = true;//默认加入缓存
    //服务器API接口
    public String api;
    //消息类型（type:register/Remove）
    public String type;
    //服务器API接口参数（本地可采用其他数据结构中转）
    public String[] params;
    //HTTP被尝方案的参数（Object对应于基本参数类型）
    public Map<String, Object> maps = new HashMap<String, Object>();
    //向服务器发送的完整密文数据（根据服务器协议，发送完整密文数据，无需再处理）
    public String ciphertext;
    //服务器向客户端推送的消息间隔时间
    public long interval;
    //请求时间
    public long reqTime;
    /*****************************************/
    //T的类全名
    public String className;
    //请求结果数据
    public T data;

    public Message() {
    }

    public Message(String api, String type, String[] params, Map<String, Object> maps, String ciphertext, long interval, long reqTime) {
        //默认加入缓存
        this(true, api, type, params, maps, ciphertext, interval, reqTime);
    }

    public Message(boolean cache, String api, String type, String[] params, Map<String, Object> maps, String ciphertext, long interval, long reqTime) {
        this.cache = cache;
        this.api = api;
        this.type = type;
        this.params = params;
        this.maps = maps;
        //服务器暂不支持对socket传输内容加密
        this.ciphertext = ciphertext;
        this.interval = interval;
        this.reqTime = reqTime;
    }

    protected Message(Parcel in) {
        cache = in.readByte() != 0;
        api = in.readString();
        type = in.readString();
        params = in.createStringArray();
        maps = in.readHashMap(Object.class.getClassLoader());
        ciphertext = in.readString();
        interval = in.readLong();
        reqTime = in.readLong();
        className = in.readString();
        if (!TextUtils.isEmpty(className)) {
            try {
                ClassLoader loader = Class.forName(className).getClassLoader();
                data = in.readParcelable(loader);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (cache ? 1 : 0));
        dest.writeString(api);
        dest.writeString(type);
        dest.writeStringArray(params);
        dest.writeMap(maps);
        dest.writeString(ciphertext);
        dest.writeLong(interval);
        dest.writeLong(reqTime);
        dest.writeString(className);
        if (data instanceof Parcelable) {
            dest.writeParcelable((Parcelable) data, 0);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Message) {
            Message message = (Message) obj;
            if (TextUtils.equals(this.ciphertext, message.ciphertext)) {
                return true;
            } else if (TextUtils.equals(this.api, message.api) && this.params == message.params) {//有可能type不同
                return true;
            }
        }
        return false;
    }
}
