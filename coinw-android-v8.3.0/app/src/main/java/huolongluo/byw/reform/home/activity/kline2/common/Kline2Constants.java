package huolongluo.byw.reform.home.activity.kline2.common;
import androidx.annotation.IntDef;

import com.android.legend.model.enumerate.Kline.KLineGranularity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;
//改版k线的常量类，区分于老的k线的常量类
public class Kline2Constants {
    public static final int ORDER_SIZE=20;//委托订单的数量
    public static final int TIME_SCREEN_SHOT=300;//截屏动画300ms
    /**
     * k线页面底部tab的位置类型
     */
    public static final int TYPE_ORDER=0;
    public static final int TYPE_LATEST=1;
    public static final int TYPE_WIKI=2;
    /**
     * K线页面的交易类型
     */
    public static final int TRADE_TYPE_COIN = 0;
    public static final int TRADE_TYPE_LEVER = 1;
    public static final int TRADE_TYPE_CONTRACT = 2;
    public static final int TRADE_TYPE_ETF = 3;

    @IntDef({Kline2Constants.TRADE_TYPE_COIN,
            Kline2Constants.TRADE_TYPE_LEVER,
            Kline2Constants.TRADE_TYPE_CONTRACT,
            Kline2Constants.TRADE_TYPE_ETF})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TradeType{}

    /**
     * K线图时间参数，数组的位置与UI相对应
     * 位置分别是 分时、15分、1小时、4小时、1天、1分、5分、30分钟、6小时、1周、1月
     * 分时和1分钟K线的时间会冲突，这里分时用0来代替
     */
    public static final int[] SELECT_TIME_ARRAY = {
            0,
            15 * 60,
            60 * 60,
            4 * 60 * 60,
            24 * 60 * 60,
            60,
            5 * 60,
            30 * 60,
            6 * 60 * 60,
            7 * 24 * 60 * 60,
            30 * 24 * 60 * 60
    };
    /**
     * 新系统使用枚举，将int的时间转为枚举字符串
     */
    public static final Map<Integer,String> SELECT_TIME_TO_GRANULARITY= new HashMap<Integer, String>(){
        {
            put(SELECT_TIME_ARRAY[5],KLineGranularity.MIN1.getValue());
            put(SELECT_TIME_ARRAY[6],KLineGranularity.MIN5.getValue());
            put(SELECT_TIME_ARRAY[1],KLineGranularity.MIN15.getValue());
            put(SELECT_TIME_ARRAY[7],KLineGranularity.MIN30.getValue());
            put(SELECT_TIME_ARRAY[2],KLineGranularity.HOUR1.getValue());
            put(SELECT_TIME_ARRAY[3],KLineGranularity.HOUR4.getValue());
            put(SELECT_TIME_ARRAY[8],KLineGranularity.HOUR6.getValue());
            put(SELECT_TIME_ARRAY[4],KLineGranularity.DAY1.getValue());
            put(SELECT_TIME_ARRAY[9],KLineGranularity.WEEK1.getValue());
            put(SELECT_TIME_ARRAY[10],KLineGranularity.MONTH1.getValue());
        }
    };
    /**
     * K线全屏时间参数，数组的位置与UI相对应
     * 位置分别是 分时、1分、5分、15分、30分钟、1小时、4小时、6小时、1天、1周、1月
     */
    public static final int[] FULLSCREEN_SELECT_TIME_ARRAY = {
            0,
            60,
            5 * 60,
            15 * 60,
            30 * 60,
            60 * 60,
            4 * 60 * 60,
            6 * 60 * 60,
            24 * 60 * 60,
            7 * 24 * 60 * 60,
            30 * 24 * 60 * 60
    };
}
