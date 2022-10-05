package huolongluo.byw.byw.ui.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.TradeAccountBean;
import huolongluo.byw.superAdapter.recycler.BaseViewHolder;
import huolongluo.byw.superAdapter.recycler.SuperAdapter;

/**
 * Created by 火龙裸 on 2018/1/5.
 */

public class AddressListAdapter extends SuperAdapter<TradeAccountBean.CoinsBean>
{
    public AddressListAdapter(Context context, List<TradeAccountBean.CoinsBean> list, int layoutResId)
    {
        super(context, list, layoutResId);
    }

    @Override
    public void onBind(int viewType, BaseViewHolder holder, int position, TradeAccountBean.CoinsBean item)
    {
        RequestOptions ro = new RequestOptions();
        ro.error(R.mipmap.rmblogo);
        ro.centerCrop();
        Glide.with(mContext).load(item.getLogo()).apply(ro).into((ImageView) holder.getView(R.id.iv_logo));
        holder.setText(R.id.tv_title, item.getCnName() + "(" + item.getShortName() + ")");
    }
}
