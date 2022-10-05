package com.android.legend.model

data class SocketIOResponse(
    val data: String,
    val subject: String,
    val channel: String
)