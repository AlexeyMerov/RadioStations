package com.alexeymerov.radiostations.core.remote.response

import kotlinx.serialization.Serializable


@Serializable
data class CountryBody(
    val name: CountryName,
    val cca2: String,
    val idd: CountryIdd
)

@Serializable
data class CountryIdd(
    val root: String,
    val suffixes: List<String>
)

@Serializable
data class CountryName(
    val common: String,
    val official: String,
    val nativeName: Map<String, CountryNativeName>
)

@Serializable
data class CountryNativeName(
    val official: String,
    val common: String
)
