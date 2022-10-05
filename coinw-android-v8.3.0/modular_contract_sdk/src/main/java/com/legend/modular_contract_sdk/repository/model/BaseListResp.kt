package com.legend.modular_contract_sdk.repository.model

class BaseListResp<T> : BaseResp<List<T>> {

    var maxPage = 0
    var currPage = 0


    constructor(maxPage: Int, currPage: Int, data: List<T>) {
        this.maxPage = maxPage
        this.currPage = currPage
        this.data = data
    }

}