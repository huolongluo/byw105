package huolongluo.byw.byw.ui.fragment.cancelorder;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.github.mikephil.charting.utils.ValueUtils;

import java.text.DecimalFormat;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.ui.dialog.AddDialog;
import huolongluo.byw.reform.bean.EntrustInfoBean;
import huolongluo.bywx.utils.DoubleUtils;
/**
 * Created by LS on 2018/7/20.
 */
public class CancelAdapter extends BaseAdapter {
    private Context context;
    private List<EntrustInfoBean> mData;
    private FragmentManager fragmentManager;
    private DecimalFormat df = new DecimalFormat("0.########");
    private DecimalFormat df2 = new DecimalFormat("0.##");
    private DecimalFormat df3 = new DecimalFormat("0.0000");

    public CancelAdapter(Context context, List<EntrustInfoBean> mData, FragmentManager fragmentManager) {
        this.context = context;
        this.mData = mData;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_history, null);
            holder = new ViewHolder();
            holder.tv_history_updat_time = view.findViewById(R.id.tv_history_updat_time);
            holder.iv_history_cancle_order = view.findViewById(R.id.iv_history_cancle_order);
            holder.tv_statu = view.findViewById(R.id.tv_statu);
            holder.tv_history_famount = view.findViewById(R.id.tv_history_famount);
            holder.tv_history_fprice = view.findViewById(R.id.tv_history_fprice);
            holder.btb_c_user = view.findViewById(R.id.btb_c_user);
            holder.tv_history_successamount = view.findViewById(R.id.tv_history_successamount);
            holder.tv_history_fsuccessprice = view.findViewById(R.id.tv_history_fsuccessprice);
            holder.iv_history_title = view.findViewById(R.id.iv_history_title);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        EntrustInfoBean bean = mData.get(i);
        if (bean.getType() == 0) {
            holder.iv_history_title.setText(R.string.buy_d);
            holder.iv_history_title.setBackground(viewGroup.getResources().getDrawable(R.drawable.shape_buy));
        } else {
            holder.iv_history_title.setText(R.string.sell_d);
            holder.iv_history_title.setBackground(viewGroup.getResources().getDrawable(R.drawable.shape_sell));
        }
        holder.tv_history_updat_time.setText(bean.getFlastUpdatTime()); // 时间
        holder.tv_history_fprice.setText(bean.getFprice() + ""); // 委托价格
        holder.tv_history_fsuccessprice.setText(bean.getFsuccessprice() + ""); // 成交价格
        holder.tv_history_famount.setText(bean.getFcount() + "");
        holder.tv_history_successamount.setText("" + ValueUtils.getValue(DoubleUtils.parseDouble(bean.getFcount()) - DoubleUtils.parseDouble(bean.getFsuccessCount()), 4));
        holder.iv_history_cancle_order.setVisibility(View.VISIBLE);
        holder.tv_statu.setVisibility(View.GONE);
        holder.iv_history_cancle_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDialog dialog = new AddDialog();
                Bundle bundle = new Bundle();
                bundle.putString("orderId", bean.getFid() + "");
                bundle.putInt("position", i);
                dialog.setArguments(bundle);
                dialog.setDialog(AddDialog.CANCEL_ORDER);
                dialog.show(fragmentManager, getClass().getSimpleName());
            }
        });
        return view;
    }

    static class ViewHolder {
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
        private TextView historyPriceTipTxt, historyAmountTipTxt, feeTipTxt, successPriceTipTxt, unsuccessPriceTipTxt, allMoneyTipTxt;
    }
}
