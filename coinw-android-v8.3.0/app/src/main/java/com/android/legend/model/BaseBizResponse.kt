package com.android.legend.model

data class BaseBizResponse<T>(
        val bizCode: String,//一般依据业务码，判断业务是否成功。 或国际化信息
        val bizVo: T,
        val bizMsg: String?,
        val success: Boolean
)