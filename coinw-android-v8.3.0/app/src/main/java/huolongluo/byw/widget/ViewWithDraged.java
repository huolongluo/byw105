package huolongluo.byw.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;


/**
 * Created by LS on 2018/7/11.
 */

public class ViewWithDraged extends FrameLayout implements View.OnTouchListener {
    private Context mContext;
    /**
     * 子view数组
     */
    private View[] views;

    private GestureDetector gestureDetector;

    /**
     * 定义滑动的时间
     */
    private static final int SCROLL_TIME = 500;
    /**
     * 除第一个子View宽度
     */
    private int otherWidth;
    /**
     * 定义一条中线（偏右则打开侧栏控件）
     */
    private int middleLine;

    /**
     * 定义侧拉状态（默认为侧拉关闭）
     */
    private enum SlideState {
        OPENED, SLIDING, CLOSED
    }

    private SlideState slideState = SlideState.CLOSED;

    public ViewWithDraged(Context context) {
        this(context, null);
    }

    public ViewWithDraged(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewWithDraged(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setOnTouchListener(this);
        gestureDetector = new GestureDetector(context, new MyGesture() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (velocityX < -750 && slideState != SlideState.OPENED) {//左滑
                    doAnimation(views[0].getLeft(), -otherWidth - views[0].getLeft());
                } else if (velocityX > 750 && slideState != SlideState.CLOSED) {//右滑
                    doAnimation(views[0].getLeft(), -views[0].getLeft());
                }
                return false;
            }
        });
    }

    /**
     * 判断是否正在滑动
     */
    private boolean isDoingAnimation;
    private ValueAnimator valueAnimator;
    /**
     * @param startLeft views[0]的getLeft（固定）
     * @param dx        偏移量
     */
    public void doAnimation(final int startLeft, int dx) {
        valueAnimator = ValueAnimator.ofInt(0, dx);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!isDoingAnimation)
                    isDoingAnimation = true;
                int animatedValue = (int) animation.getAnimatedValue();
                for (int i = 0; i < views.length; i++) {
                    if (i == 0)
                        views[i].layout(startLeft + animatedValue, getTop(), startLeft + animatedValue + views[i].getWidth(), getTop() + getHeight());
                    else
                        views[i].layout(views[i - 1].getRight(), getTop(), views[i - 1].getRight() + views[i].getWidth(), getTop() + getHeight());
                }
            }
        });

//        valueAnimator.addListener(new SimpleAnimationListener() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                isDoingAnimation = false;
//                isOutBorder();
//            }
//        });
        valueAnimator.setDuration(SCROLL_TIME);
        valueAnimator.start();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() < 2) {
            throw new RuntimeException("子控件必须大于等于2个！！");
        }
        views = new View[getChildCount()];
        for (int i = 0; i < views.length; i++) {
            views[i] = getChildAt(i);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        for (int i = 1; i < views.length; i++) {
            if (i == 1)
                otherWidth = 0;
            views[i].layout(views[i - 1].getRight(), getTop(), views[i - 1].getRight() + views[i].getWidth(), getTop() + getHeight());
            otherWidth += views[i].getWidth();
        }
        middleLine = getWidth() - otherWidth / 2;
    }


    /**
     * 设置关闭状态
     */
    public void setClose() {
        slideState = SlideState.CLOSED;
        views[0].layout(0, getTop(), getWidth(), getTop() + getHeight());
        for (int i = 1; i < views.length; i++)
            views[i].layout(views[i - 1].getRight(), getTop(), views[i - 1].getRight() + views[i].getWidth(), getTop() + getHeight());
    }

    public void setOpen() {
        slideState = SlideState.OPENED;
        // 设置最后一个的位置
        views[getChildCount() - 1].layout(getWidth() - views[getChildCount() - 1].getWidth(), getTop(), getWidth(), getTop() + getHeight());
        for (int i = views.length - 2; i >= 0; i--) {
            views[i].layout(views[i + 1].getLeft() - views[i].getWidth(), getTop(), views[i + 1].getLeft(), getTop() + getHeight());
        }
    }

    private int downX;
    private int lastX;
    private int downY;
    private int lastY;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                lastX = downX;
                lastY = downY;
                break;
            case MotionEvent.ACTION_MOVE:
                final int startX = (int) event.getX();
                final int startY = (int) event.getY();
                final int dX = startX - lastX;
                if (Math.abs(startX - downX) > Math.abs(startY - downY)) {
                    if (valueAnimator!=null && valueAnimator.isRunning())
                        valueAnimator.cancel();

                    if (!canSliding(dX))
                        return true;

                    if (!isOutBorder()) {
                        setLayoutByx(dX);
                    }

                    isOutBorder(dX);
                }
                lastX = startX;
                break;
            case MotionEvent.ACTION_UP:
                if (slideState == SlideState.SLIDING && !isDoingAnimation) {
                    // 超过中线实现右滑效果
                    if (views[0].getRight() > middleLine) {
                        doAnimation(views[0].getLeft(), -views[0].getLeft());
                    } else {
                        doAnimation(views[0].getLeft(), -otherWidth - views[0].getLeft());
                    }
                }
                break;
        }
        return true;
    }


    private boolean canSliding(int dX) {
        return (dX < 0 || slideState != SlideState.CLOSED) && (dX > 0 || slideState != SlideState.OPENED);
    }

    /**
     * 通过滑动移动x
     *
     * @param dx 偏移量
     */
    public void setLayoutByx(int dx) {
        for (int i = 0; i < views.length; i++) {
            final int newLeft = views[i].getLeft() + dx;
            views[i].layout(newLeft, getTop(), newLeft + views[i].getWidth(), getTop() + getHeight());
        }
    }


    /**
     * 判断是否越界
     *
     * @return
     */
    private boolean isOutBorder() {
        // 关闭状态
        if (views[0].getLeft() >= 0 && slideState != SlideState.CLOSED) {
            getParent().requestDisallowInterceptTouchEvent(false);
            if (listener != null) {
                listener.close();
            }

            if (slideState != SlideState.CLOSED) {
                slideState = SlideState.CLOSED;
            }
            return true;
            // 打开状态
        } else if (views[getChildCount() - 1].getRight() <= getWidth() && slideState != SlideState.OPENED) {
            if (listener != null)
                listener.open();

            getParent().requestDisallowInterceptTouchEvent(false);
            if (slideState != SlideState.OPENED) {
                slideState = SlideState.OPENED;
            }
            return true;
        }
        // 滑动状态
        if (slideState != SlideState.SLIDING) {
            getParent().requestDisallowInterceptTouchEvent(true);
            slideState = SlideState.SLIDING;
        }
        return false;
    }

    /**
     * 判断是否越界(自动纠正)
     *
     * @param dx 偏移量
     */
    private void isOutBorder(int dx) {
        if (views[0].getLeft() > 0 || views[views.length - 1].getRight() < getWidth())
            setLayoutByx(-dx);
    }


    private StateListener listener;

    public interface StateListener {
        void open();

        void close();
    }

    public void setStateListener(StateListener listener) {
        this.listener = listener;
    }

}
