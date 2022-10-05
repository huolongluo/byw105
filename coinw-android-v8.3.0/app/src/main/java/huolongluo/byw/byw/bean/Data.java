package huolongluo.byw.byw.bean;


import java.io.Serializable;

/**
 * Created by 火龙裸 on 2018/5/9.
 */
public class Data implements Serializable
{
    public float xValue;
    public float yValue;
    public String xAxisValue;

    public Data(float xValue, float yValue, String xAxisValue)
    {
        this.xValue = xValue;
        this.yValue = yValue;
        this.xAxisValue = xAxisValue;
    }
}
