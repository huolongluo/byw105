package com.legend.modular_contract_sdk.utils

import com.legend.modular_contract_sdk.component.market_listener.MarketData
import com.legend.modular_contract_sdk.component.market_listener.Price
import com.legend.modular_contract_sdk.repository.model.ContractAssetInfo
import com.legend.modular_contract_sdk.repository.model.PositionAndOrder
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.repository.model.wrap.PositionWrap
import com.legend.modular_contract_sdk.ui.contract.Direction
import com.legend.modular_contract_sdk.ui.contract.PositionMode
import com.legend.modular_contract_sdk.ui.contract.QuantityUnit
import java.util.*
import kotlin.math.abs
import kotlin.math.min

object CalculateUtil {
    private const val TAG = "CalculateUtil"

    /*********全仓计算********/
    //计算浮动盈亏
    fun getFloating(usableBalances: ContractAssetInfo, tickerMap: MutableMap<String, MarketData>): Double {
        var floating = 0.0 //浮动盈亏
        try {
            if (usableBalances == null) return 0.0

            for (userPositionsBean in usableBalances.mUserPositions) {
                var floatingTemp = 0.0
                if (userPositionsBean.mContractType == 1) {//永续合约
                    // 用标记价格计算浮动盈亏
                    val markPrice =
                            (tickerMap[userPositionsBean.mInstrument.toLowerCase(Locale.ROOT)] as Price).p.toDouble()
                    when (userPositionsBean.mDirection) {
                        "long" -> floatingTemp =
                                (markPrice - userPositionsBean.mOpenPrice) * userPositionsBean.mBaseSize
                        "short" -> floatingTemp =
                                (userPositionsBean.mOpenPrice - markPrice) * userPositionsBean.mBaseSize
                    }
                } else {
//                    var coinPrice = BibeexMainActivity.self.priceMap[userPositionsBean.mInstrument.toLowerCase()]!!
//                    when (userPositionsBean.mDirection) {
//                        "long" -> floatingTemp= (coinPrice * (1 - usableBalances.mCloseSpreadRateMap[userPositionsBean.mInstrument]!!) - userPositionsBean.mOpenPrice) * userPositionsBean.mBaseSize
//                        "short" -> floatingTemp= (userPositionsBean.mOpenPrice - coinPrice * (1 + usableBalances.mCloseSpreadRateMap[userPositionsBean.mInstrument]!!)) * userPositionsBean.mBaseSize
//                    }
                }
//                userPositionsBean.mFloating=floatingTemp
                floating += floatingTemp
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return floating
    }

    //计算净值
    fun getNetValue(usableBalances: ContractAssetInfo, tickerMap: MutableMap<String, MarketData>): Double {
        try {
            return usableBalances.mAvailable + getFloating(
                    usableBalances,
                    tickerMap
            ) - usableBalances.mTotalFee - usableBalances.mTotalFundingFee
        } catch (e: Exception) {

        }
        return 0.0
    }

    //未实现盈亏
    fun getUnrealizedLoss(usableBalances: ContractAssetInfo, tickerMap: MutableMap<String, MarketData>): Double {
        try {
            return getFloating(usableBalances, tickerMap) - usableBalances.mTotalFundingFee
        } catch (e: Exception) {

        }
        return 0.0
    }

    //风险率
    fun getRiskRate(usableBalances: ContractAssetInfo, tickerMap: MutableMap<String, MarketData>): Double {
        try {
            return (getNetValue(usableBalances, tickerMap) / usableBalances.mTotalMargin) * 100
        } catch (e: Exception) {

        }
        return 0.0
    }

    //可用保证金
    fun getUsableDeposit(mode: PositionMode, usableBalances: ContractAssetInfo, tickerMap: MutableMap<String, MarketData>): Double {
        try {
            return when (mode) {
                PositionMode.FULL -> {
                    usableBalances.mAvailable - usableBalances.mTotalMargin + getFloating(
                            usableBalances,
                            tickerMap
                    ) - usableBalances.mTotalFee - usableBalances.mTotalFundingFee
                }
                PositionMode.PART -> {
                    usableBalances.mAvailable
                }
            }

        } catch (e: Exception) {

        }
        return 0.0
    }
    /*********全仓计算********/


    /*********仓位计算 start********/
    // 真实的浮动盈亏
    // 看多:=(标记价格 - 开仓价格)*实物数量
    // 看空:=(开仓价格 - 标记价格)*实物数量
    fun getPositionProfitAndLoss(direction: Direction, openPrice: Double, indexPrice: Double, value: Double): Double {
        try {
            if (direction == Direction.LONG) {
                return (indexPrice - openPrice) * value
            } else {
                return (openPrice - indexPrice) * value
            }
        } catch (e: Exception) {

        }
        return 0.0

    }

    //多单预计强平价=开仓价*(1-1/杠杆)；
    //空单预计强平价=开仓价*(1+1/杠杆)。
    fun getPositionLiquidationPrice(direction: Direction, openPrice: Double, leverage: Int): Double {
        try {
            return if (direction == Direction.LONG) {
                openPrice * (1 - 1 / leverage)
            } else {
                openPrice * (1 + 1 / leverage)
            }
        } catch (e: Exception) {

        }
        return 0.0

    }

    //风险率或保证金率=(账户余额available + SUM(真实的浮动盈亏)-总的手续费totalFee-总的过夜费totalFundingFee)/总的已用保证金totalMargin;
    fun getRiskRate(
            available: Double,
            profitAndLoss: Double,
            totalFee: Double,
            totalFundingFee: Double,
            totalMargin: Double
    ): Double {
        try {
            return (available + profitAndLoss - totalFee - totalFundingFee) / totalMargin
        } catch (e: Exception) {

        }
        return 0.0

    }

    /*********仓位计算 end********/

//    /*********逐仓计算********/
//    //计算逐仓的浮动盈亏
//    fun getFloatingWarehouse(usableBalances: ContractAssetInfo, context: Context):Double{
//        var floating = 0.0 //浮动盈亏
//        try {
//            if(usableBalances==null) return 0.0
//            for (userPositionsBean in usableBalances.mUserPositions) {
//                var currentPrice = BibeexMainActivity.self.priceMap[userPositionsBean.mInstrument.toLowerCase()]!!
//                if (currentPrice == 0.0) return 0.0
//                //开仓价(预估成交价)=开仓时指数价格(1+点差)
//                val coinsConfig = SPUtils.getBibeexCoinConfigByCoin(context, userPositionsBean.mInstrument)
//                        ?: return 0.0
//                var floatingTemp = 0.0
//                if(userPositionsBean.mContractType==1){//永续合约
//                    var markPrice = BibeexMainActivity.self.permanentPriceMap[userPositionsBean.mInstrument.toLowerCase()]!!
//                    when (userPositionsBean.mDirection) {
//                        "long" -> floatingTemp= (markPrice - userPositionsBean.mOpenPrice) * userPositionsBean.mBaseSize
//                        "short" -> floatingTemp= (userPositionsBean.mOpenPrice - markPrice) * userPositionsBean.mBaseSize
//                    }
//                }else{
//                    when (userPositionsBean.mDirection) {
//                        "long" -> floatingTemp = (currentPrice * (1 - usableBalances.mCloseSpreadRateMap[userPositionsBean.mInstrument]!!) - userPositionsBean.mOpenPrice) * userPositionsBean.mBaseSize
//                        "short" -> floatingTemp = (userPositionsBean.mOpenPrice - currentPrice * (1 + usableBalances.mCloseSpreadRateMap[userPositionsBean.mInstrument]!!)) * userPositionsBean.mBaseSize
//                    }
//                }
//
//                val precision = when(coinsConfig.pricePrecision){
//                    1->{
//                        2
//                    }
//                    0->{
//                        2
//                    }
//                    else->{
//                        coinsConfig.pricePrecision
//                    }
//
//                }
//                userPositionsBean.floating = CastUtil.castDouble(CastUtil.castDoubleByCount(floatingTemp, precision))
//                floating += floatingTemp
//            }
//        }catch (e:Exception){
//
//        }
//
//        return floating
//    }
//    /*********逐仓计算********/
//


    // 最大可开张数
    fun getMaxOpenSheet(
            price: Double, usableBalances: ContractAssetInfo?, leverage: Int,
            product: Product?, positionMode: PositionMode,
            tickerMap: MutableMap<String, MarketData>,
            takerFee:Double,
            unit: QuantityUnit
    ): Double {

        return try {
            val maxMargin = getMaxMargin(positionMode, usableBalances, product, tickerMap)
            val oneLotSize = product?.mOneLotSize ?: 1.0

            when(unit){
                QuantityUnit.SIZE -> {
                    // 计算最大可开(张) = 可用资金 * 杠杆倍数 / (预成交价价格 * 面值 * (1 + taker费率 * 杠杆倍数))
                    return maxMargin * leverage / (price *  oneLotSize * (1 + leverage * takerFee))
                }
                QuantityUnit.USDT -> {
                    // 计算最大可开(USDT) = 可用资金 * 杠杆倍数 / (1 + taker费率 * 杠杆倍数)
                    return maxMargin * leverage / (1 + leverage * takerFee)
                }
                QuantityUnit.COIN -> {
                    // 计算最大可开(币) = 可用资金 * 杠杆倍数 / (预成交价价格 * (1 + taker费率 * 杠杆倍数))
                    return maxMargin * leverage / (price * (1 + leverage * takerFee))
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }



    /**
     * 获取最大可用保证金
     * min{可用保证金USDT数量，合约品种单笔仓位最大限制}
     */
    fun getMaxMargin(
            positionMode: PositionMode, usableBalances: ContractAssetInfo?, product: Product?,
            tickerMap: MutableMap<String, MarketData>
    ): Double {
        if (usableBalances == null) {
            return 0.0
        }
        product?.let {
            return if (positionMode == PositionMode.FULL) {
                val margin = usableBalances.mAvailable - usableBalances.mTotalMargin + getFloating(usableBalances, tickerMap) - usableBalances.mTotalFee - usableBalances.mTotalFundingFee
                min(margin, it.mOneMaxPosition.toDouble())
            } else {
                min(usableBalances.mAvailable, it.mOneMaxPosition.toDouble())
            }
        }
        return 0.0
    }


    /**
     * 逐仓爆仓价
     * 手续费 = 持仓保证金 * takerFee * 杠杆倍数
     * 最大亏损 = 维持保证金率 * (合约面值 * 合约张数 * 开仓价格 / 杠杆倍数）- 持仓保证金
     * 强平价 = 持仓均价 + 开仓方向 * (最大亏损 / 张数 * 合约面值)
     */
    fun getPartPositionLiquidationPrice(positionWrap: PositionWrap, product: Product): String {
        return getPartPositionLiquidationPrice(positionWrap, product, positionWrap.position.mMargin)

    }

    fun getPartPositionLiquidationPrice(positionWrap: PositionWrap, product: Product, margin:Double): String {
        try {
            val openPrice = positionWrap.position.mOpenPrice // 持仓均价
            val minMarginRate = product.mStopSurplusRate // 维持保证金率
            val size = positionWrap.position.mBaseSize // 合约价值 = 张数*合约面值
            val margin = margin // 保证金
            val leverage = positionWrap.position.mLeverage
            val positionMarkPrice: Double = positionWrap.markPrice ?: openPrice
            // 方向
            val direction = if (positionWrap.position.mDirection == Direction.LONG.direction) {
                1
            } else {
                -1
            }

            // 最大亏损
            val maxLoss = minMarginRate * (size * openPrice / leverage) - margin

            val result = (openPrice + direction * (maxLoss / size))

            if (result < 0 || result > positionMarkPrice * 10) {
                return "- -"
            }

            return result.toString().getNum(product.mPricePrecision)
        } catch (e: Exception) {
            e.printStackTrace()
            return "- -"
        }

    }

    /**
     * 全仓预估爆仓价
     * 强平价 = 开仓均价 + 开仓方向 * (available  + sum(仓位保证金) + sum(所有仓位浮动盈亏) - sum(手续费) - sum(仓位保证金) * 风险率) / 开仓总张数 * 合约面值
     *
     * 公式1, 适用于有对冲单，且可以完全对冲: 两单都显示 --

    公式2, 适用于有对冲单，单不能完全对冲:
    强平价 = 标记价 - 开仓方向(多1、空-1) * (available(接口字段) + sum(所有仓位浮动盈亏) - sum(手续费) - sum(仓位保证金) * 风险率) / abs(BaseSize之差)

    公式3, 适用于其他情况:
    强平价 = 标记价 - 开仓方向(多1、空-1) * (available(接口字段) + sum(所有仓位浮动盈亏) - sum(手续费) - sum(仓位保证金) * 风险率) / (张数 * 合约面值)
     */
    fun getFullPositionLiquidationPrice(usableBalances: ContractAssetInfo, tickerMap: MutableMap<String, MarketData>, positionWrap: PositionWrap, product: Product): String {
        try {

            val totalMargin = usableBalances.mTotalMargin // 所有仓位保证金
            val risk = product.mStopCrossPositionRate // 风险率
            var takerFeeSum = usableBalances.mTotalFee // 强平手续费
            var profitSum = 0.0 // 其它仓位浮动盈亏
            var positionMarkPrice: Double = positionWrap.markPrice
                    ?: positionWrap.position.mOpenPrice
            val available = usableBalances.mAvailable // 可用保证金
            val allPosition = mutableListOf<PositionAndOrder>()// 同方向 同币种的仓位集合


            var direction = if (positionWrap.position.mDirection == Direction.LONG.direction) {
                1
            } else {
                -1
            }

            val positionMapByInstrument = mutableMapOf<String, MutableList<PositionWrap>>()

            usableBalances.mUserPositions.forEach {

                if (positionWrap.position.mInstrument == it.mInstrument && positionWrap.position.mDirection == it.mDirection) {
                    allPosition.add(it)
                }

                val markPrice =
                        (tickerMap[it.mInstrument.toLowerCase(Locale.getDefault())] as Price).p.toDouble()

                val positionWrap = PositionWrap(it).apply {
                    this.markPrice = markPrice
                }

                profitSum += positionWrap.getRealProfit()

                if (positionMapByInstrument[it.mInstrument] == null) {
                    positionMapByInstrument[it.mInstrument] = mutableListOf()
                }
                positionMapByInstrument[it.mInstrument]?.add(positionWrap)
            }

            var longPositionSize = 0.0
            var shortPositionSize = 0.0

            val type = positionMapByInstrument[positionWrap.position.mInstrument]?.let { positionWrapList ->
                // 当前币种只有一个仓位 使用公式3
                if (positionWrapList.size == 1) {
                    return@let 3
                }

                for (positionWrap in positionWrapList) {
                    if (positionWrap.getDirectionIsLong()) {
                        longPositionSize += positionWrap.position.mBaseSize
                    } else {
                        shortPositionSize += positionWrap.position.mBaseSize
                    }
                }

                if (longPositionSize == shortPositionSize) {
                    // 同币种 多空完全对冲
                    return@let 1
                } else {
                    // 多空不对等时 方向 = 仓位更大的仓位方向
                    // 产品设计如此，有可能造成多仓的爆仓价高于开仓价。（其他交易所也是这么算的）
                    if (longPositionSize > shortPositionSize){
                        direction = 1
                    } else{
                        direction = -1
                    }
                    return@let 2
                }
            }

            val result = when (type) {
                1 -> {
                    0.0
                }
                2 -> {
                    // 公式2, 适用于有对冲单，单不能完全对冲:
                    // 强平价 = 标记价 - 开仓方向(多1、空-1) * (available(接口字段) + sum(所有仓位浮动盈亏) - sum(手续费) - sum(仓位保证金) * 风险率) / abs(BaseSize之差)
                    positionMarkPrice - direction * (available + profitSum - takerFeeSum - totalMargin * risk) / abs(longPositionSize - shortPositionSize)

                }
                3 -> {
                    // 当前币种只有一个仓位 使用公式3
                    // 强平价 = 标记价 - 开仓方向(多1、空-1) * (available(接口字段) + sum(所有仓位浮动盈亏) - sum(手续费) - sum(仓位保证金) * 风险率) / (张数 * 合约面值)
                    positionMarkPrice - direction * (available + profitSum - takerFeeSum - totalMargin * risk) / positionWrap.position.mBaseSize

                }
                else -> {
                    0.0
                }
            }

            if (result <= 0 || result >= positionMarkPrice*10) {
                return "- -"
            }

            return result.toString().getNum(product.mPricePrecision)

        } catch (e: Exception) {
            e.printStackTrace()
            return "- -"
        }

    }


}