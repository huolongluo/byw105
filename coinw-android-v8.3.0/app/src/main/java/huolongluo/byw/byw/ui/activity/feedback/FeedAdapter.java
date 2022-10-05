package huolongluo.byw.byw.ui.activity.feedback;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import huolongluo.byw.R;

/**
 * Created by LS on 2018/7/15.
 */
public class FeedAdapter extends BaseAdapter {

    private Context context;
    private List<FeedList> mData;

    public FeedAdapter(Context context, List<FeedList> mData) {
        this.context = context;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
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
            view = LayoutInflater.from(context).inflate(R.layout.feed_type_item, null);
            holder = new ViewHolder();
            holder.tvName = view.findViewById(R.id.tv_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        FeedList list = mData.get(i);
        if (list != null) {
            holder.tvName.setText(list.getName());
        }
        return view;
    }

    static class ViewHolder {

        TextView tvName;
    }
}
