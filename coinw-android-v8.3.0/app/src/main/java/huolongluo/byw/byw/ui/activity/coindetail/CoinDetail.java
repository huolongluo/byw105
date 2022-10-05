package huolongluo.byw.byw.ui.activity.coindetail;

import java.util.List;

/**
 * Created by LS on 2018/7/18.
 */

public class CoinDetail {



   /* {
        "code": 0,
            "result": true,
            "trades": [
        {
            "cointype": "HC",
                "fb": "CNYT",
                "isSelf": "false",
                "price": 12.181,
                "rose": -11.49,
                "trademapping": 5
        }
    ]
    }*/




    private boolean result;
    private List<CoinDetailList> trades;
    private int code;

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

    public List<CoinDetailList> getTrades() {
        return trades;
    }

    public void setTrades(List<CoinDetailList> trades) {
        this.trades = trades;
    }
}
