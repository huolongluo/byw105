package huolongluo.byw.model;

import android.os.Parcel;
import android.os.Parcelable;

import huolongluo.byw.byw.bean.MarketListBean;

import java.util.ArrayList;
import java.util.List;

public class MarketResult {

    public String message;
    public String code;
    public SubMarketResult<Market> data;

    public static class SubMarketResult<Market> {

        public String value;
        public int code;
        public Market data;
    }

    public static class Market implements Parcelable {

        public int type;
        public boolean result;
        public String lastUpdateTime;
        public String title;
        public List<MarketListBean> list;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.type);
            dest.writeByte(this.result ? (byte) 1 : (byte) 0);
            dest.writeString(this.lastUpdateTime);
            dest.writeString(this.title);
            dest.writeList(this.list);
        }

        public Market() {
        }

        protected Market(Parcel in) {
            this.type = in.readInt();
            this.result = in.readByte() != 0;
            this.lastUpdateTime = in.readString();
            this.title = in.readString();
            this.list = new ArrayList<MarketListBean>();
            in.readList(this.list, MarketListBean.class.getClassLoader());
        }

        public static final Creator<Market> CREATOR = new Creator<Market>() {
            @Override
            public Market createFromParcel(Parcel source) {
                return new Market(source);
            }

            @Override
            public Market[] newArray(int size) {
                return new Market[size];
            }
        };
    }
}
