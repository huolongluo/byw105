package huolongluo.byw.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by hy on 2016/11/3.
 * 可嵌套scrollview中的listview
 */
public class CanNestListView extends ListView{
    public CanNestListView(Context context) {
        super(context);
    }

    public CanNestListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CanNestListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
