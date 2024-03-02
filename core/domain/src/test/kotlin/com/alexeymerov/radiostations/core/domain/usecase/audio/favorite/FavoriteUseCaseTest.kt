package com.alexeymerov.radiostations.core.domain.usecase.audio.favorite

import com.alexeymerov.radiostations.core.data.repository.audio.FakeMediaRepository
import com.alexeymerov.radiostations.core.domain.mapper.category.DtoCategoriesMapper
import com.alexeymerov.radiostations.core.domain.mapper.category.DtoCategoriesMapperImpl
import com.google.common.truth.Truth.assertThat
import com.google.firebase.analytics.FirebaseAnalytics
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FavoriteUseCaseTest {

    private lateinit var useCase: FavoriteUseCase

    private lateinit var fakeMediaRepository: FakeMediaRepository

    private lateinit var dtoCategoriesMapper: DtoCategoriesMapper

    @Before
    fun setup() {
        fakeMediaRepository = FakeMediaRepository()
        dtoCategoriesMapper = DtoCategoriesMapperImpl()

        useCase = FavoriteUseCaseImpl(
            fakeMediaRepository,
            dtoCategoriesMapper,
            mockk<FirebaseAnalytics>(relaxed = true)
        )
    }

    private suspend fun getTestItem() = dtoCategoriesMapper.mapEntityToDto(fakeMediaRepository.getItemByUrl(FakeMediaRepository.VALID_ITEM_URL)!!)

    @Test
    fun `getFavorites when not favorites return empty list`() = runTest {
        val items = useCase.getFavorites().first().items

        assertThat(items).isEmpty()
    }

    @Test
    fun `setFavorite saves item and getFavorites returns it`() = runTest {
        val items = useCase.getFavorites().first().items

        assertThat(items).isEmpty()

        val testItem = getTestItem()

        useCase.setFavorite(testItem)

        val updatedItems = useCase.getFavorites().first().items

        assertThat(updatedItems).isNotEmpty()

        val theSameItem = updatedItems.find { it.id == testItem.id }

        assertThat(theSameItem).isNotNull()
    }

    @Test
    fun `toggleFavorite(item) with true changes favorite state to false`() = runTest {
        val testItem = getTestItem()

        useCase.setFavorite(testItem)

        val items = useCase.getFavorites().first().items

        assertThat(items).isNotEmpty()

        val theSameItem = items.find { it.id == testItem.id }

        assertThat(theSameItem).isNotNull()
        theSameItem!!

        useCase.toggleFavorite(theSameItem)

        val updatedItems = useCase.getFavorites().first().items

        assertThat(updatedItems).isEmpty()
    }

    @Test
    fun `toggleFavorite(item) with false changes favorite state to true`() = runTest {
        val items = useCase.getFavorites().first().items

        assertThat(items).isEmpty()

        val testItem = getTestItem()

        useCase.toggleFavorite(testItem)

        val updatedItems = useCase.getFavorites().first().items

        assertThat(updatedItems).isNotEmpty()
    }

    @Test
    fun `toggleFavorite(id) with true changes favorite state to false`() = runTest {
        val testItem = getTestItem()

        useCase.setFavorite(testItem)

        val items = useCase.getFavorites().first().items

        assertThat(items).isNotEmpty()

        val theSameItem = items.find { it.id == testItem.id }

        assertThat(theSameItem).isNotNull()
        theSameItem!!

        useCase.toggleFavorite(theSameItem.id)

        val updatedItems = useCase.getFavorites().first().items

        assertThat(updatedItems).isEmpty()
    }

    @Test
    fun `toggleFavorite(id) with false changes favorite state to true`() = runTest {
        val items = useCase.getFavorites().first().items

        assertThat(items).isEmpty()

        val testItem = getTestItem()

        useCase.toggleFavorite(testItem.id)

        val updatedItems = useCase.getFavorites().first().items

        assertThat(updatedItems).isNotEmpty()
    }

    @Test
    fun `unfavorite removes item from favorites`() = runTest {
        val testItem = getTestItem()

        useCase.setFavorite(testItem)

        val items = useCase.getFavorites().first().items

        assertThat(items).isNotEmpty()

        useCase.unfavorite(testItem)

        val updatedItems = useCase.getFavorites().first().items

        assertThat(updatedItems).isEmpty()
    }
}