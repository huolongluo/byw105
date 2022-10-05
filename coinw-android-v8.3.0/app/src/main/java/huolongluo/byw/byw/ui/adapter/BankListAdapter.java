package huolongluo.byw.byw.ui.adapter;

import android.content.Context;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.BankListBean;
import huolongluo.byw.superAdapter.recycler.BaseViewHolder;
import huolongluo.byw.superAdapter.recycler.SuperAdapter;

/**
 * <p>
 * Created by 火龙裸 on 2018/1/5 0005.
 */

public class BankListAdapter extends SuperAdapter<BankListBean.ListBean>
{
    public BankListAdapter(Context context, List<BankListBean.ListBean> list, int layoutResId)
    {
        super(context, list, layoutResId);
    }

    @Override
    public void onBind(int viewType, BaseViewHolder holder, int position, BankListBean.ListBean item)
    {
        holder.setText(R.id.tv_bank_name, item.getName());
    }
}
