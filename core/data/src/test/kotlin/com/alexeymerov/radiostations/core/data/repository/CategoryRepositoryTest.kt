package com.alexeymerov.radiostations.core.data.repository

import com.alexeymerov.radiostations.core.data.mapper.category.CategoryMapper
import com.alexeymerov.radiostations.core.data.mapper.geocoder.LocationGeocoder
import com.alexeymerov.radiostations.core.data.repository.category.CategoryRepository
import com.alexeymerov.radiostations.core.data.repository.category.CategoryRepositoryImpl
import com.alexeymerov.radiostations.core.database.dao.CategoryDao
import com.alexeymerov.radiostations.core.remote.client.radio.RadioClient
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerifyOrder
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.spyk
import io.mockk.verifyOrder
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class CategoryRepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var client: RadioClient

    @MockK
    lateinit var categoryDao: CategoryDao

    @MockK
    lateinit var categoryMapper: CategoryMapper

    @MockK
    lateinit var locationGeocoder: LocationGeocoder

    private lateinit var repository: CategoryRepository

    @Before
    fun setup() {
        repository = spyk(CategoryRepositoryImpl(client, categoryDao, categoryMapper, locationGeocoder))
        every { categoryDao.getAllByParentUrl(any()) } returns flowOf(emptyList())
        coEvery { client.requestCategoriesByUrl(any()) } returns emptyList()
        coEvery { categoryMapper.mapCategoryResponseToEntity(any(), any()) } returns emptyList()
        every { categoryDao.getAllIdsByParentUrl(any()) } returns emptyList()
        coJustRun { categoryDao.removeAllByIds(any()) }
        coJustRun { categoryDao.insertAll(any()) }
    }

    @Test
    fun `get categories by url`() = runTest {
        repository.getCategoriesByUrl("")

        verifyOrder {
            repository.getCategoriesByUrl(any())
            categoryDao.getAllByParentUrl(any())
        }

        confirmVerified(repository, categoryDao)
    }

    @Test
    fun `load categories by url`() = runTest {
        repository.loadCategoriesByUrl("")

        coVerifyOrder {
            client.requestCategoriesByUrl(any())
            categoryDao.getAllIdsByParentUrl(any())
            categoryDao.removeAllByIds(any())
            categoryDao.insertAll(any())
        }

        confirmVerified(client, categoryDao)
    }

}