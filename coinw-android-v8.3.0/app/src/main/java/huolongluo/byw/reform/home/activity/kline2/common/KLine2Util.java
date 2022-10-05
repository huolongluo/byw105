package huolongluo.byw.reform.home.activity.kline2.common;
import android.text.TextUtils;
//k线改版后的工具类
public class KLine2Util {
    private static final String TAG = "KLine2Util";
    /**
     * 获取自选本地的key
     * @return
     */
    public static String getSelfSpKey(String coinName,String cnyName,@Kline2Constants.TradeType int tradeType){
        //java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String java.lang.String.toLowerCase()' on a null object reference
        //	at i.a.j.d.a.a.b.a.a(KLine2Util.java:1)
        return (TextUtils.isEmpty(coinName)?"":coinName.toLowerCase())+(TextUtils.isEmpty(cnyName)?"":cnyName.toLowerCase())+"_"+tradeType;
    }

}
