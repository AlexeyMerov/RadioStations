package com.alexeymerov.radiostations.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response


class JsonResponseInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val url = request.url.newBuilder()
            .addQueryParameter("render", "json")
            .build()

        val newRequest = request.newBuilder()
            .url(url)
            .build()

        return chain.proceed(newRequest)
    }

}