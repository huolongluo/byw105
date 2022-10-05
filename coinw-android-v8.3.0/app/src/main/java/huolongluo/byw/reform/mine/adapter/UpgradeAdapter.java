package huolongluo.byw.reform.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.reform.mine.bean.VipPurchaseBean;

/**
 * Created by hy on 2019/3/5 0005.
 */

public class UpgradeAdapter extends RecyclerView.Adapter<UpgradeAdapter.GradeViewHolder> {

    Context context;
    List<VipPurchaseBean.VipPurchase> list = new ArrayList<>();

    public UpgradeAdapter(Context context, List<VipPurchaseBean.VipPurchase> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public GradeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_upgrade_list_item, parent, false);

        GradeViewHolder viewHolder = new GradeViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GradeViewHolder holder, int position) {
        VipPurchaseBean.VipPurchase item = list.get(position);


        holder.text2_tv.setText(item.getCreateTime());
        holder.text4_tv.setText(item.getSendTime());
        //https://jira.legenddigital.com.au/browse/COIN-1492
        holder.text6_tv.setText(item.getCoinsCount() + " CWT");
        holder.grade_tv.setText("VIP" + item.getFfees());
        if(position==0){
            holder.linearLayout.setBackgroundResource(R.drawable.vip_bg8);
        }
        if (item.getStatus() == 1) {

            if (position == 0) {
                holder.rl.setBackgroundResource(R.drawable.vip_bg6);
                holder.grade_tv.setBackgroundResource(R.drawable.vip_bg5);
            } else {
                holder.rl.setBackgroundColor(context.getResources().getColor(R.color.f1aef5402));
                holder.grade_tv.setBackgroundResource(R.drawable.vip_bg2);
            }
            holder.text2_tv.setTextColor(context.getResources().getColor(R.color.ff6f6699));
            holder.status_tv.setText(R.string.qs33);
            holder.status_tv.setTextColor(context.getResources().getColor(R.color.ffef5402));
            holder.text4_tv.setTextColor(context.getResources().getColor(R.color.ff6f6699));
            holder.text6_tv.setTextColor(context.getResources().getColor(R.color.ff6f6699));

        } else {

            if (position == 0) {
                holder.rl.setBackgroundResource(R.drawable.vip_bg7);
                holder.grade_tv.setBackgroundResource(R.drawable.vip_bg4);
            } else {
                holder.rl.setBackgroundColor(context.getResources().getColor(R.color.fff3f3fb));
                holder.grade_tv.setBackgroundResource(R.drawable.vip_bg3);
            }

            holder.text2_tv.setTextColor(context.getResources().getColor(R.color.ffbab7cc));
            holder.text4_tv.setTextColor(context.getResources().getColor(R.color.ffbab7cc));
            holder.text6_tv.setTextColor(context.getResources().getColor(R.color.ffbab7cc));

            holder.status_tv.setText(R.string.qs34);
            holder.status_tv.setTextColor(context.getResources().getColor(R.color.ffbab7cc));
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class GradeViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rl;
        public TextView grade_tv;
        public TextView status_tv;
        public TextView text1_tv;
        public TextView text2_tv;
        public TextView text3_tv;
        public TextView text4_tv;
        public TextView text6_tv;
        public LinearLayout linearLayout;

        public GradeViewHolder(View itemView) {
            super(itemView);

            rl = itemView.findViewById(R.id.rl);
            grade_tv = itemView.findViewById(R.id.grade_tv);
            status_tv = itemView.findViewById(R.id.status_tv);
            text1_tv = itemView.findViewById(R.id.text1_tv);
            text2_tv = itemView.findViewById(R.id.text2_tv);
            text3_tv = itemView.findViewById(R.id.text3_tv);
            text4_tv = itemView.findViewById(R.id.text4_tv);
            text6_tv = itemView.findViewById(R.id.text6_tv);
            linearLayout = itemView.findViewById(R.id.linearLayout);

        }
    }

}
