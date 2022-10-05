package huolongluo.byw.byw.bean;


import java.util.List;

/**
 * Created by LS on 2018/7/5.
 */

public class KChartMarketBean {
    private boolean result;
    private String lastUpdateTime;
    private List<KChartBean> data;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public List<KChartBean> getData() {
        return data;
    }

    public void setData(List<KChartBean> data) {
        this.data = data;
    }
}
