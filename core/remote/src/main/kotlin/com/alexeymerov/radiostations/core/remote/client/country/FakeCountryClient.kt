package com.alexeymerov.radiostations.core.remote.client.country

import com.alexeymerov.radiostations.core.remote.response.CountryBody

class FakeCountryClient : CountryClient {

    val countries = mutableListOf<CountryBody>()

    private val correctNativeName = "United Kingdom"
    private val correctName = "United Kingdom"
    private val correctIdd = "+44"

    init {
        repeat(10) {
            countries.add(
                CountryBody(
                    countryCode = "A" + (it + 65).toChar(),
                    nameNative = correctNativeName,
                    nameEnglish = correctName,
                    phoneCode = correctIdd
                )
            )
        }
    }

    override suspend fun requestAllCountries(): List<CountryBody> {
        return countries
    }
}