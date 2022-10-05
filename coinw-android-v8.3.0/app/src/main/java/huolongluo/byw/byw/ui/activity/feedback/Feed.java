package huolongluo.byw.byw.ui.activity.feedback;

import java.util.List;
/**
 * Created by LS on 2018/7/15.
 */

public class Feed {
    private boolean result;
    private List<FeedList> list;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public List<FeedList> getList() {
        return list;
    }

    public void setList(List<FeedList> list) {
        this.list = list;
    }
}
