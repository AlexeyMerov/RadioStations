package com.alexeymerov.radiostations.core.remote

import java.io.BufferedReader
import java.io.InputStreamReader

internal object TestConst {

    const val CATEGORIES_RESPONSE_200 = "response_200_categories.json"
    const val CATEGORIES_TOP40_RESPONSE_200 = "response_200_top40.json"
    const val AUDIO_RESPONSE_200 = "response_200_audio.json"
    const val COUNTRIES_CA_UK_RESPONSE_200 = "response_200_countries_ca_uk.json"

    const val CATEGORY_NAME_WORLD_MUSIC = "World Music"
    const val VALID_MEDIA_TYPE = "mp3"

    const val TYPE_AUDIO = "audio"
    const val TYPE_LINK = "link"

    fun readResourceFile(fileName: String): String {
        val classLoader = javaClass.classLoader
        val inputStream = classLoader?.getResourceAsStream(fileName)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        return bufferedReader.use { it.readText() }
    }
}
