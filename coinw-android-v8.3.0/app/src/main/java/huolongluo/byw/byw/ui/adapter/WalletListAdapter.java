package huolongluo.byw.byw.ui.adapter;

import android.content.Context;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.WithdrawChainBean;
import huolongluo.byw.superAdapter.list.SuperAdapter;

/**
 * Created by 火龙裸 on 2018/1/4.
 */

public class WalletListAdapter extends SuperAdapter<WithdrawChainBean>
{
    public WalletListAdapter(Context context, List<WithdrawChainBean> data, int layoutResId)
    {
        super(context, data, layoutResId);
    }

    @Override
    protected void onBind(int viewType, huolongluo.byw.superAdapter.list.BaseViewHolder holder, int position, WithdrawChainBean item)
    {
        holder.setText(R.id.tv_title, item.getAddress());
//        holder.setText(R.id.tv_beizhu, item.getRemark());
    }
}
