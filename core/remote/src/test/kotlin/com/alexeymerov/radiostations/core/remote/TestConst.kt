package com.alexeymerov.radiostations.core.remote

import com.alexeymerov.radiostations.core.remote.client.NetworkDefaults
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import java.io.BufferedReader
import java.io.InputStreamReader

internal object TestConst {

    const val CATEGORIES_RESPONSE_200 = "response_200_categories.json"
    const val CATEGORIES_TOP40_RESPONSE_200 = "response_200_top40.json"
    const val AUDIO_RESPONSE_200 = "response_200_audio.json"
    const val COUNTRIES_CA_UK_RESPONSE_200 = "response_200_countries_ca_uk.json"
    const val EMPTY_RESPONSE = "[{},{}]"

    const val CATEGORY_NAME_WORLD_MUSIC = "World Music"
    const val VALID_MEDIA_TYPE = "mp3"

    const val TYPE_AUDIO = "audio"
    const val TYPE_LINK = "link"

    const val URL_ERROR = "error"
    val HEADERS = headersOf(HttpHeaders.ContentType, "application/json")

    fun readResourceFile(fileName: String): String {
        val classLoader = javaClass.classLoader
        val inputStream = classLoader?.getResourceAsStream(fileName)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        return bufferedReader.use { it.readText() }
    }

    fun getTestClient(jsonString: String, returnError: Boolean = false): HttpClient {
        val engine = MockEngine { request ->
            println("request.url.encodedPath ->" + request.url.encodedPath)

            if (returnError) {
                respondBadRequest()
            } else {
                when (request.url.encodedPath) {
                    "/${URL_ERROR}" -> respondBadRequest()
                    else -> respond(jsonString, HttpStatusCode.OK, HEADERS)
                }
            }
        }

        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(NetworkDefaults.getJson())
            }
        }
    }
}
