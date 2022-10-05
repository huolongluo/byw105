package huolongluo.byw.byw.ui.fragment.contractTab;

import java.util.List;

public class ContractCoinTypeEntity {

    /**
     * code : 0
     * data : [{"coinId":29,"coinName":"USDT","quota":null,"status":1},{"coinId":16,"coinName":"ETH","quota":null,"status":1},{"coinId":28,"coinName":"EOS","quota":null,"status":1}]
     * i18nMsgCode : null
     * msg : null
     * success : true
     */

    private int code;
    private Object i18nMsgCode;
    private String msg;
    private boolean success;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getI18nMsgCode() {
        return i18nMsgCode;
    }

    public void setI18nMsgCode(Object i18nMsgCode) {
        this.i18nMsgCode = i18nMsgCode;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * coinId : 29
         * coinName : USDT
         * quota : null
         * status : 1
         */

        private int coinId;
        private String coinName;
        private Object quota;
        private int status;

        public int getCoinId() {
            return coinId;
        }

        public void setCoinId(int coinId) {
            this.coinId = coinId;
        }

        public String getCoinName() {
            return coinName;
        }

        public void setCoinName(String coinName) {
            this.coinName = coinName;
        }

        public Object getQuota() {
            return quota;
        }

        public void setQuota(Object quota) {
            this.quota = quota;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
