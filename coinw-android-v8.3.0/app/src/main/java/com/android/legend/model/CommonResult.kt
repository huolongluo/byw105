package com.android.legend.model

data class CommonResult<out T : Any>(
    val isSuccess: Boolean,
    val code:String,
    val data: T?,
    val message: String,
    val throwable: Throwable?
)

