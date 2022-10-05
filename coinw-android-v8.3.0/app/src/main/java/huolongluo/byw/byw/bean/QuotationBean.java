package huolongluo.byw.byw.bean;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 火龙裸 on 2017/12/27.
 */
public class QuotationBean implements Parcelable {
    /**
     * LatestDealPrice : 0.096
     * SellOnePrice : 0.096
     * BuyOnePrice : 0.095
     * OneDayLowest : 0.092
     * OneDayHighest : 0.097
     * OneDayTotal : 7464642.821
     * priceRaiseRate : 3.22
     */
    private Double LatestDealPrice;
    private Double SellOnePrice;
    private Double BuyOnePrice;
    private Double OneDayLowest;
    private Double OneDayHighest;
    private Double OneDayTotal;
    private String priceRaiseRate;
    private Double CurrencyPrice;//买币价钱
    private Double transferPrice;
    private String transferSymbol;
    private String LatestDealPriceExchange;

    public Double getLatestDealPrice() {
        return LatestDealPrice;
    }

    public void setLatestDealPrice(Double LatestDealPrice) {
        this.LatestDealPrice = LatestDealPrice;
    }

    public Double getSellOnePrice() {
        return SellOnePrice;
    }

    public void setSellOnePrice(Double SellOnePrice) {
        this.SellOnePrice = SellOnePrice;
    }

    public Double getBuyOnePrice() {
        return BuyOnePrice;
    }

    public void setBuyOnePrice(Double BuyOnePrice) {
        this.BuyOnePrice = BuyOnePrice;
    }

    public String getLatestDealPriceExchange() {
        return LatestDealPriceExchange;
    }

    public void setLatestDealPriceExchange(String latestDealPriceExchange) {
        LatestDealPriceExchange = latestDealPriceExchange;
    }

    public Double getOneDayLowest() {
        return OneDayLowest;
    }

    public void setOneDayLowest(Double OneDayLowest) {
        this.OneDayLowest = OneDayLowest;
    }

    public Double getOneDayHighest() {
        return OneDayHighest;
    }

    public void setOneDayHighest(Double OneDayHighest) {
        this.OneDayHighest = OneDayHighest;
    }

    public Double getOneDayTotal() {
        return OneDayTotal;
    }

    public void setOneDayTotal(Double OneDayTotal) {
        this.OneDayTotal = OneDayTotal;
    }

    public Double getCurrencyPrice() {
        return CurrencyPrice;
    }

    public void setCurrencyPrice(Double currencyPrice) {
        CurrencyPrice = currencyPrice;
    }

    @Override
    public String toString() {
        return "QuotationBean{" + "LatestDealPrice=" + LatestDealPrice + ", SellOnePrice=" + SellOnePrice + ", BuyOnePrice=" + BuyOnePrice + ", OneDayLowest=" + OneDayLowest + ", OneDayHighest=" + OneDayHighest + ", OneDayTotal=" + OneDayTotal + ", priceRaiseRate='" + priceRaiseRate + '\'' + ", CurrencyPrice=" + CurrencyPrice + '}';
    }

    public String getPriceRaiseRate() {
        return priceRaiseRate;
    }

    public void setPriceRaiseRate(String priceRaiseRate) {
        this.priceRaiseRate = priceRaiseRate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.LatestDealPrice);
        dest.writeValue(this.SellOnePrice);
        dest.writeValue(this.BuyOnePrice);
        dest.writeValue(this.OneDayLowest);
        dest.writeValue(this.OneDayHighest);
        dest.writeValue(this.OneDayTotal);
        dest.writeString(this.priceRaiseRate);
        dest.writeValue(this.CurrencyPrice);
        dest.writeValue(this.transferPrice);
        dest.writeString(this.transferSymbol);
        dest.writeString(this.LatestDealPriceExchange);
    }

    public QuotationBean() {
    }

    protected QuotationBean(Parcel in) {
        this.LatestDealPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.SellOnePrice = (Double) in.readValue(Double.class.getClassLoader());
        this.BuyOnePrice = (Double) in.readValue(Double.class.getClassLoader());
        this.OneDayLowest = (Double) in.readValue(Double.class.getClassLoader());
        this.OneDayHighest = (Double) in.readValue(Double.class.getClassLoader());
        this.OneDayTotal = (Double) in.readValue(Double.class.getClassLoader());
        this.priceRaiseRate = in.readString();
        this.CurrencyPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.transferPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.transferSymbol = in.readString();
        this.LatestDealPriceExchange = in.readString();
    }

    public static final Creator<QuotationBean> CREATOR = new Creator<QuotationBean>() {
        @Override
        public QuotationBean createFromParcel(Parcel source) {
            return new QuotationBean(source);
        }

        @Override
        public QuotationBean[] newArray(int size) {
            return new QuotationBean[size];
        }
    };
}
