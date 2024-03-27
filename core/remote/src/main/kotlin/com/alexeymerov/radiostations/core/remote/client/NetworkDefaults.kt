package com.alexeymerov.radiostations.core.remote.client

import kotlinx.serialization.json.Json

object NetworkDefaults {
    const val RADIO_URL_HOST = "opml.radiotime.com"
    const val COUNTRIES_URL = "https://restcountries.com/v3.1/"

    const val QUERY_RENDER_NAME = "render"
    const val QUERY_RENDER_JSON_PARAMETER = "json"
    const val TYPE_AUDIO = "audio"
    const val TYPE_LINK = "link"

    val REGEX_VALID_URL = Regex("(http(s)?)://[(www.)?a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@;:%_+.~#?&/=]*)")

    fun getJson() = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }
}

sealed class ApiResponse<T> {
    class Success<T>(data: T) : ApiResponse<T>()
    class Error<T>(error: String) : ApiResponse<T>()
}
