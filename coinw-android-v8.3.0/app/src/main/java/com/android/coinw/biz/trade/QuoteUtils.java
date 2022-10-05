package com.android.coinw.biz.trade;
import android.animation.ValueAnimator;
import android.widget.ProgressBar;
import android.widget.TextView;
public class QuoteUtils {
    public static void setValue(final ProgressBar progressBar, final int mProgressBar) {
        ValueAnimator animator = ValueAnimator.ofInt(0, mProgressBar).setDuration(100);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator va) {
                progressBar.setProgress((int) va.getAnimatedValue());
            }
        });
        animator.start();
    }

    public static void setValue(final TextView srcTxt, final int value) {
        ValueAnimator animator = ValueAnimator.ofInt(0, value).setDuration(100);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                srcTxt.setText(String.valueOf(valueAnimator.getAnimatedValue()));
            }
        });
        animator.start();
    }
}
