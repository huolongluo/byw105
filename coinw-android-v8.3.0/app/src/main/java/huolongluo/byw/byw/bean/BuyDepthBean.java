package huolongluo.byw.byw.bean;


import java.io.Serializable;

/**
 * Created by 火龙裸 on 2017/12/27.
 */
public class BuyDepthBean implements Serializable
{
    /**
     * id : 1
     * price : 0.095
     * amount : 67538.85800000001
     */

    private int id;
    private String price="0.0000";
    private String amount="0.0000";

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getPrice()
    {
        return price;
    }

    public void setPrice(String price)
    {
        this.price = price;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "BuyDepthBean{" +
                "id=" + id +
                ", price='" + price + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
