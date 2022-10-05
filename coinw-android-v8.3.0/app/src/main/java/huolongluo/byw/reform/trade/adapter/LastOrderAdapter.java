package huolongluo.byw.reform.trade.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.reform.trade.bean.TradeOrderBean;
/**
 * Created by hy on 2018/11/14 0014.
 */
public class LastOrderAdapter extends RecyclerView.Adapter<LastOrderAdapter.ViewHolder> {
    List<TradeOrderBean> dataList;

    public LastOrderAdapter(List<TradeOrderBean> latestList) {
        this.dataList = latestList;
    }

    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public TradeOrderBean getItem(int position) {
        if (position < 0 || dataList == null || dataList.isEmpty()) {
            return null;
        }
        return dataList.get(position);
    }

    public void clear() {
        if (dataList != null) {
            this.dataList.clear();
        }
        notifyDataSetChanged();
    }

    public void update(List<TradeOrderBean> latestList) {
        if (latestList == null) {
            if (dataList != null) {
                this.dataList.clear();
            }
            notifyDataSetChanged();
            return;
        }
        //TODO 检查数据是否已经更新
        //
        if (this.dataList != null) {
            this.dataList.clear();
            this.dataList.addAll(latestList);
        } else {
            this.dataList = latestList;
        }
//        this.dataList = latestList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_lastorder_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TradeOrderBean bean = getItem(position);
        //java.lang.NullPointerException: Attempt to invoke virtual method &#39;java.lang.String huolongluo.byw.reform.trade.bean.TradeOrderBean.getAmount()&#39; on a null object reference
        // at g.a.j.j.b.c.onBindViewHolder(LastOrderAdapter.java:4)
        // at androidx.recyclerview.widget.RecyclerView$a.onBindViewHolder(RecyclerView.java:1)
        if (bean == null) {
            return;
        }
        holder.number_tv.setText(bean.getAmount());
        holder.price_tv.setText(bean.getPrice());
        holder.time_tv.setText(bean.getTime());
        if (TextUtils.equals(bean.getEn_type(), "bid")) {
            holder.price_tv.setTextColor(BaseApp.getSelf().getResources().getColor(R.color.color_4ED3AD));
        } else {
            holder.price_tv.setTextColor(BaseApp.getSelf().getResources().getColor(R.color.FF5763));
        }
    }

    @Override
    public int getItemCount() {
        return getCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView time_tv;
        public TextView number_tv;
        public TextView price_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            time_tv = itemView.findViewById(R.id.time_tv);
            price_tv = itemView.findViewById(R.id.price_tv);
            number_tv = itemView.findViewById(R.id.number_tv);
        }
    }
}
