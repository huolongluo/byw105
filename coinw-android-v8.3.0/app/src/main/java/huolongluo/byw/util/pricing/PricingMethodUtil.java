package huolongluo.byw.util.pricing;

import android.content.Context;
import android.text.TextUtils;
import com.android.coinw.biz.event.BizEvent;
import org.greenrobot.eventbus.EventBus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.log.Logger;
import huolongluo.byw.reform.bean.ExchangeRate;
import huolongluo.byw.util.Constant;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.LogicLanguage;
import huolongluo.byw.util.MathHelper;
import huolongluo.byw.util.NumberUtil;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.noru.NorUtils;

public class PricingMethodUtil {
    private static final String TAG = "PricingMethodUtil";
    public static final int PRICING_RMB=1;
    public static final int PRICING_DOLLAR=2;
    public static final int PRICING_WON=3;

    private static int PRICING_METHOD=-1;
    public static String EXCHANGE_RATE_HY="";//该值为空说明读取汇率接口失败了
    //记录汇率的map key格式为 dstType_srcType  例： cny转usd  srcType为cny  dstType为usd  key则为  usd_cny
    //因为计价方式是cnyt转为usd  srcType很可能不同，所以srcType放在前面，保证contain操作匹配
    private static LikeHashMap<String,String> mapExchangeRate=new LikeHashMap<>();

    public static int getPricingMethod(){
        if (PRICING_METHOD==-1){
            PRICING_METHOD=SPUtils.getInt(BaseApp.getSelf(),Constant.KEY_PRICING_METHOD,PRICING_DOLLAR);
        }
        return PRICING_METHOD;
    }

    public static void setPricingMethod(int method){
        if(PRICING_METHOD==method){//本次未修改
            return;
        }
        PRICING_METHOD=method;
        SPUtils.saveInt(BaseApp.getSelf(), Constant.KEY_PRICING_METHOD,method);
        EventBus.getDefault().post(new BizEvent.ChangeExchangeRate());//切换了计价方式需要重新刷新合约的汇率并重新计算估值刷新页面

    }

    public static String getPricingName(){
        String name="";
        int pricingMethod=getPricingMethod();
        if(pricingMethod==PRICING_RMB){
            name=BaseApp.getSelf().getResources().getString(R.string.pricing_rmb);
        }
        else if(pricingMethod==PRICING_DOLLAR){
            name=BaseApp.getSelf().getResources().getString(R.string.pricing_dollar);
        }else if(pricingMethod==PRICING_WON){
            name=BaseApp.getSelf().getResources().getString(R.string.pricing_won);
        }
        return name;
    }

    public static String getPricingName(Context context){
        String name="";
        int pricingMethod=getPricingMethod();
        if(pricingMethod==PRICING_RMB){
            name=context.getResources().getString(R.string.pricing_rmb);
        }
        else if(pricingMethod==PRICING_DOLLAR){
            name=context.getResources().getString(R.string.pricing_dollar);
        }else if(pricingMethod==PRICING_WON){
            name=context.getResources().getString(R.string.pricing_won);
        }
        return name;
    }

    /**
     * 获取接口header需要传的selectType
     * @return
     */
    public static String getPricingSelectType(){
        String selectType="";
        int pricingMethod=getPricingMethod();
        if(pricingMethod==PRICING_RMB){
            selectType="CNY";
        }
        else if(pricingMethod==PRICING_DOLLAR){
            selectType="USD";
        }else if(pricingMethod==PRICING_WON){
            selectType="KRW";
        }

        return selectType;
    }

    /**
     * 获取计价方式对应的单位
     * @return
     */
    public static String getPricingUnit(){
        String unit="";
        int pricingMethod=getPricingMethod();
        if(pricingMethod==PRICING_RMB){
            unit="¥";
        }else if(pricingMethod==PRICING_DOLLAR){
            unit="$";
        }else if(pricingMethod==PRICING_WON){
            unit="₩";
        }

        return unit;
    }

    /**
     * 通过汇率的列表初始化汇率的map
     * @param list
     */
    public static void initMap(List<ExchangeRate> list){
        Logger.getInstance().debug(TAG,"initMap list:"+ GsonUtil.obj2Json(list,List.class)+" class:"+list.getClass());
        if(list==null||list.size()==0){
            return;
        }
        mapExchangeRate.clear();
        for (int i = 0; i < list.size(); i++) {
            ExchangeRate rate=list.get(i);
            mapExchangeRate.put(rate.getDstType()+"_"+rate.getSrcType(),rate.getRate());
        }
    }
    /**
     * 获取汇率计算后的结果数据
     * @param price 待计算的价格
     * @param srcType 币对右币，即币自身的计价方式
     * @param place 精度
     * @return
     */
    public static String getResultByExchangeRate(String price,String srcType,int place){
        if(TextUtils.isEmpty(price)||TextUtils.isEmpty(srcType)||mapExchangeRate==null){
            return "--";
        }

        if(NumberUtil.toDouble(price)<=0){//如果price为0不需要计算汇率
            return NorUtils.NumberFormat(place).format(0.0);
        }

        String exchangeRate=null;
        if(srcType.toUpperCase().contains(getPricingSelectType().toUpperCase())){//src和dst相同
            exchangeRate="1";
        }else{
            exchangeRate=mapExchangeRate.get(getPricingSelectType()+"_"+srcType);
        }

        if(TextUtils.isEmpty(exchangeRate)){//汇率未找到
            return "--";
        }

        return NorUtils.NumberFormat(place).format(MathHelper.mul(price,exchangeRate));
    }
    public static String getResultByExchangeRate(String price,String srcType){
        if(TextUtils.isEmpty(price)||TextUtils.isEmpty(srcType)){
            return "--";
        }
        return getResultByExchangeRate(price,srcType, getHyPrecision(price));//默认使用合约统一的精度
    }
    /**
     * 设置合约的汇率，合约独立的
     * @param list
     */
    public static void setHyExchangeRate(List<ExchangeRate> list){
        if(list==null){
            return;
        }
        if(getPricingSelectType().equals("USD")){//相同则是1
            EXCHANGE_RATE_HY="1";
            return;
        }
        for(ExchangeRate rate : list){
            if(rate.getSrcType().equals("USD")&&rate.getDstType().equals(getPricingSelectType())){
                EXCHANGE_RATE_HY=rate.getRate();
                return;
            }
        }
        EXCHANGE_RATE_HY="-1";//-1代表汇率列表未找到需要显示--

    }
    // 默认保留2位小数
    public static String getLargePrice( String price){
        return getLargePrice(price,2);
    }
    /**
     * 当价格数量较大使用该方法显示
     */
    public static String getLargePrice(String price,int place){
        String result="";
        try {
            BigDecimal priceDecimal=new BigDecimal(price);
            if (priceDecimal.compareTo(new BigDecimal(1000000000d))>=0) {//10亿
                result = NorUtils.NumberFormat(place, RoundingMode.DOWN).format(MathHelper.div(NumberUtil.toDouble(price),1000000000d)) + "B";
            } else if (priceDecimal.compareTo(new BigDecimal(1000000d))>=0) {//百万
                result = NorUtils.NumberFormat(place, RoundingMode.DOWN).format(MathHelper.div(NumberUtil.toDouble(price),1000000d)) + "M";
            }
            else if (priceDecimal.compareTo(new BigDecimal(1000d))>=0) {
                result = NorUtils.NumberFormat(place, RoundingMode.DOWN).format(MathHelper.div(NumberUtil.toDouble(price),1000d)) + "K";
            } else {
                result=NorUtils.NumberFormat(place).format(priceDecimal);
            }
        }catch (Exception e){
            result="--";
        }

        return result;
    }
    /**
     * 合约是自己通过汇率计算，精度遵循大于1保留2位，小于1保留6位，若遇见0.00000001则保留8位
     * @return
     */
    public static int getHyPrecision(String price){
        int precision=6;
        if(NumberUtil.toDouble(price)>=1){
            precision=2;
        }else{
            int dotNum=getPrecision(price+"");
            if(dotNum>=7&&NumberUtil.toDouble(price)<=0.00000099){
                precision=8;
            }
        }
        return precision;
    }
    /**
     * 获取price的小数点后的位数（通用）
     * @param price
     * @return
     */
    public static int getPrecision(String price){
        int precision=9;
        if(TextUtils.isEmpty(price)||NumberUtil.toDouble(price)==0){
            return precision;
        }
        int num=price.indexOf(".");
        if(num==-1){
            return precision;
        }
        return price.length()-num-1;
    }
}
