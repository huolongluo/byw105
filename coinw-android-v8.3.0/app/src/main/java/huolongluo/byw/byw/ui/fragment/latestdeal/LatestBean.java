package huolongluo.byw.byw.ui.fragment.latestdeal;

import java.util.List;

/**
 * Created by LS on 2018/7/11.
 */

public class LatestBean {
    private boolean result;
    private String lastUpdateTime;
    private List<LatestListBean> rows;

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

    public List<LatestListBean> getRows() {
        return rows;
    }

    public void setRows(List<LatestListBean> rows) {
        this.rows = rows;
    }
}
