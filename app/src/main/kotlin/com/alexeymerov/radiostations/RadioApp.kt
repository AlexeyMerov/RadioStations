package com.alexeymerov.radiostations

import android.app.Application
import com.alexeymerov.radiostations.common.NumberedTimberTree
import com.alexeymerov.radiostations.core.common.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class RadioApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(NumberedTimberTree())
    }

}