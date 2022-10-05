package com.android.coinw.biz.trade.helper;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;

import huolongluo.byw.BuildConfig;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.SPUtils;
public class DepthHelper {
    private static DepthHelper instance;
    private PopupWindow shenDPopupWindow;
    private View contentView;

    public interface IDepthCallback {
        void onOpen();
        void onClose();
        void onItemClick(View v);
    }

    public static DepthHelper getInstance() {
        if (instance == null) {
            instance = new DepthHelper();
        }
        return instance;
    }

    /**
     * 深度对话框
     * @param id
     * @param context
     * @param callback
     */
    public void showDepthPopwindow(String id, Context context, IDepthCallback callback) {
        if (TextUtils.isEmpty(id) || context == null) {
            //TODO处理异常情况
            return;
        }
        //07-08 20:00:50.182 E/AndroidRuntime(14439): FATAL EXCEPTION: main
        //07-08 20:00:50.182 E/AndroidRuntime(14439): Process: huolongluo.byw, PID: 14439
        //07-08 20:00:50.182 E/AndroidRuntime(14439): android.view.WindowManager$BadTokenException: Unable to add window -- token null for displayid = 0 is not valid; is your activity running?
        //07-08 20:00:50.182 E/AndroidRuntime(14439):     at android.view.ViewRootImpl.setView(ViewRootImpl.java:936)
        //07-08 20:00:50.182 E/AndroidRuntime(14439):     at android.view.WindowManagerGlobal.addView(WindowManagerGlobal.java:398)
        //07-08 20:00:50.182 E/AndroidRuntime(14439):     at android.view.WindowManagerImpl.addView(WindowManagerImpl.java:131)
        //07-08 20:00:50.182 E/AndroidRuntime(14439):     at android.widget.PopupWindow.invokePopup(PopupWindow.java:1454)
        //07-08 20:00:50.182 E/AndroidRuntime(14439):     at android.widget.PopupWindow.showAtLocation(PopupWindow.java:1216)
        //07-08 20:00:50.182 E/AndroidRuntime(14439):     at android.widget.PopupWindow.showAtLocation(PopupWindow.java:1183)
        //07-08 20:00:50.182 E/AndroidRuntime(14439):     at com.android.coinw.biz.trade.helper.DepthHelper.showDepthPopwindow(DepthHelper.java:93)
        try {
            if (shenDPopupWindow == null || contentView == null) {
                contentView = LayoutInflater.from(context).inflate(R.layout.trade_shendu_pup_list, null, false);
                shenDPopupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
                LinearLayout defaultLayout = contentView.findViewById(R.id.tv_4_0);
                LinearLayout depthLayout = contentView.findViewById(R.id.ll_depth);
                LinearLayout cancelLayout = contentView.findViewById(R.id.shendu_cancle_bn);
                defaultLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.onItemClick(v);
                        }
                        if (shenDPopupWindow != null) {
                            shenDPopupWindow.dismiss();
                        }
                    }
                });
                //设置深度数据
                setDepthData(id, depthLayout, context, callback);
                cancelLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (shenDPopupWindow != null) {
                            shenDPopupWindow.dismiss();
                        }
                    }
                });
                shenDPopupWindow.setOnDismissListener(() -> {
                    //关闭窗口
                    if (callback != null) {
                        callback.onClose();
                    }
                });
                shenDPopupWindow.setOutsideTouchable(true);
                shenDPopupWindow.setTouchable(true);
                shenDPopupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
            } else {
                //设置深度数据
                LinearLayout depthLayout = contentView.findViewById(R.id.ll_depth);
                depthLayout.removeAllViews();
                setDepthData(id, depthLayout, context, callback);
                shenDPopupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
            }
            if (callback != null) {//打开窗口
                callback.onOpen();
            }
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
    }
    private void setDepthData(String id, LinearLayout depthLayout, Context context, IDepthCallback callback) {
        String[] arrDepth = getDepthArr(id);
        if (TextUtils.isEmpty(id) || arrDepth==null || depthLayout == null) {
            //TODO 处理异常情况
            setDepthItemData(null, depthLayout, context, callback);
            return;
        }
        setDepthItemData(arrDepth, depthLayout, context, callback);
    }
    //数据精度已大到小排序
    public String[] getDepthArr(String pairId){
        String json =SPUtils.getString(BaseApp.getSelf(), AppConstants.COMMON.KEY_DEPTH_CONFIG, "");
        if(TextUtils.isEmpty(json)){
            return null;
        }
        Type type = new TypeToken<HashMap<String, String[]>>() {
        }.getType();
        HashMap<String, String[]> dataMap = GsonUtil.json2Obj(json, type);
        if(dataMap==null|| dataMap.isEmpty()){
            return null;
        }
        return dataMap.get(pairId);
    }

    private void setDepthItemData(String[] arrDepth, LinearLayout depthLayout, Context context, IDepthCallback callback) {
        if (arrDepth == null || arrDepth.length == 0) {
            //TODO 处理异常情况
            //由产品决定，如果为异常情况，则返回与原币币的深度切换数据一样
            arrDepth = new String[]{AppConstants.BIZ.DEFAULT_DEPTH, "0.01", "0.1"};
        }
        Arrays.sort(arrDepth);
        for (String depth : arrDepth) {
            if (TextUtils.isEmpty(depth)) {
                continue;
            }
            View rootView = LayoutInflater.from(context).inflate(R.layout.trade_shendu_pup_list_item, null, false);
            View itemView = rootView.findViewById(R.id.ll_depth_item);
            itemView.setTag(depth);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        callback.onItemClick(v);
                    }
                    if (shenDPopupWindow != null) {
                        shenDPopupWindow.dismiss();
                    }
                }
            });
            TextView depthTxt = rootView.findViewById(R.id.tv_depth);
            depthTxt.setText(depth);
            depthLayout.addView(rootView);
        }
    }
}

