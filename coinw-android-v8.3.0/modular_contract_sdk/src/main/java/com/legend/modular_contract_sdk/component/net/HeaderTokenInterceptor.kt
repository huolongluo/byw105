package com.legend.modular_contract_sdk.component.net

import android.content.Context
import android.os.Build
import android.util.Log
import com.legend.modular_contract_sdk.api.ModularContractSDK
import com.legend.modular_contract_sdk.utils.McConfigurationUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

class HeaderTokenInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request =
            chain.request().newBuilder()
                .addHeader("cookie", "locale=" + ModularContractSDK.getCurrentLanguage())
                .addHeader("thirdAppId", ModularContractSDK.appId)
                .addHeader("thirdAppToken", ModularContractSDK.userToken)
                .build()

        val response = chain.proceed(request)

        return response
    }

}