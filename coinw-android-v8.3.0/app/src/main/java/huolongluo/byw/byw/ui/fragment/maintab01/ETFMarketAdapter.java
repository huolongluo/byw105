package huolongluo.byw.byw.ui.fragment.maintab01;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.coinw.biz.event.BizEvent;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.present.HotMoneyPresenter;
import huolongluo.byw.util.MathHelper;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.noru.NorUtils;
public class ETFMarketAdapter extends RecyclerView.Adapter<ETFMarketAdapter.ViewHolder> {
    private Context context;
    private List<MarketListBean> dataList;
    private DecimalFormat df = new DecimalFormat("0.00");
    private Animation riseAnimation;
    private boolean scroll = false;

    public ETFMarketAdapter(Context context, List<MarketListBean> list) {
        this.context = context;
        this.dataList = list;
        riseAnimation = AnimationUtils.loadAnimation(context, R.anim.rise_anim);
        riseAnimation.setInterpolator(new DecelerateInterpolator());
    }

    @Override
    public int getItemCount() {
        return this.dataList == null ? 0 : this.dataList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_market, parent, false);
        ViewHolder ViewHolder = new ViewHolder(view);
        return ViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //根据 currentItem 记录的点击位置来设置"对应Item"的可见性（在list依次加载列表数据时，每加载一个时都看一下是不是需改变可见性的那一条）
        final MarketListBean data = dataList.get(position);
        if (data == null) {
            //TODO 处理异常情况
            return;
        }
        holder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MarketNewFragment.isSearching) {
                    SPUtils.save(BaseApp.getSelf().getApplicationContext(), data.getCoinName() + "/" + data.getCnyName() + "/" + Integer.valueOf(data.getId()));
                }
                if(MainActivity.self==null){
                    return;
                }
                MainActivity.self.gotoTrade(data);
                HotMoneyPresenter.collectHotData(null, data.getId() + "");
                EventBus.getDefault().post(new BizEvent.SelectCurrentPage(0));
            }
        });
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.mipmap.rmblogo);
        requestOptions.error(R.mipmap.rmblogo);
        requestOptions.centerCrop();
        Glide.with(context).load(data.getLogo()).apply(requestOptions).into(holder.ivLogo);
        holder.tvName.setText(data.getCoinName());
        holder.tv_name1.setText("/" + data.getCnyName());
        holder.tvVol.setText(df.format(data.getOneDayTotal() / 1000) + "k");
        if ("USDT".equalsIgnoreCase(data.getCnyName())) {
            holder.tvMoney.setText(NorUtils.NumberFormatNo(8).format(Double.parseDouble(data.getLatestDealPrice())) + "");
            holder.tvMoney1.setText("¥" + NorUtils.NumberFormatNo(8).format(Double.parseDouble(data.getCurrencyPrize())));
        } else {
            holder.tvMoney.setText(data.getLatestDealPrice() + "");
            holder.tvMoney1.setText(data.getCurrencySymbol() + data.getCurrencyPrize());
        }
        if (!scroll) {
            if (Double.parseDouble(data.getLatestDealPrice()) > data.getLastSocketPrice()) {
                holder.animal_view.setBackgroundResource(R.drawable.rise_bg);
                Animation animation = holder.animal_view.getAnimation();
                if (animation == null) {
                    holder.animal_view.setAnimation(riseAnimation);
                } else {
                    holder.animal_view.getAnimation().startNow();
                }
            } else if (Double.parseDouble(data.getLatestDealPrice()) < data.getLastSocketPrice()) {
                holder.animal_view.setBackgroundResource(R.drawable.drop_bg);
                Animation animation = holder.animal_view.getAnimation();
                if (animation == null) {
                    holder.animal_view.setAnimation(riseAnimation);
                } else {
                    holder.animal_view.getAnimation().startNow();
                }
            }
        }
        double ratiod= MathHelper.mul(100.0, data.getPriceRaiseRate());
        String ratio = NorUtils.NumberFormat(2).format(ratiod);
        if (data.getPriceRaiseRate() >= 0) {
            if (data.getPriceRaiseRate() == 0) {
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

    public void setScrollStatus(boolean scroll) {
        this.scroll = scroll;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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

        public ViewHolder(View view) {
            super(view);
            this.ivLogo = view.findViewById(R.id.iv_logo);
            this.coinStatus_iv1 = view.findViewById(R.id.coinStatus_iv1);
            this.tvName = view.findViewById(R.id.tv_name);
            this.tv_name1 = view.findViewById(R.id.tv_name1);
            this.tvVol = view.findViewById(R.id.tv_vol);
            this.tvMoney = view.findViewById(R.id.tv_money);
            this.tvMoney1 = view.findViewById(R.id.tv_money1);
            this.tvRate = view.findViewById(R.id.tv_rate);
            this.ll_main = view.findViewById(R.id.ll_main);
            this.animal_view = view.findViewById(R.id.animal_view);
        }
    }

    public String getTime() {
        long time = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳
        String str = String.valueOf(time);
        return str;
    }
}
