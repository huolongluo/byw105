package com.legend.modular_contract_sdk.ui.chart;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.legend.common.util.ThemeUtil;
import com.legend.modular_contract_sdk.BuildConfig;
import com.legend.modular_contract_sdk.R;
import com.legend.modular_contract_sdk.common.UserConfigStorage;
import com.legend.modular_contract_sdk.utils.McDataUtils;
import com.legend.modular_contract_sdk.utils.McDateUtils;
import com.legend.modular_contract_sdk.utils.NumberStringUtil;
import com.legend.modular_contract_sdk.utils.StringUtilKt;
import com.mob.tools.utils.ResHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 * kline
 * Created by guoziwei on 2017/10/26.
 */
public class McKLineView extends LinearLayout implements OnChartValueSelectedListener, McCoupleChartGestureListener.OnAxisChangeListener {
    public static final int TYPE_K = 111;
    public static final int TYPE_LINE = 222;
    public static final Comparator<McHisData> HIS_DATA_COMPARATOR = (o1, o2) -> (int) (o1.getDate() - o2.getDate());
    /**
     * 图表的类型
     */
    public int mChartType = TYPE_LINE;
    public static final int NORMAL_LINE = 0;
    /**
     * average line
     */
    public static final int AVE_LINE = 1;
    /**
     * ma指标
     */
    public static final int MA5 = 5;
    public static final int MA10 = 10;
    public static final int MA30 = 30;
    public static final int VOL_MA5 = 6;
    public static final int VOL_MA10 = 7;
    /**
     * boll指标
     */
    public static final int BOLL_UP = 31;
    public static final int BOLL_MB = 32;
    public static final int BOLL_DN = 33;
    /**
     * ema 指标
     */
    public static final int EMA5 = 34;
    public static final int EMA10 = 35;
    public static final int EMA30 = 36;
    public static final int DIF = 38;
    public static final int DEA = 39;
    public static final int K = 40;
    public static final int D = 41;
    public static final int J = 42;
    public static final int RSI1 = 43;
    public static final int RSI2 = 44;
    public static final int RSI3 = 45;
    public static final int WR10 = 46;
    public static final int WR6 = 47;
    public static final int OBV = 48;
    public static final int MAOBV = 49;
    public static final int SAR = 50;
    /**
     * 图表默认显示个数
     */
    private int MAX_COUNT = 200;
    private int INIT_COUNT = 100;
    private int MIN_COUNT = 10;
    /**
     * 图表右侧的padding
     */
    private float RIGHT_PADDING = 20.5F;
    protected McAppCombinedChart mChartPrice;
    protected McAppCombinedChart mChartVolume;
    protected McAppCombinedChart mChartIndex;
    protected YAxis axisRightPrice;
    protected YAxis axisLeftPrice;
    protected List<McHisData> mData = new ArrayList<>(300);
    protected McChartInfoView mKChartInfoView;
    protected View mProgressView;
    /**
     * 指标
     */
    private TextView mTvMainIndex;
    private TextView mTvSubIndex;
    private TextView mTvVolumeIndex;
    private View mRootIndexChart;
    private View mRootVolumeChart;
    /**
     * 水印
     */
    private View mWaterMarkView;
    /**
     * 分时图小红点动画
     */
    private McRippleBackground mPointView;
    protected Context mContext;
    private int mAxisColor;
    private int mValueColor;
    private int mTransparentColor;
    private final int mWidth;
    /**
     * yesterday close price
     */
    private double mLastClose;
    /**
     * the digits of the symbol
     */
    private int mDigits = 2;
    private int mDecreasingColor;
    private int mIncreasingColor;
    private int mBorderColor;
    private int mbackgroundColor;
    /**
     * x轴时间的格式
     */
    private String mDateFormat = "MM-dd HH:mm";
    /**
     * 是否初始化
     */
    private boolean mIsInit = false;
    /**
     * 是否是全屏样式
     */
    private boolean mIsFullScreen = false;
    private McMainIndex mMainIndex;
    private McSubIndex mSubIndex;
    private McCoupleChartGestureListener mCoupleChartGestureListener;
    private IAxisValueFormatter mAxisValueFormatter = (value, axis) -> {
        /*
         * 7. 横坐标时间轴格式
         * 	a. 分时：时:分
         * 	b. 1分：时:分
         * 	c. 5分：月-日 时:分
         * 	d. 15分：月-日 时:分
         * 	e. 30分：月-日 时:分
         * 	f. 1小时：月-日 时:分
         * 	g. 4小时：月-日 时:分
         * 	h. 6小时：月-日 时:分
         * 	i. 1天：年-月-日
         * 	j. 1周：年-月-日
         * 	k. 1月：年-月-日
         */
        String format;
        if (axis.mEntryCount > 0 && (value == axis.mEntries[0] || value == axis.mEntries[axis.mEntryCount - 1])) {
            if (TextUtils.equals(mDateFormat, "yy-MM-dd")) {
                format = "MM-dd";
            } else {
                format = "HH:mm";
            }
        } else {
            format = mDateFormat;
        }
        if (value < 0) {
            value = 0;
        }
        int index = (int) (value);
        if (mData != null && index < mData.size()) {
            long date = mData.get(index).getDate();
            return McDateUtils.format(date, format);
        }
        return "";
    };
    private Runnable chartRunnable = () -> {
        try {
            showMainIndex(mMainIndex);
            showSubIndex(mSubIndex);
            setVolumeText();
        }catch (Exception e) {
            // ignore
        }
    };

    public McKLineView(Context context) {
        this(context, null);
    }

    public McKLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public McKLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mWidth = ResHelper.getScreenWidth(context);
        LayoutInflater.from(context).inflate(R.layout.mc_sdk_view_kline, this);
        mChartPrice = findViewById(R.id.line_chart);
        mChartVolume = findViewById(R.id.vol_chart);
        mChartIndex = findViewById(R.id.index_chart);
        mRootIndexChart = findViewById(R.id.root_index_chart);
        mRootVolumeChart = findViewById(R.id.root_vol_chart);
        mWaterMarkView = findViewById(R.id.iv_water_mark);
        mKChartInfoView = findViewById(R.id.k_info);
        mKChartInfoView.setChart(mChartPrice, mChartVolume, mChartIndex);
        mProgressView = findViewById(R.id.progress);
        mTvMainIndex = findViewById(R.id.tv_main_index);
        mTvSubIndex = findViewById(R.id.tv_sub_index);
        mTvVolumeIndex = findViewById(R.id.tv_vol_index);
        mPointView = findViewById(R.id.ripple_bg);
        mAxisColor = ContextCompat.getColor(mContext, R.color.mc_sdk_axis_color);
        mValueColor = ContextCompat.getColor(mContext, R.color.mc_sdk_white);
        mTransparentColor = getResources().getColor(android.R.color.transparent);
        mDecreasingColor = ThemeUtil.INSTANCE.getDropColor(context);
        mIncreasingColor = ThemeUtil.INSTANCE.getUpColor(context);
        mBorderColor = ContextCompat.getColor(getContext(), R.color.mc_sdk_chart_border_color);
        mbackgroundColor = ContextCompat.getColor(getContext(), R.color.mc_sdk_chart_background);
        mChartVolume.setNoDataText(null);
        mChartPrice.setNoDataText(null);
        mChartIndex.setNoDataText(null);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mChartType == TYPE_LINE && mPointView != null) {
            mPointView.startRippleAnimation();
        }
    }

    protected void initChartPrice() {
        mChartPrice.setHardwareAccelerationEnabled(true);
        mChartPrice.setLogEnabled(BuildConfig.DEBUG);
        mChartPrice.setScaleEnabled(true);
        mChartPrice.setBorderWidth(1);
        mChartPrice.setDrawBorders(false);
        mChartPrice.setDragEnabled(true);
        mChartPrice.setScaleYEnabled(false);
        mChartPrice.setDoubleTapToZoomEnabled(false);
        mChartPrice.getDescription().setEnabled(false);
        mChartPrice.setAutoScaleMinMaxEnabled(true);
        mChartPrice.setHighlightPerDragEnabled(false);
        mChartPrice.setHighlightPerTapEnabled(false);
        mChartPrice.setDragDecelerationFrictionCoef(0.95F);
        mChartPrice.setDrawMarkers(true);
        McLineChartXMarkerView mvx = new McLineChartXMarkerView(mContext, mData, mDateFormat);
        mvx.setChartView(mChartPrice);
        mChartPrice.setXMarker(mvx);
        McLineChartYMarkerView mvy = new McLineChartYMarkerView(mContext, mDigits);
        mvy.setChartView(mChartPrice);
        mChartPrice.setMarker(mvy);
        Legend lineChartLegend = mChartPrice.getLegend();
        lineChartLegend.setEnabled(false);
        XAxis xAxisPrice = mChartPrice.getXAxis();
        xAxisPrice.setEnabled(true);
        xAxisPrice.setGridColor(mBorderColor);
        xAxisPrice.setDrawAxisLine(false);
        xAxisPrice.setDrawGridLines(true);
        xAxisPrice.setTextColor(mAxisColor);
        xAxisPrice.setLabelCount(6, true);
        xAxisPrice.setTextSize(8);
        xAxisPrice.setTypeface(Typeface.MONOSPACE);
        xAxisPrice.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisPrice.setAvoidFirstLastClipping(true);
        // 全屏，坐标在中间显示
        if (mIsFullScreen) {
            xAxisPrice.setDrawLabels(true);
            xAxisPrice.setValueFormatter(mAxisValueFormatter);
        } else {
            xAxisPrice.setDrawLabels(false);
        }
        xAxisPrice.setAxisMinimum(-0.5f);
        axisLeftPrice = mChartPrice.getAxisLeft();
        axisLeftPrice.setLabelCount(5, true);
        axisLeftPrice.setDrawLabels(false);
        axisLeftPrice.setDrawAxisLine(false);
        axisLeftPrice.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisLeftPrice.setTextColor(mAxisColor);
        axisLeftPrice.setDrawGridLines(false);
        axisLeftPrice.setSpaceTop(0f);
        axisLeftPrice.setSpaceBottom(0f);
        axisRightPrice = mChartPrice.getAxisRight();
        axisRightPrice.setLabelCount(5, true);
        axisRightPrice.setDrawLabels(true);
        axisRightPrice.setDrawGridLines(true);
        axisRightPrice.setGridColor(mBorderColor);
        axisRightPrice.setDrawAxisLine(false);
        axisRightPrice.setTextColor(mAxisColor);
        axisRightPrice.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisRightPrice.setValueFormatter((value, axis) -> NumberStringUtil.formatAmount(value,mDigits));
        axisRightPrice.setSpaceTop(0f);
        axisRightPrice.setSpaceBottom(0f);
        axisRightPrice.setTextSize(8);
        axisRightPrice.setTypeface(Typeface.MONOSPACE);
//        设置标签Y渲染器
        int[] colorArray = {mAxisColor, mAxisColor, mAxisColor, mAxisColor, mAxisColor};
        Transformer rightYTransformer = mChartPrice.getRendererRightYAxis().getTransformer();
        McColorContentYAxisRenderer rightColorContentYAxisRenderer = new McColorContentYAxisRenderer(mChartPrice.getViewPortHandler(), mChartPrice.getAxisRight(), rightYTransformer);
        rightColorContentYAxisRenderer.setLabelInContent(true);
        rightColorContentYAxisRenderer.setUseDefaultLabelXOffset(true);
        rightColorContentYAxisRenderer.setLabelColor(colorArray);
        mChartPrice.setRendererRightYAxis(rightColorContentYAxisRenderer);
        mChartPrice.setOnLastPointChangeListener((x, y) -> {
            if (mPointView == null) {
                return;
            }
            if (x != 0 && y != 0) {
                mPointView.startRippleAnimation();
                mPointView.setVisibility(VISIBLE);
            } else {
                mPointView.stopRippleAnimation();
                mPointView.setVisibility(GONE);
            }
            ViewGroup.LayoutParams lp = mPointView.getLayoutParams();
            mPointView.setTranslationX(x - lp.width / 2F);
            mPointView.setTranslationY(y - lp.height / 2F);
        });
    }

    protected void initChartVolume(McAppCombinedChart chart) {
        boolean isVolumeChart = (chart == mChartVolume);
        float spaceTop = 30F;
        chart.setHardwareAccelerationEnabled(true);
        chart.setLogEnabled(false);
        chart.setScaleEnabled(true);
        chart.setDrawBorders(false);
        chart.setBorderWidth(0.34f);
        chart.setBorderColor(mBorderColor);
        chart.setDragEnabled(true);
        chart.setScaleYEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setAutoScaleMinMaxEnabled(true);
        chart.setDragDecelerationEnabled(false);
        chart.setHighlightPerDragEnabled(false);
        Legend lineChartLegend = chart.getLegend();
        lineChartLegend.setEnabled(false);
        XAxis xAxisVolume = chart.getXAxis();
        xAxisVolume.setEnabled(true);
        xAxisVolume.setDrawAxisLine(false);
        xAxisVolume.setDrawGridLines(true);
        xAxisVolume.setGridColor(mBorderColor);
        xAxisVolume.setTextColor(mAxisColor);
        xAxisVolume.setAxisMinimum(-0.5f);
        xAxisVolume.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisVolume.setLabelCount(6, true);
        xAxisVolume.setAvoidFirstLastClipping(true);
        xAxisVolume.setTextSize(8);
        xAxisVolume.setTypeface(Typeface.MONOSPACE);
        xAxisVolume.setDrawLabels(true);
        xAxisVolume.setValueFormatter(mAxisValueFormatter);
        YAxis axisLeftVolume = chart.getAxisLeft();
        axisLeftVolume.setDrawLabels(false);
        axisLeftVolume.setDrawGridLines(false);
        axisLeftVolume.setDrawAxisLine(false);
        axisLeftVolume.setSpaceTop(spaceTop);
        axisLeftVolume.setSpaceBottom(0f);
        YAxis axisRightVolume = chart.getAxisRight();
        axisRightVolume.setLabelCount(1, true);
        axisRightVolume.setYOffset(8);
        axisRightVolume.setDrawBottomYLabelEntry(false);
        axisRightVolume.setDrawLabels(isVolumeChart);
        axisRightVolume.setDrawGridLines(true);
        axisRightVolume.setGridColor(mBorderColor);
        axisRightVolume.setTextColor(mAxisColor);
        axisRightVolume.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisRightVolume.setSpaceTop(spaceTop);
        axisRightVolume.setSpaceBottom(0f);
        axisRightVolume.setTextSize(8);
        axisRightVolume.setTypeface(Typeface.MONOSPACE);
        if (isVolumeChart) {
            axisRightVolume.setAxisMinimum(0);
            axisLeftVolume.setAxisMinimum(0);
            double rate = 100.0 / (100.0 + spaceTop);
            axisRightVolume.setValueFormatter((value, axis) -> NumberStringUtil.formatVolume(value * rate));
        }
    }

    private void initChartListener() {
        mCoupleChartGestureListener = new McCoupleChartGestureListener(this, mChartPrice, mChartVolume, mChartIndex);
        mChartPrice.setOnChartGestureListener(mCoupleChartGestureListener);
        mChartPrice.setOnChartValueSelectedListener(this);
        runMainThread(mChartPrice, () -> mChartPrice.setOnTouchListener(new McChartInfoViewHandler(mChartPrice, this::onNothingSelected, () -> {
            float toX = getMoveLastX();
            mChartPrice.moveViewToX(toX);
            mChartVolume.moveViewToX(toX);
            mChartIndex.moveViewToX(toX);
        })));
    }

    private float getMoveLastX() {
        return mData.size() - 1 - mChartPrice.getVisibleXRange() * 3f / 5f;
    }

    public void initChartKData(List<McHisData> hisDatas, boolean isShowBar) {
        mIsInit = true;
        mChartType = TYPE_K;
        mChartPrice.setShowBar(isShowBar);
        axisLeftPrice.setSpaceTop(8);
        axisRightPrice.setSpaceTop(8);
        axisLeftPrice.setSpaceBottom(8);
        axisRightPrice.setSpaceBottom(8);
        mData.clear();
        mData.addAll(McDataUtils.calculateHisData(hisDatas));
        mChartPrice.setRealCount(mData.size());
        ArrayList<CandleEntry> lineCJEntries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> ma5Entries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> ma10Entries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> ma30Entries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> bollUpEntries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> bollMbEntries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> bollDnEntries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> ema5Entries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> ema10Entries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> ema30Entries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> sarEntries = new ArrayList<>(MAX_COUNT);
        for (int i = 0; i < mData.size(); i++) {
            McHisData hisData = mData.get(i);
            lineCJEntries.add(new CandleEntry(i, (float) hisData.getHigh(), (float) hisData.getLow(), (float) hisData.getOpen(), (float) hisData.getClose()));
            if (!Double.isNaN(hisData.getMa5())) {
                ma5Entries.add(new Entry(i, (float) hisData.getMa5()));
            }
            if (!Double.isNaN(hisData.getMa10())) {
                ma10Entries.add(new Entry(i, (float) hisData.getMa10()));
            }
            if (!Double.isNaN(hisData.getMa30())) {
                ma30Entries.add(new Entry(i, (float) hisData.getMa30()));
            }
            if (!Double.isNaN(hisData.getUp())) {
                bollUpEntries.add(new Entry(i, (float) hisData.getUp()));
            }
            if (!Double.isNaN(hisData.getMb())) {
                bollMbEntries.add(new Entry(i, (float) hisData.getMb()));
            }
            if (!Double.isNaN(hisData.getDn())) {
                bollDnEntries.add(new Entry(i, (float) hisData.getDn()));
            }
            ema5Entries.add(new Entry(i, (float) hisData.getEma5()));
            ema10Entries.add(new Entry(i, (float) hisData.getEma10()));
            ema30Entries.add(new Entry(i, (float) hisData.getEma30()));
            if (!Double.isNaN(hisData.getSar())) {
                sarEntries.add(new Entry(i, (float) hisData.getSar()));
            }
        }
        LineData lineData = new LineData(setLine(MA5, ma5Entries), setLine(MA10, ma10Entries), setLine(MA30, ma30Entries), setLine(BOLL_UP, bollUpEntries), setLine(BOLL_MB, bollMbEntries), setLine(BOLL_DN, bollDnEntries), setLine(EMA5, ema5Entries), setLine(EMA10, ema10Entries), setLine(EMA30, ema30Entries), setLine(SAR, sarEntries));
        CandleData candleData = new CandleData(setKLine(NORMAL_LINE, lineCJEntries));
        candleData.setDigit(mDigits);
        CombinedData combinedData = new CombinedData();
        combinedData.setData(lineData);
        combinedData.setData(candleData);
        mChartPrice.setData(combinedData);
        mChartPrice.getXAxis().setAxisMaximum(Math.max(MAX_COUNT - 1, combinedData.getXMax()) + RIGHT_PADDING);
        mChartPrice.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        mMainIndex = UserConfigStorage.INSTANCE.getKLineMainIndex();
        mSubIndex = UserConfigStorage.INSTANCE.getKLineSubIndex();
        setOffset(isShowBar);
        if (MAX_COUNT != INIT_COUNT) {
            mChartPrice.zoom(MAX_COUNT * 1F / INIT_COUNT, 0, 0, 0, false);
        }
        mChartPrice.moveViewToX(mData.size() - INIT_COUNT + RIGHT_PADDING);
        initChartVolumeData();
        initChartIndexData();
        mChartPrice.post(chartRunnable);
    }
    public void initChartPriceData(List<McHisData> hisDatas, boolean isShowBar) {
        mIsInit = true;
        mChartType = TYPE_LINE;
        mChartPrice.setShowBar(isShowBar);
        int percent = 6;
        axisLeftPrice.setSpaceTop(percent);
        axisLeftPrice.setSpaceBottom(percent);
        axisRightPrice.setSpaceTop(percent);
        axisRightPrice.setSpaceBottom(percent);
        mData.clear();
        mData.addAll(McDataUtils.calculateHisData(hisDatas));
        mChartPrice.setRealCount(mData.size());
        ArrayList<Entry> lineEntries = new ArrayList<>(MAX_COUNT);
        for (int i = 0; i < mData.size(); i++) {
            McHisData hisData = mData.get(i);
            lineEntries.add(new Entry(i, (float) hisData.getClose(), hisData));
        }
        LineData lineData = new LineData(setLine(NORMAL_LINE, lineEntries));
        lineData.setDigit(mDigits);
        CombinedData combinedData = new CombinedData();
        combinedData.setData(lineData);
        mChartPrice.setData(combinedData);
        mChartPrice.getXAxis().setAxisMaximum(Math.max(MAX_COUNT - 1, combinedData.getXMax()) + RIGHT_PADDING);
        mChartPrice.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        mMainIndex = McMainIndex.NONE;
        mSubIndex = UserConfigStorage.INSTANCE.getKLineSubIndex();
        setOffset(isShowBar);
        if (MAX_COUNT != INIT_COUNT) {
            mChartPrice.zoom(MAX_COUNT * 1F / INIT_COUNT, 0, 0, 0, false);
        }
        mChartPrice.moveViewToX(mData.size() - INIT_COUNT + RIGHT_PADDING);
        initChartVolumeData();
        initChartIndexData();
        mChartPrice.post(chartRunnable);
    }

    private BarDataSet setBar(ArrayList<BarEntry> barEntries) {
        BarDataSet barDataSet = new BarDataSet(barEntries, "vol");
        barDataSet.setHighLightColor(getResources().getColor(R.color.mc_sdk_highlight_color_vertical));
        barDataSet.setDrawValues(false);
        barDataSet.setColors(mIncreasingColor, mDecreasingColor);
        return barDataSet;
    }

    private LineDataSet setLine(int type, ArrayList<Entry> lineEntries) {
        LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "ma" + type);
        lineDataSetMa.setDrawValues(false);
        lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSetMa.setLineWidth(0.8f);
        lineDataSetMa.setCircleRadius(2f);
        lineDataSetMa.setDrawCircles(false);
        lineDataSetMa.setDrawCircleHole(false);
        lineDataSetMa.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSetMa.setCubicIntensity(0.1f);
        if (type == NORMAL_LINE) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_normal_line_color));
            // 这里设置是否显示红点
            lineDataSetMa.setCircleColor(ContextCompat.getColor(mContext, R.color.mc_sdk_white));
            lineDataSetMa.setHighLightColor(ContextCompat.getColor(mContext, R.color.mc_sdk_highlight_color_vertical));
            lineDataSetMa.setHighlightLineWidth(6);
            lineDataSetMa.setHighlightHorizontalLineWidth(0.34F);
            lineDataSetMa.setHighLightHorizontalColor(ContextCompat.getColor(mContext, R.color.mc_sdk_highlight_color_horizontal));
            lineDataSetMa.setDrawFilled(true);
            lineDataSetMa.setFillDrawable(ContextCompat.getDrawable(mContext, R.drawable.mc_sdk_bg_time_line_chart));
            lineDataSetMa.setDrawValues(true);
            lineDataSetMa.setValueTypeface(Typeface.MONOSPACE);
            lineDataSetMa.setValueTextColor(mValueColor);
            lineDataSetMa.setValueTextSize(8);
            lineDataSetMa.setDrawLastCircles(true);
        } else if (type == AVE_LINE) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_ave_color));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
        } else if (type == MA5) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color1));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == MA10) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color2));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == MA30) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color3));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == VOL_MA5) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color1));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(true);
        } else if (type == VOL_MA10) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color2));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(true);
        } else if (type == BOLL_UP) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color1));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == BOLL_MB) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color2));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == BOLL_DN) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color3));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == EMA5) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color1));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == EMA10) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color2));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == EMA30) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color3));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == DEA) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color2));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == DIF) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color3));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == K) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color1));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == D) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color2));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == J) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color3));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == RSI1) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color1));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == RSI2) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color2));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == RSI3) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color3));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == WR10) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color1));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == WR6) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color2));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == OBV) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color1));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == MAOBV) {
            lineDataSetMa.setColor(getResources().getColor(R.color.mc_sdk_index_color2));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else if (type == SAR) {
            lineDataSetMa.setColor(mTransparentColor);
            lineDataSetMa.setCircleColor(getResources().getColor(R.color.mc_sdk_index_color1));
            lineDataSetMa.setCircleColorHole(mbackgroundColor);
            lineDataSetMa.setDrawCircles(true);
            lineDataSetMa.setDrawCircleHole(true);
            lineDataSetMa.setCircleRadius(1.5F);
            lineDataSetMa.setCircleHoleRadius(1F);
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setVisible(false);
        } else {
            lineDataSetMa.setVisible(false);
            lineDataSetMa.setHighlightEnabled(false);
        }
        return lineDataSetMa;
    }

    public CandleDataSet setKLine(int type, ArrayList<CandleEntry> lineEntries) {
        CandleDataSet set1 = new CandleDataSet(lineEntries, "KLine" + type);
        set1.setDrawIcons(false);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setShadowColor(Color.DKGRAY);
        set1.setShadowWidth(0.7f);
        set1.setDecreasingColor(mDecreasingColor);
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setShadowColorSameAsCandle(true);
        set1.setIncreasingColor(mIncreasingColor);
        set1.setIncreasingPaintStyle(Paint.Style.FILL);
        set1.setNeutralColor(mIncreasingColor);
        set1.setHighlightEnabled(true);
        set1.setHighlightLineWidth(6);
        set1.setHighLightColor(ContextCompat.getColor(getContext(), R.color.mc_sdk_highlight_color_vertical));
        set1.setHighlightHorizontalLineWidth(0.33F);
        set1.setHighLightHorizontalColor(ContextCompat.getColor(getContext(), R.color.mc_sdk_highlight_color_horizontal));
        if (type != NORMAL_LINE) {
            set1.setDrawValues(false);
            set1.setVisible(false);
        } else {
            set1.setDrawValues(true);
            set1.setValueTypeface(Typeface.MONOSPACE);
            set1.setValueTextColor(mValueColor);
            set1.setValueTextSize(8);
        }
        return set1;
    }

    private void initChartVolumeData() {
        ArrayList<BarEntry> barEntries = new ArrayList<>(mData.size());
        ArrayList<Entry> ma5Entries = new ArrayList<>(mData.size());
        ArrayList<Entry> ma10Entries = new ArrayList<>(mData.size());
        for (int i = 0; i < mData.size(); i++) {
            McHisData t = mData.get(i);
            barEntries.add(new BarEntry(i, (float) t.getVol(), t));
            if (mChartType == TYPE_K) {
                ma5Entries.add(new Entry(i, (float) t.getVolMa5()));
                ma10Entries.add(new Entry(i, (float) t.getVolMa10()));
            }
        }
        BarDataSet barDataSet = setBar(barEntries);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(mChartType == TYPE_LINE ? 0.15F : 0.75F);
        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(new LineData(setLine(VOL_MA5, ma5Entries), setLine(VOL_MA10, ma10Entries)));
        mChartVolume.setData(combinedData);
        mChartVolume.getXAxis().setAxisMaximum(Math.max(MAX_COUNT - 1, combinedData.getXMax()) + RIGHT_PADDING);
        mChartVolume.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        if (MAX_COUNT != INIT_COUNT) {
            mChartVolume.zoom(MAX_COUNT * 1F / INIT_COUNT, 0, 0, 0, false);
        }
        mChartVolume.moveViewToX(mData.size() - INIT_COUNT + RIGHT_PADDING);
    }

    private void initChartIndexData() {
        ArrayList<BarEntry> barEntries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> deaEntries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> difEntries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> kEntries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> dEntries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> jEntries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> rsi1Entries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> rsi2Entries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> rsi3Entries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> wr10Entries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> wr6Entries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> obvEntries = new ArrayList<>(MAX_COUNT);
        ArrayList<Entry> maobvEntries = new ArrayList<>(MAX_COUNT);
        for (int i = 0; i < mData.size(); i++) {
            McHisData hisData = mData.get(i);
            barEntries.add(new BarEntry(i, (float) hisData.getMacd()));
            deaEntries.add(new Entry(i, (float) hisData.getDea()));
            difEntries.add(new Entry(i, (float) hisData.getDiff()));
            kEntries.add(new Entry(i, (float) hisData.getK()));
            dEntries.add(new Entry(i, (float) hisData.getD()));
            jEntries.add(new Entry(i, (float) hisData.getJ()));
            rsi1Entries.add(new Entry(i, (float) hisData.getRsi1()));
            rsi2Entries.add(new Entry(i, (float) hisData.getRsi2()));
            rsi3Entries.add(new Entry(i, (float) hisData.getRsi3()));
            wr10Entries.add(new Entry(i, (float) hisData.getWr10()));
            wr6Entries.add(new Entry(i, (float) hisData.getWr6()));
            obvEntries.add(new Entry(i, (float) hisData.getObv()));
            maobvEntries.add(new Entry(i, (float) hisData.getMaobv()));
        }
        LineData lineData = new LineData(setLine(DEA, deaEntries), setLine(DIF, difEntries), setLine(K, kEntries), setLine(D, dEntries), setLine(J, jEntries), setLine(RSI1, rsi1Entries), setLine(RSI2, rsi2Entries), setLine(RSI3, rsi3Entries), setLine(WR10, wr10Entries), setLine(WR6, wr6Entries), setLine(OBV, obvEntries), setLine(MAOBV, maobvEntries));
        BarDataSet barDataSet = setBar(barEntries);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.75f);
        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(lineData);
        mChartIndex.setData(combinedData);
        mChartIndex.getXAxis().setAxisMaximum(Math.max(MAX_COUNT - 1, combinedData.getXMax()) + RIGHT_PADDING);
        mChartIndex.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        if (MAX_COUNT != INIT_COUNT) {
            mChartIndex.zoom(MAX_COUNT * 1F / INIT_COUNT, 0, 0, 0, false);
        }
        mChartIndex.moveViewToX(mData.size() - INIT_COUNT + RIGHT_PADDING);
    }

    /**
     * 刷新最后一个点的InfoView信息
     */
    private void refreshInfoView() {
        mChartPrice.post(() -> {
            McHisData hisData = getLastData();
            if (hisData == null || mChartPrice == null || mKChartInfoView == null) {
                return;
            }
            Highlight[] highlights = mChartPrice.getHighlighted();
            if (highlights != null) {
                for (Highlight h : highlights) {
                    if (h.getX() == mData.size() - 1) {
                        double lastClose;
                        if (mLastClose != 0) {
                            lastClose = mLastClose;
                        } else {
                            if (mData.size() >= 2) {
                                lastClose = mData.get(mData.size() - 2).getClose();
                            } else {
                                lastClose = 0;
                            }
                        }
                        mKChartInfoView.setData(lastClose, hisData, mDigits);
                    }
                }
            }
            if (mKChartInfoView.getVisibility() == GONE) {
                setMainIndexText();
                setSubIndexText();
                setVolumeText();
            }
        });
    }

    public void addKDataList(List<McHisData> hisDatas) {
        if (!mIsInit) {
            return;
        }
        KSetHolder KSetHolder = new KSetHolder();
        for (McHisData h : hisDatas) {
            int hisIndex = mData.lastIndexOf(h);
            if (hisIndex >= 0) {
                McHisData oldData = mData.get(hisIndex);
                McDataUtils.copyIndex(oldData, h);
                mData.set(hisIndex, h);
            } else {
                mData.add(h);
                McDataUtils.calculateHisData(mData);
            }
            mChartPrice.setRealCount(mData.size());
            McHisData hisData = mData.get(hisIndex >= 0 ? hisIndex : mData.size() - 1);
            if (hisIndex >= 0) { //若存在，改变里面的值
                CandleEntry kEntry = KSetHolder.candleDataSet.getEntryForIndex(hisIndex);
                if (kEntry != null) {
                    kEntry.setHigh((float) hisData.getHigh());
                    kEntry.setLow((float) hisData.getLow());
                    kEntry.setOpen((float) hisData.getOpen());
                    kEntry.setClose((float) hisData.getClose());
                }
                BarEntry barEntry = KSetHolder.volSet.getEntryForIndex(hisIndex);
                if (barEntry != null) {
                    barEntry.setY((float) hisData.getVol());
                    barEntry.setData(hisData);
                }
            } else {
                int i = KSetHolder.candleDataSet.getEntryCount();
                KSetHolder.candleDataSet.addEntry(new CandleEntry(i, (float) hisData.getHigh(), (float) hisData.getLow(), (float) hisData.getOpen(), (float) hisData.getClose()));
                KSetHolder.volSet.addEntry(new BarEntry(i, (float) hisData.getVol(), hisData));
                if (!Double.isNaN(hisData.getMa5())) {
                    KSetHolder.ma5Set.addEntry(new Entry(i, (float) hisData.getMa5()));
                }
                if (!Double.isNaN(hisData.getMa10())) {
                    KSetHolder.ma10Set.addEntry(new Entry(i, (float) hisData.getMa10()));
                }
                if (!Double.isNaN(hisData.getMa30())) {
                    KSetHolder.ma30Set.addEntry(new Entry(i, (float) hisData.getMa30()));
                }
                if (!Double.isNaN(hisData.getVolMa5())) {
                    KSetHolder.volMa5Set.addEntry(new Entry(i, (float) hisData.getVolMa5()));
                }
                if (!Double.isNaN(hisData.getVolMa10())) {
                    KSetHolder.volMa10Set.addEntry(new Entry(i, (float) hisData.getVolMa10()));
                }
                if (!Double.isNaN(hisData.getUp())) {
                    KSetHolder.bollUpSet.addEntry(new Entry(i, (float) hisData.getUp()));
                }
                if (!Double.isNaN(hisData.getMb())) {
                    KSetHolder.bollMbSet.addEntry(new Entry(i, (float) hisData.getMb()));
                }
                if (!Double.isNaN(hisData.getDn())) {
                    KSetHolder.bollDnSet.addEntry(new Entry(i, (float) hisData.getDn()));
                }
                KSetHolder.ema5Set.addEntry(new Entry(i, (float) hisData.getEma5()));
                KSetHolder.ema10Set.addEntry(new Entry(i, (float) hisData.getEma10()));
                KSetHolder.ema30Set.addEntry(new Entry(i, (float) hisData.getEma30()));
                if (!Double.isNaN(hisData.getSar())) {
                    KSetHolder.sarSet.addEntry(new Entry(i, (float) hisData.getSar()));
                }
                KSetHolder.macdSet.addEntry(new BarEntry(i, (float) hisData.getMacd()));
                KSetHolder.deaSet.addEntry(new Entry(i, (float) hisData.getDea()));
                KSetHolder.difSet.addEntry(new Entry(i, (float) hisData.getDiff()));
                KSetHolder.kSet.addEntry(new Entry(i, (float) hisData.getK()));
                KSetHolder.dSet.addEntry(new Entry(i, (float) hisData.getD()));
                KSetHolder.jSet.addEntry(new Entry(i, (float) hisData.getJ()));
                KSetHolder.rsi1Set.addEntry(new Entry(i, (float) hisData.getRsi1()));
                KSetHolder.rsi2Set.addEntry(new Entry(i, (float) hisData.getRsi2()));
                KSetHolder.rsi3Set.addEntry(new Entry(i, (float) hisData.getRsi3()));
                KSetHolder.wr10Set.addEntry(new Entry(i, (float) hisData.getWr10()));
                KSetHolder.wr6Set.addEntry(new Entry(i, (float) hisData.getWr6()));
                KSetHolder.obvSet.addEntry(new Entry(i, (float) hisData.getObv()));
                KSetHolder.maobvSet.addEntry(new Entry(i, (float) hisData.getMaobv()));
            }
        }
        mChartPrice.notifyDataSetChanged();
        mChartVolume.notifyDataSetChanged();
        mChartIndex.notifyDataSetChanged();
        CombinedData combinedData = KSetHolder.combinedData;
        mChartPrice.getXAxis().setAxisMaximum(Math.max(MAX_COUNT - 1, combinedData.getXMax()) + RIGHT_PADDING);
        mChartVolume.getXAxis().setAxisMaximum(Math.max(MAX_COUNT - 1, combinedData.getXMax()) + RIGHT_PADDING);
        mChartIndex.getXAxis().setAxisMaximum(Math.max(MAX_COUNT - 1, combinedData.getXMax()) + RIGHT_PADDING);
        mChartPrice.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        mChartVolume.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        mChartIndex.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        mChartPrice.postInvalidate();
        mChartVolume.postInvalidate();
        mChartIndex.postInvalidate();
        refreshInfoView();
    }

    public void addKDataListToPre(List<McHisData> hisDatas, boolean moveToLast) {
        if (!mIsInit) {
            return;
        }
        ((BarLineChartTouchListener) (mChartPrice.getOnTouchListener())).stopDeceleration();
        ((BarLineChartTouchListener) (mChartVolume.getOnTouchListener())).stopDeceleration();
        ((BarLineChartTouchListener) (mChartIndex.getOnTouchListener())).stopDeceleration();
        ViewPortHandler portHandler = mChartPrice.getViewPortHandler();
        float[] values = new float[9];
        Matrix matrix = portHandler.getMatrixTouch();
        matrix.getValues(values);
        float scaleXRate = values[Matrix.MSCALE_X] / portHandler.getMaxScaleX();
        int oldSize = mData.size();
        // 对数据进行去重+排序
        for (int i = hisDatas.size() - 1; i >= 0; i--) {
            McHisData data = hisDatas.get(i);
            if (!mData.contains(data)) {
                mData.add(0, data);
            }
        }
        Collections.sort(mData, HIS_DATA_COMPARATOR);
        int newSize = mData.size();
        // 重新计算各个指标
        McDataUtils.calculateHisData(mData);
        mChartPrice.setRealCount(mData.size());
        KSetHolder KSetHolder = new KSetHolder();
        // 这里需要重新绘制图表，把之前的图表清理掉
        KSetHolder.candleDataSet.clear();
        KSetHolder.ma5Set.clear();
        KSetHolder.ma10Set.clear();
        KSetHolder.ma30Set.clear();
        KSetHolder.volMa5Set.clear();
        KSetHolder.volMa10Set.clear();
        KSetHolder.bollUpSet.clear();
        KSetHolder.bollMbSet.clear();
        KSetHolder.bollDnSet.clear();
        KSetHolder.ema5Set.clear();
        KSetHolder.ema10Set.clear();
        KSetHolder.ema30Set.clear();
        KSetHolder.sarSet.clear();
        KSetHolder.volSet.clear();
        KSetHolder.macdSet.clear();
        KSetHolder.deaSet.clear();
        KSetHolder.difSet.clear();
        KSetHolder.kSet.clear();
        KSetHolder.dSet.clear();
        KSetHolder.jSet.clear();
        KSetHolder.rsi1Set.clear();
        KSetHolder.rsi2Set.clear();
        KSetHolder.rsi3Set.clear();
        KSetHolder.wr10Set.clear();
        KSetHolder.wr6Set.clear();
        KSetHolder.obvSet.clear();
        KSetHolder.maobvSet.clear();
        for (int i = 0; i < mData.size(); i++) {
            McHisData hisData = mData.get(i);
            KSetHolder.candleDataSet.addEntry(new CandleEntry(i, (float) hisData.getHigh(), (float) hisData.getLow(), (float) hisData.getOpen(), (float) hisData.getClose()));
            if (!Double.isNaN(hisData.getMa5())) {
                KSetHolder.ma5Set.addEntry(new Entry(i, (float) hisData.getMa5()));
            }
            if (!Double.isNaN(hisData.getMa10())) {
                KSetHolder.ma10Set.addEntry(new Entry(i, (float) hisData.getMa10()));
            }
            if (!Double.isNaN(hisData.getMa30())) {
                KSetHolder.ma30Set.addEntry(new Entry(i, (float) hisData.getMa30()));
            }
            if (!Double.isNaN(hisData.getVolMa5())) {
                KSetHolder.volMa5Set.addEntry(new Entry(i, (float) hisData.getVolMa5()));
            }
            if (!Double.isNaN(hisData.getVolMa10())) {
                KSetHolder.volMa10Set.addEntry(new Entry(i, (float) hisData.getVolMa10()));
            }
            if (!Double.isNaN(hisData.getUp())) {
                KSetHolder.bollUpSet.addEntry(new Entry(i, (float) hisData.getUp()));
            }
            if (!Double.isNaN(hisData.getMb())) {
                KSetHolder.bollMbSet.addEntry(new Entry(i, (float) hisData.getMb()));
            }
            if (!Double.isNaN(hisData.getDn())) {
                KSetHolder.bollDnSet.addEntry(new Entry(i, (float) hisData.getDn()));
            }
            KSetHolder.ema5Set.addEntry(new Entry(i, (float) hisData.getEma5()));
            KSetHolder.ema10Set.addEntry(new Entry(i, (float) hisData.getEma10()));
            KSetHolder.ema30Set.addEntry(new Entry(i, (float) hisData.getMa30()));
            if (!Double.isNaN(hisData.getSar())) {
                KSetHolder.sarSet.addEntry(new Entry(i, (float) hisData.getSar()));
            }
            KSetHolder.volSet.addEntry(new BarEntry(i, (float) hisData.getVol(), hisData));
            KSetHolder.macdSet.addEntry(new BarEntry(i, (float) hisData.getMacd()));
            KSetHolder.deaSet.addEntry(new Entry(i, (float) hisData.getDea()));
            KSetHolder.difSet.addEntry(new Entry(i, (float) hisData.getDiff()));
            KSetHolder.kSet.addEntry(new Entry(i, (float) hisData.getK()));
            KSetHolder.dSet.addEntry(new Entry(i, (float) hisData.getD()));
            KSetHolder.jSet.addEntry(new Entry(i, (float) hisData.getJ()));
            KSetHolder.rsi1Set.addEntry(new Entry(i, (float) hisData.getRsi1()));
            KSetHolder.rsi2Set.addEntry(new Entry(i, (float) hisData.getRsi2()));
            KSetHolder.rsi3Set.addEntry(new Entry(i, (float) hisData.getRsi3()));
            KSetHolder.wr10Set.addEntry(new Entry(i, (float) hisData.getWr10()));
            KSetHolder.wr6Set.addEntry(new Entry(i, (float) hisData.getWr6()));
            KSetHolder.obvSet.addEntry(new Entry(i, (float) hisData.getObv()));
            KSetHolder.maobvSet.addEntry(new Entry(i, (float) hisData.getMaobv()));
        }
        CombinedData combinedData = KSetHolder.combinedData;
        combinedData.notifyDataChanged();
        mChartPrice.getXAxis().setAxisMaximum(Math.max(MAX_COUNT - 1, combinedData.getXMax()) + RIGHT_PADDING);
        mChartVolume.getXAxis().setAxisMaximum(Math.max(MAX_COUNT - 1, combinedData.getXMax()) + RIGHT_PADDING);
        mChartIndex.getXAxis().setAxisMaximum(Math.max(MAX_COUNT - 1, combinedData.getXMax()) + RIGHT_PADDING);
        mChartPrice.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        mChartVolume.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        mChartIndex.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        mChartPrice.notifyDataSetChanged();
        mChartVolume.notifyDataSetChanged();
        mChartIndex.notifyDataSetChanged();
        matrix.getValues(values);
        values[Matrix.MSCALE_X] = portHandler.getMaxScaleX() * scaleXRate;
        matrix.setValues(values);
        portHandler.refresh(matrix, mChartPrice, false);
        mChartVolume.getViewPortHandler().refresh(matrix, mChartVolume, false);
        mChartIndex.getViewPortHandler().refresh(matrix, mChartIndex, false);
        if (moveToLast) {
            mChartPrice.moveViewToX(mData.size() - 0.5f);
            mChartVolume.moveViewToX(mData.size() - 0.5f);
            mChartIndex.moveViewToX(mData.size() - 0.5f);
        } else {
            mChartPrice.moveViewToX(newSize - oldSize - 0.5f);
            mChartVolume.moveViewToX(newSize - oldSize - 0.5f);
            mChartIndex.moveViewToX(newSize - oldSize - 0.5f);
        }
    }

    public void dismissProgressBar() {
        runMainThread(mProgressView, () -> mProgressView.setVisibility(GONE));
    }

    public void showProgressBar() {
        runMainThread(mProgressView, () -> mProgressView.setVisibility(VISIBLE));
    }

    public void addLineDataListToPre(List<McHisData> hisDatas, boolean moveToLast) {
        if (!mIsInit) {
            return;
        }
        ((BarLineChartTouchListener) (mChartPrice.getOnTouchListener())).stopDeceleration();
        ((BarLineChartTouchListener) (mChartVolume.getOnTouchListener())).stopDeceleration();
        ((BarLineChartTouchListener) (mChartIndex.getOnTouchListener())).stopDeceleration();
        ViewPortHandler portHandler = mChartPrice.getViewPortHandler();
        float[] values = new float[9];
        Matrix matrix = portHandler.getMatrixTouch();
        matrix.getValues(values);
        float scaleXRate = values[Matrix.MSCALE_X] / portHandler.getMaxScaleX();
        int oldSize = mData.size();
        for (int i = hisDatas.size() - 1; i >= 0; i--) {
            McHisData data = hisDatas.get(i);
            if (!mData.contains(data)) {
                mData.add(0, data);
            }
        }
        Collections.sort(mData, HIS_DATA_COMPARATOR);
        int newSize = mData.size();
        // 重新计算各个指标
        McDataUtils.calculateHisData(mData);
        mChartPrice.setRealCount(mData.size());
        CombinedData combinedData = mChartPrice.getData();
        LineData priceData = combinedData.getLineData();
        ILineDataSet priceSet = priceData.getDataSetByIndex(0);
        IBarDataSet volSet = mChartVolume.getData().getBarData().getDataSetByIndex(0);
        // 这里需要重新绘制图表，把之前的图表清理掉
        priceSet.clear();
        volSet.clear();
        for (int i = 0; i < mData.size(); i++) {
            McHisData hisData = mData.get(i);
            priceSet.addEntry(new Entry(i, (float) hisData.getClose(), hisData));
            volSet.addEntry(new BarEntry(i, (float) hisData.getVol(), hisData));
        }
        combinedData.notifyDataChanged();
        mChartPrice.getXAxis().setAxisMaximum(Math.max(MAX_COUNT - 1, combinedData.getXMax()) + RIGHT_PADDING);
        mChartVolume.getXAxis().setAxisMaximum(Math.max(MAX_COUNT - 1, combinedData.getXMax()) + RIGHT_PADDING);
        mChartIndex.getXAxis().setAxisMaximum(Math.max(MAX_COUNT - 1, combinedData.getXMax()) + RIGHT_PADDING);
        mChartPrice.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        mChartVolume.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        mChartIndex.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        mChartPrice.notifyDataSetChanged();
        mChartVolume.notifyDataSetChanged();
        mChartIndex.notifyDataSetChanged();
        // 设置缩放和移动
        matrix.getValues(values);
        values[Matrix.MSCALE_X] = portHandler.getMaxScaleX() * scaleXRate;
        matrix.setValues(values);
        portHandler.refresh(matrix, mChartPrice, false);
        mChartVolume.getViewPortHandler().refresh(matrix, mChartVolume, false);
        if (moveToLast) {
            mChartPrice.moveViewToX(mData.size() - 0.5f);
            mChartVolume.moveViewToX(mData.size() - 0.5f);
            mChartIndex.moveViewToX(mData.size() - 0.5f);
        } else {
            mChartPrice.moveViewToX(newSize - oldSize - 0.5f);
            mChartVolume.moveViewToX(newSize - oldSize - 0.5f);
            mChartIndex.moveViewToX(newSize - oldSize - 0.5f);
        }
    }

    public void addLineDataList(List<McHisData> hisDatas) {
        if (!mIsInit) {
            return;
        }
        LineSetHolder holder = new LineSetHolder();
        for (McHisData h : hisDatas) {
            int hisIndex = mData.lastIndexOf(h);
            if (hisIndex >= 0) {
                McHisData oldData = mData.get(hisIndex);
                McDataUtils.copyIndex(oldData, h);
                mData.set(hisIndex, h);
            } else {
                mData.add(h);
                McDataUtils.calculateHisData(mData);
            }
            McHisData hisData = mData.get(hisIndex >= 0 ? hisIndex : mData.size() - 1);
            mChartPrice.setRealCount(mData.size());
            if (hisIndex >= 0) { //若存在，改变里面的值
                BarEntry barEntry = holder.volSet.getEntryForIndex(hisIndex);
                if (barEntry != null) {
                    barEntry.setY((float) hisData.getVol());
                    barEntry.setData(hisData);
                }
                Entry priceEntry = holder.priceSet.getEntryForIndex(hisIndex);
                if (priceEntry != null) {
                    priceEntry.setY((float) hisData.getClose());
                    priceEntry.setData(hisData);
                }
            } else {
                int i = holder.priceSet.getEntryCount();
                holder.priceSet.addEntry(new Entry(i, (float) hisData.getClose(), hisData));
                holder.volSet.addEntry(new BarEntry(i, (float) hisData.getVol(), hisData));
                holder.macdSet.addEntry(new BarEntry(i, (float) hisData.getMacd()));
                holder.deaSet.addEntry(new Entry(i, (float) hisData.getDea()));
                holder.difSet.addEntry(new Entry(i, (float) hisData.getDiff()));
                holder.kSet.addEntry(new Entry(i, (float) hisData.getK()));
                holder.dSet.addEntry(new Entry(i, (float) hisData.getD()));
                holder.jSet.addEntry(new Entry(i, (float) hisData.getJ()));
                holder.rsi1Set.addEntry(new Entry(i, (float) hisData.getRsi1()));
                holder.rsi2Set.addEntry(new Entry(i, (float) hisData.getRsi2()));
                holder.rsi3Set.addEntry(new Entry(i, (float) hisData.getRsi3()));
                holder.wr10Set.addEntry(new Entry(i, (float) hisData.getWr10()));
                holder.wr6Set.addEntry(new Entry(i, (float) hisData.getWr6()));
                holder.obvSet.addEntry(new Entry(i, (float) hisData.getObv()));
                holder.maobvSet.addEntry(new Entry(i, (float) hisData.getMaobv()));
            }
        }
        mChartPrice.notifyDataSetChanged();
        mChartVolume.notifyDataSetChanged();
        mChartIndex.notifyDataSetChanged();
        CombinedData combinedData = holder.combinedData;
        mChartPrice.getXAxis().setAxisMaximum(Math.max(MAX_COUNT - 1, combinedData.getXMax()) + RIGHT_PADDING);
        mChartVolume.getXAxis().setAxisMaximum(Math.max(MAX_COUNT - 1, combinedData.getXMax()) + RIGHT_PADDING);
        mChartIndex.getXAxis().setAxisMaximum(Math.max(MAX_COUNT - 1, combinedData.getXMax()) + RIGHT_PADDING);
        mChartPrice.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        mChartVolume.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        mChartIndex.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        mChartPrice.postInvalidate();
        mChartVolume.postInvalidate();
        mChartIndex.postInvalidate();
        refreshInfoView();
    }

    /**
     * align two chart
     */
    private void setOffset(boolean isShowBar) {
        int chartHeight = getResources().getDimensionPixelSize(R.dimen.mc_sdk_bottom_chart_height);
        int bottomOffset = getResources().getDimensionPixelSize(R.dimen.mc_sdk_chart_xaxis_height);
        float topOffset = Utils.convertDpToPixel(20);
        MarginLayoutParams waterMarkLp = (MarginLayoutParams) mWaterMarkView.getLayoutParams();
        if (McSubIndex.NONE.equals(mSubIndex)) { // 不显示子指标
            runMainThread(mRootIndexChart, () -> mRootIndexChart.setVisibility(GONE));
            if (isShowBar) {
                runMainThread(mRootVolumeChart, () -> mRootVolumeChart.setVisibility(VISIBLE));
                mChartPrice.setViewPortOffsets(0, topOffset, 0, chartHeight + bottomOffset);
                if (mIsFullScreen) {
                    mChartVolume.setViewPortOffsets(0, 0, 0, 0);
                } else {
                    mChartVolume.setViewPortOffsets(0, 0, 0, bottomOffset);
                }
                ViewGroup.LayoutParams bottomChartLP = mRootVolumeChart.getLayoutParams();
                if (mIsFullScreen) {
                    bottomChartLP.height = chartHeight;
                } else {
                    bottomChartLP.height = chartHeight + bottomOffset;
                }
                runMainThread(mRootVolumeChart, () -> mRootVolumeChart.setLayoutParams(bottomChartLP));
                waterMarkLp.bottomMargin = chartHeight + bottomOffset;
            } else {
                runMainThread(mRootVolumeChart, () -> mRootVolumeChart.setVisibility(GONE));
                mChartPrice.setViewPortOffsets(0, topOffset, 0, bottomOffset);
                waterMarkLp.bottomMargin = bottomOffset;
            }
        } else {
            runMainThread(mRootIndexChart, () -> mRootIndexChart.setVisibility(VISIBLE));
            if (isShowBar) {
                runMainThread(mRootVolumeChart, () -> mRootVolumeChart.setVisibility(VISIBLE));
                mChartPrice.setViewPortOffsets(0, topOffset, 0, 2 * chartHeight + bottomOffset);
                mChartVolume.setViewPortOffsets(0, 0, 0, 0);
                if (mIsFullScreen) {
                    mChartIndex.setViewPortOffsets(0, 0, 0, 0);
                } else {
                    mChartIndex.setViewPortOffsets(0, 0, 0, bottomOffset);
                }
                ViewGroup.LayoutParams bottomChartLP = mRootVolumeChart.getLayoutParams();
                bottomChartLP.height = chartHeight;
                runMainThread(mRootVolumeChart, () -> mRootVolumeChart.setLayoutParams(bottomChartLP));
                waterMarkLp.bottomMargin = 2 * chartHeight + bottomOffset;
            } else {
                runMainThread(mRootVolumeChart, () -> mRootVolumeChart.setVisibility(GONE));
                mChartPrice.setViewPortOffsets(0, topOffset, 0, chartHeight + bottomOffset);
                if (mIsFullScreen) {
                    mChartIndex.setViewPortOffsets(0, 0, 0, 0);
                } else {
                    mChartIndex.setViewPortOffsets(0, 0, 0, bottomOffset);
                }
                waterMarkLp.bottomMargin = chartHeight + bottomOffset;
            }
            ViewGroup.LayoutParams bottomChartLP = mRootIndexChart.getLayoutParams();
            if (mIsFullScreen) {
                bottomChartLP.height = chartHeight;
            } else {
                bottomChartLP.height = chartHeight + bottomOffset;
            }
            runMainThread(mRootIndexChart, () -> mRootIndexChart.setLayoutParams(bottomChartLP));
        }
        runMainThread(mWaterMarkView, () -> mWaterMarkView.setLayoutParams(waterMarkLp));
    }

    /**
     * set the count of k chart
     */
    public void setChartCount(int max, int init, int min) {
        MAX_COUNT = max;
        INIT_COUNT = init;
        MIN_COUNT = min;
        RIGHT_PADDING = (init * 1F / 5) + 0.5F;
    }

    /**
     * add limit line to chart
     */
    public void setLimitLine(double lastClose) {
        LimitLine limitLine = new LimitLine((float) lastClose);
        limitLine.enableDashedLine(5, 10, 0);
        limitLine.setLineColor(getResources().getColor(R.color.mc_sdk_limit_color));
        axisLeftPrice.addLimitLine(limitLine);
    }

    public void setLimitLine() {
        setLimitLine(mLastClose);
    }

    public void setLastClose(double lastClose) {
        mLastClose = lastClose;
        mChartPrice.setYCenter((float) lastClose);
    }

    /**
     * 配置图表，需要最先调用此方法
     * @param digits 精度
     * @param isFullScreen 是否全屏
     */
    public void config(int digits, String dateFormat, boolean isFullScreen) {
        mDigits = digits;
        mDateFormat = dateFormat;
        mIsFullScreen = isFullScreen;
        initChartPrice();
        initChartVolume(mChartVolume);
        initChartVolume(mChartIndex);
        initChartListener();
        mChartPrice.setFullScreen(isFullScreen);
        mKChartInfoView.setDateFormat(dateFormat);
    }

    public int size() {
        return mData.size();
    }

    public McHisData getFirstData() {
        if (mData != null && !mData.isEmpty()) {
            return mData.get(0);
        }
        return null;
    }

    public McHisData getLastData() {
        if (mData != null && !mData.isEmpty()) {
            return mData.get(mData.size() - 1);
        }
        return null;
    }

    public McHisData getLastMinsData(long date) {
        try {
            if (getLastData().getDate() != date) {
                return getLastData();
            } else {
                return mData.get(mData.size() - 2);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public int getCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        McChartInfoView infoView = mKChartInfoView;
        int x = (int) e.getX();
        if (x <= mData.size()) {
            // 分时图不显示弹窗
            boolean isKLine = (mChartType == TYPE_K);
            infoView.setVisibility(isKLine ? VISIBLE : GONE);
            McHisData data = mData.get(x);
            if (mLastClose == 0) {
                if (x > 0) {
                    infoView.setData(mData.get(x - 1).getClose(), data, mDigits);
                } else {
                    infoView.setData(mLastClose, data, mDigits);
                }
            } else {
                infoView.setData(mLastClose, data, mDigits);
            }
            setMainIndexByData(data);
            setSubIndexByData(data);
            setVolumeByData(data);
        }
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) infoView.getLayoutParams();
        if (h.getXPx() < mWidth / 2) {
            lp.gravity = Gravity.RIGHT;
        } else {
            lp.gravity = Gravity.LEFT;
        }
        infoView.setLayoutParams(lp);
    }

    @Override
    public void onNothingSelected() {
        mKChartInfoView.setVisibility(View.GONE);
        mChartPrice.highlightValues(null);
        setMainIndexText();
        setSubIndexText();
        setVolumeText();
    }

    @Override
    public void onAxisChange(BarLineChartBase chart) {
        float lowestVisibleX = chart.getLowestVisibleX();
        float highestVisibleX = chart.getHighestVisibleX();
        if (lowestVisibleX <= chart.getXAxis().getAxisMinimum()
                || highestVisibleX >= chart.getXAxis().getAxisMaximum())
            return;
    }

    public void setOnLoadMoreListener(McOnLoadMoreListener l) {
        if (mCoupleChartGestureListener != null) {
            mCoupleChartGestureListener.setOnLoadMoreListener(l);
        }
    }

    public void loadMoreComplete() {
        if (mCoupleChartGestureListener != null) {
            mCoupleChartGestureListener.loadMoreComplete();
        }
    }

    public void clear() {
        mIsInit = false;
        mData.clear();
        mChartPrice.setRealCount(0);
        mChartVolume.clear();
        mChartPrice.clear();
        mChartIndex.clear();
        mChartPrice.removeCallbacks(chartRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mPointView != null) {
            mPointView.stopRippleAnimation();
        }
    }

    /**
     * 显示K线主指标
     */
    public void showMainIndex(McMainIndex mainIndex) {
        if (mChartType != TYPE_K) {
            return;
        }
        mMainIndex = mainIndex;
        KSetHolder holder = new KSetHolder();
        holder.ma5Set.setVisible(false);
        holder.ma10Set.setVisible(false);
        holder.ma30Set.setVisible(false);
        holder.bollUpSet.setVisible(false);
        holder.bollMbSet.setVisible(false);
        holder.bollDnSet.setVisible(false);
        holder.ema5Set.setVisible(false);
        holder.ema10Set.setVisible(false);
        holder.ema30Set.setVisible(false);
        holder.sarSet.setVisible(false);
        switch (mainIndex) {
            case MA:
                holder.ma5Set.setVisible(true);
                holder.ma10Set.setVisible(true);
                holder.ma30Set.setVisible(true);
                break;
            case BOLL:
                holder.bollUpSet.setVisible(true);
                holder.bollMbSet.setVisible(true);
                holder.bollDnSet.setVisible(true);
                break;
            case EMA:
                holder.ema5Set.setVisible(true);
                holder.ema10Set.setVisible(true);
                holder.ema30Set.setVisible(true);
                break;
            case SAR:
                holder.sarSet.setVisible(true);
                break;
        }
        mChartPrice.notifyDataSetChanged();
        mChartPrice.postInvalidate();
        setMainIndexText();
    }

    /**
     * 显示K线副指标
     */
    public void showSubIndex(McSubIndex subIndex) {
        mSubIndex = subIndex;
        BaseSetHolder holder;
        if (mChartType == TYPE_K) {
            holder = new KSetHolder();
        } else {
            holder = new LineSetHolder();
        }
        holder.macdSet.setVisible(false);
        holder.deaSet.setVisible(false);
        holder.difSet.setVisible(false);
        holder.kSet.setVisible(false);
        holder.dSet.setVisible(false);
        holder.jSet.setVisible(false);
        holder.rsi1Set.setVisible(false);
        holder.rsi2Set.setVisible(false);
        holder.rsi3Set.setVisible(false);
        holder.wr10Set.setVisible(false);
        holder.wr6Set.setVisible(false);
        holder.obvSet.setVisible(false);
        holder.maobvSet.setVisible(false);
        switch (subIndex) {
            case MACD:
                if (mRootIndexChart.getVisibility() == GONE) {
                    setOffset(mRootVolumeChart.getVisibility() == VISIBLE);
                    mChartPrice.notifyDataSetChanged();
                    mChartPrice.postInvalidate();
                    mChartPrice.post(() -> mCoupleChartGestureListener.syncCharts());
                }
                holder.macdSet.setVisible(true);
                holder.deaSet.setVisible(true);
                holder.difSet.setVisible(true);
                mChartPrice.setShowSubIndex(true);
                break;
            case KDJ:
                if (mRootIndexChart.getVisibility() == GONE) {
                    setOffset(mRootVolumeChart.getVisibility() == VISIBLE);
                    mChartPrice.notifyDataSetChanged();
                    mChartPrice.postInvalidate();
                    mChartPrice.post(() -> mCoupleChartGestureListener.syncCharts());
                }
                holder.kSet.setVisible(true);
                holder.dSet.setVisible(true);
                holder.jSet.setVisible(true);
                mChartPrice.setShowSubIndex(true);
                break;
            case RSI:
                if (mRootIndexChart.getVisibility() == GONE) {
                    setOffset(mRootVolumeChart.getVisibility() == VISIBLE);
                    mChartPrice.notifyDataSetChanged();
                    mChartPrice.postInvalidate();
                    mChartPrice.post(() -> mCoupleChartGestureListener.syncCharts());
                }
                holder.rsi1Set.setVisible(true);
                holder.rsi2Set.setVisible(true);
                holder.rsi3Set.setVisible(true);
                mChartPrice.setShowSubIndex(true);
                break;
            case OBV:
                if (mRootIndexChart.getVisibility() == GONE) {
                    setOffset(mRootVolumeChart.getVisibility() == VISIBLE);
                    mChartPrice.notifyDataSetChanged();
                    mChartPrice.postInvalidate();
                    mChartPrice.post(() -> mCoupleChartGestureListener.syncCharts());
                }
                holder.obvSet.setVisible(true);
                holder.maobvSet.setVisible(true);
                mChartPrice.setShowSubIndex(true);
                break;
            case WR:
                if (mRootIndexChart.getVisibility() == GONE) {
                    setOffset(mRootVolumeChart.getVisibility() == VISIBLE);
                    mChartPrice.notifyDataSetChanged();
                    mChartPrice.postInvalidate();
                    mChartPrice.post(() -> mCoupleChartGestureListener.syncCharts());
                }
                holder.wr10Set.setVisible(true);
                holder.wr6Set.setVisible(true);
                mChartPrice.setShowSubIndex(true);
                break;
            case NONE:
                setOffset(mRootVolumeChart.getVisibility() == VISIBLE);
                mChartPrice.notifyDataSetChanged();
                mChartPrice.postInvalidate();
                mChartPrice.setShowSubIndex(false);
                break;
        }
        mChartVolume.notifyDataSetChanged();
        mChartVolume.postInvalidate();
        mChartIndex.notifyDataSetChanged();
        mChartIndex.postInvalidate();
        setSubIndexText();
    }

    /**
     * 设置主指标的值
     */
    private void setMainIndexText() {
        McHisData lastData = getLastData();
        if (lastData == null || mTvMainIndex == null) {
            return;
        }
        setMainIndexByData(lastData);
    }

    private void setMainIndexByData(McHisData hisData) {
        if (mChartType != TYPE_K || mMainIndex == null) {
            return;
        }
        CharSequence text = null;
        switch (mMainIndex) {
            case MA:
                CharSequence ma5 = StringUtilKt.getStringByColor("MA5:" + formatIndexValue(hisData.getMa5(), mDigits) + "  ",mContext,  R.color.mc_sdk_index_color1);
                CharSequence ma10 = StringUtilKt.getStringByColor("MA10:" + formatIndexValue(hisData.getMa10(), mDigits) + "  ",mContext,  R.color.mc_sdk_index_color2);
                CharSequence ma30 = StringUtilKt.getStringByColor("MA30:" + formatIndexValue(hisData.getMa30(), mDigits) + "  ",mContext,  R.color.mc_sdk_index_color3);
                text = TextUtils.concat(ma5, ma10, ma30);
                break;
            case BOLL:
                CharSequence ub = StringUtilKt.getStringByColor("UB:" + formatIndexValue(hisData.getUp(), mDigits) + "  ",mContext,  R.color.mc_sdk_index_color1);
                CharSequence boll = StringUtilKt.getStringByColor("BOLL:" + formatIndexValue(hisData.getMb(), mDigits) + "  ",mContext,  R.color.mc_sdk_index_color2);
                CharSequence lb = StringUtilKt.getStringByColor("LB:" + formatIndexValue(hisData.getDn(), mDigits) + "  ", mContext, R.color.mc_sdk_index_color3);
                text = TextUtils.concat(boll, ub, lb);
                break;
            case EMA:
                CharSequence ema5 = StringUtilKt.getStringByColor("EMA5:" + formatIndexValue(hisData.getEma5(), mDigits) + "  ",mContext,  R.color.mc_sdk_index_color1);
                CharSequence ema10 = StringUtilKt.getStringByColor("EMA10:" + formatIndexValue(hisData.getEma10(), mDigits) + "  ",mContext,  R.color.mc_sdk_index_color2);
                CharSequence ema20 = StringUtilKt.getStringByColor( "EMA30:" + formatIndexValue(hisData.getEma30(), mDigits) + "  ",mContext,  R.color.mc_sdk_index_color3);
                text = TextUtils.concat(ema5, ema10, ema20);
                break;
            case SAR:
                CharSequence sar = StringUtilKt.getStringByColor("SAR(2,20) SAR:" + formatIndexValue(hisData.getSar(), mDigits) + "  ", mContext, R.color.mc_sdk_index_color1);
                text = TextUtils.concat(sar);
                break;
        }
        mTvMainIndex.setText(text);
    }

    /**
     * 设置子指标的值
     */
    private void setSubIndexText() {
        McHisData lastData = getLastData();
        if (lastData == null || mTvSubIndex == null) {
            return;
        }
        setSubIndexByData(lastData);
    }

    private void setSubIndexByData(McHisData hisData) {
        if (mSubIndex == null) {
            return;
        }
        CharSequence text = null;
        switch (mSubIndex) {
            case MACD:
                CharSequence macdName = StringUtilKt.getStringByColor("MACD(12,26,9)  ",mContext,  R.color.mc_sdk_index_name_color);
                CharSequence macd = StringUtilKt.getStringByColor( "MACD:" + formatIndexValue(hisData.getMacd(), mDigits) + "  ", mContext, R.color.mc_sdk_index_color1);
                CharSequence dif = StringUtilKt.getStringByColor( "DIF:" + formatIndexValue(hisData.getDiff(), mDigits) + "  ",mContext,  R.color.mc_sdk_index_color2);
                CharSequence dea = StringUtilKt.getStringByColor( "DEA:" + formatIndexValue(hisData.getDea(), mDigits) + "  ", mContext, R.color.mc_sdk_index_color3);
                text = TextUtils.concat(macdName, macd, dif, dea);
                break;
            case KDJ:
                // kdj保留两位精度
                CharSequence kdjName = StringUtilKt.getStringByColor( "KDJ(9,3,3)  ",mContext,  R.color.mc_sdk_index_name_color);
                CharSequence k = StringUtilKt.getStringByColor( "K:" + formatIndexValue(hisData.getK(), 2) + "  ", mContext, R.color.mc_sdk_index_color1);
                CharSequence d = StringUtilKt.getStringByColor("D:" + formatIndexValue(hisData.getD(), 2) + "  ", mContext, R.color.mc_sdk_index_color2);
                CharSequence j = StringUtilKt.getStringByColor( "J:" + formatIndexValue(hisData.getJ(), 2) + "  ", mContext, R.color.mc_sdk_index_color3);
                text = TextUtils.concat(kdjName, k, d, j);
                break;
            case RSI:
                CharSequence rsi6 = StringUtilKt.getStringByColor( "RSI6:" + formatIndexValue(hisData.getRsi1(), mDigits) + "  ",mContext,  R.color.mc_sdk_index_color1);
                CharSequence rsi12 = StringUtilKt.getStringByColor("RSI12:" + formatIndexValue(hisData.getRsi2(), mDigits) + "  ",mContext,  R.color.mc_sdk_index_color2);
                CharSequence rsi24 = StringUtilKt.getStringByColor( "RSI24:" + formatIndexValue(hisData.getRsi3(), mDigits) + "  ",mContext,  R.color.mc_sdk_index_color3);
                text = TextUtils.concat(rsi6, rsi12, rsi24);
                break;
            case OBV:
                CharSequence obv = StringUtilKt.getStringByColor( "OBV(30) OBV:" + formatIndexValue(hisData.getObv(), mDigits) + "  ",mContext,  R.color.mc_sdk_index_color1);
                CharSequence maobv = StringUtilKt.getStringByColor( "MAOBV:" + formatIndexValue(hisData.getMaobv(), mDigits) + "  ", mContext, R.color.mc_sdk_index_color2);
                text = TextUtils.concat(obv, maobv);
                break;
            case WR:
                CharSequence wr10 = StringUtilKt.getStringByColor("WR10:" + formatIndexValue(hisData.getWr10(), mDigits) + "  ", mContext, R.color.mc_sdk_index_color1);
                CharSequence wr6 = StringUtilKt.getStringByColor("WR6:" + formatIndexValue(hisData.getWr6(), mDigits) + "  ", mContext, R.color.mc_sdk_index_color2);
                text = TextUtils.concat(wr10, wr6);
                break;
        }
        mTvSubIndex.setText(text);
    }

    /**
     * 设置交易量的值
     */
    private void setVolumeText() {
        McHisData lastData = getLastData();
        if (lastData == null || mTvVolumeIndex == null) {
            return;
        }
        setVolumeByData(lastData);
    }

    private void setVolumeByData(McHisData hisData) {
        if (mTvVolumeIndex == null) {
            return;
        }
        CharSequence text = null;
        CharSequence vol = StringUtilKt.getStringByColor( "VOL:" + NumberStringUtil.formatVolume(hisData.getVol()) + "  ", mContext,R.color.mc_sdk_index_name_color);
        if (mChartType == TYPE_K) {
            CharSequence ma5 = StringUtilKt.getStringByColor( "MA5:" + NumberStringUtil.formatVolume(hisData.getVolMa5()) + "  ",mContext, R.color.mc_sdk_index_color1);
            CharSequence ma10 = StringUtilKt.getStringByColor("MA10:" + NumberStringUtil.formatVolume(hisData.getVolMa10()) + "  ",mContext, R.color.mc_sdk_index_color2);
            text = TextUtils.concat(vol, ma5, ma10);
        } else {
            text = vol;
        }
        mTvVolumeIndex.setText(text);
    }

    private String formatIndexValue(double value, int digits) {
        if (Double.isNaN(value)) {
            return "--";
        }
        return NumberStringUtil.formatAmount(value,digits) ;
    }

    public boolean isInit() {
        return mIsInit;
    }

    private void runMainThread(View v, Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
            return;
        }
        v.post(runnable);
    }

    private static class BaseSetHolder {
        IBarDataSet macdSet;
        ILineDataSet deaSet;
        ILineDataSet difSet;
        ILineDataSet kSet;
        ILineDataSet dSet;
        ILineDataSet jSet;
        ILineDataSet rsi1Set;
        ILineDataSet rsi2Set;
        ILineDataSet rsi3Set;
        ILineDataSet wr10Set;
        ILineDataSet wr6Set;
        ILineDataSet obvSet;
        ILineDataSet maobvSet;
    }

    private class KSetHolder extends BaseSetHolder {
        private CombinedData combinedData;
        private ILineDataSet ma5Set;
        private ILineDataSet ma10Set;
        private ILineDataSet ma30Set;
        private ILineDataSet volMa5Set;
        private ILineDataSet volMa10Set;
        private ILineDataSet bollUpSet;
        private ILineDataSet bollMbSet;
        private ILineDataSet bollDnSet;
        private ILineDataSet ema5Set;
        private ILineDataSet ema10Set;
        private ILineDataSet ema30Set;
        private ICandleDataSet candleDataSet;
        private IBarDataSet volSet;
        private ILineDataSet sarSet;

        KSetHolder() {
            combinedData = mChartPrice.getData();
            LineData priceData = combinedData.getLineData();
            ma5Set = priceData.getDataSetByIndex(0);
            ma10Set = priceData.getDataSetByIndex(1);
            ma30Set = priceData.getDataSetByIndex(2);
            bollUpSet = priceData.getDataSetByIndex(3);
            bollMbSet = priceData.getDataSetByIndex(4);
            bollDnSet = priceData.getDataSetByIndex(5);
            ema5Set = priceData.getDataSetByIndex(6);
            ema10Set = priceData.getDataSetByIndex(7);
            ema30Set = priceData.getDataSetByIndex(8);
            sarSet = priceData.getDataSetByIndex(9);
            CandleData kData = combinedData.getCandleData();
            candleDataSet = kData.getDataSetByIndex(0);
            CombinedData volData = mChartVolume.getData();
            volSet = volData.getBarData().getDataSetByIndex(0);
            volMa5Set = volData.getLineData().getDataSetByIndex(0);
            volMa10Set = volData.getLineData().getDataSetByIndex(1);
            CombinedData indexCombindData = mChartIndex.getData();
            LineData indexLineData = indexCombindData.getLineData();
            deaSet = indexLineData.getDataSetByIndex(0);
            difSet = indexLineData.getDataSetByIndex(1);
            kSet = indexLineData.getDataSetByIndex(2);
            dSet = indexLineData.getDataSetByIndex(3);
            jSet = indexLineData.getDataSetByIndex(4);
            rsi1Set = indexLineData.getDataSetByIndex(5);
            rsi2Set = indexLineData.getDataSetByIndex(6);
            rsi3Set = indexLineData.getDataSetByIndex(7);
            wr10Set = indexLineData.getDataSetByIndex(8);
            wr6Set = indexLineData.getDataSetByIndex(9);
            obvSet = indexLineData.getDataSetByIndex(10);
            maobvSet = indexLineData.getDataSetByIndex(11);
            macdSet = indexCombindData.getBarData().getDataSetByIndex(0);
        }
    }

    private class LineSetHolder extends BaseSetHolder {
        private CombinedData combinedData;
        private ILineDataSet priceSet;
        private IBarDataSet volSet;

        LineSetHolder() {
            combinedData = mChartPrice.getData();
            LineData priceData = combinedData.getLineData();
            priceSet = priceData.getDataSetByIndex(0);
            CombinedData volData = mChartVolume.getData();
            volSet = volData.getBarData().getDataSetByIndex(0);
            CombinedData indexCombindData = mChartIndex.getData();
            LineData indexLineData = indexCombindData.getLineData();
            deaSet = indexLineData.getDataSetByIndex(0);
            difSet = indexLineData.getDataSetByIndex(1);
            kSet = indexLineData.getDataSetByIndex(2);
            dSet = indexLineData.getDataSetByIndex(3);
            jSet = indexLineData.getDataSetByIndex(4);
            rsi1Set = indexLineData.getDataSetByIndex(5);
            rsi2Set = indexLineData.getDataSetByIndex(6);
            rsi3Set = indexLineData.getDataSetByIndex(7);
            wr10Set = indexLineData.getDataSetByIndex(8);
            wr6Set = indexLineData.getDataSetByIndex(9);
            obvSet = indexLineData.getDataSetByIndex(10);
            maobvSet = indexLineData.getDataSetByIndex(11);
            macdSet = indexCombindData.getBarData().getDataSetByIndex(0);
        }
    }
}
