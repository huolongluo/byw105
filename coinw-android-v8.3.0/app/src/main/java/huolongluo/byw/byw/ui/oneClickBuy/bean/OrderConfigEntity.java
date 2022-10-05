package huolongluo.byw.byw.ui.oneClickBuy.bean;

public class OrderConfigEntity {

    /**
     * result : true
     * code : 0
     * data : {"openStatus":true,"sellMax":"1500","sellMin":"100","buyMax":"1500","buyMin":"100"}
     * value : 操作成功
     */

    private boolean result;
    private int code;
    private DataBean data;
    private String value;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static class DataBean {
        /**
         * openStatus : true
         * sellMax : 1500
         * sellMin : 100
         * buyMax : 1500
         * buyMin : 100
         */

        private boolean openStatus;
        private String sellMax;
        private String sellMin;
        private String buyMax;
        private String buyMin;

        public boolean isOpenStatus() {
            return openStatus;
        }

        public void setOpenStatus(boolean openStatus) {
            this.openStatus = openStatus;
        }

        public String getSellMax() {
            return sellMax;
        }

        public void setSellMax(String sellMax) {
            this.sellMax = sellMax;
        }

        public String getSellMin() {
            return sellMin;
        }

        public void setSellMin(String sellMin) {
            this.sellMin = sellMin;
        }

        public String getBuyMax() {
            return buyMax;
        }

        public void setBuyMax(String buyMax) {
            this.buyMax = buyMax;
        }

        public String getBuyMin() {
            return buyMin;
        }

        public void setBuyMin(String buyMin) {
            this.buyMin = buyMin;
        }
    }
}
