package com.alexeymerov.radiostations.core.common

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