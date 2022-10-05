package huolongluo.byw.util;

/**
 * Created by 火龙裸 on 2018/5/9.
 */

public class Test
{
    private static String timea = "1525845600000";

    public static void main(String[] args)
    {
        System.out.println("==============" + DateUtils.formatDateTime(Long.parseLong(timea), "yyyy-MM-dd HH-mm-ss"));
        System.out.println("==============" + DateUtils.formatDateTime(Long.parseLong(timea), "HH:mm"));
    }
}
