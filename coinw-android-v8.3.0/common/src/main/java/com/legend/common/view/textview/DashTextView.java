package com.legend.common.view.textview;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;
import com.legend.common.R;
/**
 * 文本下面带有虚线的TextView
 */
public class DashTextView extends AppCompatTextView {

    private Paint mPaint;
    private Path mPath;
    private int color;

    public DashTextView(Context context) {
        super(context);
    }

    public DashTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs);
    }

    public DashTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,attrs);
    }

    private void initView(Context context,AttributeSet attrs){
        color = R.color.color_dash;
        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DashTextView);
            //虚线颜色
            if (typedArray.hasValue(R.styleable.DashTextView_color)) {
                color = typedArray.getResourceId(R.styleable.DashTextView_color,R.color.color_dash);
            }
        }
        initPaint();
    }

    private void initPaint(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //默认使用textview当前颜色
        mPaint.setColor(getResources().getColor(color));
        mPaint.setStrokeWidth(3);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(new DashPathEffect(new float[] {5, 5}, 0));
        mPath = new Path();
        //设置虚线距离
        setPadding(0,0,0,2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        int centerY = getHeight();
        mPath.reset();
        mPath.moveTo(0, centerY);
        mPath.lineTo(getTextWidth(), centerY);
        canvas.drawPath(mPath, mPaint);
    }

    //获取每行长度，选取最大长度。因为部分手机换行导致虚线过长
    private float getTextWidth(){
        float textWidth = 0;
        //循环遍历打印每一行
        for (int i = 0; i < getLineCount(); i++) {
            if(textWidth < getLayout().getLineWidth(i)){
                textWidth = getLayout().getLineWidth(i);
            }
        }
        return textWidth == 0 ? getWidth() : textWidth;
    }

}
