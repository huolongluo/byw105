package huolongluo.byw.view.kline;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.Utils;

import huolongluo.byw.R;
/**
 * Created by dell on 2017/6/22.
 */

public class AppCombinedChart extends CombinedChart {

    private IMarker mXMarker;

    private float mYCenter;

    /**
     * 是否显示交易量图表
     */
    private boolean mIsShowBar = true;

    private boolean mIsShowSubIndex = false;

    private boolean mIsFullScreen = false;

    private OnLastPointChangeListener mOnLastPointChangeListener;

    public AppCombinedChart(Context context) {
        this(context, null);
    }

    public AppCombinedChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppCombinedChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        mRenderer = new AppCombinedChartRenderer(this, mAnimator, mViewPortHandler);
    }


    public void setShowBar(boolean isShowBar) {
        this.mIsShowBar = isShowBar;
    }

    public void setShowSubIndex(boolean isShowSubIndex) {
        this.mIsShowSubIndex = isShowSubIndex;
    }

    public void setFullScreen(boolean isFullScreen) {
        this.mIsFullScreen = isFullScreen;
    }

    public void setXMarker(IMarker marker) {
        mXMarker = marker;
    }

    public void setOnLastPointChangeListener(OnLastPointChangeListener listener) {
        mOnLastPointChangeListener = listener;
    }

    public OnLastPointChangeListener getOnLastPointChangeListener() {
        return mOnLastPointChangeListener;
    }

    @Override
    public void setData(CombinedData data) {
        try {
            super.setData(data);
        } catch (ClassCastException e) {
            // ignore
        }
        ((AppCombinedChartRenderer) mRenderer).createRenderers();
        mRenderer.initBuffers();
    }

    @Override
    protected void drawMarkers(Canvas canvas) {
        if (mMarker == null || mXMarker == null || !isDrawMarkersEnabled() || !valuesToHighlight())
            return;

        for (int i = 0; i < mIndicesToHighlight.length; i++) {

            Highlight highlight = mIndicesToHighlight[i];

            IDataSet set = mData.getDataSetByIndex(highlight.getDataSetIndex());

            Entry e = mData.getEntryForHighlight(mIndicesToHighlight[i]);
            int entryIndex = set.getEntryIndex(e);

            // make sure entry not null
            if (e == null || entryIndex > set.getEntryCount() * mAnimator.getPhaseX())
                continue;

            float[] pos = getMarkerPosition(highlight);

            // check bounds
            if (!mViewPortHandler.isInBounds(pos[0], pos[1]))
                continue;

            // callbacks to update the content
            mMarker.refreshContent(e, highlight);
            mXMarker.refreshContent(e, highlight);

            // draw the marker
//            if (mMarker instanceof LineChartYMarkerView) {
            LineChartYMarkerView yMarker = (LineChartYMarkerView) mMarker;
            LineChartXMarkerView xMarker = (LineChartXMarkerView) mXMarker;
            float width = mViewPortHandler.offsetRight();
            mMarker.draw(canvas, mViewPortHandler.getChartWidth() - width, pos[1] - yMarker.getMeasuredHeight() / 2F);

            if (mIsFullScreen) {
                int chartHeight;
                if (mIsShowSubIndex) {
                    if (mIsShowBar) {
                        chartHeight = 2 * getResources().getDimensionPixelSize(R.dimen.bottom_chart_height);
                    } else {
                        chartHeight = getResources().getDimensionPixelSize(R.dimen.bottom_chart_height);
                    }
                } else {
                    if (mIsShowBar) {
                        chartHeight = getResources().getDimensionPixelSize(R.dimen.bottom_chart_height);
                    } else {
                        chartHeight = 0;
                    }
                }
                mXMarker.draw(canvas, pos[0] - (xMarker.getMeasuredWidth() / 2F),
                        mViewPortHandler.getChartHeight() - xMarker.getMeasuredHeight() - chartHeight);
            } else {
                mXMarker.draw(canvas, pos[0] - (xMarker.getMeasuredWidth() / 2F),
                        mViewPortHandler.getChartHeight() - xMarker.getMeasuredHeight());
            }
//            } else {
//                mMarker.draw(canvas, pos[0], pos[1]);
//            }
        }
    }


    /**
     * 重写这两个方法，为了让开盘价和涨跌幅剧中显示
     * Performs auto scaling of the axis by recalculating the minimum and maximum y-values based on the entries currently in view.
     */
    @Override
    protected void autoScale() {
        try {
            final float fromX = getLowestVisibleX();
            final float toX = getHighestVisibleX();

            mData.calcMinMaxY(fromX, toX);

            mXAxis.calculate(mData.getXMin(), mData.getXMax());

            // calculate axis range (min / max) according to provided data

            if (mAxisLeft.isEnabled()) {
                if (mYCenter == 0) {
                    mAxisLeft.calculate(mData.getYMin(YAxis.AxisDependency.LEFT),
                            mData.getYMax(YAxis.AxisDependency.LEFT));
                } else {
                    float yMin = mData.getYMin(YAxis.AxisDependency.LEFT);
                    float yMax = mData.getYMax(YAxis.AxisDependency.LEFT);
                    float interval = (float) Math.max(Math.abs(mYCenter - yMax), Math.abs(mYCenter - yMin));
                    yMax = (float) Math.max(yMax, (mYCenter + interval));
                    yMin = (float) Math.min(yMin, (mYCenter - interval));
                    mAxisLeft.calculate(yMin, yMax);
                }
            }

            if (mAxisRight.isEnabled()) {
                if (mYCenter == 0) {
                    mAxisRight.calculate(mData.getYMin(YAxis.AxisDependency.RIGHT),
                            mData.getYMax(YAxis.AxisDependency.RIGHT));
                } else {
                    float yMin = mData.getYMin(YAxis.AxisDependency.RIGHT);
                    float yMax = mData.getYMax(YAxis.AxisDependency.RIGHT);
                    float interval = (float) Math.max(Math.abs(mYCenter - yMax), Math.abs(mYCenter - yMin));
                    yMax = (float) Math.max(yMax, (mYCenter + interval));
                    yMin = (float) Math.min(yMin, (mYCenter - interval));
                    mAxisRight.calculate(yMin, yMax);
                }
            }

            calculateOffsets();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重写这两个方法，为了让开盘价和涨跌幅剧中显示
     */
    @Override
    protected void calcMinMax() {

        mXAxis.calculate(mData.getXMin(), mData.getXMax());

        if (mYCenter == 0) {
            // calculate axis range (min / max) according to provided data
            mAxisLeft.calculate(mData.getYMin(YAxis.AxisDependency.LEFT), mData.getYMax(YAxis.AxisDependency.LEFT));
            mAxisRight.calculate(mData.getYMin(YAxis.AxisDependency.RIGHT), mData.getYMax(YAxis.AxisDependency
                    .RIGHT));
        } else {
            float yLMin = mData.getYMin(YAxis.AxisDependency.LEFT);
            float yLMax = mData.getYMax(YAxis.AxisDependency.LEFT);
            float interval = (float) Math.max(Math.abs(mYCenter - yLMax), Math.abs(mYCenter - yLMin));
            yLMax = (float) Math.max(yLMax, (mYCenter + interval));
            yLMin = (float) Math.min(yLMin, (mYCenter - interval));
            mAxisLeft.calculate(yLMin, yLMax);

            float yRMin = mData.getYMin(YAxis.AxisDependency.RIGHT);
            float yRMax = mData.getYMax(YAxis.AxisDependency.RIGHT);
            float rinterval = (float) Math.max(Math.abs(mYCenter - yRMax), Math.abs(mYCenter - yRMin));
            yRMax = (float) Math.max(yRMax, (mYCenter + rinterval));
            yRMin = (float) Math.min(yRMin, (mYCenter - rinterval));
            mAxisRight.calculate(yRMin, yRMax);
        }
    }

    /**
     * 设置图表中Y居中的值
     */
    public void setYCenter(float YCenter) {
        mYCenter = YCenter;
    }

}
