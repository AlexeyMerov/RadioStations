package com.alexeymerov.radiostations.data.repository

import com.alexeymerov.radiostations.data.local.db.dao.CategoryDao
import com.alexeymerov.radiostations.data.mapper.category.CategoryMapper
import com.alexeymerov.radiostations.data.mapper.response.ResponseMapper
import com.alexeymerov.radiostations.data.remote.client.radio.RadioClient
import com.alexeymerov.radiostations.data.remote.response.CategoryBody
import com.alexeymerov.radiostations.data.remote.response.MainBody
import com.alexeymerov.radiostations.data.repository.category.CategoryRepository
import com.alexeymerov.radiostations.data.repository.category.CategoryRepositoryImpl
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerifyOrder
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verifyOrder
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response


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
    lateinit var responseMapper: ResponseMapper

    private lateinit var repository: CategoryRepository

    @Before
    fun setup() {
        repository = spyk(CategoryRepositoryImpl(client, categoryDao, responseMapper, categoryMapper))
    }

    @Test
    fun `get categories by url`() = runTest {
        every { categoryDao.getAllByParentUrl(any()) } returns flowOf(emptyList())

        repository.getCategoriesByUrl("")

        verifyOrder {
            repository.getCategoriesByUrl(any())
            categoryDao.getAllByParentUrl(any())
        }

        confirmVerified(repository, categoryDao)
    }

    @Test
    fun `load categories by url`() = runTest {
        val responseMock = mockk<Response<MainBody<CategoryBody>>>()
        coEvery { client.requestCategoriesByUrl(any()) } returns responseMock
        every { responseMapper.mapResponseBody(responseMock) } returns emptyList()
        coEvery { categoryMapper.mapCategoryResponseToEntity(any(), any()) } returns emptyList()
        coJustRun { categoryDao.insertAll(any()) }

        repository.loadCategoriesByUrl("")

        coVerifyOrder {
            repository.loadCategoriesByUrl(any())
            client.requestCategoriesByUrl(any())
            categoryMapper.mapCategoryResponseToEntity(any(), any())
            categoryDao.insertAll(any())
        }

        confirmVerified(repository, client, categoryMapper, categoryDao)
    }

}