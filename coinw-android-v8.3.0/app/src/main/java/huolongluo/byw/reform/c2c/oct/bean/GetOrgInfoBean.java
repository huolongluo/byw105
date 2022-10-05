package huolongluo.byw.reform.c2c.oct.bean;

/**
 * Created by Administrator on 2019/7/23 0023.
 */

public class GetOrgInfoBean {
    /**
     * code : 0
     * data : {"avgDealTime":0.4,"emailAuth":false,"highGradeAuth":true,"isAuthMerchant":false,"nickname":"测试2","phoneAuth":true,"platformRegisterTime":"2019-07-10","realNameAuth":true,"thirtyDaysOrder":4,"totalOrder":4,"totalOrderCompPercentage":80}
     * result : true
     * value : 操作成功
     */

    private int code;
    private DataBean data;
    private boolean result;
    private String value;

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

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static class DataBean {
        /**
         * avgDealTime : 0.4
         * emailAuth : false
         * highGradeAuth : true
         * isAuthMerchant : false
         * nickname : 测试2
         * phoneAuth : true
         * platformRegisterTime : 2019-07-10
         * realNameAuth : true
         * thirtyDaysOrder : 4
         * totalOrder : 4
         * totalOrderCompPercentage : 80
         */

        private double avgDealTime;
        private boolean emailAuth;
        private boolean highGradeAuth;
        private boolean isAuthMerchant;
        private String nickname;
        private boolean phoneAuth;
        private String platformRegisterTime;
        private boolean realNameAuth;
        private int thirtyDaysOrder;
        private int totalOrder;
        private double totalOrderCompPercentage;
        private int code;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public double getAvgDealTime() {
            return avgDealTime;
        }

        public void setAvgDealTime(double avgDealTime) {
            this.avgDealTime = avgDealTime;
        }

        public boolean isEmailAuth() {
            return emailAuth;
        }

        public void setEmailAuth(boolean emailAuth) {
            this.emailAuth = emailAuth;
        }

        public boolean isHighGradeAuth() {
            return highGradeAuth;
        }

        public void setHighGradeAuth(boolean highGradeAuth) {
            this.highGradeAuth = highGradeAuth;
        }

        public boolean isIsAuthMerchant() {
            return isAuthMerchant;
        }

        public void setIsAuthMerchant(boolean isAuthMerchant) {
            this.isAuthMerchant = isAuthMerchant;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public boolean isPhoneAuth() {
            return phoneAuth;
        }

        public void setPhoneAuth(boolean phoneAuth) {
            this.phoneAuth = phoneAuth;
        }

        public String getPlatformRegisterTime() {
            return platformRegisterTime;
        }

        public void setPlatformRegisterTime(String platformRegisterTime) {
            this.platformRegisterTime = platformRegisterTime;
        }

        public boolean isRealNameAuth() {
            return realNameAuth;
        }

        public void setRealNameAuth(boolean realNameAuth) {
            this.realNameAuth = realNameAuth;
        }

        public int getThirtyDaysOrder() {
            return thirtyDaysOrder;
        }

        public void setThirtyDaysOrder(int thirtyDaysOrder) {
            this.thirtyDaysOrder = thirtyDaysOrder;
        }

        public int getTotalOrder() {
            return totalOrder;
        }

        public void setTotalOrder(int totalOrder) {
            this.totalOrder = totalOrder;
        }

        public double getTotalOrderCompPercentage() {
            return totalOrderCompPercentage;
        }

        public void setTotalOrderCompPercentage(double totalOrderCompPercentage) {
            this.totalOrderCompPercentage = totalOrderCompPercentage;
        }
    }
}
