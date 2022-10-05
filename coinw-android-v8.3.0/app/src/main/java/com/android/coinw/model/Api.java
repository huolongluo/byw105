package com.android.coinw.model;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.lang.reflect.Type;
public class Api implements Parcelable {
    //http
    public String url;
    //socket
    public String api;
    public String className;
    public Type type;

    public Api(String url, String api, Type type, String className) {
        this.url = url;
        this.api = api;
        this.type = type;
        this.className = className;
    }

    protected Api(Parcel in) {
        url = in.readString();
        api = in.readString();
        className = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(api);
        dest.writeString(className);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Api> CREATOR = new Creator<Api>() {
        @Override
        public Api createFromParcel(Parcel in) {
            return new Api(in);
        }

        @Override
        public Api[] newArray(int size) {
            return new Api[size];
        }
    };
}
