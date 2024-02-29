package com.alexeymerov.radiostations.core.remote.client

import com.alexeymerov.radiostations.core.remote.interceptor.JsonResponseInterceptor
import com.alexeymerov.radiostations.core.remote.interceptor.RetryRequestInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration


object NetworkDefaults {
    const val COUNTRIES_URL = "https://restcountries.com/v3.1/"

    const val MEDIA_TYPE_STRING = "application/json"

    const val QUERY_RENDER_NAME = "render"
    const val QUERY_RENDER_JSON_PARAMETER = "json"
    const val TYPE_AUDIO = "audio"
    const val TYPE_LINK = "link"

    val REGEX_VALID_URL = Regex("(http(s)?)://[(www.)?a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@;:%_+.~#?&/=]*)")

    fun getJson() = Json { ignoreUnknownKeys = true }

    fun getMediaType() = MEDIA_TYPE_STRING.toMediaType()

    fun getConverterFactory(json: Json, mediaType: MediaType): Converter.Factory = json.asConverterFactory(mediaType)

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
        val json = getJson()
        val mediaType = getMediaType()
        val converterFactory = getConverterFactory(json, mediaType)
        val jsonInterceptor = getJsonInterceptor()
        val okHttpClient = getOkHttpClient(forTest = true, jsonInterceptor)

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(converterFactory)
            .build()
    }
}
