package huolongluo.byw.byw.ui.fragment.maintab05.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LS on 2018/7/19.
 */

public class BuyBean implements Parcelable{
    private boolean result;
    private String money;
    private int tradeId;
    private String fbankName;
    private String fownerName;
    private String fbankAddress;
    private String fbankNumber;
    private String fremark;
    private int code;

    private BuyBean(){

    }

    protected BuyBean(Parcel in) {
        result = in.readByte() != 0;
        money = in.readString();
        tradeId = in.readInt();
        fbankName = in.readString();
        fownerName = in.readString();
        fbankAddress = in.readString();
        fbankNumber = in.readString();
        fremark = in.readString();
        code = in.readInt();
    }

    public static final Creator<BuyBean> CREATOR = new Creator<BuyBean>() {
        @Override
        public BuyBean createFromParcel(Parcel in) {
            return new BuyBean(in);
        }

        @Override
        public BuyBean[] newArray(int size) {
            return new BuyBean[size];
        }
    };

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public int getTradeId() {
        return tradeId;
    }

    public void setTradeId(int tradeId) {
        this.tradeId = tradeId;
    }

    public String getFbankName() {
        return fbankName;
    }

    public void setFbankName(String fbankName) {
        this.fbankName = fbankName;
    }

    public String getFownerName() {
        return fownerName;
    }

    public void setFownerName(String fownerName) {
        this.fownerName = fownerName;
    }

    public String getFbankAddress() {
        return fbankAddress;
    }

    public void setFbankAddress(String fbankAddress) {
        this.fbankAddress = fbankAddress;
    }

    public String getFbankNumber() {
        return fbankNumber;
    }

    public void setFbankNumber(String fbankNumber) {
        this.fbankNumber = fbankNumber;
    }

    public String getFremark() {
        return fremark;
    }

    public void setFremark(String fremark) {
        this.fremark = fremark;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (result ? 1 : 0));
        parcel.writeString(money);
        parcel.writeInt(tradeId);
        parcel.writeString(fbankName);
        parcel.writeString(fownerName);
        parcel.writeString(fbankAddress);
        parcel.writeString(fbankNumber);
        parcel.writeString(fremark);
        parcel.writeInt(code);
    }

    @Override
    public String toString() {
        return "BuyBean{" +
                "result=" + result +
                ", money='" + money + '\'' +
                ", tradeId=" + tradeId +
                ", fbankName='" + fbankName + '\'' +
                ", fownerName='" + fownerName + '\'' +
                ", fbankAddress='" + fbankAddress + '\'' +
                ", fbankNumber='" + fbankNumber + '\'' +
                ", fremark='" + fremark + '\'' +
                ", code=" + code +
                '}';
    }
}
