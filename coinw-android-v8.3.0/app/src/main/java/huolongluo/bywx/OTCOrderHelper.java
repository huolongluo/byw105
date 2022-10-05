package huolongluo.bywx;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import huolongluo.byw.reform.c2c.oct.activity.OtcAppealDetailActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcOrderCancleDetailActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcPaymentActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcPrepaidActivity;
import huolongluo.byw.reform.c2c.oct.activity.OtcTradeCompleteActivity;
import huolongluo.byw.reform.c2c.oct.activity.sellactivity.OtcUserSellOtherPayedActivity;
import huolongluo.byw.reform.c2c.oct.bean.OrderDetailBean;
import huolongluo.byw.reform.c2c.oct.bean.OtcUserInfoBean;
import huolongluo.byw.user.UserInfoManager;
public class OTCOrderHelper {
    private static final String TAG = "OTCOrderHelper";

    /**
     * 刷新订单当前状态，并跳转相应界面
     * @param atv
     * @param bean
     * @return
     */
    public static boolean refreshOrderStatus(Activity atv, OrderDetailBean bean) {
        if (atv == null || bean == null) {
            return false;
        }
        //1、判断当前Activity是否为顶底窗口
        if (atv.isFinishing()) {
            return false;
        }
        //处理数据
        OrderDetailBean.DataBean.OrdersBean order = bean.getData().getOrders();
        int status = order.getStatus();
        int adStatus = order.getAdStatus();
        int uid = order.getUid();
        int type = order.getType();
        int adUserId = order.getAdUserId();
        String orderId = order.getOrderNo();
        String currentClz = atv.getClass() == null ? "" : atv.getClass().getSimpleName();
        String msg = String.format("currentClz:%s,status:%d,adStatus:%d,uid:%d,type:%d,adUserId:%d,orderId:%s", currentClz, status, adStatus, uid, type, adUserId, orderId);
        Log.d(TAG, msg, new Exception());
        //2、判断买家和卖家，根据业务跳转不同界面
        //判断当前用户是不是此订单中的卖家
        boolean isSeller = isSeller(bean);
        //3、判断当前用户针对当前订单是出售还是购买
        if (isSeller) {//卖家
            return seller(atv, order);
        } else {
            return buyer(atv, order);
        }
    }

    private static boolean seller(Activity atv, OrderDetailBean.DataBean.OrdersBean order) {
        int status = order.getStatus();
        int adStatus = order.getAdStatus();
        int s = adStatus >= status ? adStatus : status;
        int orderId = order.getId();
        int tradeType = 2;//出售
        if (s == 1) {   //已支付
            return gotoTarget(atv, OtcUserSellOtherPayedActivity.class, orderId, tradeType);
        } else if (s >= 5 && s <= 7) {  //申诉
            return gotoTarget(atv, OtcAppealDetailActivity.class, orderId, tradeType);
        } else if (s == 3 || s == 4) {  //取消
            return gotoTarget(atv, OtcOrderCancleDetailActivity.class, orderId, tradeType);//出售
        } else if (s == 2) {//已完成
            return gotoTarget(atv, OtcTradeCompleteActivity.class, orderId, tradeType);
        }
        return false;
    }

    //TODO 备注信息
//=============================================================
    //买家
    //购买
    //购买-已取消页
    //商家-用户订单-购买-已取消页  OtcOrderCancleDetailActivity
    //购买-已完成页
    //商家-商户订单-购买-已完成页  OtcTradeCompleteActivity
    //商家-商户订单-购买-申诉页    OtcAppealDetailActivity
    //
    //商家-购买-等待支付页      OtcPaymentActivity
    //商家-购买-等待卖家放币（已支付）    OtcPrepaidActivity

    //=============================================================
    //出售
    //出售-已取消页
    //商家-商户订单-出售-已取消页  OtcOrderCancleDetailActivity
    //出售-已完成页
    //商家-商户订单-出售-已完成页  OtcTradeCompleteActivity
    //出售-申诉页
    //商家-商户订单-出售-申诉页    OtcAppealDetailActivity
//=============================================================
    //卖家
    //商家-商户订单-出售-已支付页  OtcUserSellOtherPayedActivity
//=============================================================
//  status  用户状态 0 未付款，1 已付款，2 已完成，3 用户取消，4 系统撤单，5 申诉中
//  adStatus  商家状态 0 未付款，1 已付款，2 已完成，3 已取消，4 确认收款，5 申诉中
//  6 申请取消申诉  7 取消申诉已完成
//=============================================================
    private static boolean buyer(Activity atv, OrderDetailBean.DataBean.OrdersBean order) {
        int status = order.getStatus();
        int adStatus = order.getAdStatus();
        int s = adStatus >= status ? adStatus : status;
        int orderId = order.getId();
        int tradeType = 1;//购买
        if (s == 0) {
            return gotoTarget(atv, OtcPaymentActivity.class, orderId, tradeType);
        } else if (s == 1) {
            return gotoTarget(atv, OtcPrepaidActivity.class, orderId, tradeType);
        } else if (s >= 5 && s <= 7) {//申诉状态范围
            return gotoTarget(atv, OtcAppealDetailActivity.class, orderId, tradeType);
        } else if (s == 3 || s == 4) {  //取消
            return gotoTarget(atv, OtcOrderCancleDetailActivity.class, orderId, tradeType);//出售
        } else if (s == 2) {
            return gotoTarget(atv, OtcTradeCompleteActivity.class, orderId, tradeType);
        }
        return false;
    }

    private static boolean gotoTarget(Activity atv, Class targetClz, int orderId, int tradeType) {
        if (atv == null || atv.getClass() == null || targetClz == null || orderId <= 0) {
            return false;
        }
        String msg = String.format("gotoTarget-currentClz:%s,targetClz:%s,orderId:%s,tradeType:%d", atv.getClass().getSimpleName(), targetClz.getSimpleName(), orderId, tradeType);
        Log.d(TAG, msg);
        //相同的界面，不进行跳转
        if (TextUtils.equals(atv.getClass().getSimpleName(), targetClz.getSimpleName())) {
            return false;
        }
        Intent intent = new Intent(atv, targetClz);
        intent.putExtra("orderId", orderId);
        if (tradeType != -1) {
            intent.putExtra("tradeType", tradeType);
        }
        atv.startActivity(intent);
        atv.finish();
        return true;
    }

    /**
     * 判断是否为卖家
     * @param bean
     * @return
     */
    public static boolean isSeller(OrderDetailBean bean) {
        //买方：
        //登陆人ID=uid      并且 type=1
        //登陆人ID=adUserId 并且 type=2
        //卖方：
        //登陆人ID=uid      并且 type=2
        //登陆人ID=adUserId 并且 type=1
        int uid = bean.getData().getOrders().getUid();
        int type = bean.getData().getOrders().getType();
        int adUserId = bean.getData().getOrders().getAdUserId();
        OtcUserInfoBean ouib = UserInfoManager.getOtcUserInfoBean();
        int cuid = ouib.getData().getOtcUser().getUid();
        if (uid == cuid && type == 2) {
            return true;
        } else if (adUserId == cuid && type == 1) {
            return true;
        }
        return false;
    }

}
