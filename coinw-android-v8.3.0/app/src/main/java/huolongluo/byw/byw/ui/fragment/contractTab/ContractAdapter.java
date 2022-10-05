package huolongluo.byw.byw.ui.fragment.contractTab;

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
import com.android.legend.ui.transfer.AccountTransferActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils;

import org.greenrobot.eventbus.EventBus;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.home.activity.NewsWebviewActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.FastClickUtils;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.bywx.helper.AppHelper;
import huolongluo.bywx.utils.DoubleUtils;

/**
 * Created by Administrator on 2019/1/16 0016.
 */
public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.ViewHole> {
    private List<ContractListEntity.DataBean.DetailBean> mAssetcoinsList = new ArrayList<>();
    private Context context;
    boolean isHide = false;
    private DecimalFormat df = new DecimalFormat(Constant.ASSETS_DEFAULT_AMOUNT);

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public boolean hasData() {
        return getItemCount() > 0;
    }

    public ContractAdapter(Context context, List<ContractListEntity.DataBean.DetailBean> mAssetcoinsList) {
        this.mAssetcoinsList.addAll(mAssetcoinsList);
        this.context = context;
    }

    public ContractListEntity.DataBean.DetailBean getItem(int position) {
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

    public void replaceAll(List<ContractListEntity.DataBean.DetailBean> items) {
        mAssetcoinsList.clear();
        mAssetcoinsList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHole holder, int position) {
        ContractListEntity.DataBean.DetailBean item = mAssetcoinsList.get(position);
        holder.tv_name.setText(item.getCoinName());
        if (isHide) {
            holder.tv_unuseable.setText("****");
            holder.tv_useable.setText("****");
            holder.tv_total.setText("****");
        } else {
            holder.tv_unuseable.setText(df.format(DoubleUtils.parseDouble(item.getTotalIm())));
            holder.tv_useable.setText(df.format(DoubleUtils.parseDouble(item.getAvailableBalance())));
            if (item.getCashVol() == null) {
                holder.tv_total.setText(Constant.ASSETS_DEFAULT_AMOUNT);
            } else {
                holder.tv_total.setText(df.format(DoubleUtils.parseDouble(item.getTotalVol())));
            }
        }
        holder.tvTitle1.setText(context.getString(R.string.hy_account_balance));
        holder.tvTitle2.setText(context.getString(R.string.hy_free_margin));
        holder.tvTitle3.setText(context.getString(R.string.hy_position_margin));
        holder.tvTransfer.setText(context.getString(R.string.d33));
        holder.tvTransfer.setVisibility(View.VISIBLE);
        holder.tvTransfer.setOnClickListener(v -> {
            if(CoinwHyUtils.checkIsStopService(context))
            {
                return;
            }
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
                AccountTransferActivity.Companion.launch(context, TransferAccount.WEALTH.getValue(),TransferAccount.CONTRACT.getValue(),item.getCoinId(),null,
                        true,item.getCoinName());
            }
        });
        holder.tvTrade.setText(context.getString(R.string.trade));
        holder.tvTrade.setVisibility(View.VISIBLE);
        holder.tvTrade.setOnClickListener(v -> gotoTrade(item.getCoinName()));
        holder.ll_main.setOnClickListener(view -> {
            Intent intent = new Intent(context, ContractCoinDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("coinBean", item);
            intent.putExtras(bundle);
            context.startActivity(intent);
        });
        holder.bybView.setVisibility(View.GONE);
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
        View bybView;
        TextView tvTransfer;
        TextView tvTrade;
        TextView tvTitle1;
        TextView tvTitle2;
        TextView tvTitle3;

        public ViewHole(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_total = itemView.findViewById(R.id.tvValue1);
            this.tv_useable = itemView.findViewById(R.id.tvValue2);
            this.tv_unuseable = itemView.findViewById(R.id.tvValue3);
            this.ll_main = itemView.findViewById(R.id.ll_main);
            this.bybView = itemView.findViewById(R.id.tv_byb);
            this.tvTrade = itemView.findViewById(R.id.tvOperate1);
            this.tvTransfer = itemView.findViewById(R.id.tvOperate2);
            this.tvTitle1 = itemView.findViewById(R.id.tv1);
            this.tvTitle2 = itemView.findViewById(R.id.tv2);
            this.tvTitle3 = itemView.findViewById(R.id.tv3);
        }
    }

    private void gotoTrade(String coinName) {
        //TODO 交易
        try {
            if (TextUtils.isEmpty(coinName)) {
                return;
            }
            Intent intent = new Intent(BaseApp.getSelf(), MainActivity.class);
            intent.putExtra(AppConstants.COMMON.KEY_ACTION_FROM, AppConstants.COMMON.FLAG_ACTION_CONTRACT_INTERFACE);
            intent.putExtra(AppConstants.COMMON.KEY_ACTION_PARAM, AppConstants.COMMON.ACTION_NATIVE_INTERFACE_TRADE);
            intent.putExtra("type", 1);//代表为合约交易
            intent.putExtra("coinName", coinName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            BaseApp.getSelf().startActivity(intent);
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }
}
