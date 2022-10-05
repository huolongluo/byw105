package huolongluo.byw.byw.ui.redEnvelope;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.android.coinw.biz.event.BizEvent;

import cn.baymax.android.keyboard.Utils;
import huolongluo.byw.byw.base.BaseApp;

public class RedEnvelopeAnimation {
    private static boolean showAnimation = false;
    private static boolean showAnimation1 = false;

    public static void scrollShowEedEnvelope(BizEvent.ShowRedEnvelope showRedEnvelope, ImageView redEnvelope, int windowWidth) {
        int[] location = new int[2];
        redEnvelope.getLocationInWindow(location); //获取在当前窗口内的绝对坐标，含toolBar
        if (showRedEnvelope.showRedEnvelope && !showAnimation) {
            showAnimation = true;
            Log.i("redEnvelope.getX()", redEnvelope.getX() + "-" + location[0] + "----" + windowWidth / 2);
            ObjectAnimator animator = ObjectAnimator.ofFloat(redEnvelope, "x",
                    redEnvelope.getX() < windowWidth / 2 ? 0 :
                            windowWidth - Utils.dipToPx(BaseApp.getSelf(), 80), redEnvelope.getX()
                            < windowWidth / 2 ? -Utils.dipToPx(BaseApp.getSelf(), 30) :
                            windowWidth - Utils.dipToPx(BaseApp.getSelf(), 30));
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(redEnvelope, "alpha",
                    1.0f, 0.5f);
            AnimatorSet animationSet = new AnimatorSet();
            animationSet.play(animator).with(animator2);
            animationSet.setDuration(500);
            animationSet.start();
        }
        if (!showRedEnvelope.showRedEnvelope && showAnimation && !showAnimation1) {
            showAnimation1 = true;
            new Handler().postDelayed(() -> redEnvelopeAnimation(redEnvelope, windowWidth, redEnvelope.getX()), 1000);
        }
    }


    private static void redEnvelopeAnimation(ImageView redEnvelope, int windowWidth, float x) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(redEnvelope, "x",
                redEnvelope.getX() < windowWidth / 2 ? -Utils.dipToPx(BaseApp.getSelf(), 30) :
                        windowWidth - Utils.dipToPx(BaseApp.getSelf(), 30), redEnvelope.getX() < windowWidth / 2 ? 0 :
                        windowWidth - Utils.dipToPx(BaseApp.getSelf(), 80));
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(redEnvelope, "alpha",
                0.5f, 1f);
        AnimatorSet animationSet = new AnimatorSet();
        animationSet.play(animator).with(animator2);
        animationSet.setDuration(500);
        animationSet.start();
        animationSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                showAnimation = false;
                showAnimation1 = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

}
