package huolongluo.byw.reform.trade.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by LS on 2018/7/11.
 */

public class TradeOrderBean implements Parcelable {
    private String id;
    private String time;
    private String price;
    private String amount;
    private String en_type;
    private String type;

    protected TradeOrderBean(Parcel in) {
        id = in.readString();
        time = in.readString();
        price = in.readString();
        amount = in.readString();
        en_type = in.readString();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(time);
        dest.writeString(price);
        dest.writeString(amount);
        dest.writeString(en_type);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TradeOrderBean> CREATOR = new Creator<TradeOrderBean>() {
        @Override
        public TradeOrderBean createFromParcel(Parcel in) {
            return new TradeOrderBean(in);
        }

        @Override
        public TradeOrderBean[] newArray(int size) {
            return new TradeOrderBean[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getEn_type() {
        return en_type;
    }

    public void setEn_type(String en_type) {
        this.en_type = en_type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
