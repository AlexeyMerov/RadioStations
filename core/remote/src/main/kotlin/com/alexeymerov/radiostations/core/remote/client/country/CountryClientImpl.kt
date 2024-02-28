package com.alexeymerov.radiostations.core.remote.client.country

import com.alexeymerov.radiostations.core.remote.api.CountryApi
import com.alexeymerov.radiostations.core.remote.mapper.response.ResponseMapper
import com.alexeymerov.radiostations.core.remote.response.CountryBody
import timber.log.Timber
import javax.inject.Inject

class CountryClientImpl @Inject constructor(
    private val countryApi: CountryApi,
    private val responseMapper: ResponseMapper,
) : CountryClient {

    override suspend fun requestAllCountries(): List<CountryBody> {
        return try {
            val response = countryApi.getAllCountries(fields = ALL_FIELDS)
            responseMapper.mapCountriesResponseBody(response)
        } catch (e: Exception) {
            Timber.e(e)
            emptyList()
        }
    }

    internal companion object {
        const val FIELD_NAME = "name"
        const val FIELD_IDD = "idd"
        const val FIELD_CCA2 = "cca2"

        const val ALL_FIELDS = "$FIELD_NAME,$FIELD_IDD,$FIELD_CCA2"
    }
}