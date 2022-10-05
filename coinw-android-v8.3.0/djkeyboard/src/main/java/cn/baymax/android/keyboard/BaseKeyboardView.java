package cn.baymax.android.keyboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import java.util.List;

/**
 * Created by xud on 2017/3/2.
 */

public class BaseKeyboardView extends KeyboardView{

    private static final String TAG = "BaseKeyboardView";
    private Drawable rKeyBackground;
    private int rLabelTextSize;
    private int rKeyTextSize;
    private int rKeyTextColor;
    private float rShadowRadius;
    private int rShadowColor;

    private Rect rClipRegion;
    private Keyboard.Key rInvalidatedKey;
    private Drawable mKeybgDrawable;
    private Drawable mKeybgDrawable1;
    private Rect rect;
    private Paint paint;
    private Paint paint1;
    public BaseKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public BaseKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseKeyboardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        rKeyBackground = (Drawable) ReflectionUtils.getFieldValue(this, "mKeyBackground");
        rLabelTextSize = (int) ReflectionUtils.getFieldValue(this, "mLabelTextSize");
        rKeyTextSize = (int) ReflectionUtils.getFieldValue(this, "mKeyTextSize");
        rKeyTextColor = (int) ReflectionUtils.getFieldValue(this, "mKeyTextColor");
        rShadowColor = (int) ReflectionUtils.getFieldValue(this, "mShadowColor");
        rShadowRadius = (float) ReflectionUtils.getFieldValue(this, "mShadowRadius");
        initResources(context);
    }
    private void initResources(Context context) {
        Resources res = context.getResources();
        mKeybgDrawable = res.getDrawable(R.drawable.btn_keyboard_key);
      //  mKeybgDrawable1 = res.getDrawable(R.drawable.key_down_bg);

        rect = new Rect();
        paint = new Paint();
        paint1 = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(50);
        paint.setColor(res.getColor(android.R.color.black));

        paint1.setAntiAlias(true);
        paint1.setTextAlign(Paint.Align.CENTER);
        paint1.setTextSize(50);
        paint1.setColor(res.getColor(android.R.color.white));
    }
    @Override
    public void onDraw(Canvas canvas) {
        //说明CustomKeyboardView只针对CustomBaseKeyboard键盘进行重绘,
        // 且CustomBaseKeyboard必需有设置CustomKeyStyle的回调接口实现, 才进行重绘, 这才有意义
        if (null == getKeyboard() || !(getKeyboard() instanceof BaseKeyboard) || null == ((BaseKeyboard) getKeyboard()).getKeyStyle()) {
            Log.e(TAG, "");
            super.onDraw(canvas);
            return;
        }
        rClipRegion = (Rect) ReflectionUtils.getFieldValue(this, "mClipRegion");
        rInvalidatedKey = (Keyboard.Key) ReflectionUtils.getFieldValue(this, "mInvalidatedKey");
        super.onDraw(canvas);
        onRefreshKey(canvas);
    }

        public  int type=0;

    /**
     * onRefreshKey是对父类的private void onBufferDraw()进行的重写. 只是在对key的绘制过程中进行了重新设置.
     *
     * @param canvas
     */
    private void onRefreshKey(Canvas canvas) {
      //  final Paint paint = (Paint) ReflectionUtils.getFieldValue(this, "mPaint");
       // final Rect padding = (Rect) ReflectionUtils.getFieldValue(this, "mPadding");

      //  paint.setColor(rKeyTextColor);
        final int kbdPaddingLeft = getPaddingLeft();
        final int kbdPaddingTop = getPaddingTop();
        Drawable keyBackground = null;
        List<Keyboard.Key> keys = getKeyboard().getKeys();
        for (Keyboard.Key key : keys) {
            canvas.save();

            int offsety = 0;
            if (key.y == 0) {
                offsety = 1;
            }
            int initdrawy = key.y + offsety;
            rect.left = key.x;
            rect.top = initdrawy;
            rect.right = key.x+key.width;
            rect.bottom = key.y+key.height;
            canvas.clipRect(rect);
            int[] state = key.getCurrentDrawableState();

            if(key.codes[0]==-41&&key.codes[1]==123){

                if(type==0){
                    mKeybgDrawable1=getResources().getDrawable(R.drawable.key_down_bg);

                }else {

                    mKeybgDrawable1=getResources().getDrawable(R.drawable.key_down_bg1);
                }

               // drawable.draw(canvas);
                mKeybgDrawable1.setState(state);
                mKeybgDrawable1.setBounds(rect);
                mKeybgDrawable1.draw(canvas);
            }else {
                mKeybgDrawable.setState(state);
                mKeybgDrawable.setBounds(rect);
                mKeybgDrawable.draw(canvas);
            }


            if (key.label != null) {
                if(key.codes[0]==-4||key.codes[0]==-41){
                    canvas.drawText(
                            key.label.toString(),
                            key.x + (key.width / 2),
                            initdrawy + (key.height + paint1.getTextSize() - paint1.descent()) / 2,
                            paint1);
                }else {
                    canvas.drawText(
                            key.label.toString(),
                            key.x + (key.width / 2),
                            initdrawy + (key.height + paint.getTextSize() - paint.descent()) / 2,
                            paint);
                }
            } else if (key.icon != null) {

                if(key.codes[0]==-4||key.codes[0]==-41){
                    int intriWidth = (int) (key.width/2.5);
                    int intriHeight = (int) (key.height/2.5);

                    final int drawableX = key.x + (key.width - intriWidth) / 2;
                    final int drawableY = initdrawy + (key.height - intriHeight) / 2;

                    key.icon.setBounds(
                            drawableX, drawableY, drawableX + intriWidth,
                            drawableY + intriHeight);

                    key.icon.draw(canvas);
                }else {
                    //   int intriWidth = (int) (key.icon.getIntrinsicWidth());
                    //  int intriHeight = (int) (key.icon.getIntrinsicHeight());
                    int intriWidth = (int) (key.width/3.5);
                    int intriHeight = (key.height/2);

                    final int drawableX = key.x + (key.width - intriWidth) / 2;
                    final int drawableY = initdrawy + (key.height - intriHeight) / 2;

                    key.icon.setBounds(
                            drawableX, drawableY, drawableX + intriWidth,
                            drawableY + intriHeight);

                    key.icon.draw(canvas);
                }


            }

            canvas.restore();
        }
        rInvalidatedKey = null;
    }

    private CharSequence adjustCase(CharSequence label) {
        if (getKeyboard().isShifted() && label != null && label.length() < 3
                && Character.isLowerCase(label.charAt(0))) {
            label = label.toString().toUpperCase();
        }
        return label;
    }
}
