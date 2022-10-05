package com.android.legend.base

import huolongluo.byw.BuildConfig
import huolongluo.byw.helper.SSLHelper
import huolongluo.byw.util.ApkUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

abstract class BaseRetrofitClient {

    companion object {
        private const val TIME_OUT = 15
    }

    val client: OkHttpClient by lazy {
        val builder = SSLHelper.getBuilder(10, 10, 10) //
        handleBuilder(builder)
        builder.connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)

        if (BuildConfig.ENV_DEV){
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }

        builder.build()
    }

    protected abstract fun handleBuilder(builder: OkHttpClient.Builder)

    inline fun <reified T> getService(baseUrl: String): T {
        return Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build().create(T::class.java)
    }
}