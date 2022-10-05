package huolongluo.byw.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import huolongluo.byw.R;

public class LoadingView extends RelativeLayout {
    private Animation anim1, anim2;
    private ImageView iv_loading;
    private ImageView iv_loading2;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        anim1 = AnimationUtils.loadAnimation(context, R.anim.comm_loading);
        anim2 = AnimationUtils.loadAnimation(context, R.anim.comm_loading2);
        LayoutInflater.from(context).inflate(R.layout.loading_views, this);

        iv_loading = findViewById(R.id.iv_loading);
        iv_loading2 = findViewById(R.id.iv_loading2);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    private void startAnimation() {
        if(iv_loading != null) {
            iv_loading.startAnimation(anim1);
        }
        if(iv_loading2 != null) {
            iv_loading2.startAnimation(anim2);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            iv_loading.startAnimation(anim1);
            iv_loading2.startAnimation(anim2);
        } else {
            cancelAnimation(anim1);
            cancelAnimation(anim2);
        }
    }

    private void cancelAnimation(Animation anim) {
        if (anim == null) {
            return;
        }
        try {
            anim.cancel();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAnimation(anim1);
        cancelAnimation(anim2);
    }
}
