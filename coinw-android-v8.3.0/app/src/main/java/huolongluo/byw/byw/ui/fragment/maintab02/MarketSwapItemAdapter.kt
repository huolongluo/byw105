package huolongluo.byw.byw.ui.fragment.maintab02

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.android.legend.extension.formatStringByDigits
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.legend.modular_contract_sdk.common.event.ChangeProductEvent
import com.legend.modular_contract_sdk.repository.model.Product
import huolongluo.byw.R
import huolongluo.byw.byw.ui.activity.main.MainActivity
import huolongluo.byw.io.AppConstants
import huolongluo.byw.util.MathHelper
import huolongluo.byw.util.pricing.PricingMethodUtil
import kotlinx.android.synthetic.main.market_swap_item.view.*
import org.greenrobot.eventbus.EventBus

/**
 * 行情合约列表适配器
 */
class MarketSwapItemAdapter(context: Context) : BaseQuickAdapter<Product, BaseViewHolder>(R.layout.market_swap_item) {

    override fun convert(holder: BaseViewHolder, product: Product) {
        if(product==null) return
        holder.itemView.run {
            tv_name.text=product.mBase.toUpperCase()
            tv_name1.text="/"+product.mQuote.toUpperCase()

            val close=product?.mLast
            val lastClose=product?.mOldPrice
            if(TextUtils.isEmpty(close)){
                tv_money.text=AppConstants.COMMON.DEFAULT_DISPLAY
                tv_money1.text=AppConstants.COMMON.DEFAULT_DISPLAY
            } else{
//                anim(close?.toDouble(),lastClose?.toDoubleOrNull(),holder)
                product.mOldPrice=close
                tv_money.text="$close"
                tv_money1.text="≈${PricingMethodUtil.getPricingUnit()}${
                    PricingMethodUtil.getResultByExchangeRate(close,AppConstants.COMMON.USDT)}"
            }

            val rateStr=product.mChangeRate
            if(TextUtils.isEmpty(rateStr)){
                tv_rate.text=AppConstants.COMMON.DEFAULT_DISPLAY
            }else{
                val rate=MathHelper.mul(rateStr, "100").formatStringByDigits(2).toDouble()
                if(rate>0){
                    tv_rate.text="+$rate%"
                    tv_rate.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.bg_cor5_up))
                }else{
                    tv_rate.text="$rate%"
                    tv_rate.setBackgroundResource(R.drawable.market_item_bg0)
                }
            }

            ll_main.setOnClickListener {
                EventBus.getDefault().post(ChangeProductEvent(product))
                MainActivity.self.gotoSwapForContractId(0)
            }
        }
    }

    private fun anim(close:Double?,lastClose:Double?, holder: BaseViewHolder) {
        if(close==null||lastClose==null||close-lastClose ==0.0) return
        holder.itemView.run {
            if (close-lastClose > 0) {
                animal_view.setBackgroundResource(R.drawable.rise_bg)
                val animation = animal_view.animation
                if (animation == null) {
                    val riseAnimation=AnimationUtils.loadAnimation(context, R.anim.rise_anim)
                    riseAnimation.interpolator = DecelerateInterpolator()
                    animal_view.animation = riseAnimation
                } else {
                    animal_view.animation.startNow()
                }
            } else{
                animal_view.setBackgroundResource(R.drawable.drop_bg)
                val animation = animal_view.animation
                if (animation == null) {
                    val riseAnimation=AnimationUtils.loadAnimation(context, R.anim.rise_anim)
                    riseAnimation.interpolator = DecelerateInterpolator()
                    animal_view.animation = riseAnimation
                } else {
                    animal_view.animation.startNow()
                }
            }
        }
    }
}