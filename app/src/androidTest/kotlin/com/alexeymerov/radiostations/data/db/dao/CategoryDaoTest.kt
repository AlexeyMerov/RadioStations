package com.alexeymerov.radiostations.data.db.dao

import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.data.local.db.RadioDatabase
import com.alexeymerov.radiostations.data.local.db.dao.CategoryDao
import com.alexeymerov.radiostations.data.local.db.entity.CategoryEntity
import com.alexeymerov.radiostations.data.local.db.entity.EntityItemType
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
class CategoryDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Named("AndroidTest")
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
    fun insert_category_in_db() = runTest {
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
        categoryDao.insert(item)
        val entityList = categoryDao.getAllByParentUrl(parentUrl).first()
        assert(entityList.contains(item))
    }

    @Test
    fun replace_item_in_db_if_same_URl_and_TEXT() = runTest {
        val parentUrl = "uniqueUrl"
        val item = CategoryEntity(
            id = String.EMPTY,
            position = 0,
            url = String.EMPTY,
            parentUrl = parentUrl,
            text = "123",
            image = String.EMPTY,
            currentTrack = String.EMPTY,
            type = EntityItemType.CATEGORY,
            childCount = null
        )
        categoryDao.insert(item)
        val newItem = item.copy(parentUrl = parentUrl, text = "123")
        categoryDao.insert(newItem)

        val entityList = categoryDao.getAllByParentUrl(parentUrl).first()
        assert(entityList.size == 1)
    }

    @Test
    fun not_replace_item_in_db_if_same_URl_but_different_TEXT() = runTest {
        val parentUrl = "sameurl"

        val item = CategoryEntity(
            id = UUID.randomUUID().toString(),
            position = 0,
            url = String.EMPTY,
            parentUrl = parentUrl,
            text = "111",
            image = String.EMPTY,
            currentTrack = String.EMPTY,
            type = EntityItemType.CATEGORY,
            childCount = null
        )
        val newItem = item.copy(id = UUID.randomUUID().toString(), text = "222")

        categoryDao.insert(item)
        categoryDao.insert(newItem)

        val entityList = categoryDao.getAllByParentUrl(parentUrl).first()
        assert(entityList.containsAll(listOf(item, newItem)))
    }

    @Test
    fun not_replace_item_in_db_if_same_TEXT_but_different_URL() = runTest {
        val text = "text"
        val firstUrl = "one"
        val secondUrl = "two"

        val item = CategoryEntity(
            id = UUID.randomUUID().toString(),
            position = 0,
            url = String.EMPTY,
            parentUrl = firstUrl,
            text = text,
            image = String.EMPTY,
            currentTrack = String.EMPTY,
            type = EntityItemType.CATEGORY,
            childCount = null
        )
        val newItem = item.copy(id = UUID.randomUUID().toString(), parentUrl = secondUrl)

        categoryDao.insert(item)
        categoryDao.insert(newItem)

        val firstList = categoryDao.getAllByParentUrl(firstUrl).first()
        val secondList = categoryDao.getAllByParentUrl(secondUrl).first()
        val resultList = firstList + secondList
        assert(resultList.containsAll(listOf(item, newItem)))
    }

    @Test
    fun insert_unique_list_of_items() = runTest {
        val list = mutableListOf<CategoryEntity>()
        val parentUrl = "sameListUrl"

        repeat(10) {
            val item = CategoryEntity(
                id = UUID.randomUUID().toString(),
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
        assert(dbList.size == list.size && dbList.containsAll(list))
    }

    @Test
    fun insert_not_unique_list_of_items() = runTest {
        val list = mutableListOf<CategoryEntity>()
        val parentUrl = "sameListUrl"
        val sameText = "sameText"

        repeat(10) {
            val item = CategoryEntity(
                id = String.EMPTY,
                position = 0,
                url = UUID.randomUUID().toString(),
                parentUrl = parentUrl,
                text = sameText,
                image = String.EMPTY,
                currentTrack = String.EMPTY,
                type = EntityItemType.CATEGORY,
                childCount = null
            )
            list.add(item)
        }

        categoryDao.insertAll(list)

        val dbList = categoryDao.getAllByParentUrl(parentUrl).first()
        assert(dbList.size == 1)
    }

    @Test
    fun items_ordered_by_position_asc() = runTest {
        val list = mutableListOf<CategoryEntity>()
        val parentUrl = "sameListUrl"

        for (i in 10 downTo 0) {
            val item = CategoryEntity(
                id = UUID.randomUUID().toString(),
                position = i,
                url = String.EMPTY,
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
        assert(dbList.size == list.size && dbList != list)

        list.sortBy { it.position }
        assert(dbList.size == list.size && dbList == list)
    }

}