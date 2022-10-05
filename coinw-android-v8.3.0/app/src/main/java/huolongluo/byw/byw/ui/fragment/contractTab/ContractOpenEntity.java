package huolongluo.byw.byw.ui.fragment.contractTab;

public class ContractOpenEntity {


    /**
     * result : true
     * code : 0
     * value : 操作成功
     * data : {"accessKey":"20112ee9-3433-4229-9903-cd9cec24faf3","accountId":2671357811,"expiredTs":"1583486330000000","status":1,"token":"94fa5f7a55be511b6826ef6dae0e952a"}
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
         * accessKey : 20112ee9-3433-4229-9903-cd9cec24faf3
         * accountId : 2671357811
         * expiredTs : 1583486330000000
         * status : 1
         * token : 94fa5f7a55be511b6826ef6dae0e952a
         */

        private String accessKey;
        private long accountId;
        private String expiredTs;
        private int status;
        private String token;

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public long getAccountId() {
            return accountId;
        }

        public void setAccountId(long accountId) {
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
