package com.android.coinw.model;
import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import huolongluo.byw.io.AppConstants;
/**
 * 请求对象
 */
public class Request implements Parcelable {
    //是否加入缓存
    public boolean cache = false;
    //是否加入策略控制
    public boolean policy = false;
    //立即请求
    public boolean immed = true;
    //间隔时间（加入策略控制后，需要设置该参数）
    public long interval;
    //服务器请求URL
    public String url;
    //服务器接口参数（本地可采用其他数据结构中转）
    //服务器接口参数（Map对象，采用HashMap实现方式）
    public Map<String, Object> params;
    //请求时间
    public long reqTime;
    //超时时间
    public long timeout;
    //请求方式（GET/POST）
    public int method = AppConstants.COMMON.VAL_HTTP_POST;

    public Request() {
    }

    public Request(boolean cache, boolean policy, long interval, String url, Map<String, Object> params) {
        this(cache, policy, interval, url, params, System.currentTimeMillis(), 10 * 1000L);
    }

    public Request(boolean cache, boolean policy, long interval, String url, Map<String, Object> params, int method) {
        this(cache, policy, interval, url, params, System.currentTimeMillis(), 10 * 1000L, method);
    }

    public Request(boolean cache, boolean policy, boolean immed, long interval, String url, Map<String, Object> params) {
        this(cache, policy, immed, interval, url, params, System.currentTimeMillis(), 10 * 1000L);
    }

    public Request(boolean cache, boolean policy, long interval, String url, Map<String, Object> params, long reqTime, long timeout) {
        this(cache, policy, true, interval, url, params, reqTime, timeout);
    }

    public Request(boolean cache, boolean policy, long interval, String url, Map<String, Object> params, long reqTime, long timeout, int method) {
        this(cache, policy, true, interval, url, params, reqTime, timeout, method);
    }

    public Request(boolean cache, boolean policy, boolean immed, long interval, String url, Map<String, Object> params, long reqTime, long timeout) {
        //默认采用post方式，目的兼容币币旧接口
        this(cache, policy, true, interval, url, params, reqTime, timeout, AppConstants.COMMON.VAL_HTTP_POST);
    }

    public Request(boolean cache, boolean policy, boolean immed, long interval, String url, Map<String, Object> params, long reqTime, long timeout, int method) {
        this.cache = cache;
        this.policy = policy;
        this.immed = immed;
        this.interval = interval;
        this.url = url;
        this.params = params;
        this.reqTime = reqTime;
        this.timeout = timeout;
        this.method = method;
    }

    protected Request(@NotNull Parcel in) {
        cache = in.readByte() != 0;
        policy = in.readByte() != 0;
        immed = in.readByte() != 0;
        interval = in.readLong();
        url = in.readString();
        params = in.readHashMap(Object.class.getClassLoader());
        reqTime = in.readLong();
        timeout = in.readLong();
        method = in.readInt();
    }

    @Override
    public void writeToParcel(@NotNull Parcel dest, int flags) {
        dest.writeByte((byte) (cache ? 1 : 0));
        dest.writeByte((byte) (policy ? 1 : 0));
        dest.writeByte((byte) (immed ? 1 : 0));
        dest.writeLong(interval);
        dest.writeString(url);
        dest.writeMap(params);
        dest.writeLong(reqTime);
        dest.writeLong(timeout);
        dest.writeInt(method);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel in) {
            return new Request(in);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };
}
