package com.android.legend.model.earn

import java.io.Serializable

//language	语言
//name	理财名字
//cover	理财图片链接
//problem	常见问题
//productClassifiy	分类见上面解释
//id	定期组合id 以逗号分隔
//financialType	1 : 活期 2：定期 '
//productType	产品类型 1 : 常规产品 2：活动产品'
//deadline	理财期限，多个理财组合逗号分隔
//investTotalAmount	该项目总投资金额 多币种用逗号分割
//startTime	上架时间
//raiseTime	申购开启日期，如果不填默认为上架时间
//endTime	申购结束日期
//state	状态 1：上架 2：结束购买（属于上架） 3 ：下架
//productInvestList	投资币种信息****

data class EarnProduct(
    val cover: String,
    val deadline: String,

    val financialType: Int,
    val id: String,
    val investTotalAmount: String, // 投资总额 做为产品列表时表示该产品的投资总额限制，作为个人投资信息返回时表示个人的投资金额
    val incomeTotalAmount: String, // 该字段个人投资信息才有用，表示这个产品的投资回报
    val language: String,
    val name: String,
    val problem: String,
    val productClassifiy: String,
    val productIncomeList: List<ProductIncome>,
    val productInvestList: List<ProductInvest>,
    val productType: Int,
    val raiseTime: String,
    val startTime: Long,
    val endTime: Long?,
    val createTime: Long,
    var state: String
):Serializable

//productIncomeList	收益币种信息*****
//productIncomeList productId	收益币种产品id
//productIncomeList currencyId	收益币种id
//productIncomeList currencyName	收益币种名称
//productIncomeList expectedRate	收益币种预期收益率
//productIncomeList actualRate	收益币种实际收益率
//productIncomeList incomeHighPrice	收益币兑换USDT价格
//productIncomeList incomeTotalAmount	我的收益金额 混合理财使用
data class ProductIncome(
    val actualRate: Double,
    val currencyId: Int,
    val currencyName: String,
    val expectedRate: String,
    val incomeHighPrice: Double,
    val incomeTotalAmount: Double,
    val productId: Int
):Serializable

//productInvestList productId	产品id
//productInvestList currencyId	币对id
//productInvestList currencyName	币对名称
//productInvestList minAmount	最小投资金额
//productInvestList maxAmount	最大投资金额
//productInvestList totalAmount	项目总额度
//productIncomeList investLowPrice	投资币兑换USDT价格
//productIncomeList investTotalAmount	我的投资金额 混合理财使用
data class ProductInvest(
    val currencyId: Int,
    val currencyName: String,
    val maxAmount: Double,
    val minAmount: Double,
    val investLowPrice: Double,
    val productId: Int,
    val totalAmount: Double,
    var investTotalAmount: Double
) : Serializable