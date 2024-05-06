package com.alexeymerov.radiostations.core.remote.response


data class CountryBody(
    val countryCode: String,
    val nameEnglish: String,
    val nameNative: String,
    val phoneCode: String
)