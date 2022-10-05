package huolongluo.byw.reform.c2c.oct.bean;

import java.io.Serializable;
import java.util.List;

public class BrandCardsEneity {

    /**
     * result : true
     * code : 0
     * value : 操作成功
     * data : {"data":[{"account":"51111111111111123","bankAdress":"454545","bankCity":"毕节市","bankName":"招商银行","bankProvince":"贵州省","createTime":1571046671000,"id":47,"realName":"云1003","status":2,"type":1,"uid":400078,"updateTime":1571046671000},{"account":"51111111111111122","bankAdress":"454545","bankCity":"毕节市","bankName":"招商银行","bankProvince":"贵州省","createTime":1571046657000,"id":46,"realName":"云1003","status":2,"type":1,"uid":400078,"updateTime":1571046657000},{"account":"51111111111111121","bankAdress":"454545","bankCity":"毕节市","bankName":"招商银行","bankProvince":"贵州省","createTime":1571046635000,"id":45,"realName":"云1003","status":2,"type":1,"uid":400078,"updateTime":1571046635000},{"account":"51111111111111111","bankAdress":"454545","bankCity":"毕节市","bankName":"招商银行","bankProvince":"贵州省","createTime":1571046608000,"id":44,"realName":"云1003","status":2,"type":1,"uid":400078,"updateTime":1571046608000},{"account":"511111111111111117","bankAdress":"3223","bankCity":"毕节市","bankName":"建设银行","bankProvince":"贵州省","createTime":1571048635000,"id":51,"realName":"云1003","status":1,"type":1,"uid":400078,"updateTime":1571048635000},{"account":"51111111111111119","bankAdress":"4234234","bankCity":"毕节市","bankName":"工商银行","bankProvince":"贵州省","createTime":1571048603000,"id":50,"realName":"云1003","status":1,"type":1,"uid":400078,"updateTime":1571048603000},{"account":"4324324324324324324","bankAdress":"223432","bankCity":"毕节市","bankName":"交通银行","bankProvince":"贵州省","createTime":1571048357000,"id":49,"realName":"云1003","status":1,"type":1,"uid":400078,"updateTime":1571048357000},{"account":"51111111111111124","bankAdress":"454545","bankCity":"毕节市","bankName":"招商银行","bankProvince":"贵州省","createTime":1571046752000,"id":48,"realName":"云1003","status":1,"type":1,"uid":400078,"updateTime":1571046752000}],"pageNo":1,"pageSize":10,"total":8}
     */

    private boolean result;
    private int code;
    private String value;
    private DataBeanX data;

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

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public static class DataBeanX {
        /**
         * data : [{"account":"51111111111111123","bankAdress":"454545","bankCity":"毕节市","bankName":"招商银行","bankProvince":"贵州省","createTime":1571046671000,"id":47,"realName":"云1003","status":2,"type":1,"uid":400078,"updateTime":1571046671000},{"account":"51111111111111122","bankAdress":"454545","bankCity":"毕节市","bankName":"招商银行","bankProvince":"贵州省","createTime":1571046657000,"id":46,"realName":"云1003","status":2,"type":1,"uid":400078,"updateTime":1571046657000},{"account":"51111111111111121","bankAdress":"454545","bankCity":"毕节市","bankName":"招商银行","bankProvince":"贵州省","createTime":1571046635000,"id":45,"realName":"云1003","status":2,"type":1,"uid":400078,"updateTime":1571046635000},{"account":"51111111111111111","bankAdress":"454545","bankCity":"毕节市","bankName":"招商银行","bankProvince":"贵州省","createTime":1571046608000,"id":44,"realName":"云1003","status":2,"type":1,"uid":400078,"updateTime":1571046608000},{"account":"511111111111111117","bankAdress":"3223","bankCity":"毕节市","bankName":"建设银行","bankProvince":"贵州省","createTime":1571048635000,"id":51,"realName":"云1003","status":1,"type":1,"uid":400078,"updateTime":1571048635000},{"account":"51111111111111119","bankAdress":"4234234","bankCity":"毕节市","bankName":"工商银行","bankProvince":"贵州省","createTime":1571048603000,"id":50,"realName":"云1003","status":1,"type":1,"uid":400078,"updateTime":1571048603000},{"account":"4324324324324324324","bankAdress":"223432","bankCity":"毕节市","bankName":"交通银行","bankProvince":"贵州省","createTime":1571048357000,"id":49,"realName":"云1003","status":1,"type":1,"uid":400078,"updateTime":1571048357000},{"account":"51111111111111124","bankAdress":"454545","bankCity":"毕节市","bankName":"招商银行","bankProvince":"贵州省","createTime":1571046752000,"id":48,"realName":"云1003","status":1,"type":1,"uid":400078,"updateTime":1571046752000}]
         * pageNo : 1
         * pageSize : 10
         * total : 8
         */

        private int pageNo;
        private int pageSize;
        private int total;
        private List<DataBean> data;

        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean implements Serializable {
            /**
             * account : 51111111111111123
             * bankAdress : 454545
             * bankCity : 毕节市
             * bankName : 招商银行
             * bankProvince : 贵州省
             * createTime : 1571046671000
             * id : 47
             * realName : 云1003
             * status : 2
             * type : 1
             * uid : 400078
             * updateTime : 1571046671000
             */
            private String qrcode;

            public String getQrcode() {
                return qrcode;
            }

            public void setQrcode(String qrcode) {
                this.qrcode = qrcode;
            }

            private String account;
            private String bankAdress;
            private String bankCity;
            private String bankName;
            private String bankProvince;
            private long createTime;
            private int id;
            private String realName;
            private int status;
            private int type;
            private int uid;
            private long updateTime;

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

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
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

            public long getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(long updateTime) {
                this.updateTime = updateTime;
            }
        }
    }
}
