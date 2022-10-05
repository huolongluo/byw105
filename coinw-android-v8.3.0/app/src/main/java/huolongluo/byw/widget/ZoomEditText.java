package huolongluo.byw.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import androidx.appcompat.widget.AppCompatEditText;

public class ZoomEditText extends AppCompatEditText {
    public static final int AMPLIFY = 1;
    public static final int REDUCE = 2;
    private boolean isZoom;
    private SpannableString msp;
    private float strethScale = 1.2f;// 缩放比例
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case AMPLIFY:
                    amplify();
                    break;
                case REDUCE:
                    reduce();
                    break;
            }
        }
    };

    public void reduce() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                msp = new SpannableString(getText().toString());
                msp.setSpan(new RelativeSizeSpan(1), 0, getText().toString().length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                msp.setSpan(new ScaleXSpan(1), 0, getText().toString().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                setText(msp);
                mHandler.sendEmptyMessage(AMPLIFY);
            }
        }, 500);
    }

    public void amplify() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                msp = new SpannableString(getText().toString());
                // 相对比缩放
                msp.setSpan(new RelativeSizeSpan(strethScale), 0, getText().toString().length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                // 横向缩放
                msp.setSpan(new ScaleXSpan(strethScale), 0, getText().toString().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                setText(msp);
                mHandler.sendEmptyMessage(REDUCE);
            }
        }, 500);
    }

    public void doRun() {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.15f, 1.0f, 1.15f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(100);//动画持续时间
        ScaleAnimation scaleAnimation2 = new ScaleAnimation(1.15f, 1.0f, 1.15f, 1.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation2.setDuration(100);//动画持续时间
        animationSet.addAnimation(scaleAnimation);//保存动画效果到。。
        animationSet.setFillAfter(false);//结束后保存状态
        this.startAnimation(animationSet);//设置给控件
    }

    public ZoomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public ZoomEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomEditText(Context context) {
        this(context, null);
    }

    public boolean isZoom() {
        return isZoom;
    }

    public void setZoom(boolean isZoom) {
        release();
        this.isZoom = isZoom;
        if (isZoom) {
            amplify();
        }
        this.requestFocus();
    }

    public void release() {
        mHandler.removeCallbacksAndMessages(null);
    }
}
