package huolongluo.byw.byw.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.legend.model.enumerate.transfer.TransferAccount;
import com.android.legend.model.finance.BbFinanceAccountListBean;
import com.android.legend.ui.transfer.AccountTransferActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import huolongluo.byw.R;
import huolongluo.byw.byw.ui.activity.coindetail.BbCoinDetailActivity;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.home.activity.NewsWebviewActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.FastClickUtils;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.DoubleUtils;
/**
 * 币币资产列表适配器
 */
public class BbFinanceAdapter extends RecyclerView.Adapter<BbFinanceAdapter.ViewHole> {
    private List<BbFinanceAccountListBean> mAssetcoinsList = new ArrayList<>();
    private Context context;
    boolean isHide = false;
    private DecimalFormat df = new DecimalFormat(Constant.ASSETS_DEFAULT_AMOUNT);

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public boolean hasData() {
        return getItemCount() > 0;
    }

    public BbFinanceAdapter(Context context, List<BbFinanceAccountListBean> mAssetcoinsList) {
        this.mAssetcoinsList.addAll(mAssetcoinsList);
        this.context = context;
    }

    public BbFinanceAccountListBean getItem(int position) {
        if (mAssetcoinsList.size() > position) {
            return mAssetcoinsList.get(position);
        }
        return null;
    }

    @Override
    public ViewHole onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_assets, parent, false);
        ViewHole viewHole = new ViewHole(view);
        return viewHole;
    }

    public void replaceAll(List<BbFinanceAccountListBean> items) {
        mAssetcoinsList.clear();
        mAssetcoinsList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHole holder, int position) {
        BbFinanceAccountListBean item = mAssetcoinsList.get(position);
        holder.tv_name.setText(item.getCoin().getCoinName());
        if (isHide) {
            holder.tv_unuseable.setText("****");
            holder.tv_useable.setText("****");
            holder.tv_total.setText("****");
        } else {
            holder.tv_unuseable.setText(df.format(DoubleUtils.parseDouble(item.getHoldBalance())));
            holder.tv_useable.setText(df.format(DoubleUtils.parseDouble(item.getAvailableBalance())));
            if (item.getBalanceAmount() == null) {
                holder.tv_total.setText(PricingMethodUtil.getPricingUnit() + Constant.ASSETS_DEFAULT_AMOUNT);
            } else {
                holder.tv_total.setText(PricingMethodUtil.getPricingUnit() +PricingMethodUtil.getLargePrice(
                        PricingMethodUtil.getResultByExchangeRate(item.getBalanceAmount(),"CNY", Constant.ASSETS_AMOUNT_PRECISION),Constant.ASSETS_AMOUNT_PRECISION));
            }
        }
        holder.tvTransfer.setText(context.getString(R.string.d33));
        if (item.getSupportTransfer()) {
            holder.tvTransfer.setVisibility(View.VISIBLE);
        } else {
            holder.tvTransfer.setVisibility(View.GONE);
        }
        holder.tvTrade.setText(context.getString(R.string.trade));
        if(item.getPairs()==null||item.getPairs().size()==0){
            holder.tvTrade.setVisibility(View.GONE);
        }else{
            holder.tvTrade.setVisibility(View.VISIBLE);
        }
        holder.tvTransfer.setOnClickListener(new View.OnClickListener() {//划转
            @Override
            public void onClick(View v) {
                if (FastClickUtils.isFastClick(1000)) {
                    return;
                }
                if (context == null) {
                    return;
                }
                //JIRA:COIN-1721
                //业务控制条件：是否禁止提币 0.可以提币， 1.禁止提币
                if (!AppHelper.isNoWithdrawal()) {//该用户已被禁止提币
                    DialogUtils.getInstance().showOneButtonDialog(context, context.getString(R.string.no_transfer), context.getString(R.string.as16));
                    return;
                }
                AccountTransferActivity.Companion.launch(context, TransferAccount.WEALTH.getValue(),TransferAccount.SPOT.getValue(),item.getCoin().getCoinId(),
                        null,true, item.getCoin().getCoinName());
            }
        });
        holder.tvTrade.setOnClickListener(new View.OnClickListener() {//交易
            @Override
            public void onClick(View v) {
                if (FastClickUtils.isFastClick(1000)) {
                    return;
                }
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("tradeId", item.getPairs().get(0).getPairId());
                intent.putExtra("coinName",item.getPairs().get(0).getBaseName());
                intent.putExtra("from", "CoinDetailActivity");
                intent.putExtra("areaType", item.getCoin().getEtf()? 4 : 1);
                context.startActivity(intent);
            }
        });
        holder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.equals(AppConstants.COMMON.CNYT,item.getCoin().getCoinName())){
                    return;
                }
                Intent intent = new Intent(context, BbCoinDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("coinBean", item);
                bundle.putInt("areaType", 1);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        //ETF
        holder.etfView.setVisibility(item.getCoin().getEtf() ? View.VISIBLE : View.GONE);
        holder.tv_byb.setVisibility(View.GONE);
    }

    private void addEvent(View view, String url) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(url)) {
                    //获得的URL数据为空
                    Logger.getInstance().debug("NewUserAssetsAdapter", "url: " + url);
                    return;
                }
                try {
                    gotoTarget(url);
                } catch (Throwable t) {
                }
            }

            private void gotoTarget(String url) {
                Intent intent = new Intent(context, NewsWebviewActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("token", UserInfoManager.getToken());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAssetcoinsList.size();
    }

    public static class ViewHole extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_total;
        TextView tv_useable;
        TextView tv_unuseable;
        CardView ll_main;
        View  etfView,tv_byb;
        TextView tvTransfer;
        TextView tvTrade;

        public ViewHole(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_total = itemView.findViewById(R.id.tvValue1);
            this.tv_useable = itemView.findViewById(R.id.tvValue2);
            this.tv_unuseable = itemView.findViewById(R.id.tvValue3);
            this.ll_main = itemView.findViewById(R.id.ll_main);
            this.etfView = itemView.findViewById(R.id.tv_etf);
            this.tv_byb = itemView.findViewById(R.id.tv_byb);
            this.tvTrade = itemView.findViewById(R.id.tvOperate1);
            this.tvTransfer = itemView.findViewById(R.id.tvOperate3);
        }
    }
}
