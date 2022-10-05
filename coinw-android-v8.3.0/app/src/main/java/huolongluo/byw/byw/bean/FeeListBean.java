package huolongluo.byw.byw.bean;


import java.io.Serializable;

/**
 * <p>
 * Created by 火龙裸 on 2018/1/4 0004.
 */
public class FeeListBean implements Serializable
{
    /**
     * level : 1
     * fee : 0.003
     */

    private int level;
    private double fee;

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public double getFee()
    {
        return fee;
    }

    public void setFee(double fee)
    {
        this.fee = fee;
    }
}
