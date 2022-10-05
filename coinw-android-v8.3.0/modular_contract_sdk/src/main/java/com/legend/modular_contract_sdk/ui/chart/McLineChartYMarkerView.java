package com.legend.modular_contract_sdk.ui.chart;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.legend.modular_contract_sdk.R;
import com.legend.modular_contract_sdk.utils.NumberStringUtil;

import java.math.BigDecimal;
/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class McLineChartYMarkerView extends MarkerView {

    private final int digits;
    private TextView tvContent;

    public McLineChartYMarkerView(Context context, int digits) {
        super(context, R.layout.mc_sdk_view_kline_y_marker);
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
        tvContent.setText(NumberStringUtil.formatAmount(valueD,digits));
        super.refreshContent(e, highlight);
    }

}
