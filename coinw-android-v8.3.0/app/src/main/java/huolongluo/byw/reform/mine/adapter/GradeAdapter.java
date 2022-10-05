package huolongluo.byw.reform.mine.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.reform.mine.bean.VipBean;

/**
 * Created by hy on 2019/3/5 0005.
 */

public class GradeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int Type1 = 1, Type2 = 2;

    private int[] vipImg0 = {R.mipmap.v1_0, R.mipmap.v2_0, R.mipmap.v3_0, R.mipmap.v4_0, R.mipmap.v5_0, R.mipmap.v6_0, R.mipmap.v7_0};
    private int[] vipImg1 = {R.mipmap.v1_1, R.mipmap.v2_1, R.mipmap.v3_1, R.mipmap.v4_1, R.mipmap.v5_1, R.mipmap.v6_1, R.mipmap.v7_1};
    private int[] vipImg2 = {R.mipmap.v1_2, R.mipmap.v2_2, R.mipmap.v3_2, R.mipmap.v4_2, R.mipmap.v5_2, R.mipmap.v6_2, R.mipmap.v7_2};
    private int[] color = {R.color.b3ffcb30, R.color.b3fda61f, R.color.b3f99017, R.color.b3f99017, R.color.b3f36a08, R.color.b3f36a08};
    List<VipBean.ViP> viPList = new ArrayList<>();

    private String[] gradeFee;
    int currentGrade = 1;
    Context context;

    public GradeAdapter(int grade, List<VipBean.ViP> viPList, Context context) {

        currentGrade = grade;
        this.viPList = viPList;

        this.context = context;


        gradeFee = context.getResources().getStringArray(R.array.grade_fee);


    }

    public interface OnClickListener {

        void onClick(int grade, String price);

    }

    OnClickListener onClickListener;


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setCurrentGrade(int currentGrade) {
        this.currentGrade = currentGrade;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Type1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grade_list_item, parent, false);
            return new GradeViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_des, parent, false);
            return new FooterViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == viPList.size()) {
            return Type2;
        } else {
            return Type1;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderType, int position) {


        if (position == 0) {
            // holder.text3_tv.setVisibility(View.GONE);
        }
        if (holderType instanceof GradeViewHolder) {
            GradeViewHolder holder = (GradeViewHolder) holderType;
            if (viPList.get(position).getId() < currentGrade) {
                holder.icon_iv.setImageResource(vipImg0[position]);
                holder.text1_tv.setTextColor(context.getResources().getColor(R.color.ffbab7cc));
                holder.text2_tv.setTextColor(context.getResources().getColor(R.color.ffbab7cc));
                holder.text1_tv1.setTextColor(context.getResources().getColor(R.color.ffbab7cc));
                holder.text_tv1.setTextColor(context.getResources().getColor(R.color.ffbab7cc));
                //  holder.text3_tv.setVisibility(View.GONE);
                holder.updata_tv.setVisibility(View.GONE);
                holder.rl.setBackgroundColor(context.getResources().getColor(R.color.white));
            } else if (viPList.get(position).getId() > currentGrade) {
                holder.icon_iv.setImageResource(vipImg1[position]);
                holder.text1_tv.setTextColor(context.getResources().getColor(R.color.ff333333));
                holder.text2_tv.setTextColor(context.getResources().getColor(R.color.ff6f6699));
                holder.text1_tv1.setTextColor(context.getResources().getColor(R.color.ff6f6699));
                holder.text_tv1.setTextColor(context.getResources().getColor(R.color.ff6f6699));
                holder.text3_tv.setVisibility(View.VISIBLE);
                //   holder.text3_tv.setText("所需"+viPList.get(position).getCoinsCount()+" Coins");
                holder.updata_tv.setVisibility(View.VISIBLE);
                holder.rl.setBackgroundColor(context.getResources().getColor(R.color.white));
            } else {
                holder.icon_iv.setImageResource(vipImg2[position]);
                holder.text1_tv.setTextColor(context.getResources().getColor(R.color.fff99017));
                holder.text2_tv.setTextColor(context.getResources().getColor(R.color.fff99017));
                holder.text_tv2.setTextColor(context.getResources().getColor(R.color.fff99017));
                holder.text1_tv1.setTextColor(context.getResources().getColor(R.color.fff99017));
                holder.text_tv1.setTextColor(context.getResources().getColor(R.color.fff99017));
                holder.text3_tv.setVisibility(View.GONE);
                holder.updata_tv.setVisibility(View.GONE);
                holder.rl.setBackgroundColor(context.getResources().getColor(color[position]));
            }
            if (!TextUtils.equals(viPList.get(position).getCoinsCount(), "0") && viPList.get(position).getId() != currentGrade) {
                holder.text3_tv.setVisibility(View.VISIBLE);
//            holder.text3_tv.setText(String.format(context.getString(R.string.a2), viPList.get(position).getCoinsCount()));
                holder.text3_tv.setText(String.format(context.getString(R.string.buy_des), viPList.get(position).getCoinsCount()));
            } else {
                holder.text3_tv.setText("");
                holder.text3_tv.setVisibility(View.GONE);
            }
            holder.text2_tv.setText(viPList.get(position).getMakerFfees() + "%");
            holder.text_tv2.setText(viPList.get(position).getTakerFfees() + "%");
            holder.updata_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onClick(position + 1, viPList.get(position).getCoinsCount());
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return viPList.size() + 1;
    }

    public static class GradeViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rl;
        public ImageView icon_iv;
        public TextView text1_tv;
        public TextView text2_tv;
        public TextView text3_tv;
        public TextView updata_tv;
        public TextView text_tv2;
        public TextView text1_tv1;
        public TextView text_tv1;

        public GradeViewHolder(View itemView) {
            super(itemView);

            rl = itemView.findViewById(R.id.rl);
            icon_iv = itemView.findViewById(R.id.icon_iv);
            text1_tv = itemView.findViewById(R.id.text1_tv);
            text2_tv = itemView.findViewById(R.id.text2_tv);
            text3_tv = itemView.findViewById(R.id.text3_tv);
            updata_tv = itemView.findViewById(R.id.updata_tv);
            text_tv2 = itemView.findViewById(R.id.text_tv2);
            text1_tv1 = itemView.findViewById(R.id.text1_tv1);
            text_tv1 = itemView.findViewById(R.id.text_tv1);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

}
