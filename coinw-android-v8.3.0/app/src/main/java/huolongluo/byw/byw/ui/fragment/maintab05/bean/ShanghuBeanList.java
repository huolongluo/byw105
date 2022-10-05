package huolongluo.byw.byw.ui.fragment.maintab05.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LS on 2018/7/19.
 */

public class ShanghuBeanList implements Parcelable{
    private String fname;
    private int dealNumber;
    private String rechargeSpeed;
    private String amount;
    private int pcId;

    public ShanghuBeanList(){

    }

    protected ShanghuBeanList(Parcel in) {
        fname = in.readString();
        dealNumber = in.readInt();
        rechargeSpeed = in.readString();
        amount = in.readString();
        pcId = in.readInt();
    }

    public static final Creator<ShanghuBeanList> CREATOR = new Creator<ShanghuBeanList>() {
        @Override
        public ShanghuBeanList createFromParcel(Parcel in) {
            return new ShanghuBeanList(in);
        }

        @Override
        public ShanghuBeanList[] newArray(int size) {
            return new ShanghuBeanList[size];
        }
    };

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public int getDealNumber() {
        return dealNumber;
    }

    public void setDealNumber(int dealNumber) {
        this.dealNumber = dealNumber;
    }

    public String getRechargeSpeed() {
        return rechargeSpeed;
    }

    public void setRechargeSpeed(String rechargeSpeed) {
        this.rechargeSpeed = rechargeSpeed;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getPcId() {
        return pcId;
    }

    public void setPcId(int pcId) {
        this.pcId = pcId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fname);
        parcel.writeInt(dealNumber);
        parcel.writeString(rechargeSpeed);
        parcel.writeString(amount);
        parcel.writeInt(pcId);
    }
}
