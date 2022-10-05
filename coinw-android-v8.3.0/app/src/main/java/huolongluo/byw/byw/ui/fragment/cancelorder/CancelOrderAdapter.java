package huolongluo.byw.byw.ui.fragment.cancelorder;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.coinw.biz.trade.model.OrderSide;
import com.android.legend.model.order.OrderItemBean;
import java.util.List;
import huolongluo.byw.R;
import huolongluo.byw.byw.ui.dialog.AddDialog;
import huolongluo.byw.util.CurrencyPairUtil;
import huolongluo.byw.util.DateUtils;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.bywx.utils.DoubleUtils;
/**
 * Created by hy on 2018/11/14 0014.
 */
public class CancelOrderAdapter extends RecyclerView.Adapter<CancelOrderAdapter.ViewHolder> {
    List<OrderItemBean> dataList;
    private FragmentManager fragmentManager;
    Context context;

    public CancelOrderAdapter(Context context, List<OrderItemBean> latestList, FragmentManager fragmentManager) {
        this.context = context;
        this.dataList = latestList;
        this.fragmentManager = fragmentManager;
    }

    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public OrderItemBean getItem(int position) {
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

    public void update(List<OrderItemBean> latestList) {
        if (latestList == null) {
            if (dataList != null) {
                this.dataList.clear();
            }
            notifyDataSetChanged();
            return;
        }
        if (this.dataList != null) {
            this.dataList.clear();
            this.dataList.addAll(latestList);
        } else {
            this.dataList = latestList;
        }
        notifyDataSetChanged();
        notifyItemInserted(getItemCount());//必须用此方法才能进行recycleview的刷新。（末尾刷新）
    }
    public void remove(long orderId){
        if(dataList==null) return;
        for(OrderItemBean bean:dataList){
            if(bean.getOrderId()==orderId){
                dataList.remove(bean);
                notifyDataSetChanged();
                break;
            }
        }
    }
    //根据推送刷新未成交数
    public void updateRemainSize(long orderId,double remainSize){
        if(dataList==null) return;
        for(OrderItemBean bean:dataList){
            if(bean.getOrderId()==orderId){
                bean.setDealSize(DoubleUtils.parseDouble(bean.getSize())-remainSize+"");
                notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        OrderItemBean bean = getItem(position);
        String coinName= CurrencyPairUtil.getCoinName(bean.getSymbol());
        String cnyName= CurrencyPairUtil.getCnyName(bean.getSymbol());
        if (TextUtils.equals(bean.getSide(), OrderSide.BUY.getSide())) {
            holder.iv_history_title.setText(R.string.buy_d);
            holder.iv_history_title.setBackground(context.getResources().getDrawable(R.drawable.shape_buy));
            holder.historyPriceTipTxt.setText(getString(R.string.d20) + "(" + cnyName + ")");
            holder.historyAmountTipTxt.setText(getString(R.string.d18) + "(" + coinName + ")");
            holder.successPriceTipTxt.setText(getString(R.string.d21) + "(" + cnyName + ")");
            holder.unsuccessPriceTipTxt.setText(getString(R.string.d19) + "(" + coinName + ")");
        } else {
            holder.iv_history_title.setText(R.string.sell_d);
            holder.iv_history_title.setBackground(context.getResources().getDrawable(R.drawable.shape_sell));

            holder.historyPriceTipTxt.setText(getString(R.string.d20) + "(" + cnyName + ")");
            holder.historyAmountTipTxt.setText(getString(R.string.d18) + "(" + coinName + ")");
            holder.successPriceTipTxt.setText(getString(R.string.d21) + "(" + cnyName + ")");
            holder.unsuccessPriceTipTxt.setText(getString(R.string.d19) + "(" + coinName + ")");
        }
        holder.tv_history_updat_time.setText(DateUtils.format(bean.getTime(),DateUtils.FORMAT1)); // 时间
        holder.tv_history_fprice.setText(bean.getPrice()); // 委托价格
        holder.tv_history_fsuccessprice.setText(bean.getStopPrice()==null?"0.0":bean.getStopPrice()); // 成交价格
        holder.tv_history_famount.setText(bean.getSize());//委托量
        holder.tv_history_successamount.setText("" + NorUtils.NumberFormat(4).format((DoubleUtils.parseDouble(bean.getSize())
                - DoubleUtils.parseDouble(bean.getDealSize()))));//未成交
        holder.iv_history_cancle_order.setVisibility(View.VISIBLE);
        holder.tv_statu.setVisibility(View.GONE);
        holder.iv_history_cancle_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDialog dialog = new AddDialog();
                Bundle bundle = new Bundle();
                bundle.putString("orderId", bean.getOrderId() + "");
                bundle.putInt("position", position);
                dialog.setArguments(bundle);
                dialog.setDialog(AddDialog.CANCEL_ORDER);
                dialog.show(fragmentManager, getClass().getSimpleName());
            }
        });
    }

    private String getString(int resId) {
        if (context != null) {
            try {
                return context.getResources().getString(resId);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return getCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
            tv_history_updat_time = view.findViewById(R.id.tv_history_updat_time);
            iv_history_cancle_order = view.findViewById(R.id.iv_history_cancle_order);
            tv_statu = view.findViewById(R.id.tv_statu);
            tv_history_famount = view.findViewById(R.id.tv_history_famount);
            tv_history_fprice = view.findViewById(R.id.tv_history_fprice);
            btb_c_user = view.findViewById(R.id.btb_c_user);
            tv_history_successamount = view.findViewById(R.id.tv_history_successamount);
            tv_history_fsuccessprice = view.findViewById(R.id.tv_history_fsuccessprice);
            iv_history_title = view.findViewById(R.id.iv_history_title);
            //Tip
            historyPriceTipTxt = view.findViewById(R.id.tv_history_fprice_tip);
            historyAmountTipTxt = view.findViewById(R.id.tv_history_famount_tip);
            feeTipTxt = view.findViewById(R.id.tv_fee_tip);
            successPriceTipTxt = view.findViewById(R.id.tv_history_fsuccessprice_tip);
            unsuccessPriceTipTxt = view.findViewById(R.id.btb_c_user);
            allMoneyTipTxt = view.findViewById(R.id.tv_all_money_tip);
        }

        private TextView tv_history_updat_time;
        private TextView iv_history_cancle_order;
        private TextView tv_statu;
        private TextView tv_history_famount;
        private TextView tv_history_fprice;
        private TextView btb_c_user;
        private TextView tv_history_successamount;
        private TextView tv_history_fsuccessprice;
        private TextView iv_history_title;
        //Tip
        private TextView historyPriceTipTxt, feeTipTxt, historyAmountTipTxt, successPriceTipTxt, unsuccessPriceTipTxt, allMoneyTipTxt;
    }
}
