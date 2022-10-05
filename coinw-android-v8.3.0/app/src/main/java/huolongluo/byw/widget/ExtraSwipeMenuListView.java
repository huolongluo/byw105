package huolongluo.byw.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.baoyz.swipemenulistview.SwipeMenuListView;

/**
 * Created by 火龙裸 on 2017/04/07.
 */

public class ExtraSwipeMenuListView extends SwipeMenuListView
{

    public ExtraSwipeMenuListView(Context context)
    {
        // TODO Auto-generated method stub
        super(context);
    }

    public ExtraSwipeMenuListView(Context context, AttributeSet attrs)
    {
        // TODO Auto-generated method stub
        super(context, attrs);
    }

    public ExtraSwipeMenuListView(Context context, AttributeSet attrs, int defStyle)
    {
        // TODO Auto-generated method stub
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // TODO Auto-generated method stub
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
