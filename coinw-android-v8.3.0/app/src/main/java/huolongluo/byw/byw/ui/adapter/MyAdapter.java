package huolongluo.byw.byw.ui.adapter;

import android.content.Context;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.ItemBean;
import huolongluo.byw.superAdapter.recycler.BaseViewHolder;
import huolongluo.byw.superAdapter.recycler.IMultiItemViewType;
import huolongluo.byw.superAdapter.recycler.SuperAdapter;

/**
 * Created by Administrator on 2017/8/10 0010.
 */

public class MyAdapter extends SuperAdapter<ItemBean>
{

    /**
     * 只加载一种Item布局，就用这个构造方法
     */
    public MyAdapter(Context context, List list, int layoutResId)
    {
        super(context, list, layoutResId);
    }


    /**
     * 加载多种不同Item布局，就用这个构造方法
     */
    public MyAdapter(Context context, List list, IMultiItemViewType multiItemViewType)
    {
        super(context, list, multiItemViewType);
    }

    @Override
    public void onBind(int viewType, BaseViewHolder holder, int position, ItemBean item)
    {
        holder.setText(R.id.tv_title, item.getTitle());
        holder.setText(R.id.tv_content, item.getContent());
    }
}
