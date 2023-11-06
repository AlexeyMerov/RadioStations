package com.alexeymerov.radiostations.common

/**
 * For some reason server returns http links.
 * */
fun String.httpsEverywhere() = replace("http:", "https:")

val String.Companion.EMPTY: String
    get() = ""

val String.Companion.SPACE: String
    get() = " "

fun Boolean.toInt() = if (this) 1 else 0