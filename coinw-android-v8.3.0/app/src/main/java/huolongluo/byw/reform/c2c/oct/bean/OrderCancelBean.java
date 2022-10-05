package huolongluo.byw.reform.c2c.oct.bean;

public class OrderCancelBean {
    private int code;
    private DataBean data;
    private boolean result;
    private String value;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
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

    public static class DataBean {
        private OrderCancelCountsByDay orderCancelCountsByDay;
        private EnableExchangeMer enableExchangeMer;

        public EnableExchangeMer getEnableExchangeMer() {
            return enableExchangeMer;
        }

        public void setEnableExchangeMer(EnableExchangeMer enableExchangeMer) {
            this.enableExchangeMer = enableExchangeMer;
        }

        public OrderCancelCountsByDay getOrderCancelCountsByDay() {
            return orderCancelCountsByDay;
        }

        public void setOrderCancelCountsByDay(OrderCancelCountsByDay orderCancelCountsByDay) {
            this.orderCancelCountsByDay = orderCancelCountsByDay;
        }

        public static class OrderCancelCountsByDay {
            private String describe;
            private int value;

            public void setDescribe(String describe) {
                this.describe = describe;
            }

            public String getDescribe() {
                return describe;
            }

            public void setValue(int value) {
                this.value = value;
            }

            public int getValue() {
                return value;
            }

        }

        public static class EnableExchangeMer {
            private String describe;
            private boolean value;

            public void setDescribe(String describe) {
                this.describe = describe;
            }

            public String getDescribe() {
                return describe;
            }

            public void setValue(boolean value) {
                this.value = value;
            }

            public boolean getValue() {
                return value;
            }
        }
    }
}
