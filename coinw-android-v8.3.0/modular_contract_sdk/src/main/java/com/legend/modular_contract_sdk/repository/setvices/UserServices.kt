package com.legend.modular_contract_sdk.repository.setvices

import com.legend.modular_contract_sdk.component.net.RetrofitInstance
import com.legend.modular_contract_sdk.repository.model.BaseResp
import com.legend.modular_contract_sdk.repository.model.ContractAssetInfo
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserServices {

    companion object {
        private val mUserService: UserServices
            by lazy { RetrofitInstance.create(UserServices::class.java) }

        fun instance(): UserServices = mUserService
    }

    @GET("v1/futuresc/thirdClient/trade/accountsCountInfo/{positionModelType}")
    suspend fun fetchAccountInfo(@Path("positionModelType") positionModelType: String = "2"): BaseResp<ContractAssetInfo>

}