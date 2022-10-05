package huolongluo.byw.reform.mine.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.reform.mine.bean.BindHpyBean;
import huolongluo.byw.reform.mine.bean.HppRecord;
import huolongluo.byw.util.DateUtils;

/**
 * Created by Administrator on 2019/2/18 0018.
 */
public class HppAdapter extends BaseAdapter {

    Context context;
    List<HppRecord> list = new ArrayList<>();

    public HppAdapter(List<HppRecord> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public HppRecord getItem(int position) {
        if (position < 0 || list == null || list.isEmpty()) {
            return null;
        }
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HppRecord bean = list.get(position);
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.hppcharge_item, null);
        }
        ImageView type_iv = convertView.findViewById(R.id.type_iv);
        TextView state_tv = convertView.findViewById(R.id.state_tv);
        TextView time_tv = convertView.findViewById(R.id.time_tv);
        TextView id_tv = convertView.findViewById(R.id.id_tv);
        TextView amount_tv = convertView.findViewById(R.id.amount_tv);
        TextView coinName_tv = convertView.findViewById(R.id.coinName_tv);
        if (bean.getType() == 1) {
            type_iv.setImageResource(R.drawable.chongzhi);
            amount_tv.setText(bean.getAmount() + "");
            amount_tv.setTextColor(context.getResources().getColor(R.color.ffff5763));
            coinName_tv.setTextColor(context.getResources().getColor(R.color.ffff5763));
        } else {
            amount_tv.setTextColor(context.getResources().getColor(R.color.ff44c09c));
            amount_tv.setText("-" + bean.getAmount() + "");
            coinName_tv.setTextColor(context.getResources().getColor(R.color.ff44c09c));
            type_iv.setImageResource(R.drawable.tixian);
        }
        state_tv.setText(bean.getStatus_s());
        if (bean.getFcreateTime() != null) {
            time_tv.setText(DateUtils.format(bean.getFcreateTime().getTime(), "yyyy-MM-dd HH:mm:ss"));
        }
        if (bean.getStatus() == 2 && bean.getType() == 1) {
            id_tv.setVisibility(View.VISIBLE);
            id_tv.setText("ID:" + bean.getOrderNo());
        } else {
            id_tv.setVisibility(View.GONE);
        }
        if (bean.getCoinType() != null) {
            coinName_tv.setText(bean.getCoinType().getfShortName());
        }
        return convertView;
    }
}
