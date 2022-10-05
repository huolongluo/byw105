package huolongluo.byw.byw.ui.fragment.maintab05.cnycthis;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.util.noru.NorUtils;

/**
 * Created by LS on 2018/7/20.
 */
public class CnyCZAdapter extends BaseAdapter {

    private Context context;
    private List<ChongzhiListBean> mData;
    private OperationClickListener clickListener = null;

    public CnyCZAdapter(Context context, List<ChongzhiListBean> list, OperationClickListener clickListener) {
        this.context = context;
        this.mData = list;
        this.clickListener = clickListener;
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
            //    view = LayoutInflater.from(context).inflate(R.layout.fragment_cny_his_cz_item,null);
            view = LayoutInflater.from(context).inflate(R.layout.cnyt_hiscz_item, null);
            holder = new ViewHolder();
            holder.tv_time = view.findViewById(R.id.tv_time);
            holder.tv_money = view.findViewById(R.id.tv_money);
            holder.tv_status = view.findViewById(R.id.tv_status);
            holder.tv_detail = view.findViewById(R.id.tv_detail);
            holder.tv_cancel = view.findViewById(R.id.tv_cancel);
            holder.ll_right = view.findViewById(R.id.ll_right);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        ChongzhiListBean bean = mData.get(i);
        Log.d("充值记录状态", i + ">>>>>>>" + bean.getFstatus());
        if (bean != null) {
            holder.tv_time.setText(bean.getFcreateTime());
            holder.tv_money.setText(NorUtils.NumberFormat(2).format(bean.getFamount()));
            holder.tv_status.setText(bean.getFstatus_s());
            //撤销后隐藏撤销按钮
            if (bean.getFstatus().equals("4")) {
                holder.tv_cancel.setVisibility(View.GONE);
            } else if (bean.getFstatus().equals("3")) {
                holder.tv_cancel.setVisibility(View.GONE);
            } else {
                holder.tv_cancel.setVisibility(View.VISIBLE);
            }
        }
        holder.tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    int tag = (int) view.getTag();
                    clickListener.delete(tag);
                }
            }
        });
        holder.tv_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    int tag = (int) view.getTag();
                    clickListener.detail(tag);
                }
            }
        });
        holder.tv_detail.setTag(i);
        holder.tv_cancel.setTag(i);
        return view;
    }

    static class ViewHolder {

        private TextView tv_time;
        private TextView tv_money;
        private TextView tv_status;
        private TextView tv_detail;
        private TextView tv_cancel;
        private LinearLayout ll_right;
    }

    public interface OperationClickListener {

        void delete(int position);

        void detail(int position);
    }
}
