package huolongluo.byw.byw.bean;

import java.io.Serializable;

/**
 * Created by LS on 2018/7/9.
 */

public class SelectCoinBean implements Serializable{

        private String selfselection;
        private String id;
        private String cnyName;
        private String coinName;
        private String cnName;
        private String exchangeCode;
        private String currencySymbol;
        private String LatestDealPrice;
        private String OneDayLowest;
        private String OneDayHighest;
        private String OneDayTotal;
        private String priceRaiseRate;
        private String logo;

    public SelectCoinBean() {

    }

    public String getSelfselection() {
        return selfselection;
    }

    public void setSelfselection(String selfselection) {
        this.selfselection = selfselection;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getLatestDealPrice() {
        return LatestDealPrice;
    }

    public void setLatestDealPrice(String latestDealPrice) {
        LatestDealPrice = latestDealPrice;
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

    public String getOneDayTotal() {
        return OneDayTotal;
    }

    public void setOneDayTotal(String oneDayTotal) {
        OneDayTotal = oneDayTotal;
    }

    public String getPriceRaiseRate() {
        return priceRaiseRate;
    }

    public void setPriceRaiseRate(String priceRaiseRate) {
        this.priceRaiseRate = priceRaiseRate;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @Override
    public String toString() {
        return "SelectCoinBean{" +
                "selfselection='" + selfselection + '\'' +
                ", id='" + id + '\'' +
                ", cnyName='" + cnyName + '\'' +
                ", coinName='" + coinName + '\'' +
                ", cnName='" + cnName + '\'' +
                ", exchangeCode='" + exchangeCode + '\'' +
                ", currencySymbol='" + currencySymbol + '\'' +
                ", LatestDealPrice='" + LatestDealPrice + '\'' +
                ", OneDayLowest='" + OneDayLowest + '\'' +
                ", OneDayHighest='" + OneDayHighest + '\'' +
                ", OneDayTotal='" + OneDayTotal + '\'' +
                ", priceRaiseRate='" + priceRaiseRate + '\'' +
                ", logo='" + logo + '\'' +
                '}';
    }
}
