package huolongluo.byw.reform.base;

import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import huolongluo.byw.byw.ui.activity.main.MySwipeBackActivity;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by Administrator on 2019/1/12 0012.
 */

public class BaseSwipeBackActivity extends MySwipeBackActivity {

    public SwipeBackLayout mSwipeBackLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSwipeBackLayout = getSwipeBackLayout();

        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.setEdgeSize(200);//

    }


}
