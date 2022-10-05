package com.legend.modular_contract_sdk.widget.depthview;

import android.text.TextUtils;

import com.legend.modular_contract_sdk.component.market_listener.Depth;
import com.legend.modular_contract_sdk.component.market_listener.Depth.PendingOrder;
import com.legend.modular_contract_sdk.utils.NumberStringUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link DepthFutureViewDelegate}
 */
public class DepthFutureViewDelegate {
    public static final int SCALE_OFFSET_DEFAULT = 0,   // 默认
            SCALE_OFFSET_LEFT_1 = -1,   // 少一位
            SCALE_OFFSET_LEFT_2 = -2;   // 少两位

    private final DepthFutureViewInterface mView;
    private String mData;
    private String mLast;    // 最新价
    private String mLastIndex;   // 最新指数价格
    private String mMarkIndex;   // 标记价格
    private String mChange; //涨跌幅
    private boolean mLastUpOrDown;   // 暂存颜色
    private int mScaleOffset = SCALE_OFFSET_DEFAULT;      // 深度小数位数偏移量
    private int mDefaultScale;
    private OnItemClickListener mOnItemClickListener;

    private String mBid1, mAsk1;

    public DepthFutureViewDelegate(DepthFutureViewInterface view) {
        this.mView = view;
    }

    public void init() {
        mView.setData(Collections.emptyList(), Collections.emptyList(), 0, "");
        mView.setLastIndex("", "", "", "", true);
        mData = null;
        mLast = null;
        mLastIndex = null;
        mMarkIndex = null;
        mChange = null;
        mScaleOffset = SCALE_OFFSET_DEFAULT;
        mDefaultScale = 0;
    }

    public void reset() {
        init();
    }

    public void resetPaint() {
        mView.reset();
    }

    public void setType(int type) {
        mView.setType(type);
    }

    public void setUnit(String priceUnit, String amountUnit) {
        mView.setUnit(priceUnit, amountUnit);
        reset();
        mView.reset();
    }

//    /**
//     * 深度数据
//     */
//    public void setData(String data, int scale, String name) {
//        // TODO: @Enel 2018/10/27 MESS
//        try {
//            mDefaultScale = scale;
//            if (mData != data) {
//                mData = data;
//            }
//            scale = adjustScaleOffset(mDefaultScale, mScaleOffset);
//            Map<String, List<PendingOrder>> map = TickerParseUtil.parseDepth(mData);
//            List<PendingOrder> buysList = map.get("buys");
//            List<PendingOrder> sellsList = map.get("sells");
//            Collections.reverse(buysList);
//            List<PendingOrder> mFilteredBids = Depth.computeDepthData(buysList, 0, scale, 6);
//            List<PendingOrder> mFilteredAsks = Depth.computeDepthData(sellsList, 1, scale, 6);
//            mView.setData(mFilteredAsks, mFilteredBids, getMaxAmount(buysList, sellsList), name);
//            mAsk1 = mFilteredAsks.size() > 0 ? String.valueOf(mFilteredAsks.get(0).getRealPrice()) : "";
//            mBid1 = mFilteredBids.size() > 0 ? String.valueOf(mFilteredBids.get(0).getRealPrice()) : "";
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public void setData(Depth depth) {
        mView.setData(depth.getAsks(), depth.getBids(), getMaxAmount(depth.getAsks(), depth.getBids()), "");
    }

    public void setData(List<PendingOrder> sellList, List<PendingOrder> buyList) {
        mView.setData(sellList, buyList, getMaxAmount(sellList, buyList), "");
    }

    /**
     * @param scale  默认精度
     * @param offset {@link #SCALE_OFFSET_DEFAULT},{@link #SCALE_OFFSET_LEFT_1}, {@link #SCALE_OFFSET_LEFT_2},
     * @return
     */
    private int adjustScaleOffset(int scale, int offset) {
        switch (offset) {
            case SCALE_OFFSET_DEFAULT:
                break;
            case SCALE_OFFSET_LEFT_1:
                scale = scale - 1;
                break;
            case SCALE_OFFSET_LEFT_2:
                scale = scale - 2;
                break;
        }
        scale = scale < 0 ? 0 : scale;
        return scale;
    }

//    /**
//     * 合并精度
//     */
//    public void setPriceScaleOffset(int offset) {
//        mScaleOffset = offset;
//        if (mData != null) {
//            setData(mData, mDefaultScale, mView.getName());
//        }
//    }

    /**
     * 最新价格
     */
    public void setLast(String last, int lastScale, String change) {
        // 格式化
        last = NumberStringUtil.formatAmount(last, lastScale, NumberStringUtil.AmountStyle.FillZeroNoComma);
        // 颜色
//        mLastUpOrDown = Double.valueOf(TextUtils.isEmpty(mLast) ? "0" : mLast).compareTo(Double.valueOf(last)) >= 0;
        mLast = last;
        mChange = NumberStringUtil.formatAmount(
                Double.parseDouble(TextUtils.isEmpty(change) ? "0" : change) * 100,
                2, NumberStringUtil.AmountStyle.FillZeroNoComma);
        mLastUpOrDown = Double.valueOf(TextUtils.isEmpty(mChange) ? "0" : mChange).compareTo(0.0) >= 0;
        if (mLastIndex == null) {
            return;
        }
        mView.setLastIndex(last, mLastIndex, mChange, mMarkIndex, mLastUpOrDown);
    }

    public void setMaxSize(int maxSize) {
        mView.setMaxSize(maxSize);
    }

    /**
     * 最新指数价格
     */
    public void setLastIndex(String lastIndex, int scale) {
        if (scale > 0) {
            mLastIndex = NumberStringUtil.formatAmount(lastIndex, scale, NumberStringUtil.AmountStyle.FillZeroNoComma);
        } else {
            mLastIndex = lastIndex;
        }

        if (mLast == null) {
            return;
        }
        // TODO: @Enel 2018/10/27 相同就不刷新view
        mView.setLastIndex(mLast, mLastIndex, mChange, mMarkIndex, mLastUpOrDown);
    }

    /**
     * 最新指数价格
     */
    public void setMarkIndex(String markIndex, int scale) {
        mMarkIndex = NumberStringUtil.formatAmount(markIndex, scale, NumberStringUtil.AmountStyle.FillZeroNoComma);
        if (mLast == null) {
            return;
        }
        // TODO: @Enel 2018/10/27 相同就不刷新view
        mView.setLastIndex(mLast, mLastIndex, mChange, mMarkIndex, mLastUpOrDown);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
        mView.setOnItemClickListener(
                 (isBuy, depth) ->  {
                    if (depth == null) {
                        return;
                    }
                    mOnItemClickListener.onItemClicked(isBuy,
                            Arrays.asList(Double.parseDouble(depth.getP()), Double.parseDouble(depth.getM())));
                });
    }

    public void setOnIndexPriceClickListener(DepthFutureViewInterface.OnIndexPriceClickListener onIndexPriceClickListener){
        mView.setOnIndexPriceClickListener(onIndexPriceClickListener);
    }

    public interface OnItemClickListener {
        void onItemClicked(boolean isBuy, List<Double> depth);
    }

    private double getMaxAmount(List<PendingOrder> buysList, List<PendingOrder> sellsList) {
        if (buysList == null && sellsList == null) {
            return 0;
        }
        double maxAmount = 0;
        for (PendingOrder one : buysList) {
            if (Double.parseDouble(one.getM()) > maxAmount) {
                maxAmount = Double.parseDouble(one.getM());
            }
        }
        for (PendingOrder one : sellsList) {
            if (Double.parseDouble(one.getM()) > maxAmount) {
                maxAmount = Double.parseDouble(one.getM());
            }
        }
        return maxAmount;
    }

    public String getAsk1() {
        return mAsk1;
    }

    public String getBid1() {
        return mBid1;
    }

    public void setLotSize(double lotSize, int amountScale, int priceScale) {
        mView.setLotSize(lotSize, amountScale, priceScale);
    }

    public void setAmountTitle(String title) {
        mView.setAmountTitle(title);
    }

}
