package huolongluo.byw.reform.c2c.oct.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.reform.c2c.oct.bean.BrandCardsEneity;

public class BrandCardAdapter extends RecyclerView.Adapter<BrandCardAdapter.BrandHolder> {
    private List<BrandCardsEneity.DataBeanX.DataBean> datas;
    public static final int ZFB = 3, WX = 2, BRAND_CARD = 1;
    public onItemClick listener;
    public onItemStatusClick itemStatusClick;

    public BrandCardAdapter(List<BrandCardsEneity.DataBeanX.DataBean> data) {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        this.datas = data;
    }

    @Override
    public BrandHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BrandHolder(View.inflate(parent.getContext(), R.layout.brand_card_item, null));
    }

    @Override
    public void onBindViewHolder(BrandHolder holder, int position) {
        holder.cardNum.setText(datas.get(position).getAccount());
        holder.card_changer.setChecked(datas.get(position).getStatus() != 1);
        switch (datas.get(position).getType()) {
            case ZFB:
                holder.type.setVisibility(View.VISIBLE);
                holder.brandName.setVisibility(View.GONE);
                holder.type.setImageDrawable(BaseApp.getSelf().getResources().getDrawable(R.mipmap.zfb_ic));
                break;
            case WX:
                holder.type.setVisibility(View.VISIBLE);
                holder.brandName.setVisibility(View.GONE);
                holder.type.setImageDrawable(BaseApp.getSelf().getResources().getDrawable(R.mipmap.wx_ic));
                break;
            case BRAND_CARD:
                holder.type.setVisibility(View.GONE);
                holder.brandName.setVisibility(View.VISIBLE);
                holder.brandName.setText(datas.get(position).getBankName());
                break;
        }
//        holder.itemView.setOnClickListener(view -> listener.onClick(view, position));
        holder.card_changer.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (!compoundButton.isPressed()) {
                return;
            }
            if (isChecked) {
                if (null != itemStatusClick) {
                    itemStatusClick.onClick(holder.card_changer, position, true);
                }
            } else {
                if (null != itemStatusClick) {
                    itemStatusClick.onClick(holder.card_changer, position, false);
                }
            }
        });
        holder.edit_card.setOnClickListener(view -> listener.onClick(view, position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    static class BrandHolder extends RecyclerView.ViewHolder {
        private TextView brandName;
        private TextView cardNum;
        private Switch card_changer;
        private ImageView edit_card;
        private ImageView type;

        public BrandHolder(View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.type_ico);
            brandName = itemView.findViewById(R.id.brand_name);
            cardNum = itemView.findViewById(R.id.card_num);
            card_changer = itemView.findViewById(R.id.card_changer);
            edit_card = itemView.findViewById(R.id.edit_card);
        }
    }

    public interface onItemClick {
        void onClick(View v, int i);
    }

    public interface onItemStatusClick {
        void onClick(View v, int i, boolean status);
    }

    public void onClickListener(onItemClick listener) {
        this.listener = listener;
    }

    public void onCardStatusListener(onItemStatusClick onItemStatusClick) {
        this.itemStatusClick = onItemStatusClick;
    }
}
