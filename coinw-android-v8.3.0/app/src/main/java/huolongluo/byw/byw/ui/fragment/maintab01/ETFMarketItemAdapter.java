package huolongluo.byw.byw.ui.fragment.maintab01;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import huolongluo.byw.R;
import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.util.MathHelper;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.pricing.PricingMethodUtil;
//etf行情
public class ETFMarketItemAdapter extends BaseAdapter {
    private Context context;
    private List<MarketListBean> mData;

    public ETFMarketItemAdapter(Context context, List<MarketListBean> list) {
        this.context = context;
        this.mData = list;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public MarketListBean getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_market, null);
            holder = new ViewHolder();
            holder.tvName = view.findViewById(R.id.tv_name);
            holder.tv_name1 = view.findViewById(R.id.tv_name1);
            holder.tvVol = view.findViewById(R.id.tv_vol);
            holder.tvMoney = view.findViewById(R.id.tv_money);
            holder.tvMoney1 = view.findViewById(R.id.tv_money1);
            holder.tvRate = view.findViewById(R.id.tv_rate);
            holder.ll_main = view.findViewById(R.id.ll_main);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        MarketListBean listBean = mData.get(i);
        if (listBean != null) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.mipmap.rmblogo);
            requestOptions.error(R.mipmap.rmblogo);
            requestOptions.centerCrop();
            holder.tvName.setText(listBean.getCoinName());
            holder.tv_name1.setText("/" + listBean.getCnyName());
            holder.tvVol.setText(PricingMethodUtil.getLargePrice(listBean.getOneDayTotal()+"",3));

            if(!TextUtils.isEmpty(listBean.getLatestDealPrice())){
                holder.tvMoney.setText(listBean.getLatestDealPrice() + "");
                holder.tvMoney1.setText("≈"+PricingMethodUtil.getPricingUnit() + PricingMethodUtil.getResultByExchangeRate(
                        listBean.getLatestDealPrice(),listBean.getCnyName()
                ));
            }
            double ratiod= MathHelper.mul(100.0, listBean.getPriceRaiseRate());
            String ratio = NorUtils.NumberFormat(2).format(ratiod);
            if (listBean.getPriceRaiseRate() >= 0) {
                if (listBean.getPriceRaiseRate() == 0) {
                    holder.tvRate.setText(ratio + "%");
                } else {
                    holder.tvRate.setText("+" + ratio + "%");
                }
                holder.tvRate.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_cor5_up));
            } else {
                holder.tvRate.setText(ratio + "%");
                holder.tvRate.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_cor5_drop));
            }
        }
        holder.ll_main.setTag(i);
        return view;
    }

    static class ViewHolder {
        private TextView tvName;
        private TextView tvVol;
        private TextView tvMoney;
        private TextView tvMoney1;
        private TextView tvRate;
        private TextView tv_name1;
        private LinearLayout ll_main;
    }

    public String getTime() {
        long time = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳
        String str = String.valueOf(time);
        return str;
    }
}
