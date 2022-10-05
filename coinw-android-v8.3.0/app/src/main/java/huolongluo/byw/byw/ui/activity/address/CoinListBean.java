package huolongluo.byw.byw.ui.activity.address;

import java.util.List;

/**
 * Created by LS on 2018/7/12.
 */

public class CoinListBean {
    private String firstLetter;
    private List<CoinList1Bean> coins;

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    public List<CoinList1Bean> getCoins() {
        return coins;
    }

    public void setCoins(List<CoinList1Bean> coins) {
        this.coins = coins;
    }
}
