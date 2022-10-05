package huolongluo.byw.byw.ui.fragment.maintab05;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import huolongluo.byw.R;
import huolongluo.byw.byw.ui.fragment.maintab05.bean.ShanghuBeanList;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.bywx.utils.DoubleUtils;

import java.util.List;

/**
 * Created by LS on 2018/7/19.
 */
public class CZAdapter extends BaseAdapter {

    private Context context;
    private List<ShanghuBeanList> mData;
    private OperationClickListener clickListener;

    public CZAdapter(Context context, List<ShanghuBeanList> lists, OperationClickListener listener) {
        this.context = context;
        this.mData = lists;
        this.clickListener = listener;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        if (i < 0 || mData == null || mData.isEmpty()) {
            return null;
        }
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
            // view = LayoutInflater.from(context).inflate(R.layout.fragment_chongzhi_item,null);
            view = LayoutInflater.from(context).inflate(R.layout.c2c_cz_item, null);
            holder = new ViewHolder();
            holder.tv_time = view.findViewById(R.id.tv_time);
            holder.tv_name = view.findViewById(R.id.tv_name);
            holder.tv_amount = view.findViewById(R.id.tv_amount);
            holder.tv_buy = view.findViewById(R.id.tv_buy);
            holder.amount_tv = view.findViewById(R.id.amount_tv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        ShanghuBeanList list = mData.get(i);
        if (list != null) {
            holder.tv_name.setText(list.getFname());
            holder.tv_amount.setText(String.valueOf(list.getDealNumber()));
            holder.tv_time.setText(list.getRechargeSpeed());
            holder.amount_tv.setText(NorUtils.NumberFormatNo(2).format(DoubleUtils.parseDouble(list.getAmount())));
        }
        holder.tv_buy.setTag(i);
        holder.tv_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    int tag = (int) view.getTag();
                    clickListener.isBuy(tag);
                }
            }
        });
        return view;
    }

    static class ViewHolder {

        private TextView tv_name, tv_amount, tv_time, tv_buy, amount_tv;
    }

    public interface OperationClickListener {

        void isBuy(int position);
    }
}
