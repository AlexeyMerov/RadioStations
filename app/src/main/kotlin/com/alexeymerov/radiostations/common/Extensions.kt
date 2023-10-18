package com.alexeymerov.radiostations.common

/**
 * For some reason server returns http links.
 * */
fun String.httpsEverywhere() = replace("http:", "https:")

val String.Companion.EMPTY: String
    get() = ""