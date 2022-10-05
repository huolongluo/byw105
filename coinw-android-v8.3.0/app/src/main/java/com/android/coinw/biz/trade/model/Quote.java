package com.android.coinw.biz.trade.model;
import android.os.Parcel;
import android.os.Parcelable;
public class Quote implements Parcelable {
    public int id;
    public String price = "0.0000";
    public String amount = "0.0000";

    protected Quote(Parcel in) {
        id = in.readInt();
        price = in.readString();
        amount = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(price);
        dest.writeString(amount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Quote> CREATOR = new Creator<Quote>() {
        @Override
        public Quote createFromParcel(Parcel in) {
            return new Quote(in);
        }

        @Override
        public Quote[] newArray(int size) {
            return new Quote[size];
        }
    };
}
