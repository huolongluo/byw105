package huolongluo.byw.byw.ui.fragment.maintab01;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.text.DecimalFormat;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.util.MathHelper;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.pricing.PricingMethodUtil;
//etf交易页侧滑栏
public class ETFMarketItemSlideAdapter extends BaseAdapter {
    private Context context;
    private List<MarketListBean> mData;
    DecimalFormat df = new DecimalFormat("0.00");
    private Animation riseAnimation;
    boolean scroll;

    public ETFMarketItemSlideAdapter(Context context, List<MarketListBean> list) {
        this.context = context;
        this.mData = list;
        riseAnimation = AnimationUtils.loadAnimation(context, R.anim.rise_anim);
        riseAnimation.setInterpolator(new DecelerateInterpolator());
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
            view = LayoutInflater.from(context).inflate(R.layout.market_slide_item, null);
            holder = new ViewHolder();
            holder.ivLogo = view.findViewById(R.id.iv_logo);
            holder.coinStatus_iv1 = view.findViewById(R.id.coinStatus_iv1);
            holder.tvName = view.findViewById(R.id.tv_name);
            holder.tv_name1 = view.findViewById(R.id.tv_name1);
            holder.tvVol = view.findViewById(R.id.tv_vol);
            holder.tvMoney = view.findViewById(R.id.tv_money);
            holder.tvMoney1 = view.findViewById(R.id.tv_money1);
            holder.tvRate = view.findViewById(R.id.tv_rate);
            holder.ll_main = view.findViewById(R.id.ll_main);
            holder.animal_view = view.findViewById(R.id.animal_view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //根据 currentItem 记录的点击位置来设置"对应Item"的可见性（在list依次加载列表数据时，每加载一个时都看一下是不是需改变可见性的那一条）
//        holder.ll_main.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MarketListBean bean = mData.get(i);
//                if (MarketNewFragment.isSearching) {
//                    SPUtils.save(BaseApp.getSelf().getApplicationContext(), bean.getCoinName() + "/" + bean.getCnyName() + "/" + Integer.valueOf(bean.getId()));
//                }
//                MainActivity.self.gotoETFTrade(bean);
//                HotMoneyPresenter.collectHotData(null, mData.get(i).getId() + "");
//                EventBus.getDefault().post(new BizEvent.SelectCurrentPage(0));
//            }
//        });
        MarketListBean listBean = mData.get(i);
        if (listBean != null) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.mipmap.rmblogo);
            requestOptions.error(R.mipmap.rmblogo);
            requestOptions.centerCrop();
            Glide.with(context).load(listBean.getLogo()).apply(requestOptions).into(holder.ivLogo);
            holder.tvName.setText(listBean.getCoinName());
            holder.tv_name1.setText("/" + listBean.getCnyName());
            holder.tvVol.setText(df.format(listBean.getOneDayTotal() / 1000) + "k");
            if ("USDT".equalsIgnoreCase(listBean.getCnyName())) {
                holder.tvMoney.setText(NorUtils.NumberFormatNo(8).format(Double.parseDouble(listBean.getLatestDealPrice())) + "");
            } else {
                holder.tvMoney.setText(listBean.getLatestDealPrice() + "");
            }
            holder.tvMoney1.setText("≈"+PricingMethodUtil.getPricingUnit() + PricingMethodUtil.getResultByExchangeRate(
                    listBean.getLatestDealPrice(),listBean.getCnyName(),4
            ));
            if (!scroll) {
                if (Double.parseDouble(listBean.getLatestDealPrice()) > listBean.getLastSocketPrice()) {
                    holder.animal_view.setBackgroundResource(R.drawable.rise_bg);
                    Animation animation = holder.animal_view.getAnimation();
                    if (animation == null) {
                        holder.animal_view.setAnimation(riseAnimation);
                    } else {
                        holder.animal_view.getAnimation().startNow();
                    }
                } else if (Double.parseDouble(listBean.getLatestDealPrice()) < listBean.getLastSocketPrice()) {
                    holder.animal_view.setBackgroundResource(R.drawable.drop_bg);
                    Animation animation = holder.animal_view.getAnimation();
                    if (animation == null) {
                        holder.animal_view.setAnimation(riseAnimation);
                    } else {
                        holder.animal_view.getAnimation().startNow();
                    }
                }
            }
            double ratiod= MathHelper.mul(100.0, listBean.getPriceRaiseRate());
            String ratio = NorUtils.NumberFormat(2).format(ratiod);
            if (listBean.getPriceRaiseRate() >= 0) {
                if (listBean.getPriceRaiseRate() == 0) {
                    holder.tvRate.setText(ratio + "%");
                } else {
                    holder.tvRate.setText("+" + ratio + "%");
                }
                holder.coinStatus_iv1.setImageResource(R.mipmap.zhang);
                holder.tvMoney.setTextColor(context.getResources().getColor(R.color.ffff5763));
                holder.tvRate.setBackground(context.getResources().getDrawable(R.drawable.dingdan_bg));
            } else {
                holder.tvRate.setText(ratio + "%");
                holder.tvMoney.setTextColor(context.getResources().getColor(R.color.ff44c09c));
                holder.tvRate.setBackground(context.getResources().getDrawable(R.drawable.market_item_bg0));
                holder.coinStatus_iv1.setImageResource(R.mipmap.die);
            }
        }
        holder.ll_main.setTag(i);
        return view;
    }

    static class ViewHolder {
        private ImageView ivLogo;
        private ImageView coinStatus_iv1;
        private TextView tvName;
        private TextView tvVol;
        private TextView tvMoney;
        private TextView tvMoney1;
        private TextView tvRate;
        private TextView tv_name1;
        private LinearLayout ll_main;
        private View animal_view;
    }

    public String getTime() {
        long time = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳
        String str = String.valueOf(time);
        return str;
    }
}
