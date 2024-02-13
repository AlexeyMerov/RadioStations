package com.alexeymerov.radiostations.core.database.dao

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.alexeymerov.radiostations.core.database.RadioDatabase
import com.alexeymerov.radiostations.core.database.entity.CountryEntity
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class CountryDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: RadioDatabase

    @Inject
    lateinit var countryDao: CountryDao

    private val emptyCountry = CountryEntity(
        tag = "",
        nameEnglish = "",
        nameNative = "",
        phoneCode = ""
    )

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
    fun insert_country_list_saves_in_db() = runTest {
        val list = mutableListOf<CountryEntity>()
        repeat(5) {
            list.add(
                CountryEntity(
                    tag = "$it",
                    nameEnglish = "",
                    nameNative = "",
                    phoneCode = ""
                )
            )
        }

        countryDao.insertAll(list)
        val entityList = countryDao.getAllForTest()

        assertThat(entityList).hasSize(5)
    }

    @Test
    fun insert_duplicate_country_saves_only_unique() = runTest {
        val list = mutableListOf<CountryEntity>()
        repeat(5) {
            list.add(
                CountryEntity(
                    tag = "$it",
                    nameEnglish = "",
                    nameNative = "",
                    phoneCode = ""
                )
            )
        }
        repeat(5) {
            list.add(
                CountryEntity(
                    tag = "$it",
                    nameEnglish = "",
                    nameNative = "",
                    phoneCode = ""
                )
            )
        }

        countryDao.insertAll(list)
        val entityList = countryDao.getAllForTest()

        assertThat(entityList).hasSize(5)
    }

    @Test
    fun insert_empty_country_list_do_nothing() = runTest {
        val list = mutableListOf<CountryEntity>()

        countryDao.insertAll(list)
        val entityList = countryDao.getAllForTest()

        assertThat(entityList).isEmpty()
    }

    @Test
    fun size_returns_total_amount_of_countries() = runTest {
        val list = mutableListOf<CountryEntity>()
        repeat(5) {
            list.add(
                CountryEntity(
                    tag = "$it",
                    nameEnglish = "",
                    nameNative = "",
                    phoneCode = ""
                )
            )
        }

        countryDao.insertAll(list)
        val size = countryDao.size()

        assertThat(size).isEqualTo(list.size)
    }

    @Test
    fun size_with_empty_db_returns_zero() = runTest {
        val size = countryDao.size()
        assertThat(size).isEqualTo(0)
    }

    @Test
    fun removeAll_clear_country_db_table() = runTest {
        val list = mutableListOf<CountryEntity>()
        repeat(5) {
            list.add(
                CountryEntity(
                    tag = "$it",
                    nameEnglish = "",
                    nameNative = "",
                    phoneCode = ""
                )
            )
        }

        countryDao.insertAll(list)
        val entityList = countryDao.getAllForTest()
        assertThat(entityList).hasSize(5)

        countryDao.removeAll()
        val newList = countryDao.getAllForTest()
        assertThat(newList).isEmpty()
    }

    @Test
    fun get_all_returns_valid_paged_data() = runTest {
        val list = mutableListOf<CountryEntity>()
        repeat(9) {
            list.add(
                CountryEntity(
                    tag = "$it",
                    nameEnglish = "",
                    nameNative = "",
                    phoneCode = ""
                )
            )
        }
        countryDao.insertAll(list)

        val pagingSource = countryDao.getAll()
        val pageSize = 3
        val pager = TestPager(
            config = PagingConfig(
                pageSize = pageSize,
                prefetchDistance = 0,
                initialLoadSize = pageSize
            ),
            pagingSource = pagingSource
        )

        // 1
        val result = pager.refresh() as PagingSource.LoadResult.Page
        assertThat(result.data).hasSize(pageSize)

        // 2
        val newResult = pager.append() as PagingSource.LoadResult.Page
        assertThat(newResult.data).hasSize(pageSize)

        // 3
        pager.append()

        val allData = pager.getPages().flatMap { it.data }
        assertThat(allData).hasSize(pageSize * 3)
        assertThat(allData).containsExactlyElementsIn(list)

        assertThat(pager.getPages()).hasSize(3)
        assertThat(pagingSource).isInstanceOf(PagingSource::class.java)
    }

    @Test
    fun get_all_for_empty_db_returns_empty_page() = runTest {
        val pagingSource = countryDao.getAll()
        val pageSize = 3
        val pager = TestPager(
            config = PagingConfig(
                pageSize = pageSize,
                prefetchDistance = 0,
                initialLoadSize = pageSize
            ),
            pagingSource = pagingSource
        )

        val result = pager.refresh() as PagingSource.LoadResult.Page
        assertThat(result.data).hasSize(0)
    }

    @Test
    fun search_by_name_returns_valid_paged_data() = runTest {
        val list = mutableListOf<CountryEntity>()
        list.add(
            emptyCountry.copy(
                tag = "a1",
                nameEnglish = "England"
            )
        )
        list.add(
            emptyCountry.copy(
                tag = "a2",
                nameEnglish = "Egland"
            )
        )
        list.add(
            emptyCountry.copy(
                tag = "a3",
                nameEnglish = "england",
            )
        )
        list.add(
            emptyCountry.copy(
                tag = "a4",
                nameEnglish = "ENGLAND",
            )
        )
        list.add(
            emptyCountry.copy(
                tag = "a5",
                nameEnglish = "ALSOENGLAND",
            )
        )

        countryDao.insertAll(list)

        val pager = TestPager(
            config = PagingConfig(pageSize = 5),
            pagingSource = countryDao.searchByText("eng")
        )

        val result = pager.refresh() as PagingSource.LoadResult.Page
        assertThat(result.data).hasSize(4)
    }

    @Test
    fun search_by_native_name_returns_valid_paged_data() = runTest {
        val list = mutableListOf<CountryEntity>()
        list.add(
            emptyCountry.copy(
                tag = "a1",
                nameNative = "England",
            )
        )
        list.add(
            emptyCountry.copy(
                tag = "a2",
                nameNative = "Egland",
            )
        )
        list.add(
            emptyCountry.copy(
                tag = "a3",
                nameNative = "england",
            )
        )
        list.add(
            emptyCountry.copy(
                tag = "a4",
                nameNative = "ENGLAND",
            )
        )
        list.add(
            emptyCountry.copy(
                tag = "a5",
                nameNative = "ALSOENGLAND",
            )
        )

        countryDao.insertAll(list)

        val pager = TestPager(
            config = PagingConfig(pageSize = 5),
            pagingSource = countryDao.searchByText("eng")
        )

        val result = pager.refresh() as PagingSource.LoadResult.Page
        assertThat(result.data).hasSize(4)
    }

    @Test
    fun search_by_phone_returns_valid_paged_data() = runTest {
        val list = mutableListOf<CountryEntity>()
        list.add(
            emptyCountry.copy(
                tag = "a1",
                phoneCode = "England",
            )
        )
        list.add(
            emptyCountry.copy(
                tag = "a2",
                phoneCode = "Egland",
            )
        )
        list.add(
            emptyCountry.copy(
                tag = "a3",
                phoneCode = "england",
            )
        )
        list.add(
            emptyCountry.copy(
                tag = "a4",
                phoneCode = "ENGLAND",
            )
        )
        list.add(
            emptyCountry.copy(
                tag = "a5",
                phoneCode = "ALSOENGLAND",
            )
        )

        countryDao.insertAll(list)

        val pager = TestPager(
            config = PagingConfig(pageSize = 5),
            pagingSource = countryDao.searchByText("eng")
        )

        val result = pager.refresh() as PagingSource.LoadResult.Page
        assertThat(result.data).hasSize(4)
    }

    @Test
    fun search_by_text_returns_sorted_by_name_list() = runTest {
        val list = mutableListOf<CountryEntity>()
        list.add(
            emptyCountry.copy(
                tag = "a1",
                nameEnglish = "AEngland"
            )
        )
        list.add(
            emptyCountry.copy(
                tag = "a3",
                nameEnglish = "CEngland",
            )
        )
        list.add(
            emptyCountry.copy(
                tag = "a2",
                nameEnglish = "BEngland",
            )
        )
        list.add(
            emptyCountry.copy(
                tag = "a5",
                nameEnglish = "EEngland",
            )
        )
        list.add(
            emptyCountry.copy(
                tag = "a4",
                nameEnglish = "DEngland",
            )
        )

        countryDao.insertAll(list)

        val sortedList = list.sortedBy { it.nameEnglish }

        val pager = TestPager(
            config = PagingConfig(pageSize = 5),
            pagingSource = countryDao.searchByText("eng")
        )

        val result = pager.refresh() as PagingSource.LoadResult.Page
        assertThat(result.data).containsExactlyElementsIn(sortedList).inOrder()
    }


}