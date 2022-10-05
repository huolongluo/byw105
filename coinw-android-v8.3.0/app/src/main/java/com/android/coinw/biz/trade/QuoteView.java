package com.android.coinw.biz.trade;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.coinw.biz.trade.model.Quote;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.widget.WrapContentLinearLayoutManager;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.DoubleUtils;
public class QuoteView extends RecyclerView {
    private QuoteAdapter adapter = new QuoteAdapter();

    public QuoteView(Context context) {
        this(context, null);
    }

    public QuoteView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuoteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.setLayoutManager(new WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, true));
        try {
            String s = SPUtils.getString(context, SPUtils.Trade_dangwei, AppConstants.BIZ.KEY_BIZ_DEFAULT_GEAR_POSITION + "");
            int length = TextUtils.isEmpty(s) ? AppConstants.BIZ.KEY_BIZ_DEFAULT_GEAR_POSITION : Integer.parseInt(s);
            adapter.setLength(length);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
        this.setAdapter(adapter);
    }

    public void setResource(int priceColor, int progressColor, int clickColor) {
        adapter.setResource(priceColor, progressColor, clickColor);
    }

    /**
     * 重新绘制
     */
    public void redraw() {
        //TODO 重新绘制
    }

    /**
     * 重置数据
     */
    public void reset() {
        adapter.reset();
    }

    /**
     * 更新数据
     * @param dataList
     */
    public void update(List<Quote> dataList) {
        adapter.update(dataList);
    }

    public interface OnItemClickListener {
        void onItem(Quote quote);
    }

    private static class QuoteAdapter extends RecyclerView.Adapter<QuoteChangeViewHolder> {
        private int qttPrecision = AppConstants.BIZ.DEFAULT_QUANTITY_PRECISION;
        private int pricePrecision = AppConstants.BIZ.DEFAULT_PRICE_PRECISION;

        /**
         * 设置精度显示
         * @param qttPrecision
         * @param pricePrecision
         */
        public void setPrecision(int qttPrecision, int pricePrecision) {
            this.qttPrecision = qttPrecision;
            this.pricePrecision = pricePrecision;
        }

        private int length = AppConstants.BIZ.KEY_BIZ_DEFAULT_GEAR_POSITION;
        private double totalAmount;
        private boolean isReset = false;
        private OnItemClickListener listener;
        private List<Quote> dataList = new ArrayList<>();
        //买
        private int priceColor = R.color.color_4ED3AD, progressColor = R.color.color_1A3849, clickColor = R.color.color_1A3849;

        public void setLength(int length) {
            this.length = length;
            this.totalAmount = getMaxAmountBuy(this.dataList);
            notifyDataSetChanged();
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        public void setResource(int priceColor, int progressColor, int clickColor) {
            this.priceColor = priceColor;
            this.progressColor = progressColor;
            this.clickColor = clickColor;
        }

        public void reset() {
            this.dataList.clear();
            this.isReset = true;
            notifyDataSetChanged();
        }

        public void update(List<Quote> dataList) {
            if (dataList != null) {
                this.dataList.clear();
                this.dataList.addAll(dataList);
            }
            this.totalAmount = getMaxAmountBuy(dataList);
            this.isReset = false;
            notifyDataSetChanged();
        }

        @Override
        public QuoteChangeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //涨跌幅
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quote_change, parent, false);
            QuoteChangeViewHolder holder = new QuoteChangeViewHolder(view);
            //设置颜色值
//            holder.positionTxt.setTextColor(ContextCompat.getColor(parent.getContext(), progressColor));
            holder.priceTxt.setTextColor(ContextCompat.getColor(parent.getContext(), priceColor));
            return holder;
        }

        @Override
        public void onBindViewHolder(QuoteChangeViewHolder holder, int position) {
            if (isReset) {
                holder.positionTxt.setText((position + 1) + "");
                holder.numberTxt.setText("--");
                holder.priceTxt.setText("--");
                return;
            }
            if (dataList.size() < position) {
                return;
            }
            Quote quote = dataList.get(position);
            holder.positionTxt.setText((position + 1) + "");
            holder.numberTxt.setText(NorUtils.NumberFormat(qttPrecision).format(DoubleUtils.parseDouble(quote.amount)) + "");
            holder.priceTxt.setText(NorUtils.NumberFormat(pricePrecision).format(DoubleUtils.parseDouble(quote.price)) + "");
//            holder.progressbar.setProgress(AppUtils.getProgress(DoubleUtils.parseDouble(quote.amount), totalAmount));
            //采用动画更新显示
            QuoteUtils.setValue(holder.progressBar, AppUtils.getProgress(DoubleUtils.parseDouble(quote.amount), totalAmount));
            holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener == null) {
                        //TODO 异常处理
                        return;
                    }
                    try {
                        //请在异常实现接口内容
                        listener.onItem(quote);
                    } catch (Throwable t) {
                        Logger.getInstance().error(t);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return getCount();
        }

        private int getCount() {
            if (isReset) {
                if (dataList.size() > 10) {
                    return 10;
                } else {
                    return dataList.size();
                }
            } else if (dataList.size() < length) {
                return dataList.size();
            } else {
                return length;
            }
        }

        private int getProgress(double amount) {
            Logger.getInstance().debug("progressbar", "amount: " + amount + " totalAmount: " + totalAmount);
            if (totalAmount == 0.0) {
                return 0;
            }
            double value = amount * 100 / totalAmount;
            int am = (int) (Math.ceil(value));
            return am <= 0 ? 0 : am;
        }

        private double getMaxAmountBuy(List<Quote> dataList) {
            double max = 0d;
            int l = 10;
            if (dataList.size() < length) {
                l = dataList.size();
            } else {
                l = length;
            }
            for (int i = 0; i < l; i++) {
                max += Double.valueOf(dataList.get(i).amount);
            }
            return max < 10 ? 10 : max;
        }
    }

    private static class QuoteChangeViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public TextView positionTxt, numberTxt, priceTxt;
        public RelativeLayout itemLayout;

        public QuoteChangeViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressbar);
            positionTxt = itemView.findViewById(R.id.tv_position);
            numberTxt = itemView.findViewById(R.id.tv_number);
            priceTxt = itemView.findViewById(R.id.tv_price);
            itemLayout = itemView.findViewById(R.id.rl_item);
        }
    }

    private float mStartX;
    private float mStartY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = ev.getRawX();
                mStartY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float endY = ev.getRawY();
                float endX = ev.getRawX();
                float x = endX - mStartX;
                float y = endY - mStartY;
                /* 左右滑动不拦截,上下滑动拦截*/
                if (Math.abs(y) > Math.abs(x)) {
                    /* 已经在顶部了*/
                    if (y > 0 && !canScrollVertically(-1)) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else if (y < 0 && !canScrollVertically(1)) {
                        // 不能再上滑了 ========================
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        Log.e("Rv正在滑动", "-dx =" + dx + "---dy =" + dy);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
