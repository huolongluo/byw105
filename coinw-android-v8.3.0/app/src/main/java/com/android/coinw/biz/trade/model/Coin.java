package com.android.coinw.biz.trade.model;
import android.os.Parcel;
import android.os.Parcelable;

import huolongluo.bywx.utils.AppUtils;
public class Coin implements Parcelable {
    //由需求确定，
    public int id = AppUtils.getDefaultCoinId();
    public String coinName = AppUtils.getDefaultCoinName();//币名称
    public String cnyName = AppUtils.getDefaultCnyName();//买币名称
    public String times="";//杠杆使用的倍数
    public int isSelf = 0;//是否自选（1：是，0：否）
    public boolean isETF = false;

    public Coin() {
    }

    public Coin(int id, String coinName, String cnyName, int isSelf) {
        this.id = id;
        this.coinName = coinName;
        this.cnyName = cnyName;
        this.isSelf = isSelf;
    }

    public Coin(int id, String coinName, String cnyName, int isSelf, boolean isETF) {
        this.id = id;
        this.coinName = coinName;
        this.cnyName = cnyName;
        this.isSelf = isSelf;
        this.isETF = isETF;
    }

    protected Coin(Parcel in) {
        id = in.readInt();
        coinName = in.readString();
        cnyName = in.readString();
        isSelf = in.readInt();
        isETF = in.readByte() != 0;
    }

    public static final Creator<Coin> CREATOR = new Creator<Coin>() {
        @Override
        public Coin createFromParcel(Parcel in) {
            return new Coin(in);
        }

        @Override
        public Coin[] newArray(int size) {
            return new Coin[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(coinName);
        dest.writeString(cnyName);
        dest.writeInt(isSelf);
        dest.writeByte((byte) (isETF ? 1 : 0));
    }
}
