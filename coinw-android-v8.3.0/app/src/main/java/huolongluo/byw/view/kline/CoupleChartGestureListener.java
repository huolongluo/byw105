package huolongluo.byw.view.kline;
import android.graphics.Matrix;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
/**
 * http://stackoverflow.com/questions/28521004/mpandroidchart-have-one-graph-mirror-the-zoom-swipes-on-a-sister-graph
 */
public class CoupleChartGestureListener implements OnChartGestureListener {
    private static final String TAG = CoupleChartGestureListener.class.getSimpleName();
    private BarLineChartBase srcChart;
    private Chart[] dstCharts;
    /**
     * 加载更多的间隔
     */
    public static final int LOAD_MORE_INTERVAL = 1000;
    private long mLastLoadMoreTime;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoadMore = false;
    private OnAxisChangeListener listener;

    public CoupleChartGestureListener(OnAxisChangeListener listener, BarLineChartBase srcChart, Chart... dstCharts) {
        this(srcChart, dstCharts);
        this.listener = listener;
    }

    public CoupleChartGestureListener(BarLineChartBase srcChart, Chart... dstCharts) {
        this.srcChart = srcChart;
        this.dstCharts = dstCharts;
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        syncCharts();
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        syncCharts();
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        syncCharts();
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        syncCharts();
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        syncCharts();
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
//        if (listener != null) {
//            listener.onAxisChange(srcChart);
//        }
//        performLoadMore();
        syncCharts();
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
//        Log.d(TAG, "onChartScale " + scaleX + "/" + scaleY + " X=" + me.getX() + "Y=" + me.getY());
        if (listener != null) {
            listener.onAxisChange(srcChart);
        }
        performLoadMore();
        syncCharts();
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
//        Log.d(TAG, "onChartTranslate " + dX + "/" + dY + " X=" + me.getX() + "Y=" + me.getY());
//        Log.d(TAG, "getHighestVisibleX  " +srcChart.getHighestVisibleX());
        if (listener != null) {
            listener.onAxisChange(srcChart);
        }
        performLoadMore();
        syncCharts();
    }

    public void syncCharts() {
        Matrix srcMatrix;
        float[] srcVals = new float[9];
        Matrix dstMatrix;
        float[] dstVals = new float[9];
        // get src chart translation matrix:
        srcMatrix = srcChart.getViewPortHandler().getMatrixTouch();
        srcMatrix.getValues(srcVals);
        // apply X axis scaling and position to dst charts:
        for (Chart dstChart : dstCharts) {
            dstMatrix = dstChart.getViewPortHandler().getMatrixTouch();
            dstMatrix.getValues(dstVals);
            dstVals[Matrix.MSCALE_X] = srcVals[Matrix.MSCALE_X];
            dstVals[Matrix.MSKEW_X] = srcVals[Matrix.MSKEW_X];
            dstVals[Matrix.MTRANS_X] = srcVals[Matrix.MTRANS_X];
            dstVals[Matrix.MSKEW_Y] = srcVals[Matrix.MSKEW_Y];
            dstVals[Matrix.MSCALE_Y] = srcVals[Matrix.MSCALE_Y];
            dstVals[Matrix.MTRANS_Y] = srcVals[Matrix.MTRANS_Y];
            dstVals[Matrix.MPERSP_0] = srcVals[Matrix.MPERSP_0];
            dstVals[Matrix.MPERSP_1] = srcVals[Matrix.MPERSP_1];
            dstVals[Matrix.MPERSP_2] = srcVals[Matrix.MPERSP_2];
            dstMatrix.setValues(dstVals);
            dstChart.getViewPortHandler().refresh(dstMatrix, dstChart, true);
        }
    }

    private void performLoadMore() {
        // 加载更多
        if (mOnLoadMoreListener != null && !isLoadMore && System.currentTimeMillis() - mLastLoadMoreTime > LOAD_MORE_INTERVAL) {
            if (srcChart.getLowestVisibleX() == srcChart.getXAxis().mAxisMinimum) {
                isLoadMore = true;
                mOnLoadMoreListener.onLoadMore();
            }
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public void loadMoreComplete() {
        isLoadMore = false;
        mLastLoadMoreTime = System.currentTimeMillis();
    }

    public interface OnAxisChangeListener {
        void onAxisChange(BarLineChartBase chart);
    }
}
