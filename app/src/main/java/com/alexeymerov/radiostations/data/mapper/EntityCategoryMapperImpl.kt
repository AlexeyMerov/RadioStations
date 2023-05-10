package com.alexeymerov.radiostations.data.mapper

import com.alexeymerov.radiostations.common.httpsEverywhere
import com.alexeymerov.radiostations.data.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.db.entity.StationEntity
import com.alexeymerov.radiostations.data.remote.response.ChildrenBody
import com.alexeymerov.radiostations.data.remote.response.ResponseBody
import timber.log.Timber
import javax.inject.Inject

class EntityCategoryMapperImpl @Inject constructor() : EntityCategoryMapper {

    override suspend fun mapCategoryResponseToEntity(list: List<ResponseBody>, parentUrl: String): List<CategoryEntity> {
        return list.mapIndexed { index, responseBody -> mapCategoryResponseToEntity(responseBody, parentUrl, index) }
    }

    /**
     * Maps all response Categories and it's children's radio stations.
     * In case there is 'children' array but not with station it handles it too as simple category.
     *
     * @return HashMap - not sure about the decision. Feel free to change.
     * */
    override suspend fun mapCategoryWithStationsResponseToMap(
        list: List<ResponseBody>,
        parentUrl: String
    ): HashMap<CategoryEntity, List<StationEntity>?> {
        val result = HashMap<CategoryEntity, List<StationEntity>?>()
        list.forEachIndexed { index, responseBody ->
            Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] response has children ${responseBody.children != null}")
            if (responseBody.children != null) {
                val categoryEntity = mapCategoryResponseToEntity(responseBody, parentUrl, index, true)
                val stationList = mapCategoryWithStationsResponseToList(responseBody.children, mapUrlForStation(parentUrl, categoryEntity))
                result[categoryEntity] = stationList
            } else {
                val categoryEntity = mapCategoryResponseToEntity(responseBody, parentUrl, index)
                result[categoryEntity] = null // not audio type
            }
        }
        return result
    }

    override suspend fun mapUrlForStation(parentUrl: String, categoryEntity: CategoryEntity) = "$parentUrl#${categoryEntity.key}"

    private fun mapCategoryWithStationsResponseToList(list: List<ChildrenBody>, parentUrl: String): List<StationEntity> {
        return list.mapIndexed { index, childrenBody -> mapResponseToStationEntity(childrenBody, parentUrl, index) }
    }

    private fun mapCategoryResponseToEntity(body: ResponseBody, parentUrl: String, position: Int, isHeader: Boolean = false): CategoryEntity {
        return CategoryEntity(
            position = position,
            url = body.url ?: "",
            parentUrl = parentUrl,
            text = body.text,
            key = body.key,
            isHeader = isHeader
        )
    }

    private fun mapResponseToStationEntity(body: ChildrenBody, parentUrl: String, position: Int): StationEntity {
        return StationEntity(
            position = position,
            url = body.url,
            parentUrl = parentUrl,
            text = body.text,
            image = body.image.httpsEverywhere(),
            currentTrack = body.currentTrack
        )
    }

}