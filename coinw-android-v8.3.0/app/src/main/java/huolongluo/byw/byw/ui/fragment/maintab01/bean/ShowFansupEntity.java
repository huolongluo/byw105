package huolongluo.byw.byw.ui.fragment.maintab01.bean;

public class ShowFansupEntity {

    /**
     * code : 200
     * data : {"result":true,"code":0,"value":"操作成功","data":true}
     * message : 执行成功
     */

    private String code;
    private String data;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class FansUpEntity {

        /**
         * code : 200
         * data : {"result":true,"code":0,"value":"操作成功","data":true}
         * message : 执行成功
         */

        private int code;
        private boolean data;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public boolean getData() {
            return data;
        }

        public void setData(boolean data) {
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
