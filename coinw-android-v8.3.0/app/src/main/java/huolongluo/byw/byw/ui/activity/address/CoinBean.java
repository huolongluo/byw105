package huolongluo.byw.byw.ui.activity.address;

import java.util.List;

/**
 * Created by LS on 2018/7/12.
 */

public class CoinBean {
    private boolean result;
    private String code;
    private List<CoinListBean> coinsSord;
    private List<CoinList1Bean> recentCoin;

    public List<CoinList1Bean> getRecentCoin() {
        return recentCoin;
    }

    public void setRecentCoin(List<CoinList1Bean> recentCoin) {
        this.recentCoin = recentCoin;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<CoinListBean> getCoinsSord() {
        return coinsSord;
    }

    public void setCoinsSord(List<CoinListBean> coinsSord) {
        this.coinsSord = coinsSord;
    }
}
