package huolongluo.byw.reform.c2c.adapter;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.reform.c2c.oct.bean.AdvertisementBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.tip.DialogUtils;
/**
 * Created by Administrator on 2019/7/22 0022.
 */
public class SellInfoAdapter extends RecyclerView.Adapter<SellinfoBaseHold> {
    private List<AdvertisementBean.DataBean.SellBean> list = new ArrayList<>();

    public SellInfoAdapter(List<AdvertisementBean.DataBean.SellBean> list) {
        this.list = list;
    }

    private ItemClickListener mItemClickListener;

    public interface ItemClickListener {
        public void onItemClick(int position, AdvertisementBean.DataBean.SellBean bean);
        public void onDeleteClick(String id);
        public void editAdw(int id);
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    @Override
    public SellinfoBaseHold onCreateViewHolder(ViewGroup parent, int viewType) {
        SellinfoBaseHold viewHold = null;
        if (viewType == 0) {
            viewHold = new ViewHoldMa(LayoutInflater.from(parent.getContext()).inflate(R.layout.sellinfo_item2, parent, false));
        } else if (viewType == 1) {
            viewHold = new ViewHoldTit(LayoutInflater.from(parent.getContext()).inflate(R.layout.sellinfo_item1, parent, false));
        } else if (viewType == 2) {
            viewHold = new ViewHoldEmpt(LayoutInflater.from(parent.getContext()).inflate(R.layout.sellinfo_item3, parent, false));
        }
        return viewHold;
    }

    @Override
    public void onBindViewHolder(SellinfoBaseHold holder, int position) {
        AdvertisementBean.DataBean.SellBean bean = list.get(position);
        if (holder instanceof ViewHoldTit) {
            ViewHoldTit viewHoldTit = (ViewHoldTit) holder;
            if (position == 0) {
                viewHoldTit.title_tv.setText(R.string.sell_online);
            } else {
                viewHoldTit.title_tv.setText(R.string.buy_online);
            }
        } else if (holder instanceof ViewHoldMa) {
            ViewHoldMa viewHoldMa = (ViewHoldMa) holder;
//            viewHoldMa.head_tv.setText(bean.getUserName().charAt(0) + "");
            RequestOptions ro = new RequestOptions();
            ro.error(R.mipmap.rmblogo);
            ro.centerCrop();
            if (BaseApp.getSelf() != null && !TextUtils.isEmpty(bean.getCoinUrl())) {
                Glide.with(BaseApp.getSelf()).load(bean.getCoinUrl()).apply(ro).into((viewHoldMa.head_tv));
            }
            viewHoldMa.coinName_tv.setText(bean.getCoinName());
            viewHoldMa.limit_num_tv.setText(bean.getOrderMin_s() + "-" + bean.getOrderMax_s() + " " + bean.getCoinName());
            viewHoldMa.total_tv.setText(bean.getLeftAmount() + " " + bean.getCoinName());
            viewHoldMa.price_tv.setText(bean.getPrice_s() + " CNY");
            viewHoldMa.btStartSell.setText((bean.getStatus() == 0 || bean.getStatus() == 2) ? BaseApp.getSelf().getString(R.string.shelves_) : BaseApp.getSelf().getString(R.string.shelves_1));
            viewHoldMa.btStartSell.setBackground((bean.getStatus() == 0 || bean.getStatus() == 2) ? holder.itemView.getContext().getResources().getDrawable(R.drawable.off) : holder.itemView.getContext().getResources().getDrawable(R.drawable.on));
            if (UserInfoManager.isLogin() && UserInfoManager.getUserInfo() != null && UserInfoManager.getUserInfo().getFid() == bean.getUid()) {
                if (bean.getStatus() == 0 || bean.getStatus() == 2) {
                    viewHoldMa.btStartSell.setVisibility(View.VISIBLE);
                    viewHoldMa.unPublish.setVisibility(View.GONE);
                } else {
                    viewHoldMa.unPublish.setVisibility(View.VISIBLE);
                    viewHoldMa.btStartSell.setVisibility(View.GONE);
                    viewHoldMa.bt_delete.setOnClickListener(v -> {
                        mItemClickListener.onDeleteClick(String.valueOf(bean.getId()));
                    });
                    viewHoldMa.bt_edit.setOnClickListener(v -> {
                        mItemClickListener.editAdw(bean.getId());
                    });
                    viewHoldMa.bt_putaway.setOnClickListener(v -> {
                        adManagerDialog(viewHoldMa, bean, position);
                    });
                }
                //https://docs.qq.com/doc/DRHJlZ2lHZXV5VlJy
                viewHoldMa.bt_edit.setVisibility(View.VISIBLE);
                viewHoldMa.bt_delete.setVisibility(View.VISIBLE);
            } else {
                viewHoldMa.btStartSell.setVisibility(View.GONE);
                viewHoldMa.unPublish.setVisibility(View.GONE);
            }
            if (mItemClickListener != null) {
                viewHoldMa.btStartSell.setOnClickListener(v -> adManagerDialog(viewHoldMa, bean, position));
            }
            if (bean.getBankId() == 0) {
                viewHoldMa.bank_iv.setVisibility(View.GONE);
            } else {
                viewHoldMa.bank_iv.setVisibility(View.VISIBLE);
            }
            if (bean.getAlipayId() == 0) {
                viewHoldMa.alipay_iv.setVisibility(View.GONE);
            } else {
                viewHoldMa.alipay_iv.setVisibility(View.VISIBLE);
            }
            if (bean.getWechatId() == 0) {
                viewHoldMa.wechat_iv.setVisibility(View.GONE);
            } else {
                viewHoldMa.wechat_iv.setVisibility(View.VISIBLE);
            }
            viewHoldMa.alipay_iv.setVisibility(View.GONE);
            viewHoldMa.bank_iv.setVisibility(View.GONE);
            viewHoldMa.wechat_iv.setVisibility(View.GONE);
            if (bean.getPayAccountTypes() != null) {
                for (int i = 0; i < bean.getPayAccountTypes().size(); i++) {
                    switch (bean.getPayAccountTypes().get(i)) {
                        case Constant.ALIPAY:
                            viewHoldMa.alipay_iv.setVisibility(View.VISIBLE);
                            break;
                        case Constant.BANK:
                            viewHoldMa.bank_iv.setVisibility(View.VISIBLE);
                            break;
                        case Constant.WECHAT:
                            viewHoldMa.wechat_iv.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }
        } else if (holder instanceof ViewHoldEmpt) {
            ViewHoldEmpt viewHoldEmpt = (ViewHoldEmpt) holder;
        }
    }

    /**
     * 广告上架下架确认弹窗
     * @param viewHoldMa
     * @param bean
     * @param position
     */
    private void adManagerDialog(ViewHoldMa viewHoldMa, AdvertisementBean.DataBean.SellBean bean, int position) {
        DialogUtils.getInstance().showTwoButtonDialog(viewHoldMa.itemView.getContext(), (bean.getStatus() == 0 || bean.getStatus() == 2) ? BaseApp.getSelf().getString(R.string.toRemove) : BaseApp.getSelf().getString(R.string.toRelease), BaseApp.getSelf().getString(R.string.b74), BaseApp.getSelf().getString(R.string.c33), new DialogUtils.onBnClickListener() {
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
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(position, bean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getViewType();
    }

    static class ViewHoldTit extends SellinfoBaseHold {
        TextView title_tv;

        public ViewHoldTit(View itemView) {
            super(itemView);
            title_tv = itemView.findViewById(R.id.title_tv);
        }
    }

    static class ViewHoldEmpt extends SellinfoBaseHold {
        public ViewHoldEmpt(View itemView) {
            super(itemView);
        }
    }

    static class ViewHoldMa extends SellinfoBaseHold {
        private final TextView bt_delete;
        private final TextView bt_putaway;
        private final TextView bt_edit;
        public ImageView head_tv;
        public TextView coinName_tv;
        public TextView limit_num_tv;
        public TextView total_tv;
        public TextView price_tv;
        public ImageView bank_iv;
        public ImageView alipay_iv;
        public ImageView wechat_iv;
        public Button btStartSell;
        public LinearLayout unPublish;

        public ViewHoldMa(View itemView) {
            super(itemView);
            head_tv = itemView.findViewById(R.id.head_tv);
            coinName_tv = itemView.findViewById(R.id.coinName_tv);
            limit_num_tv = itemView.findViewById(R.id.limit_num_tv);
            total_tv = itemView.findViewById(R.id.total_tv);
            price_tv = itemView.findViewById(R.id.price_tv);
            bank_iv = itemView.findViewById(R.id.bank_iv);
            alipay_iv = itemView.findViewById(R.id.alipay_iv);
            wechat_iv = itemView.findViewById(R.id.wechat_iv);
            btStartSell = itemView.findViewById(R.id.btStartSell);
            unPublish = itemView.findViewById(R.id.unPublish);
            unPublish = itemView.findViewById(R.id.unPublish);
            bt_delete = itemView.findViewById(R.id.bt_delete);
            bt_putaway = itemView.findViewById(R.id.bt_putaway);
            bt_edit = itemView.findViewById(R.id.bt_edit);
        }
    }
}
