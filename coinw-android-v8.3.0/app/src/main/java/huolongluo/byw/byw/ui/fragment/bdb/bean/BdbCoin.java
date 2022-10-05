package huolongluo.byw.byw.ui.fragment.bdb.bean;

public class BdbCoin {
    private String coinId;//币种id
    private String coinName;//币种名称
    private String availableVol;//coinw余额
    private String coinStatus;//状态0不可用 1可用

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

    public String getAvailableVol() {
        return availableVol;
    }

    public void setAvailableVol(String availableVol) {
        this.availableVol = availableVol;
    }

    public String getCoinStatus() {
        return coinStatus;
    }

    public void setCoinStatus(String coinStatus) {
        this.coinStatus = coinStatus;
    }
}
