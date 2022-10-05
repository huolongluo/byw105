package com.legend.modular_contract_sdk.ui.chart;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.legend.common.util.ThemeUtil;
import com.legend.modular_contract_sdk.R;
import com.legend.modular_contract_sdk.utils.McDateUtils;
import com.legend.modular_contract_sdk.utils.NumberStringUtil;
import com.legend.modular_contract_sdk.utils.StringUtilKt;
public class McKLineChartInfoView extends McChartInfoView {
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

    public McKLineChartInfoView(Context context) {
        this(context, null);
    }

    public McKLineChartInfoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public McKLineChartInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.mc_sdk_view_kline_chart_info, this);
        mTvOpenPrice = findViewById(R.id.tv_open_price);
        mTvClosePrice = findViewById(R.id.tv_close_price);
        mTvHighPrice = findViewById(R.id.tv_high_price);
        mTvLowPrice = findViewById(R.id.tv_low_price);
        mTvVol = findViewById(R.id.tv_vol);
        mTvRate = findViewById(R.id.tv_rate);
        mTvTime = findViewById(R.id.tv_time);
        mTvChangeAmount = findViewById(R.id.tv_change_amount);

        mIncreasingColor = ThemeUtil.INSTANCE.getUpColor(context);
        mDecreasingColor = ThemeUtil.INSTANCE.getDropColor(context);
        mTextColor = context.getResources().getColor(R.color.mc_sdk_ebe8f7);
    }

    @Override
    public void setData(double lastClose, McHisData data, int digits) {
        mTvTime.setText(McDateUtils.formatDateTime(data.getDate(), mDateFormat));
        setInfoText(mTvClosePrice, data.getClose(), lastClose, digits);
        setInfoText(mTvOpenPrice, data.getOpen(), lastClose, digits);
        setInfoText(mTvHighPrice, data.getHigh(), lastClose, digits);
        setInfoText(mTvLowPrice, data.getLow(), lastClose, digits);
        mTvVol.setText(NumberStringUtil.formatVolume(data.getVol()));
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
        CharSequence rateText = StringUtilKt.getStringByColorValue(NumberStringUtil.formatAmount(rate * 100,2)+ "%", rateColor);
        mTvRate.setText(rateText);
        mTvChangeAmount.setText(StringUtilKt.getStringByColorValue(NumberStringUtil.formatAmount(changeAmount,digits), rateColor));
    }

    private void setInfoText(TextView tv, double value, double lastClose, int digits) {
        tv.setText(NumberStringUtil.formatAmount(value,digits));
    }
}
