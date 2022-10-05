package huolongluo.byw.util.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.home.activity.NewsWebviewActivity;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;

public class JPushServiceMessage extends BroadcastReceiver {
    private final String TAG = "JPushServiceMessage";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String title2 = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        String content = bundle.getString(JPushInterface.EXTRA_ALERT);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        String fileHtml = bundle.getString(JPushInterface.EXTRA_RICHPUSH_HTML_PATH);

//        if(!TextUtils.isEmpty(title2)&&title2.equals("币赢")){//屏蔽未知来源的通知
//
//            return;
//        }

        Logger.getInstance().error("onReceive", "--------------------------------------------------");
        Logger.getInstance().error("onReceive", "title : " + title);
        Logger.getInstance().error("onReceive", "title2 : " + title2);
        Logger.getInstance().error("onReceive", "message: " + message);
        Logger.getInstance().error("onReceive", "content: 测试有值 " + content);
        Logger.getInstance().error("onReceive", "extras: " + extras);
        Logger.getInstance().error("onReceive", "fileHtml: " + fileHtml);
        Logger.getInstance().error("onReceive", "intent.getAction(): " + intent.getAction());
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Logger.getInstance().debug(TAG, "[MyReceiver] 接收 Registration Id : " + regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Logger.getInstance().debug(TAG, "收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Logger.getInstance().debug(TAG, "收到了通知");
            // 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Logger.getInstance().debug(TAG, "用户点击打开了通知");

        } else {
            Logger.getInstance().debug(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

}

