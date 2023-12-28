package com.alexeymerov.radiostations.core.data.mapper.country

import com.alexeymerov.radiostations.core.database.entity.CountryEntity
import com.alexeymerov.radiostations.core.remote.response.CountryBody
import timber.log.Timber
import javax.inject.Inject

class CountryMapperImpl @Inject constructor() : CountryMapper {

    override suspend fun mapCountries(list: List<CountryBody>): List<CountryEntity> {
        val result = mutableListOf<CountryEntity>()
        list.asSequence()
            .filter {
                it.idd.root.isNotEmpty()
                    && it.cca2.isNotEmpty()
                    && it.name.nativeName.isNotEmpty()
            }
            .forEach { result.add(mapCountry(it)) }
        return result
    }

    private fun mapCountry(body: CountryBody): CountryEntity {
        var nativeName = ""

        try {
            val nativeNameFirstEntry = body.name.nativeName.entries.first().value
            nativeName = nativeNameFirstEntry.common.ifEmpty { nativeNameFirstEntry.official }
        } catch (e: Exception) {
            Timber.e(e, "Country native name is incorrect for ${body.name}")
        }

        var phoneCode = body.idd.root
        if (body.idd.suffixes.size == 1) {
            phoneCode += body.idd.suffixes[0]
        }

        return CountryEntity(
            tag = body.cca2,
            nameEnglish = body.name.common.ifEmpty { body.name.official },
            nameNative = nativeName,
            phoneCode = phoneCode,
        )
    }

}