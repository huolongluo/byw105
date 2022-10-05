package huolongluo.byw.byw.bean;
import android.os.Parcel;
import android.os.Parcelable;


import java.io.Serializable;
import java.util.List;

import huolongluo.byw.reform.bean.EntrustInfoBean;
/**
 * Created by 火龙裸 on 2017/12/29.
 */
public class HistoryListBean implements Parcelable {
    /**
     * result : true
     * totalPage : 1
     * currentPage : 1
     * lastUpdateTime : 2018-01-12 16:10:33
     * list : [{"fid":7440646,"type":0,"title":"限价买入","fstatus":1,"fstatus_s":"未成交","flastUpdatTime":"2018-01-12 16:03:00","fprice":0.1,"fcount":200,
     * "fsuccessCount":0,"fsuccessprice":0}]
     */
    private boolean result;
    private int totalPage;
    private int currentPage;
    private String lastUpdateTime;
    private List<EntrustInfoBean> list;
    private int symbol;
    private int code;
    private String value;

    protected HistoryListBean(Parcel in) {
        result = in.readByte() != 0;
        totalPage = in.readInt();
        currentPage = in.readInt();
        lastUpdateTime = in.readString();
        list = in.createTypedArrayList(EntrustInfoBean.CREATOR);
        symbol = in.readInt();
        code = in.readInt();
        value = in.readString();
    }

    public static final Creator<HistoryListBean> CREATOR = new Creator<HistoryListBean>() {
        @Override
        public HistoryListBean createFromParcel(Parcel in) {
            return new HistoryListBean(in);
        }

        @Override
        public HistoryListBean[] newArray(int size) {
            return new HistoryListBean[size];
        }
    };

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public List<EntrustInfoBean> getList() {
        return list;
    }

    public void setList(List<EntrustInfoBean> list) {
        this.list = list;
    }

    public void setSymbol(int symbol) {
        this.symbol = symbol;
    }

    public int getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return "HistoryListBean{" + "result=" + result + ", totalPage=" + totalPage + ", currentPage=" + currentPage + ", lastUpdateTime='" + lastUpdateTime + '\'' + ", list=" + list + '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (result ? 1 : 0));
        dest.writeInt(totalPage);
        dest.writeInt(currentPage);
        dest.writeString(lastUpdateTime);
        dest.writeTypedList(list);
        dest.writeInt(symbol);
        dest.writeInt(code);
        dest.writeString(value);
    }
}
