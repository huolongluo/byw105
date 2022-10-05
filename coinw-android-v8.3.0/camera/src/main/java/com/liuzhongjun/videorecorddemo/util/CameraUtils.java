package com.liuzhongjun.videorecorddemo.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.liuzhongjun.videorecorddemo.activity.CustomRecordActivity;

public class CameraUtils {
    public static OnErrorListener onErrorListener;

    public static void toReZheng(Activity activity) {

    }

    /**
     * 跳转到初级认证
     *
     * @param activity
     */
    public static void toBaseReZheng(Activity activity) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(activity, "huolongluo.byw.byw.ui.activity.renzheng.RenZhengBeforeActivity"));
        activity.startActivity(intent);
    }

    /**
     * 高级认证
     *
     * @param activity
     */
    public static void toHighReZheng(Activity activity,OnErrorListener listener) {
        onErrorListener=listener;
        Intent intent = new Intent(activity, CustomRecordActivity.class);
        intent.setComponent(new ComponentName(activity, CustomRecordActivity.class));
        activity.startActivity(intent);
    }

    public static int sp2px(Context context, int spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    //将错误发送给umeng
    public static void report(String msg,String route){
        if(onErrorListener!=null){
            onErrorListener.report(msg,route);
        }
    }
}
