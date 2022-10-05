package huolongluo.byw.byw.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.android.coinw.biz.trade.helper.TradeDataHelper;
import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.bean.MarketListBean;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.MathHelper;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.bywx.utils.DoubleUtils;
/**
 * Created by LS on 2018/7/9.
 */
public class SelectCoinDialogAdapter extends BaseAdapter {
    private static final String TAG = "SelectCoinDialogAdapter";
    private Context context;
    private List<MarketListBean> mData = new ArrayList<>();

    public SelectCoinDialogAdapter(Context context, List<MarketListBean> mData) {
        this.context = context;
        this.mData.clear();
        Log.e("bbbbaabbbbb", "ogAdapter  mData=  " + mData);
        this.mData.addAll(mData);
    }

    @Override
    public int getCount() {
//        Log.d("币种条数", String.valueOf(mData.size()));
        return mData.size();
    }

    public void setmData(List<MarketListBean> listBeans) {
        mData.clear();
        mData.addAll(listBeans);
    }

    @Override
    public Object getItem(int i) {
        if (i < 0 || mData == null || mData.isEmpty()) {
            return null;
        }
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_select_coin_dialog_item, null);
            holder = new ViewHolder();
            // holder.ivLogo = view.findViewById(R.id.iv_logo);
            holder.tvName = view.findViewById(R.id.tv_name);
            holder.tvName1 = view.findViewById(R.id.tv_name1);
            holder.tvMoney = view.findViewById(R.id.tv_money);
            holder.tvRate = view.findViewById(R.id.tv_rate);
            holder.mainLayout = view.findViewById(R.id.ll_main);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        MarketListBean coinBean = mData.get(i);
        double ratiod= MathHelper.mul(100.0, coinBean.getPriceRaiseRate());
        String ratio = NorUtils.NumberFormat(2).format(ratiod);
        if (coinBean != null) {
            //判断是否为当前选择币种
            if (coinBean.getId() > 0 && coinBean.getId() == TradeDataHelper.getInstance().getId()) {
                holder.mainLayout.setBackgroundResource(R.color.color_f9f9fc);
            } else {
                holder.mainLayout.setBackgroundResource(R.color.white);
            }
//            Log.d("coinBeans>>>",coinBean.getExchangeCode());
//            Log.d("coinBeans>>>",coinBean.getLatestDealPrice());
//            Log.d("coinBeans>>>",coinBean.getLogo());
            // Glide.with(context).load(coinBean.getLogo()).error(R.mipmap.rmblogo).centerCrop().into((ImageView) holder.ivLogo);
            holder.tvName.setText(coinBean.getCoinName());
            holder.tvName1.setText("/" + coinBean.getCnyName());
            Logger.getInstance().debug(TAG,"币币行情的LatestDealPrice："+coinBean.getLatestDealPrice());
            if ("USDT".equalsIgnoreCase(coinBean.getCnyName())) {
                holder.tvMoney.setText(NorUtils.NumberFormatNo(8).format(DoubleUtils.parseDouble(coinBean.getLatestDealPrice())) + "");
            } else {
                holder.tvMoney.setText(coinBean.getLatestDealPrice() + "");
            }
            if (coinBean.getPriceRaiseRate() < 0) {
                holder.tvRate.setText(ratio + "%");
            } else {
                holder.tvRate.setText("+" + ratio + "%");
            }
            if (Double.valueOf(coinBean.getPriceRaiseRate()) < 0) {
                holder.tvMoney.setTextColor(context.getResources().getColor(R.color.ff44c09c));
                holder.tvRate.setBackground(context.getResources().getDrawable(R.drawable.market_item_bg0));
            } else {
                holder.tvMoney.setTextColor(context.getResources().getColor(R.color.ffff5763));
                holder.tvRate.setBackground(context.getResources().getDrawable(R.drawable.dingdan_bg));
            }
        }
        return view;
    }

    private static class ViewHolder {
        private ImageView ivLogo;
        private TextView tvName;
        private TextView tvName1;
        private TextView tvVol;
        private TextView tvMoney;
        private TextView tvMoney1;
        private TextView tvRate;
        private LinearLayout mainLayout;
    }
}
