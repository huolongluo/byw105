package com.legend.modular_contract_sdk.ui.chart;
/**
 * Created by guoziwei on 2016/2/1.
 */
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.legend.modular_contract_sdk.R;
import com.legend.modular_contract_sdk.utils.McDateUtils;

import java.util.List;
/**
 * Custom implementation of the MarkerView.
 * @author Philipp Jahoda
 */
public class McLineChartXMarkerView extends MarkerView {
    private List<McHisData> mList;
    private TextView tvContent;
    private String mDateFormat;

    public McLineChartXMarkerView(Context context, List<McHisData> list, String dateFormat) {
        super(context, R.layout.mc_sdk_view_kline_x_marker);
        mList = list;
        mDateFormat = dateFormat;
        tvContent = findViewById(R.id.tv_content);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int value = (int) e.getX();
        if (mList != null && value < mList.size()) {
            String text = McDateUtils.format(mList.get(value).getDate(), mDateFormat);
            tvContent.setText(text);
        }
        super.refreshContent(e, highlight);
    }
}
