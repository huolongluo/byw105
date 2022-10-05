package huolongluo.byw.byw.ui.activity.coindetail;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.List;

import huolongluo.byw.R;

/**
 * Created by LS on 2018/7/18.
 */

public class CoinDetailAdapter extends BaseAdapter{
    private Context context;
    private List<CoinDetailList> mData;
    private OperationClickListener clickListener = null;

    public CoinDetailAdapter(Context context, List<CoinDetailList> mData,OperationClickListener listener) {
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
            view = LayoutInflater.from(context).inflate(R.layout.activity_coin_details_item,null);
            holder = new ViewHolder();
            holder.iv_self = view.findViewById(R.id.iv_self);
            holder.tv_name = view.findViewById(R.id.tv_name);
            holder.tv_name1 = view.findViewById(R.id.tv_name1);
            holder.tv_coin = view.findViewById(R.id.tv_coin);
            holder.tv_coin1 = view.findViewById(R.id.tv_coin1);
            holder.tv_rate = view.findViewById(R.id.tv_rate);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        CoinDetailList list = mData.get(i);
        if (list != null){
            holder.tv_name.setText(list.getCointype());
            holder.tv_name1.setText("/"+list.getFb());
            holder.tv_coin.setText("¥"+list.getPrice());
            holder.tv_coin1.setText("/"+list.getFb());
            holder.tv_coin1.setVisibility(View.GONE);
            holder.tv_rate.setText(list.getRose() +"%");
            if (list.getRose() >= 0){
                holder.tv_rate.setBackground(context.getResources().getDrawable(R.drawable.rate_bg));
            }else {
                holder.tv_rate.setBackground(context.getResources().getDrawable(R.drawable.rate_green_bg));
            }
            if (list.getIsSelf().equals("true")){
                holder.iv_self.setImageResource(R.mipmap.zixuan_2);
            }else {
                holder.iv_self.setImageResource(R.mipmap.zixuan);
            }
        }
        holder.iv_self.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null){
                    int tag = (int) view.getTag();
                    clickListener.self(tag);
                }
            }
        });
        holder.iv_self.setTag(i);
        return view;
    }

    static class ViewHolder{
        private ImageView iv_self;
        private TextView tv_name;
        private TextView tv_name1;
        private TextView tv_coin;
        private TextView tv_coin1;
        private TextView tv_rate;
    }

    public interface OperationClickListener{
        void self(int position);
    }
}
