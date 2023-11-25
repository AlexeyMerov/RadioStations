package com.alexeymerov.radiostations

import android.app.Application
import com.alexeymerov.radiostations.common.BuildConfig
import com.alexeymerov.radiostations.common.NumberedTimberTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class RadioApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(NumberedTimberTree())
    }

}