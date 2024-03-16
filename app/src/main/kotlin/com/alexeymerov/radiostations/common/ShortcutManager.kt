package com.alexeymerov.radiostations.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import timber.log.Timber

fun Context.createShortcut(
    activityClass: Class<*>,
    shortcutId: String,
    uri: Uri,
    shortLabel: String,
    longLabel: String,
    disabledMessage: String = "Not",
    icon: IconCompat
) {
    Timber.d("-> Activity.createDynamicShortcut: ")

    val shortcut = ShortcutInfoCompat.Builder(this, shortcutId)
        .setShortLabel(shortLabel)
        .setLongLabel(longLabel)
        .setDisabledMessage(disabledMessage)
        .setIcon(icon)
        .setIntent(Intent(Intent.ACTION_VIEW, uri, this, activityClass))
        .build()

    ShortcutManagerCompat.pushDynamicShortcut(this, shortcut)
}