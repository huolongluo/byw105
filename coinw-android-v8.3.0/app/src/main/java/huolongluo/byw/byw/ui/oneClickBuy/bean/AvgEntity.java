package huolongluo.byw.byw.ui.oneClickBuy.bean;

public class AvgEntity {

    /**
     * result : true
     * code : 0
     * data : {"avgPrice":"1.01","coinName":"CNYT"}
     */

    private boolean result;
    private int code;
    private DataBean data;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * avgPrice : 1.01
         * coinName : CNYT
         */

        private String avgPrice;
        private String coinName;

        public String getAvgPrice() {
            return avgPrice;
        }

        public void setAvgPrice(String avgPrice) {
            this.avgPrice = avgPrice;
        }

        public String getCoinName() {
            return coinName;
        }

        public void setCoinName(String coinName) {
            this.coinName = coinName;
        }
    }
}
