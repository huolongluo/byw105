package huolongluo.byw.byw.ui.fragment.contractTab;

public class ContractUserInfoEntity {

    /**
     * result : true
     * code : 0
     * value : 操作成功
     * data : {"accessKey":"fcf75836-45e4-47f0-aceb-bb42947235f8","accountId":2673940716,"expiredTs":"1583640619000000","status":1,"token":"6079c8ea0d2a75504cc51f2865e9923d"}
     */

    private boolean result;
    private int code;
    private String value;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * accessKey : fcf75836-45e4-47f0-aceb-bb42947235f8
         * accountId : 2673940716
         * expiredTs : 1583640619000000
         * status : 1
         * token : 6079c8ea0d2a75504cc51f2865e9923d
         */

        private String accessKey;
        private String accountId;
        private String expiredTs;
        private int status; //0 是未开通 1是开通 2是冻结
        private String token;

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public String getExpiredTs() {
            return expiredTs;
        }

        public void setExpiredTs(String expiredTs) {
            this.expiredTs = expiredTs;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
