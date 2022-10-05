package huolongluo.byw.byw.ui.fragment.latestdeal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import huolongluo.byw.R;

/**
 * Created by LS on 2018/7/11.
 */

public class LatestAdapter extends RecyclerView.Adapter<LatestAdapter.ViewHolder>{
    private Context mContent;
    private List<LatestListBean> mData;

    public LatestAdapter(Context mContent, List<LatestListBean> mData) {
        this.mContent = mContent;
        this.mData = mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContent).inflate(R.layout.fragment_latest_deal_item,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
//            name = itemView.findViewById(R.id.name);

        }
    }
}
