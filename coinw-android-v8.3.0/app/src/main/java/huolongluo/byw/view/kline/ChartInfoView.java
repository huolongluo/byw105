package huolongluo.byw.view.kline;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.Chart;

/**
 * Created by dell on 2017/10/25.
 */

public abstract class ChartInfoView extends LinearLayout {


    public Chart[] mLineCharts;

    protected String mDateFormat = "yy-MM-dd HH:mm";


    public ChartInfoView(Context context) {
        super(context);
    }

    public ChartInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChartInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void setData(double lastClose, HisData data, int digits);

    public void setDateFormat(String format) {
        mDateFormat = format;
    }

    public void setChart(Chart... chart) {
        mLineCharts = chart;
    }
}