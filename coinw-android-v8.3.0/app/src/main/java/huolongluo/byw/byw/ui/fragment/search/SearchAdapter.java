package huolongluo.byw.byw.ui.fragment.search;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.SelectCoinBean;

/**
 * Created by LS on 2018/7/16.
 */

public class SearchAdapter extends BaseAdapter{
    private Context context;
    private List<SelectCoinBean> mData;
    private OperationClickListener clickListener;

    public SearchAdapter(Context context, List<SelectCoinBean> mData,OperationClickListener listener) {
        this.context = context;
        this.mData = mData;
        this.clickListener = listener;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.activity_search_new_item,null);
            holder = new ViewHolder();
            holder.ivLogo = view.findViewById(R.id.iv_logo);
            holder.tvName = view.findViewById(R.id.tv_name);
            holder.tvName1 = view.findViewById(R.id.tv_name1);
            holder.tvMoney = view.findViewById(R.id.tv_money);
            holder.tvRate = view.findViewById(R.id.tv_rate);
            holder.iv_zixuan = view.findViewById(R.id.iv_zixuan);
            holder.ll_zixuan = view.findViewById(R.id.ll_zixuan);

            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        SelectCoinBean coinBean = mData.get(i);
        if (coinBean != null){

            RequestOptions ro = new RequestOptions();
            ro.error(R.mipmap.rmblogo);
            ro.centerCrop();
            Glide.with(context).load(coinBean.getLogo()).apply(ro).into((ImageView) holder.ivLogo);
            holder.tvName.setText(coinBean.getCoinName());
            holder.tvName1.setText("/"+coinBean.getCnyName());
            holder.tvMoney.setText("¥"+coinBean.getLatestDealPrice());
            holder.tvRate.setText(coinBean.getPriceRaiseRate() + "%");
            if (Double.valueOf(coinBean.getPriceRaiseRate()) <= 0){
//                holder.tvMoney.setTextColor(context.getResources().getColor(R.color.F0CB810));
                holder.tvRate.setBackground(context.getResources().getDrawable(R.drawable.rate_green_bg));
            }else {
//                holder.tvMoney.setTextColor(context.getResources().getColor(R.color.FE4950));
                holder.tvRate.setBackground(context.getResources().getDrawable(R.drawable.rate_bg));
            }

            if (coinBean.getSelfselection().equals("0")){
                holder.iv_zixuan.setImageDrawable(context.getResources().getDrawable(R.mipmap.zixuan));
            }else {
                holder.iv_zixuan.setImageDrawable(context.getResources().getDrawable(R.mipmap.zixuan_2));
            }
        }

        holder.ll_zixuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (clickListener != null){
                        int tag = (int)view.getTag();
                        clickListener.delete(tag);
                    }
            }
        });


        holder.ll_zixuan.setTag(i);
        return view;
    }

    static class ViewHolder{
        private ImageView ivLogo;
        private TextView tvName;
        private TextView tvName1;
        private TextView tvMoney;
        private TextView tvRate;
        private ImageView iv_zixuan;
        private LinearLayout ll_zixuan;
    }

    public interface OperationClickListener {

        void delete(int position);

    }
}
