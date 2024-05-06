package com.alexeymerov.radiostations.core.remote.client.country

import com.alexeymerov.radiostations.core.remote.CountriesQuery
import com.alexeymerov.radiostations.core.remote.mapper.response.ResponseMapper
import com.alexeymerov.radiostations.core.remote.response.CountryBody
import com.apollographql.apollo3.ApolloClient
import timber.log.Timber
import javax.inject.Inject

class CountryClientImpl @Inject constructor(
    private val apolloClient: ApolloClient,
    private val responseMapper: ResponseMapper
) : CountryClient {

    override suspend fun requestAllCountries(): List<CountryBody> {
        return try {
            val response = apolloClient.query(CountriesQuery()).execute()
            return responseMapper.mapCountriesResponseBody(response)
        } catch (e: Exception) {
            Timber.e(e)
            emptyList()
        }
    }
}