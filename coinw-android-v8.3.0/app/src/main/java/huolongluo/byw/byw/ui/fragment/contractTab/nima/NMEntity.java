package huolongluo.byw.byw.ui.fragment.contractTab.nima;

public class NMEntity {

    /**
     * result : true
     * code : 0
     * value : 操作成功
     * data : {"beginDt":null,"endDt":null,"gainAll":false,"hasMudRecord":false,"hasRealValidate":false,"mudQuota":"","mudTotalQuota":"","status":0,"transferQuota":""}
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
         * beginDt : null
         * endDt : null
         * gainAll : false
         * hasMudRecord : false
         * hasRealValidate : false
         * mudQuota :
         * mudTotalQuota :
         * status : 0
         * transferQuota :
         */

        private Object beginDt;
        private Object endDt;
        private boolean gainAll;
        private boolean hasMudRecord;
        private boolean hasRealValidate;
        private String mudQuota;
        private String mudTotalQuota;
        private int status;
        private String transferQuota;

        public Object getBeginDt() {
            return beginDt;
        }

        public void setBeginDt(Object beginDt) {
            this.beginDt = beginDt;
        }

        public Object getEndDt() {
            return endDt;
        }

        public void setEndDt(Object endDt) {
            this.endDt = endDt;
        }

        public boolean isGainAll() {
            return gainAll;
        }

        public void setGainAll(boolean gainAll) {
            this.gainAll = gainAll;
        }

        public boolean isHasMudRecord() {
            return hasMudRecord;
        }

        public void setHasMudRecord(boolean hasMudRecord) {
            this.hasMudRecord = hasMudRecord;
        }

        public boolean isHasRealValidate() {
            return hasRealValidate;
        }

        public void setHasRealValidate(boolean hasRealValidate) {
            this.hasRealValidate = hasRealValidate;
        }

        public String getMudQuota() {
            return mudQuota;
        }

        public void setMudQuota(String mudQuota) {
            this.mudQuota = mudQuota;
        }

        public String getMudTotalQuota() {
            return mudTotalQuota;
        }

        public void setMudTotalQuota(String mudTotalQuota) {
            this.mudTotalQuota = mudTotalQuota;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTransferQuota() {
            return transferQuota;
        }

        public void setTransferQuota(String transferQuota) {
            this.transferQuota = transferQuota;
        }
    }
}
