package com.android.legend.model.home

data class BannerBean(
        val bannerList: List<Banner>,
        val code: Int,
        val msg: String
)

data class Banner(
    val content: String,
    val fcreateDateYear: String,
    val id: Int,
    val img: String,
    val newid: String,
    val title: String,
    val type: Int,
    val url: String,
    val whether: Int
)

data class Notice(
    val content: String,
    val date: String,
    val datetime: String,
    val goOut: Boolean,
    val id: Int,
    val outUrl: String,
    val title: String,
    val url: String
)

data class HomePairList(
    val mainCoins: List<Pair>,
    val newCoins: List<Pair>,
    val recommendeds: List<Pair>
)

data class HomePairList2(
    val legalMoneyList: List<Ticker2>,
    val riseList: List<Ticker2>
)

data class Pair(
    val pairId: Int,
    val pairName: String,
    val sort: Int,
    var ticker: Ticker?
)

/**
 * {
"activityState": 0,
"fPartitionIds": "9,6",
"fav": false,
"leftCoinName": "BTC",
"leftCoinUrl": "https://btc018.oss-cn-shenzhen.aliyuncs.com/201810020046047_T9g8i.png",
"oneDayHighest": "40010.00",
"oneDayLowest": "40010.00",
"oneDayTotal": "0.0000000000",
"price": "40010.00",
"rightCoinName": "USDT",
"rose": "0.0000",
"symbol": "$",
"tmId": 78,
"transferPrice": "265238.2930",
"transferSymbol": "￥"
}
 */
data class Ticker(
    val activityState: Int,
    val fPartitionIds: String,
    val fav: Boolean,
    val leftCoinName: String,
    val leftCoinUrl: String,
    val oneDayHighest: String,
    val oneDayLowest: String,
    val oneDayTotal: String,
    val price: String,
    val rightCoinName: String,
    val rose: String,
    val symbol: String,
    val tmId: Int,
    val transferPrice: String,
    val transferSymbol: String
)

/**
 * {
"priceRaiseRate": "0.0000",
"showTag": false,
"OneDayHighest": "0.0000",
"currencyPrize": "0.000000",
"currencySymbol": "$",
"weight": 999999.0,
"legalMoney": "0",
"OneDayLowest": "0.0000",
"OneDayTotal": "0.0000",
"cnName": "lat",
"cnyName": "USDT",
"fPartitionIds": "1",
"OneDayTotalK": "0k",
"LatestDealPrice": "0.0000",
"legalMoneyCny": "0",
"logo": "https://btc018.oss-cn-shenzhen.aliyuncs.com/favicon.ico",
"id": 238.0,
"coinName": "lat",
"status": "+"
}
 */
data class Ticker2(
    val LatestDealPrice: String,
    val OneDayHighest: String,
    val OneDayLowest: String,
    val OneDayTotal: String,
    val OneDayTotalK: String,
    val cnName: String,
    val cnyName: String,
    val coinName: String,
    val currencyPrize: String,
    val currencySymbol: String,
    val fPartitionIds: String,
    val id: Double,
    val legalMoney: String,
    val legalMoneyCny: String,
    val logo: String,
    val priceRaiseRate: String,
    val showTag: Boolean,
    val status: String,
    val weight: Double
)

data class DynamicHomeMenu(
    val level0: List<HomeMenu>,
    val level1: List<HomeMenu>,
    val level2: List<HomeMenu>
)


data class HomeMenu(
    val desc: String,
    val imgUrl: String,
    val portalType: Int,
    val portalUrl: String,
    val secondTitle: String,
    val sort: Int,
    val tag: String,
    val title: String,
    val type: Int,
    val typeDesc: String
)