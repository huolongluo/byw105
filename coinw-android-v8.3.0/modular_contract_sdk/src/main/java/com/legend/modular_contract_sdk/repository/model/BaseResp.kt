package com.legend.modular_contract_sdk.repository.model


const val SUCCESS_CODE = "0"

interface RespInterface<T> {
    fun isReqSuccess(): Boolean
    fun getReqCode() : String
    fun getReqMsg():String?
    fun getReqData():T?
}

open class BaseResp<T> : RespInterface<T> {
    val code = "0"
    val msg: String? = null
    var data: T? = null

    val isSuccess: Boolean
        get() = code == SUCCESS_CODE



    override fun isReqSuccess(): Boolean {
        return isSuccess
    }

    override fun getReqCode(): String {
        return code
    }

    override fun getReqMsg(): String? {
        return msg
    }

    override fun getReqData(): T? {
        return data
    }
}