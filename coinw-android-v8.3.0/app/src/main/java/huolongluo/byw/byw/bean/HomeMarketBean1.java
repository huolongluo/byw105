package huolongluo.byw.byw.bean;


import java.io.Serializable;
import java.util.List;

/**
 * Created by LS on 2018/7/29.
 */
public class HomeMarketBean1 implements Serializable
{
    private boolean result;
    private String lastUpdateTime;
    private List<MarketListBean1> list;

    public boolean isResult()
    {
        return result;
    }

    public void setResult(boolean result)
    {
        this.result = result;
    }

    public String getLastUpdateTime()
    {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime)
    {
        this.lastUpdateTime = lastUpdateTime;
    }

    public List<MarketListBean1> getList()
    {
        return list;
    }

    public void setList(List<MarketListBean1> list)
    {
        this.list = list;
    }

    @Override
    public String toString() {
        return "HomeMarketBean1{" +
                "result=" + result +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                ", list=" + list +
                '}';
    }
}
