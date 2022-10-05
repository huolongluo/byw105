package huolongluo.byw.byw.ui.adapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import huolongluo.byw.reform.c2c.oct.bean.OtcUserInfoBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.FastClickUtils;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.bywx.helper.AppHelper;
/**
 * Created by Administrator on 2019/1/16 0016.
 */
public class NewUserOtcAssetsAdapter extends RecyclerView.Adapter<NewUserOtcAssetsAdapter.ViewHole> {
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

    public NewUserOtcAssetsAdapter(Context context, List<AssetCoinsBean> mAssetcoinsList) {
        this.mAssetcoinsList.addAll(mAssetcoinsList);
        this.context = context;
    }

    public AssetCoinsBean getItem(int position) {
        if (position < 0 || mAssetcoinsList == null || mAssetcoinsList.isEmpty()) {
            return null;
        }
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
            holder.tv_unuseable.setText(item.getFrozen() + "");
            holder.tv_useable.setText(item.getTotal() + "");
            if (item.getZhehe() == null) {
                holder.tv_total.setText(PricingMethodUtil.getPricingUnit()+Constant.ASSETS_DEFAULT_AMOUNT);
            } else {
                holder.tv_total.setText(PricingMethodUtil.getPricingUnit() +PricingMethodUtil.getLargePrice(
                        PricingMethodUtil.getResultByExchangeRate(item.getZhehe(),"CNY", Constant.ASSETS_AMOUNT_PRECISION),Constant.ASSETS_AMOUNT_PRECISION));
            }
        }
        //TODO 兑换买币
        if (null != UserInfoManager.getOtcUserInfoBean() && UserInfoManager.getOtcUserInfoBean().getData() != null && UserInfoManager.getOtcUserInfoBean().getData().isMerch() && !UserInfoManager.getOtcUserInfoBean().getData().isConvertMerchant() && UserInfoManager.isEnableExchangeMer()) {//如果是商家 则显示兑换买币按钮
            holder.tvTransfer2.setVisibility(View.VISIBLE);
            holder.tvTransfer2.setText(context.getString(R.string.d33));
            holder.tvBuyCoin.setVisibility(View.VISIBLE);
            holder.tvBuyCoin.setText(context.getString(R.string.exchange));
            holder.tvBuyCoin.setOnClickListener(view -> DialogUtils.getInstance().merchantsExchange(context, UserInfoManager.getOtcUserInfoBean().getData().getExchangeQuota(), new DialogUtils.onBnClickListener() {
                @Override
                public void onLiftClick(AlertDialog dialog, View view) {
                    dialog.dismiss();
                }

                @Override
                public void onRightClick(AlertDialog dialog, View view) {
                    Event.exchangeClick exchangeClick = new Event.exchangeClick();
                    exchangeClick.setId(item.getId());
                    EventBus.getDefault().post(exchangeClick);
                    dialog.dismiss();
                }
            }));
            holder.tvTransfer2.setOnClickListener(new View.OnClickListener() {//划转
                @Override
                public void onClick(View v) {
                    if (FastClickUtils.isFastClick(1000)||!transfer()) {
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
                        AccountTransferActivity.Companion.launch(context, TransferAccount.WEALTH.getValue(),TransferAccount.OTC.getValue(),item.getId(),null,
                                true,item.getShortName());
                    }
                }
            });
        }else{
            holder.tvTransfer1.setVisibility(View.VISIBLE);
            holder.tvTransfer1.setOnClickListener(new View.OnClickListener() {//划转
                @Override
                public void onClick(View v) {
                    if (FastClickUtils.isFastClick(1000)||!transfer()) {
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
                        AccountTransferActivity.Companion.launch(context, TransferAccount.WEALTH.getValue(),TransferAccount.OTC.getValue(),item.getId(),null,
                                true,item.getShortName());
                    }
                }
            });
        }

        holder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("CNYT".equalsIgnoreCase(item.getShortName())) {
                    return;
                }
                Intent intent = new Intent(context, CoinDetailActivity.class);
                Log.d("点击了", "第" + position + "项");
                Bundle bundle = new Bundle();
                bundle.putParcelable("coinBean", item);
                bundle.putString("memoName", item.getMemoName() + "");
                bundle.putInt("position", position);
                intent.putExtras(bundle);
                // context.startActivity(intent);
            }
        });
        //处理活期理财
        /**买币账户没有此项，故隐藏（买币和币币用的同一布局文件）**/
        holder.bybView.setVisibility(View.GONE);
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
        TextView tvTransfer1;//如果不是商户只显示一个按钮使用该tv
        TextView tvTransfer2;//如果是商户需要显示2个按钮使用该tv
        TextView tvBuyCoin;

        public ViewHole(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_total = itemView.findViewById(R.id.tvValue1);
            this.tv_useable = itemView.findViewById(R.id.tvValue2);
            this.tv_unuseable = itemView.findViewById(R.id.tvValue3);
            this.ll_main = itemView.findViewById(R.id.ll_main);
            this.bybView = itemView.findViewById(R.id.tv_byb);
            this.tvTransfer1 = itemView.findViewById(R.id.tvTransfer);
            this.tvTransfer2 = itemView.findViewById(R.id.tvOperate1);
            this.tvBuyCoin = itemView.findViewById(R.id.tvOperate2);
        }
    }
    private boolean transfer() {
        OtcUserInfoBean uib = UserInfoManager.getOtcUserInfoBean();
        if (uib != null && uib.getData() != null && uib.getData().getOtcUser() != null && !uib.getData().getOtcUser().isAllowOtc()) {
            DialogUtils.getInstance().showOneButtonDialog(context, context.getString(R.string.as3), context.getString(R.string.qq29));
            DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
                @Override
                public void onLiftClick(AlertDialog dialog, View view) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }

                @Override
                public void onRightClick(AlertDialog dialog, View view) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            return false;
        } else {
            return true;
        }
    }
}
