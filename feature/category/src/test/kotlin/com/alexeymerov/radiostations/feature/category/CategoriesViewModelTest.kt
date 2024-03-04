package com.alexeymerov.radiostations.feature.category

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.alexeymerov.radiostations.core.domain.usecase.audio.favorite.FakeFavoriteUseCase
import com.alexeymerov.radiostations.core.domain.usecase.category.FakeCategoryUseCase
import com.alexeymerov.radiostations.core.test.MainDispatcherRule
import com.alexeymerov.radiostations.core.ui.navigation.Screens
import com.alexeymerov.radiostations.feature.category.CategoriesViewModel.ViewAction.FilterByHeader
import com.alexeymerov.radiostations.feature.category.CategoriesViewModel.ViewAction.LoadCategories
import com.alexeymerov.radiostations.feature.category.CategoriesViewModel.ViewAction.UpdateCategories
import com.alexeymerov.radiostations.feature.category.CategoriesViewModel.ViewState.CategoriesLoaded
import com.alexeymerov.radiostations.feature.category.CategoriesViewModel.ViewState.Loading
import com.alexeymerov.radiostations.feature.category.CategoriesViewModel.ViewState.NothingAvailable
import com.google.common.truth.Truth.assertThat
import com.google.firebase.analytics.FirebaseAnalytics
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CategoriesViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var categoryUseCase: FakeCategoryUseCase

    private lateinit var favoriteUseCase: FakeFavoriteUseCase

    private lateinit var viewModel: CategoriesViewModel

    private val savedStateHandle = SavedStateHandle(
        mapOf(
            Screens.Categories.Const.ARG_URL to FakeCategoryUseCase.VALID_URL,
            Screens.Categories.Const.ARG_TITLE to "Test Title"
        )
    )

    @Before
    fun setup() {
        categoryUseCase = FakeCategoryUseCase()
        favoriteUseCase = FakeFavoriteUseCase()

        viewModel = CategoriesViewModel(
            favoriteUseCase,
            categoryUseCase,
            dispatcherRule.testDispatcher,
            savedStateHandle,
            mockk<FirebaseAnalytics>(relaxed = true),
        )
    }

    @Test
    fun whenUpdateCategories_refreshingIsTrue() = runTest {
        assertThat(viewModel.isRefreshing.first()).isFalse()

        categoryUseCase.delay = 1000
        viewModel.setAction(UpdateCategories)

        assertThat(viewModel.isRefreshing.first()).isTrue()
    }

    @Test
    fun initState_returnLoading() = runTest {
        categoryUseCase.delay = 1000
        viewModel = CategoriesViewModel(
            favoriteUseCase,
            categoryUseCase,
            dispatcherRule.testDispatcher,
            savedStateHandle,
            mockk<FirebaseAnalytics>(relaxed = true),
        )

        val state = viewModel.viewState.first()

        assertThat(state).isInstanceOf(Loading::class.java)
    }

    @Test
    fun ifListEmpty_returnNothingAvailable() = runTest {
        categoryUseCase.returnEmptyList = true
        viewModel = CategoriesViewModel(
            favoriteUseCase,
            categoryUseCase,
            dispatcherRule.testDispatcher,
            savedStateHandle,
            mockk<FirebaseAnalytics>(relaxed = true),
        )

        viewModel.setAction(LoadCategories)

        advanceTimeBy(15_000)

        val state = viewModel.viewState.first()
        assertThat(state).isInstanceOf(NothingAvailable::class.java)
    }

    @Test
    fun ifError_returnNothingAvailable() = runTest {
        categoryUseCase.emulateError = true
        viewModel = CategoriesViewModel(
            favoriteUseCase,
            categoryUseCase,
            dispatcherRule.testDispatcher,
            savedStateHandle,
            mockk<FirebaseAnalytics>(relaxed = true),
        )

        viewModel.setAction(LoadCategories)

        advanceTimeBy(15_000)

        val state = viewModel.viewState.first()
        assertThat(state).isInstanceOf(NothingAvailable::class.java)
    }

    @Test
    fun `if Data Valid return CategoriesLoaded With Flow Of Items`() = runTest {
        viewModel.setAction(LoadCategories)

        val state = viewModel.viewState.first()
        assertThat(state).isInstanceOf(CategoriesLoaded::class.java)
        state as CategoriesLoaded

        state.categoryItems.test {
            assertThat(awaitItem()).isNotEmpty()
        }

        assertThat(state.filterHeaderItems.first()).isNull()

        state.itemsWithLocation.test {
            assertThat(awaitItem()).isNull()
        }
    }

    @Test
    fun `if Headers Available return CategoriesLoaded With Flow Of Headers`() = runTest {
        categoryUseCase.addHeaders = true
        viewModel.setAction(LoadCategories)

        val state = viewModel.viewState.first()
        assertThat(state).isInstanceOf(CategoriesLoaded::class.java)
        state as CategoriesLoaded

        val headers = state.filterHeaderItems.first()
        assertThat(headers).isNotNull()
        assertThat(headers).isNotEmpty()
    }

    @Test
    fun `if Locations Available return CategoriesLoaded With Flow Of ItemsWithLocation`() = runTest {
        categoryUseCase.addLocations = true
        viewModel.setAction(LoadCategories)

        val state = viewModel.viewState.first()
        assertThat(state).isInstanceOf(CategoriesLoaded::class.java)
        state as CategoriesLoaded

        state.itemsWithLocation.test {
            val awaitItem = awaitItem()
            assertThat(awaitItem).isNotNull()
        }
    }

    @Test
    fun `on action UpdateCategories updated CategoriesLoaded With Flow Of Items`() = runTest {
        viewModel.setAction(LoadCategories)

        advanceTimeBy(11000)

        val state = viewModel.viewState.first()
        assertThat(state).isInstanceOf(CategoriesLoaded::class.java)
        state as CategoriesLoaded

        state.categoryItems.test {
            val itemsSize = awaitItem()[0].items.size

            viewModel.setAction(UpdateCategories)

            val newItems = awaitItem()[0].items
            assertThat(newItems.size).isNotEqualTo(itemsSize)
        }
    }

    @Test
    fun `on action FilterByHeader updated CategoriesLoaded With Flow Of Filtered Items`() = runTest {
        categoryUseCase.addHeaders = true
        viewModel.setAction(LoadCategories)

        val state = viewModel.viewState.first()
        assertThat(state).isInstanceOf(CategoriesLoaded::class.java)
        state as CategoriesLoaded

        val items = state.categoryItems.first()
        val initSize = items.size

        val headers = state.filterHeaderItems.first()
        assertThat(headers).isNotNull()
        headers!!
        assertThat(headers).isNotEmpty()

        viewModel.setAction(FilterByHeader(headers[0]))

        val newItems = state.categoryItems.first()
        val newSize = newItems.size

        assertThat(newSize).isLessThan(initSize)

        val updatedHeaders = state.filterHeaderItems.first()
        assertThat(updatedHeaders).isNotNull()
        updatedHeaders!!
        assertThat(updatedHeaders).isNotEmpty()

        viewModel.setAction(FilterByHeader(updatedHeaders[0]))

        val reversedItems = state.categoryItems.first()
        val reversedSize = reversedItems.size

        assertThat(reversedSize).isEqualTo(initSize)
    }

}

