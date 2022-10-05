package com.liuzhongjun.videorecorddemo.util;

public class ConvertSecondUtil {
    /**
     * 一小时的秒数
     */
    private static final int HOUR_SECOND = 60 * 60;

    /**
     * 一分钟的秒数
     */
    private static final int MINUTE_SECOND = 60;

    /**
     * 时分秒格式00:00:00转换秒数
     *
     * @param time //时分秒格式00:00:00
     * @return 秒数
     */
    public static int getSecond(String time) {
        int s = 0;
        if (time.length() == 8) { //时分秒格式00:00:00
            int index1 = time.indexOf(":");
            int index2 = time.indexOf(":", index1 + 1);
            s = Integer.parseInt(time.substring(0, index1)) * 3600;//小时
            s += Integer.parseInt(time.substring(index1 + 1, index2)) * 60;//分钟
            s += Integer.parseInt(time.substring(index2 + 1));//秒
        }
        if (time.length() == 5) {//分秒格式00:00
            s = Integer.parseInt(time.substring(time.length() - 2)); //秒  后两位肯定是秒
            s += Integer.parseInt(time.substring(0, 2)) * 60;    //分钟
        }
        return s;
    }

    /**
     * 根据秒数获取时间串
     *
     * @param second (eg: 100s)
     * @return (eg : 00 : 01 : 40)
     */
    public static String getTimeStrBySecond(int second) {
        if (second <= 0) {
            return "00:00:00";
        }
        StringBuilder sb = new StringBuilder();
        int hours = second / HOUR_SECOND;
        if (hours > 0) {

            second -= hours * HOUR_SECOND;
        }
        int minutes = second / MINUTE_SECOND;
        if (minutes > 0) {

            second -= minutes * MINUTE_SECOND;
        }
        return (hours >= 10 ? (hours + "")
                : ("0" + hours) + ":" + (minutes >= 10 ? (minutes + "") : ("0" + minutes)) + ":"
                + (second >= 10 ? (second + "") : ("0" + second)));
    }
}
