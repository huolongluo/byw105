package huolongluo.byw.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import com.github.mikephil.charting.charts.CombinedChart;

import java.util.List;

/**
 * Created by 火龙裸 on 2018/5/4.
 */
public class MyCombinedChart extends CombinedChart {

    private static final String TAG = "MyCombinedChart";
    //    private MyLeftMarkerView myMarkerViewLeft;
//    private MyRightMarkerView myMarkerViewRight;
//    private MyBottomMarkerView myBottomMarkerView;
//    private MyHMarkerView myMarkerViewH;
    private List<String> timeList;

    public MyCombinedChart(Context context) {
        super(context);
    }

    public MyCombinedChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCombinedChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setMarker(MyLeftMarkerView markerLeft, MyRightMarkerView markerH, List<String> timeList) {
//        this.myMarkerViewLeft = markerLeft;
//        this.myMarkerViewRight = markerH;
        this.timeList = timeList;
    }

    public void setMarker(MyLeftMarkerView markerLeft, MyBottomMarkerView markerBottom, List<String> timeList) {
//        this.myMarkerViewLeft = markerLeft;
//        this.myBottomMarkerView = markerBottom;
        this.timeList = timeList;
    }

    public void setMarker(MyLeftMarkerView markerLeft, MyBottomMarkerView markerBottom, MyRightMarkerView markerH, List<String> timeList) {
//        this.myMarkerViewLeft = markerLeft;
//        this.myBottomMarkerView = markerBottom;
//        this.myMarkerViewRight = markerH;
        this.timeList = timeList;
    }

    public void setMarker(MyLeftMarkerView markerLeft, MyBottomMarkerView markerBottom, MyHMarkerView markerH, List<String> timeList) {
//        this.myMarkerViewLeft = markerLeft;
//        this.myBottomMarkerView = markerBottom;
//        this.myMarkerViewH = markerH;
        this.timeList = timeList;
    }

    @Override
    protected void drawMarkers(Canvas canvas) {
        if (Build.VERSION.SDK_INT >= 21) {
            drawableHotspotChanged(200, 200);
        }
    }
}
