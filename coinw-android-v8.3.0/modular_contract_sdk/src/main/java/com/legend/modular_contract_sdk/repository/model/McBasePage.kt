package com.legend.modular_contract_sdk.repository.model

class McBasePage<T>{
    val nextId=0
    val prevId=0
    val total = 0
    var rows:List<T>? = null
}