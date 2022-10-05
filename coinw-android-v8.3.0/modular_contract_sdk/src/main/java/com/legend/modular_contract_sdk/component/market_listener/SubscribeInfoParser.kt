package com.legend.modular_contract_sdk.component.market_listener

import com.legend.modular_contract_sdk.utils.JsonUtil

fun <T : MarketSubscribeType> parseSubscribeType(marketSubscribeType: T): String {
    //1.3.1.订阅格式及示例:
    // {"event":"subscribe","params":{"biz":"funding_rate","type":"funding_rate","base":"btc","quote":"usd","zip":false}}
    if (marketSubscribeType.isUserSubscribe) {
        return "{\"event\":\"subscribe\",\"params\":{\"biz\":\"${marketSubscribeType.biz}\",\"type\":\"${marketSubscribeType.type}\",\"zip\":${marketSubscribeType.zip}}}"
    } else {
        return "{\"event\":\"subscribe\",\"params\":${JsonUtil.toJson(marketSubscribeType)}}"
    }
}

fun parseUnsubscribeType(marketSubscribeType: MarketSubscribeType): String {
    //1.3.1.订阅格式及示例:
    // {"event":"subscribe","params":{"biz":"funding_rate","type":"funding_rate","base":"btc","quote":"usd","zip":false}}
    return "{\"event\":\"unsubscribe\",\"params\":${JsonUtil.toJson(marketSubscribeType)}}"
}

fun <T> parseMessage(msg: String, clazz: Class<T>) = JsonUtil.fromJsonToObject(msg, clazz)

fun <T> parseMessageByList(msg: String, clazz: Class<T>) = JsonUtil.fromJsonToList(msg, clazz)