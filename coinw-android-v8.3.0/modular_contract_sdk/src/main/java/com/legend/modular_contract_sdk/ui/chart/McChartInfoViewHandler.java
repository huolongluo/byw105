package com.legend.modular_contract_sdk.ui.chart;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.renderer.DataRenderer;
/**
 * Created by dell on 2017/10/27.
 */
public class McChartInfoViewHandler implements View.OnTouchListener {
    private BarLineChartBase mChart;
    private final GestureDetector mDetector;
    private boolean mIsLongPress = false;
    private boolean mIsLastLongPress = false;
    private DismissInfoViewCallback mDismissInfoViewCallback;
    private ScrollToRightCallback mScrollToRightCallback;

    public McChartInfoViewHandler(BarLineChartBase chart, DismissInfoViewCallback dismissInfoViewCallback, ScrollToRightCallback scrollToRightCallback) {
        mChart = chart;
        mDismissInfoViewCallback = dismissInfoViewCallback;
        mScrollToRightCallback = scrollToRightCallback;
        mDetector = new GestureDetector(mChart.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                for (DataRenderer renderer : ((McAppCombinedChartRenderer) mChart.getRenderer()).getSubRenderers()) {
                    RectF rectF = renderer.mScrollToRightRect;
                    // 增大点击区域
                    rectF.inset(-20f, -20f);
                    boolean isClick = rectF.contains(e.getX(), e.getY());
                    if (isClick && (mScrollToRightCallback != null)) {
                        mScrollToRightCallback.onScrollRight();
                        return true;
                    }
                }
                // 上一次是长按，点击取消
                if (mIsLastLongPress) {
                    mDismissInfoViewCallback.onDismiss();
                    return true;
                }
                if (mChart.getViewPortHandler().isInBounds(e.getX(), e.getY())) {
                    Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());
                    if (h != null) {
                        mChart.highlightValue(h, true);
                        return true;
                    }
                } else {
                    mDismissInfoViewCallback.onDismiss();
                    return true;
                }
                return super.onSingleTapUp(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                mIsLongPress = true;
                Highlight h = mChart.getHighlightByTouchPoint(e.getX(), e.getY());
                if (h != null) {
                    mChart.highlightValue(h, true);
                    mChart.disableScroll();
                }
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            mIsLastLongPress = mIsLongPress;
            mIsLongPress = false;
        }
        if (mIsLongPress && event.getAction() == MotionEvent.ACTION_MOVE) {
            Highlight h = mChart.getHighlightByTouchPoint(event.getX(), event.getY());
            if (h != null) {
                mChart.highlightValue(h, true);
                mChart.disableScroll();
            }
            return true;
        }
        return false;
    }

    public interface DismissInfoViewCallback {
        void onDismiss();
    }

    public interface ScrollToRightCallback {
        void onScrollRight();
    }
}
