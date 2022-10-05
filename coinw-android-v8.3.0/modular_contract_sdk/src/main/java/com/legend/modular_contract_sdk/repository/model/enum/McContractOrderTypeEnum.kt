package com.legend.modular_contract_sdk.repository.model.enum

enum class McContractOrderTypeEnum(val requestValue:String) {
    MARKET("execute"),LIMIT("plan"),PLAN("planTrigger")
}