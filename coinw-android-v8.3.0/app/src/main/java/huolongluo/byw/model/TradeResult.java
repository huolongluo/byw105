package huolongluo.byw.model;

import huolongluo.byw.reform.trade.bean.TradeInfoBean;

import java.util.List;

public class TradeResult {

    public String message;
    public String code;
    public SubTradeResult<TradeInfoBean> data;

    public static class SubTradeResult<TradeInfoBean> {

        public String value;
        public int code;
        public TradeInfoBean data;
    }
}
