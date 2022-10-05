package huolongluo.byw.view.kline;
/**
 * Created by guoziwei on 2016/2/1.
 */
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.util.DateUtils;
/**
 * Custom implementation of the MarkerView.
 * @author Philipp Jahoda
 */
public class LineChartXMarkerView extends MarkerView {
    private List<HisData> mList;
    private TextView tvContent;
    private String mDateFormat;

    public LineChartXMarkerView(Context context, List<HisData> list, String dateFormat) {
        super(context, R.layout.view_kline_x_marker);
        mList = list;
        mDateFormat = dateFormat;
        tvContent = findViewById(R.id.tv_content);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int value = (int) e.getX();
        if (mList != null && value < mList.size()) {
            String text = DateUtils.format(mList.get(value).getDate(), mDateFormat);
            tvContent.setText(text);
        }
        super.refreshContent(e, highlight);
    }
}
