package com.alexeymerov.radiostations.remote.interceptor

import com.alexeymerov.radiostations.remote.client.NetworkDefaults
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Base return type for server is XML, so we have to add JSON key for every request.
 * */
class JsonResponseInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val url = request.url.newBuilder()
            .addQueryParameter(NetworkDefaults.QUERY_RENDER_NAME, NetworkDefaults.QUERY_RENDER_JSON_PARAMETER)
            .build()

        val newRequest = request.newBuilder()
            .url(url)
            .build()

        return chain.proceed(newRequest)
    }

}