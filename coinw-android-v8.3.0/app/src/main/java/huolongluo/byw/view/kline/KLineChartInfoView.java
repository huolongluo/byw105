package huolongluo.byw.view.kline;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.legend.common.util.ThemeUtil;

import huolongluo.byw.R;
import huolongluo.byw.util.DateUtils;
import huolongluo.byw.util.StringUtil;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.pricing.PricingMethodUtil;
/**
 * zhk线点击的某点的详情框
 */
public class KLineChartInfoView extends ChartInfoView {
    private TextView mTvOpenPrice;
    private TextView mTvClosePrice;
    private TextView mTvHighPrice;
    private TextView mTvLowPrice;
    private TextView mTvVol;
    private TextView mTvRate;
    private TextView mTvTime;
    private TextView mTvChangeAmount;
    private int mIncreasingColor;
    private int mDecreasingColor;
    private int mTextColor;

    public KLineChartInfoView(Context context) {
        this(context, null);
    }

    public KLineChartInfoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KLineChartInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_kline_chart_info, this);
        mTvOpenPrice = findViewById(R.id.tv_open_price);
        mTvClosePrice = findViewById(R.id.tv_close_price);
        mTvHighPrice = findViewById(R.id.tv_high_price);
        mTvLowPrice = findViewById(R.id.tv_low_price);
        mTvVol = findViewById(R.id.tv_vol);
        mTvRate = findViewById(R.id.tv_rate);
        mTvTime = findViewById(R.id.tv_time);
        mTvChangeAmount = findViewById(R.id.tv_change_amount);
        mDecreasingColor = ThemeUtil.INSTANCE.getDropColor(context);
        mIncreasingColor = ThemeUtil.INSTANCE.getUpColor(context);
        mTextColor = context.getResources().getColor(R.color.color_ebe8f7);
    }

    @Override
    public void setData(double lastClose, HisData data, int digits) {
        mTvTime.setText(DateUtils.formatDateTime(data.getDate(), mDateFormat));
        setInfoText(mTvClosePrice, data.getClose(), lastClose, digits);
        setInfoText(mTvOpenPrice, data.getOpen(), lastClose, digits);
        setInfoText(mTvHighPrice, data.getHigh(), lastClose, digits);
        setInfoText(mTvLowPrice, data.getLow(), lastClose, digits);
        mTvVol.setText(PricingMethodUtil.getLargePrice(data.getVol()+"",3));
        double changeAmount = data.getClose() - data.getOpen();
        double rate = changeAmount / data.getOpen();

        int rateColor;
        if (rate > 0) {
            rateColor = mIncreasingColor;
        } else if (rate < 0) {
            rateColor = mDecreasingColor;
        } else {
            rateColor = mTextColor;
        }
        CharSequence rateText = StringUtil.getStringByColorValue(
                NorUtils.NumberFormat(2).format(rate * 100) + "%", rateColor);
        mTvRate.setText(rateText);
        mTvChangeAmount.setText(
                StringUtil.getStringByColorValue(NorUtils.NumberFormat(digits).format(changeAmount), rateColor));
    }

    private void setInfoText(TextView tv, double value, double lastClose, int digits) {
//        int valueColor;
//        if (lastClose == 0) {
//            valueColor = mTextColor;
//        } else {
//            if (value - lastClose > 0) {
//                valueColor = mIncreasingColor;
//            } else if (value - lastClose < 0) {
//                valueColor = mDecreasingColor;
//            } else {
//                valueColor = mTextColor;
//            }
//        }
//        CharSequence valueText = StringUtil.getStringByColor(getContext(), NorUtils.NumberFormat(digits).format(value), valueColor);
//        tv.setText(valueText);
        tv.setText(NorUtils.NumberFormat(digits).format(value));
    }
}
