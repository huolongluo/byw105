package huolongluo.byw.byw.ui.fragment.contractTab.nima;

import java.util.List;

public class NMHistoryEntity {

    /**
     * result : true
     * code : 0
     * value : 操作成功
     * data : [{"grantDt":"2020-03-11 16:55:35","mudQuota":"7","transferQuota":"10"},{"grantDt":"2020-03-11 17:39:59","mudQuota":"10","transferQuota":"20"}]
     */

    private boolean result;
    private int code;
    private String value;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * grantDt : 2020-03-11 16:55:35
         * mudQuota : 7
         * transferQuota : 10
         */

        private String grantDt;
        private String mudQuota;
        private String transferQuota;

        public String getGrantDt() {
            return grantDt;
        }

        public void setGrantDt(String grantDt) {
            this.grantDt = grantDt;
        }

        public String getMudQuota() {
            return mudQuota;
        }

        public void setMudQuota(String mudQuota) {
            this.mudQuota = mudQuota;
        }

        public String getTransferQuota() {
            return transferQuota;
        }

        public void setTransferQuota(String transferQuota) {
            this.transferQuota = transferQuota;
        }
    }
}
