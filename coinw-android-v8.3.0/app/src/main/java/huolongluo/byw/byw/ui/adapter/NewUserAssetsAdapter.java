package huolongluo.byw.byw.ui.adapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.android.legend.ui.transfer.AccountTransferActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.coindetail.CoinDetailActivity;
import huolongluo.byw.byw.ui.fragment.maintab03.bean.AssetCoinsBean;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.home.activity.NewsWebviewActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.FastClickUtils;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.DoubleUtils;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
/**
 * Created by Administrator on 2019/1/16 0016.
 * 资产账户适配器
 */
public class NewUserAssetsAdapter extends RecyclerView.Adapter<NewUserAssetsAdapter.ViewHole> {
    private List<AssetCoinsBean> mAssetcoinsList = new ArrayList<>();
    private Context context;
    boolean isHide = false;
    private DecimalFormat df = new DecimalFormat(Constant.ASSETS_DEFAULT_AMOUNT);

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public boolean hasData() {
        return getItemCount() > 0;
    }

    public NewUserAssetsAdapter(Context context, List<AssetCoinsBean> mAssetcoinsList) {
        this.mAssetcoinsList.addAll(mAssetcoinsList);
        this.context = context;
    }

    public AssetCoinsBean getItem(int position) {
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

    public void replaceAll(List<AssetCoinsBean> items) {
        mAssetcoinsList.clear();
        mAssetcoinsList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHole holder, int position) {
        AssetCoinsBean item = mAssetcoinsList.get(position);
        holder.tv_name.setText(item.getShortName());
        if (isHide) {
            holder.tv_unuseable.setText("****");
            holder.tv_useable.setText("****");
            holder.tv_total.setText("****");
        } else {
            holder.tv_unuseable.setText(df.format(DoubleUtils.parseDouble(item.getFrozen())));
            holder.tv_useable.setText(df.format(DoubleUtils.parseDouble(item.getTotal())));
            if (item.getZhehe() == null) {
                holder.tv_total.setText(PricingMethodUtil.getPricingUnit() + Constant.ASSETS_DEFAULT_AMOUNT);
            } else {
                holder.tv_total.setText(PricingMethodUtil.getPricingUnit() +PricingMethodUtil.getLargePrice(
                        PricingMethodUtil.getResultByExchangeRate(item.getZhehe(),"CNY", Constant.ASSETS_AMOUNT_PRECISION),Constant.ASSETS_AMOUNT_PRECISION));
            }
        }
        String coinName = item.getShortName();
        String finalCoinName = coinName;
        holder.tvTransfer.setVisibility(View.VISIBLE);
        holder.tvTransfer.setText(context.getString(R.string.d33));
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
                if (context != null) {
                    AccountTransferActivity.Companion.launch(context, null,null,item.getId(),null,
                            true, item.getShortName());
                }
            }
        });

        if (android.text.TextUtils.equals(item.getCnytIshide(), "0")) {
            holder.tvRecharge.setVisibility(View.GONE);
            holder.tvWithdraw.setVisibility(View.GONE);
        } else {
            holder.tvRecharge.setVisibility(View.VISIBLE);
            holder.tvRecharge.setText(context.getString(R.string.recharge1));
            holder.tvWithdraw.setVisibility(View.VISIBLE);
            holder.tvWithdraw.setText(context.getString(R.string.tixian));
            holder.tvRecharge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new Event.clickAssets(1, position, finalCoinName));
                }
            });
            holder.tvWithdraw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(FastClickUtils.isFastClick(1000)){
                        return;
                    }
                    EventBus.getDefault().post(new Event.clickAssets(2, position, finalCoinName));
                }
            });
        }
        holder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CoinDetailActivity.class);
                Log.d("点击了", "第" + position + "项");
                Bundle bundle = new Bundle();
                bundle.putParcelable("coinBean", item);
                bundle.putInt("areaType", item.getAreaType());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
         //处理活期理财
        holder.bybView.setVisibility(item.getBiyingbao() ? View.VISIBLE : View.GONE);
        //ETF
        holder.etfView.setVisibility(item.getAreaType() == 4 ? View.VISIBLE : View.GONE);
        if (item.getBiyingbao()) {
            addEvent(holder.bybView, item.getBiyingbaoUrl());
        }
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
        TextView tvRecharge;
        TextView tvWithdraw;
        CardView ll_main;
        View bybView, etfView;
        TextView tvTransfer;

        public ViewHole(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_total = itemView.findViewById(R.id.tvValue1);
            this.tv_useable = itemView.findViewById(R.id.tvValue2);
            this.tv_unuseable = itemView.findViewById(R.id.tvValue3);
            this.tvTransfer = itemView.findViewById(R.id.tvOperate3);
            this.tvRecharge = itemView.findViewById(R.id.tvOperate1);
            this.tvWithdraw = itemView.findViewById(R.id.tvOperate2);
            this.ll_main = itemView.findViewById(R.id.ll_main);
            this.bybView = itemView.findViewById(R.id.tv_byb);
            this.etfView = itemView.findViewById(R.id.tv_etf);
        }
    }
}
