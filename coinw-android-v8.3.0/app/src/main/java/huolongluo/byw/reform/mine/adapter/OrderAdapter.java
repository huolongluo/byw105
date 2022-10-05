package huolongluo.byw.reform.mine.adapter;
import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.coinw.biz.trade.helper.TradeDataHelper;
import com.android.coinw.biz.trade.helper.TradeHelper;
import com.android.coinw.biz.trade.model.OrderSide;
import com.android.legend.model.enumerate.order.OrderStatus;
import com.android.legend.model.enumerate.order.OrderType;
import com.android.legend.model.order.OrderItemBean;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.reform.mine.activity.TradeOrderListActivity;
import huolongluo.byw.util.CurrencyPairUtil;
import huolongluo.byw.util.DateUtils;
import huolongluo.byw.util.MathHelper;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.bywx.utils.DoubleUtils;
/**
 * Created by Administrator on 2019/1/5 0005.
 */
public class OrderAdapter extends BaseAdapter {
    TradeOrderListActivity.onDeleteListener listener;
    Context context;
    List<OrderItemBean> list = new ArrayList<>();

    private int quoteAmountScale = 8;// 成交额 手续费 保留位数

    public OrderAdapter(Context context, List<OrderItemBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
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
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.tradeorder_list_item, null);
            holder = new ViewHolder();
            holder.tv_history_updat_time = view.findViewById(R.id.tv_history_updat_time);
            holder.allMoney = view.findViewById(R.id.allMoney);
            holder.iv_history_cancle_order = view.findViewById(R.id.iv_history_cancle_order);
            holder.tv_statu = view.findViewById(R.id.tv_statu);
            holder.tv_history_famount = view.findViewById(R.id.tv_history_famount);
            holder.tv_history_fprice = view.findViewById(R.id.tv_history_fprice);
            holder.tv_history_successamount = view.findViewById(R.id.tv_history_successamount);
            holder.tv_history_fsuccessprice = view.findViewById(R.id.tv_history_fsuccessprice);
            holder.iv_history_title = view.findViewById(R.id.iv_history_title);
            holder.coinName_tv = view.findViewById(R.id.coinName_tv);
            holder.cnyName_tv = view.findViewById(R.id.cnyName_tv);
            holder.fee_tv = view.findViewById(R.id.fee_tv);
            holder.fee_ll = view.findViewById(R.id.fee_ll);
            //Tip
            holder.historyPriceTipTxt = view.findViewById(R.id.tv_history_fprice_tip);
            holder.historyAmountTipTxt = view.findViewById(R.id.tv_history_famount_tip);
            holder.feeTipTxt = view.findViewById(R.id.tv_fee_tip);
            holder.successPriceTipTxt = view.findViewById(R.id.tv_history_fsuccessprice_tip);
            holder.unsuccessPriceTipTxt = view.findViewById(R.id.btb_c_user);
            holder.allMoneyTipTxt = view.findViewById(R.id.tv_all_money_tip);
            holder.rootLayout = view.findViewById(R.id.ll_root);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        OrderItemBean bean = list.get(position);
        String coinName= CurrencyPairUtil.getCoinName(bean.getSymbol());
        String cnyName= CurrencyPairUtil.getCnyName(bean.getSymbol());
        if (TextUtils.equals(bean.getSide(), OrderSide.BUY.getSide())) {
            holder.iv_history_title.setText(R.string.buy_d);
            holder.iv_history_title.setBackground(parent.getResources().getDrawable(R.drawable.shape_buy));
            //
            holder.historyPriceTipTxt.setText(getString(R.string.d20) + "(" + cnyName + ")");
            holder.historyAmountTipTxt.setText(getString(R.string.d18) + "(" + coinName + ")");
            holder.feeTipTxt.setText(getString(R.string.fee) + "(" + cnyName + ")");
            holder.successPriceTipTxt.setText(getString(R.string.d21) + "(" + cnyName + ")");
            holder.allMoneyTipTxt.setText(getString(R.string.all_money) + "(" + cnyName + ")");
        } else {
            holder.iv_history_title.setText(R.string.sell_d);
            holder.iv_history_title.setBackground(parent.getResources().getDrawable(R.drawable.shape_sell));
            //
            holder.historyPriceTipTxt.setText(getString(R.string.d20) + "(" + cnyName + ")");
            holder.historyAmountTipTxt.setText(getString(R.string.d18) + "(" + coinName + ")");
            holder.feeTipTxt.setText(getString(R.string.fee) + "(" + cnyName + ")");
            holder.successPriceTipTxt.setText(getString(R.string.d21) + "(" + cnyName + ")");
            holder.allMoneyTipTxt.setText(getString(R.string.all_money) + "(" + cnyName + ")");
        }

        holder.tv_history_updat_time.setText(DateUtils.format(bean.getTime(),DateUtils.FORMAT1)); // 时间

        holder.tv_history_famount.setText(bean.getSize());

        if (bean.getType().equalsIgnoreCase(OrderType.MARKET.getType())){
            holder.tv_history_fprice.setText(getString(R.string.market_price)); // 委托价格
            holder.unsuccessPriceTipTxt.setText(getString(R.string.d23) + "(" + coinName + ")");
            holder.tv_history_successamount.setText("" + DoubleUtils.parseDouble(bean.getDealSize()));

            if (TextUtils.equals(bean.getSide(), OrderSide.BUY.getSide())){
                holder.historyAmountTipTxt.setText(getString(R.string.d18) + "(" + cnyName + ")");
                holder.tv_history_famount.setText(bean.getFunds());
            }

        } else {
            holder.tv_history_fprice.setText(bean.getPrice()); // 委托价格
            holder.unsuccessPriceTipTxt.setText(getString(R.string.d19) + "(" + coinName + ")");

            holder.tv_history_successamount.setText("" + NorUtils.NumberFormatNo(15).format(
                    MathHelper.subReturnBigDecimal(bean.getSize(), bean.getDealSize())
            ));
        }

        int priceScale = CurrencyPairUtil.getPricePreciousById(bean.getSymbol());
        // todo stopPrice始终为0
        holder.tv_history_fsuccessprice.setText(bean.getDealAvgPrice()==null ? "0.0" : NorUtils.NumberFormat(priceScale, RoundingMode.DOWN).format(DoubleUtils.parseDouble(bean.getDealAvgPrice()))); // 成交价格


        holder.coinName_tv.setText(coinName);
        holder.cnyName_tv.setText("/" + cnyName);

        holder.iv_history_cancle_order.setVisibility(View.VISIBLE);
        //需求：如果是用户撤销的订单，则改变背景颜色值
        if (TextUtils.equals(bean.getStatus(), OrderStatus.CANCELLED.getStatus())) {
            holder.rootLayout.setBackgroundColor(context.getResources().getColor(R.color.dfe0e3));
        } else if (context != null) {
            holder.rootLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        //历史订单(非活跃订单)显示手续费和成交总额
        if(!bean.getActive()){
            if(bean.getFee()==null){
                holder.fee_tv.setText("0");
            } else {
                holder.fee_tv.setText(NorUtils.NumberFormat(quoteAmountScale, RoundingMode.DOWN).format(DoubleUtils.parseDouble(bean.getFee())));
            }

            holder.tv_statu.setText(getStatusTxt(bean.getStatus()));
            holder.iv_history_cancle_order.setVisibility(View.GONE);

            holder.allMoney.setText(NorUtils.NumberFormat(quoteAmountScale, RoundingMode.DOWN).format(DoubleUtils.parseDouble(bean.getDealFunds())));
            holder.fee_ll.setVisibility(View.VISIBLE);
        }else{
            holder.fee_ll.setVisibility(View.GONE);
            holder.tv_statu.setText("");
            holder.iv_history_cancle_order.setVisibility(View.VISIBLE);
        }
        holder.iv_history_cancle_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.getInstance().cancelOrder(context, new DialogUtils.onBnClickListener() {
                    @Override
                    public void onLiftClick(AlertDialog dialog, View view) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onRightClick(AlertDialog dialog, View view) {
                        if (listener != null) {
                            listener.delete(bean.getOrderId());
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
        return view;
    }
    private String getStatusTxt(String status){
        String txt="";
        if(TextUtils.equals(status,OrderStatus.CANCELLED.getStatus())){//撤销
            txt=getString(R.string.cex);
        }else if(TextUtils.equals(status,OrderStatus.PARTIALLY_FILLED.getStatus())){//已取消部分成交
            txt=getString(R.string.str_order_part_filled);
        }
        else if(TextUtils.equals(status,OrderStatus.FILLED.getStatus())){//已成交
            txt=getString(R.string.chengjall);
        }
        else if(TextUtils.equals(status,OrderStatus.REJECTED.getStatus())){//失败
            txt=getString(R.string.str_settle_failed);
        }
        return txt;
    }
    public void setListener(TradeOrderListActivity.onDeleteListener listener) {
        this.listener = listener;
    }

    static class ViewHolder {
        private TextView tv_history_updat_time;
        private TextView iv_history_cancle_order;
        private TextView tv_statu;
        private TextView tv_history_famount;
        private TextView tv_history_fprice;
        private TextView tv_history_successamount;
        private TextView tv_history_fsuccessprice;
        private TextView iv_history_title;
        private TextView coinName_tv;
        private TextView cnyName_tv;
        private TextView fee_tv;//手续费（新加）
        private LinearLayout fee_ll;//
        private TextView allMoney;
        private LinearLayout rootLayout;
        //Tip
        private TextView historyPriceTipTxt, historyAmountTipTxt, feeTipTxt, successPriceTipTxt, unsuccessPriceTipTxt, allMoneyTipTxt;
    }
}





