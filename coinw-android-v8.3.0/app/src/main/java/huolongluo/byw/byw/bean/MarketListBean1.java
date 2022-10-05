package huolongluo.byw.byw.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LS on 2018/7/29.
 */
public class MarketListBean1 implements Serializable
{
    private int id;
    private String cnyName;
    private int vir_id1;
    private boolean vir_id1_isWithDraw;
    private int vir_id2;
    private boolean vir_id2_isWithDraw;
    private String coinName;
    private String cnName;
    private String currency;
    private String currencySymbol;
    private String title;
    private boolean hasKline;
    private int count1;
    private int count2;
    private boolean isLimittrade;
    private String upPrice;
    private String downPrice;
    private double LatestDealPrice;
    private double SellOnePrice;
    private double BuyOnePrice;
    private double OneDayLowest;
    private double OneDayHighest;
    private double OneDayTotal;
    private double selfselection;
    private double priceRaiseRate;
    private String logo;
    private List<Double> day7;

    public double getSelfselection() {
        return selfselection;
    }

    public void setSelfselection(double selfselection) {
        this.selfselection = selfselection;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getCnyName()
    {
        return cnyName;
    }

    public void setCnyName(String cnyName)
    {
        this.cnyName = cnyName;
    }

    public int getVir_id1()
    {
        return vir_id1;
    }

    public void setVir_id1(int vir_id1)
    {
        this.vir_id1 = vir_id1;
    }

    public boolean isVir_id1_isWithDraw()
    {
        return vir_id1_isWithDraw;
    }

    public void setVir_id1_isWithDraw(boolean vir_id1_isWithDraw)
    {
        this.vir_id1_isWithDraw = vir_id1_isWithDraw;
    }

    public int getVir_id2()
    {
        return vir_id2;
    }

    public void setVir_id2(int vir_id2)
    {
        this.vir_id2 = vir_id2;
    }

    public boolean isVir_id2_isWithDraw()
    {
        return vir_id2_isWithDraw;
    }

    public void setVir_id2_isWithDraw(boolean vir_id2_isWithDraw)
    {
        this.vir_id2_isWithDraw = vir_id2_isWithDraw;
    }

    public String getCoinName()
    {
        return coinName;
    }

    public void setCoinName(String coinName)
    {
        this.coinName = coinName;
    }

    public String getCnName()
    {
        return cnName;
    }

    public void setCnName(String cnName)
    {
        this.cnName = cnName;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getCurrencySymbol()
    {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol)
    {
        this.currencySymbol = currencySymbol;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public boolean isHasKline()
    {
        return hasKline;
    }

    public void setHasKline(boolean hasKline)
    {
        this.hasKline = hasKline;
    }

    public int getCount1()
    {
        return count1;
    }

    public void setCount1(int count1)
    {
        this.count1 = count1;
    }

    public int getCount2()
    {
        return count2;
    }

    public void setCount2(int count2)
    {
        this.count2 = count2;
    }

    public boolean isIsLimittrade()
    {
        return isLimittrade;
    }

    public void setIsLimittrade(boolean isLimittrade)
    {
        this.isLimittrade = isLimittrade;
    }

    public String getUpPrice()
    {
        return upPrice;
    }

    public void setUpPrice(String upPrice)
    {
        this.upPrice = upPrice;
    }

    public String getDownPrice()
    {
        return downPrice;
    }

    public void setDownPrice(String downPrice)
    {
        this.downPrice = downPrice;
    }

    public double getLatestDealPrice()
    {
        return LatestDealPrice;
    }

    public void setLatestDealPrice(double LatestDealPrice)
    {
        this.LatestDealPrice = LatestDealPrice;
    }

    public double getSellOnePrice()
    {
        return SellOnePrice;
    }

    public void setSellOnePrice(double SellOnePrice)
    {
        this.SellOnePrice = SellOnePrice;
    }

    public double getBuyOnePrice()
    {
        return BuyOnePrice;
    }

    public void setBuyOnePrice(double BuyOnePrice)
    {
        this.BuyOnePrice = BuyOnePrice;
    }

    public double getOneDayLowest()
    {
        return OneDayLowest;
    }

    public void setOneDayLowest(double OneDayLowest)
    {
        this.OneDayLowest = OneDayLowest;
    }

    public double getOneDayHighest()
    {
        return OneDayHighest;
    }

    public void setOneDayHighest(double OneDayHighest)
    {
        this.OneDayHighest = OneDayHighest;
    }

    public double getOneDayTotal()
    {
        return OneDayTotal;
    }

    public void setOneDayTotal(double OneDayTotal)
    {
        this.OneDayTotal = OneDayTotal;
    }

    public double getPriceRaiseRate()
    {
        return priceRaiseRate;
    }

    public void setPriceRaiseRate(double priceRaiseRate)
    {
        this.priceRaiseRate = priceRaiseRate;
    }

    public String getLogo()
    {
        return logo;
    }

    public void setLogo(String logo)
    {
        this.logo = logo;
    }

    public List<Double> getDay7()
    {
        return day7;
    }

    public void setDay7(List<Double> day7)
    {
        this.day7 = day7;
    }

    @Override
    public String toString() {
        return "MarketListBean1{" +
                "id=" + id +
                ", cnyName='" + cnyName + '\'' +
                ", vir_id1=" + vir_id1 +
                ", vir_id1_isWithDraw=" + vir_id1_isWithDraw +
                ", vir_id2=" + vir_id2 +
                ", vir_id2_isWithDraw=" + vir_id2_isWithDraw +
                ", coinName='" + coinName + '\'' +
                ", cnName='" + cnName + '\'' +
                ", currency='" + currency + '\'' +
                ", currencySymbol='" + currencySymbol + '\'' +
                ", title='" + title + '\'' +
                ", hasKline=" + hasKline +
                ", count1=" + count1 +
                ", count2=" + count2 +
                ", isLimittrade=" + isLimittrade +
                ", upPrice='" + upPrice + '\'' +
                ", downPrice='" + downPrice + '\'' +
                ", LatestDealPrice=" + LatestDealPrice +
                ", SellOnePrice=" + SellOnePrice +
                ", BuyOnePrice=" + BuyOnePrice +
                ", OneDayLowest=" + OneDayLowest +
                ", OneDayHighest=" + OneDayHighest +
                ", OneDayTotal=" + OneDayTotal +
                ", selfselection=" + selfselection +
                ", priceRaiseRate=" + priceRaiseRate +
                ", logo='" + logo + '\'' +
                ", day7=" + day7 +
                '}';
    }
}
