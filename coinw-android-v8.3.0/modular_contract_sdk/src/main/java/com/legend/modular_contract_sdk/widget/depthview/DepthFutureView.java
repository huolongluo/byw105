package com.legend.modular_contract_sdk.widget.depthview;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.icu.math.BigDecimal;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.legend.common.view.textview.DashTextView;
import com.legend.modular_contract_sdk.R;
import com.legend.modular_contract_sdk.common.theme.ThemeUtilKt;
import com.legend.modular_contract_sdk.component.market_listener.Depth.PendingOrder;
import com.legend.modular_contract_sdk.utils.NumberStringUtil;
import com.legend.modular_contract_sdk.utils.StringUtilKt;
import com.legend.modular_contract_sdk.utils.ViewUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * A class About 👇
 * 关于USDT合约深度列表的view（交易页盘口的view）
 *
 * @author liuliu
 * @date 2018/1/27
 */

public class DepthFutureView extends View implements DepthFutureViewInterface {
    private final static String TAG = "PriceView";
    private final static String TEXT_PLACEHOLDER = "- -";
    private final static int ITEM_HEIGHT = 25;
    private final static int ITEM_SPACE = 0;
    private final static int HEADER_SPACE = 0;
    private final static int CENTER_SPACE = 50;
    private final static int LETTER_WIDTH = 6;
    private final static float LEFT_PADDING = 10;
    private final static float RIGHT_PADDING = 12;
    private final static float SIDE_BG_SIZE = 10;
    /**
     * 默认PriceView大小
     */
    private final static int DEFAULT_VIEW_WIDTH = 150;

    // 默认计算6条的高度
    private final static int DEFAULT_VIEW_HEIGHT = (ITEM_HEIGHT + ITEM_SPACE) * 2 * 6 + CENTER_SPACE + ITEM_HEIGHT;//溢出2dp防止不同手机计算误差

    private Context mContext;

    /**
     * 文字和背景画笔
     */
    private Paint mPaintText;
    private Paint mPaintArea;
    private Paint mPaintPath;

    private Path mDashPath;
    /**
     * 用于测量文字长度的画笔，只初始化一次（新增画笔维持原文字画笔作用的单一性）
     */
    private Paint mPaintMeasure1, mPaintMeasure2;

    /**
     * 文本测量对象
     */
    private Paint.FontMetrics mFontMetrics;
    /**
     * 文本高度
     */
    private float textHeight;
    /**
     * item高度
     */
    private float itemHeight;
    /**
     * item间隔
     */
    private float itemSpace;
    /**
     * 标题下方间隔，默认8dp
     */
    private float headerSpace;
    /**
     * 买入卖出中间间隔，默认10dp（8+2）
     */
    private float centerSpace;
    /**
     * item起始点坐标
     */
    private float itemX, itemY;
    /**
     * item中文字的y坐标
     */
    private float textBaselineY;
    /**
     * 数量、价格、方向文字的x轴偏移量
     */
    // private float amountCenterOffsetX = (150 - 20) / 150f;
    // private float priceCenterOffsetX = (150 - 76) / 150f;
    // private float depthCenterOffsetX = (150 - 144) / 150f;

    private float amountRightOffsetX = (150 - RIGHT_PADDING) / 150f;
    private float priceRightOffsetX = LEFT_PADDING / 150f;
    // private float depthRightOffsetX = LEFT_PADDING / 150f;

    private float mHalfSideSize = SIDE_BG_SIZE / 2 / 150f;

    /**
     * view最大高度，跟数组数量有关
     */
    private int mMaxHeightSize = DEFAULT_VIEW_HEIGHT;

    /**
     * 产品名称
     * 当产品发生变化时，才重新计算price两边的间隔
     */
    private String mName = "";

    /**
     * 单个数字所占宽度，方便计算价格和数量的偏移量
     */
    private int letterOffset;

    /**
     * 记录卖出买入第一个item的x坐标，方便计算
     */
    private float firstSellX;
    private float firstBuyX;
    /**
     * 记录手势位置
     */
    private float mTouchX, mTouchY;

    /**
     * 头部标题
     */
    private String mHeaderDepth;
    private String mHeaderPrice;
    private String mHeaderAmount;

//    /**
//     * 渐变背景色
//     */
//    private int mBuyStartColor;
//    private int mBuyEndColor;
//    private int mSellStartColor;
//    private int mSellEndColor;
//    /**
//     * side始末色
//     */
//    private int mBuySideStartColor;
//    private int mBuySideEndColor;
//    private int mSellSideStartColor;
//    private int mSellSideEndColor;
//    private ArgbEvaluator mArgbEvaluator;
//    private LinearGradient mGradient;
    /**
     * 文本颜色
     */
    private int mUpColor;
    private int mDropColor;
    private int mAmountColor;
    private int mTitleColor;
    /**
     * 背景颜色
     */
    private int mUpBgColor;
    private int mDropBgColor;

    /**
     * 列表数据
     */
    private List<PendingOrder> mDepthSells;
    private List<PendingOrder> mDepthBuys;
    /**
     * 设置单向显示最大size，方便以后拓展其他规格的size
     */
    private int mMaxSize = 6;
    /**
     * 显示数据不足时，用emptyDepth补充
     */
    private PendingOrder mEmptyDepth;
    /**
     * 最大数量，方便计算背景条百分比
     */
    private double mMaxAmount = Integer.MAX_VALUE;

    /**
     * 初始触摸点是否在Item区域内，如果处于某个Item区域，checked为true
     */
    private boolean checked;

    /**
     * 是否点击了指数价格
     */
    private boolean checkedIndexPrice;

    /**
     * checked为true时，区分初始触摸点处于卖出/买入区域，卖出为true
     */
    private boolean checkSellOrBuy;
    /**
     * 判定为item选中时，此时对应到集合的下标
     */
    private int checkedIndex;
    /**
     * 选中区域的Y轴上下限，方便计算手势移动是否还在区域内，如不在，舍弃点击事件
     */
    private float checkedAreaY1, checkedAreaY2;

    //    private boolean isSum = false;//是否显示总量
//    private int amountScale = 0;//数量保留位数
    private double mOneLotSize = 1;
    private int amountScale = 0;
    private int priceScale = 0;

    private String mLast;
    private String mLastIndex;
    private String mMarkIndex;
    private String mChange;
    /**
     * 最新指数最右x值
     */
    private float mLastIndexX;
    private boolean mUpOrDown = true;

//    private boolean mShowIndex = true;

    /**
     * 设置点击事件监听
     */
    private OnItemClickListener mOnItemClickListener;

    private OnIndexPriceClickListener mOnIndexPriceClickListener;

    /**
     * 类型 1:币币 2:合约 币币时展示折合CNY价格,合约时展示指数价格
     */
    private int mType = TYPE_CONTRACT;

    private String mPriceUnit;
    private String mAmountUnit;

    // 指数价格的位置数据
    private RectF mIndexPricePositionRect = new RectF();

    public DepthFutureView(Context context) {
        this(context, null);
    }

    public DepthFutureView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DepthFutureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        init();
    }

    private void init() {

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaintText.setTextSize(getResources().getDimension(R.dimen.mc_sdk_txt_depth));
        mPaintText.setTextAlign(Align.CENTER);

        AssetManager assetManager = getResources().getAssets();
        Typeface typeface = Typeface.createFromAsset(assetManager, "fonts/DINPRO-MEDIUM.OTF");
        mPaintText.setTypeface(typeface);

        mPaintArea = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

        mPaintMeasure1 = new Paint();
        mPaintMeasure1.setTextSize(getResources().getDimension(R.dimen.mc_sdk_txt_depth));
        mPaintMeasure2 = new Paint();
        mPaintMeasure2.setTextSize(getResources().getDimension(R.dimen.mc_sdk_txt_depth));

        // mFontMetrics = mPaintText.getFontMetrics();
        // textHeight = Math.abs(mFontMetrics.ascent) + Math.abs(mFontMetrics.descent) + Math.abs(mFontMetrics.leading);
        //        Log.d(TAG, "ascent-->" + mFontMetrics.ascent);
        //        Log.d(TAG, "bottom-->" + mFontMetrics.bottom);
        //        Log.d(TAG, "descent-->" + mFontMetrics.descent);
        //        Log.d(TAG, "top-->" + mFontMetrics.top);
        //        Log.d(TAG, "leading-->" + mFontMetrics.leading);

        mHeaderDepth = mContext.getResources().getString(R.string.mc_sdk_contract_depth_side);
        if (mType == TYPE_CONTRACT){
            mPriceUnit = mContext.getResources().getString(R.string.mc_sdk_usdt);
            mAmountUnit = mContext.getResources().getString(R.string.mc_sdk_contract_unit);
        }
        mHeaderPrice = mContext.getResources().getString(R.string.mc_sdk_price_depth_title, mPriceUnit);
        if (TextUtils.isEmpty(mAmountUnit)){
            mHeaderAmount = mContext.getResources().getString(R.string.mc_sdk_contract_number);
        } else {
            mHeaderAmount = mContext.getResources().getString(R.string.mc_sdk_amount_depth_title, mAmountUnit);
        }

        mLast = String.format("%s", TEXT_PLACEHOLDER);
        mLastIndex = String.format(" %s", TEXT_PLACEHOLDER);
        mMarkIndex = String.format(" %s", TEXT_PLACEHOLDER);
        mChange = String.format(" %s", TEXT_PLACEHOLDER);

//        mBuyStartColor = ContextCompat.getColor(mContext, R.color.item_buy_start);
//        mBuyEndColor = ContextCompat.getColor(mContext, R.color.item_buy_end);
//        mSellStartColor = ContextCompat.getColor(mContext, R.color.item_sell_start);
//        mSellEndColor = ContextCompat.getColor(mContext, R.color.item_sell_end);
//
//        mBuySideStartColor = ContextCompat.getColor(mContext, R.color.side_buy_start);
//        mBuySideEndColor = ContextCompat.getColor(mContext, R.color.side_buy_end);
//        mSellSideStartColor = ContextCompat.getColor(mContext, R.color.side_sell_start);
//        mSellSideEndColor = ContextCompat.getColor(mContext, R.color.side_sell_end);

        mUpColor = ThemeUtilKt.getThemeColor(mContext, R.attr.up_color);
        mUpBgColor = ThemeUtilKt.getThemeColor(mContext, R.attr.up_depth_color);
        mDropColor = ThemeUtilKt.getThemeColor(mContext, R.attr.drop_color);
        mDropBgColor = ThemeUtilKt.getThemeColor(mContext, R.attr.drop_depth_color);
        mAmountColor = ThemeUtilKt.getThemeColor(mContext, R.attr.col_text_content);
        mTitleColor = ThemeUtilKt.getThemeColor(mContext, R.attr.col_text_title);

        mPaintPath = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintPath.setColor(mAmountColor);
        mPaintPath.setStrokeWidth(3);
        mPaintPath.setStyle(Paint.Style.STROKE);
        mPaintPath.setPathEffect(new DashPathEffect(new float[] {5, 5}, 0));
        mDashPath = new Path();

//        mArgbEvaluator = new ArgbEvaluator();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        itemHeight = ViewUtil.dip2px(mContext, ITEM_HEIGHT);
        itemSpace = ViewUtil.dip2px(mContext, ITEM_SPACE);
        headerSpace = ViewUtil.dip2px(mContext, HEADER_SPACE);
        centerSpace = ViewUtil.dip2px(mContext, CENTER_SPACE);
        letterOffset = ViewUtil.dip2px(mContext, LETTER_WIDTH);

        // 获取宽-测量规则的模式和大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        // 获取高-测量规则的模式和大小
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // 设置wrap_content的默认宽 / 高值
        // 默认宽/高的设定并无固定依据,根据需要灵活设置
        // 类似TextView,ImageView等针对wrap_content均在onMeasure()对设置默认宽 / 高值有特殊处理,具体读者可以自行查看
        int mWidth = ViewUtil.dip2px(mContext, DEFAULT_VIEW_WIDTH);
        int mHeight = ViewUtil.dip2px(mContext, mMaxHeightSize);

        // Logger.e("onMeasure -> " + mMaxHeightSize);

        // 当布局参数设置为wrap_content时，设置默认值
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT &&
                getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, mHeight);
            // 宽 / 高任意一个布局参数为= wrap_content时，都设置默认值
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, heightSize);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, mHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        itemX = 0;
        itemY = 0;
        //ascent，descent与文本内容无关，影响因素为textSize和typeface，所以这里可以直接通过paint拿到baseline=0,ascent<0,descent>0
        // float baseY = canvas.getHeight() / 2 + Math.abs((mPaintSell.ascent()) / 2 + Math.abs(mPaintSell.descent()) / 2);
        textBaselineY = getTextBaseline(itemHeight, mPaintText, itemY);

        drawHeader(canvas);
        itemY += itemHeight + headerSpace;
        textBaselineY = getTextBaseline(itemHeight, mPaintText, itemY);

        if (mDepthSells != null) {
            for (int i = mMaxSize - 1; i >= 0; i--) {
                if (i >= mDepthSells.size()) {
                    drawItem(canvas, mEmptyDepth, true, i, true);
                } else {
                    drawItem(canvas, mDepthSells.get(i), true, i, false);
                }
                itemY += itemHeight + itemSpace;
                textBaselineY = getTextBaseline(itemHeight, mPaintText, itemY);
            }
        }
        itemY -= itemSpace;
        drawCenter(canvas, itemY);

        // 添加买入卖出中间间隔
        itemY += centerSpace;
        textBaselineY = getTextBaseline(itemHeight, mPaintText, itemY);

        if (mDepthBuys != null) {
            for (int i = 0; i < mMaxSize; i++) {
                if (i >= mDepthBuys.size()) {
                    drawItem(canvas, mEmptyDepth, false, i, true);
                } else {
                    drawItem(canvas, mDepthBuys.get(i), false, i, false);
                }
                itemY += itemHeight + itemSpace;
                textBaselineY = getTextBaseline(itemHeight, mPaintText, itemY);
            }
        }
    }

    private void drawCenter(Canvas canvas, float itemY) {
        //center里y的变化不要影响itemY的变化
        float tempY = new Float(itemY);
        float tempTextBaselineY;
        tempY += ViewUtil.dip2px(mContext,10);
        tempTextBaselineY = getTextBaseline(ViewUtil.dip2px(mContext,19), mPaintText, tempY);

        mPaintText.setTextSize(getResources().getDimension(R.dimen.mc_sdk_txt_price));
        mPaintText.setTextAlign(Align.LEFT);
        mPaintText.setFakeBoldText(true);
        if (mUpOrDown) {
            mPaintText.setColor(mUpColor);
        } else {
            mPaintText.setColor(mDropColor);
        }
        itemX = canvas.getWidth() * priceRightOffsetX;
        canvas.drawText(NumberStringUtil.formatAmount(mLast, priceScale, NumberStringUtil.AmountStyle.FillZeroNoComma),
                itemX, tempTextBaselineY, mPaintText);

        mPaintText.setTextSize(getResources().getDimension(R.dimen.mc_sdk_txt_depth));
        mPaintText.setTextAlign(Align.RIGHT);
//            mPaintText.setFakeBoldText(true);
        itemX = canvas.getWidth() * amountRightOffsetX;
        //暂时不绘制涨跌百分比
//        canvas.drawText(mChange + "%", itemX, tempTextBaselineY, mPaintText);
        if (mUpOrDown) {
            mPaintText.setColor(mUpColor);
        } else {
            mPaintText.setColor(mDropColor);
        }


        tempY += ViewUtil.dip2px(mContext,20);
        mPaintText.setTextSize(getResources().getDimension(R.dimen.mc_sdk_txt_depth));
        mPaintText.setColor(mAmountColor);
        mPaintText.setTextAlign(Align.LEFT);
        // mPaintText.setTypeface(Typeface.DEFAULT);
        mPaintText.setFakeBoldText(false);
        tempTextBaselineY = getTextBaseline(ViewUtil.dip2px(mContext,14), mPaintText, tempY);
        itemX = canvas.getWidth() * priceRightOffsetX;
        String typeStr = "";
        if(mType == TYPE_SPOT){
            typeStr = "";
        } else  if(mType == TYPE_CONTRACT){
            typeStr = mContext.getResources().getString(R.string.mc_sdk_contract_index_price);
        }

        // 指数价格的点击区域 上下各加4像素的点击范围
        mIndexPricePositionRect.left = itemX;
        mIndexPricePositionRect.top = tempTextBaselineY - getResources().getDimension(R.dimen.mc_sdk_txt_depth) + 4;
        mIndexPricePositionRect.right = itemX + mPaintText.measureText(typeStr+ " " + mLastIndex);
        mIndexPricePositionRect.bottom = tempTextBaselineY + 4;

        if (mType == TYPE_CONTRACT) {
            // 合约指数价格绘制下划线
            mDashPath.reset();
            mDashPath.moveTo(mIndexPricePositionRect.left, mIndexPricePositionRect.bottom + 4);
            mDashPath.lineTo(mIndexPricePositionRect.right, mIndexPricePositionRect.bottom + 4);
            canvas.drawPath(mDashPath, mPaintPath);
        }

        canvas.drawText( typeStr+ " " + mLastIndex,
                itemX, tempTextBaselineY, mPaintText);

        mPaintText.setTextAlign(Align.RIGHT);
        itemX = canvas.getWidth() * amountRightOffsetX;

        // 暂时不绘制标记价格
//        canvas.drawText(mContext.getResources().getString(R.string.mc_sdk_contract_mark_price) + " " + NumberStringUtil.formatAmount(mMarkIndex, priceScale, NumberStringUtil.AmountStyle.FillZeroNoComma),
//                itemX, tempTextBaselineY, mPaintText);

        mPaintText.setTextSize(getResources().getDimension(R.dimen.mc_sdk_txt_depth));
    }

    /**
     * 绘制头部标题
     *
     * @param canvas
     */
    private void drawHeader(Canvas canvas) {
        mPaintText.setTextSize(getResources().getDimension(R.dimen.mc_sdk_txt_depth));
        mPaintText.setTextAlign(Align.RIGHT);
        mPaintText.setColor(mTitleColor);
        itemX = canvas.getWidth() * amountRightOffsetX;
        canvas.drawText(mHeaderAmount, itemX, textBaselineY, mPaintText);
        itemX = canvas.getWidth() * priceRightOffsetX;
        mPaintText.setTextAlign(Align.LEFT);
        canvas.drawText(mHeaderPrice, itemX, textBaselineY, mPaintText);
        // itemX = canvas.getWidth() * depthRightOffsetX;
        // mPaintText.setTextAlign(Align.LEFT);
        // canvas.drawText(mHeaderDepth, itemX, textBaselineY, mPaintText);

        mPaintText.setTextSize(getResources().getDimension(R.dimen.mc_sdk_txt_depth));
    }

    /**
     * 绘制单个item
     *
     * @param canvas     画布
     * @param depth      深度数据对象
     * @param sellOrBuy  true：sell；  false：buy
     * @param index      下标
     * @param emptyDepth true: 居中显示- -；false：文字右对齐
     */
    private void drawItem(Canvas canvas, PendingOrder depth, boolean sellOrBuy, int index, boolean emptyDepth) {
        if (sellOrBuy) {
            if (checked && checkSellOrBuy && checkedIndex == index) {
                mPaintArea.setColor(mDropBgColor);
                canvas.drawRect(0, itemY, canvas.getWidth(), itemY + itemHeight,
                        mPaintArea);
            } else {
                double percent = Double.parseDouble(depth.getM()) / mMaxAmount;
                mPaintArea.setColor(mDropBgColor);
                canvas.drawRect((float) (canvas.getWidth() * (1 - percent)), itemY, canvas.getWidth(),
                        itemY + itemHeight,
                        mPaintArea);

                // int color = (int)mArgbEvaluator.evaluate((float)(1 - percent), mSellStartColor, mSellEndColor);
                // if (Double.isNaN(percent)) {
                //     percent = 0;
                // }
                // mGradient = new LinearGradient((float)(canvas.getWidth() * (1 - percent)), itemY, canvas.getWidth(), itemY + itemHeight, color, mSellEndColor, TileMode.REPEAT);
                // mPaintArea.setShader(mGradient);
                // canvas.drawRect((float)(canvas.getWidth() * (1 - percent)), itemY, canvas.getWidth(), itemY + itemHeight,
                //     mPaintArea);
                // mPaintArea.setShader(null);
            }

            mPaintText.setColor(mAmountColor);
            mPaintText.setTextAlign(Align.RIGHT);
            itemX = canvas.getWidth() * amountRightOffsetX;
            String showAmount = "";
            double amount = Double.parseDouble(TextUtils.isEmpty(depth.getM()) ? "0" : depth.getM()) / mOneLotSize;
            if (amount > 1000){
                // 超过1000 添加K 超过一百万 添加M ，保留3位小数
                showAmount = StringUtilKt.getNum(String.valueOf(amount), 3, false,  BigDecimal.ROUND_DOWN, false, mType == TYPE_SPOT);
            } else {
                showAmount = StringUtilKt.getNum(String.valueOf(amount), amountScale, false,  BigDecimal.ROUND_DOWN, false, mType == TYPE_SPOT);
            }

            canvas.drawText(showAmount, itemX, textBaselineY, mPaintText);

            itemX = canvas.getWidth() * priceRightOffsetX;
            mPaintText.setTextAlign(Align.LEFT);
            mPaintText.setColor(mDropColor);
            canvas.drawText(
                    NumberStringUtil.formatAmount(depth.getP(), priceScale, NumberStringUtil.AmountStyle.FillZeroNoComma),
                    itemX, textBaselineY, mPaintText);

            // itemX = canvas.getWidth() * depthRightOffsetX;
            // // int color = (int)mArgbEvaluator.evaluate((float)index / (mMaxSize - 1), mSellSideStartColor, mSellSideEndColor);
            // // mPaintArea.setColor(color);
            // // float halfSideSize = canvas.getWidth() * mHalfSideSize;
            // // canvas.drawRect(itemX - halfSideSize, itemY + itemHeight / 2 - halfSideSize, itemX + halfSideSize, itemY + itemHeight / 2 + halfSideSize, mPaintArea);
            // mPaintText.setTextAlign(Align.LEFT);
            // mPaintText.setColor(ContextCompat.getColor(mContext, R.color.ticker_drop));
            // canvas.drawText(String.format(mContext.getResources().getString(R.string.depth_sell), index + 1), itemX,
            //     textBaselineY, mPaintText);
        } else {
            if (checked && !checkSellOrBuy && checkedIndex == index) {
                mPaintArea.setColor(mUpBgColor);
                canvas.drawRect(0, itemY, canvas.getWidth(), itemY + itemHeight,
                        mPaintArea);
            } else {
                double percent = Double.parseDouble(depth.getM()) / mMaxAmount;
                mPaintArea.setColor(mUpBgColor);
                canvas.drawRect((float) (canvas.getWidth() * (1 - percent)), itemY, canvas.getWidth(),
                        itemY + itemHeight,
                        mPaintArea);

                // int color = (int)mArgbEvaluator.evaluate((float)(1 - percent), mBuyStartColor, mBuyEndColor);
                // if (Double.isNaN(percent)) {
                //     percent = 0;
                // }
                // mGradient = new LinearGradient((float)(canvas.getWidth() * (1 - percent)), itemY, canvas.getWidth(), itemY + itemHeight, color, mBuyEndColor, TileMode.REPEAT);
                // mPaintArea.setShader(mGradient);
                // canvas.drawRect((float)(canvas.getWidth() * (1 - percent)), itemY, canvas.getWidth(), itemY + itemHeight,
                //     mPaintArea);
                // mPaintArea.setShader(null);
            }

            mPaintText.setColor(mAmountColor);
            mPaintText.setTextAlign(Align.RIGHT);
            itemX = canvas.getWidth() * amountRightOffsetX;
            String showAmount = "";
            double amount = Double.parseDouble(TextUtils.isEmpty(depth.getM()) ? "0" : depth.getM()) / mOneLotSize;
            if (amount > 1000){
                // 超过1000 添加K 超过一百万 添加M ，保留3位小数
                showAmount = StringUtilKt.getNum(String.valueOf(amount), 3, false,  BigDecimal.ROUND_DOWN, false, mType == TYPE_SPOT);
            } else {
                showAmount = StringUtilKt.getNum(String.valueOf(amount), amountScale, false,  BigDecimal.ROUND_DOWN, false, mType == TYPE_SPOT);
            }
            canvas.drawText(showAmount, itemX, textBaselineY, mPaintText);

            itemX = canvas.getWidth() * priceRightOffsetX;
            mPaintText.setTextAlign(Align.LEFT);
            mPaintText.setColor(mUpColor);
            canvas.drawText(
                    NumberStringUtil.formatAmount(depth.getP(), priceScale, NumberStringUtil.AmountStyle.FillZeroNoComma),
                    itemX, textBaselineY, mPaintText);

            // itemX = canvas.getWidth() * depthRightOffsetX;
            // // int color = (int)mArgbEvaluator.evaluate((float)index / (mMaxSize - 1), mBuySideStartColor, mBuySideEndColor);
            // // mPaintArea.setColor(color);
            // // float halfSideSize = canvas.getWidth() * mHalfSideSize;
            // // canvas.drawRect(itemX - halfSideSize, itemY + itemHeight / 2 - halfSideSize, itemX + halfSideSize, itemY + itemHeight / 2 + halfSideSize, mPaintArea);
            // mPaintText.setTextAlign(Align.LEFT);
            // mPaintText.setColor(ContextCompat.getColor(mContext, R.color.ticker_up));
            // canvas.drawText(String.format(mContext.getResources().getString(R.string.depth_buy), index + 1), itemX,
            //     textBaselineY, mPaintText);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mOnItemClickListener != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //                    Log.e(TAG, "ACTION_DOWN");
                    mTouchX = event.getX();
                    mTouchY = event.getY();
                    initClickView();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //                    Log.e(TAG, "ACTION_MOVE");
                    if (checked) {
                        if (!verifyInArea(event)) {
                            checked = false;
                            invalidate();
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    //目前是选中的情况，才处理UP事件
                    //                    Log.e(TAG, "ACTION_UP");
                    if (checked) {
                        checked = false;
                        mOnItemClickListener.onItemClicked(!checkSellOrBuy, getTouchDepth());
                        invalidate();
                    }

                    if (checkedIndexPrice){
                        checkedIndexPrice = false;
                        mOnIndexPriceClickListener.onIndexPriceClicked(mLastIndex);
                    }

                    break;
                case MotionEvent.ACTION_CANCEL:
                    //                    Log.e(TAG, "ACTION_CANCEL");
                    if (checked) {
                        checked = false;
                        invalidate();
                    }
                    break;
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    private float getTextBaseline(float itemHeight, Paint paintText, float itemY) {
        return itemHeight / 2 + Math.abs((mPaintText.ascent()) / 2 + Math.abs(mPaintText.descent()) / 2) +
                itemY;
    }

    /**
     * 验证手势移动后触摸点是否还在选中的item区域内
     *
     * @param event
     * @return
     */
    private boolean verifyInArea(MotionEvent event) {
        if (event.getY() >= checkedAreaY1 && event.getY() <= checkedAreaY2) {
            return true;
        }
        return false;
    }

    /**
     * 获取选中item对应的对象depth
     *
     * @return {(int)挂单方向, (Depth)深度, (double)累加数量}
     */
    private PendingOrder getTouchDepth() {
        if (mDepthSells == null) {
            return null;
        }
        PendingOrder depth = null;
        if (checkSellOrBuy) {
            if (checkedIndex < mDepthSells.size()) {
                depth = mDepthSells.get(checkedIndex);
            }
        } else {
            if (checkedIndex < mDepthBuys.size()) {
                depth = mDepthBuys.get(checkedIndex);
            }
        }

        return depth;
    }

    /**
     * 手势Down触发时处理逻辑
     */
    private void initClickView() {

        if (mTouchX > mIndexPricePositionRect.left && mTouchX  < mIndexPricePositionRect.right && mTouchY > mIndexPricePositionRect.top && mTouchY < mIndexPricePositionRect.bottom){
            checkedIndexPrice = true;
            return;
        }

        if (firstBuyX == 0) {
            return;
        }

        for (int i = mMaxSize - 1; i >= 0; i--) {
            float top = firstSellX + (itemHeight + itemSpace) * (mMaxSize - i - 1);
            float bottom = top + itemHeight;
            if (mTouchY >= top && mTouchY <= bottom) {
                checked = true;
                checkSellOrBuy = true;
                checkedIndex = i;
                checkedAreaY1 = top;
                checkedAreaY2 = bottom;
                break;
            }
        }

        for (int i = 0; i < mMaxSize; i++) {
            float top = firstBuyX + (itemHeight + itemSpace) * i;
            float bottom = top + itemHeight;
            if (mTouchY >= top && mTouchY <= bottom) {
                checked = true;
                checkSellOrBuy = false;
                checkedIndex = i;
                checkedAreaY1 = top;
                checkedAreaY2 = bottom;
                break;
            }
        }

        //        Log.e(TAG, "itemHeight -> " + itemHeight);
        //        Log.e(TAG, "mTouchX -> " + mTouchX);
        //        Log.e(TAG, "firstSellX -> " + firstSellX);
        //        Log.e(TAG, "firstBuyX -> " + firstBuyX);
        //        Log.e(TAG, "checked -> " + checked);
        //        Log.e(TAG, "checkSellOrBuy -> " + checkSellOrBuy);
        //        Log.e(TAG, "checkedIndex -> " + checkedIndex);

        invalidate();
    }

    @Override
    public void setData(List<PendingOrder> depthSells, List<PendingOrder> depthBuys, double maxAmount, String name) {
        mDepthSells = depthSells;
        mDepthBuys = depthBuys;
        mMaxAmount = maxAmount;

        if (mDepthSells.size() < mMaxSize || mDepthBuys.size() < mMaxSize) {
            mEmptyDepth = new PendingOrder("0", "0");
        }

        firstSellX = itemHeight + headerSpace;
        firstBuyX = itemHeight + headerSpace + (itemHeight + itemSpace) * mMaxSize - itemSpace + centerSpace;

        // for (int i = 0; i < mDepthSells.size(); i++) {
        //     mDepthSells.get(i).setAmount(Math.random() * 3000000);
        // }
        // for (int i = 0; i < mDepthBuys.size(); i++) {
        //     mDepthBuys.get(i).setAmount(Math.random() * 3000000);
        // }

        if (!mName.equals(name)) {
            getMaxOffset(depthSells, depthBuys, name);
        }

        invalidate();
    }

    @Override
    public void setType(int type) {
        mType = type;
    }

    @Override
    public void setUnit(String priceUnit, String amountUnit) {

        mPriceUnit = priceUnit;
        mAmountUnit = amountUnit;
        invalidate();
    }

    @Override
    public void setLastIndex(String last, String lastIndex, String change, String markIndex, boolean upOrDown) {

        mLast = String.format("%s", TextUtils.isEmpty(last) ? "- -" : last);
        mLastIndex = String.format("%s", TextUtils.isEmpty(lastIndex) ? "- -" : lastIndex);
        mMarkIndex = String.format("%s", TextUtils.isEmpty(markIndex) ? "- -" : markIndex);
        mChange = String.format("%s", TextUtils.isEmpty(change) ? "- -" : change);
//        mChange = change;
        mUpOrDown = upOrDown;

        if (getWidth() != 0) {
            float widthTitle = mPaintMeasure1.measureText(mContext.getString(R.string.mc_sdk_contract_index_price));
            float widthLastIndex = mPaintMeasure2.measureText(mLastIndex);
            float sum = widthTitle + widthLastIndex;
            mLastIndexX = (getWidth() - sum) / 2 + widthTitle;
        }

        invalidate();
    }

    /**
     * 设置最大显示数量
     * DEFAULT_VIEW_HEIGHT对应size为6时的高度，以此为基准
     *
     * @param maxSize
     */
    @Override
    public void setMaxSize(int maxSize) {
        if (mMaxSize == maxSize) {
            return;
        }
        mMaxSize = maxSize;

        mMaxHeightSize = DEFAULT_VIEW_HEIGHT - (ITEM_HEIGHT + ITEM_SPACE) * 2 * (6 - maxSize);

        requestLayout();
    }

    /**
     * 最新指数是否可见
     *
     * @param showIndex
     */
    public void setShowIndex(boolean showIndex) {
//        mShowIndex = showIndex;
        invalidate();
    }

    @Override
    public void reset() {
        init();
        invalidate();
    }

    // public void updateUnit(String unit) {
    //     mHeaderAmount = String.format(mContext.getResources().getString(R.string.count_page), unit);
    //     if (mDepthSells != null) {
    //         getMaxOffset(mDepthSells, mDepthBuys, mName);
    //     }
    //     invalidate();
    // }

    /**
     * 切换显示单位
     *
     * @param lotSize     是否把数量折合为金额
     * @param amountScale 保留位数
     */
    @Override
    public void setLotSize(double lotSize, int amountScale, int priceScale) {
        this.mOneLotSize = lotSize;
        this.amountScale = amountScale;
        this.priceScale = priceScale;
        invalidate();
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void setAmountTitle(String title) {
        mHeaderAmount = title;
    }

    /**
     * 根据价格、数量的文字长度，计算画笔起始位置
     *
     * @param depthSells
     * @param depthBuys
     * @param name
     */
    private void getMaxOffset(List<PendingOrder> depthSells, List<PendingOrder> depthBuys, String name) {
        // float maxPriceOffset = 0;
        // float maxNumOffset = 0;
        // float maxSideOffset = 0;
        // double maxPrice = 0;
        // for (Depth depthSell : depthSells) {
        //     maxPriceOffset = Math.max(maxPriceOffset, mPaintText.measureText(depthSell.getPrice()));
        //     maxNumOffset = Math.max(maxNumOffset, mPaintText.measureText(depthSell.getAmount(isSum, amountScale)));
        //     maxPrice = Math.max(maxPrice, depthSell.getRealPrice());
        // }
        //
        // for (Depth depthBuy : depthBuys) {
        //     maxPriceOffset = Math.max(maxPriceOffset, mPaintText.measureText(depthBuy.getPrice()));
        //     maxNumOffset = Math.max(maxNumOffset, mPaintText.measureText(depthBuy.getAmount(isSum, amountScale)));
        //     maxPrice = Math.max(maxPrice, depthBuy.getRealPrice());
        // }
        //
        // maxSideOffset = mPaintText.measureText("00");
        //
        // float width = getWidth();
        // float leftSpace = LEFT_PADDING / DEFAULT_VIEW_WIDTH * width;
        // float rightSpace = RIGHT_PADDING / DEFAULT_VIEW_WIDTH * width;
        // float space = (width - leftSpace - maxSideOffset - maxPriceOffset - maxNumOffset - rightSpace) / 2;
        // priceRightOffsetX = (width - space - maxNumOffset - rightSpace) / width;
        //
        // if (maxPrice > 0) {
        //     mName = name;
        // }
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public void setOnIndexPriceClickListener(OnIndexPriceClickListener onIndexPriceClickListener) {

        mOnIndexPriceClickListener = onIndexPriceClickListener;
    }
}
