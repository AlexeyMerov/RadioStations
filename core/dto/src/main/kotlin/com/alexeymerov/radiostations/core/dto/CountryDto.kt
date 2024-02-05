package com.alexeymerov.radiostations.core.dto

data class CountryDto(
    val tag: String,
    val englishName: String,
    val nativeName: String?, // null if english and native are the same
    val phoneCode: Int,
    val flagUrl: String,
    val englishNameHighlights: Set<IntRange>? = null,
    val nativeNameHighlights: Set<IntRange>? = null,
)