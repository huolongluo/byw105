package huolongluo.byw.byw.ui.fragment.bdb.bean;

public class BdbCoinBalance {
    private String coinLoanAmount;//贷宝账户余额
    private String coinwAmount;//币币账户余额
    private String coinName;//币种名称

    public String getCoinLoanAmount() {
        return coinLoanAmount;
    }

    public void setCoinLoanAmount(String coinLoanAmount) {
        this.coinLoanAmount = coinLoanAmount;
    }

    public String getCoinwAmount() {
        return coinwAmount;
    }

    public void setCoinwAmount(String coinwAmount) {
        this.coinwAmount = coinwAmount;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }
}
