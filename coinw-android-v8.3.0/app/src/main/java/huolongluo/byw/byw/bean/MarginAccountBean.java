package huolongluo.byw.byw.bean;


import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * Created by 火龙裸 on 2017/12/30 0030.
 */
public class MarginAccountBean implements Serializable
{
    /**
     * coins : [{"id":0,"total":0,"frozen":0,"borrow":0},{"id":3,"total":0,"frozen":0,"borrow":0},{"id":6,"total":0,"frozen":0,"borrow":0},{"id":7,
     * "total":0,"frozen":0,"borrow":0},{"id":8,"total":0,"frozen":0,"borrow":0},{"id":9,"total":0,"frozen":0,"borrow":0},{"id":11,"total":0,"frozen":0,
     * "borrow":0},{"id":12,"total":0,"frozen":0,"borrow":0},{"id":13,"total":0,"frozen":0,"borrow":0},{"id":14,"total":0,"frozen":0,"borrow":0},
     * {"id":16,"total":0,"frozen":0,"borrow":0},{"id":17,"total":0,"frozen":0,"borrow":0},{"id":18,"total":0,"frozen":0,"borrow":0},{"id":19,"total":0,
     * "frozen":0,"borrow":0},{"id":20,"total":0,"frozen":0,"borrow":0},{"id":21,"total":0,"frozen":0,"borrow":0},{"id":22,"total":0,"frozen":0,
     * "borrow":0},{"id":23,"total":0,"frozen":0,"borrow":0},{"id":24,"total":0,"frozen":0,"borrow":0},{"id":25,"total":0,"frozen":0,"borrow":0},
     * {"id":26,"total":0,"frozen":0,"borrow":0},{"id":27,"total":0,"frozen":0,"borrow":0},{"id":28,"total":0,"frozen":0,"borrow":0}]
     * totalAsset : 0
     */

    private int totalAsset;
    private List<MarginAccountBean.CoinsBeanX> coins;

    public int getTotalAsset()
    {
        return totalAsset;
    }

    public void setTotalAsset(int totalAsset)
    {
        this.totalAsset = totalAsset;
    }

    public List<MarginAccountBean.CoinsBeanX> getCoins()
    {
        return coins;
    }

    public void setCoins(List<MarginAccountBean.CoinsBeanX> coins)
    {
        this.coins = coins;
    }

    @Override
    public String toString() {
        return "MarginAccountBean{" +
                "totalAsset=" + totalAsset +
                ", coins=" + coins +
                '}';
    }

    public static class CoinsBeanX
    {
        /**
         * id : 0
         * total : 0
         * frozen : 0
         * borrow : 0
         */

        private int id;
        private int total;
        private int frozen;
        private int borrow;

        public int getId()
        {
            return id;
        }

        public void setId(int id)
        {
            this.id = id;
        }

        public int getTotal()
        {
            return total;
        }

        public void setTotal(int total)
        {
            this.total = total;
        }

        public int getFrozen()
        {
            return frozen;
        }

        public void setFrozen(int frozen)
        {
            this.frozen = frozen;
        }

        public int getBorrow()
        {
            return borrow;
        }

        public void setBorrow(int borrow)
        {
            this.borrow = borrow;
        }
    }
}
