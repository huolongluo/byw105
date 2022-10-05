package com.legend.modular_contract_sdk.widget.depthview;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.legend.modular_contract_sdk.R;
import com.legend.modular_contract_sdk.common.theme.ThemeUtilKt;
import com.legend.modular_contract_sdk.component.market_listener.Depth.PendingOrder;
import com.legend.modular_contract_sdk.utils.NumberStringUtil;
import com.legend.modular_contract_sdk.utils.ViewUtil;

import java.util.List;

/**
 * A class About 👇
 * 关于横向深度列表的view(k线盘口深度的view)
 *
 * @author liuliu
 * @date 2018/1/27
 */

public class DepthHorizontalView extends View implements DepthFutureViewInterface {
    private final static String TAG = "PriceView";
    private final static String TEXT_PLACEHOLDER = "- -";
    private final static int ITEM_HEIGHT = 30;
    private final static int ITEM_SPACE = 0;
    private final static int HEADER_SPACE = 10;
    private final static int CENTER_SPACE = 50;

    private final static float INDEX_PADDING = 24;
    private final static float AMOUNT_PADDING = 48;
    private final static float PRICE_PADDING = 6;
    /**
     * 默认PriceView大小
     */
    private final static int DEFAULT_VIEW_WIDTH = 150;
    private final static int DEFAULT_VIEW_HEIGHT = 280;//溢出2dp防止不同手机计算误差

    private Context mContext;

    /**
     * 文字和背景画笔
     */
    private Paint mPaintText;
    private Paint mPaintArea;
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
     * 买盘/买盘文本偏移
     */
    private float indexOffset;
    /**
     * 数量文本偏移
     */
    private float sizeOffset;
    /**
     * 价格文本偏移
     */
    private float priceOffset;

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
    private String mHeaderDepthBuy;
    private String mHeaderDepthSell;
    private String mHeaderPrice;
    private String mHeaderAmount;

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

    /**
     * 设置点击事件监听
     */
    private OnItemClickListener mOnItemClickListener;
    private String mPriceUnit;
    private String mAmountUnit;

    public DepthHorizontalView(Context context) {
        this(context, null);
    }

    public DepthHorizontalView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DepthHorizontalView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

        mHeaderDepthBuy = mContext.getResources().getString(R.string.mc_sdk_contract_depth_side_buy);
        mHeaderDepthSell = mContext.getResources().getString(R.string.mc_sdk_contract_depth_side_sell);
        mHeaderPrice = mContext.getResources().getString(R.string.mc_sdk_price_usdt);
        mHeaderAmount = mContext.getResources().getString(R.string.mc_sdk_contract_number_with_unit);

        mUpColor = ThemeUtilKt.getThemeColor(mContext, R.attr.up_color);
        mUpBgColor = ThemeUtilKt.getThemeColor(mContext, R.attr.up_depth_color);
        mDropColor = ThemeUtilKt.getThemeColor(mContext, R.attr.drop_color);
        mDropBgColor = ThemeUtilKt.getThemeColor(mContext, R.attr.drop_depth_color);
        mAmountColor = ContextCompat.getColor(mContext, R.color.mc_sdk_txt_title);
        mTitleColor = ContextCompat.getColor(mContext, R.color.mc_sdk_txt_sub_title);

//        mArgbEvaluator = new ArgbEvaluator();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        itemHeight = ViewUtil.dip2px(mContext, ITEM_HEIGHT);
        itemSpace = ViewUtil.dip2px(mContext, ITEM_SPACE);
        headerSpace = ViewUtil.dip2px(mContext, HEADER_SPACE);
        centerSpace = ViewUtil.dip2px(mContext, CENTER_SPACE);

        indexOffset = ViewUtil.dip2px(mContext, INDEX_PADDING);
        sizeOffset = ViewUtil.dip2px(mContext, AMOUNT_PADDING);
        priceOffset = ViewUtil.dip2px(mContext, PRICE_PADDING);

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
            for (int i = 0; i < mMaxSize; i++) {
                if (i >= mDepthBuys.size()) {
                    drawItem(canvas, mEmptyDepth, false, i, true);
                } else {
                    drawItem(canvas, mDepthBuys.get(i), false, i, false);
                }
                if (i >= mDepthSells.size()) {
                    drawItem(canvas, mEmptyDepth, true, i, true);
                } else {
                    drawItem(canvas, mDepthSells.get(i), true, i, false);
                }
                itemY += itemHeight + itemSpace;
                textBaselineY = getTextBaseline(itemHeight, mPaintText, itemY);
            }
        }
    }


    /**
     * 绘制头部标题
     *
     * @param canvas
     */
    private void drawHeader(Canvas canvas) {
        mPaintText.setTextSize(getResources().getDimension(R.dimen.mc_sdk_txt_depth));
        mPaintText.setColor(mTitleColor);

        mPaintText.setTextAlign(Align.CENTER);
        itemX = indexOffset;
        canvas.drawText(mHeaderDepthBuy, itemX, textBaselineY, mPaintText);

        mPaintText.setTextAlign(Align.LEFT);
        itemX = sizeOffset;
        canvas.drawText(mHeaderAmount, itemX, textBaselineY, mPaintText);

        mPaintText.setTextAlign(Align.CENTER);
        itemX = getWidth() / 2.0f;
        canvas.drawText(mHeaderPrice, itemX, textBaselineY, mPaintText);

        mPaintText.setTextAlign(Align.RIGHT);
        itemX = getWidth() - sizeOffset;
        canvas.drawText(mHeaderAmount, itemX, textBaselineY, mPaintText);

        mPaintText.setTextAlign(Align.CENTER);
        itemX = getWidth() - indexOffset;
        canvas.drawText(mHeaderDepthSell, itemX, textBaselineY, mPaintText);

        mPaintText.setTextSize(ViewUtil.sp2px(mContext,12));
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
                canvas.drawRect(canvas.getWidth() / 2.0f, itemY, canvas.getWidth(), itemY + itemHeight,
                        mPaintArea);
            } else {
                double percent = Double.parseDouble(depth.getM()) / mMaxAmount;
                mPaintArea.setColor(mDropBgColor);
                canvas.drawRect(canvas.getWidth() / 2.0f, itemY, (float) (canvas.getWidth() / 2.0f + canvas.getWidth() / 2.0f * percent),
                        itemY + itemHeight,
                        mPaintArea);
            }

            mPaintText.setColor(mAmountColor);
            mPaintText.setTextAlign(Align.CENTER);
            itemX = canvas.getWidth() - indexOffset;
            canvas.drawText(String.valueOf(index + 1), itemX, textBaselineY, mPaintText);

            mPaintText.setTextAlign(Align.RIGHT);
            itemX = canvas.getWidth() - sizeOffset;
            String showAmount = NumberStringUtil.formatAmount(
                    Double.parseDouble(TextUtils.isEmpty(depth.getM()) ? "0" : depth.getM()) / mOneLotSize,
                    amountScale, NumberStringUtil.AmountStyle.NotFillZeroNoComma);
            canvas.drawText(showAmount, itemX, textBaselineY, mPaintText);

            itemX = canvas.getWidth() / 2.0f + priceOffset;
            mPaintText.setTextAlign(Align.LEFT);
            mPaintText.setColor(mDropColor);
            canvas.drawText(
                    NumberStringUtil.formatAmount(depth.getP(), priceScale, NumberStringUtil.AmountStyle.FillZeroNoComma),
                    itemX, textBaselineY, mPaintText);

        } else {
            if (checked && !checkSellOrBuy && checkedIndex == index) {
                mPaintArea.setColor(mUpBgColor);
                canvas.drawRect(0, itemY, canvas.getWidth() / 2.0f, itemY + itemHeight,
                        mPaintArea);
            } else {
                double percent = Double.parseDouble(depth.getM()) / mMaxAmount;
                mPaintArea.setColor(mUpBgColor);
                canvas.drawRect((float) (canvas.getWidth() / 2 * (1 - percent)), itemY, canvas.getWidth() / 2.0f,
                        itemY + itemHeight,
                        mPaintArea);
            }

            mPaintText.setColor(mAmountColor);
            mPaintText.setTextAlign(Align.CENTER);
            itemX = indexOffset;
            canvas.drawText(String.valueOf(index + 1), itemX, textBaselineY, mPaintText);

            mPaintText.setTextAlign(Align.LEFT);
            itemX = sizeOffset;
            String showAmount = NumberStringUtil.formatAmount(
                    Double.parseDouble(TextUtils.isEmpty(depth.getM()) ? "0" : depth.getM()) / mOneLotSize,
                    amountScale, NumberStringUtil.AmountStyle.NotFillZeroNoComma);
            canvas.drawText(showAmount, itemX, textBaselineY, mPaintText);

            itemX = canvas.getWidth() / 2.0f - priceOffset;
            mPaintText.setTextAlign(Align.RIGHT);
            mPaintText.setColor(mUpColor);
            canvas.drawText(
                    NumberStringUtil.formatAmount(depth.getP(), priceScale, NumberStringUtil.AmountStyle.FillZeroNoComma),
                    itemX, textBaselineY, mPaintText);

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

    }

    @Override
    public void setUnit(String priceUnit, String amountUnit) {

        mPriceUnit = priceUnit;
        mAmountUnit = amountUnit;
    }

    @Override
    public void setLastIndex(String last, String lastIndex, String change, String markIndex, boolean upOrDown) {

//        mLast = String.format("%s", TextUtils.isEmpty(last) ? "- -" : last);
//        mLastIndex = String.format("%s", TextUtils.isEmpty(lastIndex) ? "- -" : lastIndex);
//        mMarkIndex = String.format("%s", TextUtils.isEmpty(markIndex) ? "- -" : markIndex);
//        mChange = String.format("%s", TextUtils.isEmpty(change) ? "- -" : change);
////        mChange = change;
//        mUpOrDown = upOrDown;
//
//        if (getWidth() != 0) {
//            float widthTitle = mPaintMeasure1.measureText(mContext.getString(R.string.mc_sdk_contract_index_price));
//            float widthLastIndex = mPaintMeasure2.measureText(mLastIndex);
//            float sum = widthTitle + widthLastIndex;
//            mLastIndexX = (getWidth() - sum) / 2 + widthTitle;
//        }
//
//        invalidate();
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

        // maxSize + 1是header的高度
        mMaxHeightSize = HEADER_SPACE + (ITEM_HEIGHT + ITEM_SPACE) * (maxSize + 1);

        requestLayout();
    }


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
    public void reset() {
        init();
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

    }
}
