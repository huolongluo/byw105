package huolongluo.byw.view;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import huolongluo.byw.R;
public class TabItemView extends LinearLayout {
    private TextView tvTitle;
    private ImageView ivBottom;

    public TabItemView(Context context) {
        this(context, null);
    }

    public TabItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_tab_item, this);
        tvTitle = findViewById(R.id.tvTitle);
        ivBottom = findViewById(R.id.ivBottom);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TabItemView);
        String title = a.getString(R.styleable.TabItemView_title);
        a.recycle();
        tvTitle.setText(title);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            ivBottom.setImageResource(R.mipmap.transfer_tab_indicator);
        } else {
            tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.color_a5a2be));
            ivBottom.setImageResource(R.drawable.transparent);
        }
    }
}
