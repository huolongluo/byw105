package com.legend.modular_contract_sdk.repository.model.wrap

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.legend.common.util.ThemeUtil
import com.legend.modular_contract_sdk.BR
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.component.market_listener.MarketData
import com.legend.modular_contract_sdk.repository.model.ContractAssetInfo
import com.legend.modular_contract_sdk.repository.model.PositionAndOrder
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.ui.contract.*
import com.legend.modular_contract_sdk.utils.*

class PositionWrap(val position: PositionAndOrder) : BaseObservable() {

    constructor(position: PositionAndOrder, product: Product) : this(position) {
        this.product = product
    }

    var markPrice: Double? = null
        set(vaule) {
            field = vaule
            notifyPropertyChanged(BR.profit)
            notifyPropertyChanged(BR.profitRate)
            notifyPropertyChanged(BR.riskFormat)
            notifyPropertyChanged(BR.liquidationPrice)
        }
        get() = field

    var risk: Double = 0.0
        set(vaule) {
            field = vaule
            notifyPropertyChanged(BR.riskFormat)
        }
        get() = field

    var product: Product? = null
    var usableBalances: ContractAssetInfo? = null
    var tickerMap: MutableMap<String, MarketData>? = null

    // 仓位是否有收益
    var haveProfit: Boolean = false

    fun haveProfit(isHistoryOrder:Boolean) : Boolean{
        return if (isHistoryOrder){
            realProfitOfHistoryOrder > 0
        } else {
            haveProfit
        }
    }

    fun getContractName() = position.mInstrument.toUpperCase() + "/USDT"

//    fun getLeverage() = "${position.mLeverage}倍杠杆"

    fun getMargin() = position.mMargin.toString().getNum()

    fun getOpen() = position.mOpenPrice.toString().getNum()

    fun getClosePrice() = position.mClosePrice.toString().getNum()

    fun getDealPrice(): String {
        return if (isOpenOrder()) {
            position.mOpenPrice.toString().getNum()
        } else {
            position.mClosePrice.toString().getNum()
        }
    }

    fun getEntrustPrice(context: Context) :String{
        if (position.mOriginalType == PositionType.EXECUTE.type ||
                position.mOriginalType == PositionType.PLAN_TRIGGER.type && position.mTriggerType == TriggerType.EXECUTE.type) {
            return context.getString(R.string.mc_sdk_contract_market_price)
        }
        return position.mOrderPrice.toString().getNum()
    }

    fun getMoveTPSLTriggerPrice(context: Context):String{
        if (position.mTriggerPrice <= 0){
           return context.getString(R.string.mc_sdk_contract_market_price)
        }
        return position.mTriggerPrice.toString().getNum()
    }

    fun isOpenOrder() = position.mStatus == OpenPositionType.OPEN.type

    fun isMoveTPSLOrder() = position.mCallbackRate > 0

    // 移动止盈止损回调率
    fun getCallbackRate(): String {
        return (position.mCallbackRate * 100).toString().getNum(2, withZero = true) + "%"
    }

    fun getFee() = position.mFee.toString().getNum()

    fun getLast() = markPrice.toString().getNum(if (product != null) product!!.mPricePrecision else McConstants.COMMON.CURRENT_PAIR_PRECISION)

    fun getForceClosePrice(): String {
        val direction: Direction =
                if (position.mDirection == Direction.LONG.direction) Direction.LONG else Direction.SHORT
        return CalculateUtil.getPositionLiquidationPrice(direction, position.mOpenPrice, position.mLeverage).toString()
                .getNum(2)
    }


    fun getHoldPosition(quantityUnit: Int = QuantityUnit.SIZE.unit):String{

        return if (quantityUnit == QuantityUnit.SIZE.unit){
            position.mCurrentPiece.toString().getNum(0)
        } else if (quantityUnit == QuantityUnit.USDT.unit){

            val price = if (isOpenOrder()) {
                position.mOpenPrice
            } else {
                position.mClosePrice
            }
            (price * position.mBaseSize).toString().getNum(4)

        } else if (quantityUnit == QuantityUnit.COIN.unit){
            position.mBaseSize.toString().getNum(product?.mOneLotSize?.getPrecision() ?: 6)
        }

        else {
            ""
        }
    }

    fun getEntrustCount(quantityUnit: Int = QuantityUnit.SIZE.unit): String{
        return if (quantityUnit == QuantityUnit.SIZE.unit){
            position.mCurrentPiece.toString().getNum(0)
        } else if (quantityUnit == QuantityUnit.USDT.unit){

            val price = if (isOpenOrder()) {
                position.mOpenPrice
            } else {
                position.mOrderPrice
            }
            (price * position.mBaseSize).toString().getNum(4)

        } else if (quantityUnit == QuantityUnit.COIN.unit){
            position.mBaseSize.toString().getNum(product?.mOneLotSize?.getPrecision() ?: 6)
        }

        else {
            ""
        }
    }

    fun getCanClosePosition(quantityUnit: Int = QuantityUnit.SIZE.unit):String{

        return if (quantityUnit == QuantityUnit.SIZE.unit){
            position.mRemainCurrentPiece.toString().getNum(0)
        } else if (quantityUnit == QuantityUnit.USDT.unit){

            if(product == null){
               return "0"
            }

            val price = if (isOpenOrder()) {
                position.mOpenPrice
            } else {
                position.mClosePrice
            }
            (price * (product!!.mOneLotSize) * position.mRemainCurrentPiece).toString().getNum(4)

        } else if (quantityUnit == QuantityUnit.COIN.unit){

            if(product == null){
                return "0"
            }

            ((product!!.mOneLotSize) * position.mRemainCurrentPiece).toString().getNum(product?.mOneLotSize?.getPrecision() ?: 6)
        }

        else {
            ""
        }
    }

    fun getTradeUnitStr(unit: QuantityUnit, context: Context):String{
        val unit = when(unit){
            QuantityUnit.SIZE -> {
                context.getString(R.string.mc_sdk_contract_unit)
            }
            QuantityUnit.USDT -> {
                context.getString(R.string.mc_sdk_usdt)
            }
            QuantityUnit.COIN -> {
                position.mInstrument.toUpperCase()
            }
        }
        return unit
    }

    fun isExpGold() = position.mIsExperienceGold

    fun getPositionModeAndLeverage(context: Context): String {
        return if (position.mPositionModel == PositionMode.FULL.mode) {
            context.getString(R.string.mc_sdk_contract_order_type_full_and_leverage, " ${position.mLeverage}X")
        } else {
            context.getString(R.string.mc_sdk_contract_order_type_part_and_leverage, " ${position.mLeverage}X")
        }
    }

    // 风险率
    @Bindable
    fun getRiskFormat(): String {

        if (position.mPositionModel == PositionMode.FULL.mode) {
//            mAvailable?.let {
//                val risk = CalculateUtil.getRiskRate(
//                    mAvailable!!,
//                    getRealProfit(),
//                    mTotalFee!!,
//                    mTotalFundingFee!!,
//                    mTotalMargin!!
//                ) * 100
//                return NumberStringUtil.formatAmount(risk, 2, NumberStringUtil.AmountStyle.FillZeroNoComma) + "%"
//            }
            return NumberStringUtil.formatAmount(risk, 2, NumberStringUtil.AmountStyle.FillZeroNoComma) + "%"
        } else {
            return "- -"
        }

        return "- -"
    }

    // 预计止盈
    @Bindable
    fun getTakeProfit(): String {
        if (position.mStopProfitPrice <= 0) {
            return "- -"
        }
        return position.mStopProfitPrice.toString().getNum(McConstants.COMMON.CURRENT_PAIR_PRECISION)
    }

    // 预计止损
    @Bindable
    fun getStopLoss(): String {
        if (position.mStopLossPrice <= 0) {
            return "- -"
        }
        return position.mStopLossPrice.toString().getNum(McConstants.COMMON.CURRENT_PAIR_PRECISION)
    }

    @Bindable
    fun getTakeProfitStopLoss():String{
        return getTakeProfit()+"/"+getStopLoss()
    }

    @Bindable
    fun getLiquidationPrice(): String {
        if (position.mPositionModel == PositionMode.FULL.mode) {
            if (usableBalances != null && tickerMap != null && product != null) {
                return CalculateUtil.getFullPositionLiquidationPrice(usableBalances!!, tickerMap!!, this, product!!)
            }
            return "- -"
        } else {
            product?.let {
                return CalculateUtil.getPartPositionLiquidationPrice(this, it)
            }
            return "- -"
        }

    }

    fun getCreateDate() = position.mCreatedDate.getDate()

    fun getUpdatedDate() = position.mUpdatedDate.getDate()

    fun getDirection() = run {
        if (position.mDirection == Direction.LONG.direction) {
            if (position.mPositionModel == PositionMode.FULL.mode) {
                R.string.mc_sdk_full_long
            } else {
                R.string.mc_sdk_part_long
            }
        } else {
            if (position.mPositionModel == PositionMode.FULL.mode) {
                R.string.mc_sdk_full_short
            } else {
                R.string.mc_sdk_part_short
            }
        }
    }

    fun getDirectionShare() = run {
        if (position.mDirection == Direction.LONG.direction) {
            R.string.mc_sdk_contract_long
        } else {
            R.string.mc_sdk_contract_short
        }
    }

    fun getDirectionIsLong() = position.mDirection == Direction.LONG.direction

    fun getDirectionBackgroundByHistory(context: Context): Drawable? = run {
        if (isOpenOrder()){
            if (position.mDirection == Direction.LONG.direction) {
                ThemeUtil.getThemeDrawable(context, R.attr.bg_buy_btn)
            } else {
                ThemeUtil.getThemeDrawable(context, R.attr.bg_sell_btn)
            }
        } else {
            if (position.mDirection == Direction.LONG.direction) {
                ThemeUtil.getThemeDrawable(context, R.attr.bg_sell_btn)
            } else {
                ThemeUtil.getThemeDrawable(context, R.attr.bg_buy_btn)
            }
        }


    }

    fun isLong() = position.mDirection == Direction.LONG.direction

    // 展示的浮动盈亏=SUM(真实的浮动盈亏)-总的过夜费totalFundingFee盈亏
    @Bindable
    fun getProfit(): String {
        return getRealProfit().apply {
            haveProfit = this > 0
            notifyChange()
        }.toString().getNum(2, withSymbol = true)
    }

    fun getRealProfit(): Double {
        markPrice?.let { markPrice ->
            val direction: Direction =
                    if (position.mDirection == Direction.LONG.direction) Direction.LONG else Direction.SHORT
            return CalculateUtil.getPositionProfitAndLoss(
                    direction,
                    position.mOpenPrice,
                    markPrice,
                    position.mBaseSize
            )
        }
        return 0.0
    }

    @Bindable
    fun getProfitRate(): String {
        markPrice?.let { markPrice ->
            val direction: Direction =
                    if (position.mDirection == Direction.LONG.direction) Direction.LONG else Direction.SHORT
            return (CalculateUtil.getPositionProfitAndLoss(
                    direction,
                    position.mOpenPrice,
                    markPrice,
                    position.mBaseSize
            ) / position.mMargin * 100).toString().getNum(2, withSymbol = true) + "%"
        }
        return "- -"
    }


    fun getProfitRateNoPercent(): String {
        markPrice?.let { markPrice ->
            val direction: Direction =
                    if (position.mDirection == Direction.LONG.direction) Direction.LONG else Direction.SHORT
            return (CalculateUtil.getPositionProfitAndLoss(
                    direction,
                    position.mOpenPrice,
                    markPrice,
                    position.mBaseSize
            ) / position.mMargin * 100).toString().getNum(2, withSymbol = true)
        }
        return "- -"
    }

    @Bindable
    fun isHaveProfit(): Boolean = haveProfit


    val stopOriginProfitPrice = position.mStopProfitPrice.toString().getNum()
    val stopOriginProfitRate = (position.mStopProfitRate * 100.0).toString().getNum(2)
    val stopOriginLossPrice = position.mStopLossPrice.toString().getNum()
    val stopOriginLossRate = (position.mStopLossRate * 100.0).toString().getNum(2)

    var stopProfitPrice: String = run {
        if (position.mStopProfitPrice <= 0) {
            ""
        } else {
            val scale = if (product != null) product!!.mPricePrecision else 1
            position.mStopProfitPrice.toString().getNum(scale)
        }
    }
        set(value) {
            field = value
            notifyChange()
        }
    var stopProfitRate: String = (position.mStopProfitRate * 100.0).toString().getNum(2)
        set(value) {
            field = value
            notifyChange()
        }
    var stopLossPrice: String = run {
        if (position.mStopLossPrice <= 0) {
            ""
        } else {
            val scale = if (product != null) product!!.mPricePrecision else 1
            position.mStopLossPrice.toString().getNum(scale)
        }
    }
        set(value) {
            field = value
            notifyChange()
        }
    var stopLossRate: String = (position.mStopLossRate * 100.0).toString().getNum(2)
        set(value) {
            field = value
            notifyChange()
        }


    fun takeProfitDesc(context: Context?, type: TriggerSetType): String {
        var stopProfitPrice = ""
        var stopProfitRate = ""
        var profitAmount = "" // 收益额 USDT

        if (type == TriggerSetType.PRICE && this.stopProfitPrice.isNullOrEmpty() || type == TriggerSetType.PERCENT && this.stopProfitRate.isNullOrEmpty()) {
            return context!!.resources.getString(R.string.mc_sdk_stop_profit_predict, "- -", "- -")
        }

        if (position.mDirection == Direction.LONG.direction) {
            //止盈价 = 开仓价(1+止盈率/杠杆)
            stopProfitPrice = if (type == TriggerSetType.PRICE) {
                this.stopProfitPrice
            } else {
                (position.mOpenPrice * (1 + this.stopProfitRate.getDouble() / 100 / position.mLeverage)).toString()
                        .getNum(McConstants.COMMON.CURRENT_PAIR_PRECISION)
            }

            //止盈率 =杠杆(止盈价/开仓价-1)
            stopProfitRate = if (type == TriggerSetType.PRICE) {
                (position.mLeverage * (this.stopProfitPrice.getDouble() / position.mOpenPrice - 1)).toString()
            } else {
                (this.stopProfitRate.getDouble() / 100).toString().getNum(2)
            }

        } else {

            //止盈价 = 开仓价(1-止盈率/杠杆)
            //止盈率 =杠杆(1-止盈价/开仓价)
            stopProfitPrice = if (type == TriggerSetType.PRICE) {
                this.stopProfitPrice
            } else {
                (position.mOpenPrice * (1 - this.stopProfitRate.getDouble() / 100 / position.mLeverage)).toString()
                        .getNum(McConstants.COMMON.CURRENT_PAIR_PRECISION)
            }

            stopProfitRate = if (type == TriggerSetType.PRICE) {
                (position.mLeverage * (1 - this.stopProfitPrice.getDouble() / position.mOpenPrice)).toString()
            } else {
                (this.stopProfitRate.getDouble() / 100).toString().getNum(2)
            }
        }

        profitAmount =
                (position.mMargin * stopProfitRate.toDouble()).toString().getNum(2) + if (type == TriggerSetType.PRICE) {
                    "(${(stopProfitRate.getDouble() * 100).toString().getNum(2)}%)"
                } else ""

        return context!!.resources.getString(R.string.mc_sdk_stop_profit_predict, stopProfitPrice, profitAmount)

    }

    fun stopLossDesc(context: Context?, type: TriggerSetType): String {
        var stopLossPrice = ""
        var stopLossRate = ""
        var lossAmount = "" // 亏损额 USDT

        if (type == TriggerSetType.PRICE && this.stopLossPrice.isNullOrEmpty() || type == TriggerSetType.PERCENT && this.stopLossRate.isNullOrEmpty()) {
            return context!!.resources.getString(R.string.mc_sdk_stop_loss_predict, "- -", "- -")
        }

        if (position.mDirection == Direction.LONG.direction) {
//            止损价 = 开仓价(1-止损率/杠杆)
            stopLossPrice = if (type == TriggerSetType.PRICE) {
                this.stopLossPrice
            } else {
                (position.mOpenPrice * (1 - this.stopLossRate.getDouble() / 100 / position.mLeverage)).toString()
                        .getNum(McConstants.COMMON.CURRENT_PAIR_PRECISION)
            }

//            止损率 =杠杆(1-止损价/开仓价)
            stopLossRate = if (type == TriggerSetType.PRICE) {
                (position.mLeverage * (1 - this.stopLossPrice.getDouble() / position.mOpenPrice)).toString()
            } else {
                (this.stopLossRate.getDouble() / 100).toString().getNum(2)
            }

        } else {
//            止损价 =开仓价(1+止损率/杠杆)
//            止损率 =杠杆(止损价/开仓价-1)
            stopLossPrice = if (type == TriggerSetType.PRICE) {
                this.stopLossPrice
            } else {
                (position.mOpenPrice * (1 + this.stopLossRate.getDouble() / 100 / position.mLeverage)).toString()
                        .getNum(McConstants.COMMON.CURRENT_PAIR_PRECISION)
            }

            stopLossRate = if (type == TriggerSetType.PRICE) {
                (position.mLeverage * (this.stopLossPrice.getDouble() / position.mOpenPrice - 1)).toString()
            } else {
                (this.stopLossRate.getDouble() / 100).toString().getNum(2)
            }

        }

        lossAmount =
                (position.mMargin * stopLossRate.toDouble()).toString().getNum(2) + if (type == TriggerSetType.PRICE) {
                    "(${(stopLossRate.getDouble() * 100).toString().getNum(2)}%)"
                } else ""

        return context!!.resources.getString(R.string.mc_sdk_stop_loss_predict, stopLossPrice, lossAmount)

    }

    fun getTakeProfitAmount(takeProfitPrice:String):String{

        if (takeProfitPrice.isEmpty()){
            return "- -"
        }

        return try {
            var stopProfitRate = 0.0
            //止盈率 =杠杆(止盈价/开仓价-1)
            stopProfitRate = if (position.mDirection == Direction.LONG.direction) {
                (position.mLeverage * (takeProfitPrice.getDouble() / position.mOpenPrice - 1))
            } else {
                (position.mLeverage * (1 - takeProfitPrice.getDouble() / position.mOpenPrice))
            }
            // 收益额 USDT
            (position.mMargin * stopProfitRate.toDouble()).toString().getNum(2, withSymbol = true)
        } catch (e: Exception) {
            "- -"
        }

    }

    fun getStopLossAmount(stopLossPrice:String):String{

        if (stopLossPrice.isEmpty()){
            return "- -"
        }

        return try {
            var stopLossRate = if (position.mDirection == Direction.LONG.direction) {
                (position.mLeverage * (stopLossPrice.getDouble() / position.mOpenPrice - 1))
            } else {
                (position.mLeverage * (1 - stopLossPrice.getDouble() / position.mOpenPrice))
            }
            (position.mMargin * stopLossRate).toString().getNum(2, withSymbol = true)
        } catch (e: Exception) {
            "- -"
        }

    }

    fun getEntrustType(): Int {

        return when(position.mOriginalType){
            PositionType.EXECUTE.type ->{
                R.string.mc_sdk_contract_market_order
            }
            PositionType.PLAN.type ->{
                R.string.mc_sdk_contract_limit_order
            }
            PositionType.PLAN_TRIGGER.type ->{
                R.string.mc_sdk_contract_plan_order
            }
            else -> {
                R.string.mc_sdk_empty
            }
        }
    }

    fun getCloseType(context: Context): String {
        return when (position.mLiquidateBy) {
            "manual" -> context.getString(R.string.mc_sdk_contract_market_close)
            "planClose" -> context.getString(R.string.mc_sdk_contract_limit_close)
            "stopProfit" -> context.getString(R.string.mc_sdk_contract_stop_profit)
            "stopLoss" -> context.getString(R.string.mc_sdk_contract_stop_loss)
            "cancel" -> context.getString(R.string.mc_sdk_contract_manual_cancle)
            "bossCancel" -> context.getString(R.string.mc_sdk_contract_manager_close)
            "coerceClose" -> context.getString(R.string.mc_sdk_contract_coerce_close)
            "sysCancel" -> context.getString(R.string.mc_sdk_contract_sys_cancle)
            "overCancel" -> context.getString(R.string.mc_sdk_contract_over_cancle)
            "sysGoldClose" -> context.getString(R.string.mc_sdk_contract_sys_gold_close)
            "goldExpire" -> context.getString(R.string.mc_sdk_contract_gold_expire)
            "moveStopProfitAndLoss" -> context.getString(R.string.mc_sdk_modify_move_tp_sl)
            else -> ""
        }
    }



    fun getPositionDesc(context: Context) :String{
        val positionType = when(position.mPositionModel){
            PositionMode.FULL.mode -> {
                context.getString(R.string.mc_sdk_contract_order_type_full)
            }
            PositionMode.PART.mode -> {
                context.getString(R.string.mc_sdk_contract_order_type_part)
            }
            else -> {
                context.getString(R.string.mc_sdk_contract_order_type_full)
            }
        }

        val orderStatus = if (isOpenOrder()){
            if (position.mDirection == Direction.LONG.direction){
                context.getString(R.string.mc_sdk_history_open_long)
            } else {
                context.getString(R.string.mc_sdk_history_open_short)
            }
        } else {
            if (position.mDirection == Direction.LONG.direction){
                context.getString(R.string.mc_sdk_history_close_long)
            } else {
                context.getString(R.string.mc_sdk_history_close_short)
            }
        }

        return positionType + orderStatus
    }

    val realProfitOfHistoryOrder = position.mNetProfit+position.mFee+position.mFundingFee+position.mFundingSettle

    fun isShowProfitOfHistoryOrder(): Boolean {
        return if (isOpenOrder()){
            false
        }  else {
            when(position.mLiquidateBy){
                "cancel" -> false
                "bossCancel" -> false
                "sysCancel" -> false
                "overCancel" -> false
                "goldExpire" -> false
                else -> true
            }
        }
    }

    fun isHaveProfitOfHistoryOrder() =  realProfitOfHistoryOrder > 0

    fun getProfitOfHistoryOrder(): String {
       return realProfitOfHistoryOrder.toString().getNum(2)
    }

    fun getProfitRateOfHistoryOrder(): String {
        return if(position.mMargin==0.0) "" else
                "${((realProfitOfHistoryOrder/ position.mMargin) * 100).toString().getNum(McConstants.COMMON.DEFAULT_PERCENT_PRECISION,true)}%"
    }

    fun getProfitRateOfHistoryOrderNoPercent(): String {
        return if(position.mMargin==0.0) "" else
            "${((realProfitOfHistoryOrder/ position.mMargin) * 100).toString().getNum(McConstants.COMMON.DEFAULT_PERCENT_PRECISION,true)}"
    }

}