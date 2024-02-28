package com.alexeymerov.radiostations.core.remote.client

import com.alexeymerov.radiostations.core.remote.interceptor.JsonResponseInterceptor
import com.alexeymerov.radiostations.core.remote.interceptor.RetryRequestInterceptor
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration


object NetworkDefaults {
    const val COUNTRIES_URL = "https://restcountries.com/v3.1/"

    const val QUERY_RENDER_NAME = "render"
    const val QUERY_RENDER_JSON_PARAMETER = "json"
    const val TYPE_AUDIO = "audio"
    const val TYPE_LINK = "link"
    val REGEX_VALID_URL = Regex("(http(s)?)://[(www.)?a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@;:%_+.~#?&/=]*)")

    fun getJsonAdapterFactory() = KotlinJsonAdapterFactory()

    fun getMoshi(factory: JsonAdapter.Factory): Moshi = Moshi.Builder().add(factory).build()

    fun getConverterFactory(moshi: Moshi): Converter.Factory = MoshiConverterFactory.create(moshi).asLenient()

    fun getJsonInterceptor() = JsonResponseInterceptor()

    fun getOkHttpClient(forTest: Boolean = false, vararg interceptors: Interceptor): OkHttpClient {
        val builder = OkHttpClient.Builder().apply {

            val timeoutDuration = 15.seconds.toJavaDuration()
            connectTimeout(timeoutDuration)
            readTimeout(timeoutDuration)
            writeTimeout(timeoutDuration)

            retryOnConnectionFailure(true)

            if (!forTest) addInterceptor(RetryRequestInterceptor())
            interceptors.forEach { addInterceptor(it) }
        }

        return builder.build()
    }

    fun getTestRetrofit(baseUrl: HttpUrl): Retrofit {
        val jsonFactory = getJsonAdapterFactory()
        val moshi = getMoshi(jsonFactory)
        val moshiConverterFactory = getConverterFactory(moshi)
        val jsonInterceptor = getJsonInterceptor()
        val okHttpClient = getOkHttpClient(forTest = true, jsonInterceptor)

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(moshiConverterFactory)
            .build()
    }
}
