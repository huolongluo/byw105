package huolongluo.byw.reform.c2c.oct.bean;

public class DeleteCardEntity {

    /**
     * code : 200
     * data : {"result":true,"code":-1,"value":"收款方式处于开启状态,不能删除","error":"业务异常"}
     * forceUpdate : 0
     * message : 执行成功
     */

    private String code;
    private String data;
    private int forceUpdate;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(int forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Eneity {

        /**
         * result : true
         * code : -1
         * value : 收款方式处于开启状态,不能删除
         * error : 业务异常
         */

        private boolean result;
        private int code;
        private String value;
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

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
