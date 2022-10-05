package huolongluo.byw.byw.ui.fragment.maintab01;

import java.util.List;

/**
 * Created by LS on 2018/7/18.
 */

public class KlineBean {
    private boolean result;
    private String lastUpdateTime;
    private List<List<String>> rows;

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

    public List<List<String>> getRows() {
        return rows;
    }

    public void setRows(List<List<String>> rows) {
        this.rows = rows;
    }
}
