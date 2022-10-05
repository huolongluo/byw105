package com.android.legend.model.earn.wrap

import android.content.Context
import android.text.TextUtils
import androidx.databinding.BaseObservable
import com.android.legend.model.earn.EarnProduct
import com.android.legend.ui.earn.EarnTimeLimitType
import com.android.legend.ui.earn.EarnType
import com.legend.modular_contract_sdk.utils.*
import huolongluo.byw.R

class EarnProductWrap(val earnProduct: EarnProduct) : BaseObservable() {

    val now = System.currentTimeMillis()

    var index: Int = 0
        set(value) {
            field = value
            notifyChange()
        }
        get() = field

    fun getProductCoinName(): String {
        var name = StringBuilder()
        val nameSet = LinkedHashSet<String>()
        earnProduct.productInvestList.forEach {
            nameSet.add(it.currencyName)
        }
        nameSet.forEachIndexed{index, currencyName ->
            if (!name.contains(currencyName)) {
                name.append(currencyName)
                if (index != nameSet.size - 1) {
                    name.append(" & ")
                }
            }
        }
        return name.toString()
    }

    fun getProfitRate(): String {
        return earnProduct.productIncomeList[index].expectedRate
    }

    fun getRealProfitRate(): String {
        return (earnProduct.productIncomeList[index].actualRate * 100).toString().getNum(2) + "%"
    }

    /**
     * 是否进行中
     */
    fun isInProgress(): Boolean {

        if (earnProduct.state.split(",").get(index).getInt() == 1) {
            val start = earnProduct.raiseTime.split(",").get(index).toLong()
            val end = earnProduct.endTime
            if (now > start /*&& (now < end || !isHotProduct())*/) {
                return true
            }
        }

        return false

    }


    fun getStartTime(context: Context): String {

        val start = earnProduct.raiseTime.split(",").get(index).toLong()
        val end = earnProduct.endTime ?: 0
        return if (now < start) {
            context.getString(R.string.earn_start_date, TimeUtils.millis2String(start))
        } else if (now > end || earnProduct.state.split(",").get(index).getInt() == 2) {
            context.getString(R.string.earn_end)
        } else {
            ""
        }
    }

    fun getEndTime():String{
        val end = earnProduct.endTime ?: 0
        if (end == 0L){
            return ""
        } else {
            return TimeUtils.millis2String(end)
        }
    }

    /**
     * 活动产品期限 用于活期的活动产品
     */
    fun getBuyDeadlineDays(context: Context): String {
        if (earnProduct.deadline != null) {
            val days = earnProduct.deadline.split(",").get(index)
            return context.getString(R.string.earn_days, days)
        } else {
            return context.getString(R.string.earn_current)
        }

    }

    fun getEarnType(): Int {
        return if (isMixRegularProduct()) {
            R.string.earn_regular
        } else {
            R.string.earn_current
        }
    }

    fun getBuyProgress(): Int {
        if (earnProduct.productInvestList.isEmpty()) {
            return 0
        }

        val progress = (earnProduct.investTotalAmount.getDoubleValue() / earnProduct.productInvestList[index].totalAmount * 100).toInt()

        return progress
    }

    fun getTotalCurrencyNum(): String {
        if (earnProduct.productInvestList.isEmpty()) {
            return ""
        }

        return earnProduct.productInvestList[index].totalAmount.toString().getNum() + earnProduct.productInvestList[index].currencyName
    }

    fun getCurrentCurrentNum(): String {
        if (earnProduct.productInvestList.isEmpty()) {
            return ""
        }

        return earnProduct.investTotalAmount.getNum() + earnProduct.productInvestList[index].currencyName
    }

    fun isHotProduct(): Boolean {
        return earnProduct.productType == EarnType.HOT.type
    }
    // 是否是定期理财 这个定期指 -> 活期，定期，混合 三个分类中的定期
    fun isRegularProduct(): Boolean {
        return earnProduct.productClassifiy == EarnTimeLimitType.REGULAR.timeLimitName
    }

    fun isMixProduct(): Boolean {
        return earnProduct.productClassifiy == EarnTimeLimitType.MIX.timeLimitName
    }

    // 是否是定期理财
    fun isMixRegularProduct(): Boolean {
        return earnProduct.financialType == EarnTimeLimitType.REGULAR.type
    }

    fun isHasDeadline(): Boolean {
        return !TextUtils.isEmpty(earnProduct.deadline)
    }

    fun getMixType(context: Context):String{
        return if (isMixProduct()) {
            if (earnProduct.financialType == EarnTimeLimitType.CURRENT.type) {
                context.getString(R.string.earn_mix) + context.getString(R.string.earn_current)
            } else if (earnProduct.financialType == EarnTimeLimitType.REGULAR.type) {
                context.getString(R.string.earn_mix) + context.getString(R.string.earn_regular)
            } else {
                context.getString(R.string.earn_mix)
            }
        } else {
            context.getString(R.string.earn_mix)
        }
    }

    fun getInvestCoinId(): Int {
        if (earnProduct.productInvestList.isEmpty()) {
            return -1
        }
        return earnProduct.productInvestList[index].currencyId
    }

    fun getBuyAmount(): String {
        if (earnProduct.productInvestList.isEmpty()) {
            return ""
        }
        return earnProduct.investTotalAmount.getNum() + " " + earnProduct.productInvestList[0].currencyName
    }

    fun getIncomeAmount(): String {
        if (earnProduct.productInvestList.isEmpty()) {
            return ""
        }
        return earnProduct.incomeTotalAmount.getNum() + " " + earnProduct.productIncomeList[0].currencyName
    }

    fun getInvestTimeLimit(context: Context): String {
        return context.getString(R.string.earn_days, earnProduct.deadline)
    }

    fun getExpiryDate(): String {
        try {
            val days = earnProduct.deadline.split(",")[0].getInt()
            val expiryDate = earnProduct.createTime + (1000L * 60L * 60L * 24L) * (days + 1)

            return TimeUtils.millis2String(expiryDate, "yyyy-MM-dd")
        } catch (e: Exception) {
            return ""
        }

    }

    fun getInvestCurrencyName(): String {
        return earnProduct.productInvestList[index].currencyName
    }

    fun getIncomeCurrencyName(): String {
        return earnProduct.productIncomeList[index].currencyName
    }

    // 获取预期收益
    fun getExpectedProfit(amount: Double): String {
        // 投资金额*投资币种最低价格 *年化收益率 /365 *期限 /收益币种最高价格  = 收益币种数量
        try {
            val investCoinPrice = earnProduct.productInvestList[index].investLowPrice
            val incomeCoinPrice = earnProduct.productIncomeList[index].incomeHighPrice
            val profitRate = earnProduct.productIncomeList[index].actualRate / 365
            val timelimit = earnProduct.deadline.split(",")[index].getDouble()
            return (amount * investCoinPrice * profitRate * timelimit / incomeCoinPrice).toString().getNum(8)
        } catch (e: Exception) {
            return ""
        }
    }

    /**
     获取混合预期收益
     incomeIndex 要获取的收益币种
     */
    fun getMixExpectedProfit(incomeIndex: Int): String{
        val profitRate = earnProduct.productIncomeList[incomeIndex].actualRate / 365.0
        val incomeCoinPrice = earnProduct.productIncomeList[incomeIndex].incomeHighPrice
        val incomeCurrencyId = earnProduct.productIncomeList[incomeIndex].currencyId

        var totalAmount = 0.0

        earnProduct.productInvestList.forEach {
            val amount = it.investTotalAmount
            val deadline = earnProduct.deadline.getDouble()
            val investCoinPrice = it.investLowPrice
            val investCurrencyId = it.currencyId

//            if (investCurrencyId == incomeCurrencyId){
//                totalAmount += amount * profitRate * deadline
//            } else {
                totalAmount += amount * investCoinPrice * profitRate * deadline / incomeCoinPrice
//            }

        }

        if (totalAmount <= 0) {
            return "- - ${earnProduct.productIncomeList[incomeIndex].currencyName}"
        } else {
            return "${totalAmount.toString().getNum(8)} ${earnProduct.productIncomeList[incomeIndex].currencyName}"
        }
    }

    fun getCover(): String {
        try {
            return earnProduct.cover.split(",").get(index)
        } catch (e: Exception) {
            return ""
        }
    }

    // 获取预期收益
    fun getExpectedProfitByBuy(): String {

        if (isMixRegularProduct()) {
//            "+" + (earnProduct.regularProduct ? earnProduct.getExpectedProfitByBuy()+earnProduct.incomeCurrencyName : earnProduct.incomeAmount
            // 投资金额*投资币种最低价格 *年化收益率 /365 *期限 /收益币种最高价格  = 收益币种数量
            try {
                val investCoinPrice = earnProduct.productInvestList[index].investLowPrice
                val incomeCoinPrice = earnProduct.productIncomeList[index].incomeHighPrice
                val profitRate = earnProduct.productIncomeList[index].actualRate / 365
                val timelimit = earnProduct.deadline.split(",")[index].getDouble()
                val amount = earnProduct.investTotalAmount.getDouble()
                return "+" + (amount * investCoinPrice * profitRate * timelimit / incomeCoinPrice).toString().getNum(8) + " " + getIncomeCurrencyName()
            } catch (e: Exception) {
                return ""
            }
        } else {
            return "+" + getIncomeAmount()
        }

    }

    // 理财产品是否赎回中
    fun getMyEarnIsRedeeming(): Boolean {
        val state = earnProduct.state.split(",").get(index).toInt()
        return (state == 3 || state == 4)
    }
}