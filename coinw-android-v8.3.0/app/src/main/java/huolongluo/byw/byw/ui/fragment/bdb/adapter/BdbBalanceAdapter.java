package huolongluo.byw.byw.ui.fragment.bdb.adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.legend.model.enumerate.transfer.TransferAccount;
import com.android.legend.ui.transfer.AccountTransferActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.share.Event;
import huolongluo.byw.byw.ui.fragment.bdb.bean.BdbFinancialDetail;
import huolongluo.byw.reform.home.activity.NewsWebviewActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.AgreementUtils;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.FastClickUtils;
import huolongluo.byw.util.NumberUtil;
import huolongluo.byw.util.Util;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.bywx.helper.AppHelper;

/**
 * Created by Administrator on 2019/1/16 0016.
 */
public class BdbBalanceAdapter extends RecyclerView.Adapter<BdbBalanceAdapter.ViewHole> {
    private static final String TAG = "BdbBalanceAdapter";
    private List<BdbFinancialDetail> mAssetcoinsList = new ArrayList<>();
    private Context context;
    boolean isHide = false;

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public boolean hasData() {
        return getItemCount() > 0;
    }

    public BdbBalanceAdapter(Context context, List<BdbFinancialDetail> mAssetcoinsList) {
        this.mAssetcoinsList.addAll(mAssetcoinsList);
        this.context = context;
    }

    public BdbFinancialDetail getItem(int position) {
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

    public void replaceAll(List<BdbFinancialDetail> items) {
        mAssetcoinsList.clear();
        mAssetcoinsList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHole holder, int position) {
        BdbFinancialDetail item = mAssetcoinsList.get(position);
        holder.tv_name.setText(item.getCoinName());
        if (isHide) {
            holder.tv_useable.setText("****");
            holder.tv_total.setText("****");
        } else {
            holder.tv_useable.setText(Util.getStrByPrecision(item.getAvailableVol()));
            if (item.getConvertCny() == null) {
                holder.tv_total.setText(PricingMethodUtil.getPricingUnit()+ Constant.ASSETS_DEFAULT_AMOUNT);
            } else {
                holder.tv_total.setText(PricingMethodUtil.getPricingUnit() +PricingMethodUtil.getLargePrice(
                        PricingMethodUtil.getResultByExchangeRate(item.getConvertCny(),"CNY",Constant.ASSETS_AMOUNT_PRECISION),Constant.ASSETS_AMOUNT_PRECISION));
            }
        }
        holder.tvTitle3.setVisibility(View.GONE);
        holder.tvValue3.setVisibility(View.GONE);

        holder.tvLend.setVisibility(View.VISIBLE);
        holder.tvLend.setText(context.getString(R.string.bdb_to_lend));
        holder.tvLend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAgreementOpen()){
                    return;
                }
                Intent intent = new Intent(context, NewsWebviewActivity.class);
                intent.putExtra("url", UrlConstants.getTradeBdb());
                intent.putExtra("token", UserInfoManager.getToken());
                intent.putExtra("title", context.getResources().getString(R.string.bdb));
                intent.putExtra("hideTitle", false);
                intent.putExtra("isBdb", true);
                context.startActivity(intent);
            }
        });
        holder.tvTransfer.setVisibility(View.VISIBLE);
        holder.tvTransfer.setText(context.getString(R.string.d33));
        holder.tvTransfer.setOnClickListener(new View.OnClickListener() {//划转
            @Override
            public void onClick(View v) {
                if(!isAgreementOpen()){
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
                    AccountTransferActivity.Companion.launch(context, TransferAccount.WEALTH.getValue(),TransferAccount.BDB.getValue(),
                            NumberUtil.toInt(item.getCoinId(),-1),null,true,item.getCoinName());
                }
            }
        });

        /**买币账户没有此项，故隐藏（买币和币币用的同一布局文件）**/
        holder.bybView.setVisibility(View.GONE);
    }
    private boolean isAgreementOpen(){
        if(!AgreementUtils.isBdbOpen()){
            EventBus.getDefault().post(new Event.BdbOpenAgreement());
            return false;
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return mAssetcoinsList.size();
    }

    public static class ViewHole extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_total;
        TextView tv_useable;
        View bybView;
        TextView tvTitle3;
        TextView tvValue3;
        TextView tvTransfer;
        TextView tvLend;

        public ViewHole(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_total = itemView.findViewById(R.id.tvValue1);
            this.tv_useable = itemView.findViewById(R.id.tvValue2);
            this.bybView = itemView.findViewById(R.id.tv_byb);
            this.tvTitle3 = itemView.findViewById(R.id.tv3);
            this.tvValue3 = itemView.findViewById(R.id.tvValue3);
            this.tvLend = itemView.findViewById(R.id.tvOperate1);
            this.tvTransfer = itemView.findViewById(R.id.tvOperate2);
        }
    }
}
