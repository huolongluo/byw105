package huolongluo.byw.widget;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import huolongluo.byw.R;

/**
 * author：ajiang
 * mail：1025065158@qq.com
 * blog：http://blog.csdn.net/qqyanjiang
 */
public class MyBottomMarkerView extends MarkerView
{
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
//    private TextView markerTv;
    private String time;

    public MyBottomMarkerView(Context context, int layoutResource)
    {
        super(context, layoutResource);
//        markerTv = (TextView) findViewById(R.id.marker_tv);
//        markerTv.setTextSize(10);
    }

    public void setData(String time)
    {

        this.time = time;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight)
    {
//        markerTv.setText(time);
    }
}
