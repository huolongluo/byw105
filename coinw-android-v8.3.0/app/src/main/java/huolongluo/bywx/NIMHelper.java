package huolongluo.bywx;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.session.activity.P2PMessageActivity;
import com.netease.nim.uikit.impl.customization.DefaultP2PSessionCustomization;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.bean.UserInfoBean;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.model.IM;
import huolongluo.byw.model.IMResult;
import huolongluo.byw.reform.c2c.oct.bean.OrderDetailBean;
import huolongluo.byw.reform.c2c.oct.bean.OtcUserInfoBean;
import huolongluo.byw.reform.c2c.oct.bean.PayOrderInfoBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.SPUtils;
import okhttp3.Request;
public class NIMHelper {
    private static final String TAG = "NIMHelper";

    /**
     * 登录获得IM通道Token
     */
    public static void getToken() {
        String token = UserInfoManager.getToken();
        if (TextUtils.isEmpty(token)) {
            Log.e(TAG, "token is null or empty.");
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        OkhttpManager.getAsync(UrlConstants.IM_GET_TOKEN + "?loginToken=" + token + "&" + OkhttpManager.encryptGet(params), new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                Log.e(TAG, "requestFailure: " + errorMsg, e);
            }

            @Override
            public void requestSuccess(String result) {
                Log.d(TAG, "result: " + result);
                if (TextUtils.isEmpty(result)) {
                    //TODO 处理异常数据
                    return;
                }
                //{"success":true,"code":0,"data":{"accid":"5d78b8e3a0932547d1d020cc","token":"c3f57cd12562a10f17f3703de91ac2fc"}}
                Type type = new TypeToken<IMResult<IM>>() {
                }.getType();
                IMResult<IM> im = GsonUtil.json2Obj(result, type);
                if (im == null || im.data == null) {
                    //TODO 处理异常数据
                    return;
                }
                login(im.data.accid, im.data.token);
            }
        });
    }

    /**
     * 获得商家的IM通道ID
     * @param merchantId
     * @param callback
     */
    public static void getIMAccount(String merchantId, OkhttpManager.DataCallBack callback) {
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", merchantId);
        OkhttpManager.getAsync(UrlConstants.IM_GET_ACCID + "?loginToken=" + UserInfoManager.getToken() + "&" + OkhttpManager.encryptGet(params), callback);
    }

    /**
     * 由于历史原因，同一数据定义多次实例对象，牵涉较多，暂时不处理该处。后面定计划来对应修改
     * adStatus:0 未付款，1 已付款，2 已完成，3 用户取消，4 系统撤单，5 申诉中
     * @param bean
     */
    public static void contactMerchant(Activity atv, PayOrderInfoBean bean) {
        if (bean == null || bean.getData() == null || bean.getData().getOrder() == null) {
            return;
        }
        contactMerchant(atv, bean, 0);
    }

    public static void contactMerchant(Activity atv, PayOrderInfoBean bean, long time) {
        if (bean == null || bean.getData() == null || bean.getData().getOrder() == null) {
            Log.e(TAG, "contactMerchant: bean.getData()==null?" + (bean.getData() == null ? true : false), new Exception());
            return;
        }
        PayOrderInfoBean.DataBean.OrderBean order = bean.getData().getOrder();
        String merchantId = order.getAdUserId() + "";
        HashMap dataMap = new HashMap();
        //组装数据
        dataMap.put("orderNo", order.getOrderNo());
        dataMap.put("account", order.getAccount());
        dataMap.put("adId", order.getAdId());
        dataMap.put("adStatus", order.getAdStatus());
        dataMap.put("adUserId", order.getAdUserId());
        dataMap.put("adUserName", order.getAdUserName());
        dataMap.put("amount", order.getAmount());
        dataMap.put("coinId", order.getCoinId());
        dataMap.put("coinName", order.getCoinName());
        dataMap.put("createTime", getCreateTime(order.getCreateTime()));
        dataMap.put("createTime_s", order.getCreateTime_s());
        dataMap.put("endTime", order.getEndTime());
        dataMap.put("fee", order.getFee());
        dataMap.put("id", order.getId());
        dataMap.put("payLimit", order.getPayLimit());
        dataMap.put("countdown", time);
        dataMap.put("payType", order.getPayType());
        dataMap.put("price", order.getPrice());
        dataMap.put("qrCode", order.getQrCode());
        dataMap.put("remark", order.getRemark());
        dataMap.put("startTime", order.getStartTime());
        dataMap.put("status", order.getStatus());
        dataMap.put("totalAmount", order.getTotalAmount());
        dataMap.put("transReferNum", order.getTransReferNum());
        dataMap.put("type", order.getType());
        dataMap.put("uid", order.getUid());
        dataMap.put("updateTime", getUpdateTime(order.getUpdateTime()));
        dataMap.put("userName", order.getUserName());
        dataMap.put("version", order.getVersion());
        dataMap.put("currentUserName", UserInfoManager.getUserInfo().getNickName());
//        dataMap.put("oppositeUserName", bean.getData().getOppositeUserName());
        dataMap.put("oppositePhoneNumber", bean.getData().getInformation());
        //商家部分数据
        PayOrderInfoBean.DataBean.OtcUserinfoBean otcBean = bean.getData().getOtcUserinfo();
        //处理商家数据，取的原始字段名字，未转成以ad开头的字段名字
        if (otcBean != null) {
            dataMap.put("otcLevel", otcBean.getOtcLevel());
            dataMap.put("otcLevelName", otcBean.getOtcLevelName());
            dataMap.put("telephone", otcBean.getTelephone());
        }
        //处理名称问题
        String oppositeUserName = getOppositeUserName(bean.getData().getOppositeUserName(), order.getUserName(), order.getAdUserName());
        dataMap.put("oppositeUserName", oppositeUserName);
        UserInfoBean uib = UserInfoManager.getUserInfo();
        if (uib != null) {
            if (TextUtils.equals(merchantId, uib.getFid() + "")) {
                merchantId = order.getUid() + "";
            }
        }
        contactMerchant(atv, merchantId, dataMap);
    }

    private static String getOppositeUserName(String oppositeUserName, String userName, String adUserName) {
        if (!TextUtils.isEmpty(oppositeUserName)) {
            return oppositeUserName;
        }
        //处理名称问题
        OtcUserInfoBean ouib = UserInfoManager.getOtcUserInfoBean();
        if (null == ouib.getData().getOtcUser()) return "";
        String nickname = ouib.getData().getOtcUser().getNickname();
        if (!TextUtils.isEmpty(nickname)) {
            if (TextUtils.equals(nickname, userName)) {
                return adUserName;
            } else {
                return userName;
            }
        }
        return oppositeUserName;
    }

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

    private static HashMap getCreateTime(PayOrderInfoBean.DataBean.OrderBean.CreateTimeBean timeBean) {
        HashMap timeMap = new HashMap();
        if (timeBean == null) {
            return timeMap;
        }
        timeMap.put("date", timeBean.getDate());
        timeMap.put("day", timeBean.getDay());
        timeMap.put("hours", timeBean.getHours());
        timeMap.put("minutes", timeBean.getMinutes());
        timeMap.put("month", timeBean.getMonth());
        timeMap.put("seconds", timeBean.getSeconds());
        timeMap.put("time", timeBean.getTime());
        timeMap.put("timezoneOffset", timeBean.getTimezoneOffset());
        timeMap.put("year", timeBean.getYear());
        return timeMap;
    }

    private static HashMap getUpdateTime(PayOrderInfoBean.DataBean.OrderBean.UpdateTimeBean timeBean) {
        HashMap timeMap = new HashMap();
        if (timeBean == null) {
            return timeMap;
        }
        timeMap.put("date", timeBean.getDate());
        timeMap.put("day", timeBean.getDay());
        timeMap.put("hours", timeBean.getHours());
        timeMap.put("minutes", timeBean.getMinutes());
        timeMap.put("month", timeBean.getMonth());
        timeMap.put("seconds", timeBean.getSeconds());
        timeMap.put("time", timeBean.getTime());
        timeMap.put("timezoneOffset", timeBean.getTimezoneOffset());
        timeMap.put("year", timeBean.getYear());
        return timeMap;
    }

    public static void contactMerchant(Activity atv, OrderDetailBean bean) {
        contactMerchant(atv, bean, 0);
    }

    public static void contactMerchant(Activity atv, OrderDetailBean bean, long time) {
        if (bean == null || bean.getData() == null || bean.getData().getOrders() == null) {
            Log.e(TAG, "contactMerchant: bean.getData()==null?" + (bean.getData() == null ? true : false), new Exception());
            return;
        }
        OrderDetailBean.DataBean.OrdersBean order = bean.getData().getOrders();
        String merchantId = order.getAdUserId() + "";
        HashMap dataMap = new HashMap();
        dataMap.put("orderNo", order.getOrderNo());
        dataMap.put("account", order.getAccount());
        dataMap.put("adId", order.getAdId());
        dataMap.put("adStatus", order.getAdStatus());
        dataMap.put("adUserId", order.getAdUserId());
        dataMap.put("adUserName", order.getAdUserName());
        dataMap.put("amount", order.getAmount());
        dataMap.put("coinId", order.getCoinId());
        dataMap.put("coinName", order.getCoinName());
        dataMap.put("createTime", getCreateTime(order.getCreateTime()));
        dataMap.put("createTime_s", order.getCreateTime_s());
        dataMap.put("endTime", order.getEndTime());
        dataMap.put("fee", order.getFee());
        dataMap.put("id", order.getId());
        dataMap.put("payLimit", order.getPayLimit());
        dataMap.put("countdown", time);
        dataMap.put("payType", order.getPayType());
        dataMap.put("price", order.getPrice());
        dataMap.put("qrCode", order.getQrCode());
        dataMap.put("remark", order.getRemark());
        dataMap.put("startTime", order.getStartTime());
        dataMap.put("status", order.getStatus());
        dataMap.put("totalAmount", order.getTotalAmount());
        dataMap.put("transReferNum", order.getTransReferNum());
        dataMap.put("type", order.getType());
        dataMap.put("uid", order.getUid());
        dataMap.put("updateTime", getUpdateTime(order.getUpdateTime()));
        dataMap.put("userName", order.getUserName());
        dataMap.put("version", order.getVersion());
        dataMap.put("currentUserName", UserInfoManager.getUserInfo().getNickName());
        dataMap.put("oppositePhoneNumber", bean.getData().getInformation());
        //商家部分数据
//        PayOrderInfoBean.DataBean.OtcUserinfoBean otcBean = bean.getData().getComplaintInfo();
        //处理商家数据，取的原始字段名字，未转成以ad开头的字段名字
//            dataMap.put("otcLevel", orgInfoBean.getOtcLevel());
//            dataMap.put("otcLevelName", orgInfoBean.getOtcLevelName());
        //商家电话号码
        dataMap.put("telephone", bean.getData().getInformation());
        //处理名称问题
        String oppositeUserName = getOppositeUserName(bean.getData().getOppositeUserName(), order.getUserName(), order.getAdUserName());
        dataMap.put("oppositeUserName", oppositeUserName);
        dataMap.put("oppositeUserName", oppositeUserName);
        UserInfoBean uib = UserInfoManager.getUserInfo();
        if (uib != null) {
            if (TextUtils.equals(merchantId, uib.getFid() + "")) {
                merchantId = order.getUid() + "";
            }
        }
        //组装数据
        contactMerchant(atv, merchantId, dataMap);
    }

    private static HashMap getCreateTime(OrderDetailBean.DataBean.OrdersBean.CreateTimeBean timeBean) {
        HashMap timeMap = new HashMap();
        if (timeBean == null) {
            return timeMap;
        }
        timeMap.put("date", timeBean.getDate());
        timeMap.put("day", timeBean.getDay());
        timeMap.put("hours", timeBean.getHours());
        timeMap.put("minutes", timeBean.getMinutes());
        timeMap.put("month", timeBean.getMonth());
        timeMap.put("seconds", timeBean.getSeconds());
        timeMap.put("time", timeBean.getTime());
        timeMap.put("timezoneOffset", timeBean.getTimezoneOffset());
        timeMap.put("year", timeBean.getYear());
        return timeMap;
    }

    private static HashMap getUpdateTime(OrderDetailBean.DataBean.OrdersBean.UpdateTimeBean timeBean) {
        HashMap timeMap = new HashMap();
        if (timeBean == null) {
            return timeMap;
        }
        timeMap.put("date", timeBean.getDate());
        timeMap.put("day", timeBean.getDay());
        timeMap.put("hours", timeBean.getHours());
        timeMap.put("minutes", timeBean.getMinutes());
        timeMap.put("month", timeBean.getMonth());
        timeMap.put("seconds", timeBean.getSeconds());
        timeMap.put("time", timeBean.getTime());
        timeMap.put("timezoneOffset", timeBean.getTimezoneOffset());
        timeMap.put("year", timeBean.getYear());
        return timeMap;
    }

    private static void contactMerchant(Activity atv, String merchantId, HashMap dataMap) {
        //商家ID
        if (TextUtils.isEmpty(merchantId) || dataMap == null) {
            //TODO show
            return;
        }
        //获得商家的IM通道名称
        NIMHelper.getIMAccount(merchantId, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                Log.e(TAG, "errorMsg: " + errorMsg);
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void requestSuccess(String result) {
                Log.e(TAG, "result: " + result);
                try {
                    praseData(result);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            private void praseData(String result) throws Throwable {
                if (TextUtils.isEmpty(result)) {
                    //TODO 处理异常数据
                    return;
                }
                //
                //{"success":true,"code":0,"data":"5d78c9a1a0932547d1d020d0"}
                Type type = new TypeToken<IMResult<String>>() {
                }.getType();
                IMResult<String> im = GsonUtil.json2Obj(result, type);
                if (im == null || im.data == null) {
                    //TODO 处理异常数据
                    return;
                }
                //启动IM界面进行聊天
//                NimUIKit.startP2PSession(BaseApp.getSelf().getApplicationContext(), im.data.accid);
                DefaultP2PSessionCustomization commonP2PSessionCustomization = new DefaultP2PSessionCustomization();
//                NimUIKitImpl.startChatting(BaseApp.getSelf().getApplicationContext(), im.data.accid, SessionTypeEnum.P2P, commonP2PSessionCustomization, anchor);
                P2PMessageActivity.start(atv, im.data, commonP2PSessionCustomization, dataMap);
            }
        });
    }

    public static void login(final String account, String pwd) {
        final String token = tokenFromPassword(pwd);
        // 登录
        AbortableFuture<LoginInfo> loginRequest = NimUIKit.login(new LoginInfo(account, token), new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                Log.e(TAG, "login success");
                try {
                    SPUtils.saveString(BaseApp.getSelf().getApplicationContext(), "im_account", param.getAccount());
                    SPUtils.saveString(BaseApp.getSelf().getApplicationContext(), "im_token", param.getToken());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
//                NimUIKit.startP2PSession(context, "ben");
            }

            @Override
            public void onFailed(int code) {
                Log.e(TAG, "onFailed code: " + code);
                SPUtils.saveString(BaseApp.getSelf().getApplicationContext(), "im_account", "");
                SPUtils.saveString(BaseApp.getSelf().getApplicationContext(), "im_token", "");
            }

            @Override
            public void onException(Throwable exception) {
//                Logger.getInstance().debug(TAG, "exception", exception);
                Log.e(TAG, "onException", exception);
            }
        });
    }

    private static String tokenFromPassword(String password) {
        String appKey = readAppKey(BaseApp.getSelf());
        boolean isDemo = "45c6af3c98409b18a84451215d0bdd6e".equals(appKey) || "fe416640c8e8a72734219e1847ad2547".equals(appKey);
        return isDemo ? MD5.getStringMD5(password) : password;
    }

    private static String readAppKey(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo != null) {
                return appInfo.metaData.getString("com.netease.nim.appKey");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
