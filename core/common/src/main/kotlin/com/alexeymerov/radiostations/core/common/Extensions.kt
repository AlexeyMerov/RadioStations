package com.alexeymerov.radiostations.core.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

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

private val textInsideBracketsRegex = "\\(.+\\)".toRegex()
private val blankInsideBracketsRegex = "\\((\\s|[^A-Za-z0-9])*\\)".toRegex()
private val parenthesisRegex = "\\(|\\)".toRegex()
fun String.extractTextFromRoundBrackets(): Pair<String, String?> {
    var bracketsText: String? = null
    val mainText = replace(blankInsideBracketsRegex, "").replace(textInsideBracketsRegex) { match ->
        bracketsText = match.value.replace(parenthesisRegex, String.EMPTY).trim()
        return@replace String.EMPTY
    }.trim()

    bracketsText?.let {
        val uniqueWords = it.split(String.SPACE).toSet()
        bracketsText = uniqueWords.joinToString(String.SPACE).trim()
    }
    return Pair(mainText, bracketsText)
}

@OptIn(ExperimentalEncodingApi::class)
fun Bitmap.toBase64(): String? = runCatching {
    val byteArray = ByteArrayOutputStream().use { stream ->
        compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.toByteArray()
    }
    return Base64.encode(byteArray)
}.getOrNull()

@OptIn(ExperimentalEncodingApi::class)
fun String.base64ToBitmap(): Bitmap? = runCatching {
    val byteArray = Base64.decode(this)
    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}.getOrNull()

fun String.substringAfterOrNull(delimiter: String): String? {
    val index = indexOf(delimiter)
    return if (index == -1) null else substring(index + delimiter.length, length)
}