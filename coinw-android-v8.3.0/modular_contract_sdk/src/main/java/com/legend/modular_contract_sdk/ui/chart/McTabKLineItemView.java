package com.legend.modular_contract_sdk.ui.chart;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.legend.modular_contract_sdk.R;

import org.jetbrains.annotations.NotNull;

public class McTabKLineItemView extends LinearLayout {
    private TextView tvTitle;
    private ImageView ivBottom;

    public McTabKLineItemView(Context context) {
        this(context, null);
    }

    public McTabKLineItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public McTabKLineItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.mc_sdk_view_tab_kline_item, this);
        tvTitle = findViewById(R.id.tvTitle);
        ivBottom = findViewById(R.id.ivBottom);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.mc_sdk_TabKLineItemView);
        String title = a.getString(R.styleable.mc_sdk_TabKLineItemView_title);
        Drawable drawableRight = a.getDrawable(R.styleable.mc_sdk_TabKLineItemView_mc_sdk_drawableRight);
        a.recycle();
        tvTitle.setText(title);
        if(drawableRight != null) {
            tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
        }
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        tvTitle.setSelected(selected);
        if (selected) {
            tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.mc_sdk_white));
            ivBottom.setImageResource(R.drawable.mc_sdk_transfer_tab_indicator);
        } else {
            tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.mc_sdk_6f698b));
            ivBottom.setImageResource(R.color.mc_sdk_transparent);
        }
    }

    public void setTitle(@NotNull CharSequence text) {
        if (tvTitle != null) {
            tvTitle.setText(text);
        }
    }
}
