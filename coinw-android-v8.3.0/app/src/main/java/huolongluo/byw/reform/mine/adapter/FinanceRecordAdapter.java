package huolongluo.byw.reform.mine.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.model.FinanceRecordBean;
import huolongluo.byw.util.Constant;
import huolongluo.bywx.utils.DoubleUtils;
/**
 * Created by Administrator on 2018/11/29 0029.
 */
public class FinanceRecordAdapter extends BaseAdapter {

    Context context;
    List<FinanceRecordBean> list = new ArrayList<>();

    public FinanceRecordAdapter(Context context) {
        this.context = context;
    }
    public void clear(){
        if(list==null){
            return;
        }
        list.clear();
        notifyDataSetChanged();
    }
    public void refresh(List<FinanceRecordBean> list) {
        if(list==null||this.list==null){
            return;
        }
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public FinanceRecordBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_coindatail_activity, null);
            viewHolder.status_tv = convertView.findViewById(R.id.status_tv);
            viewHolder.time_tv = convertView.findViewById(R.id.time_tv);
            viewHolder.money_tv = convertView.findViewById(R.id.money_tv);
            viewHolder.coinName_tv = convertView.findViewById(R.id.coinName_tv);
            //viewHolder.logo = convertView.findViewById(R.id.logo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FinanceRecordBean bean=list.get(position);

        viewHolder.status_tv.setText(bean.getStatus());
        viewHolder.time_tv.setText(bean.getCreateTime());
        viewHolder.money_tv.setText(bean.getNumber());
        viewHolder.coinName_tv.setText(bean.getCoinName());

        switch (bean.getQueryType()){
            case Constant.TYPE_FINANCE_RECORD_AIR:
                viewHolder.status_tv.setText(bean.getFremark());
                viewHolder.money_tv.setTextColor(context.getResources().getColor(R.color.ffff5763));
                viewHolder.coinName_tv.setTextColor(context.getResources().getColor(R.color.ffff5763));
               // viewHolder.logo.setImageResource(R.mipmap.kongtou);
                break;
            case Constant.TYPE_FINANCE_RECORD_FEE:
                viewHolder.money_tv.setTextColor(context.getResources().getColor(R.color.ffff5763));
                viewHolder.coinName_tv.setTextColor(context.getResources().getColor(R.color.ffff5763));
               // viewHolder.logo.setImageResource(R.mipmap.kongtou);
                break;
            case Constant.TYPE_FINANCE_RECORD_RECHARGE:
                if ("1".equals(bean.getFtype())) {//充值
                    //viewHolder.logo.setImageResource(R.drawable.chongzhi);
                    viewHolder.money_tv.setTextColor(context.getResources().getColor(R.color.ffff5763));
                    viewHolder.coinName_tv.setTextColor(context.getResources().getColor(R.color.ffff5763));
                } else {//提现
                    //viewHolder.logo.setImageResource(R.drawable.tixian);
                    viewHolder.money_tv.setTextColor(context.getResources().getColor(R.color.ff44c09c));
                    viewHolder.coinName_tv.setTextColor(context.getResources().getColor(R.color.ff44c09c));
                }
                break;
            case Constant.TYPE_FINANCE_RECORD_MANAGE_MONEY:
               // viewHolder.logo.setImageResource(R.mipmap.ic_manage_money);
                if(DoubleUtils.parseDouble(bean.getNumber())<0){
                    viewHolder.money_tv.setText(bean.getNumber());
                    viewHolder.money_tv.setTextColor(context.getResources().getColor(R.color.ff44c09c));
                    viewHolder.coinName_tv.setTextColor(context.getResources().getColor(R.color.ff44c09c));
                }else{
                    viewHolder.money_tv.setText("+"+bean.getNumber());
                    viewHolder.money_tv.setTextColor(context.getResources().getColor(R.color.ffff5763));
                    viewHolder.coinName_tv.setTextColor(context.getResources().getColor(R.color.ffff5763));
                }
                break;
        }
        return convertView;
    }

    private static class ViewHolder {

        public TextView status_tv;
        public TextView time_tv;
        public TextView money_tv;
        public TextView coinName_tv;
        //public ImageView logo;
    }
}
