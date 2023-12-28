package com.alexeymerov.radiostations.core.domain.mapper.country

import com.alexeymerov.radiostations.core.database.entity.CountryEntity
import com.alexeymerov.radiostations.core.dto.CountryDto

interface DtoCountryMapper {

    fun mapEntityToDto(list: List<CountryEntity>): List<CountryDto>

    fun mapEntityToDto(entity: CountryEntity): CountryDto
}