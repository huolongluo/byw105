package huolongluo.byw.byw.ui.oneClickBuy.bean;

import java.util.Map;

public class AdvertisementEntity {

    /**
     * result : true
     * code : 0
     * data : {"totalAmount":100,"adId":132,"price_s":"1.000","price":1,"num":100,"advertisement":null,"coinName":"CNYT","totalAmount_s":"100.00","num_s":"100.00"}
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
         * totalAmount : 100
         * adId : 132
         * price_s : 1.000
         * price : 1
         * num : 100
         * advertisement : null
         * coinName : CNYT
         * totalAmount_s : 100.00
         * num_s : 100.00
         */
        private Map<String, String> orderInfoMap;

        public Map<String, String> getOrderInfoMap() {
            return orderInfoMap;
        }

        public void setOrderInfoMap(Map<String, String> orderInfoMap) {
            this.orderInfoMap = orderInfoMap;
        }

        private String totalAmount;
        private int adId;
        private String price_s;
        private String price;
        private String num;
        private Object advertisement;
        private String coinName;
        private String totalAmount_s;
        private String num_s;
        private String url;//用于区分阿里人脸验证来源

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(String totalAmount) {
            this.totalAmount = totalAmount;
        }

        public int getAdId() {
            return adId;
        }

        public void setAdId(int adId) {
            this.adId = adId;
        }

        public String getPrice_s() {
            return price_s;
        }

        public void setPrice_s(String price_s) {
            this.price_s = price_s;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public Object getAdvertisement() {
            return advertisement;
        }

        public void setAdvertisement(Object advertisement) {
            this.advertisement = advertisement;
        }

        public String getCoinName() {
            return coinName;
        }

        public void setCoinName(String coinName) {
            this.coinName = coinName;
        }

        public String getTotalAmount_s() {
            return totalAmount_s;
        }

        public void setTotalAmount_s(String totalAmount_s) {
            this.totalAmount_s = totalAmount_s;
        }

        public String getNum_s() {
            return num_s;
        }

        public void setNum_s(String num_s) {
            this.num_s = num_s;
        }
    }
}
