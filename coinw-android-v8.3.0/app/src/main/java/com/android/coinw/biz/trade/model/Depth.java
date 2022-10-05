package com.android.coinw.biz.trade.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Depth implements Parcelable {
    private String api;//quote.depth
    private List<Quote> asks;//卖盘
    private List<Quote> bids;//买盘
    private long id;
    private String topic;
    private String ts;

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public List<Quote> getAsks() {
        return asks;
    }

    public void setAsks(List<Quote> asks) {
        this.asks = asks;
    }

    public List<Quote> getBids() {
        return bids;
    }

    public void setBids(List<Quote> bids) {
        this.bids = bids;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.api);
        dest.writeTypedList(this.asks);
        dest.writeTypedList(this.bids);
        dest.writeLong(this.id);
        dest.writeString(this.topic);
        dest.writeString(this.ts);
    }

    public Depth() {
    }

    protected Depth(Parcel in) {
        this.api = in.readString();
        this.asks = in.createTypedArrayList(Quote.CREATOR);
        this.bids = in.createTypedArrayList(Quote.CREATOR);
        this.id = in.readLong();
        this.topic = in.readString();
        this.ts = in.readString();
    }

    public static final Parcelable.Creator<Depth> CREATOR = new Parcelable.Creator<Depth>() {
        @Override
        public Depth createFromParcel(Parcel source) {
            return new Depth(source);
        }

        @Override
        public Depth[] newArray(int size) {
            return new Depth[size];
        }
    };
}
