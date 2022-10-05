package huolongluo.byw.reform.trade.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.trade.bean.BuyOrderBean;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.DoubleUtils;

/**
 * Created by hy on 2018/11/14 0014.
 */
public class BuyAdapter extends RecyclerView.Adapter<BuyAdapter.BuyViewHolder> {

    public interface OnClickListener {

        void onPrice(String onePrice, String totalPrice);
    }

    int fcountNum = 3;
    int fcountPrice = 4;

    public void setCount(int fcountPrice, int fcountNum) {
        this.fcountNum = fcountNum;
        this.fcountPrice = fcountPrice;
    }

    private static int length = 20;
    private List<BuyOrderBean> list = new ArrayList<>();
    public int resId;
    public double totalAmount;
    private OnClickListener onClickListener;

    public BuyAdapter(List<BuyOrderBean> list, Context context) {
        if (list == null || list.size() == 0) {
            return;
        }
        this.list.addAll(list);
        length = Integer.parseInt(SPUtils.getString(context, SPUtils.Trade_dangwei, 20 + ""));
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

    public void update(List<BuyOrderBean> list) {
        if (list != null) {
            this.list.clear();
            this.list.addAll(list);
        }
        totalAmount = getMaxAmountBuy();
        // notifyDataSetChanged();
        isReset = false;
        notifyDataSetChanged();
    }

    public void setDangWei(int length) {
        BuyAdapter.length = length;
        totalAmount = getMaxAmountBuy();
        notifyDataSetChanged();
    }

    @Override
    public BuyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trade_buy_item, parent, false);
        BuyViewHolder viewHolder = new BuyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BuyViewHolder holder, int position) {
        if (isReset) {
            holder.position_tv.setText((position + 1) + "");
            holder.number_tv.setText("--");
            holder.price_tv.setText("--");
            return;
        }
        if (list.size() < position) {
            return;
        }
        BuyOrderBean bean = list.get(position);
        holder.position_tv.setText((position + 1) + "");
        holder.number_tv.setText(NorUtils.NumberFormat(fcountNum).format(DoubleUtils.parseDouble(bean.getAmount())) + "");
        holder.price_tv.setText(NorUtils.NumberFormat(fcountPrice).format(DoubleUtils.parseDouble(bean.getPrice())) + "");
        // holder.progressbar.setMax((int) totalAmount);
        //Log.e("progressbar","totalAmount= "+totalAmount+ " ;getAmount= "+getProgress(Double.parseDouble(bean.getAmount())));
        holder.progressbar.setProgress(AppUtils.getProgress(DoubleUtils.parseDouble(bean.getAmount()), totalAmount));
        holder.item_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    Double amount = 0d;
                    for (int i = 0; i < position + 1; i++) {
                        if (list.size() > i) {
                            amount = amount + DoubleUtils.parseDouble(list.get(i).getAmount());
                        }
                    }
                    onClickListener.onPrice(bean.getPrice(), amount.doubleValue() + "");
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
            return 10;
        }
        if (list.size() < length) {
            return list.size();
        } else {
            return length;
        }
    }

    int getProgress(double amount) {
        Logger.getInstance().debug("progressbar", "amount: " + amount + " totalAmount: " + totalAmount);
        if (totalAmount == 0.0) {
            return 0;
        }
        double value = amount * 100 / totalAmount;
        int am = (int) (Math.ceil(value));
        return am <= 0 ? 0 : am;
    }

    public double getMaxAmountBuy() {
        double max = 0d;
        int length;
        if (list.size() < BuyAdapter.length) {
            length = list.size();
        } else {
            length = BuyAdapter.length;
        }
        for (int i = 0; i < length; i++) {
            max += Double.valueOf(list.get(i).getAmount());
        }
        return max < 10 ? 10 : max;
    }

    static class BuyViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressbar;
        public TextView position_tv;
        public TextView number_tv;
        public TextView price_tv;
        public RelativeLayout item_rl;

        public BuyViewHolder(View itemView) {
            super(itemView);
            progressbar = itemView.findViewById(R.id.progressbar);
            position_tv = itemView.findViewById(R.id.position_tv);
            number_tv = itemView.findViewById(R.id.number_tv);
            price_tv = itemView.findViewById(R.id.price_tv);
            item_rl = itemView.findViewById(R.id.item_rl);
        }
    }
}
