package huolongluo.byw.byw.bean;


import java.io.Serializable;

/**
 * <p>
 * Created by 火龙裸 on 2017/12/30 0030.
 */
public class PersonalAssetBean implements Serializable
{
    private String totalAsset="11";
    private TradeAccountBean tradeAccount;
    private MarginAccountBean marginAccount;

    public String getTotalAsset()
    {
        return totalAsset;
    }

    public void setTotalAsset(String totalAsset)
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

    @Override
    public String toString() {
        return "PersonalAssetBean{" +
                "totalAsset='" + totalAsset + '\'' +
                ", tradeAccount=" + tradeAccount +
                ", marginAccount=" + marginAccount +
                '}';
    }
}
