package com.android.legend.model

import com.legend.modular_contract_sdk.repository.model.RespInterface

/**
 * 最外层通用结构
 */
data class BaseResponse<T> (
    val code: String,
    val data: T,
    val message: String?,
    val success: Boolean
) : RespInterface<T> {
    override fun isReqSuccess()= success || code == "200"

    override fun getReqCode() = code

    override fun getReqMsg(): String? = message

    override fun getReqData(): T? = data
}

data class BaseListResponse<T> (
        val code: String,
        val data: List<T>,
        val message: String?,
        val success: Boolean
) : RespInterface<List<T>> {
    override fun isReqSuccess()= success || code == "200"

    override fun getReqCode() = code

    override fun getReqMsg(): String? = message

    override fun getReqData(): List<T>? = data
}