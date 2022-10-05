package huolongluo.byw.byw.bean;


import java.io.Serializable;
import java.util.List;

/**
 * Created by 火龙裸 on 2017/12/27.
 */
public class BuyInBean implements Serializable
{
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
     *
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
    private int fcount1;
    private int fcount2;
    private String coinName;
    private String cnName;
    private String currency;
    private String currencySymbol;
    private QuotationBean quotation;
    private CoinInfoBean coinInfo;
    private AssetBean asset;
    private List<SellDepthBean> sellDepth;
    private List<BuyDepthBean> buyDepth;
    private String value;

    private int selfselection;
    private int fid;

    public int getFcount1() {
        return fcount1;
    }

    public void setFcount1(int fcount1) {
        this.fcount1 = fcount1;
    }

    public int getFcount2() {
        return fcount2;
    }

    public void setFcount2(int fcount2) {
        this.fcount2 = fcount2;
    }

    public boolean isResult()
    {
        return result;
    }

    public void setResult(boolean result)
    {
        this.result = result;
    }

    public String getLastUpdateTime()
    {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime)
    {
        this.lastUpdateTime = lastUpdateTime;
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

    public QuotationBean getQuotation()
    {
        return quotation;
    }

    public void setQuotation(QuotationBean quotation)
    {
        this.quotation = quotation;
    }

    public CoinInfoBean getCoinInfo()
    {
        return coinInfo;
    }

    public void setCoinInfo(CoinInfoBean coinInfo)
    {
        this.coinInfo = coinInfo;
    }

    public AssetBean getAsset()
    {
        return asset;
    }

    public void setAsset(AssetBean asset)
    {
        this.asset = asset;
    }

    public List<SellDepthBean> getSellDepth()
    {
        return sellDepth;
    }

    public void setSellDepth(List<SellDepthBean> sellDepth)
    {
        this.sellDepth = sellDepth;
    }

    public List<BuyDepthBean> getBuyDepth()
    {
        return buyDepth;
    }

    public void setBuyDepth(List<BuyDepthBean> buyDepth)
    {
        this.buyDepth = buyDepth;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public int getSelfselection() {
        return selfselection;
    }

    public void setSelfselection(int selfselection) {
        this.selfselection = selfselection;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    @Override
    public String toString() {
        return "BuyInBean{" +
                "result=" + result +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                ", cnyName='" + cnyName + '\'' +
                ", vir_id1=" + vir_id1 +
                ", vir_id1_isWithDraw=" + vir_id1_isWithDraw +
                ", vir_id2=" + vir_id2 +
                ", vir_id2_isWithDraw=" + vir_id2_isWithDraw +
                ", fcount1=" + fcount1 +
                ", fcount2=" + fcount2 +
                ", coinName='" + coinName + '\'' +
                ", cnName='" + cnName + '\'' +
                ", currency='" + currency + '\'' +
                ", currencySymbol='" + currencySymbol + '\'' +
                ", quotation=" + quotation +
                ", coinInfo=" + coinInfo +
                ", asset=" + asset +
                ", sellDepth=" + sellDepth +
                ", buyDepth=" + buyDepth +
                ", value='" + value + '\'' +
                ", selfselection=" + selfselection +
                ", fid=" + fid +
                '}';
    }
}
