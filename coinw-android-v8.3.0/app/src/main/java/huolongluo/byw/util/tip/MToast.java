package huolongluo.byw.util.tip;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;
import huolongluo.byw.R;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.Util;

/**
 * Created by Administrator on 2018/9/12 0012.
 */
public class MToast {
    private static final String TAG = "MToast";
//    private static Toast toast;

    public static void show(Context context, String message) {
        show(context,message,1);
    }
    public static void show(Context context, String message, int time) {
        Logger.getInstance().debug(TAG,"show message:"+message+" context:"+((Activity)context).getLocalClassName());
        if(TextUtils.isEmpty(message)){
            return;
        }
        if(!Util.isShowErrorMsg(message)){//停机维护屏蔽某些文案
            return;
        }
        if (!TextUtils.isEmpty(message) && message.contains(context.getString(R.string.qe93))) {
            Logger.getInstance().debug("MToast", "show", new Exception());
        }
        if (TextUtils.equals(context.getString(R.string.qe92), message)) {
            if (context instanceof Activity) {
                SnackBarUtils.ShowBlue((Activity) context, message);
                return;
            }
        }
        if (context != null && !TextUtils.isEmpty(message)) {
            Toast toast = Toast.makeText(context, message, time);
//            if (toast == null) {
//                toast = Toast.makeText(context, message, time);
//            } else {
//                toast.setText(message);
//                toast.setDuration(time);
//            }
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public static void showButton(Context context, String message, int time) {
        if (TextUtils.equals(context.getString(R.string.qe92), message)) {
            if (context instanceof Activity) {
                SnackBarUtils.ShowBlue((Activity) context, message);
                return;
            }
        }
        if (time == 1) {
            time = 0;
        } else if (time == 2) {
            time = 1;
        }
        if (context != null && !TextUtils.isEmpty(message)) {
            Toast.makeText(context, message, time).show();
        }
    }
}
