package huolongluo.byw.reform.home.activity.kline2.common;
import android.os.Parcel;
import android.os.Parcelable;
//跳转k线页面需要传入的参数
public class KLineEntity implements Parcelable {
    private int id;//币种id
    private String coinName;
    private String cnyName;
    private @Kline2Constants.TradeType int tradeType;

    public KLineEntity() {
    }

    public KLineEntity(int id, String coinName, String cnyName, @Kline2Constants.TradeType int tradeType) {
        this.id = id;
        this.coinName = coinName;
        this.cnyName = cnyName;
        this.tradeType = tradeType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getCnyName() {
        return cnyName;
    }

    public void setCnyName(String cnyName) {
        this.cnyName = cnyName;
    }

    public @Kline2Constants.TradeType int getTradeType() {
        return tradeType;
    }

    public void setTradeType(@Kline2Constants.TradeType int tradeType) {
        this.tradeType = tradeType;
    }

    public String getKLineId() {
        String kid;
        if(tradeType == Kline2Constants.TRADE_TYPE_LEVER){
            kid = (coinName + cnyName).toLowerCase();
        }else{
            kid = (coinName + "_" + cnyName).toUpperCase();
        }
        return kid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.coinName);
        dest.writeString(this.cnyName);
        dest.writeInt(this.tradeType);
    }

    protected KLineEntity(Parcel in) {
        this.id = in.readInt();
        this.coinName = in.readString();
        this.cnyName = in.readString();
        this.tradeType = in.readInt();
    }

    public static final Parcelable.Creator<KLineEntity> CREATOR = new Parcelable.Creator<KLineEntity>() {
        @Override
        public KLineEntity createFromParcel(Parcel source) {
            return new KLineEntity(source);
        }

        @Override
        public KLineEntity[] newArray(int size) {
            return new KLineEntity[size];
        }
    };
}
