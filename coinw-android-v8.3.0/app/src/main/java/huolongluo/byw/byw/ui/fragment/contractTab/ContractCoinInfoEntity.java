package huolongluo.byw.byw.ui.fragment.contractTab;
public class ContractCoinInfoEntity {
    /**
     * result : true
     * code : 0
     * value : 操作成功
     * data : {"accountId":0,"availableVol":"","cashVol":"","coinCode":"","createdAt":"","earningsVol":"","freezeVol":"","realisedVol":"","updatedAt":""}
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
         * accountId : 0
         * availableVol :
         * cashVol :
         * coinCode :
         * createdAt :
         * earningsVol :
         * freezeVol :
         * realisedVol :
         * updatedAt :
         */
        private int accountId;
        private String availableVol;//新接口改为availableBalance
        private String cashVol;//新接口废弃改为totalVol
        private String coinCode;
        private String createdAt;
        private String earningsVol;
        private String freezeVol;
        private String realisedVol;
        private String updatedAt;
        //新接口要求
        private String availableBalance;//可用保证金、可用余额
        private String totalVol;//总资产
        private String totalIm;//仓位保证金

        public String getTotalVol() {
            return totalVol;
        }

        public void setTotalVol(String totalVol) {
            this.totalVol = totalVol;
        }

        public String getTotalIm() {
            return totalIm;
        }

        public void setTotalIm(String totalIm) {
            this.totalIm = totalIm;
        }

        public String getAvailableBalance() {
            return availableBalance;
        }

        public void setAvailableBalance(String availableBalance) {
            this.availableBalance = availableBalance;
        }

        public int getAccountId() {
            return accountId;
        }

        public void setAccountId(int accountId) {
            this.accountId = accountId;
        }

        public String getAvailableVol() {
            return availableVol;
        }

        public void setAvailableVol(String availableVol) {
            this.availableVol = availableVol;
        }

        public String getCashVol() {
            return cashVol;
        }

        public void setCashVol(String cashVol) {
            this.cashVol = cashVol;
        }

        public String getCoinCode() {
            return coinCode;
        }

        public void setCoinCode(String coinCode) {
            this.coinCode = coinCode;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getEarningsVol() {
            return earningsVol;
        }

        public void setEarningsVol(String earningsVol) {
            this.earningsVol = earningsVol;
        }

        public String getFreezeVol() {
            return freezeVol;
        }

        public void setFreezeVol(String freezeVol) {
            this.freezeVol = freezeVol;
        }

        public String getRealisedVol() {
            return realisedVol;
        }

        public void setRealisedVol(String realisedVol) {
            this.realisedVol = realisedVol;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}
