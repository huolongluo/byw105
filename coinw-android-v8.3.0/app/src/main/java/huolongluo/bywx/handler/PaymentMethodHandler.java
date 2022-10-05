package huolongluo.bywx.handler;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import huolongluo.byw.R;
import huolongluo.byw.reform.c2c.oct.bean.PayOrderInfoBean;
public class PaymentMethodHandler {
    public static View getPaymentView(Context context, PayOrderInfoBean.DataBean data, OnPaymentItemClickListener listener) {
        LinearLayout rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        //出售
        for (PayOrderInfoBean.PayAccountsBean bean : data.getPayAccounts()) {
            View view = getView(context, rootLayout, bean, listener);
            rootLayout.addView(view);
        }
        return rootLayout;
    }

    private static View getView(Context context, View inflate, PayOrderInfoBean.PayAccountsBean bean, OnPaymentItemClickListener listener) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_one_click_select_card_item, null);
        ImageView iconIV = itemView.findViewById(R.id.iv_icon);
        TextView titleTxt = itemView.findViewById(R.id.tv_title);
        TextView countTxt = itemView.findViewById(R.id.tv_count);
        switch (bean.getType()) {
            case 3:
                iconIV.setImageResource(R.mipmap.zfb);
                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onItem(3, bean.getAccount(), bean.getId(), inflate);
                    }
                });
                titleTxt.setText(R.string.b92);
                countTxt.setText(bean.getAccount().replaceAll("(\\w+)\\w{5}(\\w+)", "$1***$2"));
                break;
            case 2:
                iconIV.setImageResource(R.mipmap.wx);
                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onItem(2, bean.getAccount(), bean.getId(), inflate);
                    }
                });
                titleTxt.setText(R.string.b93);
                countTxt.setText(bean.getAccount().replaceAll("(\\w+)\\w{5}(\\w+)", "$1***$2"));
                break;
            case 1:
                iconIV.setImageResource(R.mipmap.zfsz);
                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onItem(1, bean.getAccount(), bean.getId(), inflate);
                    }
                });
                titleTxt.setText(R.string.b91);
                countTxt.setText(bean.getAccount().replaceAll("(\\w+)\\w{5}(\\w+)", "$1***$2"));
                break;
        }
        return itemView;
    }

    public interface OnPaymentItemClickListener {
        void onItem(int payType, String account, int accountId, View inflate);
    }
}
