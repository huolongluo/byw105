package huolongluo.byw.byw.ui.activity.feedback;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import huolongluo.byw.R;

/**
 * Created by LS on 2018/7/15.
 */
public class FeedHisAdapter extends BaseAdapter {

    private Context context;
    private List<FeedListHis> mData;
    private OperationClickListener clickListener;

    public FeedHisAdapter(Context context, List<FeedListHis> mData, OperationClickListener clickListener) {
        this.context = context;
        this.mData = mData;
        this.clickListener = clickListener;
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
            view = LayoutInflater.from(context).inflate(R.layout.feed_his_item, null);
            holder = new ViewHolder();
            holder.ll_answer = view.findViewById(R.id.ll_answer);
            holder.tv_answer = view.findViewById(R.id.tv_answer);
            holder.tv_content = view.findViewById(R.id.tv_content);
            holder.tv_status = view.findViewById(R.id.tv_status);
            holder.tv_date = view.findViewById(R.id.tv_date);
            holder.tv_type = view.findViewById(R.id.tv_type);
            holder.iv_delete = view.findViewById(R.id.iv_delete);
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        int tag = (int) view.getTag();
                        clickListener.delete(tag);
                    }
                }
            });
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        FeedListHis listHis = mData.get(i);
        if (listHis != null) {
            holder.tv_type.setText(listHis.getType());
            holder.tv_answer.setText(listHis.getAnswer());
            if (listHis.getStatus() == 1) {
                holder.tv_status.setText(context.getString(R.string.unsolved));
                holder.ll_answer.setVisibility(View.GONE);
            } else {
                holder.tv_status.setText(context.getString(R.string.resolved));
                holder.ll_answer.setVisibility(View.VISIBLE);
            }
            holder.tv_date.setText(listHis.getDate());
            holder.tv_content.setText(listHis.getAsk());
        }
        holder.iv_delete.setTag(i);
        return view;
    }

    static class ViewHolder {

        private TextView tv_type, tv_status, tv_date, tv_content, tv_answer;
        private LinearLayout ll_answer;
        private ImageView iv_delete;
    }

    public interface OperationClickListener {

        void delete(int position);
    }
}
