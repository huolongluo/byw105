package huolongluo.byw.byw.ui.adapter;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentManager;

import java.text.DecimalFormat;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.ui.dialog.AddDialog;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.bean.EntrustInfoBean;
import huolongluo.byw.superAdapter.recycler.BaseViewHolder;
import huolongluo.byw.superAdapter.recycler.SuperAdapter;
import huolongluo.bywx.utils.DoubleUtils;
/**
 * Created by 火龙裸 on 2017/12/29.
 */
public class CancelListAdapter extends SuperAdapter<EntrustInfoBean> {
    private FragmentManager fragmentManager;
    private DecimalFormat df;
    private DecimalFormat df2;

    public CancelListAdapter(Context context, List<EntrustInfoBean> list, int layoutResId, FragmentManager fragmentManager) {
        super(context, list, layoutResId);
        this.fragmentManager = fragmentManager;
        df = new DecimalFormat("0.########");
        df2 = new DecimalFormat("0.##");
    }

    @Override
    public void onBind(int viewType, BaseViewHolder holder, int position, EntrustInfoBean item) {
        if (item.getTitle().equals(mContext.getString(R.string.d1))) {
            holder.setImageResource(R.id.iv_history_title, R.mipmap.buy);
        } else {
            holder.setImageResource(R.id.iv_history_title, R.mipmap.sell);
        }
        holder.setText(R.id.tv_history_updat_time, item.getFlastUpdatTime()); // 时间
        holder.setText(R.id.tv_history_fprice, df.format(item.getFprice()) + ""); // 委托价格
        holder.setText(R.id.tv_history_fsuccessprice, df.format(item.getFsuccessprice()) + ""); // 成交价格
        holder.setText(R.id.tv_history_famount, df2.format(item.getFcount()) + "");
        holder.setText(R.id.tv_history_successamount, "" + df2.format((DoubleUtils.parseDouble(item.getFcount()) - DoubleUtils.parseDouble(item.getFsuccessCount()))));
        holder.setVisibility(R.id.iv_history_cancle_order, View.VISIBLE);
        holder.setVisibility(R.id.tv_statu, View.GONE);
        eventClick(holder.getView(R.id.iv_history_cancle_order)).subscribe(v -> {
            AddDialog dialog = new AddDialog();
            Bundle bundle = new Bundle();
            bundle.putString("orderId", item.getFid() + "");
            bundle.putInt("position", position);
            dialog.setArguments(bundle);
            dialog.setDialog(AddDialog.CANCEL_ORDER);
            dialog.show(fragmentManager, getClass().getSimpleName());
        }, throwable -> {
            Logger.getInstance().error(throwable);
        });
    }
}
