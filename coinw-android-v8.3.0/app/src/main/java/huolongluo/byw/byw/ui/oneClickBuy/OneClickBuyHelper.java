package huolongluo.byw.byw.ui.oneClickBuy;
import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import huolongluo.byw.R;
import huolongluo.byw.byw.ui.oneClickBuy.bean.CardInfosEntity;
import huolongluo.byw.reform.c2c.oct.activity.PaymentAccountActivityNew;
public class OneClickBuyHelper {
    /**
     * 获取支付方式
     * 支付方式: 1 银行卡, 2 微信, 3 支付宝
     */
    public static void showPopupWindow(Activity atv, CardInfosEntity orderTipBean, IClickOrderInfoCallback callback) {
        if (atv == null) {
            return;
        }
        View inflate = LayoutInflater.from(atv).inflate(R.layout.layout_one_click_select_card, null);
        LinearLayout payLayout = inflate.findViewById(R.id.ll_pay);
//        RelativeLayout zfb = inflate.findViewById(R.id.zfb_view);
//        RelativeLayout yhk = inflate.findViewById(R.id.yhk_view);
//        RelativeLayout wx = inflate.findViewById(R.id.wx_view);
        TextView hide = inflate.findViewById(R.id.hide);//取消弹框
//        TextView count_zhb = inflate.findViewById(R.id.count_zhb);
//        TextView count_yhk = inflate.findViewById(R.id.count_yhk);
//        TextView count_wx = inflate.findViewById(R.id.count_wx);
        PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        hide.setOnClickListener(v -> popupWindow.dismiss());
        if (null == orderTipBean) {//购买
//            zfb.setVisibility(View.VISIBLE);
//            wx.setVisibility(View.VISIBLE);
//            yhk.setVisibility(View.VISIBLE);
//            zfb.setOnClickListener(v -> {
//                if (callback != null) {
//                    callback.orderInfo(3, null, -1, inflate, popupWindow);
//                }
//            });
//            wx.setOnClickListener(v -> {
//                if (callback != null) {
//                    callback.orderInfo(2, null, -1, inflate, popupWindow);
//                }
//            });
//            yhk.setOnClickListener(v -> {
//                if (callback != null) {
//                    callback.orderInfo(1, null, -1, inflate, popupWindow);
//                }
//            });
            popupWindow.showAtLocation(inflate, Gravity.BOTTOM, 0, 0);
            return;
        }
        //出售
        for (CardInfosEntity.DataBean dataBean : orderTipBean.getData()) {
            View view = getView(atv, inflate, popupWindow, dataBean, callback);
            payLayout.addView(view);
        }
        if (orderTipBean.getData().size() == 0) {
            RelativeLayout noPayType_ll = inflate.findViewById(R.id.noPayType_ll);
            noPayType_ll.setVisibility(View.VISIBLE);
            TextView to_set_card = inflate.findViewById(R.id.to_set_card);
            to_set_card.setOnClickListener(view -> {
                Intent intent = new Intent(atv, PaymentAccountActivityNew.class);
                atv.startActivity(intent);
                popupWindow.dismiss();
            });
        } else {
        }
        popupWindow.showAtLocation(inflate, Gravity.BOTTOM, 0, 0);
    }

    private static View getView(Activity atv, View inflate, PopupWindow popupWindow, CardInfosEntity.DataBean dataBean, IClickOrderInfoCallback callback) {
        View itemView = LayoutInflater.from(atv).inflate(R.layout.layout_one_click_select_card_item, null);
        ImageView iconIV = itemView.findViewById(R.id.iv_icon);
        TextView titleTxt = itemView.findViewById(R.id.tv_title);
        TextView countTxt = itemView.findViewById(R.id.tv_count);
        switch (dataBean.getType()) {
            case 3:
                iconIV.setImageResource(R.mipmap.zfb);
                itemView.setOnClickListener(v -> {
                    if (callback != null) {
                        callback.orderInfo(3, dataBean.getAccount(), dataBean.getId(), inflate, popupWindow);
                    }
                });
                titleTxt.setText(R.string.b92);
                countTxt.setText(dataBean.getAccount().replaceAll("(\\w+)\\w{5}(\\w+)", "$1***$2"));
                break;
            case 2:
                iconIV.setImageResource(R.mipmap.wx);
                itemView.setOnClickListener(v -> {
                    if (callback != null) {
                        callback.orderInfo(2, dataBean.getAccount(), dataBean.getId(), inflate, popupWindow);
                    }
                });
                titleTxt.setText(R.string.b93);
                countTxt.setText(dataBean.getAccount().replaceAll("(\\w+)\\w{5}(\\w+)", "$1***$2"));
                break;
            case 1:
                iconIV.setImageResource(R.mipmap.zfsz);
                itemView.setOnClickListener(v -> {
                    if (callback != null) {
                        callback.orderInfo(1, dataBean.getAccount(), dataBean.getId(), inflate, popupWindow);
                    }
                });
                titleTxt.setText(R.string.b91);
                countTxt.setText(dataBean.getAccount().replaceAll("(\\w+)\\w{5}(\\w+)", "$1***$2"));
                break;
        }
        return itemView;
    }

    public static interface IClickOrderInfoCallback {
        void orderInfo(int payType, String account, int accountId, View inflate, PopupWindow popupWindow);
    }
}
