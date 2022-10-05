package com.legend.modular_contract_sdk.ui.chart;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.legend.modular_contract_sdk.R;
public class McTabItemView extends LinearLayout {
    private TextView tvTitle;
    private ImageView ivBottom;

    public McTabItemView(Context context) {
        this(context, null);
    }

    public McTabItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public McTabItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.mc_sdk_view_tab_item, this);
        tvTitle = findViewById(R.id.tvTitle);
        ivBottom = findViewById(R.id.ivBottom);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.mc_sdk_TabItemView);
        String title = a.getString(R.styleable.mc_sdk_TabItemView_title);
        a.recycle();
        tvTitle.setText(title);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.mc_sdk_white));
            ivBottom.setImageResource(R.drawable.mc_sdk_transfer_tab_indicator);
        } else {
            tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.mc_sdk_a5a2be));
            ivBottom.setImageResource(R.color.mc_sdk_transparent);
        }
    }
}
