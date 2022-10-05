package huolongluo.byw.reform.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Administrator on 2018/11/22 0022.
 */
public class EntrustInfoBean implements Parcelable {
    /*    "cnytName": "CNYT",
         "coinName": "HC",
         "fcount": 123.4560,
         "fid": 1865854,
         "flastUpdatTime": "2018-11-16 13:56:54",
         "fprice": 123.456000,
         "fstatus": 1,
         "fstatus_s": "未成交",
         "fsuccessCount": 0.0000,
         "fsuccessprice": 0.0,
         "title": "限价卖出",
         "type": 1
         tradeId:6

         */
    public String ffees;//新添，手续费
    public String cnytName;
    public String coinName;
    public String fcount;
    public long fid;
    public String flastUpdatTime;
    public String fprice;
    public float fstatus;
    public String fstatus_s;
    public String fsuccessCount;
    public String fsuccessprice;
    public String title;
    public int type;

    protected EntrustInfoBean(Parcel in) {
        ffees = in.readString();
        cnytName = in.readString();
        coinName = in.readString();
        fcount = in.readString();
        fid = in.readLong();
        flastUpdatTime = in.readString();
        fprice = in.readString();
        fstatus = in.readFloat();
        fstatus_s = in.readString();
        fsuccessCount = in.readString();
        fsuccessprice = in.readString();
        title = in.readString();
        type = in.readInt();
        tradeId = in.readInt();
    }

    public static final Creator<EntrustInfoBean> CREATOR = new Creator<EntrustInfoBean>() {
        @Override
        public EntrustInfoBean createFromParcel(Parcel in) {
            return new EntrustInfoBean(in);
        }

        @Override
        public EntrustInfoBean[] newArray(int size) {
            return new EntrustInfoBean[size];
        }
    };

    public int getTradeId() {
        return tradeId;
    }

    public void setTradeId(int tradeId) {
        this.tradeId = tradeId;
    }

    public int tradeId;

    public String getCnytName() {
        return cnytName;
    }

    public void setCnytName(String cnytName) {
        this.cnytName = cnytName;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getFcount() {
        return fcount;
    }

    public void setFcount(String fcount) {
        this.fcount = fcount;
    }

    public long getFid() {
        return fid;
    }

    public String getFlastUpdatTime() {
        return flastUpdatTime;
    }

    public void setFlastUpdatTime(String flastUpdatTime) {
        this.flastUpdatTime = flastUpdatTime;
    }

    public String getFprice() {
        return fprice;
    }

    public void setFprice(String fprice) {
        this.fprice = fprice;
    }

    public float getFstatus() {
        return fstatus;
    }

    public void setFstatus(float fstatus) {
        this.fstatus = fstatus;
    }

    public String getFstatus_s() {
        return fstatus_s;
    }

    public void setFstatus_s(String fstatus_s) {
        this.fstatus_s = fstatus_s;
    }

    public String getFsuccessCount() {
        return fsuccessCount;
    }

    public void setFsuccessCount(String fsuccessCount) {
        this.fsuccessCount = fsuccessCount;
    }

    public String getFsuccessprice() {
        return fsuccessprice;
    }

    public void setFsuccessprice(String fsuccessprice) {
        this.fsuccessprice = fsuccessprice;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "EntrustInfoBean{" + "cnytName='" + cnytName + '\'' + ", coinName='" + coinName + '\'' + ", fcount=" + fcount + ", fid=" + fid + ", flastUpdatTime='" + flastUpdatTime + '\'' + ", fprice=" + fprice + ", fstatus=" + fstatus + ", fstatus_s='" + fstatus_s + '\'' + ", fsuccessCount=" + fsuccessCount + ", fsuccessprice=" + fsuccessprice + ", title='" + title + '\'' + ", type=" + type + ", tradeId=" + tradeId + '}';
    }

    public void setFid(long fid) {
        this.fid = fid;
    }

    public String getFfees() {
        return ffees;
    }

    public void setFfees(String ffees) {
        this.ffees = ffees;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ffees);
        dest.writeString(cnytName);
        dest.writeString(coinName);
        dest.writeString(fcount);
        dest.writeLong(fid);
        dest.writeString(flastUpdatTime);
        dest.writeString(fprice);
        dest.writeFloat(fstatus);
        dest.writeString(fstatus_s);
        dest.writeString(fsuccessCount);
        dest.writeString(fsuccessprice);
        dest.writeString(title);
        dest.writeInt(type);
        dest.writeInt(tradeId);
    }
}
