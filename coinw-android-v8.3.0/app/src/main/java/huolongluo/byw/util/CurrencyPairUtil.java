package huolongluo.byw.util;
import android.content.Context;
import android.util.ArrayMap;
import android.util.LongSparseArray;

import com.android.legend.model.config.CurrencyPairBean;
import com.android.legend.model.config.PairFeeBean;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import huolongluo.byw.io.AppConstants;
import huolongluo.byw.log.Logger;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.sp.SpUtils2;
import huolongluo.bywx.utils.DoubleUtils;
//本地管理币对列表数据，币对例子：BTC-USDT,都使用大写且使用“-”间隔
public class CurrencyPairUtil {
    private static final String TAG = "CurrencyPairUtil";
    public static final String SEPARATOR = "-";
    private static ArrayMap<String, CurrencyPairBean> nameMap = new ArrayMap<>();//通过币对名称返回币对数据
    private static LongSparseArray<CurrencyPairBean> idMap = new LongSparseArray<>();//通过id返回币对数据

    public static void initMap(Context context) {//使用本地数据初始化
        initMap(getSpData(context));
    }

    public static void initMap(List<CurrencyPairBean> list) {
        if (list == null || list.size() == 0) return;
        nameMap.clear();
        idMap.clear();
        for (CurrencyPairBean bean : list) {
            nameMap.put(bean.getTradeMappingName().toUpperCase(), bean);
            idMap.put(bean.getTradeMappingId(), bean);
        }
    }
    public static CurrencyPairBean getCurrencyPairByName(String pairName) {
        return nameMap.get(pairName.toUpperCase());
    }
    public static CurrencyPairBean getCurrencyPairById(long pairId) {
        return idMap.get(pairId);
    }
    //根据交易对名称获取id
    public static long getPairId(String pairName) {
        return getCurrencyPairByName(pairName) == null ? 0 : getCurrencyPairByName(pairName).getTradeMappingId();
    }
    //根据交易对id获取名称
    public static String getPairName(long pairId) {
        return getCurrencyPairById(pairId) == null ? "" : getCurrencyPairById(pairId).getTradeMappingName().toUpperCase();
    }
    //根据交易对id获取价格精度
    public static int getPricePreciousByName(String pairName){
        return getCurrencyPairByName(pairName) == null ? AppConstants.BIZ.DEFAULT_PRICE_PRECISION : getCurrencyPairByName(pairName).getPriceScale();
    }
    public static int getPricePreciousById(long pairId){
        int defaultScale = AppConstants.BIZ.DEFAULT_PRICE_PRECISION;
        if (pairId == 172){
            defaultScale = 6;
        }
        return getCurrencyPairById(pairId) == null ? defaultScale : getCurrencyPairById(pairId).getPriceScale();
    }
    public static int getQuantityPreciousByName(String pairName){
        return getCurrencyPairByName(pairName) == null ? AppConstants.BIZ.DEFAULT_QUANTITY_PRECISION : getCurrencyPairByName(pairName).getCountScale();
    }
    public static int getQuantityPreciousById(long pairId){
        return getCurrencyPairById(pairId) == null ? AppConstants.BIZ.DEFAULT_QUANTITY_PRECISION : getCurrencyPairById(pairId).getCountScale();
    }
    //通过id获取对应币种的手续费，计算了账户的等级
    public static double getFeeRateById(long pairId){
        CurrencyPairBean bean=getCurrencyPairById(pairId);
        if(bean==null){
            return AppConstants.BIZ.DEFAULT_TRADE_FEE;
        }else{
            try {
                if(UserInfoManager.isLogin()){//使用手续费大的
                    double maker= DoubleUtils.parseDouble(bean.getPairFees().get(UserInfoManager.getUserInfo().getVip()+"").getMakerFee());
                    double taker=DoubleUtils.parseDouble(bean.getPairFees().get(UserInfoManager.getUserInfo().getVip()+"").getTakerFee());
                    return maker>taker?maker:taker;
                }
                else{//没登录取最低等级vip1的费率
                    return DoubleUtils.parseDouble(bean.getPairFees().get("1").getMakerFee());
                }
            }catch (Exception e){
                return AppConstants.BIZ.DEFAULT_TRADE_FEE;
            }

        }

    }

    //获取左币名称
    public static String getCoinName(long pairId) {
        String pairName = getPairName(pairId);
        String[] strs = pairName.split(SEPARATOR);
        if (strs == null || strs.length < 2) {
            return "";
        }
        return strs[0];
    }

    //获取右币名称
    public static String getCnyName(long pairId) {
        String pairName = getPairName(pairId);
        String[] strs = pairName.split(SEPARATOR);
        if (strs == null || strs.length < 2) {
            return "";
        }
        return strs[1];
    }

    public static List<CurrencyPairBean> getSpData(Context context) {
        Type type = new TypeToken<List<CurrencyPairBean>>() {
        }.getType();
        Object object = SpUtils2.getObject(context, "CurrencyPairs", type);
        if (object == null) {
            return null;
        }
        return (List<CurrencyPairBean>) object;
    }

    public static void saveSpData(Context context, List<CurrencyPairBean> list) {
        Type type = new TypeToken<List<CurrencyPairBean>>() {
        }.getType();
        SpUtils2.saveObject(context, "CurrencyPairs", list, type);
    }
}
