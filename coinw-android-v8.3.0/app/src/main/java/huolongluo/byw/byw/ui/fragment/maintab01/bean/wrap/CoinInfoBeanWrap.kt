package huolongluo.byw.byw.ui.fragment.maintab01.bean.wrap

import android.text.TextUtils
import com.legend.modular_contract_sdk.utils.getDouble
import com.legend.modular_contract_sdk.utils.getNum
import huolongluo.byw.byw.base.BaseApp
import huolongluo.byw.byw.ui.fragment.maintab01.bean.CoinInfoBean
import huolongluo.byw.util.Util
import huolongluo.byw.util.pricing.PricingMethodUtil

class CoinInfoBeanWrap(val coinInfoBean: CoinInfoBean?) {
    init {
//        CoinInfoBean bean1 = list.get(position);
//        ViewGroup.LayoutParams layoutParams = holder.ll1.getLayoutParams();
//        layoutParams.width = this.width;
//        holder.ll1.setLayoutParams(layoutParams);
//        double ratiod= MathHelper.mul(100.0, DoubleUtils.parseDouble(bean1.getPriceRaiseRate()));
//        String ratio = NorUtils.NumberFormat(2).format(ratiod);
//        if (DoubleUtils.parseDouble(bean1.getPriceRaiseRate()) >= 0) {
//            holder.coinPrice_tv1.setTextColor(SkinCompatResources.getColorStateList(context,R.color.up));
//            holder.coinRange_tv1.setTextColor(SkinCompatResources.getColorStateList(context,R.color.up));
//            holder.coinRange_tv1.setText("+" + ratio + "%");
//        } else {
//            holder.coinPrice_tv1.setTextColor(SkinCompatResources.getColorStateList(context,R.color.drop));
//            holder.coinRange_tv1.setTextColor(SkinCompatResources.getColorStateList(context,R.color.drop));
//            holder.coinRange_tv1.setText(ratio + "%");
//        }
//        holder.coinName_tv1.setText(bean1.getCoinName() + "/");
//        holder.coinName_tv1_1.setText(bean1.getCnyName());
//        holder.coinPrice_tv1.setText(bean1.getLatestDealPrice());
//        Logger.getInstance().debug("HomeAdapter2","position:"+position+"  coinName:"+bean1.getCoinName()+" unit:"+PricingMethodUtil.getPricingUnit());
//        if(TextUtils.isEmpty(bean1.getLatestDealPrice())){
//            holder.tvPricingExchange.setText("≈"+ PricingMethodUtil.getPricingUnit() +"--");
//        }else{
//            holder.tvPricingExchange.setText("≈"+ PricingMethodUtil.getPricingUnit() +PricingMethodUtil.getResultByExchangeRate(bean1.getLatestDealPrice(),
//                    bean1.getCnyName()));
//        }
//        holder.ll1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (onItemClockListener != null) {
//                    onItemClockListener.onItemClockListener(position);
//                }
//            }
//        });

    }

    fun getProductName() = coinInfoBean?.getCoinName() + "/" + coinInfoBean?.getCnyName()


    fun getChange(): String {
        return (coinInfoBean?.getPriceRaiseRate().getDouble() * 100).toString().getNum(2, true, withSymbol = true) + "%"
    }

    fun getLast() :String{
        return coinInfoBean?.latestDealPrice?:""
    }
    fun getTextSize() :Int{
        val last=getLast()
        return if(last.length>=10) Util.sp2px(12) else Util.sp2px(18)
    }
    fun getMarginTop():Int{
        val last=getLast()
        return if(last.length>=10) Util.dp2px(BaseApp.getSelf(),12.0f) else Util.dp2px(BaseApp.getSelf(),6.0f)
    }

    // 折合法比
    fun getConvertCurrency(): String {
        return if (TextUtils.isEmpty(coinInfoBean?.latestDealPrice)) {
            "≈ ${PricingMethodUtil.getPricingUnit()} --"
        } else {
            val result = PricingMethodUtil.getResultByExchangeRate(coinInfoBean?.latestDealPrice, coinInfoBean?.getCnyName())
            "≈ ${PricingMethodUtil.getPricingUnit()} $result"
        }
    }

    fun isUp() = coinInfoBean?.getPriceRaiseRate().getDouble() >= 0

}