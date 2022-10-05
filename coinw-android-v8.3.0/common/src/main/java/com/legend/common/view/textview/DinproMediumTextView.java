package com.legend.common.view.textview;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
//DinproMedium字体的textview
public class DinproMediumTextView extends androidx.appcompat.widget.AppCompatTextView {
    public DinproMediumTextView(Context context) {
        super(context);
    }

    public DinproMediumTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DinproMediumTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTypeface(@Nullable Typeface tf) {
        tf= Typeface.createFromAsset(getContext().getAssets(), "fonts/DINPRO-MEDIUM.OTF");
        super.setTypeface(tf);
    }

}
