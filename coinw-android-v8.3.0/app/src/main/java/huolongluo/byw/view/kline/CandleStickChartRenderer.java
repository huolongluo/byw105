package huolongluo.byw.view.kline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineScatterCandleRadarDataSet;
import com.github.mikephil.charting.renderer.LineScatterCandleRadarRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.math.BigDecimal;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.util.noru.NorUtils;
public class CandleStickChartRenderer extends LineScatterCandleRadarRenderer {

    public CandleDataProvider mChart;

    private float[] mShadowBuffers = new float[8];
    private float[] mBodyBuffers = new float[4];
    private float[] mRangeBuffers = new float[4];
    private float[] mOpenBuffers = new float[4];
    private float[] mCloseBuffers = new float[4];

    protected Paint mLastPricePaint;

    public CandleStickChartRenderer(CandleDataProvider chart, ChartAnimator animator,
                                    ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mLastPricePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLastPricePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mLastPricePaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void initBuffers() {

    }

    @Override
    public void drawData(Canvas c) {

        CandleData candleData = mChart.getCandleData();

        for (ICandleDataSet set : candleData.getDataSets()) {

            if (set.isVisible())
                drawDataSet(c, set);
        }
    }

    @SuppressWarnings("ResourceAsColor")
    protected void drawDataSet(Canvas c, ICandleDataSet dataSet) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseY = mAnimator.getPhaseY();
        float barSpace = dataSet.getBarSpace();
        boolean showCandleBar = dataSet.getShowCandleBar();

        mXBounds.set(mChart, dataSet);

        mRenderPaint.setStrokeWidth(dataSet.getShadowWidth());

        // draw the body
        for (int j = mXBounds.min; j <= mXBounds.range + mXBounds.min; j++) {

            // get the entry
            CandleEntry e = dataSet.getEntryForIndex(j);

            if (e == null)
                continue;

            final float xPos = e.getX();

            final float open = e.getOpen();
            final float close = e.getClose();
            final float high = e.getHigh();
            final float low = e.getLow();

            if (showCandleBar) {
                // calculate the shadow

                mShadowBuffers[0] = xPos;
                mShadowBuffers[2] = xPos;
                mShadowBuffers[4] = xPos;
                mShadowBuffers[6] = xPos;

                if (open > close) {
                    mShadowBuffers[1] = high * phaseY;
                    mShadowBuffers[3] = open * phaseY;
                    mShadowBuffers[5] = low * phaseY;
                    mShadowBuffers[7] = close * phaseY;
                } else if (open < close) {
                    mShadowBuffers[1] = high * phaseY;
                    mShadowBuffers[3] = close * phaseY;
                    mShadowBuffers[5] = low * phaseY;
                    mShadowBuffers[7] = open * phaseY;
                } else {
                    mShadowBuffers[1] = high * phaseY;
                    mShadowBuffers[3] = open * phaseY;
                    mShadowBuffers[5] = low * phaseY;
                    mShadowBuffers[7] = mShadowBuffers[3];
                }

                trans.pointValuesToPixel(mShadowBuffers);

                // draw the shadows

                if (dataSet.getShadowColorSameAsCandle()) {

                    if (open > close)
                        mRenderPaint.setColor(
                                dataSet.getDecreasingColor() == ColorTemplate.COLOR_NONE ?
                                        dataSet.getColor(j) :
                                        dataSet.getDecreasingColor()
                        );

                    else if (open < close)
                        mRenderPaint.setColor(
                                dataSet.getIncreasingColor() == ColorTemplate.COLOR_NONE ?
                                        dataSet.getColor(j) :
                                        dataSet.getIncreasingColor()
                        );

                    else
                        mRenderPaint.setColor(
                                dataSet.getNeutralColor() == ColorTemplate.COLOR_NONE ?
                                        dataSet.getColor(j) :
                                        dataSet.getNeutralColor()
                        );

                } else {
                    mRenderPaint.setColor(
                            dataSet.getShadowColor() == ColorTemplate.COLOR_NONE ?
                                    dataSet.getColor(j) :
                                    dataSet.getShadowColor()
                    );
                }

                mRenderPaint.setStyle(Paint.Style.STROKE);

                c.drawLines(mShadowBuffers, mRenderPaint);

                // calculate the body

                mBodyBuffers[0] = xPos - 0.5f + barSpace;
                mBodyBuffers[1] = close * phaseY;
                mBodyBuffers[2] = (xPos + 0.5f - barSpace);
                mBodyBuffers[3] = open * phaseY;

                trans.pointValuesToPixel(mBodyBuffers);

                // draw body differently for increasing and decreasing entry
                if (open > close) { // decreasing

                    if (dataSet.getDecreasingColor() == ColorTemplate.COLOR_NONE) {
                        mRenderPaint.setColor(dataSet.getColor(j));
                    } else {
                        mRenderPaint.setColor(dataSet.getDecreasingColor());
                    }

                    mRenderPaint.setStyle(dataSet.getDecreasingPaintStyle());

                    c.drawRect(
                            mBodyBuffers[0], mBodyBuffers[3],
                            mBodyBuffers[2], mBodyBuffers[1],
                            mRenderPaint);

                } else if (open < close) {

                    if (dataSet.getIncreasingColor() == ColorTemplate.COLOR_NONE) {
                        mRenderPaint.setColor(dataSet.getColor(j));
                    } else {
                        mRenderPaint.setColor(dataSet.getIncreasingColor());
                    }

                    mRenderPaint.setStyle(dataSet.getIncreasingPaintStyle());

                    c.drawRect(
                            mBodyBuffers[0], mBodyBuffers[1],
                            mBodyBuffers[2], mBodyBuffers[3],
                            mRenderPaint);
                } else { // equal values

                    if (dataSet.getNeutralColor() == ColorTemplate.COLOR_NONE) {
                        mRenderPaint.setColor(dataSet.getColor(j));
                    } else {
                        mRenderPaint.setColor(dataSet.getNeutralColor());
                    }

                    c.drawLine(
                            mBodyBuffers[0], mBodyBuffers[1],
                            mBodyBuffers[2], mBodyBuffers[3],
                            mRenderPaint);
                }
            } else {

                mRangeBuffers[0] = xPos;
                mRangeBuffers[1] = high * phaseY;
                mRangeBuffers[2] = xPos;
                mRangeBuffers[3] = low * phaseY;

                mOpenBuffers[0] = xPos - 0.5f + barSpace;
                mOpenBuffers[1] = open * phaseY;
                mOpenBuffers[2] = xPos;
                mOpenBuffers[3] = open * phaseY;

                mCloseBuffers[0] = xPos + 0.5f - barSpace;
                mCloseBuffers[1] = close * phaseY;
                mCloseBuffers[2] = xPos;
                mCloseBuffers[3] = close * phaseY;

                trans.pointValuesToPixel(mRangeBuffers);
                trans.pointValuesToPixel(mOpenBuffers);
                trans.pointValuesToPixel(mCloseBuffers);

                // draw the ranges
                int barColor;

                if (open > close)
                    barColor = dataSet.getDecreasingColor() == ColorTemplate.COLOR_NONE
                            ? dataSet.getColor(j)
                            : dataSet.getDecreasingColor();
                else if (open < close)
                    barColor = dataSet.getIncreasingColor() == ColorTemplate.COLOR_NONE
                            ? dataSet.getColor(j)
                            : dataSet.getIncreasingColor();
                else
                    barColor = dataSet.getNeutralColor() == ColorTemplate.COLOR_NONE
                            ? dataSet.getColor(j)
                            : dataSet.getNeutralColor();

                mRenderPaint.setColor(barColor);
                c.drawLine(
                        mRangeBuffers[0], mRangeBuffers[1],
                        mRangeBuffers[2], mRangeBuffers[3],
                        mRenderPaint);
                c.drawLine(
                        mOpenBuffers[0], mOpenBuffers[1],
                        mOpenBuffers[2], mOpenBuffers[3],
                        mRenderPaint);
                c.drawLine(
                        mCloseBuffers[0], mCloseBuffers[1],
                        mCloseBuffers[2], mCloseBuffers[3],
                        mRenderPaint);
            }
        }
    }

   /* @Override
    public void drawValues(Canvas c) {

        // if values are drawn
        if (isDrawingValuesAllowed(mChart)) {

            List<ICandleDataSet> dataSets = mChart.getCandleData().getDataSets();

            for (int i = 0; i < dataSets.size(); i++) {

                ICandleDataSet dataSet = dataSets.get(i);

                if (!shouldDrawValues(dataSet))
                    continue;

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet);

                Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

                mXBounds.set(mChart, dataSet);

                float[] positions = trans.generateTransformedValuesCandle(
                        dataSet, mAnimator.getPhaseX(), mAnimator.getPhaseY(), mXBounds.min, mXBounds.max);

                float yOffset = Utils.convertDpToPixel(5f);

                MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
                iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x);
                iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y);

                for (int j = 0; j < positions.length; j += 2) {

                    float x = positions[j];
                    float y = positions[j + 1];

                    if (!mViewPortHandler.isInBoundsRight(x))
                        break;

                    if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y))
                        continue;

                    CandleEntry entry = dataSet.getEntryForIndex(j / 2 + mXBounds.min);

                    if (dataSet.isDrawValuesEnabled()) {
                        drawValue(c,
                                dataSet.getValueFormatter(),
                                entry.getHigh(),
                                entry,
                                i,
                                x,
                                y - yOffset,
                                dataSet
                                        .getValueTextColor(j / 2));
                    }

                    if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {

                        Drawable icon = entry.getIcon();

                        Utils.drawImage(
                                c,
                                icon,
                                (int)(x + iconsOffset.x),
                                (int)(y + iconsOffset.y),
                                icon.getIntrinsicWidth(),
                                icon.getIntrinsicHeight());
                    }
                }

                MPPointF.recycleInstance(iconsOffset);
            }
        }
    }*/

    @Override
    public void drawValues(Canvas c) {

        CandleData data = mChart.getCandleData();
        List<ICandleDataSet> dataSets = data.getDataSets();

        int digit = data.getDigit();

        for (int i = 0; i < dataSets.size(); i++) {

            ICandleDataSet dataSet = dataSets.get(i);

            if (!dataSet.isDrawValuesEnabled() || dataSet.getEntryCount() == 0)
                continue;

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

            int minx = (int) Math.max(dataSet.getXMin(), 0);
            int maxx = (int) Math.min(dataSet.getXMax(), dataSet.getEntryCount() - 1);

            float[] positions = trans.generateTransformedValuesCandle(
                    dataSet, mAnimator.getPhaseX(), mAnimator.getPhaseY(), minx, maxx);


            //计算最大值和最小值
            float maxValue = 0, minValue = 0;
            int maxIndex = 0, minIndex = 0;
            // 计算最后的一个价格
            float lastValue = 0;
            int lastIndex = dataSet.getEntryCount() - 1;
            CandleEntry lastEntry = dataSet.getEntryForIndex(lastIndex);
            if (lastEntry != null) {
                lastValue = lastEntry.getClose();
            }
            boolean firstInit = true;
            for (int j = 0; j < positions.length; j += 2) {

                float x = positions[j];
                float y = positions[j + 1];

                if (!mViewPortHandler.isInBoundsRight(x))
                    break;

                // 这里不需要判断是否bound y
                if (!mViewPortHandler.isInBoundsLeft(x) /*|| !mViewPortHandler.isInBoundsY(y)*/)
                    continue;

                CandleEntry entry = dataSet.getEntryForIndex(j / 2 + minx);
                if (entry == null) {
                    continue;
                }

                if (firstInit) {
                    maxValue = entry.getHigh();
                    minValue = entry.getLow();
                    minIndex = j;
                    maxIndex = j;
                    firstInit = false;
                } else {
                    if (entry.getHigh() > maxValue) {
                        maxValue = entry.getHigh();
                        maxIndex = j;
                    }

                    if (entry.getLow() < minValue) {
                        minValue = entry.getLow();
                        minIndex = j;
                    }

                }
            }

            mValuePaint.setTextSize(dataSet.getValueTextSize());
            mValuePaint.setColor(dataSet.getValueTextColor());
            mValuePaint.setStrokeWidth(Utils.convertDpToPixel(0.34F));
            int lineWidth = (int) Utils.convertDpToPixel(15);

//            int offsetX = (int) Utils.convertDpToPixel(1);
//            int offsetY = (int) Utils.convertDpToPixel(1);
            int offsetX = 0;
            int offsetY = 0;


            //绘制最小值
            float minX = positions[minIndex];
            float[] minFloats = {minIndex, minValue};
            trans.pointValuesToPixel(minFloats);
            float minY = minFloats[1];
            double minValueD=new BigDecimal(minValue+"").doubleValue();
            String minString = NorUtils.NumberFormat(digit).format(minValueD);
            //计算显示位置
            int minStringWidth = Utils.calcTextWidth(mValuePaint, minString);
            int minStringHeight = Utils.calcTextHeight(mValuePaint, minString);

            float minTextXRight = minX + lineWidth + minStringWidth;
            if (minTextXRight < (c.getWidth() / 2F)) {
                float startY = minY + offsetY;
                c.drawLine(minX, startY, minX + lineWidth, startY, mValuePaint);
                c.drawText(minString, minX + lineWidth + minStringWidth / 2F + offsetX, minY + minStringHeight / 2F + offsetY, mValuePaint);
            } else {
                float startY = minY + offsetY;
                c.drawLine(minX - lineWidth, startY, minX, startY, mValuePaint);
                c.drawText(minString, minX - lineWidth - minStringWidth / 2F - offsetX, minY + minStringHeight / 2F + offsetY, mValuePaint);
            }


            float maxX = positions[maxIndex];
            float[] maxFloats = {maxIndex, maxValue};
            trans.pointValuesToPixel(maxFloats);
            float maxY = maxFloats[1];
            double maxValueD=new BigDecimal(maxValue+"").doubleValue();
            String maxString =NorUtils.NumberFormat(digit).format(maxValueD);

            int maxStringWidth = Utils.calcTextWidth(mValuePaint, maxString);
            int maxStringHeight = Utils.calcTextHeight(mValuePaint, maxString);

            float maxTextXLeft = maxX - lineWidth - maxStringWidth;


            if (maxTextXLeft > (c.getWidth() / 2F)) {
                float startY = Math.max(maxStringHeight / 2F, maxY - offsetY);
                c.drawLine(maxX - lineWidth, startY, maxX, startY, mValuePaint);
                c.drawText(maxString, maxX - lineWidth - maxStringWidth / 2F - offsetX, Math.max(maxStringHeight, maxY + maxStringHeight / 2F - offsetY), mValuePaint);
            } else {
                float startY = Math.max(maxStringHeight / 2F, maxY - offsetY);
                c.drawLine(maxX, startY, maxX + lineWidth, startY, mValuePaint);
                c.drawText(maxString, maxX + lineWidth + maxStringWidth / 2F + offsetX, Math.max(maxStringHeight, maxY + maxStringHeight / 2F - offsetY), mValuePaint);
            }

            mValuePaint.setTextSize(dataSet.getValueTextSize());
            mValuePaint.setColor(dataSet.getValueTextColor());
            mValuePaint.setStrokeWidth(Utils.convertDpToPixel(0.34F));

            drawLastValue(c, digit, dataSet, trans, lastValue, lastIndex);
        }
    }

    private void drawLastValue(Canvas c, int digit, ILineScatterCandleRadarDataSet dataSet, Transformer trans, float lastValue, int lastIndex) {
        // 绘制最后一个点的价格线
        if (lastValue != 0) {
            mLastPriceTextPaint.setTextSize(dataSet.getValueTextSize());
            mLastPriceTextPaint.setTypeface(dataSet.getValueTypeface());
            double lastValueD=new BigDecimal(lastValue+"").doubleValue();
            String lastString = NorUtils.NumberFormat(digit).format(lastValueD);
            int lastStringWidth = Utils.calcTextWidth(mLastPriceTextPaint, lastString);
            int lastStringHeight = Utils.calcTextHeight(mLastPriceTextPaint, lastString);
            float[] lastFloats = {lastIndex, lastValue};
            trans.pointValuesToPixel(lastFloats);
            float lastX = lastFloats[0];
            float lastY = Math.min(lastFloats[1], c.getHeight() - mViewPortHandler.offsetBottom());
            Context context = ((ViewGroup) mChart).getContext();
            float padding = Utils.convertDpToPixel(4);
            if (mViewPortHandler.isInBoundsRight(lastX + lastStringWidth + padding)) { // 最后一个点是否在画布范围内
                // 绘制线
                float lineY = Math.max(lastY, (lastStringHeight + padding) / 2);
                // 在范围内
                mLastPriceLinePaint.setColor(ContextCompat.getColor(context, R.color.last_value_color));
                c.drawLine(lastX, lineY, c.getWidth() - lastStringWidth - padding, lineY, mLastPriceLinePaint);
                mScrollToRightRect.set(0, 0, 0, 0);
                // 绘制值
                mLastPriceTextPaint.setColor(ContextCompat.getColor(context, R.color.chart_background));
                c.drawRect(
                        c.getWidth() - lastStringWidth - padding,
                        lastY - (lastStringHeight + padding) / 2F,
                        c.getWidth(),
                        lastY + (lastStringHeight + padding) / 2F,
                        mLastPriceTextPaint);
                mLastPriceTextPaint.setColor(ContextCompat.getColor(context, R.color.last_value_color));
                c.drawText(lastString,
                        c.getWidth() - lastStringWidth / 2F - padding,
                        Math.max(lastStringHeight + padding / 2, lastY + lastStringHeight / 2F),
                        mLastPriceTextPaint);
            } else {
                padding = Utils.convertDpToPixel(8);
                // 绘制线
                float lineY = Math.max(lastY, (lastStringHeight + padding) / 2);
                mLastPriceLinePaint.setColor(ContextCompat.getColor(context, R.color.axis_color));
                c.drawLine(0, lineY, c.getWidth(), lineY, mLastPriceLinePaint);
                // 绘制背景
                mLastPriceTextPaint.setColor(ContextCompat.getColor(context, R.color.marker_color2));
                float left = (c.getWidth() - lastStringWidth - padding) * 0.7F;
                RectF rectF = new RectF(left,
                        Math.max(0, lastY - padding / 2F - lastStringHeight / 2F),
                        left + lastStringWidth + 2 * padding,
                        Math.max(lastStringHeight + padding, lastY + padding / 2F + lastStringHeight / 2F));
                mScrollToRightRect.set(rectF);
                c.drawRoundRect(rectF, 4, 4, mLastPriceTextPaint);
                // 绘制值
                mLastPriceTextPaint.setColor(dataSet.getValueTextColor());
                c.drawText(lastString,
                        left + (lastStringWidth + padding) / 2F,
                        Math.max(lastStringHeight + padding / 2F, lastY + lastStringHeight / 2F),
                        mLastPriceTextPaint);
                // 绘制箭头
                mLastPriceArrowPath.reset();
                float arrowLeft = left + lastStringWidth + padding;
                float arrowOffset = dataSet.getValueTextSize() / 4F;
                mLastPriceArrowPath.moveTo(arrowLeft, lastY - arrowOffset);
                mLastPriceArrowPath.lineTo(arrowLeft + arrowOffset, lastY);
                mLastPriceArrowPath.lineTo(arrowLeft, lastY + arrowOffset);
                mLastPriceBorderPaint.setColor(ContextCompat.getColor(context, R.color.axis_color));
                c.drawPath(mLastPriceArrowPath, mLastPriceBorderPaint);
                // 绘制边框
                c.drawRoundRect(rectF, 4, 4, mLastPriceBorderPaint);
            }
        }
    }

    @Override
    public void drawExtras(Canvas c) {
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        CandleData candleData = mChart.getCandleData();

        for (Highlight high : indices) {

            ICandleDataSet set = candleData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            CandleEntry e = set.getEntryForXValue(high.getX(), high.getY());

            if (!isInBoundsX(e, set))
                continue;

            float lowValue = e.getLow() * mAnimator.getPhaseY();
            float highValue = e.getHigh() * mAnimator.getPhaseY();
            float y = e.getClose() * mAnimator.getPhaseY();

            MPPointD pix = mChart.getTransformer(set.getAxisDependency()).getPixelForValues(e.getX(), y);

            high.setDraw((float) pix.x, (float) pix.y);

            // draw the lines
            drawHighlightLines(c, (float) pix.x, (float) pix.y, set);
        }
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
//            mHighlightPaint.setStrokeWidth(set.getHighlightLineWidth());
            mHighlightPaint.setStrokeWidth(mBodyBuffers[2]-mBodyBuffers[0]);

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
