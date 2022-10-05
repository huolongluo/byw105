package huolongluo.byw.byw.ui.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.superAdapter.recycler.BaseViewHolder;
import huolongluo.byw.superAdapter.recycler.SuperAdapter;

/**
 * Created by 火龙裸 on 2017/12/26.
 */

public class OptionPopAdapter extends SuperAdapter<MarketListBean>
{
    public OptionPopAdapter(Context context, List<MarketListBean> list, int layoutResId)
    {
        super(context, list, layoutResId);
    }

    @Override
    public void onBind(int viewType, BaseViewHolder holder, int position, MarketListBean item)
    {
        RequestOptions ro = new RequestOptions();
        ro.centerCrop();
        Glide.with(mContext).load(item.getLogo()).apply(ro).into((ImageView) holder.getView(R.id.iv_option));
        holder.setText(R.id.tv_option_name, item.getCnName() + "(" + item.getCoinName() + ")");
    }
}