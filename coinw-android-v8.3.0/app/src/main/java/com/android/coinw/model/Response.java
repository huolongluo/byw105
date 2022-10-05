package com.android.coinw.model;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.coinw.ServiceConstants;

import java.util.HashMap;

public class Response<T> implements Parcelable {
    //请求对象
    public Request req;
    //请求结果状态码
    public int status = ServiceConstants.TYPE_RECV_MSG_STATUS_SUCCESS;//成功
    //请求结果消息提示
    public String message;
    //T的类全名
    public String className;
    //请求结果数据
    public T data;

    public Response() {
    }

    public Response(Request request, String message, T data, String className) {
        this(request, ServiceConstants.TYPE_RECV_MSG_STATUS_SUCCESS, message, data, className);
    }

    public Response(Request request, int status, String message, T data, String className) {
        this.req = request;
        this.status = status;
        this.message = message;
        this.data = data;
        this.className = className;
    }

    protected Response(Parcel in) {
        req = in.readParcelable(Request.class.getClassLoader());
        status = in.readInt();
        message = in.readString();
        className = in.readString();
        try {
            ClassLoader loader = Class.forName(className).getClassLoader();
            data = in.readParcelable(loader);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(req, flags);
        dest.writeInt(status);
        dest.writeString(message);
        dest.writeString(className);
        if (data instanceof Parcelable) {
            dest.writeParcelable((Parcelable) data, 0);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Response> CREATOR = new Creator<Response>() {
        @Override
        public Response createFromParcel(Parcel in) {
            return new Response(in);
        }

        @Override
        public Response[] newArray(int size) {
            return new Response[size];
        }
    };
}
