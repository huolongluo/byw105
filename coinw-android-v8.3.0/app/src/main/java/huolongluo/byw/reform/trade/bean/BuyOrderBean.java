package huolongluo.byw.reform.trade.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by hy on 2018/11/14 0014.
 */

public class BuyOrderBean implements Parcelable {

    /**
     * id : 1
     * price : 0.095
     * amount : 67538.85800000001
     */

    private int id;
    private String price = "0.0000";
    private String amount = "0.0000";

    protected BuyOrderBean(Parcel in) {
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

    public static final Creator<BuyOrderBean> CREATOR = new Creator<BuyOrderBean>() {
        @Override
        public BuyOrderBean createFromParcel(Parcel in) {
            return new BuyOrderBean(in);
        }

        @Override
        public BuyOrderBean[] newArray(int size) {
            return new BuyOrderBean[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "BuyOrderBean{" + "id=" + id + ", price='" + price + '\'' + ", amount='" + amount + '\'' + '}';
    }
}
