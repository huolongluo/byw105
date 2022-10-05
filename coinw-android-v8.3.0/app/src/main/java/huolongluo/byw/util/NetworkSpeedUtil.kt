package huolongluo.byw.util

import huolongluo.byw.helper.SSLHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.Request
import kotlin.system.measureTimeMillis

/**
 * 网络测速工具类
 * 用于计算接口的延迟、下载速度
 * 返回的字符串，不需要国际化
 */
object NetworkSpeedUtil {

    /**
     * 使用无缓存的httpclient
     */
    private val okHttpClient by lazy {
        SSLHelper.getBuilder(10, 30, 10)
                .cache(null)
                .retryOnConnectionFailure(false)
                .build()
    }


    /**
     * 接口调用失败时的错误码
     */
    const val NETWORK_ERROR = -1L

    suspend fun getSpeedText(url: String): String {
        val startTime = System.currentTimeMillis()
        var total = 0L // 文件的总大小，单位为b
        val speedTime = withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(url).header(SSLHelper.getHeadKey(), SSLHelper.getHeadValue(11, url)).build()
                val response = okHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    total = response.body?.contentLength() ?: 0
//                    // 网络请求的时间
//                    val requestTime = response.receivedResponseAtMillis() - response.sentRequestAtMillis()
                    System.currentTimeMillis() - startTime
                } else {
                    NETWORK_ERROR
                }
            } catch (e: Throwable) {
                NETWORK_ERROR
            }
        }
        return if (speedTime == -1L) { // 请求失败
            "失败"
        } else { // 成功
            val speed = (total / 1024.0) / (speedTime / 1000.0)  // 单位为kb/s
            if (speed < 1024) {
                "%.2fKB/s".format(speed)
            } else {
                "%.2fMB/s".format(speed / 1024.0) //单位为mb/s
            }
        }
    }

    suspend fun getPingText(url: String, params: Map<String, String>): String {
        // 失败返回失败提示的字符串
        val pingTime = getPing(url, params)
        return if (pingTime != -1L) "$pingTime ms  状态: (成功)" else "状态: (失败)"
    }

    suspend fun getPing(url: String, params: Map<String, String>) = withContext(Dispatchers.IO) {
        try {
            val bodyBuilder = FormBody.Builder()
            params.entries.forEach {
                bodyBuilder.add(it.key, it.value)
            }
            val request = Request.Builder()
                    .url(url)
                    .header(SSLHelper.getHeadKey(), SSLHelper.getHeadValue(11, url))
                    .post(bodyBuilder.build())
                    .build()
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                // 网络请求的时间
                response.receivedResponseAtMillis - response.sentRequestAtMillis
            } else {
                NETWORK_ERROR
            }
        } catch (e: Throwable) {
            NETWORK_ERROR
        }
    }

    suspend fun getPing(request: suspend () -> Unit) = try {
        measureTimeMillis { request() }
    } catch (e: Throwable) {
        NETWORK_ERROR
    }

}