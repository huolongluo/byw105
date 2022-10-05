package huolongluo.byw.util;

/**
 * sloop 2019.1.15
 */
public class FastClickUtils {
    /**
     * 防止快速点击，传一个“两次点击间隔”时间即可
     *
     * 如果两次的时间大于点击的时间差，返回false
     */
    private static long lastClickTime;

    public static boolean isFastClick(int DoubleClickTime) {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= DoubleClickTime) {
            flag = false;
            lastClickTime = currentClickTime;
        }
        return flag;
    }
}
