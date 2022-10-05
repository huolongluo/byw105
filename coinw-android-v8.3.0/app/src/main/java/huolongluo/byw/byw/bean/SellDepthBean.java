package huolongluo.byw.byw.bean;


import java.io.Serializable;

/**
 * Created by 火龙裸 on 2017/12/27.
 */
public class SellDepthBean implements Serializable
{
    /**
     * id : 1
     * price : 0.096
     * amount : 29310.493
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
        return "SellDepthBean{" +
                "id=" + id +
                ", price='" + price + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
