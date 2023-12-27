package com.alexeymerov.radiostations.core.data.mapper.country

import com.alexeymerov.radiostations.core.database.entity.CountryEntity
import com.alexeymerov.radiostations.core.remote.response.CountryBody
import timber.log.Timber
import javax.inject.Inject

class CountryMapperImpl @Inject constructor() : CountryMapper {

    override suspend fun mapCountries(list: List<CountryBody>): List<CountryEntity> {
        val result = mutableListOf<CountryEntity>()
        list.forEach { result.add(mapCountry(it)) }
        return result
    }

    private fun mapCountry(body: CountryBody): CountryEntity {
        var nativeName = ""

        try {
            val name = body.name.nativeName.entries.first().value
            nativeName = name.common.ifEmpty { name.official }
        } catch (e: Exception) {
            Timber.e(e, "Country native name is incorrect")
        }

        var phoneCode = body.idd.root
        if (body.idd.suffixes.isNotEmpty()) {
            phoneCode += body.idd.suffixes[0]
        }

        return CountryEntity(
            tag = body.cca2.lowercase(),
            nameEnglish = body.name.official,
            nameNative = nativeName,
            phoneCode = phoneCode,
        )
    }

}