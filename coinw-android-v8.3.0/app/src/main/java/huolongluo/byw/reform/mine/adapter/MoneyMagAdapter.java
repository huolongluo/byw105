package huolongluo.byw.reform.mine.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.reform.base.ByperpayBean;
import huolongluo.byw.reform.mine.click.CheckHppClick;
import huolongluo.byw.util.DateUtils;
/**
 * Created by Administrator on 2019/1/6 0006.
 */
public class MoneyMagAdapter extends BaseMoneyMagAdapter {
    public List<ByperpayBean.LicaiBean> list = new ArrayList<>();
    public Context context;
    private String imageUrl = "https://www.hyperpay.tech/data/upload/";

    public MoneyMagAdapter(CheckHppClick click, List<ByperpayBean.LicaiBean> list, Context context) {
        this.list.addAll(list);
        this.context = context;
        checkHppClick = click;
    }

    @Override
    public void updata(List<ByperpayBean.LicaiBean> list) {
        super.updata(list);
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public BaseMoneyMagAdapter.ViewHole onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_moneymanager_item, parent, false);
        BaseMoneyMagAdapter.ViewHole viewHole = new BaseMoneyMagAdapter.ViewHole(view);
        return viewHole;
    }

    @Override
    public void onBindViewHolder(BaseMoneyMagAdapter.ViewHole holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.baifenbi_tv.setText(list.get(position).getRate());
        RequestOptions ro = new RequestOptions();
        ro.error(R.mipmap.default_coin);
        ro.centerCrop();
        Glide.with(context).load(imageUrl + list.get(position).getIco_url()).apply(ro).into(holder.logo_iv1);
        holder.miaoshu_tv.setText(list.get(position).getInfo());
        holder.info_tv.setText(list.get(position).getKeywords());
        holder.coin_name_tv.setText(list.get(position).getTitle());
        if (android.text.TextUtils.isEmpty(list.get(position).getStart_in())) {
            holder.huodong_tv.setVisibility(View.GONE);
        } else {
            if ("0".equals(list.get(position).getStart_in())) {
                holder.huodong_tv.setVisibility(View.GONE);
            } else {
                holder.huodong_tv.setVisibility(View.VISIBLE);
                long time = Long.parseLong(list.get(position).getStart_in());
                holder.huodong_tv.setText(DateUtils.format(time * 1000, "yyyy-MM-dd HH:mm" + context.getResources().getString(R.string.hp_time)));
            }
        }
        holder.main_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkHppClick != null) {
                    checkHppClick.onClick();
                }
            }
        });
    }

    /* @Override
     public ViewHole onCreateViewHolder(ViewGroup parent, int viewType) {

         View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_moneymanager_item, parent, false);
         ViewHole viewHole=new ViewHole(view);
         return viewHole;
     }





     @Override
     public void onBindViewHolder(ViewHole holder, int position) {


         holder.baifenbi_tv.setText(list.get(position).getRate());


         Glide.with(context).load(imageUrl+list.get(position).getIco_url()).error(R.mipmap.rmblogo).centerCrop().into( holder.logo_iv1);
         holder.info_tv.setText(list.get(position).getInfo());
     }
 */
    @Override
    public int getItemCount() {
        Log.e("getItemCount", "== " + list.size());
        return list.size();
    }

    public static class ViewHole extends RecyclerView.ViewHolder {
        public ImageView logo_iv1;
        public TextView coin_name_tv;
        public TextView baifenbi_tv;
        public TextView miaoshu_tv;
        public TextView info_tv;

        public ViewHole(View itemView) {
            super(itemView);
            this.logo_iv1 = itemView.findViewById(R.id.logo_iv1);
            this.coin_name_tv = itemView.findViewById(R.id.coin_name_tv);
            this.baifenbi_tv = itemView.findViewById(R.id.baifenbi_tv);
            this.miaoshu_tv = itemView.findViewById(R.id.miaoshu_tv);
            this.info_tv = itemView.findViewById(R.id.info_tv);
        }
    }
}
