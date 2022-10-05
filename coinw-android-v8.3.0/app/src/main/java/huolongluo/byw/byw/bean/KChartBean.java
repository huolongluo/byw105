package huolongluo.byw.byw.bean;
import android.os.Parcel;
import android.os.Parcelable;


import java.util.ArrayList;
import java.util.List;
/**
 * Created by 火龙裸 on 2018/5/9.
 */
public class KChartBean implements Parcelable {
    /**
     * code : 0
     * msg : 获取K线数据
     * time : 1525836259751
     * id : 31
     * cnyName : CNYT
     * coinName : Coins
     * cnName : Coins
     */
    String coinSymbol;//币种符号
    private int code;
    private String msg;
    private long time;
    private String LatestDealPrice;
    private String LatestDealPriceExchange;
    private String OneDayLowest;
    private String OneDayHighest;
    private String OneDayTotal;
    private String priceRaiseRate;
    private String coinAsset_cny;
    private String coinAsset_usd;
    private int id;
    private String cnyName;
    private String coinName;
    private String cnName;
    private List<List<String>> data;
    private YearData oneYearData;
    private String netVal = "";

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getLatestDealPrice() {
        return LatestDealPrice;
    }

    public void setLatestDealPrice(String LatestDealPrice) {
        this.LatestDealPrice = LatestDealPrice;
    }

    public String getOneDayLowest() {
        return OneDayLowest;
    }

    public String getLatestDealPriceExchange() {
        return LatestDealPriceExchange;
    }

    public void setLatestDealPriceExchange(String latestDealPriceExchange) {
        LatestDealPriceExchange = latestDealPriceExchange;
    }

    public void setOneDayLowest(String OneDayLowest) {
        this.OneDayLowest = OneDayLowest;
    }

    public String getOneDayHighest() {
        return OneDayHighest;
    }

    public void setOneDayHighest(String OneDayHighest) {
        this.OneDayHighest = OneDayHighest;
    }

    public String getOneDayTotal() {
        return OneDayTotal;
    }

    public void setOneDayTotal(String OneDayTotal) {
        this.OneDayTotal = OneDayTotal;
    }

    public String getPriceRaiseRate() {
        return priceRaiseRate;
    }

    public void setPriceRaiseRate(String priceRaiseRate) {
        this.priceRaiseRate = priceRaiseRate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCnyName() {
        return cnyName;
    }

    public void setCnyName(String cnyName) {
        this.cnyName = cnyName;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getNetVal() {
        return netVal;
    }

    public void setNetVal(String netVal) {
        this.netVal = netVal;
    }

    public List<List<String>> getData() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }

    public String getCoinAsset_cny() {
        return coinAsset_cny;
    }

    public void setCoinAsset_cny(String coinAsset_cny) {
        this.coinAsset_cny = coinAsset_cny;
    }

    public String getCoinAsset_usd() {
        return coinAsset_usd;
    }

    public void setCoinAsset_usd(String coinAsset_usd) {
        this.coinAsset_usd = coinAsset_usd;
    }

    public YearData getOneYearData() {
        return oneYearData;
    }

    public void setOneYearData(YearData oneYearData) {
        this.oneYearData = oneYearData;
    }

    public static Creator<KChartBean> getCREATOR() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "KChartBean{" + "code=" + code + ", msg='" + msg + '\'' + ", time=" + time + ", LatestDealPrice='" + LatestDealPrice + '\'' + ", OneDayLowest='" + OneDayLowest + '\'' + ", OneDayHighest='" + OneDayHighest + '\'' + ", OneDayTotal='" + OneDayTotal + '\'' + ", priceRaiseRate='" + priceRaiseRate + '\'' + ", coinAsset_cny='" + coinAsset_cny + '\'' + ", coinAsset_usd='" + coinAsset_usd + '\'' + ", id=" + id + ", cnyName='" + cnyName + '\'' + ", coinName='" + coinName + '\'' + ", cnName='" + cnName + '\'' + ", data=" + data + ", oneYearData=" + oneYearData + '}';
    }

    public String getCoinSymbol() {
        return coinSymbol;
    }

    public void setCoinSymbol(String coinSymbol) {
        this.coinSymbol = coinSymbol;
    }

    public static class YearData implements Parcelable {
        public int exchangeId;
        public String maxYear;
        public String minYear;

        protected YearData(Parcel in) {
            exchangeId = in.readInt();
            maxYear = in.readString();
            minYear = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(exchangeId);
            dest.writeString(maxYear);
            dest.writeString(minYear);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<YearData> CREATOR = new Creator<YearData>() {
            @Override
            public YearData createFromParcel(Parcel in) {
                return new YearData(in);
            }

            @Override
            public YearData[] newArray(int size) {
                return new YearData[size];
            }
        };

        @Override
        public String toString() {
            return "YearData{" + "exchangeId=" + exchangeId + ", maxYear='" + maxYear + '\'' + ", minYear='" + minYear + '\'' + '}';
        }

        public int getExchangeId() {
            return exchangeId;
        }

        public void setExchangeId(int exchangeId) {
            this.exchangeId = exchangeId;
        }

        public String getMaxYear() {
            return maxYear;
        }

        public void setMaxYear(String maxYear) {
            this.maxYear = maxYear;
        }

        public String getMinYear() {
            return minYear;
        }

        public void setMinYear(String minYear) {
            this.minYear = minYear;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.coinSymbol);
        dest.writeInt(this.code);
        dest.writeString(this.msg);
        dest.writeLong(this.time);
        dest.writeString(this.LatestDealPrice);
        dest.writeString(this.LatestDealPriceExchange);
        dest.writeString(this.OneDayLowest);
        dest.writeString(this.OneDayHighest);
        dest.writeString(this.OneDayTotal);
        dest.writeString(this.priceRaiseRate);
        dest.writeString(this.coinAsset_cny);
        dest.writeString(this.coinAsset_usd);
        dest.writeInt(this.id);
        dest.writeString(this.cnyName);
        dest.writeString(this.coinName);
        dest.writeString(this.cnName);
        dest.writeList(this.data);
        dest.writeParcelable(this.oneYearData, flags);
        dest.writeString(this.netVal);
    }

    public KChartBean() {
    }

    protected KChartBean(Parcel in) {
        this.coinSymbol = in.readString();
        this.code = in.readInt();
        this.msg = in.readString();
        this.time = in.readLong();
        this.LatestDealPrice = in.readString();
        this.LatestDealPriceExchange = in.readString();
        this.OneDayLowest = in.readString();
        this.OneDayHighest = in.readString();
        this.OneDayTotal = in.readString();
        this.priceRaiseRate = in.readString();
        this.coinAsset_cny = in.readString();
        this.coinAsset_usd = in.readString();
        this.id = in.readInt();
        this.cnyName = in.readString();
        this.coinName = in.readString();
        this.cnName = in.readString();
        this.data = new ArrayList<List<String>>();
        in.readList(this.data, List.class.getClassLoader());
        this.oneYearData = in.readParcelable(YearData.class.getClassLoader());
        this.netVal = in.readString();
    }

    public static final Creator<KChartBean> CREATOR = new Creator<KChartBean>() {
        @Override
        public KChartBean createFromParcel(Parcel source) {
            return new KChartBean(source);
        }

        @Override
        public KChartBean[] newArray(int size) {
            return new KChartBean[size];
        }
    };
}
