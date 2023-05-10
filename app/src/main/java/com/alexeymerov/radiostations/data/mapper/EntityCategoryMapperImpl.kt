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
        return list.map { mapCategoryResponseToEntity(it, parentUrl) }
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
        list.forEach {
            Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] response has children ${it.children != null}")
            if (it.children != null) {
                val categoryEntity = mapCategoryResponseToEntity(it, parentUrl, true)
                val stationList = mapCategoryWithStationsResponseToList(it.children, mapUrlForStation(parentUrl, categoryEntity))
                result[categoryEntity] = stationList
            } else {
                val categoryEntity = mapCategoryResponseToEntity(it, parentUrl)
                result[categoryEntity] = null // not audio type
            }
        }
        return result
    }

    override suspend fun mapUrlForStation(parentUrl: String, categoryEntity: CategoryEntity) = "$parentUrl#${categoryEntity.key}"

    private fun mapCategoryWithStationsResponseToList(list: List<ChildrenBody>, parentUrl: String): List<StationEntity> {
        return list.map { mapResponseToStationEntity(it, parentUrl) }
    }

    private fun mapCategoryResponseToEntity(body: ResponseBody, parentUrl: String, isHeader: Boolean = false): CategoryEntity {
        return CategoryEntity(
            url = body.url ?: "",
            parentUrl = parentUrl,
            text = body.text,
            key = body.key,
            isHeader = isHeader
        )
    }

    private fun mapResponseToStationEntity(body: ChildrenBody, parentUrl: String): StationEntity {
        return StationEntity(
            url = body.url,
            parentUrl = parentUrl,
            text = body.text,
            image = body.image.httpsEverywhere(),
            currentTrack = body.currentTrack
        )
    }

}