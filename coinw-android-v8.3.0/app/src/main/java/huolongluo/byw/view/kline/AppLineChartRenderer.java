package huolongluo.byw.view.kline;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineScatterCandleRadarDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.math.BigDecimal;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.util.noru.NorUtils;
/**
 * Created by dell on 2017/6/26.
 */
public class AppLineChartRenderer extends LineChartRenderer {
    private float[] mCirclesBuffer = new float[2];

    public AppLineChartRenderer(LineDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }


   /* @Override
    protected void drawHighlightLines(Canvas c, float x, float y, ILineScatterCandleRadarDataSet set) {
        // set color and stroke-width
        mHighlightPaint.setColor(set.getHighLightColor());
        mHighlightPaint.setStrokeWidth(set.getHighlightLineWidth());

        // draw highlighted lines (if enabled)
        mHighlightPaint.setPathEffect(set.getDashPathEffectHighlight());

        // draw vertical highlight lines
        if (set.isVerticalHighlightIndicatorEnabled()) {

            // create vertical path
            mHighlightLinePath.reset();
            mHighlightLinePath.moveTo(x, mViewPortHandler.contentTop());
            mHighlightLinePath.lineTo(x, mViewPortHandler.contentBottom());

            c.drawPath(mHighlightLinePath, mHighlightPaint);
        }

        // draw horizontal highlight lines
        if (set.isHorizontalHighlightIndicatorEnabled()) {

            // create horizontal path
            mHighlightLinePath.reset();
//            mHighlightLinePath.moveTo(mViewPortHandler.contentLeft(), y);
            mHighlightLinePath.moveTo(x, y);
            mHighlightLinePath.lineTo(mViewPortHandler.contentRight(), y);

            c.drawPath(mHighlightLinePath, mHighlightPaint);
        }
    }*/

    @Override
    public void drawExtras(Canvas c) {
        super.drawExtras(c);
        drawLastPointCircle(c);
    }

    public void drawLastPointCircle(Canvas c) {
        OnLastPointChangeListener listener = null;
        if (mChart instanceof AppCombinedChart) {
            listener = ((AppCombinedChart) mChart).getOnLastPointChangeListener();
        }
        mRenderPaint.setStyle(Paint.Style.FILL);
        float phaseY = mAnimator.getPhaseY();
        mCirclesBuffer[0] = 0;
        mCirclesBuffer[1] = 0;
        List<ILineDataSet> dataSets = mChart.getLineData().getDataSets();
        for (int i = 0; i < dataSets.size(); i++) {
            ILineDataSet dataSet = dataSets.get(i);
            if (!dataSet.isVisible() || !dataSet.isDrawLastCirclesEnabled() ||
                    dataSet.getEntryCount() == 0)
                continue;
            mRenderPaint.setColor(dataSet.getCircleColor(0));
            mCirclePaintInner.setColor(dataSet.getCircleHoleColor());
            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
            mXBounds.set(mChart, dataSet);
            float circleRadius = dataSet.getCircleRadius();
            float circleHoleRadius = dataSet.getCircleHoleRadius();
            boolean drawCircleHole = dataSet.isDrawCircleHoleEnabled() &&
                    circleHoleRadius < circleRadius &&
                    circleHoleRadius > 0.f;
            Entry e = dataSet.getEntryForIndex(dataSet.getEntryCount() - 1);
            if (e == null) return;
            mCirclesBuffer[0] = e.getX();
            mCirclesBuffer[1] = e.getY() * phaseY;
            trans.pointValuesToPixel(mCirclesBuffer);
            if (!mViewPortHandler.isInBoundsRight(mCirclesBuffer[0])) {
                if (listener != null) listener.onChange(0, 0);
                return;
            }
            if (!mViewPortHandler.isInBoundsLeft(mCirclesBuffer[0]) ||
                    !mViewPortHandler.isInBoundsY(mCirclesBuffer[1])) {
                if (listener != null) listener.onChange(0, 0);
                return;
            }
            c.drawCircle(
                    mCirclesBuffer[0],
                    mCirclesBuffer[1],
                    circleRadius,
                    mRenderPaint);
            if (drawCircleHole) {
                c.drawCircle(
                        mCirclesBuffer[0],
                        mCirclesBuffer[1],
                        circleHoleRadius,
                        mCirclePaintInner);
            }
            if (listener != null) listener.onChange(mCirclesBuffer[0], mCirclesBuffer[1]);
        }
    }

    public float[] getCirclesBuffer() {
        return mCirclesBuffer;
    }

    @Override
    public void drawValues(Canvas c) {
        LineData data = mChart.getLineData();
        List<ILineDataSet> dataSets = data.getDataSets();
        int digit = data.getDigit();
        for (int i = 0; i < dataSets.size(); i++) {
            ILineDataSet dataSet = dataSets.get(i);
            if (!dataSet.isDrawValuesEnabled() || dataSet.getEntryCount() == 0)
                continue;
            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);
            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
            int minx = (int) Math.max(dataSet.getXMin(), 0);
            int maxx = (int) Math.min(dataSet.getXMax(), dataSet.getEntryCount() - 1);
            float[] positions = trans.generateTransformedValuesLine(
                    dataSet, mAnimator.getPhaseX(), mAnimator.getPhaseY(), minx, maxx);
            // 计算最后的一个价格
            float lastValue = 0;
            int lastIndex = dataSet.getEntryCount() - 1;
            Entry lastEntry = dataSet.getEntryForIndex(lastIndex);
            if (lastEntry != null) {
                lastValue = lastEntry.getY();
            }
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
            if (mViewPortHandler.isInBoundsRight(lastX + lastStringWidth)) { // 最后一个点是否在画布范围内
                float padding = Utils.convertDpToPixel(4);
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
                float padding = Utils.convertDpToPixel(8);
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
}
