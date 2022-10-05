package huolongluo.byw.byw.bean;

import java.io.Serializable;

/**
 * <p>
 * Created by 火龙裸 on 2017/12/30 0030.
 */
public class CoinAddressBean implements Serializable
{
    public CoinAddressBean() {
    }

    public CoinAddressBean(int coinId, String coinName) {
        this.coinId = coinId;
        this.coinName = coinName;
    }

    /**
     * coinId : 3
     * coinName : BC
     * coinShortName : BC
     * address : null
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

    @Override
    public String toString() {
        return "CoinAddressBean{" +
                "coinId=" + coinId +
                ", coinName='" + coinName + '\'' +
                ", coinShortName='" + coinShortName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
