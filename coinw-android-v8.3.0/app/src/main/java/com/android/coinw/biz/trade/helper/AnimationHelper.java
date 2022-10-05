package com.android.coinw.biz.trade.helper;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ProgressBar;
public class AnimationHelper {
    private static AnimationHelper instance;
    private boolean mOpenAnimationEnable = false;

    public static AnimationHelper getInstance() {
        if (instance == null) {
            synchronized (AnimationHelper.class) {
                if (instance == null) {
                    instance = new AnimationHelper();
                }
            }
        }
        return instance;
    }

    public boolean isEnableAnimation() {
        return mOpenAnimationEnable;
    }

    public void openAnimationEnable(boolean mOpenAnimationEnable) {
        this.mOpenAnimationEnable = mOpenAnimationEnable;
    }

    public void postAnimation(View view, int w) {
        TranslateAnimation ta = new TranslateAnimation(w, 0, 0, 0);//这里的我是y轴上动画。y起始点是原点。distance是要平移的距离
        ta.setDuration(300);
        ta.setFillAfter(true);
        view.startAnimation(ta);
    }

    public void transY(View view, int startY, int endY) {
        TranslateAnimation ta = new TranslateAnimation(0, 0, startY, endY);
        ta.setDuration(200);
        view.startAnimation(ta);
    }

    public void updateProgress(ProgressBar progressbar, int p) {
//        if (Build.VERSION.SDK_INT >= 24) {
//            progressbar.setProgress(p, true);
//        } else {
//            progressbar.setProgress(p);
//        }
        progressbar.setProgress(p);
    }
}
