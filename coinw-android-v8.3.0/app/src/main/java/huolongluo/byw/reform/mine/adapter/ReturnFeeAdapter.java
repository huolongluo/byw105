package huolongluo.byw.reform.mine.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.reform.bean.ReturnFeeBean;
import huolongluo.bywx.utils.DoubleUtils;

/**
 * Created by Administrator on 2019/1/6 0006.
 */
public class ReturnFeeAdapter extends RecyclerView.Adapter<ReturnFeeAdapter.ViewHole> {

    public List<ReturnFeeBean> list = new ArrayList<>();
    public Context context;

    @Override
    public ViewHole onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_returnfee_item, parent, false);
        ViewHole viewHole = new ViewHole(view);
        return viewHole;
    }

    @Override
    public void onBindViewHolder(ViewHole holder, int position) {
        ReturnFeeBean bean = list.get(position);
        holder.date_tv.setText(bean.getEntrustCreateTime());
        if (TextUtils.equals(bean.getEntrustType(), "0")) {
            holder.tradeSum_tv.setTextColor(context.getResources().getColor(R.color.ff40bc99));
            holder.tradeSum_tv.setText(String.format(context.getString(R.string.a3), bean.getEntrustAmount(), bean.getLeftCoinName()));
            holder.fee_tv.setText(String.format(context.getString(R.string.a4), bean.getEntrustFeeAmount(), bean.getLeftCoinName()));
        } else {
            holder.tradeSum_tv.setTextColor(context.getResources().getColor(R.color.ffff5864));
            holder.tradeSum_tv.setText(String.format(context.getString(R.string.a5), bean.getEntrustAmount(), bean.getLeftCoinName()));
            holder.fee_tv.setText(String.format(context.getString(R.string.a6), bean.getEntrustFeeAmount(), bean.getRightCoinName()));
        }
        holder.return_fee_tv.setText(String.format(context.getString(R.string.a7), bean.getReturnAmount(), bean.getReturnCoin()));
        if (DoubleUtils.parseDouble(bean.getEntrustFeeCoins()) == 0) {
            holder.retuin_coin_rl.setVisibility(View.GONE);
        } else {
            holder.retuin_coin_rl.setVisibility(View.VISIBLE);
            holder.fee_coins_tv.setText(String.format(context.getString(R.string.a8), bean.getEntrustFeeCoins()));
            if (TextUtils.isEmpty(bean.getReturnCoinwAmount())) {
                bean.setReturnCoinwAmount("0");
            }
            holder.return_fee_coins_tv.setText(String.format(context.getString(R.string.a9), bean.getReturnCoinwAmount()));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String imageUrl = "https://www.hyperpay.tech/data/upload/";

    public ReturnFeeAdapter(List<ReturnFeeBean> list, Context context) {
        this.list.addAll(list);
        this.context = context;
    }

    public void updata(List<ReturnFeeBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public static class ViewHole extends RecyclerView.ViewHolder {

        public TextView date_tv;
        public TextView tradeSum_tv;
        public TextView fee_tv;
        public TextView fee_coins_tv;
        public TextView return_fee_tv;
        public TextView return_fee_coins_tv;
        public RelativeLayout retuin_coin_rl;

        public ViewHole(View itemView) {
            super(itemView);
            this.date_tv = itemView.findViewById(R.id.date_tv);
            this.tradeSum_tv = itemView.findViewById(R.id.tradeSum_tv);
            this.fee_tv = itemView.findViewById(R.id.fee_tv);
            this.fee_coins_tv = itemView.findViewById(R.id.fee_coins_tv);
            this.return_fee_tv = itemView.findViewById(R.id.return_fee_tv);
            this.return_fee_coins_tv = itemView.findViewById(R.id.return_fee_coins_tv);
            this.retuin_coin_rl = itemView.findViewById(R.id.retuin_coin_rl);
        }
    }
}
