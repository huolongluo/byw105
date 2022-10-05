package huolongluo.byw.reform.c2c.oct.bean;

public class CardStatusEntity {

    /**
     * result : true
     * code : -1
     * value : 您要关闭的收款方式正在订单中使用，请先完成或取消订单再进行关闭
     * data : {"code":-102,"result":false,"value":"您要关闭的收款方式正在订单中使用，请先完成或取消订单再进行关闭"}
     * error : 业务异常
     */

    private boolean result;
    private int code;
    private String value;
    private DataBean data;
    private String error;

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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public static class DataBean {
        /**
         * code : -102
         * result : false
         * value : 您要关闭的收款方式正在订单中使用，请先完成或取消订单再进行关闭
         */

        private int code;
        private boolean result;
        private String value;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
