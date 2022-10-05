package huolongluo.byw.util.jpush;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.GsonUtil;
public class JPushReceiver extends JPushMessageReceiver {
    private static final String TAG = "JPushReceiver";
    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onAliasOperatorResult(context, jPushMessage);
        Logger.getInstance().debug("onReceive", jPushMessage.toString());
    }

    @Override
    public void onRegister(Context context, String content) {
        super.onRegister(context, content);
        Logger.getInstance().debug("onReceive", content);
    }

    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        super.onMessage(context, customMessage);
        Logger.getInstance().debug("onReceive", customMessage.toString());
    }

    @Override
    public void onConnected(Context context, boolean b) {
        super.onConnected(context, b);
        Logger.getInstance().debug("onReceive", b + "");
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        try {
            String extras=notificationMessage.notificationExtras;
            PushEntity pushEntity = null;
            if (TextUtils.isEmpty(extras)) {
                toHome(context);
                return;
            }
            pushEntity = GsonUtil.json2Obj(extras, PushEntity.class);
            Logger.getInstance().debug(TAG, "pushEntity:"+GsonUtil.obj2Json(pushEntity,PushEntity.class));
            if (pushEntity == null) {
                toHome(context);
            }
            if (!TextUtils.isEmpty(pushEntity.getH5())) {
                toPage(context, pushEntity.getH5());
            } else if (!TextUtils.isEmpty(pushEntity.getApp())) {
                toPage(context, pushEntity.getApp(), pushEntity.getLeft(), pushEntity.getRight());
            }
        } catch (Exception e) {
            Logger.getInstance().debug(TAG, "error:"+e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param appPage 路径
     * @param left    左币名称，需要切换币对才有，否则为null
     * @param right   右币名称，需要切换币对才有，否则为null
     */
    private void toPage(Context context, String appPage, String left, String right) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(AppConstants.NOTIFICATION.KEY_NOTIFICATION, appPage);
        intent.putExtra(AppConstants.NOTIFICATION.KEY_LEFT, left);
        intent.putExtra(AppConstants.NOTIFICATION.KEY_RIGHT, right);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void toPage(Context context, String url) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(AppConstants.NOTIFICATION.KEY_URL, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void toHome(Context context) {
        Intent intents = new Intent(context, MainActivity.class);
        intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intents);
    }
}
