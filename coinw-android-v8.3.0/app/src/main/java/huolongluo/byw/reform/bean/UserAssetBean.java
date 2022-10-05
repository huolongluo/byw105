package huolongluo.byw.reform.bean;


/**
 * Created by Administrator on 2019/1/4 0004.
 */
public class UserAssetBean {

    public int code;
    public String dailyAmount;
    public int entrustNumber;
     public boolean result;
     public double totalAsset;
      public String value;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDailyAmount() {
        return dailyAmount;
    }

    public void setDailyAmount(String dailyAmount) {
        this.dailyAmount = dailyAmount;
    }

    public int getEntrustNumber() {
        return entrustNumber;
    }

    public void setEntrustNumber(int entrustNumber) {
        this.entrustNumber = entrustNumber;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public double getTotalAsset() {
        return totalAsset;
    }

    public void setTotalAsset(double totalAsset) {
        this.totalAsset = totalAsset;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "UserAssetBean{" +
                "code=" + code +
                ", dailyAmount='" + dailyAmount + '\'' +
                ", entrustNumber=" + entrustNumber +
                ", result=" + result +
                ", totalAsset=" + totalAsset +
                ", value='" + value + '\'' +
                '}';
    }
}
