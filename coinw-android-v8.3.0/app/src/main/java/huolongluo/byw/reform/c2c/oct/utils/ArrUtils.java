package huolongluo.byw.reform.c2c.oct.utils;

import java.util.List;

public class ArrUtils {
    public static boolean listContains(List<Integer> list1, List<Integer> list2) {
        for (int x : list1) {
            if (list2.contains(x)) {
                return true;
            }
        }
        return false;
    }
}
