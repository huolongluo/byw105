package huolongluo.byw.reform.c2c.oct.bean;

public class C2cIsShowBean {
    /**
     * OTC_ONE_KEY_OPEN : {"describe":"开启OTC快捷交易 true 开启","value":false}
     * C2C_CLOSE : {"describe":"关闭C2C true 关闭","value":"false"}
     */
    //{"RED_ENVELOPE_DISPLAY_CLOSE":{"describe":"关闭红包功能展示给用户 true 关闭","value":"true"},"C2C_CLOSE":{"describe":"关闭C2C true 关闭","value":"true"},"RED_ENVELOPE_CLOSE":{"describe":"关闭红包 true 关闭","value":"false"}}
    private Dict C2C_CLOSE;
    private Dict RED_ENVELOPE_CLOSE;
    private Dict RED_ENVELOPE_DISPLAY_CLOSE;
    private Dict DEFAULT_ETF_TRADE_ID;
    private Dict COIN_LOAN_DISPLAY_CLOSE;//value true贷宝关闭，false为开启
    private Dict OTC_KYC_ALI_YUN_FACE_ID;//value: true开启kyc的阿里人脸识别
    private Dict onFidoSwitch;//海外认证sdk onfido ,value: false 关
    private Dict limitCertNumWithDay;//海外认证sdk onfido,value: onfido一天最大认证次数
    private String OTC_ADVERTISE_VIEW_HOST ;//OTC广告域名前缀

    public Dict getOnFidoSwitch() {
        return onFidoSwitch;
    }

    public void setOnFidoSwitch(Dict onFidoSwitch) {
        this.onFidoSwitch = onFidoSwitch;
    }

    public Dict getLimitCertNumWithDay() {
        return limitCertNumWithDay;
    }

    public void setLimitCertNumWithDay(Dict limitCertNumWithDay) {
        this.limitCertNumWithDay = limitCertNumWithDay;
    }

    public String getOTC_ADVERTISE_VIEW_HOST() {
        return OTC_ADVERTISE_VIEW_HOST;
    }

    public void setOTC_ADVERTISE_VIEW_HOST(String OTC_ADVERTISE_VIEW_HOST) {
        this.OTC_ADVERTISE_VIEW_HOST = OTC_ADVERTISE_VIEW_HOST;
    }

    public Dict getOTC_KYC_ALI_YUN_FACE_ID() {
        return OTC_KYC_ALI_YUN_FACE_ID;
    }

    public void setOTC_KYC_ALI_YUN_FACE_ID(Dict OTC_KYC_ALI_YUN_FACE_ID) {
        this.OTC_KYC_ALI_YUN_FACE_ID = OTC_KYC_ALI_YUN_FACE_ID;
    }

    public C2cIsShowBean.Dict getRED_ENVELOPE_CLOSE() {
        return RED_ENVELOPE_CLOSE;
    }

    private OTCONEKEYOPENBean OTC_ONE_KEY_OPEN;
//    private C2CCLOSEBean C2C_CLOSE;

    public void setRED_ENVELOPE_DISPLAY_CLOSE(C2cIsShowBean.Dict RED_ENVELOPE_DISPLAY_CLOSE) {
        this.RED_ENVELOPE_DISPLAY_CLOSE = RED_ENVELOPE_DISPLAY_CLOSE;
    }

    public Dict getDEFAULT_ETF_TRADE_ID() {
        return DEFAULT_ETF_TRADE_ID;
    }
    public void setDEFAULT_ETF_TRADE_ID(C2cIsShowBean.Dict DEFAULT_ETF_TRADE_ID) {
        this.DEFAULT_ETF_TRADE_ID = DEFAULT_ETF_TRADE_ID;
    }

    public OTCONEKEYOPENBean getOTC_ONE_KEY_OPEN() {
        return OTC_ONE_KEY_OPEN;
    }

    public Dict getCOIN_LOAN_DISPLAY_CLOSE() {
        return COIN_LOAN_DISPLAY_CLOSE;
    }

    public void setCOIN_LOAN_DISPLAY_CLOSE(Dict COIN_LOAN_DISPLAY_CLOSE) {
        this.COIN_LOAN_DISPLAY_CLOSE = COIN_LOAN_DISPLAY_CLOSE;
    }

    public void setOTC_ONE_KEY_OPEN(OTCONEKEYOPENBean OTC_ONE_KEY_OPEN) {
        this.OTC_ONE_KEY_OPEN = OTC_ONE_KEY_OPEN;
    }

    public Dict getRED_ENVELOPE_DISPLAY_CLOSE() {
        return this.RED_ENVELOPE_DISPLAY_CLOSE;
    }

    public void setRED_ENVELOPE_CLOSE(C2cIsShowBean.Dict RED_ENVELOPE_CLOSE) {
        this.RED_ENVELOPE_CLOSE = RED_ENVELOPE_CLOSE;
    }

    public Dict getC2C_CLOSE() {
        return C2C_CLOSE;
    }

    public void setC2C_CLOSE(Dict C2C_CLOSE) {
        this.C2C_CLOSE = C2C_CLOSE;
    }

    public static class Dict {
        public String describe;
        public String value;

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class OTCONEKEYOPENBean {
        /**
         * describe : 开启OTC快捷交易 true 开启
         * value : false
         */

        private String describe;
        private String value;

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}