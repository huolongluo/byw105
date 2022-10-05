package huolongluo.byw.byw.ui.fragment.bdb.bean;

import java.util.List;

public class BdbFinancialInfo {
    private String cnytPrice;//cnyt估值
    private String cnytPriceExchange;//计价方式转换后的cnyt估值
    private List<BdbFinancialDetail> detail;
    private List<BdbFinancialCoinwAccount> coinwAccount;

    public String getCnytPrice() {
        return cnytPrice;
    }

    public void setCnytPrice(String cnytPrice) {
        this.cnytPrice = cnytPrice;
    }

    public String getCnytPriceExchange() {
        return cnytPriceExchange;
    }

    public void setCnytPriceExchange(String cnytPriceExchange) {
        this.cnytPriceExchange = cnytPriceExchange;
    }

    public List<BdbFinancialDetail> getDetail() {
        return detail;
    }

    public void setDetail(List<BdbFinancialDetail> detail) {
        this.detail = detail;
    }

    public List<BdbFinancialCoinwAccount> getCoinwAccount() {
        return coinwAccount;
    }

    public void setCoinwAccount(List<BdbFinancialCoinwAccount> coinwAccount) {
        this.coinwAccount = coinwAccount;
    }
}
