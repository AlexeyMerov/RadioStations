package com.alexeymerov.radiostations.feature.player.screen

import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alexeymerov.radiostations.core.domain.usecase.audio.FakeAudioUseCase
import com.alexeymerov.radiostations.core.domain.usecase.audio.favorite.FakeFavoriteUseCase
import com.alexeymerov.radiostations.core.domain.usecase.audio.playing.FakePlayingUseCase
import com.alexeymerov.radiostations.core.test.MainDispatcherRule
import com.alexeymerov.radiostations.core.ui.navigation.Screens
import com.alexeymerov.radiostations.feature.player.screen.PlayerViewModel.ScreenPlayState
import com.alexeymerov.radiostations.feature.player.screen.PlayerViewModel.ViewAction
import com.alexeymerov.radiostations.feature.player.screen.PlayerViewModel.ViewState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlayerViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: PlayerViewModel

    private lateinit var favoriteUseCase: FakeFavoriteUseCase

    private lateinit var playingUseCase: FakePlayingUseCase

    private lateinit var audioUseCase: FakeAudioUseCase

    private val savedStateHandle = SavedStateHandle(
        mapOf(Screens.Player.Const.ARG_URL to FakeAudioUseCase.VALID_URL)
    )

    @Before
    fun setup() {
        favoriteUseCase = FakeFavoriteUseCase()
        playingUseCase = FakePlayingUseCase()
        audioUseCase = FakeAudioUseCase()

        viewModel = PlayerViewModel(savedStateHandle, favoriteUseCase, playingUseCase, audioUseCase, dispatcherRule.testDispatcher)
    }

    @Test
    fun whenDataIsLoading_stateIsLoading() = runTest {
        audioUseCase.delay = 500
        viewModel = PlayerViewModel(savedStateHandle, favoriteUseCase, playingUseCase, audioUseCase, dispatcherRule.testDispatcher)

        assertThat(viewModel.viewState.first()).isInstanceOf(ViewState.Loading::class.java)
    }

    @Test
    fun whenUrlInvalid_stateIsError() = runTest {
        val wrongSavedStateHandle = SavedStateHandle(
            mapOf(Screens.Player.Const.ARG_URL to "")
        )
        viewModel = PlayerViewModel(wrongSavedStateHandle, favoriteUseCase, playingUseCase, audioUseCase, dispatcherRule.testDispatcher)

        assertThat(viewModel.viewState.first()).isInstanceOf(ViewState.Error::class.java)
    }

    @Test
    fun whenUrlValid_stateIsReadyToPlay() = runTest {
        assertThat(viewModel.viewState.first()).isInstanceOf(ViewState.ReadyToPlay::class.java)
    }

    @Test
    fun whenDataValid_subTitleNotNull() = runTest {
        assertThat(viewModel.viewState.first()).isInstanceOf(ViewState.ReadyToPlay::class.java)
        assertThat(viewModel.subTitle).isNotNull()
    }

    @Test
    fun whenNoSubtitle_fieldIsNull() = runTest {
        val noSubtitleSavedStateHandle = SavedStateHandle(
            mapOf(Screens.Player.Const.ARG_URL to FakeAudioUseCase.VALID_URL_NO_SUBTITLE)
        )
        viewModel = PlayerViewModel(noSubtitleSavedStateHandle, favoriteUseCase, playingUseCase, audioUseCase, dispatcherRule.testDispatcher)

        assertThat(viewModel.viewState.first()).isInstanceOf(ViewState.ReadyToPlay::class.java)
        assertThat(viewModel.subTitle).isNull()
    }

    @Test
    fun whenIsFavorite_fieldIsTrue() = runTest {
        val noSubtitleSavedStateHandle = SavedStateHandle(
            mapOf(Screens.Player.Const.ARG_URL to FakeAudioUseCase.VALID_URL_IS_FAVORITE)
        )
        viewModel = PlayerViewModel(noSubtitleSavedStateHandle, favoriteUseCase, playingUseCase, audioUseCase, dispatcherRule.testDispatcher)

        assertThat(viewModel.viewState.first()).isInstanceOf(ViewState.ReadyToPlay::class.java)
        assertThat(viewModel.isFavorite).isTrue()
    }

    @Test
    fun whenNotFavorite_fieldIsFalse() = runTest {
        assertThat(viewModel.viewState.first()).isInstanceOf(ViewState.ReadyToPlay::class.java)
        assertThat(viewModel.isFavorite).isFalse()
    }

    @Test
    fun onAction_ToggleFavorite_fieldChanges() = runTest {
        assertThat(viewModel.viewState.first()).isInstanceOf(ViewState.ReadyToPlay::class.java)
        assertThat(viewModel.isFavorite).isFalse()

        viewModel.setAction(ViewAction.ToggleFavorite)
        assertThat(viewModel.isFavorite).isTrue()

        viewModel.setAction(ViewAction.ToggleFavorite)
        assertThat(viewModel.isFavorite).isFalse()
    }

    @Test
    fun whenDataValid_PlayStateIsStopped() = runTest {
        val state = viewModel.viewState.first()
        assertThat(state).isInstanceOf(ViewState.ReadyToPlay::class.java)
        state as ViewState.ReadyToPlay

        assertThat(state.playState).isInstanceOf(ScreenPlayState.STOPPED::class.java)
    }

    @Test
    fun whenPlayStopped_onAction_ChangeOrToggleAudio_PlayStateIsLoading() = runTest {
        val state = viewModel.viewState.first()
        assertThat(state).isInstanceOf(ViewState.ReadyToPlay::class.java)
        state as ViewState.ReadyToPlay
        assertThat(state.playState).isInstanceOf(ScreenPlayState.STOPPED::class.java)

        viewModel.setAction(ViewAction.ChangeOrToggleAudio(state.item, null))

        val newState = viewModel.viewState.first()
        assertThat(newState).isInstanceOf(ViewState.ReadyToPlay::class.java)
        newState as ViewState.ReadyToPlay
        assertThat(state.playState).isInstanceOf(ScreenPlayState.LOADING::class.java)
    }

    @Test
    fun whenPlayLoading_onAction_ChangeOrToggleAudio_PlayStateIsPlaying() = runTest {
        val state = viewModel.viewState.first()
        assertThat(state).isInstanceOf(ViewState.ReadyToPlay::class.java)
        state as ViewState.ReadyToPlay
        assertThat(state.playState).isInstanceOf(ScreenPlayState.STOPPED::class.java)

        viewModel.setAction(ViewAction.ChangeOrToggleAudio(state.item, null))

        val newState = viewModel.viewState.first()
        assertThat(newState).isInstanceOf(ViewState.ReadyToPlay::class.java)
        newState as ViewState.ReadyToPlay
        assertThat(state.playState).isInstanceOf(ScreenPlayState.LOADING::class.java)

        advanceTimeBy(1000)

        val playState = viewModel.viewState.first()
        assertThat(playState).isInstanceOf(ViewState.ReadyToPlay::class.java)
        playState as ViewState.ReadyToPlay
        assertThat(playState.playState).isInstanceOf(ScreenPlayState.PLAYING::class.java)
    }

    @Test
    fun whenPlaying_onAction_ChangeOrToggleAudio_PlayStateIsStopped() = runTest {
        val state = viewModel.viewState.first()
        assertThat(state).isInstanceOf(ViewState.ReadyToPlay::class.java)
        state as ViewState.ReadyToPlay
        assertThat(state.playState).isInstanceOf(ScreenPlayState.STOPPED::class.java)

        viewModel.setAction(ViewAction.ChangeOrToggleAudio(state.item, null))

        advanceTimeBy(1000)

        val playState = viewModel.viewState.first()
        assertThat(playState).isInstanceOf(ViewState.ReadyToPlay::class.java)
        playState as ViewState.ReadyToPlay
        assertThat(playState.playState).isInstanceOf(ScreenPlayState.PLAYING::class.java)

        viewModel.setAction(ViewAction.ChangeOrToggleAudio(state.item, null))

        val newState = viewModel.viewState.first()
        assertThat(newState).isInstanceOf(ViewState.ReadyToPlay::class.java)
        newState as ViewState.ReadyToPlay
        assertThat(newState.playState).isInstanceOf(ScreenPlayState.STOPPED::class.java)
    }

}