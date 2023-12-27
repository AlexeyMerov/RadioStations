package com.alexeymerov.radiostations.core.remote.response


data class CountryBody(
    val name: Name,
    val cca2: String,
    val idd: Idd
) : ServerBodyType

data class Idd(
    val root: String,
    val suffixes: List<String>
)

data class Name(
    val common: String,
    val official: String,
    val nativeName: Map<String, NativeName>
)

data class NativeName(
    val official: String,
    val common: String
)
