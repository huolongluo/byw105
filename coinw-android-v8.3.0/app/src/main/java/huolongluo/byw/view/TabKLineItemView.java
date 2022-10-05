package huolongluo.byw.view;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

import huolongluo.byw.R;
public class TabKLineItemView extends LinearLayout {
    private TextView tvTitle;
    private ImageView ivBottom;

    public TabKLineItemView(Context context) {
        this(context, null);
    }

    public TabKLineItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabKLineItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_tab_kline_item, this);
        tvTitle = findViewById(R.id.tvTitle);
        ivBottom = findViewById(R.id.ivBottom);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TabKLineItemView);
        String title = a.getString(R.styleable.TabKLineItemView_title);
        Drawable drawableRight = a.getDrawable(R.styleable.TabKLineItemView_drawableRight);
        a.recycle();
        tvTitle.setText(title);
        if(drawableRight != null) {
            tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
//            ViewGroup.MarginLayoutParams lp = (MarginLayoutParams) ivBottom.getLayoutParams();
//            lp.rightMargin = drawableRight.getIntrinsicWidth();
//            ivBottom.setLayoutParams(lp);
        }
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        tvTitle.setSelected(selected);
        if (selected) {
            tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            ivBottom.setImageResource(R.mipmap.transfer_tab_indicator);
        } else {
            tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.color_6f698b));
            ivBottom.setImageResource(R.drawable.transparent);
        }
    }

    public void setTitle(@NotNull CharSequence text) {
        if (tvTitle != null) {
            tvTitle.setText(text);
        }
    }
}
