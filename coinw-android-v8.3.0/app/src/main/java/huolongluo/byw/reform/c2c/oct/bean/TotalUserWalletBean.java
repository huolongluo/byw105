package huolongluo.byw.reform.c2c.oct.bean;

/**
 * Created by dell on 2019/6/19.
 */

public class TotalUserWalletBean {
    /**
     * code : 0
     * result : true
     * totalAsset : 24603159.704994064
     * totalOtcAsset : 2314
     * totalVirtualAsset : 24600845.704994064
     * unit : CNYT
     * value : 操作成功！
     */

    private int code;
    private boolean result;
    private String totalAsset;
    private String totalOtcAsset;
    private String totalVirtualAsset;
    private String unit;
    private String value;
    private String totalAssetExchange;

    public String getTotalAssetExchange() {
        return totalAssetExchange;
    }

    public void setTotalAssetExchange(String totalAssetExchange) {
        this.totalAssetExchange = totalAssetExchange;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getTotalAsset() {
        return totalAsset;
    }

    public void setTotalAsset(String totalAsset) {
        this.totalAsset = totalAsset;
    }

    public String getTotalOtcAsset() {
        return totalOtcAsset;
    }

    public void setTotalOtcAsset(String totalOtcAsset) {
        this.totalOtcAsset = totalOtcAsset;
    }

    public String getTotalVirtualAsset() {
        return totalVirtualAsset;
    }

    public void setTotalVirtualAsset(String totalVirtualAsset) {
        this.totalVirtualAsset = totalVirtualAsset;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
