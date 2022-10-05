package huolongluo.byw.reform.home.activity;

import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.android.coinw.biz.event.BizEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import huolongluo.byw.log.Logger;
import huolongluo.byw.model.AliManMachineEntity;

/**
 * 阿里人机验证的js回调
 */
public class JSCallAlManMachineJavaInterface {
    private static final String TAG = "JSCallAlManMachineJavaI";

    public static final int TYPE_LOGIN = 1;
    public static final int TYPE_RESET_PWD = 2;
    public static final int TYPE_REGISTER = 3;
    private int type;

    public JSCallAlManMachineJavaInterface(int type) {
        this.type = type;
    }

    @JavascriptInterface
    public void callJava(String msg) {
        Logger.getInstance().debug(TAG, "回调msg: " + msg);
        try {
            JSONObject json = new JSONObject(msg);
            String callName = json.getString("callName");
            if (callName.equals("behaviorVerifyResult")) {
                if (!TextUtils.isEmpty(json.getString("sessionId"))) {//人机验证成功
                    //成功
                    AliManMachineEntity entity = new AliManMachineEntity(json.getString("sessionId"),
                            json.getString("sig"), json.getString("token"), json.getString("scene"));
                    EventBus.getDefault().post(new BizEvent.AliManMachine(type, entity));
                }
            } else {
                String code = json.getString("code");
                EventBus.getDefault().post(new BizEvent.Aliverify(callName, code, ""));
                //上报错误日志
                String reportMsg = "aliverify_" + msg;
                Logger.getInstance().report(reportMsg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
