package com.alexeymerov.radiostations.feature.settings.language

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.alexeymerov.radiostations.core.ui.R
import com.alexeymerov.radiostations.core.ui.view.BasicText
import com.alexeymerov.radiostations.feature.settings.SettingsTestTags
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LanguageSettings(modifier: Modifier) {
    val context = LocalContext.current
    val config = LocalConfiguration.current
    val colorScheme = MaterialTheme.colorScheme
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }

    Button(
        modifier = modifier.testTag(SettingsTestTags.LANGUAGE_BUTTON),
        onClick = { showBottomSheet = true }
    ) {
        BasicText(text = stringResource(R.string.language))
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onDismissRequest = { showBottomSheet = false }
        ) {
            LazyColumn(
                modifier = Modifier.testTag(SettingsTestTags.LANGUAGE_SHEET),
            ) {
                itemsIndexed(
                    items = localeOptions,
                    key = { _, item -> item.language }
                ) { index, item ->

                    val isSelectedLocale = remember(item) { item.language == config.locales[0].language }

                    val textColor = remember(item) {
                        if (isSelectedLocale) {
                            colorScheme.primary
                        } else {
                            Color.Unspecified
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clickable {
                                if (!isSelectedLocale) context.changeLocale(item.language)
                            }
                            .wrapContentHeight()
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            BasicText(
                                text = item.displayLanguage.replaceFirstChar { it.titlecase(item) },
                                textAlign = TextAlign.Center,
                                color = textColor
                            )
                            BasicText(
                                modifier = Modifier.alpha(0.5f),
                                // capitalize is deprecated :|
                                text = item.getDisplayName(item).replaceFirstChar { it.titlecase(item) },
                                textAlign = TextAlign.Center,
                                textStyle = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    if (index < localeOptions.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = DividerDefaults.color.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

private val localeOptions = listOf(
    Locale("en"),
    Locale("de"),
    Locale("fr"),
    Locale("es"),
    Locale("uk"),
    Locale("ru"),
)

private fun Context.changeLocale(localeTag: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val systemService = getSystemService(LocaleManager::class.java)
        systemService.applicationLocales = LocaleList.forLanguageTags(localeTag)
    } else {
        val locales = LocaleListCompat.forLanguageTags(localeTag)
        AppCompatDelegate.setApplicationLocales(locales)
    }
}