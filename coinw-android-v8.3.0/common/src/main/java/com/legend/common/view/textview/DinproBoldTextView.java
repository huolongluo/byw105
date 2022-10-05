package com.legend.common.view.textview;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
//DinproBold字体的textview
public class DinproBoldTextView extends androidx.appcompat.widget.AppCompatTextView {
    public DinproBoldTextView(Context context) {
        super(context);
    }

    public DinproBoldTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DinproBoldTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTypeface(@Nullable Typeface tf) {
        tf= Typeface.createFromAsset(getContext().getAssets(), "fonts/DINPRO-BOLD.OTF");
        super.setTypeface(tf);
    }

}
