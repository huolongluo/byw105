package huolongluo.byw.reform.c2c.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.c2c.oct.bean.PayOrderInfoBean;
import huolongluo.bywx.utils.AppUtils;
public class RechargeAdapter extends BaseAdapter {
    //TODO 待优化
    private int selectIndex = 0;
    private Context context;
    private List<PayOrderInfoBean.PayAccountsBean> dataList;
    private boolean isMulti = false;
    private List<Integer> selectIndexList = new ArrayList<>();
    private boolean enable = true;//是否可选择使用

    public RechargeAdapter(Context context, List<PayOrderInfoBean.PayAccountsBean> list) {
        this.context = context;
        this.dataList = list;
    }

    public RechargeAdapter(Context context, List<PayOrderInfoBean.PayAccountsBean> list, boolean isMulti, boolean enable) {
        this.context = context;
        this.dataList = list;
        this.isMulti = isMulti;
        this.enable = enable;
    }

    public RechargeAdapter(Context context, List<PayOrderInfoBean.PayAccountsBean> list, boolean isMulti) {
        this.context = context;
        this.dataList = list;
        this.isMulti = isMulti;
        selectIndexList.add(0);//默认选中第一条数据
    }

    public void setSelectEnable(boolean enable) {
        this.enable = enable;
    }

    public void select(int selectIndex) {
        if (isMulti) {
            if (selectIndexList.contains(selectIndex)) {
                selectIndexList.remove((Integer) selectIndex);
            } else {
                selectIndexList.add((Integer) selectIndex);
            }
        } else {
            this.selectIndex = selectIndex;
        }
    }

    public void selectPayType(int payType) {
        if (dataList == null || payType < 0) {
            return;
        }
        for (int i = 0; i < dataList.size(); i++) {
            PayOrderInfoBean.PayAccountsBean bean = dataList.get(i);
            if (bean == null) {
                continue;
            }
            if (payType == bean.getType()) {
                selectIndex = i;
                notifyDataSetChanged();
                return;
            }
        }
    }

    public PayOrderInfoBean.PayAccountsBean getPaymentAccount() {
        if (dataList == null || selectIndex >= dataList.size()) {
            return null;
        }
        return dataList.get(selectIndex);
    }

    public int getPayType() {
        if (dataList == null | dataList.isEmpty()) {
            return -1;
        }
        if (dataList.size() <= selectIndex) {
            return -1;
        }
        return dataList.get(selectIndex).getType();
    }

    public List<Integer> getPayTypes() {
        return selectIndexList;
    }

    public String getPayIds() {
        if (dataList == null) {
            return "";
        }
        StringBuilder selectAccount = new StringBuilder();
        for (int i = 0; i < dataList.size(); i++) {
            PayOrderInfoBean.PayAccountsBean bean = dataList.get(i);
            if (bean == null) {
                continue;
            }
            for (Integer index : selectIndexList) {
                if (index == i) {
                    selectAccount = selectAccount.append(bean.getId()).append(",");
                }
            }
        }
        return selectAccount.toString();
    }

    @Override
    public int getCount() {
        return dataList == null || dataList.isEmpty() ? 0 : dataList.size();
    }

    @Override
    public PayOrderInfoBean.PayAccountsBean getItem(int i) {
        if (i < 0 || dataList == null || dataList.isEmpty()) {
            return null;
        }
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_payment_item, null);
            holder = new ViewHolder();
            holder.rootView = view.findViewById(R.id.rl_root);
            holder.rootView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, AppUtils.dp2px(45)));
            holder.iconIV = view.findViewById(R.id.iv_icon);
            holder.titleTxt = view.findViewById(R.id.tv_title);
            holder.shortNameTxt = view.findViewById(R.id.tv_short_name);
            holder.statusIV = view.findViewById(R.id.iv_status);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        PayOrderInfoBean.PayAccountsBean bean = getItem(i);
        switch (bean.getType()) {
            case 3:
                holder.iconIV.setImageResource(R.mipmap.zfb);
                holder.titleTxt.setText(R.string.b92);
                break;
            case 2:
                holder.iconIV.setImageResource(R.mipmap.wx);
                holder.titleTxt.setText(R.string.b93);
                break;
            case 1:
                holder.iconIV.setImageResource(R.mipmap.zfsz);
                holder.titleTxt.setText(R.string.b91);
                break;
        }
        if (enable) {
            holder.statusIV.setVisibility(View.VISIBLE);
        } else {
            holder.statusIV.setVisibility(View.GONE);
        }
        String shortName = bean.getAccount();
        try {
            shortName = String.format("(%s)", bean.getAccount().substring(0, 4));
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
        holder.shortNameTxt.setText(shortName);
        if (isMulti) {
            if (selectIndexList.contains(i)) {
                holder.rootView.setBackgroundColor(context.getResources().getColor(R.color.ffdbdbea));
                holder.statusIV.setImageResource(R.mipmap.select);
            } else {
                holder.rootView.setBackgroundColor(context.getResources().getColor(R.color.white));
                holder.statusIV.setImageResource(R.mipmap.unselect);
            }
        } else if (enable) {
            if (selectIndex == i) {
                holder.rootView.setBackgroundColor(context.getResources().getColor(R.color.ffdbdbea));
                holder.statusIV.setImageResource(R.mipmap.select);
            } else {
                holder.rootView.setBackgroundColor(context.getResources().getColor(R.color.white));
                holder.statusIV.setImageResource(R.mipmap.unselect);
            }
        }
        return view;
    }

    public static class ViewHolder {
        RelativeLayout rootView;
        ImageView iconIV, statusIV;
        TextView titleTxt, shortNameTxt;
    }
}
