package huolongluo.byw.byw.bean;

/**
 * c2 认证成功页面限额描述
 */
public class C2AuthSuccessfulEntity {


    /**
     * result : true
     * code : 200
     * value : {"KYC_SINGLE_SELL":"20","KYC_DAY_SELL":"200"}
     */

    private boolean result;
    private int code;
    private ValueBean value;

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

    public ValueBean getValue() {
        return value;
    }

    public void setValue(ValueBean value) {
        this.value = value;
    }

    public static class ValueBean {
        /**
         * KYC_SINGLE_SELL : 20
         * KYC_DAY_SELL : 200
         */

        private String KYC_SINGLE_SELL;
        private String KYC_DAY_SELL;

        public String getKYC_SINGLE_SELL() {
            return KYC_SINGLE_SELL;
        }

        public void setKYC_SINGLE_SELL(String KYC_SINGLE_SELL) {
            this.KYC_SINGLE_SELL = KYC_SINGLE_SELL;
        }

        public String getKYC_DAY_SELL() {
            return KYC_DAY_SELL;
        }

        public void setKYC_DAY_SELL(String KYC_DAY_SELL) {
            this.KYC_DAY_SELL = KYC_DAY_SELL;
        }
    }
}
