package com.alexeymerov.radiostations.core.database.dao

import com.alexeymerov.radiostations.core.database.RadioDatabase
import com.alexeymerov.radiostations.core.database.entity.MediaEntity
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class MediaDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: RadioDatabase

    @Inject
    lateinit var mediaDao: MediaDao

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
    fun insert_media_saves_item_in_db() = runTest {
        val entity = MediaEntity(
            url = "",
            directMediaUrl = "",
            imageUrl = "",
            title = "",
            subtitle = "",
        )

        assertThat(mediaDao.getMedia().first()).isNull()
        mediaDao.insert(entity)
        assertThat(mediaDao.getMedia().first()).isNotNull()
    }

    @Test
    fun insert_new_media_replace_item_in_db() = runTest {
        val entity = MediaEntity(
            url = "",
            directMediaUrl = "",
            imageUrl = "",
            title = "",
            subtitle = "",
        )
        mediaDao.insert(entity)
        mediaDao.insert(entity.copy(title = "Test"))

        assertThat(mediaDao.getMedia().first()?.title).isEqualTo("Test")
    }

    @Test
    fun get_with_empty_db_return_flow_with_null() = runTest {
        assertThat(mediaDao.getMedia()).isInstanceOf(Flow::class.java)
        assertThat(mediaDao.getMedia().first()).isNull()
    }

    @Test
    fun get_item_return_media_with_id_zero() = runTest {
        val entity = MediaEntity(
            url = "",
            directMediaUrl = "",
            imageUrl = "",
            title = "",
            subtitle = "",
        )
        mediaDao.insert(entity)
        mediaDao.insert(entity.copy(id = 23))

        assertThat(mediaDao.getMedia().first()?.id).isEqualTo(0)
    }

    @Test
    fun clear_table_wipe_media_table() = runTest {
        val entity = MediaEntity(
            url = "",
            directMediaUrl = "",
            imageUrl = "",
            title = "",
            subtitle = "",
        )
        mediaDao.insert(entity)
        mediaDao.insert(entity.copy(id = 23))

        assertThat(mediaDao.getAllMediaForTest().first()).isNotEmpty()

        mediaDao.clearTable()

        assertThat(mediaDao.getAllMediaForTest().first()).isEmpty()
    }
}