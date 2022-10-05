package huolongluo.byw.model;

public class LimitedTimeTipsResult {

    public String message;
    public String code;
    public LimitedTimeTips data;

    public static class LimitedTimeTips {

        public String isLimitedTimeContent;
        public int code;
        public String value;
        public int tradeRule;
        public String tradeRuleUrl;
        public String tradeRuleDesc;
    }
}
