package com.legend.modular_contract_sdk.component.net

import com.legend.common.event.TokenExpired
import com.legend.modular_contract_sdk.utils.JsonUtil
import com.legend.modular_contract_sdk.utils.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.lang.Exception

class TokenExpiredInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val response = chain.proceed(chain.request())
        if (response.code == 401){
            EventBus.getDefault().post(TokenExpired())
            return response
        }

        val size = response.body?.let {
            it.source().buffer.size
        }

        size?.let {
            val resp = response.peekBody(size).string()
            val codeStr = try {
                JSONObject(resp).getString("code")
            } catch (e: Exception) {
                ""
            }

            val codeInt = try {
                JSONObject(resp).getInt("code")
            } catch (e: Exception) {
                0
            }
            if (codeInt == 401 || "401" == codeStr){
                EventBus.getDefault().post(TokenExpired())
                return response
            }
        }

        return response
    }
}