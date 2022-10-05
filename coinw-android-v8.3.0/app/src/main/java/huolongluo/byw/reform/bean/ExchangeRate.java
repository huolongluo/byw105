package huolongluo.byw.reform.bean;
//汇率
public class ExchangeRate {
    private String srcType;//源货币类型
    private String dstType;//目标货币类型
    private String rate;//汇率

    public String getSrcType() {
        return srcType;
    }

    public void setSrcType(String srcType) {
        this.srcType = srcType;
    }

    public String getDstType() {
        return dstType;
    }

    public void setDstType(String dstType) {
        this.dstType = dstType;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
