package huolongluo.byw.byw.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * <p>
 * Created by 火龙裸 on 2017/12/27 0027.
 */
public class AssetBean implements Parcelable
{
    /**
     * totalAsset : 0
     * rmb : {"total":0,"frozen":0,"canBuy":0}
     * coin : {"total":0,"frozen":0,"canSell":0}
     */

    private double totalAsset;
    private RmbBean rmb;
    private CoinBean coin;

     int code;
    AssetBean data;
    AssetBean asset;
    String message;
    boolean result;
   int  selfselection;
   int tradeId;
   String value;

   public AssetBean(){
   }
    protected AssetBean(Parcel in) {
        totalAsset = in.readDouble();
        rmb = in.readParcelable(RmbBean.class.getClassLoader());
        coin = in.readParcelable(CoinBean.class.getClassLoader());
        code = in.readInt();
        data = in.readParcelable(AssetBean.class.getClassLoader());
        asset = in.readParcelable(AssetBean.class.getClassLoader());
        message = in.readString();
        result = in.readByte() != 0;
        selfselection = in.readInt();
        tradeId = in.readInt();
        value = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(totalAsset);
        dest.writeParcelable(rmb, flags);
        dest.writeParcelable(coin, flags);
        dest.writeInt(code);
        dest.writeParcelable(data, flags);
        dest.writeParcelable(asset, flags);
        dest.writeString(message);
        dest.writeByte((byte) (result ? 1 : 0));
        dest.writeInt(selfselection);
        dest.writeInt(tradeId);
        dest.writeString(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AssetBean> CREATOR = new Creator<AssetBean>() {
        @Override
        public AssetBean createFromParcel(Parcel in) {
            return new AssetBean(in);
        }

        @Override
        public AssetBean[] newArray(int size) {
            return new AssetBean[size];
        }
    };

    public double getTotalAsset()
    {
        return totalAsset;
    }

    public void setTotalAsset(double totalAsset)
    {
        this.totalAsset = totalAsset;
    }

    public RmbBean getRmb()
    {
        return rmb;
    }

    public void setRmb(RmbBean rmb)
    {
        this.rmb = rmb;
    }

    public CoinBean getCoin()
    {
        return coin;
    }

    public void setCoin(CoinBean coin)
    {
        this.coin = coin;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public AssetBean getData() {
        return data;
    }

    public void setData(AssetBean data) {
        this.data = data;
    }

    public AssetBean getAsset() {
        return asset;
    }

    public void setAsset(AssetBean asset) {
        this.asset = asset;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getSelfselection() {
        return selfselection;
    }

    public void setSelfselection(int selfselection) {
        this.selfselection = selfselection;
    }

    public int getTradeId() {
        return tradeId;
    }

    public void setTradeId(int tradeId) {
        this.tradeId = tradeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AssetBean{" +
                "totalAsset=" + totalAsset +
                ", rmb=" + rmb +
                ", coin=" + coin +
                ", code='" + code + '\'' +
                ", data=" + data +
                ", asset=" + asset +
                ", message='" + message + '\'' +
                ", result=" + result +
                ", selfselection=" + selfselection +
                ", tradeId=" + tradeId +
                ", value='" + value + '\'' +
                '}';
    }
}
