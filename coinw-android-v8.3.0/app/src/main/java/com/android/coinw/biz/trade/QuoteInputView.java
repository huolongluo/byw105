package com.android.coinw.biz.trade;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import huolongluo.byw.R;
import huolongluo.byw.log.Logger;
public class QuoteInputView extends ViewGroup {
    private RelativeLayout rootLayout;
    private EditText dataTxt;
    private TextView unitTxt;

    public QuoteInputView(Context context) {
        this(context, null);
    }

    public QuoteInputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuoteInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public QuoteInputView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        View childView = LayoutInflater.from(context).inflate(R.layout.layout_input, null, false);
        this.rootLayout = childView.findViewById(R.id.rl_root);
        this.dataTxt = childView.findViewById(R.id.et_data);
        this.unitTxt = childView.findViewById(R.id.tv_unit);
        this.addView(childView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void addTextChangedListener(QuoteTextWatcher watcher) {
        if (this.dataTxt == null || watcher == null) {
            //TODO 处理异常情况
            return;
        }
        this.dataTxt.addTextChangedListener(watcher);
    }

    public void setHint(int resid) {
        if (this.dataTxt == null || resid <= 0) {
            //TODO 处理异常情况
            return;
        }
        try {
            this.dataTxt.setHint(resid);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }

    public void setText(String value) {
        //数据为空情况
        if (this.dataTxt == null) {
            //TODO 处理异常情况
            return;
        }
        this.dataTxt.clearAnimation();
        //目的为处理value为null（空情况）
        value = TextUtils.isEmpty(value) ? "" : value;
        this.dataTxt.setText(value);
    }

    public void setText(String value, boolean isScale) {
//        this.dataTxt.clearFocus();
        setText(value);
        if (isScale) {
            Animation a = AnimationUtils.loadAnimation(getContext(), R.anim.scalebig);
            a.setFillAfter(true);
            this.dataTxt.startAnimation(a);
//            this.dataTxt.setZoom(isScale);
        }
    }

    public void requestCustomFocus() {
        if (this.dataTxt == null) {
            return;
        }
        this.dataTxt.requestFocus();
    }

    public void setGravity(int gravity){
        this.dataTxt.setGravity(gravity);
    }

    public void setEnable(boolean enable){
        this.dataTxt.setEnabled(enable);
    }

    public void setUnit(String unit) {
        if (this.unitTxt == null) {
            //TODO 处理异常情况
            return;
        }
        //目的为处理value为null（空情况）
        unit = TextUtils.isEmpty(unit) ? "" : unit;
        this.unitTxt.setText(unit);
    }

    public void setSelection(int index) {
        //数据为空情况
        if (this.dataTxt == null) {
            //TODO 处理异常情况
            return;
        }
        this.dataTxt.setSelection(index);
    }

    /**
     * 设置数据
     * @param value
     * @param unit
     */
    public void setValue(String value, String unit) {
        //
        setText(value);
        //
        if (this.unitTxt == null) {
            //TODO 处理异常情况
            return;
        }
        //目的为处理value为null（空情况）
        unit = TextUtils.isEmpty(unit) ? "" : unit;
        this.unitTxt.setText(unit);
    }

    public String getText() {
        if (this.dataTxt != null) {
            return this.dataTxt.getText().toString();
        }
        //TODO 处理异常情况
        return "";
    }

    /**
     * 清除数据
     */
    public void clear() {
        if (this.dataTxt != null) {
            this.dataTxt.setText("");
        }
        if (this.unitTxt != null) {
            this.unitTxt.setText("");
        }
    }

    //在onMeasure中测量子view
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setCustomBackgroudRes(int resId) {
        this.dataTxt.setBackgroundResource(resId);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        for (int i = 0; i < getChildCount(); i++) {
//            View childView = getChildAt(i);
//            // 数组是在onMeasure中计算并储存的，把位置和尺寸转换成左、上、右、下就可以了
//            childView.layout(childLeft[i], childTop[i], childRight[i], childBottom[i]);
//
//        }
//        childCount = getChildCount();
//        //设置这个ViewGroup的高度
//        MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
//        lp.height=mScreenHeight * childCount;
//        setLayoutParams(lp);
//        //绘制子view的位置
//        for (int i = 0; i< childCount; i++){
//            View childView = getChildAt(i);
//            if(childView.getVisibility()!=View.GONE){
//                childView.layout(l,i*mScreenHeight,r,(i+1)*mScreenHeight);
//            }
//        }
        final int count = getChildCount();
        int childMeasureWidth = 0;
        int childMeasureHeight = 0;
        int layoutWidth = 0;    // 容器已经占据的宽度
        int layoutHeight = 0;   // 容器已经占据的宽度
        int maxChildHeight = 0; //一行中子控件最高的高度，用于决定下一行高度应该在目前基础上累加多少
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            //注意此处不能使用getWidth和getHeight，这两个方法必须在onLayout执行完，才能正确获取宽高
            childMeasureWidth = child.getMeasuredWidth();
            childMeasureHeight = child.getMeasuredHeight();
            if (layoutWidth < getWidth()) {
                //如果一行没有排满，继续往右排列
                l = layoutWidth;
                r = l + childMeasureWidth;
                t = layoutHeight;
                b = t + childMeasureHeight;
            } else {
                //排满后换行
                layoutWidth = 0;
                layoutHeight += maxChildHeight;
                maxChildHeight = 0;
                l = layoutWidth;
                r = l + childMeasureWidth;
                t = layoutHeight;
                b = t + childMeasureHeight;
            }
            layoutWidth += childMeasureWidth;  //宽度累加
            if (childMeasureHeight > maxChildHeight) {
                maxChildHeight = childMeasureHeight;
            }
            //确定子控件的位置，四个参数分别代表（左上右下）点的坐标值
            child.layout(l, t, r, b);
        }
    }

}
