package com.android.legend.model

data class Page2<T>(
        val after:Long,
        val before:Long,
        val hasMore:Boolean,
        val result:T
)