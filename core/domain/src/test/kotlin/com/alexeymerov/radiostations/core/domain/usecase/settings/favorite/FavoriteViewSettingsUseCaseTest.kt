package com.alexeymerov.radiostations.core.domain.usecase.settings.favorite

import com.alexeymerov.radiostations.core.datastore.FakeSettingsStore
import com.alexeymerov.radiostations.core.datastore.SettingsStore
import com.alexeymerov.radiostations.core.domain.usecase.settings.favorite.FavoriteViewSettingsUseCase.*
import com.google.common.truth.Truth.*
import com.google.firebase.analytics.FirebaseAnalytics
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FavoriteViewSettingsUseCaseTest {

    private lateinit var useCase: FavoriteViewSettingsUseCaseImpl
    private lateinit var settingsStore: SettingsStore

    @Before
    fun setup() {
        settingsStore = spyk(FakeSettingsStore())
        useCase = FavoriteViewSettingsUseCaseImpl(settingsStore, mockk<FirebaseAnalytics>(relaxed = true))
    }

    private fun setCurrentType(type: ViewType) {
        every { settingsStore.getIntPrefsFlow(any(), any()) } returns flowOf(type.columnCount)
    }

    @Test
    fun `get current type returns valid value`() = runTest {
        val type = useCase.getViewType().first()

        assertThat(type).isAnyOf(ViewType.LIST, ViewType.GRID_2_COLUMN, ViewType.GRID_3_COLUMN)
    }

    @Test
    fun `get current type if LIST is saved returns LIST type`() = runTest {
        setCurrentType(ViewType.LIST)
        val type = useCase.getViewType().first()

        assertThat(type).isEqualTo(ViewType.LIST)
    }

    @Test
    fun `get current type if GRID 2 is saved returns GRID 2 type`() = runTest {
        setCurrentType(ViewType.GRID_2_COLUMN)
        val type = useCase.getViewType().first()

        assertThat(type).isEqualTo(ViewType.GRID_2_COLUMN)
    }

    @Test
    fun `get current type if GRID 3 is saved returns GRID 3 type`() = runTest {
        setCurrentType(ViewType.GRID_3_COLUMN)
        val type = useCase.getViewType().first()

        assertThat(type).isEqualTo(ViewType.GRID_3_COLUMN)
    }

    @Test
    fun `set current type changes saved value`() = runTest {
        val initValue = useCase.getViewType().first()

        assertThat(initValue).isNotEqualTo(ViewType.GRID_2_COLUMN)

        useCase.setViewType(ViewType.GRID_2_COLUMN)

        assertThat(useCase.getViewType().first()).isEqualTo(ViewType.GRID_2_COLUMN)
    }

    @Test
    fun `set same type do nothing`() = runTest {
        setCurrentType(ViewType.LIST)

        assertThat(useCase.getViewType().first()).isEqualTo(ViewType.LIST)

        useCase.setViewType(ViewType.LIST)

        assertThat(useCase.getViewType().first()).isEqualTo(ViewType.LIST)
    }

}