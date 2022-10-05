package huolongluo.byw.reform.trade.bean;
import android.os.Parcel;
import android.os.Parcelable;


import java.util.List;

import huolongluo.byw.byw.bean.AssetBean;
import huolongluo.byw.byw.bean.CoinInfoBean;
import huolongluo.byw.byw.bean.QuotationBean;
/**
 * Created by 火龙裸 on 2017/12/27.
 */
public class TradeInfoBean implements Parcelable {
    /**
     * result : true
     * lastUpdateTime : 2017-12-27 16:59:13
     * cnyName : CNY
     * vir_id1 : 0
     * vir_id1_isWithDraw : true
     * vir_id2 : 3
     * vir_id2_isWithDraw : true
     * coinName : BC
     * cnName : BC
     * currency : CNY
     * currencySymbol : ¥
     * sellDepth : [{"id":1,"price":"0.096","amount":"29310.493"},{"id":2,"price":"0.097","amount":"26537.804"},{"id":3,"price":"0.098",
     * "amount":"412272.168"},{"id":4,"price":"0.099","amount":"126620.93"},{"id":5,"price":"0.1","amount":"304078.169"}]
     * buyDepth : [{"id":1,"price":"0.095","amount":"67538.85800000001"},{"id":2,"price":"0.094","amount":"209392.8"},{"id":3,"price":"0.093",
     * "amount":"129560.4"},{"id":4,"price":"0.092","amount":"34610.936"},{"id":5,"price":"0.091","amount":"24803.772"}]
     * quotation : {"LatestDealPrice":0.096,"SellOnePrice":0.096,"BuyOnePrice":0.095,"OneDayLowest":0.092,"OneDayHighest":0.097,"OneDayTotal":7464642.821,
     * "priceRaiseRate":3.22}
     * coinInfo : {"id":3,"fname":"BC","fShortName":"BC","fSymbol":"b"}
     * <p>
     * fid       新加
     * selfselection   新加
     */
    private boolean result;
    private String lastUpdateTime;
    private String cnyName;
    private int vir_id1;
    private boolean vir_id1_isWithDraw;
    private int vir_id2;
    private boolean vir_id2_isWithDraw;
    private String coinName;
    private String cnName;
    private String currency;
    private String currencySymbol;
    private QuotationBean quotation;
    private CoinInfoBean coinInfo;
    private AssetBean asset;
    private List<BuyOrderBean> sellDepth;
    private List<BuyOrderBean> buyDepth;
    private String value;
    private int selfselection;
    private int fid;
    private List<TradeOrderBean> trades;
    private String netVal = "";
    private String currentPriceType;

    //以前socket返回使用了的字段
    public String getCnyName() {
        return cnyName;
    }
    public String getCoinName() {
        return coinName;
    }
    public String getNetVal() {
        return netVal;
    }
    public String getCurrency() {
        return currency;
    }
    public QuotationBean getQuotation() {
        return quotation;
    }
    public int getFid() {
        return fid;
    }
    public List<TradeOrderBean> getTrades() {
        return trades;
    }

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


    public void setCnyName(String cnyName) {
        this.cnyName = cnyName;
    }

    public int getVir_id1() {
        return vir_id1;
    }

    public void setVir_id1(int vir_id1) {
        this.vir_id1 = vir_id1;
    }

    public boolean isVir_id1_isWithDraw() {
        return vir_id1_isWithDraw;
    }

    public void setVir_id1_isWithDraw(boolean vir_id1_isWithDraw) {
        this.vir_id1_isWithDraw = vir_id1_isWithDraw;
    }

    public int getVir_id2() {
        return vir_id2;
    }

    public void setVir_id2(int vir_id2) {
        this.vir_id2 = vir_id2;
    }

    public boolean isVir_id2_isWithDraw() {
        return vir_id2_isWithDraw;
    }

    public void setVir_id2_isWithDraw(boolean vir_id2_isWithDraw) {
        this.vir_id2_isWithDraw = vir_id2_isWithDraw;
    }

    public String getCurrentPriceType() {
        return currentPriceType;
    }

    public void setCurrentPriceType(String currentPriceType) {
        this.currentPriceType = currentPriceType;
    }


    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public void setNetVal(String netVal) {
        this.netVal = netVal;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }


    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }


    public void setQuotation(QuotationBean quotation) {
        this.quotation = quotation;
    }

    public CoinInfoBean getCoinInfo() {
        return coinInfo;
    }

    public void setCoinInfo(CoinInfoBean coinInfo) {
        this.coinInfo = coinInfo;
    }

    public AssetBean getAsset() {
        return asset;
    }

    public void setAsset(AssetBean asset) {
        this.asset = asset;
    }

    public List<BuyOrderBean> getSellDepth() {
        return sellDepth;
    }

    public void setSellDepth(List<BuyOrderBean> sellDepth) {
        this.sellDepth = sellDepth;
    }

    public List<BuyOrderBean> getBuyDepth() {
        return buyDepth;
    }

    public void setBuyDepth(List<BuyOrderBean> buyDepth) {
        this.buyDepth = buyDepth;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getSelfselection() {
        return selfselection;
    }

    public void setSelfselection(int selfselection) {
        this.selfselection = selfselection;
    }


    public void setFid(int fid) {
        this.fid = fid;
    }


    public void setTrades(List<TradeOrderBean> trades) {
        this.trades = trades;
    }

    @Override
    public String toString() {
        return "BuyInBean{" + "result=" + result + ", lastUpdateTime='" + lastUpdateTime + '\'' + ", cnyName='" + cnyName + '\'' + ", vir_id1=" + vir_id1 + ", vir_id1_isWithDraw=" + vir_id1_isWithDraw + ", vir_id2=" + vir_id2 + ", vir_id2_isWithDraw=" + vir_id2_isWithDraw +", coinName='" + coinName + '\'' + ", cnName='" + cnName + '\'' + ", currency='" + currency + '\'' + ", currencySymbol='" + currencySymbol + '\'' + ", quotation=" + quotation + ", coinInfo=" + coinInfo + ", asset=" + asset + ", sellDepth=" + sellDepth + ", buyDepth=" + buyDepth + ", value='" + value + '\'' + ", selfselection=" + selfselection + ", fid=" + fid + '}';
    }

    public TradeInfoBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.result ? (byte) 1 : (byte) 0);
        dest.writeString(this.lastUpdateTime);
        dest.writeString(this.cnyName);
        dest.writeInt(this.vir_id1);
        dest.writeByte(this.vir_id1_isWithDraw ? (byte) 1 : (byte) 0);
        dest.writeInt(this.vir_id2);
        dest.writeByte(this.vir_id2_isWithDraw ? (byte) 1 : (byte) 0);
        dest.writeString(this.coinName);
        dest.writeString(this.cnName);
        dest.writeString(this.currency);
        dest.writeString(this.currencySymbol);
        dest.writeParcelable(this.quotation, flags);
        dest.writeParcelable(this.coinInfo, flags);
        dest.writeParcelable(this.asset, flags);
        dest.writeTypedList(this.sellDepth);
        dest.writeTypedList(this.buyDepth);
        dest.writeString(this.value);
        dest.writeInt(this.selfselection);
        dest.writeInt(this.fid);
        dest.writeTypedList(this.trades);
        dest.writeString(this.netVal);
        dest.writeString(this.currentPriceType);
    }

    protected TradeInfoBean(Parcel in) {
        this.result = in.readByte() != 0;
        this.lastUpdateTime = in.readString();
        this.cnyName = in.readString();
        this.vir_id1 = in.readInt();
        this.vir_id1_isWithDraw = in.readByte() != 0;
        this.vir_id2 = in.readInt();
        this.vir_id2_isWithDraw = in.readByte() != 0;
        this.coinName = in.readString();
        this.cnName = in.readString();
        this.currency = in.readString();
        this.currencySymbol = in.readString();
        this.quotation = in.readParcelable(QuotationBean.class.getClassLoader());
        this.coinInfo = in.readParcelable(CoinInfoBean.class.getClassLoader());
        this.asset = in.readParcelable(AssetBean.class.getClassLoader());
        this.sellDepth = in.createTypedArrayList(BuyOrderBean.CREATOR);
        this.buyDepth = in.createTypedArrayList(BuyOrderBean.CREATOR);
        this.value = in.readString();
        this.selfselection = in.readInt();
        this.fid = in.readInt();
        this.trades = in.createTypedArrayList(TradeOrderBean.CREATOR);
        this.netVal = in.readString();
        this.currentPriceType = in.readString();
    }

    public static final Creator<TradeInfoBean> CREATOR = new Creator<TradeInfoBean>() {
        @Override
        public TradeInfoBean createFromParcel(Parcel source) {
            return new TradeInfoBean(source);
        }

        @Override
        public TradeInfoBean[] newArray(int size) {
            return new TradeInfoBean[size];
        }
    };
}
