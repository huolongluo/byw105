package huolongluo.byw.byw.ui.fragment.maintab05.cnycthis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.util.noru.NorUtils;

/**
 * Created by LS on 2018/7/20.
 */

public class CnyTXAdapter extends BaseAdapter{
    private Context context;
    private List<ChongzhiListBean> mData;
    private OperationClickListener clickListener = null;

    public CnyTXAdapter(Context context, List<ChongzhiListBean> mData, OperationClickListener clickListener) {
        this.context = context;
        this.mData = mData;
        this.clickListener = clickListener;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null){
            //view = LayoutInflater.from(context).inflate(R.layout.fragment_cny_his_tx_item,null);
            view = LayoutInflater.from(context).inflate(R.layout.cnyt_histv_item,null);
            holder = new ViewHolder();

            holder.tv_time = view.findViewById(R.id.tv_time);
            holder.tv_money = view.findViewById(R.id.tv_money);
            holder.tv_fee = view.findViewById(R.id.tv_fee);
            holder.tv_status = view.findViewById(R.id.tv_status);
            holder.tv_detail = view.findViewById(R.id.tv_cancel);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        ChongzhiListBean bean = mData.get(i);
        if (bean != null){

            holder.tv_time.setText(bean.getFcreateTime());
            holder.tv_money.setText(NorUtils.NumberFormat(2).format(bean.getFamount()));
            holder.tv_status.setText(bean.getFstatus_s());
            holder.tv_fee.setText(String.valueOf(bean.getFfees()));
            if (bean.getFstatus().equals("4")){
                holder.tv_detail.setVisibility(View.GONE);
            }else if (bean.getFstatus().equals("3")){
                holder.tv_detail.setVisibility(View.GONE);
            }else {
                holder.tv_detail.setVisibility(View.VISIBLE);
            }
        }

        holder.tv_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null){
                    int tag = (int) view.getTag();
                    clickListener.delete(tag);
                }
            }
        });
        holder.tv_detail.setTag(i);
        return view;
    }

    static class ViewHolder{
        private TextView tv_time;
        private TextView tv_money;
        private TextView tv_fee;
        private TextView tv_status;
        private TextView tv_detail;
    }

    public interface OperationClickListener {

        void delete(int position);

    }

}
