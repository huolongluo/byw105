package com.android.coinw.test.log

import androidx.lifecycle.ViewModelProvider
import huolongluo.byw.byw.base.BaseApp
import huolongluo.byw.log.Logger
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import org.json.JSONObject
import java.net.URLDecoder

class HttpLogInterceptor : Interceptor {

    private val logViewModel by lazy { ViewModelProvider(BaseApp.getSelf()).get(HttpLogViewModel::class.java) }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val sb = StringBuilder()
        Logger.getInstance().debug("发送请求: ${URLDecoder.decode(url, "utf-8")}")
        sb.append("${request.method}: ${URLDecoder.decode(url, "utf-8")}\n")
        request.headers.forEach { header ->
            Logger.getInstance().debug("请求header: $header")
            sb.append("header: $header \n")
        }
        val body = request.body
        if (body != null) {
            val buffer = Buffer()
            body.writeTo(buffer)
            val requestBodyStr = buffer.readUtf8()
            Logger.getInstance().debug("请求body: $requestBodyStr")
            sb.append("请求body: $requestBodyStr\n")
        }
        var response: Response? = null
        try {
            response = chain.proceed(request)
            if (response.isSuccessful) {
                //注意这里不能直接使用response.body.string(),否则流会关闭,会报异常
                val responseBody = response.peekBody(1024 * 1024).string()
                Logger.getInstance()
                    .debug("请求时间: $url(${(response.receivedResponseAtMillis - response.sentRequestAtMillis)}ms)")
                sb.append("请求时间: ${(response.receivedResponseAtMillis - response.sentRequestAtMillis)}ms\n")
                Logger.getInstance().debug("请求成功: ${responseBody}\n")
                try {
                    sb.append("请求成功: \n${JSONObject(responseBody).toString(2)}\n\n")
                } catch (t: Throwable) {
                    sb.append("请求成功: ${responseBody}\n")

                }
            } else {
                sb.append("请求失败: ${response.code} ${response.message}\n")
            }
            logViewModel.addLog(sb.toString())
            return response
        } catch (t: Throwable) {
            Logger.getInstance().error(t)
            sb.append("请求失败: ${t.message}\n")
            logViewModel.addLog(sb.toString())
            return response ?: chain.proceed(request)
        }
    }
}