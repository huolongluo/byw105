package huolongluo.byw.byw.ui.adapter;
import android.content.Context;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.model.Area;
import huolongluo.byw.superAdapter.recycler.SuperAdapter;
public class AreaListAdapter extends SuperAdapter<Area> {
    public AreaListAdapter(Context context, List<Area> data, int layoutResId) {
        super(context, data, layoutResId);
    }

    @Override
    public void onBind(int viewType, huolongluo.byw.superAdapter.recycler.BaseViewHolder holder, int position, Area area) {
        holder.setText(R.id.tv_name, area.area);
    }
}
