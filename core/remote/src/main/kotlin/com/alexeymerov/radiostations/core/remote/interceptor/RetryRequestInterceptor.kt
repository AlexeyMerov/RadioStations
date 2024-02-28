package com.alexeymerov.radiostations.core.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class RetryRequestInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)

        var retryCount = 0
        while (!response.isSuccessful && retryCount < MAX_RETRY) {
            Timber.d("Retry request. $retryCount attempt")
            retryCount++

            response.close()
            response = chain.proceed(request);
        }

        if (retryCount == MAX_RETRY) {
            Timber.e("All retry attempts failed.")
        }

        return response
    }

    private companion object {
        const val MAX_RETRY = 3
    }

}