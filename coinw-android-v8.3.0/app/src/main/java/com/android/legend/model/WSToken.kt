package com.android.legend.model

data class WSToken(
        val token: String,
        val endpoint: String,
        val protocol: String,
        val timestamp: Long,
        val expiredTime: Long,
        val pingInterval: Long
) {
    fun getUrl() = endpoint.replace("wss://", "https://").replace("ws://", "http://")
}