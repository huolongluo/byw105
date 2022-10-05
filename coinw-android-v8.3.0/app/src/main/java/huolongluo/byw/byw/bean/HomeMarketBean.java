package huolongluo.byw.byw.bean;


import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * Created by 火龙裸 on 2017/12/24 0024.
 */
public class HomeMarketBean implements Serializable
{
    private boolean result;
    private String lastUpdateTime;
    private List<MarketListBean> list;

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

    public List<MarketListBean> getList()
    {
        return list;
    }

    public void setList(List<MarketListBean> list)
    {
        this.list = list;
    }
}
