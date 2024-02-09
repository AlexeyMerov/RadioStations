package com.alexeymerov.radiostations.core.database.dao

import com.alexeymerov.radiostations.core.common.EMPTY
import com.alexeymerov.radiostations.core.common.toInt
import com.alexeymerov.radiostations.core.database.RadioDatabase
import com.alexeymerov.radiostations.core.database.entity.CategoryEntity
import com.alexeymerov.radiostations.core.database.entity.EntityItemType
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID
import javax.inject.Inject

@HiltAndroidTest
class CategoryDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: RadioDatabase

    @Inject
    lateinit var categoryDao: CategoryDao

    @Before
    fun init() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        database.clearAllTables()
        database.close()
    }

    @Test
    fun get_by_valid_id_return_entity_with_the_id() = runTest {
        val id = "someid"

        val item = CategoryEntity(
            id = id,
            position = 0,
            url = String.EMPTY,
            parentUrl = "parentUrl",
            text = String.EMPTY,
            type = EntityItemType.CATEGORY
        )
        categoryDao.insertAll(listOf(item))
        val entity = categoryDao.getById(id)
        assertThat(entity.id).isEqualTo(id)
    }

    @Test
    fun get_by_wrong_id_throw_null_pointer() = runTest {
        val item = CategoryEntity(
            id = "someid",
            position = 0,
            url = String.EMPTY,
            parentUrl = "parentUrl",
            text = String.EMPTY,
            type = EntityItemType.CATEGORY
        )
        categoryDao.insertAll(listOf(item))

        try {
            categoryDao.getById("")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(NullPointerException::class.java)
        }
    }

    @Test
    fun get_by_valid_url_return_entity_with_the_url() = runTest {
        val url = "someurl"

        val item = CategoryEntity(
            id = "id",
            position = 0,
            url = url,
            parentUrl = "parentUrl",
            text = String.EMPTY,
            type = EntityItemType.CATEGORY
        )
        categoryDao.insertAll(listOf(item))
        val entity = categoryDao.getByUrl(url)
        assertThat(entity.url).isEqualTo(url)
    }

    @Test
    fun get_by_wrong_url_throw_null_pointer() = runTest {
        val item = CategoryEntity(
            id = "someid",
            position = 0,
            url = "someurl",
            parentUrl = "parentUrl",
            text = String.EMPTY,
            type = EntityItemType.CATEGORY
        )
        categoryDao.insertAll(listOf(item))

        try {
            categoryDao.getByUrl("")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(NullPointerException::class.java)
        }
    }

    @Test
    fun get_all_by_valid_parentUrl_return_flow_with_entities_with_the_parent_url() = runTest {
        val parentUrl = "someparenturl"

        val list = listOf(
            CategoryEntity(
                id = "id1",
                position = 2,
                url = "someurl",
                parentUrl = parentUrl,
                text = String.EMPTY,
                type = EntityItemType.CATEGORY
            ),
            CategoryEntity(
                id = "id2",
                position = 0,
                url = "someurl",
                parentUrl = parentUrl,
                text = String.EMPTY,
                type = EntityItemType.CATEGORY
            ),
            CategoryEntity(
                id = "id3",
                position = 1,
                url = "someurl",
                parentUrl = "anotherurl",
                text = String.EMPTY,
                type = EntityItemType.CATEGORY
            )
        )
        categoryDao.insertAll(list)

        val categoryEntities = categoryDao.getAllByParentUrl(parentUrl).first()
        val allItemsWithSameParentUrl = categoryEntities.all { it.parentUrl == parentUrl }

        assertThat(categoryEntities).hasSize(2)
        assertThat(allItemsWithSameParentUrl).isTrue()

        val sortedList = list.filter { it.parentUrl == parentUrl }.sortedBy { it.position }
        categoryEntities.forEachIndexed { index, item ->
            assertThat(sortedList[index].position).isEqualTo(item.position)
        }
    }

    @Test
    fun get_all_by_wrong_parentUrl_return_flow_with_empty_list() = runTest {
        val parentUrl = "someparenturl"

        categoryDao.insertAll(
            listOf(
                CategoryEntity(
                    id = "id",
                    position = 0,
                    url = "someurl",
                    parentUrl = parentUrl,
                    text = String.EMPTY,
                    type = EntityItemType.CATEGORY
                ),
                CategoryEntity(
                    id = "id",
                    position = 0,
                    url = "someurl",
                    parentUrl = parentUrl,
                    text = String.EMPTY,
                    type = EntityItemType.CATEGORY
                ),
                CategoryEntity(
                    id = "id",
                    position = 0,
                    url = "someurl",
                    parentUrl = "anotherurl",
                    text = String.EMPTY,
                    type = EntityItemType.CATEGORY
                )
            )
        )
        val entityFlow = categoryDao.getAllByParentUrl("")

        assertThat(entityFlow.first()).isEmpty()
    }

    @Test
    fun get_all_ids_by_valid_parentUrl_return_list_with_id_strings() = runTest {
        val parentUrl = "someparenturl"
        val id1 = "id1"
        val id2 = "id2"

        categoryDao.insertAll(
            listOf(
                CategoryEntity(
                    id = id1,
                    position = 0,
                    url = "someurl",
                    parentUrl = parentUrl,
                    text = String.EMPTY,
                    type = EntityItemType.CATEGORY
                ),
                CategoryEntity(
                    id = id2,
                    position = 0,
                    url = "someurl",
                    parentUrl = parentUrl,
                    text = String.EMPTY,
                    type = EntityItemType.CATEGORY
                ),
                CategoryEntity(
                    id = "id3",
                    position = 0,
                    url = "someurl",
                    parentUrl = "someurl",
                    text = String.EMPTY,
                    type = EntityItemType.CATEGORY
                )
            )
        )
        val idsList = categoryDao.getAllIdsByParentUrl(parentUrl)

        assertThat(idsList).hasSize(2)
        assertThat(idsList).containsExactly(id1, id2)
    }

    @Test
    fun get_all_ids_by_wrong_parentUrl_return_empty_list() = runTest {
        categoryDao.insertAll(
            listOf(
                CategoryEntity(
                    id = "id1",
                    position = 0,
                    url = "someurl",
                    parentUrl = "someparenturl",
                    text = String.EMPTY,
                    type = EntityItemType.CATEGORY
                ),
                CategoryEntity(
                    id = "id1",
                    position = 0,
                    url = "someurl",
                    parentUrl = "someparenturl",
                    text = String.EMPTY,
                    type = EntityItemType.CATEGORY
                ),
                CategoryEntity(
                    id = "id3",
                    position = 0,
                    url = "someurl",
                    parentUrl = "someurl",
                    text = String.EMPTY,
                    type = EntityItemType.CATEGORY
                )
            )
        )
        val idsList = categoryDao.getAllIdsByParentUrl("")

        assertThat(idsList).hasSize(0)
    }

    @Test
    fun get_all_favs_return_flow_list_with_favorite_param_equal_true() = runTest {
        val list = listOf(
            CategoryEntity(
                id = "id1",
                position = 0,
                url = "someurl",
                parentUrl = "someparenturl",
                text = String.EMPTY,
                type = EntityItemType.CATEGORY,
                isFavorite = true
            ),
            CategoryEntity(
                id = "id2",
                position = 0,
                url = "someurl",
                parentUrl = "someparenturl",
                text = String.EMPTY,
                type = EntityItemType.CATEGORY,
                isFavorite = true
            ),
            CategoryEntity(
                id = "id3",
                position = 0,
                url = "someurl",
                parentUrl = "someurl",
                text = String.EMPTY,
                type = EntityItemType.CATEGORY,
                isFavorite = false
            )
        )
        categoryDao.insertAll(list)

        val entities = categoryDao.getFavoritesFlow().first()
        val allFavorite = entities.all { it.isFavorite }

        assertThat(entities).hasSize(2)
        assertThat(allFavorite).isTrue()

        val sortedList = list.sortedBy { it.position }
        entities.forEachIndexed { index, item ->
            assertThat(sortedList[index].position).isEqualTo(item.position)
        }
    }

    @Test
    fun get_all_favs_return_empty_flow_if_no_entities_with_favorite_true() = runTest {
        categoryDao.insertAll(
            listOf(
                CategoryEntity(
                    id = "id1",
                    position = 0,
                    url = "someurl",
                    parentUrl = "someparenturl",
                    text = String.EMPTY,
                    type = EntityItemType.CATEGORY,
                    isFavorite = false
                ),
                CategoryEntity(
                    id = "id1",
                    position = 0,
                    url = "someurl",
                    parentUrl = "someparenturl",
                    text = String.EMPTY,
                    type = EntityItemType.CATEGORY,
                    isFavorite = false
                ),
                CategoryEntity(
                    id = "id3",
                    position = 0,
                    url = "someurl",
                    parentUrl = "someurl",
                    text = String.EMPTY,
                    type = EntityItemType.CATEGORY,
                    isFavorite = false
                )
            )
        )
        val favoriteFlow = categoryDao.getFavoritesFlow()

        assertThat(favoriteFlow.first()).hasSize(0)
    }

    @Test
    fun insert_category_in_db_success() = runTest {
        val parentUrl = "someurl"
        val item = CategoryEntity(
            id = String.EMPTY,
            position = 0,
            url = String.EMPTY,
            parentUrl = parentUrl,
            text = String.EMPTY,
            image = String.EMPTY,
            currentTrack = String.EMPTY,
            type = EntityItemType.CATEGORY,
            childCount = null
        )
        categoryDao.insertAll(listOf(item))
        val entityList = categoryDao.getAllByParentUrl(parentUrl).first()

        assertThat(entityList).contains(item)
    }

    @Test
    fun insert_item_with_same_id_replace_old_entity() = runTest {
        val id = "id1"
        val initText = "123"
        val item = CategoryEntity(
            id = id,
            position = 0,
            url = String.EMPTY,
            parentUrl = "parentUrl",
            text = initText,
            image = String.EMPTY,
            currentTrack = String.EMPTY,
            type = EntityItemType.CATEGORY,
            childCount = null
        )
        categoryDao.insertAll(listOf(item))

        var entity = categoryDao.getById(id)
        assertThat(entity.text).isEqualTo(initText)

        val newText = "456"
        val newItem = item.copy(text = newText)
        categoryDao.insertAll(listOf(newItem))

        entity = categoryDao.getById(id)
        assertThat(entity.text).isEqualTo(newText)
    }

    @Test
    fun insert_unique_list_of_items_success() = runTest {
        val list = mutableListOf<CategoryEntity>()
        val parentUrl = "sameListUrl"

        repeat(10) {
            val item = CategoryEntity(
                id = "$it",
                position = 0,
                url = UUID.randomUUID().toString(),
                parentUrl = parentUrl,
                text = UUID.randomUUID().toString(),
                image = String.EMPTY,
                currentTrack = String.EMPTY,
                type = EntityItemType.CATEGORY,
                childCount = null
            )
            list.add(item)
        }

        categoryDao.insertAll(list)

        val dbList = categoryDao.getAllByParentUrl(parentUrl).first()

        assertThat(dbList).hasSize(list.size)
        assertThat(dbList).containsExactlyElementsIn(list)
    }

    @Test
    fun insert_not_unique_list_of_items_replace_duplicates() = runTest {
        val list = mutableListOf<CategoryEntity>()
        val parentUrl = "sameListUrl"

        repeat(10) {
            val item = CategoryEntity(
                id = "sameId",
                position = 0,
                url = UUID.randomUUID().toString(),
                parentUrl = parentUrl,
                text = "sameText",
                image = String.EMPTY,
                currentTrack = String.EMPTY,
                type = EntityItemType.CATEGORY,
                childCount = null
            )
            list.add(item)
        }

        categoryDao.insertAll(list)

        val dbList = categoryDao.getAllByParentUrl(parentUrl).first()
        assertThat(dbList).hasSize(1)
    }

    @Test
    fun update_list_of_items_updates_db_items() = runTest {
        val list = mutableListOf<CategoryEntity>()
        val parentUrl = "sameListUrl"

        repeat(10) {
            val item = CategoryEntity(
                id = "$it",
                position = it,
                url = UUID.randomUUID().toString(),
                parentUrl = parentUrl,
                text = UUID.randomUUID().toString(),
                type = EntityItemType.CATEGORY,
            )
            list.add(item)
        }

        categoryDao.insertAll(list)
        val dbList = categoryDao.getAllByParentUrl(parentUrl).first()
        val allLocationAreNull = dbList.all { it.latitude == null && it.longitude == null }

        assertThat(allLocationAreNull).isTrue()

        val updatedList = list.map {
            it.copy(
                latitude = 0.0,
                longitude = 0.0
            )
        }
        categoryDao.updateAll(updatedList)
        val updatedDbList = categoryDao.getAllByParentUrl(parentUrl).first()
        val updatedLocationNotNull = updatedDbList.all { it.latitude != null && it.longitude != null }

        assertThat(updatedLocationNotNull).isTrue()
    }

    @Test
    fun remove_all_by_id_remove_from_db() = runTest {
        val list = mutableListOf<CategoryEntity>()
        val parentUrl = "sameListUrl"

        repeat(10) {
            val item = CategoryEntity(
                id = "$it",
                position = it,
                url = UUID.randomUUID().toString(),
                parentUrl = parentUrl,
                text = UUID.randomUUID().toString(),
                type = EntityItemType.CATEGORY,
            )
            list.add(item)
        }

        categoryDao.insertAll(list)
        val dbList = categoryDao.getAllByParentUrl(parentUrl).first()
        assertThat(dbList).hasSize(list.size)

        val idsToRemove = list.filterIndexed { index, _ -> index % 2 == 0 }.map { it.id }
        categoryDao.removeAllByIds(idsToRemove)
        val updatedDbList = categoryDao.getAllByParentUrl(parentUrl).first()

        assertThat(updatedDbList).hasSize(5)

        val filteredList = list.filter { it.id !in idsToRemove }
        assertThat(updatedDbList).containsExactlyElementsIn(filteredList)
    }

    @Test
    fun update_station_favorite_with_new_value_success() = runTest {
        val id = "id"

        val entity = CategoryEntity(
            id = id,
            position = 0,
            url = UUID.randomUUID().toString(),
            parentUrl = "parentUrl",
            text = UUID.randomUUID().toString(),
            type = EntityItemType.CATEGORY,
            isFavorite = false
        )

        categoryDao.insertAll(listOf(entity))
        val item = categoryDao.getById(id)
        assertThat(item.isFavorite).isFalse()

        categoryDao.setStationFavorite(id, true.toInt())
        val updatedItem = categoryDao.getById(id)
        assertThat(updatedItem.isFavorite).isTrue()
    }

    @Test
    fun update_station_favorite_with_wrong_id_changes_nothing() = runTest {
        val id = "id"

        val entity = CategoryEntity(
            id = id,
            position = 0,
            url = UUID.randomUUID().toString(),
            parentUrl = "parentUrl",
            text = UUID.randomUUID().toString(),
            type = EntityItemType.CATEGORY,
            isFavorite = false
        )

        categoryDao.insertAll(listOf(entity))
        val item = categoryDao.getById(id)
        assertThat(item.isFavorite).isFalse()

        categoryDao.setStationFavorite("", true.toInt())
        val updatedItem = categoryDao.getById(id)
        assertThat(updatedItem.isFavorite).isFalse()
    }

}