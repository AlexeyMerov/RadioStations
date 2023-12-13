package com.alexeymerov.radiostations.core.domain.usecase.settings.favorite

import com.alexeymerov.radiostations.core.datastore.SettingsStore
import com.alexeymerov.radiostations.core.domain.usecase.settings.favorite.FavoriteViewSettingsUseCase.ViewType
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteViewSettingsUseCaseImpl @Inject constructor(
    private val settingsStore: SettingsStore,
    private val analytics: FirebaseAnalytics
) : FavoriteViewSettingsUseCase {

    override fun getViewType(): Flow<ViewType> {
        return settingsStore.getIntPrefsFlow(VIEW_TYPE_KEY, defValue = ViewType.LIST.columnCount)
            .map { prefValue -> ViewType.entries.first { it.columnCount == prefValue } }
    }

    override suspend fun setViewType(type: ViewType) {
        analytics.logEvent("favorite_view") {
            param("view_type", type.name.lowercase())
        }
        settingsStore.setIntPrefs(VIEW_TYPE_KEY, type.columnCount)
    }

    companion object {
        const val VIEW_TYPE_KEY = "view_type"
    }
}