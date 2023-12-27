package com.alexeymerov.radiostations.core.remote.client.country

import com.alexeymerov.radiostations.core.remote.response.CountryBody

interface CountryClient {

    suspend fun requestAllCountries(): List<CountryBody>

}