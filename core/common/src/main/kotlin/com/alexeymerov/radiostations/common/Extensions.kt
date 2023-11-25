package com.alexeymerov.radiostations.common

/**
 * For some reason server returns http links.
 * */
fun String.httpsEverywhere() = replace("http:", "https:")

@Suppress("SameReturnValue")
val String.Companion.EMPTY: String
    get() = ""

@Suppress("SameReturnValue")
val String.Companion.SPACE: String
    get() = " "

fun Boolean.toInt() = if (this) 1 else 0