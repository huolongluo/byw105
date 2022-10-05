package huolongluo.byw.byw.bean;


import java.io.Serializable;
import java.util.List;

/**
 * Created by 火龙裸 on 2017/12/26.
 */
public class MarketListBean implements Serializable {
    private static final long serialVersionUID = 536871009;
    private int fid = -1;//类别
    private int trademId;//自选特有
    private int uId;    //自选特有
    private int id;
    private String cnyName;
    private int vir_id1;
    private boolean vir_id1_isWithDraw;
    private int vir_id2;
    private boolean vir_id2_isWithDraw;
    private String coinName = "";
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
    private String LatestDealPrice;
    private double SellOnePrice;
    private double BuyOnePrice;
    private double OneDayLowest;
    private double OneDayHighest;
    private Double OneDayTotal;
    private int selfselection;
    private double priceRaiseRate;
    private int innovationZone;//是否是创新区
    private String logo;
    private List<Double> day7;
    private String legalMoney;//交易额
    private int mycurrency;//是否是我的币种
    private String currencyPrize;
    private String currencyPrizeExchange;
    private String LatestDealPriceExchange;
    private int type;
    private String netVal = "";
    private String fPartitionIds;//分区id  "1,2,3"
    private boolean selective;//是否是研选

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private double lastSocketPrice;//上一次的价格

    /**
     * @param id
     * @param cnyName
     * @param coinName
     */
    public MarketListBean(int id, String cnyName, String coinName) {
        this.id = id;
        this.cnyName = cnyName;
        this.coinName = coinName;
    }

    public MarketListBean() {
    }

    public boolean isSelective() {
        return selective;
    }

    public void setSelective(boolean selective) {
        this.selective = selective;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public double getLastSocketPrice() {
        return lastSocketPrice;
    }

    public void setLastSocketPrice(double lastSocketPrice) {
        this.lastSocketPrice = lastSocketPrice;
    }

    public String getfPartitionIds() {
        return fPartitionIds;
    }

    public void setfPartitionIds(String fPartitionIds) {
        this.fPartitionIds = fPartitionIds;
    }

    public int getSelfselection() {
        return selfselection;
    }

    public void setSelfselection(int selfselection) {
        this.selfselection = selfselection;
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

    public String getCurrency() {
        return currency;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isHasKline() {
        return hasKline;
    }

    public void setHasKline(boolean hasKline) {
        this.hasKline = hasKline;
    }

    public int getCount1() {
        return count1;
    }

    public void setCount1(int count1) {
        this.count1 = count1;
    }

    public int getCount2() {
        return count2;
    }

    public void setCount2(int count2) {
        this.count2 = count2;
    }

    public boolean isIsLimittrade() {
        return isLimittrade;
    }

    public void setIsLimittrade(boolean isLimittrade) {
        this.isLimittrade = isLimittrade;
    }

    public String getUpPrice() {
        return upPrice;
    }

    public void setUpPrice(String upPrice) {
        this.upPrice = upPrice;
    }

    public String getDownPrice() {
        return downPrice;
    }

    public void setDownPrice(String downPrice) {
        this.downPrice = downPrice;
    }

    public String getNetVal() {
        return netVal;
    }

    public void setNetVal(String netVal) {
        this.netVal = netVal;
    }

    public String getLatestDealPrice() {
        return LatestDealPrice;
    }

    public void setLatestDealPrice(String LatestDealPrice) {
        this.LatestDealPrice = LatestDealPrice;
    }

    public double getSellOnePrice() {
        return SellOnePrice;
    }

    public void setSellOnePrice(double SellOnePrice) {
        this.SellOnePrice = SellOnePrice;
    }

    public double getBuyOnePrice() {
        return BuyOnePrice;
    }

    public void setBuyOnePrice(double BuyOnePrice) {
        this.BuyOnePrice = BuyOnePrice;
    }

    public double getOneDayLowest() {
        return OneDayLowest;
    }

    public void setOneDayLowest(double OneDayLowest) {
        this.OneDayLowest = OneDayLowest;
    }

    public double getOneDayHighest() {
        return OneDayHighest;
    }

    public void setOneDayHighest(double OneDayHighest) {
        this.OneDayHighest = OneDayHighest;
    }

    public Double getOneDayTotal() {
        return OneDayTotal;
    }

    public void setOneDayTotal(Double OneDayTotal) {
        this.OneDayTotal = OneDayTotal;
    }

    public double getPriceRaiseRate() {
        return priceRaiseRate;
    }

    public void setPriceRaiseRate(double priceRaiseRate) {
        this.priceRaiseRate = priceRaiseRate;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<Double> getDay7() {
        return day7;
    }

    public void setDay7(List<Double> day7) {
        this.day7 = day7;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public boolean isLimittrade() {
        return isLimittrade;
    }

    public void setLimittrade(boolean limittrade) {
        isLimittrade = limittrade;
    }

    public int getInnovationZone() {
        return innovationZone;
    }

    public void setInnovationZone(int innovationZone) {
        this.innovationZone = innovationZone;
    }

    public int getMycurrency() {
        return mycurrency;
    }

    public void setMycurrency(int mycurrency) {
        this.mycurrency = mycurrency;
    }

    public String getLegalMoney() {
        return legalMoney;
    }

    public void setLegalMoney(String legalMoney) {
        this.legalMoney = legalMoney;
    }

    public int getTrademId() {
        return trademId;
    }

    public void setTrademId(int trademId) {
        this.trademId = trademId;
    }

    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }

    public String getCurrencyPrize() {
        return currencyPrize;
    }

    public void setCurrencyPrize(String currencyPrize) {
        this.currencyPrize = currencyPrize;
    }

    @Override
    public String toString() {
        return "MarketListBean{" + "fid=" + fid + ", trademId=" + trademId + ", uId=" + uId + ", id=" + id + ", cnyName='" + cnyName + '\'' + ", vir_id1=" + vir_id1 + ", vir_id1_isWithDraw=" + vir_id1_isWithDraw + ", vir_id2=" + vir_id2 + ", vir_id2_isWithDraw=" + vir_id2_isWithDraw + ", coinName='" + coinName + '\'' + ", cnName='" + cnName + '\'' + ", currency='" + currency + '\'' + ", currencySymbol='" + currencySymbol + '\'' + ", title='" + title + '\'' + ", hasKline=" + hasKline + ", count1=" + count1 + ", count2=" + count2 + ", isLimittrade=" + isLimittrade + ", upPrice='" + upPrice + '\'' + ", downPrice='" + downPrice + '\'' + ", LatestDealPrice=" + LatestDealPrice + ", SellOnePrice=" + SellOnePrice + ", BuyOnePrice=" + BuyOnePrice + ", OneDayLowest=" + OneDayLowest + ", OneDayHighest=" + OneDayHighest + ", OneDayTotal=" + OneDayTotal + ", selfselection=" + selfselection + ", priceRaiseRate=" + priceRaiseRate + ", innovationZone=" + innovationZone + ", logo='" + logo + '\'' + ", day7=" + day7 + ", legalMoney='" + legalMoney + '\'' + ", mycurrency=" + mycurrency + ", currencyPrize='" + currencyPrize + '\'' + ", lastSocketPrice=" + lastSocketPrice + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketListBean bean = (MarketListBean) o;
        return id == bean.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
