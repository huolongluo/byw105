package huolongluo.byw.widget;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

/**
 * Created by LS on 2018/7/13.
 */

@SuppressLint("AppCompatCustomView")
public class RunTextView extends TextView{
    private int duration = 1000;
    private float number;

    public float getNumber() {
        return number;
    }

    public void setNumber(float number) {
        this.number = number;
        setText(String.format("%,.3f",number));

    }

    public RunTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 显示
     * @param number
     */
    public void runWithAnimation(float number){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
                this, "number", 0, number);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        objectAnimator.start();

    }

}
