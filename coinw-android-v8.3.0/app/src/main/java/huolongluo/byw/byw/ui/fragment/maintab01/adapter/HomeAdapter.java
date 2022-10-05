package huolongluo.byw.byw.ui.fragment.maintab01.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.legend.common.util.ThemeUtil;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.ui.fragment.maintab01.bean.CoinInfoBean;
import huolongluo.byw.util.MathHelper;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.bywx.utils.DoubleUtils;
/**
 * Created by Administrator on 2018/10/18 0018.
 */
public class HomeAdapter extends BaseAdapter {
    private Context context;
    private int type = 0;
    private List<CoinInfoBean> list = new ArrayList<>();

    public HomeAdapter(Context context, int type) {
        this.context = context;
        this.type = type;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public List<CoinInfoBean> getCoinInfoList() {
        return list;
    }

    public void notifyData(int type, List<CoinInfoBean> list) {
        this.type = type;
        this.list.clear();
        //java.lang.NullPointerException: Attempt to invoke interface method 'java.lang.Object[] java.util.Collection.toArray()' on a null object reference
        //    at java.util.ArrayList.addAll(ArrayList.java:588)
        //    at huolongluo.byw.byw.ui.fragment.maintab01.adapter.HomeAdapter.notifyData(HomeAdapter.java:3)
        if (list != null) {
            this.list.addAll(list);
        }
        notifyDataSetChanged();
//        notifyDataSetInvalidated();
    }

    public void notifyData(List<CoinInfoBean> list) {
        if (list != null) {
            this.list.clear();
            this.list.addAll(list);
            notifyDataSetChanged();
//            notifyDataSetInvalidated();
        }
    }

    private int length = 10;

    public void setLength(int length) {
        if (this.length != length) {
            this.length = length;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return list.size() > length ? length : list.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < 0 || list == null || list.isEmpty()) {
            return null;
        }
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold viewHold;
        CoinInfoBean coinInfoBean = list.get(position);
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.home_page_listitem_1, null);
            viewHold = new ViewHold();
            viewHold.zhang_name_tv1 = convertView.findViewById(R.id.zhang_name_tv1);
            viewHold.zhang_name_tv1_1 = convertView.findViewById(R.id.zhang_name_tv1_1);
            viewHold.zhang_price_tv1 = convertView.findViewById(R.id.zhang_price_tv1);
            viewHold.zhang_price_tv1_1 = convertView.findViewById(R.id.zhang_price_tv1_1);
            viewHold.zhang_rate_tv1 = convertView.findViewById(R.id.zhang_rate_tv1);
            viewHold.zhang_rate_tv2 = convertView.findViewById(R.id.zhang_rate_tv2);
            viewHold.zhang_rate_tv22 = convertView.findViewById(R.id.zhang_rate_tv22);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }
        viewHold.zhang_name_tv1.setText(coinInfoBean.getCoinName());
        viewHold.zhang_name_tv1_1.setText("/" + coinInfoBean.getCnyName());
        viewHold.zhang_price_tv1.setText(coinInfoBean.getLatestDealPrice());
        double ratiod= MathHelper.mul(100.0, DoubleUtils.parseDouble(coinInfoBean.getPriceRaiseRate()));
        String ratio = NorUtils.NumberFormat(2).format(ratiod);
        if (type == 0) {//涨幅榜
            if (coinInfoBean.getPriceRaiseRate() != null && DoubleUtils.parseDouble(coinInfoBean.getPriceRaiseRate()) >= 0) {
                viewHold.zhang_price_tv1_1.setTextColor(context.getResources().getColor(R.color.ff8881a6));
                if ("USDT".equalsIgnoreCase(coinInfoBean.getCnyName())) {
                    viewHold.zhang_price_tv1_1.setText(context.getString(R.string.rmb) + coinInfoBean.getCurrencyPrize());
                } else {
                    viewHold.zhang_price_tv1_1.setText(coinInfoBean.getCurrencySymbol() + coinInfoBean.getCurrencyPrize());
                }
                viewHold.zhang_rate_tv1.setVisibility(View.VISIBLE);
                viewHold.zhang_rate_tv2.setVisibility(View.INVISIBLE);
                viewHold.zhang_rate_tv22.setVisibility(View.INVISIBLE);
                viewHold.zhang_rate_tv1.setBackground(ThemeUtil.INSTANCE.getThemeDrawable(context,R.attr.bg_buy_btn));
                if (DoubleUtils.parseDouble(coinInfoBean.getPriceRaiseRate()) >= 0) {
                    viewHold.zhang_rate_tv1.setText("+" + ratio + "%");
                } else {
                    viewHold.zhang_rate_tv1.setText(ratio + "%");
                }
            } else {
                viewHold.zhang_price_tv1_1.setTextColor(context.getResources().getColor(R.color.ff8881a6));
                if ("USDT".equalsIgnoreCase(coinInfoBean.getCnyName())) {
                    viewHold.zhang_price_tv1_1.setText("¥" + coinInfoBean.getCurrencyPrize());
                } else {
                    viewHold.zhang_price_tv1_1.setText(coinInfoBean.getCurrencySymbol() + coinInfoBean.getCurrencyPrize());
                }
                viewHold.zhang_price_tv1_1.setText(coinInfoBean.getCurrencySymbol() + coinInfoBean.getCurrencyPrize());
                viewHold.zhang_rate_tv2.setVisibility(View.INVISIBLE);
                viewHold.zhang_rate_tv22.setVisibility(View.INVISIBLE);
                viewHold.zhang_rate_tv1.setVisibility(View.VISIBLE);
                viewHold.zhang_rate_tv1.setBackground(ThemeUtil.INSTANCE.getThemeDrawable(context,R.attr.bg_sell_btn));
                viewHold.zhang_rate_tv1.setText(coinInfoBean.getLegalMoney() + "");
                viewHold.zhang_rate_tv1.setText(ratio + "%");
            }
            viewHold.zhang_price_tv1_1.setTextColor(context.getResources().getColor(R.color.ff9F9AB6));
            if(TextUtils.isEmpty(coinInfoBean.getLatestDealPrice())){
                viewHold.zhang_price_tv1_1.setText("≈"+ PricingMethodUtil.getPricingUnit()+"--");
            }else{
                viewHold.zhang_price_tv1_1.setText("≈"+ PricingMethodUtil.getPricingUnit()+PricingMethodUtil.getResultByExchangeRate(
                        coinInfoBean.getLatestDealPrice(),coinInfoBean.getCnyName()
                ));
            }

        } else if (type == 1) {//跌幅榜
            if (coinInfoBean.getPriceRaiseRate() != null && DoubleUtils.parseDouble(coinInfoBean.getPriceRaiseRate()) < 0) {
                viewHold.zhang_price_tv1_1.setTextColor(context.getResources().getColor(R.color.ff8881a6));
                if ("USDT".equalsIgnoreCase(coinInfoBean.getCnyName())) {
                    viewHold.zhang_price_tv1_1.setText("¥" + coinInfoBean.getCurrencyPrize());
                } else {
                    viewHold.zhang_price_tv1_1.setText(coinInfoBean.getCurrencySymbol() + coinInfoBean.getCurrencyPrize());
                }
                viewHold.zhang_rate_tv2.setVisibility(View.INVISIBLE);
                viewHold.zhang_rate_tv22.setVisibility(View.INVISIBLE);
                viewHold.zhang_rate_tv1.setVisibility(View.VISIBLE);
                viewHold.zhang_rate_tv1.setBackground(ThemeUtil.INSTANCE.getThemeDrawable(context,R.attr.bg_sell_btn));
                viewHold.zhang_rate_tv1.setText(coinInfoBean.getLegalMoney() + "");
                viewHold.zhang_rate_tv1.setText(ratio + "%");
            } else {
                viewHold.zhang_price_tv1_1.setTextColor(context.getResources().getColor(R.color.ff8881a6));
                if ("USDT".equalsIgnoreCase(coinInfoBean.getCnyName())) {
                    viewHold.zhang_price_tv1_1.setText("¥" + coinInfoBean.getCurrencyPrize());
                } else {
                    viewHold.zhang_price_tv1_1.setText(coinInfoBean.getCurrencySymbol() + coinInfoBean.getCurrencyPrize());
                }
                viewHold.zhang_rate_tv1.setVisibility(View.VISIBLE);
                viewHold.zhang_rate_tv2.setVisibility(View.INVISIBLE);
                viewHold.zhang_rate_tv22.setVisibility(View.INVISIBLE);
                viewHold.zhang_rate_tv1.setBackground(ThemeUtil.INSTANCE.getThemeDrawable(context,R.attr.bg_buy_btn));
                if (DoubleUtils.parseDouble(coinInfoBean.getPriceRaiseRate()) >= 0) {
                    viewHold.zhang_rate_tv1.setText("+" + ratio + "%");
                } else {
                    viewHold.zhang_rate_tv1.setText(ratio + "%");
                }
            }
        } else if (type == 2) {//新币榜
            if (coinInfoBean.getPriceRaiseRate() != null && DoubleUtils.parseDouble(coinInfoBean.getPriceRaiseRate()) >= 0) {
                viewHold.zhang_price_tv1_1.setTextColor(context.getResources().getColor(R.color.ff8881a6));
                if ("USDT".equalsIgnoreCase(coinInfoBean.getCnyName())) {
                    viewHold.zhang_price_tv1_1.setText(context.getString(R.string.rmb) + coinInfoBean.getCurrencyPrize());
                } else {
                    viewHold.zhang_price_tv1_1.setText(coinInfoBean.getCurrencySymbol() + coinInfoBean.getCurrencyPrize());
                }
                viewHold.zhang_rate_tv1.setVisibility(View.VISIBLE);
                viewHold.zhang_rate_tv2.setVisibility(View.INVISIBLE);
                viewHold.zhang_rate_tv22.setVisibility(View.INVISIBLE);
                viewHold.zhang_rate_tv1.setBackground(ThemeUtil.INSTANCE.getThemeDrawable(context,R.attr.bg_buy_btn));
                if (DoubleUtils.parseDouble(coinInfoBean.getPriceRaiseRate()) >= 0) {
                    viewHold.zhang_rate_tv1.setText("+" + ratio + "%");
                } else {
                    viewHold.zhang_rate_tv1.setText(ratio + "%");
                }
            } else {
                viewHold.zhang_price_tv1_1.setTextColor(context.getResources().getColor(R.color.ff8881a6));
                if ("USDT".equalsIgnoreCase(coinInfoBean.getCnyName())) {
                    viewHold.zhang_price_tv1_1.setText("¥" + coinInfoBean.getCurrencyPrize());
                } else {
                    viewHold.zhang_price_tv1_1.setText(coinInfoBean.getCurrencySymbol() + coinInfoBean.getCurrencyPrize());
                }
                viewHold.zhang_price_tv1_1.setText(coinInfoBean.getCurrencySymbol() + coinInfoBean.getCurrencyPrize());
                viewHold.zhang_rate_tv2.setVisibility(View.INVISIBLE);
                viewHold.zhang_rate_tv22.setVisibility(View.INVISIBLE);
                viewHold.zhang_rate_tv1.setVisibility(View.VISIBLE);
                viewHold.zhang_rate_tv1.setBackground(ThemeUtil.INSTANCE.getThemeDrawable(context,R.attr.bg_sell_btn));
                viewHold.zhang_rate_tv1.setText(coinInfoBean.getLegalMoney() + "");
                viewHold.zhang_rate_tv1.setText(ratio + "%");
            }
            viewHold.zhang_price_tv1_1.setTextColor(context.getResources().getColor(R.color.ff9F9AB6));
            if(TextUtils.isEmpty(coinInfoBean.getLatestDealPrice())){
                viewHold.zhang_price_tv1_1.setText("≈"+ PricingMethodUtil.getPricingUnit()+"--");
            }else{
                viewHold.zhang_price_tv1_1.setText("≈"+ PricingMethodUtil.getPricingUnit()+PricingMethodUtil.getResultByExchangeRate(
                        coinInfoBean.getLatestDealPrice(),coinInfoBean.getCnyName()
                ));
            }
        } else {//成交额榜
            if(TextUtils.isEmpty(coinInfoBean.getLatestDealPrice())){
                viewHold.zhang_price_tv1_1.setText("≈"+ PricingMethodUtil.getPricingUnit()+"--");
            }else{
                viewHold.zhang_price_tv1_1.setText("≈"+ PricingMethodUtil.getPricingUnit()+PricingMethodUtil.getResultByExchangeRate(
                        coinInfoBean.getLatestDealPrice(),coinInfoBean.getCnyName()
                ));
            }
            viewHold.zhang_rate_tv1.setVisibility(View.GONE);
            viewHold.zhang_rate_tv2.setVisibility(View.VISIBLE);
            viewHold.zhang_rate_tv22.setVisibility(View.VISIBLE);
            //成交额数据根据服务器单位来取数据
            if(coinInfoBean.getLegalMoneyCny()==null){
                viewHold.zhang_rate_tv2.setText("--");
            }else{
                //因为使用的getLegalMoneyCny为cny的值，所以srcType直接使用CNY
                viewHold.zhang_rate_tv2.setText(PricingMethodUtil.getLargePrice(PricingMethodUtil.getResultByExchangeRate(coinInfoBean.getLegalMoneyCny(),"CNY")));
            }
        }
        return convertView;
    }

    static class ViewHold {
        public TextView zhang_name_tv1;
        public TextView zhang_name_tv1_1;
        public TextView zhang_price_tv1;
        public TextView zhang_price_tv1_1;
        public TextView zhang_rate_tv1;
        public TextView zhang_rate_tv2;
        public LinearLayout zhang_rate_tv22;
    }
}
