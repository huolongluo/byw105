package com.android.legend.api

import android.text.TextUtils
import android.util.Log
import com.android.coinw.biz.trade.model.OrderSide
import com.android.legend.model.enumerate.order.OrderType
import com.android.legend.model.login.LoginParam
import huolongluo.byw.io.AppConstants
import huolongluo.byw.log.Logger
import huolongluo.byw.model.AliManMachineEntity
import huolongluo.byw.user.UserInfoManager
import huolongluo.byw.util.Constant
import huolongluo.byw.util.GsonUtil
import huolongluo.bywx.utils.EncryptUtils

/**
 * 新系统的接口不加密，表单还是json参考文档，老系统数据加密使用表单
 */
class ApiRepository {
    companion object {
        val instance = ApiRepository()
        const val TAG = "ApiRepository"
    }

    suspend fun getTransferData(srcAccount: String, targetAccount: String, coinId: Int) = parseResponse {
        var body = mapOf(
                "accountType" to srcAccount,
                "targetAccountType" to targetAccount,
                "coinId" to coinId
        )
        Logger.getInstance().debug(TAG, "getTransferData body:$body")
        body = EncryptUtils.encrypt(body)
        body["loginToken"] = UserInfoManager.getToken()
        apiService.getTransferData(body)
    }

    suspend fun getTransferFinance(srcAccount: String, targetAccount: String, coinId: Int) = parseResponse {
        var body = mapOf(
                "accountType" to srcAccount,
                "targetAccountType" to targetAccount,
                "coinId" to coinId
        )
        body = EncryptUtils.encrypt(body)
        body["loginToken"] = UserInfoManager.getToken()
        apiService.getTransferFinance(body)
    }

    suspend fun transfer(srcAccount: String, targetAccount: String, amount: String, coinId: Int) = parseResponse {
        var body = mapOf(
                "accountType" to srcAccount,
                "targetAccountType" to targetAccount,
                "bizType" to "${srcAccount}_TO_$targetAccount",
                "amount" to amount,
                "coinId" to coinId
        )
        Logger.getInstance().debug(TAG, "transfer params:${GsonUtil.obj2Json(body, Map::class.java)}")
        body = EncryptUtils.encrypt(body)
        body["loginToken"] = UserInfoManager.getToken()
        apiService.transfer(body)
    }

    suspend fun getContractMudInfo() = parseResponse {
        val body = mapOf(
                "loginToken" to UserInfoManager.getToken()
        )
        apiService.getContractMudInfo(body)
    }

    suspend fun getNMInfo() = parseResponse {
        val body = mapOf(
                "loginToken" to UserInfoManager.getToken()
        )
        apiService.getNMInfo(body)
    }

    suspend fun getTransferRecord(srcAccount: String?, targetAccount: String?, pageNum: Int) = parseResponse {
        var body = mapOf(
                "fromAccount" to srcAccount,
                "targetAccount" to targetAccount,
                "pageNum" to pageNum,
                "pageSize" to AppConstants.UI.DEFAULT_PAGE_SIZE
        )
        body = EncryptUtils.encrypt(body)
        body["loginToken"] = UserInfoManager.getToken()
        apiService.getTransferRecord(body)
    }

    suspend fun getBbFinanceData(account: String) = parseResponse {
        var body = mapOf<String, Any>(
                "accountType" to account
        )
        body = EncryptUtils.encrypt(body)
        body["loginToken"] = UserInfoManager.getToken()
        apiService.getBbFinanceData(body)
    }
    //获取订单列表
    suspend fun getOrders(active: Boolean, before: Long, startAt: Long?, endAt: Long?, symbol: String?, type: String? = null, status: String?, side: String?) =
            parseResponse {
//                不能使用queryMap,value为null会报错
//        val body = mapOf(
//                "active" to active,// 活跃委托标志:true-查询活跃委托，false-历史委托.默认活跃
//                "after" to afterId,//向后id 分页
//                "startAt" to startAt,//开始时间–历史委托有效
//                "endAt" to endAt,//截止时间–历史委托有效
//                "limit" to AppConstants.UI.DEFAULT_PAGE_SIZE,//条数-默认10,最大100
//                "orderType" to type,//订单类型;limit-限价；market-市价
//                "symbol" to symbol,//交易对 可传id或类似BTC-USDT
//                "status" to status//订单状态
//        )
        apiService.getOrders(active, before, startAt, endAt, AppConstants.UI.DEFAULT_PAGE_SIZE, type, symbol, status, side)
    }

    suspend fun order(price: String, size: String = "", funds: String = "", side: String, symbol: String, type: String) = parseResponse {

        if (OrderType.MARKET.type.equals(type)){
            val body = mapOf(
                    "side" to side,//buy（买） 或 sell（卖）
                    "symbol" to symbol,//交易对 可传id或类似BTC-USDT
                    "type" to type,//订单类型;limit-限价；market-市价
                    if (OrderSide.BUY.side.equals(side)) "funds" to funds else "size" to size
            )
            apiService.order(body)
        } else {
            val body = mapOf(
                    "price" to price,// 交易价格
                    "size" to size,//数量-限价必传，市价可选
                    "side" to side,//buy（买） 或 sell（卖）
                    "symbol" to symbol,//交易对 可传id或类似BTC-USDT
                    "type" to type//订单类型;limit-限价；market-市价
            )
            apiService.order(body)
        }

    }

    suspend fun cancelOrder(orderId: Long) = parseResponse {
        apiService.cancelOrder(orderId)
    }
    suspend fun getZjList(giveCoinType: Int) = parseResponse {
        val body = mapOf(
                "giveCoinType" to giveCoinType,//失效/有效赠金 1:有效,2失效（也可以理解为 1：赠金账户 2：使用记录）（默认1）
                "loginToken" to UserInfoManager.getToken() //拉取条数，最大100,默认50
        )
        apiService.getZjList(body)
    }
    suspend fun useZj(id: Int) = parseResponse {
        val body = mapOf(
                "id" to id,//失效/有效赠金 1:有效,2失效（也可以理解为 1：赠金账户 2：使用记录）（默认1）
                "loginToken" to UserInfoManager.getToken() //拉取条数，最大100,默认50
        )
        apiService.useZj(body)
    }
    //symbol:交易对 可传id或类似BTC-USDT
    suspend fun get24HData(symbol: String) = parseResponse {
        apiService.get24HData(symbol)
    }

    suspend fun getLatestData(symbol: String) = parseResponse {
        val body = mapOf(
                "symbol" to symbol,//交易对 可传id或类似BTC-USDT
                "limit" to Constant.MAX_SIZE //拉取条数，最大100,默认50
        )
        apiService.getLatestData(body)
    }

    suspend fun getDepthData(symbol: String) = parseResponse {
        apiService.getDepthData("LEVEL2_20", symbol)
    }

    suspend fun getKlineHistoryData(granularity: String, symbol: String) = parseResponse {
        val body = mapOf(
                "granularity" to granularity,//粒度
                "limit" to 500, //条数–可选参数，最大限制500
                "symbol" to symbol
        )
        apiService.getKlineHistoryData(body)
    }
    suspend fun getNetValue(symbol: String) = parseResponse {
        var body = mapOf<String, Any>(
                "tradeId" to symbol //交易对id
        )
        body = EncryptUtils.encrypt(body)
        apiService.getNetValue(body)
    }
    suspend fun getCurrencyPair() = parseResponse {
        var body = mapOf<String, Any>(
                "type" to 1
        )
        body = EncryptUtils.encrypt(body)
        apiService.getCurrencyPair(body)
    }
    suspend fun aliVerify(entity: AliManMachineEntity) = parseResponse {
        var body = mutableMapOf<String, Any>(
                "nvcSessionId" to entity.nvcSessionId,
                "nvcSig" to entity.nvcSig,
                "nvcToken" to entity.nvcToken,
                "nvcScene" to entity.nvcScene
        )
        log("aliVerify",body)
        body = EncryptUtils.encrypt(body)
        apiService.aliVerify(body)
    }
    suspend fun aliVerifyFindPwd(loginParam: LoginParam,entity: AliManMachineEntity) = parseResponse {
        var body = mutableMapOf<String, Any>(
                "phoneOrEmail" to loginParam.username,
                "areaCode" to loginParam.areaCode,
                "nvcSessionId" to entity.nvcSessionId,
                "nvcSig" to entity.nvcSig,
                "nvcToken" to entity.nvcToken,
                "nvcScene" to entity.nvcScene
        )
        log("aliVerifyFindPwd",body)
        body = EncryptUtils.encrypt(body)
        apiService.aliVerifyFindPwd(body)
    }
    suspend fun login(loginParam: LoginParam) = parseResponse {
        var body = mapOf<String, Any>(
                "email" to loginParam.username,
                "password" to loginParam.password,
                "phoneCode" to loginParam.phoneCode,
                "googleCode" to loginParam.googleCode,
                "imei" to loginParam.imei,
                "areaCode" to loginParam.areaCode
        )
        log("login",body)
        body = EncryptUtils.encrypt(body)
        body["random"]=loginParam.random
        body["appVersion"]= loginParam.appVersion
        apiService.login(body)
    }
    suspend fun register(loginParam: LoginParam) = parseResponse {
        var body = mapOf<String, Any>(
                "email" to loginParam.username,
                "password" to loginParam.password,
                "code" to loginParam.phoneCode,
                "intro_user" to loginParam.inviteCode,
                "areaCode" to if(loginParam.isPhone) loginParam.areaCode else "",
                "type" to if(loginParam.isPhone) 1 else 2//1手机注册，2邮箱注册
        )
        log("register",body)
        body = EncryptUtils.encrypt(body)
        apiService.register(body)
    }
    suspend fun sendSms(type:Int,phone:String="",areaCode:String="",entity: AliManMachineEntity) = parseResponse {
        var body = mapOf<String, Any>(
                "type" to type,
                "phone" to phone,
                "areaCode" to areaCode,
                "nvcSessionId" to entity.nvcSessionId,
                "nvcSig" to entity.nvcSig,
                "nvcToken" to entity.nvcToken,
                "nvcScene" to entity.nvcScene
        )
        log("sendSms",body)
        body = EncryptUtils.encrypt(body)
        if(UserInfoManager.getUserInfo()!=null&&!TextUtils.isEmpty(UserInfoManager.getUserInfo().loginToken)){
            body["loginToken"]=UserInfoManager.getUserInfo().loginToken
        }
        apiService.sendSms(body)
    }
    suspend fun sendLoginEmail(loginParam: LoginParam,entity: AliManMachineEntity) = parseResponse {
        var body = mapOf<String, Any>(
                "type" to loginParam.messageType,
                "email" to loginParam.username,
                "nvcSessionId" to entity.nvcSessionId,
                "nvcSig" to entity.nvcSig,
                "nvcToken" to entity.nvcToken,
                "nvcScene" to entity.nvcScene
        )
        log("sendLoginEmail",body)
        body = EncryptUtils.encrypt(body)
        apiService.sendLoginEmail(body)
    }
    suspend fun checkRegisterAccount(loginParam: LoginParam) = parseResponse {
        var body = mapOf<String, Any>(
                "areaCode" to loginParam.areaCode,
                "email" to loginParam.username,
                "messageType" to loginParam.messageType,//9 找回密码 12 注册
                "password" to loginParam.password,
                "type" to if(loginParam.isPhone) 1 else 2,//1手机注册，2邮箱注册 如果是注册为必填项
                "intro_user" to loginParam.inviteCode,//推荐人用户id
                "inviteRedId" to ""//邀请红包id
        )
        log("checkRegisterAccount",body)
        body = EncryptUtils.encrypt(body)
        apiService.checkRegisterAccount(body)
    }
    suspend fun checkRegisterCode(loginParam: LoginParam) = parseResponse {
        var body = mapOf<String, Any>(
                "areaCode" to loginParam.areaCode,
                "email" to loginParam.username,
                "code" to loginParam.phoneCode,
                "type" to if(loginParam.isPhone) 1 else 2//1手机注册，2邮箱注册 如果是注册为必填项
        )
        log("checkRegisterCode",body)
        body = EncryptUtils.encrypt(body)
        apiService.checkRegisterCode(body)
    }
    suspend fun resetPwdVerifyCode(loginParam: LoginParam) = parseResponse {
        var body = mapOf<String, Any>(
                "email" to loginParam.username,
                "code" to loginParam.phoneCode
        )
        log("resetPwdVerifyCode",body)
        body = EncryptUtils.encrypt(body)
        apiService.resetPwdVerifyCode(body)
    }
    suspend fun resetPwd(loginParam: LoginParam) = parseResponse {
        var body = mapOf<String, Any>(
                "email" to loginParam.username,
                "newpassword" to loginParam.password
        )
        log("resetPwd",body)
        body = EncryptUtils.encrypt(body)
        apiService.resetPwd(body)
    }
    suspend fun modifyLoginPwd(oldPwd: String,newPwd: String,smsCode:String,googleCode:String) = parseResponse {
        var body = mapOf<String, Any>(
                "type" to "1",
                "password1" to oldPwd,
                "password2" to newPwd,
                "vcode" to smsCode,
                "totpCode" to googleCode
        )
        log("modifyLoginPwd",body)
        body = EncryptUtils.encrypt(body)
        body["loginToken"] = UserInfoManager.getToken()
        apiService.modifyLoginPwd(body)
    }
    suspend fun logout() = parseResponse {
        var body = mapOf<String, Any>(
                "type" to 1
        )
        body = EncryptUtils.encrypt(body)
        body["loginToken"] = UserInfoManager.getToken()
        apiService.logout(body)
    }
    suspend fun bindEmail(email: String) = parseResponse {
        var body = mutableMapOf<String, Any>(
                "email" to email
        )
        body = EncryptUtils.encrypt(body)
        body["loginToken"] = UserInfoManager.getToken()
        apiService.bindEmail(body)
    }
    suspend fun getWithdrawBankList() = parseResponse {
        var body = mutableMapOf<String, Any>(
                "type" to 1
        )
        body = EncryptUtils.encrypt(body)
        body["loginToken"] = UserInfoManager.getToken()
        apiService.getWithdrawBankList(body)
    }
    suspend fun sendSmsNoManMachine(type: String) = parseResponse {
        var body = mutableMapOf<String, Any>(
                "type" to type
        )
        body = EncryptUtils.encrypt(body)
        body["loginToken"] = UserInfoManager.getToken()
        apiService.sendSmsNoManMachine(body)
    }
    suspend fun withDrawCny(bodyStr: String) = parseResponse {
        var body = mutableMapOf<String, Any>(
                "body" to bodyStr
        )
        body["loginToken"] = UserInfoManager.getToken()
        apiService.withDrawCny(body)
    }
    suspend fun overseaSeniorKyc() = parseResponse {
        var body = mapOf<String, Any>(
                "type" to 1
        )
        body = EncryptUtils.encrypt(body)
        body["loginToken"] = UserInfoManager.getToken()
        apiService.overseaSeniorKyc(body)
    }
    suspend fun getAreaCode() = parseResponse {
        var body = mapOf<String, Any>(
                "type" to 1
        )
        body = EncryptUtils.encrypt(body)
        apiService.getAreaCode(body)
    }
    suspend fun getMarketArea() = parseResponse {
        apiService.getMarketArea()
    }
    suspend fun getMarketPair() = parseResponse {
        var body = mapOf<String, Any>(
                "type" to 1
        )
        body = EncryptUtils.encrypt(body)
        apiService.getMarketPair(body)
    }

    suspend  fun getNewCoins(state:Int) = parseResponse {
        var body = mutableMapOf<String, Any>(
                "state" to state
        )
        log("getNewCoins",body)
        apiService.getNewCoins(body)
    }

    suspend  fun getTaskCenterBanner() = parseResponse {
        apiService.getTaskCenterBanner()
    }


    suspend  fun getTaskCenterList(classify:Int,loginToken:String) = parseResponse {
        val body = mutableMapOf<String, Any>(
                "classify" to classify,
                "loginToken" to loginToken
        )
        apiService.getTaskCenterList(body)
    }

    suspend  fun getTaskCenterList(loginToken:String) = parseResponse {
        val body = mutableMapOf<String, Any>(
                "loginToken" to loginToken
        )
        apiService.getTaskCenterList(body)
    }

    suspend  fun getMyTaskList(loginToken:String) = parseResponse {
        val body = mutableMapOf<String, Any>(
                "loginToken" to loginToken
        )
        apiService.getMyTaskList(body)
    }
    suspend  fun getMyTask(id:Int,loginToken:String) = parseResponse {
        val body = mutableMapOf<String, Any>(
                "loginToken" to loginToken,
                "id" to id
        )
        apiService.getMyTask(body)
    }
    suspend  fun getMailDetails(farticleId:Long) = parseResponse {
        val body = mutableMapOf<String, Any>(
                "farticleId" to farticleId
        )
        apiService.getMailDetails(body)
    }
    suspend  fun getMailUnread() = parseResponse {
        val body = mutableMapOf<String, Any>(
        )
        apiService.getMailUnread(body)
    }

    fun log(function:String,param:Map<String,Any>){
        Logger.getInstance().debug(TAG,"$function  param:$param")
    }
}