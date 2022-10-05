package huolongluo.byw.byw.ui.fragment.latestdeal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import huolongluo.byw.R;

/**
 * Created by LS on 2018/7/11.
 */
public class LatestListAdapter extends BaseAdapter {

    private List<LatestListBean> mData;
    private Context mContent;

    public LatestListAdapter(List<LatestListBean> mData, Context mContent) {
        this.mData = mData;
        this.mContent = mContent;
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
            view = LayoutInflater.from(mContent).inflate(R.layout.fragment_latest_deal_item, null);
            holder = new ViewHolder();
            holder.tvTime = view.findViewById(R.id.tv_time);
            holder.tvPrice = view.findViewById(R.id.tv_price);
            holder.tvAmount = view.findViewById(R.id.tv_amount);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        LatestListBean listBean = mData.get(i);
        if (listBean != null) {
            holder.tvTime.setText(listBean.getTime());
            holder.tvPrice.setText(listBean.getPrice());
            holder.tvAmount.setText(listBean.getAmount());
            if (listBean.getEn_type().equals("bid")) {
                holder.tvPrice.setTextColor(mContent.getResources().getColor(R.color.green));
            } else {
                holder.tvPrice.setTextColor(mContent.getResources().getColor(R.color.red));
            }
        }
        return view;
    }

    static class ViewHolder {

        private TextView tvTime;
        private TextView tvPrice;
        private TextView tvAmount;
    }
}
