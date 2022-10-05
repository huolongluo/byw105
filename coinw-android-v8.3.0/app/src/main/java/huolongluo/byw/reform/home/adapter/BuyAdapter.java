package huolongluo.byw.reform.home.adapter;

import android.content.Context;
import android.util.Log;
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
import huolongluo.byw.byw.base.BaseApp;
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

    private static int length = 10;
    private List<BuyOrderBean> list = new ArrayList<>();
    public int resId;
    public double totalAmount;
    private OnClickListener onClickListener;

    public BuyAdapter(List<BuyOrderBean> list, Context context) {
        this.list.addAll(list);
        length = Integer.parseInt(SPUtils.getString(context, SPUtils.Trade_dangwei, 10 + ""));
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    boolean isReset = true;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trade_buy_item1, parent, false);
        BuyViewHolder viewHolder = new BuyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BuyViewHolder holder, int position) {
        if (isReset) {
            holder.position_tv.setText(BaseApp.getSelf().getString(R.string.cz67) + (position + 1) + "");
            holder.number_tv.setText("--");
            holder.price_tv.setText("--");
            return;
        }
        if (list.size() < position) {
            return;
        }
        BuyOrderBean bean = list.get(position);
        holder.position_tv.setText(BaseApp.getSelf().getString(R.string.cz68) + (position + 1) + "");
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
                        if (i >= list.size())
                            break;
                        amount = amount + DoubleUtils.parseDouble(list.get(i).getAmount());
                    }
                    onClickListener.onPrice(bean.getPrice(), amount.doubleValue() + "");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.i("getCount", "getCount =" + getCount());
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
        int am = (int) (amount * 100 / totalAmount);
        return am < 1 ? 1 : am;
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
            progressbar.setProgressDrawable(itemView.getResources().getDrawable(R.drawable.buy_bg));
            position_tv = itemView.findViewById(R.id.position_tv);
            number_tv = itemView.findViewById(R.id.number_tv);
            price_tv = itemView.findViewById(R.id.price_tv);
            price_tv.setTextColor(itemView.getResources().getColor(R.color.buy_color));
            item_rl = itemView.findViewById(R.id.item_rl);
        }
    }
}
