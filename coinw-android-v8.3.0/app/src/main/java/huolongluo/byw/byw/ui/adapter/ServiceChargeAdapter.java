package huolongluo.byw.byw.ui.adapter;

import android.content.Context;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.FeeListBean;
import huolongluo.byw.superAdapter.recycler.BaseViewHolder;
import huolongluo.byw.superAdapter.recycler.SuperAdapter;

/**
 * Created by 火龙裸 on 2018/1/3.
 */

public class ServiceChargeAdapter extends SuperAdapter<FeeListBean>
{
    public ServiceChargeAdapter(Context context, List<FeeListBean> list, int layoutResId)
    {
        super(context, list, layoutResId);
    }

    @Override
    public void onBind(int viewType, BaseViewHolder holder, int position, FeeListBean item)
    {
//        holder.setText(R.id.tv_service_name, item.getFee() + "");
    }
}
