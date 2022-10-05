package huolongluo.byw.reform.home.activity.kline2.adapter;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.coinw.biz.trade.helper.AnimationHelper;
import com.legend.common.util.ThemeUtil;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.home.activity.kline2.common.KLineEntity;
import huolongluo.byw.reform.trade.bean.TradeOrder;
import huolongluo.byw.R;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.bywx.utils.AppUtils;
import huolongluo.bywx.utils.DoubleUtils;
/**
 * k线委托订单适配器
 */
public class KLineOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "KLineOrderAdapter";
    private Context mContext;
    private List<TradeOrder.OrderInfo> list=new ArrayList<>();
    private KLineEntity entity;
    private double totalAmountBuy,totalAmountSell;

    public static class OrderBookViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rlTitle;
        LinearLayout rlContent;
        TextView tvBid;
        TextView tvAsk;
        TextView tvPrice;
        ProgressBar pbBidVolume;
        TextView tvBidNum;
        TextView tvBidVolume;
        TextView tvBidPrice;
        ProgressBar pbAskVolume;
        TextView tvAskNum;
        TextView tvAskVolume;
        TextView tvAskPrice;

        public OrderBookViewHolder(View itemView, int type) {
            super(itemView);
            tvBid = itemView.findViewById(R.id.tv_buy_volume);
            tvAsk = itemView.findViewById(R.id.tv_sell_volume);
            tvPrice = itemView.findViewById(R.id.tv_price);
            rlTitle = itemView.findViewById(R.id.ll_title);
            rlContent = itemView.findViewById(R.id.ll_content);
            pbBidVolume = itemView.findViewById(R.id.pb_bid_volume);
            tvBidNum = itemView.findViewById(R.id.tv_bid_num);
            tvBidVolume = itemView.findViewById(R.id.tv_bid_volume);
            tvBidPrice = itemView.findViewById(R.id.tv_bid_price);
            pbAskVolume = itemView.findViewById(R.id.pb_ask_volume);
            tvAskNum = itemView.findViewById(R.id.tv_ask_num);
            tvAskVolume = itemView.findViewById(R.id.tv_ask_volume);
            tvAskPrice = itemView.findViewById(R.id.tv_ask_price);
        }
    }

    public KLineOrderAdapter(Context context, KLineEntity entity) {
        mContext = context;
        this.entity = entity;
    }
    //http更新列表
    public void update(List<TradeOrder.OrderInfo> list){
        Logger.getInstance().debug(TAG,"update list:"+ GsonUtil.obj2Json(list,List.class));
        if(this.list==null)
        {
            this.list=new ArrayList<>();
        }
        if(list==null||list.size()==0){
            this.list.clear();
            notifyDataSetChanged();
            return;
        }
        this.list.clear();
        this.list.addAll(list);
        if(TextUtils.isEmpty(list.get(0).sellAmount)){
            totalAmountBuy=getMaxAmountBuy();
        }else{
            totalAmountSell=getMaxAmountSell();
        }
        checkList();
        notifyDataSetChanged();
    }
    //只显示最多20条数据
    private void checkList(){
        if(list.size()> Constant.MAX_SIZE){
            list=list.subList(0,Constant.MAX_SIZE);
        }
    }
    private double getMaxAmountBuy() {
        double max = 0d;
        for (int i = 0; i < list.size(); i++) {
            if(!TextUtils.isEmpty(list.get(i).buyAmount)){
                max += Double.valueOf(list.get(i).buyAmount);
            }
        }
        return max < 0 ? 1 : max;
    }
    private double getMaxAmountSell() {
        double max = 0d;
        for (int i = 0; i < list.size(); i++) {
            if(!TextUtils.isEmpty(list.get(i).sellAmount)) {
                max += Double.valueOf(list.get(i).sellAmount);
            }
        }
        return max < 0 ? 1 : max;
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? 0 : 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final OrderBookViewHolder itemViewHolder = (OrderBookViewHolder) holder;
        itemViewHolder.rlTitle.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        TradeOrder.OrderInfo info = list.get(position);
        itemViewHolder.tvBidPrice.setTextColor(ThemeUtil.INSTANCE.getUpColor(mContext));
        itemViewHolder.tvAskPrice.setTextColor(ThemeUtil.INSTANCE.getDropColor(mContext));
        if (entity != null) {
            itemViewHolder.tvBid.setText(mContext.getString(R.string.str_amount) + "(" + entity.getCoinName() + ")");
            itemViewHolder.tvAsk.setText(mContext.getString(R.string.str_amount) + "(" + entity.getCoinName() + ")");
            itemViewHolder.tvPrice.setText(mContext.getString(R.string.str_price) + "(" + entity.getCnyName() + ")");
        } else {
            itemViewHolder.tvBid.setText(mContext.getString(R.string.str_amount));
            itemViewHolder.tvAsk.setText(mContext.getString(R.string.str_amount));
            itemViewHolder.tvPrice.setText(mContext.getString(R.string.str_price));
        }
        if (TextUtils.isEmpty(info.buyPrice)) {//有可能没有买的数据
            itemViewHolder.tvBidVolume.setText("");
            itemViewHolder.tvBidPrice.setText("");
            itemViewHolder.tvBidNum.setText((position + 1) + "");
            itemViewHolder.pbBidVolume.setProgress(0);
        } else {
            itemViewHolder.tvBidVolume.setText(PricingMethodUtil.getLargePrice(info.buyAmount,3));
            itemViewHolder.tvBidPrice.setText(info.buyPrice);
            itemViewHolder.tvBidNum.setText((position + 1) + "");
            int progress=AppUtils.getProgress(DoubleUtils.parseDouble(info.buyAmount), totalAmountBuy);
            AnimationHelper.getInstance().updateProgress(itemViewHolder.pbBidVolume, progress);
        }
        if (TextUtils.isEmpty(info.sellPrice)) {
            itemViewHolder.tvAskVolume.setText("");
            itemViewHolder.tvAskPrice.setText("");
            itemViewHolder.tvAskNum.setText((position + 1) + "");
            itemViewHolder.pbAskVolume.setProgress(0);
        } else {
            itemViewHolder.tvAskVolume.setText(PricingMethodUtil.getLargePrice(info.sellAmount,3));
            itemViewHolder.tvAskPrice.setText(info.sellPrice);
            itemViewHolder.tvAskNum.setText((position + 1) + "");
            int progress=AppUtils.getProgress(DoubleUtils.parseDouble(info.sellAmount), totalAmountSell);
            AnimationHelper.getInstance().updateProgress(itemViewHolder.pbAskVolume, progress);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kline_order, parent, false);
        return new OrderBookViewHolder(v, viewType);
    }
}
