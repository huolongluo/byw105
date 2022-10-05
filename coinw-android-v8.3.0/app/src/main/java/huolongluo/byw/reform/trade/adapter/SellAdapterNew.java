package huolongluo.byw.reform.trade.adapter;
import android.content.Context;
import android.text.TextUtils;
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
public class SellAdapterNew extends RecyclerView.Adapter<SellAdapterNew.BuyViewHolder> {
    private static final String TAG = "SellAdapterNew";
    public interface OnClickListener {
        void onPrice(boolean isSell, String onePrice, String totalNumber);
    }

    private OnClickListener onClickListener;
    private List<TradeOrder.OrderInfo> list = new ArrayList<>();
    public int resId;
    public double totalAmount;

    public SellAdapterNew(Context context) {
    }

    int fcountNum = 3;
    private int depth= PricingMethodUtil.getPrecision(AppConstants.BIZ.DEFAULT_DEPTH);//盘口深度控制

    public void setCount(int fcountPrice, int fcountNum) {
        this.fcountNum = fcountNum;
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
        isReset = false;
        this.list = TradeUtils.getInstance().mergeSellDepthList(this.list, depth);
        totalAmount = getMaxAmountSell();
        if (notify) {
            notifyDataSetChanged();
        }
    }

    public void setDangWei() {
        totalAmount = getMaxAmountSell();
        notifyDataSetChanged();
    }

    public void refreshDepth(int depth) {
        this.depth = depth;
        update(true);
    }

    @Override
    public BuyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trade_sell_item_new, parent, false);
        BuyViewHolder viewHolder = new BuyViewHolder(view);
        return viewHolder;
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
        holder.positionTxt.setText(position + 1 + "");
        TradeOrder.OrderInfo bean = list.get(position);
        if (bean.sellPrice == null) {
            holder.number_tv.setText("");
            holder.price_tv.setText("");
            holder.positionTxt.setText("");
            holder.progressbar.setProgress(0);
            return;
        }
        int p2 = AppUtils.getProgress(DoubleUtils.parseDouble(bean.sellAmount), totalAmount);
        holder.number_tv.setText(PricingMethodUtil.getLargePrice(bean.sellAmount,fcountNum));
        if (depth <= 0) {
            holder.price_tv.setText(TradeUtils.getInstance().getDepthValue(DoubleUtils.parseDouble(bean.sellPrice), depth) + "");
        } else {
            holder.price_tv.setText(NorUtils.NumberFormat(depth, RoundingMode.UP).format(DoubleUtils.parseDouble(bean.sellPrice)) + "");
        }
        Object obj = holder.progressbar.getTag();
        if (obj instanceof TradeOrder.OrderInfo) {
            TradeOrder.OrderInfo orderBean = (TradeOrder.OrderInfo) obj;
            if (TextUtils.equals(orderBean.sellPrice, bean.sellPrice) && TextUtils.equals(orderBean.sellAmount, bean.sellAmount)) {
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
                            amount = amount + DoubleUtils.parseDouble(list.get(i).sellAmount);
                        }
                    }
                    onClickListener.onPrice(true, bean.sellPrice, amount.doubleValue() + "");
                }
            }
        });
    }

    @Override
    public void onViewRecycled(BuyViewHolder holder) {
        super.onViewRecycled(holder);
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

    public double getMaxAmountSell() {
        double max = 0d;
        int length;
        if (list.size() < TradeDataHelper.getInstance().getGear()) {
            length = list.size();
        } else {
            length = TradeDataHelper.getInstance().getGear();
        }
        for (int i = 0; i < length; i++) {
            if (list.get(i).sellAmount != null) {
                max += Double.valueOf(list.get(i).sellAmount);
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
            progressbar.setProgressDrawable(itemView.getResources().getDrawable(R.drawable.sell_bg));
            positionTxt = itemView.findViewById(R.id.tv_position);
            number_tv = itemView.findViewById(R.id.number_tv);
            price_tv = itemView.findViewById(R.id.price_tv);
            item_rl = itemView.findViewById(R.id.item_rl);
        }
    }
}
