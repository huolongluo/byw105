package huolongluo.byw.byw.bean;


import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/1/20 0020.
 */
public class FinanceBean implements Serializable
{
    private boolean result;
    private AssetBean asset;
    private List<CoinAddressBean> coinAddress;

    public boolean isResult()
    {
        return result;
    }

    public void setResult(boolean result)
    {
        this.result = result;
    }

    public AssetBean getAsset()
    {
        return asset;
    }

    public void setAsset(AssetBean asset)
    {
        this.asset = asset;
    }

    public List<CoinAddressBean> getCoinAddress()
    {
        return coinAddress;
    }

    public void setCoinAddress(List<CoinAddressBean> coinAddress)
    {
        this.coinAddress = coinAddress;
    }

    public static class AssetBean
    {
        private double totalAsset;
        private TradeAccountBean tradeAccount;
        private MarginAccountBean marginAccount;

        public double getTotalAsset()
        {
            return totalAsset;
        }

        public void setTotalAsset(double totalAsset)
        {
            this.totalAsset = totalAsset;
        }

        public TradeAccountBean getTradeAccount()
        {
            return tradeAccount;
        }

        public void setTradeAccount(TradeAccountBean tradeAccount)
        {
            this.tradeAccount = tradeAccount;
        }

        public MarginAccountBean getMarginAccount()
        {
            return marginAccount;
        }

        public void setMarginAccount(MarginAccountBean marginAccount)
        {
            this.marginAccount = marginAccount;
        }

        public static class TradeAccountBean
        {
            private double totalAsset;
            private double netAsset;
            private List<CoinsBean> coins;

            public double getTotalAsset()
            {
                return totalAsset;
            }

            public void setTotalAsset(double totalAsset)
            {
                this.totalAsset = totalAsset;
            }

            public double getNetAsset()
            {
                return netAsset;
            }

            public void setNetAsset(double netAsset)
            {
                this.netAsset = netAsset;
            }

            public List<CoinsBean> getCoins()
            {
                return coins;
            }

            public void setCoins(List<CoinsBean> coins)
            {
                this.coins = coins;
            }

            public static class CoinsBean
            {
                /**
                 * id : 0
                 * cnName : 人民币
                 * shortName : CNY
                 * total : 85.6
                 * frozen : 0
                 * isCoin : false
                 * logo : https://btc018.oss-cn-shenzhen.aliyuncs.com/201709010330040_9rOWN.png
                 * borrow : 0
                 * zhehe : 8.18
                 * curPrice : 0.08
                 * isWithDraw : true
                 * isRecharge : true
                 */

                private int id;
                private String cnName;
                private String shortName;
                private double total;
                private int frozen;
                private boolean isCoin;
                private String logo;
                private int borrow;
                private double zhehe;
                private double curPrice;
                private boolean isWithDraw;
                private boolean isRecharge;

                public int getId()
                {
                    return id;
                }

                public void setId(int id)
                {
                    this.id = id;
                }

                public String getCnName()
                {
                    return cnName;
                }

                public void setCnName(String cnName)
                {
                    this.cnName = cnName;
                }

                public String getShortName()
                {
                    return shortName;
                }

                public void setShortName(String shortName)
                {
                    this.shortName = shortName;
                }

                public double getTotal()
                {
                    return total;
                }

                public void setTotal(double total)
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

                public boolean isIsCoin()
                {
                    return isCoin;
                }

                public void setIsCoin(boolean isCoin)
                {
                    this.isCoin = isCoin;
                }

                public String getLogo()
                {
                    return logo;
                }

                public void setLogo(String logo)
                {
                    this.logo = logo;
                }

                public int getBorrow()
                {
                    return borrow;
                }

                public void setBorrow(int borrow)
                {
                    this.borrow = borrow;
                }

                public double getZhehe()
                {
                    return zhehe;
                }

                public void setZhehe(double zhehe)
                {
                    this.zhehe = zhehe;
                }

                public double getCurPrice()
                {
                    return curPrice;
                }

                public void setCurPrice(double curPrice)
                {
                    this.curPrice = curPrice;
                }

                public boolean isIsWithDraw()
                {
                    return isWithDraw;
                }

                public void setIsWithDraw(boolean isWithDraw)
                {
                    this.isWithDraw = isWithDraw;
                }

                public boolean isIsRecharge()
                {
                    return isRecharge;
                }

                public void setIsRecharge(boolean isRecharge)
                {
                    this.isRecharge = isRecharge;
                }
            }
        }

        public static class MarginAccountBean
        {
            /**
             * coins : [{"id":0,"total":0,"frozen":0,"borrow":0},{"id":3,"total":0,"frozen":0,"borrow":0},{"id":6,"total":0,"frozen":0,"borrow":0},{"id":7,
             * "total":0,"frozen":0,"borrow":0},{"id":8,"total":0,"frozen":0,"borrow":0},{"id":9,"total":0,"frozen":0,"borrow":0},{"id":11,"total":0,
             * "frozen":0,"borrow":0},{"id":12,"total":0,"frozen":0,"borrow":0},{"id":13,"total":0,"frozen":0,"borrow":0},{"id":14,"total":0,"frozen":0,
             * "borrow":0},{"id":16,"total":0,"frozen":0,"borrow":0},{"id":17,"total":0,"frozen":0,"borrow":0},{"id":18,"total":0,"frozen":0,"borrow":0},
             * {"id":19,"total":0,"frozen":0,"borrow":0},{"id":20,"total":0,"frozen":0,"borrow":0},{"id":21,"total":0,"frozen":0,"borrow":0},{"id":22,
             * "total":0,"frozen":0,"borrow":0},{"id":23,"total":0,"frozen":0,"borrow":0},{"id":24,"total":0,"frozen":0,"borrow":0},{"id":25,"total":0,
             * "frozen":0,"borrow":0},{"id":26,"total":0,"frozen":0,"borrow":0},{"id":27,"total":0,"frozen":0,"borrow":0},{"id":28,"total":0,"frozen":0,
             * "borrow":0},{"id":31,"total":0,"frozen":0,"borrow":0},{"id":32,"total":0,"frozen":0,"borrow":0},{"id":33,"total":0,"frozen":0,"borrow":0},
             * {"id":34,"total":0,"frozen":0,"borrow":0},{"id":35,"total":0,"frozen":0,"borrow":0},{"id":36,"total":0,"frozen":0,"borrow":0}]
             * totalAsset : 0
             */

            private int totalAsset;
            private List<CoinsBeanX> coins;

            public int getTotalAsset()
            {
                return totalAsset;
            }

            public void setTotalAsset(int totalAsset)
            {
                this.totalAsset = totalAsset;
            }

            public List<CoinsBeanX> getCoins()
            {
                return coins;
            }

            public void setCoins(List<CoinsBeanX> coins)
            {
                this.coins = coins;
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
    }

    public static class CoinAddressBean
    {
        /**
         * coinId : 3
         * coinName : BC
         * coinShortName : BC
         * address : LdAvacc6sVSTEnLfUFXVoDtY4tr6UufrHu
         */

        private int coinId;
        private String coinName;
        private String coinShortName;
        private String address;

        public int getCoinId()
        {
            return coinId;
        }

        public void setCoinId(int coinId)
        {
            this.coinId = coinId;
        }

        public String getCoinName()
        {
            return coinName;
        }

        public void setCoinName(String coinName)
        {
            this.coinName = coinName;
        }

        public String getCoinShortName()
        {
            return coinShortName;
        }

        public void setCoinShortName(String coinShortName)
        {
            this.coinShortName = coinShortName;
        }

        public String getAddress()
        {
            return address;
        }

        public void setAddress(String address)
        {
            this.address = address;
        }
    }
}
