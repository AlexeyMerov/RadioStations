package com.alexeymerov.radiostations.core.data.mapper.country

import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.database.entity.CountryEntity
import com.alexeymerov.radiostations.core.remote.response.CountryBody
import timber.log.Timber
import javax.inject.Inject

class CountryMapperImpl @Inject constructor() : CountryMapper {

    private val correctIddRootRegex = "^\\+\\d+$".toRegex()
    private val correctCca2Regex = "^[A-Z]{2}$".toRegex()
    private val correctIddSuffix = "^\\d+$".toRegex()

    override suspend fun mapCountries(list: List<CountryBody>): List<CountryEntity> {
        return list
            .filter {
                it.idd.root.matches(correctIddRootRegex)
                    && it.cca2.matches(correctCca2Regex)
                    && it.name.official.isNotEmpty()
            }
            .map(::mapCountry)
    }

    private fun mapCountry(body: CountryBody): CountryEntity {
        var nativeName = String.EMPTY

        try {
            val nativeNameFirstEntry = body.name.nativeName.entries.first().value
            val nativeOfficialName = nativeNameFirstEntry.official
            if (nativeOfficialName.isNotEmpty()) {
                nativeName = nativeNameFirstEntry.common.ifEmpty { nativeNameFirstEntry.official }.trim()
            }
        } catch (e: Exception) {
            Timber.e(e, "Country native name is incorrect for ${body.name}")
        }

        var phoneCode = body.idd.root
        if (body.idd.suffixes.size == 1 && body.idd.suffixes.first().matches(correctIddSuffix)) {
            phoneCode += body.idd.suffixes[0]
        }

        return CountryEntity(
            tag = body.cca2.trim(),
            nameEnglish = body.name.common.ifEmpty { body.name.official }.trim(),
            nameNative = nativeName,
            phoneCode = phoneCode.trim(),
        )
    }

}