package com.android.legend.model

data class Page<T>(
    val pageNum: Int,
    val pageSize: Int,
    val totalSize: Int,
    val totalPages: Int,
    val result: List<T>
)