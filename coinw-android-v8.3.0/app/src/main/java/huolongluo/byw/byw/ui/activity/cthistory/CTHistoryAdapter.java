package huolongluo.byw.byw.ui.activity.cthistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import huolongluo.byw.R;

/**
 * Created by LS on 2018/7/11.
 */
public class CTHistoryAdapter extends BaseAdapter {

    private List<CTHistoryListBean> mData;
    private Context context;

    public CTHistoryAdapter(List<CTHistoryListBean> mData, Context context) {
        this.mData = mData;
        this.context = context;
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
            view = LayoutInflater.from(context).inflate(R.layout.activity_ct_item, null);
            holder = new ViewHolder();
            holder.tvName = view.findViewById(R.id.tv_name);
            holder.tvTime = view.findViewById(R.id.tv_time);
            holder.tvStatus = view.findViewById(R.id.tv_status);
            holder.tvAmount = view.findViewById(R.id.tv_amount);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        CTHistoryListBean listBean = mData.get(i);
        if (listBean != null) {
            holder.tvName.setText(listBean.getFshortName());
            holder.tvTime.setText(listBean.getFcreateTime());
            holder.tvStatus.setText(listBean.getFstatus());
            holder.tvAmount.setText(listBean.getFamount());
        }
        return view;
    }

    static class ViewHolder {

        private TextView tvName;
        private TextView tvTime;
        private TextView tvStatus;
        private ImageView ivStatus;
        private TextView tvAmount;
    }
}
