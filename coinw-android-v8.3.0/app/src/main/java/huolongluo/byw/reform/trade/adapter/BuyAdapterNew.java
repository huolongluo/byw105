package huolongluo.byw.reform.trade.adapter;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.coinw.biz.trade.TradeUtils;
import com.android.coinw.biz.trade.helper.AnimationHelper;
import com.android.coinw.biz.trade.helper.TradeDataHelper;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.reform.trade.bean.TradeOrder;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.DoubleUtils;

/**
 * Created by hy on 2018/11/14 0014.
 */
public class BuyAdapterNew extends RecyclerView.Adapter<BuyAdapterNew.BuyViewHolder> {

    public interface OnClickListener {
        void onPrice(boolean isSell, String onePrice, String totalPrice);
    }

    int fcountNum = 3;
    private int depth= PricingMethodUtil.getPrecision(AppConstants.BIZ.DEFAULT_DEPTH);//盘口深度控制

    public void setCount(int fcountPrice, int fcountNum) {
        this.fcountNum = fcountNum;
    }
    private List<TradeOrder.OrderInfo> list = new ArrayList<>();
    public int resId;
    public double totalAmount;
    private OnClickListener onClickListener;

    public BuyAdapterNew(Context context) {
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    boolean isReset = false;

    public void reset() {
        list.clear();
        isReset = true;
        notifyDataSetChanged();
    }

    public void update(boolean notify) {
        this.list = TradeUtils.getInstance().mergeBuyDepthList(this.list, depth);
        totalAmount = getMaxAmountBuy();
        isReset = false;
        if (notify) {
            notifyDataSetChanged();
        }
    }

    public void setDangWei() {
        totalAmount = getMaxAmountBuy();
        notifyDataSetChanged();
    }

    public void refreshDepth(int depth) {
        this.depth = depth;
        update(true);
    }

    @Override
    public BuyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trade_buy_item_new, parent, false);
        BuyViewHolder viewHolder = new BuyViewHolder(view);
        return viewHolder;
    }

    private TradeOrder.OrderInfo getItem(int position) {
        return list.get(position);
    }

    @Override
    public void onBindViewHolder(BuyViewHolder holder, int position) {
        if (isReset) {
            holder.number_tv.setText("--");
            holder.price_tv.setText("--");
            holder.positionTxt.setText("--");
            holder.progressbar.setProgress(0);
            return;
        }
        if (list.size() < position) {
            return;
        }
        final TradeOrder.OrderInfo bean = getItem(position);
        if (bean.buyPrice == null) {
            holder.number_tv.setText("");
            holder.price_tv.setText("");
            holder.positionTxt.setText("");
            holder.progressbar.setProgress(0);
            return;
        }
        holder.number_tv.setText(PricingMethodUtil.getLargePrice(bean.buyAmount,fcountNum));
        if (depth <= 0) {
            holder.price_tv.setText(TradeUtils.getInstance().getDepthValue(DoubleUtils.parseDouble(bean.buyPrice), depth) + "");
        } else {
            holder.price_tv.setText(NorUtils.NumberFormat(depth, RoundingMode.UP).format(DoubleUtils.parseDouble(bean.buyPrice)) + "");
        }
        holder.positionTxt.setText(position + 1 + "");
        Object obj = holder.progressbar.getTag();
        int p2 = AppUtils.getProgress(DoubleUtils.parseDouble(bean.buyAmount), totalAmount);
        if (obj instanceof TradeOrder.OrderInfo) {
            TradeOrder.OrderInfo orderBean = (TradeOrder.OrderInfo) obj;
            if (TextUtils.equals(orderBean.buyPrice, bean.buyPrice) && TextUtils.equals(orderBean.buyAmount, bean.buyAmount)) {
            } else {
                AnimationHelper.getInstance().updateProgress(holder.progressbar, p2);
            }
        } else {
            holder.progressbar.setTag(bean);
            AnimationHelper.getInstance().updateProgress(holder.progressbar, p2);
        }
        holder.item_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //数据重置状态，不可点击
                if (isReset) {
                    return;
                }
                if (onClickListener != null) {
                    Double amount = 0d;
                    for (int i = 0; i < position + 1; i++) {
                        if (list.size() > i) {
                            amount = amount + DoubleUtils.parseDouble(list.get(i).buyAmount);
                        }
                    }
                    onClickListener.onPrice(false, bean.buyPrice, amount.doubleValue() + "");
                }
            }
        });
    }

    @Override
    public void onViewRecycled(BuyAdapterNew.BuyViewHolder holder) {
        super.onViewRecycled(holder);
        Object posObj = holder.progressbar.getTag();
        if (!(posObj instanceof Integer)) {
            return;
        }
        int pos = (int) posObj;
        if (pos == 0) {
            holder.progressbar.clearAnimation();
        }
    }

    @Override
    public int getItemCount() {
        return getCount();
    }

    private int getCount() {
        if (isReset) {
            return TradeDataHelper.getInstance().getGear();
        }
        if (list.size() < TradeDataHelper.getInstance().getGear()) {
            return list.size();
        } else {
            return TradeDataHelper.getInstance().getGear();
        }
    }

    public double getMaxAmountBuy() {
        double max = 0d;
        int length;
        if (list.size() < TradeDataHelper.getInstance().getGear()) {
            length = list.size();
        } else {
            length = TradeDataHelper.getInstance().getGear();
        }
        for (int i = 0; i < length; i++) {
            if (list.get(i).buyAmount != null) {
                max += Double.valueOf(list.get(i).buyAmount);
            }
        }
        return max < 0 ? 1 : max;
    }

    static class BuyViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressbar;
        public TextView number_tv;
        public TextView price_tv;
        public RelativeLayout item_rl;
        public TextView positionTxt;

        public BuyViewHolder(View itemView) {
            super(itemView);
            progressbar = itemView.findViewById(R.id.progressbar);
            positionTxt = itemView.findViewById(R.id.tv_position);
            number_tv = itemView.findViewById(R.id.number_tv);
            price_tv = itemView.findViewById(R.id.price_tv);
            item_rl = itemView.findViewById(R.id.item_rl);
        }
    }
}
