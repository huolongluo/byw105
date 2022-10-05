package huolongluo.byw.reform.trade.bean;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
public class TradeOrder implements Parcelable {
    public CoinInfo coinInfo;
    public List<OrderInfo> handicap;
    public boolean result;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.coinInfo, flags);
        dest.writeTypedList(this.handicap);
        dest.writeByte(this.result ? (byte) 1 : (byte) 0);
    }

    public TradeOrder() {
    }

    protected TradeOrder(Parcel in) {
        this.coinInfo = in.readParcelable(CoinInfo.class.getClassLoader());
        this.handicap = in.createTypedArrayList(OrderInfo.CREATOR);
        this.result = in.readByte() != 0;
    }

    public static final Parcelable.Creator<TradeOrder> CREATOR = new Parcelable.Creator<TradeOrder>() {
        @Override
        public TradeOrder createFromParcel(Parcel source) {
            return new TradeOrder(source);
        }

        @Override
        public TradeOrder[] newArray(int size) {
            return new TradeOrder[size];
        }
    };

    public static class CoinInfo implements Parcelable {
        public long id;
        public String currency;
        public String fShortName;
        public String fSymbol;
        public String fname;

        protected CoinInfo(Parcel in) {
            id = in.readLong();
            currency = in.readString();
            fShortName = in.readString();
            fSymbol = in.readString();
            fname = in.readString();
        }

        public static final Creator<CoinInfo> CREATOR = new Creator<CoinInfo>() {
            @Override
            public CoinInfo createFromParcel(Parcel in) {
                return new CoinInfo(in);
            }

            @Override
            public CoinInfo[] newArray(int size) {
                return new CoinInfo[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(currency);
            dest.writeString(fShortName);
            dest.writeString(fSymbol);
            dest.writeString(fname);
        }
    }

    public static class OrderInfo implements Parcelable,Cloneable {
        public int id;//
        public String buyAmount;
        public String buyPrice;
        public String sellAmount;
        public String sellPrice;
        public OrderInfo(){}
        public OrderInfo(int id, String buyAmount, String buyPrice, String sellAmount, String sellPrice) {
            this.id = id;
            this.buyAmount = buyAmount;
            this.buyPrice = buyPrice;
            this.sellAmount = sellAmount;
            this.sellPrice = sellPrice;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.buyAmount);
            dest.writeString(this.buyPrice);
            dest.writeString(this.sellAmount);
            dest.writeString(this.sellPrice);
        }

        protected OrderInfo(Parcel in) {
            this.id = in.readInt();
            this.buyAmount = in.readString();
            this.buyPrice = in.readString();
            this.sellAmount = in.readString();
            this.sellPrice = in.readString();
        }

        public static final Creator<OrderInfo> CREATOR = new Creator<OrderInfo>() {
            @Override
            public OrderInfo createFromParcel(Parcel source) {
                return new OrderInfo(source);
            }

            @Override
            public OrderInfo[] newArray(int size) {
                return new OrderInfo[size];
            }
        };

        @Override
        public Object clone() {
            OrderInfo info=null;
            try {
                info=(OrderInfo)super.clone();
            }catch (CloneNotSupportedException e){
                e.printStackTrace();
            }
            return info;
        }
    }
}
