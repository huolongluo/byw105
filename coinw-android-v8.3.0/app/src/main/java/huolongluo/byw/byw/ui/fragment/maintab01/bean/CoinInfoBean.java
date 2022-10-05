package huolongluo.byw.byw.ui.fragment.maintab01.bean;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hy on 2018/10/18 0018.
 */
public class CoinInfoBean implements Parcelable {
    public String legalMoneyCny;//人民币价钱
    public String OneDayTotal;
    public String OneDayLowest;
    public String OneDayHighest;
    public String LatestDealPrice;
    public String coinName;
    public String cnyName;
    public String cnName;
    public String currencyPrize;
    public String currencySymbol;
    public double legalMoney;
    public String priceRaiseRate;
    public String OneDayTotalK;
    public String logo;
    public int id;
    public  String currencyPrizeExchange;
    public  String LatestDealPriceExchange;
    public  String legalMoneyCnyExchange;
    public boolean showTag;



    public String getLatestDealPrize() {
        return LatestDealPrice;
    }

    public void setLatestDealPrize(String latestDealPrize) {
        LatestDealPrice = latestDealPrize;
    }

    public String getOneDayTotal() {
        return OneDayTotal;
    }

    public void setOneDayTotal(String oneDayTotal) {
        OneDayTotal = oneDayTotal;
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

    public void setCurrencyName(String cnyName) {
        this.cnyName = cnyName;
    }

    public String getCurrencyPrize() {
        return currencyPrize;
    }

    public void setCurrencyPrize(String currencyPrize) {
        this.currencyPrize = currencyPrize;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public double getLegalMoney() {
        return legalMoney;
    }

    public void setLegalMoney(double legalMoney) {
        this.legalMoney = legalMoney;
    }

    public String getPriceRaiseRate() {
        return priceRaiseRate;
    }

    public void setPriceRaiseRate(String priceRaiseRate) {
        this.priceRaiseRate = priceRaiseRate;
    }

    public String getOneDayLowest() {
        return OneDayLowest;
    }

    public void setOneDayLowest(String oneDayLowest) {
        OneDayLowest = oneDayLowest;
    }

    public String getOneDayHighest() {
        return OneDayHighest;
    }

    public void setOneDayHighest(String oneDayHighest) {
        OneDayHighest = oneDayHighest;
    }

    public String getLatestDealPrice() {
        return LatestDealPrice;
    }

    public void setLatestDealPrice(String latestDealPrice) {
        LatestDealPrice = latestDealPrice;
    }

    public void setCnyName(String cnyName) {
        this.cnyName = cnyName;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getOneDayTotalK() {
        return OneDayTotalK;
    }

    public void setOneDayTotalK(String oneDayTotalK) {
        OneDayTotalK = oneDayTotalK;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getShowTag() {
        return showTag;
    }

    public void setShowTag(boolean showTag) {
        this.showTag = showTag;
    }

    @Override
    public String toString() {
        return "CoinInfoBean{" + "LatestDealPrize='" + LatestDealPrice + '\'' + ", OneDayTotal='" + OneDayTotal + '\'' + ", OneDayLowest='" + OneDayLowest + '\'' + ", OneDayHighest='" + OneDayHighest + '\'' + ", LatestDealPrice='" + LatestDealPrice + '\'' + ", coinName='" + coinName + '\'' + ", cnyName='" + cnyName + '\'' + ", cnName='" + cnName + '\'' + ", currencyPrize='" + currencyPrize + '\'' + ", currencySymbol='" + currencySymbol + '\'' + ", legalMoney=" + legalMoney + ", priceRaiseRate='" + priceRaiseRate + '\'' + ", OneDayTotalK='" + OneDayTotalK + '\'' + ", logo='" + logo + '\'' + ", id=" + id + '}';
    }

    public String getLegalMoneyCny() {
        return legalMoneyCny;
    }

    public void setLegalMoneyCny(String legalMoneyCny) {
        this.legalMoneyCny = legalMoneyCny;
    }

    public String getCurrencyPrizeExchange() {
        return currencyPrizeExchange;
    }

    public void setCurrencyPrizeExchange(String currencyPrizeExchange) {
        this.currencyPrizeExchange = currencyPrizeExchange;
    }

    public String getLatestDealPriceExchange() {
        return LatestDealPriceExchange;
    }

    public void setLatestDealPriceExchange(String latestDealPriceExchange) {
        LatestDealPriceExchange = latestDealPriceExchange;
    }

    public String getLegalMoneyCnyExchange() {
        return legalMoneyCnyExchange;
    }

    public void setLegalMoneyCnyExchange(String legalMoneyCnyExchange) {
        this.legalMoneyCnyExchange = legalMoneyCnyExchange;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.legalMoneyCny);
        dest.writeString(this.OneDayTotal);
        dest.writeString(this.OneDayLowest);
        dest.writeString(this.OneDayHighest);
        dest.writeString(this.LatestDealPrice);
        dest.writeString(this.coinName);
        dest.writeString(this.cnyName);
        dest.writeString(this.cnName);
        dest.writeString(this.currencyPrize);
        dest.writeString(this.currencySymbol);
        dest.writeDouble(this.legalMoney);
        dest.writeString(this.priceRaiseRate);
        dest.writeString(this.OneDayTotalK);
        dest.writeString(this.logo);
        dest.writeInt(this.id);
        dest.writeString(this.currencyPrizeExchange);
        dest.writeString(this.LatestDealPriceExchange);
        dest.writeString(this.legalMoneyCnyExchange);
        dest.writeByte(this.showTag ? (byte) 1 : (byte) 0);
    }

    public CoinInfoBean() {
    }

    protected CoinInfoBean(Parcel in) {
        this.legalMoneyCny = in.readString();
        this.OneDayTotal = in.readString();
        this.OneDayLowest = in.readString();
        this.OneDayHighest = in.readString();
        this.LatestDealPrice = in.readString();
        this.coinName = in.readString();
        this.cnyName = in.readString();
        this.cnName = in.readString();
        this.currencyPrize = in.readString();
        this.currencySymbol = in.readString();
        this.legalMoney = in.readDouble();
        this.priceRaiseRate = in.readString();
        this.OneDayTotalK = in.readString();
        this.logo = in.readString();
        this.id = in.readInt();
        this.currencyPrizeExchange = in.readString();
        this.LatestDealPriceExchange = in.readString();
        this.legalMoneyCnyExchange = in.readString();
        this.showTag = in.readByte() != 0;
    }

    public static final Creator<CoinInfoBean> CREATOR = new Creator<CoinInfoBean>() {
        @Override
        public CoinInfoBean createFromParcel(Parcel source) {
            return new CoinInfoBean(source);
        }

        @Override
        public CoinInfoBean[] newArray(int size) {
            return new CoinInfoBean[size];
        }
    };
}
