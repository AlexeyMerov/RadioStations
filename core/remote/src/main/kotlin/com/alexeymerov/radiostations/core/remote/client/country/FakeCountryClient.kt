package com.alexeymerov.radiostations.core.remote.client.country

import com.alexeymerov.radiostations.core.remote.response.CountryBody
import com.alexeymerov.radiostations.core.remote.response.CountryIdd
import com.alexeymerov.radiostations.core.remote.response.CountryName
import com.alexeymerov.radiostations.core.remote.response.CountryNativeName

class FakeCountryClient : CountryClient {

    val countries = mutableListOf<CountryBody>()

    private val correctNativeName = CountryNativeName(
        official = "United Kingdom of Great Britain and Northern Ireland",
        common = "United Kingdom"
    )

    private val correctName = CountryName(
        common = "United Kingdom",
        official = "United Kingdom of Great Britain and Northern Ireland",
        nativeName = mapOf("eng" to correctNativeName)
    )

    private val correctIdd = CountryIdd(
        root = "+4",
        suffixes = listOf("4")
    )

    init {
        repeat(10) {
            countries.add(
                CountryBody(
                    cca2 = "A" + (it + 65).toChar(),
                    name = correctName,
                    idd = correctIdd
                )
            )
        }
    }

    override suspend fun requestAllCountries(): List<CountryBody> {
        return countries
    }
}