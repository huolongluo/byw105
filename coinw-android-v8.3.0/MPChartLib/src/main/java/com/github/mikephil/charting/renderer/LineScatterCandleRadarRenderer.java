package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.interfaces.datasets.ILineScatterCandleRadarDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Philipp Jahoda on 11/07/15.
 */
public abstract class LineScatterCandleRadarRenderer extends BarLineScatterCandleBubbleRenderer {

    /**
     * path that is used for drawing highlight-lines (drawLines(...) cannot be used because of dashes)
     */
    protected Path mHighlightLinePath = new Path();

    protected Paint mLastPriceTextPaint;
    protected Paint mLastPriceLinePaint;
    protected Paint mLastPriceBorderPaint;
    protected Path mLastPriceArrowPath = new Path();

    public LineScatterCandleRadarRenderer(ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mLastPriceTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLastPriceTextPaint.setStyle(Paint.Style.FILL);
        mLastPriceTextPaint.setTextAlign(Paint.Align.CENTER);

        mLastPriceLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLastPriceLinePaint.setStyle(Paint.Style.STROKE);
        mLastPriceLinePaint.setPathEffect(new DashPathEffect(new float[] {10, 5}, 0));
        mLastPriceLinePaint.setStrokeWidth(Utils.convertDpToPixel(0.34F));

        mLastPriceBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLastPriceBorderPaint.setStyle(Paint.Style.STROKE);
        mLastPriceBorderPaint.setStrokeWidth(Utils.convertDpToPixel(0.34F));
    }

    /**
     * Draws vertical & horizontal highlight-lines if enabled.
     *
     * @param c
     * @param x x-position of the highlight line intersection
     * @param y y-position of the highlight line intersection
     * @param set the currently drawn dataset
     */
    protected void drawHighlightLines(Canvas c, float x, float y, ILineScatterCandleRadarDataSet set) {


        // draw highlighted lines (if enabled)
        mHighlightPaint.setPathEffect(set.getDashPathEffectHighlight());
        mHighlightPaint.setStyle(Paint.Style.STROKE);

        // draw vertical highlight lines
        if (set.isVerticalHighlightIndicatorEnabled()) {

            // set color and stroke-width
            mHighlightPaint.setColor(set.getHighLightColor());
            mHighlightPaint.setStrokeWidth(set.getHighlightLineWidth());

            // create vertical path
            mHighlightLinePath.reset();
//            mHighlightLinePath.moveTo(x, mViewPortHandler.contentTop());
//            mHighlightLinePath.lineTo(x, mViewPortHandler.contentBottom());
            mHighlightLinePath.moveTo(x, 0);
            mHighlightLinePath.lineTo(x, mViewPortHandler.getChartHeight());

            c.drawPath(mHighlightLinePath, mHighlightPaint);
        }

        // draw horizontal highlight lines
        if (set.isHorizontalHighlightIndicatorEnabled()) {

            // set color and stroke-width
            mHighlightPaint.setColor(set.getHighLightHorizontalColor());
            mHighlightPaint.setStrokeWidth(set.getHighlightHorizontalLineWidth());

            // create horizontal path
            mHighlightLinePath.reset();
            mHighlightLinePath.moveTo(mViewPortHandler.contentLeft(), y);
            mHighlightLinePath.lineTo(mViewPortHandler.contentRight(), y);

            c.drawPath(mHighlightLinePath, mHighlightPaint);
            mHighlightPaint.setStyle(Paint.Style.FILL);
            c.drawCircle(x, y, Utils.convertDpToPixel(2), mHighlightPaint);
        }
    }
}
