package huolongluo.byw.byw.ui.fragment.bdb.bean;

public class BdbFinancialDetail {
    private String coinId;//币种Id
    private String coinName	;//币种名称
    private String coinUrl;//币种icon
    private String coinStatus;//币种状态
    private String availableVol;//可用金额
    private String convertCny;//折合人民币金额
    private String convertCnyExchange;//折合人民币金额

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getCoinUrl() {
        return coinUrl;
    }

    public void setCoinUrl(String coinUrl) {
        this.coinUrl = coinUrl;
    }

    public String getCoinStatus() {
        return coinStatus;
    }

    public void setCoinStatus(String coinStatus) {
        this.coinStatus = coinStatus;
    }

    public String getAvailableVol() {
        return availableVol;
    }

    public void setAvailableVol(String availableVol) {
        this.availableVol = availableVol;
    }

    public String getConvertCny() {
        return convertCny;
    }

    public void setConvertCny(String convertCny) {
        this.convertCny = convertCny;
    }

    public String getConvertCnyExchange() {
        return convertCnyExchange;
    }

    public void setConvertCnyExchange(String convertCnyExchange) {
        this.convertCnyExchange = convertCnyExchange;
    }
}
