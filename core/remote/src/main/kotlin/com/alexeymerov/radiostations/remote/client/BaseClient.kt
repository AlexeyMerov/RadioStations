package com.alexeymerov.radiostations.remote.client

import com.alexeymerov.radiostations.remote.interceptor.JsonResponseInterceptor
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


abstract class BaseClient<T>(val apiService: T)

object NetworkDefaults {

    const val QUERY_RENDER_NAME = "render"
    const val QUERY_RENDER_JSON_PARAMETER = "json"
    const val TYPE_AUDIO = "audio"
    const val TYPE_LINK = "link"
    val REGEX_VALID_URL = Regex("(http(s)?)://[(www.)?a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)")

    fun getJsonAdapterFactory() = KotlinJsonAdapterFactory()

    fun getMoshi(factory: JsonAdapter.Factory): Moshi = Moshi.Builder().add(factory).build()

    fun getConverterFactory(moshi: Moshi): Converter.Factory = MoshiConverterFactory.create(moshi).asLenient()

    fun getForceJsonInterceptor() = JsonResponseInterceptor()

    fun getOkHttpClient(forTest: Boolean = false, vararg interceptors: Interceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()

        if (forTest) {
            builder.connectTimeout(1, TimeUnit.MINUTES)
            builder.readTimeout(1, TimeUnit.MINUTES)
        }

        interceptors.forEach { builder.addInterceptor(it) }

        return builder.build()
    }
}
