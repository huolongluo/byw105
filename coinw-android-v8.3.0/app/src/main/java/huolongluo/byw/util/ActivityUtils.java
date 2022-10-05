package huolongluo.byw.util;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.text.TextUtils;
import android.content.Context;

import java.util.List;
public class ActivityUtils {
    /**
     * 判断某个界面是否在前台
     * @param activity 要判断的Activity
     * @return 是否在前台显示
     */
    public static boolean isForeground(Activity activity) {
        return isForeground(activity, activity.getClass().getName());
    }

    /**
     * 判断某个界面是否在前台
     * @param context Context
     * @param className 界面的类名
     * @return 是否在前台显示
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) return false;
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
            if (list != null && list.size() > 0) {
                ComponentName cpn = list.get(0).topActivity;
                if (className.equals(cpn.getClassName())) return true;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }
}
