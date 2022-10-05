package huolongluo.byw.reform.trade.bean;

import android.os.Parcel;
import android.os.Parcelable;


import java.util.List;

import huolongluo.byw.byw.ui.fragment.latestdeal.LatestListBean;

/**
 * Created by LS on 2018/7/11.
 */

public class TradeOrderReBean implements Parcelable {
    private boolean result;
    private String lastUpdateTime;
    private List<TradeOrderBean> rows;

    protected TradeOrderReBean(Parcel in) {
        result = in.readByte() != 0;
        lastUpdateTime = in.readString();
        rows = in.createTypedArrayList(TradeOrderBean.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (result ? 1 : 0));
        dest.writeString(lastUpdateTime);
        dest.writeTypedList(rows);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TradeOrderReBean> CREATOR = new Creator<TradeOrderReBean>() {
        @Override
        public TradeOrderReBean createFromParcel(Parcel in) {
            return new TradeOrderReBean(in);
        }

        @Override
        public TradeOrderReBean[] newArray(int size) {
            return new TradeOrderReBean[size];
        }
    };

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public List<TradeOrderBean> getRows() {
        return rows;
    }

    public void setRows(List<TradeOrderBean> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "TradeOrderReBean{" +
                "result=" + result +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                ", rows=" + rows +
                '}';
    }
}
