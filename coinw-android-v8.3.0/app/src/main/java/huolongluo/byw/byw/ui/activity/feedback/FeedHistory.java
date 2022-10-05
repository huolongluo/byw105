package huolongluo.byw.byw.ui.activity.feedback;

import android.widget.ListView;

import java.util.List;

/**
 * Created by LS on 2018/7/15.
 */

public class FeedHistory {
    private boolean result;
    private List<FeedListHis> list;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public List<FeedListHis> getList() {
        return list;
    }

    public void setList(List<FeedListHis> list) {
        this.list = list;
    }
}
