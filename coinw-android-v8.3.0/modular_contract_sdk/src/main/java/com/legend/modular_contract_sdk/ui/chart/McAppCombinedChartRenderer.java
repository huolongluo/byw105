package com.legend.modular_contract_sdk.ui.chart;

import android.graphics.Canvas;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.renderer.BubbleChartRenderer;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.github.mikephil.charting.renderer.ScatterChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
/**
 * Renderer class that is responsible for rendering multiple different data-types.
 */
public class McAppCombinedChartRenderer extends DataRenderer {

    /**
     * all rederers for the different kinds of data this combined-renderer can draw
     */
    protected List<DataRenderer> mRenderers = new ArrayList<DataRenderer>(5);

    protected WeakReference<Chart> mChart;

    public McAppCombinedChartRenderer(CombinedChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = new WeakReference<Chart>(chart);
        createRenderers();
    }

    /**
     * Creates the renderers needed for this combined-renderer in the required order. Also takes the DrawOrder into
     * consideration.
     */
    public void createRenderers() {

        mRenderers.clear();

        CombinedChart chart = (CombinedChart) mChart.get();
        if (chart == null)
            return;

        DrawOrder[] orders = chart.getDrawOrder();

        for (DrawOrder order : orders) {

            switch (order) {
                case BAR:
                    if (chart.getBarData() != null)
                        mRenderers.add(new McBarChartRenderer(chart, mAnimator, mViewPortHandler));
                    break;
                case BUBBLE:
                    if (chart.getBubbleData() != null)
                        mRenderers.add(new BubbleChartRenderer(chart, mAnimator, mViewPortHandler));
                    break;
                case LINE:
                    if (chart.getLineData() != null)
                        mRenderers.add(new McAppLineChartRenderer(chart, mAnimator, mViewPortHandler));
                    break;
                case CANDLE:
                    if (chart.getCandleData() != null)
                        mRenderers.add(new McCandleStickChartRenderer(chart, mAnimator, mViewPortHandler));
                    break;
                case SCATTER:
                    if (chart.getScatterData() != null)
                        mRenderers.add(new ScatterChartRenderer(chart, mAnimator, mViewPortHandler));
                    break;
            }
        }
    }

    @Override
    public void initBuffers() {

        //12-29 11:31:02.966 E/AndroidRuntime( 5531): FATAL EXCEPTION: main
        //12-29 11:31:02.966 E/AndroidRuntime( 5531): Process: huolongluo.byw, PID: 5531
        //12-29 11:31:02.966 E/AndroidRuntime( 5531): java.util.ConcurrentModificationException
        //12-29 11:31:02.966 E/AndroidRuntime( 5531):     at java.util.ArrayList$Itr.next(ArrayList.java:860)
        //12-29 11:31:02.966 E/AndroidRuntime( 5531):     at huolongluo.byw.view.kline.AppCombinedChartRenderer.initBuffers(AppCombinedChartRenderer.java:82)
        if(mRenderers!=null) {
            for (DataRenderer renderer : mRenderers)
                renderer.initBuffers();
        }
    }

    @Override
    public void drawData(Canvas c) {
        try {
            for (DataRenderer renderer : mRenderers)
                renderer.drawData(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawValues(Canvas c) {
        try {
            for (DataRenderer renderer : mRenderers)
                renderer.drawValues(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawExtras(Canvas c) {
        try {
            for (DataRenderer renderer : mRenderers)
                renderer.drawExtras(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected List<Highlight> mHighlightBuffer = new ArrayList<Highlight>();

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        Chart chart = mChart.get();
        if (chart == null) return;

        for (DataRenderer renderer : mRenderers) {
            ChartData data = null;

            if (renderer instanceof McBarChartRenderer)
                data = ((McBarChartRenderer) renderer).mChart.getBarData();
            else if (renderer instanceof McAppLineChartRenderer)
                data = ((McAppLineChartRenderer) renderer).mChart.getLineData();
            else if (renderer instanceof McCandleStickChartRenderer)
                data = ((McCandleStickChartRenderer) renderer).mChart.getCandleData();
            else if (renderer instanceof ScatterChartRenderer)
                data = ((ScatterChartRenderer) renderer).mChart.getScatterData();
            else if (renderer instanceof BubbleChartRenderer)
                data = ((BubbleChartRenderer) renderer).mChart.getBubbleData();

            int dataIndex = data == null ? -1
                    : ((CombinedData) chart.getData()).getAllData().indexOf(data);

            mHighlightBuffer.clear();

            for (Highlight h : indices) {
                if (h.getDataIndex() == dataIndex || h.getDataIndex() == -1)
                    mHighlightBuffer.add(h);
            }

            renderer.drawHighlighted(c, mHighlightBuffer.toArray(new Highlight[mHighlightBuffer.size()]));
        }
    }

    /**
     * Returns the sub-renderer object at the specified index.
     */
    public DataRenderer getSubRenderer(int index) {
        if (index >= mRenderers.size() || index < 0)
            return null;
        else
            return mRenderers.get(index);
    }

    /**
     * Returns all sub-renderers.
     */
    public List<DataRenderer> getSubRenderers() {
        return mRenderers;
    }

    public void setSubRenderers(List<DataRenderer> renderers) {
        this.mRenderers = renderers;
    }
}
