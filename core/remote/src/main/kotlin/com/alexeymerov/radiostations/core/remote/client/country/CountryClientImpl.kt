package com.alexeymerov.radiostations.core.remote.client.country

import com.alexeymerov.radiostations.core.remote.di.Backend
import com.alexeymerov.radiostations.core.remote.di.Server
import com.alexeymerov.radiostations.core.remote.mapper.response.ResponseMapper
import com.alexeymerov.radiostations.core.remote.response.CountryBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import timber.log.Timber
import javax.inject.Inject

class CountryClientImpl @Inject constructor(
    @Backend(Server.Countries) private val httpClient: HttpClient,
    private val responseMapper: ResponseMapper,
) : CountryClient {

    override suspend fun requestAllCountries(): List<CountryBody> {
        return try {
            val response = httpClient.get(URL_PATH_ALL) { parameter(QUERY_FIELDS, ALL_FIELDS) }
            val body = response.body<List<CountryBody>>()
            responseMapper.mapCountriesResponseBody(response, body)
        } catch (e: Exception) {
            Timber.e(e)
            emptyList()
        }
    }

    internal companion object {
        private const val URL_PATH_ALL = "all"

        private const val FIELD_NAME = "name"
        private const val FIELD_IDD = "idd"
        private const val FIELD_CCA2 = "cca2"

        const val QUERY_FIELDS = "fields"
        const val ALL_FIELDS = "$FIELD_NAME,$FIELD_IDD,$FIELD_CCA2"
    }
}