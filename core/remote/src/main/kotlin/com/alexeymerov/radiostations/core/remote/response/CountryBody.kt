package com.alexeymerov.radiostations.core.remote.response


data class CountryBody(
    val name: CountryName,
    val cca2: String,
    val idd: CountryIdd
) : ServerBodyType

data class CountryIdd(
    val root: String,
    val suffixes: List<String>
)

data class CountryName(
    val common: String,
    val official: String,
    val nativeName: Map<String, CountryNativeName>
)

data class CountryNativeName(
    val official: String,
    val common: String
)
