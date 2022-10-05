package huolongluo.byw.reform.c2c.oct.bean;

import java.util.List;

public class Text {


    private List<PayAccountsBean> payAccounts;

    public List<PayAccountsBean> getPayAccounts() {
        return payAccounts;
    }

    public void setPayAccounts(List<PayAccountsBean> payAccounts) {
        this.payAccounts = payAccounts;
    }

    public static class PayAccountsBean {
        /**
         * account : alipay2
         * bankAdress :
         * bankCity :
         * bankName :
         * bankProvince :
         * id : 45
         * qrcode : /upload/images/201909291734048_D7si9.jpg
         * realName : MAX
         * type : 3
         */

        private String account;
        private String bankAdress;
        private String bankCity;
        private String bankName;
        private String bankProvince;
        private int id;
        private String qrcode;
        private String realName;
        private int type;

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

        public String getQrcode() {
            return qrcode;
        }

        public void setQrcode(String qrcode) {
            this.qrcode = qrcode;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
