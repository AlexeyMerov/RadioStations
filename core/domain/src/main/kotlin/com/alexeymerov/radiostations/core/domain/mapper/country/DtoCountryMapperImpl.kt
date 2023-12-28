package com.alexeymerov.radiostations.core.domain.mapper.country

import com.alexeymerov.radiostations.core.database.entity.CountryEntity
import com.alexeymerov.radiostations.core.dto.CountryDto
import javax.inject.Inject

class DtoCountryMapperImpl @Inject constructor() : DtoCountryMapper {

    override fun mapEntitytoDto(list: List<CountryEntity>): List<CountryDto> = list.map(::mapToDto)

    private fun mapToDto(entity: CountryEntity): CountryDto {
        val tag = entity.tag.lowercase()
        val nativeName = if (entity.nameNative == entity.nameEnglish) null else entity.nameNative
        val phoneCode = entity.phoneCode.replace("+", "").toInt()

        return CountryDto(
            tag = tag,
            englishName = entity.nameEnglish,
            nativeName = nativeName,
            phoneCode = phoneCode,
            flagUrl = FLAG_BASE_URL + tag + FLAG_EXT
        )
    }

    private companion object {
        const val FLAG_BASE_URL = "https://flagcdn.com/"
        const val FLAG_EXT = ".svg"
    }
}