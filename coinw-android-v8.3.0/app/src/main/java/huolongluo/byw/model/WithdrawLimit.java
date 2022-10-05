package huolongluo.byw.model;
//提现的额度对象
public class WithdrawLimit {
    private String total;//总额度
    private String quota;//剩余额度
    private String kycLevel;//C3   C2  none
    private String coinName;//限额币种名称
    private String maxQuota;//最大额度

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    public String getKycLevel() {
        return kycLevel;
    }

    public void setKycLevel(String kycLevel) {
        this.kycLevel = kycLevel;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getMaxQuota() {
        return maxQuota;
    }

    public void setMaxQuota(String maxQuota) {
        this.maxQuota = maxQuota;
    }
}
