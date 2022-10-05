package huolongluo.byw.view.kline;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import java.math.BigDecimal;

import huolongluo.byw.R;
import huolongluo.byw.util.noru.NorUtils;
/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class LineChartYMarkerView extends MarkerView {

    private final int digits;
    private TextView tvContent;

    public LineChartYMarkerView(Context context, int digits) {
        super(context, R.layout.view_kline_y_marker);
        this.digits = digits;
        tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        float value;
        if (e instanceof CandleEntry) {
            value = ((CandleEntry) e).getClose();
        } else {
            value = e.getY();
        }

        double valueD=new BigDecimal(value+"").doubleValue();
        tvContent.setText(NorUtils.NumberFormat(digits).format(valueD));
        super.refreshContent(e, highlight);
    }

}
