package huolongluo.byw.reform.c2c.oct.bean;

public class AddPayAcountEntity {

    /**
     * result : true
     * code : 0
     * value : 操作成功
     * data : {"account":"4324324324324324324","bankAdress":"223432","bankCity":"毕节市","bankName":"交通银行","bankProvince":"贵州省","id":49,"realName":"云1003","status":1,"type":1,"uid":400078}
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
         * account : 4324324324324324324
         * bankAdress : 223432
         * bankCity : 毕节市
         * bankName : 交通银行
         * bankProvince : 贵州省
         * id : 49
         * realName : 云1003
         * status : 1
         * type : 1
         * uid : 400078
         */

        private String account;
        private String bankAdress;
        private String bankCity;
        private String bankName;
        private String bankProvince;
        private int id;
        private String realName;
        private int status;
        private int type;
        private int uid;

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getBankAdress() {
            return bankAdress;
        }

        public void setBankAdress(String bankAdress) {
            this.bankAdress = bankAdress;
        }

        public String getBankCity() {
            return bankCity;
        }

        public void setBankCity(String bankCity) {
            this.bankCity = bankCity;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getBankProvince() {
            return bankProvince;
        }

        public void setBankProvince(String bankProvince) {
            this.bankProvince = bankProvince;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }
    }
}
