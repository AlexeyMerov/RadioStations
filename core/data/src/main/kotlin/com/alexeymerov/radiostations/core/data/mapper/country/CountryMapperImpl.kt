package com.alexeymerov.radiostations.core.data.mapper.country

import com.alexeymerov.radiostations.core.database.entity.CountryEntity
import com.alexeymerov.radiostations.core.remote.response.CountryBody
import javax.inject.Inject

class CountryMapperImpl @Inject constructor() : CountryMapper {

    private val correctCountryCodeRegex = "^[A-Z]{2}$".toRegex() //Cca2
    private val correctPhoneCodeRegex = "^\\d+$".toRegex() //Idd

    override suspend fun mapCountries(list: List<CountryBody>): List<CountryEntity> {
        return list
            .filter {
                it.phoneCode.matches(correctPhoneCodeRegex)
                    && it.countryCode.matches(correctCountryCodeRegex)
                    && it.nameEnglish.isNotEmpty()
            }
            .map(::mapCountry)
    }

    private fun mapCountry(body: CountryBody): CountryEntity {
        return CountryEntity(
            tag = body.countryCode.trim(),
            nameEnglish = body.nameEnglish.trim(),
            nameNative = body.nameNative.trim(),
            phoneCode = "+${body.phoneCode.trim()}"
        )
    }

}