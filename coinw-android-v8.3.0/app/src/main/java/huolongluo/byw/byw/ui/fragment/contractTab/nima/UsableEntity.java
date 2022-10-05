package huolongluo.byw.byw.ui.fragment.contractTab.nima;

public class UsableEntity {

    /**
     * result : true
     * code : 0
     * value : 操作成功
     * data : {"transferable":"5408.9040","available":"5408.9040","mud":"0.0000"}
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
         * transferable : 5408.9040
         * available : 5408.9040
         * mud : 0.0000
         */

        private String transferable;
        private String available;
        private String mud;

        public String getTransferable() {
            return transferable;
        }

        public void setTransferable(String transferable) {
            this.transferable = transferable;
        }

        public String getAvailable() {
            return available;
        }

        public void setAvailable(String available) {
            this.available = available;
        }

        public String getMud() {
            return mud;
        }

        public void setMud(String mud) {
            this.mud = mud;
        }
    }
}
