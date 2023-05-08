package com.alexeymerov.radiostations.data.mapper

import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.db.entity.StationEntity
import com.alexeymerov.radiostations.data.remote.response.ResponseBody

interface EntityCategoryMapper {

    suspend fun mapCategoryResponseToEntity(list: List<ResponseBody>, parentUrl: String): List<CategoryEntity>

    suspend fun mapCategoryWithStationsResponseToMap(list: List<ResponseBody>, parentUrl: String): HashMap<CategoryEntity, List<StationEntity>?>

    suspend fun mapUrlForStation(parentUrl: String, categoryEntity: CategoryEntity): String

}