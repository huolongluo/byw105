package huolongluo.byw.byw.ui.fragment.maintab01;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.android.coinw.biz.event.BizEvent;
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
import huolongluo.byw.util.pricing.PricingMethodUtil;
/**
 * Created by LS on 2018/7/4.
 */

public class MarketItemAdapter extends BaseAdapter {
    private Context context;
    private List<MarketListBean> mData;
    DecimalFormat df = new DecimalFormat("0.00");
    private Animation riseAnimation;

    private String mType;
    boolean scroll;

    public MarketItemAdapter(Context context, List<MarketListBean> list) {
        this.context = context;
        this.mData = list;
        riseAnimation = AnimationUtils.loadAnimation(context, R.anim.rise_anim);
        riseAnimation.setInterpolator(new DecelerateInterpolator());
    }


    public void setmType(String mType) {
        this.mType = mType;
    }


    public void setmData(List<MarketListBean> list) {
        this.mData = list;
        Log.e("onSuccess ", "setmData=   " + mData);
    }


    public void setScrollStatus(boolean scroll) {
        this.scroll = scroll;
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
            holder.animal_view = view.findViewById(R.id.animal_view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //根据 currentItem 记录的点击位置来设置"对应Item"的可见性（在list依次加载列表数据时，每加载一个时都看一下是不是需改变可见性的那一条）
        holder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarketListBean bean = mData.get(i);

                if (MarketNewFragment.isSearching) {
                    SPUtils.save(BaseApp.getSelf().getApplicationContext(), bean.getCoinName() + "/" + bean.getCnyName() + "/" + Integer.valueOf(bean.getId()));
                }
                MainActivity.self.gotoTrade(bean);
                HotMoneyPresenter.collectHotData(null, mData.get(i).getId() + "");
                EventBus.getDefault().post(new BizEvent.SelectCurrentPage(0));
            }
        });
        MarketListBean listBean = mData.get(i);

        if (listBean != null) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.mipmap.rmblogo);
            requestOptions.error(R.mipmap.rmblogo);
            requestOptions.centerCrop();

            holder.tvName.setText(listBean.getCoinName());
            holder.tv_name1.setText("/" + listBean.getCnyName());
            try {
                holder.tvVol.setText(PricingMethodUtil.getLargePrice(listBean.getOneDayTotal().toString(),3));
            }catch (Throwable t){
                t.printStackTrace();
            }

            if(listBean.getLatestDealPrice()!=null){
                holder.tvMoney.setText(listBean.getLatestDealPrice() + "");
                holder.tvMoney1.setText( "≈"+PricingMethodUtil.getPricingUnit() + PricingMethodUtil.getResultByExchangeRate(
                        listBean.getLatestDealPrice(),listBean.getCnyName()
                ));
            }else{
                holder.tvMoney.setText( "--");
                holder.tvMoney1.setText( "≈"+PricingMethodUtil.getPricingUnit() + "--");
            }

//            if (!scroll) {
//                if (Double.parseDouble(listBean.getLatestDealPrice()) > listBean.getLastSocketPrice()) {
//                    holder.animal_view.setBackgroundResource(R.drawable.rise_bg);
//                    Animation animation = holder.animal_view.getAnimation();
//                    if (animation == null) {
//                        holder.animal_view.setAnimation(riseAnimation);
//                    } else {
//                        holder.animal_view.getAnimation().startNow();
//                    }
//                } else if (Double.parseDouble(listBean.getLatestDealPrice()) < listBean.getLastSocketPrice()) {
//                    holder.animal_view.setBackgroundResource(R.drawable.drop_bg);
//                    Animation animation = holder.animal_view.getAnimation();
//                    if (animation == null) {
//                        holder.animal_view.setAnimation(riseAnimation);
//                    } else {
//                        holder.animal_view.getAnimation().startNow();
//                    }
//                }
//            }
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

    public boolean isNeedData = true;

    static class ViewHolder {
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
