package com.alexeymerov.radiostations.feature.favorite

import app.cash.turbine.test
import com.alexeymerov.radiostations.core.domain.usecase.audio.FakeAudioUseCase
import com.alexeymerov.radiostations.core.domain.usecase.settings.favorite.FakeFavoriteViewSettingsUseCase
import com.alexeymerov.radiostations.core.domain.usecase.settings.favorite.FavoriteViewSettingsUseCase.*
import com.alexeymerov.radiostations.feature.favorite.FavoritesViewModel.*
import com.google.common.truth.Truth.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FavoritesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var audioUseCase: FakeAudioUseCase

    private lateinit var settingsUseCase: FakeFavoriteViewSettingsUseCase

    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        audioUseCase = FakeAudioUseCase()
        settingsUseCase = FakeFavoriteViewSettingsUseCase()

        viewModel = FavoritesViewModel(audioUseCase, settingsUseCase, testDispatcher)
    }

    @After
    fun teardown() {
        testDispatcher.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun getState_afterInit_returnsLoading() = runTest {
        audioUseCase.flowDelay = 200
        viewModel = FavoritesViewModel(audioUseCase, settingsUseCase, testDispatcher)

        viewModel.viewState.test {
            assertThat(awaitItem()).isInstanceOf(ViewState.Loading::class.java)
        }
    }

    @Test
    fun getState_ifListEmpty_returnsNothingAvailable() = runTest {
        audioUseCase.returnEmptyList = true
        viewModel = FavoritesViewModel(audioUseCase, settingsUseCase, testDispatcher)

        viewModel.viewState.test {
            assertThat(awaitItem()).isInstanceOf(ViewState.NothingAvailable::class.java)
        }
    }

    @Test
    fun getState_ifAnyError_returnsNothingAvailable() = runTest {
        audioUseCase.emulateError = true
        viewModel = FavoritesViewModel(audioUseCase, settingsUseCase, testDispatcher)

        viewModel.viewState.test {
            assertThat(awaitItem()).isInstanceOf(ViewState.NothingAvailable::class.java)
        }
    }

    @Test
    fun getState_ifDataValid_returnsLoadedWitValidData() = runTest {
        viewModel.viewState.test {
            val viewState = awaitItem()

            assertThat(viewState).isInstanceOf(ViewState.FavoritesLoaded::class.java)
            viewState as ViewState.FavoritesLoaded

            assertThat(viewState.items).isNotEmpty()
        }
    }

    @Test
    fun onAction_SetViewType_updatesStateData() = runTest {
        viewModel.viewState.test {
            val viewState = awaitItem()

            assertThat(viewState).isInstanceOf(ViewState.FavoritesLoaded::class.java)
            viewState as ViewState.FavoritesLoaded

            val viewType = viewState.viewType
            assertThat(viewType).isNotEqualTo(ViewType.GRID_3_COLUMN)

            viewModel.setAction(ViewAction.SetViewType(ViewType.GRID_3_COLUMN))

            val newState = awaitItem()
            assertThat(newState).isInstanceOf(ViewState.FavoritesLoaded::class.java)
            newState as ViewState.FavoritesLoaded

            val newType = newState.viewType
            assertThat(newType).isNotEqualTo(viewType)
            assertThat(newType).isEqualTo(ViewType.GRID_3_COLUMN)
        }
    }

    @Test
    fun onAction_SelectItem_updatesCount() = runTest {
        viewModel.viewState.test {
            val viewState = awaitItem()

            assertThat(viewState).isInstanceOf(ViewState.FavoritesLoaded::class.java)
            viewState as ViewState.FavoritesLoaded

            val items = viewState.items
            val item = items[0]

            assertThat(viewModel.selectedItemsCount.intValue).isEqualTo(0)

            viewModel.setAction(ViewAction.SelectItem(item))

            assertThat(viewModel.selectedItemsCount.intValue).isEqualTo(1)

            viewModel.setAction(ViewAction.SelectItem(item))

            assertThat(viewModel.selectedItemsCount.intValue).isEqualTo(0)
        }
    }

    @Test
    fun onAction_Unfavorite_updatesStateData() = runTest {
        val viewState = viewModel.viewState.first()

        assertThat(viewState).isInstanceOf(ViewState.FavoritesLoaded::class.java)
        viewState as ViewState.FavoritesLoaded

        val items = viewState.items
        val itemsSize = items.size
        val item = items[0]

        viewModel.setAction(ViewAction.Unfavorite(item))

        val newState = viewModel.viewState.first()
        assertThat(newState).isInstanceOf(ViewState.FavoritesLoaded::class.java)
        newState as ViewState.FavoritesLoaded

        val newItems = newState.items

        assertThat(newItems.size).isLessThan(itemsSize)
    }

    @Test
    fun onAction_UnfavoriteSelected_updatesStateData() = runTest {
        val viewState = viewModel.viewState.first()

        assertThat(viewState).isInstanceOf(ViewState.FavoritesLoaded::class.java)
        viewState as ViewState.FavoritesLoaded

        val items = viewState.items
        val itemsSize = items.size
        val item = items[0]

        viewModel.setAction(ViewAction.SelectItem(item))
        viewModel.setAction(ViewAction.UnfavoriteSelected)

        val newState = viewModel.viewState.first()
        assertThat(newState).isInstanceOf(ViewState.FavoritesLoaded::class.java)
        newState as ViewState.FavoritesLoaded

        val newItems = newState.items

        assertThat(newItems.size).isLessThan(itemsSize)
    }

    @Test
    fun onAction_UndoRecentUnfavorite_updatesStateData() = runTest {
        val viewState = viewModel.viewState.first()

        assertThat(viewState).isInstanceOf(ViewState.FavoritesLoaded::class.java)
        viewState as ViewState.FavoritesLoaded

        val items = viewState.items
        val itemsSize = items.size
        val item = items[0]

        viewModel.setAction(ViewAction.SelectItem(item))
        viewModel.setAction(ViewAction.UnfavoriteSelected)

        val newState = viewModel.viewState.first()
        assertThat(newState).isInstanceOf(ViewState.FavoritesLoaded::class.java)
        newState as ViewState.FavoritesLoaded

        val newItems = newState.items

        assertThat(newItems.size).isLessThan(itemsSize)

        viewModel.setAction(ViewAction.UndoRecentUnfavorite)

        val reversedState = viewModel.viewState.first()
        assertThat(reversedState).isInstanceOf(ViewState.FavoritesLoaded::class.java)
        reversedState as ViewState.FavoritesLoaded

        val reversedItems = reversedState.items

        assertThat(items).containsExactlyElementsIn(reversedItems)
    }

    @Test
    fun onUnfavoriteAction_invokeToastEffect() = runTest {
        val viewState = viewModel.viewState.first()
        assertThat(viewState).isInstanceOf(ViewState.FavoritesLoaded::class.java)
        viewState as ViewState.FavoritesLoaded

        val items = viewState.items
        val item = items[0]

        viewModel.viewEffect.test {
            viewModel.setAction(ViewAction.Unfavorite(item))

            val effect = awaitItem()
            assertThat(effect).isInstanceOf(ViewEffect.ShowUnfavoriteToast::class.java)
            effect as ViewEffect.ShowUnfavoriteToast

            assertThat(effect.itemCount).isEqualTo(1)
        }
    }

    @Test
    fun onUnfavoriteSelectedAction_invokeToastEffect() = runTest {
        val viewState = viewModel.viewState.first()
        assertThat(viewState).isInstanceOf(ViewState.FavoritesLoaded::class.java)
        viewState as ViewState.FavoritesLoaded

        val items = viewState.items
        val item = items[0]
        viewModel.setAction(ViewAction.SelectItem(item))

        viewModel.viewEffect.test {
            viewModel.setAction(ViewAction.UnfavoriteSelected)

            val effect = awaitItem()
            assertThat(effect).isInstanceOf(ViewEffect.ShowUnfavoriteToast::class.java)
            effect as ViewEffect.ShowUnfavoriteToast

            assertThat(effect.itemCount).isEqualTo(1)
        }

    }
}
