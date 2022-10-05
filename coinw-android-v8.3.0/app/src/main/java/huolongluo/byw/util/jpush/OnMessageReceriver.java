package huolongluo.byw.util.jpush;

import android.content.Context;
import android.util.Log;

import com.blankj.utilcodes.utils.ToastUtils;

import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.service.JPushMessageReceiver;

public class OnMessageReceriver extends JPushMessageReceiver {
    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onAliasOperatorResult(context, jPushMessage);
        Log.d(this.getClass().toString(), jPushMessage.toString());
    }

    @Override
    public void onRegister(Context context, String content) {
        super.onRegister(context, content);
        ToastUtils.showLongToast(content);
    }
}
