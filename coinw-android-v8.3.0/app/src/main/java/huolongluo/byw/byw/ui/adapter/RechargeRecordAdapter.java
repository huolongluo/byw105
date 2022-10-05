package huolongluo.byw.byw.ui.adapter;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.ChongZhiBean;
import huolongluo.byw.superAdapter.recycler.BaseViewHolder;
import huolongluo.byw.superAdapter.recycler.SuperAdapter;

/**
 * Created by 火龙裸 on 2018/1/3.
 */

public class RechargeRecordAdapter extends SuperAdapter<ChongZhiBean.ListBean>
{
    public RechargeRecordAdapter(Context context, List<ChongZhiBean.ListBean> list, int layoutResId)
    {
        super(context, list, layoutResId);
    }

    @Override
    public void onBind(int viewType, BaseViewHolder holder, int position, ChongZhiBean.ListBean item)
    {
        holder.setText(R.id.tv_order_id, item.getId() + "");
        if (TextUtils.isEmpty(item.getFremark()))
        {
            holder.setText(R.id.tv_remark, mContext.getString(R.string.j4));
        }
        else
        {
            holder.setText(R.id.tv_remark, item.getFremark() + "");
        }
        holder.setText(R.id.tv_chognzhi_date, item.getFcreateTime());
        holder.setText(R.id.tv_state, item.getFstatus());
        holder.setText(R.id.tv_chongzhi_count, item.getFamount() + "");
    }
}
